<!-- components/region-picker/RegionPicker.vue -->
<template>
  <view class="region-picker">
    <!-- 联动选择模式 -->
    <view class="region-cascader">
      <!-- 省份选择 -->
      <view class="picker-wrapper">
        <picker
          class="region-picker-select"
          :value="provinceIndex"
          :range="provincesList"
          range-key="name"
          :disabled="disabled"
          @change="handleProvinceChange"
        >
          <view class="picker-view" :class="{ placeholder: !selectedProvince }">
            {{ selectedProvince?.name || provincePlaceholder }}
          </view>
        </picker>
      </view>

      <!-- 城市选择 -->
      <view class="picker-wrapper">
        <picker
          class="region-picker-select"
          :value="cityIndex"
          :range="currentCities"
          range-key="name"
          :disabled="!selectedProvince || disabled"
          @change="handleCityChange"
        >
          <view class="picker-view" :class="{ placeholder: !selectedCity }">
            {{ selectedCity?.name || cityPlaceholder }}
          </view>
        </picker>
      </view>

      <!-- 区县选择 -->
      <view v-if="showDistrict" class="picker-wrapper">
        <picker
          class="region-picker-select"
          :value="districtIndex"
          :range="currentDistricts"
          range-key="name"
          :disabled="!selectedCity || disabled"
          @change="handleDistrictChange"
        >
          <view class="picker-view" :class="{ placeholder: !selectedDistrict }">
            {{ selectedDistrict?.name || districtPlaceholder }}
          </view>
        </picker>
      </view>
    </view>

    <!-- 已选区域显示 -->
    <view v-if="selectedRegionText" class="selected-region">
      <text class="selected-text">{{ selectedRegionText }}</text>
    </view>

    <!-- 加载状态 -->
    <view v-if="loadingProvinces && provincesList.length === 0" class="loading-state">
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 调试信息 -->
    <view v-if="debug" class="debug-info">
      <text>省份数量: {{ provincesList.length }}</text>
      <text>当前城市数量: {{ currentCities.length }}</text>
      <text>当前区县数量: {{ currentDistricts.length }}</text>
      <text>选中省份: {{ selectedProvince?.name || '无' }}</text>
      <text>选中城市: {{ selectedCity?.name || '无' }}</text>
      <text>选中区县: {{ selectedDistrict?.name || '无' }}</text>
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
  disabled?: boolean
  debug?: boolean
  provincePlaceholder?: string
  cityPlaceholder?: string
  districtPlaceholder?: string
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => ({}),
  showDistrict: true,
  disabled: false,
  debug: false,
  provincePlaceholder: '请选择省份',
  cityPlaceholder: '请选择城市',
  districtPlaceholder: '请选择区县'
})

const emit = defineEmits<{
  'update:modelValue': [value: SelectedRegion]
  'change': [value: SelectedRegion]
}>()

// Store
const regionStore = useRegionStore()

// 选中的区域
const selectedProvince = ref<Province | null>(null)
const selectedCity = ref<City | null>(null)
const selectedDistrict = ref<District | null>(null)

// 加载状态
const loadingProvinces = ref(false)
const loadingCities = ref(false)
const loadingDistricts = ref(false)

// 省份列表 - 安全访问
const provincesList = computed(() => {
  try {
    return regionStore.provinces || []
  } catch (error) {
    console.warn('获取省份列表失败:', error)
    return []
  }
})

// 当前显示的城市和区县
const currentCities = computed(() => {
  if (!selectedProvince.value) {
    return []
  }
  try {
    return regionStore.getCitiesByProvince(selectedProvince.value.id) || []
  } catch (error) {
    console.warn('获取城市列表失败:', error)
    return []
  }
})

const currentDistricts = computed(() => {
  if (!selectedCity.value) {
    return []
  }
  try {
    return regionStore.getDistrictsByCity(selectedCity.value.id) || []
  } catch (error) {
    console.warn('获取区县列表失败:', error)
    return []
  }
})

// 计算索引 - 添加安全检查
const provinceIndex = computed(() => {
  if (!selectedProvince.value || !provincesList.value || provincesList.value.length === 0) {
    return -1
  }

  try {
    const index = provincesList.value.findIndex(p => {
      try {
        return p.id === selectedProvince.value!.id
      } catch (error) {
        return false
      }
    })
    return index
  } catch (error) {
    console.warn('计算省份索引失败:', error)
    return -1
  }
})

