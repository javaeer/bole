package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.constant.TemplateComponentKey;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * FileName: ComponentDataPopulatorFactory Description: Created By laughtiger Created At 2025/12/14
 * 02:21 Modified By Modified At
 */
@Component
@RequiredArgsConstructor
public class ComponentDataPopulatorFactory {

    /** 这个注入了多个实现类对象 */
    // @Autowired
    private final List<IComponentDataPopulatorStrategy> strategies;

    /**
     * 根据组件类型 组件数据填充策略
     *
     * @param component
     * @return
     */
    public IComponentDataPopulatorStrategy getComponentDataPopulatorStrategy(
            TemplateComponentKey component) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(component))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No strategy found for " + component));
    }
}
