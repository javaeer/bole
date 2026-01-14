package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.model.create.RoleCreate;
import cn.net.yunlou.bole.model.edit.RoleEdit;
import cn.net.yunlou.bole.model.entity.Role;
import cn.net.yunlou.bole.model.query.RoleQuery;
import cn.net.yunlou.bole.model.view.RoleView;

/**
 * FileName: RoleService Description: Created By MR. WANG Created At 2025/11/24 23:59 Modified By
 * Modified At
 */
public interface RoleService
        extends IBaseService<Role, RoleCreate, RoleView, RoleEdit, RoleQuery> {}
