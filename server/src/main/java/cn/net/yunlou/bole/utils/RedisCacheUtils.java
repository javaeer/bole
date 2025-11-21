package cn.net.yunlou.bole.utils;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisCacheUtils {

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final RedisTemplate<String, Object> redisTemplate;

    // 静态初始化块配置 ObjectMapper
    static {
        // 反序列化时候遇到不匹配的属性并不抛出异常
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 序列化时候遇到空对象不抛出异常
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 反序列化的时候如果是无效子类型,不抛出异常
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        // 不使用默认的dateTime进行序列化,
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        // 使用JSR310提供的序列化类,里面包含了大量的JDK8时间序列化类
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        // 启用反序列化所需的类型信息,在属性中添加@class
        OBJECT_MAPPER.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
    }

    // 动态获取Operations，减少冗余注入
    private ValueOperations<String, Object> valueOps() {
        return redisTemplate.opsForValue();
    }

    private HashOperations<String, Object, Object> hashOps() {
        return redisTemplate.opsForHash();
    }

    private ListOperations<String, Object> listOps() {
        return redisTemplate.opsForList();
    }

    private SetOperations<String, Object> setOps() {
        return redisTemplate.opsForSet();
    }

    private ZSetOperations<String, Object> zSetOps() {
        return redisTemplate.opsForZSet();
    }

    // 检查键是否存在
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    // 获取过期时间（默认秒）
    public Long getExpire(String key) {
        return getExpire(key, TimeUnit.SECONDS);
    }

    // 获取指定时间单位的过期时间
    public Long getExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    //设置到期时长
    public Boolean putExpire(String key, Duration duration) {
        return redisTemplate.expire(key, duration);
    }

    //设置到期时长
    public Boolean putExpire(String key, long timeout, TimeUnit timeUnit) {
        return redisTemplate.expire(key, timeout, timeUnit);
    }

    public Boolean putExpireAt(String key, Date date) {
        return redisTemplate.expireAt(key, date);
    }

    // 存储对象（支持过期时间）
    public void putObject(String key, Object value) {
        putObject(key, value, 0, TimeUnit.SECONDS);
    }

    public void putObject(String key, Object value, long timeout) {
        putObject(key, value, timeout, TimeUnit.SECONDS);
    }

    public void putObject(String key, Object value, long timeout, TimeUnit timeUnit) {
        try {
            if (timeout > 0) {
                valueOps().set(key, value, timeout, timeUnit);
            } else {
                valueOps().set(key, value);
            }
        } catch (Exception e) {
            throw new RedisOperationException("Failed to put object into Redis", e);
        }
    }

    // 存储List（左推）
    public void leftPush(String key, Object value) {
        try {
            listOps().leftPush(key, value);
        } catch (Exception e) {
            throw new RedisOperationException("Failed to left push value to list", e);
        }
    }

    public void leftPushAll(String key, Collection<Object> values) {
        try {
            listOps().leftPushAll(key, values);
        } catch (Exception e) {
            throw new RedisOperationException("Failed to left push all values to list", e);
        }
    }

    // 存储List（右推）
    public void rightPush(String key, Object value) {
        try {
            listOps().rightPush(key, value);
        } catch (Exception e) {
            throw new RedisOperationException("Failed to right push value to list", e);
        }
    }

    public void rightPushAll(String key, Collection<Object> values) {
        try {
            listOps().rightPushAll(key, values);
        } catch (Exception e) {
            throw new RedisOperationException("Failed to right push all values to list", e);
        }
    }

    // 存储Set
    public void addToSet(String key, Object... values) {
        try {
            setOps().add(key, values);
        } catch (Exception e) {
            throw new RedisOperationException("Failed to add values to set", e);
        }
    }

    // 存储ZSet（带分数）
    public void addToZSet(String key, Object value, double score) {
        try {
            zSetOps().add(key, value, score);
        } catch (Exception e) {
            throw new RedisOperationException("Failed to add value to zset", e);
        }
    }

    // 存储Hash
    public void putHash(String key, Object hashKey, Object value) {
        try {
            hashOps().put(key, hashKey, value);
        } catch (Exception e) {
            throw new RedisOperationException("Failed to put hash value", e);
        }
    }

    // 获取对象（类型安全）
    public <T> T getObject(String key, Class<T> type) {
        try {
            Object value = valueOps().get(key);
            if (value == null) {
                return null;
            }
            // 如果已经是目标类型，直接返回
            if (type.isInstance(value)) {
                return type.cast(value);
            }

            return doSafeConvert(value, type);
        } catch (ClassCastException e) {
            throw new RedisOperationException("Type mismatch for key: " + key, e);
        } catch (NumberFormatException e) {
            throw new RedisOperationException("Cannot convert value to Long for key: " + key, e);
        } catch (Exception e) {
            throw new RedisOperationException("Failed to get object from Redis", e);
        }
    }

    private <T> T doSafeConvert(Object value, Class<T> type) {

        // 处理数字类型转换
        if (type == Long.class) {
            if (value instanceof Integer) {
                return type.cast(((Integer) value).longValue());
            } else if (value instanceof String) {
                return type.cast(Long.parseLong((String) value));
            } else if (value instanceof Double) {
                return type.cast(((Double) value).longValue());
            } else if (value instanceof Float) {
                return type.cast(((Float) value).longValue());
            } else if (value instanceof Short) {
                return type.cast(((Short) value).longValue());
            } else if (value instanceof Byte) {
                return type.cast(((Byte) value).longValue());
            }
        }

        // 处理 Integer 类型转换
        if (type == Integer.class) {
            if (value instanceof Long) {
                return type.cast(((Long) value).intValue());
            } else if (value instanceof String) {
                return type.cast(Integer.parseInt((String) value));
            } else if (value instanceof Double) {
                return type.cast(((Double) value).intValue());
            } else if (value instanceof Float) {
                return type.cast(((Float) value).intValue());
            } else if (value instanceof Short) {
                return type.cast(((Short) value).intValue());
            } else if (value instanceof Byte) {
                return type.cast(((Byte) value).intValue());
            }
        }

        // 处理 Double 类型转换
        if (type == Double.class) {
            if (value instanceof Integer) {
                return type.cast(((Integer) value).doubleValue());
            } else if (value instanceof Long) {
                return type.cast(((Long) value).doubleValue());
            } else if (value instanceof String) {
                return type.cast(Double.parseDouble((String) value));
            } else if (value instanceof Float) {
                return type.cast(((Float) value).doubleValue());
            } else if (value instanceof Short) {
                return type.cast(((Short) value).doubleValue());
            } else if (value instanceof Byte) {
                return type.cast(((Byte) value).doubleValue());
            }
        }

        // 处理 Float 类型转换
        if (type == Float.class) {
            if (value instanceof Integer) {
                return type.cast(((Integer) value).floatValue());
            } else if (value instanceof Long) {
                return type.cast(((Long) value).floatValue());
            } else if (value instanceof String) {
                return type.cast(Float.parseFloat((String) value));
            } else if (value instanceof Double) {
                return type.cast(((Double) value).floatValue());
            } else if (value instanceof Short) {
                return type.cast(((Short) value).floatValue());
            } else if (value instanceof Byte) {
                return type.cast(((Byte) value).floatValue());
            }
        }

        // 处理 Short 类型转换
        if (type == Short.class) {
            if (value instanceof Integer) {
                return type.cast(((Integer) value).shortValue());
            } else if (value instanceof Long) {
                return type.cast(((Long) value).shortValue());
            } else if (value instanceof String) {
                return type.cast(Short.parseShort((String) value));
            } else if (value instanceof Double) {
                return type.cast(((Double) value).shortValue());
            } else if (value instanceof Float) {
                return type.cast(((Float) value).shortValue());
            } else if (value instanceof Byte) {
                return type.cast(((Byte) value).shortValue());
            }
        }

        // 处理 Byte 类型转换
        if (type == Byte.class) {
            if (value instanceof Integer) {
                return type.cast(((Integer) value).byteValue());
            } else if (value instanceof Long) {
                return type.cast(((Long) value).byteValue());
            } else if (value instanceof String) {
                return type.cast(Byte.parseByte((String) value));
            } else if (value instanceof Double) {
                return type.cast(((Double) value).byteValue());
            } else if (value instanceof Float) {
                return type.cast(((Float) value).byteValue());
            } else if (value instanceof Short) {
                return type.cast(((Short) value).byteValue());
            }
        }

        // 处理 String 类型转换
        if (type == String.class) {
            return type.cast(String.valueOf(value));
        }

        // 处理 Boolean 类型转换
        if (type == Boolean.class) {
            if (value instanceof String) {
                String strValue = ((String) value).toLowerCase();
                if ("true".equals(strValue) || "1".equals(strValue) || "yes".equals(strValue)) {
                    return type.cast(true);
                } else if ("false".equals(strValue) || "0".equals(strValue) || "no".equals(strValue)) {
                    return type.cast(false);
                }
            } else if (value instanceof Number) {
                int intValue = ((Number) value).intValue();
                return type.cast(intValue != 0);
            }
        }

        // 如果是复杂对象，使用 Jackson 进行转换
        // 因为 Redis 中存储的可能是 LinkedHashMap，需要转换为目标类型
        if (value instanceof Map) {
            return OBJECT_MAPPER.convertValue(value, type);
        }

        // 如果以上都不匹配，尝试直接类型转换
        return type.cast(value);

    }

    // 获取Hash所有条目
    public Map<Object, Object> getHash(String key) {
        try {
            return hashOps().entries(key);
        } catch (Exception e) {
            throw new RedisOperationException("Failed to get hash entries", e);
        }
    }

    // 获取Hash单个值
    public <T> T getHashValue(String key, Object hashKey, Class<T> type) {
        try {
            Object value = hashOps().get(key, hashKey);
            return type.cast(value);
        } catch (ClassCastException e) {
            throw new RedisOperationException("Type mismatch for hash key: " + hashKey, e);
        } catch (Exception e) {
            throw new RedisOperationException("Failed to get hash value", e);
        }
    }

    // 弹出List元素
    public <T> T leftPop(String key, Class<T> type) {
        try {
            Object value = listOps().leftPop(key);
            return type.cast(value);
        } catch (ClassCastException e) {
            throw new RedisOperationException("Type mismatch for leftPop", e);
        } catch (Exception e) {
            throw new RedisOperationException("Failed to left pop from list", e);
        }
    }

    public <T> T rightPop(String key, Class<T> type) {
        try {
            Object value = listOps().rightPop(key);
            return type.cast(value);
        } catch (ClassCastException e) {
            throw new RedisOperationException("Type mismatch for rightPop", e);
        } catch (Exception e) {
            throw new RedisOperationException("Failed to right pop from list", e);
        }
    }

    // 检查Set成员
    public Boolean isSetMember(String key, Object value) {
        try {
            return setOps().isMember(key, value);
        } catch (Exception e) {
            throw new RedisOperationException("Failed to check set membership", e);
        }
    }

    // 删除键
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            throw new RedisOperationException("Failed to delete key: " + key, e);
        }
    }

    // 删除Hash子键
    public void deleteHashKeys(String key, Object... hashKeys) {
        try {
            hashOps().delete(key, hashKeys);
        } catch (Exception e) {
            throw new RedisOperationException("Failed to delete hash keys", e);
        }
    }

    // 批量删除
    public void deleteBatch(Collection<String> keys) {
        try {
            redisTemplate.delete(keys);
        } catch (Exception e) {
            throw new RedisOperationException("Failed to delete batch keys", e);
        }
    }

    // 自定义异常类
    public static class RedisOperationException extends RuntimeException {
        public RedisOperationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}