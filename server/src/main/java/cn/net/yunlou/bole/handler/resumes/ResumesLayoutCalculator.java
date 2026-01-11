package cn.net.yunlou.bole.handler.resumes;

import cn.net.yunlou.bole.model.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.model.entity.ResumesTemplateLayout;
import cn.net.yunlou.bole.model.entity.ResumesTemplateStyle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 布局计算工具类 - 完善版（支持Vue所有布局类型）
 */
@Component
@RequiredArgsConstructor
public class ResumesLayoutCalculator {

    private final ResumesStyleCalculator resumesStyleCalculator;

    /**
     * 根据布局类型分配组件到各栏
     */
    public Map<String, Object> calculateLayout(List<ResumesTemplateComponent> components,
                                               ResumesTemplateLayout globalLayout,
                                               ResumesTemplateStyle globalStyle) {

        Map<String, Object> result = new HashMap<>();

        // 转换布局类型为Vue兼容格式
        String layoutType = resumesStyleCalculator.convertLayoutType(
                globalLayout != null ? globalLayout.getType() : null
        );

        // 过滤掉可能为空的组件
        List<ResumesTemplateComponent> validComponents = Optional.ofNullable(components)
                .orElse(Collections.emptyList())
                .stream()
                .filter(comp -> comp != null && comp.getComponentId() != null)
                .collect(Collectors.toList());

        // 创建布局上下文（用于传递额外信息）
        Map<String, Object> layoutContext = new HashMap<>();
        layoutContext.put("layoutType", layoutType);
        layoutContext.put("globalStyle", globalStyle);

        // 处理组件数据（为每个组件添加计算好的样式）
        List<Map<String, Object>> processedComponents = resumesStyleCalculator.processComponents(
                validComponents, globalStyle, layoutType, layoutContext
        );

        // 按组件顺序排序（如果有配置顺序）
        processedComponents.sort((a, b) -> {
            Integer orderA = getComponentOrder(a, globalLayout);
            Integer orderB = getComponentOrder(b, globalLayout);
            return Integer.compare(orderA, orderB);
        });

        // 根据不同布局类型计算布局
        switch (layoutType) {
            case "two-column":
                result.putAll(calculateTwoColumnLayout(processedComponents, globalLayout, globalStyle));
                break;
            case "three-column":
                result.putAll(calculateThreeColumnLayout(processedComponents, globalLayout, globalStyle));
                break;
            case "timeline":
                result.putAll(calculateTimelineLayout(processedComponents, globalLayout, globalStyle));
                break;
            case "card":
                result.putAll(calculateCardLayout(processedComponents, globalLayout, globalStyle));
                break;
            case "mixed":
                result.putAll(calculateMixedLayout(processedComponents, globalLayout, globalStyle));
                break;
            case "single":
            default:
                result.put("allComponents", processedComponents);
                result.put("layoutClass", "single-layout");
                break;
        }

        // 添加布局样式
        Map<String, String> layoutStyle = resumesStyleCalculator.getLayoutStyle(layoutType, globalStyle, globalLayout);
        result.put("layoutStyle", layoutStyle);

        // 添加时间线/卡片特定样式
        if ("timeline".equals(layoutType)) {
            result.putAll(resumesStyleCalculator.getTimelineStyles(globalStyle));
        }

        result.put("layoutType", layoutType);
        result.put("isEmpty", processedComponents.isEmpty());
        result.put("globalStyle", globalStyle);

        return result;
    }

    /**
     * 获取组件顺序
     */
    private Integer getComponentOrder(Map<String, Object> component, ResumesTemplateLayout layout) {
        if (layout == null || layout.getComponentOrder() == null) {
            return 0;
        }

        String componentKey = (String) component.get("key");
        List<String> orderList = layout.getComponentOrder();

        for (int i = 0; i < orderList.size(); i++) {
            if (orderList.get(i).equals(componentKey)) {
                return i;
            }
        }

        return orderList.size(); // 未排序的组件放在最后
    }

