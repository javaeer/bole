<!-- components/resumes/Renderer.vue -->
<template>
  <view class="component-renderer">
    <!-- 同步注册所有组件 -->
    <UserBasicInfo
      v-if="componentKey === 'UserBasicInfo'"
      :component-data="componentData"
      :global-style="globalStyle"
      :override-styles="customStyles"
	  :responsive-center="true"
    />

    <WorkExperience
      v-else-if="componentKey === 'WorkExperience'"
      :component-data="componentData"
      :global-style="globalStyle"
      :override-styles="customStyles"
	  :responsive-center="true"
    />

    <EducationExperience
      v-else-if="componentKey === 'EducationExperience'"
      :component-data="componentData"
      :global-style="globalStyle"
      :override-styles="customStyles"
	  :responsive-center="true"
    />

    <Skills
      v-else-if="componentKey === 'Skills'"
      :component-data="componentData"
      :global-style="globalStyle"
      :override-styles="customStyles"
	  :responsive-center="true"
    />

    <ProjectExperience
      v-else-if="componentKey === 'ProjectExperience'"
      :component-data="componentData"
      :global-style="globalStyle"
      :override-styles="customStyles"
	  :responsive-center="true"
    />

    <SelfEvaluation
      v-else-if="componentKey === 'SelfEvaluation'"
      :component-data="componentData"
      :global-style="globalStyle"
      :override-styles="customStyles"
	  :responsive-center="true"
    />

    <JobIntention
      v-else-if="componentKey === 'JobIntention'"
      :component-data="componentData"
      :global-style="globalStyle"
      :override-styles="customStyles"
	  :responsive-center="true"
    />

    <!-- 默认组件 -->
    <view v-else class="unknown-component">
      <text class="unknown-text">{{ componentName || '未知组件' }}</text>
      <text v-if="componentKey" class="unknown-key">({{ componentKey }})</text>
    </view>
  </view>
</template>

<script setup lang="ts">
// 导入所有具体组件
import UserBasicInfo from './UserBasicInfo.vue';
import WorkExperience from './WorkExperience.vue';
import EducationExperience from './EducationExperience.vue';
import Skills from './Skills.vue';
import ProjectExperience from './ProjectExperience.vue';
import SelfEvaluation from './SelfEvaluation.vue';
import JobIntention from './JobIntention.vue';

import { computed } from 'vue';
import type { TemplateGlobalStyle } from '@/types/template';
import { TemplateComponentResult } from "@/types/template-component";

interface Props {
  componentData: TemplateComponentResult;
  globalStyle?: TemplateGlobalStyle;
  customStyles?: Record<string, any>;
}

const props = withDefaults(defineProps<Props>(), {
  globalStyle: () => ({}),
  customStyles: () => ({})
});

// 计算属性
const componentKey = computed(() => props.componentData.key || '');
const componentName = computed(() => props.componentData.name || '未命名组件');

// 现在不需要合并样式，直接传递 customStyles 作为 overrideStyles
</script>

<style scoped lang="scss">
.component-renderer {
  width: 100%;
  box-sizing: border-box;
}

.unknown-component {
  padding: 40px;
  text-align: center;
  background-color: #f9f9f9;
  border: 2px dashed #ddd;
  border-radius: 8px;
  margin: 20px 0;
}

.unknown-text {
  font-size: 16px;
  color: #999;
  margin-bottom: 8px;
}

.unknown-key {
  font-size: 14px;
  color: #ccc;
  font-family: monospace;
}
</style>