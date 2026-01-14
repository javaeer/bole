package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.model.create.EducationExperienceCreate;
import cn.net.yunlou.bole.model.edit.EducationExperienceEdit;
import cn.net.yunlou.bole.model.entity.EducationExperience;
import cn.net.yunlou.bole.model.query.EducationExperienceQuery;
import cn.net.yunlou.bole.model.view.EducationExperienceView;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * FileName: EducationExperienceStructMapper Description: Created By MR. WANG Created At 2025/11/26
 * 19:15 Modified By Modified At
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface EducationExperienceStructMapper
        extends BaseStructMapper<
                EducationExperience,
                EducationExperienceCreate,
                EducationExperienceView,
                EducationExperienceEdit,
                EducationExperienceQuery> {}