    /**
     * 双列布局计算
     */
    private Map<String, Object> calculateTwoColumnLayout(List<Map<String, Object>> components,
                                                         ResumesTemplateLayout layout,
                                                         ResumesTemplateStyle globalStyle) {
        Map<String, Object> result = new HashMap<>();

        // 计算分栏点
        int splitIndex = (int) Math.ceil(components.size() / 2.0);

        List<Map<String, Object>> leftCol = components.subList(0, splitIndex);
        List<Map<String, Object>> rightCol = components.subList(splitIndex, components.size());

        // 为左列组件添加位置上下文
        for (Map<String, Object> component : leftCol) {
            Map<String, Object> context = new HashMap<>();
            context.put("columnPosition", "left");
            component.put("layoutContext", context);
        }

        // 为右列组件添加位置上下文
        for (Map<String, Object> component : rightCol) {
            Map<String, Object> context = new HashMap<>();
            context.put("columnPosition", "right");
            component.put("layoutContext", context);
        }

        // 获取栏位宽度（来自布局配置或默认值）
        Integer leftWidth = 50; // 默认值
        Integer rightWidth = 50;

        if (layout != null && layout.getColumns() != null) {
            leftWidth = Optional.ofNullable(layout.getColumns().getLeft()).orElse(50);
            rightWidth = Optional.ofNullable(layout.getColumns().getRight()).orElse(50);
        }

        result.put("leftColumnComponents", leftCol);
        result.put("rightColumnComponents", rightCol);
        result.put("leftColumnStyle", resumesStyleCalculator.getColumnStyle("left", globalStyle));
        result.put("rightColumnStyle", resumesStyleCalculator.getColumnStyle("right", globalStyle));
        result.put("leftWidth", leftWidth + "%");
        result.put("rightWidth", rightWidth + "%");
        result.put("layoutClass", "two-column-layout");

        return result;
    }

    /**
     * 三列布局计算
     */
    private Map<String, Object> calculateThreeColumnLayout(List<Map<String, Object>> components,
                                                           ResumesTemplateLayout layout,
                                                           ResumesTemplateStyle globalStyle) {
        Map<String, Object> result = new HashMap<>();

        int size = components.size();
        int part = (int) Math.ceil(size / 3.0);

        List<List<Map<String, Object>>> columns = new ArrayList<>();
        columns.add(components.subList(0, Math.min(part, size)));
        columns.add(components.subList(Math.min(part, size), Math.min(part * 2, size)));
        columns.add(components.subList(Math.min(part * 2, size), size));

        // 为每列组件添加位置上下文
        String[] columnNames = {"left", "center", "right"};
        for (int i = 0; i < columns.size(); i++) {
            for (Map<String, Object> component : columns.get(i)) {
                Map<String, Object> context = new HashMap<>();
                context.put("columnPosition", columnNames[i]);
                component.put("layoutContext", context);
            }
        }

        result.put("columns", columns);
        result.put("columnStyles", Arrays.asList(
                resumesStyleCalculator.getColumnStyle("left", globalStyle),
                resumesStyleCalculator.getColumnStyle("center", globalStyle),
                resumesStyleCalculator.getColumnStyle("right", globalStyle)
        ));
        result.put("layoutClass", "layout-three-column");

        return result;
    }

    /**
     * 时间线布局计算
     */
    private Map<String, Object> calculateTimelineLayout(List<Map<String, Object>> components,
                                                        ResumesTemplateLayout layout,
                                                        ResumesTemplateStyle globalStyle) {
        Map<String, Object> result = new HashMap<>();

        // 为每个组件添加时间线上下文
        for (int i = 0; i < components.size(); i++) {
            Map<String, Object> component = components.get(i);
            Map<String, Object> context = new HashMap<>();
            context.put("timelineIndex", i);
            context.put("isEven", i % 2 == 0);
            context.put("isLast", i == components.size() - 1);
            component.put("layoutContext", context);

            // 计算时间线项样式
            Map<String, String> timelineItemStyle = new LinkedHashMap<>();
            timelineItemStyle.put("position", "relative");
            timelineItemStyle.put("width", "100%");
            timelineItemStyle.put("margin-bottom", i == components.size() - 1 ? "0" : "40px");
            timelineItemStyle.put("min-height", "100px");

            // 合并到组件样式
            Map<String, String> componentStyles = (Map<String, String>) component.get("styles");
            if (componentStyles != null) {
                componentStyles.putAll(timelineItemStyle);
            }
        }

        result.put("allComponents", components);
        result.put("layoutClass", "timeline-layout");

        return result;
    }

    /**
     * 卡片布局计算
     */
    private Map<String, Object> calculateCardLayout(List<Map<String, Object>> components,
                                                    ResumesTemplateLayout layout,
                                                    ResumesTemplateStyle globalStyle) {
        Map<String, Object> result = new HashMap<>();

        // 为每个组件添加卡片样式
        for (Map<String, Object> component : components) {
            Map<String, Object> context = new HashMap<>();
            context.put("isCard", true);
            component.put("layoutContext", context);

            // 添加卡片样式
            Map<String, String> cardStyles = resumesStyleCalculator.getCardStyles(null, globalStyle);
            Map<String, String> componentStyles = (Map<String, String>) component.get("styles");
            if (componentStyles != null) {
                componentStyles.putAll(cardStyles);
            }
        }

        result.put("allComponents", components);
        result.put("layoutClass", "card-layout");

        return result;
    }

