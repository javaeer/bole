import { request } from "@/utils/request";
import { ConfigResult } from "@/types/config";


const ConfigAPI = {
	/**
	 * 获取系统配置
	 */
	getConfig() {
		return request.get<ConfigResult>("/config");
	}
};

export default ConfigAPI;