<template>
  <view class="template-preview" :class="device" :style="containerStyle">
    <view class="color-preview-bar" v-if="globalStyle?.primaryColor || globalStyle?.accentColor">
      <view
        class="color-block primary"
        :style="{ backgroundColor: globalStyle?.primaryColor || '#d4af37' }"
      >主色调</view>
      <view
        class="color-block secondary"
        :style="{ backgroundColor: globalStyle?.secondaryColor || '#f9f3e3' }"
      >辅色调</view>
      <view
        class="color-block accent"
        :style="{ backgroundColor: globalStyle?.accentColor || '#f7ef8a' }"
      >强调色</view>
    </view>

    <!-- 根据布局类型渲染不同的布局 -->
    <view v-if="globalLayout?.type === 'single-column'" class="layout-single-column">
      <view
        v-for="(component, index) in filteredComponents"
        :key="`component-${index}-${component.componentId || 'empty'}`"
        class="preview-component-wrapper"
        :style="getComponentWrapperStyle(index)"
      >
        <component-preview
          :component="getComponentConfig(component)"
          :global-style="globalStyle"
          :style="getComponentStyle(component)"
        />
      </view>

      <!-- 空状态 -->
      <view v-if="!filteredComponents || filteredComponents.length === 0" class="empty-preview">
        <view class="empty-content">
          <text class="empty-icon">📄</text>
          <text class="empty-title">暂无内容</text>
          <text class="empty-desc">请添加区块以预览模板效果</text>
        </view>
      </view>
    </view>

    <view v-else-if="globalLayout?.type === 'two-column'" class="layout-two-column">
      <view class="column-left" :style="{ width: getColumnWidth('left') }">
        <view
          v-for="(component, index) in leftColumnComponents"
          :key="`left-${index}-${component.componentId || 'empty'}`"
          class="preview-component-wrapper"
          :style="getComponentWrapperStyle(index, 'left')"
        >
          <component-preview
            :component="getComponentConfig(component)"
            :global-style="globalStyle"
            :style="getComponentStyle(component)"
          />
        </view>

        <!-- 左侧为空状态 -->
        <view v-if="leftColumnComponents.length === 0" class="empty-column">
          <text class="empty-column-text">左侧栏位</text>
        </view>
      </view>

      <view class="column-right" :style="{ width: getColumnWidth('right') }">
        <view
          v-for="(component, index) in rightColumnComponents"
          :key="`right-${index}-${component.componentId || 'empty'}`"
          class="preview-component-wrapper"
          :style="getComponentWrapperStyle(index, 'right')"
        >
          <component-preview
            :component="getComponentConfig(component)"
            :global-style="globalStyle"
            :style="getComponentStyle(component)"
          />
        </view>

        <!-- 右侧为空状态 -->
        <view v-if="rightColumnComponents.length === 0" class="empty-column">
          <text class="empty-column-text">右侧栏位</text>
        </view>
      </view>
    </view>

    <view v-else-if="globalLayout?.type === 'three-column'" class="layout-three-column">
      <view class="column" v-for="(columnComps, colIndex) in [firstColumnComponents, secondColumnComponents, thirdColumnComponents]" :key="`col-${colIndex}`">
        <view
          v-for="(component, index) in columnComps"
          :key="`col${colIndex}-${index}-${component.componentId || 'empty'}`"
          class="preview-component-wrapper"
          :style="getComponentWrapperStyle(index, `col${colIndex}`)"
        >
          <component-preview
            :component="getComponentConfig(component)"
            :global-style="globalStyle"
            :style="getComponentStyle(component)"
          />
        </view>

        <!-- 栏位为空状态 -->
        <view v-if="columnComps.length === 0" class="empty-column">
          <text class="empty-column-text">栏位 {{ colIndex + 1 }}</text>
        </view>
      </view>
    </view>

    <!-- 默认布局 -->
    <view v-else class="layout-single-column">
      <view
        v-for="(component, index) in filteredComponents"
        :key="`default-${index}-${component.componentId || 'empty'}`"
        class="preview-component-wrapper"
        :style="getComponentWrapperStyle(index)"
      >
        <component-preview
          :component="getComponentConfig(component)"
          :global-style="globalStyle"
          :style="getComponentStyle(component)"
        />
      </view>

      <view v-if="!filteredComponents || filteredComponents.length === 0" class="empty-preview">
        <view class="empty-content">
          <text class="empty-icon">📄</text>
          <text class="empty-title">暂无内容</text>
          <text class="empty-desc">请添加区块以预览模板效果</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from "vue";
import ComponentPreview from "./ComponentPreview.vue";
import { COMPONENT_LIBRARY } from "@/constants/component";

interface Props {
  components?: any[];
  globalLayout?: {
    type?: string;
    columns?: {
      left?: number;
      right?: number;
    };
    orientation?: string;
    componentOrder?: string[];
  };
  globalStyle?: {
    primaryColor?: string;
    secondaryColor?: string;
    accentColor?: string;
    backgroundColor?: string;
    fontFamily?: string;
    fontSizes?: {
      h1?: string;
      body?: string;
    };
    spacing?: {
      sectionMargin?: string;
      padding?: string;
      lineHeight?: string;
    };
    theme?: string;
    headerColor?: string;
  };
  device?: string;
}

