<!-- subpackages/pages/education/education.vue -->
<template>
  <view class="page-container">
    <!-- 头部 -->
    <view class="detail-header card-container">
      <view class="header-left" >
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
        <button class="btn btn-primary" :disabled="isSaving" @click="handleSave">
          {{ isSaving ? "保存中..." : "保存" }}
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
                v-model="formData.university"
                class="form-input"
                :class="{ 'error': errors.university }"
                :disabled="!isEditMode"
                placeholder="请输入学校名称"
                @blur="validateField('university')"
              />
              <text v-if="errors.university" class="error-text">{{ errors.university }}</text>
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
                  {{ formData.degree || "请选择学位" }}
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
                  {{ formData.startDate || "请选择开始时间" }}
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
                  {{ formData.endDate || "请选择结束时间" }}
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
                  style="transform: scale(0.8); margin-right: 10rpx;"
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
            />
            <view class="textarea-count">
              {{ formData.description.length }}/500
            </view>
          </view>

          <!-- 成就/获奖列表 -->
          <view class="form-group">
            <text class="form-label">成就/获奖</text>

            <!-- 编辑模式下的成就列表 -->
            <view v-if="isEditMode" class="achievements-edit-container">
              <view
                v-for="(achievement, index) in formData.achievements"
                :key="index"
                class="achievement-item-edit"
              >
                <input
                  v-model="formData.achievements[index]"
                  class="achievement-input"
                  :placeholder="`成就 ${index + 1}`"
                  maxlength="100"
                />
                <button
                  v-if="formData.achievements.length > 1"
                  class="btn-remove-achievement"
                  @click="removeAchievement(index)"
                >
                  ×
                </button>
              </view>

              <button class="btn-add-achievement" @click="addAchievement">
                + 添加成就
              </button>

              <view class="achievements-count">
                共 {{ formData.achievements.length }} 项成就
              </view>
            </view>

            <!-- 只读模式下的成就列表 -->
            <view v-else class="achievements-readonly-container">
              <view
                v-for="(achievement, index) in detailData.achievements"
                :key="index"
                class="achievement-item-readonly"
              >
                <view class="achievement-index">{{ index + 1 }}.</view>
                <view class="achievement-content">{{ achievement }}</view>
              </view>

              <view v-if="!detailData.achievements || detailData.achievements.length === 0" class="no-achievements">
                暂无成就信息
              </view>
            </view>
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
              <text class="info-value">{{ formatDateTime(detailData.createdAt) }}</text>
            </view>

            <view class="info-row">
              <text class="info-label">更新时间</text>
              <text class="info-value">{{ formatDateTime(detailData.updatedAt) }}</text>
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
      <button class="btn btn-danger btn-block" @click="handleDelete" :disabled="isSaving">
        {{ deleting ? "删除中..." : "删除" }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import type { EducationExperienceForm, EducationExperienceResult } from "@/types/education-experience";
import EducationExperienceAPI from "@/subpackages/api/education-experience";
import { useSaveAndBack } from "@/composables/useSaveAndBack";
import { useDeleteAndBack } from "@/composables/useDeleteAndBack";

// 使用 composable - 修复事件前缀
const { saving: isSaving, saveAndBack } = useSaveAndBack();
const { deleting, deleteAndBack } = useDeleteAndBack();

interface FormErrors {
  university?: string;
  major?: string;
  degree?: string;
  startDate?: string;
  endDate?: string;
}

// 路由参数
const routeParams = ref<any>({});

// 响应式数据
const detailData = ref<EducationExperienceResult & { achievements?: string[] }>({
  id: null,
  createdAt: "",
  updatedAt: "",
  deleted: 0,
  userId: 1,
  university: "",
  major: "",
  degree: "",
  startDate: "",
  endDate: "",
  isHighest: 0,
  description: "",
  achievements: [],
  sort: 0,
});

const formData = reactive<EducationExperienceForm>({
  id: null,
  university: "",
  major: "",
  degree: "",
  startDate: "",
  endDate: "",
  isHighest: 0,
  description: "",
  achievements: [],
});

const errors = reactive<FormErrors>({});
const isEditMode = ref(false);

// 学位选项
const degreeOptions = ["本科", "硕士", "博士", "专科", "其他"];
const degreeIndex = computed(() => {
  const index = degreeOptions.findIndex(opt => opt === formData.degree);
  return index >= 0 ? index : 0;
});

// 获取状态文本和样式
const getStatusText = (item: any) => {
  if (!item || !item.endDate) return "已毕业";

  const now = new Date();
  const endDate = new Date(item.endDate);

  if (now < endDate) {
    return "在读";
  } else if (item.isHighest === 1) {
    return "最高学历";
  } else {
    return "已毕业";
  }
};

const getStatusClass = (item: any) => {
  if (!item || !item.endDate) return "status-info";

  const now = new Date();
  const endDate = new Date(item.endDate);

  if (now < endDate) {
    return "status-warning";
  } else if (item.isHighest === 1) {
    return "status-success";
  } else {
    return "status-info";
  }
};

// 格式化日期时间
const formatDateTime = (dateStr: string) => {
  if (!dateStr) return "";
  try {
    const date = new Date(dateStr);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")} ${String(date.getHours()).padStart(2, "0")}:${String(date.getMinutes()).padStart(2, "0")}`;
  } catch {
    return dateStr;
  }
};

// 添加成就
const addAchievement = () => {
  formData.achievements.push("");
};

// 删除成就
const removeAchievement = (index: number) => {
  if (formData.achievements.length > 1) {
    formData.achievements.splice(index, 1);
  }
};

// 加载数据
const loadDetailData = async (id?: number) => {
  try {
    if (id) {
      const result = await EducationExperienceAPI.getById(id);

      if (result) {
        let achievementsArray: string[] = [];
        if (result.achievements) {
          if (typeof result.achievements === "string") {
            achievementsArray = result.achievements
              .split("\n")
              .filter(item => item.trim() !== "")
              .map(item => item.trim());
          } else if (Array.isArray(result.achievements)) {
            achievementsArray = result.achievements;
          }
        }

        detailData.value = {
          ...result,
          achievements: achievementsArray,
        };

        formData.id = result.id || null;
        formData.university = result.university || "";
        formData.major = result.major || "";
        formData.degree = result.degree || "";
        formData.startDate = result.startDate || "";
        formData.endDate = result.endDate || "";
        formData.isHighest = result.isHighest || 0;
        formData.description = result.description || "";
        formData.achievements = achievementsArray;

        if (formData.achievements.length === 0 && isEditMode.value) {
          formData.achievements.push("");
        }
      }
    } else {
      detailData.value = {
        id: null,
        createdAt: "",
        updatedAt: "",
        deleted: 0,
        userId: 1,
        university: "",
        major: "",
        degree: "",
        startDate: "",
        endDate: "",
        isHighest: 0,
        description: "",
        achievements: [],
        sort: 0,
      };

      isEditMode.value = true;
      formData.achievements.push("");
    }
  } catch (error) {
    console.error("加载数据失败:", error);
    uni.showToast({
      title: "加载失败",
      icon: "error",
    });
  }
};

// 表单验证
const validateField = (field: keyof FormErrors) => {
  const value = formData[field as keyof EducationExperienceForm];

  switch (field) {
    case "university":
      if (!value?.trim()) {
        errors.university = "请输入学校名称";
      } else {
        delete errors.university;
      }
      break;

    case "major":
      if (!value?.trim()) {
        errors.major = "请输入专业名称";
      } else {
        delete errors.major;
      }
      break;

    case "degree":
      if (!value) {
        errors.degree = "请选择学位";
      } else {
        delete errors.degree;
      }
      break;

    case "startDate":
      if (!value) {
        errors.startDate = "请选择开始时间";
      } else {
        delete errors.startDate;
      }
      break;

    case "endDate":
      if (!value) {
        errors.endDate = "请选择结束时间";
      } else if (formData.startDate && new Date(value) < new Date(formData.startDate)) {
        errors.endDate = "结束时间不能早于开始时间";
      } else {
        delete errors.endDate;
      }
      break;
  }
};

const validateForm = (): boolean => {
  validateField("university");
  validateField("major");
  validateField("degree");
  validateField("startDate");
  validateField("endDate");

  return Object.keys(errors).length === 0;
};

// 表单事件处理
const onDegreeChange = (e: any) => {
  const index = e.detail.value;
  formData.degree = degreeOptions[index];
  validateField("degree");
};

const onStartDateChange = (e: any) => {
  formData.startDate = e.detail.value;
  validateField("startDate");
  if (formData.endDate) {
    validateField("endDate");
  }
};

const onEndDateChange = (e: any) => {
  formData.endDate = e.detail.value;
  validateField("endDate");
};

const onHighestChange = (e: any) => {
  formData.isHighest = e.detail.value ? 1 : 0;
};

// 保存数据 - 修复：添加事件前缀
const handleSave = async () => {
  if (!validateForm()) {
    uni.showToast({
      title: "请填写完整信息",
      icon: "error",
    });
    return;
  }

  try {
    const submitData = {
      ...formData,
      achievements: formData.achievements.filter(item => item.trim() !== ""),
    };

    const saveFunction = detailData.value.id
      ? () => EducationExperienceAPI.update(submitData)
      : () => EducationExperienceAPI.add(submitData);

    await saveAndBack({
      saveFn: saveFunction,
      successMessage: detailData.value.id ? "教育经历编辑成功" : "教育经历新增成功",
      showLoading: true,
      loadingText: detailData.value.id ? "正在更新..." : "正在保存...",
    });
  } catch (error) {
    console.error("保存失败:", error);
    // saveAndBack 已经处理了错误提示，这里不需要再处理
  }
};

// 删除项目 - 修复：添加事件前缀
const handleDelete = async () => {
  await deleteAndBack({
    deleteFn: () => EducationExperienceAPI.delete(detailData.value.id),
    confirmMessage: "确定要删除这条教育经历吗？删除后不可恢复！",
    successMessage: "删除成功",
  });
};

// 切换编辑模式
const toggleEditMode = () => {
  isEditMode.value = true;
  if (formData.achievements.length === 0) {
    formData.achievements.push("");
  }
};

const cancelEdit = () => {
  if (detailData.value.id) {
    formData.university = detailData.value.university || "";
    formData.major = detailData.value.major || "";
    formData.degree = detailData.value.degree || "";
    formData.startDate = detailData.value.startDate || "";
    formData.endDate = detailData.value.endDate || "";
    formData.isHighest = detailData.value.isHighest || 0;
    formData.description = detailData.value.description || "";
    formData.achievements = detailData.value.achievements ? [...detailData.value.achievements] : [];

    isEditMode.value = false;
    Object.keys(errors).forEach(key => {
      delete errors[key as keyof FormErrors];
    });
  } else {
    uni.navigateBack();
  }
};

// 生命周期
onLoad((options: any) => {
  routeParams.value = options;
  const id = options?.id ? parseInt(options.id) : undefined;
  const edit = options?.edit === "true";

  if (id) {
    loadDetailData(id);
    isEditMode.value = edit;
  } else {
    loadDetailData();
  }
});
</script>

<style lang="scss">

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

    .back-icon {
      font-size: 40rpx;
      color: $text-primary;
      width: 40rpx;
      height: 40rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
    }

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
      width: 90%;
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

    .textarea-count {
      text-align: right;
      font-size: $font-size-extra-small;
      color: $text-secondary;
      margin-top: 8rpx;
    }
  }
}

