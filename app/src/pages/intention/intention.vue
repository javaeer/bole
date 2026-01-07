<template>
  <!-- 模板部分保持不变 -->
  <view class="page-container">
    <!-- 头部 -->
    <view class="detail-header card-container">
      <view class="header-left">
      </view>

      <view v-if="!isEditMode && detailData.id" class="header-actions">
        <button class="btn btn-secondary" @click="toggleEditMode">编辑</button>
      </view>

      <view v-else-if="isEditMode" class="header-actions">
        <button class="btn btn-secondary" @click="cancelEdit">取消</button>
        <button class="btn btn-primary" :disabled="savingRef" @click="handleSave">{{ savingRef ? "保存中..." : "保存" }}
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
            <view v-if="!isEditMode" class="type-tag" :class="getJobTypeClass(detailData.jobType)">{{ detailData.jobType
              }}
            </view>
          </view>

          <view class="form-container">
            <!-- 期望职位 -->
            <view class="form-group">
              <text class="form-label required">期望职位</text>
              <view v-if="!isEditMode" class="form-input form-input-text">{{ formData.position || "未填写" }}</view>
              <input
                v-else
                v-model="formData.position"
                class="form-input"
                :class="{ 'error': errors.position }"
                placeholder="请输入期望职位"
                @blur="validateField('position')"
              />
              <text v-if="errors.position" class="error-text">{{ errors.position }}</text>
            </view>

            <!-- 工作城市 -->
            <view class="form-group">
              <text class="form-label required">工作城市</text>
              <view v-if="!isEditMode" class="form-input form-input-text">{{ formData.city || "未选择" }}</view>
              <view v-else class="city-picker-container">
                <!-- 替换为 RegionPicker 组件 -->
                <RegionPicker
                  v-model="selectedRegion"
                  :show-district="false"
                  :disabled="false"
                  :show-selected-text="false"
                  :show-clear="true"
                  :province-placeholder="'请选择省份'"
                  :city-placeholder="'请选择城市'"
                  :auto-load-provinces="true"
                  :preload-top-provinces="5"
                  :force-refresh="forceRefresh"
                  class="custom-region-picker"
                  @change="onCityChange"
                  @province-change="onProvinceChange"
                  @city-change="onCitySelected"
                  @error="onRegionError"
                  @loading="onRegionLoading"
                />
              </view>
              <text v-if="errors.city" class="error-text">{{ errors.city }}</text>
              <!-- 显示当前选择的城市 -->
              <view v-if="isEditMode && selectedRegion.city" class="selected-city-preview">
                <text class="preview-label">已选择：</text>
                <text class="preview-value">
                  {{ selectedRegion.province?.name }}{{ selectedRegion.province?.name && selectedRegion.city?.name ? '/' : '' }}{{ selectedRegion.city?.name }}
                </text>
              </view>
            </view>

            <!-- 工作类型 -->
            <view class="form-group">
              <text class="form-label required">工作类型</text>
              <view v-if="!isEditMode" class="form-input form-input-text">{{ formData.jobType || "未选择" }}</view>
              <view v-else class="job-type-picker-container">
                <picker
                  mode="selector"
                  :value="jobTypeIndex"
                  :range="jobTypeOptions"
                  @change="onJobTypeChange"
                >
                  <view class="form-input picker-input" :class="{ 'error': errors.jobType }">
                    {{ formData.jobType || "请选择工作类型" }}
                  </view>
                </picker>
              </view>
              <text v-if="errors.jobType" class="error-text">{{ errors.jobType }}</text>
            </view>

            <!-- 期望薪资 -->
            <view class="form-group">
              <text class="form-label">期望薪资</text>
              <view v-if="!isEditMode" class="form-input form-input-text">
                {{ formData.salary ? formData.salary + "元/月" : "面议" }}
              </view>
              <view v-else class="salary-input-group">
                <input
                  v-model="formData.salary"
                  class="salary-input"
                  :class="{ 'error': errors.salary }"
                  placeholder="请输入期望月薪"
                  @input="onSalaryInput"
                  @blur="validateField('salary')"
                />
                <text class="salary-unit">元/月</text>
              </view>
              <text v-if="errors.salary" class="error-text">{{ errors.salary }}</text>
              <view class="salary-preview" v-if="formData.salary && formData.salary !== ''">
                <text class="preview-text">{{ formatSalary(formData.salary) }}</text>
                <text class="preview-analysis">{{ getSalaryAnalysis(formData.salary) }}</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 薪资分析卡片 -->
        <view class="info-card card-container" v-if="formData.salary && formData.salary !== ''">
          <view class="card-header">
            <text class="card-title">薪资分析</text>
          </view>

          <view class="analysis-content">
            <!-- 薪资水平 -->
            <view class="analysis-item">
              <view class="analysis-icon">💰</view>
              <view class="analysis-info">
                <text class="analysis-label">薪资水平</text>
                <text class="analysis-value">{{ getSalaryLevel(formData.salary) }}</text>
              </view>
              <view class="analysis-progress">
                <view class="progress-bar" :style="{ width: `${getSalaryPercentage(formData.salary)}%` }"></view>
              </view>
            </view>

            <!-- 市场对比 -->
            <view class="analysis-item">
              <view class="analysis-icon">📊</view>
              <view class="analysis-info">
                <text class="analysis-label">市场对比</text>
                <text class="analysis-value">{{ getMarketComparison(formData.salary) }}</text>
              </view>
            </view>

            <!-- 建议 -->
            <view class="analysis-item">
              <view class="analysis-icon">💡</view>
              <view class="analysis-info">
                <text class="analysis-label">建议</text>
                <text class="analysis-value">{{ getSalarySuggestion(formData.salary) }}</text>
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
import type { JobIntentionForm, JobIntentionResult } from "@/types/job-intention";
import type { SelectedRegion, Province, City } from "@/types/region";
import JobIntentionAPI from "@/api/job-intention";
import RegionPicker from "@/components/region-picker/RegionPicker.vue";
import { useRegionStore } from "@/stores/region";
import { useSaveAndBack } from "@/composables/useSaveAndBack";
import { useDeleteAndBack } from "@/composables/useDeleteAndBack";

