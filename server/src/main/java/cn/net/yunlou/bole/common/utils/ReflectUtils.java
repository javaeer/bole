package cn.net.yunlou.bole.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 精简版反射工具类 - 底层反射操作基础层
 * <p>
 * 职责范围：
 * ✅ 基础字段操作（获取、设置、查找）
 * ✅ 基础方法操作（调用、查找）
 * ✅ 安全对象创建
 * ✅ 基础类型转换
 * ✅ 基础缓存管理
 *
 * @author YourName
 * @version 3.0.0
 * @since 2024
 */
@Slf4j
public final class ReflectUtils {

    // ====== 基础配置 ======
    private static final Set<String> ALLOWED_PACKAGES = ConcurrentHashMap.newKeySet();
    private static final Set<String> DENIED_METHOD_PATTERNS = ConcurrentHashMap.newKeySet();
    // ====== 基础缓存系统 ======
    private static final ConcurrentMap<String, Class<?>> CLASS_CACHE = new ConcurrentHashMap<>(128);
    private static final ConcurrentMap<String, Method> METHOD_CACHE = new ConcurrentHashMap<>(256);
    private static final ConcurrentMap<String, Field> FIELD_CACHE = new ConcurrentHashMap<>(256);
    private static final ConcurrentMap<String, MethodHandle> METHOD_HANDLE_CACHE = new ConcurrentHashMap<>(128);
    // ====== 性能统计 ======
    private static final AtomicLong METHOD_CACHE_HITS = new AtomicLong();
    private static final AtomicLong METHOD_CACHE_MISSES = new AtomicLong();

    static {
        // 基础安全配置
        ALLOWED_PACKAGES.addAll(Arrays.asList(
                "java.util.", "java.lang.", "cn.net.yunlou.", "org.example."
        ));

        DENIED_METHOD_PATTERNS.addAll(Arrays.asList(
                "exec", "runtime", "process", "shell", "classloader", "forname",
                "getclassloader", "defineclass", "unsafe"
        ));

        log.debug("ReflectUtils security configuration loaded");
    }

    /**
     * 私有构造函数
     */
    private ReflectUtils() {
        log.debug("ReflectUtils initialized");
    }

    // ====== 类操作 ======

    /**
     * 安全加载类
     */
    public static Class<?> loadClass(String className) {
        if (className == null || className.trim().isEmpty()) {
            throw new ReflectionException("Class name cannot be null or empty");
        }

        // 基础安全验证
        if (className.contains("..") || className.contains("/") || className.contains("\\")) {
            throw new ReflectionException("Invalid class name: " + className);
        }

        return CLASS_CACHE.computeIfAbsent(className, name -> {
            try {
                Class<?> clazz = Thread.currentThread()
                        .getContextClassLoader()
                        .loadClass(name);

                // 基础包安全检查
                validateClassPackage(clazz);
                return clazz;

            } catch (ClassNotFoundException e) {
                log.error("Class not found: {}", name, e);
                throw new ReflectionException("Class not found: " + name, e);
            }
        });
    }

    /**
     * 创建对象实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T createInstance(Class<T> clazz, Object... args) {
        try {
            if (args == null || args.length == 0) {
                return clazz.getDeclaredConstructor().newInstance();
            }

            Class<?>[] paramTypes = Arrays.stream(args)
                    .map(Object::getClass)
                    .toArray(Class<?>[]::new);

            Constructor<T> constructor = (Constructor<T>) findConstructor(clazz, paramTypes);
            return constructor.newInstance(args);

        } catch (Exception e) {
            log.error("Failed to create instance: {}", clazz.getName(), e);
            throw new ReflectionException("Failed to create instance: " + clazz.getName(), e);
        }
    }

    private static Constructor<?> findConstructor(Class<?> clazz, Class<?>[] paramTypes) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            if (isParamTypesMatch(constructor.getParameterTypes(), paramTypes)) {
                makeAccessible(constructor);
                return constructor;
            }
        }
        throw new ReflectionException("No matching constructor found for: " + clazz.getName());
    }

    // ====== 字段操作 ======

    /**
     * 查找字段
     */
    public static Field findField(Class<?> clazz, String fieldName) {
        String cacheKey = clazz.getName() + "#" + fieldName;
        return FIELD_CACHE.computeIfAbsent(cacheKey, key -> {
            Field field = findFieldInHierarchy(clazz, fieldName);
            if (field == null) {
                throw new ReflectionException("Field not found: " + fieldName);
            }
            makeAccessible(field);
            return field;
        });
    }

