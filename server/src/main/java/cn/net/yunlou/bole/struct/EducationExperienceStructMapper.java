package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.EducationExperience;
import cn.net.yunlou.bole.model.dto.EducationExperienceDTO;
import cn.net.yunlou.bole.model.query.EducationExperienceQuery;
import org.mapstruct.Mapper;

/**
 * FileName: EducationExperienceStructMapper
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/26 19:15
 * Modified By
 * Modified At
 */
@Mapper(componentModel = "spring")
public interface EducationExperienceStructMapper extends BaseStructMapper<EducationExperience, EducationExperienceDTO, EducationExperienceQuery> {
}
