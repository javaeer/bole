package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.model.dto.WorkExperienceDTO;
import cn.net.yunlou.bole.entity.WorkExperience;
import cn.net.yunlou.bole.mapper.WorkExperienceMapper;
import cn.net.yunlou.bole.struct.WorkExperienceStructMapper;
import cn.net.yunlou.bole.model.query.WorkExperienceQuery;
import cn.net.yunlou.bole.service.WorkExperienceService;
import org.springframework.stereotype.Service;

/**
 * FileName: WorkExperienceServiceImpl
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/25 00:05
 * Modified By
 * Modified At
 */
@Service
public class WorkExperienceServiceImpl extends BaseService<
        WorkExperienceMapper,
        WorkExperience,
        WorkExperienceDTO,
        WorkExperienceQuery,
        WorkExperienceStructMapper> implements WorkExperienceService {
}
