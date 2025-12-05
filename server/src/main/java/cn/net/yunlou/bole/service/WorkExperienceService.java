package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.entity.WorkExperience;
import cn.net.yunlou.bole.model.WorkExperienceCreate;
import cn.net.yunlou.bole.model.WorkExperienceEdit;
import cn.net.yunlou.bole.model.WorkExperienceQuery;
import cn.net.yunlou.bole.model.WorkExperienceView;

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
