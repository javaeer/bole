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
          <view class="preview-avatar">
            <view class="avatar-placeholder">👤</view>
          </view>
          <view class="preview-info">
            <view class="preview-line-bold">王彦博</view>
            <view class="preview-line">高级工程师</view>
            <view class="preview-line">工作年限：5年</view>
            <view class="preview-line">性别：男</view>
            <view class="preview-line">所在地：深圳</view>
            <view class="preview-line">电话：186****0038</view>
            <view class="preview-line">邮箱：zhangsan@tencent.com</view>
            <view class="preview-line">个人网站：https://zhangsan.dev</view>
            <view class="preview-line">GitHub：zhangsan</view>
            <view class="preview-line">微信：zhangsan_wx</view>
          </view>
        </view>
      </template>

      <template v-else-if="componentKey === 'WorkExperience'">
        <view class="preview-placeholder">
          <view class="preview-section">
            <view class="preview-line-bold">伯乐科技</view>
            <view class="preview-line">高级软件工程师 | 2023.07 - 至今</view>
            <view class="preview-line">负责系统架构设计和团队指导</view>
            <view class="achievements">
              <view class="achievement-title">主要成就：</view>
              <view class="achievement-item">• 主导系统重构</view>
              <view class="achievement-item">• 培养3名初级工程师</view>
            </view>
          </view>
          <view class="preview-section">
            <view class="preview-line-bold">伯乐科技</view>
            <view class="preview-line">软件工程师 | 2021.07 - 2023.06</view>
            <view class="preview-line">负责核心业务功能开发</view>
            <view class="achievements">
              <view class="achievement-title">主要成就：</view>
              <view class="achievement-item">• 完成3个重大项目</view>
              <view class="achievement-item">• 获得年度优秀员工</view>
            </view>
          </view>
        </view>
      </template>

      <template v-else-if="componentKey === 'EducationExperience'">
        <view class="preview-placeholder">
          <view class="preview-section">
            <view class="preview-line-bold">北京大学出版社</view>
            <view class="preview-line">后背 | 博士 | 1997.10 - 2026.01</view>
            <view class="preview-line description-text">哒哒哒哒哒哒多多多多多多多多多多多多多多多多多多多多多多多多多多多多多多多多多多多多多多多多</view>
          </view>
          <view class="preview-section">
            <view class="preview-line-bold">北京大学</view>
            <view class="preview-line">软件工程 | 硕士 | 2018.09 - 2021.06</view>
            <view class="preview-line description-text">研究方向：分布式系统</view>
            <view class="achievements">
              <view class="achievement-title">荣誉奖项：</view>
              <view class="achievement-item">• 发表论文2篇</view>
              <view class="achievement-item">• 国家奖学金</view>
            </view>
          </view>
          <view class="preview-section">
            <view class="preview-line-bold">清华大学</view>
            <view class="preview-line">计算机科学与技术 | 本科 | 2014.09 - 2018.06</view>
            <view class="preview-line description-text">主修计算机相关课程</view>
            <view class="achievements">
              <view class="achievement-title">荣誉奖项：</view>
              <view class="achievement-item">• 校级优秀毕业生</view>
              <view class="achievement-item">• ACM竞赛二等奖</view>
            </view>
          </view>
        </view>
      </template>

      <template v-else-if="componentKey === 'Skills'">
        <view class="preview-placeholder">
          <!-- 按分类分组显示 -->
          <view class="skill-category">
            <view class="category-title">编程语言</view>
            <view class="skill-items">
              <view class="skill-item">
                <view class="skill-header">
                  <text class="skill-name">Java编程</text>
                  <view class="skill-badge">高级</view>
                </view>
                <view class="skill-details">
                  <view class="skill-progress">
                    <view class="skill-progress-bar">
                      <view class="skill-progress-fill" :style="{ width: '85%' }"></view>
                    </view>
                    <text class="skill-level">高级 (85%)</text>
                  </view>
                  <view class="experience-years">
                    经验：5.5年
                  </view>
                  <view class="certification">
                    认证：Oracle Certified Professional
                  </view>
                </view>
                <view class="skill-description">熟练掌握Java语言特性，包括集合、多线程、IO等</view>
                <view class="skill-tags">
                  <view class="skill-tag">Java</view>
                  <view class="skill-tag">编程</view>
                </view>
              </view>
            </view>
          </view>
          
          <view class="skill-category">
            <view class="category-title">框架工具</view>
            <view class="skill-items">
              <view class="skill-item">
                <view class="skill-header">
                  <text class="skill-name">Spring框架</text>
                  <view class="skill-badge">高级</view>
                </view>
                <view class="skill-details">
                  <view class="skill-progress">
                    <view class="skill-progress-bar">
                      <view class="skill-progress-fill" :style="{ width: '90%' }"></view>
                    </view>
                    <text class="skill-level">高级 (90%)</text>
                  </view>
                  <view class="experience-years">
                    经验：4年
                  </view>
                  <view class="certification">
                    认证：Spring Professional Certification
                  </view>
                </view>
                <view class="skill-description">精通Spring、Spring Boot、Spring Cloud等框架</view>
              </view>
              
              <view class="skill-item">
                <view class="skill-header">
                  <text class="skill-name">SpringCloud</text>
                  <view class="skill-badge">专家</view>
                </view>
                <view class="skill-details">
                  <view class="skill-progress">
                    <view class="skill-progress-bar">
                      <view class="skill-progress-fill" :style="{ width: '100%' }"></view>
                    </view>
                    <text class="skill-level">专家 (100%)</text>
                  </view>
                  <view class="experience-years">
                    经验：10年
                  </view>
                </view>
                <view class="skill-description">VVVVVV</view>
                <view class="skill-tags">
                  <view class="skill-tag">河流</view>
                  <view class="skill-tag">天剑</view>
                </view>
              </view>
            </view>
          </view>
          
          <view class="skill-category">
            <view class="category-title">数据库</view>
            <view class="skill-items">
              <view class="skill-item">
                <view class="skill-header">
                  <text class="skill-name">MySQL</text>
                  <view class="skill-badge">中级</view>
                </view>
                <view class="skill-details">
                  <view class="skill-progress">
                    <view class="skill-progress-bar">
                      <view class="skill-progress-fill" :style="{ width: '75%' }"></view>
                    </view>
                    <text class="skill-level">中级 (75%)</text>
                  </view>
                  <view class="experience-years">
                    经验：3年
                  </view>
                </view>
                <view class="skill-description">熟悉MySQL数据库设计、优化和SQL调优</view>
                <view class="skill-tags">
                  <view class="skill-tag">sss</view>
                  <view class="skill-tag">okook</view>
                </view>
              </view>
            </view>
          </view>
        </view>
      </template>

      <template v-else-if="componentKey === 'ProjectExperience'">
        <view class="preview-placeholder">
          <view class="preview-section">
            <view class="preview-line-bold">分布式消息队列系统</view>
            <view class="preview-line">技术负责人 | 2022.01 - 2022.12</view>
            <view class="preview-line description-text">设计并实现高可用分布式消息队列</view>
            <view class="preview-tech-tags">
              <view class="tech-tag">分布式系统</view>
              <view class="tech-tag">消息队列</view>
              <view class="tech-tag">高可用</view>
            </view>
            <view class="achievements">
              <view class="achievement-title">项目成果：</view>
              <view class="achievement-item">• 系统吞吐量提升50%</view>
              <view class="achievement-item">• 支持每秒百万级消息处理</view>
            </view>
          </view>
          <view class="preview-section">
            <view class="preview-line-bold">微服务架构迁移</view>
            <view class="preview-line">核心开发 | 2021.03 - 2021.11</view>
            <view class="preview-line description-text">将单体应用迁移到微服务架构</view>
            <view class="preview-tech-tags">
              <view class="tech-tag">微服务</view>
              <view class="tech-tag">Spring Cloud</view>
              <view class="tech-tag">架构迁移</view>
            </view>
            <view class="achievements">
              <view class="achievement-title">项目成果：</view>
              <view class="achievement-item">• 系统可用性达到99.99%</view>
              <view class="achievement-item">• 开发效率提升30%</view>
            </view>
          </view>
        </view>
      </template>

      <template v-else-if="componentKey === 'SelfEvaluation'">
        <view class="preview-placeholder">
          <view class="evaluation-item">
            <view class="evaluation-content">
              6666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666
            </view>
            <view class="highlights">
              <view class="highlight-tag">6</view>
              <view class="highlight-tag">7</view>
              <view class="highlight-tag">8</view>
              <view class="highlight-tag">9</view>
              <view class="highlight-tag">0</view>
              <view class="highlight-tag">777</view>
            </view>
          </view>
        </view>
      </template>

      <template v-else-if="componentKey === 'JobIntention'">
        <view class="preview-placeholder">
          <view class="intention-item">
            <view class="preview-line">期望职位：CTO</view>
            <view class="preview-line">期望薪资：100万</view>
            <view class="preview-line">工作地点：北京市</view>
            <view class="preview-line">工作类型：全职</view>
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

