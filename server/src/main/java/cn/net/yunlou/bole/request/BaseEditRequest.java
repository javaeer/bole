package cn.net.yunlou.bole.request;

import cn.net.yunlou.bole.common.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* FileName: BaseEditRequest
* Description: 
* Created By laughtiger
* Created At 2025/11/24 21:50
* Modified By 
* Modified At 
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "编辑请求基类")
public class BaseEditRequest extends BaseModel {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "编辑主键不得为空")
    private Long id;
}
