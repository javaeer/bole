package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IMultiService;
import cn.net.yunlou.bole.model.entity.Role;
import cn.net.yunlou.bole.model.entity.User;
import cn.net.yunlou.bole.model.entity.UserRole;

/**
 * FileName: IUserRoleService Description: Created By MR. WANG Created At 2025/11/19 16:49 Modified
 * By Modified At
 */
public interface UserRoleService extends IMultiService<UserRole, User, Role> {
    void initRole(User user);
}
