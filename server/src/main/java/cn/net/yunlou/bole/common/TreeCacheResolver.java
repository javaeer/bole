package cn.net.yunlou.bole.common;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;

/** 树形服务缓存解析器 */
public class TreeCacheResolver implements CacheResolver {

    private final CacheManager cacheManager;

    public TreeCacheResolver(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        // 获取目标对象
        Object target = context.getTarget();

        // 如果是BaseTreeService，动态获取缓存名称
        if (target instanceof BaseTreeService) {
            BaseTreeService<?, ?, ?, ?, ?> service = (BaseTreeService<?, ?, ?, ?, ?>) target;
            String cacheName = service.getCacheName();

            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                return Collections.singleton(cache);
            }
        }

        // 默认使用第一个缓存
        return cacheManager.getCacheNames().stream()
                .limit(1)
                .map(cacheManager::getCache)
                .collect(Collectors.toList());
    }
}
