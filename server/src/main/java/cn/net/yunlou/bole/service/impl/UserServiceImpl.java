package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.common.IEnum;
import cn.net.yunlou.bole.common.constant.UserKeyField;
import cn.net.yunlou.bole.common.utils.ValueUtils;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.mapper.UserMapper;
import cn.net.yunlou.bole.model.create.UserCreate;
import cn.net.yunlou.bole.model.edit.UserEdit;
import cn.net.yunlou.bole.model.query.UserQuery;
import cn.net.yunlou.bole.model.view.UserView;
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
        UserMapper, User, UserCreate, UserView, UserEdit, UserQuery, UserStructMapper>
        implements UserService {

    @Override
    public User findByUsername(String username) {
        return get(User.builder().username(username).build());
    }

    @Override
    public User findByEmail(String email) {
        return get(User.builder().email(email).build());
    }

    @Override
    public User findByPhone(String phone) {
        return get(User.builder().phone(phone).build());
    }

    @Override
    public User findByWechatOpenId(String wechatOpenid) {
        return get(User.builder().wechatOpenId(wechatOpenid).build());
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
        return exist(User.builder().username(username).build());
    }

    @Override
    public boolean existsByEmail(String email) {
        return exist(User.builder().email(email).build());
    }

    @Override
    public boolean existsByPhone(String phone) {
        return exist(User.builder().phone(phone).build());
    }

    @Override
    protected QueryWrapper<User> getKeyFieldQueryWrapper(
            QueryWrapper<User> queryWrapper, User entity) {

        UserKeyField ukfe =
                ValueUtils.isValid(entity.getKeyField())
                        ? IEnum.valueOf(entity.getKeyField(), UserKeyField.class)
                        : UserKeyField.ALL;

        if (ukfe == null) {
            log.warn("Unknown key field: {}, using default ALL search", entity.getKeyField());
            ukfe = UserKeyField.ALL;
        }

        ukfe.applyQuery(queryWrapper, entity.getKeyWords());

        return queryWrapper;
    }
}
