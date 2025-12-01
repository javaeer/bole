import { defineStore } from "pinia";
import ConfigAPI from "@/api/config";
import type { ConfigResult } from "@/types/config";
import { getConfig, setConfig } from "@/utils/store";

export const useConfigStore = defineStore("config", () => {
  // 状态
  const config = ref<ConfigResult>([]);
  const loading = ref(false);

  // 计算属性：将配置数组转换为对象，便于通过 key 访问
  const configMap = computed(() => {
    const map: Record<string, string> = {};
    config.value.forEach(item => {
      map[item.configKey] = item.configValue;
    });
    return map;
  });

  // 获取配置值的方法
  const getConfigValue = (key: string): string => {
    return configMap.value[key] || "";
  };

  // Actions
  const fetchConfig = async (): Promise<ConfigResult> => {
    loading.value = true;
    try {
      const response = await ConfigAPI.getConfig();
      config.value = response;

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
    config,
    loading,

    // Getters
    configMap,
    getConfigValue,

    // Actions
    fetchConfig,
    loadConfig,

  };
});