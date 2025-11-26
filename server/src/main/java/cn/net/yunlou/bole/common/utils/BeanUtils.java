package cn.net.yunlou.bole.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 增强版Bean工具类 - 整合反射和Bean操作功能
 * <p>
 * 核心特性：
 * ✅ 基于BeanWrapper的高性能拷贝
 * ✅ 集成ReflectUtils的安全反射操作
 * ✅ 统一的缓存和异常处理
 * ✅ 支持复杂类型转换
 * ✅ Map与Bean智能互转
 */
@Slf4j
public class BeanUtils {

    // ====== 缓存配置 ======
    private static final ConcurrentMap<ClassPair, List<FieldMapping>> FIELD_MAPPING_CACHE = new ConcurrentHashMap<>(256);

    // ====== 简单类型判断 ======
    private static final Set<Class<?>> SIMPLE_TYPES = Set.of(
            int.class, Integer.class, boolean.class, Boolean.class, float.class, Float.class,
            double.class, Double.class, long.class, Long.class, byte.class, Byte.class,
            short.class, Short.class, java.math.BigInteger.class, java.math.BigDecimal.class,
            char.class, String.class, Date.class, java.time.LocalDate.class,
            java.time.LocalDateTime.class, java.time.LocalTime.class
    );

    private BeanUtils() {
        log.debug("BeanUtils initialized with BeanWrapper");
    }

    // ====== Bean拷贝核心方法 ======

    /**
     * 基于反射的Bean属性拷贝
     */
    public static <T> T copyProperties(Object source, Class<T> targetClass) {
        return copyProperties(source, targetClass, null);
    }

    public static <T> T copyProperties(Object source, Class<T> targetClass, PropertyConverter converter) {
        if (Objects.isNull(source)) {
            return null;
        }

        try {
            T targetInstance = createInstance(targetClass);
            copyProperties(source, targetInstance, converter);
            return targetInstance;
        } catch (Exception e) {
            log.error("Bean copy failed from {} to {}",
                    source.getClass().getSimpleName(), targetClass.getSimpleName(), e);
            throw new BeanOperationException("Bean copy failed", e);
        }
    }

    /**
     * 对象到对象的属性拷贝
     */
    public static void copyProperties(Object source, Object target) {
        copyProperties(source, target, null);
    }

    public static void copyProperties(Object source, Object target, PropertyConverter converter) {
        if (Objects.isNull(source) || Objects.isNull(target)) {
            return;
        }

        try {
            List<FieldMapping> fieldMappings = getFieldMappings(source.getClass(), target.getClass());

            for (FieldMapping mapping : fieldMappings) {
                try {
                    Object sourceValue = ReflectUtils.getFieldValue(source, mapping.sourceField);
                    Object targetValue = convertValue(sourceValue, mapping.targetField.getType(), converter);

                    if (targetValue != null || mapping.targetField.getType().isPrimitive()) {
                        ReflectUtils.setFieldValue(target, mapping.targetField.getName(), targetValue);
                    }
                } catch (Exception e) {
                    log.debug("Failed to copy field {} to {}, skipping",
                            mapping.sourceField.getName(), mapping.targetField.getName(), e);
                }
            }
        } catch (Exception e) {
            log.error("Bean copy failed from {} to {}",
                    source.getClass().getSimpleName(), target.getClass().getSimpleName(), e);
            throw new BeanOperationException("Bean copy failed", e);
        }
    }

    /**
     * 批量Bean拷贝
     */
    public static <T> List<T> copyList(List<?> sources, Class<T> targetClass) {
        return copyList(sources, targetClass, null);
    }

    public static <T> List<T> copyList(List<?> sources, Class<T> targetClass, PropertyConverter converter) {
        if (Objects.isNull(sources) || sources.isEmpty()) {
            return Collections.emptyList();
        }

        return sources.stream()
                .filter(Objects::nonNull)
                .map(source -> copyProperties(source, targetClass, converter))
                .collect(Collectors.toList());
    }

    // ====== Map与Bean互转 ======

    /**
     * Bean转Map（基于反射）
     */
    public static Map<String, Object> toMap(Object source) {
        return toMap(source, false);
    }

