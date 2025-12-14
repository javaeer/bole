package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.entity.ResumesTemplate;
import cn.net.yunlou.bole.model.create.ResumesTemplateCreate;
import cn.net.yunlou.bole.model.edit.ResumesTemplateEdit;
import cn.net.yunlou.bole.model.query.ResumesTemplateQuery;
import cn.net.yunlou.bole.model.view.ResumesTemplateView;
import java.util.List;

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
                ResumesTemplateQuery> {

    List<ResumesTemplate> getAllActiveTemplates();

    ResumesTemplate getByCode(String code);
}