interface FormErrors {
  position?: string;
  city?: string;
  jobType?: string;
  salary?: string;
}

// 路由参数
const routeParams = ref<any>({});

// 响应式数据
const detailData = ref<JobIntentionResult>({
  id: 0,
  createdAt: "",
  updatedAt: "",
  deleted: 0,
  userId: 1,
  position: "",
  city: "",
  salary: "",
  jobType: "",
});

// 表单数据
const formData = reactive<JobIntentionForm>({
  id: undefined,
  position: "",
  city: "",
  salary: "",
  jobType: "",
});

// RegionPicker 相关数据
const selectedRegion = ref<SelectedRegion>({});
const regionStore = useRegionStore();
const forceRefresh = ref(false);

const errors = reactive<FormErrors>({});
const isEditMode = ref(false);

// 使用 composables
const { saving: savingRef, saveAndBack } = useSaveAndBack()
const { deleting: deletingRef, deleteAndBack } = useDeleteAndBack()

// 工作类型选项
const jobTypeOptions = ["全职", "兼职", "实习", "远程"];

// 计算属性
const jobTypeIndex = computed(() => {
  if (!formData.jobType) return 0;
  const index = jobTypeOptions.findIndex(opt => opt === formData.jobType);
  return index >= 0 ? index : 0;
});

// 获取工作类型样式
const getJobTypeClass = (jobType?: string) => {
  if (!jobType) return "type-default";

  switch (jobType) {
    case "全职":
      return "type-fulltime";
    case "兼职":
      return "type-parttime";
    case "实习":
      return "type-intern";
    case "远程":
      return "type-remote";
    default:
      return "type-default";
  }
};

// 格式化日期时间
const formatDateTime = (dateStr?: string) => {
  if (!dateStr) return "";
  try {
    const date = new Date(dateStr);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")} ${String(date.getHours()).padStart(2, "0")}:${String(date.getMinutes()).padStart(2, "0")}`;
  } catch {
    return dateStr;
  }
};

// 格式化薪资
const formatSalary = (salary?: string): string => {
  if (!salary || salary === "") return "面议";

  const num = parseFloat(salary);
  if (isNaN(num)) return "面议";

  if (num >= 10000) {
    return `${(num / 10000).toFixed(1)}万/月`;
  } else {
    return `${num}元/月`;
  }
};

// 薪资分析
const getSalaryAnalysis = (salary?: string): string => {
  if (!salary || salary === "") return "";

  const num = parseFloat(salary);
  if (isNaN(num)) return "";

  if (num >= 30000) {
    return "高级职位薪资范围";
  } else if (num >= 15000) {
    return "中级职位薪资范围";
  } else {
    return "初级职位薪资范围";
  }
};

// 薪资水平
const getSalaryLevel = (salary?: string): string => {
  if (!salary || salary === "") return "未设置";

  const num = parseFloat(salary);
  if (isNaN(num)) return "未设置";

  if (num >= 50000) return "资深专家";
  if (num >= 30000) return "高级水平";
  if (num >= 15000) return "中级水平";
  if (num >= 8000) return "初级水平";
  return "入门水平";
};

