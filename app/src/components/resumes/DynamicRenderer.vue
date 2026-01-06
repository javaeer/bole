<!-- components/resumes/DynamicRenderer.vue -->
<template>
  <!-- 整个简历容器 -->
  <view class="resume-container" :style="resumeContainerStyle">

    <!-- 单列布局 -->
    <template v-if="layoutType === 'single'">
      <view class="single-layout" :style="singleLayoutStyle">
        <template v-for="component in orderedComponents" :key="component.key + '_' + component.componentId">
          <view class="component-item" :style="getComponentItemStyle(component)">
            <Renderer
              :component-data="component"
              :global-style="globalStyle"
            />
          </view>
        </template>
      </view>
    </template>

    <!-- 双列布局 -->
    <template v-else-if="layoutType === 'two-column'">
      <view class="two-column-layout" :style="twoColumnLayoutStyle">
        <!-- 左列 -->
        <view class="left-column" :style="leftColumnStyle">
          <template v-for="component in leftColumnComponents" :key="component.key + '_' + component.componentId">
            <view class="component-item" :style="getComponentItemStyle(component)">
              <Renderer
                :component-data="component"
                :global-style="globalStyle"
              />
            </view>
          </template>
        </view>

        <!-- 右列 -->
        <view class="right-column" :style="rightColumnStyle">
          <template v-for="component in rightColumnComponents" :key="component.key + '_' + component.componentId">
            <view class="component-item" :style="getComponentItemStyle(component)">
              <Renderer
                :component-data="component"
                :global-style="globalStyle"
              />
            </view>
          </template>
        </view>
      </view>
    </template>

    <!-- 时间线布局 -->
    <template v-else-if="layoutType === 'timeline'">
      <view class="timeline-layout" :style="timelineLayoutStyle">
        <!-- 时间线 -->
        <view class="timeline-line" :style="timelineLineStyle"></view>

        <template v-for="(component, index) in orderedComponents" :key="component.key + '_' + component.componentId">
          <view class="timeline-item" :style="getTimelineItemStyle(index)">
            <!-- 时间线节点 -->
            <view class="timeline-node" :style="timelineNodeStyle"></view>

            <!-- 组件内容 -->
            <view class="timeline-content" :style="getTimelineContentStyle(component)">
              <Renderer
                :component-data="component"
                :global-style="globalStyle"
              />
            </view>
          </view>
        </template>
      </view>
    </template>

    <!-- 卡片式布局 -->
    <template v-else-if="layoutType === 'card'">
      <view class="card-layout" :style="cardLayoutStyle">
        <template v-for="component in orderedComponents" :key="component.key + '_' + component.componentId">
          <view class="card-item" :style="getCardItemStyle(component)">
            <Renderer
              :component-data="component"
              :global-style="globalStyle"
              :custom-styles="getCardContentStyle(component)"
            />
          </view>
        </template>
      </view>
    </template>

    <!-- 混合布局（上部分单列，下部分双列） -->
    <template v-else-if="layoutType === 'mixed'">
      <view class="mixed-layout" :style="mixedLayoutStyle">
        <!-- 上部分（单列） -->
        <view class="mixed-top" v-if="topComponents.length > 0">
          <template v-for="component in topComponents" :key="component.key + '_' + component.componentId">
            <view class="component-item" :style="getComponentItemStyle(component)">
              <Renderer
                :component-data="component"
                :global-style="globalStyle"
              />
            </view>
          </template>
        </view>

        <!-- 下部分（双列） -->
        <view class="mixed-bottom" v-if="bottomComponents.length > 0" :style="mixedBottomStyle">
          <view class="left-column" :style="leftColumnStyle">
            <template v-for="component in bottomLeftComponents" :key="component.key + '_' + component.componentId">
              <view class="component-item" :style="getComponentItemStyle(component)">
                <Renderer
                  :component-data="component"
                  :global-style="globalStyle"
                />
              </view>
            </template>
          </view>

          <view class="right-column" :style="rightColumnStyle">
            <template v-for="component in bottomRightComponents" :key="component.key + '_' + component.componentId">
              <view class="component-item" :style="getComponentItemStyle(component)">
                <Renderer
                  :component-data="component"
                  :global-style="globalStyle"
                />
              </view>
            </template>
          </view>
        </view>
      </view>
    </template>

    <!-- 默认单列布局 -->
    <template v-else>
      <view class="single-layout" :style="singleLayoutStyle">
        <template v-for="component in orderedComponents" :key="component.key + '_' + component.componentId">
          <view class="component-item" :style="getComponentItemStyle(component)">
            <Renderer
              :component-data="component"
              :global-style="globalStyle"
            />
          </view>
        </template>
      </view>
    </template>

  </view>
