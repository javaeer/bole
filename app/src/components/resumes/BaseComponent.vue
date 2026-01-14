<template>
  <view 
    class="base-component" 
    :class="componentClasses"
    :style="wrapperStyle"
  >
    <view v-if="showHeader" class="component-header" :style="headerStyle">
      <text class="component-title" :style="titleStyle">{{ displayTitle }}</text>
    </view>
    <view class="component-content" :style="contentStyle">
      <!-- 只传递必要的样式给子组件 -->
      <slot :styles="exposedStyles"></slot>
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
  responsiveCenter?: boolean;
  maxWidth?: string;
}

const props = withDefaults(defineProps<Props>(), {
  globalStyle: () => ({}),
  overrideStyles: () => ({}),
  showHeader: true,
  customTitle: "",
  responsiveCenter: false,
  maxWidth: "100%"
});

// ===================== 样式工具函数 =====================

/**
 * 样式合并函数 - 按照优先级合并样式
 * 优先级: overrideStyles > componentStyles > defaultStyles > globalStyle
 */
const mergeStyleValue = (
  key: string, 
  defaultValue: any,
  unitTransformer?: (value: any) => string
) => {
  // 定义样式源及其优先级
  const styleSources = [
    props.overrideStyles,
    props.componentData?.styles,
    props.componentData?.defaultConfig?.styles,
    props.globalStyle
  ];

  // 按优先级查找值
  for (const source of styleSources) {
    const value = source?.[key];
    if (value !== undefined && value !== null && value !== "") {
      return unitTransformer ? unitTransformer(value) : value;
    }
  }

  // 返回默认值
  return defaultValue;
};

/**
 * CSS单位标准化函数
 * - 数字转换为px
 * - 已有单位的字符串保持不变
 */
const normalizeUnit = (value: string | number): string => {
  if (typeof value === "number") {
    return `${value}px`;
  }
  
  // 如果是纯数字字符串，添加px
  if (/^\d+(\.\d+)?$/.test(value)) {
    return `${value}px`;
  }
  
  return value;
};

/**
 * 提取字体相关样式
 */
const getFontStyles = () => {
  const global = props.globalStyle || {};
  
  return {
    // 字体族
    fontFamily: mergeStyleValue(
      "fontFamily",
      "'PingFang SC', 'Helvetica Neue', Arial, sans-serif"
    ),
    
    // 标题字号
    titleSize: mergeStyleValue(
      "titleSize",
      "18px",
      (value) => normalizeUnit(value)
    ),
    
    // 正文字号
    bodySize: mergeStyleValue(
      "fontSize",
      normalizeUnit(global.fontSizes?.body || 14)
    ),
    
    // 行高
    lineHeight: mergeStyleValue(
      "lineHeight",
      "1.6"
    )
  };
};

/**
 * 提取颜色相关样式
 */
const getColorStyles = () => {
  const global = props.globalStyle || {};
  
  return {
    // 主色系
    primaryColor: global.primaryColor || "#1890ff",
    accentColor: global.accentColor || "#52c41a",
    secondaryColor: global.secondaryColor || "#f0f0f0",
    backgroundColor: global.backgroundColor || "#ffffff",
    
    // 文本颜色
    titleColor: mergeStyleValue(
      "titleColor",
      global.primaryColor || "#1890ff"
    ),
    
    textColor: mergeStyleValue(
      "color",
      mergeStyleValue("contentColor", "#555555")
    )
  };
};

/**
 * 提取间距和布局样式
 */
const getLayoutStyles = () => {
  const global = props.globalStyle || {};
  
  return {
    // 内边距
    padding: mergeStyleValue(
      "padding",
      normalizeUnit(global.spacing?.padding || 16)
    ),
    
    // 外边距 - 默认由父容器控制
    margin: mergeStyleValue(
      "margin",
      "0"
    ),
    
    // 宽度
    width: mergeStyleValue("width", "100%"),
    
    // 最大宽度
    maxWidth: mergeStyleValue("maxWidth", props.maxWidth),
    
    // 圆角
    borderRadius: mergeStyleValue(
      "borderRadius",
      normalizeUnit(8)
    ),
    
    // 边框
    border: mergeStyleValue("border", "1px solid #f0f0f0"),
    
    // 阴影
    boxShadow: mergeStyleValue("boxShadow", "0 2px 8px rgba(0,0,0,0.1)")
  };
};

/**
 * 提取特殊样式（用于子组件）
 */
const getSpecialStyles = () => {
  return {
    highlightBackground: mergeStyleValue("highlightBackground", "#fff7e6"),
    tagBackground: mergeStyleValue("tagBackground", "#e6f7ff"),
    progressColor: mergeStyleValue("progressColor", "#1890ff")
  };
};

// ===================== 计算属性 =====================

// 合并所有样式
const allStyles = computed(() => ({
  ...getFontStyles(),
  ...getColorStyles(),
  ...getLayoutStyles(),
  ...getSpecialStyles()
}));

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

// 组件类名
const componentClasses = computed(() => ({
  "responsive-center": props.responsiveCenter,
  "with-header": showHeader.value,
  "without-header": !showHeader.value
}));

// ===================== 样式计算 =====================

