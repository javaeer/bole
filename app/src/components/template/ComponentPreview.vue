<!-- components/template/ComponentPreview.vue - 确保包含所有类型 -->
<template>
  <view class="component-preview" :style="mergedStyle">
    <view class="component-header" :style="headerStyle">
      <text class="component-title" :style="titleStyle">{{ component?.name || '未命名区块' }}</text>
      <view class="component-actions" v-if="showActions">
        <button class="btn-icon" @click="$emit('configure')" :style="{ color: accentColor }">⚙️</button>
        <button class="btn-icon" @click="$emit('remove')" :style="{ color: dangerColor }">×</button>
      </view>
    </view>
    <view class="component-content" :style="contentStyle">
      <!-- 根据组件ID显示不同的预览内容 -->
      <template v-if="component?.id === 'UserBasicInfo' || component?.name === '基本信息'">
        <view class="preview-placeholder">
          <view class="preview-line">姓名：张三</view>
          <view class="preview-line">电话：138****8888</view>
          <view class="preview-line">邮箱：zhangsan@example.com</view>
          <view class="preview-line">地址：北京市海淀区</view>
        </view>
      </template>

      <template v-else-if="component?.id === 'WorkExperience' || component?.name === '工作经历'">
        <view class="preview-placeholder">
          <view class="preview-section">
            <view class="preview-line-bold">ABC科技有限公司</view>
            <view class="preview-line">高级前端开发工程师 | 2020.06 - 至今</view>
            <view class="preview-line">负责公司核心产品的开发与维护</view>
          </view>
          <view class="preview-section">
            <view class="preview-line-bold">XYZ互联网公司</view>
            <view class="preview-line">前端开发工程师 | 2018.03 - 2020.05</view>
            <view class="preview-line">参与多个大型项目开发</view>
          </view>
        </view>
      </template>

      <template v-else-if="component?.id === 'EducationExperience' || component?.name === '教育背景'">
        <view class="preview-placeholder">
          <view class="preview-section">
            <view class="preview-line-bold">清华大学</view>
            <view class="preview-line">计算机科学与技术 | 硕士 | 2016.09 - 2019.06</view>
          </view>
          <view class="preview-section">
            <view class="preview-line-bold">北京大学</view>
            <view class="preview-line">软件工程 | 本科 | 2012.09 - 2016.06</view>
          </view>
        </view>
      </template>

      <template v-else-if="component?.id === 'Skills' || component?.name === '技能专长'">
        <view class="preview-placeholder">
          <view class="preview-tags">
            <view class="tag">Vue.js</view>
            <view class="tag">React</view>
            <view class="tag">TypeScript</view>
            <view class="tag">Node.js</view>
            <view class="tag">Webpack</view>
            <view class="tag">uniApp</view>
          </view>
        </view>
      </template>

      <template v-else-if="component?.id === 'ProjectExperience' || component?.name === '项目经验'">
        <view class="preview-placeholder">
          <view class="preview-section">
            <view class="preview-line-bold">电商后台管理系统</view>
            <view class="preview-line">技术栈：Vue3 + TypeScript + Element Plus</view>
            <view class="preview-line">负责前端架构设计和核心模块开发</view>
          </view>
        </view>
      </template>

      <template v-else-if="component?.id === 'SelfEvaluation' || component?.name === '自我评价'">
        <view class="preview-placeholder">
          <view class="preview-line">5年前端开发经验，精通Vue生态...</view>
          <view class="preview-line">具备良好的团队协作能力和沟通能力...</view>
        </view>
      </template>

      <template v-else-if="component?.id === 'JobIntention' || component?.name === '求职意向'">
        <view class="preview-placeholder">
          <view class="preview-line">期望职位：高级前端开发工程师</view>
          <view class="preview-line">期望薪资：25-35K</view>
          <view class="preview-line">工作地点：北京</view>
        </view>
      </template>

      <template v-else>
        <view class="preview-placeholder">
          <text class="placeholder-text">选择组件类型后显示预览内容</text>
        </view>
      </template>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from "vue";

interface Props {
  component?: {
    id: string
    name: string
    config?: any
  }
  style?: Record<string, any>
  globalStyle?: {
    primaryColor?: string
    accentColor?: string
    secondaryColor?: string
    fontFamily?: string
    fontSize?: string
  }
  showActions?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  component: () => ({ id: '', name: '' }),
  style: () => ({}),
  globalStyle: () => ({}),
  showActions: false
})

// 计算样式
const primaryColor = computed(() => props.globalStyle?.primaryColor || '#d4af37');
const accentColor = computed(() => props.globalStyle?.accentColor || '#f7ef8a');
const dangerColor = computed(() => '#f56c6c');

const mergedStyle = computed(() => {
  const baseStyle = {
    marginBottom: '20rpx',
    border: '1px solid #eee',
    borderRadius: '8rpx',
    padding: '16rpx',
    background: 'white',
    transition: 'all 0.3s'
  };

  return {
    ...baseStyle,
    ...props.style
  };
});

const headerStyle = computed(() => ({
  borderBottom: `1px solid ${props.globalStyle?.secondaryColor || '#f0f0f0'}`,
  borderLeft: `3px solid ${primaryColor.value}`
}));

const titleStyle = computed(() => ({
  color: primaryColor.value,
  fontSize: props.globalStyle?.fontSize || '28rpx'
}));

const contentStyle = computed(() => ({
  fontFamily: props.globalStyle?.fontFamily || 'inherit'
}));

const emit = defineEmits(['remove', 'configure'])
</script>

<style scoped>
.component-preview {
  --primary-color: #d4af37;
  --accent-color: #f7ef8a;
  --danger-color: #f56c6c;

  margin-bottom: 20rpx;
  border: 1px solid #eee;
  border-radius: 8rpx;
  padding: 16rpx;
  background: white;
  transition: all 0.3s;
}

.component-preview:hover {
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.1);
}

.component-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
  padding-bottom: 12rpx;
  border-bottom: 1px solid #f0f0f0;
  border-left: 3px solid var(--primary-color);
  padding-left: 8rpx;
}

.component-title {
  font-size: 28rpx;
  font-weight: bold;
  color: var(--primary-color);
}

.component-actions {
  display: flex;
  gap: 8rpx;
}

.btn-icon {
  background: transparent;
  border: none;
  font-size: 24rpx;
  padding: 8rpx;
  border-radius: 4rpx;
  cursor: pointer;
}

.btn-icon:hover {
  background: #f5f5f5;
}

.component-content {
  min-height: 60rpx;
}

.preview-placeholder {
  color: #666;
  font-size: 24rpx;
  line-height: 1.6;
}

.preview-section {
  margin-bottom: 16rpx;
}

.preview-line {
  margin-bottom: 8rpx;
  color: #555;
}

.preview-line-bold {
  margin-bottom: 4rpx;
  font-weight: bold;
  color: #333;
}

.preview-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
}

.tag {
  padding: 4rpx 12rpx;
  background: #e8f4ff;
  border-radius: 4rpx;
  font-size: 22rpx;
  color: #007aff;
}

.placeholder-text {
  color: #999;
  font-style: italic;
}
</style>