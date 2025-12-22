import { RequestConfig } from "@/types/request";
import { useUserStore } from "@/stores/user";
import { hasRefreshToken, isTokenExpiring } from "@/utils/store";
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
  async requestInterceptor<C extends RequestConfig>(config: C): Promise<C> {

    console.log("服务器地址：" + config.baseURL);

    // 跳过认证的请求（如刷新令牌接口，登录等）
    if (config.skipAuth) {
      return config;
    }

    if (config.loading) {
      uni.showLoading({ title: "加载中...", mask: true });
    }

    const userStore = useUserStore();
    let token = userStore.token;

    // 如果令牌即将过期且有刷新令牌，尝试刷新
    if (isTokenExpiring() && hasRefreshToken() && token) {
      try {
        token = await userStore.refreshTokenAction()
      } catch (error) {
        console.warn("刷新令牌失败:", error);
        // 刷新失败不立即跳转，等接口返回401再处理
      }
    }
    if (token){
      config.header = {
        ...config.header,
        Authorization: `Bearer ${token}`,
      };
    }
	
	console.log("请求body参数：" + JSON.stringify(config.data))
	
	console.log("请求头参数：" + JSON.stringify(config.header))
	
	console.log("请求参数" + JSON.stringify(config.params))

    return config;
  }

  //响应拦截器
  async responseInterceptor<T, C extends RequestConfig>(response: UniApp.RequestSuccessCallbackResult, config: C): Promise<T> {

    console.log("进入响应拦截器");

    // 1. 首先打印完整的响应对象
    // console.log("完整响应对象:", response);
    // console.log("响应状态码:", response.statusCode);
    // console.log("响应数据类型:", typeof response.data);

    // 2. 查看原始响应字符串
    // console.log("原始响应数据:", response.data);

// 3. 尝试解析（如果是字符串）
    let parsedData;
    if (typeof response.data === 'string') {
      try {
        parsedData = JSON.parse(response.data);
        console.log("解析后的JSON:", parsedData);
      } catch (e) {
        console.error("JSON解析失败:", e);
        parsedData = response.data;
      }
    } else {
      parsedData = response.data;
    }

    // 4. 查看解析后的结构
    // console.log("解析后的完整结构:", JSON.stringify(parsedData, null, 2));
    // console.log("解析后的code属性:", parsedData?.code);
    // console.log("解析后的所有键:", Object.keys(parsedData || {}));

    // 继续原有逻辑...
    const result = parsedData as ResponseResult<T>;

    // console.log("取得真实数据结构：" + JSON.stringify(result));

    // 关闭 loading
    if (config.loading) {
      uni.hideLoading();
    }

    console.log("返回数据编码：" + result.code);
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