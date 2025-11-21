package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.request.LoginRequest;
import cn.net.yunlou.bole.request.RegisterRequest;
import cn.net.yunlou.bole.response.UserResponse;
import cn.net.yunlou.bole.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户认证相关接口")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ResponseEntity<BusinessResponse<UserResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        UserResponse userResponse = authService.login(loginRequest);
        return ResponseEntity.ok(BusinessResponse.success(userResponse));
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ResponseEntity<BusinessResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest registerRequest) {
        UserResponse userResponse = authService.register(registerRequest);
        return ResponseEntity.ok(BusinessResponse.success(userResponse));
    }

    @PostMapping("/logout")
    @Operation(summary = "用户退出")
    public ResponseEntity<BusinessResponse<Void>> logout() {
        // JWT 无状态，客户端删除 token 即可
        return ResponseEntity.ok(BusinessResponse.success(null));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "刷新Token")
    public ResponseEntity<BusinessResponse<String>> refreshToken() {
        // 刷新 token 逻辑
        return ResponseEntity.ok(BusinessResponse.success("new-token"));
    }
}
