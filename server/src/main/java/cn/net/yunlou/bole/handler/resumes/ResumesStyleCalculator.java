package cn.net.yunlou.bole.handler.resumes;

import cn.net.yunlou.bole.common.utils.StyleUtils;
import cn.net.yunlou.bole.model.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.model.entity.ResumesTemplateLayout;
import cn.net.yunlou.bole.model.entity.ResumesTemplateStyle;
import java.util.*;
import org.springframework.stereotype.Component;

/** 样式计算器 - 完善版（匹配Vue样式） */
@Component
public class ResumesStyleCalculator {

    /** 计算简历容器样式（对应Vue的resumeContainerStyle） */
    public Map<String, String> getContainerStyle(
            ResumesTemplateStyle globalStyle, String layoutType) {
        Map<String, String> css = new LinkedHashMap<>();

        if (globalStyle == null) {
            globalStyle = new ResumesTemplateStyle();
        }

        // 基础样式
        css.put("width", "100%");
        css.put("max-width", "auto");
        css.put("height", "auto");
        css.put("margin", "0 auto");
        css.put("box-sizing", "border-box");
        css.put("min-height", "297mm");
        css.put("page-break-inside", "avoid");
        css.put("break-inside", "avoid");

        // 背景色
        String backgroundColor = "#f0f8ff";
        if (globalStyle.getBackgroundColor() != null) {
            backgroundColor = globalStyle.getBackgroundColor();
        }
        css.put("background-color", backgroundColor);

        // 字体
        String fontFamily = "'PingFang SC', 'Helvetica Neue', Arial, sans-serif";
        if (globalStyle.getFontFamily() != null) {
            fontFamily = globalStyle.getFontFamily();
        }
        css.put("font-family", fontFamily);

        // 字体大小
        String fontSize = "13px";
        if (globalStyle.getFontSizes() != null && globalStyle.getFontSizes().getBody() != null) {
            fontSize = globalStyle.getFontSizes().getBody() + "px";
        }
        css.put("font-size", fontSize);

        // 行高
        String lineHeight = "1.5";
        if (globalStyle.getSpacing() != null && globalStyle.getSpacing().getLineHeight() != null) {
            lineHeight = globalStyle.getSpacing().getLineHeight();
        }
        css.put("line-height", lineHeight);

        // 内边距 - 根据布局类型调整
        String padding = "20px";
        if (layoutType != null) {
            switch (layoutType) {
                case "timeline":
                case "card":
                    padding = "20px";
                    if ("card".equals(layoutType)) {
                        css.put("background-color", "#f5f7fa");
                    }
                    break;
                default:
                    if (globalStyle.getSpacing() != null
                            && globalStyle.getSpacing().getPadding() != null) {
                        padding = globalStyle.getSpacing().getPadding();
                    }
            }
        } else if (globalStyle.getSpacing() != null
                && globalStyle.getSpacing().getPadding() != null) {
            padding = globalStyle.getSpacing().getPadding();
        }
        css.put("padding", padding);

        return css;
    }

