package cn.net.yunlou.bole.handler.resumes;

import cn.net.yunlou.bole.model.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.model.entity.ResumesTemplateLayout;
import cn.net.yunlou.bole.model.entity.ResumesTemplateStyle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 布局计算工具类
 */
@Component
@RequiredArgsConstructor
public class ResumesLayoutCalculator {

    private final ResumesStyleCalculator resumesStyleCalculator;

    /**
     * 根据布局类型分配组件到各栏
     */
    public Map<String, Object> calculateLayout(List<ResumesTemplateComponent> components,
                                               ResumesTemplateLayout layout,
                                               ResumesTemplateStyle globalStyle) {

        Map<String, Object> result = new HashMap<>();
        String layoutType = layout != null ? layout.getType() : "single-column";


        // 过滤掉可能为空的组件
        List<ResumesTemplateComponent> validComponents = Optional.ofNullable(components)
                .orElse(Collections.emptyList())
                .stream()
                .filter(comp -> comp != null && comp.getComponentId() != null)
                .collect(Collectors.toList());

        //处理组件数据（为每个组件添加计算好的样式）（用于组件渲染）
        List<Map<String, Object>> processedComponents = resumesStyleCalculator.processComponents(validComponents, globalStyle);

        switch (layoutType) {
            case "two-column":
                result.putAll(calculateTwoColumnLayout(processedComponents, layout));
                break;
            case "three-column":
                result.putAll(calculateThreeColumnLayout(processedComponents));
                break;
            case "single-column":
            default:
                result.put("allComponents", processedComponents);
                result.put("layoutClass", "layout-single-column");
                break;
        }

        result.put("layoutType", layoutType);
        result.put("isEmpty", processedComponents.isEmpty());

        return result;
    }

    /**
     * 双栏布局计算
     */
    private Map<String, Object> calculateTwoColumnLayout(List<Map<String, Object>> components,
                                                         ResumesTemplateLayout layout) {
        Map<String, Object> result = new HashMap<>();

        // 计算分栏点
        int splitIndex = (int) Math.ceil(components.size() / 2.0);

        List<Map<String, Object>> leftCol = components.subList(0, splitIndex);
        List<Map<String, Object>> rightCol = components.subList(splitIndex, components.size());

        // 获取栏位宽度（来自布局配置或默认值）
        Integer leftWidth = 40; // 默认值
        Integer rightWidth = 60;

        if (layout != null && layout.getColumns() != null) {
            leftWidth = Optional.ofNullable(layout.getColumns().getLeft()).orElse(40);
            rightWidth = Optional.ofNullable(layout.getColumns().getRight()).orElse(60);
        }

        result.put("leftColumnComponents", leftCol);
        result.put("rightColumnComponents", rightCol);
        result.put("leftWidth", leftWidth + "%");
        result.put("rightWidth", rightWidth + "%");
        result.put("layoutClass", "layout-two-column");

        return result;
    }

    /**
     * 三栏布局计算
     */
    private Map<String, Object> calculateThreeColumnLayout(List<Map<String, Object>> components) {
        Map<String, Object> result = new HashMap<>();

        int size = components.size();
        int part = (int) Math.ceil(size / 3.0);

        List<List<Map<String, Object>>> columns = new ArrayList<>();
        columns.add(components.subList(0, Math.min(part, size)));
        columns.add(components.subList(Math.min(part, size), Math.min(part * 2, size)));
        columns.add(components.subList(Math.min(part * 2, size), size));

        result.put("columns", columns);
        result.put("layoutClass", "layout-three-column");

        return result;
    }

}