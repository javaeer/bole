package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.constant.StorageType;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/** 存储策略注册中心 负责管理和提供各种存储策略 */
@Component
@Slf4j
public class StorageStrategyRegistry {

    /** 存储策略映射表 key: 存储类型枚举值 value: 对应的存储策略实现 */
    private final Map<String, IStorage> strategyMap = new ConcurrentHashMap<>();

    /**
     * 构造函数，自动注入所有IStorage实现
     *
     * @param strategies Spring会自动收集所有IStorage实现
     */
    public StorageStrategyRegistry(List<IStorage> strategies) {
        if (CollectionUtils.isEmpty(strategies)) {
            log.warn("未找到任何存储策略实现");
            return;
        }

        // 将策略按类型注册到映射表
        for (IStorage strategy : strategies) {
            StorageType type = strategy.getType();
            strategyMap.put(type.getValue(), strategy);
            log.info("注册存储策略: {} -> {}", type.getLabel(), strategy.getClass().getSimpleName());
        }

        log.info("共注册 {} 个存储策略", strategyMap.size());
    }

    /**
     * 根据存储类型获取策略
     *
     * @param storageType 存储类型字符串（如"minio", "local"）
     * @return 存储策略实现
     */
    public IStorage getStrategy(String storageType) {
        if (StringUtils.isBlank(storageType)) {
            throw new IllegalArgumentException("存储类型不能为空");
        }

        IStorage strategy = strategyMap.get(storageType.toLowerCase());
        if (strategy == null) {
            String supportedTypes = String.join(", ", strategyMap.keySet());
            throw new UnsupportedOperationException(
                    String.format("不支持的存储类型: %s。支持的存储类型: %s", storageType, supportedTypes));
        }

        return strategy;
    }

    /**
     * 根据存储类型枚举获取策略
     *
     * @param type 存储类型枚举
     * @return 存储策略实现
     */
    public IStorage getStrategy(StorageType type) {
        return getStrategy(type.getValue());
    }

    /**
     * 获取所有可用的存储类型
     *
     * @return 存储类型列表
     */
    public List<StorageType> getAvailableTypes() {
        return strategyMap.values().stream().map(IStorage::getType).collect(Collectors.toList());
    }

    /**
     * 检查是否支持某种存储类型
     *
     * @param storageType 存储类型
     * @return 是否支持
     */
    public boolean supports(String storageType) {
        return strategyMap.containsKey(storageType.toLowerCase());
    }
}