    /**
     * 获取字段值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object target, String fieldName) {
        try {
            Field field = findField(target.getClass(), fieldName);
            return (T) field.get(target);
        } catch (IllegalAccessException e) {
            log.error("Field access denied: {}.{}", target.getClass().getName(), fieldName, e);
            throw new ReflectionException("Field access denied: " + fieldName, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object target, Field field) {
        try {
            return (T) field.get(target);
        } catch (IllegalAccessException e) {
            log.error("Field access denied: {}", field.getName(), e);
            throw new ReflectionException("Field access denied: " + field.getName(), e);
        }
    }

    /**
     * 设置字段值
     */
    public static void setFieldValue(Object target, String fieldName, Object value) {
        try {
            Field field = findField(target.getClass(), fieldName);
            Object convertedValue = TypeConverter.convert(value, field.getType());
            field.set(target, convertedValue);
        } catch (IllegalAccessException e) {
            log.error("Field set access denied: {}.{}", target.getClass().getName(), fieldName, e);
            throw new ReflectionException("Field set access denied: " + fieldName, e);
        }
    }

    /**
     * 获取所有字段
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            Field[] declaredFields = current.getDeclaredFields();
            for (Field field : declaredFields) {
                makeAccessible(field);
            }
            fields.addAll(Arrays.asList(declaredFields));
            current = current.getSuperclass();
        }
        return fields;
    }

    private static Field findFieldInHierarchy(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    // ====== 方法操作 ======

    /**
     * 查找方法
     */
    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        String cacheKey = generateMethodKey(clazz, methodName, paramTypes);

        Method method = METHOD_CACHE.computeIfAbsent(cacheKey, key -> {
            METHOD_CACHE_MISSES.incrementAndGet();
            try {
                Method m = clazz.getDeclaredMethod(methodName, paramTypes);
                makeAccessible(m);
                validateMethodAccess(m);
                return m;
            } catch (NoSuchMethodException e) {
                Method m = findMethodInHierarchy(clazz, methodName, paramTypes);
                if (m != null) {
                    makeAccessible(m);
                    validateMethodAccess(m);
                    return m;
                }
                log.error("Method not found: {}.{}", clazz.getName(), methodName, e);
                throw new ReflectionException("Method not found: " + methodName, e);
            }
        });

