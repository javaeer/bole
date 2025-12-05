package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.model.UserCreate;
import cn.net.yunlou.bole.model.UserEdit;
import cn.net.yunlou.bole.model.UserQuery;
import cn.net.yunlou.bole.model.UserView;

/**
 * FileName: IUserService Description: Created By MR. WANG Created At 2025/11/19 13:48 Modified By
 * Modified At
 */
public interface UserService extends IBaseService<User, UserCreate, UserView, UserEdit, UserQuery> {
    User findByUsername(String username);

    User findByEmail(String email);

    boolean updateLastLoginTime(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
