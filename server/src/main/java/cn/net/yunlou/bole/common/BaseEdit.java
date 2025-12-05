package cn.net.yunlou.bole.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * FileName: BaseEditRequest Description: Created By MR. WANG Created At 2025/11/24 21:50 Modified
 * By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "编辑请求基类")
public class BaseEdit extends BaseDTO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "编辑主键不得为空")
    private Long id;
}