</template>

<script setup lang="ts">
import { computed } from "vue";
import Renderer from "./Renderer.vue";

interface Props {
  resumeData: {
    id?: number | string;
    globalStyle?: any;
    globalLayout?: any;
    components?: any[];
  };
  width?: string;
  height?: string;
}

const props = withDefaults(defineProps<Props>(), {
  width: "210mm", // A4纸宽度
  height: "auto"
});

// 提取数据
const globalStyle = computed(() => props.resumeData.globalStyle || {});
const layout = computed(() => props.resumeData.globalLayout || {});
const components = computed(() => props.resumeData.components || []);

// 布局类型
const layoutType = computed(() => {
  return layout.value.type || "single";
});

// 按 componentOrder 排序的组件
const orderedComponents = computed(() => {
  const order = layout.value.componentOrder || [];
  const componentMap = new Map();

  // 创建组件映射
  components.value.forEach(comp => {
    if (comp.key) {
      componentMap.set(comp.key, comp);
    }
  });

  // 按顺序过滤和排序
  const ordered: any[] = [];
  order.forEach(key => {
    if (componentMap.has(key)) {
      ordered.push(componentMap.get(key));
    }
  });

  // 添加未排序的组件
  components.value.forEach(comp => {
    if (!order.includes(comp.key)) {
      ordered.push(comp);
    }
  });

  return ordered;
});

// 双列布局的左右列组件
const leftColumnComponents = computed(() => {
  return orderedComponents.value.filter((_, index) => index % 2 === 0);
});

const rightColumnComponents = computed(() => {
  return orderedComponents.value.filter((_, index) => index % 2 === 1);
});

// 混合布局的组件分组
const topComponents = computed(() => {
  // 混合布局：前2个组件在上面，剩下的在下面双列
  return orderedComponents.value.slice(0, 2);
});

const bottomComponents = computed(() => {
  return orderedComponents.value.slice(2);
});

const bottomLeftComponents = computed(() => {
  return bottomComponents.value.filter((_, index) => index % 2 === 0);
});

const bottomRightComponents = computed(() => {
  return bottomComponents.value.filter((_, index) => index % 2 === 1);
});

// 简历容器样式 - 修复居中问题
const resumeContainerStyle = computed(() => {
  const style: any = {
    width: "100%",
    maxWidth: props.width,
    height: props.height,
    backgroundColor: globalStyle.value.backgroundColor || "#f0f8ff",
    fontFamily: globalStyle.value.fontFamily || "'PingFang SC', 'Helvetica Neue', Arial, sans-serif",
    fontSize: (globalStyle.value.fontSizes?.body ? `${globalStyle.value.fontSizes.body}px` : "13px"),
    lineHeight: globalStyle.value.spacing?.lineHeight || "1.5",
    margin: "0 auto",
    boxSizing: "border-box",
    minHeight: "297mm", // A4纸高度
    display: "flex",
    flexDirection: "column",
    alignItems: "center"
  };

  // 根据布局类型调整内边距
  if (layoutType.value === 'timeline') {
    style.padding = "20px";
  } else if (layoutType.value === 'card') {
    style.padding = "20px";
    style.backgroundColor = "#f5f7fa";
  } else {
    style.padding = globalStyle.value.spacing?.padding || "20px";
  }

  return style;
});

// 单列布局样式 - 修复居中
const singleLayoutStyle = computed(() => ({
  width: "100%",
  maxWidth: "800px",
  margin: "0 auto"
}));

