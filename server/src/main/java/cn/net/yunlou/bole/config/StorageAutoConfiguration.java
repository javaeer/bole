package cn.net.yunlou.bole.config;

import cn.net.yunlou.bole.handler.IStorage;
import cn.net.yunlou.bole.handler.StorageStrategyRegistry;
import cn.net.yunlou.bole.service.FileService;
import cn.net.yunlou.bole.service.StorageService;
import cn.net.yunlou.bole.service.impl.FileServiceImpl;
import cn.net.yunlou.bole.service.impl.StorageServiceImpl;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 存储自动配置类 负责自动配置存储相关组件 */
@Configuration
@EnableConfigurationProperties({
    StorageProperties.class,
    StorageMinioProperties.class,
    StorageLocalProperties.class
})
@Slf4j
public class StorageAutoConfiguration {

    /**
     * 创建存储策略注册中心
     *
     * @param strategies 所有IStorage实现
     * @return StorageStrategyRegistry
     */
    @Bean
    @ConditionalOnMissingBean
    public StorageStrategyRegistry storageStrategyRegistry(ObjectProvider<IStorage> strategies) {

        List<IStorage> strategyList = strategies.stream().collect(Collectors.toList());

        return new StorageStrategyRegistry(strategyList);
    }

    /**
     * 创建存储服务
     *
     * @param registry 策略注册中心
     * @param properties 存储配置
     * @return StorageService
     */
    @Bean
    @ConditionalOnMissingBean
    public StorageService storageService(
            StorageStrategyRegistry registry, StorageProperties properties) {

        return new StorageServiceImpl(registry, properties);
    }

    /**
     * 创建文件服务
     *
     * @param storageService 存储服务
     * @return FileService
     */
    @Bean
    @ConditionalOnMissingBean
    public FileService fileService(StorageService storageService) {
        return new FileServiceImpl(storageService);
    }
}
