package cn.net.yunlou.bole.common;

import cn.net.yunlou.bole.common.utils.EntityUtils;
import cn.net.yunlou.bole.common.utils.ValueUtils;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import java.util.Collection;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 跳过无效值的Lambda更新包装器 自动过滤null、空字符串、空集合等无效值，避免这些值被拼接到SQL更新语句中
 *
 * <p>// 基础使用 SkipInvalidValueUpdateWrapper<User> wrapper =
 * SkipInvalidValueUpdateWrapper.of(User.class) .set(User::getName, "张三") .set(User::getAge, 25)
 * .set(User::getEmail, "") // 自动跳过 .eq(User::getId, 1L);
 *
 * <p>// 启用严格模式 SkipInvalidValueUpdateWrapper<User> wrapper = SkipInvalidValueUpdateWrapper.create()
 * .strictMode() .set(User::getName, "李四") .eq(User::getStatus, null); // 会记录跳过日志
 *
 * <p>// 允许null值 SkipInvalidValueUpdateWrapper<User> wrapper =
 * SkipInvalidValueUpdateWrapper.create() .allowNullValue() .set(User::getName, null) // 允许设置null
 * .eq(User::getId, 1L);
 *
 * <p>// 链式设置多个字段 SkipInvalidValueUpdateWrapper<User> wrapper =
 * SkipInvalidValueUpdateWrapper.create() .setMulti(w -> { w.set(User::getName, "王五");
 * w.set(User::getAge, 30); }) .eq(User::getId, 1L);
 *
 * <p>// 数值增减操作 SkipInvalidValueUpdateWrapper<User> wrapper = SkipInvalidValueUpdateWrapper.create()
 * .setInc(User::getVisitCount, 1) // visit_count = visit_count + 1 .eq(User::getId, 1L);
 *
 * @param <T> 实体类型
 */
@Slf4j
@Getter
public class SkipInvalidValueUpdateWrapper<T> extends UpdateWrapper<T> {

    private boolean strictMode = false; // 严格模式，记录跳过的条件
    private boolean allowNullValue = false; // 是否允许null值

    public SkipInvalidValueUpdateWrapper() {
        super();
    }

    public SkipInvalidValueUpdateWrapper(T entity) {
        super();
        T filteredEntity = EntityUtils.filterInvalidValues(entity);
        super.setEntity(filteredEntity);
    }

    public SkipInvalidValueUpdateWrapper(Class<T> entityClass) {
        super((T) entityClass);
    }

    // ============ 静态工厂方法 ============

    public static <T> SkipInvalidValueUpdateWrapper<T> of(Class<T> entityClass) {
        return new SkipInvalidValueUpdateWrapper<>(entityClass);
    }

    public static <T> SkipInvalidValueUpdateWrapper<T> of(T entity) {
        return new SkipInvalidValueUpdateWrapper<>(entity);
    }

    public static <T> SkipInvalidValueUpdateWrapper<T> create() {
        return new SkipInvalidValueUpdateWrapper<>();
    }

    public static <T> SkipInvalidValueUpdateWrapper<T> create(Class<T> entityClass) {
        return new SkipInvalidValueUpdateWrapper<>(entityClass);
    }

    // ============ 配置方法 ============

    /** 启用严格模式，跳过无效条件时会记录日志 */
    public SkipInvalidValueUpdateWrapper<T> strictMode() {
        this.strictMode = true;
        return this;
    }

    /** 禁用严格模式 */
    public SkipInvalidValueUpdateWrapper<T> lenientMode() {
        this.strictMode = false;
        return this;
    }

    /** 允许设置null值 */
    public SkipInvalidValueUpdateWrapper<T> allowNullValue() {
        this.allowNullValue = true;
        return this;
    }

    /** 禁止设置null值（默认） */
    public SkipInvalidValueUpdateWrapper<T> disallowNullValue() {
        this.allowNullValue = false;
        return this;
    }

    // ============ 重写核心方法 ============

    @Override
    public UpdateWrapper<T> setEntity(T entity) {
        T filteredEntity = EntityUtils.filterInvalidValues(entity);
        super.setEntity(filteredEntity);
        return this;
    }

    // ============ 重写set方法 ============

