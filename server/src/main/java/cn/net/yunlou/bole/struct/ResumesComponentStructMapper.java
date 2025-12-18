package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.ResumesComponent;
import cn.net.yunlou.bole.model.create.ResumesComponentCreate;
import cn.net.yunlou.bole.model.edit.ResumesComponentEdit;
import cn.net.yunlou.bole.model.query.ResumesComponentQuery;
import cn.net.yunlou.bole.model.view.ResumesComponentView;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * FileName: ResumesComponentStructMapper Description: Created By laughtiger Created At 2025/12/17
 * 16:26 Modified By Modified At
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ResumesComponentStructMapper
        extends BaseStructMapper<
                ResumesComponent,
                ResumesComponentCreate,
                ResumesComponentView,
                ResumesComponentEdit,
                ResumesComponentQuery> {}
