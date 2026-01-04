<!-- components/region-picker/RegionPicker.vue -->
<template>
  <view class="region-picker">
    <!-- 联动选择模式 -->
    <view class="region-cascader" :class="{ 'disabled-cascader': disabled }">
      <!-- 省份选择 -->
      <view class="picker-wrapper">
        <picker
          class="region-picker-select"
          :value="provinceIndex"
          :range="provincesList"
          range-key="name"
          :disabled="disabled || isLoading"
          @change="handleProvinceChange"
        >
          <view class="picker-view" :class="{
            placeholder: !selectedProvince,
            disabled: disabled || isLoading
          }">
            <view v-if="isLoading && provincesList.length === 0" class="loading-text">
              加载中...
            </view>
            <view v-else>
              {{ selectedProvince?.name || provincePlaceholder }}
            </view>
          </view>
        </picker>
        <view v-if="provinceError" class="error-message">
          {{ provinceError }}
        </view>
      </view>

      <!-- 城市选择 -->
      <view v-if="showCity" class="picker-wrapper">
        <picker
          class="region-picker-select"
          :value="cityIndex"
          :range="currentCities"
          range-key="name"
          :disabled="!selectedProvince || disabled || loadingCities"
          @change="handleCityChange"
        >
          <view class="picker-view" :class="{
            placeholder: !selectedCity,
            disabled: !selectedProvince || disabled || loadingCities
          }">
            <view v-if="loadingCities" class="loading-text">
              加载中...
            </view>
            <view v-else-if="!selectedProvince" class="disabled-text">
              {{ cityPlaceholder }}
            </view>
            <view v-else>
              {{ selectedCity?.name || cityPlaceholder }}
            </view>
          </view>
        </picker>
        <view v-if="cityError" class="error-message">
          {{ cityError }}
        </view>
      </view>

      <!-- 区县选择 -->
      <view v-if="showDistrict" class="picker-wrapper">
        <picker
          class="region-picker-select"
          :value="districtIndex"
          :range="currentDistricts"
          range-key="name"
          :disabled="!selectedCity || disabled || loadingDistricts"
          @change="handleDistrictChange"
        >
          <view class="picker-view" :class="{
            placeholder: !selectedDistrict,
            disabled: !selectedCity || disabled || loadingDistricts
          }">
            <view v-if="loadingDistricts" class="loading-text">
              加载中...
            </view>
            <view v-else-if="!selectedCity" class="disabled-text">
              {{ districtPlaceholder }}
            </view>
            <view v-else>
              {{ selectedDistrict?.name || districtPlaceholder }}
            </view>
          </view>
        </picker>
        <view v-if="districtError" class="error-message">
          {{ districtError }}
        </view>
      </view>
    </view>

    <!-- 已选区域显示 -->
    <view v-if="selectedRegionText && showSelectedText" class="selected-region">
      <text class="selected-text">{{ selectedRegionText }}</text>
      <text v-if="showClear && selectedRegionText" class="clear-btn" @click="handleClear">
        清除
      </text>
    </view>

    <!-- 错误显示 -->
    <view v-if="error" class="global-error">
      <text>{{ error }}</text>
    </view>

    <!-- 调试信息 -->
    <view v-if="debug" class="debug-info">
      <text>组件状态:</text>
      <text>省份数量: {{ provincesList.length }}</text>
      <text>当前城市数量: {{ currentCities.length }}</text>
      <text>当前区县数量: {{ currentDistricts.length }}</text>
      <text>选中省份: {{ selectedProvince?.name || '无' }} (ID: {{ selectedProvince?.id || '无' }})</text>
      <text>选中城市: {{ selectedCity?.name || '无' }} (ID: {{ selectedCity?.id || '无' }})</text>
      <text v-if="showDistrict">选中区县: {{ selectedDistrict?.name || '无' }} (ID: {{ selectedDistrict?.id || '无' }})</text>
      <text>加载中: {{ isLoading }}</text>
      <text>存储错误: {{ regionStore.error }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRegionStore } from '@/stores/region'
import type { Province, City, District, SelectedRegion } from '@/types/region'

interface Props {
  modelValue?: SelectedRegion
  showDistrict?: boolean
  showCity?: boolean
  disabled?: boolean
  debug?: boolean
  showClear?: boolean
  showSelectedText?: boolean
  provincePlaceholder?: string
  cityPlaceholder?: string
  districtPlaceholder?: string
  autoLoadProvinces?: boolean
  preloadTopProvinces?: number
  forceRefresh?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => ({}),
  showDistrict: true,
  showCity: true,
  disabled: false,
  debug: false,
  showClear: true,
  showSelectedText: true,
  provincePlaceholder: '请选择省份',
  cityPlaceholder: '请选择城市',
  districtPlaceholder: '请选择区县',
  autoLoadProvinces: true,
  preloadTopProvinces: 3,
  forceRefresh: false
})

