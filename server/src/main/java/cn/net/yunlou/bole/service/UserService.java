package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.model.create.UserCreate;
import cn.net.yunlou.bole.model.edit.UserEdit;
import cn.net.yunlou.bole.model.query.UserQuery;
import cn.net.yunlou.bole.model.view.UserView;

/**
 * FileName: IUserService Description: Created By MR. WANG Created At 2025/11/19 13:48 Modified By
 * Modified At
 */
public interface UserService extends IBaseService<User, UserCreate, UserView, UserEdit, UserQuery> {
    User findByUsername(String username);

    User findByEmail(String email);

    User findByPhone(String phone);

    User findByWechatOpenId(String wechatOpenid);

    boolean updateLastLoginTime(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);



}
