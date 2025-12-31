package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import cn.net.yunlou.bole.mapper.ProjectExperienceMapper;
import cn.net.yunlou.bole.model.create.ProjectExperienceCreate;
import cn.net.yunlou.bole.model.edit.ProjectExperienceEdit;
import cn.net.yunlou.bole.model.entity.ProjectExperience;
import cn.net.yunlou.bole.model.query.ProjectExperienceQuery;
import cn.net.yunlou.bole.model.view.ProjectExperienceView;
import cn.net.yunlou.bole.service.ProjectExperienceService;
import cn.net.yunlou.bole.struct.ProjectExperienceStructMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @Transactional(rollbackFor = Exception.class)
    public boolean saveByCreate(ProjectExperienceCreate create) {
        ProjectExperience entity = structMapper.createToEntity(create);
        entity.setUserId(SecurityContextUtils.getCurrentUserId());
        return save(entity);
    }

    @Override
    public QueryWrapper<ProjectExperience> getBaseQueryWrapper(ProjectExperience entity) {
        QueryWrapper<ProjectExperience> queryWrapper = super.getBaseQueryWrapper(entity);
        queryWrapper.lambda().eq(ProjectExperience::getUserId, SecurityContextUtils.getCurrentUserId());
        queryWrapper.lambda().orderByDesc(ProjectExperience::getStartDate);
        return queryWrapper;
    }
}
