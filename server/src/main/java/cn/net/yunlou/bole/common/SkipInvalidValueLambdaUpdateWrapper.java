package cn.net.yunlou.bole.common;

import cn.net.yunlou.bole.common.utils.EntityUtils;
import cn.net.yunlou.bole.common.utils.ValueUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * 跳过无效值的Lambda更新包装器
 * 自动过滤null、空字符串、空集合等无效值，避免这些值被拼接到SQL更新语句中
 *
 *
 * // 基础使用
 * SkipInvalidValueLambdaUpdateWrapper<User> wrapper = SkipInvalidValueLambdaUpdateWrapper.of(User.class)
 *     .set(User::getName, "张三")
 *     .set(User::getAge, 25)
 *     .set(User::getEmail, "") // 自动跳过
 *     .eq(User::getId, 1L);
 *
 * // 启用严格模式
 * SkipInvalidValueLambdaUpdateWrapper<User> wrapper = SkipInvalidValueLambdaUpdateWrapper.create()
 *     .strictMode()
 *     .set(User::getName, "李四")
 *     .eq(User::getStatus, null); // 会记录跳过日志
 *
 * // 允许null值
 * SkipInvalidValueLambdaUpdateWrapper<User> wrapper = SkipInvalidValueLambdaUpdateWrapper.create()
 *     .allowNullValue()
 *     .set(User::getName, null) // 允许设置null
 *     .eq(User::getId, 1L);
 *
 * // 链式设置多个字段
 * SkipInvalidValueLambdaUpdateWrapper<User> wrapper = SkipInvalidValueLambdaUpdateWrapper.create()
 *     .setMulti(w -> {
 *         w.set(User::getName, "王五");
 *         w.set(User::getAge, 30);
 *     })
 *     .eq(User::getId, 1L);
 *
 * // 数值增减操作
 * SkipInvalidValueLambdaUpdateWrapper<User> wrapper = SkipInvalidValueLambdaUpdateWrapper.create()
 *     .setInc(User::getVisitCount, 1) // visit_count = visit_count + 1
 *     .eq(User::getId, 1L);
 *
 * @param <T> 实体类型
 */
@Slf4j
@Getter
public class SkipInvalidValueLambdaUpdateWrapper<T> extends LambdaUpdateWrapper<T> {

    private boolean strictMode = false; // 严格模式，记录跳过的条件
    private boolean allowNullValue = false; // 是否允许null值

    public SkipInvalidValueLambdaUpdateWrapper() {
        super();
    }

    public SkipInvalidValueLambdaUpdateWrapper(T entity) {
        super();
        T filteredEntity = EntityUtils.filterInvalidValues(entity);
        super.setEntity(filteredEntity);
    }

    public SkipInvalidValueLambdaUpdateWrapper(Class<T> entityClass) {
        super(entityClass);
    }

    // ============ 静态工厂方法 ============

    public static <T> SkipInvalidValueLambdaUpdateWrapper<T> of(Class<T> entityClass) {
        return new SkipInvalidValueLambdaUpdateWrapper<>(entityClass);
    }

    public static <T> SkipInvalidValueLambdaUpdateWrapper<T> of(T entity) {
        return new SkipInvalidValueLambdaUpdateWrapper<>(entity);
    }

    public static <T> SkipInvalidValueLambdaUpdateWrapper<T> create() {
        return new SkipInvalidValueLambdaUpdateWrapper<>();
    }

    public static <T> SkipInvalidValueLambdaUpdateWrapper<T> create(Class<T> entityClass) {
        return new SkipInvalidValueLambdaUpdateWrapper<>(entityClass);
    }

    // ============ 配置方法 ============

    /**
     * 启用严格模式，跳过无效条件时会记录日志
     */
    public SkipInvalidValueLambdaUpdateWrapper<T> strictMode() {
        this.strictMode = true;
        return this;
    }

    /**
     * 禁用严格模式
     */
    public SkipInvalidValueLambdaUpdateWrapper<T> lenientMode() {
        this.strictMode = false;
        return this;
    }

    /**
     * 允许设置null值
     */
    public SkipInvalidValueLambdaUpdateWrapper<T> allowNullValue() {
        this.allowNullValue = true;
        return this;
    }

    /**
     * 禁止设置null值（默认）
     */
    public SkipInvalidValueLambdaUpdateWrapper<T> disallowNullValue() {
        this.allowNullValue = false;
        return this;
    }

    // ============ 重写核心方法 ============

    @Override
    public LambdaUpdateWrapper<T> setEntity(T entity) {
        T filteredEntity = EntityUtils.filterInvalidValues(entity);
        super.setEntity(filteredEntity);
        return this;
    }

    // ============ 重写set方法 ============

