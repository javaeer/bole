package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseEdit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * FileName: WorkExperienceEditRequest Description: Created By MR. WANG Created At 2025/11/25 00:08
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "编辑工作经历请求")
public class WorkExperienceEdit extends BaseEdit {}
