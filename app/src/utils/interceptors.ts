import { RequestConfig } from "@/types/request";
import { useUserStore } from "@/stores/user";
import { hasRefreshToken, isTokenExpiring } from "@/utils/store";
import { ResponseCode } from "@/constants/response-code";
import { errorHandles } from "@/utils/error-handles";
import { BusinessCode } from "@/constants/business-code";

class Interceptors {
	private static instance : Interceptors;

	static getInstance() : Interceptors {
		if (!Interceptors.instance) {
			Interceptors.instance = new Interceptors();
		}
		return Interceptors.instance;
	}

	//请求拦截器
	async requestInterceptor<C extends RequestConfig>(config : C) : Promise<C> {

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
				token = await userStore.refreshTokenAction();
			} catch (error) {
				console.warn("刷新令牌失败:", error);
				// 刷新失败不立即跳转，等接口返回401再处理
			}
		}
		if (token) {
			config.header = {
				...config.header,
				Authorization: `Bearer ${token}`,
			};
		}

		console.log("请求body参数：" + JSON.stringify(config.data));

		console.log("请求头参数：" + JSON.stringify(config.header));

		console.log("请求参数" + JSON.stringify(config.params));

		console.log("请求方法" + JSON.stringify(config.method));


		return config;
	}

	//响应拦截器
	async responseInterceptor<T, C extends RequestConfig>(response : UniApp.RequestSuccessCallbackResult, config : C) : Promise<T> {

		console.log("进入响应拦截器");

		// 确保无论什么状态码都关闭loading
		if (config.loading) {
			uni.hideLoading();
		}

		console.log("原始数据：" + JSON.stringify(response));
		//判断response 状态码
		switch (response.statusCode) {
			case ResponseCode.SUCCESS:
				// 尝试解析（如果是字符串）
				let parsedData;
				if (typeof response.data === "string") {
					try {
						parsedData = JSON.parse(response.data);
					} catch (e) {
						console.error("JSON解析失败:", e);
						parsedData = response.data;
					}
				} else {
					parsedData = response.data;
				}

				// 继续原有逻辑...
				const result = parsedData as ResponseResult<T>;

				console.log("返回数据编码：" + result.code);
				console.log("解析后数据：" + JSON.stringify(result))
				// 业务状态码处理
				switch (result.code) {
					case BusinessCode.SUCCESS:
						console.log("业务数据：" + JSON.stringify(result.data))
						return result.data;
					default:
						throw errorHandles.handleBusinessError(result, config);
				}
			case ResponseCode.UNAUTHORIZED:
				errorHandles.handleUnauthorized();
				throw new RequestError("登录已过期", ResponseCode.UNAUTHORIZED);
			case ResponseCode.INTERNAL_SERVER_ERROR:
				// 处理500服务器错误
				if (config.showError !== false) {
					await uni.showToast({
						title: "服务器内部错误，请稍后重试",
						icon: "none",
						duration: 3000,
					});
				}
				throw new RequestError("服务器内部错误", ResponseCode.INTERNAL_SERVER_ERROR);
			case ResponseCode.BAD_REQUEST:
				// 处理400错误请求
				if (config.showError !== false) {
					await uni.showToast({
						title: "请求参数错误",
						icon: "none",
						duration: 3000,
					});
				}
				throw new RequestError("请求参数错误", ResponseCode.BAD_REQUEST);
			case ResponseCode.FORBIDDEN:
				// 处理403禁止访问
				if (config.showError !== false) {
					await uni.showToast({
						title: "无权限访问",
						icon: "none",
						duration: 3000,
					});
				}
				throw new RequestError("无权限访问", ResponseCode.FORBIDDEN);
			case ResponseCode.NOT_FOUND:
				// 处理404资源不存在
				if (config.showError !== false) {
					await uni.showToast({
						title: "请求的资源不存在",
						icon: "none",
						duration: 3000,
					});
				}
				throw new RequestError("请求的资源不存在", ResponseCode.NOT_FOUND);
			case ResponseCode.REQUEST_TIMEOUT:
				// 处理408请求超时
				if (config.showError !== false) {
					await uni.showToast({
						title: "请求超时，请检查网络连接",
						icon: "none",
						duration: 3000,
					});
				}
				throw new RequestError("请求超时", ResponseCode.REQUEST_TIMEOUT);
			default:
				// 处理其他未知错误
				if (config.showError !== false) {
					await uni.showToast({
						title: `网络错误 (${response.statusCode})`,
						icon: "none",
						duration: 3000,
					});
				}
				throw new RequestError(`网络错误 (${response.statusCode})`, response.statusCode);
		}
	}
}

export const interceptors = Interceptors.getInstance();