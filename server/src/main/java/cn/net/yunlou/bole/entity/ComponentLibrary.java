package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: ComponentLibrary
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/25 18:34
 * Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_component_library")
public class ComponentLibrary extends BaseEntity {
    private String componentType;

    private String componentName;

    private String defaultConfig; // JSON格式

    private String frameworkType;

    private String category;

    private String description;

    private String thumbnail;
}
