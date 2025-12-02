package cn.net.yunlou.bole.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("debug")
@Tag(name = "16.系统诊断", description = "系统状态诊断接口")
public class DebugController {

    @GetMapping("status")
    @Operation(summary = "系统状态检查")
    public Map<String, Object> status() {
        log.info("系统状态检查接口被调用");
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("timestamp", System.currentTimeMillis());
        result.put("knife4jEnabled", true);
        return result;
    }

    @GetMapping("knife4j-paths")
    @Operation(summary = "Knife4j 路径检查")
    public Map<String, Object> knife4jPaths() {
        Map<String, Object> result = new HashMap<>();
        result.put("doc.html", "http://localhost:8080/doc.html");
        result.put("v3/api-docs", "http://localhost:8080/v3/api-docs");
        result.put("swagger-ui", "http://localhost:8080/swagger-ui.html");
        return result;
    }
}
