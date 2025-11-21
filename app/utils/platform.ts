/**
 * 平台判断工具
 */
export enum PlatformType {
	WECHAT = 'wechat',
	HARMONY = 'harmony',
	ANDROID = 'android',
	IOS = 'ios',
	H5 = 'h5'
}

let cachedPlatform : PlatformType | null = null

/**
 * 获取当前运行平台
 */
export const getPlatform = () : PlatformType => {
	if (cachedPlatform) {
		return cachedPlatform
	}
	try {
		// #ifdef APP-ANDROID
		cachedPlatform = PlatformType.ANDROID
		// #endif

		// #ifdef APP-IOS
		cachedPlatform = PlatformType.IOS
		// #endif

		// #ifdef APP-HARMONY
		cachedPlatform = PlatformType.HARMONY
		// #endif

		// 特殊处理：如果是小程序环境，检查具体的小程序类型
		// #ifdef MP
		// #ifdef MP-WEIXIN
		cachedPlatform = PlatformType.WECHAT
		// #endif
		// #ifdef MP-TOUTIAO
		// cachedPlatform = PlatformType.TOUTIAO // 如果有需要
		// #endif
		// ... 其他小程序平台
		// #endif

		// #ifdef H5
		cachedPlatform = PlatformType.H5
		// #endif

		// #ifdef APP-PLUS
		// APP-PLUS 包含 Android 和 iOS，上面已经处理过了
		// #endif

	} catch (e) {
		console.error('Failed to get system info:', e)
		cachedPlatform = PlatformType.H5 // Fallback
	}
	return cachedPlatform!
}

// 获取状态栏高度
export const getStatusBarHeight = () : number => {
	const systemInfo = uni.getSystemInfoSync()
	return systemInfo.statusBarHeight || 44
}

// 获取安全区域
export const getSafeArea = () => {
	const systemInfo = uni.getSystemInfoSync()
	return {
		top: systemInfo.safeAreaInsets?.top || 0,
		bottom: systemInfo.safeAreaInsets?.bottom || 0,
		left: systemInfo.safeAreaInsets?.left || 0,
		right: systemInfo.safeAreaInsets?.right || 0
	}
}

export const isHarmony = () => getPlatform() === PlatformType.HARMONY
export const isWeChat = () => getPlatform() === PlatformType.WECHAT
export const isAndroid = () => getPlatform() === PlatformType.ANDROID
export const isIOS = () => getPlatform() === PlatformType.IOS
export const isH5 = () => getPlatform() === PlatformType.H5