package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.constant.TemplateComponentType;
import cn.net.yunlou.bole.common.utils.BeanUtils;
import cn.net.yunlou.bole.entity.SelfEvaluation;
import cn.net.yunlou.bole.model.view.SelfEvaluationView;
import cn.net.yunlou.bole.service.SelfEvaluationService;
import cn.net.yunlou.bole.struct.SelfEvaluationStructMapper;
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

    private final SelfEvaluationStructMapper selfEvaluationStructMapper;

    @Override
    public Map<String, Object> populate(Long userId, Map<String, Object> templateProps) {
        List<SelfEvaluation> selfEvaluations =
                selfEvaluationService.list(SelfEvaluation.builder().userId(userId).build());

        Map<String, Object> props = new HashMap<>(templateProps);

        if (!selfEvaluations.isEmpty()) {

            List<Map<String, Object>> realProps = Lists.newArrayList();

            List<SelfEvaluationView> views = selfEvaluationStructMapper.toViews(selfEvaluations);
            for (SelfEvaluationView view : views) {

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
    public boolean supports(TemplateComponentType componentType) {
        return TemplateComponentType.SELF_EVALUATION == componentType;
    }
}
