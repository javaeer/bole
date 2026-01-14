<!-- /components/ThemeSwitcher.vue -->
<template>
  <view class="theme-switcher" :class="{ 'switcher-collapsed': collapsed }">
    <!-- 折叠/展开按钮 -->
    <view class="switcher-toggle" @click="collapsed = !collapsed">
      <text class="toggle-icon">{{ collapsed ? '🎨' : '✕' }}</text>
      <text class="toggle-text">{{ collapsed ? '切换样式' : '关闭' }}</text>
    </view>

    <!-- 展开后的样式选择器 -->
    <view v-if="!collapsed" class="switcher-content">
      <!-- 主题切换 -->
      <view class="section">
        <text class="section-title">主题风格</text>
        <view class="theme-grid">
          <view
            v-for="theme in availableThemes"
            :key="theme.id"
            :class="['theme-item', { active: currentTheme === theme.id }]"
            @click="switchTheme(theme.id)"
          >
            <view class="theme-preview" :style="theme.previewStyle">
              <view class="preview-header"></view>
              <view class="preview-body">
                <view class="preview-line"></view>
                <view class="preview-line short"></view>
              </view>
            </view>
            <text class="theme-name">{{ theme.name }}</text>
          </view>
        </view>
      </view>

      <!-- 颜色切换 -->
      <view class="section">
        <text class="section-title">主色调</text>
        <view class="color-grid">
          <view
            v-for="color in availableColors"
            :key="color.id"
            :class="['color-item', { active: currentColor === color.id }]"
            @click="switchColor(color.id)"
          >
            <view class="color-circle" :style="{ backgroundColor: color.value }"></view>
            <text class="color-name">{{ color.name }}</text>
          </view>
        </view>
      </view>

      <!-- 布局切换 -->
      <view class="section">
        <text class="section-title">布局模式</text>
        <view class="layout-grid">
          <view
            v-for="layout in availableLayouts"
            :key="layout.id"
            :class="['layout-item', { active: currentLayout === layout.id }]"
            @click="switchLayout(layout.id)"
          >
            <view class="layout-preview">
              <view v-if="layout.id === 'card'" class="preview-card">
                <view class="card-header"></view>
                <view class="card-content">
                  <view class="line"></view>
                  <view class="line short"></view>
                </view>
              </view>
              <view v-if="layout.id === 'list'" class="preview-list">
                <view class="list-item"></view>
                <view class="list-item"></view>
                <view class="list-item"></view>
              </view>
              <view v-if="layout.id === 'timeline'" class="preview-timeline">
                <view class="timeline-dot"></view>
                <view class="timeline-line"></view>
                <view class="timeline-content"></view>
              </view>
              <view v-if="layout.id === 'compact'" class="preview-compact">
                <view class="compact-row"></view>
                <view class="compact-row"></view>
              </view>
            </view>
            <text class="layout-name">{{ layout.name }}</text>
          </view>
        </view>
      </view>

      <!-- 字体大小 -->
      <view class="section">
        <text class="section-title">字体大小</text>
        <view class="font-size-control">
          <button
            class="font-btn small"
            :class="{ active: fontSize === 'small' }"
            @click="switchFontSize('small')"
          >
            小
          </button>
          <button
            class="font-btn medium"
            :class="{ active: fontSize === 'medium' }"
            @click="switchFontSize('medium')"
          >
            中
          </button>
          <button
            class="font-btn large"
            :class="{ active: fontSize === 'large' }"
            @click="switchFontSize('large')"
          >
            大
          </button>
        </view>
      </view>

      <!-- 显示设置 -->
      <view class="section">
        <text class="section-title">显示设置</text>
        <view class="display-settings">
          <view class="setting-item">
            <text class="setting-label">显示头像</text>
            <switch :checked="showAvatar" @change="toggleSetting('showAvatar')" />
          </view>
          <view class="setting-item">
            <text class="setting-label">显示项目详情</text>
            <switch :checked="showProjects" @change="toggleSetting('showProjects')" />
          </view>
          <view class="setting-item">
            <text class="setting-label">显示技能标签</text>
            <switch :checked="showSkills" @change="toggleSetting('showSkills')" />
          </view>
          <view class="setting-item">
            <text class="setting-label">显示在校成就</text>
            <switch :checked="showAchievements" @change="toggleSetting('showAchievements')" />
          </view>
        </view>
      </view>

      <!-- 操作按钮 -->
      <view class="action-buttons">
        <button class="action-btn reset" @click="resetSettings">重置样式</button>
        <button class="action-btn apply" @click="applySettings">应用设置</button>
        <button class="action-btn export" @click="exportConfig">导出配置</button>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'ThemeSwitcher',

  props: {
    initialConfig: {
      type: Object,
      default: () => ({})
    }
  },

  data() {
    return {
      collapsed: true,

      // 当前设置
      currentTheme: 'modern',
      currentColor: 'gold',
      currentLayout: 'card',
      fontSize: 'medium',

      // 显示设置
      showAvatar: true,
      showProjects: true,
      showSkills: true,
      showAchievements: true,

      // 可用的主题
      availableThemes: [
        {
          id: 'modern',
          name: '现代简约',
          previewStyle: {
            background: 'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)',
            borderColor: '#e4e7ed'
          }
        },
        {
          id: 'classic',
          name: '经典商务',
          previewStyle: {
            background: 'linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%)',
            borderColor: '#dee2e6'
          }
        },
        {
          id: 'tech',
          name: '科技蓝',
          previewStyle: {
            background: 'linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%)',
            borderColor: '#90caf9'
          }
        },
        {
          id: 'dark',
          name: '深色模式',
          previewStyle: {
            background: 'linear-gradient(135deg, #2c3e50 0%, #34495e 100%)',
            borderColor: '#4a6278'
          }
        },
        {
          id: 'warm',
          name: '温暖色调',
          previewStyle: {
            background: 'linear-gradient(135deg, #fff9c4 0%, #ffecb3 100%)',
            borderColor: '#ffd54f'
          }
        },
        {
          id: 'creative',
          name: '创意色彩',
          previewStyle: {
            background: 'linear-gradient(135deg, #fce4ec 0%, #f3e5f5 100%)',
            borderColor: '#ce93d8'
          }
        }
      ],

      // 可用的颜色
      availableColors: [
        { id: 'gold', name: '典雅金', value: '#d4af37' },
        { id: 'blue', name: '科技蓝', value: '#007aff' },
        { id: 'green', name: '生机绿', value: '#4cd964' },
        { id: 'purple', name: '浪漫紫', value: '#9b59b6' },
        { id: 'red', name: '热情红', value: '#ff3b30' },
        { id: 'cyan', name: '清新青', value: '#5ac8fa' },
        { id: 'orange', name: '活力橙', value: '#ff9500' },
        { id: 'slate', name: '高级灰', value: '#8e8e93' }
      ],

      // 可用的布局
      availableLayouts: [
        { id: 'card', name: '卡片式' },
        { id: 'list', name: '列表式' },
        { id: 'timeline', name: '时间线' },
        { id: 'compact', name: '紧凑型' }
      ]
    };
  },

  created() {
    // 从传入的配置中初始化设置
    this.initFromConfig();
  },

  methods: {
    initFromConfig() {
      if (this.initialConfig.globalStyle) {
        this.currentTheme = this.initialConfig.globalStyle.theme || 'modern';

        // 从主色推断颜色
        const primaryColor = this.initialConfig.globalStyle.primaryColor;
        if (primaryColor) {
          const matchedColor = this.availableColors.find(c => c.value === primaryColor);
          if (matchedColor) {
            this.currentColor = matchedColor.id;
          }
        }
      }
    },

    switchTheme(themeId) {
      this.currentTheme = themeId;
      this.emitChange();
    },

    switchColor(colorId) {
      this.currentColor = colorId;
      const color = this.availableColors.find(c => c.id === colorId);
      if (color) {
        this.emitChange();
      }
    },

    switchLayout(layoutId) {
      this.currentLayout = layoutId;
      this.emitChange();
    },

    switchFontSize(size) {
      this.fontSize = size;
      this.emitChange();
    },

    toggleSetting(setting) {
      this[setting] = !this[setting];
      this.emitChange();
    },

    resetSettings() {
      this.currentTheme = 'modern';
      this.currentColor = 'gold';
      this.currentLayout = 'card';
      this.fontSize = 'medium';
      this.showAvatar = true;
      this.showProjects = true;
      this.showSkills = true;
      this.showAchievements = true;

      uni.showToast({
        title: '设置已重置',
        icon: 'success'
      });

      this.emitChange();
    },

    applySettings() {
      uni.showToast({
        title: '样式已应用',
        icon: 'success'
      });

      this.emitChange();
    },

    exportConfig() {
      const config = this.generateConfig();

      // 复制到剪贴板
      uni.setClipboardData({
        data: JSON.stringify(config, null, 2),
        success: () => {
          uni.showToast({
            title: '配置已复制到剪贴板',
            icon: 'success'
          });
        }
      });
    },

    generateConfig() {
      const color = this.availableColors.find(c => c.id === this.currentColor);

      return {
        globalStyle: {
          theme: this.currentTheme,
          primaryColor: color ? color.value : '#d4af37',
          fontSize: this.fontSize,
          layout: this.currentLayout
        },
        displaySettings: {
          showAvatar: this.showAvatar,
          showProjects: this.showProjects,
          showSkills: this.showSkills,
          showAchievements: this.showAchievements
        }
      };
    },

    emitChange() {
      const config = this.generateConfig();
      this.$emit('change', config);
    }
  }
};
</script>

