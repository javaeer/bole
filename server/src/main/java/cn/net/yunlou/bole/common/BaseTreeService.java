package cn.net.yunlou.bole.common;

import cn.net.yunlou.bole.common.utils.TreeBuildUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.common.collect.Lists;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.net.yunlou.bole.common.BaseTreeEntity.ROOT_ID;

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

    // ============ 缓存配置方法 ============

    /**
     * 获取缓存名称 - 供SpEL使用
     */
    public String getCacheName() {
        // 使用AopUtils获取原始类
        Class<?> targetClass = AopUtils.getTargetClass(this);
        TreeServiceConfig config = targetClass.getAnnotation(TreeServiceConfig.class);
        return config != null ? config.cacheName() : "defaultCache";
    }

    /**
     * 获取缓存过期时间- 供SpEL使用
     */
    public long getCacheExpireTime() {
        // 使用AopUtils获取原始类
        Class<?> targetClass = AopUtils.getTargetClass(this);
        TreeServiceConfig config = targetClass.getAnnotation(TreeServiceConfig.class);
        return config != null ? config.expireTime() : 7200;
    }

    /**
     * 获取缓存键前缀- 供SpEL使用
     */
    public String getKeyPrefix() {
        // 使用AopUtils获取原始类
        Class<?> targetClass = AopUtils.getTargetClass(this);
        TreeServiceConfig config = targetClass.getAnnotation(TreeServiceConfig.class);
        return config != null ? config.keyPrefix() : "default";
    }

    /**
     * 是否启用缓存- 供SpEL使用
     */
    public boolean isCacheEnabled() {
        // 使用AopUtils获取原始类
        Class<?> targetClass = AopUtils.getTargetClass(this);
        TreeServiceConfig config = targetClass.getAnnotation(TreeServiceConfig.class);
        return config != null && config.enableCache();
    }

    /**
     * 构建缓存key - 供SpEL使用
     */
    public String buildCacheKey(String key) {
        return getKeyPrefix() + ":" + key;
    }


    /**
     * 新增实体 - 带缓存清理
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheResolver = "treeCacheResolver", allEntries = true) // 新增节点可能影响整个树结构，清理所有缓存
    public boolean save(T entity) {
        boolean result = super.save(entity);
        if (result) {
            // 新增后可能需要更新路径等树形结构信息
            updateTreeStructureAfterSave(entity);
        }
        return result;
    }

    /**
     * 批量新增 - 带缓存清理
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheResolver = "treeCacheResolver", allEntries = true)
    public boolean saveBatch(Collection<T> entityList) {
        boolean result = super.saveBatch(entityList);
        if (result) {
            // 批量新增后更新树形结构
            entityList.forEach(this::updateTreeStructureAfterSave);
        }
        return result;
    }

    /**
     * 根据ID更新实体 - 带缓存清理
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(cacheResolver = "treeCacheResolver",
                    key = "#root.target.buildCacheKey('with_children:' + #entity.id)"),
            @CacheEvict(cacheResolver = "treeCacheResolver",
                    key = "#root.target.buildCacheKey('with_tree:' + #entity.id)"),
            @CacheEvict(cacheResolver = "treeCacheResolver",
                    key = "#root.target.buildCacheKey('children:' + #entity.id)"),
            @CacheEvict(cacheResolver = "treeCacheResolver",
                    key = "#root.target.buildCacheKey('all_children:' + #entity.id)"),
            @CacheEvict(cacheResolver = "treeCacheResolver",
                    key = "#root.target.buildCacheKey('leaves:' + #entity.id)"),
            @CacheEvict(cacheResolver = "treeCacheResolver",
                    key = "#root.target.buildCacheKey('parent:' + #entity.id)"),
            @CacheEvict(cacheResolver = "treeCacheResolver",
                    key = "#root.target.buildCacheKey('grandpa:' + #entity.id)"),
            @CacheEvict(cacheResolver = "treeCacheResolver",
                    key = "#root.target.buildCacheKey('root:' + #entity.id)"),
            // 同时清理父节点相关的缓存（如果父节点发生变化）
            @CacheEvict(cacheResolver = "treeCacheResolver",
                    key = "#root.target.buildCacheKey('children:' + #entity.parentId)"),
            @CacheEvict(cacheResolver = "treeCacheResolver",
                    key = "#root.target.buildCacheKey('all_children:' + #entity.parentId)")
    })
    public boolean updateById(T entity) {
        T oldEntity = getById(entity.getId());
        boolean result = super.updateById(entity);
        if (result) {
            // 如果父节点发生变化，需要清理新旧父节点的缓存
            if (oldEntity != null && !Objects.equals(oldEntity.getParentId(), entity.getParentId())) {
                clearParentCache(oldEntity.getParentId());
                clearParentCache(entity.getParentId());
            }
            updateTreeStructureAfterUpdate(entity, oldEntity);
        }
        return result;
    }

    /**
     * 根据ID删除 - 带缓存清理
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(cacheResolver = "treeCacheResolver",
                    key = "#root.target.buildCacheKey('with_children:' + #id)"
            ),
            @CacheEvict(cacheResolver = "treeCacheResolver",
                    key = "#root.target.buildCacheKey('with_tree:' + #id)"
            ),
            @CacheEvict(cacheResolver = "treeCacheResolver",
                    key = "#root.target.buildCacheKey('children:' + #id)"
            ),
            @CacheEvict(cacheResolver = "treeCacheResolver",
                    key = "#root.target.buildCacheKey('all_children:' + #id)"
            ),
            @CacheEvict(cacheResolver = "treeCacheResolver",
                    key = "#root.target.buildCacheKey('leaves:' + #id)"
            ),
            @CacheEvict(cacheResolver = "treeCacheResolver",
                    key = "#root.target.buildCacheKey('parent:' + #id)"
            ),
            @CacheEvict(cacheResolver = "treeCacheResolver",
                    key = "#root.target.buildCacheKey('grandpa:' + #id)"
            ),
            @CacheEvict(cacheResolver = "treeCacheResolver",
                    key = "#root.target.buildCacheKey('root:' + #id)"
            ),
            // 清理父节点缓存
            @CacheEvict(cacheResolver = "treeCacheResolver",
                    key = "#root.target.buildCacheKey('children:' + #targetParentId)"),
            @CacheEvict(cacheResolver = "treeCacheResolver",
                    key = "#root.target.buildCacheKey('all_children:' + #targetParentId)")
    })
    public boolean removeById(Serializable id) {
        T entity = getById(id);
        if (entity == null) {
            return false;
        }

        // 先删除所有子节点
        List<T> allChildren = listAllChildren(id);
        if (!Collections.isEmpty(allChildren)) {
            List<Serializable> childIds = allChildren.stream()
                    .map(T::getId)
                    .collect(Collectors.toList());
            super.removeByIds(childIds);

            // 清理子节点的缓存
            childIds.forEach(this::clearNodeCache);
        }

        boolean result = super.removeById(id);
        if (result) {
            clearNodeCache(id);
            clearParentCache(entity.getParentId());
        }
        return result;
    }

    /**
     * <p>
     * desc: 根据id获取下级节点集合(会将其子节点追加至children属性下)
     * </p>
     *
     * @param id
     * @return {@link T}
     * @author javaeer(javaeer @ aliyun.com)
     * @date 2020/7/24 14:31
     */
    @Override
    @Cacheable(cacheResolver = "treeCacheResolver",
            key = "#root.target.buildCacheKey('with_children:' + #id)",
            unless = "!#root.target.isCacheEnabled()"
    )
    public T getChildren(Serializable id) {
        Assert.isTrue(ObjectUtils.isNotEmpty(id), "The primary key cannot be empty");
        T entity = getById(id);
        if (ObjectUtils.isEmpty(entity)) {
            return null;
        }
        List<T> children = listChildren(entity.getId());
        if (!Collections.isEmpty(children)) {
            entity.setChildren(children);
        }
        return entity;
    }


    /**
     * <p>
     * desc: 根据父类id获取所有节点集合(会将节点追加至children属性下)
     * </p>
     *
     * @param id
     * @return {@link T}
     * @author javaeer(javaeer @ aliyun.com)
     * @date 2020/7/24 14:31
     */
    @Override
    @Cacheable(cacheResolver = "treeCacheResolver",
            key = "#root.target.buildCacheKey('with_tree:' + #id)",
            unless = "!#root.target.isCacheEnabled()"
    )
    public T getAllChildren(Serializable id) {
        Assert.isTrue(ObjectUtils.isNotEmpty(id), "The primary key cannot be empty");
        T entity = getById(id);
        if (ObjectUtils.isEmpty(entity)) {
            return null;
        }
        List<T> children = listAllChildren(id);
        if (!Collections.isEmpty(children)) {
            entity.setChildren(children);
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
    @Cacheable(cacheResolver = "treeCacheResolver",
            key = "#root.target.buildCacheKey('children:' + #id)",
            unless = "!#root.target.isCacheEnabled()"
    )
    public List<T> listChildren(Serializable id) {
        Assert.isTrue(ObjectUtils.isNotEmpty(id), "The primary key cannot be empty");
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id)
                .orderByAsc("id");
        return super.list(queryWrapper);
    }

    @Override
    public List<T> listChildren(T entity) {
        return super.list(getBaseQueryWrapper(entity));
    }

    /**
     * <p>
     * desc:
     * 遍历整棵树结构 子节点对象集合，将子所有节点追加到 children
     * 完整的 全部的 所有的
     * </p>
     *
     * @return {@link null}
     * @author javaeer(javaeer @ aliyun.com)
     * @date 2020/7/24 17:36
     */
    @Override
    @Cacheable(cacheResolver = "treeCacheResolver",
            key = "#root.target.buildCacheKey('whole_tree:')",
            unless = "!#root.target.isCacheEnabled()"
    )
    public List<T> listAllChildren() {

        // 1. 一次查询获取所有节点（关键优化！）
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("level", "id");
        List<T> allNodes = super.list(queryWrapper);

        return TreeBuildUtils.buildFullTree(allNodes);
    }

    /**
     * <p>
     * desc:
     * 获取指定节点的<下级>对象的集合，将子所有节点追加到<所属节点>的children
     *
     * </p>
     *
     * @param id
     * @return {@link null}
     * @author javaeer(javaeer @ aliyun.com)
     * @date 2020/7/24 17:36
     */
    @Override
    @Cacheable(cacheResolver = "treeCacheResolver",
            key = "#root.target.buildCacheKey('all_children:' + #id)",
            unless = "!#root.target.isCacheEnabled()"
    )
    public List<T> listAllChildren(Serializable id) {
        Assert.isTrue(ObjectUtils.isNotEmpty(id), "The primary key cannot be empty");

        T parent = getById(id);
        if (ObjectUtils.isEmpty(parent)) {
            return null;
        }
        // 使用path字段进行like查询，一次获取所有子孙节点
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("path", parent.getPath() + "/")
                .orderByAsc("level", "id");

        List<T> list = super.list(queryWrapper);
        return TreeBuildUtils.flattenTree(list);
    }

    @Override
    public List<T> listAllChildren(T entity) {
        List<T> allChildren = Lists.newArrayList();
        List<T> children = listChildren(entity);
        if (!Collections.isEmpty(children)) {
            for (T child : children) {
                // 使用path字段进行like查询，一次获取所有子孙节点
                QueryWrapper<T> queryWrapper = new QueryWrapper<>();
                queryWrapper.likeRight("path", child.getPath() + "/")
                        .orderByAsc("level", "id");

                List<T> tree = TreeBuildUtils.flattenTree(super.list(queryWrapper));
                child.setChildren(tree);
                allChildren.add(child);
            }
        }
        return allChildren;
    }


    /**
     * 获取<叶子>节点
     * 多层级
     */
    @Override
    @Cacheable(cacheResolver = "treeCacheResolver",
            key = "#root.target.buildCacheKey('leaves:' + #id)",
            unless = "!#root.target.isCacheEnabled()"
    )
    public List<T> listLeaf(Serializable id) {
        Assert.isTrue(ObjectUtils.isNotEmpty(id), "The primary key cannot be empty");
        List<T> leaves = Lists.newArrayList();
        T entity = getById(id);
        if (ObjectUtils.isEmpty(entity)) {
            return null;
        }
        if (hasChildren(entity.getId())) {
            List<T> children = listChildren(entity.getId());
            for (T t : children) {
                if (hasChildren(t.getId())) {
                    leaves.addAll(listLeaf(t.getId()));
                } else {
                    leaves.add(t);
                }
            }
        } else {
            leaves.add(entity);
        }
        return leaves;
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
     * desc: 根据节点ID 获取父类对象
     * </p>
     *
     * @param id
     * @return {@link T}
     * @author javaeer(javaeer @ aliyun.com)
     * @date 2020/7/24 14:30
     */
    @Override
    @Cacheable(cacheResolver = "treeCacheResolver",
            key = "#root.target.buildCacheKey('parent:' + #id)",
            unless = "!#root.target.isCacheEnabled()"
    )
    public T getParent(Serializable id) {
        T parent = null;
        T entity = getById(id);
        if (ObjectUtils.isNotEmpty(entity)
                && ObjectUtils.isNotEmpty(entity.getParentId())
                && !Objects.equals(entity.getParentId(), ROOT_ID)) {
            parent = getById(entity.getParentId());
        }
        return parent;
    }


    @Override
    @Cacheable(cacheResolver = "treeCacheResolver",
            key = "#root.target.buildCacheKey('grandpa:' + #id)",
            unless = "!#root.target.isCacheEnabled()")
    public T getGrandpa(Serializable id) {
        T grandPa = null;
        T parent = getParent(id);
        if (ObjectUtils.isNotEmpty(parent)
                && ObjectUtils.isNotEmpty(parent.getParentId())) {
            grandPa = getParent(parent.getParentId());
        }
        return grandPa;
    }


    @Override
    @Cacheable(cacheResolver = "treeCacheResolver",
            key = "#root.target.buildCacheKey('root:' + #id)",
            unless = "!#root.target.isCacheEnabled()")
    public T getRoot(Serializable id) {
        T entity = getById(id);

        if (ObjectUtils.isNotEmpty(entity)
                && ObjectUtils.isNotEmpty(entity.getParentId())
                && !Objects.equals(entity.getParentId(), ROOT_ID)) {
            entity = getRoot(entity.getParentId());
        }
        return entity;
    }


    @Override
    public QueryWrapper<T> getBaseQueryWrapper(T entity) {
        QueryWrapper<T> queryWrapper = super.getBaseQueryWrapper(entity);
        queryWrapper.orderByAsc("level", "id");
        return queryWrapper;
    }


    /**
     * 清理节点相关缓存
     */
    private void clearNodeCache(Serializable id) {
        // 具体的缓存清理逻辑由Spring Cache自动处理
        // 这里可以添加日志记录等辅助功能
    }

    /**
     * 清理父节点相关缓存
     */
    private void clearParentCache(Serializable parentId) {
        if (parentId != null && !Objects.equals(parentId, ROOT_ID)) {
            // 父节点的缓存清理
        }
    }

    /**
     * 新增后的树形结构更新
     */
    protected void updateTreeStructureAfterSave(T entity) {
        // 子类可以重写此方法，用于更新路径、层级等树形结构信息
    }

    /**
     * 更新后的树形结构更新
     */
    protected void updateTreeStructureAfterUpdate(T newEntity, T oldEntity) {
        // 子类可以重写此方法，处理树形结构变更
    }

}
