package cn.net.yunlou.bole.common.annotation;

import cn.net.yunlou.bole.common.constant.StorageType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StorageStrategy {
    StorageType value() default StorageType.AUTO;
}
