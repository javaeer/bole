package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.constant.TemplateComponentType;
import cn.net.yunlou.bole.common.utils.BeanUtils;
import cn.net.yunlou.bole.entity.WorkExperience;
import cn.net.yunlou.bole.model.view.WorkExperienceView;
import cn.net.yunlou.bole.service.WorkExperienceService;
import cn.net.yunlou.bole.struct.WorkExperienceStructMapper;
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

    private final WorkExperienceStructMapper workExperienceStructMapper;

    @Override
    public Map<String, Object> populate(Long userId, Map<String, Object> templateProps) {
        List<WorkExperience> workExperiences =
                workExperienceService.list(WorkExperience.builder().userId(userId).build());

        Map<String, Object> props = new HashMap<>(templateProps);

        if (!workExperiences.isEmpty()) {

            List<Map<String, Object>> realProps = Lists.newArrayList();

            List<WorkExperienceView> views = workExperienceStructMapper.toViews(workExperiences);
            for (WorkExperienceView view : views) {

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
    public boolean supports(TemplateComponentType componentType) {
        return TemplateComponentType.WORK_EXPERIENCE == componentType;
    }
}