    /** 根据布局类型计算组件样式（对应Vue的getComponentItemStyle） */
    public Map<String, String> getComponentItemStyle(
            ResumesTemplateComponent component,
            ResumesTemplateStyle globalStyle,
            String layoutType,
            Map<String, Object> componentContext) {
        Map<String, String> styles = new LinkedHashMap<>();

        // 基础样式
        styles.put("width", "100%");
        styles.put("margin-bottom", "16px");

        // 应用全局边距
        if (globalStyle != null
                && globalStyle.getSpacing() != null
                && globalStyle.getSpacing().getSectionMargin() != null) {
            styles.put("margin-bottom", globalStyle.getSpacing().getSectionMargin());
        }

        // 根据布局类型设置不同样式
        if (layoutType != null) {
            switch (layoutType) {
                case "single":
                case "single-column":
                    styles.put("max-width", "800px");
                    styles.put("margin-left", "auto");
                    styles.put("margin-right", "auto");
                    break;
                case "two-column":
                    styles.put("max-width", "100%");
                    break;
                case "timeline":
                    styles.put("max-width", "100%");
                    break;
                case "card":
                    styles.put("margin-bottom", "0");
                    styles.put("max-width", "100%");
                    break;
                case "mixed":
                    // 检查是否是顶部组件
                    boolean isTopComponent =
                            componentContext != null
                                    && Boolean.TRUE.equals(componentContext.get("isTopComponent"));
                    if (isTopComponent) {
                        styles.put("max-width", "800px");
                        styles.put("margin-left", "auto");
                        styles.put("margin-right", "auto");
                    } else {
                        styles.put("max-width", "100%");
                    }
                    break;
                default:
                    styles.put("max-width", "800px");
                    styles.put("margin-left", "auto");
                    styles.put("margin-right", "auto");
            }
        }

        // 合并组件自定义样式（优先级最高）
        if (component != null && component.getStyles() != null) {
            component
                    .getStyles()
                    .forEach(
                            (key, value) -> {
                                if (value != null && !value.toString().trim().isEmpty()) {
                                    styles.put(key, normalizeUnit(value.toString()));
                                }
                            });
        }

        // 合并组件默认配置样式
        if (component != null
                && component.getDefaultConfig() != null
                && component.getDefaultConfig().getStyles() != null) {
            component
                    .getDefaultConfig()
                    .getStyles()
                    .forEach(
                            (key, value) -> {
                                if (value != null
                                        && !value.toString().trim().isEmpty()
                                        && !styles.containsKey(key)) {
                                    styles.put(key, normalizeUnit(value.toString()));
                                }
                            });
        }

        return styles;
    }

    /** 获取布局容器样式（对应Vue的各种布局样式） */
    public Map<String, String> getLayoutStyle(
            String layoutType, ResumesTemplateStyle globalStyle, ResumesTemplateLayout layout) {
        Map<String, String> styles = new LinkedHashMap<>();

        if (layoutType == null) {
            layoutType = "single";
        }

        switch (layoutType) {
            case "single":
            case "single-column":
                styles.put("width", "90%");
                styles.put("display", "flex");
                styles.put("flex-direction", "column");
                styles.put("align-items", "stretch");
                String gap = "16px";
                if (globalStyle != null
                        && globalStyle.getSpacing() != null
                        && globalStyle.getSpacing().getSectionMargin() != null) {
                    gap = globalStyle.getSpacing().getSectionMargin();
                }
                styles.put("gap", gap);
                break;

            case "two-column":
                styles.put("display", "flex");
                styles.put("gap", "20px");
                styles.put("width", "90%");
                styles.put("max-width", "1200px");
                styles.put("margin", "0 auto");

                // 如果布局配置有间距，使用布局配置
                if (layout != null && layout.getGap() != null) {
                    styles.put("gap", layout.getGap());
                } else if (globalStyle != null
                        && globalStyle.getSpacing() != null
                        && globalStyle.getSpacing().getSectionMargin() != null) {
                    styles.put("gap", globalStyle.getSpacing().getSectionMargin());
                }
                break;

            case "three-column":
                styles.put("display", "flex");
                styles.put("gap", "20px");
                styles.put("width", "90%");
                styles.put("max-width", "1200px");
                styles.put("margin", "0 auto");
                break;

            case "timeline":
                styles.put("position", "relative");
                styles.put("width", "90%");
                styles.put("max-width", "900px");
                styles.put("margin", "0 auto");
                styles.put("padding", "20px 0");
                break;

            case "card":
                styles.put("display", "grid");
                styles.put("grid-template-columns", "repeat(auto-fit, minmax(300px, 1fr))");
                styles.put("gap", "20px");
                styles.put("width", "90%");
                styles.put("max-width", "1200px");
                styles.put("margin", "0 auto");
                break;

            case "mixed":
                styles.put("width", "90%");
                styles.put("max-width", "1000px");
                styles.put("margin", "0 auto");
                styles.put("display", "flex");
                styles.put("flex-direction", "column");
                gap = "20px";
                if (globalStyle != null
                        && globalStyle.getSpacing() != null
                        && globalStyle.getSpacing().getSectionMargin() != null) {
                    gap = globalStyle.getSpacing().getSectionMargin();
                }
                styles.put("gap", gap);
                break;
        }

        return styles;
    }

