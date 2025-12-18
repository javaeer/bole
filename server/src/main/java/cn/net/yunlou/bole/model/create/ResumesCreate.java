package cn.net.yunlou.bole.model.create;

import cn.net.yunlou.bole.common.BaseCreate;
import cn.net.yunlou.bole.entity.ResumesTemplateComponent;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.*;

/**
 * FileName: ResumesAddRequest Description: Created By MR. WANG Created At 2025/11/24 22:18 Modified
 * By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "新增简历请求")
public class ResumesCreate extends BaseCreate {

    private Long userId;

    private Long templateId;

    private List<ResumesTemplateComponent> components;
}
