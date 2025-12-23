import { request } from "@/utils/request";
import { LoginForm, LoginResult, RegisterForm, ResetPasswordForm } from "@/types/user";

const AuthAPI = {
	/**
	 * 登录接口
	 */
	async login(data : LoginForm) : Promise<LoginResult> {
		console.log("登录请求数据:", JSON.stringify(data));
		return await request.post<LoginResult>("/auth/login", data, { skipAuth: true });
	},

	/**
	 * 微信登录接口
	 */
	async wechatLogin(code : string) : Promise<LoginResult> {
		return await request.post<LoginResult>("/auth/wechat-login", { code }, { skipAuth: true });
	},

	/**
	 * 登出接口
	 */
	async logout() : Promise<void> {
		await request.post("/auth/logout", {});
	},

	/**
	 * 刷新令牌
	 */
	async refreshToken(refreshToken : string) : Promise<TokenResult> {
		return await request.post<TokenResult>(
			"/auth/refresh",
			{ refreshToken },
			{ skipAuth: true },
		);
	},

	/**
	 * 注册接口
	 */
	async register(data : RegisterForm) : Promise<LoginResult> {
		return await request.post<LoginResult>("/auth/register", data, { skipAuth: true });
	},

	/**
	 * 发送重置密码邮件
	 */
	sendResetPasswordEmail(email : string) : Promise<void> {
		return request.post<void>("/auth/send-reset-email", { email }, { skipAuth: true });
	},

	/**
	 * 重置密码
	 */
	resetPassword(data : ResetPasswordForm) : Promise<void> {
		return request.post<void>("/auth/reset-password", data, { skipAuth: true });
	},

	/**
	 * 验证令牌是否有效
	 */
	verifyToken() : Promise<{ valid : boolean }> {
		return request.get<{ valid : boolean }>("/auth/verify");
	},
};

export default AuthAPI;