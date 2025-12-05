package cn.net.yunlou.bole.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 增强版Bean工具类 - 整合反射和Bean操作功能
 *
 * <p>核心特性： ✅ 基于BeanWrapper的高性能拷贝 ✅ 集成ReflectUtils的安全反射操作 ✅ 统一的缓存和异常处理 ✅ 支持复杂类型转换 ✅ Map与Bean智能互转
 */
@Slf4j
@Component
public class BeanUtils {
    // 简单类型集合
    private static final Set<Class<?>> SIMPLE_TYPES = Set.of(
            String.class, Integer.class, int.class, Long.class, long.class,
            Double.class, double.class, Float.class, float.class, Boolean.class, boolean.class,
            Byte.class, byte.class, Short.class, short.class, Character.class, char.class,
            Date.class, java.time.LocalDate.class, java.time.LocalDateTime.class,
            java.math.BigDecimal.class, java.math.BigInteger.class
    );

    /**
     * 创建实例（自动检测builder()静态方法）
     */
    public static <T> T createInstance(Class<T> clazz) {
        try {
            // 1. 尝试使用@Builder模式
            T builderInstance = tryCreateWithBuilder(clazz);
            if (builderInstance != null) {
                return builderInstance;
            }

            // 2. 尝试无参构造
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                // 3. 尝试全参构造（使用默认值）
                return createWithDefaultValues(clazz);
            }
        } catch (Exception e) {
            log.error("Failed to create instance of {}", clazz.getSimpleName(), e);
            throw new BeanOperationException("Create instance failed", e);
        }
    }

    /**
     * 尝试使用@Builder模式创建实例
     */
    private static <T> T tryCreateWithBuilder(Class<T> clazz) {
        try {
            // 查找builder()方法
            Method builderMethod = findBuilderMethod(clazz);
            if (builderMethod != null) {
                Object builder = builderMethod.invoke(null);

                // 查找build()方法
                Method buildMethod = findBuildMethod(builder.getClass());
                if (buildMethod != null) {
                    return clazz.cast(buildMethod.invoke(builder));
                }
            }
        } catch (Exception e) {
            log.debug("Builder pattern not available for {}", clazz.getSimpleName());
        }
        return null;
    }

    /**
     * 查找builder()静态方法
     */
    private static Method findBuilderMethod(Class<?> clazz) {
        try {
            return clazz.getMethod("builder");
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * 查找build()方法
     */
    private static Method findBuildMethod(Class<?> builderClass) {
        try {
            return builderClass.getMethod("build");
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * 使用默认值创建实例（用于无默认构造的情况）
     */
    private static <T> T createWithDefaultValues(Class<T> clazz) throws Exception {
        // 查找所有构造器，选择参数最多的一个
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Constructor<?> maxParamConstructor = Arrays.stream(constructors)
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow(() -> new NoSuchMethodException("No constructor found"));

        maxParamConstructor.setAccessible(true);
        Class<?>[] paramTypes = maxParamConstructor.getParameterTypes();
        Object[] defaultParams = Arrays.stream(paramTypes)
                .map(BeanUtils::getDefaultValue)
                .toArray();

        return clazz.cast(maxParamConstructor.newInstance(defaultParams));
    }

    /**
     * 获取类型的默认值
     */
    private static Object getDefaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;

        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == boolean.class) return false;
        if (type == double.class) return 0.0;
        if (type == float.class) return 0.0f;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == char.class) return '\0';
        return null;
    }

    /**
     * 属性拷贝（支持@Builder对象）
     */
    public static <T> T copyProperties(Object source, Class<T> targetClass) {
        if (source == null) return null;

        try {
            T target = createInstance(targetClass);
            copyProperties(source, target);
            return target;
        } catch (Exception e) {
            log.error("Copy properties failed", e);
            throw new BeanOperationException("Copy properties failed", e);
        }
    }

    /**
     * 对象到对象的属性拷贝
     */
    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) return;

        try {
            org.springframework.beans.BeanUtils.copyProperties(source, target);
        } catch (Exception e) {
            log.warn("Spring BeanUtils copy failed, fallback to reflection", e);
            // 降级处理：使用反射
            copyPropertiesByReflection(source, target);
        }
    }

    /**
     * 反射方式拷贝属性
     */
    private static void copyPropertiesByReflection(Object source, Object target) {
        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();

        // 获取所有字段
        Map<String, Field> sourceFields = getAllFieldsMap(sourceClass);
        Map<String, Field> targetFields = getAllFieldsMap(targetClass);

        // 拷贝匹配的字段
        for (Map.Entry<String, Field> entry : sourceFields.entrySet()) {
            String fieldName = entry.getKey();
            Field sourceField = entry.getValue();

            Field targetField = targetFields.get(fieldName);
            if (targetField != null && isAssignable(sourceField.getType(), targetField.getType())) {
                try {
                    sourceField.setAccessible(true);
                    targetField.setAccessible(true);

                    Object value = sourceField.get(source);
                    if (value != null) {
                        targetField.set(target, value);
                    }
                } catch (Exception e) {
                    log.debug("Copy field {} failed, skip", fieldName, e);
                }
            }
        }
    }

    /**
     * 批量拷贝
     */
    public static <T> List<T> copyList(List<?> sources, Class<T> targetClass) {
        if (sources == null || sources.isEmpty()) {
            return Collections.emptyList();
        }

        return sources.stream()
                .filter(Objects::nonNull)
                .map(source -> copyProperties(source, targetClass))
                .collect(Collectors.toList());
    }

    /**
     * Bean转Map
     */
    public static Map<String, Object> toMap(Object bean) {
        if (bean == null) return Collections.emptyMap();

        Map<String, Object> result = new HashMap<>();
        Map<String, Field> fields = getAllFieldsMap(bean.getClass());

        for (Map.Entry<String, Field> entry : fields.entrySet()) {
            try {
                Field field = entry.getValue();
                field.setAccessible(true);
                Object value = field.get(bean);

                if (value != null) {
                    result.put(field.getName(), convertForMap(value));
                }
            } catch (Exception e) {
                log.debug("Get field {} failed", entry.getKey(), e);
            }
        }

        return result;
    }

    /**
     * Map转Bean（支持@Builder）
     */
    public static <T> T fromMap(Map<String, Object> map, Class<T> targetClass) {
        if (map == null || map.isEmpty()) {
            return createInstance(targetClass);
        }

        try {
            // 尝试使用@Builder模式
            T builderInstance = tryCreateWithBuilderAndMap(map, targetClass);
            if (builderInstance != null) {
                return builderInstance;
            }

            // 使用BeanWrapper
            T instance = createInstance(targetClass);
            BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(instance);

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                if (wrapper.isWritableProperty(key)) {
                    try {
                        wrapper.setPropertyValue(key, entry.getValue());
                    } catch (Exception e) {
                        log.debug("Set property {} failed", key, e);
                    }
                }
            }

            return instance;
        } catch (Exception e) {
            log.error("Map to bean conversion failed", e);
            throw new BeanOperationException("Map to bean conversion failed", e);
        }
    }

    /**
     * 过滤实体无效值
     */
    public static <T> T filterInvalidValues(T entity) {
        return filterInvalidValues(entity, ValueUtils::isValid);
    }

    /**
     * 使用自定义验证器过滤无效值
     */
    public static <T> T filterInvalidValues(T entity, Predicate<Object> validator) {
        if (entity == null) return null;

        try {
            // 创建新实例
            @SuppressWarnings("unchecked")
            T newEntity = copyProperties(entity, (Class<T>) entity.getClass());

            // 过滤无效字段
            List<Field> fields = ReflectUtils.getAllFields(entity.getClass());
            for (Field field : fields) {
                Object value = ReflectUtils.getFieldValue(entity, field.getName());
                if (!validator.test(value)) {
                    ReflectUtils.setFieldValue(newEntity, field.getName(), null);
                }
            }

            return newEntity;
        } catch (Exception e) {
            log.warn("Filter invalid values failed", e);
            return entity;
        }
    }


    /**
     * 使用@Builder模式从Map创建对象
     */
    private static <T> T tryCreateWithBuilderAndMap(Map<String, Object> map, Class<T> targetClass) {
        try {
            Method builderMethod = findBuilderMethod(targetClass);
            if (builderMethod == null) return null;

            Object builder = builderMethod.invoke(null);
            Class<?> builderClass = builder.getClass();

            // 为Builder设置属性
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String fieldName = entry.getKey();
                try {
                    // Builder的setter方法格式：fieldName(value)
                    String setterName = fieldName;
                    Method setter = builderClass.getMethod(setterName, entry.getValue().getClass());
                    setter.invoke(builder, entry.getValue());
                } catch (NoSuchMethodException e) {
                    // 尝试兼容类型
                    log.debug("Builder setter {} not found", fieldName);
                } catch (Exception e) {
                    log.debug("Set builder property {} failed", fieldName, e);
                }
            }

            // 调用build()方法
            Method buildMethod = findBuildMethod(builderClass);
            return targetClass.cast(buildMethod.invoke(builder));

        } catch (Exception e) {
            log.debug("Create with builder and map failed, fallback to normal method", e);
            return null;
        }
    }

    /**
     * 为Map转换处理值
     */
    private static Object convertForMap(Object value) {
        if (value == null) return null;

        Class<?> clazz = value.getClass();
        if (isSimpleType(clazz) || clazz.isEnum()) {
            return value;
        }

        if (value instanceof Collection) {
            return ((Collection<?>) value).stream()
                    .map(BeanUtils::convertForMap)
                    .collect(Collectors.toList());
        }

        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            Map<String, Object> result = new HashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                result.put(String.valueOf(entry.getKey()), convertForMap(entry.getValue()));
            }
            return result;
        }

        // 其他对象转换为Map
        return toMap(value);
    }

    /**
     * 获取类的所有字段（包含父类）
     */
    private static Map<String, Field> getAllFieldsMap(Class<?> clazz) {
        Map<String, Field> fields = new HashMap<>();
        Class<?> current = clazz;

        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                // 跳过静态和final字段
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                    continue;
                }
                fields.putIfAbsent(field.getName(), field);
            }
            current = current.getSuperclass();
        }

        return fields;
    }

    /**
     * 判断是否是简单类型
     */
    private static boolean isSimpleType(Class<?> clazz) {
        return SIMPLE_TYPES.contains(clazz) || clazz.isPrimitive();
    }

    /**
     * 类型是否可赋值
     */
    private static boolean isAssignable(Class<?> sourceType, Class<?> targetType) {
        if (sourceType.equals(targetType)) return true;

        // 包装类型处理
        if (sourceType.isPrimitive()) {
            return isPrimitiveWrapper(sourceType, targetType);
        }

        if (targetType.isPrimitive()) {
            return isPrimitiveWrapper(targetType, sourceType);
        }

        return targetType.isAssignableFrom(sourceType);
    }

    /**
     * 判断基本类型和包装类型是否匹配
     */
    private static boolean isPrimitiveWrapper(Class<?> primitive, Class<?> wrapper) {
        return (primitive == int.class && wrapper == Integer.class) ||
                (primitive == long.class && wrapper == Long.class) ||
                (primitive == boolean.class && wrapper == Boolean.class) ||
                (primitive == double.class && wrapper == Double.class) ||
                (primitive == float.class && wrapper == Float.class) ||
                (primitive == char.class && wrapper == Character.class) ||
                (primitive == byte.class && wrapper == Byte.class) ||
                (primitive == short.class && wrapper == Short.class);
    }

    // 异常类
    public static class BeanOperationException extends RuntimeException {
        public BeanOperationException(String message) {
            super(message);
        }

        public BeanOperationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
