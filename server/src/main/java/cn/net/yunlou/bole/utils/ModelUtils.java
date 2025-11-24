package cn.net.yunlou.bole.utils;

import cn.net.yunlou.bole.common.BaseModel;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 模型工具类 - 专注于BeseModel及其子类的特殊处理
 * <p>
 * 职责范围：
 * ✅ BeseModel特定格式转换
 * ✅ 下划线到驼峰命名字段名转换
 * ✅ 嵌套BeseModel递归处理
 * ✅ 与BeanUtils和ReflectUtils整合
 *
 * @author javaeer
 * @since 2019/9/10
 */
@Slf4j
public class ModelUtils {

    // ====== 简单类型判断 ======
    private static final Set<Class<?>> SIMPLE_TYPES = Set.of(
            int.class, Integer.class, boolean.class, Boolean.class, float.class, Float.class,
            double.class, Double.class, long.class, Long.class, byte.class, Byte.class,
            short.class, Short.class, java.math.BigInteger.class, java.math.BigDecimal.class,
            char.class, String.class, Date.class, java.time.LocalDate.class,
            java.time.LocalDateTime.class, java.time.LocalTime.class
    );

    /**
     * 私有构造函数
     */
    private ModelUtils() {
        log.debug("ModelUtils initialized");
    }

    // ====== 核心转换方法 ======

    /**
     * 将BeseModel转换为Map（支持下划线到驼峰命名转换）
     *
     * <p>只支持继承自BeseModel的对象</p>
     *
     * @param model BeseModel实例
     * @param <M>   模型类型
     * @return 转换后的Map
     */
    public static <M> Map<String, Object> modelToMap(M model) {
        return modelToMap(model, true);
    }

    /**
     * 将BeseModel转换为Map
     *
     * @param model            BeseModel实例
     * @param convertFieldName 是否转换字段名（下划线转驼峰）
     * @param <M>              模型类型
     * @return 转换后的Map
     */
    public static <M> Map<String, Object> modelToMap(M model, boolean convertFieldName) {
        Map<String, Object> map = new HashMap<>();

        try {
            if (!(model instanceof BaseModel)) {
                log.warn("Model is null or not instance of BeseModel, returning empty map");
                return map;
            }

            // 使用ReflectUtils获取所有字段
            List<Field> fields = ReflectUtils.getAllFields(model.getClass());

            for (Field field : fields) {
                processField(model, field, map, convertFieldName);
            }

            log.debug("Successfully converted BeseModel to map: {}", model.getClass().getSimpleName());
            return map;

        } catch (Exception e) {
            log.error("Failed to convert BeseModel to map: {}", model.getClass().getSimpleName(), e);
            throw new ModelConversionException("Model to map conversion failed", e);
        }
    }

    /**
     * 处理单个字段
     */
    private static void processField(Object model, Field field, Map<String, Object> map, boolean convertFieldName) {
        try {
            // 跳过不需要处理的字段
            if (shouldSkipField(field)) {
                return;
            }

            String fieldName = field.getName();
            String mapKey = convertFieldName ? NamedUtils.toCamelCase(fieldName) : fieldName;
            Object fieldValue = ReflectUtils.getFieldValue(model, fieldName);

            // 处理空值
            if (fieldValue == null) {
                return;
            }

            // 根据字段类型处理值
            Object processedValue = processFieldValue(fieldValue, field.getType());
            map.put(mapKey, processedValue);

        } catch (Exception e) {
            log.warn("Failed to process field {} in model {}, skipping",
                    field.getName(), model.getClass().getSimpleName(), e);
        }
    }

    /**
     * 处理字段值
     */
    @SuppressWarnings("unchecked")
    private static Object processFieldValue(Object fieldValue, Class<?> fieldType) {
        // 简单类型直接返回
        if (isSimpleType(fieldType)) {
            return fieldValue;
        }

        // 数组类型处理
        if (fieldType.isArray()) {
            return processArrayValue(fieldValue);
        }

        // 集合类型处理
        if (fieldValue instanceof List) {
            return processListValue((List<?>) fieldValue);
        }

        // Map类型处理
        if (fieldValue instanceof Map) {
            return processMapValue((Map<?, ?>) fieldValue);
        }

        // BeseModel递归处理
        if (fieldValue instanceof BaseModel) {
            return modelToMap(fieldValue);
        }

        // 其他自定义对象，尝试转换为Map
        return tryConvertToMap(fieldValue);
    }

    /**
     * 处理数组值
     */
    private static Object processArrayValue(Object arrayValue) {
        int length = Array.getLength(arrayValue);
        List<Object> result = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            Object element = Array.get(arrayValue, i);
            result.add(processSingleElement(element));
        }

