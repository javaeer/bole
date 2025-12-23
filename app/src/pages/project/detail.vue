<template>
  <view class="page-container">
    <!-- 头部 -->
    <view class="detail-header card-container">
      <view class="header-left" @click="goBack">
                <text class="header-title">{{ isEditMode ? (detailData.id ? '编辑项目' : '添加项目') : '项目详情' }}</text>
      </view>

      <view v-if="!isEditMode && detailData.id" class="header-actions">
        <button class="btn btn-secondary" @click="toggleEditMode">
          编辑
        </button>
      </view>

      <view v-else-if="isEditMode" class="header-actions">
        <button class="btn btn-secondary" @click="cancelEdit">
          取消
        </button>
        <button class="btn btn-primary" :disabled="saving" @click="saveData">
          {{ saving ? '保存中...' : '保存' }}
        </button>
      </view>
    </view>

    <!-- 内容区域 -->
    <scroll-view class="detail-scroll" scroll-y>
      <view class="detail-content">
        <!-- 基本信息卡片 -->
        <view class="info-card card-container">
          <view class="card-header">
            <text class="card-title">基本信息</text>
            <view v-if="!isEditMode" class="status-badge">
              <view :class="['project-status', getStatusClass(detailData.status)]">
                {{ getStatusText(detailData.status) }}
              </view>
            </view>
          </view>

          <view class="form-container">
            <!-- 项目名称 -->
            <view class="form-group">
              <text class="form-label required">项目名称</text>
              <input
                v-model="formData.name"
                class="form-input"
                :class="{ 'error': errors.name }"
                :disabled="!isEditMode"
                placeholder="请输入项目名称"
                @blur="validateField('name')"
              />
              <text v-if="errors.name" class="error-text">{{ errors.name }}</text>
            </view>

            <!-- 项目状态 -->
            <view class="form-group">
              <text class="form-label required">项目状态</text>
              <picker
                :value="formData.status"
                :range="statusOptions"
                :disabled="!isEditMode"
                @change="onStatusChange"
              >
                <view class="form-input" :class="{ 'error': errors.status }">
                  {{ getStatusText(formData.status) || '请选择项目状态' }}
                </view>
              </picker>
              <text v-if="errors.status" class="error-text">{{ errors.status }}</text>
            </view>

            <!-- 开始时间 -->
            <view class="form-group">
              <text class="form-label required">开始时间</text>
              <picker
                mode="date"
                :value="formData.startDate"
                :disabled="!isEditMode"
                @change="onStartDateChange"
              >
                <view class="form-input" :class="{ 'error': errors.startDate }">
                  {{ formData.startDate || '请选择开始时间' }}
                </view>
              </picker>
              <text v-if="errors.startDate" class="error-text">{{ errors.startDate }}</text>
            </view>

            <!-- 结束时间 -->
            <view class="form-group">
              <text class="form-label required">结束时间</text>
              <picker
                mode="date"
                :value="formData.endDate"
                :disabled="!isEditMode"
                @change="onEndDateChange"
              >
                <view class="form-input" :class="{ 'error': errors.endDate }">
                  {{ formData.endDate || '请选择结束时间' }}
                </view>
              </picker>
              <text v-if="errors.endDate" class="error-text">{{ errors.endDate }}</text>
            </view>

            <!-- 项目进度 -->
            <view v-if="formData.startDate && formData.endDate" class="form-group progress-display">
              <text class="progress-label">项目进度</text>
              <view class="progress-info">
                <text class="progress-value">{{ calculateProgress() }}%</text>
                <view class="progress-bar">
                  <view
                    class="progress-fill"
                    :class="getProgressClass()"
                    :style="{ width: `${calculateProgress()}%` }"
                  ></view>
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- 项目描述卡片 -->
        <view class="info-card card-container">
          <view class="card-header">
            <text class="card-title">项目描述</text>
          </view>

          <view class="form-group">
            <text class="form-label">项目描述</text>
            <textarea
              v-model="formData.description"
              class="form-textarea"
              :disabled="!isEditMode"
              placeholder="请输入项目描述，包括项目背景、目标、技术栈等"
              maxlength="2000"
              show-count
            />
          </view>
        </view>

        <!-- 项目成就卡片 -->
        <view class="info-card card-container">
          <view class="card-header">
            <text class="card-title">项目成就</text>
          </view>

          <view class="form-group">
            <text class="form-label">项目成就</text>
            <textarea
              v-model="formData.achievements"
              class="form-textarea"
              :disabled="!isEditMode"
              placeholder="请输入项目取得的成就、成果、获奖情况等"
              maxlength="2000"
              show-count
            />
          </view>
        </view>

        <!-- 系统信息卡片（只读） -->
        <view v-if="detailData.id" class="info-card card-container">
          <view class="card-header">
            <text class="card-title">系统信息</text>
          </view>

          <view class="system-info">
            <view class="info-row">
              <text class="info-label">创建时间</text>
              <text class="info-value">{{ dateUtils.format(detailData.createdAt) }}</text>
            </view>

            <view class="info-row">
              <text class="info-label">更新时间</text>
              <text class="info-value">{{ dateUtils.format(detailData.updatedAt) }}</text>
            </view>

            <view class="info-row">
              <text class="info-label">排序序号</text>
              <text class="info-value">{{ detailData.sort }}</text>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 底部操作栏（编辑模式下） -->
    <view v-if="isEditMode && detailData.id" class="detail-footer">
      <button class="btn btn-danger btn-block" @click="showDeleteConfirm" :disabled="saving">
        删除
      </button>
    </view>

    <!-- 删除确认弹窗 -->
    <uni-popup ref="deletePopup" type="dialog">
      <uni-popup-dialog
        type="warn"
        title="确认删除"
        content="确定要删除这个项目吗？删除后不可恢复！"
        :before-close="true"
        @confirm="deleteItem"
        @close="closeDeletePopup"
      />
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from "vue";
import { onLoad, onShow } from "@dcloudio/uni-app";
import { ProjectExperienceItem } from "@/types/project-experience";
import { dateUtils } from "@/utils/date";


