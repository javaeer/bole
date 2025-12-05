package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseEdit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * FileName: CompanyCommentEditRequest Description: Created By MR. WANG Created At 2025/11/24 23:21
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "编辑企业评论请求")
public class CompanyCommentEdit extends BaseEdit {}
