package cn.net.yunlou.bole.common.utils;

import org.springframework.stereotype.Component;

/** 命名 工具类 - 补充方法 */
@Component
public class NamedUtils {

    /**
     * 将下划线命名转换为驼峰命名
     *
     * @param underscore 下划线命名字符串
     * @return 驼峰命名字符串
     */
    public static String toCamelCase(String underscore) {
        if (underscore == null || underscore.isEmpty()) {
            return underscore;
        }

        StringBuilder result = new StringBuilder();
        boolean nextUpper = false;

        for (int i = 0; i < underscore.length(); i++) {
            char c = underscore.charAt(i);

            if (c == '_') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    result.append(Character.toUpperCase(c));
                    nextUpper = false;
                } else {
                    result.append(c);
                }
            }
        }

        return result.toString();
    }

    /**
     * 将驼峰命名转换为下划线命名
     *
     * @param camelCase 驼峰命名字符串
     * @return 下划线命名字符串
     */
    public static String toUnderscore(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);

            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}
