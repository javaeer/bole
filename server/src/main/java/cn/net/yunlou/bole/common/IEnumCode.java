package cn.net.yunlou.bole.common;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * 扩展枚举接口，支持code字段
 *
 * @param <T> 值类型
 * @param <C> code类型
 */
public interface IEnumCode<T, C> extends IEnum<T>, Serializable {

    /**
     * 根据code获取枚举
     *
     * @param code 枚举code
     * @param enumType 枚举类型
     * @param <E> 枚举类型
     * @param <T> 值类型
     * @param <C> code类型
     * @return 枚举实例
     */
    static <E extends Enum<E> & IEnumCode<T, C>, T, C> E codeOf(C code, Class<E> enumType) {
        Objects.requireNonNull(code, "Code cannot be null");
        Objects.requireNonNull(enumType, "EnumType cannot be null");

        return EnumCodeCacheManager.getCodeCache(enumType).get(code);
    }

    /**
     * 根据code获取枚举（安全版本）
     *
     * @param code 枚举code
     * @param enumType 枚举类型
     * @param <E> 枚举类型
     * @param <T> 值类型
     * @param <C> code类型
     * @return 枚举实例，未找到返回null
     */
    static <E extends Enum<E> & IEnumCode<T, C>, T, C> E codeOfSafe(C code, Class<E> enumType) {
        if (code == null || enumType == null) {
            return null;
        }
        return EnumCodeCacheManager.getCodeCache(enumType).get(code);
    }

    /**
     * 根据code获取值
     *
     * @param code 枚举code
     * @param enumType 枚举类型
     * @param <E> 枚举类型
     * @param <T> 值类型
     * @param <C> code类型
     * @return 枚举值
     */
    static <E extends Enum<E> & IEnumCode<T, C>, T, C> T codeOfValue(C code, Class<E> enumType) {
        E enumInstance = codeOf(code, enumType);
        return enumInstance != null ? enumInstance.getValue() : null;
    }

    /**
     * 根据code获取标签
     *
     * @param code 枚举code
     * @param enumType 枚举类型
     * @param <E> 枚举类型
     * @param <T> 值类型
     * @param <C> code类型
     * @return 枚举标签
     */
    static <E extends Enum<E> & IEnumCode<T, C>, T, C> String codeOfLabel(
            C code, Class<E> enumType) {
        E enumInstance = codeOf(code, enumType);
        return enumInstance != null ? enumInstance.getLabel() : null;
    }

    /**
     * 验证code是否有效
     *
     * @param code 待验证的code
     * @param enumType 枚举类型
     * @param <E> 枚举类型
     * @param <T> 值类型
     * @param <C> code类型
     * @return 是否有效
     */
    static <E extends Enum<E> & IEnumCode<T, C>, T, C> boolean isCode(C code, Class<E> enumType) {
        if (code == null || enumType == null) {
            return false;
        }
        return EnumCodeCacheManager.getCodeCache(enumType).containsKey(code);
    }

