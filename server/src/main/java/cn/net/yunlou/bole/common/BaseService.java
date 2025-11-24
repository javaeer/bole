package cn.net.yunlou.bole.common;

import cn.net.yunlou.bole.utils.DateUtils;
import cn.net.yunlou.bole.utils.ValueUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * FileName: BaseService
 * Description:
 * Created By laughtiger
 * Created At 2025/11/19 13:50
 * Modified By
 * Modified At
 */
@Slf4j
@Transactional(readOnly = true)
public abstract class BaseService<M extends BaseMapper<T>, T extends BaseEntity>
        extends ServiceImpl<M, T> implements IBaseService<T> {

    @Override
    public boolean exist(T entity) {
        return count(entity) > 0;
    }

    @Override
    public T get(T entity) {
        return getOne(getBaseQueryWrapper(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean remove(T entity) {
        return remove(getBaseQueryWrapper(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(T entity) {
        return update(getBaseUpdateWrapper(entity));
    }

    @Override
    public long count(T entity) {
        return count(getBaseQueryWrapper(entity));
    }

    @Override
    public List<T> list(T entity) {
        return list(getBaseQueryWrapper(entity));
    }

    @Override
    public Page<T> page(long pageNum, long pageSize, T entity) {
        return page(pageNum, pageSize, 0L, entity);
    }

    @Override
    public Page<T> page(long pageNum, long pageSize, long timestamp, T entity) {
        return page(pageNum, pageSize, timestamp, getBaseQueryWrapper(entity));
    }

    @Override
    public Page<T> page(
            long pageNum, long pageSize, long timestamp, LambdaQueryWrapper<T> queryWrapper) {
        Page<T> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);

        if (timestamp > 0) {
            Date date;
            if (timestamp < 1_000_000_000_000L) { // 小于 10^12，认为是秒级
                date = DateUtils.fromUnixTimestamp(timestamp);
            } else {
                date = DateUtils.fromTimestamp(timestamp);
            }
            queryWrapper.lt(T::getCreatedAt, DateUtils.truncate(date, Calendar.SECOND));
        }
        return page(page, queryWrapper);
    }

    @Override
    public LambdaQueryWrapper<T> getBaseQueryWrapper(T entity) {
        SkipInvalidValueLambdaQueryWrapper<T> queryWrapper = SkipInvalidValueWrappers.lambdaQuery(entity);
        if (ValueUtils.isValid(entity.getKeyWords())) {
            queryWrapper = getKeyFieldQueryWrapper(queryWrapper, entity);
        }
        return queryWrapper;
    }

    @Override
    public LambdaUpdateWrapper<T> getBaseUpdateWrapper(T entity) {
        return SkipInvalidValueWrappers.lambdaUpdate(entity);
    }

    /**
     * 如果存在 关键字 查询 请务必 重写此方法
     *
     * @param queryWrapper 现有 条件
     * @param entity       实体
     * @return 组合条件
     */
    protected SkipInvalidValueLambdaQueryWrapper<T> getKeyFieldQueryWrapper(SkipInvalidValueLambdaQueryWrapper<T> queryWrapper, T entity) {
        return queryWrapper;
    }
}
