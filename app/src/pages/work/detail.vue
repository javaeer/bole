<template>
  <view class="page-container">
    <!-- 头部 -->
    <view class="detail-header card-container">
      <view class="header-left" @click="goBack">
                <text class="header-title">{{ isEditMode ? (detailData.id ? '编辑工作经历' : '添加工作经历') : '工作经历详情' }}</text>
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
              <view :class="['work-status', detailData.isCurrent ? 'status-current' : 'status-past']">
                {{ detailData.isCurrent ? '在职' : '离职' }}
              </view>
            </view>
          </view>
          
          <view class="form-container">
            <!-- 公司ID -->
            <view class="form-group">
              <text class="form-label required">公司ID</text>
              <input 
                v-model.number="formData.companyId" 
                type="number"
                class="form-input" 
                :class="{ 'error': errors.companyId }"
                :disabled="!isEditMode"
                placeholder="请输入公司ID"
                @blur="validateField('companyId')"
              />
              <text v-if="errors.companyId" class="error-text">{{ errors.companyId }}</text>
            </view>
            
            <!-- 职位 -->
            <view class="form-group">
              <text class="form-label required">职位</text>
              <input 
                v-model="formData.position" 
                class="form-input" 
                :class="{ 'error': errors.position }"
                :disabled="!isEditMode"
                placeholder="请输入职位名称"
                @blur="validateField('position')"
              />
              <text v-if="errors.position" class="error-text">{{ errors.position }}</text>
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
              <text class="form-label" :class="{ 'required': !formData.isCurrent }">结束时间</text>
              <view class="date-input-group">
                <picker 
                  v-if="!formData.isCurrent"
                  mode="date" 
                  :value="formData.endDate" 
                  :disabled="!isEditMode"
                  @change="onEndDateChange"
                >
                  <view class="form-input" :class="{ 'error': errors.endDate }">
                    {{ formData.endDate || '请选择结束时间' }}
                  </view>
                </picker>
                <view v-else class="form-input disabled-input">
                  至今
                </view>
                <view class="checkbox-container">
                  <label class="checkbox-label">
                    <checkbox 
                      :checked="formData.isCurrent" 
                      @change="onCurrentChange" 
                      :disabled="!isEditMode"
                      color="#d4af37"
                    />
                    <text>至今在职</text>
                  </label>
                </view>
              </view>
              <text v-if="errors.endDate" class="error-text">{{ errors.endDate }}</text>
            </view>
            
            <!-- 工作时长 -->
            <view v-if="formData.startDate && (formData.endDate || formData.isCurrent)" class="form-group duration-display">
              <text class="duration-label">工作时长</text>
              <text class="duration-value">{{ calculateDurationDisplay() }}</text>
            </view>
          </view>
        </view>
        
        <!-- 工作描述卡片 -->
        <view class="info-card card-container">
          <view class="card-header">
            <text class="card-title">工作描述</text>
          </view>
          
          <view class="form-group">
            <text class="form-label">工作职责描述</text>
            <textarea 
              v-model="formData.description" 
              class="form-textarea" 
              :disabled="!isEditMode"
              placeholder="请输入工作职责描述"
              maxlength="1000"
              show-count
            />
          </view>
        </view>
        
        <!-- 工作成就卡片 -->
        <view class="info-card card-container">
          <view class="card-header">
            <text class="card-title">工作成就</text>
          </view>
          
          <view class="form-group">
            <text class="form-label">主要成就</text>
            <textarea 
              v-model="formData.achievements" 
              class="form-textarea" 
              :disabled="!isEditMode"
              placeholder="请输入在工作期间取得的主要成就、项目成果等"
              maxlength="1000"
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
        content="确定要删除这份工作经历吗？删除后不可恢复！" 
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
import { WorkExperienceItem } from "@/types/work-experience";
import { dateUtils } from "@/utils/date";


interface FormErrors {
  companyId?: string
  position?: string
  startDate?: string
  endDate?: string
}

// 路由参数
const routeParams = ref<any>({})

// 响应式数据
const detailData = ref<WorkExperienceItem>({
  id: 0,
  createdAt: '',
  updatedAt: '',
  deleted: 0,
  userId: 1,
  companyId: 0,
  position: '',
  startDate: '',
  endDate: '',
  isCurrent: false,
  description: '',
  achievements: null,
  sort: 0
})

const formData = reactive({
  companyId: 0,
  position: '',
  startDate: '',
  endDate: '',
  isCurrent: false,
  description: '',
  achievements: null as string | null
})

const errors = reactive<FormErrors>({})
const isEditMode = ref(false)
const saving = ref(false)
const deletePopup = ref()

// 监听当前状态变化
watch(() => formData.isCurrent, (newVal) => {
  if (newVal) {
    formData.endDate = ''
    delete errors.endDate
  }
})

