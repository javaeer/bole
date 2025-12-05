package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.entity.Resumes;
import cn.net.yunlou.bole.mapper.ResumesMapper;
import cn.net.yunlou.bole.model.ResumesCreate;
import cn.net.yunlou.bole.model.ResumesEdit;
import cn.net.yunlou.bole.model.ResumesQuery;
import cn.net.yunlou.bole.model.ResumesView;
import cn.net.yunlou.bole.service.ResumesService;
import cn.net.yunlou.bole.struct.ResumesStructMapper;
import org.springframework.stereotype.Service;

/**
 * FileName: ResumesServiceImpl Description: Created By MR. WANG Created At 2025/11/24 22:00
 * Modified By Modified At
 */
@Service
public class ResumesServiceImpl
        extends BaseService<
                ResumesMapper,
                Resumes,
                ResumesCreate,
                ResumesView,
                ResumesEdit,
                ResumesQuery,
                ResumesStructMapper>
        implements ResumesService {}
