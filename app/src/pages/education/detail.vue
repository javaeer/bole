<template>
  <view class="page-container">
    <!-- 头部 -->
    <view class="detail-header card-container">
      <view class="header-left" @click="goBack">
                <text class="header-title">{{ isEditMode ? '编辑信息' : '详情信息' }}</text>
      </view>
      
      <view v-if="!isEditMode" class="header-actions">
        <button class="btn btn-secondary" @click="toggleEditMode">
          编辑
        </button>
      </view>
      
      <view v-else class="header-actions">
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
            <view v-if="!isEditMode" class="status-tag" :class="getStatusClass(detailData)">
              {{ getStatusText(detailData) }}
            </view>
          </view>
          
          <view class="form-container">
            <!-- 学校 -->
            <view class="form-group">
              <text class="form-label required">学校名称</text>
              <input 
                v-model="formData.school" 
                class="form-input" 
                :class="{ 'error': errors.school }"
                :disabled="!isEditMode"
                placeholder="请输入学校名称"
                @blur="validateField('school')"
              />
              <text v-if="errors.school" class="error-text">{{ errors.school }}</text>
            </view>
            
            <!-- 专业 -->
            <view class="form-group">
              <text class="form-label required">专业</text>
              <input 
                v-model="formData.major" 
                class="form-input" 
                :class="{ 'error': errors.major }"
                :disabled="!isEditMode"
                placeholder="请输入专业名称"
                @blur="validateField('major')"
              />
              <text v-if="errors.major" class="error-text">{{ errors.major }}</text>
            </view>
            
            <!-- 学位 -->
            <view class="form-group">
              <text class="form-label required">学位</text>
              <picker 
                :value="degreeIndex" 
                :range="degreeOptions" 
                :disabled="!isEditMode"
                @change="onDegreeChange"
              >
                <view class="form-input" :class="{ 'error': errors.degree }">
                  {{ formData.degree || '请选择学位' }}
                </view>
              </picker>
              <text v-if="errors.degree" class="error-text">{{ errors.degree }}</text>
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
            
            <!-- 最高学历 -->
            <view v-if="isEditMode" class="form-group checkbox-group">
              <label class="checkbox-label">
                <checkbox 
                  :checked="formData.isHighest === 1" 
                  @change="onHighestChange" 
                  color="#d4af37"
                />
                <text>设为最高学历</text>
              </label>
            </view>
          </view>
        </view>
        
        <!-- 描述信息卡片 -->
        <view class="info-card card-container">
          <view class="card-header">
            <text class="card-title">描述信息</text>
          </view>
          
          <view class="form-group">
            <text class="form-label">描述</text>
            <textarea 
              v-model="formData.description" 
              class="form-textarea" 
              :disabled="!isEditMode"
              placeholder="请输入描述信息"
              maxlength="500"
              show-count
            />
          </view>
          
          <view class="form-group">
            <text class="form-label">成就/获奖</text>
            <textarea 
              v-model="formData.achievements" 
              class="form-textarea" 
              :disabled="!isEditMode"
              placeholder="请输入成就或获奖信息"
              maxlength="500"
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
              <text class="info-value">{{ dateUtils(detailData.createdAt) }}</text>
            </view>
            
            <view class="info-row">
              <text class="info-label">更新时间</text>
              <text class="info-value">{{ dateUtils(detailData.updatedAt) }}</text>
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
    <view v-if="isEditMode" class="detail-footer">
      <button class="btn btn-danger btn-block" @click="showDeleteConfirm" :disabled="saving">
        删除
      </button>
    </view>
    
    <!-- 删除确认弹窗 -->
    <uni-popup ref="deletePopup" type="dialog">
      <uni-popup-dialog 
        type="warn" 
        title="确认删除" 
        content="确定要删除这条记录吗？删除后不可恢复！" 
        :before-close="true" 
        @confirm="deleteItem" 
        @close="closeDeletePopup"
      />
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from "vue";
import { onLoad, onShow } from "@dcloudio/uni-app";
import { EducationExperienceItem } from "@/types/education-experience";
import { dateUtils } from "../../utils/date";


interface FormErrors {
  school?: string
  major?: string
  degree?: string
  startDate?: string
  endDate?: string
}

// 路由参数
const routeParams = ref<any>({})

// 响应式数据
const detailData = ref<EducationExperienceItem>({
  id: 0,
  createdAt: '',
  updatedAt: '',
  deleted: 0,
  userId: 1,
  school: '',
  major: '',
  degree: '',
  startDate: '',
  endDate: '',
  isHighest: 0,
  description: '',
  achievements: '',
  sort: 0
})

const formData = reactive({
  school: '',
  major: '',
  degree: '',
  startDate: '',
  endDate: '',
  isHighest: 0,
  description: '',
  achievements: ''
})

const errors = reactive<FormErrors>({})
const isEditMode = ref(false)
const saving = ref(false)
const deletePopup = ref()

