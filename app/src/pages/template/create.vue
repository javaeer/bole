<template>
  <view class="template-editor">
    <!-- 头部 -->
    <view class="editor-header">
      <view class="header-title">
        <h2 class="title">{{ isEditMode ? "编辑模板" : "创建模板" }}</h2>
        <text class="subtitle">{{ isEditMode ? "修改模板配置" : "创建新的文档模板" }}</text>
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
          scroll-y
          :scroll-top="scrollTop"
          @scroll="onScroll"
        >
          <!-- 基础配置 -->
          <view v-if="activeTab === 'basic'" class="config-section">
            <view class="section-header">
              <text class="section-title">基础信息</text>
              <text class="section-desc">设置模板的基本属性和分类</text>
            </view>

            <view class="form-group">
              <text class="form-label required">模板编码</text>
              <input
                v-model="config.templateCode"
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
                v-model="config.templateName"
                placeholder="如: 科技风格简历"
                class="form-input"
                @focus="onInputFocus"
                @blur="onInputBlur"
              />
            </view>

            <view class="form-row">
              <view class="form-group half">
                <text class="form-label">分类</text>
                <picker
                  :value="categoryIndex"
                  :range="categoryOptions"
                  @change="onCategoryChange"
                  class="form-picker"
                >
                  <view class="picker-display flex-between">
                    <text>{{ config.category || "请选择分类" }}</text>
                    <text class="picker-arrow">▼</text>
                  </view>
                </picker>
              </view>

              <view class="form-group half">
                <text class="form-label">风格</text>
                <picker
                  :value="styleIndex"
                  :range="styleOptions"
                  @change="onStyleChange"
                  class="form-picker"
                >
                  <view class="picker-display flex-between">
                    <text>{{ config.style || "请选择风格" }}</text>
                    <text class="picker-arrow">▼</text>
                  </view>
                </picker>
              </view>
            </view>

            <view class="form-group">
              <text class="form-label">描述</text>
              <textarea
                v-model="config.description"
                placeholder="请输入模板描述..."
                class="form-textarea"
                maxlength="200"
                @focus="onTextareaFocus"
                @blur="onTextareaBlur"
              />
              <view class="textarea-footer flex-between">
                <text class="textarea-tip">简要描述模板用途和特点</text>
                <text class="textarea-count">{{ config.description.length }}/200</text>
              </view>
            </view>
          </view>

          <!-- 布局配置 -->
          <view v-if="activeTab === 'layout'" class="config-section">
            <view class="section-header">
              <text class="section-title">布局设置</text>
              <text class="section-desc">调整页面结构和区块排列</text>
            </view>

            <view class="form-group">
              <text class="form-label">布局类型</text>
              <view class="layout-types">
                <view
                  v-for="type in layoutTypes"
                  :key="type.value"
                  :class="['layout-type-item', { active: config.layoutConfig.type === type.value }]"
                  @click="config.layoutConfig.type = type.value"
                >
                  <view :class="['layout-icon', type.icon]"></view>
                  <text class="layout-name">{{ type.label }}</text>
                </view>
              </view>
            </view>

            <!-- 双栏布局配置 -->
            <view v-if="config.layoutConfig.type === 'two-column'" class="layout-config">
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
                  <text class="control-label">左侧宽度: {{ config.layoutConfig.columns.left.width }}%</text>
                  <slider
                    :value="config.layoutConfig.columns.left.width"
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
                  <text class="control-label">右侧宽度: {{ config.layoutConfig.columns.right.width }}%</text>
                  <slider
                    :value="config.layoutConfig.columns.right.width"
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
            <view class="section-management">
              <view class="section-header">
                <text class="section-title">区块管理</text>
                <text class="section-desc">拖拽调整顺序，点击编辑</text>
              </view>

              <view class="section-list">
                <view
                  v-for="(section, index) in config.layoutConfig.sections"
                  :key="index"
                  class="section-item"
                  @touchstart="onSectionTouchStart(index)"
                  @touchmove="onSectionTouchMove"
                  @touchend="onSectionTouchEnd"
                  :style="{ transform: sectionTransform(index) }"
                >
                  <view class="section-item-main">
                    <view class="drag-handle">
                      <text class="drag-icon">⋮⋮</text>
                    </view>
                    <input
                      v-model="section.name"
                      placeholder="区块名称"
                      class="section-input"
                      @focus="onSectionInputFocus(index)"
                    />
                    <picker
                      :value="getFragmentIndex(section.fragment)"
                      :range="fragmentOptions"
                      @change="(e: any) => onFragmentChange(index, e)"
                      class="fragment-picker"
                    >
                      <view class="picker-display flex-between">
                        <text>{{ getFragmentLabel(section.fragment) || "选择片段" }}</text>
                        <text class="picker-arrow">▼</text>
                      </view>
                    </picker>
                    <button
                      @click.stop="removeSection(index)"
                      class="section-remove"
                    >
                      <text class="remove-icon">✕</text>
                    </button>
                  </view>
                </view>
              </view>

              <button @click="addSection" class="add-section-btn">
                <text class="add-icon">+</text>
                添加区块
              </button>
            </view>
          </view>

          <!-- 样式配置 -->
          <view v-if="activeTab === 'style'" class="config-section">
            <view class="section-header">
              <text class="section-title">样式设置</text>
              <text class="section-desc">自定义颜色、字体和间距</text>
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
                      v-model="config.styleConfig.theme.primary_color"
                      class="color-input"
                      @input="validateColor"
                    />
                    <view
                      class="color-preview"
                      :style="{ backgroundColor: config.styleConfig.theme.primary_color }"
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
                      v-model="config.styleConfig.theme.secondary_color"
                      class="color-input"
                      @input="validateColor"
                    />
                    <view
                      class="color-preview"
                      :style="{ backgroundColor: config.styleConfig.theme.secondary_color }"
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
                      v-model="config.styleConfig.theme.accent_color"
                      class="color-input"
                      @input="validateColor"
                    />
                    <view
                      class="color-preview"
                      :style="{ backgroundColor: config.styleConfig.theme.accent_color }"
                    />
                  </view>
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
                  v-model="config.styleConfig.theme.font_family"
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
                      v-model="config.styleConfig.font_sizes.h1"
                      class="size-input"
                      min="12"
                      max="72"
                    />
                    <text class="size-unit">px</text>
                    <view class="size-slider">
                      <slider
                        :value="config.styleConfig.font_sizes.h1"
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
                      v-model="config.styleConfig.font_sizes.body"
                      class="size-input"
                      min="10"
                      max="36"
                    />
                    <text class="size-unit">px</text>
                    <view class="size-slider">
                      <slider
                        :value="config.styleConfig.font_sizes.body"
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
                    v-model="config.styleConfig.spacing.section_margin"
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
                    v-model="config.styleConfig.spacing.padding"
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
                    v-model="config.styleConfig.spacing.line_height"
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
          <view class="preview-container">
            <web-view
              v-if="previewUrl"
              :src="previewUrl"
              class="preview-webview"
            ></web-view>
            <view v-else class="preview-placeholder flex-center">
              <view class="placeholder-content">
                <text class="placeholder-icon">📄</text>
                <text class="placeholder-title">模板预览</text>
                <text class="placeholder-desc">配置完成后点击预览按钮查看效果</text>
                <button @click="previewTemplate" class="placeholder-btn">
                  开始预览
                </button>
              </view>
            </view>
          </view>

          <view class="preview-info">
            <text class="info-title">模板信息</text>
            <view class="info-items">
              <view class="info-item">
                <text class="info-label">名称:</text>
                <text class="info-value">{{ config.templateName || "未命名" }}</text>
              </view>
              <view class="info-item">
                <text class="info-label">分类:</text>
                <text class="info-value">{{ config.category || "未选择" }}</text>
              </view>
              <view class="info-item">
                <text class="info-label">布局:</text>
                <text class="info-value">{{ layoutTypeLabel }}</text>
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
import { computed, onMounted, reactive, ref } from "vue";

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

