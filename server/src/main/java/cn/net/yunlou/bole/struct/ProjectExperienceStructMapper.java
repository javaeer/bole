package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.ProjectExperience;
import cn.net.yunlou.bole.model.dto.ProjectExperienceDTO;
import cn.net.yunlou.bole.model.query.ProjectExperienceQuery;
import org.mapstruct.Mapper;

/**
 * FileName: ProjectExperienceStructMapper
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/26 19:15
 * Modified By
 * Modified At
 */
@Mapper(componentModel = "spring")
public interface ProjectExperienceStructMapper extends BaseStructMapper<ProjectExperience, ProjectExperienceDTO, ProjectExperienceQuery> {
}
