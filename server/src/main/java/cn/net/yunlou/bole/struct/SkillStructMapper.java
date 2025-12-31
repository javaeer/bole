package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.model.entity.Skill;
import cn.net.yunlou.bole.model.create.SkillCreate;
import cn.net.yunlou.bole.model.edit.SkillEdit;
import cn.net.yunlou.bole.model.query.SkillQuery;
import cn.net.yunlou.bole.model.view.SkillView;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * FileName: SkillStructMapper Description: Created By MR. WANG Created At 2025/11/26 19:16 Modified
 * By Modified At
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface SkillStructMapper
        extends BaseStructMapper<Skill, SkillCreate, SkillView, SkillEdit, SkillQuery> {}
