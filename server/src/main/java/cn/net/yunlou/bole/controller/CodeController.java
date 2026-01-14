package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import cn.net.yunlou.bole.model.create.EmailCreate;
import cn.net.yunlou.bole.model.create.SmsCreate;
import cn.net.yunlou.bole.model.entity.User;
import cn.net.yunlou.bole.service.EmailService;
import cn.net.yunlou.bole.service.SmsService;
import cn.net.yunlou.bole.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("code")
@Tag(name = "24.验证码管理", description = "验证码相关接口")
@RequiredArgsConstructor
public class CodeController {

    private final SmsService smsService;

    private final EmailService emailService;

    private final UserService userService;

    @PostMapping("send/sms")
    @Operation(summary = "发送短信")
    public BusinessResponse<Boolean> sms(@Valid @RequestBody SmsCreate create) {
        return BusinessResponse.success(smsService.send(create));
    }

    @PostMapping("send/email")
    @Operation(summary = "发送邮件")
    public BusinessResponse<Boolean> email(@Valid @RequestBody EmailCreate create) {
        return BusinessResponse.success(emailService.send(create));
    }

    @PostMapping("unbind/phone/send")
    @Operation(summary = "发送解绑短信")
    public BusinessResponse<Boolean> unbindPhone() {

        User user = userService.getById(SecurityContextUtils.getCurrentUserId());
        if (ObjectUtils.isEmpty(user) || ObjectUtils.isEmpty(user.getPhone())) {
            throw new BusinessException(BusinessStatus.NOT_FOUND_RECORD);
        }

        return BusinessResponse.success(
                smsService.send(SmsCreate.builder().phone(user.getPhone()).templateId(5L).build()));
    }

    @PostMapping("unbind/email/send")
    @Operation(summary = "发送解绑邮件")
    public BusinessResponse<Boolean> unbindEmail() {

        User user = userService.getById(SecurityContextUtils.getCurrentUserId());
        if (ObjectUtils.isEmpty(user) || ObjectUtils.isEmpty(user.getPhone())) {
            throw new BusinessException(BusinessStatus.NOT_FOUND_RECORD);
        }
        return BusinessResponse.success(
                emailService.send(
                        EmailCreate.builder().email(user.getEmail()).templateId(5L).build()));
    }
}