    /**
     * 获取所有code的列表
     *
     * @param enumType 枚举类型
     * @param <E> 枚举类型
     * @param <T> 值类型
     * @param <C> code类型
     * @return code列表
     */
    static <E extends Enum<E> & IEnumCode<T, C>, T, C> List<C> codes(Class<E> enumType) {
        Objects.requireNonNull(enumType, "EnumType cannot be null");
        return EnumSet.allOf(enumType).stream()
                .map(IEnumCode::getCode)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有完整信息列表
     *
     * @param enumType 枚举类型
     * @param <E> 枚举类型
     * @param <T> 值类型
     * @param <C> code类型
     * @return 完整信息列表
     */
    @SuppressWarnings("unchecked")
    static <E extends Enum<E> & IEnumCode<T, C>, T, C> List<EnumCodeItem<T, C>> toEnumCodeItemList(
            Class<E> enumType) {
        Objects.requireNonNull(enumType, "EnumType cannot be null");
        return new ArrayList<>(
                (List<EnumCodeItem<T, C>>)
                        (List<?>) EnumCodeCacheManager.getFullInfoCache(enumType));
    }

    /**
     * 获取code到EnumItem的映射
     *
     * @param enumType 枚举类型
     * @param <E> 枚举类型
     * @param <T> 值类型
     * @param <C> code类型
     * @return 映射Map
     */
    static <E extends Enum<E> & IEnumCode<T, C>, T, C> Map<C, IEnum.EnumItem<T>> toEnumCodeItemMap(
            Class<E> enumType) {
        Objects.requireNonNull(enumType, "EnumType cannot be null");
        return EnumSet.allOf(enumType).stream()
                .collect(
                        Collectors.toMap(
                                IEnumCode::getCode,
                                e -> new IEnum.EnumItem<>(e.getValue(), e.getLabel())));
    }

    /**
     * 清理指定枚举类型的缓存
     *
     * @param enumType 枚举类型
     */
    static void clearEnumCodeCache(Class<?> enumType) {
        EnumCodeCacheManager.clearCache(enumType);
    }

    /** 清理所有扩展枚举缓存 */
    static void clearAllEnumCodeCache() {
        EnumCodeCacheManager.clearAllCache();
    }

    /**
     * 获取枚举code
     *
     * @return 枚举code
     */
    C getCode();

    /**
     * 转换为完整枚举信息
     *
     * @return 完整枚举信息
     */
    default EnumCodeItem<T, C> toEnumCodeItem() {
        return new EnumCodeItem<>(getValue(), getLabel(), getCode());
    }

    /** 缓存管理器 */
    class EnumCodeCacheManager {
        private static final Map<Class<?>, Map<Object, ?>> VALUE_CACHE = new ConcurrentHashMap<>();
        private static final Map<Class<?>, Map<Object, ?>> CODE_CACHE = new ConcurrentHashMap<>();
        private static final Map<Class<?>, List<?>> FULL_INFO_CACHE = new ConcurrentHashMap<>();

        @SuppressWarnings("unchecked")
        static <E extends Enum<E> & IEnumCode<?, ?>> Map<Object, E> getValueCache(
                Class<E> enumType) {
            return (Map<Object, E>)
                    VALUE_CACHE.computeIfAbsent(
                            enumType,
                            k ->
                                    EnumSet.allOf(enumType).stream()
                                            .collect(
                                                    Collectors.toMap(
                                                            e -> ((IEnumCode<?, ?>) e).getValue(),
                                                            Function.identity(),
                                                            (e1, e2) -> e1,
                                                            () ->
                                                                    new HashMap<>(
                                                                            (int)
                                                                                    (EnumSet.allOf(
                                                                                                                    enumType)
                                                                                                            .size()
                                                                                                    / 0.75f
                                                                                            + 1)))));
        }

        @SuppressWarnings("unchecked")
        static <E extends Enum<E> & IEnumCode<?, ?>> Map<Object, E> getCodeCache(
                Class<E> enumType) {
            return (Map<Object, E>)
                    CODE_CACHE.computeIfAbsent(
                            enumType,
                            k ->
                                    EnumSet.allOf(enumType).stream()
                                            .collect(
                                                    Collectors.toMap(
                                                            e -> ((IEnumCode<?, ?>) e).getCode(),
                                                            Function.identity(),
                                                            (e1, e2) -> e1,
                                                            () ->
                                                                    new HashMap<>(
                                                                            (int)
                                                                                    (EnumSet.allOf(
                                                                                                                    enumType)
                                                                                                            .size()
                                                                                                    / 0.75f
                                                                                            + 1)))));
        }

        @SuppressWarnings("unchecked")
        static <E extends Enum<E> & IEnumCode<?, ?>> List<EnumCodeItem<?, ?>> getFullInfoCache(
                Class<E> enumType) {
            return (List<EnumCodeItem<?, ?>>)
                    FULL_INFO_CACHE.computeIfAbsent(
                            enumType,
                            k ->
                                    EnumSet.allOf(enumType).stream()
                                            .map(
                                                    e -> {
                                                        IEnumCode<?, ?> ie = (IEnumCode<?, ?>) e;
                                                        return new EnumCodeItem<>(
                                                                ie.getValue(),
                                                                ie.getLabel(),
                                                                ie.getCode());
                                                    })
                                            .collect(Collectors.toList()));
        }

        static void clearCache(Class<?> enumType) {
            if (enumType != null) {
                VALUE_CACHE.remove(enumType);
                CODE_CACHE.remove(enumType);
                FULL_INFO_CACHE.remove(enumType);
            }
        }

        static void clearAllCache() {
            VALUE_CACHE.clear();
            CODE_CACHE.clear();
            FULL_INFO_CACHE.clear();
        }
    }

    /**
     * 完整枚举信息类
     *
     * @param <T> 值类型
     * @param <C> code类型
     */
    @Getter
    class EnumCodeItem<T, C> {
        private final T value;
        private final String label;
        private final C code;

        public EnumCodeItem(T value, String label, C code) {
            this.value = value;
            this.label = label;
            this.code = code;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EnumCodeItem<?, ?> that = (EnumCodeItem<?, ?>) o;
            return Objects.equals(value, that.value)
                    && Objects.equals(label, that.label)
                    && Objects.equals(code, that.code);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, label, code);
        }

        @Override
        public String toString() {
            return String.format("EnumCodeItem{value=%s, label='%s', code=%s}", value, label, code);
        }
    }
}
