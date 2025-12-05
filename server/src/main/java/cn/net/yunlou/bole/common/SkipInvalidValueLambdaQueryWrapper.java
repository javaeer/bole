package cn.net.yunlou.bole.common;

import cn.net.yunlou.bole.common.utils.BeanUtils;
import cn.net.yunlou.bole.common.utils.ValueUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * 忽略无效值的 LambdaQueryWrapper 自动过滤 null、空字符串、空集合等无效值，避免无效条件被拼接到SQL中
 *
 * <p>// 基础使用 SkipInvalidValueLambdaQueryWrapper<User> wrapper =
 * SkipInvalidValueLambdaQueryWrapper.of(User.class) .eq(User::getName, "张三") .eq(User::getAge,
 * null) // 自动跳过 .like(User::getEmail, ""); // 自动跳过
 *
 * <p>// 启用严格模式（调试用） SkipInvalidValueLambdaQueryWrapper<User> wrapper =
 * SkipInvalidValueLambdaQueryWrapper.create() .strictMode() .eq(User::getName, "张三");
 *
 * <p>// 链式操作 SkipInvalidValueLambdaQueryWrapper<User> wrapper =
 * SkipInvalidValueLambdaQueryWrapper.create() .apply(w -> w.eq(User::getStatus, 1))
 * .likeRight(User::getName, "张");
 */
@Getter
@Slf4j
public class SkipInvalidValueLambdaQueryWrapper<T> extends LambdaQueryWrapper<T> {

    private boolean strictMode = false; // 严格模式，如果为true会记录跳过条件的日志
    private boolean allowNullValue = false; // 是否允许null值

    public SkipInvalidValueLambdaQueryWrapper() {
        super();
    }

    public SkipInvalidValueLambdaQueryWrapper(T entity) {
        super();
        T filteredEntity = BeanUtils.filterInvalidValues(entity);
        super.setEntity(filteredEntity);
    }

    public SkipInvalidValueLambdaQueryWrapper(Class<T> entityClass) {
        super(entityClass);
    }

    // ============ 静态工厂方法 ============

    public static <T> SkipInvalidValueLambdaQueryWrapper<T> of(Class<T> entityClass) {
        return new SkipInvalidValueLambdaQueryWrapper<>(entityClass);
    }

    public static <T> SkipInvalidValueLambdaQueryWrapper<T> of(T entity) {
        return new SkipInvalidValueLambdaQueryWrapper<>(entity);
    }

    public static <T> SkipInvalidValueLambdaQueryWrapper<T> create() {
        return new SkipInvalidValueLambdaQueryWrapper<>();
    }

    public static <T> SkipInvalidValueLambdaQueryWrapper<T> create(Class<T> entityClass) {
        return new SkipInvalidValueLambdaQueryWrapper<>(entityClass);
    }

    // ============ 配置方法 ============

    /** 启用严格模式，跳过无效条件时会记录日志 */
    public SkipInvalidValueLambdaQueryWrapper<T> strictMode() {
        this.strictMode = true;
        return this;
    }

    /** 禁用严格模式 */
    public SkipInvalidValueLambdaQueryWrapper<T> lenientMode() {
        this.strictMode = false;
        return this;
    }

    public SkipInvalidValueLambdaQueryWrapper<T> allowNullValue() {
        this.allowNullValue = true;
        return this;
    }

    // ============ 重写核心方法 ============

    @Override
    public LambdaQueryWrapper<T> setEntity(T entity) {
        T filteredEntity = BeanUtils.filterInvalidValues(entity);
        super.setEntity(filteredEntity);
        return this;
    }

    // ============ 重写条件方法 ============

    @Override
    public SkipInvalidValueLambdaQueryWrapper<T> eq(SFunction<T, ?> column, Object val) {
        if (ValueUtils.isValid(val)) {
            super.eq(column, val);
        } else if (strictMode) {
            log.debug("Skip invalid EQ condition: {} = {}", column, val);
        }
        return this;
    }

    @Override
    public SkipInvalidValueLambdaQueryWrapper<T> ne(SFunction<T, ?> column, Object val) {
        if (ValueUtils.isValid(val)) {
            super.ne(column, val);
        } else if (strictMode) {
            log.debug("Skip invalid NE condition: {} != {}", column, val);
        }
        return this;
    }

    @Override
    public SkipInvalidValueLambdaQueryWrapper<T> like(SFunction<T, ?> column, Object val) {
        if (ValueUtils.isValid(val)) {
            super.like(column, val);
        } else if (strictMode) {
            log.debug("Skip invalid LIKE condition: {} LIKE {}", column, val);
        }
        return this;
    }