// 学位选项
const degreeOptions = ['本科', '硕士', '博士', '专科', '其他']
const degreeIndex = computed(() => {
  const index = degreeOptions.findIndex(opt => opt === formData.degree)
  return index >= 0 ? index : 0
})

// 获取状态文本和样式
const getStatusText = (item: EducationExperienceItem) => {
  const now = new Date()
  const endDate = new Date(item.endDate)
  
  if (now < endDate) {
    return '在读'
  } else if (item.isHighest === 1) {
    return '最高学历'
  } else {
    return '已毕业'
  }
}

const getStatusClass = (item: EducationExperienceItem) => {
  const now = new Date()
  const endDate = new Date(item.endDate)
  
  if (now < endDate) {
    return 'status-warning'
  } else if (item.isHighest === 1) {
    return 'status-success'
  } else {
    return 'status-info'
  }
}

// 加载数据
const loadDetailData = async (id?: number) => {
  try {
    if (id) {
      // 模拟API请求
      const mockData: EducationExperienceItem = {
        id: id,
        createdAt: "2025-12-17 20:06:50",
        updatedAt: "2025-12-18 10:30:25",
        deleted: 0,
        userId: 1,
        school: id === 1 ? "清华大学" : id === 2 ? "北京大学" : "复旦大学",
        major: id === 1 ? "计算机科学与技术" : id === 2 ? "软件工程" : "人工智能",
        degree: id === 1 ? "本科" : id === 2 ? "硕士" : "博士",
        startDate: id === 1 ? "2014-09-01" : id === 2 ? "2018-09-01" : "2021-09-01",
        endDate: id === 1 ? "2018-06-30" : id === 2 ? "2021-06-30" : "2025-06-30",
        isHighest: id === 1 ? 0 : id === 2 ? 0 : 1,
        description: id === 1 ? "主修计算机相关课程，包括数据结构、算法、操作系统等核心课程" : 
                     id === 2 ? "深入研究软件工程理论与方法，参与多个大型项目开发" : 
                     "研究方向为机器学习与深度学习，参与多个国家级AI项目",
        achievements: id === 1 ? "获得国家奖学金，参与国家级科研项目" : 
                     id === 2 ? "发表SCI论文一篇，获得优秀毕业生称号" : 
                     "获得博士学位，发表多篇高水平论文",
        sort: id
      }
      
      detailData.value = mockData
      Object.assign(formData, {
        school: mockData.school,
        major: mockData.major,
        degree: mockData.degree,
        startDate: mockData.startDate,
        endDate: mockData.endDate,
        isHighest: mockData.isHighest,
        description: mockData.description || '',
        achievements: mockData.achievements || ''
      })
    } else {
      // 新增模式
      detailData.value = {
        id: 0,
        createdAt: new Date().toISOString().slice(0, 19).replace('T', ' '),
        updatedAt: new Date().toISOString().slice(0, 19).replace('T', ' '),
        deleted: 0,
        userId: 1,
        school: '',
        major: '',
        degree: '',
        startDate: '',
        endDate: '',
        isHighest: 0,
        description: '',
        achievements: '',
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
    case 'school':
      if (!value?.trim()) {
        errors.school = '请输入学校名称'
      } else {
        delete errors.school
      }
      break
      
    case 'major':
      if (!value?.trim()) {
        errors.major = '请输入专业名称'
      } else {
        delete errors.major
      }
      break
      
    case 'degree':
      if (!value) {
        errors.degree = '请选择学位'
      } else {
        delete errors.degree
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
  validateField('school')
  validateField('major')
  validateField('degree')
  validateField('startDate')
  validateField('endDate')
  
  return Object.keys(errors).length === 0
}

// 表单事件处理
const onDegreeChange = (e: any) => {
  const index = e.detail.value
  formData.degree = degreeOptions[index]
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

const onHighestChange = (e: any) => {
  formData.isHighest = e.detail.value ? 1 : 0
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
        id: Math.floor(Math.random() * 1000) + 4,
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
        url: '/pages/list/index?refresh=true'
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
        url: '/pages/list/index?refresh=true'
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
      school: detailData.value.school,
      major: detailData.value.major,
      degree: detailData.value.degree,
      startDate: detailData.value.startDate,
      endDate: detailData.value.endDate,
      isHighest: detailData.value.isHighest,
      description: detailData.value.description || '',
      achievements: detailData.value.achievements || ''
    })
    isEditMode.value = false
    errors.school = errors.major = errors.degree = errors.startDate = errors.endDate = undefined
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
    
    .status-tag {
      padding: 4rpx 16rpx;
      border-radius: $border-radius-small;
      font-size: $font-size-extra-small;
      font-weight: $font-weight-medium;
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
      display: flex;
      align-items: center;
      
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
      
      &[disabled] {
        background: $background-color;
        color: $text-secondary;
        cursor: not-allowed;
      }
    }
    
    .checkbox-group {
      .checkbox-label {
        display: flex;
        align-items: center;
        gap: 12rpx;
        font-size: $font-size-base;
        color: $text-primary;
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
  
  .detail-footer {
    padding: 16rpx $padding-small;
  }
}
</style>