<template>
  <view class="template-detail-container">
    <!-- 模板基本信息 -->
    <view class="template-header">
      <view class="header-main">
        <view class="template-badge" :style="{ backgroundColor: getAvatarColor(template?.id || 0) }">
          <text class="badge-text">{{ getTemplateInitial(template?.name || "") }}</text>
        </view>
        <view class="template-info">
          <text class="template-name">{{ template?.name || "未知模板" }}</text>
          <text class="template-description">{{ template?.description || "暂无描述" }}</text>
          <view class="template-meta">
            <view class="meta-item">
              <text class="meta-label">版本：</text>
              <text class="meta-value">{{ template?.version || "1.0.0" }}</text>
            </view>
            <view class="meta-item">
              <text class="meta-label">状态：</text>
              <view class="status-tag" :class="template?.isActive ? 'active' : 'inactive'">
                <text class="status-text">{{ template?.isActive ? "已启用" : "未启用" }}</text>
              </view>
            </view>
            <view class="meta-item">
              <text class="meta-label">编码：</text>
              <text class="meta-value">{{ template?.code || "未设置" }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 操作按钮 -->
    <view class="action-buttons">
      <button class="action-btn primary" @click="handleUseTemplate">
        <text class="btn-icon">🎨</text>
        <text class="btn-text">使用此模板</text>
      </button>
      <button class="action-btn secondary" @click="handleShare">
        <text class="btn-icon">💯</text>
        <text class="btn-text">分享</text>
      </button>
    </view>

    <!-- 模板预览 -->
    <view class="preview-section">
      <view class="section-header">
        <text class="section-title">模板预览</text>
        <view class="device-switch">
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
      </view>
      <view class="preview-card">
        <!-- 使用统一的预览组件 -->
        <view class="preview-content" :class="previewDevice">
          <template-preview
            :key="previewKey"
            :components="template.components || []"
            :layout="template.globalLayout"
            :global-style="template.globalStyle"
            :device="previewDevice"
            class="template-preview-container"
          />
        </view>

        <!-- 预览信息 -->
        <view class="preview-info">
          <view class="info-item">
            <text class="info-label">布局类型：</text>
            <text class="info-value">{{ getLayoutTypeLabel(template?.globalLayout?.type) }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">颜色主题：</text>
            <view class="color-theme-preview">
              <view class="color-dot" :style="{ backgroundColor: template?.globalStyle?.primaryColor || '#d4af37' }"></view>
              <view class="color-dot" :style="{ backgroundColor: template?.globalStyle?.secondaryColor || '#f9f3e3' }"></view>
              <view class="color-dot" :style="{ backgroundColor: template?.globalStyle?.accentColor || '#f7ef8a' }"></view>
            </view>
          </view>
          <view class="info-item">
            <text class="info-label">字体：</text>
            <text class="info-value">{{ template?.globalStyle?.fontFamily || '默认字体' }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 模板配置详情 -->
    <view class="config-section">
      <view class="section-header">
        <text class="section-title">配置详情</text>
      </view>

      <!-- 全局样式配置 -->
      <view class="config-card" v-if="template?.globalStyle">
        <view class="config-header">
          <text class="config-title">全局样式</text>
        </view>
        <view class="config-content">
          <view class="config-item">
            <text class="config-label">主色调</text>
            <view class="config-value">
              <view class="color-value" :style="{ backgroundColor: template.globalStyle.primaryColor }">
                {{ template.globalStyle.primaryColor || '#d4af37' }}
              </view>
            </view>
          </view>
          <view class="config-item">
            <text class="config-label">辅色调</text>
            <view class="config-value">
              <view class="color-value" :style="{ backgroundColor: template.globalStyle.secondaryColor }">
                {{ template.globalStyle.secondaryColor || '#f9f3e3' }}
              </view>
            </view>
          </view>
          <view class="config-item">
            <text class="config-label">强调色</text>
            <view class="config-value">
              <view class="color-value" :style="{ backgroundColor: template.globalStyle.accentColor }">
                {{ template.globalStyle.accentColor || '#f7ef8a' }}
              </view>
            </view>
          </view>
          <view class="config-item">
            <text class="config-label">字体家族</text>
            <text class="config-value">{{ template.globalStyle.fontFamily || "'Microsoft YaHei', 'PingFang SC', sans-serif" }}</text>
          </view>
          <view class="config-item">
            <text class="config-label">标题字体</text>
            <text class="config-value">{{ template.globalStyle.fontSizes?.h1 || '24' }}px</text>
          </view>
          <view class="config-item">
            <text class="config-label">正文字体</text>
            <text class="config-value">{{ template.globalStyle.fontSizes?.body || '14' }}px</text>
          </view>
        </view>
      </view>

      <!-- 布局配置 -->
      <view class="config-card" v-if="template?.globalLayout">
        <view class="config-header">
          <text class="config-title">布局配置</text>
        </view>
        <view class="config-content">
          <view class="config-item">
            <text class="config-label">布局类型</text>
            <text class="config-value">{{ getLayoutTypeLabel(template.globalLayout.type) }}</text>
          </view>
          <view v-if="template.globalLayout.type === 'two-column'" class="config-item">
            <text class="config-label">左侧宽度</text>
            <text class="config-value">{{ template.globalLayout.columns?.left || 40 }}%</text>
          </view>
          <view v-if="template.globalLayout.type === 'two-column'" class="config-item">
            <text class="config-label">右侧宽度</text>
            <text class="config-value">{{ template.globalLayout.columns?.right || 60 }}%</text>
          </view>
          <view class="config-item">
            <text class="config-label">页面方向</text>
            <text class="config-value">{{ template.globalLayout.orientation === 'portrait' ? '纵向' : '横向' }}</text>
          </view>
        </view>
      </view>

      <!-- 组件列表 -->
      <view class="config-card">
        <view class="config-header">
          <text class="config-title">启用组件</text>
          <text class="config-count">{{ template?.components?.length || 0 }}个</text>
        </view>
        <view class="components-list" v-if="template?.components?.length > 0">
          <view class="component-item" v-for="(component, index) in template.components" :key="component.id || index">
            <view class="component-index">
              <text class="index-text">{{ index + 1 }}</text>
            </view>
            <view class="component-info">
              <text class="component-name">{{ component.name || '未命名区块' }}</text>
              <text class="component-type">{{ getComponentTypeLabel(component.fragment) }}</text>
            </view>
            <view class="component-actions">
              <text class="component-action" @click.stop="viewComponentDetail(component)">查看</text>
            </view>
          </view>
        </view>
        <view v-else class="empty-components">
          <text class="empty-text">暂无组件配置</text>
        </view>
      </view>

      <!-- 时间信息 -->
      <view class="config-card" v-if="template?.createdAt || template?.updatedAt">
        <view class="config-header">
          <text class="config-title">时间信息</text>
        </view>
        <view class="config-content">
          <view class="config-item" v-if="template?.createdAt">
            <text class="config-label">创建时间</text>
            <text class="config-value">{{ formatDateTime(template.createdAt) }}</text>
          </view>
          <view class="config-item" v-if="template?.updatedAt">
            <text class="config-label">更新时间</text>
            <text class="config-value">{{ formatDateTime(template.updatedAt) }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-actions">
      <button class="bottom-btn delete" @click="handleDelete" v-if="!template?.deleted">
        <text class="btn-text">删除模板</text>
      </button>
      <button class="bottom-btn toggle-status" @click="handleToggleStatus">
        <text class="btn-text">{{ template?.isActive ? "停用模板" : "启用模板" }}</text>
      </button>
      <button class="bottom-btn primary" @click="handleEdit">
        <text class="btn-text">编辑模板</text>
      </button>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-overlay">
      <view class="loading-content">
        <view class="loading-spinner"></view>
        <text class="loading-text">加载中...</text>
      </view>
    </view>
  </view>
</template>

<script lang="ts">
import { computed, defineComponent, ref, onMounted } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import TemplatePreview from "@/components/template/TemplatePreview.vue";
import type { TemplateResult } from "@/types/template";
import { useTemplate } from "@/composables/useTemplate";

interface DeviceOption {
  value: string;
  name: string;
  icon: string;
}

export default defineComponent({
  name: "TemplateDetail",

  components: {
    TemplatePreview
  },

  setup() {
    const template = ref<TemplateResult | null>(null);
    const loading = ref(true);
    const previewKey = ref(0);
    const previewDevice = ref("desktop");
    const { fetchTemplateDetail, setCurrentTemplate } = useTemplate();

    // 设备选项
    const deviceOptions: DeviceOption[] = [
      { value: "desktop", name: "桌面", icon: "🖥️" },
      { value: "tablet", name: "平板", icon: "📱" },
      { value: "mobile", name: "手机", icon: "📲" },
    ];

    // 模拟加载模板数据
    const loadTemplateDetail = async (id: string) => {
      try {
        loading.value = true;
        const result = await fetchTemplateDetail(id);
        if (result) {
          template.value = result;
        } else {
          uni.showToast({
            title: "模板不存在",
            icon: "error",
          });
          setTimeout(() => {
            uni.navigateBack();
          }, 1500);
        }
        previewKey.value += 1;
      } catch (error) {
        console.error("加载模板详情失败:", error);
        uni.showToast({
          title: "加载失败",
          icon: "error",
        });
      } finally {
        loading.value = false;
      }
    };

    // 导航处理
    const handleBack = () => {
      uni.navigateBack();
    };

    const handleEdit = () => {
      if (!template.value) return;
      uni.navigateTo({
        url: `/pages/template/create?id=${template.value.id}`,
      });
    };

    const handleShare = () => {
      if (!template.value) return;
      uni.share({
        title: template.value.name,
        content: template.value.description || "这是一个优秀的简历模板",
        href: `https://example.com/template/${template.value.id}`,
        success: () => {
          uni.showToast({ title: "分享成功" });
        },
      });
    };

    // 模板操作
    const handleUseTemplate = () => {
      if (!template.value) return;
      uni.showToast({
        title: "开始使用此模板",
        icon: "success",
      });
      // 这里应该导航到使用模板创建简历的页面
      uni.navigateTo({
        url: `/pages/resumes/create?templateId=${template.value.id}`,
      });
    };

    const handleDelete = () => {
      if (!template.value) return;

      uni.showModal({
        title: "确认删除",
        content: "确定要删除这个模板吗？删除后无法恢复。",
        success: (res) => {
          if (res.confirm) {
            uni.showLoading({ title: "删除中..." });
            setTimeout(() => {
              uni.hideLoading();
              uni.showToast({
                title: "删除成功",
                icon: "success",
              });
              setTimeout(() => {
                uni.navigateBack();
              }, 1500);
            }, 1500);
          }
        },
      });
    };

    const handleToggleStatus = () => {
      if (!template.value) return;

      const newStatus = !template.value.isActive;
      const action = newStatus ? "启用" : "停用";

      uni.showModal({
        title: `确认${action}`,
        content: `确定要${action}这个模板吗？`,
        success: (res) => {
          if (res.confirm) {
            template.value!.isActive = newStatus;
            previewKey.value += 1;
            uni.showToast({
              title: `${action}成功`,
              icon: "success",
            });
          }
        },
      });
    };

    const viewComponentDetail = (component: any) => {
      uni.showModal({
        title: component.name,
        content: `组件类型: ${getComponentTypeLabel(component.fragment)}\n组件代码: ${component.fragment}`,
        showCancel: false,
        confirmText: "关闭",
      });
    };

    // 工具函数
    const getTemplateInitial = (name: string) => {
      if (!name) return "R";
      return name.charAt(0).toUpperCase();
    };

    const getAvatarColor = (id: number) => {
      const colors = [
        "#3498db", "#2ecc71", "#e74c3c", "#f39c12",
        "#9b59b6", "#1abc9c", "#d35400", "#c0392b",
      ];
      return colors[id % colors.length];
    };

    const getLayoutTypeLabel = (type?: string) => {
      const typeMap: Record<string, string> = {
        "single-column": "单栏",
        "two-column": "双栏",
        "three-column": "三栏",
        "creative": "创意布局"
      };
      return type ? (typeMap[type] || type) : "单栏";
    };

    const getComponentTypeLabel = (fragment?: string) => {
      const typeMap: Record<string, string> = {
        "UserBasicInfo": "基本信息",
        "EducationExperience": "教育背景",
        "WorkExperience": "工作经历",
        "Skills": "技能专长",
        "ProjectExperience": "项目经验",
        "SelfEvaluation": "自我评价",
        "JobIntention": "求职意向"
      };
      return fragment ? (typeMap[fragment] || fragment) : "未知类型";
    };

    const formatDateTime = (dateStr?: string) => {
      if (!dateStr) return "未知";
      try {
        const date = new Date(dateStr);
        return date.toLocaleString("zh-CN", {
          year: "numeric",
          month: "2-digit",
          day: "2-digit",
          hour: "2-digit",
          minute: "2-digit",
        });
      } catch {
        return "未知";
      }
    };

    // 生命周期
    onLoad((options) => {
      if (options.id) {
        loadTemplateDetail(options.id);
      }
    });

    return {
      // 状态
      template,
      loading,
      previewDevice,
      deviceOptions,
      previewKey,

      // 方法
      handleBack,
      handleEdit,
      handleShare,
      handleUseTemplate,
      handleDelete,
      handleToggleStatus,
      viewComponentDetail,

      // 工具函数
      getTemplateInitial,
      getAvatarColor,
      getLayoutTypeLabel,
      getComponentTypeLabel,
      formatDateTime,
    };
  },
});
</script>

<style scoped lang="scss">

.template-detail-container {
  background-color: $uni-bg-color-grey;
  min-height: 100vh;
  padding-bottom: 140rpx;
}

/* 模板头部 */
.template-header {
  background: $uni-bg-color;
  padding: $uni-spacing-row-base;
  border-bottom: 1rpx solid $border-color-extra-light;
}

.header-main {
  display: flex;
  align-items: center;
  gap: $uni-spacing-col-base;
}

.template-badge {
  width: 100rpx;
  height: 100rpx;
  border-radius: $uni-border-radius-lg;
  @extend .flex-center;
  flex-shrink: 0;
  box-shadow: $box-shadow;
}

.badge-text {
  font-size: $font-size-extra-large;
  color: $uni-text-color-inverse;
  font-weight: $font-weight-bold;
}

.template-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: $uni-spacing-col-sm;
}

.template-name {
  font-size: $uni-font-size-lg;
  color: $text-primary;
  font-weight: $font-weight-bold;
  line-height: 1.4;
}

.template-description {
  font-size: $uni-font-size-base;
  color: $text-regular;
  line-height: 1.5;
}

.template-meta {
  display: flex;
  flex-wrap: wrap;
  gap: $uni-spacing-col-base;
  align-items: center;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: math.div($uni-spacing-col-sm, 2);
}

.meta-label {
  font-size: $uni-font-size-sm;
  color: $text-secondary;
}

.meta-value {
  font-size: $uni-font-size-sm;
  color: $text-regular;
  font-weight: $font-weight-medium;
}

.status-tag {
  padding: math.div($uni-spacing-col-sm, 2) $uni-spacing-col-sm;
  border-radius: $uni-border-radius-sm;
  font-size: $uni-font-size-sm;
  font-weight: $font-weight-medium;

  &.active {
    background: $success-bg;
    color: $success-color;
    border: 1rpx solid $success-border;
  }

  &.inactive {
    background: $background-color;
    color: $uni-text-color-grey;
    border: 1rpx solid $border-color-light;
  }
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: $uni-spacing-col-base;
  padding: $uni-spacing-row-base;
  background: $uni-bg-color;
  border-bottom: 1rpx solid $border-color-extra-light;
}

.action-btn {
  flex: 1;
  height: $button-height;
  border-radius: $uni-border-radius-lg;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $uni-spacing-col-sm;
  font-size: $uni-font-size-base;
  font-weight: $font-weight-medium;
  transition: all $transition-fast;

  &.primary {
    background: $primary-color;
    color: $uni-bg-color;
    border: none;

    &:active {
      background: color.adjust($primary-color, $lightness: -10%);
      opacity: 0.9;
    }
  }

  &.secondary {
    background: $uni-bg-color;
    color: $text-regular;
    border: 1rpx solid $border-color-light;

    &:active {
      background: $background-color;
      border-color: $border-color;
    }
  }
}

.btn-icon {
  font-size: $uni-font-size-lg;
}

.btn-text {
  font-weight: $font-weight-medium;
}

/* 模板预览 */
.preview-section {
  padding: $uni-spacing-row-base;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $uni-spacing-col-base;
}

.section-title {
  font-size: $uni-font-size-lg;
  color: $text-primary;
  font-weight: $font-weight-bold;
}

.device-switch {
  .switch-buttons {
    display: flex;
    gap: 8rpx;

    .device-btn {
      padding: $device-btn-padding;
      border: 1rpx solid $border-color;
      border-radius: $uni-border-radius-sm;
      background: $uni-bg-color;
      font-size: $uni-font-size-sm;
      display: flex;
      align-items: center;
      gap: 4rpx;

      &.active {
        background: $primary-color;
        color: $uni-bg-color;
        border-color: $primary-color;
      }

      .device-icon {
        font-size: $uni-font-size-base;
      }
    }
  }
}

.preview-card {
  background: $uni-bg-color;
  border-radius: $border-radius-large;
  padding: $uni-spacing-row-base;
  box-shadow: $card-shadow;
}

.preview-content {
  &.desktop .template-preview-container {
    max-width: 100%;
    height: $preview-min-height;
  }

  &.tablet .template-preview-container {
    max-width: 768rpx;
    height: 800rpx;
    margin: 0 auto;
    border-radius: $border-radius-large;
  }

  &.mobile .template-preview-container {
    max-width: 375rpx;
    height: 800rpx;
    margin: 0 auto;
    border-radius: $border-radius-large;
    box-shadow: $box-shadow-dark;
  }
}

.template-preview-container {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  background: $uni-bg-color;
  border: 1rpx solid $border-color-lighter;
  border-radius: $uni-border-radius-lg;
}

.preview-info {
  margin-top: $margin-base;
  padding: $padding-base;
  background: $background-color;
  border-radius: $border-radius;
  display: flex;
  flex-direction: column;
  gap: $uni-spacing-col-sm;
}

.info-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.info-label {
  font-size: $uni-font-size-base;
  color: $text-secondary;
}

.info-value {
  font-size: $uni-font-size-base;
  color: $text-primary;
  font-weight: $font-weight-medium;
}

.color-theme-preview {
  display: flex;
  gap: $uni-spacing-col-sm;

  .color-dot {
    width: 32rpx;
    height: 32rpx;
    border-radius: $uni-border-radius-circle;
    border: 1rpx solid rgba(0, 0, 0, 0.1);
  }
}

/* 配置详情 */
.config-section {
  padding: 0 $uni-spacing-row-base $uni-spacing-row-base;
}

.config-card {
  background: $uni-bg-color;
  border-radius: $border-radius-large;
  padding: $padding-base;
  margin-bottom: $margin-base;
  box-shadow: $card-shadow;
}

.config-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $margin-base;
  padding-bottom: $padding-small;
  border-bottom: 1rpx solid $border-color-extra-light;
}

.config-title {
  font-size: $uni-font-size-lg;
  color: $text-primary;
  font-weight: $font-weight-medium;
}

.config-count {
  font-size: $uni-font-size-sm;
  color: $text-secondary;
  background: $background-color;
  padding: math.div($uni-spacing-col-sm, 2) $uni-spacing-col-sm;
  border-radius: $uni-border-radius-sm;
}

.config-content {
  display: flex;
  flex-direction: column;
  gap: $uni-spacing-col-sm;
}

.config-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: math.div($uni-spacing-col-sm, 2) 0;
}

.config-label {
  font-size: $uni-font-size-base;
  color: $text-secondary;
  flex-shrink: 0;
}

.config-value {
  font-size: $uni-font-size-base;
  color: $text-primary;
  font-weight: $font-weight-medium;
  text-align: right;
  margin-left: $uni-spacing-col-sm;
}

.color-value {
  padding: math.div($uni-spacing-col-sm, 2) $uni-spacing-col-sm;
  border-radius: $uni-border-radius-sm;
  color: $uni-text-color-inverse;
  text-shadow: 0 1rpx 2rpx rgba(0, 0, 0, 0.3);
  font-family: monospace;
  font-size: $uni-font-size-sm;
  min-width: 120rpx;
  text-align: center;
}

.components-list {
  display: flex;
  flex-direction: column;
  gap: $uni-spacing-col-sm;
}

.component-item {
  display: flex;
  align-items: center;
  padding: $padding-small;
  background: $background-color;
  border-radius: $uni-border-radius-lg;
  border: 1rpx solid $border-color-light;
  transition: all $transition-fast;

  &:active {
    background: color.adjust($background-color, $lightness: -5%);
  }
}

.component-index {
  width: $avatar-size;
  height: $avatar-size;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $avatar-bg-color;
  border-radius: $uni-border-radius-circle;
  margin-right: $uni-spacing-col-base;
  flex-shrink: 0;

  .index-text {
    font-size: $uni-font-size-base;
    color: $text-secondary;
    font-weight: $font-weight-medium;
  }
}

.component-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: math.div($uni-spacing-col-sm, 2);
}

