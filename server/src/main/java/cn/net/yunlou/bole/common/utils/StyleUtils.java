package cn.net.yunlou.bole.common.utils;

import java.util.*;

public class StyleUtils {

    public StyleUtils() {
    }

    /**
     * 将样式Map转换为CSS字符串
     */
    public static String toCss(Map<String, String> styleMap, String... excludeKeys) {
        if (styleMap == null || styleMap.isEmpty()) {
            return "";
        }

        Set<String> excludes = new HashSet<>(Arrays.asList(excludeKeys));
        StringBuilder css = new StringBuilder();

        for (Map.Entry<String, String> entry : styleMap.entrySet()) {
            if (entry.getKey() != null &&
                    entry.getValue() != null &&
                    !entry.getValue().trim().isEmpty() &&
                    !excludes.contains(entry.getKey())) {
                css.append(entry.getKey())
                        .append(": ")
                        .append(entry.getValue())
                        .append("; ");
            }
        }
        return css.toString().trim();
    }

    /**
     * 提取特定样式（用于需要特殊处理的样式）
     */
    public static String getStyleValue(Map<String, String> styleMap, String key, String defaultValue) {
        if (styleMap != null && styleMap.containsKey(key)) {
            String value = styleMap.get(key);
            return value != null && !value.trim().isEmpty() ? value : defaultValue;
        }
        return defaultValue;
    }

    /**
     * 获取CSS安全颜色（确保颜色值有效）
     */
    public static String getSafeColor(String color) {
        if (!StringUtils.hasText(color)) {
            return "#6c757d"; // Bootstrap默认灰色
        }

        // 验证是否为有效的十六进制颜色
        if (color.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
            return color;
        }

        // 如果是颜色名称，转换为十六进制
        Map<String, String> colorMap = new HashMap<>();
        colorMap.put("primary", "#007bff");
        colorMap.put("secondary", "#6c757d");
        colorMap.put("success", "#28a745");
        colorMap.put("danger", "#dc3545");
        colorMap.put("warning", "#ffc107");
        colorMap.put("info", "#17a2b8");
        colorMap.put("light", "#f8f9fa");
        colorMap.put("dark", "#343a40");

        return colorMap.getOrDefault(color.toLowerCase(), "#6c757d");
    }

    /**
     * 生成渐变色样式
     */
    public static String generateGradient(String color1, String color2, String direction) {
        String safeColor1 = getSafeColor(color1);
        String safeColor2 = getSafeColor(color2);
        String dir = "to right"; // 默认方向

        if ("vertical".equals(direction)) {
            dir = "to bottom";
        } else if ("diagonal".equals(direction)) {
            dir = "135deg";
        }

        return String.format("linear-gradient(%s, %s, %s)", dir, safeColor1, safeColor2);
    }

    /**
     * 根据背景色确定文本颜色（确保可读性）
     */
    public static String getContrastColor(String backgroundColor) {
        if (!StringUtils.hasText(backgroundColor) || !backgroundColor.startsWith("#")) {
            return "#212529"; // Bootstrap默认深色
        }

        try {
            // 简化版亮度计算
            String hex = backgroundColor.replace("#", "");
            if (hex.length() == 3) {
                hex = String.valueOf(hex.charAt(0)) + hex.charAt(0) +
                        hex.charAt(1) + hex.charAt(1) +
                        hex.charAt(2) + hex.charAt(2);
            }

            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);

            // 计算相对亮度
            double brightness = (0.299 * r + 0.587 * g + 0.114 * b) / 255;

            return brightness > 0.5 ? "#212529" : "#ffffff"; // 深色背景用白色文字，浅色背景用黑色文字
        } catch (Exception e) {
            return "#212529";
        }
    }

}