// 双列布局样式 - 修复响应式
const twoColumnLayoutStyle = computed(() => ({
  display: "flex",
  gap: layout.value.gap || globalStyle.value.spacing?.sectionMargin || "20px",
  width: "100%",
  maxWidth: "1200px",
  margin: "0 auto"
}));

const leftColumnStyle = computed(() => ({
  flex: 1
}));

const rightColumnStyle = computed(() => ({
  flex: 1
}));

// 时间线布局样式 - 修复响应式
const timelineLayoutStyle = computed(() => ({
  position: "relative",
  width: "100%",
  maxWidth: "900px",
  margin: "0 auto",
  padding: "20px 0"
}));

const timelineLineStyle = computed(() => ({
  position: "absolute",
  left: "50%",
  top: "0",
  bottom: "0",
  width: "3px",
  backgroundColor: globalStyle.value.secondaryColor || globalStyle.value.primaryColor || "#5ac8fa",
  opacity: "0.6",
  transform: "translateX(-50%)",
  zIndex: 1
}));

const timelineNodeStyle = computed(() => ({
  position: "absolute",
  left: "50%",
  width: "16px",
  height: "16px",
  borderRadius: "50%",
  backgroundColor: globalStyle.value.primaryColor || "#5ac8fa",
  border: `3px solid ${globalStyle.value.backgroundColor || "#f0f8ff"}`,
  transform: "translate(-50%, -50%)",
  zIndex: 2,
  boxShadow: "0 2px 8px rgba(0,0,0,0.15)"
}));

// 卡片布局样式 - 修复响应式
const cardLayoutStyle = computed(() => ({
  display: "grid",
  gridTemplateColumns: "repeat(auto-fit, minmax(300px, 1fr))",
  gap: "20px",
  width: "100%",
  maxWidth: "1200px",
  margin: "0 auto"
}));

// 混合布局样式
const mixedLayoutStyle = computed(() => ({
  width: "100%",
  maxWidth: "1000px",
  margin: "0 auto"
}));

const mixedBottomStyle = computed(() => ({
  display: "flex",
  gap: "20px",
  width: "100%",
  marginTop: "30px"
}));

// 获取组件样式
const getComponentItemStyle = (component: any) => {
  const baseStyle: any = {
    width: "100%",
    marginBottom: globalStyle.value.spacing?.sectionMargin || "16px",
  };

  // 如果是卡片布局，添加额外样式
  if (layoutType.value === 'card') {
    baseStyle.marginBottom = "0";
  }

  if (component.styles && Object.keys(component.styles).length > 0) {
    return { ...baseStyle, ...component.styles };
  }

  // 使用默认配置的样式
  if (component.defaultConfig?.styles) {
    return { ...baseStyle, ...component.defaultConfig.styles };
  }

  return baseStyle;
};

// 获取时间线项目样式
const getTimelineItemStyle = (index: number) => {
  const isLast = index === orderedComponents.value.length - 1;

  return {
    position: "relative",
    width: "100%",
    marginBottom: isLast ? "0" : "40px",
    minHeight: "100px"
  };
};

// 获取时间线内容样式 - 修复左右交替布局
const getTimelineContentStyle = (component: any) => {
  const isEven = orderedComponents.value.indexOf(component) % 2 === 0;

  const baseStyle: any = {
    position: "relative",
    backgroundColor: "#ffffff",
    borderRadius: "8px",
    padding: "20px",
    boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
    width: "calc(50% - 40px)",
    marginLeft: isEven ? "0" : "auto",
    marginRight: isEven ? "auto" : "0",
    borderLeft: isEven ? `4px solid ${globalStyle.value.primaryColor || "#5ac8fa"}` : "none",
    borderRight: !isEven ? `4px solid ${globalStyle.value.primaryColor || "#5ac8fa"}` : "none"
  };

  // 添加组件的自定义样式
  if (component.styles && Object.keys(component.styles).length > 0) {
    Object.assign(baseStyle, component.styles);
  }

  // 添加默认配置的样式
  if (component.defaultConfig?.styles) {
    Object.assign(baseStyle, component.defaultConfig.styles);
  }

  return baseStyle;
};

