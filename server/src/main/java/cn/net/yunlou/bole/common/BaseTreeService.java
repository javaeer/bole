package cn.net.yunlou.bole.common;

import cn.net.yunlou.bole.common.annotation.TreeServiceConfig;
import cn.net.yunlou.bole.common.utils.RedissonLockUtils;
import cn.net.yunlou.bole.common.utils.TreeBuildUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import io.jsonwebtoken.lang.Collections;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RLock;
import org.springframework.aop.support.AopUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 基于 QueryWrapper 的树形服务基类 @Author javaeer(javaeer @ aliyun.com) @Date 2019/12/12 09:20 @Version 2.0
 */
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public abstract class BaseTreeService<
                M extends BaseMapper<T>,
                T extends BaseTreeEntity<T>,
                C extends BaseTreeCreate,
                V extends BaseTreeView<V>,
                E extends BaseTreeEdit,
                Q extends BaseTreeQuery,
                S extends BaseStructMapper<T, C, V, E, Q>>
        extends BaseService<M, T, C, V, E, Q, S> implements IBaseTreeService<T, C, V, E, Q> {

    // ============ 依赖注入 ============
    private final RedissonLockUtils redissonLockUtils;

    // ============ 缓存配置方法 ============

    /** 获取缓存名称 */
    public String getCacheName() {
        Class<?> targetClass = AopUtils.getTargetClass(this);
        TreeServiceConfig config = targetClass.getAnnotation(TreeServiceConfig.class);
        return config != null ? config.cacheName() : "defaultCache";
    }

    /** 获取缓存过期时间 */
    public long getCacheExpireTime() {
        Class<?> targetClass = AopUtils.getTargetClass(this);
        TreeServiceConfig config = targetClass.getAnnotation(TreeServiceConfig.class);
        return config != null ? config.expireTime() : 7200;
    }

    /** 获取缓存键前缀 */
    public String getKeyPrefix() {
        Class<?> targetClass = AopUtils.getTargetClass(this);
        TreeServiceConfig config = targetClass.getAnnotation(TreeServiceConfig.class);
        return config != null ? config.keyPrefix() : "default";
    }

    /** 是否启用缓存 */
    public boolean isCacheEnabled() {
        Class<?> targetClass = AopUtils.getTargetClass(this);
        TreeServiceConfig config = targetClass.getAnnotation(TreeServiceConfig.class);
        return config != null && config.enableCache();
    }

    /** 获取最大深度 */
    public int getMaxDepth() {
        Class<?> targetClass = AopUtils.getTargetClass(this);
        TreeServiceConfig config = targetClass.getAnnotation(TreeServiceConfig.class);
        return config != null ? Math.min(config.maxDepth(), 20) : 20; // 限制最大20层
    }

    /** 获取批量操作大小 */
    public int getBatchSize() {
        Class<?> targetClass = AopUtils.getTargetClass(this);
        TreeServiceConfig config = targetClass.getAnnotation(TreeServiceConfig.class);
        return config != null ? config.batchSize() : 100;
    }

    /** 获取分页大小 */
    public int getPageSize() {
        Class<?> targetClass = AopUtils.getTargetClass(this);
        TreeServiceConfig config = targetClass.getAnnotation(TreeServiceConfig.class);
        return config != null ? Math.min(config.pageSize(), 1000) : 200; // 限制最大1000
    }

    /** 获取锁名称 */
    public String getLockName() {
        Class<?> targetClass = AopUtils.getTargetClass(this);
        TreeServiceConfig config = targetClass.getAnnotation(TreeServiceConfig.class);
        return config != null ? config.lockName() : "treeLock";
    }

    // ============ 锁相关方法 ============

    /** 获取分布式锁 */
    protected RLock getTreeLock(Serializable nodeId) {
        String lockKey = getLockName() + ":" + getKeyPrefix() + ":" + nodeId;
        return redissonLockUtils.getLock(lockKey);
    }

    /** 获取子树锁（用于批量操作） */
    protected RLock getSubTreeLock(Serializable nodeId) {
        String lockKey = getLockName() + ":" + getKeyPrefix() + ":subtree:" + nodeId;
        return redissonLockUtils.getLock(lockKey);
    }

    /** 获取树操作锁（用于全树操作） */
    protected RLock getTreeOperationLock() {
        String lockKey = getLockName() + ":" + getKeyPrefix() + ":operation";
        return redissonLockUtils.getLock(lockKey);
    }

    /** 构建缓存key */
    public String buildCacheKey(String key) {
        return getKeyPrefix() + ":" + key;
    }

    // ============ 新增方法优化 ============

    /** 新增实体 - 优化版 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheResolver = "treeCacheResolver", allEntries = true)
    public boolean save(T entity) {
        // 验证父节点是否存在
        validateParentNodeExists(entity);

        RLock lock = getTreeLock(entity.getParentId());
        try {
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {

                boolean result = super.save(entity);
                if (result) {
                    // 设置路径和层级
                    calculateAndSetPathAndLevel(entity);
                    super.updateById(entity);

                    log.debug("保存树节点成功，节点ID: {}, 路径: {}", entity.getId(), entity.getPath());
                }
                return result;
            } else {
                log.warn("获取树操作锁超时，父节点ID: {}", entity.getParentId());
                throw new RuntimeException("获取树操作锁超时");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("树操作被中断", e);
            throw new RuntimeException("树操作被中断", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /** 批量新增 - 优化版 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheResolver = "treeCacheResolver", allEntries = true)
    public boolean saveBatch(Collection<T> entityList) {
        if (Collections.isEmpty(entityList)) {
            return true;
        }

        // 验证所有父节点存在
        entityList.forEach(this::validateParentNodeExists);

        // 按父节点分组，同一父节点的批量操作
        Map<Long, List<T>> groupByParent =
                entityList.stream().collect(Collectors.groupingBy(T::getParentId));

        boolean allSuccess = true;
        for (Map.Entry<Long, List<T>> entry : groupByParent.entrySet()) {
            RLock lock = getTreeLock(entry.getKey());
            try {
                if (lock.tryLock(5, 30, TimeUnit.SECONDS)) {
                    // 设置路径和层级
                    entry.getValue()
                            .forEach(
                                    entity -> {
                                        calculateAndSetPathAndLevel(entity);
                                    });

                    boolean result = super.saveBatch(entry.getValue());
                    if (!result) {
                        allSuccess = false;
                    }
                } else {
                    log.warn("获取批量操作锁超时，父节点ID: {}", entry.getKey());
                    throw new RuntimeException("获取批量操作锁超时");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("批量操作被中断", e);
                throw new RuntimeException("批量操作被中断", e);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
        return allSuccess;
    }

    /** 根据ID更新实体 - 优化版 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(
            evict = {
                @CacheEvict(
                        cacheResolver = "treeCacheResolver",
                        key = "#root.target.buildCacheKey('with_children:' + #entity.id)"),
                @CacheEvict(
                        cacheResolver = "treeCacheResolver",
                        key = "#root.target.buildCacheKey('with_tree:' + #entity.id)"),
                @CacheEvict(
                        cacheResolver = "treeCacheResolver",
                        key = "#root.target.buildCacheKey('children:' + #entity.id)"),
                @CacheEvict(
                        cacheResolver = "treeCacheResolver",
                        key = "#root.target.buildCacheKey('all_children:' + #entity.id)"),
                @CacheEvict(
                        cacheResolver = "treeCacheResolver",
                        key = "#root.target.buildCacheKey('leaves:' + #entity.id)"),
                @CacheEvict(
                        cacheResolver = "treeCacheResolver",
                        key = "#root.target.buildCacheKey('parent:' + #entity.id)"),
                @CacheEvict(
                        cacheResolver = "treeCacheResolver",
                        key = "#root.target.buildCacheKey('grandpa:' + #entity.id)"),
                @CacheEvict(
                        cacheResolver = "treeCacheResolver",
                        key = "#root.target.buildCacheKey('root:' + #entity.id)"),
                @CacheEvict(
                        cacheResolver = "treeCacheResolver",
                        key = "#root.target.buildCacheKey('children:' + #entity.parentId)"),
                @CacheEvict(
                        cacheResolver = "treeCacheResolver",
                        key = "#root.target.buildCacheKey('all_children:' + #entity.parentId)")
            })
    public boolean updateById(T entity) {
        T oldEntity = getById(entity.getId());
        if (oldEntity == null) {
            return false;
        }

        // 检查父节点是否发生变化
        boolean parentChanged = !Objects.equals(oldEntity.getParentId(), entity.getParentId());

        if (parentChanged) {
            // 验证新父节点是否存在
            validateParentNodeExists(entity);
        }

        boolean result = super.updateById(entity);
        if (result) {
            if (parentChanged) {
                // 更新路径和层级
                calculateAndSetPathAndLevel(entity);
                super.updateById(entity);

                // 更新子节点路径
                updateDescendantsPath(entity, oldEntity.getPath());

                // 清理新旧父节点缓存
                clearParentCache(oldEntity.getParentId());
                clearParentCache(entity.getParentId());
            }
        }
        return result;
    }

    /** 根据ID删除 - 优化版 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(
            evict = {
                @CacheEvict(
                        cacheResolver = "treeCacheResolver",
                        key = "#root.target.buildCacheKey('with_children:' + #id)"),
                @CacheEvict(
                        cacheResolver = "treeCacheResolver",
                        key = "#root.target.buildCacheKey('with_tree:' + #id)"),
                @CacheEvict(
                        cacheResolver = "treeCacheResolver",
                        key = "#root.target.buildCacheKey('children:' + #id)"),
                @CacheEvict(
                        cacheResolver = "treeCacheResolver",
                        key = "#root.target.buildCacheKey('all_children:' + #id)"),
                @CacheEvict(
                        cacheResolver = "treeCacheResolver",
                        key = "#root.target.buildCacheKey('leaves:' + #id)"),
                @CacheEvict(
                        cacheResolver = "treeCacheResolver",
                        key = "#root.target.buildCacheKey('parent:' + #id)"),
                @CacheEvict(
                        cacheResolver = "treeCacheResolver",
                        key = "#root.target.buildCacheKey('grandpa:' + #id)"),
                @CacheEvict(
                        cacheResolver = "treeCacheResolver",
                        key = "#root.target.buildCacheKey('root:' + #id)")
            })
    public boolean removeById(Serializable id) {
        T entity = getById(id);
        if (entity == null) {
            log.warn("删除节点不存在，节点ID: {}", id);
            return false;
        }

        // 检查是否有子节点
        if (hasChildNodes(id)) {
            log.warn("节点存在子节点，不能直接删除，节点ID: {}", id);
            throw new RuntimeException("节点存在子节点，请先删除子节点");
        }

        // 获取父节点锁
        RLock lock = getTreeLock(entity.getParentId());
        try {
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {

                boolean result = super.removeById(id);
                if (result) {
                    // 清理父节点缓存
                    clearParentCache(entity.getParentId());
                    log.debug("删除树节点成功，节点ID: {}", id);
                }
                return result;
            } else {
                log.warn("获取删除锁超时，节点ID: {}", id);
                throw new RuntimeException("获取删除锁超时");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("删除操作被中断", e);
            throw new RuntimeException("删除操作被中断", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /** 批量删除节点 - 优化版 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheResolver = "treeCacheResolver", allEntries = true)
    public boolean removeByIds(Collection<?> list) {
        if (Collections.isEmpty(list)) {
            return true;
        }

        List<Long> ids = list.stream().map(id -> (Long) id).collect(Collectors.toList());

        // 检查是否有节点存在子节点
        List<Long> nodesWithChildren =
                ids.stream().filter(this::hasChildNodes).collect(Collectors.toList());

        if (!nodesWithChildren.isEmpty()) {
            log.warn("以下节点存在子节点，不能直接删除: {}", nodesWithChildren);
            throw new RuntimeException("存在子节点的节点不能删除: " + nodesWithChildren);
        }

        // 获取所有节点的父节点ID，用于清理缓存
        List<T> nodes = super.listByIds(ids);
        Set<Long> parentIds =
                nodes.stream()
                        .map(T::getParentId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

        RLock lock = getTreeOperationLock();
        try {
            if (lock.tryLock(5, 30, TimeUnit.SECONDS)) {
                boolean result = super.removeByIds(ids);
                if (result) {
                    // 清理父节点缓存
                    parentIds.forEach(this::clearParentCache);
                    log.info("批量删除树节点成功，删除节点数: {}", ids.size());
                }
                return result;
            } else {
                log.warn("获取批量删除锁超时");
                throw new RuntimeException("获取批量删除锁超时");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("批量删除操作被中断", e);
            throw new RuntimeException("批量删除操作被中断", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /** 根据路径批量删除节点及其子节点 - 优化版 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeNodesByPath(String path) {
        if (ObjectUtils.isEmpty(path)) {
            return false;
        }

        // 获取节点，检查是否存在
        T entity = findByPath(path);
        if (entity == null) {
            return false;
        }

        // 获取子树锁
        RLock lock = getSubTreeLock(entity.getId());
        try {
            if (lock.tryLock(5, 30, TimeUnit.SECONDS)) {

                @SuppressWarnings({"unchecked", "deprecation"})
                T instance = (T) entity.getClass().newInstance();
                QueryWrapper<T> queryWrapper = getBaseQueryWrapper(instance);
                queryWrapper
                        .eq("path", path) // 当前节点
                        .or()
                        .likeRight("path", path + "/"); // 所有子节点

                boolean result = remove(queryWrapper);
                if (result) {
                    // 清理父节点缓存
                    clearParentCache(entity.getParentId());
                    log.debug("通过路径删除节点成功，路径: {}", path);
                }
                return result;
            } else {
                log.warn("获取子树删除锁超时，路径: {}", path);
                throw new RuntimeException("获取子树删除锁超时");
            }
        } catch (InterruptedException | InstantiationException | IllegalAccessException e) {
            Thread.currentThread().interrupt();
            log.error("删除操作被中断", e);
            throw new RuntimeException("删除操作被中断", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // ============ 查询方法优化 ============

    /** 获取子节点树（包含子节点） */
    @Override
    @Cacheable(
            cacheResolver = "treeCacheResolver",
            key = "#root.target.buildCacheKey('with_children:' + #id)",
            unless = "!#root.target.isCacheEnabled()")
    public T getNodeWithChildren(Serializable id) {
        Assert.isTrue(ObjectUtils.isNotEmpty(id), "主键不能为空");
        T entity = getById(id);
        if (ObjectUtils.isEmpty(entity)) {
            return null;
        }
        List<T> children = listDirectChildren(entity.getId());
        if (!Collections.isEmpty(children)) {
            entity.setChildren(children);
        }
        return entity;
    }

    /** 获取完整子树 */
    @Override
    @Cacheable(
            cacheResolver = "treeCacheResolver",
            key = "#root.target.buildCacheKey('with_tree:' + #id)",
            unless = "!#root.target.isCacheEnabled()")
    public T getNodeWithSubTree(Serializable id) {
        Assert.isTrue(ObjectUtils.isNotEmpty(id), "主键不能为空");
        T entity = getById(id);
        if (ObjectUtils.isEmpty(entity)) {
            return null;
        }
        List<T> children = listDescendants(id);
        if (!Collections.isEmpty(children)) {
            entity.setChildren(TreeBuildUtils.buildTree(children, entity.getId()));
        }
        return entity;
    }

    /** 获取子节点列表 */
    @Override
    @Cacheable(
            cacheResolver = "treeCacheResolver",
            key = "#root.target.buildCacheKey('children:' + #id)",
            unless = "!#root.target.isCacheEnabled()")
    public List<T> listDirectChildren(Serializable id) {
        Assert.isTrue(ObjectUtils.isNotEmpty(id), "主键不能为空");
        QueryWrapper<T> queryWrapper = getBaseQueryWrapper(null);
        queryWrapper.eq("parent_id", id);
        return super.list(queryWrapper);
    }

    @Override
    public List<V> listDirectChildrenView(Serializable id) {
        return structMapper.toViews(listDirectChildren(id));
    }

    @Override
    public List<T> listDirectChildren(T entity) {
        QueryWrapper<T> queryWrapper = getBaseQueryWrapper(entity);
        queryWrapper.eq("parent_id", entity.getId());
        return list(queryWrapper);
    }

    /** 获取完整树 */
    @Override
    @Cacheable(
            cacheResolver = "treeCacheResolver",
            key = "#root.target.buildCacheKey('whole_tree:')",
            unless = "!#root.target.isCacheEnabled()")
    public List<T> listWholeTree() {
        QueryWrapper<T> queryWrapper = getBaseQueryWrapper(null);
        List<T> allNodes = super.list(queryWrapper);
        return TreeBuildUtils.buildTree(allNodes);
    }

    /** 获取所有子孙节点（扁平列表） */
    @Override
    @Cacheable(
            cacheResolver = "treeCacheResolver",
            key = "#root.target.buildCacheKey('all_children:' + #id)",
            unless = "!#root.target.isCacheEnabled()")
    public List<T> listDescendants(Serializable id) {
        Assert.isTrue(ObjectUtils.isNotEmpty(id), "主键不能为空");

        T parent = getById(id);
        if (ObjectUtils.isEmpty(parent)) {
            return Lists.newArrayList();
        }

        QueryWrapper<T> queryWrapper = getBaseQueryWrapper(null);
        queryWrapper
                .likeRight("path", parent.getPath() + "/")
                .le("level", parent.getLevel() + getMaxDepth());
        return super.list(queryWrapper);
    }

    @Override
    public List<T> listDescendants(T entity) {
        List<T> result = Lists.newArrayList();
        List<T> children = listDirectChildren(entity);
        if (!Collections.isEmpty(children)) {
            for (T child : children) {
                List<T> grandchildren = listDescendants(child.getId());
                if (!Collections.isEmpty(grandchildren)) {
                    child.setChildren(TreeBuildUtils.buildTree(grandchildren, entity.getId()));
                }
                result.add(child);
            }
        }
        return result;
    }

    /** 获取叶子节点 */
    @Override
    @Cacheable(
            cacheResolver = "treeCacheResolver",
            key = "#root.target.buildCacheKey('leaves:' + #id)",
            unless = "!#root.target.isCacheEnabled()")
    public List<T> listLeafNodes(Serializable id) {
        Assert.isTrue(ObjectUtils.isNotEmpty(id), "主键不能为空");

        // 优化：直接查询没有子节点的节点
        T parent = getById(id);
        if (ObjectUtils.isEmpty(parent)) {
            return Lists.newArrayList();
        }

        QueryWrapper<T> queryWrapper = getBaseQueryWrapper(null);
        queryWrapper
                .likeRight("path", parent.getPath() + "/")
                .apply(
                        "NOT EXISTS (SELECT 1 FROM {0} t2 WHERE t2.parent_id = {0}.id)",
                        getTableName());

        return list(queryWrapper);
    }

    /** 分页查询子节点 */
    @Cacheable(
            cacheResolver = "treeCacheResolver",
            key =
                    "#root.target.buildCacheKey('children_page:' + #id + ':' + #page.current + ':' + #page.size)",
            unless = "!#root.target.isCacheEnabled()")
    public IPage<T> listDirectChildrenByPage(Serializable id, Page<T> page) {
        Assert.isTrue(ObjectUtils.isNotEmpty(id), "主键不能为空");

        QueryWrapper<T> queryWrapper = getBaseQueryWrapper(null);
        queryWrapper.eq("parent_id", id);

        return super.page(page, queryWrapper);
    }

    /** 懒加载子节点 */
    @Override
    @Cacheable(
            cacheResolver = "treeCacheResolver",
            key = "#root.target.buildCacheKey('lazy_children:' + #id + ':' + #page + ':' + #size)",
            unless = "!#root.target.isCacheEnabled()")
    public List<T> listDirectChildrenByPage(Serializable id, int page, int size) {
        Assert.isTrue(ObjectUtils.isNotEmpty(id), "主键不能为空");
        Assert.isTrue(page >= 0, "页码必须大于等于0");
        Assert.isTrue(size > 0 && size <= 1000, "每页大小必须在1到1000之间");

        QueryWrapper<T> queryWrapper = getBaseQueryWrapper(null);
        queryWrapper.eq("parent_id", id).last("LIMIT " + (page * size) + "," + size);

        return list(queryWrapper);
    }

    /** 分页获取子树 */
    @Override
    @Cacheable(
            cacheResolver = "treeCacheResolver",
            key =
                    "#root.target.buildCacheKey('subtree_page:' + #id + ':' + #depth + ':' + #page.current + ':' + #page.size)",
            unless = "!#root.target.isCacheEnabled()")
    public IPage<T> listDescendantsByPage(Serializable id, int depth, Page<T> page) {
        Assert.isTrue(ObjectUtils.isNotEmpty(id), "主键不能为空");
        Assert.isTrue(depth > 0 && depth <= getMaxDepth(), "深度必须在1到" + getMaxDepth() + "之间");

        T parent = getById(id);
        if (parent == null) {
            return new Page<>(page.getCurrent(), page.getSize(), 0);
        }

        QueryWrapper<T> queryWrapper = getBaseQueryWrapper(null);
        queryWrapper
                .likeRight("path", parent.getPath() + "/")
                .le("level", parent.getLevel() + depth);

        return page(page, queryWrapper);
    }

    /** 根据父节点ID列表批量查询子节点 */
    @Override
    public Map<Long, List<T>> listDirectChildrenByParentIds(List<Long> parentIds) {
        if (Collections.isEmpty(parentIds)) {
            return new HashMap<>();
        }

        QueryWrapper<T> queryWrapper = getBaseQueryWrapper(null);
        queryWrapper.in("parent_id", parentIds);

        List<T> children = list(queryWrapper);

        return children.stream().collect(Collectors.groupingBy(T::getParentId));
    }

    /** 根据层级查询节点 */
    @Override
    public List<T> listNodesByLevel(int level) {
        QueryWrapper<T> queryWrapper = getBaseQueryWrapper(null);
        queryWrapper.eq("level", level);

        return list(queryWrapper);
    }

    /** 获取根节点的直接子节点 */
    @Override
    public List<T> listRootDirectChildren() {
        QueryWrapper<T> queryWrapper = getBaseQueryWrapper(null);
        queryWrapper.eq("parent_id", BaseTreeEntity.ROOT_ID);
        return list(queryWrapper);
    }

    @Override
    public List<V> listRootDirectChildrenView() {
        return structMapper.toViews(listRootDirectChildren());
    }

    /** 检查是否有子节点 */
    @Override
    public boolean hasChildNodes(Serializable id) {
        Assert.isTrue(ObjectUtils.isNotEmpty(id), "主键不能为空");

        QueryWrapper<T> queryWrapper = getBaseQueryWrapper(null);
        queryWrapper.eq("parent_id", id).select("id").last("LIMIT 1");

        return count(queryWrapper) > 0;
    }

    /** 统计子节点数量 */
    @Override
    public long countDirectChildren(Serializable id) {
        Assert.isTrue(ObjectUtils.isNotEmpty(id), "主键不能为空");

        QueryWrapper<T> queryWrapper = getBaseQueryWrapper(null);
        queryWrapper.eq("parent_id", id);

        return count(queryWrapper);
    }

    /** 根据路径查询节点 */
    @Cacheable(
            cacheResolver = "treeCacheResolver",
            key = "#root.target.buildCacheKey('node_by_path:' + #path)",
            unless = "!#root.target.isCacheEnabled()")
    public T findByPath(String path) {
        if (ObjectUtils.isEmpty(path)) {
            return null;
        }

        QueryWrapper<T> queryWrapper = getBaseQueryWrapper(null);
        queryWrapper.eq("path", path);

        return getOne(queryWrapper);
    }

    /** 获取父节点 */
    @Override
    @Cacheable(
            cacheResolver = "treeCacheResolver",
            key = "#root.target.buildCacheKey('parent:' + #id)",
            unless = "!#root.target.isCacheEnabled()")
    public T findParentNode(Serializable id) {
        T entity = getById(id);
        if (ObjectUtils.isEmpty(entity)
                || ObjectUtils.isEmpty(entity.getParentId())
                || Objects.equals(entity.getParentId(), BaseTreeEntity.ROOT_ID)) {
            return null;
        }
        return getById(entity.getParentId());
    }

    /** 获取祖父节点 */
    @Override
    @Cacheable(
            cacheResolver = "treeCacheResolver",
            key = "#root.target.buildCacheKey('grandpa:' + #id)",
            unless = "!#root.target.isCacheEnabled()")
    public T findGrandparentNode(Serializable id) {
        T parent = findParentNode(id);
        if (ObjectUtils.isEmpty(parent)) {
            return null;
        }
        return findParentNode(parent.getId());
    }

    /** 获取根节点 */
    @Override
    @Cacheable(
            cacheResolver = "treeCacheResolver",
            key = "#root.target.buildCacheKey('root:' + #id)",
            unless = "!#root.target.isCacheEnabled()")
    public T findRootNode(Serializable id) {
        T entity = getById(id);
        if (ObjectUtils.isEmpty(entity)) {
            return null;
        }

        // 递归向上查找根节点
        while (ObjectUtils.isNotEmpty(entity.getParentId())
                && !Objects.equals(entity.getParentId(), BaseTreeEntity.ROOT_ID)) {
            T parent = getById(entity.getParentId());
            if (parent == null) {
                break;
            }
            entity = parent;
        }
        return entity;
    }

    /** 批量移动节点到新的父节点 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheResolver = "treeCacheResolver", allEntries = true)
    public boolean moveNodesToParent(List<Long> nodeIds, Long targetParentId) {
        if (Collections.isEmpty(nodeIds)) {
            return true;
        }

        // 验证目标父节点
        T targetParent = getById(targetParentId);
        if (targetParent == null && !BaseTreeEntity.ROOT_ID.equals(targetParentId)) {
            throw new IllegalArgumentException("目标父节点不存在");
        }

        // 检查是否会形成循环引用
        checkCircularReference(nodeIds, targetParentId, targetParent);

        RLock lock = getTreeOperationLock();
        try {
            if (lock.tryLock(10, 120, TimeUnit.SECONDS)) {
                // 查询所有要移动的节点
                List<T> nodesToMove = super.listByIds(nodeIds);
                if (Collections.isEmpty(nodesToMove)) {
                    return false;
                }

                // 批量更新
                List<T> updatedNodes =
                        nodesToMove.stream()
                                .map(
                                        node -> {
                                            T updatedNode = structMapper.copy(node);
                                            updatedNode.setParentId(targetParentId);
                                            // 更新路径
                                            String newPath =
                                                    targetParent != null
                                                            ? targetParent.getPath()
                                                                    + "/"
                                                                    + node.getId()
                                                            : "/" + node.getId();
                                            updatedNode.setPath(newPath);
                                            updatedNode.setLevel(
                                                    targetParent != null
                                                            ? targetParent.getLevel() + 1
                                                            : 1);
                                            return updatedNode;
                                        })
                                .collect(Collectors.toList());

                boolean result = super.updateBatchById(updatedNodes);

                // 更新子节点的路径
                updatedNodes.forEach(
                        node -> {
                            String oldPath =
                                    nodesToMove.stream()
                                            .filter(n -> n.getId().equals(node.getId()))
                                            .findFirst()
                                            .map(T::getPath)
                                            .orElse("");
                            if (!oldPath.isEmpty()) {
                                updateDescendantsPath(node, oldPath);
                            }
                        });

                log.debug("批量移动节点成功，移动节点数: {}", nodeIds.size());
                return result;
            } else {
                throw new RuntimeException("获取树移动操作锁超时");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("移动操作被中断", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /** 获取表名（子类实现） */
    protected abstract String getTableName();

    @Override
    public QueryWrapper<T> getBaseQueryWrapper(T entity) {
        QueryWrapper<T> queryWrapper = super.getBaseQueryWrapper(entity);
        queryWrapper.orderByAsc("level", "sort", "id");
        return queryWrapper;
    }

    /** 更新子节点路径 */
    private void updateDescendantsPath(T parentNode, String oldParentPath) {
        if (ObjectUtils.isEmpty(oldParentPath)
                || Objects.equals(oldParentPath, parentNode.getPath())) {
            return;
        }

        // 使用自定义SQL更新子节点路径
        if (getBaseMapper() instanceof IBaseTreeMapper) {
            @SuppressWarnings("unchecked")
            IBaseTreeMapper<T> treeMapper = (IBaseTreeMapper<T>) getBaseMapper();
            int updated =
                    treeMapper.updateChildrenPath(
                            getTableName(), oldParentPath, parentNode.getPath());
            log.debug("更新子节点路径完成，父节点ID: {}, 更新节点数: {}", parentNode.getId(), updated);
        } else {
            log.warn("Mapper未实现BaseTreeMapper接口，无法更新子节点路径");
        }
    }

    /** 清理节点缓存 */
    private void clearNodeCache(Serializable id) {
        log.debug("清理节点缓存: {}", id);
    }

    /** 清理父节点缓存 */
    private void clearParentCache(Serializable parentId) {
        if (parentId != null && !Objects.equals(parentId, BaseTreeEntity.ROOT_ID)) {
            log.debug("清理父节点缓存: {}", parentId);
        }
    }

    /** 验证父节点是否存在 */
    private void validateParentNodeExists(T entity) {
        if (!Objects.equals(entity.getParentId(), BaseTreeEntity.ROOT_ID)) {
            T parent = getById(entity.getParentId());
            if (parent == null) {
                throw new IllegalArgumentException("父节点不存在，ID: " + entity.getParentId());
            }
        }
    }

    /** 设置路径和层级 */
    private void calculateAndSetPathAndLevel(T entity) {
        if (Objects.equals(entity.getParentId(), BaseTreeEntity.ROOT_ID)) {
            entity.setPath("/" + entity.getId());
            entity.setLevel(1);
        } else {
            T parent = getById(entity.getParentId());
            if (parent != null) {
                entity.setPath(parent.getPath() + "/" + entity.getId());
                entity.setLevel(parent.getLevel() + 1);
            }
        }
    }

    /** 检查循环引用 */
    private void checkCircularReference(List<Long> nodeIds, Long targetParentId, T targetParent) {
        // 检查目标父节点是否是待移动节点的子节点
        if (targetParent != null) {
            for (Long nodeId : nodeIds) {
                if (isNodeDescendantOf(targetParentId, nodeId)) {
                    throw new IllegalArgumentException("移动节点到其子节点会形成循环引用");
                }
            }
        }

        // 检查是否移动到自身
        if (nodeIds.contains(targetParentId)) {
            throw new IllegalArgumentException("不能将节点移动到自身");
        }
    }

    /** 判断节点是否是另一个节点的后代 */
    @Override
    public boolean isNodeDescendantOf(Long parentId, Long childId) {
        T child = getById(childId);
        if (child == null) {
            return false;
        }

        T parent = getById(parentId);
        if (parent == null) {
            return false;
        }

        return child.getPath().startsWith(parent.getPath() + "/");
    }
}
