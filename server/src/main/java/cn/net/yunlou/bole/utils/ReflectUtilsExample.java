package cn.net.yunlou.bole.utils;

import cn.net.yunlou.bole.entity.User;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;

/**
 * ReflectUtils使用示例
 */
@Slf4j
public class ReflectUtilsExample {

    public static void main(String[] args) {
        try {
            // 3. 创建实例
            User userService = ReflectUtils.createInstance(User.class);

            // 4. 设置字段值
            ReflectUtils.setFieldValue(userService, "maxRetryCount", "3"); // 自动类型转换

            // 5. 调用方法
            String result = ReflectUtils.invokeMethod(userService, "getUserInfo", 123L);

            // 6. 高性能调用
            String fastResult = ReflectUtils.invokeMethodWithHandle(userService, "getUserInfo", 456L);

            // 7. 获取所有字段
            List<Field> fields = ReflectUtils.getAllFields(User.class);
            log.info("Found {} fields in User", fields.size());

        } catch (ReflectUtils.ReflectionException e) {
            log.error("Reflection operation failed", e);
        }
    }
}