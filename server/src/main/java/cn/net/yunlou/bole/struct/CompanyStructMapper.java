package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.Company;
import cn.net.yunlou.bole.model.CompanyCreate;
import cn.net.yunlou.bole.model.CompanyEdit;
import cn.net.yunlou.bole.model.CompanyQuery;
import cn.net.yunlou.bole.model.CompanyView;
import org.mapstruct.Mapper;

/**
 * FileName: CompanyStructMapper Description: Created By MR. WANG Created At 2025/11/26 19:14
 * Modified By Modified At
 */
@Mapper(componentModel = "spring")
public interface CompanyStructMapper
        extends BaseStructMapper<Company, CompanyCreate, CompanyView, CompanyEdit, CompanyQuery> {}
