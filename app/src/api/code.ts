import { request } from "@/utils/request";
import { EmailSendForm, SmsSendForm } from "@/types/code";

const CODE_BASE_URL = "/code";

const CodeAPI = {

  /**
   * 发送短信验证码
   *
   * @returns
   */
  sendSms(form: SmsSendForm) {
    return request.post(`${CODE_BASE_URL}/send/sms`, form, { skipAuth: true });
  },
  /**
   * 发送短信验证码
   *
   * @returns
   */
  sendEmail(form: EmailSendForm) {
    return request.post(`${CODE_BASE_URL}/send/email`, form, { skipAuth: true });
  },
  /**
   * 发送解绑手机 短信验证码
   *
   * @returns
   */
  sendUnbindSms() {
    return request.post(`${CODE_BASE_URL}/unbind/phone/send`);
  },
  /**
   * 发送解绑邮箱 验证码
   *
   * @returns
   */
  sendUnbindEmail() {
    return request.post(`${CODE_BASE_URL}/unbind/email/send`);
  },

};

export default CodeAPI;


