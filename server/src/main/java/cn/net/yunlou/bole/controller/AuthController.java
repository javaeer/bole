package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.model.request.*;
import cn.net.yunlou.bole.model.response.AccessTokenResponse;
import cn.net.yunlou.bole.model.response.RefreshTokenResponse;
import cn.net.yunlou.bole.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@Tag(name = "01.认证管理", description = "用户认证相关接口")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("login")
    @Operation(summary = "用户登录")
    public BusinessResponse<AccessTokenResponse> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        AccessTokenResponse accessTokenResponse = authService.login(loginRequest);
        return BusinessResponse.success(accessTokenResponse);
    }

    @PostMapping("register")
    @Operation(summary = "用户注册")
    public BusinessResponse<AccessTokenResponse> register(
            @Valid @RequestBody RegisterRequest registerRequest) {
        AccessTokenResponse accessTokenResponse = authService.register(registerRequest);
        return BusinessResponse.success(accessTokenResponse);
    }

    @PostMapping("refresh")
    @Operation(summary = "刷新访问令牌")
    public BusinessResponse<RefreshTokenResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = authService.refreshToken(request.getRefreshToken());
        return BusinessResponse.success(response);
    }

    @GetMapping("verify")
    @Operation(summary = "校验token是否有效")
    public BusinessResponse<Boolean> verify() {
        return BusinessResponse.success(SecurityContextUtils.isAuthenticated());
    }

    @GetMapping("current")
    @Operation(summary = "获取当前用户信息")
    public BusinessResponse<User> getCurrent() {

        User user = SecurityContextUtils.getCurrentUser();

        return BusinessResponse.success(user);
    }

    @PostMapping("change-password")
    @Operation(summary = "修改密码")
    public BusinessResponse<Boolean> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        return BusinessResponse.success(authService.changePassword(request));
    }

    @PostMapping("reset-password")
    @Operation(summary = "重置密码")
    public BusinessResponse<Boolean> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        return BusinessResponse.success(authService.resetPassword(request));
    }

    @PostMapping("logout")
    @Operation(summary = "用户登出")
    public BusinessResponse<String> logout() {

        authService.logout();

        return BusinessResponse.success("登出成功");
    }
}
