package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.model.entity.University;
import cn.net.yunlou.bole.model.create.UniversityCreate;
import cn.net.yunlou.bole.model.edit.UniversityEdit;
import cn.net.yunlou.bole.model.query.UniversityQuery;
import cn.net.yunlou.bole.model.view.UniversityView;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * FileName: UniversityStructMapper Description: Created By laughtiger Created At 2025/12/30 17:51
 * Modified By Modified At
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface UniversityStructMapper
        extends BaseStructMapper<
                University, UniversityCreate, UniversityView, UniversityEdit, UniversityQuery> {}
