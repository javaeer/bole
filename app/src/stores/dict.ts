import { defineStore } from "pinia";
import DictAPI from "@/api/dict";
import { getDictCache, setDictCache } from "@/utils/store";
import { DictItem, Dict } from "@/types/dict";

export const useDictStore = defineStore("dict", () => {

  const dictionary = ref<Record<string, DictItem[]>>(getDictCache());

  const setDictionary = (dict: Dict) => {
    dictionary.value[dict.dictCode] = dict.dictDataList;
    setDictCache(dictionary.value);
  };

  const loadDictionaries = async () => {
    const dictList = await DictAPI.getList();
    dictList.forEach(setDictionary);
  };

  const getDictionary = (dictCode: string): DictItem[] => {
    return dictionary.value[dictCode] || [];
  };

  const clearDictionaryCache = () => {
    dictionary.value = {};
  };

  const updateDictionaryCache = async () => {
    clearDictionaryCache(); // 先清除旧缓存
    await loadDictionaries(); // 重新加载最新字典数据
  };

  return {
    dictionary,
    setDictionary,
    loadDictionaries,
    getDictionary,
    clearDictionaryCache,
    updateDictionaryCache,
  };
});
