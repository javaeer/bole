package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.model.create.MessageTemplateCreate;
import cn.net.yunlou.bole.model.edit.MessageTemplateEdit;
import cn.net.yunlou.bole.model.entity.MessageTemplate;
import cn.net.yunlou.bole.model.query.MessageTemplateQuery;
import cn.net.yunlou.bole.model.view.MessageTemplateView;

/**
 * FileName: MessageTemplateService Description: Created By laughtiger Created At 2025/12/25 02:19
 * Modified By Modified At
 */
public interface MessageTemplateService
        extends IBaseService<
                MessageTemplate,
                MessageTemplateCreate,
                MessageTemplateView,
                MessageTemplateEdit,
                MessageTemplateQuery> {}