// 包装器样式 - 只包含容器样式
const wrapperStyle = computed(() => {
  const styles = allStyles.value;
  const style: Record<string, any> = {};

  // 基础样式
  style.fontFamily = styles.fontFamily;
  style.fontSize = styles.bodySize;
  style.lineHeight = styles.lineHeight;
  
  // 背景和边框
  if (styles.backgroundColor) {
    style.backgroundColor = styles.backgroundColor;
  }
  
  if (styles.border && styles.border !== "none") {
    style.border = styles.border;
  }
  
  if (styles.borderRadius && styles.borderRadius !== "0") {
    style.borderRadius = styles.borderRadius;
  }
  
  if (styles.boxShadow && styles.boxShadow !== "none") {
    style.boxShadow = styles.boxShadow;
  }
  
  // 间距
  if (styles.padding && styles.padding !== "0") {
    style.padding = styles.padding;
  }
  
  // 尺寸
  style.width = styles.width;
  style.maxWidth = styles.maxWidth;
  
  // 布局
  if (props.responsiveCenter) {
    style.marginLeft = "auto";
    style.marginRight = "auto";
  } else if (styles.margin && styles.margin !== "0") {
    style.margin = styles.margin;
  }
  
  // 基础设置
  style.overflow = "hidden";
  style.boxSizing = "border-box";
  
  // 设置CSS变量供子元素使用
  style["--base-primary-color"] = styles.primaryColor;
  style["--base-accent-color"] = styles.accentColor;
  style["--base-secondary-color"] = styles.secondaryColor;
  style["--base-text-color"] = styles.textColor;
  style["--base-title-color"] = styles.titleColor;
  style["--base-highlight-bg"] = styles.highlightBackground;
  style["--base-tag-bg"] = styles.tagBackground;
  style["--base-progress-color"] = styles.progressColor;
  
  return style;
});

// 标题样式
const titleStyle = computed(() => {
  const styles = allStyles.value;
  
  return {
    fontSize: styles.titleSize,
    color: "var(--base-title-color, #1890ff)",
    fontWeight: "bold",
    lineHeight: "1.2"
  };
});

// 头部样式
const headerStyle = computed(() => {
  const styles = allStyles.value;
  
  return {
    marginBottom: "12px",
    paddingBottom: "8px",
    borderBottom: `1px solid var(--base-secondary-color, #f0f0f0)`
  };
});

// 内容样式
const contentStyle = computed(() => {
  const styles = allStyles.value;
  
  return {
    color: "var(--base-text-color, #555555)"
  };
});

// 暴露给子组件的样式（仅颜色和特殊样式）
const exposedStyles = computed(() => {
  const styles = allStyles.value;
  
  return {
    primaryColor: styles.primaryColor,
    accentColor: styles.accentColor,
    textColor: styles.textColor,
    titleColor: styles.titleColor,
    highlightBackground: styles.highlightBackground,
    tagBackground: styles.tagBackground,
    progressColor: styles.progressColor,
    
    // 字体相关（只读）
    titleSize: styles.titleSize,
    bodySize: styles.bodySize,
    lineHeight: styles.lineHeight
  };
});

// 暴露必要的样式给父组件
defineExpose({
  exposedStyles,
  componentClasses
});
</script>

<style scoped lang="scss">
.base-component {
  // 基础样式
  box-sizing: border-box;
  transition: all 0.3s ease;
  break-inside: avoid; // 防止打印时内容分页断开
  
  // 使用CSS变量设置默认值
  --base-primary-color: #1890ff;
  --base-accent-color: #52c41a;
  --base-secondary-color: #f0f0f0;
  --base-text-color: #555555;
  --base-title-color: #1890ff;
  --base-highlight-bg: #fff7e6;
  --base-tag-bg: #e6f7ff;
  --base-progress-color: #1890ff;
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

// 响应式居中类
.responsive-center {
  display: block;
  margin-left: auto;
  margin-right: auto;
}

// 有标题和无标题的样式差异
.with-header {
  .component-content {
    margin-top: 0;
  }
}

.without-header {
  .component-content {
    margin-top: 0;
  }
}

/* ===================== 响应式设计 ===================== */
@media (max-width: 768px) {
  .base-component {
    // 在移动端调整内边距
    padding: 12px !important;
    border-radius: 6px !important;
    
    // 调整字体大小
    font-size: 14px !important;
  }
  
  .component-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .component-title {
    font-size: 16px !important;
  }
}

/* ===================== 打印样式 ===================== */
@media print {
  .base-component {
    // 移除阴影和背景色，确保打印清晰
    box-shadow: none !important;
    background-color: transparent !important;
    
    // 确保边框可见但不会太深
    border: 1px solid #ddd !important;
    
    // 防止分页断开
    page-break-inside: avoid;
    break-inside: avoid;
    
    // 调整边距，节省纸张空间
    margin: 0 0 12px 0 !important;
    padding: 8px !important;
  }
  
  .component-header {
    border-bottom: 1px solid #ccc !important;
  }
  
  .component-title {
    color: #000 !important;
  }
  
  .component-content {
    color: #333 !important;
  }
  
  // 隐藏不必要的交互元素
  .no-print {
    display: none !important;
  }
}

/* ===================== 深色模式支持 ===================== */
@media (prefers-color-scheme: dark) {
  .base-component:not([data-theme="light"]) {
    --base-text-color: #e0e0e0;
    --base-title-color: #64b5f6;
    --base-secondary-color: #424242;
    --base-highlight-bg: #5d4037;
    --base-tag-bg: #0d47a1;
    
    background-color: #1e1e1e;
    border-color: #424242;
  }
}
</style>