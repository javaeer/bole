package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import java.util.List;
import java.util.Map;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_resumes")
public class Resumes extends BaseEntity {

    private Long userId;

    private Long templateId;

    private String status;

    private Integer viewCount;

    private Integer downloadCount;

    /** 样式配置 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> globalStyle;

    /** 布局配置 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> layout;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<ResumesTemplateComponent> components;
}
