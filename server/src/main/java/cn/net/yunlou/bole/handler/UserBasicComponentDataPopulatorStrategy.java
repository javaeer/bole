package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.constant.TemplateComponentType;
import cn.net.yunlou.bole.common.utils.BeanUtils;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.model.view.UserBasicInfoView;
import cn.net.yunlou.bole.service.UserService;
import cn.net.yunlou.bole.struct.UserStructMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * FileName: UserBasicComponentDataPopulatorStrategy Description: Created By laughtiger Created At
 * 2025/12/14 02:38 Modified By Modified At
 */
@Component
@RequiredArgsConstructor
public class UserBasicComponentDataPopulatorStrategy implements IComponentDataPopulatorStrategy {

    private final UserService userService;

    private final UserStructMapper userStructMapper;

    @Override
    public Map<String, Object> populate(Long userId, Map<String, Object> templateProps) {
        User user = userService.getById(userId);

        Map<String, Object> props = new HashMap<>(templateProps);

        // 使用MapStruct进行类型安全的映射
        UserBasicInfoView info = userStructMapper.toBasicInfoView(user);

        // 合并数据：模板props + 用户数据
        props.putAll(BeanUtils.toMap(info));

        return props;
    }

    @Override
    public boolean supports(TemplateComponentType componentType) {
        return componentType == TemplateComponentType.USER_BASIC_INFO;
    }
}
