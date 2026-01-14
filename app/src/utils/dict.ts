import { useDictStore } from "@/stores/dict";
import { DictItem, DictNode } from "@/types/dict";

export class DictHandler {
  private dictStore: ReturnType<typeof useDictStore> | null = null;
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
    if (!this.dictStore) {
      this.dictStore = useDictStore();
    }
  }

  /**
   * 确保字典数据已初始化
   * @throws {Error} 如果初始化失败
   */
  private ensureInitialized(): void {
    if (!this.isInitialized) {
      throw new Error('字典数据未初始化，请先调用 initDictHandling() 方法');
    }

    if (this.initializationError) {
      throw new Error(`字典初始化失败: ${this.initializationError.message}`);
    }
  }

  /**
   * 初始化字典数据
   */
  async initDictHandling(forceRefresh: boolean = false): Promise<void> {
    // 如果已经初始化且不强制刷新，直接返回
    if (this.isInitialized && !forceRefresh) {
      console.log('📚 字典已初始化，跳过重复操作');
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
      console.log(`🚀 ${forceRefresh ? '强制' : ''}开始初始化字典数据...`);

      // 确保 store 已初始化
      this.ensureStore();

      // 加载字典数据（使用 store 的 initDict 方法，它支持缓存）
      await this.dictStore!.initDict(forceRefresh);

      console.log('✅ 字典数据初始化完成');
      this.isInitialized = true;
    } catch (error) {
      console.error('❌ 字典初始化失败:', error);
      this.isInitialized = false;
      throw error;
    }
  }

  /**
   * 重新加载字典
   */
  async reloadDict(): Promise<void> {
    console.log('🔄 重新加载字典数据...');
    await this.initDictHandling(true);
  }

  /**
   * 获取字典项
   */
  getDictItems(type: string): DictItem[] {
    this.ensureStore();
    this.ensureInitialized();
    // 注意：store 中的方法名是 getDictByType，不是 getDictItems
    return this.dictStore!.getDictByType(type);
  }

  /**
   * 获取字典标签（单个值）
   */
  getDictLabel(type: string, value: string): string {
    this.ensureStore();
    this.ensureInitialized();
    // 注意：store 中的方法名是 getDictLabel
    return this.dictStore!.getDictLabel(type, value);
  }

  /**
   * 获取字典标签（多个值）
   */
  getDictLabels(type: string, values: string | string[]): string | string[] {
    this.ensureStore();
    this.ensureInitialized();

    // 这里需要检查 store 是否有这个方法，如果没有需要添加
    if (typeof (this.dictStore as any).getDictLabels === 'function') {
      return (this.dictStore as any).getDictLabels(type, values);
    }

    // 如果没有 getDictLabels 方法，使用 getDictLabel 实现
    if (Array.isArray(values)) {
      return values.map(value => this.getDictLabel(type, value));
    } else {
      return this.getDictLabel(type, values);
    }
  }

  /**
   * 获取字典选项（用于选择器）
   */
  getDictOptions(type: string): Array<{ label: string; value: string }> {
    this.ensureStore();
    this.ensureInitialized();
    return this.dictStore!.getDictOptions(type);
  }

  /**
   * 根据 code 获取字典项
   */
  getDictItemByCode(type: string, code: string): DictItem | null {
    this.ensureStore();
    this.ensureInitialized();

    // 这里需要检查 store 是否有这个方法，如果没有需要添加
    if (typeof (this.dictStore as any).getDictByCode === 'function') {
      return (this.dictStore as any).getDictByCode(type, code);
    }

    // 如果没有 getDictByCode 方法，手动查找
    const items = this.getDictItems(type);
    return items.find(item => item.code === code) || null;
  }

  /**
   * 获取字典类型信息
   */
  getDictTypeInfo(type: string) {
    this.ensureStore();
    this.ensureInitialized();
    return this.dictStore!.getDictTypeInfo(type);
  }

  /**
   * 获取所有字典类型
   */
  getAllDictTypes() {
    this.ensureStore();
    this.ensureInitialized();
    // 注意：store 中的计算属性名是 allDictTypes，不是 getAllDictTypes
    return this.dictStore!.allDictTypes || [];
  }

  /**
   * 检查字典是否已加载（特定类型或全部）
   */
  isDictLoaded(type?: string): boolean {
    this.ensureStore();
    return this.dictStore!.isDictLoaded(type);
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
   * 获取原始树形数据
   */
  getRawDictTree(): DictNode[] {
    this.ensureStore();
    this.ensureInitialized();
    return this.dictStore!.getRawDictTree();
  }

  /**
   * 获取树形节点
   */
  getTreeNode(type: string): DictNode | null {
    this.ensureStore();
    this.ensureInitialized();

    // 这里需要检查 store 是否有这个方法，如果没有需要添加
    if (typeof (this.dictStore as any).getTreeNode === 'function') {
      return (this.dictStore as any).getTreeNode(type);
    }

    return null;
  }

  /**
   * 清除字典数据
   */
  clearDict(): void {
    this.ensureStore();
    this.dictStore!.clearDictData();
    this.isInitialized = false;
    this.initializationPromise = null;
    this.initializationError = null;
    console.log('🗑️ 字典数据已清除');
  }

  /**
   * 等待初始化完成
   */
  async waitForInitialization(): Promise<void> {
    if (this.initializationPromise) {
      await this.initializationPromise;
    } else if (!this.isInitialized) {
      throw new Error('字典未初始化，请先调用 initDictHandling()');
    }
  }

  /**
   * 创建一个字典映射对象
   */
  createDictMap(type: string): Record<string, string> {
    const items = this.getDictItems(type);
    const map: Record<string, string> = {};

    items.forEach(item => {
      map[item.value] = item.label;
    });

    return map;
  }

  /**
   * 创建一个反向字典映射（label -> value）
   */
  createReverseDictMap(type: string): Record<string, string> {
    const items = this.getDictItems(type);
    const map: Record<string, string> = {};

    items.forEach(item => {
      map[item.label] = item.value;
    });

    return map;
  }
}

// 创建全局实例
export const dictHandler = new DictHandler();

// 导出快捷函数
export const useDict = () => dictHandler;

// 在 Vue 组件中使用的组合式函数
export function useDictComposable() {
  const handler = dictHandler;

  // 初始化字典（在组件 setup 中调用）
  const initDict = async (forceRefresh = false) => {
    return await handler.initDictHandling(forceRefresh);
  };

  // 响应式字典数据获取
  const getDictItems = (type: string) => {
    return handler.getDictItems(type);
  };

  const getDictLabel = (type: string, value: string) => {
    return handler.getDictLabel(type, value);
  };

  const getDictOptions = (type: string) => {
    return handler.getDictOptions(type);
  };

  return {
    // 方法
    initDict,
    reloadDict: () => handler.reloadDict(),
    clearDict: () => handler.clearDict(),

    // 获取方法
    getDictItems,
    getDictLabel,
    getDictLabels: (type: string, values: string | string[]) =>
      handler.getDictLabels(type, values),
    getDictOptions,
    getDictItemByCode: (type: string, code: string) =>
      handler.getDictItemByCode(type, code),

    // 状态
    isInitialized: handler.initialized,
    initializationStatus: handler.initializationStatus,
  };
}