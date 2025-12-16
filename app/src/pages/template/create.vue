<template>
  <view class="template-editor">
    <!-- 头部 -->
    <view class="editor-header">
      <view class="header-title">
        <h2 class="title">{{ isEditMode ? "编辑模板" : "创建模板" }}</h2>
        <text class="subtitle">{{ isEditMode ? "修改模板配置" : "创建新的模板" }}</text>
      </view>
    </view>

    <!-- 主体内容 -->
    <view class="editor-container">
      <!-- 左侧配置面板 -->
      <view class="config-panel">
        <!-- 配置标签页 -->
        <view class="config-tabs">
          <view
            v-for="tab in configTabs"
            :key="tab.id"
            :class="['tab-item', { active: activeTab === tab.id }]"
            @click="activeTab = tab.id"
          >
            <text class="tab-text">{{ tab.label }}</text>
            <view v-if="activeTab === tab.id" class="tab-indicator"></view>
          </view>
        </view>
        <!-- 配置内容区域 -->
        <scroll-view
          class="config-content"
          scroll-y="true"
          :scroll-top="scrollTop"
          @scroll="onScroll"
        >
          <!-- 基础配置 -->
          <view v-if="activeTab === 'basic'" class="config-section">
            <view class="component-header">
              <text class="component-title">基础信息</text>
              <text class="component-desc">设置模板的基本属性和分类</text>
            </view>

            <view class="form-group">
              <text class="form-label required">模板编码</text>
              <input
                v-model="template.code"
                :disabled="isEditMode"
                placeholder="英文、数字、下划线，如: resume_tech"
                :class="['form-input', { disabled: isEditMode }]"
                @focus="onInputFocus"
                @blur="onInputBlur"
              />
              <text class="form-tip" v-if="isEditMode">模板编码创建后不可修改</text>
            </view>

            <view class="form-group">
              <text class="form-label required">模板名称</text>
              <input
                v-model="template.name"
                placeholder="如: 科技风格简历"
                class="form-input"
                @focus="onInputFocus"
                @blur="onInputBlur"
              />
            </view>

            <view class="form-group">
              <text class="form-label">描述</text>
              <textarea
                v-model="template.description"
                placeholder="请输入模板描述..."
                class="form-textarea"
                maxlength="200"
                @focus="onTextareaFocus"
                @blur="onTextareaBlur"
              />
              <view class="textarea-footer flex-between">
                <text class="textarea-tip">简要描述模板用途和特点</text>
                <text class="textarea-count">{{ template.description?.length || 0 }}/200</text>
              </view>
            </view>
          </view>

          <!-- 布局配置 -->
          <view v-if="activeTab === 'layout'" class="config-section">
            <view class="component-header">
              <text class="component-title">布局设置</text>
              <text class="component-desc">调整页面结构和区块排列</text>
            </view>

            <view class="form-group">
              <text class="form-label">布局类型</text>
              <view class="layout-types">
                <view
                  v-for="type in LAYOUT_TYPES"
                  :key="type.value"
                  :class="['layout-type-item', { active: template.globalLayout.type === type.value }]"
                  @click="template.globalLayout.type = type.value"
                >
                  <view :class="['layout-icon', type.icon]"></view>
                  <text class="layout-name">{{ type.label }}</text>
                </view>
              </view>
            </view>

            <!-- 双栏布局配置 -->
            <view v-if="template.globalLayout.type === 'two-column'" class="layout-config">
              <view class="layout-preview-container">
                <text class="preview-title">布局预览</text>
                <view class="two-column-preview">
                  <view
                    class="column-left"
                    :style="{ width: getColumnWidth('left') }"
                  >
                    <text class="column-label">左侧</text>
                  </view>
                  <view
                    class="column-right"
                    :style="{ width: getColumnWidth('right') }"
                  >
                    <text class="column-label">右侧</text>
                  </view>
                </view>
              </view>

              <view class="width-controls">
                <view class="control-group">
                  <text class="control-label">左侧宽度: {{ template.globalLayout.columns.left }}%</text>
                  <slider
                    :value="template.globalLayout.columns.left"
                    min="20"
                    max="80"
                    step="5"
                    @changing="onColumnWidthChanging"
                    data-side="left"
                    :active-color="primaryColor"
                    :background-color="borderColorLighter"
                    class="form-slider"
                  />
                </view>
                <view class="control-group">
                  <text class="control-label">右侧宽度: {{ template.globalLayout.columns.right }}%</text>
                  <slider
                    :value="template.globalLayout.columns.right"
                    min="20"
                    max="80"
                    step="5"
                    @changing="onColumnWidthChanging"
                    data-side="right"
                    :active-color="primaryColor"
                    :background-color="borderColorLighter"
                    class="form-slider"
                  />
                </view>
              </view>
            </view>

            <!-- 区块管理 -->
            <view class="component-management">
              <view class="component-header">
                <text class="component-title">区块管理</text>
                <text class="component-desc">选择区块后使用上下按钮调整顺序</text>
              </view>

              <view class="component-list">
                <view
                  v-for="(component, index) in template.components"
                  :key="component.id || index"
                  :class="['component-item', { 'selected': selectedIndex === index }]"
                  @click="selectComponent(index)"
                >
                  <view class="component-item-main">
                    <view class="component-index">
                      <text class="index-text">{{ index + 1 }}</text>
                    </view>
                    <view class="component-info">
                      <input
                        v-model="component.name"
                        placeholder="区块名称"
                        class="component-input"
                        @focus="onSectionInputFocus(index)"
                      />
                      <picker
                        :value="getComponentIndex(component.component)"
                        :range="componentOptionLabels"
                        @change="(e) => onComponentChange(index, e)"
                        class="component-picker"
                      >
                        <view class="picker-display flex-between">
                          <text>{{ getComponentName(component.component) || "选择片段" }}</text>
                          <text class="picker-arrow">▼</text>
                        </view>
                      </picker>
                    </view>
                    <view class="component-actions">
                      <button
                        @click.stop="moveComponentUp(index)"
                        :class="['action-btn move-up-btn', { disabled: index === 0 }]"
                        :disabled="index === 0"
                      >
                        <text class="action-icon">↑</text>
                      </button>
                      <button
                        @click.stop="moveComponentDown(index)"
                        :class="['action-btn move-down-btn', { disabled: index === template.components.length - 1 }]"
                        :disabled="index === template.components.length - 1"
                      >
                        <text class="action-icon">↓</text>
                      </button>
                      <button
                        @click.stop="removeSection(index)"
                        class="action-btn remove-btn"
                      >
                        <text class="action-icon">×</text>
                      </button>
                    </view>
                  </view>
                </view>
              </view>

              <view class="sort-controls" v-if="selectedIndex !== -1 && template.components.length > 0">
                <text class="sort-hint">当前选中: {{ template.components[selectedIndex]?.name }}</text>
                <view class="sort-buttons">
                  <button
                    @click="moveSelectedToTop"
                    :class="['sort-btn', { disabled: selectedIndex === 0 }]"
                    :disabled="selectedIndex === 0"
                  >
                    <text class="sort-icon">⏫</text>
                    移到顶部
                  </button>
                  <button
                    @click="moveSelectedToBottom"
                    :class="['sort-btn', { disabled: selectedIndex === template.components.length - 1 }]"
                    :disabled="selectedIndex === template.components.length - 1"
                  >
                    <text class="sort-icon">⏬</text>
                    移到底部
                  </button>
                </view>
              </view>

              <button @click="addSection" class="add-component-btn">
                <text class="add-icon">+</text>
                添加区块
              </button>
            </view>
          </view>

          <!-- 样式配置 -->
          <view v-if="activeTab === 'style'" class="config-section">
            <view class="component-header">
              <text class="component-title">样式设置</text>
              <text class="component-desc">自定义颜色、字体和间距</text>
            </view>

            <!-- 颜色配置 -->
            <view class="color-config">
              <view class="config-header">
                <text class="config-title">主题颜色</text>
                <button @click="resetColors" class="reset-btn">重置</button>
              </view>

              <view class="color-group">
                <view class="color-item">
                  <view class="color-info">
                    <text class="color-label">主色调</text>
                    <text class="color-desc">用于标题和重要元素</text>
                  </view>
                  <view class="color-controls">
                    <!-- 修复：使用正确的 v-model 和事件处理 -->
                    <input
                      type="text"
                      :value="template.globalStyle.primaryColor"
                      @input="onColorInput($event, 'primaryColor')"
                      class="color-input"
                      placeholder="#d4af37"
                    />
                    <view
                      class="color-preview"
                      :style="{ backgroundColor: template.globalStyle.primaryColor }"
                      @click="showColorPicker('primaryColor')"
                    />
                  </view>
                </view>

                <view class="color-item">
                  <view class="color-info">
                    <text class="color-label">辅色调</text>
                    <text class="color-desc">用于背景和次要元素</text>
                  </view>
                  <view class="color-controls">
                    <input
                      type="text"
                      :value="template.globalStyle.secondaryColor"
                      @input="onColorInput($event, 'secondaryColor')"
                      class="color-input"
                      placeholder="#f9f3e3"
                    />
                    <view
                      class="color-preview"
                      :style="{ backgroundColor: template.globalStyle.secondaryColor }"
                      @click="showColorPicker('secondaryColor')"
                    />
                  </view>
                </view>

                <view class="color-item">
                  <view class="color-info">
                    <text class="color-label">强调色</text>
                    <text class="color-desc">用于按钮和交互元素</text>
                  </view>
                  <view class="color-controls">
                    <input
                      type="text"
                      :value="template.globalStyle.accentColor"
                      @input="onColorInput($event, 'accentColor')"
                      class="color-input"
                      placeholder="#f7ef8a"
                    />
                    <view
                      class="color-preview"
                      :style="{ backgroundColor: template.globalStyle.accentColor }"
                      @click="showColorPicker('accentColor')"
                    />
                  </view>
                </view>
              </view>

              <!-- 预定义颜色选择 -->
              <view class="color-presets" v-if="showColorPresets">
                <view class="presets-title">常用颜色</view>
                <view class="presets-grid">
                  <view
                    v-for="color in colorPresets"
                    :key="color"
                    class="preset-color"
                    :style="{ backgroundColor: color }"
                    @click="setColor(currentColorField, color)"
                  />
                </view>
              </view>
            </view>

            <!-- 字体配置 -->
            <view class="font-config">
              <view class="config-header">
                <text class="config-title">字体设置</text>
              </view>

              <view class="form-group">
                <text class="form-label">字体家族</text>
                <input
                  v-model="template.globalStyle.fontFamily"
                  placeholder="如: 'Microsoft YaHei', sans-serif"
                  class="form-input"
                />
              </view>

              <view class="font-sizes">
                <view class="size-item">
                  <text class="size-label">标题文字</text>
                  <view class="size-controls">
                    <input
                      type="number"
                      v-model="template.globalStyle.fontSizes.h1"
                      class="size-input"
                      min="12"
                      max="72"
                    />
                    <text class="size-unit">px</text>
                    <view class="size-slider">
                      <slider
                        :value="template.globalStyle.fontSizes.h1"
                        min="12"
                        max="72"
                        step="2"
                        @changing="onFontSizeChanging"
                        data-type="h1"
                        :active-color="primaryColor"
                        class="size-slider-bar"
                      />
                    </view>
                  </view>
                </view>

                <view class="size-item">
                  <text class="size-label">正文字体</text>
                  <view class="size-controls">
                    <input
                      type="number"
                      v-model="template.globalStyle.fontSizes.body"
                      class="size-input"
                      min="10"
                      max="36"
                    />
                    <text class="size-unit">px</text>
                    <view class="size-slider">
                      <slider
                        :value="template.globalStyle.fontSizes.body"
                        min="10"
                        max="36"
                        step="1"
                        @changing="onFontSizeChanging"
                        data-type="body"
                        :active-color="primaryColor"
                        class="size-slider-bar"
                      />
                    </view>
                  </view>
                </view>
              </view>
            </view>

            <!-- 间距配置 -->
            <view class="spacing-config">
              <view class="config-header">
                <text class="config-title">间距设置</text>
              </view>

              <view class="spacing-items">
                <view class="spacing-item">
                  <view class="spacing-info">
                    <text class="spacing-label">区块间距</text>
                    <text class="spacing-desc">控制区块之间的垂直距离</text>
                  </view>
                  <input
                    type="text"
                    v-model="template.globalStyle.spacing.sectionMargin"
                    placeholder="如: 20px"
                    class="spacing-input"
                  />
                </view>

                <view class="spacing-item">
                  <view class="spacing-info">
                    <text class="spacing-label">内容内边距</text>
                    <text class="spacing-desc">控制内容区域的留白</text>
                  </view>
                  <input
                    type="text"
                    v-model="template.globalStyle.spacing.padding"
                    placeholder="如: 15px"
                    class="spacing-input"
                  />
                </view>

                <view class="spacing-item">
                  <view class="spacing-info">
                    <text class="spacing-label">行高</text>
                    <text class="spacing-desc">控制文字行间距</text>
                  </view>
                  <input
                    type="text"
                    v-model="template.globalStyle.spacing.lineHeight"
                    placeholder="如: 1.5"
                    class="spacing-input"
                  />
                </view>
              </view>
            </view>
          </view>
        </scroll-view>
      </view>

      <!-- 右侧预览面板 -->
      <view class="preview-panel">
        <view class="preview-header flex-between">
          <view class="preview-title-container">
            <text class="preview-title">实时预览</text>
            <text class="preview-subtitle">所见即所得</text>
          </view>

          <view class="preview-controls">
            <view class="device-switch">
              <text class="switch-label">设备:</text>
              <view class="switch-buttons">
                <button
                  v-for="device in deviceOptions"
                  :key="device.value"
                  :class="['device-btn', { active: previewDevice === device.value }]"
                  @click="previewDevice = device.value"
                >
                  <text class="device-icon">{{ device.icon }}</text>
                  <text class="device-name">{{ device.name }}</text>
                </button>
              </view>
            </view>

            <button @click="refreshPreview" class="refresh-btn">
              <text class="refresh-icon">🔄</text>
              刷新预览
            </button>
          </view>
        </view>

        <view class="preview-content" :class="previewDevice">
          <!-- 实时预览组件 -->
          <template-preview
            :key="previewKey"
            :components="template.components"
            :layout="template.globalLayout"
            :global-style="template.globalStyle"
            :device="previewDevice"
            class="template-preview-container"
          />

          <view class="preview-info">
            <text class="info-title">模板信息</text>
            <view class="info-items">
              <view class="info-item">
                <text class="info-label">名称:</text>
                <text class="info-value">{{ template.name || "未命名" }}</text>
              </view>
              <view class="info-item">
                <text class="info-label">布局:</text>
                <text class="info-value">{{ layoutTypeLabel }}</text>
              </view>
              <view class="info-item">
                <text class="info-label">区块数:</text>
                <text class="info-value">{{ template.components.length }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view class="editor-footer flex-between">
      <view class="footer-left">
        <text class="footer-status">
          {{ isEditMode ? "正在编辑模板" : "正在创建新模板" }}
        </text>
      </view>
      <view class="footer-right">
        <button @click="cancel" class="footer-btn secondary">取消</button>
        <button @click="saveAsDraft" class="footer-btn">保存草稿</button>
        <button @click="saveTemplate" class="footer-btn primary">保存模板</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from "vue";
import { COMPONENT_LIBRARY, LAYOUT_TYPES } from "@/constants/resumes-components";
import TemplatePreview from "@/components/template/TemplatePreview.vue";
import { ConfigTab, TemplateResult } from "@/types/template";
import { TemplateComponentResult } from "@/types/template-component";

// 样式变量导入
const primaryColor = "#d4af37";
const secondaryColor = "#f7ef8a";
const successColor = "#67c23a";
const dangerColor = "#f56c6c";
const warningColor = "#e6a23c";
const infoColor = "#909399";
const borderColorLighter = "#ebeef5";
const textPrimary = "#303133";
const textSecondary = "#909399";
const backgroundColor = "#f8f8f8";
const backgroundColorWhite = "#ffffff";

// 响应式数据
const isEditMode = ref(false);
const activeTab = ref("basic");
const previewDevice = ref("desktop");
const previewKey = ref(0);
const categoryIndex = ref(0);
const styleIndex = ref(0);
const layoutTypeIndex = ref(0);
const scrollTop = ref(0);
const draggingSection = ref<number | null>(null);
const showColorPresets = ref(false);
const currentColorField = ref("primaryColor");
const colorPresets = ref([
  "#d4af37", // 主色调
  "#f9f3e3", // 辅色调
  "#f7ef8a", // 强调色
  "#67c23a", // 成功色
  "#e6a23c", // 警告色
  "#f56c6c", // 危险色
  "#909399", // 信息色
  "#409eff", // 蓝色
  "#303133", // 主要文字色
  "#606266", // 常规文字色
  "#909399", // 次要文字色
  "#c0c4cc", // 占位文字色
  "#dcdfe6", // 边框色
  "#e4e7ed", // 边框色2
  "#ebeef5", // 边框色3
  "#f2f6fc",  // 边框色4
]);

// 选择排序相关
const selectedIndex = ref(-1);

// 拖拽相关数据
const dragStartY = ref(0);
const dragOffsetY = ref(0);

// 配置数据
const template = reactive<TemplateResult>({
  id: 0,
  code: "",
  name: "",
  description: "",
  previewImage: "",
  isActive: true,
  version: "1.0.0",
  globalLayout: {
    type: "single-column",
    columns: {
      left: 40,
      right: 60,
    },
    components: [],
    orientation: "portrait",
    componentOrder: [],
  },
  globalStyle: {
    theme: "light",
    fontSizes: {
      h1: "24",
      body: "14",
    },
    fontFamily: "'Microsoft YaHei', 'PingFang SC', sans-serif",
    headerColor: "",
    primaryColor: "#d4af37",
    accentColor: "#f7ef8a",
    secondaryColor: "#f9f3e3",
    backgroundColor: "",
    spacing: {
      sectionMargin: "20px",
      padding: "15px",
      lineHeight: "1.5",
    },
  },
  components: [],
  price: 0,
  users: 0,
  tags: [],
  category: "",
  rating: 0,
});

// 选项数据
const configTabs: ConfigTab[] = [
  { id: "basic", label: "基础配置", icon: "⚙️" },
  { id: "layout", label: "布局配置", icon: "📐" },
  { id: "style", label: "样式配置", icon: "🎨" },
];

const deviceOptions: DeviceOption[] = [
  { value: "desktop", name: "桌面", icon: "🖥️" },
  { value: "tablet", name: "平板", icon: "📱" },
  { value: "mobile", name: "手机", icon: "📲" },
];

const availableComponents = ref(COMPONENT_LIBRARY);

// 计算属性
const componentOptionLabels = computed(() => availableComponents.value.map(f => f.name));
const layoutTypeLabel = computed(() => {
  const type = LAYOUT_TYPES.find(t => t.value === template.globalLayout.type);
  return type ? type.label : "单栏";
});

// 方法
const refreshPreview = () => {
  previewKey.value += 1;
  uni.showToast({
    title: "预览已刷新",
    icon: "success",
  });
};

const saveAsDraft = () => {
  if (!validateRequiredFields()) return;

  uni.showModal({
    title: "保存草稿",
    content: "确定要将模板保存为草稿吗？",
    confirmText: "保存",
    confirmColor: primaryColor,
    success: (res) => {
      if (res.confirm) {
        uni.showLoading({ title: "保存中..." });
        setTimeout(() => {
          uni.hideLoading();
          uni.showToast({
            title: "已保存为草稿",
            icon: "success",
            duration: 2000,
          });
        }, 1500);
      }
    },
  });
};

const saveTemplate = () => {
  if (!validateRequiredFields()) return;

  uni.showModal({
    title: "保存模板",
    content: "确定要保存模板吗？模板将发布到模板库",
    confirmText: "发布",
    confirmColor: primaryColor,
    success: (res) => {
      if (res.confirm) {
        uni.showLoading({ title: "发布中..." });
        setTimeout(() => {
          uni.hideLoading();
          uni.showToast({
            title: "模板发布成功",
            icon: "success",
            duration: 2000,
          });
          setTimeout(() => {
            uni.navigateBack();
          }, 2000);
        }, 2000);
      }
    },
  });
};

const cancel = () => {
  uni.showModal({
    title: "确认离开",
    content: "离开后未保存的更改将会丢失",
    confirmText: "离开",
    confirmColor: dangerColor,
    success: (res) => {
      if (res.confirm) {
        uni.navigateBack();
      }
    },
  });
};

const validateRequiredFields = (): boolean => {
  if (!template.code) {
    uni.showToast({
      title: "请填写模板编码",
      icon: "none",
    });
    return false;
  }

  if (!template.name) {
    uni.showToast({
      title: "请填写模板名称",
      icon: "none",
    });
    return false;
  }

  return true;
};

const getColumnWidth = (side: "left" | "right") => {
  return `${template.globalLayout.columns[side]}%`;
};

const onColumnWidthChanging = (e: any) => {
  const value = e.detail.value;
  const side = e.currentTarget.dataset.side;
  if (side === "left") {
    template.globalLayout.columns.left = value;
    template.globalLayout.columns.right = 100 - value;
  } else {
    template.globalLayout.columns.right = value;
    template.globalLayout.columns.left = 100 - value;
  }
  previewKey.value += 1;
};

// 选择组件
const selectComponent = (index: number) => {
  selectedIndex.value = index;
  console.log("选择组件:", index, template.components[index]?.name);
};

// 上移组件
const moveComponentUp = (index: number) => {
  if (index <= 0) return;

  const components = [...template.components];
  const temp = components[index];
  components[index] = components[index - 1];
  components[index - 1] = temp;

  template.components = components;
  selectedIndex.value = index - 1;
  previewKey.value += 1;

  uni.showToast({
    title: "已上移",
    icon: "success",
    duration: 800,
  });
};

// 下移组件
const moveComponentDown = (index: number) => {
  if (index >= template.components.length - 1) return;

  const components = [...template.components];
  const temp = components[index];
  components[index] = components[index + 1];
  components[index + 1] = temp;

  template.components = components;
  selectedIndex.value = index + 1;
  previewKey.value += 1;

  uni.showToast({
    title: "已下移",
    icon: "success",
    duration: 800,
  });
};

// 移到顶部
const moveSelectedToTop = () => {
  if (selectedIndex.value <= 0) return;

  const components = [...template.components];
  const selected = components[selectedIndex.value];
  components.splice(selectedIndex.value, 1);
  components.unshift(selected);

  template.components = components;
  selectedIndex.value = 0;
  previewKey.value += 1;

  uni.showToast({
    title: "已移到顶部",
    icon: "success",
    duration: 1000,
  });
};

// 移到底部
const moveSelectedToBottom = () => {
  if (selectedIndex.value >= template.components.length - 1) return;

  const components = [...template.components];
  const selected = components[selectedIndex.value];
  components.splice(selectedIndex.value, 1);
  components.push(selected);

  template.components = components;
  selectedIndex.value = components.length - 1;
  previewKey.value += 1;

  uni.showToast({
    title: "已移到底部",
    icon: "success",
    duration: 1000,
  });
};

// 添加区块
const addSection = () => {
  const newComponent: TemplateComponentResult = {
    name: `区块${template.components.length + 1}`,
    component: "",
    props: "",
    styles: "",
    id: Date.now(),
  };
  template.components.push(newComponent);
  selectedIndex.value = template.components.length - 1;
  previewKey.value += 1;

  uni.showToast({
    title: "已添加区块",
    icon: "success",
    duration: 800,
  });
};

// 删除区块
const removeSection = (index: number) => {
  uni.showModal({
    title: "删除区块",
    content: "确定要删除这个区块吗？",
    confirmColor: dangerColor,
    success: (res) => {
      if (res.confirm) {
        template.components.splice(index, 1);

        // 调整选中索引
        if (selectedIndex.value === index) {
          selectedIndex.value = -1;
        } else if (selectedIndex.value > index) {
          selectedIndex.value -= 1;
        }

        previewKey.value += 1;
        uni.showToast({
          title: "删除成功",
          icon: "success",
        });
      }
    },
  });
};

// 片段选择相关方法
const getComponentIndex = (componentCode: string) => {
  if (!componentCode) return -1;
  return availableComponents.value.findIndex(f => f.code === componentCode);
};

const getComponentName = (componentCode: string) => {
  if (!componentCode) return "";
  const component = availableComponents.value.find(f => f.code === componentCode);
  return component ? component.name : "";
};

const onComponentChange = (sectionIndex: number, e: any) => {
  const fragmentIndex = parseInt(e.detail.value);
  if (fragmentIndex >= 0 && fragmentIndex < availableComponents.value.length) {
    const selectedComponent = availableComponents.value[fragmentIndex];
    template.components[sectionIndex].component = selectedComponent.code;
    template.components[sectionIndex].component = selectedComponent.code;
    template.components[sectionIndex].name = template.components[sectionIndex].name || selectedComponent.name;
    previewKey.value += 1;
  }
};

const onScroll = (e: any) => {
  scrollTop.value = e.detail.scrollTop;
};

const onInputFocus = () => {
  // 输入框聚焦处理
};

const onInputBlur = () => {
  // 输入框失焦处理
};

const onTextareaFocus = () => {
  // 文本域聚焦处理
};

const onTextareaBlur = () => {
  // 文本域失焦处理
};


const onFontSizeChanging = (e: any) => {
  const value = e.detail.value;
  const type = e.currentTarget.dataset.type;
  if (type === "h1") {
    template.globalStyle.fontSizes.h1 = value.toString();
  } else {
    template.globalStyle.fontSizes.body = value.toString();
  }
  previewKey.value += 1;
};

// 修复：颜色输入处理
const onColorInput = (event: any, field: string) => {
  const value = event.detail?.value || event.target?.value;
  if (value) {
    // 格式化颜色值
    let formattedValue = value.trim();

    // 如果以#开头但长度不正确
    if (formattedValue.startsWith("#") && formattedValue.length !== 4 && formattedValue.length !== 7) {
      // 尝试修复常见的3位或6位十六进制颜色
      if (formattedValue.length === 4) {
        // 格式 #rgb 转换为 #rrggbb
        formattedValue = "#" + formattedValue[1] + formattedValue[1] +
          formattedValue[2] + formattedValue[2] +
          formattedValue[3] + formattedValue[3];
      }
    }

    // 如果没有#开头，添加#
    if (!formattedValue.startsWith("#")) {
      // 检查是否是有效的十六进制
      const hexRegex = /^[0-9A-Fa-f]{3}$|^[0-9A-Fa-f]{6}$/;
      if (hexRegex.test(formattedValue)) {
        formattedValue = "#" + formattedValue;
      }
    }

    // 验证颜色
    if (validateColorHex(formattedValue)) {
      template.globalStyle[field] = formattedValue;
      previewKey.value += 1;
    } else {
      // 显示错误提示
      uni.showToast({
        title: `颜色格式无效: ${formattedValue}`,
        icon: "none",
        duration: 2000,
      });
    }
  }
};

// 修复：验证十六进制颜色
const validateColorHex = (color: string): boolean => {
  const hexRegex = /^#([0-9A-Fa-f]{3}){1,2}$/i;
  return hexRegex.test(color);
};

// 修复：显示颜色选择器
const showColorPicker = (field: string) => {
  currentColorField.value = field;

  // 在 uni-app 中，可以使用 uni.chooseColor 调起原生颜色选择器
  if (uni.chooseColor) {
    uni.chooseColor({
      color: template.globalStyle[field],
      success: (res) => {
        template.globalStyle[field] = res.color;
        previewKey.value += 1;
      },
      fail: (err) => {
        console.log("颜色选择失败:", err);
        // 如果原生选择器失败，显示自定义预设
        showColorPresets.value = !showColorPresets.value;
      },
    });
  } else {
    // 如果没有原生选择器，显示自定义预设
    showColorPresets.value = !showColorPresets.value;
  }
};

// 修复：设置颜色
const setColor = (field: string, color: string) => {
  template.globalStyle[field] = color;
  previewKey.value += 1;
  showColorPresets.value = false;

  uni.showToast({
    title: `${field} 已设置为 ${color}`,
    icon: "success",
    duration: 1000,
  });
};

// 修复：重置颜色
const resetColors = () => {
  template.globalStyle.primaryColor = "#d4af37";
  template.globalStyle.secondaryColor = "#f9f3e3";
  template.globalStyle.accentColor = "#f7ef8a";
  previewKey.value += 1;
  showColorPresets.value = false;

  uni.showToast({
    title: "颜色已重置为默认值",
    icon: "success",
  });
};


const onSectionInputFocus = (index: number) => {
  // 区块输入框聚焦处理
  selectedIndex.value = index;
};

// 监听模板变化，自动更新预览
watch(
  () => [
    template.name,
    template.code,
    template.description,
    template.globalLayout.type,
    template.globalStyle.primaryColor,
    template.globalStyle.secondaryColor,
    template.globalStyle.accentColor,
    template.globalStyle.fontFamily,
    template.globalStyle.fontSizes.h1,
    template.globalStyle.fontSizes.body,
    template.globalStyle.spacing.sectionMargin,
    template.globalStyle.spacing.padding,
    template.globalStyle.spacing.lineHeight,
  ],
  () => {
    setTimeout(() => {
      previewKey.value += 1;
    }, 100);
  },
  { deep: false },
);

// 监听组件数组变化
watch(
  () => template.components,
  () => {
    previewKey.value += 1;
  },
  { deep: true },
);

// 生命周期
onMounted(() => {
  // 初始化逻辑
  const pages = getCurrentPages();
  const currentPage = pages[pages.length - 1];
  const options = currentPage.options;

  if (options.id) {
    isEditMode.value = true;
    // 加载现有模板数据
    loadTemplateData(options.id);
  } else {
    // 初始化一个示例组件
    initExampleComponents();
  }
});

const initExampleComponents = () => {
  // 添加示例组件
  template.components = [
    {
      id: Date.now() + 1,
      name: "个人信息",
      component: "UserBasicInfo",
      props: "",
      styles: "",
    },
    {
      id: Date.now() + 2,
      name: "工作经历",
      component: "WorkExperience",
      props: "",
      styles: "",
    },
    {
      id: Date.now() + 3,
      name: "教育背景",
      component: "EducationExperience",
      props: "",
      styles: "",
    },
  ];
  previewKey.value += 1;
};

const loadTemplateData = (id: string) => {
  console.log("加载模板数据:", id);
  // 模拟加载数据
  setTimeout(() => {
    template.code = "resume_tech";
    template.name = "科技风格简历";
    template.category = "resume";
    template.description = "专业的科技行业简历模板";

    // 重置并添加示例组件
    template.components = [];

    exampleComponents.forEach(comp => {
      template.components.push(comp);
    });

    previewKey.value += 1;
  }, 500);
};
</script>

<style scoped lang="scss">

.template-editor {
  min-height: 100vh;
  background-color: $background-color;
  display: flex;
  flex-direction: column;
}

/* 头部样式 */
.editor-header {
  background: linear-gradient(135deg, $primary-color 0%, color.adjust($primary-color, $lightness: -10%) 100%);
  color: white;
  padding: $padding-base;
  border-radius: 0 0 $border-radius-large $border-radius-large;
  box-shadow: $box-shadow-dark;
  z-index: 10;

  .header-title {
    margin-bottom: $margin-small;

    .title {
      font-size: $font-size-extra-large;
      font-weight: $font-weight-bold;
      margin: 0 0 8rpx 0;
      color: white;
    }

    .subtitle {
      font-size: $font-size-small;
      opacity: 0.9;
      color: rgba(white, 0.9);
    }
  }

  .header-actions {
    display: flex;
    gap: $margin-small;
    flex-wrap: wrap;

    .action-btn {
      flex: 1;
      min-width: 0;
      background: rgba(white, 0.15);
      border: 2rpx solid rgba(white, 0.3);
      color: white;
      border-radius: $border-radius;
      padding: 16rpx 24rpx;
      font-size: $font-size-base;
      font-weight: $font-weight-medium;
      transition: all $transition-fast $ease-in-out;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8rpx;

      &.preview-btn {
        background: rgba(white, 0.2);

        &:active {
          background: rgba(white, 0.3);
        }
      }

      &.draft-btn {
        background: $warning-color;
        border-color: $warning-color;
      }

      &.save-btn {
        background: $success-color;
        border-color: $success-color;
      }

      &.cancel-btn {
        background: $danger-color;
        border-color: $danger-color;
      }

      &:disabled {
        opacity: $button-disabled-opacity;
        filter: grayscale(0.5);
      }
    }
  }
}

/* 主体容器 */
.editor-container {
  flex: 1;
  display: flex;
  padding: $padding-base;
  gap: $margin-base;
  min-height: 0;
}

/* 左侧配置面板 */
.config-panel {
  flex: 0 0 45%;
  background: $background-color-white;
  border-radius: $border-radius-large;
  box-shadow: $box-shadow;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  .config-tabs {
    display: flex;
    background: $background-color;
    padding: 8rpx;
    border-bottom: 2rpx solid $border-color-lighter;

    .tab-item {
      flex: 1;
      text-align: center;
      padding: 20rpx 0;
      position: relative;
      cursor: pointer;
      transition: all $transition-fast $ease-in-out;

      .tab-text {
        font-size: $font-size-base;
        color: $text-secondary;
        font-weight: $font-weight-medium;
      }

      &.active {
        .tab-text {
          color: $primary-color;
          font-weight: $font-weight-semibold;
        }

        .tab-indicator {
          position: absolute;
          bottom: -2rpx;
          left: 50%;
          transform: translateX(-50%);
          width: 60%;
          height: 4rpx;
          background: $primary-color;
          border-radius: 2rpx;
        }
      }

      &:hover:not(.active) {
        background: rgba($primary-color, 0.05);
      }
    }
  }

  .config-content {
    flex: 1;
    padding: $padding-base;
  }
}

/* 配置区域通用样式 */
.config-section {
  .component-header {
    margin-bottom: $margin-base;
    padding-bottom: $padding-small;
    border-bottom: 1px solid $border-color-lighter;

    .component-title {
      display: block;
      font-size: $font-size-large;
      font-weight: $font-weight-semibold;
      color: $text-primary;
      margin-bottom: 4rpx;
    }

    .component-desc {
      font-size: $font-size-small;
      color: $text-secondary;
    }
  }
}

/* 表单组件样式 */
.form-group {
  margin-bottom: $margin-base;

  .form-label {
    display: block;
    font-size: $font-size-base;
    font-weight: $font-weight-medium;
    color: $text-primary;
    margin-bottom: var(--margin-mini);

    &.required::after {
      content: '*';
      color: $danger-color;
      margin-left: 4rpx;
    }
  }

  .form-input {
    width: 100%;
    padding: 20rpx;
    border: 2rpx solid $border-color-lighter;
    border-radius: $border-radius;
    font-size: $font-size-base;
    color: $text-primary;
    background: $background-color-white;
    transition: all $transition-fast $ease-in-out;

    &:focus {
      border-color: $primary-color;
      box-shadow: $input-focus-shadow;
      outline: none;
    }

    &.disabled {
      background: $background-color;
      color: $text-secondary;
      cursor: not-allowed;
    }
  }

  .form-tip {
    display: block;
    font-size: $font-size-small;
    color: $text-secondary;
    margin-top: 4rpx;
  }
}

.form-row {
  display: flex;
  gap: $margin-base;

  .half {
    flex: 1;
  }
}

.form-picker {
  .picker-display {
    padding: 20rpx;
    border: 2rpx solid $border-color-lighter;
    border-radius: $border-radius;
    background: $background-color-white;
    font-size: $font-size-base;
    color: $text-primary;

    .picker-arrow {
      color: $text-secondary;
      font-size: $font-size-small;
    }
  }
}

.form-textarea {
  width: 100%;
  min-height: 160rpx;
  padding: 20rpx;
  border: 2rpx solid $border-color-lighter;
  border-radius: $border-radius;
  font-size: $font-size-base;
  color: $text-primary;
  background: $background-color-white;
  resize: vertical;

  &:focus {
    border-color: $primary-color;
    box-shadow: $input-focus-shadow;
    outline: none;
  }
}

.textarea-footer {
  margin-top: 8rpx;

  .textarea-tip {
    font-size: $font-size-small;
    color: $text-secondary;
  }

  .textarea-count {
    font-size: $font-size-small;
    color: $text-secondary;
  }
}

/* 布局类型选择 */
.layout-types {
  display: flex;
  gap: $margin-small;
  flex-wrap: wrap;

  .layout-type-item {
    flex: 1;
    min-width: 0;
    padding: 24rpx 16rpx;
    border: 2rpx solid $border-color-lighter;
    border-radius: $border-radius;
    background: $background-color-white;
    text-align: center;
    cursor: pointer;
    transition: all $transition-fast $ease-in-out;

    &:hover {
      border-color: $primary-color;
      transform: translateY(-2rpx);
    }

    &.active {
      border-color: $primary-color;
      background: rgba($primary-color, 0.05);
      box-shadow: $input-focus-shadow;
    }

    .layout-icon {
      width: 48rpx;
      height: 48rpx;
      margin: 0 auto 12rpx;
      background: $background-color;
      border-radius: var(--border-radius-small);

      &.single {
        background: linear-gradient(90deg, $primary-color 100%, transparent 0%);
      }

      &.double {
        background: linear-gradient(90deg, $primary-color 40%, $secondary-color 40%, $secondary-color 100%);
      }

      &.triple {
        background: linear-gradient(90deg, $primary-color 33%, $secondary-color 33%, $secondary-color 66%, $primary-color 66%);
      }

      &.creative {
        background: linear-gradient(45deg, $primary-color 25%, $secondary-color 25%, $secondary-color 50%, $primary-color 50%, $primary-color 75%, $secondary-color 75%);
        background-size: 20rpx 20rpx;
      }
    }

    .layout-name {
      font-size: $font-size-small;
      font-weight: $font-weight-medium;
      color: $text-primary;
    }
  }
}

/* 布局配置 */
.layout-config {
  background: $background-color;
  border-radius: $border-radius;
  padding: $padding-base;
  margin: $margin-base 0;

  .layout-preview-container {
    margin-bottom: $margin-base;

    .preview-title {
      display: block;
      font-size: $font-size-base;
      font-weight: $font-weight-medium;
      color: $text-primary;
      margin-bottom: $margin-small;
    }
  }

  .two-column-preview {
    height: 120rpx;
    background: $background-color-white;
    border: 2rpx solid $border-color-lighter;
    border-radius: $border-radius;
    overflow: hidden;
    display: flex;

    .column-left,
    .column-right {
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: $font-weight-medium;
      color: white;

      .column-label {
        font-size: $font-size-small;
      }
    }

    .column-left {
      background: $primary-color;
    }

    .column-right {
      background: $secondary-color;
    }
  }

  .width-controls {
    .control-group {
      margin-bottom: $margin-base;

      .control-label {
        display: block;
        font-size: $font-size-base;
        color: $text-primary;
        margin-bottom: var(--margin-mini);
      }
    }
  }
}

.form-slider {
  margin: 0;

}

/* 区块管理 */
.component-management {
  .component-list {
    margin-bottom: $margin-base;

    .component-item {
      margin-bottom: $margin-small;
      background: $background-color-white;
      border: 2rpx solid $border-color-lighter;
      border-radius: $border-radius;
      overflow: hidden;
      transition: all 0.3s ease;
      cursor: pointer;

      &:hover {
        border-color: rgba($primary-color, 0.5);
        box-shadow: $box-shadow-light;
      }

      &.selected {
        border-color: $primary-color;
        background: rgba($primary-color, 0.05);
        box-shadow: 0 4rpx 16rpx rgba($primary-color, 0.15);

        .component-index {
          background: $primary-color;
          color: white;
        }

        .component-info {
          .component-input {
            border-color: $primary-color;
          }

          .component-picker .picker-display {
            border-color: $primary-color;
            background: rgba($primary-color, 0.02);
          }
        }
      }

      .component-item-main {
        display: flex;
        align-items: center;
        padding: $padding-small;
        gap: $padding-small;

        .component-index {
          width: 48rpx;
          height: 48rpx;
          display: flex;
          align-items: center;
          justify-content: center;
          background: $background-color;
          border-radius: 50%;
          font-weight: $font-weight-semibold;
          color: $text-secondary;
          transition: all 0.3s ease;

          .index-text {
            font-size: $font-size-base;
          }
        }

        .component-info {
          flex: 1;
          display: flex;
          flex-direction: column;
          gap: 8rpx;

          .component-input {
            width: 100%;
            padding: 12rpx;
            border: 1px solid $border-color-lighter;
            border-radius: var(--border-radius-small);
            font-size: $font-size-base;
            background: $background-color-white;
            transition: all 0.2s ease;

            &:focus {
              border-color: $primary-color;
              outline: none;
            }
          }

          .component-picker {
            .picker-display {
              padding: 12rpx;
              border: 1px solid $border-color-lighter;
              border-radius: var(--border-radius-small);
              background: $background-color-white;
              font-size: $font-size-base;
              color: $text-primary;
              transition: all 0.2s ease;

              &:active {
                background: $background-color;
              }

              .picker-arrow {
                color: $text-secondary;
                font-size: $font-size-small;
              }
            }
          }
        }

        .component-actions {
          display: flex;
          flex-direction: column;
          gap: 4rpx;

          .action-btn {
            width: 64rpx;
            height: 32rpx;
            display: flex;
            align-items: center;
            justify-content: center;
            border: none;
            border-radius: var(--border-radius-small);
            font-size: $font-size-small;
            transition: all 0.2s ease;

            &.move-up-btn {
              background: rgba($success-color, 0.1);
              color: $success-color;

              &:not(.disabled):active {
                background: rgba($success-color, 0.2);
              }

              &.disabled {
                background: rgba($text-secondary, 0.1);
                color: $text-secondary;
                cursor: not-allowed;
                opacity: 0.5;
              }
            }

            &.move-down-btn {
              background: rgba($warning-color, 0.1);
              color: $warning-color;

              &:not(.disabled):active {
                background: rgba($warning-color, 0.2);
              }

              &.disabled {
                background: rgba($text-secondary, 0.1);
                color: $text-secondary;
                cursor: not-allowed;
                opacity: 0.5;
              }
            }

            &.remove-btn {
              background: rgba($danger-color, 0.1);
              color: $danger-color;
              margin-top: 4rpx;

              &:active {
                background: rgba($danger-color, 0.2);
              }
            }

            .action-icon {
              font-weight: $font-weight-bold;
              font-size: 16rpx;
            }
          }
        }
      }
    }
  }

  .sort-controls {
    margin-bottom: $margin-base;
    padding: $padding-base;
    background: $background-color;
    border-radius: $border-radius;
    border: 1px solid $border-color-lighter;

    .sort-hint {
      display: block;
      font-size: $font-size-small;
      color: $text-primary;
      margin-bottom: $padding-small;
      font-weight: $font-weight-medium;

      &:before {
        content: '📌 ';
      }
    }

    .sort-buttons {
      display: flex;
      gap: $padding-small;

      .sort-btn {
        flex: 1;
        padding: 16rpx;
        background: rgba($primary-color, 0.1);
        border: 1px solid $primary-color;
        border-radius: var(--border-radius-small);
        color: $primary-color;
        font-size: $font-size-small;
        font-weight: $font-weight-medium;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8rpx;
        transition: all 0.2s ease;

        &:not(.disabled):active {
          background: rgba($primary-color, 0.2);
          transform: translateY(1rpx);
        }

        &.disabled {
          background: rgba($text-secondary, 0.1);
          border-color: $text-secondary;
          color: $text-secondary;
          cursor: not-allowed;
          opacity: 0.5;
        }

        .sort-icon {
          font-size: $font-size-base;
        }
      }
    }
  }

  .add-component-btn {
    width: 100%;
    padding: 24rpx;
    background: rgba($primary-color, 0.05);
    border: 2rpx dashed rgba($primary-color, 0.3);
    border-radius: $border-radius;
    color: $primary-color;
    font-size: $font-size-base;
    font-weight: $font-weight-medium;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8rpx;
    transition: all $transition-fast $ease-in-out;

    &:hover {
      background: rgba($primary-color, 0.1);
      border-color: $primary-color;
    }

    &:active {
      background: rgba($primary-color, 0.15);
    }

    .add-icon {
      font-size: $font-size-large;
      font-weight: $font-weight-bold;
    }
  }

  // 操作提示
  .operation-hint {
    margin-top: $margin-small;
    padding: 12rpx;
    background: rgba($info-color, 0.05);
    border-radius: var(--border-radius-small);
    font-size: $font-size-small;
    color: $text-secondary;
    text-align: center;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8rpx;

    .hint-icon {
      color: $info-color;
      font-size: $font-size-base;
    }
  }
}

// 在区块管理标题下添加操作说明
.component-management .component-desc {
  display: block;
  font-size: $font-size-small;
  color: $text-secondary;
  margin-top: 4rpx;

  &:before {
    content: '💡 ';
  }
}

/* 颜色配置 */
.color-config {
  background: $background-color;
  border-radius: $border-radius;
  padding: $padding-base;
  margin-bottom: $margin-base;

  .config-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $margin-base;

    .config-title {
      font-size: $font-size-base;
      font-weight: $font-weight-semibold;
      color: $text-primary;
    }

    .reset-btn {
      padding: 8rpx 16rpx;
      background: rgba($danger-color, 0.1);
      color: $danger-color;
      border: 1px solid $danger-color;
      border-radius: var(--border-radius-small);
      font-size: $font-size-small;
    }
  }

  .color-group {
    .color-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: $padding-small 0;
      border-bottom: 1px solid $border-color-lighter;

      &:last-child {
        border-bottom: none;
      }

      .color-info {
        flex: 1;

        .color-label {
          display: block;
          font-size: $font-size-base;
          font-weight: $font-weight-medium;
          color: $text-primary;
          margin-bottom: 4rpx;
        }

        .color-desc {
          font-size: $font-size-small;
          color: $text-secondary;
        }
      }

      .color-controls {
        display: flex;
        align-items: center;
        gap: $padding-small;

        .color-input {
          width: 120rpx;
          padding: 12rpx;
          border: 1px solid $border-color-lighter;
          border-radius: var(--border-radius-small);
          font-size: $font-size-base;
          text-align: center;
          font-family: monospace;
        }

        .color-preview {
          width: 40rpx;
          height: 40rpx;
          border-radius: var(--border-radius-small);
          border: 2rpx solid $border-color-lighter;
          cursor: pointer;
        }
      }
    }
  }
}