<style lang="scss" scoped>
.theme-switcher {
  position: fixed;
  top: 50%;
  right: 0;
  transform: translateY(-50%);
  z-index: 1000;
  transition: all 0.3s ease;

  &.switcher-collapsed {
    .switcher-content {
      display: none;
    }

    .switcher-toggle {
      border-radius: 20rpx 0 0 20rpx;
    }
  }

  .switcher-toggle {
    background: linear-gradient(135deg, #d4af37, #f7ef8a);
    color: white;
    padding: 16rpx 20rpx;
    border-radius: 20rpx 0 0 20rpx;
    display: flex;
    align-items: center;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.1);
    cursor: pointer;

    .toggle-icon {
      font-size: 32rpx;
      margin-right: 8rpx;
    }

    .toggle-text {
      font-size: 24rpx;
      font-weight: 500;
    }
  }

  .switcher-content {
    background: white;
    width: 320rpx;
    border-radius: 16rpx 0 0 16rpx;
    padding: 24rpx;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
    margin-top: -10rpx;
    max-height: 70vh;
    overflow-y: auto;

    .section {
      margin-bottom: 32rpx;

      &:last-child {
        margin-bottom: 0;
      }

      .section-title {
        display: block;
        font-size: 26rpx;
        font-weight: 600;
        color: #333;
        margin-bottom: 20rpx;
        padding-bottom: 10rpx;
        border-bottom: 1rpx solid #f0f0f0;
      }
    }

    .theme-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 16rpx;

      .theme-item {
        cursor: pointer;
        text-align: center;

        &.active {
          .theme-preview {
            border-color: #d4af37;
            box-shadow: 0 0 0 2rpx rgba(212, 175, 55, 0.3);
          }

          .theme-name {
            color: #d4af37;
            font-weight: 500;
          }
        }

        .theme-preview {
          width: 100%;
          height: 60rpx;
          border: 2rpx solid #e0e0e0;
          border-radius: 8rpx;
          margin-bottom: 8rpx;
          overflow: hidden;
          transition: all 0.2s;

          .preview-header {
            height: 16rpx;
            background: rgba(255, 255, 255, 0.5);
          }

          .preview-body {
            padding: 8rpx;

            .preview-line {
              height: 6rpx;
              background: rgba(255, 255, 255, 0.7);
              margin-bottom: 4rpx;
              border-radius: 3rpx;

              &.short {
                width: 70%;
              }
            }
          }
        }

        .theme-name {
          font-size: 22rpx;
          color: #666;
        }
      }
    }

    .color-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 16rpx;

      .color-item {
        cursor: pointer;
        text-align: center;

        &.active {
          .color-circle {
            transform: scale(1.1);
            box-shadow: 0 0 0 2rpx white, 0 0 0 4rpx #d4af37;
          }

          .color-name {
            color: #d4af37;
            font-weight: 500;
          }
        }

        .color-circle {
          width: 40rpx;
          height: 40rpx;
          border-radius: 50%;
          margin: 0 auto 8rpx;
          transition: all 0.2s;
        }

        .color-name {
          font-size: 20rpx;
          color: #666;
        }
      }
    }

    .layout-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 16rpx;

      .layout-item {
        cursor: pointer;
        text-align: center;

        &.active {
          .layout-preview {
            border-color: #d4af37;
          }

          .layout-name {
            color: #d4af37;
            font-weight: 500;
          }
        }

        .layout-preview {
          height: 60rpx;
          border: 2rpx solid #e0e0e0;
          border-radius: 8rpx;
          margin-bottom: 8rpx;
          padding: 8rpx;
          transition: all 0.2s;

          .preview-card {
            height: 100%;
            background: #f5f5f5;
            border-radius: 4rpx;
            overflow: hidden;

            .card-header {
              height: 12rpx;
              background: #ddd;
            }

            .card-content {
              padding: 6rpx;

              .line {
                height: 4rpx;
                background: #eee;
                margin-bottom: 3rpx;
                border-radius: 2rpx;

                &.short {
                  width: 70%;
                }
              }
            }
          }

          .preview-list {
            height: 100%;
            display: flex;
            flex-direction: column;
            justify-content: space-between;

            .list-item {
              height: 6rpx;
              background: #f5f5f5;
              border-radius: 3rpx;
            }
          }

          .preview-timeline {
            height: 100%;
            position: relative;
            padding-left: 20rpx;

            .timeline-dot {
              position: absolute;
              left: 8rpx;
              top: 50%;
              transform: translateY(-50%);
              width: 8rpx;
              height: 8rpx;
              background: #ddd;
              border-radius: 50%;
            }

            .timeline-line {
              position: absolute;
              left: 11rpx;
              top: 0;
              bottom: 0;
              width: 2rpx;
              background: #eee;
            }

            .timeline-content {
              height: 100%;
              background: #f5f5f5;
              border-radius: 4rpx;
            }
          }

          .preview-compact {
            height: 100%;
            display: flex;
            flex-direction: column;
            justify-content: space-between;

            .compact-row {
              height: 8rpx;
              background: #f5f5f5;
              border-radius: 4rpx;
            }
          }
        }

        .layout-name {
          font-size: 22rpx;
          color: #666;
        }
      }
    }

    .font-size-control {
      display: flex;
      gap: 16rpx;

      .font-btn {
        flex: 1;
        background: #f5f5f5;
        border: none;
        border-radius: 6rpx;
        padding: 12rpx 0;
        font-size: 24rpx;
        color: #666;
        transition: all 0.2s;

        &.active {
          background: #d4af37;
          color: white;
          font-weight: 500;
        }

        &.small {
          font-size: 20rpx;
        }

        &.medium {
          font-size: 24rpx;
        }

        &.large {
          font-size: 28rpx;
        }
      }
    }

    .display-settings {
      .setting-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16rpx;

        &:last-child {
          margin-bottom: 0;
        }

        .setting-label {
          font-size: 24rpx;
          color: #555;
        }

        switch {
          transform: scale(0.8);
          transform-origin: right center;
        }
      }
    }

    .action-buttons {
      display: flex;
      flex-direction: column;
      gap: 12rpx;
      margin-top: 24rpx;

      .action-btn {
        border: none;
        border-radius: 8rpx;
        padding: 16rpx 0;
        font-size: 24rpx;
        font-weight: 500;
        transition: all 0.2s;

        &.reset {
          background: #f5f5f5;
          color: #666;
        }

        &.apply {
          background: linear-gradient(135deg, #d4af37, #f7ef8a);
          color: white;
        }

        &.export {
          background: #007aff;
          color: white;
        }

        &:active {
          opacity: 0.8;
          transform: translateY(2rpx);
        }
      }
    }
  }
}
</style>