    public static Map<String, Object> toMap(Object source, boolean includeNulls) {
        if (Objects.isNull(source)) {
            return Collections.emptyMap();
        }

        try {
            Map<String, Object> result = new HashMap<>();
            List<Field> fields = ReflectUtils.getAllFields(source.getClass());

            for (Field field : fields) {
                if (shouldSkipField(field)) {
                    continue;
                }

                try {
                    Object value = ReflectUtils.getFieldValue(source, field.getName());
                    if (includeNulls || Objects.nonNull(value)) {
                        Object processedValue = processValueForMap(value);
                        result.put(field.getName(), processedValue);
                    }
                } catch (Exception e) {
                    log.debug("Failed to get field {} from {}, skipping",
                            field.getName(), source.getClass().getSimpleName(), e);
                }
            }

            return result;
        } catch (Exception e) {
            log.error("Bean to map conversion failed: {}", source.getClass().getSimpleName(), e);
            throw new BeanOperationException("Bean to map conversion failed", e);
        }
    }

    /**
     * Map转Bean（基于Spring BeanWrapper）
     */
    public static <T> T fromMap(Map<String, Object> map, Class<T> targetClass) {
        if (Objects.isNull(map) || map.isEmpty()) {
            return createInstance(targetClass);
        }

        try {
            T instance = createInstance(targetClass);
            BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(instance);

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (Objects.nonNull(value) && wrapper.isWritableProperty(key)) {
                    try {
                        Object processedValue = processValueFromMap(value, targetClass, key);
                        wrapper.setPropertyValue(key, processedValue);
                    } catch (Exception e) {
                        log.debug("Failed to set property {} on {}, skipping",
                                key, targetClass.getSimpleName(), e);
                    }
                }
            }

            return instance;
        } catch (Exception e) {
            log.error("Map to bean conversion failed: {}", targetClass.getSimpleName(), e);
            throw new BeanOperationException("Map to bean conversion failed", e);
        }
    }

    // ====== 属性访问器 ======

    public static Object getProperty(Object bean, String propertyName) {
        return ReflectUtils.getFieldValue(bean, propertyName);
    }

    public static void setProperty(Object bean, String propertyName, Object value) {
        ReflectUtils.setFieldValue(bean, propertyName, value);
    }

    public static void setProperties(Object bean, Map<String, Object> properties) {
        if (Objects.isNull(bean) || Objects.isNull(properties)) {
            return;
        }

        properties.forEach((key, value) -> {
            try {
                setProperty(bean, key, value);
            } catch (Exception e) {
                log.warn("Failed to set property {} on {}, skipping", key, bean.getClass().getSimpleName(), e);
            }
        });
    }

    // ====== 高级功能 ======

    /**
     * 获取Bean的所有属性名
     */
    public static List<String> getPropertyNames(Class<?> clazz) {
        return ReflectUtils.getAllFields(clazz).stream()
                .filter(field -> !shouldSkipField(field))
                .map(Field::getName)
                .collect(Collectors.toList());
    }

    /**
     * 获取Bean的属性类型
     */
    public static Class<?> getPropertyType(Class<?> clazz, String propertyName) {
        try {
            Field field = ReflectUtils.findField(clazz, propertyName);
            return field.getType();
        } catch (Exception e) {
            log.debug("Failed to get property type for {} in {}", propertyName, clazz.getSimpleName(), e);
            return null;
        }
    }

    /**
     * 判断Bean是否包含指定属性
     */
    public static boolean hasProperty(Class<?> clazz, String propertyName) {
        try {
            ReflectUtils.findField(clazz, propertyName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ====== 私有方法 ======

    private static <T> T createInstance(Class<T> clazz) {
        return ReflectUtils.createInstance(clazz);
    }

    /**
     * 获取字段映射关系
     */
    private static List<FieldMapping> getFieldMappings(Class<?> sourceClass, Class<?> targetClass) {
        ClassPair key = new ClassPair(sourceClass, targetClass);
        return FIELD_MAPPING_CACHE.computeIfAbsent(key, k -> {
            List<FieldMapping> mappings = new ArrayList<>();
            List<Field> sourceFields = ReflectUtils.getAllFields(sourceClass);
            List<Field> targetFields = ReflectUtils.getAllFields(targetClass);

            Map<String, Field> targetFieldMap = targetFields.stream()
                    .filter(field -> !shouldSkipField(field))
                    .collect(Collectors.toMap(Field::getName, field -> field));

            for (Field sourceField : sourceFields) {
                if (shouldSkipField(sourceField)) {
                    continue;
                }

                Field targetField = targetFieldMap.get(sourceField.getName());
                if (targetField != null && isAssignable(sourceField.getType(), targetField.getType())) {
                    mappings.add(new FieldMapping(sourceField, targetField));
                }
            }

            return Collections.unmodifiableList(mappings);
        });
    }

    /**
     * 值转换
     */
    private static Object convertValue(Object value, Class<?> targetType, PropertyConverter converter) {
        if (value == null) {
            return null;
        }

        // 使用自定义转换器
        if (converter != null) {
            Object converted = converter.convert(value, targetType);
            if (converted != null) {
                return converted;
            }
        }

        // 使用内置类型转换器
        return ReflectUtils.TypeConverter.convert(value, targetType);
    }

    /**
     * 处理Map值转换
     */
    @SuppressWarnings("unchecked")
    private static Object processValueForMap(Object value) {
        if (value == null) return null;

        if (isSimpleType(value.getClass())) {
            return value;
        }

        if (value.getClass().isArray()) {
            return processArrayForMap(value);
        }

        if (value instanceof Collection) {
            return processCollectionForMap((Collection<?>) value);
        }

        if (value instanceof Map) {
            return processMapForMap((Map<?, ?>) value);
        }

        return toMap(value);
    }

    /**
     * 从Map值转换处理
     */
    private static Object processValueFromMap(Object value, Class<?> targetClass, String fieldName) {
        try {
            Class<?> fieldType = getFieldType(targetClass, fieldName);
            if (fieldType == null) {
                return value;
            }

            if (isSimpleType(fieldType)) {
                return ReflectUtils.TypeConverter.convert(value, fieldType);
            }

            if (fieldType.isArray()) {
                return processArrayFromMap(value, fieldType);
            }

            if (Collection.class.isAssignableFrom(fieldType)) {
                return processCollectionFromMap(value, targetClass, fieldName);
            }

            if (Map.class.isAssignableFrom(fieldType)) {
                return processMapFromMap(value, targetClass, fieldName);
            }

            if (value instanceof Map) {
                return fromMap((Map<String, Object>) value, fieldType);
            }

            return value;
        } catch (Exception e) {
            log.debug("Value processing failed for field {}: {}", fieldName, e.getMessage());
            return value;
        }
    }

    // ====== 类型处理辅助方法 ======

    private static Object processArrayForMap(Object array) {
        int length = Array.getLength(array);
        List<Object> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            result.add(processValueForMap(Array.get(array, i)));
        }
        return result;
    }

    private static Object processCollectionForMap(Collection<?> collection) {
        return collection.stream()
                .map(BeanUtils::processValueForMap)
                .collect(Collectors.toList());
    }

    private static Object processMapForMap(Map<?, ?> map) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            result.put(String.valueOf(entry.getKey()), processValueForMap(entry.getValue()));
        }
        return result;
    }

    private static Object processArrayFromMap(Object value, Class<?> arrayType) {
        if (!(value instanceof Collection)) {
            return value;
        }

        Collection<?> collection = (Collection<?>) value;
        Class<?> componentType = arrayType.getComponentType();
        Object array = Array.newInstance(componentType, collection.size());

        int i = 0;
        for (Object element : collection) {
            Object processedElement = processSingleElementFromMap(element, componentType);
            Array.set(array, i++, processedElement);
        }

        return array;
    }

    @SuppressWarnings("unchecked")
    private static Object processCollectionFromMap(Object value, Class<?> targetClass, String fieldName) {
        if (!(value instanceof Collection)) {
            return value;
        }

        try {
            Field field = targetClass.getDeclaredField(fieldName);
            Type genericType = field.getGenericType();

            if (!(genericType instanceof ParameterizedType)) {
                return value;
            }

            ParameterizedType pt = (ParameterizedType) genericType;
            Type elementType = pt.getActualTypeArguments()[0];
            Class<?> elementClass = getClassFromType(elementType);

            Collection<Object> result = createCollectionInstance(field.getType());
            for (Object element : (Collection<?>) value) {
                result.add(processSingleElementFromMap(element, elementClass));
            }

            return result;
        } catch (Exception e) {
            log.debug("Failed to process collection for field {}, using original value", fieldName);
            return value;
        }
    }

    @SuppressWarnings("unchecked")
    private static Object processMapFromMap(Object value, Class<?> targetClass, String fieldName) {
        if (!(value instanceof Map)) {
            return value;
        }

        try {
            Field field = targetClass.getDeclaredField(fieldName);
            Type genericType = field.getGenericType();

            if (!(genericType instanceof ParameterizedType)) {
                return value;
            }

            ParameterizedType pt = (ParameterizedType) genericType;
            Type valueType = pt.getActualTypeArguments()[1];
            Class<?> valueClass = getClassFromType(valueType);

            Map<String, Object> result = new HashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                String key = String.valueOf(entry.getKey());
                Object processedValue = processSingleElementFromMap(entry.getValue(), valueClass);
                result.put(key, processedValue);
            }

            return result;
        } catch (Exception e) {
            log.debug("Failed to process map for field {}, using original value", fieldName);
            return value;
        }
    }

    private static Object processSingleElementFromMap(Object element, Class<?> targetClass) {
        if (element == null) {
            return null;
        }

        if (isSimpleType(targetClass)) {
            return ReflectUtils.TypeConverter.convert(element, targetClass);
        }

        if (element instanceof Map) {
            return fromMap((Map<String, Object>) element, targetClass);
        }

        return element;
    }

    private static Class<?> getFieldType(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return field.getType();
        } catch (NoSuchFieldException e) {
            return findFieldTypeInHierarchy(clazz, fieldName);
        }
    }

    private static Class<?> findFieldTypeInHierarchy(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                Field field = current.getDeclaredField(fieldName);
                return field.getType();
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    private static Class<?> getClassFromType(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else {
            return Object.class;
        }
    }

    @SuppressWarnings("unchecked")
    private static Collection<Object> createCollectionInstance(Class<?> collectionType) {
        if (collectionType.isInterface()) {
            if (List.class.isAssignableFrom(collectionType)) {
                return new ArrayList<>();
            } else if (Set.class.isAssignableFrom(collectionType)) {
                return new HashSet<>();
            } else {
                return new ArrayList<>();
            }
        }

        try {
            return (Collection<Object>) collectionType.newInstance();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private static boolean shouldSkipField(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isTransient(modifiers) ||
                Modifier.isStatic(modifiers) ||
                Modifier.isFinal(modifiers);
    }

    private static boolean isSimpleType(Class<?> clazz) {
        return SIMPLE_TYPES.contains(clazz) ||
                clazz.isEnum() ||
                clazz.isPrimitive() ||
                isPrimitiveWrapper(clazz);
    }

    private static boolean isPrimitiveWrapper(Class<?> clazz) {
        return clazz == Integer.class ||
                clazz == Long.class ||
                clazz == Boolean.class ||
                clazz == Double.class ||
                clazz == Float.class ||
                clazz == Byte.class ||
                clazz == Short.class ||
                clazz == Character.class;
    }

    private static boolean isAssignable(Class<?> sourceType, Class<?> targetType) {
        if (sourceType.equals(targetType)) {
            return true;
        }

        if (sourceType.isPrimitive()) {
            return isPrimitiveAssignable(sourceType, targetType);
        }

        if (targetType.isPrimitive()) {
            return isPrimitiveAssignable(targetType, sourceType);
        }

        return targetType.isAssignableFrom(sourceType);
    }

    private static boolean isPrimitiveAssignable(Class<?> primitive, Class<?> wrapper) {
        return (primitive == int.class && wrapper == Integer.class) ||
                (primitive == long.class && wrapper == Long.class) ||
                (primitive == boolean.class && wrapper == Boolean.class) ||
                (primitive == double.class && wrapper == Double.class) ||
                (primitive == float.class && wrapper == Float.class) ||
                (primitive == char.class && wrapper == Character.class) ||
                (primitive == byte.class && wrapper == Byte.class) ||
                (primitive == short.class && wrapper == Short.class);
    }

    // ====== 内部类和接口 ======

    private static class ClassPair {
        private final Class<?> source;
        private final Class<?> target;
        private final int hashCode;

        ClassPair(Class<?> source, Class<?> target) {
            this.source = source;
            this.target = target;
            this.hashCode = Objects.hash(source, target);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClassPair classPair = (ClassPair) o;
            return Objects.equals(source, classPair.source) &&
                    Objects.equals(target, classPair.target);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }

    private static class FieldMapping {
        final Field sourceField;
        final Field targetField;

        FieldMapping(Field sourceField, Field targetField) {
            this.sourceField = sourceField;
            this.targetField = targetField;
        }
    }

    /**
     * 属性转换器接口
     */
    @FunctionalInterface
    public interface PropertyConverter {
        Object convert(Object value, Class<?> targetType);
    }

    public static class BeanOperationException extends RuntimeException {
        public BeanOperationException(String message) {
            super(message);
        }

        public BeanOperationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // ====== 缓存管理 ======

    /**
     * 清空缓存
     */
    public static void clearCache() {
        FIELD_MAPPING_CACHE.clear();
        log.info("BeanUtils cache cleared");
    }

    /**
     * 获取缓存统计
     */
    public static Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("fieldMappingCache", FIELD_MAPPING_CACHE.size());
        return stats;
    }
}