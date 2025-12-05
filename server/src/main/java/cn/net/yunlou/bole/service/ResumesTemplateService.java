package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.entity.ResumesTemplate;
import cn.net.yunlou.bole.model.ResumesTemplateCreate;
import cn.net.yunlou.bole.model.ResumesTemplateEdit;
import cn.net.yunlou.bole.model.ResumesTemplateQuery;
import cn.net.yunlou.bole.model.ResumesTemplateView;

/**
 * FileName: ResumesTemplateService Description: Created By MR. WANG Created At 2025/11/24 23:54
 * Modified By Modified At
 */
public interface ResumesTemplateService
        extends IBaseService<
                ResumesTemplate,
                ResumesTemplateCreate,
                ResumesTemplateView,
                ResumesTemplateEdit,
                ResumesTemplateQuery> {}