        METHOD_CACHE_HITS.incrementAndGet();
        return method;
    }

    /**
     * 调用方法
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Object target, String methodName, Object... args) {
        try {
            Class<?>[] paramTypes = getParameterTypes(args);
            Method method = findMethod(target.getClass(), methodName, paramTypes);
            return (T) method.invoke(target, args);
        } catch (IllegalAccessException e) {
            log.error("Method access denied: {}.{}", target.getClass().getName(), methodName, e);
            throw new ReflectionException("Method access denied: " + methodName, e);
        } catch (InvocationTargetException e) {
            log.error("Method invocation failed: {}.{}", target.getClass().getName(), methodName, e);
            throw new ReflectionException("Method invocation failed: " + methodName, e.getTargetException());
        }
    }

    /**
     * 高性能方法调用（MethodHandle）
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeMethodWithHandle(Object target, String methodName, Object... args) {
        String cacheKey = generateMethodKey(target.getClass(), methodName, getParameterTypes(args));

        MethodHandle handle = METHOD_HANDLE_CACHE.computeIfAbsent(cacheKey, k -> {
            try {
                Method method = findMethod(target.getClass(), methodName, getParameterTypes(args));
                return MethodHandles.lookup().unreflect(method);
            } catch (IllegalAccessException e) {
                log.error("Failed to create MethodHandle for: {}.{}",
                        target.getClass().getName(), methodName, e);
                throw new ReflectionException("Failed to create MethodHandle", e);
            }
        });

        try {
            return (T) handle.invokeWithArguments(prependTarget(target, args));
        } catch (Throwable e) {
            log.error("MethodHandle invocation failed: {}.{}",
                    target.getClass().getName(), methodName, e);
            throw new ReflectionException("MethodHandle invocation failed", e);
        }
    }

    /**
     * 调用静态方法
     */
    public static <T> T invokeStaticMethod(Class<?> clazz, String methodName, Object... args) {
        try {
            Class<?>[] paramTypes = getParameterTypes(args);
            Method method = findMethod(clazz, methodName, paramTypes);
            validateMethodAccess(method);
            @SuppressWarnings("unchecked")
            T result = (T) method.invoke(null, args);
            return result;
        } catch (Exception e) {
            log.error("Static method invocation failed: {}.{}", clazz.getName(), methodName, e);
            throw new ReflectionException("Static method invocation failed: " + methodName, e);
        }
    }

    private static Method findMethodInHierarchy(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredMethod(methodName, paramTypes);
            } catch (NoSuchMethodException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    // ====== 类型转换器 ======

    /**
     * 设置可访问性
     */
    public static void makeAccessible(AccessibleObject accessible) {
        if (!accessible.isAccessible()) {
            accessible.setAccessible(true);
        }
    }

    // ====== 工具方法 ======

    /**
     * 获取参数类型数组
     */
    public static Class<?>[] getParameterTypes(Object[] args) {
        if (args == null) return new Class[0];
        Class<?>[] paramTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i] != null ? args[i].getClass() : Object.class;
        }
        return paramTypes;
    }

    private static void validateClassPackage(Class<?> clazz) {
        String packageName = clazz.getPackage() != null ? clazz.getPackage().getName() : "";
        boolean allowed = ALLOWED_PACKAGES.stream().anyMatch(pattern -> {
            if (pattern.endsWith(".*")) {
                String base = pattern.substring(0, pattern.length() - 2);
                return packageName.startsWith(base);
            } else if (pattern.endsWith(".")) {
                return packageName.startsWith(pattern);
            }
            return packageName.equals(pattern);
        });

        if (!allowed) {
            log.warn("Security policy blocked access to: {}", clazz.getName());
            throw new SecurityException("Security policy blocked access to: " + clazz.getName());
        }
    }

    // ====== 安全验证 ======

    private static void validateMethodAccess(Method method) {
        String methodName = method.getName().toLowerCase();
        boolean dangerous = DENIED_METHOD_PATTERNS.stream()
                .anyMatch(pattern -> methodName.contains(pattern));

        if (dangerous) {
            log.warn("Dangerous method call detected: {}", method.getName());
            throw new SecurityException("Dangerous method call detected: " + method.getName());
        }
    }

    private static String generateMethodKey(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
        StringBuilder key = new StringBuilder();
        key.append(clazz.getName()).append("#").append(methodName);
        if (paramTypes != null) {
            for (Class<?> paramType : paramTypes) {
                key.append("_").append(paramType.getName());
            }
        }
        return key.toString();
    }

    // ====== 性能工具 ======

    private static Object[] prependTarget(Object target, Object[] args) {
        Object[] newArgs = new Object[args.length + 1];
        newArgs[0] = target;
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return newArgs;
    }

    private static boolean isParamTypesMatch(Class<?>[] expected, Class<?>[] actual) {
        if (expected.length != actual.length) return false;
        for (int i = 0; i < expected.length; i++) {
            if (!isAssignable(expected[i], actual[i])) return false;
        }
        return true;
    }

    private static boolean isAssignable(Class<?> expected, Class<?> actual) {
        if (expected.isPrimitive()) {
            return isPrimitiveAssignable(expected, actual);
        }
        return expected.isAssignableFrom(actual);
    }

    private static boolean isPrimitiveAssignable(Class<?> primitive, Class<?> actual) {
        if (!actual.isPrimitive()) {
            return (primitive == int.class && actual == Integer.class) ||
                    (primitive == long.class && actual == Long.class) ||
                    (primitive == boolean.class && actual == Boolean.class) ||
                    (primitive == double.class && actual == Double.class) ||
                    (primitive == float.class && actual == Float.class) ||
                    (primitive == char.class && actual == Character.class) ||
                    (primitive == byte.class && actual == Byte.class) ||
                    (primitive == short.class && actual == Short.class);
        }
        return primitive.equals(actual);
    }

    /**
     * 获取缓存统计
     */
    public static Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("classCache", CLASS_CACHE.size());
        stats.put("methodCache", METHOD_CACHE.size());
        stats.put("fieldCache", FIELD_CACHE.size());
        stats.put("methodHandleCache", METHOD_HANDLE_CACHE.size());
        stats.put("methodCacheHits", METHOD_CACHE_HITS.intValue());
        stats.put("methodCacheMisses", METHOD_CACHE_MISSES.intValue());
        return stats;
    }

    /**
     * 清空缓存
     */
    public static void clearCache() {
        CLASS_CACHE.clear();
        METHOD_CACHE.clear();
        FIELD_CACHE.clear();
        METHOD_HANDLE_CACHE.clear();
        log.info("ReflectUtils cache cleared");
    }

    /**
     * 基础类型转换器
     */
    public static class TypeConverter {
        private static final Map<Class<?>, Converter<?>> CONVERTERS = new ConcurrentHashMap<>();

        static {
            // 注册基础类型转换
            registerConverter(String.class, Object::toString);
            registerConverter(Integer.class, obj -> Integer.valueOf(obj.toString()));
            registerConverter(int.class, obj -> Integer.parseInt(obj.toString()));
            registerConverter(Long.class, obj -> Long.valueOf(obj.toString()));
            registerConverter(long.class, obj -> Long.parseLong(obj.toString()));
            registerConverter(Boolean.class, obj -> Boolean.valueOf(obj.toString()));
            registerConverter(boolean.class, obj -> Boolean.parseBoolean(obj.toString()));
            registerConverter(Double.class, obj -> Double.valueOf(obj.toString()));
            registerConverter(double.class, obj -> Double.parseDouble(obj.toString()));
            registerConverter(Float.class, obj -> Float.valueOf(obj.toString()));
            registerConverter(float.class, obj -> Float.parseFloat(obj.toString()));
        }

        /**
         * 基础类型转换
         */
        @SuppressWarnings("unchecked")
        public static <T> T convert(Object value, Class<T> targetType) {
            if (value == null) return null;
            if (targetType.isInstance(value)) return (T) value;

            Converter<T> converter = (Converter<T>) CONVERTERS.get(targetType);
            if (converter != null) {
                return converter.convert(value);
            }

            // 枚举类型处理
            if (targetType.isEnum()) {
                return convertToEnum(value, targetType);
            }

            log.warn("Unsupported type conversion: {} -> {}", value.getClass().getSimpleName(), targetType.getSimpleName());
            return (T) value;
        }

        @SuppressWarnings("unchecked")
        private static <T> T convertToEnum(Object value, Class<T> enumType) {
            try {
                return (T) Enum.valueOf((Class<Enum>) enumType, value.toString());
            } catch (IllegalArgumentException e) {
                log.warn("Enum value not found: {} in {}", value, enumType.getSimpleName());
                return null;
            }
        }

        public static <T> void registerConverter(Class<T> targetType, Converter<T> converter) {
            CONVERTERS.put(targetType, converter);
        }

        @FunctionalInterface
        public interface Converter<T> {
            T convert(Object value);
        }
    }

    public static class ReflectionException extends RuntimeException {
        /**
         * 构造反射异常
         *
         * @param message 异常消息
         */
        public ReflectionException(String message) {
            super(message);
        }

        /**
         * 构造反射异常
         *
         * @param message 异常消息
         * @param cause   根本原因
         */
        public ReflectionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}