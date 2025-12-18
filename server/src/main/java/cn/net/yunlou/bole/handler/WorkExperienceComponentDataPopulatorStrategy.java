package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.constant.TemplateComponentKey;
import cn.net.yunlou.bole.common.utils.BeanUtils;
import cn.net.yunlou.bole.entity.WorkExperience;
import cn.net.yunlou.bole.model.view.WorkExperienceView;
import cn.net.yunlou.bole.service.WorkExperienceService;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * FileName: WorkExperienceComponentDataPopulatorStrategy Description: Created By laughtiger Created
 * At 2025/12/14 02:50 Modified By Modified At
 */
@Component
@RequiredArgsConstructor
public class WorkExperienceComponentDataPopulatorStrategy
        implements IComponentDataPopulatorStrategy {

    private final WorkExperienceService workExperienceService;

    @Override
    public Map<String, Object> populate(Long userId, Map<String, Object> templateProps) {
        List<WorkExperienceView> workExperienceViews =
                workExperienceService.listView(WorkExperience.builder().userId(userId).build());

        Map<String, Object> props = new HashMap<>(templateProps);

        if (!workExperienceViews.isEmpty()) {

            List<Map<String, Object>> realProps = Lists.newArrayList();
            for (WorkExperienceView view : workExperienceViews) {

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
        return TemplateComponentKey.WORK_EXPERIENCE == componentType;
    }
}