/* 字体配置 */
.font-config {
  background: $background-color;
  border-radius: $border-radius;
  padding: $padding-base;
  margin-bottom: $margin-base;

  .font-sizes {
    .size-item {
      padding: $padding-small 0;
      border-bottom: 1px solid $border-color-lighter;

      &:last-child {
        border-bottom: none;
      }

      .size-label {
        display: block;
        font-size: $font-size-base;
        font-weight: $font-weight-medium;
        color: $text-primary;
        margin-bottom: var(--margin-mini);
      }

      .size-controls {
        display: flex;
        align-items: center;
        gap: $padding-small;

        .size-input {
          width: 80rpx;
          padding: 12rpx;
          border: 1px solid $border-color-lighter;
          border-radius: var(--border-radius-small);
          font-size: $font-size-base;
          text-align: center;
        }

        .size-unit {
          font-size: $font-size-base;
          color: $text-secondary;
        }

        .size-slider {
          flex: 1;

          .size-slider-bar {
            margin: 0;
          }
        }
      }
    }
  }
}

/* 间距配置 */
.spacing-config {
  background: $background-color;
  border-radius: $border-radius;
  padding: $padding-base;

  .spacing-items {
    .spacing-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: $padding-small 0;
      border-bottom: 1px solid $border-color-lighter;

      &:last-child {
        border-bottom: none;
      }

      .spacing-info {
        flex: 1;

        .spacing-label {
          display: block;
          font-size: $font-size-base;
          font-weight: $font-weight-medium;
          color: $text-primary;
          margin-bottom: 4rpx;
        }

        .spacing-desc {
          font-size: $font-size-small;
          color: $text-secondary;
        }
      }

      .spacing-input {
        width: 120rpx;
        padding: 12rpx;
        border: 1px solid $border-color-lighter;
        border-radius: var(--border-radius-small);
        font-size: $font-size-base;
        text-align: center;
      }
    }
  }
}

