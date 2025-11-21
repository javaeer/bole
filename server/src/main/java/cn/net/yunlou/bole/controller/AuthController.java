package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.request.LoginRequest;
import cn.net.yunlou.bole.request.RefreshTokenRequest;
import cn.net.yunlou.bole.request.RegisterRequest;
import cn.net.yunlou.bole.response.AccessTokenResponse;
import cn.net.yunlou.bole.response.RefreshTokenResponse;
import cn.net.yunlou.bole.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<BusinessResponse<AccessTokenResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        AccessTokenResponse accessTokenResponse = authService.login(loginRequest);
        return ResponseEntity.ok(BusinessResponse.success(accessTokenResponse));
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ResponseEntity<BusinessResponse<AccessTokenResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AccessTokenResponse accessTokenResponse = authService.register(registerRequest);
        return ResponseEntity.ok(BusinessResponse.success(accessTokenResponse));
    }

    /**
     * 刷新访问令牌
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<BusinessResponse<String>> logout() {

        authService.logout();

        return ResponseEntity.ok(BusinessResponse.success("登出成功"));
    }
}
