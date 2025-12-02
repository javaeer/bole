import { defineStore } from "pinia";
import DictAPI from "@/api/dict";
import { clearDict, getDict, setDict } from "@/utils/store";
import { DictData, DictNode, DictResult } from "@/types/dict";

export const useDictStore = defineStore("dict", () => {
  // 字典数据（转换后的扁平结构）
  const dict = ref<DictData>(getDict());

  // 原始树形数据（用于特殊场景）
  const rawDictTree = ref<DictNode[]>([]);

  // 加载状态
  const loading = ref(false);

  // 错误信息
  const error = ref<string | null>(null);

  /**
   * 获取字典列表并转换结构
   */
  const fetchDict = async (): Promise<void> => {
    loading.value = true;
    error.value = null;

    try {
      console.log("🔄 开始获取字典数据...");

      // 调用API
      const response = await DictAPI.getWholeTree() as DictResult;

      // 保存原始树形数据
      rawDictTree.value = response;
      console.log("📦 原始字典数据:", response);

      // 转换为扁平结构
      const transformedData = transformDictData(response);
      console.log("🔄 转换后的字典数据:", transformedData);

      // 清空并设置新字典
      clearDict();
      dict.value = transformedData;
      setDict(transformedData);

      console.log("✅ 字典数据加载完成，共加载", Object.keys(transformedData).length, "个字典类型");
    } catch (err: any) {
      error.value = `加载字典失败: ${err.message}`;
      console.error("❌ 加载字典失败:", err);
      throw err;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 更新字典数据
   */
  const updateDict = async (): Promise<void> => {
    console.log("🔄 更新字典数据...");
    await fetchDict();
  };


  /**
   * 获取特定类型的字典项
   */
  const getDictByType = (type: string): DictResult => {
    const dict = getDict();
    console.log("获取完成，执行转换");
    return dict[type]?.items || [];
  };

  /**
   * 获取字典项的标签
   */
  const getDictLabel = (type: string, value: string): string => {
    const items = getDictByType(type);
    const item = items.find(item => item.value === value);
    return item?.label || value;
  };

  /**
   * 获取字典选项（用于选择器）
   */
  const getDictOptions = (type: string): Array<{ label: string; value: string }> => {
    const items = getDictByType(type);
    return items.map(item => ({
      label: item.label,
      value: item.value,
    }));
  };

  /**
   * 获取所有字典类型
   */
  const getAllDictTypes = (): Array<{ type: string; name: string }> => {
    const dict = getDict();
    return Object.values(dict).map(item => ({
      type: item.type,
      name: item.name,
    }));
  };


  /**
   * 获取字典类型信息
   */
  const getDictTypeInfo = (type: string): { name: string; type: string } | null => {
    const dictType = dict.value[type];
    if (!dictType) return null;

    return {
      name: dictType.name,
      type: dictType.type,
    };
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

  /**
   * 获取原始树形数据（用于特殊场景）
   */
  const getRawDictTree = (): DictNode[] => {
    return rawDictTree.value;
  };

  /**
   * 将API的树形结构转换为前端使用的扁平结构
   */
  const transformDictData = (nodes: DictResult): DictData => {
    console.log("树形结构转换为前端使用的扁平结构");
    const dictData: DictData = {};

    nodes.forEach(node => {
      // 只处理一级节点（parentId === 0）
      if (node.parentId === 0) {
        const dictType: DictType = {
          name: node.name,
          type: node.type,
          items: [],
          rootNode: node,
        };

        // 提取所有子项（children）
        const extractItems = (children: DictNode[]) => {
          children.forEach(child => {
            if (child.state === 1) { // 只提取启用的项
              dictType.items.push({
                value: child.value,
                label: child.label,
                code: child.code,
                state: child.state,
                sort: child.sort,
              });
            }
            // 递归处理更深层的子项（如果有的话）
            if (child.children && child.children.length > 0) {
              extractItems(child.children);
            }
          });
        };

        if (node.children && node.children.length > 0) {
          extractItems(node.children);
        }

        // 按sort排序
        dictType.items.sort((a, b) => (a.sort || 0) - (b.sort || 0));

        // 存储到字典数据中
        dictData[node.type] = dictType;
      }
    });

    return dictData;
  };


  return {
    // 状态
    dict,
    rawDictTree,
    loading,
    error,

    // 方法
    fetchDict,
    updateDict,
    getDictByType,
    getDictLabel,
    getDictOptions,
    getAllDictTypes,
    getDictTypeInfo,
    isDictLoaded,
    getRawDictTree,
  };
});