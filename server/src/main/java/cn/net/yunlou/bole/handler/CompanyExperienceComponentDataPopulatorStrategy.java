package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.constant.TemplateComponentKey;
import cn.net.yunlou.bole.common.utils.BeanUtils;
import cn.net.yunlou.bole.entity.CompanyExperience;
import cn.net.yunlou.bole.model.view.CompanyExperienceView;
import cn.net.yunlou.bole.service.CompanyExperienceService;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * FileName: CompanyExperienceComponentDataPopulatorStrategy Description: Created By laughtiger
 * Created At 2025/12/14 02:51 Modified By Modified At
 */
@Component
@RequiredArgsConstructor
public class CompanyExperienceComponentDataPopulatorStrategy
        implements IComponentDataPopulatorStrategy {

    private final CompanyExperienceService companyExperienceService;

    @Override
    public Map<String, Object> populate(Long userId, Map<String, Object> templateProps) {
        CompanyExperience entity = new CompanyExperience();
        entity.setUserId(userId);

        List<CompanyExperienceView> companyExperienceViews =
                companyExperienceService.listView(entity);

        Map<String, Object> props = new HashMap<>(templateProps);

        if (!companyExperienceViews.isEmpty()) {

            List<Map<String, Object>> realProps = Lists.newArrayList();

            for (CompanyExperienceView view : companyExperienceViews) {

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
        return TemplateComponentKey.COMPANY_EXPERIENCE == componentType;
    }
}