// 成就列表样式
.achievements-edit-container {
  .achievement-item-edit {
    display: flex;
    align-items: center;
    margin-bottom: 12rpx;

    .achievement-input {
      flex: 1;
      height: 80rpx;
      padding: 20rpx 24rpx;
      border: 1rpx solid $border-color-light;
      border-radius: $border-radius;
      font-size: $font-size-base;
      color: $text-primary;
      background: $background-color-white;

      &:focus {
        border-color: $primary-color;
        outline: none;
      }
    }

    .btn-remove-achievement {
      width: 60rpx;
      height: 60rpx;
      margin-left: 12rpx;
      background: $danger-bg;
      color: $danger-color;
      border: none;
      border-radius: 50%;
      font-size: 28rpx;
      display: flex;
      align-items: center;
      justify-content: center;

      &:active {
        opacity: 0.8;
      }
    }
  }

  .btn-add-achievement {
    width: 100%;
    padding: 20rpx;
    background: $background-color;
    color: $primary-color;
    border: 2rpx dashed $primary-color;
    border-radius: $border-radius;
    font-size: $font-size-base;
    margin-top: 12rpx;

    &:active {
      background: $primary-color-light;
    }
  }

  .achievements-count {
    text-align: right;
    font-size: $font-size-extra-small;
    color: $text-secondary;
    margin-top: 8rpx;
  }
}

.achievements-readonly-container {
  .achievement-item-readonly {
    display: flex;
    align-items: flex-start;
    padding: 16rpx 0;
    border-bottom: 1rpx solid $border-color-extra-light;

    &:last-child {
      border-bottom: none;
    }

    .achievement-index {
      width: 60rpx;
      color: $text-secondary;
      font-weight: $font-weight-medium;
    }

    .achievement-content {
      flex: 1;
      color: $text-primary;
      line-height: 1.5;
    }
  }

  .no-achievements {
    padding: 40rpx 0;
    text-align: center;
    color: $text-placeholder;
    font-size: $font-size-small;
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

// 状态标签样式
.status-success {
  background-color: $success-bg;
  color: $success-color;
}

.status-warning {
  background-color: $warning-bg;
  color: $warning-color;
}

.status-info {
  background-color: $info-bg;
  color: $info-color;
}

// 响应式优化
@media (min-width: 768px) {
  .detail-content {
    max-width: 800px;
    margin: 0 auto;
  }

  .info-card {
    border-radius: $border-radius * 1.5;
    box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.08);
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