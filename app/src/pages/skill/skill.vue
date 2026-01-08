<template>
  <view class="page-container">
    <!-- 头部 -->
    <view class="detail-header card-container">
      <view class="header-left">
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
        <button class="btn btn-primary" :disabled="savingRef" @click="handleSave">
          {{ savingRef ? "保存中..." : "保存" }}
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
                :value="categoryIndex"
                :range="categoryOptions"
                :disabled="!isEditMode"
                @change="onCategoryChange"
              >
                <view class="form-input" :class="{ 'error': errors.category }">
                  {{ formData.category || "请选择分类" }}
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
                  {{ formData.level || "请选择等级" }}
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
            />
            <view class="textarea-count">
              {{ formData.description?.length || 0 }}/1000
            </view>
          </view>

          <!-- 标签（数组类型） -->
          <view class="form-group">
            <text class="form-label">标签</text>
            <view class="tag-input-container">
              <input
                v-model="tagInputRef"
                class="form-input"
                :disabled="!isEditMode"
                placeholder="输入标签后按回车添加"
                @keyup.enter="addTag"
                @blur="onTagInputBlur"
              />
              <button
                v-if="tagInputRef"
                class="tag-add-btn"
                @click="addTag"
                :disabled="!isEditMode"
              >
                +
              </button>
            </view>

            <!-- 标签显示区域 -->
            <view class="tags-container" v-if="formData.tags && formData.tags.length > 0">
              <view
                v-for="(tag, index) in formData.tags"
                :key="index"
                class="tag-item"
              >
                <text>{{ tag }}</text>
                <button
                  v-if="isEditMode"
                  class="tag-remove-btn"
                  @click="removeTag(index)"
                >
                  ×
                </button>
              </view>
            </view>
            <text class="form-hint">按回车或点击+号添加标签</text>
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
                style="transform: scale(0.8); margin-right: 10rpx;"
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
                :value="formData.certificateDate || ''"
                :disabled="!isEditMode"
                @change="onCertificateDateChange"
              >
                <view class="form-input">
                  {{ formData.certificateDate || "请选择认证日期" }}
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
                style="transform: scale(0.8); margin-right: 10rpx;"
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
              <text class="info-value">{{ formatDate(detailData.createdAt) }}</text>
            </view>

            <view class="info-row">
              <text class="info-label">更新时间</text>
              <text class="info-value">{{ formatDate(detailData.updatedAt) }}</text>
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
      <button class="btn btn-danger btn-block" @click="handleDelete" :disabled="deletingRef">
        {{ deletingRef ? "删除中..." : "删除" }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import type { SkillForm, SkillResult } from "@/types/skill";
import SkillAPI from "@/api/skill";
import { useSaveAndBack } from "@/composables/useSaveAndBack";
import { useDeleteAndBack } from "@/composables/useDeleteAndBack";

interface FormErrors {
  name?: string;
  category?: string;
  level?: string;
  experienceYears?: string;
  proficiencyPercent?: string;
}

// 路由参数
const routeParams = ref<any>({});

// 响应式数据
const detailData = ref<SkillResult & { tags?: string[] }>({
  id: 0,
  createdAt: "",
  updatedAt: "",
  deleted: 0,
  name: "",
  level: "",
  category: "",
  description: "",
  proficiencyPercent: 0,
  experienceYears: 0,
  isCertified: false,
  certificateName: null,
  certificateDate: null,
  tags: [],
  isPublic: true,
  sort: 0,
});

const formData = reactive<SkillForm>({
  id: null,
  name: "",
  level: "",
  category: "",
  description: "",
  proficiencyPercent: 0,
  experienceYears: 0,
  isCertified: false,
  certificateName: null,
  certificateDate: null,
  tags: [],
  isPublic: true,
});

// 重命名变量避免冲突
const tagInputRef = ref("");
const errors = reactive<FormErrors>({});
const isEditMode = ref(false);

// 使用 composable
const { saving: savingRef, saveAndBack } = useSaveAndBack()
const { deleting: deletingRef, deleteAndBack } = useDeleteAndBack()

// 选项
const categoryOptions = ["编程语言", "前端框架", "后端框架", "数据库", "数据分析", "人工智能", "运维部署", "工具软件", "其他"];
const levelOptions = ["初级", "中级", "高级", "专家"];

// 计算属性
const levelIndex = computed(() => {
  const index = levelOptions.findIndex(opt => opt === formData.level);
  return index >= 0 ? index : 0;
});

const categoryIndex = computed(() => {
  const index = categoryOptions.findIndex(opt => opt === formData.category);
  return index >= 0 ? index : 0;
});

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return "";
  try {
    const date = new Date(dateStr);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
  } catch {
    return dateStr;
  }
};