    @Override
    public UpdateWrapper<T> set(boolean condition, String column, Object value) {
        boolean isValid =
                allowNullValue
                        ? (value != null || ValueUtils.isValid(value))
                        : ValueUtils.isValid(value);
        if (condition && isValid) {
            return super.set(true, column, value);
        } else if (strictMode && condition) {
            log.debug("Skip invalid SET condition: {} = {}", column, value);
        }
        return this;
    }

    @Override
    public UpdateWrapper<T> set(String column, Object value) {
        boolean isValid =
                allowNullValue
                        ? (value != null || ValueUtils.isValid(value))
                        : ValueUtils.isValid(value);
        if (isValid) {
            return super.set(column, value);
        } else if (strictMode) {
            log.debug("Skip invalid SET condition: {} = {}", column, value);
        }
        return this;
    }

    /** 链式设置多个字段 */
    public SkipInvalidValueUpdateWrapper<T> setMulti(
            Consumer<SkipInvalidValueUpdateWrapper<T>> consumer) {
        consumer.accept(this);
        return this;
    }

    // ============ 重写条件方法 ============

    @Override
    public UpdateWrapper<T> eq(boolean condition, String column, Object value) {
        if (condition && ValueUtils.isValid(value)) {
            return super.eq(true, column, value);
        } else if (strictMode && condition) {
            log.debug("Skip invalid EQ condition: {} = {}", column, value);
        }
        return this;
    }

    @Override
    public UpdateWrapper<T> eq(String column, Object value) {
        if (ValueUtils.isValid(value)) {
            return super.eq(column, value);
        } else if (strictMode) {
            log.debug("Skip invalid EQ condition: {} = {}", column, value);
        }
        return this;
    }

    @Override
    public UpdateWrapper<T> ne(boolean condition, String column, Object value) {
        if (condition && ValueUtils.isValid(value)) {
            return super.ne(true, column, value);
        } else if (strictMode && condition) {
            log.debug("Skip invalid NE condition: {} != {}", column, value);
        }
        return this;
    }

    @Override
    public UpdateWrapper<T> ne(String column, Object value) {
        if (ValueUtils.isValid(value)) {
            return super.ne(column, value);
        } else if (strictMode) {
            log.debug("Skip invalid NE condition: {} != {}", column, value);
        }
        return this;
    }

    @Override
    public UpdateWrapper<T> like(boolean condition, String column, Object value) {
        if (condition && ValueUtils.isValid(value)) {
            return super.like(true, column, value);
        } else if (strictMode && condition) {
            log.debug("Skip invalid LIKE condition: {} LIKE {}", column, value);
        }
        return this;
    }

    @Override
    public UpdateWrapper<T> like(String column, Object value) {
        if (ValueUtils.isValid(value)) {
            return super.like(column, value);
        } else if (strictMode) {
            log.debug("Skip invalid LIKE condition: {} LIKE {}", column, value);
        }
        return this;
    }

    @Override
    public UpdateWrapper<T> in(boolean condition, String column, Collection<?> values) {
        if (condition && ValueUtils.isValid(values)) {
            return super.in(true, column, values);
        } else if (strictMode && condition) {
            log.debug("Skip invalid IN condition: {} IN {}", column, values);
        }
        return this;
    }

    @Override
    public UpdateWrapper<T> in(String column, Collection<?> values) {
        if (ValueUtils.isValid(values)) {
            return super.in(column, values);
        } else if (strictMode) {
            log.debug("Skip invalid IN condition: {} IN {}", column, values);
        }
        return this;
    }

    @Override
    public UpdateWrapper<T> gt(boolean condition, String column, Object value) {
        if (condition && ValueUtils.isValid(value)) {
            return super.gt(true, column, value);
        } else if (strictMode && condition) {
            log.debug("Skip invalid GT condition: {} > {}", column, value);
        }
        return this;
    }

    @Override
    public UpdateWrapper<T> gt(String column, Object value) {
        if (ValueUtils.isValid(value)) {
            return super.gt(column, value);
        } else if (strictMode) {
            log.debug("Skip invalid GT condition: {} > {}", column, value);
        }
        return this;
    }

