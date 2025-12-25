package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.MessageEntity;
import cn.net.yunlou.bole.common.constant.MessageSendType;

/**
 * FileName: MessageSendAliStrategy
 * Description:
 * Created By laughtiger
 * Created At 2025/12/25 00:01
 * Modified By
 * Modified At
 */
public class MessageSendAliStrategy implements IMessageSendStrategy {
    @Override
    public boolean send(MessageEntity message) {
        return false;
    }

    @Override
    public boolean supports(MessageSendType sendType) {
        return MessageSendType.AL_SMS == sendType;
    }
}
