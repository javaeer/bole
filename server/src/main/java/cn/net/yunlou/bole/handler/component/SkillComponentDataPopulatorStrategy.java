package cn.net.yunlou.bole.handler.component;

import cn.net.yunlou.bole.common.constant.TemplateComponentKey;
import cn.net.yunlou.bole.common.utils.BeanUtils;
import cn.net.yunlou.bole.handler.IComponentDataPopulatorStrategy;
import cn.net.yunlou.bole.model.entity.Skill;
import cn.net.yunlou.bole.model.view.SkillView;
import cn.net.yunlou.bole.service.SkillService;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * FileName: SkillComponentDataPopulatorStrategy Description: Created By laughtiger Created At
 * 2025/12/14 14:51 Modified By Modified At
 */
@Component
@RequiredArgsConstructor
public class SkillComponentDataPopulatorStrategy implements IComponentDataPopulatorStrategy {

    private final SkillService skillService;

    @Override
    public Map<String, Object> populate(Long userId, Map<String, Object> templateProps) {
        List<SkillView> skillViews = skillService.listView(Skill.builder().userId(userId).build());

        Map<String, Object> props = new HashMap<>(templateProps);

        if (!skillViews.isEmpty()) {

            List<Map<String, Object>> realProps = Lists.newArrayList();

            for (SkillView view : skillViews) {

                Map<String, Object> map = BeanUtils.toMap(view);
                realProps.add(map);
            }

            Map<String, Object> map = Map.of("skills", realProps);

            // 合并数据：模板props + 用户数据
            props.putAll(map);
        }
        return props;
    }

    @Override
    public boolean supports(TemplateComponentKey componentType) {
        return TemplateComponentKey.SKILLS == componentType;
    }
}
