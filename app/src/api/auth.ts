import { get, post } from "@/utils/request";
import { LoginForm, LoginResult, RegisterForm, ResetPasswordForm, TokenResult } from "@/types/user";
import { useUserStore } from "@/stores/user";
import { clearUserAll, setToken } from "@/utils/store";

const AuthAPI = {
  /**
   * 登录接口
   */
  async login(data: LoginForm): Promise<LoginResult> {
    console.log("登录请求数据:", data);
    const response = await post<LoginResult>("/auth/login", data, { skipAuth: true });
    setToken(response);
    return response;
  },

  /**
   * 微信登录接口
   */
  async wechatLogin(code: string): Promise<LoginResult> {
    const response = await post<LoginResult>("/auth/wechat-login", { code }, { skipAuth: true });
    setToken(response);
    return response;
  },

  /**
   * 登出接口
   */
  async logout(): Promise<void> {
    try {
      await post("/auth/logout", {}, { skipAuth: true });
    } catch (error) {
      console.warn("登出请求失败:", error);
    } finally {
      clearUserAll();
    }
  },

  /**
   * 刷新令牌
   */
  async refreshToken(): Promise<string> {
    const userStore = useUserStore();
    if (userStore.isRefreshing) {
      // 如果已经在刷新，返回一个等待的 Promise
      return new Promise((resolve) => {
        userStore.addRefreshSubscriber(resolve);
      });
    }

    userStore.setRefreshing(true);

    try {
      const refreshToken = userStore.getRefreshToken();
      if (!refreshToken) {
        throw new Error("No refresh token available");
      }

      // 调用刷新令牌接口
      const response = await post<TokenResult>(
        "/auth/refresh",
        { refreshToken },
        { skipAuth: true },
      );

      //更新令牌数据
      setToken(response);

      // 通知所有等待的请求
      userStore.onRefreshed(response.accessToken);
      userStore.setRefreshing(false);

      return response.accessToken;
    } catch (error) {
      userStore.setRefreshing(false);
      userStore.refreshSubscribers = []; // 清空等待队列
      throw error;
    }
  },

  /**
   * 获取刷新令牌状态
   */
  getRefreshStatus() {
    const userStore = useUserStore();
    return {
      isRefreshing: userStore.isRefreshing,
      refreshSubscribers: userStore.refreshSubscribers.length,
    };
  },

  /**
   * 注册接口
   */
  async register(data: RegisterForm): Promise<LoginResult> {
    const response = await post<LoginResult>("/auth/register", data, { skipAuth: true });

    // 使用设置用户数据
    setToken(response);

    return response;
  },

  /**
   * 发送重置密码邮件
   */
  sendResetPasswordEmail(email: string): Promise<void> {
    return post<void>("/auth/send-reset-email", { email }, { skipAuth: true });
  },

  /**
   * 重置密码
   */
  resetPassword(data: ResetPasswordForm): Promise<void> {
    return post<void>("/auth/reset-password", data, { skipAuth: true });
  },

  /**
   * 验证令牌是否有效
   */
  verifyToken(): Promise<{ valid: boolean }> {
    return get<{ valid: boolean }>("/auth/verify");
  },
};

export default AuthAPI;