package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.model.create.ResumesTemplateComponentCreate;
import cn.net.yunlou.bole.model.edit.ResumesTemplateComponentEdit;
import cn.net.yunlou.bole.model.query.ResumesTemplateComponentQuery;
import cn.net.yunlou.bole.model.view.ResumesTemplateComponentView;

/**
 * FileName: ResumesComponentService Description: Created By laughtiger Created At 2025/12/17 16:20
 * Modified By Modified At
 */
public interface ResumesTemplateComponentService
        extends IBaseService<
                ResumesTemplateComponent,
                ResumesTemplateComponentCreate,
                ResumesTemplateComponentView,
                ResumesTemplateComponentEdit,
                ResumesTemplateComponentQuery> {}
