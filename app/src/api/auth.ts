import request from "@/utils/request";
import { LoginFormData, LoginResult } from "@/types/user";

const AuthAPI = {
	/**
	 * 登录接口
	 *
	 * @param username 用户名
	 * @param password 密码
	 * @returns 返回 token
	 */
	login(data : LoginFormData) : Promise<LoginResult> {
		console.log("data", data);
		return request<LoginResult>({
			url: "/auth/login",
			method: "POST",
			data: data
		});
	},

	/**
	 * 微信登录接口
	 *
	 * @param code 微信登录code
	 * @returns 返回 token
	 */
	wechatLogin(code : string) : Promise<LoginResult> {
		return request<LoginResult>({
			url: "/auth/wechat-login",
			method: "POST",
			data: { code }
		});
	},

	/**
	 * 登出接口
	 */
	logout() : Promise<void> {
		return request({
			url: "/auth/logout",
			method: "DELETE",
		});
	},
};

export default AuthAPI;
