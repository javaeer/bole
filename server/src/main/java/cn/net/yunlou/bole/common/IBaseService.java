package cn.net.yunlou.bole.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * 自定义基础Service接口
 *
 * <p>IService MybatisPlus 接口 IStructService MapStructMapper 接口
 *
 * @param <T> 实体类型
 * @param <D> DTO类型
 */
public interface IBaseService<T, D, Q> extends IService<T>, IStructService<D, Q> {

    //  ==============针对 MP mapper 定制开始 =================
    boolean exist(T entity);

    T get(T entity);

    boolean remove(T entity);

    boolean update(T entity);

    long count(T entity);

    List<T> list(T entity);

    Page<T> page(long pageNum, long pageSize, T entity);

    Page<T> page(long pageNum, long pageSize, long timestamp, T entity);

    Page<T> page(long pageNum, long pageSize, long timestamp, QueryWrapper<T> queryWrapper);

    QueryWrapper<T> getBaseQueryWrapper(T entity);

    UpdateWrapper<T> getBaseUpdateWrapper(T entity);

    //  ==============针对 MP mapper 定制结束 =================
}
