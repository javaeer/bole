import {get} from "@/utils/request"
import { ConfigResult } from "@/types/config";



const ConfigAPI = {
	/**
	 * 获取系统配置
	 */
	getConfig() {
		return get<ConfigResult>("/system-config");
	}
};

export default ConfigAPI;