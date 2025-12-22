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
                v-model="templateForm.code"
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
                v-model="templateForm.name"
                placeholder="如: 科技风格简历"
                class="form-input"
                @focus="onInputFocus"
                @blur="onInputBlur"
              />
            </view>

            <view class="form-group">
              <text class="form-label">描述</text>
              <textarea
                v-model="templateForm.description"
                placeholder="请输入模板描述..."
                class="form-textarea"
                maxlength="200"
                @focus="onTextareaFocus"
                @blur="onTextareaBlur"
              />
              <view class="textarea-footer flex-between">
                <text class="textarea-tip">简要描述模板用途和特点</text>
                <text class="textarea-count">{{ templateForm.description?.length || 0 }}/200</text>
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
                  :class="['layout-type-item', { active: templateForm.globalLayout.type === type.value }]"
                  @click="templateForm.globalLayout.type = type.value"
                >
                  <view :class="['layout-icon', type.icon]"></view>
                  <text class="layout-name">{{ type.label }}</text>
                </view>
              </view>
            </view>

            <!-- 双栏布局配置 -->
            <view v-if="templateForm.globalLayout.type === 'two-column'" class="layout-config">
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
                  <text class="control-label">左侧宽度: {{ templateForm.globalLayout.columns.left }}%</text>
                  <slider
                    :value="templateForm.globalLayout.columns.left"
                    min="20"
                    max="80"
                    step="5"
                    @changing="onColumnWidthChanging"
                    data-side="left"
                    active-color="#d4af37"
                    background-color="#ebeef5"
                    class="form-slider"
                  />
                </view>
                <view class="control-group">
                  <text class="control-label">右侧宽度: {{ templateForm.globalLayout.columns.right }}%</text>
                  <slider
                    :value="templateForm.globalLayout.columns.right"
                    min="20"
                    max="80"
                    step="5"
                    @changing="onColumnWidthChanging"
                    data-side="right"
                    active-color="#d4af37"
                    background-color="#ebeef5"
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

              <!-- 组件推荐区域 -->
              <view v-if="getRecommendedComponents.length > 0" class="component-recommendation">
                <text class="recommendation-title">常用组件推荐</text>
                <view class="recommendation-list">
                  <view
                    v-for="component in getRecommendedComponents"
                    :key="component.id"
                    class="recommendation-item"
                    @click="addSectionWithComponent(component)"
                  >
                    <text>{{ component.name }}</text>
                  </view>
                </view>
              </view>

              <view class="component-list">
                <view
                  v-for="(component, index) in templateForm.components"
                  :key="`component-${component.componentId || 'empty'}-${index}`"
                  :class="['component-item', { 'selected': selectedIndex === index }]"
                  @click="selectComponent(index)"
                >
                  <view class="component-item-main">
                    <view class="component-index">
                      <text class="index-text">{{ index + 1 }}</text>
                    </view>
                    <view class="component-info">
                      <!-- 组件信息显示区域 -->
                      <view class="component-name-display">
                        <text class="component-name">
                          {{ getComponentName(component.componentId) || "未选择组件" }}
                        </text>
                        <text v-if="component.componentId" class="component-key">
                          {{ getComponentKey(component.componentId) }}
                        </text>
                      </view>

                      <!-- 组件选择器 -->
                      <picker
                        :value="getComponentIndex(component.componentId)"
                        :range="componentOptions"
                        @change="(e) => onComponentChange(index, e)"
                        class="component-picker"
                      >
                        <view class="picker-display flex-between">
                          <text>{{ getSelectedComponentLabel(component.componentId) }}</text>
                          <text class="picker-arrow">▼</text>
                        </view>
                      </picker>

                      <!-- 组件描述 -->
                      <view v-if="component.componentId" class="component-description">
                        <text class="desc-text">{{ getComponentDescription(component.componentId) }}</text>
                      </view>
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
                        :class="['action-btn move-down-btn', { disabled: index === templateForm.components.length - 1 }]"
                        :disabled="index === templateForm.components.length - 1"
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

                  <!-- 组件详情按钮 -->
                  <view v-if="component.componentId" class="component-details-container">
                    <button @click.stop="showComponentDetails(component.componentId)" class="component-details-btn">
                      <text class="details-icon">ℹ️</text>
                      <text class="details-text">查看组件详情</text>
                    </button>
                  </view>
                </view>
              </view>

              <view class="sort-controls" v-if="selectedIndex !== -1 && templateForm.components.length > 0">
                <text class="sort-hint">
                  当前选中: {{ getComponentName(templateForm.components[selectedIndex]?.componentId) || "未命名区块" }}
                </text>
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
                    :class="['sort-btn', { disabled: selectedIndex === templateForm.components.length - 1 }]"
                    :disabled="selectedIndex === templateForm.components.length - 1"
                  >
                    <text class="sort-icon">⏬</text>
                    移到底部
                  </button>
                </view>
              </view>

              <button @click="addSection" class="add-component-btn">
                <text class="add-icon">+</text>
                添加空区块
              </button>

              <!-- 组件计数显示 -->
              <view class="component-count">
                <text class="count-label">区块总数: {{ templateForm.components.length }}</text>
                <text class="count-valid">有效区块: {{ validComponentsCount }}</text>
              </view>

              <!-- 添加组件选择帮助提示 -->
              <view class="component-help">
                <text class="help-icon">💡</text>
                <text class="help-text">
                  从下拉列表中选择组件，系统会自动填充组件的默认配置
                </text>
              </view>
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
                    <input
                      type="text"
                      :value="templateForm.globalStyle.primaryColor"
                      @input="onColorInput($event, 'primaryColor')"
                      class="color-input"
                      placeholder="#d4af37"
                    />
                    <view
                      class="color-preview"
                      :style="{ backgroundColor: templateForm.globalStyle.primaryColor }"
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
                      :value="templateForm.globalStyle.secondaryColor"
                      @input="onColorInput($event, 'secondaryColor')"
                      class="color-input"
                      placeholder="#f9f3e3"
                    />
                    <view
                      class="color-preview"
                      :style="{ backgroundColor: templateForm.globalStyle.secondaryColor }"
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
                      :value="templateForm.globalStyle.accentColor"
                      @input="onColorInput($event, 'accentColor')"
                      class="color-input"
                      placeholder="#f7ef8a"
                    />
                    <view
                      class="color-preview"
                      :style="{ backgroundColor: templateForm.globalStyle.accentColor }"
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
                  v-model="templateForm.globalStyle.fontFamily"
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
                      v-model="templateForm.globalStyle.fontSizes.h1"
                      class="size-input"
                      min="12"
                      max="72"
                    />
                    <text class="size-unit">px</text>
                    <view class="size-slider">
                      <slider
                        :value="Number(templateForm.globalStyle.fontSizes.h1)"
                        min="12"
                        max="72"
                        step="2"
                        @changing="onFontSizeChanging"
                        data-type="h1"
                        active-color="#d4af37"
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
                      v-model="templateForm.globalStyle.fontSizes.body"
                      class="size-input"
                      min="10"
                      max="36"
                    />
                    <text class="size-unit">px</text>
                    <view class="size-slider">
                      <slider
                        :value="Number(templateForm.globalStyle.fontSizes.body)"
                        min="10"
                        max="36"
                        step="1"
                        @changing="onFontSizeChanging"
                        data-type="body"
                        active-color="#d4af37"
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
                    v-model="templateForm.globalStyle.spacing.sectionMargin"
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
                    v-model="templateForm.globalStyle.spacing.padding"
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
                    v-model="templateForm.globalStyle.spacing.lineHeight"
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
            :components="templateForm.components"
            :globalLayout="templateForm.globalLayout"
            :global-style="templateForm.globalStyle"
            :device="previewDevice"
            class="template-preview-container"
          />

          <view class="preview-info">
            <text class="info-title">模板信息</text>
            <view class="info-items">
              <view class="info-item">
                <text class="info-label">名称:</text>
                <text class="info-value">{{ templateForm.name || "未命名" }}</text>
              </view>
              <view class="info-item">
                <text class="info-label">布局:</text>
                <text class="info-value">{{ layoutTypeLabel }}</text>
              </view>
              <view class="info-item">
                <text class="info-label">区块数:</text>
                <text class="info-value">{{ templateForm.components.length }}</text>
              </view>
              <view class="info-item">
                <text class="info-label">有效组件:</text>
                <text class="info-value">{{ validComponentsCount }}</text>
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
        <text v-if="componentErrors.length > 0" class="footer-error">
          ⚠️ 有 {{ componentErrors.length }} 个组件未配置
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
import { computed, onMounted, ref, watch } from "vue";
import TemplatePreview from "@/components/template/TemplatePreview.vue";
import { TemplateForm } from "@/types/template";
import { TemplateComponentForm } from "@/types/template-component";
import TemplateAPI from "@/api/template";
import { COLOR_PRESETS, CONFIG_TABS, DEVICE_OPTIONS, LAYOUT_TYPES } from "@/constants/template";
import { COMPONENT_LIBRARY } from "@/constants/component";
import { useTemplateStore } from "@/stores/template";

