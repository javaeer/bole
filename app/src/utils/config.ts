import { useConfigStore } from "@/stores/config";

export class Config {
  private configStore: ReturnType<typeof useConfigStore> | null = null;
  private isInitialized = false;
  private initializationPromise: Promise<void> | null = null;

  constructor() {
    // 不在构造函数中初始化 store
  }

  // 确保 store 已初始化
  private ensureStore() {
    if (!this.configStore) {
      this.configStore = useConfigStore();
    }
  }

  async initConfigHandling(): Promise<void> {
    // 如果正在初始化，返回同一个 Promise
    if (this.initializationPromise) {
      return this.initializationPromise;
    }

    // 如果已经初始化完成，直接返回
    if (this.isInitialized) {
      console.log('配置已初始化，跳过重复操作');
      return;
    }

    this.initializationPromise = this._initialize();
    return this.initializationPromise;
  }

  private async _initialize(): Promise<void> {
    try {
      console.log("🚀 开始初始化系统配置...");

      // 确保 store 已初始化
      this.ensureStore();

      // 先尝试从本地存储加载配置
      this.configStore!.loadConfig();
      console.log("✅ 本地配置加载完成");

      // 然后从服务器获取最新配置
      await this.configStore!.fetchConfig();
      console.log('✅ 服务器配置加载完成:');

      const systemName = this.configStore!.getConfigValue('system.name') || 'bole';
      console.log('🎨 设置应用主题:', systemName);

      this.isInitialized = true;
      console.log('🎉 系统配置初始化完成');
    } catch (error) {
      console.error('❌ 启动时加载配置失败:', error);
      // 重置 Promise，允许重试
      this.initializationPromise = null;
      throw error;
    }
  }

  // 检查是否已初始化
  get initialized(): boolean {
    return this.isInitialized;
  }

  // 重新加载配置
  async reloadConfig(): Promise<void> {
    this.isInitialized = false;
    this.initializationPromise = null;
    await this.initConfigHandling();
  }
}

// 创建实例但不立即初始化
export const configHandler = new Config();