/* 描述文本添加自动换行 */
.description-text {
  word-wrap: break-word;
  word-break: break-word;
  white-space: normal;
  line-height: 1.5;
}

.achievements {
  margin-top: 8rpx;
  padding-left: 16rpx;
}

.achievement-title {
  font-weight: 500;
  color: #333;
  margin-bottom: 4rpx;
}

.achievement-item {
  color: #666;
  margin-bottom: 2rpx;
  font-size: 22rpx;
}

.skill-category {
  margin-bottom: 24rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.category-title {
  font-weight: bold;
  color: #333;
  margin-bottom: 12rpx;
  padding-bottom: 4rpx;
  border-bottom: 1px solid #eee;
}

.skill-item {
  margin-bottom: 12rpx;
  padding: 12rpx;
  background: #f9f9f9;
  border-radius: 6rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.skill-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
}

.skill-name {
  font-weight: bold;
  color: #333;
  font-size: 26rpx;
}

.skill-badge {
  background: #1890ff;
  color: white;
  padding: 2rpx 8rpx;
  border-radius: 4rpx;
  font-size: 20rpx;
}

.skill-details {
  display: flex;
  gap: 16rpx;
  margin-bottom: 8rpx;
  font-size: 22rpx;
  color: #666;
  flex-wrap: wrap;
}

.skill-progress {
  display: flex;
  align-items: center;
  gap: 12rpx;
  width: 100%;
}

.skill-progress-bar {
  flex: 1;
  height: 8rpx;
  background: #e9ecef;
  border-radius: 4rpx;
  overflow: hidden;
}

.skill-progress-fill {
  height: 100%;
  background: #1890ff;
  border-radius: 4rpx;
  transition: width 0.3s ease;
}

.skill-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4rpx;
  margin-top: 8rpx;
}

