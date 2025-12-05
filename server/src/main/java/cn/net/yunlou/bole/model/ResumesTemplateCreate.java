package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * FileName: ResumesTemplateAddRequest Description: Created By MR. WANG Created At 2025/11/24 23:56
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "新增简历模板请求")
public class ResumesTemplateCreate extends BaseCreate {}
