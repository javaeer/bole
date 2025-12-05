package cn.net.yunlou.bole.common;

import cn.net.yunlou.bole.common.utils.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * FileName: BaseService Description: Created By MR. WANG Created At 2025/11/19 13:50 Modified By
 * Modified At
 */
@Slf4j
@Transactional(readOnly = true)
public abstract class BaseService<
                M extends BaseMapper<T>,
                T extends BaseEntity,
                C extends BaseCreate,
                V extends BaseView,
                E extends BaseEdit,
                Q extends BaseQuery,
                S extends BaseStructMapper<T, C, V, E, Q> // 添加具体映射器类型参数
                >
        extends ServiceImpl<M, T> implements IBaseService<T, C, V, E, Q> {

    @Autowired protected S structMapper;

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
    public Page<T> page(long pageNum, long pageSize, long timestamp, QueryWrapper<T> queryWrapper) {
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
            queryWrapper.lt("created_at", DateUtils.truncate(date, Calendar.SECOND));
        }
        return page(page, queryWrapper);
    }

    @Override
    public QueryWrapper<T> getBaseQueryWrapper(T entity) {
        return SkipInvalidValueWrappers.query(entity);
    }

    @Override
    public UpdateWrapper<T> getBaseUpdateWrapper(T entity) {
        return SkipInvalidValueWrappers.update(entity);
    }

    /**
     * 如果存在 关键字 查询 请务必 重写此方法
     *
     * @param queryWrapper 现有 条件
     * @param entity 实体
     * @return 组合条件
     */
    protected QueryWrapper<T> getKeyFieldQueryWrapper(QueryWrapper<T> queryWrapper, T entity) {
        return queryWrapper;
    }

    // =========================MP 封装完成 ================================

    // ==========================MS 封装开始=================================

    @Override
    public V getViewById(Serializable id) {
        T entity = getById(id);
        return structMapper.toView(entity);
    }

    @Override
    public boolean saveByCreate(C dto) {
        T entity = structMapper.createToEntity(dto);
        return save(entity);
    }

    @Override
    public boolean updateByEdit(E dto) {
        T entity = structMapper.editToEntity(dto);
        return update(entity);
    }

    @Override
    public boolean removeByQuery(Q dto) {
        T entity = structMapper.queryToEntity(dto);
        return remove(entity);
    }

    @Override
    public Page<V> pageViewByQuery(long pageNum, long pageSize, Q query) {
        T entity = structMapper.queryToEntity(query);
        return structMapper.toViewPage(page(pageNum, pageSize, entity));
    }

    @Override
    public List<V> listViewByQuery(Q query) {
        T entity = structMapper.queryToEntity(query);
        return structMapper.toViews(list(entity));
    }
}
