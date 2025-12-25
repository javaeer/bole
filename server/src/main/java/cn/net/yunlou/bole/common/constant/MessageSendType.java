package cn.net.yunlou.bole.common.constant;

import cn.net.yunlou.bole.common.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FileName: MessageSendType
 * Description:
 * Created By laughtiger
 * Created At 2025/12/24 23:49
 * Modified By
 * Modified At
 */
@Getter
@AllArgsConstructor
public enum MessageSendType implements IEnum<String> {

    AL_SMS("AL_SMS", "阿里短信"),
    YP_SMS("YP_SMS", "云片短信"),
    EMAIL("EMAIL", "邮件");

    private final String value;

    private final String label;
}
