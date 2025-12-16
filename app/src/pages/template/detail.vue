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
      <button class="action-btn primary" @click="handleShare">
        <text class="btn-icon">💯 </text>
        <text class="btn-text">分享</text>
      </button>
    </view>

    <!-- 模板预览 -->
    <view class="preview-section">
      <view class="section-header">
        <text class="section-title">模板预览</text>
      </view>
      <view class="preview-card">
        <view class="preview-content" :style="previewStyle">
          <!-- 预览头部 -->
          <view class="preview-header">
            <view class="preview-avatar" v-if="template?.layout?.showPhoto">
              <text class="avatar-placeholder">👤</text>
            </view>
            <view class="preview-basic-info">
              <text class="preview-name">张三</text>
              <text class="preview-title">前端开发工程师</text>
              <view class="preview-contact">
                <text class="contact-item">📱 13800138000</text>
                <text class="contact-item">✉️ zhangsan@email.com</text>
                <text class="contact-item">📍 北京市</text>
              </view>
            </view>
          </view>

          <!-- 预览内容 -->
          <view class="preview-body">
            <view class="preview-section-item" v-if="template?.components?.find(c => c.component === 'JobIntention')">
              <text class="section-title">求职意向</text>
              <view class="section-content">
                <text class="content-item">期望职位：前端开发工程师</text>
                <text class="content-item">期望薪资：20-30K</text>
                <text class="content-item">工作地点：北京</text>
              </view>
            </view>

            <view class="preview-section-item" v-if="template?.components?.find(c => c.component === 'WorkExperience')">
              <text class="section-title">工作经历</text>
              <view class="section-content">
                <view class="experience-item">
                  <text class="company">ABC科技有限公司</text>
                  <text class="period">2020.09 - 至今</text>
                  <text class="position">高级前端开发工程师</text>
                  <text class="description">负责核心产品的前端架构设计与开发...</text>
                </view>
              </view>
            </view>

            <view class="preview-section-item"
                  v-if="template?.components?.find(c => c.component === 'EducationExperience')">
              <text class="section-title">教育背景</text>
              <view class="section-content">
                <view class="education-item">
                  <text class="school">清华大学</text>
                  <text class="period">2016.09 - 2020.06</text>
                  <text class="major">计算机科学与技术 / 本科</text>
                  <text class="gpa">GPA: 3.8/4.0</text>
                </view>
              </view>
            </view>

            <view class="preview-section-item" v-if="template?.components?.find(c => c.component === 'Skills')">
              <text class="section-title">专业技能</text>
              <view class="section-content">
                <view class="skill-item">
                  <text class="skill-name">Vue.js</text>
                  <view class="skill-level">
                    <view class="level-bar" style="width: 90%"></view>
                  </view>
                </view>
                <view class="skill-item">
                  <text class="skill-name">React</text>
                  <view class="skill-level">
                    <view class="level-bar" style="width: 80%"></view>
                  </view>
                </view>
              </view>
            </view>
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
      <view class="config-card">
        <view class="config-header">
          <text class="config-title">全局样式</text>
        </view>
        <view class="config-content">
          <view class="config-item" v-for="(value, key) in template?.globalStyle" :key="key">
            <text class="config-label">{{ getStyleLabel(key) }}</text>
            <text class="config-value">{{ value }}</text>
          </view>
        </view>
      </view>

      <!-- 布局配置 -->
      <view class="config-card">
        <view class="config-header">
          <text class="config-title">布局配置</text>
        </view>
        <view class="config-content">
          <view class="config-item">
            <text class="config-label">页面尺寸</text>
            <text class="config-value">{{ template?.layout?.pageSize || "A4" }}</text>
          </view>
          <view class="config-item">
            <text class="config-label">布局方向</text>
            <text class="config-value">{{ template?.layout?.orientation === "portrait" ? "竖向" : "横向" }}</text>
          </view>
          <view class="config-item">
            <text class="config-label">列数</text>
            <text class="config-value">{{ template?.layout?.columns || 1 }}</text>
          </view>
          <view class="config-item">
            <text class="config-label">显示照片</text>
            <text class="config-value">{{ template?.layout?.showPhoto ? "是" : "否" }}</text>
          </view>
        </view>
      </view>

      <!-- 组件列表 -->
      <view class="config-card">
        <view class="config-header">
          <text class="config-title">启用组件</text>
          <text class="config-count">{{ template?.components?.length || 0 }}个</text>
        </view>
        <view class="components-list">
          <view class="component-item" v-for="component in template?.components" :key="component.id">
            <view class="component-info">
              <text class="component-name">{{ component.name }}</text>
              <text class="component-type">{{ component.component }}</text>
            </view>
            <view class="component-actions">
              <text class="component-action" @click.stop="viewComponentDetail(component)">查看详情</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 时间信息 -->
      <view class="config-card">
        <view class="config-header">
          <text class="config-title">时间信息</text>
        </view>
        <view class="config-content">
          <view class="config-item">
            <text class="config-label">创建时间</text>
            <text class="config-value">{{ formatDateTime(template?.createdAt) }}</text>
          </view>
          <view class="config-item">
            <text class="config-label">更新时间</text>
            <text class="config-value">{{ formatDateTime(template?.updatedAt) }}</text>
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
      <button class="bottom-btn toggle-status" @click="handleEdit">
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
import { computed, defineComponent, ref } from "vue";
import { onLoad, onShow } from "@dcloudio/uni-app";
import { TemplateResult, useTemplate } from "@/composables/useTemplate";
import { TemplateComponentResult } from "@/types/template-component";

