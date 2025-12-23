<template>
  <view class="page-container">
    <!-- 头部 -->
    <view class="detail-header card-container">
      <view class="header-left" @click="goBack">
                <text class="header-title">{{ isEditMode ? (detailData.id ? '编辑技能' : '添加技能') : '技能详情' }}</text>
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
            <view v-if="!isEditMode" class="status-badges">
              <view v-if="detailData.isPublic" class="badge badge-public">
                公开
              </view>
              <view v-if="detailData.isCertified" class="badge badge-certified">
                已认证
              </view>
            </view>
          </view>

          <view class="form-container">
            <!-- 技能名称 -->
            <view class="form-group">
              <text class="form-label required">技能名称</text>
              <input
                v-model="formData.name"
                class="form-input"
                :class="{ 'error': errors.name }"
                :disabled="!isEditMode"
                placeholder="请输入技能名称"
                @blur="validateField('name')"
              />
              <text v-if="errors.name" class="error-text">{{ errors.name }}</text>
            </view>

            <!-- 分类 -->
            <view class="form-group">
              <text class="form-label required">分类</text>
              <picker
                :value="formData.category"
                :range="categoryOptions"
                :disabled="!isEditMode"
                @change="onCategoryChange"
              >
                <view class="form-input" :class="{ 'error': errors.category }">
                  {{ formData.category || '请选择分类' }}
                </view>
              </picker>
              <text v-if="errors.category" class="error-text">{{ errors.category }}</text>
            </view>

            <!-- 等级 -->
            <view class="form-group">
              <text class="form-label required">等级</text>
              <picker
                :value="levelIndex"
                :range="levelOptions"
                :disabled="!isEditMode"
                @change="onLevelChange"
              >
                <view class="form-input" :class="{ 'error': errors.level }">
                  {{ formData.level || '请选择等级' }}
                </view>
              </picker>
              <text v-if="errors.level" class="error-text">{{ errors.level }}</text>
            </view>

            <!-- 经验年数 -->
            <view class="form-group">
              <text class="form-label required">经验年数</text>
              <view class="input-with-unit">
                <input
                  v-model.number="formData.experienceYears"
                  type="number"
                  class="form-input"
                  :class="{ 'error': errors.experienceYears }"
                  :disabled="!isEditMode"
                  placeholder="请输入经验年数"
                  @blur="validateField('experienceYears')"
                />
                <text class="input-unit">年</text>
              </view>
              <text v-if="errors.experienceYears" class="error-text">{{ errors.experienceYears }}</text>
            </view>

            <!-- 熟练度百分比 -->
            <view class="form-group">
              <text class="form-label required">熟练度</text>
              <view class="proficiency-input">
                <input
                  v-model.number="formData.proficiencyPercent"
                  type="number"
                  class="form-input"
                  :class="{ 'error': errors.proficiencyPercent }"
                  :disabled="!isEditMode"
                  placeholder="0-100"
                  @blur="validateField('proficiencyPercent')"
                />
                <text class="input-percent">%</text>
                <view class="proficiency-display">
                  <view
                    class="proficiency-bar"
                    :style="{ width: `${formData.proficiencyPercent || 0}%` }"
                  ></view>
                </view>
              </view>
              <text v-if="errors.proficiencyPercent" class="error-text">{{ errors.proficiencyPercent }}</text>
            </view>
          </view>
        </view>

        <!-- 描述信息卡片 -->
        <view class="info-card card-container">
          <view class="card-header">
            <text class="card-title">描述信息</text>
          </view>

          <view class="form-group">
            <text class="form-label">技能描述</text>
            <textarea
              v-model="formData.description"
              class="form-textarea"
              :disabled="!isEditMode"
              placeholder="请输入技能描述"
              maxlength="1000"
              show-count
            />
          </view>

          <!-- 标签 -->
          <view class="form-group">
            <text class="form-label">标签</text>
            <input
              v-model="tagInput"
              class="form-input"
              :disabled="!isEditMode"
              placeholder="输入标签后按回车或逗号添加"
              @keyup.enter="addTag"
              @keyup.space="addTag"
              @blur="addTag"
            />
            <view class="tags-container" v-if="tagArray.length > 0">
              <view
                v-for="(tag, index) in tagArray"
                :key="index"
                class="tag-item"
              >
                <text>{{ tag }}</text>
                <uni-icons
                  v-if="isEditMode"
                  type="close"
                  size="12"
                  color="#999"
                  @click="removeTag(index)"
                />
              </view>
            </view>
            <text class="form-hint">用逗号或回车分隔多个标签</text>
          </view>
        </view>

        <!-- 认证信息卡片 -->
        <view class="info-card card-container">
          <view class="card-header">
            <text class="card-title">认证信息</text>
          </view>

          <view class="form-group checkbox-group">
            <label class="checkbox-label">
              <checkbox
                :checked="formData.isCertified"
                @change="onCertifiedChange"
                :disabled="!isEditMode"
                color="#d4af37"
              />
              <text>持有相关认证</text>
            </label>
          </view>

          <view v-if="formData.isCertified" class="certificate-fields">
            <view class="form-group">
              <text class="form-label">认证名称</text>
              <input
                v-model="formData.certificateName"
                class="form-input"
                :disabled="!isEditMode"
                placeholder="请输入认证名称"
              />
            </view>

            <view class="form-group">
              <text class="form-label">认证日期</text>
              <picker
                mode="date"
                :value="formData.certificateDate"
                :disabled="!isEditMode"
                @change="onCertificateDateChange"
              >
                <view class="form-input">
                  {{ formData.certificateDate || '请选择认证日期' }}
                </view>
              </picker>
            </view>
          </view>
        </view>

        <!-- 隐私设置卡片 -->
        <view class="info-card card-container">
          <view class="card-header">
            <text class="card-title">隐私设置</text>
          </view>

          <view class="form-group checkbox-group">
            <label class="checkbox-label">
              <checkbox
                :checked="formData.isPublic"
                @change="onPublicChange"
                :disabled="!isEditMode"
                color="#d4af37"
              />
              <text>公开此技能（其他人可见）</text>
            </label>
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
        content="确定要删除这个技能吗？删除后不可恢复！"
        :before-close="true"
        @confirm="deleteItem"
        @close="closeDeletePopup"
      />
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { dateUtils } from "@/utils/date";

