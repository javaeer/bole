package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * FileName: UserServiceImpl
 * Description:
 * Created By laughtiger
 * Created At 2025/11/19 13:49
 * Modified By
 * Modified At
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends BaseService<UserMapper, User> implements UserService {

    @Override
    public User findByUsername(String username) {
        User entity = new User();
        entity.setUsername(username);
        return get(entity);
    }

    @Override
    public User findByEmail(String email) {
        User entity = new User();
        entity.setEmail(email);
        return get(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateLastLoginTime(Long id) {
        User entity = new User();
        entity.setId(id);
        entity.setLastLoginAt(LocalDateTime.now());
        return updateById(entity);
    }

    @Override
    public boolean existsByUsername(String username) {
        User entity = new User();
        entity.setUsername(username);
        return exist(entity);
    }

    @Override
    public boolean existsByEmail(String email) {
        User entity = new User();
        entity.setEmail(email);
        return exist(entity);
    }

    /*@Override
    public LambdaQueryWrapper<User> getBaseQueryWrapper(User entity) {
        LambdaQueryWrapper<User> queryWrapper = super.getBaseQueryWrapper(entity);
        if (!ObjectUtils.isEmpty(entity.getUsername())) {
            queryWrapper.eq(User::getUsername, entity.getUsername());
        }
        if (!ObjectUtils.isEmpty(entity.getEmail())) {
            queryWrapper.eq(User::getEmail, entity.getEmail());
        }
        if (!ObjectUtils.isEmpty(entity.getPhone())) {
            queryWrapper.eq(User::getPhone, entity.getPhone());
        }
        return queryWrapper;
    }*/
}
