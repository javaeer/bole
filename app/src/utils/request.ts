import { clearAll, getToken } from "@/utils/store";
import { ResultCode } from "@/constants/result-code";
import { buildUrl } from "@/utils/url";

// H5 使用 VITE_APP_BASE_API 作为代理路径，其他平台使用 VITE_APP_API_URL 作为请求路径
const getBaseApi = (): string => {
  // #ifdef H5
  return import.meta.env.VITE_APP_BASE_API as string;
  // #endif
  return import.meta.env.VITE_APP_API_URL as string;
};

// 防止重复跳转的标记
let isRedirecting = false;

// 请求拦截器
const requestInterceptor = (config: RequestConfig): RequestConfig => {
  if (config.loading) {
    uni.showLoading({ title: "加载中...", mask: true });
  }
  return config;
};

// 响应拦截器
const responseInterceptor = <T>(
  response: UniApp.RequestSuccessCallbackResult,
  config: RequestConfig,
): T => {
  const resData = response.data as ResponseResult<T>;

  // 关闭 loading
  if (config.loading) {
    uni.hideLoading();
  }

  // 业务状态码处理
  switch (resData.code) {
    case ResultCode.SUCCESS:
      return resData.data;

    case ResultCode.UNAUTHORIZED:
      handleUnauthorized();
      throw new RequestError(
        "登录已过期",
        resData.code,
        resData.data,
      );

    default:
      throw handleBusinessError(resData, config);
  }
};

// 未授权处理
const handleUnauthorized = () => {
  if (isRedirecting) return;

  console.log("令牌失效或过期处理");
  isRedirecting = true;
  clearAll();

  uni.showToast({
    title: "登录已过期，请重新登录",
    icon: "none",
  });

  // 跳转到登录页
  setTimeout(() => {
    uni.reLaunch({
      url: "/pages/login/index",
    });
    setTimeout(() => {
      isRedirecting = false;
    }, 1000);
  }, 1500);
};

// 业务错误处理
const handleBusinessError = <T>(resData: ResponseResult<T>, config: RequestConfig) => {
  if (config.showError !== false) {
    uni.showToast({
      title: resData.msg || "请求失败",
      icon: "none",
      duration: 3000,
    });
  }

  return new RequestError(
    resData.msg || "请求失败",
    resData.code,
    resData.data,
  );
};

// 网络错误处理
const handleNetworkError = (error: any, config: RequestConfig) => {
  if (config.loading) {
    uni.hideLoading();
  }

  if (config.showError !== false) {
    uni.showToast({
      title: "网络请求失败",
      icon: "none",
      duration: 2000,
    });
  }

  throw new RequestError("网络请求失败", ResultCode.NETWORK_ERROR, error);
};

// 主请求函数
export default function request<T>(options: RequestConfig): Promise<T> {
  const baseApi = getBaseApi();

  // 请求拦截
  const config = requestInterceptor(options);

  // 构建完整 URL（包含查询参数）
  const url = buildUrl(`${baseApi}${options.url}`, options.params);

  return new Promise((resolve, reject) => {
    uni.request({
      ...config,
      url: url,
      header: {
        "Content-Type": "application/json",
        ...options.header,
        Authorization: getToken() ? `Bearer ${getToken()}` : "",
      },
      success: (response) => {
        console.log("请求成功:", response);
        try {
          const data = responseInterceptor<T>(response, config);
          resolve(data);
        } catch (error) {
          reject(error);
        }
      },
      fail: (error) => {
        console.log("请求失败:", error);
        try {
          handleNetworkError(error, config);
        } catch (error) {
          reject(error);
        }
      },
    });
  });
}

// 便捷方法 - 支持查询参数
export const get = <T>(
  url: string,
  params?: Record<string, any>,
  config?: Omit<RequestConfig, "url" | "params" | "method">,
) => {
  return request<T>({
    url,
    params,
    method: "GET",
    ...config,
  });
};

export const post = <T>(
  url: string,
  data?: any,
  config?: Omit<RequestConfig, "url" | "data" | "method">,
) => {
  return request<T>({
    url,
    data,
    method: "POST",
    ...config,
  });
};

export const put = <T>(
  url: string,
  data?: any,
  config?: Omit<RequestConfig, "url" | "data" | "method">,
) => {
  return request<T>({
    url,
    data,
    method: "PUT",
    ...config,
  });
};

export const del = <T>(
  url: string,
  params?: Record<string, any>,
  config?: Omit<RequestConfig, "url" | "params" | "method">,
) => {
  return request<T>({
    url,
    params,
    method: "DELETE",
    ...config,
  });
};

// 分页查询专用方法
export const page = <T>(
  url: string,
  pageQuery: PageQuery,
  queryData?: any, // 查询条件放在 body 中
  config?: Omit<RequestConfig, "url" | "data" | "params" | "method">,
) => {
  return request<T>({
    url,
    data: queryData, // 查询条件放在 body
    params: pageQuery, // 分页参数放在 query
    method: "POST",
    ...config,
  });
};