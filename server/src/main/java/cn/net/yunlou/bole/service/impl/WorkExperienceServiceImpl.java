package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import cn.net.yunlou.bole.mapper.WorkExperienceMapper;
import cn.net.yunlou.bole.model.create.WorkExperienceCreate;
import cn.net.yunlou.bole.model.edit.WorkExperienceEdit;
import cn.net.yunlou.bole.model.entity.WorkExperience;
import cn.net.yunlou.bole.model.query.WorkExperienceQuery;
import cn.net.yunlou.bole.model.view.WorkExperienceView;
import cn.net.yunlou.bole.service.WorkExperienceService;
import cn.net.yunlou.bole.struct.WorkExperienceStructMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * FileName: WorkExperienceServiceImpl Description: Created By MR. WANG Created At 2025/11/25 00:05
 * Modified By Modified At
 */
@Service
public class WorkExperienceServiceImpl
        extends BaseService<
                WorkExperienceMapper,
                WorkExperience,
                WorkExperienceCreate,
                WorkExperienceView,
                WorkExperienceEdit,
                WorkExperienceQuery,
                WorkExperienceStructMapper>
        implements WorkExperienceService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveByCreate(WorkExperienceCreate create) {
        WorkExperience entity = structMapper.createToEntity(create);
        entity.setUserId(SecurityContextUtils.getCurrentUserId());
        return save(entity);
    }

    @Override
    public QueryWrapper<WorkExperience> getBaseQueryWrapper(WorkExperience entity) {
        QueryWrapper<WorkExperience> queryWrapper = super.getBaseQueryWrapper(entity);
        queryWrapper
                .lambda()
                .eq(WorkExperience::getUserId, SecurityContextUtils.getCurrentUserId());
        queryWrapper.lambda().orderByDesc(WorkExperience::getStartDate);
        return queryWrapper;
    }
}
