package cn.net.yunlou.bole.handler.component;

import cn.net.yunlou.bole.common.constant.TemplateComponentKey;
import cn.net.yunlou.bole.common.utils.BeanUtils;
import cn.net.yunlou.bole.handler.IComponentDataPopulatorStrategy;
import cn.net.yunlou.bole.model.entity.JobIntention;
import cn.net.yunlou.bole.model.view.JobIntentionView;
import cn.net.yunlou.bole.service.JobIntentionService;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * FileName: JobIntentionComponentDataPopulatorStrategy Description: Created By laughtiger Created
 * At 2025/12/14 02:50 Modified By Modified At
 */
@Component
@RequiredArgsConstructor
public class JobIntentionComponentDataPopulatorStrategy implements IComponentDataPopulatorStrategy {

    private final JobIntentionService jobIntentionService;

    @Override
    public Map<String, Object> populate(Long userId, Map<String, Object> templateProps) {
        JobIntention entity = new JobIntention();
        entity.setUserId(userId);
        List<JobIntentionView> jobIntentions = jobIntentionService.listView(entity);

        Map<String, Object> props = new HashMap<>(templateProps);

        if (!jobIntentions.isEmpty()) {
            List<Map<String, Object>> realProps = Lists.newArrayList();
            for (JobIntentionView view : jobIntentions) {
                Map<String, Object> map = BeanUtils.toMap(view);
                realProps.add(map);
            }

            Map<String, Object> map = Map.of("intentions", realProps);

            // 合并数据：模板props + 用户数据
            props.putAll(map);
        }

        return props;
    }

    @Override
    public boolean supports(TemplateComponentKey componentType) {
        return TemplateComponentKey.JOB_INTENTION == componentType;
    }
}
