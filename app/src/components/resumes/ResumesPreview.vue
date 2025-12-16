<!-- components/ComponentPreview.vue -->
<template>
  <view class="component-preview" :style="style">
    <view class="component-header">
      <text class="component-title">{{ component?.name }}</text>
      <view class="component-actions">
        <button class="btn-icon" @click="$emit('configure')">⚙️</button>
        <button class="btn-icon" @click="$emit('remove')">×</button>
      </view>
    </view>
    <view class="component-content">
      <view v-if="component?.id === 'UserBasicInfo'" class="preview-placeholder">
        <view class="preview-line">姓名：张三</view>
        <view class="preview-line">电话：138****8888</view>
        <view class="preview-line">邮箱：zhangsan@example.com</view>
        <view class="preview-line">地址：北京市海淀区</view>
      </view>
      <view v-else-if="component?.id === 'WorkExperience'" class="preview-placeholder">
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
      <view v-else-if="component?.id === 'EducationExperience'" class="preview-placeholder">
        <view class="preview-section">
          <view class="preview-line-bold">清华大学</view>
          <view class="preview-line">计算机科学与技术 | 硕士 | 2016.09 - 2019.06</view>
        </view>
        <view class="preview-section">
          <view class="preview-line-bold">北京大学</view>
          <view class="preview-line">软件工程 | 本科 | 2012.09 - 2016.06</view>
        </view>
      </view>
      <view v-else-if="component?.id === 'Skills'" class="preview-placeholder">
        <view class="preview-tags">
          <view class="tag">Vue.js</view>
          <view class="tag">React</view>
          <view class="tag">TypeScript</view>
          <view class="tag">Node.js</view>
          <view class="tag">Webpack</view>
          <view class="tag">uniApp</view>
        </view>
      </view>
      <view v-else-if="component?.id === 'ProjectExperience'" class="preview-placeholder">
        <view class="preview-section">
          <view class="preview-line-bold">电商后台管理系统</view>
          <view class="preview-line">技术栈：Vue3 + TypeScript + Element Plus</view>
          <view class="preview-line">负责前端架构设计和核心模块开发</view>
        </view>
      </view>
      <view v-else-if="component?.id === 'SelfEvaluation'" class="preview-placeholder">
        <view class="preview-line">5年前端开发经验，精通Vue生态...</view>
        <view class="preview-line">具备良好的团队协作能力和沟通能力...</view>
      </view>
      <view v-else-if="component?.id === 'JobIntention'" class="preview-placeholder">
        <view class="preview-line">期望职位：高级前端开发工程师</view>
        <view class="preview-line">期望薪资：25-35K</view>
        <view class="preview-line">工作地点：北京</view>
      </view>
      <view v-else class="preview-placeholder">
        组件预览内容
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
interface Props {
  component?: {
    id: string
    name: string
  }
  style?: Record<string, any>
}

const props = withDefaults(defineProps<Props>(), {
  component: () => ({ id: '', name: '' }),
  style: () => ({})
})

const emit = defineEmits(['remove', 'configure'])
</script>

<style scoped>
.component-preview {
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
}

.component-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
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
</style>