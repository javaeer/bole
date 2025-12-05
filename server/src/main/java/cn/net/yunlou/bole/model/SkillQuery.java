package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * FileName: SkillSearchRequest Description: Created By MR. WANG Created At 2025/11/25 00:08
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "查询技能请求")
public class SkillQuery extends BaseQuery {}
