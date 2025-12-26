package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.MessageEntity;
import cn.net.yunlou.bole.common.constant.MessageSendType;
import cn.net.yunlou.bole.config.AppYunPianConfig;
import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.model.Result;
import com.yunpian.sdk.model.SmsSingleSend;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * FileName: YunpianMessageSendStrategy Description: Created By laughtiger Created At 2025/12/24
 * 23:59 Modified By Modified At
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageSendYunpianStrategy implements IMessageSendStrategy {

    private final AppYunPianConfig appYunPianConfig;

    @Override
    public boolean send(MessageEntity message) {
        YunpianClient yunpianClient = new YunpianClient(appYunPianConfig.getApiKey()).init();

        Map<String, String> params = yunpianClient.newParam(2);
        params.put(YunpianClient.MOBILE, message.getTo());
        params.put(YunpianClient.TEXT, message.getText());
        Result<SmsSingleSend> r = yunpianClient.sms().single_send(params);
        yunpianClient.close();
        log.info("yunpian code :{},msg :{}", r.getCode(), r.getMsg());
        return r.isSucc();
    }

    /**
     * 指定模板单发
     *
     * @param tplId
     * @param tplValue
     * @param mobile
     * @return
     */
    private boolean tplSingleSend(long tplId, String tplValue, String mobile) {

        log.info("接收号码:{}", mobile);

        YunpianClient yunpianClient = new YunpianClient(appYunPianConfig.getApiKey()).init();

        Map<String, String> params = yunpianClient.newParam(3);
        params.put("tpl_id", String.valueOf(tplId));
        params.put("tpl_value", tplValue);
        params.put("mobile", mobile);
        Result<SmsSingleSend> r = yunpianClient.sms().tpl_single_send(params);
        yunpianClient.close();
        log.info("yunpian code :{},msg :{}", r.getCode(), r.getMsg());
        return r.isSucc();
    }

    @Override
    public boolean supports(MessageSendType sendType) {
        return MessageSendType.YP_SMS == sendType;
    }
}
