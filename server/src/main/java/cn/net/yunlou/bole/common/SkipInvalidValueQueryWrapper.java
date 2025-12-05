package cn.net.yunlou.bole.common;

import cn.net.yunlou.bole.common.utils.BeanUtils;
import cn.net.yunlou.bole.common.utils.ValueUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.Collection;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 忽略无效值的 QueryWrapper 自动过滤 null、空字符串、空集合等无效值，避免无效条件被拼接到SQL中
 *
 * <p>// 基础使用 SkipInvalidValueQueryWrapper<User> wrapper =
 * SkipInvalidValueQueryWrapper.of(User.class) .eq(User::getName, "张三") .eq(User::getAge, null) //
 * 自动跳过 .like(User::getEmail, ""); // 自动跳过
 *
 * <p>// 启用严格模式（调试用） SkipInvalidValueQueryWrapper<User> wrapper =
 * SkipInvalidValueQueryWrapper.create() .strictMode() .eq(User::getName, "张三");
 *
 * <p>// 链式操作 SkipInvalidValueQueryWrapper<User> wrapper = SkipInvalidValueQueryWrapper.create()
 * .apply(w -> w.eq(User::getStatus, 1)) .likeRight(User::getName, "张");
 */
@Getter
@Slf4j
public class SkipInvalidValueQueryWrapper<T> extends QueryWrapper<T> {

    private boolean strictMode = false; // 严格模式，如果为true会记录跳过条件的日志
    private boolean allowNullValue = false; // 是否允许null值

    public SkipInvalidValueQueryWrapper() {
        super();
    }

    public SkipInvalidValueQueryWrapper(T entity) {
        super();
        T filteredEntity = BeanUtils.filterInvalidValues(entity);
        super.setEntity(filteredEntity);
    }

    public SkipInvalidValueQueryWrapper(Class<T> entityClass) {
        super(entityClass);
    }

    // ============ 静态工厂方法 ============

    public static <T> SkipInvalidValueQueryWrapper<T> of(Class<T> entityClass) {
        return new SkipInvalidValueQueryWrapper<>(entityClass);
    }

    public static <T> SkipInvalidValueQueryWrapper<T> of(T entity) {
        return new SkipInvalidValueQueryWrapper<>(entity);
    }

    public static <T> SkipInvalidValueQueryWrapper<T> create() {
        return new SkipInvalidValueQueryWrapper<>();
    }

    public static <T> SkipInvalidValueQueryWrapper<T> create(Class<T> entityClass) {
        return new SkipInvalidValueQueryWrapper<>(entityClass);
    }

    // ============ 配置方法 ============

    /** 启用严格模式，跳过无效条件时会记录日志 */
    public SkipInvalidValueQueryWrapper<T> strictMode() {
        this.strictMode = true;
        return this;
    }

    /** 禁用严格模式 */
    public SkipInvalidValueQueryWrapper<T> lenientMode() {
        this.strictMode = false;
        return this;
    }

    public SkipInvalidValueQueryWrapper<T> allowNullValue() {
        this.allowNullValue = true;
        return this;
    }

    // ============ 重写核心方法 ============

    @Override
    public QueryWrapper<T> setEntity(T entity) {
        T filteredEntity = BeanUtils.filterInvalidValues(entity);
        super.setEntity(filteredEntity);
        return this;
    }

    // ============ 重写条件方法 ============

    @Override
    public SkipInvalidValueQueryWrapper<T> eq(String column, Object val) {
        if (ValueUtils.isValid(val)) {
            super.eq(column, val);
        } else if (strictMode) {
            log.debug("Skip invalid EQ condition: {} = {}", column, val);
        }
        return this;
    }

    @Override
    public SkipInvalidValueQueryWrapper<T> ne(String column, Object val) {
        if (ValueUtils.isValid(val)) {
            super.ne(column, val);
        } else if (strictMode) {
            log.debug("Skip invalid NE condition: {} != {}", column, val);
        }
        return this;
    }

    @Override
    public SkipInvalidValueQueryWrapper<T> like(String column, Object val) {
        if (ValueUtils.isValid(val)) {
            super.like(column, val);
        } else if (strictMode) {
            log.debug("Skip invalid LIKE condition: {} LIKE {}", column, val);
        }
        return this;
    }

    @Override
    public SkipInvalidValueQueryWrapper<T> gt(String column, Object val) {
        if (ValueUtils.isValid(val)) {
            super.gt(column, val);
        } else if (strictMode) {
            log.debug("Skip invalid GT condition: {} > {}", column, val);
        }
        return this;
    }

    @Override
    public SkipInvalidValueQueryWrapper<T> ge(String column, Object val) {
        if (ValueUtils.isValid(val)) {
            super.ge(column, val);
        } else if (strictMode) {
            log.debug("Skip invalid GE condition: {} >= {}", column, val);
        }
        return this;
    }

    @Override
    public SkipInvalidValueQueryWrapper<T> lt(String column, Object val) {
        if (ValueUtils.isValid(val)) {
            super.lt(column, val);
        } else if (strictMode) {
            log.debug("Skip invalid LT condition: {} < {}", column, val);
        }
        return this;
    }

    @Override
    public SkipInvalidValueQueryWrapper<T> le(String column, Object val) {
        if (ValueUtils.isValid(val)) {
            super.le(column, val);
        } else if (strictMode) {
            log.debug("Skip invalid LE condition: {} <= {}", column, val);
        }
        return this;
    }

    @Override
    public SkipInvalidValueQueryWrapper<T> in(String column, Object... values) {
        if (ValueUtils.isValid(values)) {
            super.in(column, values);
        } else if (strictMode) {
            log.debug("Skip invalid IN condition: {} IN {}", column, values);
        }
        return this;
    }

    @Override
    public SkipInvalidValueQueryWrapper<T> in(String column, Collection<?> values) {
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
    public SkipInvalidValueQueryWrapper<T> likeLeft(String column, Object val) {
        if (ValueUtils.isValid(val)) {
            super.likeLeft(column, val);
        } else if (strictMode) {
            log.debug("Skip invalid LIKE_LEFT condition: {} LIKE {}%", column, val);
        }
        return this;
    }

    /** 模糊匹配 - 右模糊 */
    @Override
    public SkipInvalidValueQueryWrapper<T> likeRight(String column, Object val) {
        if (ValueUtils.isValid(val)) {
            super.likeRight(column, val);
        } else if (strictMode) {
            log.debug("Skip invalid LIKE_RIGHT condition: {} LIKE %{}", column, val);
        }
        return this;
    }

    /** 不包含 */
    @Override
    public SkipInvalidValueQueryWrapper<T> notIn(String column, Object... values) {
        if (ValueUtils.isValid(values)) {
            super.notIn(column, values);
        } else if (strictMode) {
            log.debug("Skip invalid NOT_IN condition: {} NOT IN {}", column, values);
        }
        return this;
    }

    @Override
    public SkipInvalidValueQueryWrapper<T> notIn(String column, Collection<?> values) {
        if (ValueUtils.isValid(values)) {
            super.notIn(column, values);
        } else if (strictMode) {
            log.debug("Skip invalid NOT_IN condition: {} NOT IN {}", column, values);
        }
        return this;
    }

    /** BETWEEN 条件 */
    @Override
    public SkipInvalidValueQueryWrapper<T> between(String column, Object val1, Object val2) {
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
    public SkipInvalidValueQueryWrapper<T> apply(
            Consumer<SkipInvalidValueQueryWrapper<T>> consumer) {
        consumer.accept(this);
        return this;
    }
}
