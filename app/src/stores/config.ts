import { defineStore } from "pinia";
import SystemConfigAPI from "@/api/system-config";
import type { SystemConfigItem, SystemConfigResult } from "@/types/config";
import { getConfig, setConfig } from "@/utils/store";

export const useConfigStore = defineStore("system-config", () => {
  // 状态
  const systemConfig = ref<SystemConfigItem[]>([]);
  const loading = ref(false);

  // 计算属性：将配置数组转换为对象，便于通过 key 访问
  const configMap = computed(() => {
    const map: Record<string, string> = {};
    systemConfig.value.forEach(item => {
      map[item.configKey] = item.configValue;
    });
    return map;
  });

  // 获取配置值的方法
  const getConfigValue = (key: string): string => {
    return configMap.value[key] || "";
  };

  // Actions
  const fetchSystemConfig = async (): Promise<SystemConfigResult> => {
    loading.value = true;
    try {
      const response = await SystemConfigAPI.getSystemConfig();
      systemConfig.value = response;

      // 将配置项存入本地存储
      setConfig(response);

      return response;
    } catch (error) {
      console.error("获取应用配置失败:", error);
      throw error;
    } finally {
      loading.value = false;
    }
  };

  const loadConfig = () => {
    return getConfig() || null;
  };

  return {
    // State
    systemConfig,
    loading,

    // Getters
    configMap,
    getConfigValue,

    // Actions
    fetchSystemConfig,
    loadConfig,

  };
});