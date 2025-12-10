package cn.net.yunlou.bole.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

/**
 * FileName: WebConfig Description: Created By laughtiger Created At 2025/12/8 17:25 Modified By
 * Modified At
 */
@Configuration
@EnableConfigurationProperties({StorageLocalProperties.class})
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final StorageLocalProperties storageLocalProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // static资源 - 使用 /static/ 前缀
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");

        // 公共资源（文件上传）
        registry.addResourceHandler(storageLocalProperties.getUrlPrefix() + "/**")
                .addResourceLocations("file:" + storageLocalProperties.getBasePath()+"/")
                .setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS));

        // Swagger/Knife4j资源
        registry.addResourceHandler("/doc.html", "/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 提供便捷访问方式
        registry.addViewController("/docs").setViewName("redirect:/doc.html");
        registry.addViewController("/swagger").setViewName("redirect:/swagger-ui.html");
    }
}