.component-name {
  font-size: $uni-font-size-base;
  color: $text-primary;
  font-weight: $font-weight-medium;
}

.component-type {
  font-size: $uni-font-size-sm;
  color: $text-secondary;
}

.component-actions {
  flex-shrink: 0;
}

.component-action {
  font-size: $uni-font-size-sm;
  color: $primary-color;
  font-weight: $font-weight-medium;
  cursor: pointer;
  padding: math.div($uni-spacing-col-sm, 2) $uni-spacing-col-sm;
  background: $primary-light;
  border-radius: $uni-border-radius-sm;

  &:active {
    background: color.adjust($primary-light, $lightness: -5%);
  }
}

.empty-components {
  padding: $padding-base 0;
  text-align: center;

  .empty-text {
    font-size: $uni-font-size-base;
    color: $text-placeholder;
  }
}

/* 底部操作栏 */
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: $uni-bg-color;
  padding: $uni-spacing-col-sm $uni-spacing-row-base;
  display: flex;
  gap: $uni-spacing-col-base;
  border-top: 1rpx solid $border-color-light;
  box-shadow: $box-shadow-dark;
  z-index: $z-index-dropdown;
}

.bottom-btn {
  flex: 1;
  height: $button-height;
  border-radius: $uni-border-radius-lg;
  font-size: $uni-font-size-base;
  font-weight: $font-weight-medium;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all $transition-fast;

  &.delete {
    background: $danger-bg;
    color: $danger-color;
    border: 1rpx solid $danger-border;

    &:active {
      background: color.adjust($danger-bg, $lightness: -10%);
    }
  }

  &.toggle-status {
    background: $info-bg;
    color: $info-color;
    border: 1rpx solid $info-border;

    &:active {
      background: color.adjust($info-bg, $lightness: -10%);
    }
  }

  &.primary {
    background: $primary-color;
    color: $uni-bg-color;
    border: none;

    &:active {
      background: color.adjust($primary-color, $lightness: -10%);
    }
  }
}

/* 加载状态 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba($uni-bg-color, 0.8);
  @extend .flex-center;
  z-index: $z-index-modal;
}

.loading-content {
  background: $uni-bg-color;
  border-radius: $border-radius-large;
  padding: $uni-spacing-row-base;
  box-shadow: $box-shadow-dark;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $uni-spacing-col-sm;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid $border-color-light;
  border-top-color: $primary-color;
  border-radius: $uni-border-radius-circle;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: $uni-font-size-base;
  color: $text-regular;
}

/* 响应式调整 */
@media (max-width: $screen-md) {
  .action-buttons {
    flex-direction: column;

    .action-btn {
      width: 100%;
    }
  }

  .device-switch .switch-buttons {
    .device-btn .device-name {
      display: none;
    }
  }

  .bottom-actions {
    flex-direction: column;

    .bottom-btn {
      width: 100%;
    }
  }
}
</style>