const cityIndex = computed(() => {
  if (!selectedCity.value || !currentCities.value || currentCities.value.length === 0) {
    return -1
  }

  try {
    const index = currentCities.value.findIndex(c => {
      try {
        return c.id === selectedCity.value!.id
      } catch (error) {
        return false
      }
    })
    return index
  } catch (error) {
    console.warn('计算城市索引失败:', error)
    return -1
  }
})

const districtIndex = computed(() => {
  if (!selectedDistrict.value || !currentDistricts.value || currentDistricts.value.length === 0) {
    return -1
  }

  try {
    const index = currentDistricts.value.findIndex(d => {
      try {
        return d.id === selectedDistrict.value!.id
      } catch (error) {
        return false
      }
    })
    return index
  } catch (error) {
    console.warn('计算区县索引失败:', error)
    return -1
  }
})

// 已选区域的文本显示
const selectedRegionText = computed(() => {
  const parts: string[] = []
  if (selectedProvince.value?.name) parts.push(selectedProvince.value.name)
  if (selectedCity.value?.name) parts.push(selectedCity.value.name)
  if (selectedDistrict.value?.name) parts.push(selectedDistrict.value.name)
  return parts.join(' / ')
})

// 组件挂载时加载数据
onMounted(async () => {
  console.log('RegionPicker 开始初始化')
  try {
    loadingProvinces.value = true
    console.log('开始加载省份数据...')

    // 加载省份数据
    await regionStore.loadProvinces()
    console.log('省份数据加载完成，数量:', regionStore.provinces?.length || 0)

    // 如果外部传入了值，同步选择状态
    if (props.modelValue?.province) {
      console.log('外部传入省份:', props.modelValue.province)
      selectedProvince.value = props.modelValue.province

      // 加载城市数据
      if (selectedProvince.value) {
        loadingCities.value = true
        try {
          await regionStore.loadCities(selectedProvince.value.id)
          console.log('城市数据加载完成')
        } finally {
          loadingCities.value = false
        }
      }
    }

    if (props.modelValue?.city && selectedProvince.value) {
      console.log('外部传入城市:', props.modelValue.city)
      selectedCity.value = props.modelValue.city

      // 加载区县数据
      if (selectedCity.value) {
        loadingDistricts.value = true
        try {
          await regionStore.loadDistricts(selectedCity.value.id)
          console.log('区县数据加载完成')
        } finally {
          loadingDistricts.value = false
        }
      }
    }

    if (props.modelValue?.district) {
      console.log('外部传入区县:', props.modelValue.district)
      selectedDistrict.value = props.modelValue.district
    }

    console.log('RegionPicker 初始化完成')
  } catch (error) {
    console.error('初始化区域选择器失败:', error)
  } finally {
    loadingProvinces.value = false
  }
})

// 监听外部值变化
watch(() => props.modelValue, (newValue) => {
  console.log('外部值变化:', newValue)
  selectedProvince.value = newValue?.province || null
  selectedCity.value = newValue?.city || null
  selectedDistrict.value = newValue?.district || null
}, { deep: true })

// 省份变化
const handleProvinceChange = async (e: any) => {
  console.log('省份选择事件:', e)

  try {
    const index = e.detail.value
    console.log('选中索引:', index)

    // 防御性检查 - 修复这里
    if (!provincesList.value || typeof index !== 'number' || index < 0 || index >= (provincesList.value?.length || 0)) {
      console.warn('无效的省份索引或省份列表为空', {
        index,
        provincesLength: provincesList.value?.length || 0
      })

      selectedProvince.value = null
      selectedCity.value = null
      selectedDistrict.value = null

      const newValue: SelectedRegion = {}
      emit('update:modelValue', newValue)
      emit('change', newValue)
      return
    }

    const province = provincesList.value[index]
    console.log('选中的省份:', province)

    if (!province) {
      console.warn('未找到省份')
      selectedProvince.value = null
      selectedCity.value = null
      selectedDistrict.value = null

      const newValue: SelectedRegion = {}
      emit('update:modelValue', newValue)
      emit('change', newValue)
      return
    }

    selectedProvince.value = province
    selectedCity.value = null
    selectedDistrict.value = null

    // 加载该省份的城市
    console.log('开始加载城市...')
    loadingCities.value = true
    try {
      await regionStore.loadCities(province.id)
      console.log('城市加载完成，数量:', regionStore.getCitiesByProvince(province.id)?.length || 0)
    } finally {
      loadingCities.value = false
    }

    // 触发更新
    const newValue: SelectedRegion = {
      province: province,
      city: null,
      district: null
    }

    console.log('触发省份变化事件:', newValue)
    emit('update:modelValue', newValue)
    emit('change', newValue)
  } catch (error) {
    console.error('选择省份时发生错误:', error)
  }
}

