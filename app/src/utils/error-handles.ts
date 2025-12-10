import { clearUserAll } from "@/utils/store";
import { RequestConfig } from "@/types/request";
import { ResultCode } from "@/constants/result-code";

class ErrorHandles {

  private static instance: ErrorHandles;

  // 防止重复跳转的标记
  isRedirecting = false;

  static getInstance(): ErrorHandles {
    if (!ErrorHandles.instance) {
      ErrorHandles.instance = new ErrorHandles();
    }
    return ErrorHandles.instance;
  }

  // 网络错误处理
  handleNetworkError(error: any, config: RequestConfig) {
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
  }

  // 未授权处理
  handleUnauthorized() {
    if (this.isRedirecting) return;

    console.log("令牌失效或过期处理");
    this.isRedirecting = true;

    // 清除用户登录数据
    clearUserAll();

    uni.showToast({
      title: "登录已过期，请重新登录",
      icon: "none",
    });

    // 跳转到登录页
    setTimeout(() => {
      uni.reLaunch({
        url: "/pages/auth/auth",
      });
      setTimeout(() => {
        this.isRedirecting = false;
      }, 1000);
    }, 1500);
  }

  // 业务错误处理
  handleBusinessError<T>(result: ResponseResult<T>, config: RequestConfig) {

    if (config.showError !== false) {
      let status = result.code;
      let message = "";

      switch (status) {
        case ResultCode.UNAUTHORIZED:
          message = "未授权，请重新登录";
          break;
        case ResultCode.FORBIDDEN:
          message = "拒绝访问";
          break;
        case ResultCode.NOT_FOUND:
          message = `请求地址出错: ${config.url}`;
          break;
        case ResultCode.REQUEST_TIMEOUT:
          message = "请求超时";
          break;
        case ResultCode.INTERNAL_SERVER_ERROR:
          message = "服务器内部错误";
          break;
        case ResultCode.BAD_GATEWAY:
          message = "网关错误";
          break;
        case ResultCode.SERVICE_UNAVAILABLE:
          message = "服务不可用";
          break;
        case ResultCode.GATEWAY_TIMEOUT:
          message = "网关超时";
          break;
        default:
          message = `连接错误${status}`;
      }


      uni.showToast({
        title: message || "请求失败",
        icon: "none",
        duration: 3000,
      });
    }

    return new RequestError(
      result.msg || "请求失败",
      result.code,
      result.data,
    );
  }

}

export const errorHandles = ErrorHandles.getInstance();