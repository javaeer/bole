package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.model.dto.UserDTO;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.model.query.UserQuery;

/**
 * FileName: IUserService
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/19 13:48
 * Modified By
 * Modified At
 */
public interface UserService extends IBaseService<User, UserDTO, UserQuery> {
    User findByUsername(String username);

    User findByEmail(String email);

    boolean updateLastLoginTime(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