    @Override
    public UpdateWrapper<T> ge(boolean condition, String column, Object value) {
        if (condition && ValueUtils.isValid(value)) {
            return super.ge(true, column, value);
        } else if (strictMode && condition) {
            log.debug("Skip invalid GE condition: {} >= {}", column, value);
        }
        return this;
    }

    @Override
    public UpdateWrapper<T> ge(String column, Object value) {
        if (ValueUtils.isValid(value)) {
            return super.ge(column, value);
        } else if (strictMode) {
            log.debug("Skip invalid GE condition: {} >= {}", column, value);
        }
        return this;
    }

    @Override
    public UpdateWrapper<T> lt(boolean condition, String column, Object value) {
        if (condition && ValueUtils.isValid(value)) {
            return super.lt(true, column, value);
        } else if (strictMode && condition) {
            log.debug("Skip invalid LT condition: {} < {}", column, value);
        }
        return this;
    }

    @Override
    public UpdateWrapper<T> lt(String column, Object value) {
        if (ValueUtils.isValid(value)) {
            return super.lt(column, value);
        } else if (strictMode) {
            log.debug("Skip invalid LT condition: {} < {}", column, value);
        }
        return this;
    }

    @Override
    public UpdateWrapper<T> le(boolean condition, String column, Object value) {
        if (condition && ValueUtils.isValid(value)) {
            return super.le(true, column, value);
        } else if (strictMode && condition) {
            log.debug("Skip invalid LE condition: {} <= {}", column, value);
        }
        return this;
    }

    @Override
    public UpdateWrapper<T> le(String column, Object value) {
        if (ValueUtils.isValid(value)) {
            return super.le(column, value);
        } else if (strictMode) {
            log.debug("Skip invalid LE condition: {} <= {}", column, value);
        }
        return this;
    }

    // ============ 新增常用条件方法 ============

    @Override
    public UpdateWrapper<T> between(boolean condition, String column, Object val1, Object val2) {
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
    public UpdateWrapper<T> between(String column, Object val1, Object val2) {
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
    public UpdateWrapper<T> notIn(boolean condition, String column, Collection<?> values) {
        if (condition && ValueUtils.isValid(values)) {
            return super.notIn(true, column, values);
        } else if (strictMode && condition) {
            log.debug("Skip invalid NOT_IN condition: {} NOT IN {}", column, values);
        }
        return this;
    }

    @Override
    public UpdateWrapper<T> notIn(String column, Collection<?> values) {
        if (ValueUtils.isValid(values)) {
            return super.notIn(column, values);
        } else if (strictMode) {
            log.debug("Skip invalid NOT_IN condition: {} NOT IN {}", column, values);
        }
        return this;
    }

    // ============ 特殊set方法 ============

    /** 强制设置值，即使值为null或无效也设置 用于需要显式设置为null的场景 */
    public UpdateWrapper<T> setForce(String column, Object value) {
        return super.set(column, value);
    }

    /** 强制设置值（带条件） */
    public UpdateWrapper<T> setForce(boolean condition, String column, Object value) {
        return condition ? super.set(true, column, value) : this;
    }

    /** 设置值为null（明确设置null值） */
    public UpdateWrapper<T> setNull(String column) {
        return super.set(column, null);
    }

    /** 设置值为null（带条件） */
    public UpdateWrapper<T> setNull(boolean condition, String column) {
        return condition ? super.set(true, column, null) : this;
    }

    /** 递增字段值 */
    public SkipInvalidValueUpdateWrapper<T> setInc(String column, Number value) {
        if (ValueUtils.isValid(value)) {
            String columnName = this.columnToString(column);
            super.setSql(String.format("%s = %s + %s", columnName, columnName, value));
        } else if (strictMode) {
            log.debug("Skip invalid INCREMENT condition: {} += {}", column, value);
        }
        return this;
    }

    /** 递减字段值 */
    public SkipInvalidValueUpdateWrapper<T> setDec(String column, Number value) {
        if (ValueUtils.isValid(value)) {
            String columnName = this.columnToString(column);
            super.setSql(String.format("%s = %s - %s", columnName, columnName, value));
        } else if (strictMode) {
            log.debug("Skip invalid DECREMENT condition: {} -= {}", column, value);
        }
        return this;
    }
}
