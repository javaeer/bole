package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseEdit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * FileName: RoleEditRequest Description: Created By MR. WANG Created At 2025/11/25 00:01 Modified
 * By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "编辑角色请求")
public class RoleEdit extends BaseEdit {}
