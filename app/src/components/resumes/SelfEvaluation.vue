<template>
  <BaseComponent
    :component-data="componentData"
    :global-style="globalStyle"
    :override-styles="overrideStyles"
    :show-header="showTitle"
    :custom-title="title"
    :responsive-center="true"
    :max-width="maxWidth"
  >
    <template #default="{ styles }">
      <view class="self-evaluation-container">
        <!-- 评估列表 -->
        <view
          v-for="(evaluation, index) in evaluations"
          :key="evaluation.id || index"
          class="evaluation-item"
          :style="getItemStyle(styles, index)"
        >
          <!-- 评估内容 -->
          <view
            class="evaluation-content"
            :style="getContentStyle(styles)"
          >
            {{ evaluation.content }}
          </view>

          <!-- 高亮关键词 -->
          <view
            v-if="evaluation.highlights && evaluation.highlights.length > 0"
            class="highlights-section"
          >
            <text class="highlights-title" :style="getSectionTitleStyle(styles)">
              高亮内容：
            </text>
            <view class="highlights-tags">
              <view
                v-for="(highlight, hIndex) in evaluation.highlights"
                :key="hIndex"
                class="highlight-tag"
                :style="getHighlightTagStyle(styles)"
              >
                {{ highlight }}
              </view>
            </view>
          </view>
        </view>

        <!-- 没有评价内容时的占位 -->
        <view
          v-if="evaluations.length === 0"
          class="empty-placeholder"
          :style="getPlaceholderStyle(styles)"
        >
          <text class="placeholder-text">暂无自我评价内容</text>
          <text class="placeholder-hint">点击编辑按钮添加内容</text>
        </view>
      </view>
    </template>
  </BaseComponent>
</template>

<script setup lang="ts">
import { computed } from "vue";
import BaseComponent from "./BaseComponent.vue";
import type { TemplateGlobalStyle } from "@/types/template";
import type { TemplateComponentResult } from "@/types/template-component";

interface Props {
  componentData: TemplateComponentResult;
  globalStyle?: TemplateGlobalStyle;
  customStyles?: Record<string, any>;
  responsiveCenter?: boolean;
  maxWidth?: string;
}

const props = withDefaults(defineProps<Props>(), {
  globalStyle: () => ({}),
  customStyles: () => ({}),
  responsiveCenter: false,
  maxWidth: "100%"
});

// ===================== 计算属性 =====================

// 组件数据
const componentProps = computed(() => props.componentData?.props || {});
const defaultConfig = computed(() => props.componentData?.defaultConfig || {});

// 配置选项
const configProps = computed(() => defaultConfig.value?.props || {});

// 组件标题
const title = computed(() => configProps.value.title || '自我评价');
const showTitle = computed(() => configProps.value.showTitle !== false);

// 评价数据
const evaluations = computed(() => {
  return componentProps.value.evaluations || [];
});

// 合并样式 - 将 customStyles 作为 overrideStyles 传递给 BaseComponent
const overrideStyles = computed(() => {
  return {
    ...defaultConfig.value?.styles,
    ...props.componentData?.styles,
    ...props.customStyles
  };
});

// ===================== 样式计算函数 =====================

// 项目样式
const getItemStyle = (styles: any, index: number) => {
  if (!styles) return {};

  return {
    marginBottom: index < evaluations.value.length - 1 ? '20px' : '0',
    paddingBottom: index < evaluations.value.length - 1 ? '20px' : '0',
    borderBottom: index < evaluations.value.length - 1 ?
      `1px dashed var(--base-secondary-color, #e8e8e8)` : 'none'
  };
};

// 内容样式
const getContentStyle = (styles: any) => {
  if (!styles) return {};

  return {
    color: 'var(--base-text-color, #555)',
    fontSize: styles.bodySize || '14px',
    lineHeight: styles.lineHeight || '1.6',
    textAlign: 'justify',
    whiteSpace: 'pre-wrap',
    wordBreak: 'break-word',
    marginBottom: '16px',
    maxWidth: '100%',
    overflowWrap: 'break-word'
  };
};

