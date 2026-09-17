<template>
  <view class="template-detail-container page-container">
    <!-- 模板基本信息 -->
    <view class="template-header card-container">
      <view class="header-main flex-between">
        <view class="template-badge" :style="{ backgroundColor: getAvatarColor(template?.id || 0) }">
          <text class="badge-text">{{ getTemplateInitial(template?.name || "") }}</text>
        </view>
        <view class="template-info">
          <text class="template-name text-truncate">{{ template?.name || "未知模板" }}</text>
          <text class="template-description text-multi-truncate">{{ template?.description || "暂无描述" }}</text>
          <view class="template-meta flex-between">
            <view class="meta-item flex-center">
              <text class="meta-label">编码：</text>
              <text class="meta-value text-ellipsis">{{ template?.code || "未设置" }}</text>
            </view>
            <view class="meta-item">
              <view class="status-tag" :class="template?.isActive ? 'active' : 'inactive'">
                <text class="status-text">{{ template?.isActive ? "已启用" : "未启用" }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 模板预览 -->
    <view class="preview-section section-container">
      <view class="section-header flex-between">
        <text class="section-title">模板预览</text>
        <view class="device-switch flex-center">
          <button
            v-for="device in deviceOptions"
            :key="device.value"
            :class="['device-btn', { active: previewDevice === device.value }]"
            @click="handleDeviceSwitch(device.value)"
          >
            <text class="device-icon">{{ device.icon }}</text>
            <text class="device-name">{{ device.name }}</text>
          </button>
        </view>
      </view>
      <view class="preview-card" :class="previewDevice">
        <template-preview
          :key="previewKey"
          :components="templateComponents"
          :global-layout="template?.globalLayout"
          :global-style="template?.globalStyle"
          :device="previewDevice"
        />
      </view>

      <!-- 预览信息 -->
      <view class="preview-info flex-between">
        <view class="info-item">
          <text class="info-label">布局类型：</text>
          <text class="info-value">{{ getLayoutTypeLabel(template?.globalLayout?.type) }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">组件数量：</text>
          <text class="info-value">{{ templateComponents.length }}个</text>
        </view>
        <view class="info-item">
          <text class="info-label">创建时间：</text>
          <text class="info-value">{{ formatDateTime(template?.createdAt) }}</text>
        </view>
      </view>
    </view>

    <!-- 模板配置详情 -->
    <view class="config-section">
      <!-- 全局样式配置 -->
      <view class="config-card card-container" v-if="template?.globalStyle">
        <view class="config-header flex-between">
          <text class="config-title">全局样式</text>
        </view>
        <view class="config-content">
          <view class="config-row">
            <text class="config-label">颜色主题：</text>
            <view class="color-theme flex-between">
              <view
                class="color-item flex-center"
                v-for="color in colorItems"
                :key="color.label"
              >
                <view
                  class="color-dot"
                  :style="{ backgroundColor: color.value }"
                ></view>
                <text class="color-label">{{ color.label }}</text>
                <text class="color-value">{{ color.value }}</text>
              </view>
            </view>
          </view>
          <view class="config-row">
            <text class="config-label">字体设置：</text>
            <text class="config-value">{{ template.globalStyle.fontFamily || "默认字体" }}</text>
          </view>
          <view class="config-row" v-if="template.globalStyle.fontSizes">
            <text class="config-label">字体大小：</text>
            <view class="font-sizes flex-between">
              <text class="font-size-item">标题: {{ template.globalStyle.fontSizes.h1 || 24 }}px</text>
              <text class="font-size-item">正文: {{ template.globalStyle.fontSizes.body || 14 }}px</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 布局配置 -->
      <view class="config-card card-container" v-if="template?.globalLayout">
        <view class="config-header">
          <text class="config-title">布局配置</text>
        </view>
        <view class="config-content">
          <view class="config-row">
            <text class="config-label">布局类型：</text>
            <text class="config-value">{{ getLayoutTypeLabel(template.globalLayout.type) }}</text>
          </view>
          <view class="config-row" v-if="template.globalLayout.type === 'two-column'">
            <text class="config-label">栏位宽度：</text>
            <view class="column-widths flex-between">
              <text>左侧: {{ template.globalLayout.columns?.left || 40 }}%</text>
              <text>右侧: {{ template.globalLayout.columns?.right || 60 }}%</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 组件列表 -->
      <view class="config-card card-container">
        <view class="config-header flex-between">
          <text class="config-title">包含组件</text>
          <text class="config-count">{{ templateComponents.length }}个</text>
        </view>
        <view class="components-list">
          <view
            class="component-item flex-between"
            v-for="(component, index) in templateComponents"
            :key="component.componentId || index"
          >
            <view class="component-index flex-center">
              <text class="index-text">{{ index + 1 }}</text>
            </view>
            <view class="component-info">
              <text class="component-name text-truncate">{{ getComponentName(component) }}</text>
              <text class="component-desc text-multi-truncate">{{ getComponentDescription(component) }}</text>
            </view>
          </view>
        </view>
        <view v-if="templateComponents.length === 0" class="empty-state flex-center">
          <text class="empty-icon">📄</text>
          <text class="empty-text">暂无组件配置</text>
        </view>
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-actions flex-center">
      <button
        class="action-btn btn"
        :class="template?.collected ? 'btn-danger' : 'btn-secondary'"
        @click="handleToggleFavorite"
      >
        <text class="btn-text">
          {{ template?.collected ? '取消收藏' : '收藏模板' }}
        </text>
      </button>

      <button class="action-btn btn btn-primary" @click="handleUseTemplate">
        <text class="btn-text">使用此模板</text>
      </button>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-overlay flex-center">
      <view class="loading-content flex-center">
        <view class="loading-spinner"></view>
        <text class="loading-text">加载中...</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import TemplatePreview from "@/components/template/TemplatePreview.vue";
import { TemplateResult } from "@/types/template";
import { TemplateComponentResult } from "@/types/template-component";
import { COMPONENT_LIBRARY } from "@/constants/component";
import { DEVICE_OPTIONS, LAYOUT_TYPES } from "@/constants/template";
import { useTemplateStore } from "@/stores/template";
import TemplateAPI from "@/api/template";

// 响应式数据
const template = ref<TemplateResult | null>(null);
const loading = ref(true);
const previewKey = ref(0);
const previewDevice = ref("desktop");

// 使用 store
const templateStore = useTemplateStore();

// 设备选项
const deviceOptions = ref(DEVICE_OPTIONS);

// 计算属性
const templateComponents = computed(() => {
  if (!template.value?.components) return [];
  return template.value.components;
});

const colorItems = computed(() => {
  if (!template.value?.globalStyle) return [];
  const style = template.value.globalStyle;
  return [
    { label: "主色", value: style.primaryColor || "#d4af37" },
    { label: "辅色", value: style.secondaryColor || "#f9f3e3" },
    { label: "强调色", value: style.accentColor || "#f7ef8a" },
  ];
});

// 加载模板数据
const loadTemplateDetail = async (id: string) => {
  try {
    loading.value = true;
    const templateId = parseInt(id);
    if (isNaN(templateId)) {
      throw new Error("无效的模板ID");
    }

    // 使用 store 中的方法或直接调用 API
    const response = await TemplateAPI.getById(templateId);
    if (response) {
      template.value = response as TemplateResult;
      templateStore.setCurrentTemplate(template.value);
    } else {
      uni.showToast({
        title: "模板不存在",
        icon: "none",
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
      icon: "none",
    });
  } finally {
    loading.value = false;
  }
};

// 导航处理
const handleEdit = () => {
  if (!template.value) return;
  uni.navigateTo({
    url: `/pages/template/edit?id=${template.value.id}`,
  });
};

// 切换收藏状态
const handleToggleFavorite = async () => {
  if (!template.value) return;

  try {
    if (template.value.collected) {
      // 取消收藏
      await TemplateAPI.unfavorite(template.value.id);
      template.value.collected = false;

      uni.showToast({
        title: "取消收藏成功",
        icon: "success",
      });

      // 发送收藏状态更新事件，让收藏列表页面可以更新
      uni.$emit('template-favorite-updated', { id: template.value.id, collected: false });
    } else {
      // 添加收藏
      await TemplateAPI.favorite(template.value.id);
      template.value.collected = true;

      uni.showToast({
        title: "收藏成功",
        icon: "success",
      });

      // 发送收藏状态更新事件
      uni.$emit('template-favorite-updated', { id: template.value.id, collected: true });
    }
  } catch (error) {
    console.error("操作失败:", error);
    uni.showToast({
      title: "操作失败，请重试",
      icon: "error",
    });
  }
};

const handleUseTemplate = () => {
  if (!template.value) return;

  uni.navigateTo({
    url: `/pages/resumes/resumes?templateId=${template.value.id}`,
  });
};

const handleToggleStatus = async () => {
  if (!template.value) return;

  const newStatus = !template.value.isActive;
  const action = newStatus ? "启用" : "停用";

  uni.showModal({
    title: `确认${action}`,
    content: `确定要${action}这个模板吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          // 调用API更新状态
          // 注意：这里需要根据实际API进行调用
          // await TemplateAPI.updateStatus(template.value!.id, newStatus)

          // 更新本地状态
          template.value!.isActive = newStatus;
          previewKey.value += 1;

          uni.showToast({
            title: `${action}成功`,
            icon: "success",
          });
        } catch (error) {
          console.error(`${action}模板失败:`, error);
          uni.showToast({
            title: `${action}失败`,
            icon: "none",
          });
        }
      }
    },
  });
};

// 设备切换
const handleDeviceSwitch = (device: string) => {
  previewDevice.value = device;
  previewKey.value += 1;
};

// 工具函数
const getTemplateInitial = (name: string) => {
  if (!name) return "T";
  return name.charAt(0).toUpperCase();
};

const getAvatarColor = (id: number) => {
  const colors = [
    "#3498db",
    "#2ecc71",
    "#e74c3c",
    "#f39c12",
    "#9b59b6",
    "#1abc9c",
    "#d35400",
    "#c0392b",
  ];
  return colors[id % colors.length];
};

const getLayoutTypeLabel = (type?: string) => {
  const layout = LAYOUT_TYPES.find(item => item.value === type);
  return layout?.label || "单栏";
};

const getComponentName = (component: TemplateComponentResult) => {
  if (!component.componentId) return "未命名组件";
  const libComponent = COMPONENT_LIBRARY.find(
    (c) => c.id === component.componentId,
  );
  return libComponent?.name || "未知组件";
};

const getComponentDescription = (component: TemplateComponentResult) => {
  if (!component.componentId) return "";
  const libComponent = COMPONENT_LIBRARY.find(
    (c) => c.id === component.componentId,
  );
  return libComponent?.description || "";
};

const formatDateTime = (dateStr?: string) => {
  if (!dateStr) return "未知";
  try {
    const date = new Date(dateStr);
    return date.toLocaleDateString("zh-CN");
  } catch {
    return dateStr;
  }
};

// 生命周期
onLoad((options) => {
  if (options.id) {
    loadTemplateDetail(options.id);
  } else {
    uni.showToast({
      title: "缺少模板参数",
      icon: "none",
    });
    setTimeout(() => {
      uni.navigateBack();
    }, 1500);
  }
});
</script>

<style scoped lang="scss">
.template-detail-container {
  bg-color: $bg-color-grey;
  min-height: 100vh;
  padding-bottom: 140rpx;
}

/* 模板头部 */
.template-header {
  background: $bg-color;
  padding: $spacing-2;
  border-bottom: 1rpx solid $color-border-extra-light;
}

.header-main {
  display: flex;
  align-items: center;
  gap: $spacing-2;
}

.template-badge {
  width: 100rpx;
  height: 100rpx;
  border-radius: $border-radius-lg;
  @extend .flex-center;
  flex-shrink: 0;
  box-shadow: $box-shadow;
}

.badge-text {
  font-size: $font-size-xl;
  color: $color-text-inverse;
  font-weight: $font-weight-bold;
}

.template-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: $spacing-1;
}

.template-name {
  font-size: $font-size-lg;
  color: $color-text-primary;
  font-weight: $font-weight-bold;
  line-height: 1.4;
}

.template-description {
  font-size: $font-size-base;
  color: $color-text-regular;
  line-height: 1.5;
}

.template-meta {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-2;
  align-items: center;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: math.div($spacing-1, 2);
}

.meta-label {
  font-size: $font-size-sm;
  color: $color-text-secondary;
}

.meta-value {
  font-size: $font-size-sm;
  color: $color-text-regular;
  font-weight: $font-weight-medium;
}

.status-tag {
  padding: math.div($spacing-1, 2) $spacing-1;
  border-radius: $border-radius-sm;
  font-size: $font-size-sm;
  font-weight: $font-weight-medium;

  &.active {
    background: $bg-color-success;
    color: $color-success;
    border: 1rpx solid $color-success-border;
  }

  &.inactive {
    background: $bg-color;
    color: $color-text-grey;
    border: 1rpx solid $color-border-light;
  }
}

/* 底部操作栏 */
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: $bg-color;
  padding: $spacing-1 $spacing-2;
  display: flex;
  gap: $spacing-2;
  border-top: 1rpx solid $color-border-light;
  box-shadow: $box-shadow-dark;
  z-index: $z-index-dropdown;
}

.action-btn {
  flex: 1;
  height: $button-height;
  border-radius: $border-radius-lg;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all $transition-fast;

  &.btn-secondary {
    background: $bg-color;
    color: $color-text-regular;
    border: 1rpx solid $color-border-light;

    &:active {
      background: $bg-color;
      color-border: $color-border;
    }
  }

  &.btn-danger {
    background: $bg-color-danger;
    color: $color-danger;
    border: 1rpx solid $color-danger-border;

    &:active {
      background: color.adjust($bg-color-danger, $lightness: -10%);
    }
  }

  &.btn-primary {
    background: $color-primary;
    color: $bg-color;
    border: none;

    &:active {
      background: color.adjust($color-primary, $lightness: -10%);
    }
  }
}

/* 模板预览 */
.preview-section {
  padding: $spacing-2;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $spacing-2;
}

.section-title {
  font-size: $font-size-lg;
  color: $color-text-primary;
  font-weight: $font-weight-bold;
}

.device-switch {
  .switch-buttons {
    display: flex;
    gap: 8rpx;

    .device-btn {
      padding: $device-btn-padding;
      border: 1rpx solid $color-border;
      border-radius: $border-radius-sm;
      background: $bg-color;
      font-size: $font-size-sm;
      display: flex;
      align-items: center;
      gap: 4rpx;

      &.active {
        background: $color-primary;
        color: $bg-color;
        color-border: $color-primary;
}

      .device-icon {
        font-size: $font-size-base;
      }
    }
  }
}

.preview-card {
  background: $bg-color;
  border-radius: $border-radius-lg;
  padding: $spacing-2;
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
    border-radius: $border-radius-lg;
  }

  &.mobile .template-preview-container {
    max-width: 375rpx;
    height: 800rpx;
    margin: 0 auto;
    border-radius: $border-radius-lg;
    box-shadow: $box-shadow-dark;
  }
}

.template-preview-container {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  background: $bg-color;
  border: 1rpx solid $color-border-light;
  border-radius: $border-radius-lg;
}

.preview-info {
  margin-top: $margin-base;
  padding: $padding-base;
  background: $bg-color;
  border-radius: $border-radius;
  display: flex;
  flex-direction: column;
  gap: $spacing-1;
}

.info-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.info-label {
  font-size: $font-size-base;
  color: $color-text-secondary;
}

.info-value {
  font-size: $font-size-base;
  color: $color-text-primary;
  font-weight: $font-weight-medium;
}

.color-theme-preview {
  display: flex;
  gap: $spacing-1;

  .color-dot {
    width: 32rpx;
    height: 32rpx;
    border-radius: $border-radius-circle;
    border: 1rpx solid rgba(0, 0, 0, 0.1);
  }
}

/* 配置详情 */
.config-section {
  padding: 0 $spacing-2 $spacing-2;
}

.config-card {
  background: $bg-color;
  border-radius: $border-radius-lg;
  padding: $padding-base;
  margin-bottom: $margin-base;
  box-shadow: $card-shadow;
}

.config-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $margin-base;
  padding-bottom: $padding-sm;
  border-bottom: 1rpx solid $color-border-extra-light;
}

.config-title {
  font-size: $font-size-lg;
  color: $color-text-primary;
  font-weight: $font-weight-medium;
}

.config-count {
  font-size: $font-size-sm;
  color: $color-text-secondary;
  background: $bg-color;
  padding: math.div($spacing-1, 2) $spacing-1;
  border-radius: $border-radius-sm;
}

.config-content {
  display: flex;
  flex-direction: column;
  gap: $spacing-1;
}

.config-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: math.div($spacing-1, 2) 0;
}

.config-label {
  font-size: $font-size-base;
  color: $color-text-secondary;
  flex-shrink: 0;
}

.config-value {
  font-size: $font-size-base;
  color: $color-text-primary;
  font-weight: $font-weight-medium;
  text-align: right;
  margin-left: $spacing-1;
}

.color-value {
  padding: math.div($spacing-1, 2) $spacing-1;
  border-radius: $border-radius-sm;
  color: $color-text-inverse;
  text-shadow: 0 1rpx 2rpx rgba(0, 0, 0, 0.3);
  font-family: monospace;
  font-size: $font-size-sm;
  min-width: 120rpx;
  text-align: center;
}

.components-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-1;
}

.component-item {
  display: flex;
  align-items: center;
  padding: $padding-sm;
  background: $bg-color;
  border-radius: $border-radius-lg;
  border: 1rpx solid $color-border-light;
  transition: all $transition-fast;

  &:active {
    background: color.adjust($bg-color, $lightness: -5%);
  }
}

.component-index {
  width: $avatar-size;
  height: $avatar-size;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $avatar-bg-color;
  border-radius: $border-radius-circle;
  margin-right: $spacing-2;
  flex-shrink: 0;

  .index-text {
    font-size: $font-size-base;
    color: $color-text-secondary;
    font-weight: $font-weight-medium;
  }
}

.component-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: math.div($spacing-1, 2);
}

.component-name {
  font-size: $font-size-base;
  color: $color-text-primary;
  font-weight: $font-weight-medium;
}

.component-type {
  font-size: $font-size-sm;
  color: $color-text-secondary;
}

.component-actions {
  flex-shrink: 0;
}

.component-action {
  font-size: $font-size-sm;
  color: $color-primary;
  font-weight: $font-weight-medium;
  cursor: pointer;
  padding: math.div($spacing-1, 2) $spacing-1;
  background: $color-primary-light;
  border-radius: $border-radius-sm;

  &:active {
    background: color.adjust($color-primary-light, $lightness: -5%);
  }
}

.empty-components {
  padding: $padding-base 0;
  text-align: center;

  .empty-text {
    font-size: $font-size-base;
    color: $color-text-placeholder;
  }
}

/* 加载状态 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba($bg-color, 0.8);
  @extend .flex-center;
  z-index: $z-index-modal;
}

.loading-content {
  background: $bg-color;
  border-radius: $border-radius-lg;
  padding: $spacing-2;
  box-shadow: $box-shadow-dark;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-1;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid $color-border-light;
  border-top-color: $color-primary;
  border-radius: $border-radius-circle;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: $font-size-base;
  color: $color-text-regular;
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

    .action-btn {
      width: 100%;
    }
  }
}

.template-actions {
  @extend .flex-center;
}

.preview-actions {
  @extend .flex-center;
}

.status-tag {
  @extend .status-tag;
}

.preview-actions {
  @extend .flex-center;

  &.btn-secondary {
    @extend .btn-secondary;
  }

  &.btn-danger {
    @extend .btn-danger;
  }

  &.btn-primary {
    @extend .btn-primary;
  }
}
</style>