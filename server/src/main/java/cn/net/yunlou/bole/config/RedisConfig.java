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
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.SimpleCacheResolver;
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
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis 统一配置类
 * 整合缓存配置和Redis模板配置
 * <p>
 * spring:
 * data:
 * redis:
 * host: ${REDIS_HOST:localhost}
 * port: ${REDIS_PORT:6379}
 * password: ${REDIS_PASSWORD:}
 * timeout: 3000ms
 * lettuce:
 * pool:
 * max-active: 8
 * max-idle: 8
 * min-idle: 0
 * max-wait: -1ms
 * cache:
 * type: redis
 * redis:
 * time-to-live: 1h
 * cache-null-values: false
 * use-key-prefix: true
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

    @Value("${spring.data.redis.timeout:3000}")
    private int redisTimeout;

    @Value("${spring.data.redis.lettuce.pool.max-idle:8}")
    private int maxIdle;

    @Value("${spring.data.redis.lettuce.pool.min-idle:0}")
    private int minIdle;

    @Value("${spring.data.redis.lettuce.pool.max-active:8}")
    private int maxActive;

    @Value("${spring.data.redis.lettuce.pool.max-wait:-1}")
    private long maxWait;

    /**
     * 自定义缓存配置（可按缓存名称配置不同的TTL）
     */
    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory,
                                     CacheProperties cacheProperties) {
        // 默认缓存配置
        RedisCacheConfiguration defaultConfig = createDefaultCacheConfig(cacheProperties);

        // 自定义缓存配置（按缓存名称设置不同的过期时间）
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 示例：为不同的缓存设置不同的TTL
        cacheConfigurations.put("userCache",
                defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("productCache",
                defaultConfig.entryTtl(Duration.ofHours(2)));
        cacheConfigurations.put("configCache",
                defaultConfig.entryTtl(Duration.ofDays(1)));
        cacheConfigurations.put("sessionCache",
                defaultConfig.entryTtl(Duration.ofMinutes(10)));

        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory))
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware() // 支持事务
                .build();
    }

    /**
     * 创建默认缓存配置
     */
    private RedisCacheConfiguration createDefaultCacheConfig(CacheProperties cacheProperties) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        // 序列化配置
        config = config.serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string())
        );
        config = config.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer())
        );

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

    /**
     * 缓存解析器
     */
    @Bean
    public CacheResolver cacheResolver(CacheManager cacheManager) {
        return new SimpleCacheResolver(cacheManager);
    }


    /**
     * 树形服务缓存解析器
     */
    @Bean
    public CacheResolver treeCacheResolver(CacheManager cacheManager) {
        return new TreeCacheResolver(cacheManager);
    }

    /**
     * Redis序列化器
     */
    @Bean
    public RedisSerializer<Object> redisSerializer() {
        ObjectMapper objectMapper = createObjectMapper();
        // 配置null值序列化
        GenericJackson2JsonRedisSerializer.registerNullValueSerializer(objectMapper, null);
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    /**
     * 共享的ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        return createObjectMapper();
    }

    /**
     * 创建ObjectMapper
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // 基础配置
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);

        // 时间处理
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
        objectMapper.registerModule(javaTimeModule);

        // 类型信息
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        return objectMapper;
    }

    /**
     * RedisTemplate配置
     */
    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(
            LettuceConnectionFactory lettuceConnectionFactory,
            RedisSerializer<Object> redisSerializer) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory);

        // 序列化配置
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(redisSerializer);
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(redisSerializer);

        // 启用事务支持
        template.setEnableTransactionSupport(true);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * Redisson客户端
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        Config config = new Config();

        String password = redisProperties.getPassword();
        Duration timeout = redisProperties.getTimeout();
        boolean sslEnabled = false;//redisProperties.isSsl();
        String schema = sslEnabled ? REDISS_SCHEMA : REDIS_SCHEMA;

        // 配置集群、哨兵或单节点
        configureRedissonServer(config, redisProperties, schema);

        // 通用配置
        config.setLockWatchdogTimeout(30000L); // 看门狗超时时间30秒

        // 线程池配置
        config.setThreads(16);
        config.setNettyThreads(32);

        // 编码配置
        config.setCodec(new org.redisson.codec.JsonJacksonCodec(objectMapper()));

        try {
            return Redisson.create(config);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create Redisson client", e);
        }
    }

    /**
     * 配置Redisson服务器模式
     */
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

    /**
     * 集群模式配置
     */
    private void configureCluster(Config config, RedisProperties properties,
                                  RedisProperties.Cluster cluster, String schema) {
        ClusterServersConfig clusterConfig = config.useClusterServers();

        // 节点地址
        cluster.getNodes().forEach(node ->
                clusterConfig.addNodeAddress(schema + node));

        // 基础配置
        configureBaseConfig(clusterConfig, properties);

        // 集群特定配置
        clusterConfig.setScanInterval(2000); // 集群扫描间隔

        // 从节点配置
        clusterConfig.setReadMode(ReadMode.SLAVE);
        clusterConfig.setSubscriptionMode(SubscriptionMode.SLAVE);
    }

    /**
     * 哨兵模式配置
     */
    private void configureSentinel(Config config, RedisProperties properties,
                                   RedisProperties.Sentinel sentinel, String schema) {
        SentinelServersConfig sentinelConfig = config.useSentinelServers();

        // 主节点名称和哨兵节点
        sentinelConfig.setMasterName(sentinel.getMaster());
        sentinel.getNodes().forEach(node ->
                sentinelConfig.addSentinelAddress(schema + node));

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

    /**
     * 单节点模式配置
     */
    private void configureSingle(Config config, RedisProperties properties, String schema) {
        SingleServerConfig singleConfig = config.useSingleServer();

        // 地址
        String address = schema + properties.getHost() + ":" + properties.getPort();
        singleConfig.setAddress(address);

        // 基础配置
        configureBaseConfig(singleConfig, properties);

        // 单节点特定配置
        singleConfig.setDatabase(properties.getDatabase());
        singleConfig.setConnectionPoolSize(maxActive);
        singleConfig.setConnectionMinimumIdleSize(minIdle);

        // 连接超时和重试
        singleConfig.setConnectTimeout(redisTimeout);
        singleConfig.setRetryAttempts(3);
        singleConfig.setRetryInterval(1500);
    }

    /**
     * 基础配置（通用）
     */
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

        // 连接池配置
        if (config instanceof BaseMasterSlaveServersConfig) {
            BaseMasterSlaveServersConfig<?> msConfig = (BaseMasterSlaveServersConfig<?>) config;
            msConfig.setMasterConnectionPoolSize(maxActive);
            msConfig.setSlaveConnectionPoolSize(maxActive);
            msConfig.setMasterConnectionMinimumIdleSize(minIdle);
            msConfig.setSlaveConnectionMinimumIdleSize(minIdle);
        }

        // Ping连接间隔
        config.setPingConnectionInterval(30000);

        // 连接空闲超时
        config.setIdleConnectionTimeout(10000);
    }
}