<template>
  <wd-picker
    v-if="type === 'select' || type === 'radio'"
    v-model="selectedValue"
    :columns="options"
    :label="label"
    :placeholder="placeholder"
    clearable
    :rules="rules"
    @confirm="handleChange"
  />
  <wd-select-picker
    v-else-if="type === 'checkbox'"
    v-model="selectedValue"
    :columns="options"
    :placeholder="placeholder"
    clearable
    :label="label"
    :rules="rules"
    @confirm="handleChange"
  />
  <wd-input
    v-else
    v-model="selectedValue"
    :label="label"
    clearable
    :placeholder="placeholder"
    :rules="rules"
    @input="handleChange"
  />
</template>

<script setup lang="ts">
import { useDictStore } from "@/stores/dict";

const dictStore = useDictStore();

const props = defineProps({
  code: {
    type: String,
    required: true,
  },
  modelValue: {
    type: [String, Number, Array],
    required: false,
  },
  label: {
    type: String,
    default: "",
  },
  type: {
    type: String,
    default: "select",
    validator: (value: string) => ["select", "radio", "checkbox"].includes(value),
  },
  placeholder: {
    type: String,
    default: "请选择",
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  rules: {
    type: Array as PropType<any[]>,
    default: () => [],
  },
});

const emit = defineEmits(["update:modelValue"]);

const options = ref<Array<{ label: string; value: string | number }>>([]);

const selectedValue = ref<any>(
  typeof props.modelValue === "string" || typeof props.modelValue === "number"
    ? props.modelValue
    : Array.isArray(props.modelValue)
      ? props.modelValue
      : undefined
);

// 监听 modelValue 变化
watch(
  () => props.modelValue,
  (newValue) => {
    if (props.type === "checkbox") {
      selectedValue.value = Array.isArray(newValue) ? newValue : [];
    } else {
      selectedValue.value = newValue?.toString() || "";
    }
  },
  { immediate: true }
);

// 监听 options 变化并重新匹配 selectedValue
watch(
  () => options.value,
  (newOptions) => {
    // options 加载后，确保 selectedValue 可以正确匹配到 options
    if (newOptions.length > 0 && selectedValue.value !== undefined) {
      const matchedOption = newOptions.find((option) => option.value === selectedValue.value);
      if (!matchedOption && props.type !== "checkbox") {
        // 如果找不到匹配项，清空选中
        selectedValue.value = "";
      }
    }
  }
);

// 监听 selectedValue 的变化并触发 update:modelValue
function handleChange(val: any) {
  emit("update:modelValue", val.value);
}

// 获取字典数据
onMounted(async () => {
  if (!props.type) {
    return;
  }
  let dictData = dictStore.getDictionary(props.code);
  options.value = dictData.map((item) => ({
    label: item.label,
    value: item.value,
  }));
});
</script>
