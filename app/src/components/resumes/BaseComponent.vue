<!-- components/resumes/BaseComponent.vue -->
<template>
  <view class="base-component" :style="wrapperStyle">
    <view v-if="showHeader" class="component-header" :style="headerStyle">
      <text class="component-title" :style="titleStyle">{{ displayTitle }}</text>
    </view>
    <view class="component-content" :style="contentStyle">
      <!-- 传入安全的样式对象 -->
      <slot :styles="computedStyles"></slot>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from "vue";
import type { TemplateGlobalStyle } from "@/types/template";
import type { TemplateComponentResult } from "@/types/template-component";

interface Props {
  componentData: TemplateComponentResult;
  globalStyle?: TemplateGlobalStyle;
  overrideStyles?: Record<string, any>;
  showHeader?: boolean;
  customTitle?: string;
  // 新增：是否启用响应式居中 - 由父组件控制
  responsiveCenter?: boolean;
  // 新增：容器最大宽度 - 由父组件控制
  maxWidth?: string;
}

const props = withDefaults(defineProps<Props>(), {
  globalStyle: () => ({}),
  overrideStyles: () => ({}),
  showHeader: true,
  customTitle: "",
  // 默认不居中，由DynamicRenderer控制布局
  responsiveCenter: false,
  maxWidth: "100%"
});

// 安全的样式获取函数
const getSafeValue = <T>(value: T, defaultValue: T): T => {
  return value !== undefined && value !== null ? value : defaultValue;
};

// 计算所有合并样式 - 确保每个属性都有安全的默认值
const computedStyles = computed(() => {
  const global = props.globalStyle || {};
  const componentStyles = props.componentData?.styles || {};
  const defaultStyles = props.componentData?.defaultConfig?.styles || {};
  const override = props.overrideStyles || {};

  return {
    // 字体相关
    fontFamily: getSafeValue(
      override.fontFamily ||
      componentStyles.fontFamily ||
      defaultStyles.fontFamily ||
      global.fontFamily,
      "'PingFang SC', 'Helvetica Neue', Arial, sans-serif"
    ),

    // 字号
    titleSize: getSafeValue(
      override.titleSize ||
      componentStyles.titleSize ||
      defaultStyles.titleSize ||
      (global.fontSizes?.h1 ? `${global.fontSizes.h1}px` : undefined),
      "18px"
    ),

    bodySize: getSafeValue(
      override.fontSize ||
      componentStyles.fontSize ||
      defaultStyles.fontSize ||
      (global.fontSizes?.body ? `${global.fontSizes.body}px` : undefined),
      "14px"
    ),

    // 颜色
    primaryColor: getSafeValue(
      global.primaryColor,
      "#1890ff"
    ),

    accentColor: getSafeValue(
      global.accentColor,
      "#52c41a"
    ),

    secondaryColor: getSafeValue(
      global.secondaryColor,
      "#f0f0f0"
    ),

    titleColor: getSafeValue(
      override.titleColor ||
      componentStyles.titleColor ||
      defaultStyles.titleColor,
      global.primaryColor || "#1890ff"
    ),

    textColor: getSafeValue(
      override.color ||
      componentStyles.color ||
      defaultStyles.color ||
      componentStyles.contentColor ||
      defaultStyles.contentColor,
      "#555555"
    ),

    // 背景和边框
    backgroundColor: getSafeValue(
      override.backgroundColor ||
      componentStyles.backgroundColor ||
      defaultStyles.backgroundColor,
      global.backgroundColor || "#ffffff"
    ),

    border: getSafeValue(
      override.border ||
      componentStyles.border ||
      defaultStyles.border,
      "1px solid #f0f0f0"
    ),

    borderRadius: getSafeValue(
      override.borderRadius ||
      componentStyles.borderRadius ||
      defaultStyles.borderRadius,
      "8px"
    ),

    // 间距 - 注意：不设置 margin，由 DynamicRenderer 控制
    padding: getSafeValue(
      override.padding ||
      componentStyles.padding ||
      defaultStyles.padding ||
      global.spacing?.padding,
      "16px"
    ),

    margin: getSafeValue(
      override.margin ||
      componentStyles.margin ||
      defaultStyles.margin,
      // 默认不设置 margin，让父容器控制
      "0"
    ),

    // 行高
    lineHeight: getSafeValue(
      override.lineHeight ||
      componentStyles.lineHeight ||
      defaultStyles.lineHeight ||
      global.spacing?.lineHeight,
      "1.6"
    ),

    // 宽度控制 - 由父组件传入
    maxWidth: getSafeValue(
      override.maxWidth ||
      componentStyles.maxWidth ||
      defaultStyles.maxWidth,
      props.maxWidth
    ),

    width: getSafeValue(
      override.width ||
      componentStyles.width ||
      defaultStyles.width,
      "100%"
    ),

    // 其他
    boxShadow: getSafeValue(
      override.boxShadow ||
      componentStyles.boxShadow ||
      defaultStyles.boxShadow,
      "0 2px 8px rgba(0,0,0,0.1)"
    ),

    // 特殊样式（用于子组件）
    highlightBackground: getSafeValue(
      componentStyles.highlightBackground ||
      defaultStyles.highlightBackground,
      "#fff7e6"
    ),

    tagBackground: getSafeValue(
      componentStyles.tagBackground ||
      defaultStyles.tagBackground,
      "#e6f7ff"
    ),

    // 从全局布局中获取
    spacing: getSafeValue(
      global.spacing,
      { sectionMargin: "16px", padding: "12px", lineHeight: "1.5" }
    )
  };
});

