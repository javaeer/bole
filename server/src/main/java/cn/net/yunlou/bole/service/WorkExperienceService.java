package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.model.create.WorkExperienceCreate;
import cn.net.yunlou.bole.model.edit.WorkExperienceEdit;
import cn.net.yunlou.bole.model.entity.WorkExperience;
import cn.net.yunlou.bole.model.query.WorkExperienceQuery;
import cn.net.yunlou.bole.model.view.WorkExperienceView;

/**
 * FileName: WorkExperienceService Description: Created By MR. WANG Created At 2025/11/25 00:05
 * Modified By Modified At
 */
public interface WorkExperienceService
        extends IBaseService<
                WorkExperience,
                WorkExperienceCreate,
                WorkExperienceView,
                WorkExperienceEdit,
                WorkExperienceQuery> {}
