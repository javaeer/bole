package cn.net.yunlou.bole.common.constant;

import cn.net.yunlou.bole.common.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FileName: TaskStatus
 * Description:
 * Created By laughtiger
 * Created At 2026/1/9 21:36
 * Modified By
 * Modified At
 */
@Getter
@AllArgsConstructor
public enum TaskStatus implements IEnum<String> {
    PENDING("pending", "等待中"),
    PROCESSING("processing", "执行中"),
    SUCCESS("success", "成功"),
    FAILED("failed", "失败"),
    CANCELLED("cancelled", "已取消");

    private final String value;

    private final String label;
}
