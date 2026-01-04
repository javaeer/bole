package cn.net.yunlou.bole.common.constant;

import cn.net.yunlou.bole.common.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FileName: BaseStatus
 * Description:
 * Created By laughtiger
 * Created At 2026/1/3 00:45
 * Modified By
 * Modified At
 */
@Getter
@AllArgsConstructor
public enum StatusType implements IEnum<Integer> {
    USABLE(1, "可用的"),
    DISABLE(1, "禁用的");

    private final Integer value;

    private final String label;
}
