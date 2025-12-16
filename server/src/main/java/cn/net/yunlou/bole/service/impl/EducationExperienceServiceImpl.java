package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.entity.EducationExperience;
import cn.net.yunlou.bole.mapper.EducationExperienceMapper;
import cn.net.yunlou.bole.model.create.EducationExperienceCreate;
import cn.net.yunlou.bole.model.edit.EducationExperienceEdit;
import cn.net.yunlou.bole.model.query.EducationExperienceQuery;
import cn.net.yunlou.bole.model.view.EducationExperienceView;
import cn.net.yunlou.bole.service.EducationExperienceService;
import cn.net.yunlou.bole.struct.EducationExperienceStructMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

/**
 * FileName: EducationExperienceServiceImpl Description: Created By MR. WANG Created At 2025/11/24
 * 23:40 Modified By Modified At
 */
@Service
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
    @Override
    public QueryWrapper<EducationExperience> getBaseQueryWrapper(EducationExperience entity) {
        QueryWrapper<EducationExperience> queryWrapper = super.getBaseQueryWrapper(entity);
        queryWrapper.lambda().orderByDesc(EducationExperience::getEndDate);
        return queryWrapper;
    }
}