/* 右侧预览面板 */
.preview-panel {
  flex: 1;
  background: $background-color-white;
  border-radius: $border-radius-large;
  box-shadow: $box-shadow;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  .preview-header {
    padding: $padding-base;
    border-bottom: 1px solid $border-color-lighter;
    background: linear-gradient(to right, rgba($primary-color, 0.03), rgba($secondary-color, 0.03));

    .preview-title-container {
      .preview-title {
        display: block;
        font-size: $font-size-large;
        font-weight: $font-weight-semibold;
        color: $text-primary;
        margin-bottom: 4rpx;
      }

      .preview-subtitle {
        font-size: $font-size-small;
        color: $text-secondary;
      }
    }

    .preview-controls {
      display: flex;
      align-items: center;
      gap: $margin-base;

      .device-switch {
        display: flex;
        align-items: center;
        gap: $padding-small;

        .switch-label {
          font-size: $font-size-base;
          color: $text-primary;
        }

        .switch-buttons {
          display: flex;
          gap: 8rpx;

          .device-btn {
            padding: 8rpx 16rpx;
            border: 1px solid $border-color-lighter;
            border-radius: var(--border-radius-small);
            background: $background-color-white;
            font-size: $font-size-small;
            display: flex;
            align-items: center;
            gap: 4rpx;

            &.active {
              background: $primary-color;
              color: white;
              border-color: $primary-color;
            }

            .device-icon {
              font-size: $font-size-base;
            }
          }
        }
      }

      .refresh-btn {
        padding: 12rpx 24rpx;
        background: $primary-color;
        color: white;
        border: none;
        border-radius: $border-radius;
        font-size: $font-size-base;
        font-weight: $font-weight-medium;
        display: flex;
        align-items: center;
        gap: 8rpx;

        .refresh-icon {
          font-size: $font-size-base;
        }
      }
    }
  }

  .preview-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: $padding-base;

    &.desktop .preview-container {
      max-width: 100%;
    }

    &.tablet .preview-container {
      max-width: 768rpx;
      margin: 0 auto;
    }

    &.mobile .preview-container {
      max-width: 375rpx;
      margin: 0 auto;
    }

    .preview-container {
      flex: 1;
      background: $background-color;
      border-radius: $border-radius;
      overflow: hidden;
      position: relative;

      .preview-webview {
        width: 100%;
        height: 100%;
      }

      .preview-placeholder {
        width: 100%;
        height: 100%;

        .placeholder-content {
          text-align: center;

          .placeholder-icon {
            font-size: 80rpx;
            display: block;
            margin-bottom: $margin-base;
          }

          .placeholder-title {
            display: block;
            font-size: $font-size-large;
            font-weight: $font-weight-semibold;
            color: $text-primary;
            margin-bottom: $margin-small;
          }

          .placeholder-desc {
            display: block;
            font-size: $font-size-base;
            color: $text-secondary;
            margin-bottom: $margin-base;
          }

          .placeholder-btn {
            padding: 20rpx 40rpx;
            background: $primary-color;
            color: white;
            border: none;
            border-radius: $border-radius;
            font-size: $font-size-base;
            font-weight: $font-weight-medium;
          }
        }
      }
    }

    .preview-info {
      margin-top: $margin-base;
      padding: $padding-base;
      background: $background-color;
      border-radius: $border-radius;

      .info-title {
        display: block;
        font-size: $font-size-base;
        font-weight: $font-weight-semibold;
        color: $text-primary;
        margin-bottom: $margin-small;
      }

      .info-items {
        .info-item {
          display: flex;
          justify-content: space-between;
          padding: 8rpx 0;
          border-bottom: 1px solid $border-color-lighter;

          &:last-child {
            border-bottom: none;
          }

          .info-label {
            font-size: $font-size-base;
            color: $text-secondary;
          }

          .info-value {
            font-size: $font-size-base;
            color: $text-primary;
            font-weight: $font-weight-medium;
          }
        }
      }
    }
  }
}