export default defineComponent({
  name: "TemplateDetail",

  setup() {
    const { fetchTemplateDetail, setCurrentTemplate } = useTemplate();

    const template = ref<TemplateResult | null>(null);
    const loading = ref(true);
    const templateId = ref<string>("");

    // 计算预览样式
    const previewStyle = computed(() => {
      if (!template.value?.globalStyle) return {};

      const style = template.value.globalStyle;
      return {
        fontFamily: style.fontFamily || "Microsoft YaHei, SimSun, serif",
        fontSize: style.fontSize || "12px",
        lineHeight: style.lineHeight || "1.6",
        color: style.primaryColor || "#2c3e50",
        backgroundColor: style.backgroundColor || "#ffffff",
      };
    });

    // 加载模板详情
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
      setCurrentTemplate(template.value);
      uni.navigateTo({
        url: `/pages/resumes/create?templateId=${template.value.id}`,
      });
    };

    const handlePreview = () => {
      if (!template.value) return;
      uni.navigateTo({
        url: `/pages/template/preview?id=${template.value.id}`,
      });
    };

    const handleDelete = () => {
      if (!template.value) return;

      uni.showModal({
        title: "确认删除",
        content: "确定要删除这个模板吗？删除后无法恢复。",
        success: (res) => {
          if (res.confirm) {
            // 调用删除API
            uni.showToast({
              title: "删除成功",
              icon: "success",
            });
            setTimeout(() => {
              uni.navigateBack();
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
            // 调用更新状态API
            template.value!.isActive = newStatus;
            uni.showToast({
              title: `${action}成功`,
              icon: "success",
            });
          }
        },
      });
    };

    const viewComponentDetail = (component: TemplateComponentResult) => {
      uni.showModal({
        title: component.name,
        content: JSON.stringify(component.props, null, 2),
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

    const getStyleLabel = (key: string) => {
      const labelMap: Record<string, string> = {
        "theme": "主题",
        "margin": "边距",
        "padding": "内边距",
        "fontSize": "字体大小",
        "fontFamily": "字体",
        "lineHeight": "行高",
        "headerColor": "页眉颜色",
        "primaryColor": "主色",
        "secondaryColor": "辅色",
        "backgroundColor": "背景色",
      };
      return labelMap[key] || key;
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
        templateId.value = options.id;
        loadTemplateDetail(options.id);
      }
    });

    onShow(() => {
      // 如果从编辑页面返回，重新加载数据
      if (templateId.value) {
        loadTemplateDetail(templateId.value);
      }
    });

    return {
      // 状态
      template,
      loading,

      // 计算属性
      previewStyle,

      // 方法
      handleBack,
      handleEdit,
      handleShare,
      handleUseTemplate,
      handlePreview,
      handleDelete,
      handleToggleStatus,
      viewComponentDetail,

      // 工具函数
      getTemplateInitial,
      getAvatarColor,
      getStyleLabel,
      formatDateTime,
    };
  },
});
</script>

<style scoped lang="scss">
.template-detail-container {
  background-color: $uni-bg-color-grey;
  min-height: 100vh;
  padding-bottom: 120rpx;
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
  height: 80rpx;
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
  margin-bottom: $uni-spacing-col-base;
}

.section-title {
  font-size: $uni-font-size-lg;
  color: $text-primary;
  font-weight: $font-weight-bold;
}

.preview-card {
  background: $uni-bg-color;
  border-radius: $border-radius-large;
  padding: $uni-spacing-row-base;
  box-shadow: $card-shadow;
}

.preview-content {
  background: $uni-bg-color;
  border: 1rpx solid $border-color-light;
  border-radius: $uni-border-radius-lg;
  padding: $uni-spacing-row-base;
  min-height: 400rpx;
}

.preview-header {
  display: flex;
  align-items: center;
  gap: $uni-spacing-col-base;
  padding-bottom: $uni-spacing-col-base;
  border-bottom: 1rpx solid $border-color-extra-light;
  margin-bottom: $uni-spacing-col-base;
}

.preview-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: $background-color;
  @extend .flex-center;
  border: 2rpx solid $border-color-light;
}

.avatar-placeholder {
  font-size: $uni-font-size-lg;
  color: $text-placeholder;
}

.preview-basic-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: math.div($uni-spacing-col-sm, 2);
}