interface FormErrors {
  name?: string
  status?: string
  startDate?: string
  endDate?: string
}

// 路由参数
const routeParams = ref<any>({})

// 响应式数据
const detailData = ref<ProjectExperienceItem>({
  id: 0,
  createdAt: '',
  updatedAt: '',
  deleted: 0,
  userId: 1,
  name: '',
  status: 0,
  startDate: '',
  endDate: '',
  description: '',
  achievements: null,
  sort: 0
})

const formData = reactive({
  name: '',
  status: 0,
  startDate: '',
  endDate: '',
  description: '',
  achievements: null as string | null
})

const errors = reactive<FormErrors>({})
const isEditMode = ref(false)
const saving = ref(false)
const deletePopup = ref()

// 状态选项
const statusOptions = ['未开始', '进行中', '已完成', '已暂停']

// 获取状态文本
const getStatusText = (status: number): string => {
  switch(status) {
    case 0: return '未开始'
    case 1: return '进行中'
    case 2: return '已完成'
    case 3: return '已暂停'
    default: return '未知'
  }
}

// 获取状态样式类
const getStatusClass = (status: number): string => {
  switch(status) {
    case 0: return 'status-pending'
    case 1: return 'status-in-progress'
    case 2: return 'status-completed'
    case 3: return 'status-paused'
    default: return 'status-default'
  }
}

// 计算项目进度
const calculateProgress = (): number => {
  if (!formData.startDate || !formData.endDate) return 0

  const start = new Date(formData.startDate).getTime()
  const end = new Date(formData.endDate).getTime()
  const now = new Date().getTime()

  if (now <= start) return 0
  if (now >= end) return 100

  const total = end - start
  const passed = now - start
  return Math.round((passed / total) * 100)
}

