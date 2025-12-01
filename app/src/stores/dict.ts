import { defineStore } from "pinia";
import DictAPI from "@/api/dict";
import { clearDict, getDict, setDict } from "@/utils/store";
import { Dict, DictItem } from "@/types/dict";

export const useDictStore = defineStore("dict", () => {
  // 字典数据
  const dict = ref<Record<string, Dict>>(getDict());

  // 加载状态
  const loading = ref(false);

  // 错误信息
  const error = ref<string | null>(null);

  /**
   * 获取字典列表
   */
  const fetchDict = async (): Promise<void> => {
    loading.value = true;
    error.value = null;

    try {
      const dictList = await DictAPI.getList();

      // 清空当前字典
      clearDict();

      // 设置新字典
      dictList.forEach(item => {
        setDict(item);
        // 同时更新 store 状态
        dict.value[item.type] = item;
      });

      console.log('✅ 字典数据加载完成，共加载', dictList.length, '个字典');
    } catch (err) {
      error.value = `加载字典失败: ${err.message}`;
      console.error('❌ 加载字典失败:', err);
      throw err;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 更新字典数据
   */
  const updateDict = async (): Promise<void> => {
    console.log('🔄 更新字典数据...');
    await fetchDict();
  };

  /**
   * 根据类型获取字典项
   */
  const getDictItems = (type: string): DictItem[] => {
    return dict.value[type]?.children || [];
  };

  /**
   * 根据类型和值获取标签
   */
  const getItemLabel = (type: string, value: string): string => {
    const items = getDictItems(type);
    const item = items.find(item => item.value === value);
    return item?.label || value;
  };

  /**
   * 获取字典选项（用于选择器）
   */
  const getDictOptions = (type: string): Array<{ label: string; value: string }> => {
    const items = getDictItems(type);
    return items
      .filter(item => item.state === 1) // 只返回启用的项
      .map(item => ({
        label: item.label,
        value: item.value
      }));
  };

  /**
   * 检查字典是否已加载
   */
  const isDictLoaded = (type?: string): boolean => {
    if (type) {
      return !!dict.value[type];
    }
    return Object.keys(dict.value).length > 0;
  };

  return {
    // 状态
    dict,
    loading,
    error,

    // 方法
    fetchDict,
    updateDict,
    getDictItems,
    getItemLabel,
    getDictOptions,
    isDictLoaded,
  };
});