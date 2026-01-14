package cn.net.yunlou.bole.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName YunpianConfig @Description TODO @Author laughtiger @Email dev@laughtiger.com @Website
 * www.laughtiger.com @Date 17:36 @Version 1.0
 */
// @ConfigurationProperties(value = "classpath:config/yunpian.properties")//方式一：单独的配置文件
@ConfigurationProperties(prefix = "app.yunpian") // 方式二直接加入到yml配置文件，这种方式貌似更好一些
@Component
@Data
public class AppYunPianConfig {

    private String apiKey;
}
