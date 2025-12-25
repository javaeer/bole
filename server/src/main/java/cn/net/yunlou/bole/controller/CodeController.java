package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.handler.MessageSendStrategyFactory;
import cn.net.yunlou.bole.model.create.EmailCreate;
import cn.net.yunlou.bole.model.create.SmsCreate;
import cn.net.yunlou.bole.service.EmailService;
import cn.net.yunlou.bole.service.SmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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


    @PostMapping("send-sms")
    @Operation(summary = "发送短信")
    public BusinessResponse<Boolean> sms(@Valid @RequestBody SmsCreate create) {
        return BusinessResponse.success(smsService.send(create));
    }


    @PostMapping("send-email")
    @Operation(summary = "发送邮件")
    public BusinessResponse<Boolean> email(@Valid @RequestBody EmailCreate create) {
        return BusinessResponse.success(emailService.send(create));
    }

}
