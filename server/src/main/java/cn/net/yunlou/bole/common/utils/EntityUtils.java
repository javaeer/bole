package cn.net.yunlou.bole.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** 实体业务工具类 - 专注于业务实体操作 */
@Slf4j
@Component
public class EntityUtils {

    public EntityUtils() {
        log.debug("ReflectUtils initialized");
    }

    /** 过滤实体无效值 */
    public static <T> T filterInvalidValues(T entity) {
        return filterInvalidValues(entity, ValueUtils::isValid);
    }

    public static <T> T filterInvalidValues(
            T entity, java.util.function.Predicate<Object> validator) {
        if (entity == null) {
            return null;
        }

        try {
            // 使用BeanUtils创建实例和拷贝属性
            T newEntity = BeanUtils.copyProperties(entity, (Class<T>) entity.getClass());

            // 获取所有字段并过滤无效值
            List<Field> fields = ReflectUtils.getAllFields(entity.getClass());

            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())
                        || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }

                Object value = BeanUtils.getProperty(entity, field.getName());
                if (!validator.test(value)) {
                    // 重置无效值为null
                    BeanUtils.setProperty(newEntity, field.getName(), null);
                }
            }

            return newEntity;
        } catch (Exception e) {
            log.warn("Entity filtering failed, returning original entity", e);
            return entity;
        }
    }
}