// 接口定义
interface ConfigTab {
  id: string;
  label: string;
  icon?: string;
}

interface LayoutColumn {
  width: number;
}

interface LayoutConfig {
  type: string;
  columns: {
    left: LayoutColumn
    right: LayoutColumn
  };
  sections: Array<{
    name: string
    fragment: string
  }>;
}

interface StyleConfig {
  theme: {
    primary_color: string
    secondary_color: string
    accent_color: string
    font_family: string
  };
  font_sizes: {
    h1: number
    body: number
  };
  spacing: {
    section_margin: string
    padding: string
    line_height: string
  };
}

interface TemplateConfig {
  templateCode: string;
  templateName: string;
  category: string;
  style: string;
  description: string;
  layoutConfig: LayoutConfig;
  styleConfig: StyleConfig;
}

interface Fragment {
  code: string;
  name: string;
  icon?: string;
}

interface DeviceOption {
  value: string;
  name: string;
  icon: string;
}

interface LayoutType {
  value: string;
  label: string;
  icon: string;
}

// 响应式数据
const isEditMode = ref(false);
const activeTab = ref("basic");
const previewDevice = ref("desktop");
const previewUrl = ref("");
const categoryIndex = ref(0);
const styleIndex = ref(0);
const layoutTypeIndex = ref(0);
const scrollTop = ref(0);
const draggingSection = ref<number | null>(null);
const dragStartY = ref(0);
const dragOffsetY = ref(0);

