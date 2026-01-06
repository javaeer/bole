<template>
  <BaseComponent
    :title="componentName"
    :component-data="componentData"
    :global-style="globalStyle"
    :custom-styles="customStyles"
  >
    <template #default="{ styles }">
      <view
        class="job-intention-container"
        :style="getContainerStyle(styles)"
      >
        <!-- 占位符：当没有意向数据时 -->
        <view
          v-if="intentions.length === 0"
          class="empty-placeholder"
          :style="getPlaceholderStyle(styles)"
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
            :style="getItemStyle(styles, index)"
            @click="handleItemClick(intention)"
          >
            <!-- 职位意向主信息 -->
            <view class="intention-main">
              <view class="position-row">
                <text class="position-icon">👔</text>
                <text class="position-value" :style="getHighlightStyle(styles)">
                  {{ intention.position }}
                </text>
                <view v-if="intention.isPrimary" class="primary-badge" :style="getBadgeStyle(styles)">
                  主意向
                </view>
              </view>

              <!-- 标签区域 -->
              <view class="intention-tags">
                <view
                  v-if="showJobType && intention.jobType"
                  class="tag-item"
                  :style="getTagStyle(styles, 'type')"
                >
                  <text class="tag-icon">📝</text>
                  <text class="tag-text">{{ intention.jobType }}</text>
                </view>

                <view
                  v-if="showWorkLocation && intention.city"
                  class="tag-item"
                  :style="getTagStyle(styles, 'location')"
                >
                  <text class="tag-icon">📍</text>
                  <text class="tag-text">{{ intention.city }}</text>
                </view>

                <view
                  v-if="showExpectedSalary && intention.salary"
                  class="tag-item"
                  :style="getTagStyle(styles, 'salary')"
                >
                  <text class="tag-icon">💰</text>
                  <text class="tag-text">{{ formatSalary(intention.salary) }}</text>
                </view>

                <view
                  v-if="showOnboardingTime && intention.onboardingTime"
                  class="tag-item"
                  :style="getTagStyle(styles, 'time')"
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
              :style="getDetailStyle(styles)"
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

const componentName = computed(() =>
  props.componentData?.name || '求职意向'
);

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

const salaryUnit = computed(() =>
  defaultConfig.value.props?.salaryUnit || 'K'
);

// 样式计算函数
const getContainerStyle = (styles: any) => {
  if (!styles) return {};

  return {
    padding: styles.padding,
    backgroundColor: styles.backgroundColor,
    borderRadius: styles.borderRadius,
    boxShadow: styles.boxShadow,
    fontFamily: styles.fontFamily,
    fontSize: styles.bodySize,
    lineHeight: styles.lineHeight,
    display: 'flex',
    flexDirection: 'column',
    gap: '16px'
  };
};

const getItemStyle = (styles: any, index: number) => {
  if (!styles) return {};

  return {
    padding: '16px',
    backgroundColor: '#ffffff',
    borderRadius: '8px',
    border: `1px solid ${styles.secondaryColor || '#e8e8e8'}`,
    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.05)',
    marginBottom: index < sortedIntentions.value.length - 1 ? '12px' : '0',
    transition: 'all 0.3s ease',
    cursor: 'pointer'
  };
};

const getHighlightStyle = (styles: any) => {
  if (!styles) return {};

  return {
    color: styles.highlightColor || styles.primaryColor || '#1890ff',
    fontSize: '18px',
    fontWeight: 'bold',
    marginLeft: '8px'
  };
};

const getBadgeStyle = (styles: any) => {
  if (!styles) return {};

  return {
    backgroundColor: styles.primaryColor || '#1890ff',
    color: '#ffffff',
    fontSize: '12px',
    padding: '2px 8px',
    borderRadius: '12px',
    marginLeft: '12px'
  };
};

const getTagStyle = (styles: any, type: string) => {
  if (!styles) return {};

  const baseStyle = {
    display: 'flex',
    alignItems: 'center',
    gap: '4px',
    padding: '6px 12px',
    borderRadius: '16px',
    fontSize: '12px',
    fontWeight: 500
  };

  const typeColors: Record<string, any> = {
    salary: {
      backgroundColor: 'rgba(255, 107, 107, 0.1)',
      color: '#ff6b6b',
      border: '1px solid rgba(255, 107, 107, 0.2)'
    },
    location: {
      backgroundColor: 'rgba(90, 200, 250, 0.1)',
      color: '#5ac8fa',
      border: '1px solid rgba(90, 200, 250, 0.2)'
    },
    type: {
      backgroundColor: 'rgba(82, 196, 26, 0.1)',
      color: '#52c41a',
      border: '1px solid rgba(82, 196, 26, 0.2)'
    },
    time: {
      backgroundColor: 'rgba(255, 193, 7, 0.1)',
      color: '#ffc107',
      border: '1px solid rgba(255, 193, 7, 0.2)'
    }
  };

  return {
    ...baseStyle,
    ...typeColors[type] || {
      backgroundColor: 'rgba(0, 0, 0, 0.05)',
      color: '#666',
      border: '1px solid rgba(0, 0, 0, 0.1)'
    }
  };
};

const getDetailStyle = (styles: any) => {
  if (!styles) return {};

  return {
    marginTop: '16px',
    paddingTop: '16px',
    borderTop: `1px dashed ${styles.secondaryColor || '#eee'}`,
    animation: 'slideDown 0.3s ease-out'
  };
};

const getPlaceholderStyle = (styles: any) => {
  if (!styles) return {};

  return {
    textAlign: 'center',
    padding: '40px 20px',
    color: '#999',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    gap: '12px'
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

<style scoped>
.job-intention-container {
  font-family: inherit;
}

.intention-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  border-color: rgba(90, 200, 250, 0.5);
}

.position-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.position-icon {
  font-size: 20px;
}

.position-value {
  flex: 1;
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
  transition: all 0.2s ease;
}

.tag-item:hover {
  transform: scale(1.05);
}

.tag-icon {
  margin-right: 4px;
  font-size: 12px;
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
  color: #666;
  font-size: 13px;
  min-width: 80px;
  flex-shrink: 0;
}

.detail-value {
  color: #333;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.expand-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 16px;
  padding: 8px;
  color: #5ac8fa;
  font-size: 13px;
  cursor: pointer;
  user-select: none;
  border-top: 1px solid #f0f0f0;
}

.expand-toggle:hover {
  background: rgba(90, 200, 250, 0.05);
  border-radius: 4px;
}

.toggle-icon {
  transition: transform 0.3s ease;
}

.toggle-icon.rotated {
  transform: rotate(180deg);
}

.empty-placeholder {
  min-height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.placeholder-icon {
  font-size: 32px;
  margin-bottom: 8px;
  opacity: 0.5;
}

.placeholder-text {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.placeholder-hint {
  font-size: 12px;
  color: #999;
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

.intention-details {
  animation: slideDown 0.3s ease-out;
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }

  .intention-tags {
    flex-direction: column;
    align-items: flex-start;
  }

  .tag-item {
    width: 100%;
    justify-content: flex-start;
  }

  .intention-item {
    padding: 12px;
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
    border: 1px solid #ddd;
    box-shadow: none;
  }

  .expand-toggle {
    display: none;
  }

  .intention-details {
    display: block !important;
    max-height: none !important;
  }
}
</style>