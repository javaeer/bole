package cn.net.yunlou.bole.common.constant;

import cn.net.yunlou.bole.common.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FileName: UsernameType
 * Description:
 * Created By laughtiger
 * Created At 2025/12/27 16:31
 * Modified By
 * Modified At
 */
@Getter
@AllArgsConstructor
public enum IdentifierType implements IEnum<String> {

    PHONE("phone", "手机号"),
    EMAIL("email", "邮箱"),
    USERNAME("username", "用户名");

    private final String value;

    private final String label;
}
