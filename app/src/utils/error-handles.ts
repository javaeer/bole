import { clearUserAll } from "@/utils/store";
import { RequestConfig } from "@/types/request";

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

    console.log("进入业务错误处理，判断是否显示错误信息：" + config.showError + result.message);
    if (config.showError !== false) {
      uni.showToast({
        title: result.message || "请求失败",
        icon: "none",
        duration: 3000,
      });
    }
  }

}

export const errorHandles = ErrorHandles.getInstance();