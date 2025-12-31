package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.model.entity.MessageTemplate;
import cn.net.yunlou.bole.model.create.MessageTemplateCreate;
import cn.net.yunlou.bole.model.edit.MessageTemplateEdit;
import cn.net.yunlou.bole.model.query.MessageTemplateQuery;
import cn.net.yunlou.bole.model.view.MessageTemplateView;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * FileName: MessageTemplateStructMapper Description: Created By laughtiger Created At 2025/12/25
 * 02:25 Modified By Modified At
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface MessageTemplateStructMapper
        extends BaseStructMapper<
                MessageTemplate,
                MessageTemplateCreate,
                MessageTemplateView,
                MessageTemplateEdit,
                MessageTemplateQuery> {}