// 配置数据
const config = reactive<TemplateConfig>({
  templateCode: "",
  templateName: "",
  category: "resume",
  style: "classic",
  description: "",
  layoutConfig: {
    type: "single-column",
    columns: {
      left: { width: 40 },
      right: { width: 60 },
    },
    sections: [],
  },
  styleConfig: {
    theme: {
      primary_color: "#d4af37",
      secondary_color: "#f9f3e3",
      accent_color: "#f7ef8a",
      font_family: "'Microsoft YaHei', 'PingFang SC', sans-serif",
    },
    font_sizes: {
      h1: 24,
      body: 14,
    },
    spacing: {
      section_margin: "20px",
      padding: "15px",
      line_height: "1.5",
    },
  },
});

// 选项数据
const configTabs: ConfigTab[] = [
  { id: "basic", label: "基础配置", icon: "⚙️" },
  { id: "layout", label: "布局配置", icon: "📐" },
  { id: "style", label: "样式配置", icon: "🎨" },
];

const categoryOptions = ["简历", "报告", "邀请函", "证书", "其他"];
const styleOptions = ["经典", "国风", "科技", "时尚", "简约", "现代", "创意"];

const layoutTypes: LayoutType[] = [
  { value: "single-column", label: "单栏", icon: "single" },
  { value: "two-column", label: "双栏", icon: "double" },
  { value: "three-column", label: "三栏", icon: "triple" },
  { value: "creative", label: "创意", icon: "creative" },
];

const deviceOptions: DeviceOption[] = [
  { value: "desktop", name: "桌面", icon: "🖥️" },
  { value: "tablet", name: "平板", icon: "📱" },
  { value: "mobile", name: "手机", icon: "📲" },
];

// 片段数据
const availableFragments: Fragment[] = [
  { code: "header", name: "页眉", icon: "📄" },
  { code: "personal_info", name: "个人信息", icon: "👤" },
  { code: "education", name: "教育背景", icon: "🎓" },
  { code: "work_experience", name: "工作经历", icon: "💼" },
  { code: "skills", name: "技能", icon: "⚡" },
  { code: "projects", name: "项目经验", icon: "📋" },
  { code: "footer", name: "页脚", icon: "📝" },
];

// 计算属性
const fragmentOptions = computed(() => availableFragments.map(f => f.name));
const layoutTypeLabel = computed(() => {
  const type = layoutTypes.find(t => t.value === config.layoutConfig.type);
  return type ? type.label : "单栏";
});

