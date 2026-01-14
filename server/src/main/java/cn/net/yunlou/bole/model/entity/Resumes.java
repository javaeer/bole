package cn.net.yunlou.bole.model.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import cn.net.yunlou.bole.common.handler.JsonbTypeHandler;
import cn.net.yunlou.bole.common.handler.JsonbTypeListHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.List;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_resumes", autoResultMap = true, resultMap = "BaseResultMap")
public class Resumes extends BaseEntity {

    private Long userId;

    private Long templateId;

    private String name;

    private String status;

    private Integer viewCount;

    private Integer downloadCount;

    /** 来自模板 */

    /** 样式配置 */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private ResumesTemplateStyle globalStyle;

    /** 布局配置 */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private ResumesTemplateLayout globalLayout;

    /** 组件列表 [包含用户真实数据] */
    @TableField(typeHandler = JsonbTypeListHandler.ResumesTemplateComponentListHandler.class)
    private List<ResumesTemplateComponent> components;

    @TableField(exist = false)
    private ResumesTemplate template;
}
