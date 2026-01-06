<!-- components/resumes/SelfEvaluation.vue -->
<template>
  <BaseComponent
    :title="componentName"
    :component-data="componentData"
    :global-style="globalStyle"
    :custom-styles="customStyles"
  >
    <template #default="{ styles }">
      <!-- 这里styles一定存在，来自BaseComponent的安全计算 -->
      <view
        class="self-evaluation-container"
        :style="getContainerStyle(styles)"
      >
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

interface Props {
  componentData: any;
  globalStyle?: TemplateGlobalStyle;
  customStyles?: Record<string, any>;
}

const props = defineProps<Props>();

const componentName = computed(() =>
  props.componentData?.name || '自我评价'
);

// 组件数据
const componentProps = computed(() =>
  props.componentData?.props || {}
);

const defaultConfig = computed(() =>
  props.componentData?.defaultConfig || {}
);

// 评价数据
const evaluations = computed(() => {
  return componentProps.value.evaluations || [];
});

// 样式函数 - 全部使用传入的安全styles对象
const getContainerStyle = (styles: any) => {
  if (!styles) return {};

  return {
    border: styles.border,
    borderRadius: styles.borderRadius,
    backgroundColor: styles.backgroundColor,
    padding: styles.padding,
    margin: styles.margin,
    fontFamily: styles.fontFamily,
    fontSize: styles.bodySize,
    lineHeight: styles.lineHeight,
    overflow: 'hidden',
    // 新增：确保容器宽度适应父容器
    width: '100%',
    boxSizing: 'border-box'
  };
};

const getItemStyle = (styles: any, index: number) => {
  if (!styles) return {};

  return {
    marginBottom: index < evaluations.value.length - 1 ? '20px' : '0',
    paddingBottom: index < evaluations.value.length - 1 ? '20px' : '0',
    borderBottom: index < evaluations.value.length - 1 ?
      `1px dashed ${styles.secondaryColor || '#e8e8e8'}` : 'none'
  };
};

const getContentStyle = (styles: any) => {
  if (!styles) return {};

  return {
    color: styles.textColor,
    fontSize: styles.bodySize,
    lineHeight: styles.lineHeight,
    textAlign: 'justify',
    whiteSpace: 'pre-wrap',
    wordBreak: 'break-word',
    marginBottom: '16px',
    // 新增：确保内容不溢出
    maxWidth: '100%',
    overflowWrap: 'break-word'
  };
};

const getSectionTitleStyle = (styles: any) => {
  if (!styles) return {};

  return {
    color: styles.titleColor,
    fontSize: styles.bodySize,
    fontWeight: 'bold',
    marginBottom: '8px',
    display: 'block'
  };
};

const getHighlightTagStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '12px',
    color: styles.accentColor || '#ff7a45',
    backgroundColor: styles.highlightBackground || '#fff7e6',
    padding: '4px 12px',
    borderRadius: '16px',
    border: `1px solid ${styles.accentColor ? `${styles.accentColor}30` : 'rgba(255, 122, 69, 0.3)'}`,
    // 新增：确保标签不超出容器
    maxWidth: '100%',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    whiteSpace: 'nowrap'
  };
};

const getPlaceholderStyle = (styles: any) => {
  if (!styles) return {};

  return {
    textAlign: 'center',
    color: '#999',
    padding: '40px 20px',
    background: 'repeating-linear-gradient(45deg, #fafafa, #fafafa 10px, #f0f0f0 10px, #f0f0f0 20px)',
    borderRadius: '4px',
    fontSize: styles.bodySize,
    // 新增：确保占位符宽度适应
    width: '100%',
    boxSizing: 'border-box'
  };
};
</script>

<style scoped>
.self-evaluation-container {
  font-family: inherit;
  /* 新增：确保基础样式 */
  width: 100%;
  box-sizing: border-box;
}

.evaluation-content {
  white-space: pre-wrap;
  word-break: break-word;
  /* 新增：响应式文本 */
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
  /* 新增：确保占位符响应式 */
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
    padding: 16px !important; /* 移动端减小内边距 */
    margin: 0 0 16px 0 !important; /* 移动端减小外边距 */
  }

  .evaluation-item {
    margin-bottom: 16px !important;
    padding-bottom: 16px !important;
  }

  .evaluation-content {
    font-size: 14px !important; /* 移动端字体稍微调整 */
    line-height: 1.6 !important;
    text-align: left; /* 移动端左对齐，更易读 */
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
    /* 移动端禁用长文本省略，直接换行 */
    white-space: normal;
    word-break: break-word;
  }

  .highlights-title,
  .section-title {
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
  .self-evaluation-container {
    padding: 12px !important;
    border-radius: 6px !important;
  }

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
  .self-evaluation-container {
    padding: 20px !important;
  }

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
    border: none !important;
    box-shadow: none !important;
    background: white !important;
    padding: 12px !important;
    margin: 0 0 12px 0 !important;
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