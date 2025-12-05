package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseEdit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * FileName: SkillEditRequest Description: Created By MR. WANG Created At 2025/11/25 00:07 Modified
 * By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "编辑技能请求")
public class SkillEdit extends BaseEdit {}
