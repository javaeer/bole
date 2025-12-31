package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.constant.TemplateComponentKey;
import cn.net.yunlou.bole.common.utils.BeanUtils;
import cn.net.yunlou.bole.model.entity.EducationExperience;
import cn.net.yunlou.bole.model.view.EducationExperienceView;
import cn.net.yunlou.bole.service.EducationExperienceService;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * FileName: EducationExperienceComponentDataPopulatorStrategy Description: Created By laughtiger
 * Created At 2025/12/14 02:52 Modified By Modified At
 */
@Component
@RequiredArgsConstructor
public class EducationExperienceComponentDataPopulatorStrategy
        implements IComponentDataPopulatorStrategy {

    private final EducationExperienceService educationExperienceService;

    @Override
    public Map<String, Object> populate(Long userId, Map<String, Object> templateProps) {

        EducationExperience entity = new EducationExperience();
        entity.setUserId(userId);

        List<EducationExperienceView> educationExperienceViews =
                educationExperienceService.listView(entity);

        Map<String, Object> props = new HashMap<>(templateProps);

        if (!educationExperienceViews.isEmpty()) {

            List<Map<String, Object>> realProps = Lists.newArrayList();

            for (EducationExperienceView view : educationExperienceViews) {

                Map<String, Object> map = BeanUtils.toMap(view);
                realProps.add(map);
            }

            Map<String, Object> map = Map.of("experiences", realProps);

            // 合并数据：模板props + 用户数据
            props.putAll(map);
        }

        return props;
    }

    @Override
    public boolean supports(TemplateComponentKey componentType) {
        return TemplateComponentKey.EDUCATION_EXPERIENCE == componentType;
    }
}