// 使用模板 store
const templateStore = useTemplateStore();

// 响应式数据
const isEditMode = ref(false);
const activeTab = ref("basic");
const previewDevice = ref("desktop");
const previewKey = ref(0);
const scrollTop = ref(0);
const showColorPresets = ref(false);
const currentColorField = ref<"primaryColor" | "secondaryColor" | "accentColor">("primaryColor");
const selectedIndex = ref(-1);

const colorPresets = ref(COLOR_PRESETS);

// 使用 store 中的 editForm
const templateForm = templateStore.editForm;

// 选项数据
const configTabs = ref(CONFIG_TABS);
const deviceOptions = ref(DEVICE_OPTIONS);
const availableComponents = ref(COMPONENT_LIBRARY);

// 组件选择历史（用于推荐）
const componentSelectionHistory = ref<number[]>([]);

// 计算属性
const componentOptions = computed(() =>
  availableComponents.value.map(c => `${c.name} (${c.key})`),
);

const layoutTypeLabel = computed(() => {
  const type = LAYOUT_TYPES.find(t => t.value === templateForm.globalLayout?.type);
  return type ? type.label : "单栏";
});

const validComponentsCount = computed(() => {
  return (templateForm.components || []).filter(c => c.componentId > 0).length;
});

// 获取组件名称（根据 componentId）
const getComponentName = (componentId: number): string => {
  if (!componentId) return "";
  const component = availableComponents.value.find(c => c.id === componentId);
  return component ? component.name : "";
};

