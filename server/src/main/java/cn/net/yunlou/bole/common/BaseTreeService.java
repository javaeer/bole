package cn.net.yunlou.bole.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.common.collect.Lists;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * </p>
 *
 * @Author javaeer(javaeer @ aliyun.com)
 * @Date 2019/12/12 09:20
 * @Version 1.0
 */
@Transactional(readOnly = true)
public abstract class BaseTreeService<
        M extends BaseMapper<T>,
        T extends BaseTreeEntity<T>,
        D extends BaseDTO,
        Q extends BaseQuery,
        S extends BaseStructMapper<T, D, Q>
        > extends BaseService<M, T, D, Q, S> implements IBaseTreeService<T, D, Q> {


    /**
     * <p>
     * desc: 根据父类id获取下级子类集合(会将子类追加至children属性下)
     * </p>
     *
     * @param id
     * @return {@link T}
     * @author javaeer(javaeer @ aliyun.com)
     * @date 2020/7/24 14:31
     */
    @Override
    public T getChildren(Serializable id) {
        Assert.isTrue(ObjectUtils.isNotEmpty(id), "The primary key cannot be empty");
        T entity = getById(id);
        if (ObjectUtils.isNotEmpty(entity) && ObjectUtils.isNotEmpty(entity.getId()) && !Objects.equals(entity.getId(), 0L)) {
            List<T> children = listChildren(entity.getId());
            if (!Collections.isEmpty(children)) {
                entity.setChildren(children);
            }
        }
        return entity;
    }

    /**
     * <p>
     * desc: 根据父类id获取所有子类集合(会将子类追加至children属性下)
     * </p>
     *
     * @param id
     * @return {@link T}
     * @author javaeer(javaeer @ aliyun.com)
     * @date 2020/7/24 14:31
     */
    @Override
    public T getAllChildren(Serializable id) {
        Assert.isTrue(ObjectUtils.isNotEmpty(id), "The primary key cannot be empty");
        T entity = getById(id);
        if (ObjectUtils.isNotEmpty(entity)) {
            List<T> children = listChildren(id);
            if (!Collections.isEmpty(children)) {
                List<T> temps = Lists.newArrayList();
                for (T t : children) {
                    if (ObjectUtils.isEmpty(t) || ObjectUtils.isEmpty(t.getId())) {
                        continue;
                    }
                    t = getAllChildren(t.getId());
                    temps.add(t);
                    entity.setChildren(temps);
                }
            }
        }
        return entity;
    }


    /**
     * 获取指定节点的下级子节点列表
     *
     * @param id
     * @return
     */
    @Override
    public List<T> listChildren(Serializable id) {
        Assert.isTrue(ObjectUtils.isNotEmpty(id), "The primary key cannot be empty");
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        return super.list(queryWrapper);
    }

    @Override
    public List<T> listChildren(T entity) {
        LambdaQueryWrapper<T> queryWrapper = getBaseQueryWrapper(entity);
        if (ObjectUtils.isNotEmpty(entity) && ObjectUtils.isNotEmpty(entity.getId()) && !Objects.equals(entity.getId(), 0L)) {
            queryWrapper.eq(T::getParentId, entity.getId());
        } else {
            queryWrapper.isNull(T::getParentId);
        }
        return super.list(queryWrapper);
    }

    /**
     * <p>
     * desc:
     * 遍历整棵树结构 子节点对象集合，将子所有节点追加到 children
     * </p>
     *
     * @return {@link null}
     * @author javaeer(javaeer @ aliyun.com)
     * @date 2020/7/24 17:36
     */
    @Override
    public List<T> listAllChildren() {
        return listAllChildren(null);
    }

    /**
     * <p>
     * desc:
     * 获取指定节点的下级对象的集合，将子所有节点追加到 children
     *
     * </p>
     *
     * @param id
     * @return {@link null}
     * @author javaeer(javaeer @ aliyun.com)
     * @date 2020/7/24 17:36
     */
    @Override
    public List<T> listAllChildren(Serializable id) {
        Assert.isTrue(ObjectUtils.isNotEmpty(id), "The primary key cannot be empty");
        List<T> allChildren = Lists.newArrayList();
        List<T> children = listChildren(id);
        if (!Collections.isEmpty(children)) {
            for (T t : children) {
                if (ObjectUtils.isEmpty(t) || ObjectUtils.isEmpty(t.getId())) {
                    continue;
                }
                t = getAllChildren(t.getId());
                if (ObjectUtils.isNotEmpty(t)) {
                    allChildren.add(t);
                }
            }
        }
        return allChildren;
    }

    @Override
    public List<T> listAllChildren(T entity) {
        List<T> allChildren = Lists.newArrayList();
        List<T> children = listChildren(entity);
        if (!Collections.isEmpty(children)) {
            for (T t : children) {
                if (ObjectUtils.isEmpty(t) || ObjectUtils.isEmpty(t.getId())) {
                    continue;
                }
                t = getAllChildren(t.getId());
                if (ObjectUtils.isNotEmpty(t)) {
                    allChildren.add(t);
                }
            }
        }
        return allChildren;
    }

    @Override
    public List<T> listLeaf(Serializable id) {
        Assert.isTrue(ObjectUtils.isNotEmpty(id), "The primary key cannot be empty");
        List<T> list = Lists.newArrayList();
        T entity = getById(id);
        if (ObjectUtils.isNotEmpty(entity) && ObjectUtils.isNotEmpty(entity.getId()) && !Objects.equals(entity.getId(), 0L)) {
            if (hasChildren(entity.getId())) {
                List<T> children = listChildren(entity.getId());
                for (T t : children) {
                    if (hasChildren(t.getId())) {
                        list.addAll(listLeaf(t.getId()));
                    } else {
                        list.add(t);
                    }
                }
            } else {
                list.add(entity);
            }
        }
        return list;
    }

    @Override
    public boolean hasChildren(Serializable id) {
        return countChildren(id) > 0;
    }


    @Override
    public long countChildren(Serializable id) {
        Assert.isTrue(ObjectUtils.isNotEmpty(id), "The primary key cannot be empty");
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        return super.count(queryWrapper);
    }

    /**
     * <p>
     * desc: 根据子类ID 获取父类对象
     * </p>
     *
     * @param id
     * @return {@link T}
     * @author javaeer(javaeer @ aliyun.com)
     * @date 2020/7/24 14:30
     */
    @Override
    public T getParent(Serializable id) {
        T parent = null;
        T entity = getById(id);
        if (ObjectUtils.isNotEmpty(entity) && ObjectUtils.isNotEmpty(entity.getParentId()) && !Objects.equals(entity.getParentId(), 0L)) {
            parent = getById(entity.getParentId());
        }
        return parent;
    }


    @Override
    public T getGrandpa(Serializable id) {
        T grandPa = null;
        T parent = getParent(id);
        if (ObjectUtils.isNotEmpty(parent) && ObjectUtils.isNotEmpty(parent.getParentId())) {
            grandPa = getParent(parent.getParentId());
        }
        return grandPa;
    }


    @Override
    public T getRoot(Serializable id) {
        T entity = getById(id);
        if (ObjectUtils.isNotEmpty(entity) && ObjectUtils.isNotEmpty(entity.getParentId()) && !Objects.equals(entity.getParentId(), 0L)) {
            entity = getRoot(entity.getParentId());
        }
        return entity;
    }


    @Override
    public LambdaQueryWrapper<T> getBaseQueryWrapper(T entity) {
        LambdaQueryWrapper<T> queryWrapper = super.getBaseQueryWrapper(entity);
        if (ObjectUtils.isNotEmpty(entity) && ObjectUtils.isNotEmpty(entity.getParentId()) && !Objects.equals(entity.getParentId(), 0L)) {
            queryWrapper.eq(T::getParentId, entity.getParentId());
        }
        return queryWrapper;
    }
}
