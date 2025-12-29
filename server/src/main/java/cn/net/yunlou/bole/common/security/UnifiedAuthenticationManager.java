package cn.net.yunlou.bole.common.security;

import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.common.constant.AuthType;
import cn.net.yunlou.bole.common.constant.UserStatus;
import cn.net.yunlou.bole.config.AppConfigProperties;
import cn.net.yunlou.bole.entity.Email;
import cn.net.yunlou.bole.entity.Sms;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.service.EmailService;
import cn.net.yunlou.bole.service.SmsService;
import cn.net.yunlou.bole.service.UserService;
import cn.net.yunlou.wechat.api.core.Config;
import cn.net.yunlou.wechat.api.core.DefaultConfig;
import cn.net.yunlou.wechat.api.core.entity.WechatSession;
import cn.net.yunlou.wechat.api.core.entity.WechatUserData;
import cn.net.yunlou.wechat.api.service.WechatAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * FileName: UnifiedAuthenticationManager Description:
 *
 * @see UnifiedUserDetailsService#loadUserByUsername(String) Created By laughtiger Created At
 *     2025/12/28 00:12 Modified By Modified At
 */
@Component
@RequiredArgsConstructor
public class UnifiedAuthenticationManager {

    private final AppConfigProperties appConfigProperties;

    private final PasswordEncoder passwordEncoder;

    private final UnifiedUserDetailsService unifiedUserDetailsService;

    private final SmsService smsService;

    private final EmailService emailService;

    private final UserService userService;

    /**
     * 必须事先约定 规则
     *
     * @param authType
     * @param principal username|phone|email|code|
     * @param credentials |password|code|encryptedData
     * @return
     */
    public Authentication authenticate(AuthType authType, String principal, String credentials) {
        try {
            switch (authType) {
                case USERNAME_PASSWORD:
                case PHONE_PASSWORD:
                case EMAIL_PASSWORD:
                    return loginUsePassword(principal, credentials);
                case PHONE_CODE:
                    return loginUsePhoneCode(principal, credentials);
                case UID:
                    return loginUseUid(principal);
                case WECHAT:
                    return loginOrRegisterUseWechatOpenId(principal, credentials);
                case EMAIL_CODE:
                    return loginUseEmailCode(principal, credentials);
                default:
                    throw new IllegalArgumentException();
            }
        } catch (AuthenticationException e) {
            handleAuthenticationException(e);
            return null;
        }
    }

    private Authentication loginUseEmailCode(String principal, String credentials) {
        // 验证邮箱验证码
        Email email =
                Email.builder()
                        .email(principal)
                        .content(credentials)
                        .templateId(2L) // 登录模板
                        .build();

        if (!emailService.verify(email)) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "验证码有误");
        }

        // 加载用户
        UnifiedUserDetails userDetails;
        try {
            userDetails = unifiedUserDetailsService.loadUserByUsername(principal);
        } catch (Exception e) {
            throw new BusinessException(BusinessStatus.NOT_FOUND_RECORD, "账号不存在");
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }

    private Authentication loginOrRegisterUseWechatOpenId(String principal, String credentials) {
        // 微信登录逻辑
        Config config =
                new DefaultConfig.Builder()
                        .appId(appConfigProperties.getWechatApp().getAppId())
                        .appSecret(appConfigProperties.getWechatApp().getAppSecret())
                        .build();

        WechatAuthService wechatAuthService =
                new WechatAuthService.Builder().config(config).build();

        WechatSession wechatSession = wechatAuthService.code2Session(principal);

        if (wechatSession == null || wechatSession.getOpenid() == null) {
            throw new AuthenticationServiceException("微信登录失败");
        }

        String openId = wechatSession.getOpenid();

        // 加载或创建用户
        UnifiedUserDetails unifiedUserDetails;

        try {
            unifiedUserDetails = unifiedUserDetailsService.loadUserByWechatOpenId(openId);
        } catch (UsernameNotFoundException e) {
            // 解密用户信息（如果需要）
            WechatUserData userData = null;
            // if (credentials != null) {
            //    userData =
            //            new WechatDataCryptUtils(
            //                    appConfigProperties.getWechatApp().getAppId(),
            //                    wechatSession.getSessionKey())
            //                    .decrypt(credentials);
            // }

            // 创建用户
            User user =
                    User.builder()
                            .wechatOpenId(wechatSession.getOpenid())
                            .wechatUnionId(wechatSession.getUnionId())
                            .name(userData != null ? userData.getNickName() : "微信用户")
                            .avatar(userData != null ? userData.getAvatarUrl() : null)
                            .gender(userData != null ? userData.getGender() : null)
                            .status(UserStatus.ACTIVE.getValue())
                            .build();

            if (!userService.save(user)) {
                throw new BusinessException(
                        BusinessStatus.INTERNAL_SERVER_CREATE_ERROR, "微信用户注册失败");
            }
            unifiedUserDetails = unifiedUserDetailsService.loadUserById(user.getId());
        }

        return new UsernamePasswordAuthenticationToken(
                unifiedUserDetails, null, unifiedUserDetails.getAuthorities());
    }

    private Authentication loginUseUid(String principal) {
        // 加载用户
        UnifiedUserDetails userDetails =
                unifiedUserDetailsService.loadUserById(Long.valueOf(principal));

        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }

    private Authentication loginUsePhoneCode(String principal, String credentials) {
        // 验证短信验证码
        if (!smsService.verify(
                Sms.builder()
                        .phone(principal)
                        .content(credentials)
                        .templateId(2L) // 登录验证码模板
                        .build())) {
            throw new BadCredentialsException("验证码错误");
        }

        // 加载用户
        UnifiedUserDetails userDetails = unifiedUserDetailsService.loadUserByPhone(principal);

        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }

    private Authentication loginUsePassword(String principal, String credentials) {
        // 加载用户
        UnifiedUserDetails userDetails = unifiedUserDetailsService.loadUserByUsername(principal);

        // 验证密码
        if (!passwordEncoder.matches(credentials, userDetails.getPassword())) {
            throw new BadCredentialsException("密码错误");
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails, credentials, userDetails.getAuthorities());
    }

    private void handleAuthenticationException(AuthenticationException e) {
        if (e instanceof BadCredentialsException) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "账号或密码错误");
        } else if (e instanceof DisabledException) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "账户已被禁用");
        } else if (e instanceof LockedException) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "账户已被锁定");
        } else if (e instanceof AccountExpiredException) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "账户已过期");
        } else if (e instanceof CredentialsExpiredException) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "凭证已过期");
        } else {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, e.getMessage());
        }
    }
}
