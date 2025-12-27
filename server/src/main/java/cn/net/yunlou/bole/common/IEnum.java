package cn.net.yunlou.bole.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wangyb
 */
public interface IEnum<T> extends Serializable {


    /**
     * 获取枚举
     *
     * @param value
     * @param enumType
     * @param <E>
     * @return
     */
    static <E extends Enum<E> & IEnum<?>> E valueOf(Object value, Class<E> enumType) {
        Objects.requireNonNull(value, "Value cannot be null");
        Objects.requireNonNull(enumType, "EnumType cannot be null");
        return EnumCacheManager.getValueCache(enumType).get(value);
    }

    /**
     * 获取枚举描述
     *
     * @param value
     * @param enumType
     * @param <E>
     * @return
     */
    static <E extends Enum<E> & IEnum<?>> String valueOfLabel(Object value, Class<E> enumType) {
        Objects.requireNonNull(value, "Value cannot be null");
        Objects.requireNonNull(enumType, "EnumType cannot be null");

        E enumInstance = EnumCacheManager.getValueCache(enumType).get(value);
        return enumInstance != null ? enumInstance.getLabel() : null;
    }

    /**
     * 根据描述 获取枚举值
     *
     * @param label
     * @param enumType
     * @param <E>
     * @return
     */
    static <E extends Enum<E> & IEnum<T>, T> T labelOfValue(String label, Class<E> enumType) {
        Objects.requireNonNull(label, "Label cannot be null");
        Objects.requireNonNull(enumType, "EnumType cannot be null");

        E enumInstance = EnumCacheManager.getLabelCache(enumType).get(label);
        return enumInstance != null ? enumInstance.getValue() : null;
    }

    /**
     * 验证枚举值
     *
     * @param value
     * @param enumType
     * @param <E>
     * @return
     */
    static <E extends Enum<E> & IEnum<?>> boolean isValid(Object value, Class<E> enumType) {
        // 明确检查参数
        if (value == null || enumType == null) {
            return false;
        }
        return EnumCacheManager.getValueCache(enumType).containsKey(value);
    }

    /**
     * 根据值定器 转换为map
     *
     * @param clazz
     * @param valueExtractor
     * @param <E>
     * @return
     */
    static <E extends Enum<E> & IEnum<?>, V> Map<E, V> toMap(
            Class<E> clazz,
            Function<E, V> valueExtractor) {
        Objects.requireNonNull(clazz, "Class cannot be null");
        Objects.requireNonNull(valueExtractor, "Value extractor cannot be null");

        EnumMap<E, V> enumMap = new EnumMap<>(clazz);
        EnumSet.allOf(clazz).forEach(e -> enumMap.put(e, valueExtractor.apply(e)));
        return enumMap;
    }

    /**
     * 获取所有值的Map
     *
     * @param enumType
     * @param <E>
     * @return
     */
    static <E extends Enum<E> & IEnum<?>> Map<E, Object> valueMap(Class<E> enumType) {
        Objects.requireNonNull(enumType, "EnumType cannot be null");

        Map<E, Object> result = new EnumMap<>(enumType);
        Map<Object, E> valueCache = EnumCacheManager.getValueCache(enumType);
        valueCache.forEach((value, e) -> result.put(e, value));
        return result;
    }

    /**
     * 获取所有标签的Map
     *
     * @param enumType
     * @param <E>
     * @return
     */
    static <E extends Enum<E> & IEnum<?>> Map<E, Object> labelMap(Class<E> enumType) {
        Objects.requireNonNull(enumType, "EnumType cannot be null");

        Map<E, Object> result = new EnumMap<>(enumType);
        Map<String, E> labelCache = EnumCacheManager.getLabelCache(enumType);
        labelCache.forEach((label, e) -> result.put(e, label));
        return result;
    }

    /**
     * 获取所有值的列表
     *
     * @param enumType
     * @param <E>
     * @return
     */
    static <E extends Enum<E> & IEnum<?>> List<Object> values(Class<E> enumType) {
        Objects.requireNonNull(enumType, "EnumType cannot be null");
        return new ArrayList<>(EnumCacheManager.getValuesCache(enumType));
    }

    /**
     * 获取所有标签的列表
     *
     * @param enumType
     * @param <E>
     * @return
     */
    static <E extends Enum<E> & IEnum<?>> List<String> labels(Class<E> enumType) {
        Objects.requireNonNull(enumType, "EnumType cannot be null");
        return new ArrayList<>(EnumCacheManager.getLabelsCache(enumType));
    }

