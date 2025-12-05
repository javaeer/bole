package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.EnumDTO;
import cn.net.yunlou.bole.common.IEnum;
import cn.net.yunlou.bole.common.constant.UserKeyField;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.model.UserEdit;
import cn.net.yunlou.bole.model.UserQuery;
import cn.net.yunlou.bole.model.UserView;
import cn.net.yunlou.bole.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@Tag(name = "02.用户管理", description = "用户相关接口")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("profile")
    @Operation(summary = "获取当前用户信息")
    public BusinessResponse<User> getCurrentUser() {
        // 从 SecurityContext 获取当前用户
        String username = SecurityContextUtils.getCurrentUsername();
        User user = userService.findByUsername(username);
        return BusinessResponse.success(user);
    }

    @GetMapping("keyField")
    @Operation(summary = "获取查询关键字")
    public BusinessResponse<List<EnumDTO>> getKeyField() {
        // 从 SecurityContext 获取当前用户
        List<EnumDTO> enumDTOS = IEnum.toDTOList(UserKeyField.class);
        return BusinessResponse.success(enumDTOS);
    }

    @PutMapping("profile")
    @Operation(summary = "更新用户信息")
    public BusinessResponse<User> updateUser(@Valid @RequestBody UserEdit request) {
        String username = SecurityContextUtils.getCurrentUsername();
        User currentUser = userService.findByUsername(username);

        userService.updateByEdit(request);
        return BusinessResponse.success(userService.getById(currentUser.getId()));
    }

    @PostMapping("page")
    @Operation(summary = "获取用户列表(管理员)")
    // @PreAuthorize("hasAnyAuthority('read','write')")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Page<UserView>> getUserList(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody UserQuery request) {

        return BusinessResponse.success(userService.pageViewByQuery(page, size, request));
    }
}
