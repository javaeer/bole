package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.model.create.UniversityCreate;
import cn.net.yunlou.bole.model.edit.UniversityEdit;
import cn.net.yunlou.bole.model.entity.University;
import cn.net.yunlou.bole.model.query.UniversityQuery;
import cn.net.yunlou.bole.model.view.UniversityView;

/**
 * FileName: UniversityService Description: Created By laughtiger Created At 2025/12/30 17:48
 * Modified By Modified At
 */
public interface UniversityService
        extends IBaseService<
                University, UniversityCreate, UniversityView, UniversityEdit, UniversityQuery> {}
