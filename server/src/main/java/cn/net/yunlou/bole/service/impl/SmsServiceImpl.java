package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.common.MessageEntity;
import cn.net.yunlou.bole.common.constant.MessageSendType;
import cn.net.yunlou.bole.common.utils.NumberUtils;
import cn.net.yunlou.bole.common.utils.RedisCacheUtils;
import cn.net.yunlou.bole.common.utils.StringUtils;
import cn.net.yunlou.bole.entity.MessageTemplate;
import cn.net.yunlou.bole.entity.Sms;
import cn.net.yunlou.bole.handler.MessageSendStrategyFactory;
import cn.net.yunlou.bole.mapper.SmsMapper;
import cn.net.yunlou.bole.model.create.SmsCreate;
import cn.net.yunlou.bole.model.edit.SmsEdit;
import cn.net.yunlou.bole.model.query.SmsQuery;
import cn.net.yunlou.bole.model.view.SmsView;
import cn.net.yunlou.bole.service.MessageTemplateService;
import cn.net.yunlou.bole.service.SmsService;
import cn.net.yunlou.bole.struct.SmsStructMapper;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * FileName: SmsServiceImpl Description: Created By laughtiger Created At 2025/12/25 01:22 Modified
 * By Modified At
 */
@Service
@RequiredArgsConstructor
public class SmsServiceImpl
        extends BaseService<SmsMapper, Sms, SmsCreate, SmsView, SmsEdit, SmsQuery, SmsStructMapper>
        implements SmsService {

    private final MessageSendStrategyFactory messageSendStrategyFactory;

    private final MessageTemplateService messageTemplateService;

    private final RedisCacheUtils redisCacheUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean send(SmsCreate create) {

        Sms generated = generate(create);

        String phoneNumber = completePhoneNumber(create.getAreaCode(), create.getPhone());

        boolean send =
                messageSendStrategyFactory
                        .getMessageSendStrategy(MessageSendType.YP_SMS)
                        .send(
                                MessageEntity.builder()
                                        .to(phoneNumber)
                                        .subject(generated.getTemplate().getSubject())
                                        .text(generated.getText())
                                        .build());

        if (send) {
            redisCacheUtils.putObject(
                    Sms.SMS_CACHE_KEY + phoneNumber,
                    generated.getContent(),
                    generated.getTemplate().getDuration(),
                    TimeUnit.MINUTES);
            generated.setState(1);
        }

        return save(generated);
    }

    @Override
    public boolean verify(Sms entity) {
        String phoneNumber = completePhoneNumber(entity.getAreaCode(), entity.getPhone());
        String code = redisCacheUtils.getObject(Sms.SMS_CACHE_KEY + phoneNumber, String.class);
        if (ObjectUtils.isNotEmpty(code) && Objects.equals(entity.getContent(), code)) {
            redisCacheUtils.delete(Sms.SMS_CACHE_KEY + phoneNumber);
            return true;
        }
        return false;
    }

    private String completePhoneNumber(String areaCode, String phone) {
        if (areaCode.equalsIgnoreCase("86")) {
            return phone;
        }
        return "+" + areaCode + phone;
    }

    private Sms generate(SmsCreate create) {

        Sms entity = structMapper.createToEntity(create);

        MessageTemplate template = messageTemplateService.getById(create.getTemplateId());
        if (ObjectUtils.isEmpty(template)) {
            throw new BusinessException(BusinessStatus.GONE);
        }

        entity.setTemplate(template);

        String code = NumberUtils.randomNums(template.getLength());

        entity.setContent(code);

        HashMap<String, Object> params = Maps.newHashMap();
        params.put("code", code);
        params.put("minutes", template.getDuration());

        String validateCodeSmsText = template.getTemplate();

        String text = StringUtils.replaceMaps(validateCodeSmsText, params);
        entity.setText(text);

        return entity;
    }
}
