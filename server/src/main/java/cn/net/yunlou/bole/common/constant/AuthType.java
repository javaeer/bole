package cn.net.yunlou.bole.common.constant;

import cn.net.yunlou.bole.common.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FileName: AuthType Description: Created By laughtiger Created At 2025/12/28 00:18 Modified By
 * Modified At
 */
@Getter
@AllArgsConstructor
public enum AuthType implements IEnum<Integer> {
    UID(100, "系统跳转"),
    WECHAT(6, "微信授权"),
    EMAIL_CODE(5, "邮箱验证码"),
    EMAIL_PASSWORD(4, "邮箱密码"),
    PHONE_CODE(3, "手机号验证码"),
    PHONE_PASSWORD(2, "手机号短信"),
    USERNAME_PASSWORD(1, "用户名密码");

    private final Integer value;

    private final String label;
}
