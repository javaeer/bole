package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.common.IEnum;
import cn.net.yunlou.bole.common.constant.AuthType;
import cn.net.yunlou.bole.common.constant.IdentifierType;
import cn.net.yunlou.bole.common.constant.UserStatus;
import cn.net.yunlou.bole.common.security.AuthenticationService;
import cn.net.yunlou.bole.common.security.UnifiedAuthenticationManager;
import cn.net.yunlou.bole.common.security.UnifiedUserDetails;
import cn.net.yunlou.bole.model.entity.Email;
import cn.net.yunlou.bole.model.entity.Sms;
import cn.net.yunlou.bole.model.entity.User;
import cn.net.yunlou.bole.model.*;
import cn.net.yunlou.bole.service.AuthService;
import cn.net.yunlou.bole.service.EmailService;
import cn.net.yunlou.bole.service.SmsService;
import cn.net.yunlou.bole.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final SmsService smsService;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationService authenticationService;

    private final UnifiedAuthenticationManager unifiedAuthenticationManager;

    @Override
    public AccessTokenDTO login(@Valid LoginDTO request) {

        Authentication authentication =
                unifiedAuthenticationManager.authenticate(
                        AuthType.USERNAME_PASSWORD, request.getUsername(), request.getPassword());

        UnifiedUserDetails userDetails = (UnifiedUserDetails) authentication.getPrincipal();

        User user = userDetails.getUser();

        return authenticationService.login(user);
    }

    @Override
    public AccessTokenDTO smsLogin(SmsLoginDTO request) {

        Authentication authentication =
                unifiedAuthenticationManager.authenticate(
                        AuthType.PHONE_CODE, request.getPhone(), request.getCode());

        UnifiedUserDetails userDetails = (UnifiedUserDetails) authentication.getPrincipal();

        User user = userDetails.getUser();

        return authenticationService.login(user);
    }

    @Override
    public AccessTokenDTO wechatLogin(WechatLoginDTO request) {

        Authentication authentication =
                unifiedAuthenticationManager.authenticate(AuthType.WECHAT, request.getCode(), null);

        UnifiedUserDetails userDetails = (UnifiedUserDetails) authentication.getPrincipal();

        User user = userDetails.getUser();

        return authenticationService.login(user);
    }

    private AccessTokenDTO uidLogin(Long id) {

        Authentication authentication =
                unifiedAuthenticationManager.authenticate(AuthType.UID, String.valueOf(id), null);

        UnifiedUserDetails userDetails = (UnifiedUserDetails) authentication.getPrincipal();

        User user = userDetails.getUser();

        return authenticationService.login(user);
    }

    @Override
    public AccessTokenDTO register(@Valid RegisterDTO register) {
        // 检查用户名是否已存在
        if (userService.existsByUsername(register.getUsername())) {
            throw new BusinessException(BusinessStatus.ALREADY_EXISTS, "用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(register.getUsername());
        user.setPassword(passwordEncoder.encode(register.getPassword()));
        user.setEmail(register.getEmail());
        user.setStatus(UserStatus.ACTIVE.getValue());

        if (!userService.save(user)) {
            throw new BusinessException(BusinessStatus.GONE_DATA_INVALID, "注册失败,请稍后");
        }

        return uidLogin(user.getId());
    }

    @Override
    public AccessTokenDTO registerPhone(RegisterPhoneDTO request) {

        Sms sms =
                Sms.builder()
                        .phone(request.getPhone())
                        .content(request.getCode())
                        .templateId(1L)
                        .build();
        if (!smsService.verify(sms)) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "验证码有误");
        }

        // 检查手机号是否已存在
        if (userService.existsByPhone(request.getPhone())) {
            throw new BusinessException(BusinessStatus.ALREADY_EXISTS, "手机号已存在");
        }

        // 创建用户
        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.ACTIVE.getValue());
        user.setFollowers(0);
        user.setFans(0);
        user.setLikes(0);

        if (!userService.save(user)) {
            throw new BusinessException(BusinessStatus.GONE_DATA_INVALID, "注册失败,请稍后");
        }

        return uidLogin(user.getId());
    }

    @Override
    public AccessTokenDTO registerEmail(RegisterEmailDTO request) {

        Email email =
                Email.builder()
                        .email(request.getEmail())
                        .content(request.getCode())
                        .templateId(1L)
                        .build();
        if (!emailService.verify(email)) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "验证码有误");
        }

        // 检查邮箱是否已存在
        if (userService.existsByEmail(request.getEmail())) {
            throw new BusinessException(BusinessStatus.ALREADY_EXISTS, "邮箱已存在");
        }

        // 创建用户
        User user = new User();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setStatus(UserStatus.ACTIVE.getValue());
        user.setFollowers(0);
        user.setFans(0);
        user.setLikes(0);

        if (!userService.save(user)) {
            throw new BusinessException(BusinessStatus.GONE_DATA_INVALID, "注册失败,请稍后");
        }

        return uidLogin(user.getId());
    }

    @Override
    public RefreshTokenViewDTO refreshToken(String refreshToken) {
        return authenticationService.refreshToken(refreshToken);
    }

    @Override
    public void logout() {
        authenticationService.logout();
    }

    @Override
    public void closure() {
        authenticationService.closure();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changePassword(@Valid ChangePasswordDTO request) {

        // (SecurityContextUtils.getCurrentUserId()).build();

        return false;
    }

    @Override
    public AccessTokenDTO resetPassword(@Valid ResetPasswordDTO request) {

        // 1.验证短信、邮箱验证码
        IdentifierType type = IEnum.valueOf(request.getType(), IdentifierType.class);
        switch (type) {
            case PHONE -> {
                if (!smsService.verify(
                        Sms.builder()
                                .phone(request.getUsername())
                                .content(request.getCode())
                                .templateId(3L)
                                .build())) {
                    throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "验证码有误");
                }
            }
            case EMAIL -> {
                if (!emailService.verify(
                        Email.builder()
                                .email(request.getUsername())
                                .content(request.getCode())
                                .templateId(3L)
                                .build())) {
                    throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "验证码有误");
                }
            }
        }

        // 2.重置密码 到用户新密码
        User user = userService.findByUsername(request.getUsername());
        if (ObjectUtils.isNotEmpty(user)) {
            User entity = User.builder().password(request.getNewPassword()).build();
            entity.setId(user.getId());
            if (userService.updateById(entity)) {
                return uidLogin(user.getId());
            }
        }

        return null;
    }
}
