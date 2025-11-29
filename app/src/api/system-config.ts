import request from "@/utils/request";

import { type SystemConfigResult } from "@/types/system-config";

const SystemConfigAPI = {
	/**
	 * 获取系统配置 
	 */
	getSystemConfig() {
		return request<SystemConfigResult[]>({
			url: "/system-config",
			method: "GET"
		});
	}
};

export default SystemConfigAPI;