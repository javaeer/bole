package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.entity.Resumes;
import cn.net.yunlou.bole.mapper.ResumesMapper;
import cn.net.yunlou.bole.model.dto.ResumesDTO;
import cn.net.yunlou.bole.model.query.ResumesQuery;
import cn.net.yunlou.bole.service.ResumesService;
import cn.net.yunlou.bole.struct.ResumesStructMapper;
import org.springframework.stereotype.Service;

/**
 * FileName: ResumesServiceImpl Description: Created By MR. WANG Created At 2025/11/24 22:00
 * Modified By Modified At
 */
@Service
public class ResumesServiceImpl
        extends BaseService<ResumesMapper, Resumes, ResumesDTO, ResumesQuery, ResumesStructMapper>
        implements ResumesService {}
