package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.model.*;
import cn.net.yunlou.bole.model.AccessTokenDTO;
import cn.net.yunlou.bole.model.RefreshTokenViewDTO;
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
    public BusinessResponse<AccessTokenDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return BusinessResponse.success(authService.login(loginDTO));
    }

    @PostMapping("register")
    @Operation(summary = "用户注册")
    public BusinessResponse<AccessTokenDTO> register(@Valid @RequestBody RegisterDTO dto) {
        return BusinessResponse.success(authService.register(dto));
    }

    @PostMapping("refresh")
    @Operation(summary = "刷新访问令牌")
    public BusinessResponse<RefreshTokenViewDTO> refreshToken(
            @Valid @RequestBody RefreshTokenDTO dto) {
        return BusinessResponse.success(authService.refreshToken(dto.getRefreshToken()));
    }

    @GetMapping("verify")
    @Operation(summary = "校验token是否有效")
    public BusinessResponse<Boolean> verify() {
        return BusinessResponse.success(SecurityContextUtils.isAuthenticated());
    }

    @GetMapping("current")
    @Operation(summary = "获取当前用户信息")
    public BusinessResponse<User> getCurrent() {
        return BusinessResponse.success(SecurityContextUtils.getCurrentUser());
    }

    @PostMapping("change-password")
    @Operation(summary = "修改密码")
    public BusinessResponse<Boolean> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        return BusinessResponse.success(authService.changePassword(dto));
    }

    @PostMapping("reset-password")
    @Operation(summary = "重置密码")
    public BusinessResponse<Boolean> resetPassword(@Valid @RequestBody ResetPasswordDTO request) {
        return BusinessResponse.success(authService.resetPassword(request));
    }

    @PostMapping("logout")
    @Operation(summary = "用户登出")
    public BusinessResponse<String> logout() {

        authService.logout();

        return BusinessResponse.success("登出成功");
    }
}