/* 底部操作栏 */
.editor-footer {
  padding: $padding-base;
  background: $background-color-white;
  border-top: 1px solid $border-color-lighter;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.05);

  .footer-left {
    .footer-status {
      font-size: $font-size-base;
      color: $text-primary;
      font-weight: $font-weight-medium;
    }
  }

  .footer-right {
    display: flex;
    gap: $margin-small;

    .footer-btn {
      padding: 20rpx 40rpx;
      border: 2rpx solid $border-color-lighter;
      border-radius: $border-radius;
      font-size: $font-size-base;
      font-weight: $font-weight-medium;
      transition: all $transition-fast $ease-in-out;

      &.secondary {
        background: $background-color-white;
        color: $text-primary;

        &:hover {
          border-color: $text-secondary;
        }
      }

      &.primary {
        background: linear-gradient(135deg, $primary-color 0%, color.adjust($primary-color, $lightness: -10%) 100%);
        color: white;
        border: none;

        &:hover {
          box-shadow: $button-active-shadow;
        }
      }
    }
  }
}

/* 工具类 */
.flex-center {
  display: flex;
  justify-content: center;
  align-items: center;
}

.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.preview-mode-switch {
  margin-top: $margin-base;
  padding: $padding-small;
  background: $background-color;
  border-radius: $border-radius;
  display: flex;
  align-items: center;
  justify-content: space-between;

  .switch-label {
    font-size: $font-size-base;
    color: $text-primary;
  }

  .switch-buttons {
    display: flex;
    gap: 8rpx;

    .mode-btn {
      padding: 8rpx 16rpx;
      border: 1px solid $border-color-lighter;
      border-radius: var(--border-radius-small);
      background: $background-color-white;
      font-size: $font-size-small;

      &.active {
        background: $primary-color;
        color: white;
        border-color: $primary-color;
      }
    }
  }
}

