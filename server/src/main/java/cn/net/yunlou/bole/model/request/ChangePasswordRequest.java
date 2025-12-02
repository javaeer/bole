package cn.net.yunlou.bole.model.request;

import cn.net.yunlou.bole.common.BaseQuery;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: ChangePasswordRequest Description: Created By laughtiger Created At 2025/11/28 10:34
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ChangePasswordRequest extends BaseQuery {

    @NotBlank(message = "旧密码不得为空")
    private String oldPassword;

    @NotBlank(message = "新密码不得为空")
    private String newPassword;
}
