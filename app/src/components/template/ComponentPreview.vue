<template>
  <view class="component-preview" :style="mergedStyle">
    <view class="component-header" :style="headerStyle">
      <text class="component-title" :style="titleStyle">{{ componentName }}</text>
      <view class="component-actions" v-if="showActions">
        <button class="btn-icon" @click="$emit('configure')" :style="{ color: accentColor }">⚙️</button>
        <button class="btn-icon" @click="$emit('remove')" :style="{ color: dangerColor }">×</button>
      </view>
    </view>
    <view class="component-content" :style="contentStyle">
      <!-- 根据组件key显示不同的预览内容 -->
      <template v-if="componentKey === 'UserBasicInfo'">
        <view class="preview-placeholder">
          <view class="preview-avatar" v-if="componentProps?.showAvatar">
            <view class="avatar-placeholder">👤</view>
          </view>
          <view class="preview-info">
            <view class="preview-line">姓名：伯乐大师</view>
            <view class="preview-line" v-if="componentProps?.showContact">电话：138****8888</view>
            <view class="preview-line" v-if="componentProps?.showContact">邮箱：zhangsan@example.com</view>
            <view class="preview-line">地址：北京市海淀区</view>
          </view>
        </view>
      </template>

      <template v-else-if="componentKey === 'WorkExperience'">
        <view class="preview-placeholder">
          <view class="preview-section">
            <view class="preview-line-bold">伯乐简历大师科技有限公司</view>
            <view class="preview-line" v-if="componentProps?.showDuration">高级前端开发工程师 | 2020.06 - 至今</view>
            <view class="preview-line" v-else>高级前端开发工程师</view>
            <view class="preview-line">负责公司核心产品的开发与维护</view>
          </view>
          <view class="preview-section">
            <view class="preview-line-bold">XYZ互联网公司</view>
            <view class="preview-line" v-if="componentProps?.showDuration">前端开发工程师 | 2018.03 - 2020.05</view>
            <view class="preview-line" v-else>前端开发工程师</view>
            <view class="preview-line">参与多个大型项目开发</view>
          </view>
        </view>
      </template>

      <template v-else-if="componentKey === 'EducationExperience'">
        <view class="preview-placeholder">
          <view class="preview-section">
            <view class="preview-line-bold">清华大学</view>
            <view class="preview-line" v-if="componentProps?.showTime">计算机科学与技术 | 硕士 | 2016.09 - 2019.06</view>
            <view class="preview-line" v-else-if="componentProps?.showDegree">计算机科学与技术 | 硕士</view>
            <view class="preview-line" v-else>计算机科学与技术</view>
          </view>
          <view class="preview-section">
            <view class="preview-line-bold">北京大学</view>
            <view class="preview-line" v-if="componentProps?.showTime">软件工程 | 本科 | 2012.09 - 2016.06</view>
            <view class="preview-line" v-else-if="componentProps?.showDegree">软件工程 | 本科</view>
            <view class="preview-line" v-else>软件工程</view>
          </view>
        </view>
      </template>

      <template v-else-if="componentKey === 'Skills'">
        <view class="preview-placeholder">
          <view class="preview-tags">
            <view class="tag" v-for="tag in skillTags" :key="tag">{{ tag }}</view>
          </view>
          <view v-if="componentProps?.skillLevel" class="skill-levels">
            <view class="skill-item">
              <text class="skill-name">Vue.js</text>
              <view class="skill-level-bar">
                <view class="skill-level-fill" :style="{ width: '90%' }"></view>
              </view>
            </view>
            <view class="skill-item">
              <text class="skill-name">React</text>
              <view class="skill-level-bar">
                <view class="skill-level-fill" :style="{ width: '80%' }"></view>
              </view>
            </view>
          </view>
        </view>
      </template>

      <template v-else-if="componentKey === 'ProjectExperience'">
        <view class="preview-placeholder">
          <view class="preview-section">
            <view class="preview-line-bold">电商后台管理系统</view>
            <view class="preview-line" v-if="componentProps?.showRole">技术负责人 | 2021.03 - 至今</view>
            <view class="preview-line">技术栈：Vue3 + TypeScript + Element Plus</view>
            <view v-if="componentProps?.showTechnologies" class="preview-tech-tags">
              <view class="tech-tag">Vue3</view>
              <view class="tech-tag">TypeScript</view>
              <view class="tech-tag">Element Plus</view>
            </view>
            <view class="preview-line">负责前端架构设计和核心模块开发</view>
          </view>
        </view>
      </template>

      <template v-else-if="componentKey === 'SelfEvaluation'">
        <view class="preview-placeholder">
          <view class="preview-line">5年前端开发经验，精通Vue生态，熟悉React、TypeScript等现代前端技术。</view>
          <view class="preview-line">具备良好的团队协作能力和沟通能力，能够快速适应新环境。</view>
          <view class="preview-line">对技术有热情，喜欢研究新技术并应用到实际项目中。</view>
        </view>
      </template>

      <template v-else-if="componentKey === 'JobIntention'">
        <view class="preview-placeholder">
          <view class="preview-line">期望职位：高级前端开发工程师</view>
          <view class="preview-line" v-if="componentProps?.showSalary">期望薪资：25-35K</view>
          <view class="preview-line" v-if="componentProps?.showLocation">工作地点：北京</view>
          <view class="preview-line">到岗时间：1个月内</view>
        </view>
      </template>

      <template v-else-if="componentKey === 'CompanyExperience'">
        <view class="preview-placeholder">
          <view class="preview-section">
            <view class="preview-line-bold">ABC科技有限公司</view>
            <view class="preview-line" v-if="componentProps?.showRole">高级前端开发工程师 | 2020.06 - 至今</view>
            <view class="preview-line">负责公司核心产品的开发与维护</view>
            <view v-if="componentProps?.showTechnologies" class="preview-tech-tags">
              <view class="tech-tag">Vue3</view>
              <view class="tech-tag">TypeScript</view>
              <view class="tech-tag">微前端</view>
            </view>
          </view>
        </view>
      </template>

      <template v-else>
        <view class="preview-placeholder">
          <text class="placeholder-text">{{ componentKey ? `${componentName} 预览` : '请选择组件类型' }}</text>
        </view>
      </template>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from "vue";