// 区域标题样式
const getSectionTitleStyle = (styles: any) => {
  if (!styles) return {};

  return {
    color: 'var(--base-title-color, #333)',
    fontSize: styles.bodySize || '14px',
    fontWeight: '600',
    marginBottom: '8px',
    display: 'block'
  };
};

// 高亮标签样式
const getHighlightTagStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '12px',
    color: 'var(--base-accent-color, #ff7a45)',
    backgroundColor: 'var(--base-highlight-bg, #fff7e6)',
    padding: '4px 12px',
    borderRadius: '16px',
    border: `1px solid color-mix(in srgb, var(--base-accent-color, #ff7a45) 30%, transparent)`,
    maxWidth: '100%',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    whiteSpace: 'nowrap'
  };
};

// 占位符样式
const getPlaceholderStyle = (styles: any) => {
  if (!styles) return {};

  return {
    textAlign: 'center',
    color: '#999',
    padding: '40px 20px',
    background: 'repeating-linear-gradient(45deg, #fafafa, #fafafa 10px, #f0f0f0 10px, #f0f0f0 20px)',
    borderRadius: '4px',
    fontSize: styles.bodySize || '14px',
    width: '100%',
    boxSizing: 'border-box'
  };
};
</script>

<style scoped lang="scss">
.self-evaluation-container {
  width: 100%;
  box-sizing: border-box;
  font-family: inherit;
}

.evaluation-content {
  white-space: pre-wrap;
  word-break: break-word;
  hyphens: auto;
}

.highlights-section {
  margin-top: 16px;
}

.highlights-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.highlight-tag {
  font-size: 12px;
  line-height: 1.5;
  transition: all 0.3s ease;
}

.highlight-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.empty-placeholder {
  min-height: 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  box-sizing: border-box;
}

.placeholder-text {
  font-size: 14px;
  color: #999;
}

.placeholder-hint {
  font-size: 12px;
  color: #ccc;
}

/* 响应式样式 */
@media (max-width: 768px) {
  .self-evaluation-container {
    padding: 0 !important;
  }

  .evaluation-item {
    margin-bottom: 16px !important;
    padding-bottom: 16px !important;
  }

  .evaluation-content {
    font-size: 14px !important;
    line-height: 1.6 !important;
    text-align: left;
    hyphens: auto;
  }

  .highlights-section {
    margin-top: 12px;
  }

  .highlights-tags {
    gap: 6px;
  }

  .highlight-tag {
    font-size: 11px;
    padding: 3px 8px;
    white-space: normal;
    word-break: break-word;
  }

  .highlights-title {
    font-size: 14px !important;
  }

  .empty-placeholder {
    padding: 30px 16px !important;
  }

  .placeholder-text {
    font-size: 13px;
  }

  .placeholder-hint {
    font-size: 11px;
  }
}

/* 小屏幕手机 */
@media (max-width: 480px) {
  .evaluation-item {
    margin-bottom: 12px !important;
    padding-bottom: 12px !important;
  }

  .evaluation-content {
    font-size: 13px !important;
    line-height: 1.6 !important;
    margin-bottom: 12px;
  }

  .highlights-tags {
    gap: 4px;
  }

  .highlight-tag {
    font-size: 10px;
    padding: 2px 6px;
    border-radius: 12px;
  }

  .empty-placeholder {
    padding: 24px 12px !important;
  }
}

/* 平板设备 */
@media (min-width: 769px) and (max-width: 1024px) {
  .evaluation-content {
    font-size: 14px !important;
  }
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.evaluation-item {
  animation: fadeIn 0.5s ease-out;
}

/* 打印样式 */
@media print {
  .self-evaluation-container {
    break-inside: avoid;
    page-break-inside: avoid;
  }

  .highlight-tag {
    border: 1px solid #333 !important;
    background: white !important;
    color: #333 !important;
    font-size: 10px !important;
    padding: 2px 8px !important;
  }

  .evaluation-content {
    font-size: 12px !important;
    line-height: 1.6 !important;
  }
}
</style>