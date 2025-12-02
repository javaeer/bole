package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.common.IEnum;
import cn.net.yunlou.bole.common.SkipInvalidValueLambdaQueryWrapper;
import cn.net.yunlou.bole.common.constant.UserKeyFieldEnum;
import cn.net.yunlou.bole.common.utils.ValueUtils;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.mapper.UserMapper;
import cn.net.yunlou.bole.model.dto.UserDTO;
import cn.net.yunlou.bole.model.query.UserQuery;
import cn.net.yunlou.bole.service.UserService;
import cn.net.yunlou.bole.struct.UserStructMapper;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * FileName: UserServiceImpl Description: Created By MR. WANG Created At 2025/11/19 13:49 Modified
 * By Modified At
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl
        extends BaseService<UserMapper, User, UserDTO, UserQuery, UserStructMapper>
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
    protected SkipInvalidValueLambdaQueryWrapper<User> getKeyFieldQueryWrapper(
            SkipInvalidValueLambdaQueryWrapper<User> queryWrapper, User entity) {

        UserKeyFieldEnum ukfe =
                ValueUtils.isValid(entity.getKeyField())
                        ? IEnum.getEnumByValue(entity.getKeyField(), UserKeyFieldEnum.class)
                        : UserKeyFieldEnum.ALL;

        if (ukfe == null) {
            log.warn("Unknown key field: {}, using default ALL search", entity.getKeyField());
            ukfe = UserKeyFieldEnum.ALL;
        }

        ukfe.applyQuery(queryWrapper, entity.getKeyWords());

        return queryWrapper;
    }
}
