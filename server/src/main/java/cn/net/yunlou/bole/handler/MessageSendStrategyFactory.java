package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.constant.MessageSendType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * FileName: MessageSendStrategyFactory
 * Description:
 * Created By laughtiger
 * Created At 2025/12/24 23:46
 * Modified By
 * Modified At
 */
@Component
@RequiredArgsConstructor
public class MessageSendStrategyFactory {

    /**
     * 这个注入了多个实现类对象
     */
    // @Autowired
    private final List<IMessageSendStrategy> strategies;

    /**
     * 根据类型 获取 消息发送策略
     *
     * @param sendType
     * @return
     */
    public IMessageSendStrategy getMessageSendStrategy(
            MessageSendType sendType) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(sendType))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No strategy found for " + sendType));
    }
}
