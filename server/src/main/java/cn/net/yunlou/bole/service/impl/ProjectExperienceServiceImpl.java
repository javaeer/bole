package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.entity.ProjectExperience;
import cn.net.yunlou.bole.mapper.ProjectExperienceMapper;
import cn.net.yunlou.bole.model.create.ProjectExperienceCreate;
import cn.net.yunlou.bole.model.edit.ProjectExperienceEdit;
import cn.net.yunlou.bole.model.query.ProjectExperienceQuery;
import cn.net.yunlou.bole.model.view.ProjectExperienceView;
import cn.net.yunlou.bole.service.ProjectExperienceService;
import cn.net.yunlou.bole.struct.ProjectExperienceStructMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;
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
        implements ProjectExperienceService {
    @Override
    public List<ProjectExperience> listByUserId(Long userId) {

        ProjectExperience projectExperience = ProjectExperience.builder().userId(userId).build();

        return list(projectExperience);
    }

    @Override
    public QueryWrapper<ProjectExperience> getBaseQueryWrapper(ProjectExperience entity) {
        QueryWrapper<ProjectExperience> queryWrapper = super.getBaseQueryWrapper(entity);
        queryWrapper.lambda().orderByDesc(ProjectExperience::getStartDate);
        return queryWrapper;
    }
}
