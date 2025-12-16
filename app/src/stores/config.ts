import { defineStore } from "pinia";
import { ref, computed } from "vue";
import ConfigAPI from "@/api/config";
import type { ConfigResult, ConfigItem } from "@/types/config";
import { getConfig, setConfig, clearConfig } from "@/utils/store";

export const useConfigStore = defineStore("config", () => {
  // 状态
  const config = ref<ConfigResult>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);
  const lastUpdated = ref<number | null>(null);

  // 计算属性：将配置数组转换为对象，便于通过 key 访问
  const configMap = computed(() => {
    const map: Record<string, string> = {};
    config.value.forEach(item => {
      // 避免重复的 key
      if (item.configKey && !map[item.configKey]) {
        map[item.configKey] = item.configValue || "";
      }
    });
    return map;
  });

  // 获取配置值的方法 - 支持默认值
  const getConfigValue = (key: string, defaultValue: string = ""): string => {
    return configMap.value[key] ?? defaultValue;
  };

  // 获取配置值（支持类型转换）
  const getConfigTyped = <T = string>(
    key: string,
    defaultValue: T,
    transform?: (value: string) => T
  ): T => {
    const value = configMap.value[key];
    if (value === undefined) {
      return defaultValue;
    }

    if (transform) {
      try {
        return transform(value);
      } catch {
        console.warn(`配置转换失败: key=${key}, value=${value}`);
        return defaultValue;
      }
    }

    // 默认返回 string，但调用方可以指定类型
    return value as unknown as T;
  };

  // 获取布尔类型配置
  const getBooleanConfig = (key: string, defaultValue: boolean = false): boolean => {
    return getConfigTyped(key, defaultValue, (value) => {
      const lowerValue = value.toLowerCase();
      return lowerValue === 'true' || lowerValue === '1' || lowerValue === 'yes';
    });
  };

  // 获取数字类型配置
  const getNumberConfig = (key: string, defaultValue: number = 0): number => {
    return getConfigTyped(key, defaultValue, (value) => {
      const num = Number(value);
      return isNaN(num) ? defaultValue : num;
    });
  };

  // 获取JSON类型配置
  const getJSONConfig = <T = any>(key: string, defaultValue: T): T => {
    return getConfigTyped(key, defaultValue, (value) => {
      try {
        return JSON.parse(value);
      } catch {
        return defaultValue;
      }
    });
  };

  // 检查配置是否存在
  const hasConfig = (key: string): boolean => {
    return key in configMap.value;
  };

  // 批量获取配置值
  const getConfigValues = (keys: string[]): Record<string, string> => {
    const result: Record<string, string> = {};
    keys.forEach(key => {
      result[key] = getConfigValue(key);
    });
    return result;
  };

  // Actions
  /**
   * 从本地存储加载配置
   */
  const loadConfig = (): ConfigResult | null => {
    try {
      const localConfig = getConfig();
      if (localConfig) {
        config.value = localConfig;
        lastUpdated.value = Date.now();
        return localConfig;
      }
      return null;
    } catch (err) {
      console.error("从本地存储加载配置失败:", err);
      error.value = "加载本地配置失败";
      return null;
    }
  };

  /**
   * 从服务器获取配置
   */
  const fetchConfig = async (forceRefresh: boolean = false): Promise<ConfigResult> => {
    // 如果不强制刷新且有缓存且未过期，直接返回缓存
    if (!forceRefresh && config.value.length > 0) {
      const cacheExpiry = 5 * 60 * 1000; // 5分钟缓存
      if (lastUpdated.value && (Date.now() - lastUpdated.value < cacheExpiry)) {
        return config.value;
      }
    }

    loading.value = true;
    error.value = null;

    try {
      const response = await ConfigAPI.getConfig();

      // 验证响应数据
      if (!Array.isArray(response)) {
        throw new Error("配置数据格式错误");
      }

      // 更新状态
      config.value = response;
      lastUpdated.value = Date.now();

      // 保存到本地存储
      setConfig(response);

      return response;
    } catch (err) {
      const message = err instanceof Error ? err.message : "获取配置失败";
      error.value = message;

      // 尝试从本地存储恢复
      const localConfig = loadConfig();
      if (localConfig) {
        console.warn("使用本地缓存的配置");
        return localConfig;
      }

      throw err;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 初始化配置
   * 先尝试本地缓存，再尝试网络请求
   */
  const initialize = async (): Promise<ConfigResult> => {
    // 先尝试本地缓存
    const localConfig = loadConfig();
    if (localConfig && localConfig.length > 0) {
      console.log("使用本地缓存的配置");
      return localConfig;
    }

    // 本地没有则从网络获取
    try {
      return await fetchConfig();
    } catch (err) {
      console.error("初始化配置失败:", err);
      // 返回空数组，避免阻塞应用启动
      return [];
    }
  };

  /**
   * 手动更新配置项
   */
  const updateConfigItem = (key: string, value: string): void => {
    const index = config.value.findIndex(item => item.configKey === key);

    if (index >= 0) {
      // 更新现有项
      config.value[index] = { ...config.value[index], configValue: value };
    } else {
      // 添加新项
      config.value.push({
        configKey: key,
        configValue: value,
        // 可以根据需要添加其他字段
      } as ConfigItem);
    }

    // 保存到本地存储
    setConfig(config.value);
    lastUpdated.value = Date.now();
  };

  /**
   * 批量更新配置
   */
  const updateConfigBatch = (updates: Record<string, string>): void => {
    const newConfig = [...config.value];

    Object.entries(updates).forEach(([key, value]) => {
      const index = newConfig.findIndex(item => item.configKey === key);

      if (index >= 0) {
        newConfig[index] = { ...newConfig[index], configValue: value };
      } else {
        newConfig.push({
          configKey: key,
          configValue: value,
        } as ConfigItem);
      }
    });

    config.value = newConfig;
    setConfig(newConfig);
    lastUpdated.value = Date.now();
  };

  /**
   * 重置配置
   */
  const resetConfig = (): void => {
    config.value = [];
    lastUpdated.value = null;
    clearConfig();
  };

  /**
   * 重新加载配置（强制刷新）
   */
  const reload = async (): Promise<ConfigResult> => {
    return await fetchConfig(true);
  };

  return {
    // State
    config,
    loading,
    error,
    lastUpdated,

    // Getters
    configMap,
    getConfigValue,
    getConfigTyped,
    getBooleanConfig,
    getNumberConfig,
    getJSONConfig,
    hasConfig,
    getConfigValues,

    // Actions
    fetchConfig,
    loadConfig,
    initialize,
    updateConfigItem,
    updateConfigBatch,
    resetConfig,
    reload,
  };
});