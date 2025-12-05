package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.entity.Role;
import cn.net.yunlou.bole.model.RoleCreate;
import cn.net.yunlou.bole.model.RoleEdit;
import cn.net.yunlou.bole.model.RoleQuery;
import cn.net.yunlou.bole.model.RoleView;

/**
 * FileName: RoleService Description: Created By MR. WANG Created At 2025/11/24 23:59 Modified By
 * Modified At
 */
public interface RoleService
        extends IBaseService<Role, RoleCreate, RoleView, RoleEdit, RoleQuery> {}
