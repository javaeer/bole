package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.entity.Company;
import cn.net.yunlou.bole.model.CompanyCreate;
import cn.net.yunlou.bole.model.CompanyEdit;
import cn.net.yunlou.bole.model.CompanyQuery;
import cn.net.yunlou.bole.model.CompanyView;

/**
 * FileName: CompanyService Description: Created By MR. WANG Created At 2025/11/24 21:29 Modified By
 * Modified At
 */
public interface CompanyService
        extends IBaseService<Company, CompanyCreate, CompanyView, CompanyEdit, CompanyQuery> {}
