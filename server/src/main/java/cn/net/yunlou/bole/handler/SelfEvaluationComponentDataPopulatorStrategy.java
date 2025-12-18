package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.constant.TemplateComponentKey;
import cn.net.yunlou.bole.common.utils.BeanUtils;
import cn.net.yunlou.bole.entity.SelfEvaluation;
import cn.net.yunlou.bole.model.view.SelfEvaluationView;
import cn.net.yunlou.bole.service.SelfEvaluationService;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * FileName: SelfEvaluationComponentDataPopulatorStrategy Description: Created By laughtiger Created
 * At 2025/12/14 02:52 Modified By Modified At
 */
@Component
@RequiredArgsConstructor
public class SelfEvaluationComponentDataPopulatorStrategy
        implements IComponentDataPopulatorStrategy {

    private final SelfEvaluationService selfEvaluationService;

    @Override
    public Map<String, Object> populate(Long userId, Map<String, Object> templateProps) {
        SelfEvaluation entity = new SelfEvaluation();
        entity.setUserId(userId);
        List<SelfEvaluationView> selfEvaluationViews = selfEvaluationService.listView(entity);

        Map<String, Object> props = new HashMap<>(templateProps);

        if (!selfEvaluationViews.isEmpty()) {

            List<Map<String, Object>> realProps = Lists.newArrayList();

            for (SelfEvaluationView view : selfEvaluationViews) {

                Map<String, Object> map = BeanUtils.toMap(view);
                realProps.add(map);
            }

            Map<String, Object> map = Map.of("evaluations", realProps);

            // 合并数据：模板props + 用户数据
            props.putAll(map);
        }

        return props;
    }

    @Override
    public boolean supports(TemplateComponentKey componentType) {
        return TemplateComponentKey.SELF_EVALUATION == componentType;
    }
}
