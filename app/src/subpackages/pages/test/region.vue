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
        @error="handlePickerError"
      />

      <view class="control-buttons">
        <button class="test-btn" @click="resetSelection">重置选择</button>
        <button class="test-btn" @click="selectBeijing">选择北京市</button>
        <button class="test-btn" @click="selectShanghai">选择上海市</button>
        <button class="test-btn" @click="testError">测试错误</button>
        <button class="test-btn" @click="logStoreState">查看Store状态</button>
        <button class="test-btn" @click="clearStoreCache">清除缓存</button>
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
      <text class="section-title">配置选项</text>
      <RegionPicker
        v-model="selectedRegion2"
        :show-district="showDistrict"
        :disabled="disabled"
        :show-selected-text="showSelectedText"
        :show-clear="showClear"
        :province-placeholder="customProvincePlaceholder"
        :city-placeholder="customCityPlaceholder"
        :district-placeholder="customDistrictPlaceholder"
      />

      <view class="config-controls">
        <view class="config-item">
          <text class="config-label">显示区县：</text>
          <switch :checked="showDistrict" @change="toggleShowDistrict" />
        </view>
        <view class="config-item">
          <text class="config-label">禁用状态：</text>
          <switch :checked="disabled" @change="toggleDisabled" />
        </view>
        <view class="config-item">
          <text class="config-label">显示选中文本：</text>
          <switch :checked="showSelectedText" @change="toggleShowSelectedText" />
        </view>
        <view class="config-item">
          <text class="config-label">显示清除按钮：</text>
          <switch :checked="showClear" @change="toggleShowClear" />
        </view>
      </view>
    </view>

    <view class="section">
      <text class="section-title">热门城市选择</text>
      <view class="hot-cities">
        <view
          v-for="city in store.hotCities"
          :key="city.id"
          class="hot-city-item"
          @click="selectHotCity(city)"
        >
          <text class="city-name">{{ city.name }}</text>
          <text class="city-pinyin">{{ city.pinyin }}</text>
        </view>
      </view>
    </view>

    <view class="section">
      <text class="section-title">Store 信息</text>
      <view class="store-info-grid">
        <view class="info-item">
          <text class="info-label">省份数量：</text>
          <text class="info-value">{{ store.provinces.length }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">热门城市：</text>
          <text class="info-value">{{ store.hotCities.length }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">加载状态：</text>
          <text class="info-value" :class="{ 'loading-text': store.isLoading }">
            {{ store.isLoading ? '加载中...' : '空闲' }}
          </text>
        </view>
        <view class="info-item">
          <text class="info-label">错误信息：</text>
          <text class="info-value error-text">{{ storeError }}</text>
        </view>
        <view class="info-item full-width">
          <text class="info-label">缓存城市数据：</text>
          <text class="info-value">
            {{ store.cities.size }} 个省份的城市缓存
          </text>
        </view>
        <view class="info-item full-width">
          <text class="info-label">缓存区县数据：</text>
          <text class="info-value">
            {{ store.districts.size }} 个城市的区县缓存
          </text>
        </view>
      </view>

      <view class="cache-stats">
        <text class="stats-title">缓存统计：</text>
        <text class="stats-text">{{ cacheStats }}</text>
      </view>
    </view>

    <view class="section">
      <text class="section-title">搜索功能测试</text>
      <input
        v-model="searchKeyword"
        class="search-input"
        placeholder="输入拼音或中文搜索"
        @input="handleSearch"
      />

      <view v-if="searchResults.length > 0" class="search-results">
        <view
          v-for="result in searchResults"
          :key="result.id"
          class="search-result-item"
          @click="selectSearchResult(result)"
        >
          <text class="result-name">{{ result.name }}</text>
          <text class="result-level">{{ getLevelText(result.level) }}</text>
          <text class="result-pinyin">{{ result.pinyin }}</text>
        </view>
      </view>

      <view v-else-if="searchKeyword" class="no-results">
        <text>未找到匹配的区域</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref, watch, onMounted } from "vue";
import { useRegionStore } from "@/stores/region";
import type { SelectedRegion, Region, HotCity } from "@/types/region";
import RegionPicker from "@/components/region-picker/RegionPicker.vue";

const store = useRegionStore()

// 安全访问 store.error
const storeError = computed(() => {
  try {
    // 因为 store.error 是一个 ref，需要访问 .value
    return store.error?.value || '无错误'
  } catch (error) {
    return '访问错误信息失败'
  }
})

// 缓存统计
const cacheStats = computed(() => {
  try {
    // 如果有缓存管理器的 getStats 方法，可以调用
    if (typeof store.getCacheStats === 'function') {
      return JSON.stringify(store.getCacheStats(), null, 2)
    }
    return '缓存统计不可用'
  } catch (error) {
    return '获取缓存统计失败'
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

// 配置选项
const showDistrict = ref(true)
const disabled = ref(false)
const showSelectedText = ref(true)
const showClear = ref(true)
const customProvincePlaceholder = ref('选择省份')
const customCityPlaceholder = ref('选择城市')
const customDistrictPlaceholder = ref('选择区县')

// 搜索功能
const searchKeyword = ref('')
const searchResults = ref<Region[]>([])

// 测试2：不显示区县
const selectedRegion2 = ref<SelectedRegion>({})

// 事件处理
const handleRegionChange = (region: SelectedRegion) => {
  console.log('区域变化事件：', region)
}

const handlePickerError = (error: string | null) => {
  console.log('Picker错误事件：', error)
}

// 控制方法
const resetSelection = () => {
  selectedRegion.value = {}
  console.log('已重置选择')
}

const toggleShowDistrict = (e: any) => {
  showDistrict.value = e.detail.value
}

const toggleDisabled = (e: any) => {
  disabled.value = e.detail.value
}

const toggleShowSelectedText = (e: any) => {
  showSelectedText.value = e.detail.value
}

const toggleShowClear = (e: any) => {
  showClear.value = e.detail.value
}

const selectBeijing = async () => {
  console.log('开始选择北京市')
  await simulateSelection(110000) // 北京市的ID
}

const selectShanghai = async () => {
  console.log('开始选择上海市')
  await simulateSelection(310000) // 上海市的ID
}

const simulateSelection = async (provinceId: number) => {
  try {
    // 先加载省份
    if (store.provinces.length === 0) {
      await store.loadProvinces()
    }

    // 查找省份
    const province = store.provinces.find(p => p.id === provinceId)
    if (!province) {
      console.error(`未找到省份 ID: ${provinceId}`)
      return
    }

    selectedRegion.value = { province }

    // 加载城市
    await store.loadCities(provinceId)
    const cities = store.getCitiesByProvince(provinceId)

    if (cities && cities.length > 0) {
      selectedRegion.value.city = cities[0]

      // 加载区县
      if (showDistrict.value) {
        await store.loadDistricts(cities[0].id)
        const districts = store.getDistrictsByCity(cities[0].id)

        if (districts && districts.length > 0) {
          selectedRegion.value.district = districts[0]
        }
      }
    }

    console.log('选择完成:', selectedRegion.value)
  } catch (error) {
    console.error('选择失败:', error)
  }
}

const selectHotCity = async (city: HotCity) => {
  console.log('选择热门城市:', city)

  try {
    // 先加载省份
    if (store.provinces.length === 0) {
      await store.loadProvinces()
    }

    // 找到热门城市对应的省份
    const province = store.provinces.find(p => p.id === city.parentId)
    if (province) {
      selectedRegion.value = {
        province,
        city: {
          id: city.id,
          parentId: city.parentId || 0,
          name: city.name,
          shortName: city.shortName,
          pinyin: city.pinyin,
          level: 2,
          path: '',
          provinceId: city.parentId || 0
        }
      }
      console.log('热门城市选择完成:', selectedRegion.value)
    }
  } catch (error) {
    console.error('选择热门城市失败:', error)
  }
}

const testError = async () => {
  console.log('测试错误处理...')
  try {
    // 尝试加载一个不存在的省份ID来触发错误
    await store.loadCities(999999)
  } catch (error) {
    console.log('成功触发错误:', error)
  }
}

const clearStoreCache = () => {
  store.clearCache()
  console.log('已清除缓存')
}

const logStoreState = () => {
  console.log('===== Store 状态 =====')

  const state = {
    provinces: store.provinces.slice(0, 5).map(p => ({ id: p.id, name: p.name })),
    provincesCount: store.provinces.length,
    hotCities: store.hotCities.slice(0, 3).map(c => ({ id: c.id, name: c.name })),
    hotCitiesCount: store.hotCities.length,
    citiesCacheSize: store.cities.size,
    districtsCacheSize: store.districts.size,
    isLoading: store.isLoading,
    error: store.error?.value
  }

  console.log('Store 状态:', state)

  // 打印缓存的城市
  console.log('缓存的城市数据:')
  for (const [provinceId, cities] of store.cities.entries()) {
    console.log(`  省份 ${provinceId}: ${cities.length} 个城市`)
  }

  console.log('===== 当前选择状态 =====')
  console.log('selectedRegion:', selectedRegion.value)
  console.log('selectedRegionText:', selectedRegionText.value)
}

const handleSearch = () => {
  if (!searchKeyword.value.trim()) {
    searchResults.value = []
    return
  }

  try {
    searchResults.value = store.searchByPinyin(searchKeyword.value)
  } catch (error) {
    console.error('搜索失败:', error)
    searchResults.value = []
  }
}

const selectSearchResult = (region: Region) => {
  console.log('选择搜索结果:', region)

  // 根据区域级别设置选择
  if (region.level === 1) {
    selectedRegion.value = { province: region }
  } else if (region.level === 2) {
    // 需要找到省份
    const province = store.provinces.find(p => p.id === region.parentId)
    if (province) {
      selectedRegion.value = {
        province,
        city: region
      }
    }
  }
}

const getLevelText = (level: number) => {
  switch (level) {
    case 1: return '省份'
    case 2: return '城市'
    case 3: return '区县'
    default: return '未知'
  }
}

// 组件挂载时预加载数据
onMounted(async () => {
  console.log('Region 测试页面挂载')

  // 预加载热门城市
  try {
    await store.loadHotCities()
    console.log('热门城市加载完成:', store.hotCities.length)
  } catch (error) {
    console.error('加载热门城市失败:', error)
  }

  // 监听 store 错误
  watch(() => store.error?.value, (newError) => {
    if (newError) {
      console.log('Store 错误更新:', newError)
    }
  })
})

// 监听选择的区域变化
watch(selectedRegion, (newValue) => {
  console.log('选择的区域变化:', newValue)
}, { deep: true })
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
  padding: 16rpx 24rpx;
  background-color: #409eff;
  color: white;
  border-radius: 8rpx;
  font-size: 24rpx;
  border: none;
  min-width: 160rpx;
  text-align: center;

  &:active {
    opacity: 0.8;
  }

  &:nth-child(2) {
    background-color: #67c23a;
  }

  &:nth-child(3) {
    background-color: #e6a23c;
  }

  &:nth-child(4) {
    background-color: #f56c6c;
  }

  &:nth-child(5) {
    background-color: #909399;
  }

  &:nth-child(6) {
    background-color: #409eff;
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
  white-space: pre-wrap;
}

.config-controls {
  margin-top: 24rpx;
  padding: 24rpx;
  background-color: #f9f9f9;
  border-radius: 8rpx;
}

.config-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.config-label {
  font-size: 28rpx;
  color: #666666;
}

.hot-cities {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 16rpx;
}

.hot-city-item {
  padding: 16rpx 24rpx;
  background-color: #ecf5ff;
  border-radius: 8rpx;
  border: 1rpx solid #d9ecff;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 160rpx;

  &:active {
    background-color: #d9ecff;
  }
}

.city-name {
  font-size: 28rpx;
  color: #409eff;
  font-weight: 500;
  margin-bottom: 4rpx;
}

.city-pinyin {
  font-size: 24rpx;
  color: #909399;
}

.store-info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16rpx;
  margin-top: 16rpx;
}

.info-item {
  padding: 16rpx;
  background-color: #f9f9f9;
  border-radius: 8rpx;

  &.full-width {
    grid-column: span 2;
  }
}

.info-label {
  font-size: 24rpx;
  color: #666666;
  display: block;
  margin-bottom: 8rpx;
}

.info-value {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
  display: block;

  &.loading-text {
    color: #409eff;
  }

  &.error-text {
    color: #f56c6c;
    font-size: 24rpx;
  }
}

.cache-stats {
  margin-top: 24rpx;
  padding: 24rpx;
  background-color: #f4f4f5;
  border-radius: 8rpx;
  border: 1rpx solid #e9e9eb;
}

.stats-title {
  display: block;
  font-size: 28rpx;
  font-weight: 500;
  color: #909399;
  margin-bottom: 12rpx;
}

.stats-text {
  font-size: 24rpx;
  color: #666666;
  font-family: monospace;
  word-break: break-all;
  white-space: pre-wrap;
}

.search-input {
  width: 100%;
  height: 80rpx;
  padding: 0 24rpx;
  background-color: #ffffff;
  border: 2rpx solid #e5e5e5;
  border-radius: 8rpx;
  font-size: 28rpx;
  color: #333333;
  margin-top: 16rpx;

  &:focus {
    border-color: #409eff;
  }
}

.search-results {
  margin-top: 16rpx;
  max-height: 400rpx;
  overflow-y: auto;
}

.search-result-item {
  padding: 24rpx;
  background-color: #ffffff;
  border-bottom: 1rpx solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;

  &:active {
    background-color: #f5f7fa;
  }

  &:last-child {
    border-bottom: none;
  }
}

.result-name {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
  flex: 1;
}

.result-level {
  font-size: 24rpx;
  color: #409eff;
  margin: 0 16rpx;
  padding: 4rpx 12rpx;
  background-color: #ecf5ff;
  border-radius: 4rpx;
}

.result-pinyin {
  font-size: 24rpx;
  color: #909399;
}

.no-results {
  margin-top: 16rpx;
  padding: 24rpx;
  text-align: center;
  color: #999999;
  font-size: 28rpx;
}
</style>