    /**
     * 批量转换为EnumItem列表
     *
     * @param enumType
     * @param <E>
     * @return
     */
    static <E extends Enum<E> & IEnum<?>> List<EnumItem<?>> toItemList(Class<E> enumType) {
        Objects.requireNonNull(enumType, "EnumType cannot be null");
        return new ArrayList<>(EnumCacheManager.getItemListCache(enumType));
    }

    /**
     * 清理指定枚举类型的缓存
     *
     * @param enumType 枚举类型
     */
    static void clearCache(Class<?> enumType) {
        EnumCacheManager.clearCache(enumType);
    }

    /**
     * 清理所有缓存
     */
    static void clearAllCache() {
        EnumCacheManager.clearAllCache();
    }

    /**
     * 键值类型 Integer、Long、String
     *
     * @return T 值
     */
    T getValue();

    /**
     * 抽象方法
     *
     * @return 描述
     */
    String getLabel();

    /**
     * 转换为EnumItem
     *
     * @return
     */
    default EnumItem<T> toItem() {
        return new EnumItem<>(getValue(), getLabel());
    }

    class EnumCacheManager {
        // 使用 ConcurrentHashMap 保证线程安全
        private static final Map<Class<?>, Map<Object, ?>> VALUE_CACHE = new ConcurrentHashMap<>();
        private static final Map<Class<?>, Map<String, ?>> LABEL_CACHE = new ConcurrentHashMap<>();
        private static final Map<Class<?>, List<EnumItem<?>>> ITEM_LIST_CACHE = new ConcurrentHashMap<>();
        private static final Map<Class<?>, List<Object>> VALUES_CACHE = new ConcurrentHashMap<>();
        private static final Map<Class<?>, List<String>> LABELS_CACHE = new ConcurrentHashMap<>();

        @SuppressWarnings("unchecked")
        static <E extends Enum<E> & IEnum<?>> Map<Object, E> getValueCache(Class<E> enumType) {
            return (Map<Object, E>) VALUE_CACHE.computeIfAbsent(enumType, k ->
                    EnumSet.allOf(enumType).stream()
                            .collect(Collectors.toMap(
                                    e -> ((IEnum<?>) e).getValue(),
                                    Function.identity(),
                                    (e1, e2) -> e1,
                                    () -> new HashMap<>((int) (EnumSet.allOf(enumType).size() / 0.75f + 1))
                            ))
            );
        }

        @SuppressWarnings("unchecked")
        static <E extends Enum<E> & IEnum<?>> Map<String, E> getLabelCache(Class<E> enumType) {
            return (Map<String, E>) LABEL_CACHE.computeIfAbsent(enumType, k ->
                    EnumSet.allOf(enumType).stream()
                            .collect(Collectors.toMap(
                                    e -> e.getLabel(),
                                    Function.identity(),
                                    (e1, e2) -> e1,
                                    () -> new HashMap<>((int) (EnumSet.allOf(enumType).size() / 0.75f + 1))
                            ))
            );
        }

        static <E extends Enum<E> & IEnum<?>> List<EnumItem<?>> getItemListCache(Class<E> enumType) {
            return ITEM_LIST_CACHE.computeIfAbsent(enumType, k ->
                    EnumSet.allOf(enumType).stream()
                            .map(e -> new EnumItem<>(((IEnum<?>) e).getValue(), e.getLabel()))
                            .collect(Collectors.toList())
            );
        }

        static <E extends Enum<E> & IEnum<?>> List<Object> getValuesCache(Class<E> enumType) {
            return VALUES_CACHE.computeIfAbsent(enumType, k ->
                    EnumSet.allOf(enumType).stream()
                            .map(e -> ((IEnum<?>) e).getValue())
                            .collect(Collectors.toList())
            );
        }

        static <E extends Enum<E> & IEnum<?>> List<String> getLabelsCache(Class<E> enumType) {
            return LABELS_CACHE.computeIfAbsent(enumType, k ->
                    EnumSet.allOf(enumType).stream()
                            .map(e -> e.getLabel())
                            .collect(Collectors.toList())
            );
        }

        // 清理缓存方法（可选，用于热部署等场景）
        static void clearCache(Class<?> enumType) {
            if (enumType != null) {
                VALUE_CACHE.remove(enumType);
                LABEL_CACHE.remove(enumType);
                ITEM_LIST_CACHE.remove(enumType);
                VALUES_CACHE.remove(enumType);
                LABELS_CACHE.remove(enumType);
            }
        }

        static void clearAllCache() {
            VALUE_CACHE.clear();
            LABEL_CACHE.clear();
            ITEM_LIST_CACHE.clear();
            VALUES_CACHE.clear();
            LABELS_CACHE.clear();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class EnumItem<T> {
        private T value;
        private String label;
    }
}
