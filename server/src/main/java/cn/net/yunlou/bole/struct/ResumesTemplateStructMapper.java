package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.ResumesTemplate;
import cn.net.yunlou.bole.model.ResumesTemplateCreate;
import cn.net.yunlou.bole.model.ResumesTemplateEdit;
import cn.net.yunlou.bole.model.ResumesTemplateQuery;
import cn.net.yunlou.bole.model.ResumesTemplateView;
import org.mapstruct.Mapper;

/**
 * FileName: ResumesTemplateStructMapper Description: Created By MR. WANG Created At 2025/11/26
 * 19:16 Modified By Modified At
 */
@Mapper(componentModel = "spring")
public interface ResumesTemplateStructMapper
        extends BaseStructMapper<
                ResumesTemplate,
                ResumesTemplateCreate,
                ResumesTemplateView,
                ResumesTemplateEdit,
                ResumesTemplateQuery> {}
