import { request } from "@/utils/request";
import { EmailForm, SmsForm } from "@/types/code";

const CODE_BASE_URL = "/code";

const CodeAPI = {

  /**
   * 发送短信验证码
   *
   * @returns
   */
  sendSms(form: SmsForm) {
    return request.post(`${CODE_BASE_URL}/send-sms`, form, { skipAuth: true });
  },
  /**
   * 发送短信验证码
   *
   * @returns
   */
  sendEmail(form: EmailForm) {
    return request.post(`${CODE_BASE_URL}/send-email`, form, { skipAuth: true });
  },

};

export default CodeAPI;