interface FormErrors {
  name?: string
  category?: string
  level?: string
  experienceYears?: string
  proficiencyPercent?: string
}

// 路由参数
const routeParams = ref<any>({})

// 响应式数据
const detailData = ref<SkillItem>({
  id: 0,
  createdAt: '',
  updatedAt: '',
  deleted: 0,
  userId: 1,
  name: '',
  level: '',
  category: '',
  description: '',
  proficiencyPercent: 0,
  experienceYears: 0,
  isCertified: false,
  certificateName: null,
  certificateDate: null,
  tags: '',
  isPublic: true,
  sort: 0
})

const formData = reactive({
  name: '',
  level: '',
  category: '',
  description: '',
  proficiencyPercent: 0,
  experienceYears: 0,
  isCertified: false,
  certificateName: null as string | null,
  certificateDate: null as string | null,
  tags: '',
  isPublic: true
})

const tagInput = ref('')
const tagArray = ref<string[]>([])
const errors = reactive<FormErrors>({})
const isEditMode = ref(false)
const saving = ref(false)
const deletePopup = ref()

// 选项
const categoryOptions = ['编程语言', '前端框架', '后端框架', '数据库', '数据分析', '人工智能', '运维部署', '工具软件', '其他']
const levelOptions = ['初级', '中级', '高级', '专家']

// 计算属性
const levelIndex = computed(() => {
  const index = levelOptions.findIndex(opt => opt === formData.level)
  return index >= 0 ? index : 0
})

// 监听标签数组变化，更新表单数据
watch(tagArray, (newTags) => {
  formData.tags = newTags.join(',')
}, { deep: true })

// 加载数据
const loadDetailData = async (id?: number) => {
  try {
    if (id) {
      // 模拟API请求
      const mockData: SkillItem = {
        id: id,
        createdAt: "2025-12-17 20:06:50",
        updatedAt: "2025-12-18 14:30:25",
        deleted: 0,
        userId: 1,
        name: "Java编程",
        level: "高级",
        category: "编程语言",
        description: "熟练掌握Java语言特性，包括集合、多线程、IO等。熟悉Spring框架，有微服务开发经验。",
        proficiencyPercent: 85,
        experienceYears: 5.5,
        isCertified: true,
        certificateName: "Oracle Certified Professional",
        certificateDate: "2022-03-15",
        tags: "Java,后端,编程,Spring",
        isPublic: true,
        sort: 1
      }

      detailData.value = mockData
      Object.assign(formData, {
        name: mockData.name,
        level: mockData.level,
        category: mockData.category,
        description: mockData.description,
        proficiencyPercent: mockData.proficiencyPercent,
        experienceYears: mockData.experienceYears,
        isCertified: mockData.isCertified,
        certificateName: mockData.certificateName,
        certificateDate: mockData.certificateDate,
        tags: mockData.tags,
        isPublic: mockData.isPublic
      })

      // 初始化标签数组
      tagArray.value = mockData.tags ? mockData.tags.split(',').map(tag => tag.trim()).filter(tag => tag) : []
    } else {
      // 新增模式
      detailData.value = {
        id: 0,
        createdAt: new Date().toISOString().slice(0, 19).replace('T', ' '),
        updatedAt: new Date().toISOString().slice(0, 19).replace('T', ' '),
        deleted: 0,
        userId: 1,
        name: '',
        level: '',
        category: '',
        description: '',
        proficiencyPercent: 0,
        experienceYears: 0,
        isCertified: false,
        certificateName: null,
        certificateDate: null,
        tags: '',
        isPublic: true,
        sort: 0
      }

      isEditMode.value = true
      tagArray.value = []
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
        errors.name = '请输入技能名称'
      } else {
        delete errors.name
      }
      break

    case 'category':
      if (!value) {
        errors.category = '请选择分类'
      } else {
        delete errors.category
      }
      break

    case 'level':
      if (!value) {
        errors.level = '请选择等级'
      } else {
        delete errors.level
      }
      break

    case 'experienceYears':
      const expValue = Number(value)
      if (isNaN(expValue) || expValue < 0) {
        errors.experienceYears = '请输入有效的经验年数'
      } else if (expValue > 50) {
        errors.experienceYears = '经验年数不能超过50年'
      } else {
        delete errors.experienceYears
      }
      break

    case 'proficiencyPercent':
      const percentValue = Number(value)
      if (isNaN(percentValue) || percentValue < 0 || percentValue > 100) {
        errors.proficiencyPercent = '请输入0-100之间的数值'
      } else {
        delete errors.proficiencyPercent
      }
      break
  }
}

