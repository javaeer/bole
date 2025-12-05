package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.WorkExperience;
import cn.net.yunlou.bole.model.WorkExperienceCreate;
import cn.net.yunlou.bole.model.WorkExperienceEdit;
import cn.net.yunlou.bole.model.WorkExperienceQuery;
import cn.net.yunlou.bole.model.WorkExperienceView;
import org.mapstruct.Mapper;

/**
 * FileName: WorkExperienceStructMapper Description: Created By MR. WANG Created At 2025/11/26 19:09
 * Modified By Modified At
 */
@Mapper(componentModel = "spring")
public interface WorkExperienceStructMapper
        extends BaseStructMapper<
                WorkExperience,
                WorkExperienceCreate,
                WorkExperienceView,
                WorkExperienceEdit,
                WorkExperienceQuery> {}