// 方法
const previewTemplate = () => {
  if (!config.templateName) {
    uni.showToast({
      title: "请先填写模板名称",
      icon: "none",
    });
    return;
  }

  previewUrl.value = TemplateAPI.getPreview(config.templateCode);
  uni.showToast({
    title: "正在生成预览...",
    icon: "loading",
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
  if (!config.templateCode) {
    uni.showToast({
      title: "请填写模板编码",
      icon: "none",
    });
    return false;
  }

  if (!config.templateName) {
    uni.showToast({
      title: "请填写模板名称",
      icon: "none",
    });
    return false;
  }

  return true;
};

const refreshPreview = () => {
  if (!previewUrl.value) {
    previewTemplate();
  } else {
    previewUrl.value = `${previewUrl.value}&refresh=${Date.now()}`;
    uni.showToast({
      title: "刷新成功",
      icon: "success",
    });
  }
};

const onCategoryChange = (e: any) => {
  const index = e.detail.value;
  categoryIndex.value = index;
  config.category = ["resume", "report", "invitation", "certificate", "other"][index];
};

const onStyleChange = (e: any) => {
  const index = e.detail.value;
  styleIndex.value = index;
  config.style = ["classic", "chinese", "tech", "fashion", "minimal", "modern", "creative"][index];
};

const getColumnWidth = (side: "left" | "right") => {
  return `${config.layoutConfig.columns[side].width}%`;
};

const onColumnWidthChanging = (e: any) => {
  const value = e.detail.value;
  const side = e.currentTarget.dataset.side;
  if (side === "left") {
    config.layoutConfig.columns.left.width = value;
    config.layoutConfig.columns.right.width = 100 - value;
  } else {
    config.layoutConfig.columns.right.width = value;
    config.layoutConfig.columns.left.width = 100 - value;
  }
};

const addSection = () => {
  config.layoutConfig.sections.push({
    name: `区块${config.layoutConfig.sections.length + 1}`,
    fragment: "",
  });
};

const removeSection = (index: number) => {
  uni.showModal({
    title: "删除区块",
    content: "确定要删除这个区块吗？",
    confirmColor: dangerColor,
    success: (res) => {
      if (res.confirm) {
        config.layoutConfig.sections.splice(index, 1);
        uni.showToast({
          title: "删除成功",
          icon: "success",
        });
      }
    },
  });
};

const getFragmentIndex = (fragmentCode: string) => {
  return availableFragments.findIndex(f => f.code === fragmentCode);
};

const getFragmentLabel = (fragmentCode: string) => {
  const fragment = availableFragments.find(f => f.code === fragmentCode);
  return fragment ? fragment.name : "";
};

const onFragmentChange = (sectionIndex: number, e: any) => {
  const fragmentIndex = e.detail.value;
  config.layoutConfig.sections[sectionIndex].fragment = availableFragments[fragmentIndex].code;
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

const onSectionTouchStart = (index: number) => {
  draggingSection.value = index;
  dragStartY.value = 0; // 实际开发中需要通过事件获取触摸位置
};

const onSectionTouchMove = () => {
  // 拖拽移动处理
};

const onSectionTouchEnd = () => {
  draggingSection.value = null;
  dragOffsetY.value = 0;
};

const sectionTransform = (index: number) => {
  if (draggingSection.value === index) {
    return `translateY(${dragOffsetY.value}px)`;
  }
  return "";
};

const onFontSizeChanging = (e: any) => {
  const value = e.detail.value;
  const type = e.currentTarget.dataset.type;
  if (type === "h1") {
    config.styleConfig.font_sizes.h1 = value;
  } else {
    config.styleConfig.font_sizes.body = value;
  }
};

const validateColor = (e: any) => {
  const value = e.detail.value;
  const colorRegex = /^#([0-9A-F]{3}){1,2}$/i;
  if (!colorRegex.test(value)) {
    // 可以添加颜色验证提示
  }
};

const resetColors = () => {
  config.styleConfig.theme.primary_color = "#d4af37";
  config.styleConfig.theme.secondary_color = "#f9f3e3";
  config.styleConfig.theme.accent_color = "#f7ef8a";
  uni.showToast({
    title: "颜色已重置",
    icon: "success",
  });
};

const onSectionInputFocus = (index: number) => {
  // 区块输入框聚焦处理
};

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
  }
});

const loadTemplateData = (id: string) => {
  // 实际应用中这里应该从API加载数据
  console.log("加载模板数据:", id);
  // 模拟加载数据
  setTimeout(() => {
    config.templateCode = "resume_tech";
    config.templateName = "科技风格简历";
    config.category = "resume";
    config.style = "tech";
    config.description = "专业的科技行业简历模板";
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
  .section-header {
    margin-bottom: $margin-base;
    padding-bottom: $padding-small;
    border-bottom: 1px solid $border-color-lighter;

    .section-title {
      display: block;
      font-size: $font-size-large;
      font-weight: $font-weight-semibold;
      color: $text-primary;
      margin-bottom: 4rpx;
    }

    .section-desc {
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
.section-management {
  .section-list {
    margin-bottom: $margin-base;
  }

  .section-item {
    margin-bottom: $margin-small;
    background: $background-color-white;
    border: 2rpx solid $border-color-lighter;
    border-radius: $border-radius;
    overflow: hidden;
    transition: all $transition-fast $ease-in-out;

    &:hover {
      border-color: $primary-color;
      box-shadow: $box-shadow-light;
    }

    .section-item-main {
      display: flex;
      align-items: center;
      padding: $padding-small;
      gap: $padding-small;

      .drag-handle {
        padding: 0 12rpx;
        cursor: move;

        .drag-icon {
          color: $text-secondary;
          font-size: $font-size-base;
          opacity: 0.5;
        }
      }

      .section-input {
        flex: 1;
        min-width: 0;
        padding: 12rpx;
        border: 1px solid $border-color-lighter;
        border-radius: var(--border-radius-small);
        font-size: $font-size-base;
        background: $background-color-white;
      }

      .fragment-picker {
        flex: 2;
        min-width: 0;
      }

      .section-remove {
        padding: 8rpx 12rpx;
        background: $danger-color;
        color: white;
        border: none;
        border-radius: var(--border-radius-small);
        font-size: $font-size-small;

        .remove-icon {
          font-weight: $font-weight-bold;
        }
      }
    }
  }

  .add-section-btn {
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

/* 响应式调整 */
@media (max-width: 768px) {
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
}
</style>