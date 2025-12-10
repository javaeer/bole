import { RequestConfig } from "@/types/request";
import { useUserStore } from "@/stores/user";
import { hasRefreshToken, isTokenExpiring } from "@/utils/store";
import AuthAPI from "@/api/auth";
import { ResultCode } from "@/constants/result-code";
import { errorHandles } from "@/utils/error-handles";

class Interceptors {
  private static instance: Interceptors;

  static getInstance(): Interceptors {
    if (!Interceptors.instance) {
      Interceptors.instance = new Interceptors();
    }
    return Interceptors.instance;
  }

  //请求拦截器
  async requestInterceptor(config: RequestConfig): Promise<RequestConfig> {
    // 跳过认证的请求（如刷新令牌接口，登录等）
    if (config.skipAuth) {
      return {};
    }

    if (config.loading) {
      uni.showLoading({ title: "加载中...", mask: true });
    }

    const userStore = useUserStore();
    let token = userStore.token;

    // 如果令牌即将过期且有刷新令牌，尝试刷新
    if (isTokenExpiring() && hasRefreshToken() && token) {
      try {
        token = await AuthAPI.refreshToken();
      } catch (error) {
        console.warn("刷新令牌失败:", error);
        // 刷新失败不立即跳转，等接口返回401再处理
      }
    }
    config.header = {
      "Content-Type": "application/json",
      ...config.header,
      Authorization: `Bearer ${token}`,
    };

    // 处理FormData
    if (config.isFormData) {
      config.headers = {
        ...config.headers,
        "Content-Type": "multipart/form-data",
      };
    }

    console.log("服务器地址：" + config.baseURL);

    return config;
  }

  //响应拦截器
  async responseInterceptor<T>(response: UniApp.RequestSuccessCallbackResult, config: RequestConfig): Promise<T> {
    const result = response.data as ResponseResult<T>;

    // 关闭 loading
    if (config.loading) {
      uni.hideLoading();
    }

    // 业务状态码处理
    switch (result.code) {
      case ResultCode.SUCCESS:
        return result.data;

      case ResultCode.UNAUTHORIZED:
        errorHandles.handleUnauthorized();
        throw new RequestError(
          "登录已过期",
          result.code,
          result.data,
        );
      default:
        throw errorHandles.handleBusinessError(result, config);
    }
  }

}

export const interceptors = Interceptors.getInstance();