package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.common.IEnum;
import cn.net.yunlou.bole.common.constant.UserKeyField;
import cn.net.yunlou.bole.common.utils.ValueUtils;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.mapper.UserMapper;
import cn.net.yunlou.bole.model.UserCreate;
import cn.net.yunlou.bole.model.UserEdit;
import cn.net.yunlou.bole.model.UserQuery;
import cn.net.yunlou.bole.model.UserView;
import cn.net.yunlou.bole.service.UserService;
import cn.net.yunlou.bole.struct.UserStructMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * FileName: UserServiceImpl Description: Created By MR. WANG Created At 2025/11/19 13:49 Modified
 * By Modified At
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl
        extends BaseService<
        UserMapper,
        User,
        UserCreate,
        UserView,
        UserEdit,
        UserQuery,
        UserStructMapper>
        implements UserService {

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

    @Override
    protected QueryWrapper<User> getKeyFieldQueryWrapper(
            QueryWrapper<User> queryWrapper, User entity) {

        UserKeyField ukfe =
                ValueUtils.isValid(entity.getKeyField())
                        ? IEnum.getEnumByValue(entity.getKeyField(), UserKeyField.class)
                        : UserKeyField.ALL;

        if (ukfe == null) {
            log.warn("Unknown key field: {}, using default ALL search", entity.getKeyField());
            ukfe = UserKeyField.ALL;
        }

        ukfe.applyQuery(queryWrapper, entity.getKeyWords());

        return queryWrapper;
    }
}
