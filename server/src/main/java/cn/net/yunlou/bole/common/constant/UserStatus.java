package cn.net.yunlou.bole.common.constant;

import cn.net.yunlou.bole.common.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus implements IEnum<Integer> {
    ACTIVE(1, "激活"),
    INACTIVE(2, "未激活"),
    BANNED(3, "禁用");

    private final Integer value;

    private final String label;
}