// 获取组件 Key（根据 componentId）
const getComponentKey = (componentId: number): string => {
  if (!componentId) return "";
  const component = availableComponents.value.find(c => c.id === componentId);
  return component ? component.key : "";
};

// 获取组件描述（根据 componentId）
const getComponentDescription = (componentId: number): string => {
  if (!componentId) return "";
  const component = availableComponents.value.find(c => c.id === componentId);
  return component ? component.description : "";
};

// 获取组件索引（根据 componentId）
const getComponentIndex = (componentId: number): number => {
  if (!componentId) return -1;
  return availableComponents.value.findIndex(c => c.id === componentId);
};

// 获取选择器显示的标签
const getSelectedComponentLabel = (componentId: number): string => {
  if (!componentId) return "选择组件";
  const component = availableComponents.value.find(c => c.id === componentId);
  return component ? `${component.name} (${component.key})` : "选择组件";
};

// 获取推荐组件（基于选择历史）
const getRecommendedComponents = computed(() => {
  const historyMap = new Map<number, number>();
  componentSelectionHistory.value.forEach(id => {
    historyMap.set(id, (historyMap.get(id) || 0) + 1);
  });

  return [...availableComponents.value]
    .sort((a, b) => {
      const freqA = historyMap.get(a.id) || 0;
      const freqB = historyMap.get(b.id) || 0;
      return freqB - freqA;
    })
    .slice(0, 3); // 返回最常用的3个组件
});

// 验证组件配置完整性
const componentErrors = computed(() => {
  const components = templateForm.components || [];
  const errors: string[] = [];

  components.forEach((component, index) => {
    if (!component.componentId) {
      errors.push(`第 ${index + 1} 个区块未选择组件`);
    }
  });

  return errors;
});

