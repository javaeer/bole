package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.common.MessageEntity;
import cn.net.yunlou.bole.common.constant.MessageSendType;
import cn.net.yunlou.bole.common.utils.NumberUtils;
import cn.net.yunlou.bole.common.utils.RedisCacheUtils;
import cn.net.yunlou.bole.common.utils.StringUtils;
import cn.net.yunlou.bole.entity.Email;
import cn.net.yunlou.bole.entity.MessageTemplate;
import cn.net.yunlou.bole.handler.MessageSendStrategyFactory;
import cn.net.yunlou.bole.mapper.EmailMapper;
import cn.net.yunlou.bole.model.create.EmailCreate;
import cn.net.yunlou.bole.model.edit.EmailEdit;
import cn.net.yunlou.bole.model.query.EmailQuery;
import cn.net.yunlou.bole.model.view.EmailView;
import cn.net.yunlou.bole.service.EmailService;
import cn.net.yunlou.bole.service.MessageTemplateService;
import cn.net.yunlou.bole.struct.EmailStructMapper;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * FileName: EmailServiceImpl Description: Created By laughtiger Created At 2025/12/25 01:22
 * Modified By Modified At
 */
@Service
@RequiredArgsConstructor
public class EmailServiceImpl
        extends BaseService<
                EmailMapper,
                Email,
                EmailCreate,
                EmailView,
                EmailEdit,
                EmailQuery,
                EmailStructMapper>
        implements EmailService {

    private final MessageSendStrategyFactory messageSendStrategyFactory;

    private final MessageTemplateService messageTemplateService;

    private final RedisCacheUtils redisCacheUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean send(EmailCreate create) {

        Email generated = generate(create);

        boolean send =
                messageSendStrategyFactory
                        .getMessageSendStrategy(MessageSendType.EMAIL)
                        .send(
                                MessageEntity.builder()
                                        .to(create.getEmail())
                                        .subject(generated.getTemplate().getSubject())
                                        .text(generated.getText())
                                        .build());

        if (send) {
            redisCacheUtils.putObject(
                    Email.EMAIL_CACHE_KEY + create.getTemplateId() + ":" + create.getEmail(),
                    generated.getContent(),
                    generated.getTemplate().getDuration(),
                    TimeUnit.MINUTES);
            generated.setState(1);
        }

        return save(generated);
    }

    @Override
    public boolean verify(Email entity) {
        String email = entity.getEmail();
        String code =
                redisCacheUtils.getObject(
                        Email.EMAIL_CACHE_KEY + entity.getTemplateId() + ":" + email, String.class);
        if (ObjectUtils.isNotEmpty(code) && Objects.equals(entity.getContent(), code)) {
            redisCacheUtils.delete(Email.EMAIL_CACHE_KEY + entity.getTemplateId() + ":" + email);
            return true;
        }
        return false;
    }

    private Email generate(EmailCreate create) {

        Email entity = structMapper.createToEntity(create);

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