// 计算工作时长显示
const calculateDurationDisplay = () => {
  if (!formData.startDate || (!formData.endDate && !formData.isCurrent)) return ''
  
  const start = new Date(formData.startDate)
  const end = formData.isCurrent ? new Date() : new Date(formData.endDate || new Date())
  
  const years = end.getFullYear() - start.getFullYear()
  const months = end.getMonth() - start.getMonth()
  
  let totalMonths = years * 12 + months
  if (end.getDate() < start.getDate()) {
    totalMonths--
  }
  
  if (totalMonths < 0) totalMonths = 0
  
  const yearsPart = totalMonths >= 12 ? Math.floor(totalMonths / 12) + '年' : ''
  const monthsPart = totalMonths % 12 > 0 ? (totalMonths % 12) + '个月' : ''
  
  return `${yearsPart}${monthsPart}`.trim() || '0个月'
}

// 加载数据
const loadDetailData = async (id?: number) => {
  try {
    if (id) {
      // 模拟API请求
      const mockData: WorkExperienceItem = {
        id: id,
        createdAt: "2025-12-17 20:06:50",
        updatedAt: "2025-12-18 09:15:30",
        deleted: 0,
        userId: 1,
        companyId: 101,
        position: "软件工程师",
        startDate: "2021-07-01",
        endDate: "2023-06-30",
        isCurrent: false,
        description: "负责核心业务功能开发，参与系统架构设计，主导多个重要模块的开发工作。",
        achievements: "成功交付3个重大项目，优化系统性能提升30%，获得年度优秀员工奖",
        sort: 1
      }
      
      detailData.value = mockData
      Object.assign(formData, {
        companyId: mockData.companyId,
        position: mockData.position,
        startDate: mockData.startDate,
        endDate: mockData.endDate,
        isCurrent: mockData.isCurrent,
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
        companyId: 0,
        position: '',
        startDate: '',
        endDate: '',
        isCurrent: false,
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
    case 'companyId':
      const companyId = Number(value)
      if (isNaN(companyId) || companyId <= 0) {
        errors.companyId = '请输入有效的公司ID'
      } else {
        delete errors.companyId
      }
      break
      
    case 'position':
      if (!value?.toString().trim()) {
        errors.position = '请输入职位名称'
      } else {
        delete errors.position
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
      if (!formData.isCurrent) {
        if (!value) {
          errors.endDate = '请选择结束时间'
        } else if (formData.startDate && new Date(value) < new Date(formData.startDate)) {
          errors.endDate = '结束时间不能早于开始时间'
        } else {
          delete errors.endDate
        }
      }
      break
  }
}

const validateForm = (): boolean => {
  validateField('companyId')
  validateField('position')
  validateField('startDate')
  if (!formData.isCurrent) {
    validateField('endDate')
  }
  
  return Object.keys(errors).length === 0
}

// 表单事件处理
const onStartDateChange = (e: any) => {
  formData.startDate = e.detail.value
  validateField('startDate')
  validateField('endDate')
}

const onEndDateChange = (e: any) => {
  formData.endDate = e.detail.value
  validateField('endDate')
}

const onCurrentChange = (e: any) => {
  formData.isCurrent = e.detail.value
  if (e.detail.value) {
    formData.endDate = ''
    delete errors.endDate
  } else {
    // 如果从"至今"切换到"离职"，需要重新验证结束时间
    validateField('endDate')
  }
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
        id: Math.floor(Math.random() * 1000) + 5,
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
        url: '/pages/work-list/index?refresh=true'
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
        url: '/pages/work-list/index?refresh=true'
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
      companyId: detailData.value.companyId,
      position: detailData.value.position,
      startDate: detailData.value.startDate,
      endDate: detailData.value.endDate,
      isCurrent: detailData.value.isCurrent,
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
    .work-status {
      padding: 4rpx 12rpx;
      border-radius: $border-radius-small;
      font-size: $font-size-extra-small;
      font-weight: $font-weight-medium;
      
      &.status-current {
        background: $success-bg;
        color: $success-color;
        border: 1rpx solid $success-border;
      }
      
      &.status-past {
        background: $info-bg;
        color: $info-color;
        border: 1rpx solid $info-border;
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
      
      &.disabled-input {
        background: $background-color;
        color: $text-secondary;
        cursor: not-allowed;
      }
    }
    
    .date-input-group {
      display: flex;
      gap: $margin-mini;
      
      .form-input {
        flex: 1;
      }
      
      .checkbox-container {
        flex-shrink: 0;
        display: flex;
        align-items: center;
        
        .checkbox-label {
          display: flex;
          align-items: center;
          gap: 8rpx;
          font-size: $font-size-small;
          color: $text-primary;
          white-space: nowrap;
        }
      }
    }
    
    .duration-display {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16rpx;
      background: $background-color;
      border-radius: $border-radius;
      
      .duration-label {
        font-size: $font-size-small;
        color: $text-secondary;
      }
      
      .duration-value {
        font-size: $font-size-base;
        font-weight: $font-weight-medium;
        color: $primary-color;
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
      min-height: 160rpx;
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
      .date-input-group {
        flex-direction: column;
        gap: 12rpx;
      }
    }
  }
  
  .detail-footer {
    padding: 16rpx $padding-small;
  }
}
</style>