.preview-name {
  font-size: $uni-font-size-lg;
  color: $text-primary;
  font-weight: $font-weight-bold;
}

.preview-title {
  font-size: $uni-font-size-base;
  color: $primary-color;
  font-weight: $font-weight-medium;
}

.preview-contact {
  display: flex;
  flex-wrap: wrap;
  gap: $uni-spacing-col-sm;
  margin-top: $uni-spacing-col-sm;
}

.contact-item {
  font-size: $uni-font-size-sm;
  color: $text-regular;
  padding-right: $uni-spacing-col-sm;
  border-right: 1rpx solid $border-color-light;

  &:last-child {
    border-right: none;
    padding-right: 0;
  }
}

.preview-body {
  display: flex;
  flex-direction: column;
  gap: $uni-spacing-col-base;
}

.preview-section-item {
  margin-bottom: $uni-spacing-col-base;
}

.section-content {
  margin-top: $uni-spacing-col-sm;
  display: flex;
  flex-direction: column;
  gap: $uni-spacing-col-sm;
}

.content-item {
  font-size: $uni-font-size-base;
  color: $text-regular;
  line-height: 1.6;
}

.experience-item, .education-item {
  display: flex;
  flex-direction: column;
  gap: math.div($uni-spacing-col-sm, 2);
  padding: $uni-spacing-col-sm;
  background: $background-color;
  border-radius: $uni-border-radius-sm;
  border-left: 3rpx solid $primary-color;
}

.company, .school {
  font-size: $uni-font-size-base;
  color: $text-primary;
  font-weight: $font-weight-medium;
}

.period {
  font-size: $uni-font-size-sm;
  color: $text-secondary;
}

.position, .major {
  font-size: $uni-font-size-sm;
  color: $text-regular;
}

.description, .gpa {
  font-size: $uni-font-size-sm;
  color: $text-secondary;
  line-height: 1.6;
}

.skill-item {
  display: flex;
  align-items: center;
  gap: $uni-spacing-col-sm;
  margin-bottom: math.div($uni-spacing-col-sm, 2);
}

.skill-name {
  font-size: $uni-font-size-sm;
  color: $text-regular;
  width: 100rpx;
}

.skill-level {
  flex: 1;
  height: 8rpx;
  background: $border-color-light;
  border-radius: 4rpx;
  overflow: hidden;
}

.level-bar {
  height: 100%;
  background: $primary-color;
  border-radius: 4rpx;
  transition: width 0.3s ease;
}

/* 配置详情 */
.config-section {
  padding: 0 $uni-spacing-row-base $uni-spacing-row-base;
}

.config-card {
  background: $uni-bg-color;
  border-radius: $border-radius-large;
  padding: $uni-spacing-col-base;
  margin-bottom: $uni-spacing-col-base;
  box-shadow: $card-shadow;
}

.config-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $uni-spacing-col-base;
  padding-bottom: $uni-spacing-col-sm;
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
  gap: math.div($uni-spacing-col-sm, 2);
}

.config-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: math.div($uni-spacing-col-sm, 2) 0;
}

.config-label {
  font-size: $uni-font-size-sm;
  color: $text-secondary;
  flex-shrink: 0;
}

.config-value {
  font-size: $uni-font-size-sm;
  color: $text-regular;
  font-weight: $font-weight-medium;
  text-align: right;
  word-break: break-all;
  margin-left: $uni-spacing-col-sm;
}

.components-list {
  display: flex;
  flex-direction: column;
  gap: $uni-spacing-col-sm;
}

.component-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $uni-spacing-col-sm;
  background: $background-color;
  border-radius: $uni-border-radius-sm;
  border: 1rpx solid $border-color-light;
  transition: all $transition-fast;

  &:active {
    background: color.adjust($background-color, $lightness: -5%);
  }
}

.component-info {
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
  font-family: monospace;
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
  height: 80rpx;
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
  border-radius: 50%;
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

/* 工具类 */
.flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>