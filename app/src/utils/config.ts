import { useConfigStore } from "@/stores/config";
import { ConfigValue, ConfigItem } from "@/types/config";

export class Config {
  private configStore: ReturnType<typeof useConfigStore> | null = null;
  private isInitialized = false;
  private initializationPromise: Promise<void> | null = null;
  private initializationError: Error | null = null;

  constructor() {
    // 不在构造函数中初始化 store
  }

  /**
   * 确保 store 已初始化
   */
  private ensureStore(): void {
    if (!this.configStore) {
      this.configStore = useConfigStore();
    }
  }

  /**
   * 确保配置已初始化
   */
  private ensureInitialized(): void {
    if (!this.isInitialized) {
      throw new Error('配置未初始化，请先调用 initConfigHandling() 方法');
    }

    if (this.initializationError) {
      throw new Error(`配置初始化失败: ${this.initializationError.message}`);
    }
  }

  /**
   * 初始化配置
   */
  async initConfigHandling(forceRefresh: boolean = false): Promise<void> {
    // 如果已经初始化且不强制刷新，直接返回
    if (this.isInitialized && !forceRefresh) {
      console.log('📚 配置已初始化，跳过重复操作');
      return;
    }

    // 如果已经有初始化进行中的 Promise，返回这个 Promise
    if (this.initializationPromise && !forceRefresh) {
      return this.initializationPromise;
    }

    this.initializationError = null;
    this.initializationPromise = this._initialize(forceRefresh);

    try {
      await this.initializationPromise;
    } catch (error) {
      this.initializationError = error as Error;
      this.initializationPromise = null;
      throw error;
    }
  }

  private async _initialize(forceRefresh: boolean): Promise<void> {
    try {
      console.log(`🚀 ${forceRefresh ? '强制' : ''}开始初始化系统配置...`);

      // 确保 store 已初始化
      this.ensureStore();

      // 先尝试从本地存储加载配置
      this.configStore!.loadConfig();
      console.log("✅ 本地配置加载完成");

      // 然后从服务器获取最新配置
      await this.configStore!.fetchConfig(forceRefresh);
      console.log('✅ 服务器配置加载完成');

      // 获取系统名称（示例）
      const systemName = this.configStore!.getConfigValue('system.name');
      if (systemName) {
        console.log('🎨 系统名称:', systemName);
        // 这里可以设置应用主题或其他全局设置
        this.applySystemTheme(systemName);
      }

      this.isInitialized = true;
      console.log('🎉 系统配置初始化完成');
    } catch (error) {
      console.error('❌ 启动时加载配置失败:', error);
      this.isInitialized = false;
      throw error;
    }
  }

  /**
   * 应用系统主题（示例方法）
   */
  private applySystemTheme(systemName: string): void {
    // 根据系统名称应用主题
    // 例如：设置主题色、修改UI等
    console.log(`🎨 应用系统主题: ${systemName}`);

    // 这里可以添加主题切换逻辑
    // uni.setStorageSync('system-theme', systemName);

    // 或者触发主题变更事件
    // uni.$emit('system-theme-change', systemName);
  }

  /**
   * 重新加载配置
   */
  async reloadConfig(): Promise<void> {
    console.log('🔄 重新加载配置...');
    await this.initConfigHandling(true);
  }

  /**
   * 获取配置项值
   */
  getConfigValue(key: string, defaultValue: ConfigValue = ''): ConfigValue {
    this.ensureStore();
    this.ensureInitialized();
    return this.configStore!.getConfigValue(key) || defaultValue;
  }

  /**
   * 获取配置项（包含元数据）
   */
  getConfigItem(key: string): ConfigItem | null {
    this.ensureStore();
    this.ensureInitialized();

    // 如果 store 有 getConfigItem 方法，使用它
    if (typeof (this.configStore as any).getConfigItem === 'function') {
      return (this.configStore as any).getConfigItem(key);
    }

    // 否则只返回值
    const value = this.getConfigValue(key);
    return value ? { key, value } : null;
  }

  /**
   * 获取多个配置项
   */
  getConfigValues(keys: string[]): Record<string, ConfigValue> {
    this.ensureStore();
    this.ensureInitialized();

    const result: Record<string, ConfigValue> = {};

    keys.forEach(key => {
      result[key] = this.getConfigValue(key);
    });

    return result;
  }

  /**
   * 获取所有配置
   */
  getAllConfigs(): Record<string, ConfigValue> {
    this.ensureStore();
    this.ensureInitialized();

    // 如果 store 有 getAllConfigs 方法，使用它
    if (typeof (this.configStore as any).getAllConfigs === 'function') {
      return (this.configStore as any).getAllConfigs();
    }

    // 否则返回空对象，或根据需要实现
    console.warn('getAllConfigs 方法未在 store 中实现');
    return {};
  }

