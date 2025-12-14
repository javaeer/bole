package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.entity.ResumesTemplate;
import cn.net.yunlou.bole.mapper.ResumesTemplateMapper;
import cn.net.yunlou.bole.model.create.ResumesTemplateCreate;
import cn.net.yunlou.bole.model.edit.ResumesTemplateEdit;
import cn.net.yunlou.bole.model.query.ResumesTemplateQuery;
import cn.net.yunlou.bole.model.view.ResumesTemplateView;
import cn.net.yunlou.bole.service.ResumesTemplateService;
import cn.net.yunlou.bole.struct.ResumesTemplateStructMapper;
import java.util.List;
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
        implements ResumesTemplateService {

    @Override
    public List<ResumesTemplate> getAllActiveTemplates() {
        return list(ResumesTemplate.builder().isActive(Boolean.TRUE).build());
    }

    @Override
    public ResumesTemplate getByCode(String code) {
        return get(ResumesTemplate.builder().code(code).build());
    }
}
