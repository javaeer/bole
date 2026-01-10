<template>
  <BaseComponent
    :component-data="componentData"
    :global-style="globalStyle"
    :override-styles="customStyles"
    :responsive-center="true"
    :show-header="showTitle"
    :custom-title="title"
  >
    <template #default="{ styles }">
      <view class="job-intention-container">
        <!-- 占位符：当没有意向数据时 -->
        <view
          v-if="intentions.length === 0"
          class="empty-placeholder"
        >
          <view class="placeholder-icon">🎯</view>
          <text class="placeholder-text">暂无求职意向信息</text>
          <text class="placeholder-hint">请添加您的求职意向</text>
        </view>

        <!-- 正常显示：有数据时 -->
        <template v-else>
          <view
            v-for="(intention, index) in sortedIntentions"
            :key="intention.id"
            class="intention-item"
            :style="getItemStyle(index)"
            @click="handleItemClick(intention)"
          >
            <!-- 职位意向主信息 -->
            <view class="intention-main">
              <view class="position-row">
                <text class="position-icon">👔</text>
                <text class="position-value">
                  {{ intention.position }}
                </text>
                <view v-if="intention.isPrimary" class="primary-badge">
                  主意向
                </view>
              </view>

              <!-- 标签区域 -->
              <view class="intention-tags">
                <view
                  v-if="showJobType && intention.jobType"
                  class="tag-item tag-type"
                >
                  <text class="tag-icon">📝</text>
                  <text class="tag-text">{{ intention.jobType }}</text>
                </view>

                <view
                  v-if="showWorkLocation && intention.city"
                  class="tag-item tag-location"
                >
                  <text class="tag-icon">📍</text>
                  <text class="tag-text">{{ intention.city }}</text>
                </view>

                <view
                  v-if="showExpectedSalary && intention.salary"
                  class="tag-item tag-salary"
                >
                  <text class="tag-icon">💰</text>
                  <text class="tag-text">{{ formatSalary(intention.salary) }}</text>
                </view>

                <view
                  v-if="showOnboardingTime && intention.onboardingTime"
                  class="tag-item tag-time"
                >
                  <text class="tag-icon">📅</text>
                  <text class="tag-text">{{ intention.onboardingTime }}</text>
                </view>
              </view>
            </view>

            <!-- 详细字段（可展开） -->
            <view
              v-if="showDetailFields && isExpandedIndex === index"
              class="intention-details"
            >
              <view class="detail-grid">
                <view
                  v-if="showExpectedIndustry && intention.industry"
                  class="detail-item"
                >
                  <text class="detail-label">期望行业：</text>
                  <text class="detail-value">{{ intention.industry }}</text>
                </view>

                <view
                  v-if="showCurrentStatus && intention.currentStatus"
                  class="detail-item"
                >
                  <text class="detail-label">当前状态：</text>
                  <text class="detail-value">{{ intention.currentStatus }}</text>
                </view>

                <view
                  v-if="intention.workType"
                  class="detail-item"
                >
                  <text class="detail-label">工作性质：</text>
                  <text class="detail-value">{{ intention.workType }}</text>
                </view>

                <view
                  v-if="intention.description"
                  class="detail-item full-width"
                >
                  <text class="detail-label">补充说明：</text>
                  <text class="detail-value">{{ intention.description }}</text>
                </view>
              </view>
            </view>

            <!-- 展开/收起按钮 -->
            <view
              v-if="showDetailFields && hasDetailContent(intention)"
              class="expand-toggle"
              @click.stop="toggleExpand(index)"
            >
              <text class="toggle-text">
                {{ isExpandedIndex === index ? '收起详情' : '查看详情' }}
              </text>
              <text class="toggle-icon">
                {{ isExpandedIndex === index ? '▲' : '▼' }}
              </text>
            </view>
          </view>
        </template>
      </view>
    </template>
  </BaseComponent>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import BaseComponent from "./BaseComponent.vue";
import type { TemplateGlobalStyle } from "@/types/template";
import type { JobIntentionResult } from "@/types/job-intention";

interface Props {
  componentData: any;
  globalStyle?: TemplateGlobalStyle;
  customStyles?: Record<string, any>;
}

const props = defineProps<Props>();

// 展开状态的索引
const isExpandedIndex = ref<number | null>(null);

// 组件数据和配置
const componentProps = computed(() =>
  props.componentData?.props || {}
);

const defaultConfig = computed(() =>
  props.componentData?.defaultConfig || {}
);

