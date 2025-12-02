package cn.net.yunlou.bole.model.request;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Data;

/**
 * FileName: SystemEditRequest Description: Created By laughtiger Created At 2025/11/28 11:59
 * Modified By Modified At
 */
@Data
public class SystemConfigRequest implements Serializable {

    @NotBlank(message = "键不得为空")
    private String configKey;

    @NotBlank(message = "值不得为空")
    private String configValue;

    @NotBlank(message = "描述不得为空")
    private String configDesc;
}
