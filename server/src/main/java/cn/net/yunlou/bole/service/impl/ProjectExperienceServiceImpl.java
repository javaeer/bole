package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.entity.ProjectExperience;
import cn.net.yunlou.bole.mapper.ProjectExperienceMapper;
import cn.net.yunlou.bole.model.ProjectExperienceCreate;
import cn.net.yunlou.bole.model.ProjectExperienceEdit;
import cn.net.yunlou.bole.model.ProjectExperienceQuery;
import cn.net.yunlou.bole.model.ProjectExperienceView;
import cn.net.yunlou.bole.service.ProjectExperienceService;
import cn.net.yunlou.bole.struct.ProjectExperienceStructMapper;
import org.springframework.stereotype.Service;

/**
 * FileName: ProjectExperienceServiceImpl Description: Created By MR. WANG Created At 2025/11/24
 * 23:48 Modified By Modified At
 */
@Service
public class ProjectExperienceServiceImpl
        extends BaseService<
                ProjectExperienceMapper,
                ProjectExperience,
                ProjectExperienceCreate,
                ProjectExperienceView,
                ProjectExperienceEdit,
                ProjectExperienceQuery,
                ProjectExperienceStructMapper>
        implements ProjectExperienceService {}