    /**
     * 混合布局计算
     */
    private Map<String, Object> calculateMixedLayout(List<Map<String, Object>> components,
                                                     ResumesTemplateLayout layout,
                                                     ResumesTemplateStyle globalStyle) {
        Map<String, Object> result = new HashMap<>();

        // 前2个组件在上面（单列）
        List<Map<String, Object>> topComponents = new ArrayList<>();
        List<Map<String, Object>> bottomComponents = new ArrayList<>();

        if (components.size() > 0) {
            topComponents = components.subList(0, Math.min(2, components.size()));
        }
        if (components.size() > 2) {
            bottomComponents = components.subList(2, components.size());
        }

        // 为顶部组件添加上下文
        for (Map<String, Object> component : topComponents) {
            Map<String, Object> context = new HashMap<>();
            context.put("isTopComponent", true);
            component.put("layoutContext", context);
        }

        // 为底部组件分左右列
        List<Map<String, Object>> bottomLeftComponents = new ArrayList<>();
        List<Map<String, Object>> bottomRightComponents = new ArrayList<>();

        if (!bottomComponents.isEmpty()) {
            int splitIndex = (int) Math.ceil(bottomComponents.size() / 2.0);
            bottomLeftComponents = bottomComponents.subList(0, splitIndex);
            bottomRightComponents = bottomComponents.subList(splitIndex, bottomComponents.size());

            // 为左列组件添加上下文
            for (Map<String, Object> component : bottomLeftComponents) {
                Map<String, Object> context = new HashMap<>();
                context.put("columnPosition", "left");
                context.put("isBottomComponent", true);
                component.put("layoutContext", context);
            }

            // 为右列组件添加上下文
            for (Map<String, Object> component : bottomRightComponents) {
                Map<String, Object> context = new HashMap<>();
                context.put("columnPosition", "right");
                context.put("isBottomComponent", true);
                component.put("layoutContext", context);
            }
        }

        result.put("topComponents", topComponents);
        result.put("bottomComponents", bottomComponents);
        result.put("bottomLeftComponents", bottomLeftComponents);
        result.put("bottomRightComponents", bottomRightComponents);

        // 计算混合布局底部样式
        Map<String, String> mixedBottomStyle = new LinkedHashMap<>();
        mixedBottomStyle.put("display", "flex");
        mixedBottomStyle.put("gap", "20px");
        mixedBottomStyle.put("width", "100%");
        mixedBottomStyle.put("margin-top", "30px");

        if (layout != null && layout.getGap() != null) {
            mixedBottomStyle.put("gap", layout.getGap());
        } else if (globalStyle != null && globalStyle.getSpacing() != null
                && globalStyle.getSpacing().getSectionMargin() != null) {
            mixedBottomStyle.put("gap", globalStyle.getSpacing().getSectionMargin());
        }

        result.put("mixedBottomStyle", mixedBottomStyle);
        result.put("leftColumnStyle", resumesStyleCalculator.getColumnStyle("left", globalStyle));
        result.put("rightColumnStyle", resumesStyleCalculator.getColumnStyle("right", globalStyle));
        result.put("layoutClass", "mixed-layout");

        return result;
    }

    /**
     * 获取响应式样式（用于前端CSS）
     */
    public Map<String, String> getResponsiveStyles() {
        Map<String, String> styles = new HashMap<>();

        // 移动端样式
        styles.put("mobileStyles",
                "@media (max-width: 768px) {\n" +
                        "  .resume-container {\n" +
                        "    width: 100% !important;\n" +
                        "    max-width: 100% !important;\n" +
                        "    padding: 16px !important;\n" +
                        "    min-height: auto !important;\n" +
                        "  }\n" +
                        "  \n" +
                        "  .two-column-layout {\n" +
                        "    flex-direction: column !important;\n" +
                        "    gap: 16px !important;\n" +
                        "  }\n" +
                        "  \n" +
                        "  .card-layout {\n" +
                        "    grid-template-columns: 1fr !important;\n" +
                        "    gap: 16px !important;\n" +
                        "  }\n" +
                        "  \n" +
                        "  .timeline-line {\n" +
                        "    display: none;\n" +
                        "  }\n" +
                        "  \n" +
                        "  .timeline-content {\n" +
                        "    width: 100% !important;\n" +
                        "    margin: 0 auto 16px auto !important;\n" +
                        "  }\n" +
                        "}");

        // 平板样式
        styles.put("tabletStyles",
                "@media (min-width: 769px) and (max-width: 1024px) {\n" +
                        "  .card-layout {\n" +
                        "    grid-template-columns: repeat(2, 1fr) !important;\n" +
                        "  }\n" +
                        "}");

        return styles;
    }
}