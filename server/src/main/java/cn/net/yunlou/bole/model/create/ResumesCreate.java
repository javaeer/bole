package cn.net.yunlou.bole.model.create;

import cn.net.yunlou.bole.common.BaseCreate;
import cn.net.yunlou.bole.model.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.model.entity.ResumesTemplateLayout;
import cn.net.yunlou.bole.model.entity.ResumesTemplateStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: ResumesAddRequest Description: Created By MR. WANG Created At 2025/11/24 22:18 Modified
 * By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "新增简历请求")
public class ResumesCreate extends BaseCreate {

    private Long templateId;

    private String name;

    private ResumesTemplateStyle globalStyle;

    private ResumesTemplateLayout globalLayout;

    private List<ResumesTemplateComponent> components;
}
