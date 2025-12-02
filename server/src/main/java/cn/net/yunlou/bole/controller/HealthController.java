package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("health")
@RequiredArgsConstructor
@Tag(name = "17.系统监控", description = "系统健康检查和状态监控")
public class HealthController {

    @Value("${spring.application.name:server}")
    private String appName;

    @Value("${app.config.version:1.0.0}")
    private String appVersion;

    @Value("${app.config.environment:dev}")
    private String environment;

    @GetMapping
    @Operation(summary = "健康检查", description = "检查系统运行状态")
    public BusinessResponse<Map<String, Object>> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("application", appName);
        healthInfo.put("version", appVersion);
        healthInfo.put("environment", environment);
        healthInfo.put("timestamp", LocalDateTime.now());
        healthInfo.put(
                "docs",
                Map.of(
                        "knife4j", "http://localhost:8080/doc.html",
                        "api-docs", "http://localhost:8080/v3/api-docs",
                        "default-group", "http://localhost:8080/v3/api-docs/default",
                        "bole-server-group", "http://localhost:8080/v3/api-docs/bole-server"));

        log.info("健康检查请求 - 应用: {}, 环境: {}", appName, environment);
        return BusinessResponse.success(healthInfo);
    }

    @GetMapping("config")
    @Operation(summary = "配置调试", description = "查看当前配置信息")
    public BusinessResponse<Map<String, Object>> debugConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("profiles", java.util.Arrays.asList("dev"));
        config.put("serverPort", 8080);
        config.put("knife4jEnabled", true);
        config.put("springDocEnabled", true);
        config.put("currentTime", LocalDateTime.now());

        return BusinessResponse.success(config);
    }
}
