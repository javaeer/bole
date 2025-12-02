package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.entity.CompanyExperience;
import cn.net.yunlou.bole.mapper.CompanyExperienceMapper;
import cn.net.yunlou.bole.model.dto.CompanyExperienceDTO;
import cn.net.yunlou.bole.model.query.CompanyExperienceQuery;
import cn.net.yunlou.bole.service.CompanyExperienceService;
import cn.net.yunlou.bole.struct.CompanyExperienceStructMapper;
import org.springframework.stereotype.Service;

/**
 * FileName: CompanyExperienceServiceImpl Description: Created By MR. WANG Created At 2025/11/24
 * 23:26 Modified By Modified At
 */
@Service
public class CompanyExperienceServiceImpl
        extends BaseService<
                CompanyExperienceMapper,
                CompanyExperience,
                CompanyExperienceDTO,
                CompanyExperienceQuery,
                CompanyExperienceStructMapper>
        implements CompanyExperienceService {}
