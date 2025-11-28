package cn.net.yunlou.bole.model.request;

import cn.net.yunlou.bole.common.BaseQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.swing.*;

/**
 * FileName: ResetPasswordRequest
 * Description:
 * Created By laughtiger
 * Created At 2025/11/28 10:50
 * Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResetPasswordRequest extends BaseQuery {

    /**
     * 验证码
     */
    private Spring verificationCode;

    /**
     * 新密码
     */
    private String newPassword;
}
