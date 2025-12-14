package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.mapper.ResumesTemplateComponentMapper;
import cn.net.yunlou.bole.model.create.ResumesTemplateComponentCreate;
import cn.net.yunlou.bole.model.edit.ResumesTemplateComponentEdit;
import cn.net.yunlou.bole.model.query.ResumesTemplateComponentQuery;
import cn.net.yunlou.bole.model.view.ResumesTemplateComponentView;
import cn.net.yunlou.bole.service.ResumesTemplateComponentService;
import cn.net.yunlou.bole.struct.ResumesTemplateComponentStructMapper;
import org.springframework.stereotype.Service;

/**
 * FileName: ResumesTemplateComponentServiceImpl Description: Created By laughtiger Created At
 * 2025/12/13 21:21 Modified By Modified At
 */
@Service
public class ResumesTemplateComponentServiceImpl
        extends BaseService<
                ResumesTemplateComponentMapper,
                ResumesTemplateComponent,
                ResumesTemplateComponentCreate,
                ResumesTemplateComponentView,
                ResumesTemplateComponentEdit,
                ResumesTemplateComponentQuery,
                ResumesTemplateComponentStructMapper>
        implements ResumesTemplateComponentService {}
