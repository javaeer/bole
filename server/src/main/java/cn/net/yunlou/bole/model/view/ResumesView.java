package cn.net.yunlou.bole.model.view;

import cn.net.yunlou.bole.common.BaseView;
import cn.net.yunlou.bole.entity.ResumesTemplateComponent;
import java.util.List;
import java.util.Map;

import cn.net.yunlou.bole.entity.ResumesTemplateLayout;
import cn.net.yunlou.bole.entity.ResumesTemplateStyle;
import lombok.*;

/**
 * FileName: ResumesDTO Description: Created By MR. WANG Created At 2025/11/26 17:45 Modified By
 * Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ResumesView extends BaseView {

    private Long userId;

    private Long templateId;

    private String status;

    private Integer viewCount;

    private Integer downloadCount;

    /** 样式配置 */
    private ResumesTemplateStyle globalStyle;

    /** 布局配置 */
    private ResumesTemplateLayout globalLayout;

    private List<ResumesTemplateComponent> components;
}
