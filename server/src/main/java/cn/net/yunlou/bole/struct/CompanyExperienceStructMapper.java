package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.CompanyExperience;
import cn.net.yunlou.bole.model.CompanyExperienceCreate;
import cn.net.yunlou.bole.model.CompanyExperienceEdit;
import cn.net.yunlou.bole.model.CompanyExperienceQuery;
import cn.net.yunlou.bole.model.CompanyExperienceView;
import org.mapstruct.Mapper;

/**
 * FileName: CompanyExperienceStructMapper Description: Created By MR. WANG Created At 2025/11/26
 * 19:14 Modified By Modified At
 */
@Mapper(componentModel = "spring")
public interface CompanyExperienceStructMapper
        extends BaseStructMapper<
                CompanyExperience,
                CompanyExperienceCreate,
                CompanyExperienceView,
                CompanyExperienceEdit,
                CompanyExperienceQuery> {}
