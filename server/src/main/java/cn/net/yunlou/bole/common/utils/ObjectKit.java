package cn.net.yunlou.bole.common.utils;

import java.util.Map;

public final class ObjectKit {

    private ObjectKit() {}

    // ====== 快速访问方法 ======

    public static <T> T copy(Object source, Class<T> targetClass) {
        return BeanUtils.copyProperties(source, targetClass);
    }

    public static <T> T fromMap(Map<String, Object> map, Class<T> targetClass) {
        return BeanUtils.fromMap(map, targetClass);
    }

    public static Map<String, Object> toMap(Object source) {
        return BeanUtils.toMap(source);
    }

    public static <T> T filter(T entity) {
        return EntityUtils.filterInvalidValues(entity);
    }

    public static Object getField(Object target, String fieldName) {
        return ReflectUtils.getFieldValue(target, fieldName);
    }

    public static void setField(Object target, String fieldName, Object value) {
        ReflectUtils.setFieldValue(target, fieldName, value);
    }
}