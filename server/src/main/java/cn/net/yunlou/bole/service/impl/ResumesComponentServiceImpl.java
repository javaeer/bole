package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.mapper.ResumesComponentMapper;
import cn.net.yunlou.bole.model.create.ResumesComponentCreate;
import cn.net.yunlou.bole.model.edit.ResumesComponentEdit;
import cn.net.yunlou.bole.model.entity.ResumesComponent;
import cn.net.yunlou.bole.model.query.ResumesComponentQuery;
import cn.net.yunlou.bole.model.view.ResumesComponentView;
import cn.net.yunlou.bole.service.ResumesComponentService;
import cn.net.yunlou.bole.struct.ResumesComponentStructMapper;
import org.springframework.stereotype.Service;

/**
 * FileName: ResumesComponentServiceImpl Description: Created By laughtiger Created At 2025/12/17
 * 16:24 Modified By Modified At
 */
@Service
public class ResumesComponentServiceImpl
        extends BaseService<
                ResumesComponentMapper,
                ResumesComponent,
                ResumesComponentCreate,
                ResumesComponentView,
                ResumesComponentEdit,
                ResumesComponentQuery,
                ResumesComponentStructMapper>
        implements ResumesComponentService {}