// 薪资百分比（用于进度条）
const getSalaryPercentage = (salary?: string): number => {
  if (!salary || salary === "") return 0;

  const num = parseFloat(salary);
  if (isNaN(num)) return 0;

  // 假设10万为最高薪资
  const maxSalary = 100000;
  const percentage = (num / maxSalary) * 100;
  return Math.min(Math.max(percentage, 0), 100);
};

// 市场对比
const getMarketComparison = (salary?: string): string => {
  if (!salary || salary === "") return "未设置";

  const num = parseFloat(salary);
  if (isNaN(num)) return "未设置";

  if (num >= 40000) return "高于市场平均水平";
  if (num >= 20000) return "市场平均水平";
  return "低于市场平均水平";
};

// 薪资建议
const getSalarySuggestion = (salary?: string): string => {
  if (!salary || salary === "") return "请设置期望薪资";

  const num = parseFloat(salary);
  if (isNaN(num)) return "请设置有效的期望薪资";

  if (num < 5000) {
    return "建议适当提高期望薪资";
  } else if (num > 80000) {
    return "薪资期望较高，建议结合自身能力调整";
  } else {
    return "薪资期望合理";
  }
};

// 根据城市名称查找区域信息
const findRegionByCityName = async (cityName: string) => {
  if (!cityName) return;

  try {
    // 使用 store 的搜索功能查找城市
    const searchResults = regionStore.searchByPinyin(cityName);

    // 查找城市级别的结果
    const cityResult = searchResults.find(item =>
      item.level === 2 &&
      (item.name === cityName || item.shortName === cityName)
    );

    if (cityResult) {
      // 找到城市，现在需要找到对应的省份
      const provinces = await regionStore.loadProvinces();
      const province = provinces.find(p => p.id === cityResult.parentId);

      if (province) {
        // 加载城市数据
        await regionStore.loadCities(province.id);
        const cities = regionStore.getCitiesByProvince(province.id);
        const city = cities.find(c => c.id === cityResult.id);

        if (city) {
          selectedRegion.value = {
            province,
            city
          };
          console.log('根据城市名称找到区域:', selectedRegion.value);
        }
      }
    }
  } catch (error) {
    console.error('查找区域信息失败:', error);
  }
};