// 获取进度条样式类
const getProgressClass = (): string => {
  const progress = calculateProgress()
  if (progress >= 100) return 'progress-completed'
  if (progress >= 70) return 'progress-late'
  if (progress >= 30) return 'progress-middle'
  return 'progress-early'
}

// 监听日期变化，更新进度
watch(() => [formData.startDate, formData.endDate], () => {
  // 进度显示会自动更新
}, { deep: true })

// 加载数据
const loadDetailData = async (id?: number) => {
  try {
    if (id) {
      // 模拟API请求
      const mockData: ProjectExperienceItem = {
        id: id,
        createdAt: "2025-12-17 20:06:50",
        updatedAt: "2025-12-18 10:30:25",
        deleted: 0,
        userId: 1,
        name: "分布式消息队列系统",
        status: 2,
        startDate: "2022-01-01",
        endDate: "2022-12-31",
        description: "设计并实现高可用分布式消息队列，支持千万级消息吞吐，保证数据不丢失。",
        achievements: "系统稳定运行一年，处理消息超过10亿条，获得公司技术创新奖",
        sort: 1
      }

      detailData.value = mockData
      Object.assign(formData, {
        name: mockData.name,
        status: mockData.status,
        startDate: mockData.startDate,
        endDate: mockData.endDate,
        description: mockData.description,
        achievements: mockData.achievements
      })
    } else {
      // 新增模式
      detailData.value = {
        id: 0,
        createdAt: new Date().toISOString().slice(0, 19).replace('T', ' '),
        updatedAt: new Date().toISOString().slice(0, 19).replace('T', ' '),
        deleted: 0,
        userId: 1,
        name: '',
        status: 0,
        startDate: '',
        endDate: '',
        description: '',
        achievements: null,
        sort: 0
      }

      isEditMode.value = true
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    uni.showToast({
      title: '加载失败',
      icon: 'error'
    })
  }
}

// 表单验证
const validateField = (field: keyof FormErrors) => {
  const value = formData[field as keyof typeof formData]

  switch (field) {
    case 'name':
      if (!value?.toString().trim()) {
        errors.name = '请输入项目名称'
      } else {
        delete errors.name
      }
      break

    case 'status':
      if (value === undefined || value === null) {
        errors.status = '请选择项目状态'
      } else {
        delete errors.status
      }
      break

    case 'startDate':
      if (!value) {
        errors.startDate = '请选择开始时间'
      } else {
        delete errors.startDate
      }
      break

    case 'endDate':
      if (!value) {
        errors.endDate = '请选择结束时间'
      } else if (formData.startDate && new Date(value) < new Date(formData.startDate)) {
        errors.endDate = '结束时间不能早于开始时间'
      } else {
        delete errors.endDate
      }
      break
  }
}

const validateForm = (): boolean => {
  validateField('name')
  validateField('status')
  validateField('startDate')
  validateField('endDate')

  return Object.keys(errors).length === 0
}

// 表单事件处理
const onStatusChange = (e: any) => {
  formData.status = e.detail.value
  validateField('status')
}

const onStartDateChange = (e: any) => {
  formData.startDate = e.detail.value
  validateField('startDate')
  validateField('endDate')
}

const onEndDateChange = (e: any) => {
  formData.endDate = e.detail.value
  validateField('endDate')
}

// 保存数据
const saveData = async () => {
  if (!validateForm()) {
    uni.showToast({
      title: '请填写完整信息',
      icon: 'error'
    })
    return
  }

  saving.value = true

  try {
    // 模拟API请求
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 更新数据
    const now = new Date().toISOString().slice(0, 19).replace('T', ' ')

    if (detailData.value.id) {
      // 更新
      detailData.value = {
        ...detailData.value,
        ...formData,
        updatedAt: now
      }
    } else {
      // 新增
      detailData.value = {
        id: Math.floor(Math.random() * 1000) + 7,
        createdAt: now,
        updatedAt: now,
        deleted: 0,
        userId: 1,
        ...formData,
        sort: Math.floor(Math.random() * 10) + 1
      }
    }

    uni.showToast({
      title: '保存成功',
      icon: 'success'
    })

    // 返回列表页并刷新
    setTimeout(() => {
      uni.navigateTo({
        url: '/pages/project/list?refresh=true'
      })
    }, 1500)

  } catch (error) {
    console.error('保存失败:', error)
    uni.showToast({
      title: '保存失败',
      icon: 'error'
    })
  } finally {
    saving.value = false
  }
}

// 删除项目
const showDeleteConfirm = () => {
  deletePopup.value.open()
}

const deleteItem = async () => {
  try {
    // 模拟API请求
    await new Promise(resolve => setTimeout(resolve, 800))

    uni.showToast({
      title: '删除成功',
      icon: 'success'
    })

    // 返回列表页并刷新
    setTimeout(() => {
      uni.navigateTo({
        url: '/pages/project/list?refresh=true'
      })
    }, 1000)

  } catch (error) {
    console.error('删除失败:', error)
    uni.showToast({
      title: '删除失败',
      icon: 'error'
    })
  }
}

const closeDeletePopup = () => {
  deletePopup.value.close()
}

// 切换编辑模式
const toggleEditMode = () => {
  isEditMode.value = true
}

const cancelEdit = () => {
  if (detailData.value.id) {
    // 恢复原始数据
    Object.assign(formData, {
      name: detailData.value.name,
      status: detailData.value.status,
      startDate: detailData.value.startDate,
      endDate: detailData.value.endDate,
      description: detailData.value.description,
      achievements: detailData.value.achievements
    })
    isEditMode.value = false
    // 清空错误信息
    Object.keys(errors).forEach(key => delete errors[key as keyof FormErrors])
  } else {
    // 如果是新增，返回列表页
    uni.navigateBack()
  }
}

// 返回上一页
const goBack = () => {
  uni.navigateBack()
}

// 生命周期
onLoad((options: any) => {
  routeParams.value = options
  const id = options?.id ? parseInt(options.id) : undefined
  const edit = options?.edit === 'true'

  if (id) {
    loadDetailData(id)
    isEditMode.value = edit
  } else {
    loadDetailData()
  }
})

onShow(() => {
  // 页面显示时的逻辑
})
</script>

<style lang="scss">

.page-container {
  min-height: 100vh;
  background-color: $background-color;
  padding-bottom: calc(env(safe-area-inset-bottom) + 120rpx);
}

.detail-header {
  position: sticky;
  top: 0;
  z-index: $z-index-base;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx $padding-base;
  background: $background-color-white;
  border-radius: 0 0 $border-radius $border-radius;
  box-shadow: $box-shadow;

  .header-left {
    display: flex;
    align-items: center;
    gap: 20rpx;

    .header-title {
      font-size: $font-size-medium;
      font-weight: $font-weight-medium;
      color: $text-primary;
    }
  }

  .header-actions {
    display: flex;
    gap: $margin-mini;

    .btn {
      padding: 12rpx 24rpx;
      font-size: $font-size-small;
      min-width: 80rpx;
    }
  }

  .status-badge {
    .project-status {
      padding: 4rpx 12rpx;
      border-radius: $border-radius-small;
      font-size: $font-size-extra-small;
      font-weight: $font-weight-medium;

      &.status-pending {
        background: $info-bg;
        color: $info-color;
        border: 1rpx solid $info-border;
      }

      &.status-in-progress {
        background: $primary-light;
        color: $primary-color;
        border: 1rpx solid $primary-border;
      }

      &.status-completed {
        background: $success-bg;
        color: $success-color;
        border: 1rpx solid $success-border;
      }

      &.status-paused {
        background: $warning-bg;
        color: $warning-color;
        border: 1rpx solid $warning-border;
      }

      &.status-default {
        background: $background-color;
        color: $text-secondary;
        border: 1rpx solid $border-color-light;
      }
    }
  }
}

.detail-scroll {
  height: calc(100vh - 120rpx);
}

.detail-content {
  padding: $padding-small;
}

.info-card {
  margin-bottom: $margin-base;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $margin-base;
    padding-bottom: $margin-mini;
    border-bottom: 1rpx solid $border-color-extra-light;

    .card-title {
      font-size: $font-size-medium;
      font-weight: $font-weight-medium;
      color: $text-primary;
    }
  }
}

