package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.entity.ResumesTemplate;
import cn.net.yunlou.bole.mapper.ResumesTemplateMapper;
import cn.net.yunlou.bole.model.ResumesTemplateCreate;
import cn.net.yunlou.bole.model.ResumesTemplateEdit;
import cn.net.yunlou.bole.model.ResumesTemplateQuery;
import cn.net.yunlou.bole.model.ResumesTemplateView;
import cn.net.yunlou.bole.service.ResumesTemplateService;
import cn.net.yunlou.bole.struct.ResumesTemplateStructMapper;
import org.springframework.stereotype.Service;

/**
 * FileName: ResumesTemplateServiceImpl Description: Created By MR. WANG Created At 2025/11/24 23:54
 * Modified By Modified At
 */
@Service
public class ResumesTemplateServiceImpl
        extends BaseService<
                ResumesTemplateMapper,
                ResumesTemplate,
                ResumesTemplateCreate,
                ResumesTemplateView,
                ResumesTemplateEdit,
                ResumesTemplateQuery,
                ResumesTemplateStructMapper>
        implements ResumesTemplateService {}
