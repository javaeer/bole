package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.model.create.ResumesComponentCreate;
import cn.net.yunlou.bole.model.edit.ResumesComponentEdit;
import cn.net.yunlou.bole.model.entity.ResumesComponent;
import cn.net.yunlou.bole.model.query.ResumesComponentQuery;
import cn.net.yunlou.bole.model.view.ResumesComponentView;

/**
 * FileName: ResumesComponentService Description: Created By laughtiger Created At 2025/12/17 16:20
 * Modified By Modified At
 */
public interface ResumesComponentService
        extends IBaseService<
                ResumesComponent,
                ResumesComponentCreate,
                ResumesComponentView,
                ResumesComponentEdit,
                ResumesComponentQuery> {}
