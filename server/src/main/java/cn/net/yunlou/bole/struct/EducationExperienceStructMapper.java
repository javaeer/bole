package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.EducationExperience;
import cn.net.yunlou.bole.model.EducationExperienceCreate;
import cn.net.yunlou.bole.model.EducationExperienceEdit;
import cn.net.yunlou.bole.model.EducationExperienceQuery;
import cn.net.yunlou.bole.model.EducationExperienceView;
import org.mapstruct.Mapper;

/**
 * FileName: EducationExperienceStructMapper Description: Created By MR. WANG Created At 2025/11/26
 * 19:15 Modified By Modified At
 */
@Mapper(componentModel = "spring")
public interface EducationExperienceStructMapper
        extends BaseStructMapper<
                EducationExperience,
                EducationExperienceCreate,
                EducationExperienceView,
                EducationExperienceEdit,
                EducationExperienceQuery> {}
