package cn.net.yunlou.bole.common.utils;

import lombok.experimental.UtilityClass;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.math.BigInteger;
import java.util.Map;

@UtilityClass
public class WordStyleUtil {
    
    /**
     * 应用段落样式
     */
    public void applyParagraphStyle(XWPFParagraph paragraph, Map<String, Object> styles) {
        if (styles == null) return;
        
        // 内边距
        if (styles.get("padding") != null) {
            String padding = styles.get("padding").toString();
            int paddingValue = parseSpacing(padding);
            paragraph.setIndentationLeft(paddingValue);
            paragraph.setIndentationRight(paddingValue);
        }
        
        // 行高
        if (styles.get("lineHeight") != null) {
            String lineHeight = styles.get("lineHeight").toString();
            try {
                float lineHeightValue = Float.parseFloat(lineHeight);
                paragraph.setSpacingBetween((int) (lineHeightValue * 240));
            } catch (NumberFormatException e) {
                // 忽略解析错误
            }
        }
    }
    
    /**
     * 应用文本样式
     */
    public void applyTextStyle(XWPFRun run, Map<String, Object> styles) {
        if (styles == null) return;
        
        // 字体大小
        if (styles.get("fontSize") != null) {
            String fontSize = styles.get("fontSize").toString();
            try {
                run.setFontSize(Integer.parseInt(fontSize));
            } catch (NumberFormatException e) {
                // 忽略解析错误
            }
        }
        
        // 文字颜色
        if (styles.get("color") != null || styles.get("fieldColor") != null) {
            String color = (styles.get("color") != null ? 
                styles.get("color") : styles.get("fieldColor")).toString();
            color = color.replace("#", "");
            if (color.length() == 6) {
                run.setColor(color);
            }
        }
    }
    
    /**
     * 解析间距值
     */
    private int parseSpacing(String spacing) {
        if (spacing == null) return 0;
        
        try {
            if (spacing.endsWith("px")) {
                return Integer.parseInt(spacing.replace("px", "")) * 20; // 转换为twips
            } else {
                return Integer.parseInt(spacing) * 20;
            }
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * 创建分隔线
     */
    public void createSeparator(XWPFDocument document) {
        XWPFParagraph separator = document.createParagraph();
        separator.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER);
        
        XWPFRun run = separator.createRun();
        run.setText("─────────────────────────────────────");
        run.setFontSize(6);
        run.setColor("cccccc");
        
        document.createParagraph();
    }
}