// 显示标题
const displayTitle = computed(() => {
  return props.customTitle || props.componentData?.name || "未命名组件";
});

// 是否显示标题
const showHeader = computed(() => {
  if (props.showHeader === false) return false;
  const propsData = props.componentData?.props || {};
  return propsData.showTitle !== false;
});

// 包装器样式（只用于容器）
const wrapperStyle = computed(() => {
  const styles = computedStyles.value;

  const baseStyle: any = {
    // 布局相关 - 由父容器控制
    margin: styles.margin,
    padding: styles.padding,
    width: styles.width,
    maxWidth: styles.maxWidth,

    // 样式相关
    backgroundColor: styles.backgroundColor,
    borderRadius: styles.borderRadius,
    border: styles.border,
    boxShadow: styles.boxShadow,
    fontFamily: styles.fontFamily,
    fontSize: styles.bodySize,
    lineHeight: styles.lineHeight,

    // 基础设置
    overflow: "hidden",
    boxSizing: "border-box"
  };

  // 只有在明确启用居中时才设置居中样式
  if (props.responsiveCenter) {
    baseStyle.marginLeft = "auto";
    baseStyle.marginRight = "auto";
    baseStyle.display = "block";
  }

  // 清理undefined值
  Object.keys(baseStyle).forEach(key => {
    if (baseStyle[key] === undefined) {
      delete baseStyle[key];
    }
  });

  return baseStyle;
});

// 标题样式
const titleStyle = computed(() => {
  const styles = computedStyles.value;

  return {
    fontSize: styles.titleSize,
    color: styles.titleColor,
    fontWeight: "bold",
    lineHeight: "1.2"
  };
});

// 头部样式
const headerStyle = computed(() => {
  const styles = computedStyles.value;

  return {
    marginBottom: "12px",
    paddingBottom: "8px",
    borderBottom: `1px solid ${styles.secondaryColor}`
  };
});

// 内容样式
const contentStyle = computed(() => {
  const styles = computedStyles.value;

  return {
    color: styles.textColor
  };
});

// 暴露给子组件使用的样式对象
defineExpose({
  computedStyles
});
</script>

<style scoped>
.base-component {
  box-sizing: border-box;
  transition: all 0.3s ease;
  break-inside: avoid; /* 防止打印时内容分页断开 */
}

.component-header {
  display: flex;
  align-items: center;
}

.component-title {
  flex: 1;
}

.component-content {
  min-height: 20px;
}

/* 响应式设计 - 最小修改原则 */
@media (max-width: 768px) {
  .base-component {
    /* 只调整内边距，不影响布局 */
    padding: 12px !important;
    border-radius: 6px !important;
  }

  .component-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}

/* 打印样式 */
@media print {
  .base-component {
    margin: 0 !important;
    box-shadow: none !important;
    border: 1px solid #eee !important;
    break-inside: avoid;
  }
}
</style>