interface Emits {
  'update:modelValue': [value: SelectedRegion]
  'change': [value: SelectedRegion]
  'province-change': [province: Province | null]
  'city-change': [city: City | null]
  'district-change': [district: District | null]
  'clear': []
  'loading': [isLoading: boolean]
  'error': [error: string | null]
}

const emit = defineEmits<Emits>()

// Store
const regionStore = useRegionStore()

// 状态
const selectedProvince = ref<Province | null>(null)
const selectedCity = ref<City | null>(null)
const selectedDistrict = ref<District | null>(null)

// 加载状态
const loadingProvinces = ref(false)
const loadingCities = ref(false)
const loadingDistricts = ref(false)

// 错误状态
const error = ref<string | null>(null)
const provinceError = ref<string | null>(null)
const cityError = ref<string | null>(null)
const districtError = ref<string | null>(null)

// 是否正在初始化
const initializing = ref(false)
const initialized = ref(false)

// 监听 store 的 loading 状态
const isLoading = computed(() => {
  return loadingProvinces.value || loadingCities.value || loadingDistricts.value
})

// 省份列表 - 安全访问
const provincesList = computed(() => {
  try {
    return regionStore.provinces || []
  } catch (err) {
    console.warn('获取省份列表失败:', err)
    provinceError.value = '获取省份列表失败'
    return []
  }
})

// 当前显示的城市和区县
const currentCities = computed(() => {
  if (!selectedProvince.value) {
    return []
  }
  try {
    const cities = regionStore.getCitiesByProvince(selectedProvince.value.id)
    return Array.isArray(cities) ? cities : []
  } catch (err) {
    console.warn('获取城市列表失败:', err)
    cityError.value = '获取城市列表失败'
    return []
  }
})

const currentDistricts = computed(() => {
  if (!selectedCity.value || !props.showDistrict) {
    return []
  }
  try {
    const districts = regionStore.getDistrictsByCity(selectedCity.value.id)
    return Array.isArray(districts) ? districts : []
  } catch (err) {
    console.warn('获取区县列表失败:', err)
    districtError.value = '获取区县列表失败'
    return []
  }
})

// 计算索引 - 添加类型安全
const provinceIndex = computed(() => {
  if (!selectedProvince.value || !Array.isArray(provincesList.value) || provincesList.value.length === 0) {
    return -1
  }

  return provincesList.value.findIndex(p => p.id === selectedProvince.value!.id)
})

const cityIndex = computed(() => {
  if (!selectedCity.value || !Array.isArray(currentCities.value) || currentCities.value.length === 0) {
    return -1
  }

  return currentCities.value.findIndex(c => c.id === selectedCity.value!.id)
})

const districtIndex = computed(() => {
  if (!selectedDistrict.value || !Array.isArray(currentDistricts.value) || currentDistricts.value.length === 0) {
    return -1
  }

  return currentDistricts.value.findIndex(d => d.id === selectedDistrict.value!.id)
})

// 已选区域的文本显示
const selectedRegionText = computed(() => {
  const parts: string[] = []
  if (selectedProvince.value?.name) parts.push(selectedProvince.value.name)
  if (props.showCity && selectedCity.value?.name) parts.push(selectedCity.value.name)
  if (props.showDistrict && selectedDistrict.value?.name) parts.push(selectedDistrict.value.name)
  return parts.join(' / ')
})

// 获取完整的选中区域对象
const getSelectedRegion = (): SelectedRegion => {
  return {
    province: selectedProvince.value || undefined,
    city: props.showCity ? selectedCity.value || undefined : undefined,
    district: props.showDistrict ? selectedDistrict.value || undefined : undefined
  }
}

// 加载省份数据
const loadProvinces = async (forceRefresh = props.forceRefresh): Promise<void> => {
  if (loadingProvinces.value) return

  loadingProvinces.value = true
  provinceError.value = null
  emit('loading', true)

  try {
    await regionStore.loadProvinces(forceRefresh)
    console.log('省份数据加载完成，数量:', regionStore.provinces?.length || 0)
  } catch (err) {
    const message = err instanceof Error ? err.message : '加载省份数据失败'
    provinceError.value = message
    error.value = message
    emit('error', message)
    console.error('加载省份数据失败:', err)
  } finally {
    loadingProvinces.value = false
    emit('loading', false)
  }
}

