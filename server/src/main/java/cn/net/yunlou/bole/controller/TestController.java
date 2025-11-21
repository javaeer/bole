package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@Tag(name = "测试接口", description = "用于测试 Knife4j 集成的接口")
public class TestController {

    @GetMapping("/hello")
    @Operation(summary = "Hello World", description = "一个简单的测试接口")
    public BusinessResponse<String> hello() {
        return BusinessResponse.success("Hello, Knife4j!");
    }

    @PostMapping("/echo")
    @Operation(summary = "回声测试", description = "返回接收到的消息")
    public BusinessResponse<EchoResponse> echo(@Valid @RequestBody EchoRequest request) {
        EchoResponse response = new EchoResponse();
        response.setMessage("Echo: " + request.getMessage());
        response.setTimestamp(System.currentTimeMillis());
        return BusinessResponse.success(response);
    }

    @GetMapping("/secure")
    @Operation(
        summary = "需要认证的接口", 
        description = "这个接口需要 JWT Token 认证",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public BusinessResponse<String> secureEndpoint() {
        return BusinessResponse.success("这个接口需要认证才能访问");
    }

    @Data
    public static class EchoRequest {
        @NotBlank(message = "消息不能为空")
        private String message;
    }

    @Data
    public static class EchoResponse {
        private String message;
        private Long timestamp;
    }
}