// 方法
const refreshPreview = () => {
  previewKey.value += 1;
  templateStore.refreshPreview();
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
    confirmColor: "#d4af37",
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

const saveTemplate = async () => {
  if (!validateRequiredFields()) return;

  // 验证组件配置
  if (componentErrors.value.length > 0) {
    uni.showModal({
      title: "组件未配置",
      content: `有 ${componentErrors.value.length} 个区块未选择组件，是否继续保存？`,
      confirmText: "继续保存",
      cancelText: "去配置",
      confirmColor: "#e6a23c",
      success: async (res) => {
        if (res.confirm) {
          await performSave();
        } else {
          activeTab.value = "layout";
        }
      },
    });
  } else {
    await performSave();
  }
};

const performSave = async () => {
  uni.showModal({
    title: "保存模板",
    content: "确定要保存模板吗？模板将发布到模板库",
    confirmText: "发布",
    confirmColor: "#d4af37",
    success: async (res) => {
      if (res.confirm) {
        uni.showLoading({ title: "发布中..." });
        try {
          // 确保组件数组存在
          if (!templateForm.components) {
            templateForm.components = [];
          }

          // 准备提交数据 - 确保数据结构正确
          const submitData: TemplateForm = {
            ...templateForm,
            components: templateForm.components.map(comp => ({
              componentId: comp.componentId,
              props: comp.props || {},
              styles: comp.styles || {},
            })),
          };

          console.log("提交数据:", JSON.stringify(submitData, null, 2));
          console.log("组件数量:", submitData.components?.length);

          await TemplateAPI.addTemplate(submitData);
          uni.hideLoading();
          uni.showToast({
            title: "模板发布成功",
            icon: "success",
            duration: 2000,
          });

          setTimeout(() => {
            uni.navigateBack();
          }, 2000);
        } catch (error) {
          uni.hideLoading();
          uni.showToast({
            title: "保存失败，请重试",
            icon: "error",
            duration: 2000,
          });
          console.error("保存模板失败:", error);
        }
      }
    },
  });
};

const cancel = () => {
  uni.showModal({
    title: "确认离开",
    content: "离开后未保存的更改将会丢失",
    confirmText: "离开",
    confirmColor: "#f56c6c",
    success: (res) => {
      if (res.confirm) {
        uni.navigateBack();
      }
    },
  });
};

const validateRequiredFields = (): boolean => {
  if (!templateForm.code) {
    uni.showToast({
      title: "请填写模板编码",
      icon: "none",
    });
    return false;
  }

  if (!templateForm.name) {
    uni.showToast({
      title: "请填写模板名称",
      icon: "none",
    });
    return false;
  }

  return true;
};

const getColumnWidth = (side: "left" | "right") => {
  return `${templateForm.globalLayout?.columns?.[side] || 50}%`;
};

const onColumnWidthChanging = (e: any) => {
  const value = e.detail.value;
  const side = e.currentTarget.dataset.side;
  if (templateForm.globalLayout?.columns) {
    if (side === "left") {
      templateForm.globalLayout.columns.left = value;
      templateForm.globalLayout.columns.right = 100 - value;
    } else {
      templateForm.globalLayout.columns.right = value;
      templateForm.globalLayout.columns.left = 100 - value;
    }
    previewKey.value += 1;
  }
};

// 选择组件
const selectComponent = (index: number) => {
  selectedIndex.value = index;
  templateStore.selectComponent(index);
};

// 添加区块 - 修复：确保正确添加并更新store
const addSection = () => {
  const newComponent: TemplateComponentForm = {
    componentId: 0,
    props: {},
    styles: {},
  };

  // 使用store的方法添加组件
  templateStore.addComponent(newComponent);
  selectedIndex.value = (templateForm.components || []).length - 1;

  console.log("添加区块后，组件数量:", templateForm.components.length);

  uni.showToast({
    title: "已添加区块",
    icon: "success",
    duration: 800,
  });
};

// 添加带指定组件的区块 - 修复：确保正确添加
const addSectionWithComponent = (component: any) => {
  const newComponent: TemplateComponentForm = {
    componentId: component.id,
    props: { ...component.defaultConfig },
    styles: {},
  };

  // 使用store的方法添加组件
  templateStore.addComponent(newComponent);
  selectedIndex.value = (templateForm.components || []).length - 1;

  // 记录选择历史
  if (!componentSelectionHistory.value.includes(component.id)) {
    componentSelectionHistory.value.push(component.id);
  }

  console.log("添加带组件区块后，组件数量:", templateForm.components.length);

  uni.showToast({
    title: `已添加: ${component.name}`,
    icon: "success",
    duration: 1000,
  });

  previewKey.value += 1;
};

// 组件选择变更处理
const onComponentChange = (sectionIndex: number, e: any) => {
  const componentIndex = parseInt(e.detail.value);
  if (componentIndex >= 0 && componentIndex < availableComponents.value.length) {
    const selectedComponent = availableComponents.value[componentIndex];
    const components = templateForm.components || [];

    if (components[sectionIndex]) {
      components[sectionIndex].componentId = selectedComponent.id;
      components[sectionIndex].props = { ...selectedComponent.defaultConfig };

      // 更新store
      templateStore.updateEditForm({ components: [...components] });

      uni.showToast({
        title: `已选择: ${selectedComponent.name}`,
        icon: "success",
        duration: 1000,
      });

      // 记录组件选择历史
      if (!componentSelectionHistory.value.includes(selectedComponent.id)) {
        componentSelectionHistory.value.push(selectedComponent.id);
      }

      previewKey.value += 1;
    }
  }
};

// 删除区块 - 修复：确保正确删除
const removeSection = (index: number) => {
  uni.showModal({
    title: "删除区块",
    content: "确定要删除这个区块吗？",
    confirmColor: "#f56c6c",
    success: (res) => {
      if (res.confirm) {
        // 使用store的方法删除组件
        templateStore.removeComponent(index);

        // 调整选中索引
        if (selectedIndex.value === index) {
          selectedIndex.value = -1;
        } else if (selectedIndex.value > index) {
          selectedIndex.value -= 1;
        }

        console.log("删除区块后，组件数量:", templateForm.components.length);

        previewKey.value += 1;
        uni.showToast({
          title: "删除成功",
          icon: "success",
        });
      }
    },
  });
};

// 上移组件 - 修复：确保正确移动
const moveComponentUp = (index: number) => {
  if (index <= 0 || !templateForm.components) return;

  // 使用store的方法移动组件
  templateStore.moveComponent(index, index - 1);
  selectedIndex.value = index - 1;
  previewKey.value += 1;

  uni.showToast({
    title: "已上移",
    icon: "success",
    duration: 800,
  });
};

// 下移组件 - 修复：确保正确移动
const moveComponentDown = (index: number) => {
  if (!templateForm.components || index >= templateForm.components.length - 1) return;

  // 使用store的方法移动组件
  templateStore.moveComponent(index, index + 1);
  selectedIndex.value = index + 1;
  previewKey.value += 1;

  uni.showToast({
    title: "已下移",
    icon: "success",
    duration: 800,
  });
};

// 移到顶部 - 修复：确保正确移动
const moveSelectedToTop = () => {
  if (selectedIndex.value <= 0 || !templateForm.components) return;

  const components = [...templateForm.components];
  const selected = components.splice(selectedIndex.value, 1)[0];
  components.unshift(selected);
  templateStore.updateEditForm({ components });
  selectedIndex.value = 0;
  previewKey.value += 1;

  uni.showToast({
    title: "已移到顶部",
    icon: "success",
    duration: 1000,
  });
};

// 移到底部 - 修复：确保正确移动
const moveSelectedToBottom = () => {
  if (!templateForm.components || selectedIndex.value >= templateForm.components.length - 1) return;

  const components = [...templateForm.components];
  const selected = components.splice(selectedIndex.value, 1)[0];
  components.push(selected);
  templateStore.updateEditForm({ components });
  selectedIndex.value = components.length - 1;
  previewKey.value += 1;

  uni.showToast({
    title: "已移到底部",
    icon: "success",
    duration: 1000,
  });
};

// 显示组件详情
const showComponentDetails = (componentId: number) => {
  if (!componentId) return;

  const component = availableComponents.value.find(c => c.id === componentId);
  if (component) {
    uni.showModal({
      title: `组件详情: ${component.name}`,
      content: `
组件标识: ${component.key}
描述: ${component.description}
分类: ${component.category}
默认配置: ${JSON.stringify(component.defaultConfig, null, 2)}
      `,
      showCancel: false,
      confirmText: "确定",
    });
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
  if (templateForm.globalStyle?.fontSizes) {
    if (type === "h1") {
      templateForm.globalStyle.fontSizes.h1 = value.toString();
    } else {
      templateForm.globalStyle.fontSizes.body = value.toString();
    }
    previewKey.value += 1;
  }
};

// 颜色输入处理
const onColorInput = (event: any, field: string) => {
  const value = event.detail?.value || event.target?.value;
  if (value && templateForm.globalStyle) {
    let formattedValue = value.trim();

    if (formattedValue.startsWith("#") && formattedValue.length !== 4 && formattedValue.length !== 7) {
      if (formattedValue.length === 4) {
        formattedValue = "#" + formattedValue[1] + formattedValue[1] +
          formattedValue[2] + formattedValue[2] +
          formattedValue[3] + formattedValue[3];
      }
    }

    if (!formattedValue.startsWith("#")) {
      const hexRegex = /^[0-9A-Fa-f]{3}$|^[0-9A-Fa-f]{6}$/;
      if (hexRegex.test(formattedValue)) {
        formattedValue = "#" + formattedValue;
      }
    }

    if (validateColorHex(formattedValue)) {
      ;(templateForm.globalStyle as any)[field] = formattedValue;
      previewKey.value += 1;
    } else {
      uni.showToast({
        title: `颜色格式无效: ${formattedValue}`,
        icon: "none",
        duration: 2000,
      });
    }
  }
};

// 验证十六进制颜色
const validateColorHex = (color: string): boolean => {
  const hexRegex = /^#([0-9A-Fa-f]{3}){1,2}$/i;
  return hexRegex.test(color);
};

// 显示颜色选择器
const showColorPicker = (field: "primaryColor" | "secondaryColor" | "accentColor") => {
  currentColorField.value = field;
  templateStore.componentState.currentColorField = field;
  templateStore.componentState.showColorPicker = true;

  if (uni.chooseColor) {
    uni.chooseColor({
      color: (templateForm.globalStyle as any)[field] || "#d4af37",
      success: (res) => {
        if (templateForm.globalStyle) {
          ;(templateForm.globalStyle as any)[field] = res.color;
          previewKey.value += 1;
        }
      },
      fail: (err) => {
        console.log("颜色选择失败:", err);
        showColorPresets.value = !showColorPresets.value;
      },
    });
  } else {
    showColorPresets.value = !showColorPresets.value;
  }
};

// 设置颜色
const setColor = (field: string, color: string) => {
  if (templateForm.globalStyle) {
    ;(templateForm.globalStyle as any)[field] = color;
    previewKey.value += 1;
    showColorPresets.value = false;

    uni.showToast({
      title: `${field} 已设置为 ${color}`,
      icon: "success",
      duration: 1000,
    });
  }
};

// 重置颜色
const resetColors = () => {
  if (templateForm.globalStyle) {
    templateForm.globalStyle.primaryColor = "#d4af37";
    templateForm.globalStyle.secondaryColor = "#f9f3e3";
    templateForm.globalStyle.accentColor = "#f7ef8a";
    previewKey.value += 1;
    showColorPresets.value = false;

    uni.showToast({
      title: "颜色已重置为默认值",
      icon: "success",
    });
  }
};

// 生命周期
onMounted(() => {
  const pages = getCurrentPages();
  const currentPage = pages[pages.length - 1];
  const options = currentPage.options;

  if (options.id) {
    isEditMode.value = true;
    loadTemplateData(parseInt(options.id));
  } else {
    initExampleComponents();
  }
});

// 初始化示例组件
const initExampleComponents = () => {
  const exampleComponents: TemplateComponentForm[] = [
    {
      componentId: 1, // UserBasicInfo
      props: { showAvatar: true, showContact: true },
      styles: {},
    },
    {
      componentId: 3, // WorkExperience
      props: { showDuration: true, showCompany: true },
      styles: {},
    },
    {
      componentId: 4, // EducationExperience
      props: { showTime: true, showDegree: true },
      styles: {},
    },
  ];

  templateStore.updateEditForm({ components: exampleComponents });

  // 初始化选择历史
  componentSelectionHistory.value = exampleComponents.map(c => c.componentId);

  previewKey.value += 1;

  console.log("初始化示例组件，数量:", exampleComponents.length);

  uni.showToast({
    title: "示例组件已加载",
    icon: "success",
    duration: 1500,
  });
};

const loadTemplateData = async (id: number) => {
  try {
    console.log("加载模板数据:", id);
    uni.showLoading({ title: "加载模板数据..." });

    // 调用 API 加载模板数据
    const response = await TemplateAPI.getById(id);

    if (response) {
      // 更新 store 中的表单数据
      templateStore.updateEditForm(response);
      console.log("模板数据加载成功，组件数量:", response.components?.length);

      uni.showToast({
        title: "模板加载成功",
        icon: "success",
        duration: 1500,
      });
    } else {
      console.warn("未找到模板数据");
      uni.showToast({
        title: "模板不存在",
        icon: "error",
        duration: 2000,
      });
    }

    previewKey.value += 1;
  } catch (error) {
    console.error("加载模板数据失败:", error);
    uni.showToast({
      title: "加载失败，请重试",
      icon: "error",
      duration: 2000,
    });
  } finally {
    uni.hideLoading();
  }
};

// 监听组件数组变化
watch(
  () => templateForm.components,
  (newComponents) => {
    console.log("组件数组变化，新数量:", newComponents?.length);
    previewKey.value += 1;
  },
  { deep: true },
);

// 响应式适配：监听窗口尺寸变化
const updateLayoutForScreen = () => {
  const systemInfo = uni.getSystemInfoSync();
  const windowWidth = systemInfo.windowWidth;

  // 根据屏幕宽度调整布局
  if (windowWidth < 375) {
    // 小屏幕设备
    previewDevice.value = "mobile";
  } else if (windowWidth < 768) {
    // 中等屏幕设备
    previewDevice.value = "tablet";
  }
};

onMounted(() => {
  updateLayoutForScreen();

  // 监听窗口变化
  uni.onWindowResize && uni.onWindowResize(() => {
    updateLayoutForScreen();
  });
});
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

/* 组件推荐区域 */
.component-recommendation {
  margin-bottom: $margin-base;
  padding: $padding-base;
  background: rgba($success-color, 0.05);
  border-radius: $border-radius;
  border: 1px solid rgba($success-color, 0.2);

  .recommendation-title {
    font-size: $font-size-small;
    color: $success-color;
    font-weight: $font-weight-medium;
    margin-bottom: $padding-small;
    display: flex;
    align-items: center;
    gap: 8rpx;

    &:before {
      content: '⭐';
    }
  }

  .recommendation-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8rpx;

    .recommendation-item {
      padding: 12rpx 16rpx;
      background: white;
      border: 1px solid $border-color-lighter;
      border-radius: var(--border-radius-small);
      font-size: $font-size-small;
      color: $text-primary;
      cursor: pointer;
      transition: all 0.2s ease;

      &:active {
        background: $background-color;
        transform: translateY(1rpx);
      }

      &:hover {
        border-color: $primary-color;
        background: rgba($primary-color, 0.05);
      }
    }
  }
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

          .component-name-display {
            display: flex;
            align-items: center;
            flex-wrap: wrap;
            gap: 8rpx;

            .component-name {
              font-size: $font-size-base;
              font-weight: $font-weight-medium;
              color: $text-primary;
              display: block;
            }

            .component-key {
              font-size: $font-size-small;
              color: $text-secondary;
              font-family: monospace;
              background: rgba($primary-color, 0.05);
              padding: 2rpx 8rpx;
              border-radius: 4rpx;
            }
          }

          .component-description {
            margin-top: 4rpx;

            .desc-text {
              font-size: $font-size-small;
              color: $text-secondary;
              font-style: italic;
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

      /* 组件详情按钮 */
      .component-details-container {
        padding: 0 $padding-small $padding-small;

        .component-details-btn {
          width: 100%;
          padding: 8rpx;
          background: rgba($info-color, 0.05);
          border: 1px solid rgba($info-color, 0.2);
          border-radius: var(--border-radius-small);
          color: $info-color;
          font-size: $font-size-small;
          text-align: center;
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 4rpx;

          &:active {
            background: rgba($info-color, 0.1);
          }

          .details-icon {
            font-size: $font-size-base;
          }

          .details-text {
            font-size: $font-size-small;
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

  .component-help {
    margin-top: $margin-base;
    padding: 12rpx;
    background: rgba($info-color, 0.05);
    border-radius: var(--border-radius-small);
    font-size: $font-size-small;
    color: $text-secondary;
    display: flex;
    align-items: center;
    gap: 8rpx;

    .help-icon {
      color: $info-color;
      font-size: $font-size-base;
    }

    .help-text {
      flex: 1;
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
    position: relative;
    overflow: hidden;

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
    display: flex;
    flex-direction: column;
    gap: 4rpx;

    .footer-status {
      font-size: $font-size-base;
      color: $text-primary;
      font-weight: $font-weight-medium;
    }

    .footer-error {
      font-size: $font-size-small;
      color: $danger-color;
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

        .component-info {
          .component-name-display {
            flex-direction: column;
            align-items: flex-start;

            .component-key {
              margin-left: 0;
              margin-top: 4rpx;
            }
          }
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

    .component-recommendation {
      .recommendation-list {
        flex-direction: column;

        .recommendation-item {
          width: 100%;
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