<template>
  <view>
    <!-- 使用字典选项 -->
    <picker :range="genderOptions" range-key="label" @change="onGenderChange">
      <view>选择性别: {{ selectedGenderLabel }}</view>
    </picker>

    <!-- 显示字典标签 -->
    <view>用户状态: {{ getUserStatusLabel(user.status) }}</view>

    <!-- 显示所有字典类型 -->
    <view v-for="dictType in dictTypes" :key="dictType.type">
      {{ dictType.name }} ({{ dictType.type }})
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { dictHandler } from "@/utils/dict";
import { useDictStore } from "@/stores/dict";

const dictStore = useDictStore()

const user = ref({
  status: '1',
  gender: '1'
})

// 获取性别选项
const genderOptions = computed(() => {
  return dictStore.getDictOptions('sys_sex') || [];
});

// 获取选中的性别标签
const selectedGenderLabel = computed(() => {
  return dictStore.getDictLabel('sys_sex', user.value.gender);
});

// 获取用户状态标签
const getUserStatusLabel = (status: string) => {
  return dictStore.getDictLabel('user_status', status);
};

// 获取所有字典类型
const dictTypes = ref<Array<{ type: string; name: string }>>([]);

onMounted(async () => {
  // 确保字典已加载
  if (!dictStore.isDictLoaded()) {
    await dictHandler.initDictHandling();
  }

  // 获取所有字典类型
  dictTypes.value = dictStore.getAllDictTypes();
});

const onGenderChange = (event: any) => {
  const index = event.detail.value;
  user.value.gender = genderOptions.value[index]?.value || '';
};
</script>