// 加载数据
const loadDetailData = async (id?: number) => {
  try {
    if (id) {
      // API请求获取详情
      const result = await JobIntentionAPI.getById(id);

      if (result) {
        detailData.value = result;

        // 填充表单数据
        formData.id = result.id;
        formData.position = result.position || "";
        formData.city = result.city || "";
        formData.salary = result.salary || "";
        formData.jobType = result.jobType || "";

        // 如果有城市信息，初始化 RegionPicker
        if (result.city && result.city.trim()) {
          // 等待省份数据加载完成
          await regionStore.loadProvinces();

          // 尝试根据城市名称查找区域信息
          await findRegionByCityName(result.city.trim());
        }
      }
    } else {
      // 新增模式
      detailData.value = {
        id: 0,
        createdAt: "",
        updatedAt: "",
        deleted: 0,
        userId: 1,
        position: "",
        city: "",
        salary: "",
        jobType: "",
      };

      // 重置表单数据
      formData.id = undefined;
      formData.position = "";
      formData.city = "";
      formData.salary = "";
      formData.jobType = "";
      selectedRegion.value = {};

      isEditMode.value = true;

      // 预加载省份数据
      regionStore.loadProvinces().catch(error => {
        console.error('预加载省份数据失败:', error);
      });
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
    case "position":
      if (!value?.toString().trim()) {
        errors.position = "请输入期望职位";
      } else if (value.toString().trim().length > 50) {
        errors.position = "职位名称不能超过50个字符";
      } else {
        delete errors.position;
      }
      break;

    case "city":
      if (!value) {
        errors.city = "请选择工作城市";
      } else {
        delete errors.city;
      }
      break;

    case "jobType":
      if (!value) {
        errors.jobType = "请选择工作类型";
      } else {
        delete errors.jobType;
      }
      break;

    case "salary":
      if (value && value.toString().trim() !== "") {
        const num = parseFloat(value.toString());
        if (isNaN(num)) {
          errors.salary = "请输入有效的薪资数字";
        } else if (num < 0) {
          errors.salary = "薪资不能为负数";
        } else if (num > 1000000) {
          errors.salary = "薪资不能超过100万";
        } else {
          delete errors.salary;
        }
      } else {
        delete errors.salary; // 薪资可选，可以为空
      }
      break;
  }
};

const validateForm = (): boolean => {
  validateField("position");
  validateField("city");
  validateField("jobType");
  validateField("salary");

  return Object.keys(errors).length === 0;
};

// RegionPicker 事件处理
const onProvinceChange = (province: Province | null) => {
  console.log('省份变化:', province);
  // 省份变化时，城市会自动清空
}

const onCitySelected = (city: City | null) => {
  console.log('城市变化:', city);

  if (city) {
    // 更新表单中的城市字段
    formData.city = city.name;
    validateField('city');
  } else {
    formData.city = "";
    errors.city = "请选择工作城市";
  }
}

const onCityChange = (region: SelectedRegion) => {
  console.log('区域选择变化:', region);
  selectedRegion.value = region;

  // 更新表单中的城市字段
  if (region.city) {
    formData.city = region.city.name;
    validateField('city');
  } else {
    formData.city = "";
    errors.city = "请选择工作城市";
  }
}

const onRegionError = (error: string | null) => {
  console.error('RegionPicker 错误:', error);
  if (error) {
    uni.showToast({
      title: `地区选择错误: ${error}`,
      icon: 'error',
      duration: 3000
    });
  }
}

const onRegionLoading = (isLoading: boolean) => {
  console.log('RegionPicker 加载状态:', isLoading);
}

const onJobTypeChange = (e: any) => {
  const index = e.detail.value;
  if (index >= 0 && index < jobTypeOptions.length) {
    formData.jobType = jobTypeOptions[index];
    validateField("jobType");
  }
};

// 薪资输入处理
const onSalaryInput = (e: any) => {
  // 获取输入值
  let value = e.detail.value;

  // 允许数字和小数点
  value = value.replace(/[^\d.]/g, "");

  // 确保只有一个小数点
  const parts = value.split(".");
  if (parts.length > 2) {
    value = parts[0] + "." + parts.slice(1).join("");
  }

  // 限制小数点后最多两位
  if (parts.length === 2 && parts[1].length > 2) {
    value = parts[0] + "." + parts[1].substring(0, 2);
  }

  formData.salary = value;
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

  // 确保城市已选择
  if (!formData.city) {
    uni.showToast({
      title: "请选择工作城市",
      icon: "error",
    });
    return;
  }

  // 准备提交数据
  const submitData: JobIntentionForm = {
    id: formData.id,
    position: formData.position?.trim(),
    city: formData.city,
    salary: formData.salary,
    jobType: formData.jobType,
  };

  // 清理空字符串字段
  Object.keys(submitData).forEach(key => {
    const typedKey = key as keyof JobIntentionForm;
    if (submitData[typedKey] === "" || submitData[typedKey] === null || submitData[typedKey] === undefined) {
      // @ts-ignore
      delete submitData[typedKey];
    }
  });

  const saveFunction = formData.id
    ? () => JobIntentionAPI.update(submitData)
    : () => {
      return JobIntentionAPI.add(submitData);
    };

  await saveAndBack({
    saveFn: saveFunction,
    successMessage: "保存成功",
    successCallback: () => {
      isEditMode.value = false;
    },
    loadingText: "保存中..."
  });
};

// 删除项目
const handleDelete = async () => {
  await deleteAndBack({
    deleteFn: () => JobIntentionAPI.delete(detailData.value.id),
    confirmMessage: "确定要删除这份求职意向吗？删除后不可恢复！",
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
    formData.id = detailData.value.id;
    formData.position = detailData.value.position || "";
    formData.city = detailData.value.city || "";
    formData.salary = detailData.value.salary || "";
    formData.jobType = detailData.value.jobType || "";

    // 恢复城市选择
    if (detailData.value.city && detailData.value.city.trim()) {
      findRegionByCityName(detailData.value.city.trim());
    } else {
      selectedRegion.value = {};
    }

    isEditMode.value = false;
    // 清空错误信息
    Object.keys(errors).forEach(key => delete errors[key as keyof FormErrors]);
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

  .type-tag {
    padding: 4rpx 12rpx;
    border-radius: $border-radius-small;
    font-size: $font-size-extra-small;
    font-weight: $font-weight-medium;

    &.type-fulltime {
      background: $success-bg;
      color: $success-color;
      border: 1rpx solid $success-border;
    }

    &.type-parttime {
      background: $warning-bg;
      color: $warning-color;
      border: 1rpx solid $warning-border;
    }

    &.type-intern {
      background: $info-bg;
      color: $info-color;
      border: 1rpx solid $info-border;
    }

    &.type-remote {
      background: $primary-color-light;
      color: $primary-color;
      border: 1rpx solid $primary-border;
    }

    &.type-default {
      background: $background-color;
      color: $text-secondary;
      border: 1rpx solid $border-color-light;
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

      &.form-input-text {
        background: $background-color;
        border: 2rpx solid $border-color-lighter;
        color: $text-primary;
      }

      &.picker-input {
        cursor: pointer;
        user-select: none;
      }

      &[disabled] {
        background: $background-color;
        color: $text-secondary;
        cursor: not-allowed;
      }
    }

    .city-picker-container {
      .custom-region-picker {
        :deep(.region-cascader) {
          flex-direction: column;
          gap: 12rpx;

          .picker-wrapper {
            margin-bottom: 0;
          }
        }

        :deep(.picker-view) {
          border: 2rpx solid $border-color-lighter;
          border-radius: $border-radius;
          font-size: $font-size-base;
          color: $text-primary;
          background: $background-color-white;
          transition: all $transition-fast $ease-in-out;
          min-height: 80rpx;
          padding: 0 24rpx;

          &:focus, &:active {
            border-color: $primary-color;
            box-shadow: $input-focus-shadow;
          }

          &.disabled {
            background: $background-color;
            color: $text-secondary;
            cursor: not-allowed;
          }
        }

        :deep(.selected-region) {
          margin-top: 12rpx;
          padding: 12rpx;
          background: $background-color;
          border-radius: $border-radius-small;
          border: 1rpx solid $border-color-lighter;
          font-size: $font-size-small;
          color: $text-secondary;
        }

        :deep(.error-message) {
          margin-top: 8rpx;
          font-size: $font-size-extra-small;
          color: $danger-color;
        }
      }
    }

    .selected-city-preview {
      margin-top: 12rpx;
      padding: 16rpx;
      background: $primary-color-light;
      border-radius: $border-radius-small;
      border: 1rpx solid $primary-border;
      display: flex;
      align-items: center;

      .preview-label {
        font-size: $font-size-small;
        color: $text-secondary;
        margin-right: 8rpx;
      }

      .preview-value {
        font-size: $font-size-base;
        color: $primary-color;
        font-weight: $font-weight-medium;
      }
    }

    .salary-input-group {
      position: relative;
      display: flex;
      align-items: center;

      .salary-input {
        flex: 1;
        width: 100%;
        padding: 20rpx 24rpx;
        padding-right: 120rpx;
        border: 2rpx solid $border-color-lighter;
        border-radius: $border-radius;
        font-size: $font-size-base;
        color: $text-primary;
        background: $background-color-white;
        transition: all $transition-fast $ease-in-out;
        box-sizing: border-box;
        min-height: 80rpx;

        &:focus {
          border-color: $primary-color;
          box-shadow: $input-focus-shadow;
          outline: none;
        }

        &.error {
          border-color: $danger-color;
          box-shadow: $input-error-shadow;
        }
      }

      .salary-unit {
        position: absolute;
        right: 24rpx;
        top: 50%;
        transform: translateY(-50%);
        color: $text-secondary;
        font-size: $font-size-base;
      }
    }

    .salary-preview {
      margin-top: 12rpx;
      padding: 16rpx;
      background: $primary-color-light;
      border-radius: $border-radius-small;
      border: 1rpx solid $primary-border;

      .preview-text {
        display: block;
        font-size: $font-size-medium;
        font-weight: $font-weight-bold;
        color: $primary-color;
        margin-bottom: 4rpx;
      }

      .preview-analysis {
        font-size: $font-size-extra-small;
        color: $text-secondary;
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

.analysis-content {
  .analysis-item {
    display: flex;
    align-items: center;
    gap: $margin-small;
    padding: 20rpx 0;
    border-bottom: 1rpx solid $border-color-extra-light;

    &:last-child {
      border-bottom: none;
    }

    .analysis-icon {
      width: 40rpx;
      height: 40rpx;
      font-size: 24rpx;
      background: $background-color;
      border-radius: $border-radius-round;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .analysis-info {
      flex: 1;

      .analysis-label {
        display: block;
        font-size: $font-size-small;
        color: $text-secondary;
        margin-bottom: 4rpx;
      }

      .analysis-value {
        font-size: $font-size-base;
        font-weight: $font-weight-medium;
        color: $text-primary;
      }
    }

    .analysis-progress {
      width: 100rpx;
      height: 8rpx;
      background: $background-color;
      border-radius: 4rpx;
      overflow: hidden;

      .progress-bar {
        height: 100%;
        background: linear-gradient(90deg, $primary-color, color.adjust($primary-color, $lightness: 20%));
        border-radius: 4rpx;
        transition: width $transition-normal $ease-in-out;
      }
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