package cn.net.yunlou.bole.common.utils;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedissonLockUtils {

    private final static String LOCK_PREFIX = "LOCK:YL";

    private final static Duration LOCK_EXPIRE_AFTER = Duration.ofSeconds(30);

    private final RedissonClient redissonClient;

    /**
     * 获取锁（自动添加前缀）
     */
    public RLock getLock(String lockKey) {
        String prefixedKey = LOCK_PREFIX + ":" + lockKey;
        return redissonClient.getLock(prefixedKey);
    }

    /**
     * 尝试获取锁（非阻塞）
     */
    public boolean tryLock(String lockKey) throws InterruptedException {
        RLock lock = getLock(lockKey);
        return lock.tryLock(0, LOCK_EXPIRE_AFTER.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 尝试获取锁（带超时等待）
     */
    public boolean tryLock(String lockKey, Duration waitTime) throws InterruptedException {
        RLock lock = getLock(lockKey);
        return lock.tryLock(waitTime.toMillis(), LOCK_EXPIRE_AFTER.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 释放锁
     */
    public void unlock(String lockKey) {
        RLock lock = getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}