const validateForm = (): boolean => {
  validateField('name')
  validateField('category')
  validateField('level')
  validateField('experienceYears')
  validateField('proficiencyPercent')

  return Object.keys(errors).length === 0
}

// 表单事件处理
const onCategoryChange = (e: any) => {
  const index = e.detail.value
  formData.category = categoryOptions[index]
  validateField('category')
}

const onLevelChange = (e: any) => {
  const index = e.detail.value
  formData.level = levelOptions[index]
  validateField('level')
}

const onCertifiedChange = (e: any) => {
  formData.isCertified = e.detail.value
  if (!e.detail.value) {
    formData.certificateName = null
    formData.certificateDate = null
  }
}

const onCertificateDateChange = (e: any) => {
  formData.certificateDate = e.detail.value
}

const onPublicChange = (e: any) => {
  formData.isPublic = e.detail.value
}

// 标签处理
const addTag = () => {
  if (!tagInput.value.trim()) return

  const tags = tagInput.value.split(/[,\s]+/).map(tag => tag.trim()).filter(tag => tag)

  tags.forEach(tag => {
    if (tag && !tagArray.value.includes(tag)) {
      tagArray.value.push(tag)
    }
  })

  tagInput.value = ''
}

const removeTag = (index: number) => {
  tagArray.value.splice(index, 1)
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
        id: Math.floor(Math.random() * 1000) + 6,
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
        url: '/pages/skill-list/index?refresh=true'
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
        url: '/pages/skill/list?refresh=true'
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
      level: detailData.value.level,
      category: detailData.value.category,
      description: detailData.value.description,
      proficiencyPercent: detailData.value.proficiencyPercent,
      experienceYears: detailData.value.experienceYears,
      isCertified: detailData.value.isCertified,
      certificateName: detailData.value.certificateName,
      certificateDate: detailData.value.certificateDate,
      tags: detailData.value.tags,
      isPublic: detailData.value.isPublic
    })

    // 恢复标签数组
    tagArray.value = detailData.value.tags ? detailData.value.tags.split(',').map(tag => tag.trim()).filter(tag => tag) : []

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

  .status-badges {
    display: flex;
    gap: 8rpx;

    .badge {
      padding: 4rpx 12rpx;
      border-radius: $border-radius-small;
      font-size: $font-size-extra-small;
      font-weight: $font-weight-medium;

      &-public {
        background: $success-bg;
        color: $success-color;
        border: 1rpx solid $success-border;
      }

      &-certified {
        background: $primary-light;
        color: $primary-color;
        border: 1rpx solid $primary-border;
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

    .input-with-unit {
      position: relative;

      .input-unit {
        position: absolute;
        right: 24rpx;
        top: 50%;
        transform: translateY(-50%);
        color: $text-secondary;
        font-size: $font-size-base;
      }
    }

    .proficiency-input {
      position: relative;

      .input-percent {
        position: absolute;
        right: 24rpx;
        top: 20rpx;
        color: $text-secondary;
        font-size: $font-size-base;
        z-index: 1;
      }

      .proficiency-display {
        position: absolute;
        left: 0;
        right: 0;
        bottom: 0;
        height: 4rpx;
        background: $background-color;
        border-radius: 2rpx;
        overflow: hidden;

        .proficiency-bar {
          height: 100%;
          background: linear-gradient(90deg, $primary-color, color.adjust($primary-color, $lightness:  20%));
          border-radius: 2rpx;
          transition: width $transition-normal $ease-in-out;
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
      min-height: 160rpx;
      line-height: 1.5;
      box-sizing: border-box;

      &[disabled] {
        background: $background-color;
        color: $text-secondary;
        cursor: not-allowed;
      }
    }

    .tags-container {
      display: flex;
      flex-wrap: wrap;
      gap: 12rpx;
      margin-top: $margin-mini;

      .tag-item {
        display: flex;
        align-items: center;
        gap: 8rpx;
        padding: 8rpx 16rpx;
        background: $background-color;
        border-radius: $border-radius-round;
        font-size: $font-size-small;
        color: $text-regular;
        border: 1rpx solid $border-color-light;
      }
    }

    .form-hint {
      display: block;
      font-size: $font-size-extra-small;
      color: $text-placeholder;
      margin-top: 8rpx;
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

    .certificate-fields {
      padding: $margin-mini 0;
      border-top: 1rpx solid $border-color-extra-light;
      margin-top: $margin-mini;
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