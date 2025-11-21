package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.MultiService;
import cn.net.yunlou.bole.entity.Role;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.entity.UserRole;
import cn.net.yunlou.bole.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;

/**
 * FileName: UserRoleServiceImpl
 * Description:
 * Created By laughtiger
 * Created At 2025/11/19 16:50
 * Modified By
 * Modified At
 */
@Service
public class UserRoleServiceImpl extends MultiService<UserRoleMapper, UserRole, User, Role>
        implements UserRoleService {

    @Override
    protected UserRole createEntity(User left, Role right) {

        UserRole userRole = new UserRole();
        if (left != null && left.getId() != null && left.getId() > 0) {
            userRole.setUserId(left.getId());
        }
        if (right != null && right.getId() != null && right.getId() > 0) {
            userRole.setRoleId(right.getId());
        }

        return userRole;
    }
}
