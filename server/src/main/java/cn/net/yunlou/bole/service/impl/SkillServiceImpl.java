package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.entity.Skill;
import cn.net.yunlou.bole.mapper.SkillMapper;
import cn.net.yunlou.bole.model.dto.SkillDTO;
import cn.net.yunlou.bole.model.query.SkillQuery;
import cn.net.yunlou.bole.service.SkillService;
import cn.net.yunlou.bole.struct.SkillStructMapper;
import org.springframework.stereotype.Service;

/**
 * FileName: SkillServiceImpl Description: Created By MR. WANG Created At 2025/11/25 00:03 Modified
 * By Modified At
 */
@Service
public class SkillServiceImpl
        extends BaseService<SkillMapper, Skill, SkillDTO, SkillQuery, SkillStructMapper>
        implements SkillService {}