interface Props {
  component?: {
    id?: number | string;
    key?: string;
    name?: string;
    props?: Record<string, any>;
    styles?: Record<string, any>;
  };
  style?: Record<string, any>;
  globalStyle?: {
    primaryColor?: string;
    accentColor?: string;
    secondaryColor?: string;
    fontFamily?: string;
    fontSize?: string;
    fontSizes?: {
      h1?: string;
      body?: string;
    };
  };
  showActions?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  component: () => ({ id: '', name: '', key: '', props: {}, styles: {} }),
  style: () => ({}),
  globalStyle: () => ({}),
  showActions: false
});

// 计算属性
const primaryColor = computed(() => props.globalStyle?.primaryColor || '#d4af37');
const accentColor = computed(() => props.globalStyle?.accentColor || '#f7ef8a');
const dangerColor = computed(() => '#f56c6c');

const componentKey = computed(() => props.component?.key || '');
const componentName = computed(() => props.component?.name || '未命名区块');
const componentProps = computed(() => props.component?.props || {});
const componentStyles = computed(() => props.component?.styles || {});

const skillTags = computed(() => ['Vue.js', 'React', 'TypeScript', 'Node.js', 'Webpack', 'uniApp']);

const mergedStyle = computed(() => {
  const baseStyle = {
    marginBottom: '20rpx',
    border: '1px solid #eee',
    borderRadius: '8rpx',
    padding: '16rpx',
    background: 'white',
    transition: 'all 0.3s',
    ...componentStyles.value
  };

  return {
    ...baseStyle,
    ...props.style
  };
});

const headerStyle = computed(() => ({
  borderBottom: `1px solid ${props.globalStyle?.secondaryColor || '#f0f0f0'}`,
  borderLeft: `3px solid ${primaryColor.value}`,
  paddingLeft: '8rpx'
}));

const titleStyle = computed(() => ({
  color: primaryColor.value,
  fontSize: props.globalStyle?.fontSizes?.h1
    ? `${props.globalStyle.fontSizes.h1}px`
    : props.globalStyle?.fontSize || '28rpx',
  fontWeight: 'bold'
}));

const contentStyle = computed(() => ({
  fontFamily: props.globalStyle?.fontFamily || 'inherit',
  fontSize: props.globalStyle?.fontSizes?.body
    ? `${props.globalStyle.fontSizes.body}px`
    : props.globalStyle?.fontSize || '24rpx'
}));

const emit = defineEmits(['remove', 'configure']);
</script>

<style scoped lang="scss">
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

.preview-avatar {
  display: flex;
  justify-content: center;
  margin-bottom: 16rpx;
}

.avatar-placeholder {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
}

.preview-info {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.preview-section {
  margin-bottom: 20rpx;
  padding-bottom: 12rpx;
  border-bottom: 1px dashed #eee;

  &:last-child {
    border-bottom: none;
    margin-bottom: 0;
    padding-bottom: 0;
  }
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
  margin-bottom: 16rpx;
}

.tag {
  padding: 4rpx 12rpx;
  background: #e8f4ff;
  border-radius: 4rpx;
  font-size: 22rpx;
  color: #007aff;
}

.preview-tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
  margin: 8rpx 0;
}

.tech-tag {
  padding: 2rpx 8rpx;
  background: #e9ecef;
  border-radius: 3rpx;
  font-size: 20rpx;
  color: #495057;
}

.skill-levels {
  margin-top: 16rpx;
}

.skill-item {
  display: flex;
  align-items: center;
  margin-bottom: 12rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.skill-name {
  width: 80rpx;
  font-size: 22rpx;
  color: #555;
}

.skill-level-bar {
  flex: 1;
  height: 8rpx;
  background: #e9ecef;
  border-radius: 4rpx;
  overflow: hidden;
  margin-left: 12rpx;
}

.skill-level-fill {
  height: 100%;
  background: var(--primary-color);
  border-radius: 4rpx;
  transition: width 0.3s ease;
}

.placeholder-text {
  color: #999;
  font-style: italic;
  text-align: center;
  display: block;
  padding: 20rpx;
}
</style>