package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.entity.MessageTemplate;
import cn.net.yunlou.bole.mapper.MessageTemplateMapper;
import cn.net.yunlou.bole.model.create.MessageTemplateCreate;
import cn.net.yunlou.bole.model.edit.MessageTemplateEdit;
import cn.net.yunlou.bole.model.query.MessageTemplateQuery;
import cn.net.yunlou.bole.model.view.MessageTemplateView;
import cn.net.yunlou.bole.service.MessageTemplateService;
import cn.net.yunlou.bole.struct.MessageTemplateStructMapper;
import org.springframework.stereotype.Service;

/**
 * FileName: MessageTemplateServiceImpl
 * Description:
 * Created By laughtiger
 * Created At 2025/12/25 02:23
 * Modified By
 * Modified At
 */
@Service
public class MessageTemplateServiceImpl extends BaseService<
        MessageTemplateMapper,
        MessageTemplate,
        MessageTemplateCreate,
        MessageTemplateView,
        MessageTemplateEdit,
        MessageTemplateQuery,
        MessageTemplateStructMapper
        > implements MessageTemplateService {
}
