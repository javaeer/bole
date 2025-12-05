package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.Skill;
import cn.net.yunlou.bole.model.SkillCreate;
import cn.net.yunlou.bole.model.SkillEdit;
import cn.net.yunlou.bole.model.SkillQuery;
import cn.net.yunlou.bole.model.SkillView;
import org.mapstruct.Mapper;

/**
 * FileName: SkillStructMapper Description: Created By MR. WANG Created At 2025/11/26 19:16 Modified
 * By Modified At
 */
@Mapper(componentModel = "spring")
public interface SkillStructMapper
        extends BaseStructMapper<Skill, SkillCreate, SkillView, SkillEdit, SkillQuery> {}
