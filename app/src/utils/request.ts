import { buildUrl } from "@/utils/url";
import { RequestConfig } from "@/types/request";
import { interceptors } from "@/utils/interceptors";
import { mergeConfig } from "@/utils/request-config";
import { errorHandles } from "@/utils/error-handles";

class Request {

	async request<T = any>(config: RequestConfig): Promise<T> {
		console.log("=== 开始请求流程 ===");

		// 不再使用 Promise 构造函数中的 async
		return new Promise((resolve, reject) => {
			console.log("Promise 构造函数开始执行");

			// 将异步逻辑放到 setTimeout 中，确保事件循环
			setTimeout(async () => {
				try {

					// 合并配置
					const mergedConfig: RequestConfig = mergeConfig(config);

					// 请求拦截
					const finalConfig = await interceptors.requestInterceptor(mergedConfig);

					// 构建完整 URL
					const url = buildUrl(`${finalConfig.baseURL}${finalConfig.url}`, finalConfig.params);

					console.log("准备调用 uni.request:", url);

					// 检查 uni.request 是否存在
					if (typeof uni.request !== 'function') {
						console.error("uni.request 不存在！");
						reject(new Error("uni.request 不存在"));
						return;
					}

					// 直接调用 uni.request
					uni.request({
						url: url,
						method: finalConfig.method,
						data: finalConfig.data,
						header: finalConfig.header,
						timeout: finalConfig.timeout,
						success: (response: any) => {
							console.log("=== SUCCESS 回调触发 ===", response.statusCode);
							try {
								const data = interceptors.responseInterceptor<T, RequestConfig>(response, finalConfig);
								resolve(data);
							} catch (error) {
								console.error("响应拦截器错误:", error);
								reject(error);
							}
						},
						fail: (error: any) => {
							console.log("=== FAIL 回调触发 ===", error);
							try {
								// 如果定义了网络错误处理，则调用
								const handledError = errorHandles?.handleNetworkError
									? errorHandles.handleNetworkError(error, finalConfig)
									: error;
								reject(handledError);
							} catch (handlerError) {
								reject(handlerError);
							}
						},
						complete: () => {
							console.log("=== COMPLETE 回调触发 ===");
						}
					});



				} catch (error) {
					console.error("请求准备阶段出错:", error);
					reject(error);
				}
			}, 0); // 使用 setTimeout 确保异步
		});
	}
	// 便捷方法 - 支持查询参数
	get<T = any>(url : string, params ?: any, config ?: Partial<RequestConfig>) : Promise<T> {
		return this.request<T>({
			url: url,
			params: params,
			method: "GET",
			...config,
		});
	};

	post<T = any>(url : string, data ?: any, config ?: Partial<RequestConfig>) : Promise<T> {
		return this.request<T>({
			url: url,
			data: data,
			method: "POST",
			...config,
		});
	};

	put<T = any>(url : string, data ?: any, config ?: Partial<RequestConfig>) : Promise<T> {
		return this.request<T>({
			url: url,
			data: data,
			method: "PUT",
			...config,
		});
	};

	delete<T = any>(url : string, params ?: any, config ?: Partial<RequestConfig>) : Promise<T> {
		return this.request<T>({
			url: url,
			params: params,
			method: "DELETE",
			...config,
		});
	};

	// 分页查询专用方法
	page<T = any>(url : string, params : PageParam, data ?: any, config ?: Partial<RequestConfig>) : Promise<T> {
		return this.request<T>({
			url: url,
			data: data, // 查询条件放在 body
			params: params, // 分页参数放在 query
			method: "POST",
			...config,
		});

	};
}

export const request = new Request();