.form-container {
  .form-group {
    margin-bottom: $margin-base;

    .form-label {
      display: block;
      font-size: $font-size-base;
      font-weight: $font-weight-medium;
      color: $text-primary;
      margin-bottom: $margin-mini;

      &.required::after {
        content: '*';
        color: $danger-color;
        margin-left: 4rpx;
      }
    }

    .form-input {
      width: 100%;
      padding: 20rpx 24rpx;
      border: 2rpx solid $border-color-lighter;
      border-radius: $border-radius;
      font-size: $font-size-base;
      color: $text-primary;
      background: $background-color-white;
      transition: all $transition-fast $ease-in-out;
      min-height: 80rpx;
      box-sizing: border-box;

      &:focus {
        border-color: $primary-color;
        box-shadow: $input-focus-shadow;
        outline: none;
      }

      &.error {
        border-color: $danger-color;
        box-shadow: $input-error-shadow;
      }

      &[disabled] {
        background: $background-color;
        color: $text-secondary;
        cursor: not-allowed;
      }
    }

    .progress-display {
      padding: 16rpx;
      background: $background-color;
      border-radius: $border-radius;

      .progress-label {
        display: block;
        font-size: $font-size-small;
        color: $text-secondary;
        margin-bottom: 12rpx;
      }

      .progress-info {
        display: flex;
        align-items: center;
        gap: 20rpx;

        .progress-value {
          font-size: $font-size-base;
          font-weight: $font-weight-medium;
          color: $primary-color;
          min-width: 60rpx;
        }

        .progress-bar {
          flex: 1;
          height: 12rpx;
          background: $background-color-white;
          border-radius: 6rpx;
          overflow: hidden;

          .progress-fill {
            height: 100%;
            border-radius: 6rpx;

            &.progress-early {
              background: linear-gradient(90deg, $success-color, color.adjust($success-color, $lightness:  20%));
            }

            &.progress-middle {
              background: linear-gradient(90deg, $primary-color, color.adjust($primary-color, $lightness:  20%));
            }

            &.progress-late {
              background: linear-gradient(90deg, $warning-color, color.adjust($warning-color, $lightness:  20%));
            }

            &.progress-completed {
              background: linear-gradient(90deg, $info-color, color.adjust($info-color, $lightness:  20%));
            }
          }
        }
      }
    }

    .form-textarea {
      width: 100%;
      padding: 20rpx 24rpx;
      border: 2rpx solid $border-color-lighter;
      border-radius: $border-radius;
      font-size: $font-size-base;
      color: $text-primary;
      background: $background-color-white;
      min-height: 200rpx;
      line-height: 1.5;
      box-sizing: border-box;

      &[disabled] {
        background: $background-color;
        color: $text-secondary;
        cursor: not-allowed;
      }
    }

    .error-text {
      display: block;
      font-size: $font-size-extra-small;
      color: $danger-color;
      margin-top: 8rpx;
    }
  }
}

.system-info {
  .info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16rpx 0;
    border-bottom: 1rpx solid $border-color-extra-light;

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

.detail-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx $padding-base;
  background: $background-color-white;
  border-top: 1rpx solid $border-color-light;
  z-index: $z-index-base;

  .btn-block {
    width: 100%;
  }
}

@media (max-width: $screen-md) {
  .detail-header {
    padding: 16rpx $padding-small;
  }

  .detail-content {
    padding: $padding-mini;
  }

  .form-container {
    .form-group {
      .progress-display {
        .progress-info {
          flex-direction: column;
          align-items: flex-start;
          gap: 12rpx;

          .progress-bar {
            width: 100%;
          }
        }
      }
    }
  }

  .detail-footer {
    padding: 16rpx $padding-small;
  }
}
</style>