  /**
   * 获取类型化的配置值
   */
  getTypedConfig<T extends ConfigValue>(key: string, defaultValue: T, type: 'string' | 'number' | 'boolean' = 'string'): T {
    const value = this.getConfigValue(key, defaultValue);

    try {
      switch (type) {
        case 'number':
          return (Number(value) || defaultValue) as T;
        case 'boolean':
          return (value === 'true' || value === true) as T;
        case 'string':
        default:
          return String(value) as T;
      }
    } catch (error) {
      console.warn(`配置项 ${key} 类型转换失败，使用默认值`, error);
      return defaultValue;
    }
  }

  /**
   * 检查配置是否存在
   */
  hasConfig(key: string): boolean {
    this.ensureStore();
    this.ensureInitialized();

    const value = this.getConfigValue(key);
    return value !== null && value !== undefined && value !== '';
  }

  /**
   * 检查是否已初始化
   */
  get initialized(): boolean {
    return this.isInitialized && !this.initializationError;
  }

  /**
   * 获取初始化状态
   */
  get initializationStatus(): {
    initialized: boolean;
    loading: boolean;
    error: Error | null;
  } {
    return {
      initialized: this.isInitialized,
      loading: !!this.initializationPromise,
      error: this.initializationError,
    };
  }

  /**
   * 等待初始化完成
   */
  async waitForInitialization(): Promise<void> {
    if (this.initializationPromise) {
      await this.initializationPromise;
    } else if (!this.isInitialized) {
      throw new Error('配置未初始化，请先调用 initConfigHandling()');
    }
  }

  /**
   * 清除配置缓存
   */
  clearConfig(): void {
    this.ensureStore();

    if (typeof (this.configStore as any).clearConfig === 'function') {
      (this.configStore as any).clearConfig();
    }

    this.isInitialized = false;
    this.initializationPromise = null;
    this.initializationError = null;
    console.log('🗑️ 配置缓存已清除');
  }

  /**
   * 设置配置项（仅本地，不保存到服务器）
   */
  setLocalConfig(key: string, value: ConfigValue): void {
    this.ensureStore();

    if (typeof (this.configStore as any).setLocalConfig === 'function') {
      (this.configStore as any).setLocalConfig(key, value);
    } else {
      console.warn('setLocalConfig 方法未在 store 中实现');
    }
  }

  /**
   * 批量设置配置项（仅本地）
   */
  setLocalConfigs(configs: Record<string, ConfigValue>): void {
    Object.entries(configs).forEach(([key, value]) => {
      this.setLocalConfig(key, value);
    });
  }

  /**
   * 监听配置变更（示例）
   */
  onConfigChange(key: string, callback: (newValue: ConfigValue, oldValue: ConfigValue) => void): () => void {
    this.ensureStore();

    // 如果 store 支持监听，使用 store 的方法
    if (typeof (this.configStore as any).onConfigChange === 'function') {
      return (this.configStore as any).onConfigChange(key, callback);
    }

    // 否则，简单的轮询实现（仅示例）
    let lastValue = this.getConfigValue(key);
    const intervalId = setInterval(() => {
      const currentValue = this.getConfigValue(key);
      if (currentValue !== lastValue) {
        callback(currentValue, lastValue);
        lastValue = currentValue;
      }
    }, 1000);

    return () => clearInterval(intervalId);
  }
}

// 创建全局实例
export const configHandler = new Config();

// 导出快捷函数
export const useConfig = () => configHandler;

// 在 Vue 组件中使用的组合式函数
export function useConfigComposable() {
  const handler = configHandler;

  // 初始化配置
  const initConfig = async (forceRefresh = false) => {
    return await handler.initConfigHandling(forceRefresh);
  };

  // 获取配置值
  const getConfigValue = (key: string, defaultValue: ConfigValue = '') => {
    return handler.getConfigValue(key, defaultValue);
  };

  // 获取类型化配置值
  const getTypedConfig = <T extends ConfigValue>(
    key: string,
    defaultValue: T,
    type: 'string' | 'number' | 'boolean' = 'string'
  ) => {
    return handler.getTypedConfig(key, defaultValue, type);
  };

  return {
    // 初始化方法
    initConfig,
    reloadConfig: () => handler.reloadConfig(),
    clearConfig: () => handler.clearConfig(),

    // 获取方法
    getConfigValue,
    getTypedConfig,
    getConfigItem: (key: string) => handler.getConfigItem(key),
    getConfigValues: (keys: string[]) => handler.getConfigValues(keys),
    getAllConfigs: () => handler.getAllConfigs(),

    // 检查方法
    hasConfig: (key: string) => handler.hasConfig(key),

    // 设置方法（本地）
    setLocalConfig: (key: string, value: ConfigValue) =>
      handler.setLocalConfig(key, value),
    setLocalConfigs: (configs: Record<string, ConfigValue>) =>
      handler.setLocalConfigs(configs),

    // 监听方法
    onConfigChange: (key: string, callback: (newValue: ConfigValue, oldValue: ConfigValue) => void) =>
      handler.onConfigChange(key, callback),

    // 状态
    isInitialized: handler.initialized,
    initializationStatus: handler.initializationStatus,
    waitForInitialization: () => handler.waitForInitialization(),
  };
}