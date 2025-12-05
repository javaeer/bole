package cn.net.yunlou.bole.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 精简版反射工具类 - 底层反射操作基础层
 *
 * <p>职责范围： </p>
 * <p>✅ 基础字段操作（获取、设置、查找） </p>
 * <p>✅ 基础方法操作（调用、查找） </p>
 * <p>✅ 安全对象创建 </p>
 * <p>✅ 基础类型转换 </p>
 * <p>✅ 基础缓存管理</p>
 *
 * @author Mr.Wang
 * @version 3.0.0
 * @since 2024
 */
@Component
@Slf4j
public final class ReflectUtils {
    // 缓存
    private static final Map<String, Class<?>> CLASS_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Method> METHOD_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Field> FIELD_CACHE = new ConcurrentHashMap<>();

    /**
     * 安全加载类
     */
    public static Class<?> loadClass(String className) {
        if (className == null || className.trim().isEmpty()) {
            throw new ReflectionException("Class name cannot be null");
        }

        return CLASS_CACHE.computeIfAbsent(className, name -> {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                throw new ReflectionException("Class not found: " + name, e);
            }
        });
    }

    /**
     * 获取字段值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object target, String fieldName) {
        try {
            Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (Exception e) {
            log.error("Get field {} failed", fieldName, e);
            throw new ReflectionException("Get field failed: " + fieldName, e);
        }
    }

    /**
     * 设置字段值
     */
    public static void setFieldValue(Object target, String fieldName, Object value) {
        try {
            Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, TypeConverter.convert(value, field.getType()));
        } catch (Exception e) {
            log.error("Set field {} failed", fieldName, e);
            throw new ReflectionException("Set field failed: " + fieldName, e);
        }
    }

    /**
     * 查找字段
     */
    public static Field findField(Class<?> clazz, String fieldName) {
        String cacheKey = clazz.getName() + "#" + fieldName;
        return FIELD_CACHE.computeIfAbsent(cacheKey, key -> {
            Class<?> current = clazz;
            while (current != null && current != Object.class) {
                try {
                    return current.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    current = current.getSuperclass();
                }
            }
            throw new ReflectionException("Field not found: " + fieldName);
        });
    }

    /**
     * 获取所有字段
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;

        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                field.setAccessible(true);
                fields.add(field);
            }
            current = current.getSuperclass();
        }

        return fields;
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
        } catch (Exception e) {
            log.error("Invoke method {} failed", methodName, e);
            throw new ReflectionException("Invoke method failed: " + methodName, e);
        }
    }

    /**
     * 查找方法
     */
    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        String cacheKey = generateMethodKey(clazz, methodName, paramTypes);
        return METHOD_CACHE.computeIfAbsent(cacheKey, key -> {
            Class<?> current = clazz;
            while (current != null && current != Object.class) {
                try {
                    return current.getDeclaredMethod(methodName, paramTypes);
                } catch (NoSuchMethodException e) {
                    current = current.getSuperclass();
                }
            }
            throw new ReflectionException("Method not found: " + methodName);
        });
    }

    /**
     * 获取参数类型
     */
    private static Class<?>[] getParameterTypes(Object[] args) {
        if (args == null) return new Class[0];

        Class<?>[] paramTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i] != null ? args[i].getClass() : Object.class;
        }
        return paramTypes;
    }

    /**
     * 生成方法缓存key
     */
    private static String generateMethodKey(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
        StringBuilder key = new StringBuilder();
        key.append(clazz.getName()).append("#").append(methodName);

        if (paramTypes != null) {
            for (Class<?> type : paramTypes) {
                key.append("_").append(type.getName());
            }
        }

        return key.toString();
    }

    /**
     * 类型转换器
     */
    public static class TypeConverter {
        private static final Map<Class<?>, Converter<?>> CONVERTERS = new ConcurrentHashMap<>();

        static {
            // 注册基础转换器
            registerConverter(String.class, Object::toString);
            registerConverter(Integer.class, obj -> Integer.valueOf(obj.toString()));
            registerConverter(int.class, obj -> Integer.parseInt(obj.toString()));
            registerConverter(Long.class, obj -> Long.valueOf(obj.toString()));
            registerConverter(long.class, obj -> Long.parseLong(obj.toString()));
            registerConverter(Boolean.class, obj -> Boolean.valueOf(obj.toString()));
            registerConverter(boolean.class, obj -> Boolean.parseBoolean(obj.toString()));
            registerConverter(Double.class, obj -> Double.valueOf(obj.toString()));
            registerConverter(double.class, obj -> Double.parseDouble(obj.toString()));
        }

        /**
         * 类型转换
         */
        @SuppressWarnings("unchecked")
        public static <T> T convert(Object value, Class<T> targetType) {
            if (value == null) return null;
            if (targetType.isInstance(value)) return (T) value;

            Converter<T> converter = (Converter<T>) CONVERTERS.get(targetType);
            if (converter != null) {
                try {
                    return converter.convert(value);
                } catch (Exception e) {
                    log.debug("Converter failed for {} to {}",
                            value.getClass().getSimpleName(), targetType.getSimpleName(), e);
                }
            }

            // 枚举处理
            if (targetType.isEnum()) {
                return convertToEnum(value, targetType);
            }

            log.warn("No converter for {} to {}, return original",
                    value.getClass().getSimpleName(), targetType.getSimpleName());
            return (T) value;
        }

        /**
         * 转换为枚举
         */
        @SuppressWarnings("unchecked")
        private static <T> T convertToEnum(Object value, Class<T> enumType) {
            try {
                return (T) Enum.valueOf((Class<Enum>) enumType, value.toString());
            } catch (Exception e) {
                log.debug("Convert to enum failed: {}", value);
                return null;
            }
        }

        /**
         * 注册转换器
         */
        public static <T> void registerConverter(Class<T> targetType, Converter<T> converter) {
            CONVERTERS.put(targetType, converter);
        }

        /**
         * 转换器接口
         */
        @FunctionalInterface
        public interface Converter<T> {
            T convert(Object value);
        }
    }

    /**
     * 反射异常
     */
    public static class ReflectionException extends RuntimeException {
        public ReflectionException(String message) {
            super(message);
        }

        public ReflectionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
