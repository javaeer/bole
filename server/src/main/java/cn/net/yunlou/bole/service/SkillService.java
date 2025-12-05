package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.entity.Skill;
import cn.net.yunlou.bole.model.SkillCreate;
import cn.net.yunlou.bole.model.SkillEdit;
import cn.net.yunlou.bole.model.SkillQuery;
import cn.net.yunlou.bole.model.SkillView;

/**
 * FileName: SkillService Description: Created By MR. WANG Created At 2025/11/25 00:03 Modified By
 * Modified At
 */
public interface SkillService
        extends IBaseService<Skill, SkillCreate, SkillView, SkillEdit, SkillQuery> {}