// 求职意向数据
const intentions = computed<JobIntentionResult[]>(() =>
  componentProps.value.intentions || []
);

// 排序后的意向（主意向优先）
const sortedIntentions = computed(() => {
  return [...intentions.value].sort((a, b) => {
    if (a.isPrimary && !b.isPrimary) return -1;
    if (!a.isPrimary && b.isPrimary) return 1;
    return 0;
  });
});

// 显示控制 - 从默认配置中获取
const showExpectedSalary = computed(() =>
  defaultConfig.value.props?.showExpectedSalary ??
  componentProps.value.showSalary ??
  false
);

const showWorkLocation = computed(() =>
  defaultConfig.value.props?.showWorkLocation ??
  componentProps.value.showLocation ??
  true
);

const showJobType = computed(() =>
  defaultConfig.value.props?.showJobType ?? true
);

const showCurrentStatus = computed(() =>
  defaultConfig.value.props?.showCurrentStatus ?? false
);

const showExpectedIndustry = computed(() =>
  defaultConfig.value.props?.showExpectedIndustry ?? false
);

const showOnboardingTime = computed(() =>
  defaultConfig.value.props?.showOnboardingTime ?? false
);

const showDetailFields = computed(() =>
  defaultConfig.value.props?.showDetailFields ?? true
);

const showTitle = computed(() =>
  defaultConfig.value.props?.showTitle !== false
);

const salaryUnit = computed(() =>
  defaultConfig.value.props?.salaryUnit || 'K'
);

const title = computed(() =>
  defaultConfig.value.props?.title || '求职意向'
);

// 样式计算函数 - 简化版本
const getItemStyle = (index: number) => {
  const isLast = index === sortedIntentions.value.length - 1;
  
  return {
    marginBottom: isLast ? '0' : '12px'
  };
};

// 辅助函数
const formatSalary = (salary: string | number) => {
  if (!salary) return '面议';

  const num = typeof salary === 'string' ? parseFloat(salary) : salary;
  if (isNaN(num)) return '面议';

  if (salaryUnit.value === 'K') {
    // 如果数字很大，可能是以分为单位，转换为元再转K
    const yuan = num >= 10000 ? num / 100 : num;
    return `${(yuan / 1000).toFixed(1)}K`;
  } else {
    // 直接显示元
    const yuan = num >= 10000 ? num / 100 : num;
    return `${yuan.toLocaleString()}元`;
  }
};

const hasDetailContent = (intention: JobIntentionResult) => {
  return !!(
    intention.industry ||
    intention.currentStatus ||
    intention.workType ||
    intention.description
  );
};

const toggleExpand = (index: number) => {
  isExpandedIndex.value = isExpandedIndex.value === index ? null : index;
};

const handleItemClick = (intention: JobIntentionResult) => {
  // 可以在这里触发编辑或查看详情
  console.log('点击求职意向:', intention);
};
</script>

<style scoped lang="scss">
.job-intention-container {
  font-family: inherit;
  width: 100%;
  box-sizing: border-box;
}

.intention-item {
  padding: 16px;
  background-color: #ffffff;
  border-radius: 8px;
  border: 1px solid var(--base-secondary-color, #e8e8e8);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  cursor: pointer;
  width: 100%;
  box-sizing: border-box;
}

.intention-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  border-color: var(--base-primary-color, #5ac8fa);
}

.position-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 12px;
  gap: 8px;
}

.position-icon {
  font-size: 20px;
  flex-shrink: 0;
}

.position-value {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--base-primary-color, #1890ff);
  font-size: 18px;
  font-weight: bold;
  margin-left: 8px;
}