// 获取卡片项目样式
const getCardItemStyle = (component: any) => {
  const baseStyle: any = {
    backgroundColor: "#ffffff",
    borderRadius: "12px",
    boxShadow: "0 6px 20px rgba(0,0,0,0.08)",
    overflow: "hidden",
    height: "100%"
  };

  return baseStyle;
};

// 获取卡片内容样式
const getCardContentStyle = (component: any) => {
  const baseStyle: any = {
    padding: "24px"
  };

  return baseStyle;
};
</script>

<style scoped>
.resume-container {
  page-break-inside: avoid;
  break-inside: avoid;
}

/* 单列布局 */
.single-layout {
  width: 100%;
}

/* 双列布局 - 修复响应式 */
.two-column-layout {
  width: 100%;
}

.two-column-layout .left-column,
.two-column-layout .right-column {
  flex: 1;
}

/* 混合布局 */
.mixed-bottom .left-column,
.mixed-bottom .right-column {
  flex: 1;
}

/* 组件项 */
.component-item {
  transition: all 0.3s ease;
  page-break-inside: avoid;
  break-inside: avoid;
  width: 100%;
}

/* 卡片项 */
.card-item {
  page-break-inside: avoid;
  break-inside: avoid;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .resume-container {
    width: 100% !important;
    max-width: 100% !important;
    padding: 16px !important;
    min-height: auto !important;
  }

  /* 双列布局在移动端改为单列 */
  .two-column-layout {
    flex-direction: column !important;
    gap: 16px !important;
  }

  .two-column-layout .left-column,
  .two-column-layout .right-column {
    width: 100% !important;
    flex: none !important;
  }

  /* 混合布局在移动端改为单列 */
  .mixed-bottom {
    flex-direction: column !important;
    gap: 16px !important;
  }

  .mixed-bottom .left-column,
  .mixed-bottom .right-column {
    width: 100% !important;
    flex: none !important;
  }

  /* 卡片布局在移动端改为单列 */
  .card-layout {
    grid-template-columns: 1fr !important;
    gap: 16px !important;
  }

  /* 时间线布局在移动端改为居中单列 */
  .timeline-layout {
    padding: 0 !important;
  }

  .timeline-line {
    display: none;
  }

  .timeline-node {
    display: none;
  }

  .timeline-content {
    width: 100% !important;
    margin: 0 auto 16px auto !important;
    border-left: 4px solid #5ac8fa !important;
    border-right: none !important;
  }

  /* 组件间距调整 */
  .component-item {
    margin-bottom: 16px !important;
  }

  /* 移除悬停效果 */
  .component-item:hover,
  .card-item:hover {
    transform: none !important;
  }
}

/* 小屏幕手机 */
@media (max-width: 480px) {
  .resume-container {
    padding: 12px !important;
  }

  .single-layout,
  .timeline-layout,
  .card-layout,
  .mixed-layout {
    max-width: 100% !important;
  }

  .timeline-content {
    padding: 16px !important;
  }

  .card-layout {
    gap: 12px !important;
  }

  .component-item {
    margin-bottom: 12px !important;
  }
}

/* 平板设备 */
@media (min-width: 769px) and (max-width: 1024px) {
  .resume-container {
    padding: 24px !important;
  }

  .card-layout {
    grid-template-columns: repeat(2, 1fr) !important;
    gap: 20px !important;
  }

  .timeline-content {
    width: calc(50% - 20px) !important;
  }
}

/* 打印样式 */
@media print {
  .resume-container {
    width: 100% !important;
    max-width: 100% !important;
    height: auto !important;
    min-height: auto !important;
    padding: 0 !important;
    background-color: white !important;
    box-shadow: none !important;
  }

  .component-item {
    break-inside: avoid;
    page-break-inside: avoid;
    margin-bottom: 12px !important;
  }

  .timeline-layout {
    padding: 20px 0 !important;
  }

  .timeline-content {
    box-shadow: none !important;
    border: 1px solid #eee !important;
  }

  .card-item {
    box-shadow: none !important;
    border: 1px solid #eee !important;
  }

  /* 打印时隐藏时间线 */
  .timeline-line,
  .timeline-node {
    display: none;
  }
}
</style>