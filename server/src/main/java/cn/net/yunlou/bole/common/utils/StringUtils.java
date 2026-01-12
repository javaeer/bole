/*
 *
 *     Copyright (c) 2019 - forever, javaeer All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the smartcloudx.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * Author: javaeer (javaeer@aliyun.com)
 *
 */
package cn.net.yunlou.bole.common.utils;

import com.google.common.collect.Lists;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.lang.Nullable;

/**
 * @Author javaeer(javaeer @ aliyun.com) @Date 2018/11/27 13:50 @Version 1.0
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /** 字符常量：斜杠 {@code '/'} */
    public static final String SLASH = "/";

    /** 字符常量：反斜杠 {@code '\\'} */
    public static final String BACKSLASH = "\\";

    /** 字符常量：冒号 {@code ':'} */
    public static final String COLONS = ":";

    /** 字符常量：井号 {@code '#'} */
    public static final String POUND = "#";

    /** 字符常量：单引号 {@code '\''} */
    public static final String SINGLE_QUOTE = "'\''";

    /** 字符常量：与 {@code '&'} */
    public static final String AMP = "&";

    /** 字符常量：艾特 {@code '@'} */
    public static final String AT = "@";

    /** 字符常量：逗号 {@code ','} */
    public static final String COMMA = ",";

    /** 字符常量：下划线 {@code '_'} */
    public static final String UNDERLINE = "_";

    /** 字符常量：点 {@code '.'} */
    public static final String DOT = ".";

    /** 字符常量：大括号（左） <code>'{'</code> */
    public static final String BRACE_START = "{";

    /** 字符常量：花括号（右） <code>'}'</code> */
    public static final String BRACE_END = "}";

    /** 字符常量：中括号（左） {@code '['} */
    public static final String BRACKET_START = "[";

    /** 字符常量：中括号（右） {@code ']'} */
    public static final String BRACKET_END = "]";

    /** 字符常量：小括号（左） {@code '('} */
    public static final String PARENTHESIS_START = "(";

    /** 字符常量：小括号（右） {@code ')'} */
    public static final String PARENTHESIS_END = ")";

    /** 字符常量：加号 {@code '+'} */
    public static final String PLUS = "+";

    /** 字符常量：减号 {@code '-'} */
    public static final String MINUS = "-";

    public static final String CHARSET_UTF_8 = "UTF-8";
    private static final String CHARSET_GBK = "GBK";

    /** 构造函数 防止创建对象 */
    public StringUtils() {
        super();
    }

    /**
     * 添加到开头位置
     *
     * @param src
     * @param target
     * @return
     */
    public static String addFirst(String src, String target) {
        return target + src;
    }

    /**
     * 缩略字符串（不区分中英文字符） 自动识别 html 包含 脚本
     *
     * <p><div>abcdefg</div> => <di...
     *
     * @param str 目标字符串
     * @param maxWidth 保留长度
     * @return
     */
    public static String smartAbbreviate(final String str, final int maxWidth) {
        if (str == null) {
            return null;
        }
        try {
            StringBuilder sb = new StringBuilder();
            int currentLength = 0;
            for (char c : replaceHtml(StringEscapeUtils.unescapeHtml4(str)).toCharArray()) {
                currentLength += String.valueOf(c).getBytes(StringUtils.CHARSET_GBK).length;
                if (currentLength <= maxWidth - 3) {
                    sb.append(c);
                } else {
                    sb.append("...");
                    break;
                }
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 缩略字符串（不区分中英文字符） 自动识别 html 只针对 文本内容
     *
     * <p><div>abcdefg</div> => <div>abc...</div>
     *
     * @param param
     * @param length
     * @return
     */
    public static String smartAbbreviatePlus(final String param, final int length) {
        if (param == null) {
            return null;
        }
        StringBuffer result = new StringBuffer();
        int n = 0;
        char temp;
        boolean isCode = false; // 是不是HTML代码
        boolean isHTML = false; // 是不是HTML特殊字符,如&nbsp;
        {
            int i = 0;
            while (i < param.length()) {
                temp = param.charAt(i);
                if (temp == '<') {
                    isCode = true;
                } else if (temp == '&') {
                    isHTML = true;
                } else if (temp == '>' && isCode) {
                    n = n - 1;
                    isCode = false;
                } else if (temp == ';' && isHTML) {
                    isHTML = false;
                }
                try {
                    if (!isCode && !isHTML) {
                        n += String.valueOf(temp).getBytes(StringUtils.CHARSET_GBK).length;
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if (n <= length - 3) {
                    result.append(temp);
                } else {
                    result.append("...");
                    break;
                }
                i++;
            }
        }
        // 取出截取字符串中的HTML标记
        String temp_result = result.toString().replaceAll("(>)[^<>]*(<?)", "$1$2");
        // 去掉不需要结素标记的HTML标记
        temp_result =
                temp_result.replaceAll(
                        "</?(AREA|BASE|BASEFONT|BODY|BR|COL|COLGROUP|DD|DT|FRAME|HEAD|HR|HTML|IMG|INPUT|ISINDEX|LI|LINK|META|OPTION|P|PARAM|TBODY|TD|TFOOT|TH|THEAD|TR|area|base|basefont|body|br|col|colgroup|dd|dt|frame|head|hr|html|img|input|isindex|li|link|meta|option|p|param|tbody|td|tfoot|th|thead|tr)[^<>]*/?>",
                        "");
        // 去掉成对的HTML标记
        temp_result = temp_result.replaceAll("<([a-zA-Z]+)[^<>]*>(.*?)</\\1>", "$2");
        // 用正则表达式取出标记
        Pattern p = Pattern.compile("<([a-zA-Z]+)[^<>]*>");
        Matcher m = p.matcher(temp_result);
        List<String> endHTML = Lists.newArrayList();
        while (m.find()) {
            endHTML.add(m.group(1));
        }
        // 补全不成对的HTML标记
        for (int i = endHTML.size() - 1; i >= 0; i--) {
            result.append("</");
            result.append(endHTML.get(i));
            result.append(">");
        }
        return result.toString();
    }

    /**
     * 转换为字节数组
     *
     * @param src
     * @return
     */
    public static byte[] getBytes(String src) {
        if (src != null) {
            try {
                return src.getBytes(CHARSET_UTF_8);
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 转换为字节数组
     *
     * @param bytes
     * @return
     */
    public static String toString(byte[] bytes) {
        try {
            return new String(bytes, CHARSET_UTF_8);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * {@link CharSequence} 转为字符串，null安全
     *
     * @param cs {@link CharSequence}
     * @return 字符串
     */
    private static String toString(CharSequence cs) {
        return null == cs ? null : cs.toString();
    }

    /**
     * 是否包含字符串
     *
     * @param src
     * @param strs
     * @return
     */
    public static boolean inString(String src, String... strs) {
        if (src != null) {
            for (String s : strs) {
                if (src.equals(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 替换掉HTML标签方法
     *
     * @param html
     * @return
     */
    public static String replaceHtml(String html) {
        if (isBlank(html)) {
            return null;
        }
        String regEx = "<.+?>";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(html);
        return m.replaceAll("");
    }

    /**
     * 替换结尾字符
     *
     * @param src
     * @param target
     * @return
     */
    public static String replaceEndingCharacter(String src, String target) {
        if (ObjectUtils.isEmpty(target)) {
            target = "*";
        }
        if (ObjectUtils.isNotEmpty(src)) {
            return src.replaceAll(".$", target);
        } else {
            return null;
        }
    }

    /**
     * @param src 预备替换字符串
     * @param prefix 前缀
     * @param suffix 后缀
     * @param map 参数列表
     * @return
     */
    public static String replaceMaps(
            String src, String prefix, String suffix, HashMap<String, Object> map) {
        StringSubstitutor substitutor =
                new StringSubstitutor(map, prefix, suffix, StringSubstitutor.DEFAULT_ESCAPE);
        return substitutor.replace(src);
    }

    /**
     * 替换井号之间的内容
     *
     * @param src 预备替换字符串
     * @param map
     * @return 参数列表
     */
    public static String replaceMaps(String src, HashMap<String, Object> map) {
        String prefix = POUND;
        String suffix = POUND;
        return replaceMaps(src, prefix, suffix, map);
    }

    public static String replaceLast(String regex, String src, String target) {

        // 创建 Pattern 对象
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(src);

        // 记录所有匹配的位置
        int lastMatchStart = -1;
        while (matcher.find()) {
            lastMatchStart = matcher.start();
        }

        if (lastMatchStart != -1) { // 如果找到了
            // 替换最后一次匹配
            return src.substring(0, lastMatchStart)
                    + src.substring(lastMatchStart).replaceAll(regex, target);
        }

        return src;
    }

    /**
     * 隐藏手机号码中间4位
     *
     * @param mobile
     * @return
     */
    public static String hidePhone(String mobile) {
        if (isBlank(mobile)) {
            return null;
        }
        if (mobile.length() < 11) {
            return mobile;
        }
        char[] ch = mobile.toCharArray();
        for (int i = 3; i < 7; i++) {
            ch[i] = '*';
        }
        return new String(ch);
    }

    /**
     * 替换为手机识别的HTML，去掉样式及属性，保留回车。
     *
     * @param html
     * @return
     */
    public static String replaceMobileHtml(String html) {
        if (html == null) {
            return null;
        }
        return html.replaceAll("<([a-z]+?)\\s+?.*?>", "<$1>");
    }

    /**
     * 替换为手机识别的HTML，去掉样式及属性，保留回车。
     *
     * @param src
     * @return
     */
    public static String toHtml(String src) {
        if (src == null) {
            return null;
        }
        return replace(
                replace(EncoderUtils.escapeHtml(src), "\n", "<br/>"), "\t", "&nbsp; &nbsp; ");
    }

    /** 转换为Double类型 */
    public static Double toDouble(Object o) {
        if (o == null) {
            return 0D;
        }
        try {
            return Double.valueOf(trim(o.toString()));
        } catch (Exception e) {
            return 0D;
        }
    }

    /**
     * 过滤字符串null
     *
     * @param src 要过滤的字符串
     * @return 过滤后的字符串
     */
    public static String getString(String src) {
        return src == null ? "" : (src.equals("null") ? "" : src);
    }

    /**
     * 获取 结尾字符
     *
     * @param src
     * @return
     */
    public static String getEndingCharacter(String src) {
        if (src.length() > 1) {
            return substring(src, src.length() - 1);
        } else {
            return src;
        }
    }

    /** 转换为Float类型 */
    public static Float toFloat(Object o) {
        return toDouble(o).floatValue();
    }

    /** 转换为Long类型 */
    public static Long toLong(Object o) {
        return toDouble(o).longValue();
    }

    /** 转换为Integer类型 */
    public static Integer toInteger(Object o) {
        return toLong(o).intValue();
    }

    /**
     * 驼峰命名法工具
     *
     * @return toCamelCase(" hello_world ") == "helloWorld" toCapitalizeCamelCase("hello_world") ==
     *     "HelloWorld" toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }

        s = s.toLowerCase();

        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '_') {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * 驼峰命名法工具
     *
     * @return toCamelCase(" hello_world ") == "helloWorld" toCapitalizeCamelCase("hello_world") ==
     *     "HelloWorld" toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * 驼峰命名法工具
     *
     * @return toCamelCase(" hello_world ") == "helloWorld" toCapitalizeCamelCase("hello_world") ==
     *     "HelloWorld" toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toUnderScoreCase(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            boolean nextUpperCase = true;

            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }

            if ((i > 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    sb.append('_');
                }
                upperCase = true;
            } else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * @param i
     * @return
     */
    public static String toUpperLetter(Integer i) {
        return (char) (64 + i) + "";
    }

    /**
     * 如果不为空，则设置值
     *
     * @param target
     * @param source
     */
    public static void setValueIfNotBlank(String target, String source) {
        if (isNotBlank(source)) {
            target = source;
        }
    }

    /**
     * 转换为JS获取对象值，生成三目运算返回结果
     *
     * @param src 对象串 例如：row.user.id 返回：!row?'':!row.user?'':!row.user.id?'':row.user.id
     */
    public static String jsGetVal(String src) {
        StringBuilder result = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        String[] vals = split(src, ".");
        for (int i = 0; i < vals.length; i++) {
            sb.append("." + vals[i]);
            result.append("!" + (sb.substring(1)) + "?'':");
        }
        result.append(sb.substring(1));
        return result.toString();
    }

    /**
     * 功能:使用urldecode对字符串解码
     *
     * @param src
     * @return
     */
    public static String urlDecode(String src) {
        try {
            if (src == null) {
                return null;
            }
            return URLDecoder.decode(src, CHARSET_UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 功能:使用urlEncode对字符串编码
     *
     * @param src
     * @return
     */
    public static String urlEncode(String src) {
        try {
            return URLEncoder.encode(src, CHARSET_UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 功能:使用unicodeDecode对字符串解码
     *
     * @param src
     * @return
     */
    public static String unicodeDecode(String src) {
        List<String> arrayList = new ArrayList<>();

        String regex = "\\\\u[0-9,a-f,A-F]{4}";

        Pattern p = Pattern.compile(regex);

        Matcher m = p.matcher(src);

        while (m.find()) {
            arrayList.add(m.group());
        }

        for (int i = 0, j = 2; i < arrayList.size(); i++) {

            String code = arrayList.get(i).substring(j, j + 4);

            char ch = (char) Integer.parseInt(code, 16);

            src = src.replace(arrayList.get(i), String.valueOf(ch));
        }

        return src;
    }

    /**
     * 功能:使用unicodeEncode对字符串编码
     *
     * @param src
     * @return
     */
    public static String unicodeEncode(String src) {
        String tmp;
        StringBuffer sb = new StringBuffer(1000);
        char c;
        int i, j;
        sb.setLength(0);
        for (i = 0; i < src.length(); i++) {
            c = src.charAt(i);
            if (c > 255) {
                sb.append("\\u");
                j = (c >>> 8);
                tmp = Integer.toHexString(j);
                if (tmp.length() == 1) sb.append("0");
                sb.append(tmp);
                j = (c & 0xFF);
                tmp = Integer.toHexString(j);
                if (tmp.length() == 1) sb.append("0");
                sb.append(tmp);
            } else {
                sb.append(c);
            }
        }
        return (new String(sb));
    }

    public static String spliceMetadata(
            List<String> list, String delimiter, String prefix, String suffix) {
        return list.stream().collect(Collectors.joining(delimiter, prefix, suffix));
    }

    /**
     * 使用 分隔符 拼接 list 中的 元数据
     *
     * @param list
     * @param delimiter
     * @return
     */
    public static String spliceMetadata(List<String> list, String delimiter) {
        return spliceMetadata(list, delimiter, EMPTY, EMPTY);
    }

    public static String spliceMetadata(List<String> list) {
        return spliceMetadata(list, String.valueOf(COLONS));
    }

    /**
     * 判断 src 是否以 prefix 为开头（忽略大小写）
     *
     * @param src
     * @param prefix
     * @return
     */
    public static boolean startWithIgnoreCase(CharSequence src, CharSequence prefix) {
        return startWith(src, prefix, true);
    }

    /**
     * 判断 src 是否以 prefix 为开头 是否忽略大小写
     *
     * @param src
     * @param prefix
     * @param ignoreCase
     * @return
     */
    public static boolean startWith(CharSequence src, CharSequence prefix, boolean ignoreCase) {
        return startWith(src, prefix, ignoreCase, false);
    }

    /**
     * 判断 src 是否以 prefix 为开头 是否忽略大小写
     *
     * @param src
     * @param prefix
     * @param ignoreCase
     * @return
     */
    public static boolean startWith(
            CharSequence src, CharSequence prefix, boolean ignoreCase, boolean ignoreEquals) {
        if (null != src && null != prefix) {
            boolean isStartWith =
                    src.toString()
                            .regionMatches(ignoreCase, 0, prefix.toString(), 0, prefix.length());
            if (!isStartWith) {
                return false;
            } else {
                return !ignoreEquals || !equals(src, prefix, ignoreCase);
            }
        } else if (ignoreEquals) {
            return false;
        } else {
            return null == src && null == prefix;
        }
    }

    /**
     * 去掉字符包装，如果未被包装则返回原字符串
     *
     * @param src 字符串
     * @param prefix 前置字符
     * @param suffix 后置字符
     * @return 去掉包装字符的字符串
     * @since 4.0.1
     */
    public static String unwrap(CharSequence src, char prefix, char suffix) {
        if (isEmpty(src)) {
            return toString(src);
        }
        if (src.charAt(0) == prefix && src.charAt(src.length() - 1) == suffix) {
            return substring(src, 1, src.length() - 1);
        }
        return src.toString();
    }

    /**
     * 去掉字符包装，如果未被包装则返回原字符串
     *
     * @param src 字符串
     * @param prefix 前置字符串
     * @param suffix 后置字符串
     * @return 去掉包装字符的字符串
     * @since 4.0.1
     */
    public static String unwrap(CharSequence src, String prefix, String suffix) {
        if (isWrap(src, prefix, suffix)) {
            return substring(src, prefix.length(), src.length() - suffix.length());
        }
        return src.toString();
    }

    /**
     * 指定字符串是否被包装
     *
     * @param src 字符串
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 是否被包装
     */
    public static boolean isWrap(CharSequence src, String prefix, String suffix) {
        if (Collections3.hasEmpty(src, prefix, suffix)) {
            return false;
        }
        final String str2 = src.toString();
        return str2.startsWith(prefix) && str2.endsWith(suffix);
    }

    /**
     * 改进JDK subString<br>
     * index从0开始计算，最后一个字符为-1<br>
     * 如果from和to位置一样，返回 null <br>
     * 如果from或to为负数，则按照length从后向前数位置，如果绝对值大于字符串长度，则from归到0，to归到length<br>
     * 如果经过修正的index中from大于to，则互换from和to example: <br>
     * abcdefgh 2 3 =》 c <br>
     * abcdefgh 2 -3 =》 cde <br>
     *
     * @param src String
     * @param start 开始的index（包括）
     * @param end 结束的index（不包括）
     * @return 字串
     */
    public static String substring(CharSequence src, int start, int end) {
        if (isEmpty(src)) {
            return toString(src);
        }
        int len = src.length();

        if (start < 0) {
            start = len + start;
            if (start < 0) {
                start = 0;
            }
        } else if (start > len) {
            start = len;
        }

        if (end < 0) {
            end = len + end;
            if (end < 0) {
                end = len;
            }
        } else if (end > len) {
            end = len;
        }

        if (end < start) {
            int tmp = start;
            start = end;
            end = tmp;
        }

        if (start == end) {
            return null;
        }

        return src.toString().substring(start, end);
    }

    /**
     * 比较两个字符是否相同
     *
     * @param cs1 字符1
     * @param cs2 字符2
     * @param ignoreCase 是否忽略大小写
     * @return 是否相同
     */
    public static boolean equals(CharSequence cs1, CharSequence cs2, boolean ignoreCase) {
        if (null == cs1) {
            return cs2 == null;
        } else if (null == cs2) {
            return false;
        } else {
            return ignoreCase
                    ? cs1.toString().equalsIgnoreCase(cs2.toString())
                    : cs1.toString().contentEquals(cs2);
        }
    }

    /**
     * 去掉字符串头尾部的空白，如果结果字符串是空字符串，则返回 null
     *
     * @param str 被处理的字符串
     * @return 处理后的字符串
     */
    public static String trimWhitespace(final String str) {
        if (isEmpty(str)) {
            return str;
        } else {
            int beginIndex = 0;

            int endIndex;
            for (endIndex = str.length() - 1;
                    beginIndex <= endIndex && Character.isWhitespace(str.charAt(beginIndex));
                    ++beginIndex) {}

            while (endIndex > beginIndex && Character.isWhitespace(str.charAt(endIndex))) {
                --endIndex;
            }

            return str.substring(beginIndex, endIndex + 1);
        }
    }

    /**
     * 去掉字符串中所有的空格
     *
     * @param cs
     * @return
     */
    public static CharSequence trimAllWhitespace(final CharSequence cs) {
        if (isEmpty(cs)) {
            return cs;
        } else {
            int len = cs.length();
            StringBuilder sb = new StringBuilder(cs.length());

            for (int i = 0; i < len; ++i) {
                char c = cs.charAt(i);
                if (!Character.isWhitespace(c)) {
                    sb.append(c);
                }
            }

            return sb.toString();
        }
    }

    /**
     * 去掉字符串中所有的空格
     *
     * @param str
     * @return
     */
    public static String trimAllWhitespace(final String str) {
        return str == null ? null : trimAllWhitespace((CharSequence) str).toString();
    }

    /**
     * 去掉字符串头部的空格
     *
     * @param str
     * @return
     */
    public static String trimLeadingWhitespace(final String str) {
        if (isEmpty(str)) {
            return str;
        } else {
            int beginIdx;
            for (beginIdx = 0;
                    beginIdx < str.length() && Character.isWhitespace(str.charAt(beginIdx));
                    ++beginIdx) {}

            return str.substring(beginIdx);
        }
    }

    /**
     * 去掉字符串尾部的空格
     *
     * @param str
     * @return
     */
    public static String trimTrailingWhitespace(final String str) {
        if (isEmpty(str)) {
            return str;
        } else {
            int endIdx;
            for (endIdx = str.length() - 1;
                    endIdx >= 0 && Character.isWhitespace(str.charAt(endIdx));
                    --endIdx) {}

            return str.substring(0, endIdx + 1);
        }
    }

    /**
     * 去掉字符串头部的指定字符，如果字符串为<code>null</code>，则返回<code>null</code>。
     *
     * @param str 字符串
     * @param leadingCharacter 需要去掉的字符
     * @return 切掉后的字符串，若字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String trimLeadingCharacter(final String str, final char leadingCharacter) {
        if (isEmpty(str)) {
            return str;
        } else {
            int beginIdx;
            for (beginIdx = 0;
                    beginIdx < str.length() && leadingCharacter == str.charAt(beginIdx);
                    ++beginIdx) {}

            return str.substring(beginIdx);
        }
    }

    /**
     * 去掉字符串尾部的指定字符，如果字符串为<code>null</code>，则返回<code>null</code>。
     *
     * @param str 字符串
     * @param trailingCharacter 需要去掉的字符
     * @return 切掉后的字符串，若字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String trimTrailingCharacter(final String str, final char trailingCharacter) {
        if (isEmpty(str)) {
            return str;
        } else {
            int endIdx;
            for (endIdx = str.length() - 1;
                    endIdx >= 0 && trailingCharacter == str.charAt(endIdx);
                    --endIdx) {}

            return str.substring(0, endIdx + 1);
        }
    }

    public static void main(String[] args) {
        // System.out.println(urlEncode("$你好"));

        // System.out.println(replaceIgnoreCase(SmsConstants.VALIDATE_CODE_SMS_TEXT, "#code#",
        // "128768909876"));

        // System.out.println(replaceIgnoreCase(SmsConstants.DEALER_CONTINUED_SMS_TEXT, "#contant#",
        // SmsConstants.WEB_DEALER));

        // HashMap<String, Object> map = Maps.newHashMap();
        // map.put("name", "大魔王");
        // map.put("mobile", "18610880038");
        // System.out.println(replaceMaps("我的电话是#mobile#,我是#name#", map));

        /* ArrayList<String> src = Lists.newArrayList();
        src.add(1, "1");
        src.add(0, "2");
        src.add(2, "3");
        System.out.println(spliceMetadata(src));*/

        // String s = "+8618610880038";
        // System.out.println(s.startsWith("+"));
        // String s =
        // "http://dealer.yunlousoho.com/submit/?productType=PRODUCT_TYPE&dealerId=DEALER_ID&tenantId=TENANT_ID";
        // s = s.replace("PRODUCT_TYPE", "1").replace("DEALER_ID", "2").replace("TENANT_ID", "9");
        // System.out.println(s);

        // System.out.println(replaceEndingCharacter("1", "*"));

        // System.out.println(addFirst("你", "我爱"));

        // System.out.println(toCamelCase("hello_http"));

        // System.out.println(smartAbbreviate("<html><div>okhttps</div><html>",5));
        // System.out.println(smartAbbreviatePlus("<html><div>okhttps</div><html>",5));

        // System.out.println(trimAllWhitespace(" abc exc"));

        // System.out.println(unicodeEncode("新用户账号密码以短信形式发送"));
        System.out.println(
                unicodeDecode(
                        "\u65b0\u7528\u6237\u8d26\u53f7\u5bc6\u7801\u4ee5\u77ed\u4fe1\u5f62\u5f0f\u53d1\u9001"));
    }

    public static boolean hasText(@Nullable CharSequence str) {
        if (str == null) {
            return false;
        }

        int strLen = str.length();
        if (strLen == 0) {
            return false;
        }

        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasText(@Nullable String str) {
        return (str != null && !str.isBlank());
    }
}
