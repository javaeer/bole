package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.model.entity.CompanyExperience;
import cn.net.yunlou.bole.model.create.CompanyExperienceCreate;
import cn.net.yunlou.bole.model.edit.CompanyExperienceEdit;
import cn.net.yunlou.bole.model.query.CompanyExperienceQuery;
import cn.net.yunlou.bole.model.view.CompanyExperienceView;

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