.primary-badge {
  background-color: var(--base-primary-color, #1890ff);
  color: #ffffff;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 12px;
  margin-left: 12px;
}

.intention-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.tag-item {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.tag-item:hover {
  transform: scale(1.05);
}

.tag-icon {
  margin-right: 4px;
  font-size: 12px;
  flex-shrink: 0;
}

.tag-text {
  white-space: nowrap;
}

// 标签类型样式
.tag-salary {
  background-color: rgba(255, 107, 107, 0.1);
  color: #ff6b6b;
  border: 1px solid rgba(255, 107, 107, 0.2);
}

.tag-location {
  background-color: rgba(var(--base-primary-rgb, 90, 200, 250), 0.1);
  color: var(--base-primary-color, #5ac8fa);
  border: 1px solid rgba(var(--base-primary-rgb, 90, 200, 250), 0.2);
}

.tag-type {
  background-color: rgba(82, 196, 26, 0.1);
  color: #52c41a;
  border: 1px solid rgba(82, 196, 26, 0.2);
}

.tag-time {
  background-color: rgba(255, 193, 7, 0.1);
  color: #ffc107;
  border: 1px solid rgba(255, 193, 7, 0.2);
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
}

.detail-item.full-width {
  grid-column: 1 / -1;
}

.detail-label {
  color: var(--base-secondary-color, #666);
  font-size: 13px;
  min-width: 80px;
  flex-shrink: 0;
}

.detail-value {
  color: var(--base-text-color, #333);
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
  flex: 1;
}

.intention-details {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px dashed var(--base-secondary-color, #eee);
  animation: slideDown 0.3s ease-out;
}

.expand-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 16px;
  padding: 8px;
  color: var(--base-primary-color, #5ac8fa);
  font-size: 13px;
  cursor: pointer;
  user-select: none;
  border-top: 1px solid var(--base-secondary-color, #f0f0f0);
}

.expand-toggle:hover {
  background: rgba(var(--base-primary-rgb, 90, 200, 250), 0.05);
  border-radius: 4px;
}

.toggle-icon {
  transition: transform 0.3s ease;
}

.empty-placeholder {
  text-align: center;
  padding: 40px 20px;
  color: var(--base-secondary-color, #999);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  min-height: 120px;
  width: 100%;
}

.placeholder-icon {
  font-size: 32px;
  margin-bottom: 8px;
  opacity: 0.5;
}

.placeholder-text {
  font-size: 14px;
  color: var(--base-secondary-color, #666);
  margin-bottom: 4px;
  text-align: center;
}

.placeholder-hint {
  font-size: 12px;
  color: var(--base-secondary-color, #999);
  text-align: center;
}

/* 动画 */
@keyframes slideDown {
  from {
    opacity: 0;
    max-height: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    max-height: 500px;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .intention-item {
    padding: 12px !important;
    margin-bottom: 12px !important;
    width: 100%;
  }

  .position-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .position-value {
    font-size: 16px !important;
    margin-left: 0 !important;
    white-space: normal;
    overflow: visible;
    text-overflow: clip;
  }

  .primary-badge {
    margin-left: 0 !important;
    align-self: flex-start;
  }

  .intention-tags {
    flex-direction: column;
    align-items: stretch;
  }

  .tag-item {
    width: 100%;
    justify-content: flex-start;
    padding: 8px 12px !important;
  }

  .detail-grid {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .detail-label {
    min-width: 70px;
    font-size: 12px;
  }

  .detail-value {
    font-size: 13px;
  }

  .expand-toggle {
    margin-top: 12px;
    padding: 8px;
    font-size: 12px;
  }

  .empty-placeholder {
    padding: 20px !important;
  }

  .placeholder-icon {
    font-size: 24px;
  }

  .placeholder-text {
    font-size: 13px;
  }

  .placeholder-hint {
    font-size: 11px;
  }

  /* 移动端移除悬停效果 */
  .intention-item:hover {
    transform: none;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }

  .tag-item:hover {
    transform: none;
  }
}

/* 小屏幕手机 */
@media (max-width: 480px) {
  .job-intention-container {
    gap: 12px !important;
  }

  .intention-item {
    padding: 10px !important;
    margin-bottom: 10px !important;
  }

  .position-value {
    font-size: 15px !important;
  }

  .tag-item {
    padding: 6px 10px !important;
  }

  .detail-label {
    min-width: 60px;
    font-size: 11px;
  }

  .detail-value {
    font-size: 12px;
  }

  .expand-toggle {
    font-size: 11px;
    padding: 6px;
  }
}

/* 平板设备 */
@media (min-width: 769px) and (max-width: 1024px) {
  .detail-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .position-value {
    font-size: 17px !important;
  }
}

/* 暗色主题适配 */
@media (prefers-color-scheme: dark) {
  .intention-item {
    background-color: #1e1e1e;
    border-color: #333;
  }

  .detail-label {
    color: #aaa;
  }

  .detail-value {
    color: #ddd;
  }

  .expand-toggle {
    border-top-color: #333;
  }
}

/* 打印优化 */
@media print {
  .intention-item {
    break-inside: avoid;
    border: 1px solid #ddd !important;
    box-shadow: none !important;
    background-color: white !important;
    color: black !important;
  }

  .expand-toggle {
    display: none !important;
  }

  .intention-details {
    display: block !important;
    max-height: none !important;
  }

  /* 打印时强制显示所有详情 */
  .intention-details {
    display: block !important;
    animation: none !important;
  }
}
</style>