.skill-tag {
  background: #e9ecef;
  color: #495057;
  padding: 2rpx 6rpx;
  border-radius: 3rpx;
  font-size: 20rpx;
}

.skill-description {
  color: #777;
  font-size: 22rpx;
  margin-top: 4rpx;
  word-wrap: break-word;
  word-break: break-word;
  white-space: normal;
  line-height: 1.5;
}

.certification {
  background: #f6ffed;
  color: #52c41a;
  padding: 2rpx 6rpx;
  border-radius: 3rpx;
  font-size: 20rpx;
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

/* 自我介绍内容自动换行 */
.evaluation-item {
  margin-bottom: 16rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.evaluation-content {
  color: #555;
  line-height: 1.6;
  margin-bottom: 8rpx;
  word-wrap: break-word;
  word-break: break-word;
  white-space: normal;
  overflow-wrap: break-word;
  line-height: 1.5;
}

.highlights {
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
}

.highlight-tag {
  background: #fff7e6;
  color: #fa8c16;
  padding: 2rpx 8rpx;
  border-radius: 4rpx;
  font-size: 20rpx;
}

.intention-item {
  padding: 12rpx;
  background: #f9f9f9;
  border-radius: 6rpx;
}

.placeholder-text {
  color: #999;
  font-style: italic;
  text-align: center;
  display: block;
  padding: 20rpx;
  word-wrap: break-word;
  word-break: break-word;
  white-space: normal;
}
</style>