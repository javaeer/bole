import { useDictStore } from "@/stores/dict";
import { DictItem } from "@/types/dict";

export class DictHandler {
  private dictStore: ReturnType<typeof useDictStore> | null = null;
  private isInitialized = false;
  private initializationPromise: Promise<void> | null = null;

  constructor() {
    // 不在构造函数中初始化 store
  }

  /**
   * 确保 store 已初始化
   */
  private ensureStore() {
    if (!this.dictStore) {
      this.dictStore = useDictStore();
    }
  }

  /**
   * 初始化字典数据
   */
  async initDictHandling(): Promise<void> {
    if (this.initializationPromise) {
      return this.initializationPromise;
    }

    if (this.isInitialized) {
      console.log('字典已初始化，跳过重复操作');
      return;
    }

    this.initializationPromise = this._initialize();
    return this.initializationPromise;
  }

  private async _initialize(): Promise<void> {
    try {
      console.log("🚀 开始初始化字典数据...");

      // 确保 store 已初始化
      this.ensureStore();

      // 加载字典数据
      await this.dictStore!.fetchDict();

      console.log('✅ 字典数据初始化完成');
      this.isInitialized = true;
    } catch (error) {
      console.error('❌ 字典初始化失败:', error);
      this.initializationPromise = null;
      throw error;
    }
  }

  /**
   * 重新加载字典
   */
  async reloadDict(): Promise<void> {
    this.isInitialized = false;
    this.initializationPromise = null;
    await this.initDictHandling();
  }

  /**
   * 获取字典项
   */
  getDictItems(type: string): DictItem[] {
    this.ensureStore();
    return this.dictStore!.getDictItems(type);
  }

  /**
   * 获取字典标签
   */
  getDictLabel(type: string, value: string): string {
    this.ensureStore();
    return this.dictStore!.getItemLabel(type, value);
  }

  /**
   * 获取字典选项
   */
  getDictOptions(type: string): Array<{ label: string; value: string }> {
    this.ensureStore();
    return this.dictStore!.getDictOptions(type);
  }

  /**
   * 获取字典类型信息
   */
  getDictTypeInfo(type: string) {
    this.ensureStore();
    return this.dictStore!.getDictTypeInfo(type);
  }

  /**
   * 获取所有字典类型
   */
  getAllDictTypes() {
    this.ensureStore();
    return this.dictStore!.getAllDictTypes();
  }

  /**
   * 检查字典是否已加载
   */
  isDictLoaded(type?: string): boolean {
    this.ensureStore();
    return this.dictStore!.isDictLoaded(type);
  }

  /**
   * 检查是否已初始化
   */
  get initialized(): boolean {
    return this.isInitialized;
  }

  /**
   * 获取原始树形数据（调试用）
   */
  getRawDictTree() {
    this.ensureStore();
    return this.dictStore!.getRawDictTree();
  }
}

// 创建全局实例
export const dictHandler = new DictHandler();