    @Override
    public SkipInvalidValueLambdaQueryWrapper<T> gt(SFunction<T, ?> column, Object val) {
        if (ValueUtils.isValid(val)) {
            super.gt(column, val);
        } else if (strictMode) {
            log.debug("Skip invalid GT condition: {} > {}", column, val);
        }
        return this;
    }

    @Override
    public SkipInvalidValueLambdaQueryWrapper<T> ge(SFunction<T, ?> column, Object val) {
        if (ValueUtils.isValid(val)) {
            super.ge(column, val);
        } else if (strictMode) {
            log.debug("Skip invalid GE condition: {} >= {}", column, val);
        }
        return this;
    }

    @Override
    public SkipInvalidValueLambdaQueryWrapper<T> lt(SFunction<T, ?> column, Object val) {
        if (ValueUtils.isValid(val)) {
            super.lt(column, val);
        } else if (strictMode) {
            log.debug("Skip invalid LT condition: {} < {}", column, val);
        }
        return this;
    }

    @Override
    public SkipInvalidValueLambdaQueryWrapper<T> le(SFunction<T, ?> column, Object val) {
        if (ValueUtils.isValid(val)) {
            super.le(column, val);
        } else if (strictMode) {
            log.debug("Skip invalid LE condition: {} <= {}", column, val);
        }
        return this;
    }

    @Override
    public SkipInvalidValueLambdaQueryWrapper<T> in(SFunction<T, ?> column, Object... values) {
        if (ValueUtils.isValid(values)) {
            super.in(column, values);
        } else if (strictMode) {
            log.debug("Skip invalid IN condition: {} IN {}", column, values);
        }
        return this;
    }

    @Override
    public SkipInvalidValueLambdaQueryWrapper<T> in(SFunction<T, ?> column, Collection<?> values) {
        if (ValueUtils.isValid(values)) {
            super.in(column, values);
        } else if (strictMode) {
            log.debug("Skip invalid IN condition: {} IN {}", column, values);
        }
        return this;
    }

    // ============ 新增常用方法 ============

    /** 模糊匹配 - 左模糊 */
    @Override
    public SkipInvalidValueLambdaQueryWrapper<T> likeLeft(SFunction<T, ?> column, Object val) {
        if (ValueUtils.isValid(val)) {
            super.likeLeft(column, val);
        } else if (strictMode) {
            log.debug("Skip invalid LIKE_LEFT condition: {} LIKE {}%", column, val);
        }
        return this;
    }

    /** 模糊匹配 - 右模糊 */
    @Override
    public SkipInvalidValueLambdaQueryWrapper<T> likeRight(SFunction<T, ?> column, Object val) {
        if (ValueUtils.isValid(val)) {
            super.likeRight(column, val);
        } else if (strictMode) {
            log.debug("Skip invalid LIKE_RIGHT condition: {} LIKE %{}", column, val);
        }
        return this;
    }

    /** 不包含 */
    @Override
    public SkipInvalidValueLambdaQueryWrapper<T> notIn(SFunction<T, ?> column, Object... values) {
        if (ValueUtils.isValid(values)) {
            super.notIn(column, values);
        } else if (strictMode) {
            log.debug("Skip invalid NOT_IN condition: {} NOT IN {}", column, values);
        }
        return this;
    }

    @Override
    public SkipInvalidValueLambdaQueryWrapper<T> notIn(
            SFunction<T, ?> column, Collection<?> values) {
        if (ValueUtils.isValid(values)) {
            super.notIn(column, values);
        } else if (strictMode) {
            log.debug("Skip invalid NOT_IN condition: {} NOT IN {}", column, values);
        }
        return this;
    }

    /** BETWEEN 条件 */
    @Override
    public SkipInvalidValueLambdaQueryWrapper<T> between(
            SFunction<T, ?> column, Object val1, Object val2) {
        boolean valid1 = ValueUtils.isValid(val1);
        boolean valid2 = ValueUtils.isValid(val2);

        if (valid1 && valid2) {
            super.between(column, val1, val2);
        } else if (valid1) {
            super.ge(column, val1); // 只有开始值，转换为 >=
        } else if (valid2) {
            super.le(column, val2); // 只有结束值，转换为 <=
        } else if (strictMode) {
            log.debug("Skip invalid BETWEEN condition: {} BETWEEN {} AND {}", column, val1, val2);
        }
        return this;
    }

    /** 链式操作支持 */
    public SkipInvalidValueLambdaQueryWrapper<T> apply(
            Consumer<SkipInvalidValueLambdaQueryWrapper<T>> consumer) {
        consumer.accept(this);
        return this;
    }
}
