package cn.net.yunlou.bole.handler.resumes;

import cn.net.yunlou.bole.model.entity.ResumesTemplateComponent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ResumeDataParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /** 解析组件数据 */
    public Map<String, Object> parseComponentData(ResumesTemplateComponent component) {
        try {
            Map<String, Object> result = new LinkedHashMap<>();

            // 合并默认配置和实际配置
            Map<String, Object> defaultProps = component.getDefaultConfig().getProps();
            Map<String, Object> props = component.getProps();
            Map<String, Object> defaultStyles = component.getDefaultConfig().getStyles();
            Map<String, Object> styles = component.getStyles();

            // 合并props，实际props优先级高于默认配置
            result.put("props", mergeMaps(defaultProps, props));

            // 合并styles，实际styles优先级高于默认配置
            result.put("styles", mergeMaps(defaultStyles, styles));

            // 添加组件元信息
            result.put("name", component.getName());
            result.put("key", component.getKey());
            result.put("componentId", component.getComponentId());

            return result;
        } catch (Exception e) {
            log.error("解析组件数据失败: componentId={}", component.getComponentId(), e);
            return Collections.emptyMap();
        }
    }

    /** 深度合并Map */
    @SuppressWarnings("unchecked")
    private Map<String, Object> mergeMaps(
            Map<String, Object> defaultMap, Map<String, Object> overrideMap) {
        if (defaultMap == null) return overrideMap != null ? overrideMap : new LinkedHashMap<>();
        if (overrideMap == null) return defaultMap;

        Map<String, Object> result = new LinkedHashMap<>(defaultMap);

        for (Map.Entry<String, Object> entry : overrideMap.entrySet()) {
            String key = entry.getKey();
            Object overrideValue = entry.getValue();
            Object defaultValue = defaultMap.get(key);

            if (overrideValue instanceof Map && defaultValue instanceof Map) {
                // 递归合并嵌套的Map
                result.put(
                        key,
                        mergeMaps(
                                (Map<String, Object>) defaultValue,
                                (Map<String, Object>) overrideValue));
            } else if (overrideValue != null) {
                // 覆盖值
                result.put(key, overrideValue);
            }
        }

        return result;
    }

    /** 解析全局样式 */
    public Map<String, Object> parseGlobalStyle(String globalStyleJson) {
        try {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            MapType mapType =
                    typeFactory.constructMapType(LinkedHashMap.class, String.class, Object.class);
            return objectMapper.readValue(globalStyleJson, mapType);
        } catch (Exception e) {
            log.error("解析全局样式失败", e);
            return new LinkedHashMap<>();
        }
    }

    /** 处理中文乱码问题 */
    public String fixChineseEncoding(String text) {
        if (text == null) return "";

        // 尝试修复常见的中文乱码问题
        try {
            // 检查是否是ISO-8859-1编码的中文
            if (text.matches(".*[À-ÿ].*") && !text.matches(".*[一-龥].*")) {
                return new String(text.getBytes("ISO-8859-1"), "UTF-8");
            }

            // 检查是否是GB2312编码
            if (text.matches(".*[\\x80-\\xFF].*")) {
                try {
                    return new String(text.getBytes("GB2312"), "UTF-8");
                } catch (Exception e) {
                    // 尝试其他编码
                }
            }
        } catch (Exception e) {
            log.warn("修复中文编码失败: {}", text, e);
        }

        return text;
    }

    /** 格式化日期 */
    public String formatDate(Object dateObj) {
        if (dateObj == null) return "";

        String dateStr = dateObj.toString();
        try {
            // 尝试不同的日期格式
            if (dateStr.contains("T")) {
                // ISO格式: 2026-01-01T17:07:50
                return dateStr.replace("T", " ").substring(0, 10);
            } else if (dateStr.length() >= 10) {
                // 标准日期格式: 2026-01-01
                return dateStr.substring(0, 10);
            }
        } catch (Exception e) {
            log.warn("格式化日期失败: {}", dateStr, e);
        }

        return dateStr;
    }
}