// 加载数据
const loadDetailData = async (id?: number) => {
  try {
    if (id) {
      // API请求
      const result = await SkillAPI.getById(id);

      if (result) {
        detailData.value = result;

        // 处理tags数据：如果是字符串，转换为数组
        let tagsArray: string[] = [];
        if (result.tags) {
          if (typeof result.tags === "string") {
            // 按逗号分割并过滤空值
            tagsArray = result.tags.split(",").map(tag => tag.trim()).filter(tag => tag);
          } else if (Array.isArray(result.tags)) {
            // 已经是数组
            tagsArray = result.tags;
          }
        }

        // 填充表单数据
        formData.id = result.id || null;
        formData.name = result.name || "";
        formData.level = result.level || "";
        formData.category = result.category || "";
        formData.description = result.description || "";
        formData.proficiencyPercent = result.proficiencyPercent || 0;
        formData.experienceYears = result.experienceYears || 0;
        formData.isCertified = result.isCertified || false;
        formData.certificateName = result.certificateName || null;
        formData.certificateDate = result.certificateDate || null;
        formData.tags = tagsArray;
        formData.isPublic = result.isPublic || true;
      }
    } else {
      // 新增模式
detailData.value = {
        id: null,
        createdAt: "",
        updatedAt: "",
        deleted: 0,
        name: "",
        level: "",
        category: "",
        description: "",
        proficiencyPercent: 0,
        experienceYears: 0,
        isCertified: false,
        certificateName: null,
        certificateDate: null,
        tags: [],
        isPublic: true,
        sort: 0,
      };

      isEditMode.value = true;
      // 清空表单数据
      formData.name = "";
      formData.level = "";
      formData.category = "";
      formData.description = "";
      formData.proficiencyPercent = 0;
      formData.experienceYears = 0;
      formData.isCertified = false;
      formData.certificateName = null;
      formData.certificateDate = null;
      formData.tags = [];
      formData.isPublic = true;
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
  const value = formData[field as keyof typeof formData];

  switch (field) {
    case "name":
      if (!value?.toString().trim()) {
        errors.name = "请输入技能名称";
      } else {
        delete errors.name;
      }
      break;

    case "category":
      if (!value) {
        errors.category = "请选择分类";
      } else {
        delete errors.category;
      }
      break;

    case "level":
      if (!value) {
        errors.level = "请选择等级";
      } else {
        delete errors.level;
      }
      break;

    case "experienceYears":
      const expValue = Number(value);
      if (isNaN(expValue) || expValue < 0) {
        errors.experienceYears = "请输入有效的经验年数";
      } else if (expValue > 50) {
        errors.experienceYears = "经验年数不能超过50年";
      } else {
        delete errors.experienceYears;
      }
      break;

    case "proficiencyPercent":
      const percentValue = Number(value);
      if (isNaN(percentValue) || percentValue < 0 || percentValue > 100) {
        errors.proficiencyPercent = "请输入0-100之间的数值";
      } else {
        delete errors.proficiencyPercent;
      }
      break;
  }
};

const validateForm = (): boolean => {
  validateField("name");
  validateField("category");
  validateField("level");
  validateField("experienceYears");
  validateField("proficiencyPercent");

  return Object.keys(errors).length === 0;
};

// 表单事件处理
const onCategoryChange = (e: any) => {
  const index = e.detail.value;
  formData.category = categoryOptions[index];
  validateField("category");
};

const onLevelChange = (e: any) => {
  const index = e.detail.value;
  formData.level = levelOptions[index];
  validateField("level");
};

const onCertifiedChange = (e: any) => {
  formData.isCertified = e.detail.value;
  if (!e.detail.value) {
    formData.certificateName = null;
    formData.certificateDate = null;
  }
};

const onCertificateDateChange = (e: any) => {
  formData.certificateDate = e.detail.value;
};

const onPublicChange = (e: any) => {
  formData.isPublic = e.detail.value;
};

// 标签处理（数组操作）
const addTag = () => {
  const tag = tagInputRef.value.trim();
  if (!tag) return;

  // 检查是否已存在
  if (!formData.tags.includes(tag)) {
    formData.tags.push(tag);
  }

  tagInputRef.value = "";
};
const onTagInputBlur = () => {
  // 失去焦点时也尝试添加标签
  if (tagInputRef.value.trim()) {
    addTag();
  }
};

const removeTag = (index: number) => {
  formData.tags.splice(index, 1);
};

// 保存数据
const handleSave = async () => {
  if (!validateForm()) {
    uni.showToast({
      title: "请填写完整信息",
      icon: "error",
    });
    return;
  }

  // 准备提交数据
  const submitData = {
    ...formData,
    id: formData.id,
    certificateName: formData.isCertified ? formData.certificateName : null,
    certificateDate: formData.isCertified ? formData.certificateDate : null,
  };

  const saveFunction = detailData.value.id
    ? () => SkillAPI.update(submitData)
    : () => SkillAPI.add(submitData);

  await saveAndBack({
    saveFn: saveFunction,
    successMessage: "保存成功",
    successCallback: () => {
      isEditMode.value = false;
    }
  });
};

// 删除项目
const handleDelete = async () => {
  await deleteAndBack({
    deleteFn: () => SkillAPI.delete(detailData.value.id),
    confirmMessage: "确定要删除这个技能吗？删除后不可恢复！",
    successMessage: "删除成功",
  });
};

// 切换编辑模式
const toggleEditMode = () => {
  isEditMode.value = true;
};

const cancelEdit = () => {
  if (detailData.value.id) {
    // 恢复原始数据
    formData.name = detailData.value.name || "";
    formData.level = detailData.value.level || "";
    formData.category = detailData.value.category || "";
    formData.description = detailData.value.description || "";
    formData.proficiencyPercent = detailData.value.proficiencyPercent || 0;
    formData.experienceYears = detailData.value.experienceYears || 0;
    formData.isCertified = detailData.value.isCertified || false;
    formData.certificateName = detailData.value.certificateName || null;
    formData.certificateDate = detailData.value.certificateDate || null;
    formData.isPublic = detailData.value.isPublic || true;

    // 恢复tags数据
    if (detailData.value.tags) {
      if (Array.isArray(detailData.value.tags)) {
        formData.tags = [...detailData.value.tags];
      } else if (typeof detailData.value.tags === "string") {
        formData.tags = detailData.value.tags.split(",").map(tag => tag.trim()).filter(tag => tag);
      } else {
        formData.tags = [];
      }
    } else {
      formData.tags = [];
    }

    tagInputRef.value = "";
    isEditMode.value = false;

    // 清空错误信息
    Object.keys(errors).forEach(key => {
      delete errors[key as keyof FormErrors];
    });
  } else {
    // 如果是新增，返回列表页
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
.page-container {
  @extend .page-container;
}

.detail-header {
  @extend .card-container;
  position: sticky;
  top: 0;
  z-index: $z-index-base;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx $padding-base;

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
    gap: $margin-mini;
    flex-wrap: wrap;
    
    .skill-level-badge {
      @extend .status-badge;
      
      &.level-expert {
        @extend .status-success;
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
  @extend .card-container;
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
          background: linear-gradient(90deg, $primary-color, color.adjust($primary-color, $lightness: 20%));
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

    .textarea-count {
      text-align: right;
      font-size: $font-size-extra-small;
      color: $text-secondary;
      margin-top: 8rpx;
    }

    .tag-input-container {
      position: relative;
      display: flex;
      align-items: center;

      .form-input {
        padding-right: 80rpx;
      }

      .tag-add-btn {
        position: absolute;
        right: 8rpx;
        width: 60rpx;
        height: 60rpx;
        background: $primary-color;
        color: white;
        border: none;
        border-radius: $border-radius-small;
        font-size: 24rpx;
        display: flex;
        align-items: center;
        justify-content: center;

        &:disabled {
          background: $text-placeholder;
          cursor: not-allowed;
        }

        &:active:not(:disabled) {
          opacity: 0.8;
        }
      }
    }

    .tags-container {
      display: flex;
      flex-wrap: wrap;
      gap: 12rpx;
      margin-top: $margin-mini;

      .tag-item {
        @extend .tag;
        display: flex;
        align-items: center;
        gap: 8rpx;

        .tag-remove-btn {
          background: transparent;
          border: none;
          font-size: 20rpx;
          color: $text-secondary;
          width: 24rpx;
          height: 24rpx;
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 0;
        }
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