package cn.net.yunlou.bole.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 值验证工具类
 */
@Slf4j
public class ValueUtils {

    /**
     * 判断值是否有效
     * 
     * @param value 要验证的值
     * @return 是否有效
     */
    public static boolean isValid(Object value) {
        if (value == null) {
            return false;
        }

        if (value instanceof CharSequence) {
            return isValidString((CharSequence) value);
        }

        if (value instanceof Collection) {
            return isValidCollection((Collection<?>) value);
        }

        if (value instanceof Map) {
            return isValidMap((Map<?, ?>) value);
        }

        //if (value instanceof Number) {
        //    return isValidNumber((Number) value);
        //}

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        // 其他对象类型，只要非null就认为是有效的
        return true;
    }

    /**
     * 判断字符串是否有效（非null、非空、非空白）
     */
    public static boolean isValidString(CharSequence str) {
        return str != null && !str.toString().trim().isEmpty();
    }

    /**
     * 判断集合是否有效（非null、非空）
     */
    public static boolean isValidCollection(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * 判断Map是否有效（非null、非空）
     */
    public static boolean isValidMap(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    /**
     * 判断数字是否有效（非null、非零、非NaN）
     */
    public static boolean isValidNumber(Number number) {
        if (number == null) {
            return false;
        }

        if (number instanceof Double doubleValue) {
            return !doubleValue.isNaN() && doubleValue != 0.0;
        }

        if (number instanceof Float floatValue) {
            return !floatValue.isNaN() && floatValue != 0.0f;
        }

        // 其他数字类型，只要非零就认为是有效的
        return number.doubleValue() != 0.0;
    }

    /**
     * 使用自定义条件验证值
     */
    public static boolean isValid(Object value, Predicate<Object> validator) {
        return value != null && validator.test(value);
    }

    /**
     * 获取有效值或默认值
     */
    public static <T> T getValidOrDefault(T value, T defaultValue) {
        return isValid(value) ? value : defaultValue;
    }

    /**
     * 批量验证值
     */
    public static boolean allValid(Object... values) {
        if (values == null) {
            return false;
        }

        for (Object value : values) {
            if (!isValid(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 至少一个值有效
     */
    public static boolean anyValid(Object... values) {
        if (values == null) {
            return false;
        }

        for (Object value : values) {
            if (isValid(value)) {
                return true;
            }
        }
        return false;
    }
}