// 加载城市数据
const loadCities = async (provinceId: number, forceRefresh = props.forceRefresh): Promise<void> => {
  if (loadingCities.value) return

  loadingCities.value = true
  cityError.value = null
  emit('loading', true)

  try {
    await regionStore.loadCities(provinceId, forceRefresh)
    console.log('城市数据加载完成，数量:', currentCities.value.length)
  } catch (err) {
    const message = err instanceof Error ? err.message : `加载城市数据失败`
    cityError.value = message
    error.value = message
    emit('error', message)
    console.error('加载城市数据失败:', err)
  } finally {
    loadingCities.value = false
    emit('loading', false)
  }
}

// 加载区县数据
const loadDistricts = async (cityId: number, forceRefresh = props.forceRefresh): Promise<void> => {
  if (!props.showDistrict || loadingDistricts.value) return

  loadingDistricts.value = true
  districtError.value = null
  emit('loading', true)

  try {
    await regionStore.loadDistricts(cityId, forceRefresh)
    console.log('区县数据加载完成，数量:', currentDistricts.value.length)
  } catch (err) {
    const message = err instanceof Error ? err.message : `加载区县数据失败`
    districtError.value = message
    error.value = message
    emit('error', message)
    console.error('加载区县数据失败:', err)
  } finally {
    loadingDistricts.value = false
    emit('loading', false)
  }
}

// 根据外部值同步选择状态
const syncFromExternalValue = async (value: SelectedRegion): Promise<void> => {
  console.log('同步外部值:', value)

  // 重置所有选择
  selectedProvince.value = null
  selectedCity.value = null
  selectedDistrict.value = null

  // 确保省份数据已加载
  if (props.autoLoadProvinces && provincesList.value.length === 0) {
    await loadProvinces()
  }

  // 设置省份
  if (value.province) {
    const province = provincesList.value.find(p => p.id === value.province!.id)
    if (province) {
      selectedProvince.value = province

      // 加载城市数据
      if (value.city || props.showCity) {
        await loadCities(province.id)

        // 设置城市
        if (value.city && currentCities.value.length > 0) {
          const city = currentCities.value.find(c => c.id === value.city!.id)
          if (city) {
            selectedCity.value = city

            // 加载区县数据
            if (value.district || props.showDistrict) {
              await loadDistricts(city.id)

              // 设置区县
              if (value.district && currentDistricts.value.length > 0) {
                const district = currentDistricts.value.find(d => d.id === value.district!.id)
                if (district) {
                  selectedDistrict.value = district
                }
              }
            }
          }
        }
      }
    }
  }
}

// 触发更新事件
const emitUpdate = () => {
  const selectedRegion = getSelectedRegion()
  emit('update:modelValue', selectedRegion)
  emit('change', selectedRegion)
}

// 省份变化
const handleProvinceChange = async (e: any) => {
  const index = e.detail.value

  if (index < 0 || index >= provincesList.value.length) {
    console.warn('无效的省份索引')
    return
  }

  const province = provincesList.value[index]

  // 重置下级选择
  selectedProvince.value = province
  selectedCity.value = null
  selectedDistrict.value = null

  // 触发事件
  emit('province-change', province)

  // 如果显示城市，加载城市数据
  if (props.showCity) {
    await loadCities(province.id)
  }

  emitUpdate()
}

// 城市变化
const handleCityChange = async (e: any) => {
  const index = e.detail.value

  if (!selectedProvince.value || index < 0 || index >= currentCities.value.length) {
    console.warn('无效的城市索引')
    return
  }

  const city = currentCities.value[index]

  // 更新选择
  selectedCity.value = city
  selectedDistrict.value = null

  // 触发事件
  emit('city-change', city)

  // 如果显示区县，加载区县数据
  if (props.showDistrict) {
    await loadDistricts(city.id)
  }

  emitUpdate()
}

// 区县变化
const handleDistrictChange = (e: any) => {
  const index = e.detail.value

  if (!selectedCity.value || !props.showDistrict || index < 0 || index >= currentDistricts.value.length) {
    console.warn('无效的区县索引')
    return
  }

  const district = currentDistricts.value[index]
  selectedDistrict.value = district

  // 触发事件
  emit('district-change', district)
  emitUpdate()
}

// 清除选择
const handleClear = () => {
  selectedProvince.value = null
  selectedCity.value = null
  selectedDistrict.value = null

  emit('clear')
  emitUpdate()
}

// 预加载数据
const preloadData = async (): Promise<void> => {
  if (initialized.value) return

  initializing.value = true
  console.log('RegionPicker 开始预加载数据')

  try {
    // 加载省份
    await loadProvinces()

    // 如果需要预加载热门省份的城市
    if (props.preloadTopProvinces > 0 && provincesList.value.length > 0) {
      const topProvinces = provincesList.value.slice(0, props.preloadTopProvinces)
      const loadPromises = topProvinces.map(province => loadCities(province.id))
      await Promise.allSettled(loadPromises)
    }

    initialized.value = true
    console.log('RegionPicker 预加载完成')
  } catch (err) {
    console.error('RegionPicker 预加载失败:', err)
  } finally {
    initializing.value = false
  }
}

