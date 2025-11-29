<template>
  <template v-if="tagType">
    <wd-tag :type="tagType" :round="round">{{ label }}</wd-tag>
  </template>
  <template v-else>
    <view>{{ label }}</view>
  </template>
</template>

<script setup lang="ts">
import { useDictStore } from "@/stores/dict";
const dictStore = useDictStore();

const props = defineProps({
  code: String,
  modelValue: [String, Number],
  round: {
    type: Boolean,
    default: false,
  },
});
type TagType = "success" | "warning" | "primary" | "danger" | "default";
const label = ref("");
const tagType = ref<TagType>();

const getLabelAndTagByValue = async (dictCode: string, value: any) => {
  // 先从本地缓存中获取字典数据
  const dictData = dictStore.getDictionary(dictCode);
  console.log("dictData", dictData);
  // 查找对应的字典项
  const dictEntry = dictData.find((item: any) => item.value == value);
  return {
    label: dictEntry ? dictEntry.label : "",
    tag: dictEntry ? dictEntry.tagType : undefined,
  };
};

// 监听 props 的变化，获取并更新 label 和 tag
const fetchLabelAndTag = async () => {
  const result = await getLabelAndTagByValue(props.code as string, props.modelValue);
  console.log("result", result);
  label.value = result.label;
  if (result.tag === "info") {
    result.tag = "default";
  }
  tagType.value = result.tag as "success" | "warning" | "primary" | "danger" | "default";
};

// 首次挂载时获取字典数据
onMounted(fetchLabelAndTag);

// 当 modelValue 发生变化时重新获取
watch(() => props.modelValue, fetchLabelAndTag);
</script>
