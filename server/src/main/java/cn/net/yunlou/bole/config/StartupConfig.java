package cn.net.yunlou.bole.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupConfig {

    private final Environment environment;

    @PostConstruct
    public void checkConfig() {
        log.info("=== 应用启动配置检查 ===");
        log.info("激活的配置文件: {}", Arrays.toString(environment.getActiveProfiles()));
        log.info("应用名称: {}", environment.getProperty("spring.application.name"));
        log.info("服务器端口: {}", environment.getProperty("server.port"));

        log.info("=== SpringDoc/Knife4j 配置 ===");
        log.info("SpringDoc API Docs Path: {}", environment.getProperty("springdoc.api-docs.path"));
        log.info("SpringDoc Swagger UI Enabled: {}", environment.getProperty("springdoc.swagger-ui.enabled"));
        log.info("Knife4j Enable: {}", environment.getProperty("knife4j.enable"));

        log.info("=== 数据库配置 ===");
        log.info("数据库 URL: {}", environment.getProperty("spring.datasource.url"));
        log.info("Redis Host: {}", environment.getProperty("spring.data.redis.host"));

        log.info("=== 访问地址 ===");
        log.info("📚 文档访问地址:");
        log.info("   Knife4j UI: http://localhost:8080/doc.html");
        log.info("   OpenAPI JSON: http://localhost:8080/v3/api-docs");
        log.info("   API 分组: http://localhost:8080/v3/api-docs/bole-server");
        log.info("   健康检查: http://localhost:8080/api/health");
        log.info("=== 配置检查结束 ===");
    }
}