const props = withDefaults(defineProps<Props>(), {
  components: () => [],
  globalLayout: () => ({
    type: 'single-column',
    columns: { left: 40, right: 60 },
    orientation: 'portrait',
    componentOrder: []
  }),
  globalStyle: () => ({
    primaryColor: '#d4af37',
    secondaryColor: '#f9f3e3',
    accentColor: '#f7ef8a',
    backgroundColor: '#ffffff',
    fontFamily: "'Microsoft YaHei', 'PingFang SC', sans-serif",
    fontSizes: { h1: '24', body: '14' },
    spacing: { sectionMargin: '20px', padding: '15px', lineHeight: '1.5' },
    theme: 'light'
  }),
  device: 'desktop'
});

// 过滤掉无效组件（没有 componentId 的组件）
const filteredComponents = computed(() => {
  return (props.components || []).filter(comp => comp && comp.componentId);
});

// 计算属性
const containerStyle = computed(() => {
  const style: any = {};
  const globalStyle = props.globalStyle;

  // 设置字体
  if (globalStyle?.fontFamily) {
    style.fontFamily = globalStyle.fontFamily;
  }

  // 设置背景颜色
  if (globalStyle?.backgroundColor) {
    style.backgroundColor = globalStyle.backgroundColor;
  } else if (globalStyle?.secondaryColor) {
    style.backgroundColor = globalStyle.secondaryColor;
  } else {
    style.backgroundColor = '#ffffff';
  }

  // 设置基础字体大小
  if (globalStyle?.fontSizes?.body) {
    style.fontSize = `${globalStyle.fontSizes.body}px`;
  }

  // 设置行高
  if (globalStyle?.spacing?.lineHeight) {
    style.lineHeight = globalStyle.spacing.lineHeight;
  }

  // 设置内边距
  if (globalStyle?.spacing?.padding) {
    style.padding = globalStyle.spacing.padding;
  }

  // 设置主题颜色作为CSS变量
  if (globalStyle?.primaryColor) {
    style['--primary-color'] = globalStyle.primaryColor;
  }
  if (globalStyle?.accentColor) {
    style['--accent-color'] = globalStyle.accentColor;
  }
  if (globalStyle?.secondaryColor) {
    style['--secondary-color'] = globalStyle.secondaryColor;
  }

  return style;
});

// 双栏布局宽度计算
const getColumnWidth = (side: 'left' | 'right') => {
  if (props.globalLayout?.type === 'two-column') {
    const columns = props.globalLayout.columns || { left: 40, right: 60 };
    if (side === 'left') {
      return `${columns.left || 40}%`;
    } else {
      return `${columns.right || 60}%`;
    }
  }
  return '50%';
};

// 组件分配逻辑 - 修正：使用 filteredComponents
const leftColumnComponents = computed(() => {
  const components = filteredComponents.value;
  const mid = Math.ceil(components.length / 2);
  return components.slice(0, mid);
});

const rightColumnComponents = computed(() => {
  const components = filteredComponents.value;
  const mid = Math.ceil(components.length / 2);
  return components.slice(mid);
});

// 三栏布局分配
const firstColumnComponents = computed(() => {
  const components = filteredComponents.value;
  const part = Math.ceil(components.length / 3);
  return components.slice(0, part);
});

const secondColumnComponents = computed(() => {
  const components = filteredComponents.value;
  const part = Math.ceil(components.length / 3);
  return components.slice(part, part * 2);
});

const thirdColumnComponents = computed(() => {
  const components = filteredComponents.value;
  const part = Math.ceil(components.length / 3);
  return components.slice(part * 2);
});

// 从组件库获取组件信息 - 修正：根据 componentId 查找
const getComponentFromLibrary = (componentId: number) => {
  return COMPONENT_LIBRARY.find(comp => comp.id === componentId);
};

// 获取组件配置 - 修正：处理 TemplateComponentForm 结构
const getComponentConfig = (component: any) => {
  if (!component || !component.componentId) return {
    id: '',
    name: '未选择组件',
    key: '',
    props: {},
    styles: {}
  };

  // 从组件库获取组件信息
  const libraryComponent = getComponentFromLibrary(component.componentId);

  if (!libraryComponent) {
    return {
      id: component.componentId,
      name: '未知组件',
      key: '',
      props: component.props || {},
      styles: component.styles || {}
    };
  }

  // 合并默认配置和组件属性
  const defaultProps = libraryComponent.defaultConfig || {};
  const componentProps = component.props || {};

  return {
    id: component.componentId,
    name: libraryComponent.name,
    key: libraryComponent.key,
    props: { ...defaultProps, ...componentProps },
    styles: component.styles || {}
  };
};

