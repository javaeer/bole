package cn.net.yunlou.bole.model.query;

import cn.net.yunlou.bole.common.BaseQuery;
import cn.net.yunlou.bole.entity.ResumesTemplateLayout;
import cn.net.yunlou.bole.entity.ResumesTemplateStyle;
import lombok.*;

/**
 * FileName: ResumesTemplateQuery Description: Created By MR. WANG Created At 2025/11/26 17:46
 * Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ResumesTemplateQuery extends BaseQuery {
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
}
