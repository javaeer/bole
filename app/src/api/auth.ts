import { request } from "@/utils/request";
import {
  EmailRegisterForm,
  LoginForm,
  LoginResult,
  PhoneRegisterForm,
  RegisterForm,
  ResetPasswordForm,
  SmsLoginForm,
  WechatLoginForm,
} from "@/types/user";

const AUTH_BASE_URL = "/auth";


const AuthAPI = {
  /**
   * 登录接口
   */
  async login(data: LoginForm): Promise<LoginResult> {
    console.log("登录请求数据:", JSON.stringify(data));
    return await request.post<LoginResult>(`${AUTH_BASE_URL}/login`, data, { skipAuth: true });
  },
  /**
   * 短信登录接口
   */
  async smsLogin(data: SmsLoginForm): Promise<LoginResult> {
    console.log("登录请求数据:", JSON.stringify(data));
    return await request.post<LoginResult>(`${AUTH_BASE_URL}/login/sms`, data, { skipAuth: true });
  },

  /**
   * 微信登录接口
   */
  async wechatLogin(data: WechatLoginForm): Promise<LoginResult> {
    return await request.post<LoginResult>(`${AUTH_BASE_URL}/login/wechat`, data, { skipAuth: true });
  },

  /**
   * 登出接口
   */
  async logout(): Promise<void> {
    await request.post(`${AUTH_BASE_URL}/logout`, {});
  },

  /**
   * 刷新令牌
   */
  async refreshToken(refreshToken: string): Promise<TokenResult> {
    return await request.post<TokenResult>(
      `${AUTH_BASE_URL}/refresh`,
      { refreshToken: refreshToken },
      { skipAuth: true },
    );
  },

  /**
   * 注册接口
   */
  async register(data: RegisterForm): Promise<LoginResult> {
    return await request.post<LoginResult>(`${AUTH_BASE_URL}/register`, data, { skipAuth: true });
  },
  /**
   * 手机号注册接口
   */
  async registerWithPhone(data: PhoneRegisterForm): Promise<LoginResult> {
    return await request.post<LoginResult>(`${AUTH_BASE_URL}/register/phone`, data, { skipAuth: true });
  },
  /**
   * 邮箱注册接口
   */
  async registerWithEmail(data: EmailRegisterForm): Promise<LoginResult> {
    return await request.post<LoginResult>(`${AUTH_BASE_URL}/register/email`, data, { skipAuth: true });
  },

  /**
   * 重置密码
   */
  async resetPassword(data: ResetPasswordForm): Promise<LoginResult> {
    return await request.post<LoginResult>(`${AUTH_BASE_URL}/password/reset`, data, { skipAuth: true });
  },

  /**
   * 验证令牌是否有效
   */
  verifyToken(): Promise<{ valid: boolean }> {
    return request.get<{ valid: boolean }>(`${AUTH_BASE_URL}/verify`);
  },
  /**
   * 注销接口
   */
  closure(): Promise<void> {
    return request.post<void>(`${AUTH_BASE_URL}/closure`);
  },
};

export default AuthAPI;