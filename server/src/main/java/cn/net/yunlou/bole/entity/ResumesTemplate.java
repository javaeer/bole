package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.List;
import lombok.*;

/**
 * FileName: ResumeTemplate Description: 简历模板 Created By MR. WANG Created At 2025/11/24 20:00
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_resumes_template")
public class ResumesTemplate extends BaseEntity {

    /** 模板名称 */
    private String name;

    /** 模板文件所在路径 */
    private String storePath;

    /** JSON格式的模板配置 */
    private String templateConfig;

    /** 模板分类 */
    private String category;

    /** 框架类型 */
    private String frameworkType;

    /** 是否激活 */
    private Boolean isActive;

    /** 版本号 */
    private String version;

    // 关联的组件列表
    @TableField(exist = false)
    private List<ResumesTemplateComponent> components;
}