.template-preview-container {
  width: 100%;
  height: 100%;
  min-height: 600rpx;
  overflow-y: auto;
  background: #ffffff;
  border: 1px solid #ebeef5;
  border-radius: 8rpx;
}

/* 更新预览容器样式 */
.preview-content {
  &.desktop .template-preview-container {
    max-width: 100%;
    height: 600rpx;
  }

  &.tablet .template-preview-container {
    max-width: 768px;
    height: 800rpx;
    margin: 0 auto;
    border-radius: 16rpx;
  }

  &.mobile .template-preview-container {
    max-width: 375px;
    height: 800rpx;
    margin: 0 auto;
    border-radius: 24rpx;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
  }
}

.template-preview-component {
  width: 100%;
  height: 100%;
  overflow-y: auto;
}

.webview-preview {
  width: 100%;
  height: 100%;
}

.color-config {
  background: $background-color;
  border-radius: $border-radius;
  padding: $padding-base;
  margin-bottom: $margin-base;

  .config-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $margin-base;

    .config-title {
      font-size: $font-size-base;
      font-weight: $font-weight-semibold;
      color: $text-primary;
    }

    .reset-btn {
      padding: 8rpx 16rpx;
      background: rgba($danger-color, 0.1);
      color: $danger-color;
      border: 1px solid $danger-color;
      border-radius: var(--border-radius-small);
      font-size: $font-size-small;
      transition: all 0.2s ease;

      &:active {
        background: rgba($danger-color, 0.2);
      }
    }
  }

  .color-group {
    .color-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: $padding-small 0;
      border-bottom: 1px solid $border-color-lighter;

      &:last-child {
        border-bottom: none;
      }

      .color-info {
        flex: 1;

        .color-label {
          display: block;
          font-size: $font-size-base;
          font-weight: $font-weight-medium;
          color: $text-primary;
          margin-bottom: 4rpx;
        }

        .color-desc {
          font-size: $font-size-small;
          color: $text-secondary;
        }
      }

      .color-controls {
        display: flex;
        align-items: center;
        gap: $padding-small;

        .color-input {
          width: 160rpx;
          padding: 12rpx;
          border: 1px solid $border-color-lighter;
          border-radius: var(--border-radius-small);
          font-size: $font-size-base;
          text-align: center;
          font-family: monospace;
          background: white;
          transition: all 0.2s ease;

          &:focus {
            border-color: $primary-color;
            box-shadow: 0 0 0 2px rgba($primary-color, 0.1);
            outline: none;
          }

          &::placeholder {
            color: #c0c4cc;
          }
        }

        .color-preview {
          width: 48rpx;
          height: 48rpx;
          border-radius: var(--border-radius-small);
          border: 2rpx solid $border-color-lighter;
          cursor: pointer;
          transition: all 0.2s ease;

          &:hover {
            transform: scale(1.1);
            box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.15);
          }

          &:active {
            transform: scale(0.95);
          }
        }
      }
    }
  }

  .color-presets {
    margin-top: $margin-base;
    padding-top: $padding-base;
    border-top: 1px solid $border-color-lighter;

    .presets-title {
      font-size: $font-size-small;
      color: $text-secondary;
      margin-bottom: $padding-small;
    }

    .presets-grid {
      display: grid;
      grid-template-columns: repeat(8, 1fr);
      gap: 8rpx;

      .preset-color {
        width: 36rpx;
        height: 36rpx;
        border-radius: 4rpx;
        cursor: pointer;
        transition: all 0.2s ease;
        border: 1px solid rgba(0, 0, 0, 0.1);

        &:hover {
          transform: scale(1.2);
          box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
        }

        &:active {
          transform: scale(0.9);
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .preview-mode-switch {
    flex-direction: column;
    gap: $margin-small;
    align-items: flex-start;
  }
  .editor-container {
    flex-direction: column;
  }
  .config-panel,
  .preview-panel {
    flex: none;
    width: 100%;
  }
  .preview-header {
    flex-direction: column;
    gap: $margin-base;

    .preview-controls {
      width: 100%;
      justify-content: space-between;
    }
  }
  .header-actions {
    flex-direction: column;
  }
  .form-row {
    flex-direction: column;
  }
  .color-config {
    .color-item {
      flex-direction: column;
      align-items: flex-start;
      gap: $padding-small;

      .color-info {
        width: 100%;
      }

      .color-controls {
        width: 100%;
        justify-content: space-between;

        .color-input {
          flex: 1;
        }
      }
    }

    .color-presets {
      .presets-grid {
        grid-template-columns: repeat(4, 1fr);
      }
    }
  }
  .component-management {
    .component-item {
      .component-item-main {
        flex-direction: column;
        align-items: stretch;

        .component-index {
          align-self: flex-start;
          margin-bottom: 8rpx;
        }

        .component-actions {
          flex-direction: row;
          justify-content: flex-end;
          margin-top: 12rpx;

          .action-btn {
            width: 32rpx;
            height: 32rpx;

            .action-icon {
              font-size: 14rpx;
            }
          }
        }
      }
    }

    .sort-controls {
      .sort-buttons {
        flex-direction: column;
      }
    }
  }
}
</style>