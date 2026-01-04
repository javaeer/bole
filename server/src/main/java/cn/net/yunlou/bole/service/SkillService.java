package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.model.create.SkillCreate;
import cn.net.yunlou.bole.model.edit.SkillEdit;
import cn.net.yunlou.bole.model.entity.Skill;
import cn.net.yunlou.bole.model.query.SkillQuery;
import cn.net.yunlou.bole.model.view.SkillView;

/**
 * FileName: SkillService Description: Created By MR. WANG Created At 2025/11/25 00:03 Modified By
 * Modified At
 */
public interface SkillService
        extends IBaseService<Skill, SkillCreate, SkillView, SkillEdit, SkillQuery> {}
