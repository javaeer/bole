package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.ApiResponse;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.request.UpdateUserRequest;
import cn.net.yunlou.bole.service.IUserRoleService;
import cn.net.yunlou.bole.service.IUserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户相关接口")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    private final IUserRoleService userRoleService;

    @GetMapping("/profile")
    @Operation(summary = "获取当前用户信息")
    public ResponseEntity<ApiResponse<User>> getCurrentUser() {
        // 从 SecurityContext 获取当前用户
        String username = "123";
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PutMapping("/profile")
    @Operation(summary = "更新用户信息")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @Valid @RequestBody UpdateUserRequest updateRequest) {
        String username ="123";
        User currentUser = userService.findByUsername(username);

        User user = new User();
        user.setId(currentUser.getId());
        user.setRealName(updateRequest.getRealName());
        user.setTitle(updateRequest.getTitle());
        user.setCompany(updateRequest.getCompany());
        user.setLocation(updateRequest.getLocation());
        user.setWebsite(updateRequest.getWebsite());
        user.setGithub(updateRequest.getGithub());
        user.setWechat(updateRequest.getWechat());
        user.setBio(updateRequest.getBio());

        userService.updateById(user);
        return ResponseEntity.ok(ApiResponse.success(userService.getById(currentUser.getId())));
    }

    @GetMapping("/list")
    @Operation(summary = "获取用户列表(管理员)")
    public ResponseEntity<ApiResponse<Page<User>>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<User> userPage = new Page<>(page, size);
        return ResponseEntity.ok(ApiResponse.success(userService.page(userPage)));
    }
}
