package cn.net.yunlou.bole.model.edit;

import cn.net.yunlou.bole.common.BaseEdit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * FileName: ResumesEditRequest Description: Created By MR. WANG Created At 2025/11/24 22:21
 * Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "编辑简历请求")
public class ResumesEdit extends BaseEdit {}
