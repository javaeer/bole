package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.Skill;
import cn.net.yunlou.bole.model.dto.SkillDTO;
import cn.net.yunlou.bole.model.query.SkillQuery;
import org.mapstruct.Mapper;

/**
 * FileName: SkillStructMapper Description: Created By MR. WANG Created At 2025/11/26 19:16 Modified
 * By Modified At
 */
@Mapper(componentModel = "spring")
public interface SkillStructMapper extends BaseStructMapper<Skill, SkillDTO, SkillQuery> {}
