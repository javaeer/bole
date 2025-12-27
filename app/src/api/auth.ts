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

const AuthAPI = {
  /**
   * 登录接口
   */
  async login(data: LoginForm): Promise<LoginResult> {
    console.log("登录请求数据:", JSON.stringify(data));
    return await request.post<LoginResult>("/auth/login", data, { skipAuth: true });
  },
  /**
   * 短信登录接口
   */
  async smsLogin(data: SmsLoginForm): Promise<LoginResult> {
    console.log("登录请求数据:", JSON.stringify(data));
    return await request.post<LoginResult>("/auth/login-sms", data, { skipAuth: true });
  },

  /**
   * 微信登录接口
   */
  async wechatLogin(data: WechatLoginForm): Promise<LoginResult> {
    return await request.post<LoginResult>("/auth/login-wechat", data, { skipAuth: true });
  },

  /**
   * 登出接口
   */
  async logout(): Promise<void> {
    await request.post("/auth/logout", {});
  },

  /**
   * 刷新令牌
   */
  async refreshToken(refreshToken: string): Promise<TokenResult> {
    return await request.post<TokenResult>(
      "/auth/refresh",
      { refreshToken },
      { skipAuth: true },
    );
  },

  /**
   * 注册接口
   */
  async register(data: RegisterForm): Promise<LoginResult> {
    return await request.post<LoginResult>("/auth/register", data, { skipAuth: true });
  },
  /**
   * 手机号注册接口
   */
  async registerWithPhone(data: PhoneRegisterForm): Promise<LoginResult> {
    return await request.post<LoginResult>("/auth/register-phone", data, { skipAuth: true });
  },
  /**
   * 邮箱注册接口
   */
  async registerWithEmail(data: EmailRegisterForm): Promise<LoginResult> {
    return await request.post<LoginResult>("/auth/register-email", data, { skipAuth: true });
  },

  /**
   * 重置密码
   */
  async resetPassword(data: ResetPasswordForm): Promise<LoginResult> {
    return await request.post<LoginResult>("/auth/reset-password", data, { skipAuth: true });
  },

  /**
   * 验证令牌是否有效
   */
  verifyToken(): Promise<{ valid: boolean }> {
    return request.get<{ valid: boolean }>("/auth/verify");
  },
};

export default AuthAPI;