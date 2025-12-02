package cn.net.yunlou.bole.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: SkillSearchRequest Description: Created By MR. WANG Created At 2025/11/25 00:08
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@Schema(description = "查询技能请求")
public class SkillSearchRequest extends BaseSearchRequest {}
