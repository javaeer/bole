package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import cn.net.yunlou.bole.mapper.EducationExperienceMapper;
import cn.net.yunlou.bole.model.create.EducationExperienceCreate;
import cn.net.yunlou.bole.model.edit.EducationExperienceEdit;
import cn.net.yunlou.bole.model.entity.EducationExperience;
import cn.net.yunlou.bole.model.entity.University;
import cn.net.yunlou.bole.model.query.EducationExperienceQuery;
import cn.net.yunlou.bole.model.view.EducationExperienceView;
import cn.net.yunlou.bole.service.EducationExperienceService;
import cn.net.yunlou.bole.service.UniversityService;
import cn.net.yunlou.bole.struct.EducationExperienceStructMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * FileName: EducationExperienceServiceImpl Description: Created By MR. WANG Created At 2025/11/24
 * 23:40 Modified By Modified At
 */
@Service
@RequiredArgsConstructor
public class EducationExperienceServiceImpl
        extends BaseService<
                EducationExperienceMapper,
                EducationExperience,
                EducationExperienceCreate,
                EducationExperienceView,
                EducationExperienceEdit,
                EducationExperienceQuery,
                EducationExperienceStructMapper>
        implements EducationExperienceService {

    private final UniversityService universityService;

    @Override
    public boolean saveByCreate(EducationExperienceCreate create) {
        EducationExperience entity = structMapper.createToEntity(create);
        entity.setUserId(SecurityContextUtils.getCurrentUserId());
        if (entity.getUniversityId() != null) {
            University university = universityService.getById(entity.getUniversityId());
            if (university != null) {
                entity.setUniversity(university.getName());
            }
        }
        return save(entity);
    }

    @Override
    public QueryWrapper<EducationExperience> getBaseQueryWrapper(EducationExperience entity) {
        QueryWrapper<EducationExperience> queryWrapper = super.getBaseQueryWrapper(entity);
        queryWrapper
                .lambda()
                .eq(EducationExperience::getUserId, SecurityContextUtils.getCurrentUserId());
        queryWrapper.lambda().orderByDesc(EducationExperience::getEndDate);
        return queryWrapper;
    }
}
