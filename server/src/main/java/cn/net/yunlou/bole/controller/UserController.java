package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.common.IEnum;
import cn.net.yunlou.bole.common.constant.UserKeyField;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import cn.net.yunlou.bole.model.entity.Email;
import cn.net.yunlou.bole.model.entity.Sms;
import cn.net.yunlou.bole.model.entity.User;
import cn.net.yunlou.bole.model.BindEmailDTO;
import cn.net.yunlou.bole.model.BindPhoneDTO;
import cn.net.yunlou.bole.model.ProfileDTO;
import cn.net.yunlou.bole.model.edit.UserEdit;
import cn.net.yunlou.bole.model.query.UserQuery;
import cn.net.yunlou.bole.model.view.UserView;
import cn.net.yunlou.bole.service.EmailService;
import cn.net.yunlou.bole.service.SmsService;
import cn.net.yunlou.bole.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@Tag(name = "02.用户管理", description = "用户相关接口")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final SmsService smsService;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    @GetMapping("profile")
    @Operation(summary = "获取当前用户信息")
    public BusinessResponse<User> getCurrentUser() {
        // 从 SecurityContext 获取当前用户
        Long currentUserId = SecurityContextUtils.getCurrentUserId();
        User user = userService.getById(currentUserId);
        return BusinessResponse.success(user);
    }

    @GetMapping("keyField")
    @Operation(summary = "获取查询关键字")
    public BusinessResponse<List<IEnum.EnumItem<?>>> getKeyField() {
        // 从 SecurityContext 获取当前用户
        List<IEnum.EnumItem<?>> enumItems = IEnum.toItemList(UserKeyField.class);
        return BusinessResponse.success(enumItems);
    }

    @PutMapping("profile")
    @Operation(summary = "更新用户信息")
    public BusinessResponse<UserView> updateUser(@Valid @RequestBody ProfileDTO request) {
        return BusinessResponse.success(userService.updateProfile(request));
    }

    @PutMapping("bind/phone")
    @Operation(summary = "绑定手机号")
    public BusinessResponse<UserView> bindPhone(@Valid @RequestBody BindPhoneDTO request) {

        // 1.检查是否已被绑定
        if (userService.existsByPhone(request.getPhone())) {
            throw new BusinessException(BusinessStatus.ALREADY_EXISTS);
        }

        Long currentUserId = SecurityContextUtils.getCurrentUserId();
        User user = userService.getById(currentUserId);
        // 2.检查密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "密码有误");
        }

        // 3.检查验证码
        if (!smsService.verify(Sms.builder().phone(request.getPhone()).templateId(4L).build())) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "验证码有误，请重新输入");
        }
        user.setPhone(request.getPhone());

        if (!userService.updateById(user)) {
            throw new BusinessException(BusinessStatus.INTERNAL_SERVER_UPDATE_ERROR, "绑定失败");
        }

        return BusinessResponse.success(userService.getViewById(currentUserId));
    }

    @PutMapping("bind/email")
    @Operation(summary = "绑定邮箱")
    public BusinessResponse<UserView> bindEmail(@Valid @RequestBody BindEmailDTO request) {

        // 1.检查是否已被绑定
        if (userService.existsByEmail(request.getEmail())) {
            throw new BusinessException(BusinessStatus.ALREADY_EXISTS);
        }
        Long currentUserId = SecurityContextUtils.getCurrentUserId();
        User user = userService.getById(currentUserId);
        // 2.检查密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "密码有误");
        }
        // 3.检查验证码
        if (!emailService.verify(
                Email.builder().email(request.getEmail()).templateId(4L).build())) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "验证码有误，请重新输入");
        }

        user.setEmail(request.getEmail());

        if (!userService.updateById(user)) {
            throw new BusinessException(BusinessStatus.INTERNAL_SERVER_UPDATE_ERROR, "绑定失败");
        }

        return BusinessResponse.success(userService.getViewById(currentUserId));
    }

    @PutMapping("unbind/phone")
    @Operation(summary = "解绑手机号")
    public BusinessResponse<UserView> unbindPhone(@Valid @RequestBody BindPhoneDTO request) {

        Long currentUserId = SecurityContextUtils.getCurrentUserId();
        User user = userService.getById(currentUserId);

        // 1.检查是否绑定
        if (ObjectUtils.isEmpty(user.getPhone())) {
            throw new BusinessException(BusinessStatus.NOT_FOUND_RECORD, "未绑定");
        }

        // 2.检查密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "密码有误");
        }
        // 3.检查验证码
        if (!smsService.verify(Sms.builder().phone(request.getPhone()).templateId(5L).build())) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "验证码有误，请重新输入");
        }

        if (!userService.updateById(user)) {
            throw new BusinessException(BusinessStatus.INTERNAL_SERVER_UPDATE_ERROR, "解绑失败");
        }

        return BusinessResponse.success(userService.getViewById(currentUserId));
    }

    @PutMapping("unbind/email")
    @Operation(summary = "解绑邮箱")
    public BusinessResponse<UserView> unbindEmail(@Valid @RequestBody BindEmailDTO request) {

        Long currentUserId = SecurityContextUtils.getCurrentUserId();
        User user = userService.getById(currentUserId);

        // 1.检查是否绑定
        if (ObjectUtils.isEmpty(user.getEmail())) {
            throw new BusinessException(BusinessStatus.NOT_FOUND_RECORD, "未绑定");
        }

        // 2.检查密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "密码有误");
        }
        // 3.检查验证码
        if (!emailService.verify(
                Email.builder().email(request.getEmail()).templateId(5L).build())) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "验证码有误，请重新输入");
        }

        user.setEmail(null);

        if (!userService.updateById(user)) {
            throw new BusinessException(BusinessStatus.INTERNAL_SERVER_UPDATE_ERROR, "解绑失败");
        }

        return BusinessResponse.success(userService.getViewById(currentUserId));
    }

    @PutMapping("edit")
    @Operation(summary = "更新用户信息")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> updateUser(@Valid @RequestBody UserEdit request) {
        return BusinessResponse.success(userService.updateByEdit(request));
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