// 组件挂载时
onMounted(async () => {
  console.log('RegionPicker 组件挂载')

  // 监听 store 错误
  const unwatchError = watch(() => regionStore.error, (newError) => {
    if (newError) {
      error.value = newError
      emit('error', newError)
    }
  })

  onUnmounted(() => {
    unwatchError()
  })

  // 如果有外部值，先同步
  if (Object.keys(props.modelValue).length > 0) {
    await syncFromExternalValue(props.modelValue)
  }

  // 自动加载省份数据
  if (props.autoLoadProvinces && provincesList.value.length === 0) {
    await preloadData()
  }
})

// 监听外部值变化
watch(() => props.modelValue, async (newValue) => {
  if (newValue && Object.keys(newValue).length > 0) {
    await syncFromExternalValue(newValue)
  }
}, { deep: true })

// 监听 forceRefresh 变化
watch(() => props.forceRefresh, (newValue) => {
  if (newValue) {
    // 重新加载当前选中的数据
    if (selectedProvince.value) {
      loadProvinces(true)
      if (selectedCity.value) {
        loadCities(selectedProvince.value.id, true)
        if (selectedDistrict.value) {
          loadDistricts(selectedCity.value.id, true)
        }
      }
    }
  }
})

// 暴露方法给父组件
defineExpose({
  loadProvinces,
  loadCities,
  loadDistricts,
  preloadData,
  clearSelection: handleClear,
  getSelectedRegion,
  refresh: () => preloadData()
})
</script>

<style scoped lang="scss">
.region-picker {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.region-cascader {
  display: flex;
  flex-direction: column;
  gap: 20rpx;

  &.disabled-cascader {
    opacity: 0.6;
    pointer-events: none;
  }
}

.picker-wrapper {
  position: relative;
  width: 100%;
}

.region-picker-select {
  width: 100%;
}

.picker-view {
  height: 80rpx;
  line-height: 80rpx;
  padding: 0 32rpx 0 24rpx;
  background-color: #ffffff;
  border: 2rpx solid #e5e5e5;
  border-radius: 8rpx;
  font-size: 28rpx;
  color: #333333;
  position: relative;
  transition: all 0.3s ease;

  &.placeholder {
    color: #999999;
  }

  &.disabled {
    background-color: #f5f5f5;
    border-color: #d9d9d9;
    cursor: not-allowed;

    .disabled-text {
      color: #bfbfbf;
    }
  }

  &::after {
    content: '';
    position: absolute;
    right: 24rpx;
    top: 50%;
    transform: translateY(-50%);
    width: 0;
    height: 0;
    border-left: 8rpx solid transparent;
    border-right: 8rpx solid transparent;
    border-top: 8rpx solid #999999;
    transition: transform 0.3s;
  }

  &:active:not(.disabled) {
    border-color: #409eff;
  }
}

.loading-text {
  color: #1890ff;
  font-size: 26rpx;
}

.error-message {
  margin-top: 8rpx;
  font-size: 24rpx;
  color: #ff4d4f;
}

.selected-region {
  margin-top: 24rpx;
  padding: 20rpx;
  background-color: #f5f7fa;
  border-radius: 8rpx;
  border: 1rpx solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.selected-text {
  font-size: 28rpx;
  color: #333333;
  line-height: 1.4;
  flex: 1;
}

.clear-btn {
  font-size: 26rpx;
  color: #1890ff;
  padding: 8rpx 16rpx;
  border-radius: 4rpx;
  cursor: pointer;
  transition: background-color 0.3s;

  &:active {
    background-color: rgba(24, 144, 255, 0.1);
  }
}

.global-error {
  margin-top: 16rpx;
  padding: 16rpx;
  background-color: #fff2f0;
  border: 1rpx solid #ffccc7;
  border-radius: 8rpx;
  font-size: 26rpx;
  color: #ff4d4f;
}

.debug-info {
  margin-top: 24rpx;
  padding: 20rpx;
  background-color: #f6ffed;
  border: 1rpx solid #b7eb8f;
  border-radius: 8rpx;
  font-size: 24rpx;
  color: #52c41a;

  text {
    display: block;
    margin-bottom: 8rpx;
    font-family: monospace;

    &:first-child {
      font-weight: bold;
      margin-bottom: 12rpx;
      color: #389e0d;
    }
  }
}
</style>