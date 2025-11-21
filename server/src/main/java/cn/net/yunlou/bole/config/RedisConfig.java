package cn.net.yunlou.bole.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.*;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * Redis 配置类
 *
 * @author laughtiger
 */
@Configuration
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class RedisConfig {

    private final String SCHEMA_URL = "redis://";

    private final Duration lockWatchdogTimeout = Duration.ofSeconds(30); // 默认30秒

    /**
     * 自定义redis序列化的机制,重新定义一个ObjectMapper.防止和MVC的冲突
     * https://juejin.im/post/5e869d426fb9a03c6148c97e
     */
    @Bean
    public RedisSerializer<Object> redisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 反序列化时候遇到不匹配的属性并不抛出异常
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 序列化时候遇到空对象不抛出异常
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 反序列化的时候如果是无效子类型,不抛出异常
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        // 不使用默认的dateTime进行序列化,
        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        // 使用JSR310提供的序列化类,里面包含了大量的JDK8时间序列化类
        objectMapper.registerModule(new JavaTimeModule());
        // 启用反序列化所需的类型信息,在属性中添加@class
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
        // 配置null值的序列化器
        GenericJackson2JsonRedisSerializer.registerNullValueSerializer(objectMapper, null);
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    /**
     * 自定义序列化
     *
     * @param lettuceConnectionFactory
     * @return
     */
    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory
            ,RedisSerializer<Object> redisSerializer) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);

        redisTemplate.setKeySerializer(StringRedisSerializer.UTF_8);
        redisTemplate.setValueSerializer(redisSerializer);

        redisTemplate.setHashKeySerializer(StringRedisSerializer.UTF_8);
        redisTemplate.setHashValueSerializer(redisSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }


    /**
     * 创建 RedissonClient，注入IOC容器
     */
    @Bean("redissonClient")
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        Config config = new Config();
        RedisProperties.Sentinel sentinelConfig = redisProperties.getSentinel();
        RedisProperties.Cluster clusterConfig = redisProperties.getCluster();
        if (clusterConfig != null) {
            configureClusterServer(redisProperties, config, clusterConfig, SCHEMA_URL);
        } else if (StringUtils.hasText(redisProperties.getHost())) {
            configureSingleServer(redisProperties, config, SCHEMA_URL);
        } else if (sentinelConfig != null) {
            configureSentinelServer(redisProperties, config, sentinelConfig, SCHEMA_URL);
        } else {
            throw new IllegalStateException("Invalid Redis configuration: expected either cluster, single, or sentinel configuration.");
        }

        config.setLockWatchdogTimeout(lockWatchdogTimeout.toMillis());
        return Redisson.create(config);
    }

    private void configureClusterServer(RedisProperties redisProperties, Config config, RedisProperties.Cluster clusterConfig, String schema) {
        ClusterServersConfig clusterServers = config.useClusterServers();
        clusterConfig.getNodes().forEach(node -> clusterServers.addNodeAddress(schema + node));
        configureBaseConfig(redisProperties, clusterServers);
        if (StringUtils.hasText(redisProperties.getPassword())) {
            clusterServers.setPassword(redisProperties.getPassword());
        }
    }

    private void configureSingleServer(RedisProperties redisProperties, Config config, String schema) {
        SingleServerConfig singleServer = config.useSingleServer();
        singleServer.setAddress(schema + redisProperties.getHost() + ":" + redisProperties.getPort());
        configureBaseConfig(redisProperties, singleServer);
        singleServer.setDatabase(redisProperties.getDatabase());
    }

    private void configureSentinelServer(RedisProperties redisProperties, Config config, RedisProperties.Sentinel sentinelConfig, String schema) {
        SentinelServersConfig sentinelServers = config.useSentinelServers();
        sentinelServers.setMasterName(sentinelConfig.getMaster());
        sentinelConfig.getNodes().forEach(node -> sentinelServers.addSentinelAddress(schema + node));
        configureBaseConfig(redisProperties, sentinelServers);
        sentinelServers.setDatabase(redisProperties.getDatabase());
        // 设置哨兵密码（如果配置）
        if (StringUtils.hasText(sentinelConfig.getPassword())) {
            sentinelServers.setPassword(sentinelConfig.getPassword());
        }
    }

    private void configureBaseConfig(RedisProperties redisProperties, BaseConfig<?> config) {
        if (StringUtils.hasText(redisProperties.getPassword())) {
            config.setPassword(redisProperties.getPassword());
        }
        long timeoutMillis = redisProperties.getTimeout().toMillis();
        if (timeoutMillis > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Redis timeout cannot exceed " + Integer.MAX_VALUE + " milliseconds.");
        }
        config.setTimeout((int) timeoutMillis);
        config.setPingConnectionInterval(30000); // 30秒
    }

}