// 城市变化
const handleCityChange = async (e: any) => {
  console.log('城市选择事件:', e)

  try {
    const index = e.detail.value
    console.log('选中索引:', index, '城市总数:', currentCities.value.length)

    // 防御性检查
    if (!selectedProvince.value || !currentCities.value ||
      typeof index !== 'number' || index < 0 || index >= (currentCities.value?.length || 0)) {
      console.warn('无效的城市索引')
      selectedCity.value = null
      selectedDistrict.value = null

      const newValue: SelectedRegion = {
        province: selectedProvince.value!,
        city: null,
        district: null
      }

      emit('update:modelValue', newValue)
      emit('change', newValue)
      return
    }

    const city = currentCities.value[index]
    console.log('选中的城市:', city)

    if (!city) {
      console.warn('未找到城市')
      selectedCity.value = null
      selectedDistrict.value = null

      const newValue: SelectedRegion = {
        province: selectedProvince.value!,
        city: null,
        district: null
      }

      emit('update:modelValue', newValue)
      emit('change', newValue)
      return
    }

    selectedCity.value = city
    selectedDistrict.value = null

    // 加载该城市的区县
    console.log('开始加载区县...')
    loadingDistricts.value = true
    try {
      await regionStore.loadDistricts(city.id)
      console.log('区县加载完成')
    } finally {
      loadingDistricts.value = false
    }

    // 触发更新
    const newValue: SelectedRegion = {
      province: selectedProvince.value!,
      city: city,
      district: null
    }

    console.log('触发城市变化事件:', newValue)
    emit('update:modelValue', newValue)
    emit('change', newValue)
  } catch (error) {
    console.error('选择城市时发生错误:', error)
  }
}

// 区县变化
const handleDistrictChange = (e: any) => {
  console.log('区县选择事件:', e)

  try {
    const index = e.detail.value
    console.log('选中索引:', index, '区县总数:', currentDistricts.value.length)

    // 防御性检查
    if (!selectedCity.value || !currentDistricts.value ||
      typeof index !== 'number' || index < 0 || index >= (currentDistricts.value?.length || 0)) {
      console.warn('无效的区县索引')
      selectedDistrict.value = null

      const newValue: SelectedRegion = {
        province: selectedProvince.value!,
        city: selectedCity.value!,
        district: null
      }

      emit('update:modelValue', newValue)
      emit('change', newValue)
      return
    }

    const district = currentDistricts.value[index]
    console.log('选中的区县:', district)

    if (!district) {
      console.warn('未找到区县')
      selectedDistrict.value = null

      const newValue: SelectedRegion = {
        province: selectedProvince.value!,
        city: selectedCity.value!,
        district: null
      }

      emit('update:modelValue', newValue)
      emit('change', newValue)
      return
    }

    selectedDistrict.value = district

    // 触发更新
    const newValue: SelectedRegion = {
      province: selectedProvince.value!,
      city: selectedCity.value!,
      district: district
    }

    console.log('触发区县变化事件:', newValue)
    emit('update:modelValue', newValue)
    emit('change', newValue)
  } catch (error) {
    console.error('选择区县时发生错误:', error)
  }
}
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
  gap: 16rpx;
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

  &.placeholder {
    color: #999999;
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
  }
}

.selected-region {
  margin-top: 16rpx;
  padding: 16rpx;
  background-color: #f5f7fa;
  border-radius: 8rpx;
  border: 1rpx solid #e4e7ed;
}

.selected-text {
  font-size: 28rpx;
  color: #333333;
  line-height: 1.4;
}

.loading-state {
  margin-top: 16rpx;
  text-align: center;
  padding: 16rpx;
}

.loading-text {
  font-size: 28rpx;
  color: #999999;
}

.debug-info {
  margin-top: 16rpx;
  padding: 16rpx;
  background-color: #f0f0f0;
  border-radius: 8rpx;
  font-size: 24rpx;
  color: #666;

  text {
    display: block;
    margin-bottom: 8rpx;
  }
}
</style>