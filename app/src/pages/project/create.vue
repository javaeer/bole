<template>
  <view class="project-add-page">
    <!-- 头部 -->
    <view class="page-header">
      <text class="page-title">新增项目经历</text>
      <text class="page-subtitle">请详细填写项目相关信息</text>
    </view>

    <scroll-view class="form-container" scroll-y="true">
      <!-- 基础信息卡片 -->
      <view class="form-card">
        <text class="card-title">基础信息</text>

        <!-- 公司选择 -->
        <view class="form-item">
          <text class="form-label required">归属公司</text>
          <picker
            class="form-picker"
            :range="companyList"
            range-key="name"
            @change="onCompanyChange"
          >
            <view class="picker-content">
              <text :class="[selectedCompany ? 'picker-text' : 'picker-placeholder']">
                {{ selectedCompany?.name || '请选择供职公司' }}
              </text>
              <uni-icons type="arrowright" size="18" color="#999"/>
            </view>
          </picker>
        </view>

        <!-- 项目时间 -->
        <view class="form-item">
          <text class="form-label required">项目时间</text>
          <view class="time-range">
            <picker
              mode="date"
              fields="month"
              :value="startTime"
              @change="onStartTimeChange"
            >
              <view class="time-picker">
                <text>{{ startTime || '开始时间' }}</text>
                <uni-icons type="calendar" size="16" color="#999"/>
              </view>
            </picker>
            <text class="time-separator">至</text>
            <picker
              mode="date"
              fields="month"
              :value="endTime"
              @change="onEndTimeChange"
            >
              <view class="time-picker">
                <text>{{ isCurrent ? '至今' : (endTime || '结束时间') }}</text>
                <uni-icons type="calendar" size="16" color="#999"/>
              </view>
            </picker>
            <view class="current-checkbox" @click="toggleCurrent">
              <view class="checkbox-icon" :class="{ checked: isCurrent }">
                <uni-icons v-if="isCurrent" type="checkmarkempty" size="12" color="#fff"/>
              </view>
              <text class="checkbox-label">至今</text>
            </view>
          </view>
        </view>

        <!-- 项目名称 -->
        <view class="form-item">
          <text class="form-label required">项目名称</text>
          <input
            class="form-input"
            v-model="projectName"
            placeholder="请输入项目名称"
            placeholder-class="placeholder"
            maxlength="50"
          />
          <text class="input-counter">{{ projectName.length }}/50</text>
        </view>

        <!-- 项目角色 -->
        <view class="form-item">
          <text class="form-label required">担任角色</text>
          <input
            class="form-input"
            v-model="projectRole"
            placeholder="如：前端负责人、核心开发"
            placeholder-class="placeholder"
            maxlength="20"
          />
        </view>
      </view>

      <!-- 项目详情卡片 -->
      <view class="form-card">
        <text class="card-title">项目详情</text>

        <!-- 项目简介 -->
        <view class="form-item">
          <text class="form-label required">项目简介</text>
          <textarea
            class="form-textarea"
            v-model="projectDesc"
            placeholder="请简要描述项目背景、目标、规模等"
            placeholder-class="placeholder"
            maxlength="300"
            auto-height="true"
          />
          <text class="textarea-counter">{{ projectDesc.length }}/300</text>
        </view>

        <!-- 技术栈 -->
        <view class="form-item">
          <text class="form-label">技术栈</text>
          <view class="tech-tags">
            <view
              v-for="(tag, index) in techStack"
              :key="index"
              class="tech-tag"
              @click="removeTechTag(index)"
            >
              <text class="tag-text">{{ tag }}</text>
              <uni-icons type="closeempty" size="12" color="#999"/>
            </view>
            <input
              v-if="techStack.length < 10"
              class="tech-input"
              v-model="techInput"
              placeholder="输入后回车添加"
              placeholder-class="placeholder"
              maxlength="15"
              @confirm="addTechTag"
              @blur="addTechTag"
            />
          </view>
          <text class="form-tip">最多添加10个技术标签</text>
        </view>

        <!-- 架构简介 -->
        <view class="form-item">
          <text class="form-label">架构简介</text>
          <textarea
            class="form-textarea"
            v-model="architecture"
            placeholder="可描述技术架构、核心模块、关键决策等"
            placeholder-class="placeholder"
            maxlength="200"
            auto-height="true"
          />
          <text class="textarea-counter">{{ architecture.length }}/200</text>
        </view>
        <!-- 项目成果 -->
        <view class="form-item">
          <text class="form-label">项目成果</text>
          <view class="achievement-list">
            <view
              v-for="(item, index) in achievements"
              :key="index"
              class="achievement-item"
            >
              <view class="achievement-icon">
                <uni-icons type="star" size="16" color="#d4af37"></uni-icons>
              </view>
              <input
                class="achievement-input"
                v-model="achievements[index]"
                :placeholder="`成果描述 ${index + 1}`"
                placeholder-class="placeholder"
                maxlength="100"
              />
              <view
                v-if="achievements.length > 1"
                class="item-remove"
                @click="removeAchievement(index)"
              >
                <uni-icons type="trash" size="16" color="#f56c6c"></uni-icons>
              </view>
            </view>
          </view>
          <view class="add-button" @click="addAchievement">
            <uni-icons type="plusempty" size="16" color="#d4af37"></uni-icons>
            <text class="add-button-text">添加成果</text>
          </view>
        </view>

        <!-- 项目链接 -->
        <view class="form-item">
          <text class="form-label">项目链接</text>
          <input
            class="form-input"
            v-model="projectLink"
            placeholder="可填写项目演示地址、GitHub链接等"
            placeholder-class="placeholder"
          />
        </view>
      </view>

      <!-- 职责与成果卡片 -->
      <view class="form-card">
        <text class="card-title">个人贡献</text>

        <!-- 个人职责 -->
        <view class="form-item">
          <text class="form-label required">个人职责</text>
          <view class="responsibility-list">
            <view
              v-for="(item, index) in responsibilities"
              :key="index"
              class="responsibility-item"
            >
              <text class="item-number">{{ index + 1 }}.</text>
              <input
                class="item-input"
                v-model="responsibilities[index]"
                :placeholder="`职责描述 ${index + 1}`"
                placeholder-class="placeholder"
                maxlength="100"
              />
              <view
                v-if="responsibilities.length > 1"
                class="item-remove"
                @click="removeResponsibility(index)"
              >
                <uni-icons type="trash" size="16" color="#f56c6c"></uni-icons>
              </view>
            </view>
          </view>
          <view class="add-button" @click="addResponsibility">
            <uni-icons type="plusempty" size="16" color="#d4af37"></uni-icons>
            <text class="add-button-text">添加职责</text>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 底部操作栏 -->
    <view class="action-bar">
      <view class="action-buttons">
        <view class="button button-secondary" @click="handleCancel">
          取消
        </view>
        <view class="button button-primary" :class="{ disabled: !isFormValid }" @click="handleSubmit">
          保存
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      // 基础信息
      companyList: [
        { id: 1, name: '阿里巴巴集团' },
        { id: 2, name: '腾讯科技' },
        { id: 3, name: '字节跳动' },
        { id: 4, name: '华为技术有限公司' },
        { id: 5, name: '其他' }
      ],
      selectedCompany: null,
      startTime: '',
      endTime: '',
      isCurrent: false,
      projectName: '',
      projectRole: '',

      // 项目详情
      projectDesc: '',
      techStack: [],
      techInput: '',
      architecture: '',

      // 个人贡献
      responsibilities: [''],
      achievements: [''],
      projectLink: ''
    };
  },

  computed: {
    isFormValid() {
      return (
        this.selectedCompany &&
        this.startTime &&
        this.projectName &&
        this.projectRole &&
        this.projectDesc &&
        this.responsibilities.some(item => item.trim())
      );
    }
  },

  methods: {
    onCompanyChange(e) {
      const index = e.detail.value;
      this.selectedCompany = this.companyList[index];
    },

    onStartTimeChange(e) {
      this.startTime = e.detail.value;
    },

    onEndTimeChange(e) {
      if (!this.isCurrent) {
        this.endTime = e.detail.value;
      }
    },

    toggleCurrent() {
      this.isCurrent = !this.isCurrent;
      if (this.isCurrent) {
        this.endTime = '';
      }
    },

    addTechTag() {
      if (this.techInput.trim() && this.techStack.length < 10) {
        this.techStack.push(this.techInput.trim());
        this.techInput = '';
      }
    },

    removeTechTag(index) {
      this.techStack.splice(index, 1);
    },

    addResponsibility() {
      if (this.responsibilities.length < 10) {
        this.responsibilities.push('');
      }
    },

    removeResponsibility(index) {
      if (this.responsibilities.length > 1) {
        this.responsibilities.splice(index, 1);
      }
    },

    addAchievement() {
      if (this.achievements.length < 10) {
        this.achievements.push('');
      }
    },

    removeAchievement(index) {
      if (this.achievements.length > 1) {
        this.achievements.splice(index, 1);
      }
    },

    handleCancel() {
      uni.navigateBack();
    },

    handleSubmit() {
      if (!this.isFormValid) return;

      const projectData = {
        company: this.selectedCompany,
        timeRange: {
          start: this.startTime,
          end: this.isCurrent ? '至今' : this.endTime,
          isCurrent: this.isCurrent
        },
        basicInfo: {
          name: this.projectName,
          role: this.projectRole
        },
        detail: {
          description: this.projectDesc,
          techStack: this.techStack,
          architecture: this.architecture
        },
        contribution: {
          responsibilities: this.responsibilities.filter(item => item.trim()),
          achievements: this.achievements.filter(item => item.trim()),
          link: this.projectLink
        }
      };

      // 保存逻辑
      console.log('项目数据:', projectData);
      uni.showToast({
        title: '保存成功',
        icon: 'success',
        success: () => {
          setTimeout(() => {
            uni.navigateBack();
          }, 1500);
        }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.project-add-page {
  min-height: 100vh;
  background-color: $uni-bg-color-grey;
  padding-bottom: calc($tabbar-height + env(safe-area-inset-bottom));
}

.page-header {
  padding: $uni-spacing-col-lg $padding-base;
  background: linear-gradient(135deg, $primary-color 0%, color.adjust($primary-color, $lightness: -10%) 100%);

  .page-title {
    display: block;
    font-size: $uni-font-size-title;
    font-weight: $font-weight-bold;
    color: $uni-text-color-inverse;
    margin-bottom: $uni-spacing-col-sm;
  }

  .page-subtitle {
    font-size: $uni-font-size-sm;
    color: rgba($uni-text-color-inverse, 0.9);
  }
}

.form-container {
  height: calc(100vh - var(--status-bar-height) - 128rpx);
  padding: $uni-spacing-col-base $padding-base;
}

.form-card {
  background: $uni-bg-color;
  border-radius: $uni-border-radius-lg;
  padding: $padding-base;
  margin-bottom: $margin-base;
  box-shadow: $box-shadow-light;

  .card-title {
    display: block;
    font-size: $uni-font-size-lg;
    font-weight: $font-weight-semibold;
    color: $text-primary;
    margin-bottom: $uni-spacing-col-lg;
    padding-bottom: $uni-spacing-col-sm;
    border-bottom: 1rpx solid $border-color-lighter;
  }
}

.form-item {
  margin-bottom: $uni-spacing-col-lg;
  position: relative;

  &:last-child {
    margin-bottom: 0;
  }
}

.form-label {
  display: block;
  font-size: $uni-font-size-base;
  font-weight: $font-weight-medium;
  color: $text-regular;
  margin-bottom: $uni-spacing-col-sm;

  &.required::after {
    content: '*';
    color: $danger-color;
    margin-left: 4rpx;
  }
}

.form-input {
  width: 100%;
  height: $input-height;
  padding: 0 $uni-spacing-row-base;
  background: $background-color;
  border: 1rpx solid $border-color-light;
  border-radius: $input-border-radius;
  font-size: $uni-font-size-base;
  color: $text-primary;
  transition: all $transition-fast;

  &:focus {
    border-color: $focus-border-color;
    box-shadow: $input-focus-shadow;
  }
}

.form-picker {
  .picker-content {
    @extend .flex-between;
    height: $input-height;
    padding: 0 $uni-spacing-row-base;
    background: $background-color;
    border: 1rpx solid $border-color-light;
    border-radius: $input-border-radius;

    .picker-text {
      font-size: $uni-font-size-base;
      color: $text-primary;
    }

    .picker-placeholder {
      font-size: $uni-font-size-base;
      color: $uni-text-color-placeholder;
    }
  }
}

.time-range {
  @extend .flex-between;
  align-items: center;

  .time-picker {
    flex: 1;
    @extend .flex-between;
    height: $input-height;
    padding: 0 $uni-spacing-row-sm;
    background: $background-color;
    border: 1rpx solid $border-color-light;
    border-radius: $input-border-radius;
    font-size: $uni-font-size-base;
    color: $text-primary;
  }

  .time-separator {
    margin: 0 $uni-spacing-row-sm;
    color: $text-secondary;
    font-size: $uni-font-size-base;
  }

  .current-checkbox {
    @extend .flex-center;
    margin-left: $uni-spacing-row-sm;

    .checkbox-icon {
      width: 32rpx;
      height: 32rpx;
      border: 1rpx solid $border-color;
      border-radius: $uni-border-radius-sm;
      margin-right: 8rpx;
      @extend .flex-center;

      &.checked {
        background: $primary-color;
        border-color: $primary-color;
      }
    }

    .checkbox-label {
      font-size: $uni-font-size-sm;
      color: $text-secondary;
    }
  }
}

.input-counter,
.textarea-counter {
  position: absolute;
  right: 0;
  bottom: -20rpx;
  font-size: $uni-font-size-sm;
  color: $text-secondary;
}

.form-textarea {
  width: 100%;
  min-height: 160rpx;
  padding: $uni-spacing-col-base $uni-spacing-row-base;
  background: $background-color;
  border: 1rpx solid $border-color-light;
  border-radius: $input-border-radius;
  font-size: $uni-font-size-base;
  color: $text-primary;
  line-height: 1.5;
  transition: all $transition-fast;

  &:focus {
    border-color: $focus-border-color;
    box-shadow: $input-focus-shadow;
  }
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: $uni-spacing-col-sm;
  margin-bottom: $uni-spacing-col-sm;

  .tech-tag {
    @extend .flex-center;
    padding: 8rpx 16rpx;
    background: $primary-lighter;
    border: 1rpx solid $primary-light;
    border-radius: $border-radius-small;

    .tag-text {
      font-size: $uni-font-size-sm;
      color: $primary-color;
      margin-right: 8rpx;
    }
  }

  .tech-input {
    flex: 1;
    min-width: 120rpx;
    height: 48rpx;
    padding: 0 $uni-spacing-row-sm;
    background: $background-color;
    border: 1rpx solid $border-color-light;
    border-radius: $border-radius-small;
    font-size: $uni-font-size-sm;
    color: $text-primary;
  }
}

.form-tip {
  font-size: $uni-font-size-sm;
  color: $text-secondary;
}

.responsibility-list,
.achievement-list {
  margin-bottom: $uni-spacing-col-base;
}

.responsibility-item,
.achievement-item {
  @extend .flex-between;
  margin-bottom: $uni-spacing-col-base;
  align-items: center;

  &:last-child {
    margin-bottom: 0;
  }
}

.responsibility-item {
  .item-number {
    width: 40rpx;
    font-size: $uni-font-size-base;
    color: $primary-color;
    font-weight: $font-weight-medium;
  }

  .item-input {
    flex: 1;
    margin: 0 $uni-spacing-row-sm;
  }
}

.achievement-item {
  .achievement-icon {
    width: 40rpx;
    @extend .flex-center;
  }

  .achievement-input {
    flex: 1;
    margin: 0 $uni-spacing-row-sm;
  }
}

.item-input,
.achievement-input {
  flex: 1;
  height: $input-height;
  padding: 0 $uni-spacing-row-sm;
  background: $background-color;
  border: 1rpx solid $border-color-light;
  border-radius: $border-radius-small;
  font-size: $uni-font-size-base;
  color: $text-primary;
}

.item-remove {
  width: 40rpx;
  @extend .flex-center;
}

.add-button {
  @extend .flex-center;
  padding: $uni-spacing-col-sm 0;

  .add-button-text {
    margin-left: 8rpx;
    font-size: $uni-font-size-base;
    color: $primary-color;
    font-weight: $font-weight-medium;
  }
}

.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: $uni-spacing-col-base $padding-base;
  background: $uni-bg-color;
  border-top: 1rpx solid $border-color-lighter;
  padding-bottom: env(safe-area-inset-bottom);
}

.action-buttons {
  display: flex;
  gap: $uni-spacing-row-base;

  .button {
    flex: 1;
    height: $button-height;
    @extend .flex-center;
    border-radius: $button-border-radius;
    font-size: $uni-font-size-base;
    font-weight: $font-weight-medium;
    transition: all $transition-fast;

    &.button-secondary {
      background: $background-color;
      border: 1rpx solid $border-color;
      color: $text-regular;

      &:active {
        background: $uni-bg-color-hover;
      }
    }

    &.button-primary {
      background: $primary-color;
      color: $uni-text-color-inverse;

      &:active {
        opacity: 0.9;
        box-shadow: $button-active-shadow;
      }

      &.disabled {
        background: $uni-text-color-placeholder;
        opacity: $button-disabled-opacity;
        cursor: not-allowed;
        pointer-events: none;
      }
    }
  }
}

.placeholder {
  color: $uni-text-color-placeholder;
  font-size: $uni-font-size-base;
}

/* 响应式调整 */
@media (max-width: $screen-sm) {
  .form-container {
    padding: $uni-spacing-col-base $uni-spacing-row-sm;
  }

  .form-card {
    padding: $padding-small;
  }

  .time-range {
    flex-wrap: wrap;

    .current-checkbox {
      width: 100%;
      margin-top: $uni-spacing-col-base;
      justify-content: flex-end;
    }
  }
}
</style>