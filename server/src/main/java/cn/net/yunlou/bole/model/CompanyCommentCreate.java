package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * FileName: CompanyCommentAddRequest Description: Created By MR. WANG Created At 2025/11/24 23:19
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "新增企业评论请求")
public class CompanyCommentCreate extends BaseCreate {

    @NotNull(message = "企业ID不能为空")
    @Schema(description = "企业ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long companyId;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 500, message = "评论内容不能超过500字")
    @Schema(description = "评论内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1分")
    @Max(value = 5, message = "评分最高为5分")
    @Schema(description = "评分", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer score;

    private Boolean anonymous = false;
}
