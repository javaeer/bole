package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * FileName: ChangePasswordRequest Description: Created By laughtiger Created At 2025/11/28 10:34
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ChangePasswordDTO extends BaseDTO {

    @NotBlank(message = "旧密码不得为空")
    private String oldPassword;

    @NotBlank(message = "新密码不得为空")
    private String newPassword;
}
