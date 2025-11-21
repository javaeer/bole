import { getPlatform } from './platform'

// 定义请求配置接口
export interface RequestOptions<T = any> {
	url : string
	method ?: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH'
	data ?: T
	header ?: Record<string, string>
	timeout ?: number
	showLoading ?: boolean
	loadingText ?: string
}

// 定义响应接口
export interface ResponseData<T = any> {
	code : number
	data : T
	message : string
	success : boolean
}

interface UniRequestSuccess {
	data : any
	statusCode : number
	header : Record<string, string>
	cookies ?: string[]
}

interface UniRequestFail {
	errMsg : string
}

// ====== token 获取器 ======
let tokenGetter : (() => string | null) | null = null

export const setTokenGetter = (getter : () => string | null) => {
	tokenGetter = getter
}
//====== 清除 token 回调 =====
let clearTokenCallback : (() => void) | null = null

export const setClearTokenCallback = (callback : () => void) => {
	clearTokenCallback = callback
}

// 基础配置
const TIMEOUT = 10000

// 动态获取 BASE_URL（避免静态初始化问题）
const getBaseUrl = () : string => {
	// #ifdef H5
	return import.meta.env.VITE_APP_BASE_API as string || ''
	// #endif
	// #ifndef H5
	return import.meta.env.VITE_APP_API_URL as string || ''
	// #endif
}

// 请求队列，用于管理全局 loading
let requestQueue = 0

const showLoading = (text : string = '加载中...') => {
	if (requestQueue === 0) {
		uni.showLoading({
			title: text,
			mask: true
		})
	}
	requestQueue++
}

const hideLoading = () => {
	requestQueue--
	if (requestQueue <= 0) {
		uni.hideLoading()
		requestQueue = 0
	}
}

// 获取HTTP错误消息
const getHttpErrorMessage = (statusCode : number) : string => {
	const errorMessages : Record<number, string> = {
		400: '请求参数错误',
		401: '未授权，请重新登录',
		403: '拒绝访问',
		404: '请求地址不存在',
		405: '请求方法不被允许',
		408: '请求超时',
		500: '服务器内部错误',
		502: '网关错误',
		503: '服务不可用',
		504: '网关超时'
	}
	return errorMessages[statusCode] || '网络错误，请稍后重试'
}

// 处理业务错误 从后台获取代码 数组
const handleBusinessError = (data : any) => {
	const errorMessage = data.message || '请求失败'

	switch (data.code) {
		case 401:
			// clearTokenCallback?.()
			// uni.navigateTo({ url: '/pages/login/login' })
			// break
			// 抛出原始错误，供上层判断
			throw new Error(JSON.stringify({ code: data.code, message: errorMessage }))
		case 403:
			uni.showToast({
				title: '没有权限访问',
				icon: 'none'
			})
			break
		default:
			uni.showToast({
				title: errorMessage,
				icon: 'none'
			})
	}
}

// 处理HTTP错误
const handleHttpError = (statusCode : number, data : any) => {
	const message = getHttpErrorMessage(statusCode)

	switch (statusCode) {
		case 401:
			// clearTokenCallback?.()
			// uni.navigateTo({url: '/pages/login/login'})
			// break
			// 抛出原始错误，供上层判断
			throw new Error(JSON.stringify({ code: data.code, message: message }))
		case 404:
			console.error('请求地址不存在:', data)
			break
		case 500:
			console.error('服务器内部错误:', data)
			break
		default:
			console.error(`HTTP错误 ${statusCode}:`, data)
	}

	uni.showToast({
		title: message,
		icon: 'none'
	})
}

// 错误处理
const handleError = (error : any, showLoading : boolean) => {
	if (showLoading) {
		hideLoading()
	}
	console.error('❌ Request Error:', error)

	if (error.errMsg && error.errMsg.includes('request:fail')) {
		uni.showToast({
			title: '网络连接失败，请检查网络设置',
			icon: 'none'
		})
	}

	return Promise.reject(error)
}

// 检查响应数据格式
const isValidResponseData = (data : any) : data is ResponseData => {
	return (
		data &&
		typeof data === 'object' &&
		(typeof data.code === 'number' || typeof data.success === 'boolean')
	)
}

// 请求拦截器
const requestInterceptor = <T>(options : RequestOptions<T>) : RequestOptions<T> => {
	console.log('🚀 Request Interceptor:', options)

	// ✅ 安全获取 token
	const token = tokenGetter?.() || null

	const headers : Record<string, string> = {
		'Content-Type': 'application/json',
		'X-Platform': getPlatform(),
		...options.header
	}

	if (token) {
		headers['Authorization'] = `Bearer ${token}`
	}

	return {
		...options,
		header: headers
	}
}

// 响应拦截器
const responseInterceptor = <T>(response : UniRequestSuccess, showLoading : boolean) : Promise<T> => {
	if (showLoading) {
		hideLoading()
	}

	const { statusCode, data } = response
	console.log('📨 Response Interceptor:', { statusCode, data })

	// HTTP 状态码处理
	if (statusCode >= 200 && statusCode < 300) {
		// 检查数据格式是否符合 ResponseData
		if (isValidResponseData(data)) {
			// 业务状态码处理
			if (data.code === 0 || data.success) {
				return Promise.resolve(data.data)
			} else {
				handleBusinessError(data)
				return Promise.reject(new Error(data.message || '请求失败'))
			}
		} else {
			// 如果数据格式不符合 ResponseData，直接返回原始数据
			return Promise.resolve(data as T)
		}
	} else {
		handleHttpError(statusCode, data)
		return Promise.reject(new Error(getHttpErrorMessage(statusCode)))
	}
}