// 获取组件样式
const getComponentStyle = (component: any) => {
  const style: any = {};
  const globalStyle = props.globalStyle;
  const componentStyles = component.styles || {};

  // 首先应用组件的自定义样式
  if (componentStyles) {
    Object.assign(style, componentStyles);
  }

  // 设置内边距
  if (globalStyle?.spacing?.padding) {
    style.padding = globalStyle.spacing.padding;
  }

  // 设置字体大小
  if (globalStyle?.fontSizes?.body) {
    style.fontSize = `${globalStyle.fontSizes.body}px`;
  }

  // 设置边框颜色
  if (globalStyle?.primaryColor) {
    style.borderLeftColor = globalStyle.primaryColor;
    style.borderLeftWidth = '4rpx';
    style.borderLeftStyle = 'solid';
  }

  // 设置背景颜色
  if (globalStyle?.secondaryColor) {
    style.backgroundColor = `${globalStyle.secondaryColor}20`; // 添加透明度
  }

  return style;
};

// 获取组件包装器样式
const getComponentWrapperStyle = (index: number, column?: string) => {
  const style: any = {};
  const globalStyle = props.globalStyle;

  // 设置区块间距
  if (globalStyle?.spacing?.sectionMargin) {
    const margin = globalStyle.spacing.sectionMargin;
    if (index > 0) {
      style.marginTop = margin;
    }

    // 为多栏布局添加水平间距
    if (column) {
      style.marginRight = '10rpx';
      style.marginLeft = '10rpx';
    }
  }

  // 设置边框和背景
  style.borderRadius = '8rpx';
  style.overflow = 'hidden';
  style.boxShadow = '0 2rpx 8rpx rgba(0, 0, 0, 0.1)';

  return style;
};
</script>

<style scoped lang="scss">
.template-preview {
  // 定义CSS变量
  --primary-color: #d4af37;
  --accent-color: #f7ef8a;
  --secondary-color: #f9f3e3;

  min-height: 400rpx;
  transition: all 0.3s ease;

  &.mobile {
    max-width: 375px;
    margin: 0 auto;
    font-size: 14px;
    padding: 15rpx;
    border-radius: 16rpx;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
  }

  &.tablet {
    max-width: 768px;
    margin: 0 auto;
    padding: 25rpx;
    border-radius: 12rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
  }

  &.desktop {
    max-width: 100%;
    padding: 30rpx;
    border-radius: 8rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
  }
}

.layout-single-column {
  display: flex;
  flex-direction: column;
}

.layout-two-column {
  display: flex;
  gap: 20rpx;

  .column-left,
  .column-right {
    display: flex;
    flex-direction: column;
    gap: 20rpx;
    min-height: 200rpx;

    .empty-column {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100%;
      background: rgba(0, 0, 0, 0.02);
      border: 2rpx dashed #ddd;
      border-radius: 8rpx;
      padding: 40rpx 20rpx;

      .empty-column-text {
        font-size: 24rpx;
        color: #999;
      }
    }
  }
}

.layout-three-column {
  display: flex;
  gap: 15rpx;

  .column {
    display: flex;
    flex-direction: column;
    gap: 20rpx;
    flex: 1;
    min-height: 200rpx;

    .empty-column {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100%;
      background: rgba(0, 0, 0, 0.02);
      border: 2rpx dashed #ddd;
      border-radius: 8rpx;
      padding: 40rpx 20rpx;

      .empty-column-text {
        font-size: 22rpx;
        color: #999;
      }
    }
  }
}

.preview-component-wrapper {
  transition: all 0.3s ease;
  background: white;

  &:hover {
    transform: translateY(-2rpx);
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);
  }
}

.empty-preview {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300rpx;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 8rpx;
  border: 2rpx dashed #ddd;

  .empty-content {
    text-align: center;

    .empty-icon {
      font-size: 60rpx;
      display: block;
      margin-bottom: 20rpx;
      opacity: 0.5;
    }

    .empty-title {
      display: block;
      font-size: 28rpx;
      font-weight: 500;
      color: #666;
      margin-bottom: 10rpx;
    }

    .empty-desc {
      display: block;
      font-size: 24rpx;
      color: #999;
    }
  }
}

.color-preview-bar {
  display: flex;
  margin-bottom: 20rpx;
  border-radius: 8rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);

  .color-block {
    flex: 1;
    height: 40rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20rpx;
    color: white;
    text-shadow: 0 1rpx 2rpx rgba(0, 0, 0, 0.3);
    transition: all 0.3s ease;

    &.primary {
      background-color: var(--primary-color);
    }

    &.secondary {
      background-color: var(--secondary-color);
      color: #333;
    }

    &.accent {
      background-color: var(--accent-color);
      color: #333;
    }

    &:hover {
      filter: brightness(1.1);
    }
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .layout-two-column,
  .layout-three-column {
    flex-direction: column;

    .column-left,
    .column-right,
    .column {
      width: 100% !important;
    }
  }

  .empty-preview {
    height: 200rpx;

    .empty-content {
      .empty-icon {
        font-size: 48rpx;
      }

      .empty-title {
        font-size: 24rpx;
      }

      .empty-desc {
        font-size: 20rpx;
      }
    }
  }

  .color-preview-bar {
    .color-block {
      height: 32rpx;
      font-size: 16rpx;
    }
  }
}
</style>