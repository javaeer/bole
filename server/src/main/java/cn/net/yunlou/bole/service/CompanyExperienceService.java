package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.entity.CompanyExperience;
import cn.net.yunlou.bole.model.CompanyExperienceCreate;
import cn.net.yunlou.bole.model.CompanyExperienceEdit;
import cn.net.yunlou.bole.model.CompanyExperienceQuery;
import cn.net.yunlou.bole.model.CompanyExperienceView;

/**
 * FileName: CompanyExperienceService Description: Created By MR. WANG Created At 2025/11/24 23:26
 * Modified By Modified At
 */
public interface CompanyExperienceService
        extends IBaseService<
                CompanyExperience,
                CompanyExperienceCreate,
                CompanyExperienceView,
                CompanyExperienceEdit,
                CompanyExperienceQuery> {}