// 主请求函数
export default function request<T = any>(options : RequestOptions) : Promise<T> {
	const needLoading = !!options.showLoading
	const loadingText = options.loadingText

	if (needLoading) {
		showLoading(loadingText)
	}

	const finalOptions = requestInterceptor({
		timeout: TIMEOUT,
		...options,
		url: `${getBaseUrl()}${options.url}`
	})

	return new Promise((resolve, reject) => {
		uni.request({
			url: finalOptions.url,
			method: finalOptions.method || 'GET',
			data: finalOptions.data,
			header: finalOptions.header as any,
			timeout: finalOptions.timeout,
			success: (res) => {
				responseInterceptor<T>(res, needLoading)
					.then(resolve)
					.catch(reject)
			},
			fail: (err) => {
				handleError(err, needLoading).catch(reject)
			}
		})
	})
}

// 快捷方法
export const http = {
	get: <T = any>(
		url : string,
		data ?: any,
		options ?: Omit<RequestOptions, 'url' | 'method' | 'data'>
	) => request<T>({ ...options, url, method: 'GET', data }),

	post: <T = any>(
		url : string,
		data ?: any,
		options ?: Omit<RequestOptions, 'url' | 'method' | 'data'>
	) => request<T>({ ...options, url, method: 'POST', data }),

	put: <T = any>(
		url : string,
		data ?: any,
		options ?: Omit<RequestOptions, 'url' | 'method' | 'data'>
	) => request<T>({ ...options, url, method: 'PUT', data }),

	delete: <T = any>(
		url : string,
		data ?: any,
		options ?: Omit<RequestOptions, 'url' | 'method' | 'data'>
	) => request<T>({ ...options, url, method: 'DELETE', data }),

	patch: <T = any>(
		url : string,
		data ?: any,
		options ?: Omit<RequestOptions, 'url' | 'method' | 'data'>
	) => request<T>({ ...options, url, method: 'PATCH', data })
}

// 文件上传
export const uploadFile = <T = any>(
	url : string,
	filePath : string,
	formData ?: Record<string, any>,
	fileName : string = 'file',
	options ?: Omit<RequestOptions, 'url' | 'method' | 'data'>
) : Promise<T> => {
	const needLoading = !!options?.showLoading
	if (needLoading) {
		showLoading(options.loadingText)
	}

	// ✅ 安全获取 token
	const token = tokenGetter?.() || null

	const header : Record<string, string> = {
		'X-Platform': getPlatform(),
		...options?.header
	}
	if (token) {
		header['Authorization'] = `Bearer ${token}`
	}

	return new Promise((resolve, reject) => {
		uni.uploadFile({
			url: `${getBaseUrl()}${url}`,
			filePath,
			name: fileName,
			formData: formData as any,
			header: header as any,
			success: (res) => {
				if (needLoading) {
					hideLoading()
				}

				try {
					// 解析响应数据
					const responseData = typeof res.data === 'string' ? JSON.parse(res.data) : res.data

					if (isValidResponseData(responseData)) {
						if (responseData.code === 0 || responseData.success) {
							resolve(responseData.data)
						} else {
							handleBusinessError(responseData)
							reject(new Error(responseData.message || '上传失败'))
						}
					} else {
						// 如果数据格式不符合 ResponseData，直接返回原始数据
						resolve(responseData as T)
					}
				} catch (error) {
					reject(new Error('解析响应数据失败'))
				}
			},
			fail: (err) => {
				handleError(err, needLoading).catch(reject)
			}
		})
	})
}

// 文件下载
export const downloadFile = (
	url : string,
	options ?: Omit<RequestOptions, 'url' | 'method'>
) : Promise<string> => {
	const needLoading = !!options?.showLoading
	if (needLoading) {
		showLoading(options.loadingText)
	}

	// ✅ 安全获取 token
	const token = tokenGetter?.() || null

	const header : Record<string, string> = {
		'X-Platform': getPlatform(),
		...options?.header
	}
	if (token) {
		header['Authorization'] = `Bearer ${token}`
	}

	return new Promise((resolve, reject) => {
		uni.downloadFile({
			url: `${getBaseUrl()}${url}`,
			header: header as any,
			success: (res) => {
				if (needLoading) {
					hideLoading()
				}

				if (res.statusCode === 200) {
					resolve(res.tempFilePath)
				} else {
					handleHttpError(res.statusCode, res.data)
					reject(new Error(getHttpErrorMessage(res.statusCode)))
				}
			},
			fail: (err) => {
				handleError(err, needLoading).catch(reject)
			}
		})
	})
}

// 带重试的请求
export const requestWithRetry = async <T = any>(options : RequestOptions, maxRetries : number = 3) : Promise<T> => {
	for (let attempt = 1; attempt <= maxRetries; attempt++) {
		try {
			return await request<T>(options)
		} catch (error : any) {
			console.log(`请求失败，第 ${attempt} 次重试:`, error)

			if (attempt === maxRetries) {
				throw error
			}

			// 等待一段时间后重试
			await new Promise(resolve => setTimeout(resolve, 1000 * attempt))
		}
	}
	throw new Error('请求失败，已达到最大重试次数')
}

// 网络状态检查
export const checkNetworkStatus = () : Promise<boolean> => {
	return new Promise((resolve) => {
		uni.getNetworkType({
			success: (res) => {
				const networkType = res.networkType
				const isConnected = networkType !== 'none'
				if (!isConnected) {
					uni.showToast({
						title: '网络连接不可用',
						icon: 'none'
					})
				}
				resolve(isConnected)
			},
			fail: () => {
				resolve(false)
			}
		})
	})
}