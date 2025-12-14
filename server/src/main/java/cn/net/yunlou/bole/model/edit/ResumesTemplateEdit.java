package cn.net.yunlou.bole.model.edit;

import cn.net.yunlou.bole.common.BaseEdit;
import cn.net.yunlou.bole.entity.ResumesTemplateComponent;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.*;

/**
 * FileName: ResumesTemplateEditRequest Description: Created By MR. WANG Created At 2025/11/24 23:57
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "编辑简历模板请求")
public class ResumesTemplateEdit extends BaseEdit {
    /** 模板名称 */
    private String name;

    /** 模板编码 */
    private String code;

    /** 描述 */
    private String description;

    /** 预览图 */
    private String previewImage;

    /** 是否激活 */
    private Boolean isActive;

    /** 版本号 */
    private String version;

    /** 样式配置 */
    private Map<String, Object> globalStyle;

    /** 布局配置 */
    private Map<String, Object> layout;

    private List<ResumesTemplateComponent> components;
}
