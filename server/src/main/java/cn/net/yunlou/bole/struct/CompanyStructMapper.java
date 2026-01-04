package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.model.create.CompanyCreate;
import cn.net.yunlou.bole.model.edit.CompanyEdit;
import cn.net.yunlou.bole.model.entity.Company;
import cn.net.yunlou.bole.model.query.CompanyQuery;
import cn.net.yunlou.bole.model.view.CompanyView;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * FileName: CompanyStructMapper Description: Created By MR. WANG Created At 2025/11/26 19:14
 * Modified By Modified At
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CompanyStructMapper
        extends BaseStructMapper<Company, CompanyCreate, CompanyView, CompanyEdit, CompanyQuery> {}
