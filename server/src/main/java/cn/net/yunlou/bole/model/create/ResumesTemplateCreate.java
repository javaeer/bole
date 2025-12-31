package cn.net.yunlou.bole.model.create;

import cn.net.yunlou.bole.common.BaseCreate;
import cn.net.yunlou.bole.model.entity.ResumesTemplateLayout;
import cn.net.yunlou.bole.model.entity.ResumesTemplateStyle;
import cn.net.yunlou.bole.model.ResumesTemplateComponentDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.*;

/**
 * FileName: ResumesTemplateAddRequest Description: Created By MR. WANG Created At 2025/11/24 23:56
 * Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "新增简历模板请求")
public class ResumesTemplateCreate extends BaseCreate {

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
    private ResumesTemplateStyle globalStyle;

    /** 布局配置 */
    private ResumesTemplateLayout globalLayout;

    private List<ResumesTemplateComponentDTO> components;
}
