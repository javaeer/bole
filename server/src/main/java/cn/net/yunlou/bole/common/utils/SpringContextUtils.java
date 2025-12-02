package cn.net.yunlou.bole.common.utils;

import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/** Spring 上下文工具类 */
@Component
public class SpringContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    private static Environment environment;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.applicationContext = applicationContext;
        SpringContextUtils.environment = applicationContext.getEnvironment();
    }

    /** 获取 ApplicationContext */
    public static ApplicationContext getApplicationContext() {
        checkApplicationContext();
        return applicationContext;
    }

    /** 通过名称获取Bean */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        checkApplicationContext();
        return (T) applicationContext.getBean(name);
    }

    /** 通过类型获取Bean */
    public static <T> T getBean(Class<T> clazz) {
        checkApplicationContext();
        return applicationContext.getBean(clazz);
    }

    /** 通过名称和类型获取Bean */
    public static <T> T getBean(String name, Class<T> clazz) {
        checkApplicationContext();
        return applicationContext.getBean(name, clazz);
    }

    /** 获取指定类型的所有Bean */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        checkApplicationContext();
        return applicationContext.getBeansOfType(clazz);
    }

    /** 获取环境配置 */
    public static Environment getEnvironment() {
        checkApplicationContext();
        return environment;
    }

    /** 获取配置属性 */
    public static String getProperty(String key) {
        checkApplicationContext();
        return environment.getProperty(key);
    }

    /** 获取配置属性，带默认值 */
    public static String getProperty(String key, String defaultValue) {
        checkApplicationContext();
        return environment.getProperty(key, defaultValue);
    }

    /** 获取配置属性（指定类型） */
    public static <T> T getProperty(String key, Class<T> targetType) {
        checkApplicationContext();
        return environment.getProperty(key, targetType);
    }

    /** 获取配置属性（指定类型，带默认值） */
    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        checkApplicationContext();
        return environment.getProperty(key, targetType, defaultValue);
    }

    /** 获取活跃的配置文件 */
    public static String[] getActiveProfiles() {
        checkApplicationContext();
        return environment.getActiveProfiles();
    }

    /** 检查当前环境是否包含指定配置文件 */
    public static boolean hasProfile(String profile) {
        checkApplicationContext();
        String[] activeProfiles = getActiveProfiles();
        for (String activeProfile : activeProfiles) {
            if (activeProfile.equals(profile)) {
                return true;
            }
        }
        return false;
    }

    /** 发布事件 */
    public static void publishEvent(Object event) {
        checkApplicationContext();
        applicationContext.publishEvent(event);
    }

    /** 检查ApplicationContext是否已注入 */
    private static void checkApplicationContext() {
        Assert.notNull(applicationContext, "ApplicationContext未注入，请在Spring配置中定义该工具类");
    }
}
