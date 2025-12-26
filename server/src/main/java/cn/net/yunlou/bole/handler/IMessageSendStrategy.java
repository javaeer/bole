package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.MessageEntity;
import cn.net.yunlou.bole.common.constant.MessageSendType;

/**
 * FileName: IMessageSend Description: Created By laughtiger Created At 2025/12/24 23:45 Modified By
 * Modified At
 */
public interface IMessageSendStrategy {

    /** 执行 发送 */
    boolean send(MessageEntity message);

    boolean supports(MessageSendType sendType);
}
