<!-- pages/region.vue -->
<template>
  <view class="region-test-page">
    <view class="header">
      <text class="title">RegionPicker 测试页面</text>
    </view>

    <view class="section">
      <text class="section-title">基本测试</text>
      <RegionPicker
        v-model="selectedRegion"
        :debug="true"
        @change="handleRegionChange"
      />

      <view class="control-buttons">
        <button class="test-btn" @click="resetSelection">重置选择</button>
        <button class="test-btn" @click="selectBeijing">选择北京市</button>
        <button class="test-btn" @click="logStoreState">查看Store状态</button>
      </view>

      <view class="result-display">
        <text class="result-title">当前选择：</text>
        <text class="result-text">{{ selectedRegionText }}</text>
      </view>

      <view class="raw-data">
        <text class="data-title">原始数据：</text>
        <text class="data-text">{{ JSON.stringify(selectedRegion, null, 2) }}</text>
      </view>
    </view>

    <view class="section">
      <text class="section-title">不显示区县</text>
      <RegionPicker
        v-model="selectedRegion2"
        :show-district="false"
      />
    </view>

    <view class="store-info">
      <text class="info-title">Store 信息：</text>
      <text class="info-text">省份数量：{{ store.provinces?.length || 0 }}</text>
      <text class="info-text">热门城市：{{ store.hotCities?.length || 0 }}</text>
      <text class="info-text">加载状态：{{ store.isLoading ? '加载中' : '空闲' }}</text>
      <text class="info-text">错误信息：{{ storeError }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { useRegionStore } from "@/stores/region";
import type { SelectedRegion } from "@/types/region";
import RegionPicker from "@/components/region-picker/RegionPicker.vue";

const store = useRegionStore()

// 安全访问 store.error
const storeError = computed(() => {
  try {
    // 检查 store.error 是否存在且有 value 属性
    if (store.error && typeof store.error === 'object' && 'value' in store.error) {
      return store.error.value || '无错误'
    }
    return 'store.error 格式不正确'
  } catch (error) {
    return '访问错误信息失败'
  }
})

// 测试1：基本用法
const selectedRegion = ref<SelectedRegion>({})
const selectedRegionText = computed(() => {
  const { province, city, district } = selectedRegion.value
  const parts: string[] = []
  if (province?.name) parts.push(province.name)
  if (city?.name) parts.push(city.name)
  if (district?.name) parts.push(district.name)
  return parts.join(' / ') || '未选择'
})

const handleRegionChange = (region: SelectedRegion) => {
  console.log('区域变化事件：', region)
}

// 测试2：不显示区县
const selectedRegion2 = ref<SelectedRegion>({})

// 控制方法
const resetSelection = () => {
  selectedRegion.value = {}
  console.log('已重置选择')
}

const selectBeijing = async () => {
  console.log('开始选择北京市')

  try {
    // 等待省份加载完成
    if (store.provinces?.length === 0) {
      console.log('开始加载省份数据...')
      await store.loadProvinces()
    }

    console.log('查找北京市...')
    // 查找北京市
    const beijing = store.provinces?.find(p => p.name === '北京市')

    if (beijing) {
      console.log('找到北京市:', beijing)
      selectedRegion.value = {
        province: beijing
      }

      // 加载北京市的城市
      console.log('开始加载北京市的城市...')
      await store.loadCities(beijing.id)
      const cities = store.getCitiesByProvince(beijing.id)
      console.log('北京市的城市数据:', cities)

      if (cities?.length > 0) {
        selectedRegion.value.city = cities[0]
        console.log('设置城市:', cities[0])

        // 加载区县
        console.log('开始加载区县...')
        await store.loadDistricts(cities[0].id)
        const districts = store.getDistrictsByCity(cities[0].id)
        console.log('区县数据:', districts)

        if (districts?.length > 0) {
          selectedRegion.value.district = districts[0]
          console.log('设置区县:', districts[0])
        }
      }

      console.log('最终选择结果:', selectedRegion.value)
    } else {
      console.log('未找到北京市')
      // 尝试查找第一个省份作为测试
      if (store.provinces?.length > 0) {
        const firstProvince = store.provinces[0]
        console.log('使用第一个省份作为测试:', firstProvince)
        selectedRegion.value = { province: firstProvince }

        // 加载城市
        await store.loadCities(firstProvince.id)
        const cities = store.getCitiesByProvince(firstProvince.id)
        if (cities?.length > 0) {
          selectedRegion.value.city = cities[0]

          // 加载区县
          await store.loadDistricts(cities[0].id)
          const districts = store.getDistrictsByCity(cities[0].id)
          if (districts?.length > 0) {
            selectedRegion.value.district = districts[0]
          }
        }
      }
    }
  } catch (error) {
    console.error('选择北京市时发生错误:', error)
  }
}

const logStoreState = () => {
  console.log('===== Store 状态 =====')

  try {
    // 安全地访问所有属性
    const state = {
      provincesCount: store.provinces?.length || 0,
      provinces: store.provinces ?
        store.provinces.slice(0, 3).map(p => ({ id: p.id, name: p.name })) :
        '未加载',
      citiesCount: store.cities?.size || 0,
      districtsCount: store.districts?.size || 0,
      hotCitiesCount: store.hotCities?.length || 0,
      isLoading: store.isLoading,
      error: storeError.value,
      // 遍历 Map 的前几项
      cachedCities: store.cities ?
        Array.from(store.cities.entries()).slice(0, 2) :
        '无缓存'
    }

    console.log('Store 状态:', state)

    // 打印详细的调试信息
    console.log('详细省份列表:')
    store.provinces?.forEach((province, index) => {
      console.log(`  [${index}] ${province.id}: ${province.name}`)
    })

  } catch (error) {
    console.error('获取 Store 状态时发生错误:', error)
  }

  console.log('===== 当前选择状态 =====')
  console.log('selectedRegion:', selectedRegion.value)
  console.log('selectedRegionText:', selectedRegionText.value)
}
</script>

<style scoped lang="scss">
.region-test-page {
  padding: 32rpx;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.header {
  margin-bottom: 32rpx;
  padding: 32rpx;
  background-color: #ffffff;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333333;
}

.section {
  margin-bottom: 32rpx;
  padding: 32rpx;
  background-color: #ffffff;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.section-title {
  display: block;
  font-size: 32rpx;
  font-weight: 500;
  color: #333333;
  margin-bottom: 24rpx;
}

.control-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin: 24rpx 0;
}

.test-btn {
  padding: 16rpx 32rpx;
  background-color: #409eff;
  color: white;
  border-radius: 8rpx;
  font-size: 28rpx;
  border: none;

  &:active {
    opacity: 0.8;
  }
}

.result-display {
  margin: 24rpx 0;
  padding: 24rpx;
  background-color: #f0f9eb;
  border-radius: 8rpx;
  border: 1rpx solid #e1f3d8;
}

.result-title {
  display: block;
  font-size: 28rpx;
  font-weight: 500;
  color: #67c23a;
  margin-bottom: 12rpx;
}

.result-text {
  font-size: 32rpx;
  color: #333333;
  font-weight: 500;
}

.raw-data {
  margin-top: 24rpx;
  padding: 24rpx;
  background-color: #f4f4f5;
  border-radius: 8rpx;
  border: 1rpx solid #e9e9eb;
}

.data-title {
  display: block;
  font-size: 28rpx;
  font-weight: 500;
  color: #909399;
  margin-bottom: 12rpx;
}

.data-text {
  font-size: 24rpx;
  color: #666666;
  font-family: monospace;
  word-break: break-all;
}

.store-info {
  padding: 32rpx;
  background-color: #ffffff;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.info-title {
  display: block;
  font-size: 32rpx;
  font-weight: 500;
  color: #333333;
  margin-bottom: 16rpx;
}

.info-text {
  display: block;
  font-size: 28rpx;
  color: #666666;
  margin-bottom: 8rpx;

  &:last-child {
    margin-bottom: 0;
  }
}
</style>