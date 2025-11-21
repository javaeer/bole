package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.ApiResponse;
import cn.net.yunlou.bole.request.LoginRequest;
import cn.net.yunlou.bole.request.RegisterRequest;
import cn.net.yunlou.bole.response.UserResponse;
import cn.net.yunlou.bole.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthController {

    private final IUserService userService;

    public AuthController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ResponseEntity<ApiResponse<UserResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        UserResponse userResponse = userService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest registerRequest) {
        UserResponse userResponse = userService.register(registerRequest);
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }

    @PostMapping("/logout")
    @Operation(summary = "用户退出")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // JWT 无状态，客户端删除 token 即可
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "刷新Token")
    public ResponseEntity<ApiResponse<String>> refreshToken() {
        // 刷新 token 逻辑
        return ResponseEntity.ok(ApiResponse.success("new-token"));
    }
}
