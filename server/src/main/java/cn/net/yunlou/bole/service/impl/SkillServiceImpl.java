package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import cn.net.yunlou.bole.mapper.SkillMapper;
import cn.net.yunlou.bole.model.create.SkillCreate;
import cn.net.yunlou.bole.model.edit.SkillEdit;
import cn.net.yunlou.bole.model.entity.Skill;
import cn.net.yunlou.bole.model.query.SkillQuery;
import cn.net.yunlou.bole.model.view.SkillView;
import cn.net.yunlou.bole.service.SkillService;
import cn.net.yunlou.bole.struct.SkillStructMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * FileName: SkillServiceImpl Description: Created By MR. WANG Created At 2025/11/25 00:03 Modified
 * By Modified At
 */
@Service
public class SkillServiceImpl
        extends BaseService<
        SkillMapper,
        Skill,
        SkillCreate,
        SkillView,
        SkillEdit,
        SkillQuery,
        SkillStructMapper>
        implements SkillService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveByCreate(SkillCreate create) {
        Skill entity = structMapper.createToEntity(create);
        entity.setUserId(SecurityContextUtils.getCurrentUserId());
        return save(entity);
    }

    @Override
    public QueryWrapper<Skill> getBaseQueryWrapper(Skill entity) {
        QueryWrapper<Skill> queryWrapper = super.getBaseQueryWrapper(entity);
        queryWrapper.lambda().eq(Skill::getUserId, SecurityContextUtils.getCurrentUserId());
        return queryWrapper;
    }
}
