export const CodeTemplateKey = {
  TEMPLATE_REGISTER: 1,//注册验证码模板
  TEMPLATE_LOGIN: 2,//登录验证码模板
  TEMPLATE_FORGOT: 3,//忘记密码验证码模板
  TEMPLATE_CHANGE_BIND: 4,//换绑类验证码模板
  TEMPLATE_UNBIND: 5,//解绑类验证码模板
} as const;

export type CodeTemplateKey = typeof CodeTemplateKey[ keyof typeof CodeTemplateKey]