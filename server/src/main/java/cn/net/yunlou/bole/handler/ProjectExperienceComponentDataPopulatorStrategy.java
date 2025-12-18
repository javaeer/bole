package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.constant.TemplateComponentKey;
import cn.net.yunlou.bole.common.utils.BeanUtils;
import cn.net.yunlou.bole.entity.ProjectExperience;
import cn.net.yunlou.bole.model.view.ProjectExperienceView;
import cn.net.yunlou.bole.service.ProjectExperienceService;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * FileName: ProjectExperienceComponentDataPopulatorStrategy Description: Created By laughtiger
 * Created At 2025/12/14 02:52 Modified By Modified At
 */
@Component
@RequiredArgsConstructor
public class ProjectExperienceComponentDataPopulatorStrategy
        implements IComponentDataPopulatorStrategy {

    private final ProjectExperienceService projectExperienceService;

    @Override
    public Map<String, Object> populate(Long userId, Map<String, Object> templateProps) {
        List<ProjectExperienceView> projectExperienceViews =
                projectExperienceService.listView(
                        ProjectExperience.builder().userId(userId).build());

        Map<String, Object> props = new HashMap<>(templateProps);

        if (!projectExperienceViews.isEmpty()) {

            List<Map<String, Object>> realProps = Lists.newArrayList();

            for (ProjectExperienceView view : projectExperienceViews) {

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
        return TemplateComponentKey.PROJECT_EXPERIENCE == componentType;
    }
}