        return result;
    }

    /**
     * 处理List值
     */
    private static Object processListValue(List<?> listValue) {
        List<Object> result = new ArrayList<>(listValue.size());

        for (Object element : listValue) {
            result.add(processSingleElement(element));
        }

        return result;
    }

    /**
     * 处理Map值
     */
    private static Object processMapValue(Map<?, ?> mapValue) {
        Map<String, Object> result = new HashMap<>(mapValue.size());

        for (Map.Entry<?, ?> entry : mapValue.entrySet()) {
            String key = String.valueOf(entry.getKey());
            Object value = processSingleElement(entry.getValue());
            result.put(key, value);
        }

        return result;
    }

    /**
     * 处理单个元素
     */
    private static Object processSingleElement(Object element) {
        if (element == null) {
            return null;
        }

        if (isSimpleType(element.getClass())) {
            return element;
        }

        if (element.getClass().isArray()) {
            return processArrayValue(element);
        }

        if (element instanceof List) {
            return processListValue((List<?>) element);
        }

        if (element instanceof Map) {
            return processMapValue((Map<?, ?>) element);
        }

        if (element instanceof BaseModel) {
            return modelToMap(element);
        }

        return tryConvertToMap(element);
    }

    /**
     * 尝试将对象转换为Map
     */
    private static Object tryConvertToMap(Object obj) {
        try {
            // 使用BeanUtils的通用转换
            return BeanUtils.toMap(obj);
        } catch (Exception e) {
            log.debug("Failed to convert object to map, using original value: {}",
                    obj.getClass().getSimpleName());
            return obj;
        }
    }

    /**
     * 判断是否应该跳过字段
     */
    private static boolean shouldSkipField(Field field) {

        int modifiers = field.getModifiers();

        return Modifier.isTransient(modifiers) ||
                Modifier.isStatic(modifiers) ||
                Modifier.isFinal(modifiers);
    }

    /**
     * 判断是否为简单类型
     */
    private static boolean isSimpleType(Class<?> clazz) {
        return SIMPLE_TYPES.contains(clazz) ||
                clazz.isEnum() ||
                isPrimitiveWrapper(clazz);
    }

    /**
     * 判断是否为基本类型的包装类
     */
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

    // ====== 模型到Bean转换 ======

    /**
     * 将BeseModel转换为指定类型的Bean
     *
     * @param om    BeseModel实例
     * @param clazz 目标Bean类型
     * @param <T>   目标类型
     * @return 转换后的Bean
     */
    public static <T> T modelToBean(BaseModel om, Class<T> clazz) {
        return modelToBean(om, clazz, false);
    }

    /**
     * 将BeseModel转换为指定类型的Bean
     *
     * @param om               BeseModel实例
     * @param clazz            目标Bean类型
     * @param convertFieldName 是否转换字段名
     * @param <T>              目标类型
     * @return 转换后的Bean
     */
    public static <T> T modelToBean(BaseModel om, Class<T> clazz, boolean convertFieldName) {
        if (om == null) {
            return null;
        }

        try {
            Map<String, Object> map = modelToMap(om, convertFieldName);
            return BeanUtils.fromMap(map, clazz);
        } catch (Exception e) {
            log.error("Failed to convert BeseModel to bean: {}", clazz.getSimpleName(), e);
            throw new ModelConversionException("Model to bean conversion failed", e);
        }
    }

    /**
     * 批量将BeseModel列表转换为Bean列表
     *
     * @param models BeseModel列表
     * @param clazz  目标Bean类型
     * @param <T>    目标类型
     * @return 转换后的Bean列表
     */
    public static <T> List<T> modelToBeans(List<? extends BaseModel> models, Class<T> clazz) {
        return modelToBeans(models, clazz, true);
    }

    /**
     * 批量将BeseModel列表转换为Bean列表
     *
     * @param models           BeseModel列表
     * @param clazz            目标Bean类型
     * @param convertFieldName 是否转换字段名
     * @param <T>              目标类型
     * @return 转换后的Bean列表
     */
    public static <T> List<T> modelToBeans(List<? extends BaseModel> models, Class<T> clazz, boolean convertFieldName) {
        if (models == null || models.isEmpty()) {
            return Collections.emptyList();
        }

        return models.stream()
                .map(model -> modelToBean(model, clazz, convertFieldName))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    // ====== Bean到BeseModel转换 ======

    /**
     * 将Bean转换为BeseModel
     *
     * @param bean       源Bean
     * @param modelClass 目标BeseModel类型
     * @param <T>        BeseModel类型
     * @return 转换后的BeseModel
     */
    public static <T extends BaseModel> T beanToModel(Object bean, Class<T> modelClass) {
        return beanToModel(bean, modelClass, false);
    }

    /**
     * 将Bean转换为BeseModel
     *
     * @param bean             源Bean
     * @param modelClass       目标BeseModel类型
     * @param convertFieldName 是否转换字段名
     * @param <T>              BeseModel类型
     * @return 转换后的BeseModel
     */
    public static <T extends BaseModel> T beanToModel(Object bean, Class<T> modelClass, boolean convertFieldName) {
        if (bean == null) {
            return null;
        }

        try {
            Map<String, Object> map = BeanUtils.toMap(bean);
            Map<String, Object> convertedMap = convertFieldNames(map, convertFieldName);
            return BeanUtils.fromMap(convertedMap, modelClass);
        } catch (Exception e) {
            log.error("Failed to convert bean to BeseModel: {}", modelClass.getSimpleName(), e);
            throw new ModelConversionException("Bean to model conversion failed", e);
        }
    }

    /**
     * 转换Map中的字段名
     */
    private static Map<String, Object> convertFieldNames(Map<String, Object> map, boolean convertFieldName) {
        if (!convertFieldName) {
            return map;
        }

        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String convertedKey = NamedUtils.toCamelCase(entry.getKey());
            result.put(convertedKey, entry.getValue());
        }
        return result;
    }

    // ====== 工具方法 ======

    /**
     * 判断对象是否为BeseModel
     */
    public static boolean isBeseModel(Object obj) {
        return obj instanceof BaseModel;
    }

    /**
     * 获取BeseModel的类信息
     */
    public static Class<?> getBeseModelClass(Object obj) {
        return isBeseModel(obj) ? obj.getClass() : null;
    }

    // ====== 异常类 ======

    public static class ModelConversionException extends RuntimeException {
        public ModelConversionException(String message) {
            super(message);
        }

        public ModelConversionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}