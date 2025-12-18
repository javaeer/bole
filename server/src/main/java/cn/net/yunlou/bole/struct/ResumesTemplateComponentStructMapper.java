package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.model.create.ResumesTemplateComponentCreate;
import cn.net.yunlou.bole.model.edit.ResumesTemplateComponentEdit;
import cn.net.yunlou.bole.model.query.ResumesTemplateComponentQuery;
import cn.net.yunlou.bole.model.view.ResumesTemplateComponentView;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * FileName: ResumesTemplateComponentStructMapper Description: Created By laughtiger Created At
 * 2025/12/18 22:02 Modified By Modified At
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ResumesTemplateComponentStructMapper
        extends BaseStructMapper<
                ResumesTemplateComponent,
                ResumesTemplateComponentCreate,
                ResumesTemplateComponentView,
                ResumesTemplateComponentEdit,
                ResumesTemplateComponentQuery> {}