    /** 获取列样式（用于双列/三列布局） */
    public Map<String, String> getColumnStyle(String columnType, ResumesTemplateStyle globalStyle) {
        Map<String, String> styles = new LinkedHashMap<>();

        styles.put("flex", "1");
        styles.put("display", "flex");
        styles.put("flex-direction", "column");

        String gap = "16px";
        if (globalStyle != null
                && globalStyle.getSpacing() != null
                && globalStyle.getSpacing().getSectionMargin() != null) {
            gap = globalStyle.getSpacing().getSectionMargin();
        }
        styles.put("gap", gap);

        return styles;
    }

    /** 计算组件头部样式（包含边框和颜色） */
    public String getComponentHeaderStyle(ResumesTemplateStyle globalStyle) {
        StringBuilder style = new StringBuilder();

        // 主色
        String primaryColor = "#5ac8fa";
        if (globalStyle != null && globalStyle.getPrimaryColor() != null) {
            primaryColor = globalStyle.getPrimaryColor();
        }

        // 辅助色
        String secondaryColor = "#f0f0f0";
        if (globalStyle != null && globalStyle.getSecondaryColor() != null) {
            secondaryColor = globalStyle.getSecondaryColor();
        }

        style.append("margin-bottom: 12px; ");
        style.append("padding-bottom: 8px; ");
        style.append("border-bottom: 1px solid ").append(secondaryColor).append("; ");
        style.append("display: flex; ");
        style.append("align-items: center; ");

        return style.toString();
    }

    /** 计算组件标题样式 */
    public String getComponentTitleStyle(ResumesTemplateStyle globalStyle) {
        StringBuilder style = new StringBuilder();

        // 标题颜色
        String titleColor = "#1890ff";
        if (globalStyle != null && globalStyle.getPrimaryColor() != null) {
            titleColor = globalStyle.getPrimaryColor();
        }

        // 标题字体大小
        String titleSize = "18px";
        if (globalStyle != null
                && globalStyle.getFontSizes() != null
                && globalStyle.getFontSizes().getTitle() != null) {
            titleSize = globalStyle.getFontSizes().getTitle() + "px";
        } else if (globalStyle != null
                && globalStyle.getFontSizes() != null
                && globalStyle.getFontSizes().getH1() != null) {
            titleSize = globalStyle.getFontSizes().getH1() + "px";
        }

        style.append("font-size: ").append(titleSize).append("; ");
        style.append("color: ").append(titleColor).append("; ");
        style.append("font-weight: bold; ");
        style.append("line-height: 1.2; ");
        style.append("flex: 1; ");

        return style.toString();
    }

    /** 计算组件内容样式 */
    public String getComponentContentStyle(ResumesTemplateStyle globalStyle) {
        StringBuilder style = new StringBuilder();

        // 文本颜色
        String textColor = "#555555";
        if (globalStyle != null) {
            // 尝试从全局样式获取
            if (globalStyle.getTextColor() != null) {
                textColor = globalStyle.getTextColor();
            }
        }

        style.append("color: ").append(textColor).append("; ");
        style.append("min-height: 20px; ");

        return style.toString();
    }

    /** 获取时间线布局特定样式 */
    public Map<String, String> getTimelineStyles(ResumesTemplateStyle globalStyle) {
        Map<String, String> styles = new HashMap<>();

        // 时间线样式
        String lineColor = "#5ac8fa";
        if (globalStyle != null) {
            if (globalStyle.getSecondaryColor() != null) {
                lineColor = globalStyle.getSecondaryColor();
            } else if (globalStyle.getPrimaryColor() != null) {
                lineColor = globalStyle.getPrimaryColor();
            }
        }

        styles.put(
                "timelineLineStyle",
                "position: absolute; left: 50%; top: 0; bottom: 0; width: 3px; "
                        + "background-color: "
                        + lineColor
                        + "; opacity: 0.6; transform: translateX(-50%); z-index: 1");

        // 时间线节点样式
        String nodeColor = "#5ac8fa";
        String bgColor = "#f0f8ff";
        if (globalStyle != null) {
            if (globalStyle.getPrimaryColor() != null) {
                nodeColor = globalStyle.getPrimaryColor();
            }
            if (globalStyle.getBackgroundColor() != null) {
                bgColor = globalStyle.getBackgroundColor();
            }
        }

        styles.put(
                "timelineNodeStyle",
                "position: absolute; left: 50%; width: 16px; height: 16px; border-radius: 50%; "
                        + "background-color: "
                        + nodeColor
                        + "; border: 3px solid "
                        + bgColor
                        + "; "
                        + "transform: translate(-50%, -50%); z-index: 2; box-shadow: 0 2px 8px rgba(0,0,0,0.15)");

        return styles;
    }

