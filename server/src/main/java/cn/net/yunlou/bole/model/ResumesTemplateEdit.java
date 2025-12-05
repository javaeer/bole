package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseEdit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * FileName: ResumesTemplateEditRequest Description: Created By MR. WANG Created At 2025/11/24 23:57
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "编辑简历模板请求")
public class ResumesTemplateEdit extends BaseEdit {}
