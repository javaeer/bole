package cn.net.yunlou.bole.config;

import cn.net.yunlou.bole.common.TreeCacheResolver;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.*;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

/**
 * Redis 统一配置类 整合缓存配置和Redis模板配置
 *
 * <p>spring: data: redis: host: ${REDIS_HOST:localhost} port: ${REDIS_PORT:6379} password:
 * ${REDIS_PASSWORD:} timeout: 3000ms lettuce: pool: max-active: 8 max-idle: 8 min-idle: 0 max-wait:
 * -1ms cache: type: redis redis: time-to-live: 1h cache-null-values: false use-key-prefix: true
 * key-prefix: "cache:"
 *
 * @author MR. WANG
 */
@Configuration
@EnableCaching(proxyTargetClass = true)
@EnableConfigurationProperties({CacheProperties.class, RedisProperties.class})
@AutoConfigureBefore(RedisAutoConfiguration.class)
@RequiredArgsConstructor
public class RedisConfig {

    private static final String REDIS_SCHEMA = "redis://";
    private static final String REDISS_SCHEMA = "rediss://"; // SSL

    /** 自定义缓存配置（可按缓存名称配置不同的TTL） */
    @Bean
    @Primary
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory, CacheProperties cacheProperties) {
        // 默认缓存配置
        RedisCacheConfiguration defaultConfig = createDefaultCacheConfig(cacheProperties);

        // 自定义缓存配置（按缓存名称设置不同的过期时间）
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 示例：为不同的缓存设置不同的TTL
        /*cacheConfigurations.put("userCache", defaultConfig.entryTtl(Duration.ofMinutes(30)));
                cacheConfigurations.put("productCache", defaultConfig.entryTtl(Duration.ofHours(2)));
                cacheConfigurations.put("configCache", defaultConfig.entryTtl(Duration.ofDays(1)));
                cacheConfigurations.put("sessionCache", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        */
        return RedisCacheManager.builder(
                        RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory))
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware() // 支持事务
                .build();
    }

    /** 创建默认缓存配置 */
    private RedisCacheConfiguration createDefaultCacheConfig(CacheProperties cacheProperties) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        // 序列化配置
        config =
                config.serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                RedisSerializer.string()));
        config =
                config.serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                redisSerializer()));

        // 应用配置属性
        CacheProperties.Redis redisProps = cacheProperties.getRedis();
        if (redisProps.getTimeToLive() != null) {
            config = config.entryTtl(redisProps.getTimeToLive());
        } else {
            // 默认TTL：1小时
            config = config.entryTtl(Duration.ofHours(1));
        }

        if (!redisProps.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }

        if (!redisProps.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        } else {
            // 自定义前缀格式
            config = config.computePrefixWith(cacheName -> "cache:" + cacheName + ":");
        }

        return config;
    }

    /** 树形服务缓存解析器 */
    @Bean
    public CacheResolver treeCacheResolver(CacheManager cacheManager) {
        return new TreeCacheResolver(cacheManager);
    }

    /** 创建专用于Redis的序列化器 使用独立的ObjectMapper，不注册为通用Bean，避免影响Web接口 */
    @Bean
    public RedisSerializer<Object> redisSerializer() {
        ObjectMapper redisObjectMapper = new ObjectMapper();

        // 反序列化时候遇到不匹配的属性并不抛出异常
        redisObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 序列化时候遇到空对象不抛出异常
        redisObjectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 反序列化的时候如果是无效子类型,不抛出异常
        redisObjectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        // 不使用默认的dateTime进行序列化,
        redisObjectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        // 使用JSR310提供的序列化类,里面包含了大量的JDK8时间序列化类
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // 配置LocalDateTime序列化和反序列化格式
        javaTimeModule.addSerializer(
                LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(
                LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        redisObjectMapper.registerModule(javaTimeModule);

        // 启用反序列化所需的类型信息,在属性中添加@class【此配置不会影响Spring MVC返回给前端的JSON】
        redisObjectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                // JsonTypeInfo.As.PROPERTY
                JsonTypeInfo.As.WRAPPER_ARRAY // 使用WRAPPER_ARRAY比PROPERTY在循环引用上更安全
                );

        // 配置null值序列化
        GenericJackson2JsonRedisSerializer.registerNullValueSerializer(redisObjectMapper, null);
        return new GenericJackson2JsonRedisSerializer(redisObjectMapper);
    }

    /** RedisTemplate配置 */
    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(
            LettuceConnectionFactory lettuceConnectionFactory,
            RedisSerializer<Object> redisSerializer) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory);

        template.setKeySerializer(StringRedisSerializer.UTF_8);
        template.setValueSerializer(redisSerializer);

        template.setHashKeySerializer(StringRedisSerializer.UTF_8);
        template.setHashValueSerializer(redisSerializer);

        // 启用事务支持
        template.setEnableTransactionSupport(true);

        template.afterPropertiesSet();
        return template;
    }

    /** Redisson客户端 */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        Config config = new Config();

        String schema = redisProperties.getSsl().isEnabled() ? REDISS_SCHEMA : REDIS_SCHEMA;

        // 配置集群、哨兵或单节点
        configureRedissonServer(config, redisProperties, schema);

        // 通用配置
        config.setLockWatchdogTimeout(30000L); // 看门狗超时时间30秒

        // 线程池配置
        config.setThreads(16);
        config.setNettyThreads(32);

        try {
            return Redisson.create(config);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create Redisson client", e);
        }
    }

    /** 配置Redisson服务器模式 */
    private void configureRedissonServer(Config config, RedisProperties properties, String schema) {
        RedisProperties.Sentinel sentinel = properties.getSentinel();
        RedisProperties.Cluster cluster = properties.getCluster();

        if (cluster != null && !cluster.getNodes().isEmpty()) {
            configureCluster(config, properties, cluster, schema);
        } else if (sentinel != null && !sentinel.getNodes().isEmpty()) {
            configureSentinel(config, properties, sentinel, schema);
        } else {
            configureSingle(config, properties, schema);
        }
    }

    /** 集群模式配置 */
    private void configureCluster(
            Config config,
            RedisProperties properties,
            RedisProperties.Cluster cluster,
            String schema) {
        ClusterServersConfig clusterConfig = config.useClusterServers();

        // 节点地址
        cluster.getNodes().forEach(node -> clusterConfig.addNodeAddress(schema + node));

        // 基础配置
        configureBaseConfig(clusterConfig, properties);

        // 集群特定配置
        clusterConfig.setScanInterval(2000); // 集群扫描间隔

        // 从节点配置
        clusterConfig.setReadMode(ReadMode.SLAVE);
        clusterConfig.setSubscriptionMode(SubscriptionMode.SLAVE);
    }

    /** 哨兵模式配置 */
    private void configureSentinel(
            Config config,
            RedisProperties properties,
            RedisProperties.Sentinel sentinel,
            String schema) {
        SentinelServersConfig sentinelConfig = config.useSentinelServers();

        // 主节点名称和哨兵节点
        sentinelConfig.setMasterName(sentinel.getMaster());
        sentinel.getNodes().forEach(node -> sentinelConfig.addSentinelAddress(schema + node));

        // 基础配置
        configureBaseConfig(sentinelConfig, properties);

        // 哨兵特定配置
        sentinelConfig.setDatabase(properties.getDatabase());
        sentinelConfig.setCheckSentinelsList(false);

        // 哨兵密码
        if (StringUtils.hasText(sentinel.getPassword())) {
            sentinelConfig.setSentinelPassword(sentinel.getPassword());
        }
    }

    /** 单节点模式配置 */
    private void configureSingle(Config config, RedisProperties properties, String schema) {
        SingleServerConfig singleConfig = config.useSingleServer();

        // 地址
        String address = schema + properties.getHost() + ":" + properties.getPort();
        singleConfig.setAddress(address);

        // 基础配置
        configureBaseConfig(singleConfig, properties);

        // 单节点特定配置
        singleConfig.setDatabase(properties.getDatabase());

        // 连接超时和重试
        singleConfig.setConnectTimeout(3000);
        singleConfig.setRetryAttempts(3);
        singleConfig.setRetryInterval(1500);
    }

    /** 基础配置（通用） */
    private void configureBaseConfig(BaseConfig<?> config, RedisProperties properties) {
        // 密码
        if (StringUtils.hasText(properties.getPassword())) {
            config.setPassword(properties.getPassword());
        }

        // 超时时间
        long timeoutMillis = properties.getTimeout().toMillis();
        if (timeoutMillis > 0 && timeoutMillis <= Integer.MAX_VALUE) {
            config.setTimeout((int) timeoutMillis);
        }

        // Ping连接间隔
        config.setPingConnectionInterval(30000);

        // 连接空闲超时
        config.setIdleConnectionTimeout(10000);
    }
}