    /** 获取卡片布局特定样式 */
    public Map<String, String> getCardStyles(
            ResumesTemplateComponent component, ResumesTemplateStyle globalStyle) {
        Map<String, String> styles = new HashMap<>();

        // 卡片项样式
        styles.put(
                "cardItemStyle",
                "background-color: #ffffff; border-radius: 12px; box-shadow: 0 6px 20px rgba(0,0,0,0.08); "
                        + "overflow: hidden; height: 100%; display: flex; flex-direction: column");

        // 卡片内容样式
        styles.put("cardContentStyle", "padding: 24px; flex: 1");

        return styles;
    }

    /** 处理组件数据（为每个组件添加计算好的样式） */
    public List<Map<String, Object>> processComponents(
            List<ResumesTemplateComponent> components,
            ResumesTemplateStyle globalStyle,
            String layoutType,
            Map<String, Object> layoutContext) {
        if (components == null) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> processed = new ArrayList<>();

        for (int i = 0; i < components.size(); i++) {
            ResumesTemplateComponent component = components.get(i);
            Map<String, Object> componentData = new HashMap<>();

            // 基础属性
            componentData.put("componentId", component.getComponentId());
            componentData.put("key", component.getKey());
            componentData.put("name", component.getName());
            componentData.put("props", component.getProps());
            componentData.put("defaultConfig", component.getDefaultConfig());

            // 创建组件上下文（用于混合布局等）
            Map<String, Object> componentContext = new HashMap<>();
            if (layoutContext != null) {
                componentContext.putAll(layoutContext);
            }

            // 为混合布局标记顶部组件
            if ("mixed".equals(layoutType) && i < 2) {
                componentContext.put("isTopComponent", true);
            }

            // 计算组件项样式
            Map<String, String> componentStyles =
                    getComponentItemStyle(component, globalStyle, layoutType, componentContext);
            componentData.put("styles", componentStyles);
            componentData.put("stylesCss", StyleUtils.toCss(componentStyles));

            // 计算组件标题样式（用于header）
            componentData.put("headerStyle", getComponentHeaderStyle(globalStyle));
            componentData.put("titleStyle", getComponentTitleStyle(globalStyle));
            componentData.put("contentStyle", getComponentContentStyle(globalStyle));

            // 添加特殊布局样式
            if ("timeline".equals(layoutType)) {
                componentData.put("timelineStyles", getTimelineStyles(globalStyle));
                componentData.put(
                        "timelineStylesCss", StyleUtils.toCss(getTimelineStyles(globalStyle)));
            } else if ("card".equals(layoutType)) {
                componentData.put("cardStyles", getCardStyles(component, globalStyle));
                componentData.put(
                        "cardStylesCss", StyleUtils.toCss(getCardStyles(component, globalStyle)));
            }

            processed.add(componentData);
        }

        return processed;
    }

    /** 转换布局类型到Vue兼容格式 */
    public String convertLayoutType(String dbLayoutType) {
        if (dbLayoutType == null) {
            return "single";
        }

        switch (dbLayoutType.toLowerCase()) {
            case "single-column":
                return "single";
            case "two-column":
                return "two-column";
            case "three-column":
                return "three-column";
            case "timeline":
            case "card":
            case "mixed":
                return dbLayoutType.toLowerCase();
            default:
                return "single";
        }
    }

    /** 标准化CSS单位 */
    private String normalizeUnit(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }

        value = value.trim();

        // 如果已经是带单位的，直接返回
        if (value.endsWith("px")
                || value.endsWith("em")
                || value.endsWith("rem")
                || value.endsWith("%")
                || value.endsWith("vh")
                || value.endsWith("vw")) {
            return value;
        }

        // 如果是纯数字，添加px单位
        if (value.matches("^\\d+(\\.\\d+)?$")) {
            return value + "px";
        }

        return value;
    }
}
