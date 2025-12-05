package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseDTO;
import javax.swing.*;
import lombok.*;

/**
 * FileName: ResetPasswordRequest Description: Created By laughtiger Created At 2025/11/28 10:50
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ResetPasswordDTO extends BaseDTO {

    /** 验证码 */
    private Spring verificationCode;

    /** 新密码 */
    private String newPassword;
}