    @Override
    public LambdaUpdateWrapper<T> set(boolean condition, SFunction<T, ?> column, Object value) {
        boolean isValid = allowNullValue ? (value != null || ValueUtils.isValid(value)) : ValueUtils.isValid(value);
        if (condition && isValid) {
            return super.set(true, column, value);
        } else if (strictMode && condition) {
            log.debug("Skip invalid SET condition: {} = {}", column, value);
        }
        return this;
    }

    @Override
    public LambdaUpdateWrapper<T> set(SFunction<T, ?> column, Object value) {
        boolean isValid = allowNullValue ? (value != null || ValueUtils.isValid(value)) : ValueUtils.isValid(value);
        if (isValid) {
            return super.set(column, value);
        } else if (strictMode) {
            log.debug("Skip invalid SET condition: {} = {}", column, value);
        }
        return this;
    }

    /**
     * 链式设置多个字段
     */
    public SkipInvalidValueLambdaUpdateWrapper<T> setMulti(Consumer<SkipInvalidValueLambdaUpdateWrapper<T>> consumer) {
        consumer.accept(this);
        return this;
    }

    // ============ 重写条件方法 ============

    @Override
    public LambdaUpdateWrapper<T> eq(boolean condition, SFunction<T, ?> column, Object value) {
        if (condition && ValueUtils.isValid(value)) {
            return super.eq(true, column, value);
        } else if (strictMode && condition) {
            log.debug("Skip invalid EQ condition: {} = {}", column, value);
        }
        return this;
    }

    @Override
    public LambdaUpdateWrapper<T> eq(SFunction<T, ?> column, Object value) {
        if (ValueUtils.isValid(value)) {
            return super.eq(column, value);
        } else if (strictMode) {
            log.debug("Skip invalid EQ condition: {} = {}", column, value);
        }
        return this;
    }

    @Override
    public LambdaUpdateWrapper<T> ne(boolean condition, SFunction<T, ?> column, Object value) {
        if (condition && ValueUtils.isValid(value)) {
            return super.ne(true, column, value);
        } else if (strictMode && condition) {
            log.debug("Skip invalid NE condition: {} != {}", column, value);
        }
        return this;
    }

    @Override
    public LambdaUpdateWrapper<T> ne(SFunction<T, ?> column, Object value) {
        if (ValueUtils.isValid(value)) {
            return super.ne(column, value);
        } else if (strictMode) {
            log.debug("Skip invalid NE condition: {} != {}", column, value);
        }
        return this;
    }

    @Override
    public LambdaUpdateWrapper<T> like(boolean condition, SFunction<T, ?> column, Object value) {
        if (condition && ValueUtils.isValid(value)) {
            return super.like(true, column, value);
        } else if (strictMode && condition) {
            log.debug("Skip invalid LIKE condition: {} LIKE {}", column, value);
        }
        return this;
    }

    @Override
    public LambdaUpdateWrapper<T> like(SFunction<T, ?> column, Object value) {
        if (ValueUtils.isValid(value)) {
            return super.like(column, value);
        } else if (strictMode) {
            log.debug("Skip invalid LIKE condition: {} LIKE {}", column, value);
        }
        return this;
    }

    @Override
    public LambdaUpdateWrapper<T> in(boolean condition, SFunction<T, ?> column, Collection<?> values) {
        if (condition && ValueUtils.isValid(values)) {
            return super.in(true, column, values);
        } else if (strictMode && condition) {
            log.debug("Skip invalid IN condition: {} IN {}", column, values);
        }
        return this;
    }

    @Override
    public LambdaUpdateWrapper<T> in(SFunction<T, ?> column, Collection<?> values) {
        if (ValueUtils.isValid(values)) {
            return super.in(column, values);
        } else if (strictMode) {
            log.debug("Skip invalid IN condition: {} IN {}", column, values);
        }
        return this;
    }

    @Override
    public LambdaUpdateWrapper<T> gt(boolean condition, SFunction<T, ?> column, Object value) {
        if (condition && ValueUtils.isValid(value)) {
            return super.gt(true, column, value);
        } else if (strictMode && condition) {
            log.debug("Skip invalid GT condition: {} > {}", column, value);
        }
        return this;
    }

    @Override
    public LambdaUpdateWrapper<T> gt(SFunction<T, ?> column, Object value) {
        if (ValueUtils.isValid(value)) {
            return super.gt(column, value);
        } else if (strictMode) {
            log.debug("Skip invalid GT condition: {} > {}", column, value);
        }
        return this;
    }

    @Override
    public LambdaUpdateWrapper<T> ge(boolean condition, SFunction<T, ?> column, Object value) {
        if (condition && ValueUtils.isValid(value)) {
            return super.ge(true, column, value);
        } else if (strictMode && condition) {
            log.debug("Skip invalid GE condition: {} >= {}", column, value);
        }
        return this;
    }

    @Override
    public LambdaUpdateWrapper<T> ge(SFunction<T, ?> column, Object value) {
        if (ValueUtils.isValid(value)) {
            return super.ge(column, value);
        } else if (strictMode) {
            log.debug("Skip invalid GE condition: {} >= {}", column, value);
        }
        return this;
    }

