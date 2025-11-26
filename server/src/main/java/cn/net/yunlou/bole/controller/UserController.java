package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.EnumDTO;
import cn.net.yunlou.bole.common.IEnum;
import cn.net.yunlou.bole.common.constant.UserKeyFieldEnum;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.model.request.UpdateUserRequest;
import cn.net.yunlou.bole.model.request.UserSearchRequest;
import cn.net.yunlou.bole.service.UserService;
import cn.net.yunlou.bole.common.utils.QueryUtils;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "02.用户管理", description = "用户相关接口")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "获取当前用户信息")
    public ResponseEntity<BusinessResponse<User>> getCurrentUser() {
        // 从 SecurityContext 获取当前用户
        String username = SecurityContextUtils.getCurrentUsername();
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(BusinessResponse.success(user));
    }

    @GetMapping("keyField")
    @Operation(summary = "获取查询关键字")
    public ResponseEntity<BusinessResponse<List<EnumDTO>>> getKeyField() {
        // 从 SecurityContext 获取当前用户
        List<EnumDTO> enumDTOS = IEnum.toDTOList(UserKeyFieldEnum.class);
        return ResponseEntity.ok(BusinessResponse.success(enumDTOS));
    }

    @PutMapping("/profile")
    @Operation(summary = "更新用户信息")
    public ResponseEntity<BusinessResponse<User>> updateUser(
            @Valid @RequestBody UpdateUserRequest request) {
        String username = SecurityContextUtils.getCurrentUsername();
        User currentUser = userService.findByUsername(username);

        User user = QueryUtils.modelToBean(request,User.class);

        user.setId(currentUser.getId());

        userService.updateById(user);
        return ResponseEntity.ok(BusinessResponse.success(userService.getById(currentUser.getId())));
    }

    @PostMapping("page")
    @Operation(summary = "获取用户列表(管理员)")
    //@PreAuthorize("hasAnyAuthority('read','write')")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public ResponseEntity<BusinessResponse<Page<User>>> getUserList(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody UserSearchRequest request) {
        //Page<User> userPage = new Page<>(page, size);
        User user  = QueryUtils.modelToBean(request,User.class);

        //user.setEmail(request.getEmail());
        //user.setPhone(request.getPhone());
        //user.setKeyField(request.getKeyField());
        //user.setKeyWords(request.getKeyWords());
        return ResponseEntity.ok(BusinessResponse.success(userService.page(page, size, user)));
    }



}
