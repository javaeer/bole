package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;

/**
 * FileName: ResumesTemplateStyle Description: Created By laughtiger Created At 2025/12/17 15:25
 * Modified By Modified At
 */
@Data
public class ResumesTemplateStyle implements Serializable {
    private String theme;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private ResumesTemplateStyleFontSizes fontSizes;

    private String fontFamily;

    private String headerColor;

    private String primaryColor;

    private String accentColor;

    private String secondaryColor;

    private String backgroundColor;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private ResumesTemplateStyleSpacing spacing;
}
