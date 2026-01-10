package cn.net.yunlou.bole.handler.resumes;

/**
 * FileName: ResumesStyleCalculator
 * Description:
 * Created By laughtiger
 * Created At 2026/1/10 18:04
 * Modified By
 * Modified At
 */

import cn.net.yunlou.bole.model.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.model.entity.ResumesTemplateStyle;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 样式计算器
 */
@Component
public class ResumesStyleCalculator {


    /**
     * 获取容器的CSS样式
     */
    public Map<String, String> getContainerStyle(ResumesTemplateStyle globalStyle) {
        Map<String, String> css = new HashMap<>();

        if (globalStyle == null) {
            css.put("background-color", "#ffffff");
            return css;
        }

        // 背景色优先级：backgroundColor > secondaryColor > 白色
        if (globalStyle.getBackgroundColor() != null) {
            css.put("background-color", globalStyle.getBackgroundColor());
        } else if (globalStyle.getSecondaryColor() != null) {
            css.put("background-color", globalStyle.getSecondaryColor());
        } else {
            css.put("background-color", "#ffffff");
        }

        // 字体
        if (globalStyle.getFontFamily() != null) {
            css.put("font-family", globalStyle.getFontFamily());
        }

        // 字体大小
        if (globalStyle.getFontSizes() != null && globalStyle.getFontSizes().getBody() != null) {
            css.put("font-size", globalStyle.getFontSizes().getBody() + "px");
        }

        // 行高
        if (globalStyle.getSpacing() != null && globalStyle.getSpacing().getLineHeight() != null) {
            css.put("line-height", globalStyle.getSpacing().getLineHeight());
        }

        // 内边距
        if (globalStyle.getSpacing() != null && globalStyle.getSpacing().getPadding() != null) {
            css.put("padding", globalStyle.getSpacing().getPadding());
        }

        return css;
    }

    /**
     * 获取组件的最终样式（合并默认配置和自定义配置）
     * 优先级 自定义 > 默认
     */
    public Map<String, Object> getMergedStyle(ResumesTemplateComponent component,
                                              ResumesTemplateStyle globalStyle) {
        Map<String, Object> styles = new LinkedHashMap<>(); // 保持顺序

        // 1. 应用组件的自定义样式
        if (component.getStyles() != null) {
            component.getStyles().forEach((key, value) -> {
                if (value != null) styles.put(key, value.toString());
            });
        }

        // 2. 应用组件的默认样式
        if (component.getDefaultConfig() != null && component.getDefaultConfig().getStyles() != null) {
            component.getDefaultConfig().getStyles().forEach((key, value) -> {
                if (value != null && !styles.containsKey(key)) {
                    styles.put(key, value.toString());
                }
            });
        }

        // 3. 应用全局样式规则
        if (globalStyle != null && globalStyle.getSpacing() != null) {
            if (!styles.containsKey("margin-bottom") && globalStyle.getSpacing().getSectionMargin() != null) {
                styles.put("margin-bottom", globalStyle.getSpacing().getSectionMargin());
            }
            if (!styles.containsKey("padding") && globalStyle.getSpacing().getPadding() != null) {
                styles.put("padding", globalStyle.getSpacing().getPadding());
            }
        }

        // 4. 特殊样式：边框（使用主色）
        if (globalStyle != null && globalStyle.getPrimaryColor() != null) {
            styles.put("border-left", "3px solid " + globalStyle.getPrimaryColor());
        }

        return styles;
    }

    /**
     * 计算组件头部样式
     */
    public String getComponentHeaderStyle(ResumesTemplateStyle globalStyle) {
        StringBuilder style = new StringBuilder();

        // 边框颜色
        String primaryColor = globalStyle != null && globalStyle.getPrimaryColor() != null
                ? globalStyle.getPrimaryColor() : "#d4af37";
        String secondaryColor = globalStyle != null && globalStyle.getSecondaryColor() != null
                ? globalStyle.getSecondaryColor() : "#f0f0f0";

        style.append("border-left: 3px solid ").append(primaryColor).append("; ");
        style.append("border-bottom: 1px solid ").append(secondaryColor).append("; ");

        return style.toString();
    }

    /**
     * 计算组件标题样式
     */
    public String getComponentTitleStyle(ResumesTemplateStyle globalStyle) {
        StringBuilder style = new StringBuilder();

        // 颜色
        if (globalStyle != null && globalStyle.getPrimaryColor() != null) {
            style.append("color: ").append(globalStyle.getPrimaryColor()).append("; ");
        } else {
            style.append("color: #d4af37; ");
        }

        // 字体大小
        if (globalStyle != null && globalStyle.getFontSizes() != null
                && globalStyle.getFontSizes().getH1() != null) {
            style.append("font-size: ").append(globalStyle.getFontSizes().getH1()).append("px; ");
        } else {
            style.append("font-size: 18px; ");
        }

        style.append("font-weight: bold; ");

        return style.toString();
    }

    /**
     * 计算组件内容样式
     */
    public String getComponentContentStyle(ResumesTemplateStyle globalStyle) {
        StringBuilder style = new StringBuilder();

        // 字体
        if (globalStyle != null && globalStyle.getFontFamily() != null) {
            style.append("font-family: ").append(globalStyle.getFontFamily()).append("; ");
        } else {
            style.append("font-family: inherit; ");
        }

        // 字体大小
        if (globalStyle != null && globalStyle.getFontSizes() != null
                && globalStyle.getFontSizes().getBody() != null) {
            style.append("font-size: ").append(globalStyle.getFontSizes().getBody()).append("px; ");
        } else {
            style.append("font-size: 14px; ");
        }

        return style.toString();
    }


    /**
     * 使用模板自定义 样式 替代 默认样式
     *
     * @param components  模板
     * @param globalStyle 全局样式
     * @return 替换样式 组件列表
     */
    public List<Map<String, Object>> processComponents(List<ResumesTemplateComponent> components, ResumesTemplateStyle globalStyle) {
        if (components == null) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> processed = new ArrayList<>();

        for (ResumesTemplateComponent component : components) {

            Map<String, Object> componentData = new HashMap<>();

            // 基础属性
            componentData.put("componentId", component.getComponentId());
            componentData.put("key", component.getKey());
            componentData.put("name", component.getName());
            componentData.put("props", component.getProps());

            //合并全局样式
            componentData.put("styles", getMergedStyle(component, globalStyle));

            //计算头部样式
            componentData.put("headerStyle", getComponentHeaderStyle(globalStyle));

            //计算标题样式
            componentData.put("titleStyle", getComponentTitleStyle(globalStyle));

            //计算内容样式
            componentData.put("contentStyle", getComponentContentStyle(globalStyle));

            processed.add(componentData);
        }

        return processed;
    }
}
