package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: ResetPasswordRequest Description: Created By laughtiger Created At 2025/11/28 10:50
 * Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ResetPasswordDTO extends BaseDTO {

    /** 类型 */
    private String type;

    /** 账号 */
    private String username;

    /** 验证码 */
    private String code;

    /** 新密码 */
    private String newPassword;
}
