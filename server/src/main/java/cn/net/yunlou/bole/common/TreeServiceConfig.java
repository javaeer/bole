package cn.net.yunlou.bole.common;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TreeServiceConfig {

    /**
     * 缓存名称，默认为 "tree"
     */
    String cacheName() default "tree";

    /**
     * 缓存过期时间（秒），默认为2小时
     */
    long expireTime() default 7200;

    /**
     * 缓存键前缀，默认为空
     */
    String keyPrefix() default "";

    /**
     * 是否启用缓存，默认为true
     */
    boolean enableCache() default true;
}