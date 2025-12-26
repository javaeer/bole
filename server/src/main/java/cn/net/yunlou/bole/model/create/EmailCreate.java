package cn.net.yunlou.bole.model.create;

import cn.net.yunlou.bole.common.BaseCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: SmsCreate Description: Created By laughtiger Created At 2025/12/25 01:13 Modified By
 * Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "邮件请求")
public class EmailCreate extends BaseCreate {

    @NotBlank(message = "邮件地址不得为空")
    @Schema(
            description = "邮件",
            example = "admin@yunlou.net.cn",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;

    @NotNull(message = "模板不能为空")
    @Schema(description = "模板id", example = "id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long templateId;
}