    @Override
    public LambdaUpdateWrapper<T> lt(boolean condition, SFunction<T, ?> column, Object value) {
        if (condition && ValueUtils.isValid(value)) {
            return super.lt(true, column, value);
        } else if (strictMode && condition) {
            log.debug("Skip invalid LT condition: {} < {}", column, value);
        }
        return this;
    }

    @Override
    public LambdaUpdateWrapper<T> lt(SFunction<T, ?> column, Object value) {
        if (ValueUtils.isValid(value)) {
            return super.lt(column, value);
        } else if (strictMode) {
            log.debug("Skip invalid LT condition: {} < {}", column, value);
        }
        return this;
    }

    @Override
    public LambdaUpdateWrapper<T> le(boolean condition, SFunction<T, ?> column, Object value) {
        if (condition && ValueUtils.isValid(value)) {
            return super.le(true, column, value);
        } else if (strictMode && condition) {
            log.debug("Skip invalid LE condition: {} <= {}", column, value);
        }
        return this;
    }

    @Override
    public LambdaUpdateWrapper<T> le(SFunction<T, ?> column, Object value) {
        if (ValueUtils.isValid(value)) {
            return super.le(column, value);
        } else if (strictMode) {
            log.debug("Skip invalid LE condition: {} <= {}", column, value);
        }
        return this;
    }

    // ============ 新增常用条件方法 ============

    @Override
    public LambdaUpdateWrapper<T> between(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        boolean valid1 = ValueUtils.isValid(val1);
        boolean valid2 = ValueUtils.isValid(val2);

        if (condition && valid1 && valid2) {
            return super.between(true, column, val1, val2);
        } else if (strictMode && condition) {
            log.debug("Skip invalid BETWEEN condition: {} BETWEEN {} AND {}", column, val1, val2);
        }
        return this;
    }

    @Override
    public LambdaUpdateWrapper<T> between(SFunction<T, ?> column, Object val1, Object val2) {
        boolean valid1 = ValueUtils.isValid(val1);
        boolean valid2 = ValueUtils.isValid(val2);

        if (valid1 && valid2) {
            return super.between(column, val1, val2);
        } else if (strictMode) {
            log.debug("Skip invalid BETWEEN condition: {} BETWEEN {} AND {}", column, val1, val2);
        }
        return this;
    }

    @Override
    public LambdaUpdateWrapper<T> notIn(boolean condition, SFunction<T, ?> column, Collection<?> values) {
        if (condition && ValueUtils.isValid(values)) {
            return super.notIn(true, column, values);
        } else if (strictMode && condition) {
            log.debug("Skip invalid NOT_IN condition: {} NOT IN {}", column, values);
        }
        return this;
    }

    @Override
    public LambdaUpdateWrapper<T> notIn(SFunction<T, ?> column, Collection<?> values) {
        if (ValueUtils.isValid(values)) {
            return super.notIn(column, values);
        } else if (strictMode) {
            log.debug("Skip invalid NOT_IN condition: {} NOT IN {}", column, values);
        }
        return this;
    }

    // ============ 特殊set方法 ============

    /**
     * 强制设置值，即使值为null或无效也设置
     * 用于需要显式设置为null的场景
     */
    public LambdaUpdateWrapper<T> setForce(SFunction<T, ?> column, Object value) {
        return super.set(column, value);
    }

    /**
     * 强制设置值（带条件）
     */
    public LambdaUpdateWrapper<T> setForce(boolean condition, SFunction<T, ?> column, Object value) {
        return condition ? super.set(true, column, value) : this;
    }

    /**
     * 设置值为null（明确设置null值）
     */
    public LambdaUpdateWrapper<T> setNull(SFunction<T, ?> column) {
        return super.set(column, null);
    }

    /**
     * 设置值为null（带条件）
     */
    public LambdaUpdateWrapper<T> setNull(boolean condition, SFunction<T, ?> column) {
        return condition ? super.set(true, column, null) : this;
    }

    /**
     * 递增字段值
     */
    public SkipInvalidValueLambdaUpdateWrapper<T> setInc(SFunction<T, ?> column, Number value) {
        if (ValueUtils.isValid(value)) {
            String columnName = this.columnToString(column);
            super.setSql(String.format("%s = %s + %s", columnName, columnName, value));
        } else if (strictMode) {
            log.debug("Skip invalid INCREMENT condition: {} += {}", column, value);
        }
        return this;
    }

    /**
     * 递减字段值
     */
    public SkipInvalidValueLambdaUpdateWrapper<T> setDec(SFunction<T, ?> column, Number value) {
        if (ValueUtils.isValid(value)) {
            String columnName = this.columnToString(column);
            super.setSql(String.format("%s = %s - %s", columnName, columnName, value));
        } else if (strictMode) {
            log.debug("Skip invalid DECREMENT condition: {} -= {}", column, value);
        }
        return this;
    }
}