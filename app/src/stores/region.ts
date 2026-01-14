// store/region.ts
import { City, District, HotCity, Province, Region, RegionResult } from "@/types/region";
import { cacheManager } from "@/utils/cache-manager";
import RegionAPI from "@/api/region";
import { defineStore } from 'pinia'
import { ref, reactive, computed } from 'vue'

// 常量定义
const CHINA_PARENT_ID = 86 // 中国的parentId

export const useRegionStore = defineStore("region", () => {
  // 状态
  const provinces = ref<Province[]>([])
  const cities = reactive(new Map<number, City[]>())
  const districts = reactive(new Map<number, District[]>())
  const hotCities = ref<HotCity[]>([])
  const loading = reactive({
    provinces: false,
    cities: new Map<number, boolean>(),
    districts: new Map<number, boolean>(),
    hotCities: false,
  })
  const error = ref<string | null>(null)

  // Getters
  const getProvinces = computed(() => provinces.value)
  const getHotCities = computed(() => hotCities.value)
  const isLoading = computed(() =>
    loading.provinces ||
    loading.hotCities ||
    Array.from(loading.cities.values()).some(v => v) ||
    Array.from(loading.districts.values()).some(v => v)
  )

  // Methods
  const getCitiesByProvince = (provinceId: number): City[] => {
    return cities.get(provinceId) || []
  }

  const getDistrictsByCity = (cityId: number): District[] => {
    return districts.get(cityId) || []
  }

  const searchByPinyin = (keyword: string): Region[] => {
    const lowerKeyword = keyword.toLowerCase().trim()
    if (!lowerKeyword) return []

    const results: Region[] = []

    // 搜索省份
    provinces.value.forEach(province => {
      if (province.pinyin.includes(lowerKeyword) ||
        province.name.includes(lowerKeyword) ||
        province.shortName.includes(lowerKeyword)) {
        results.push(province)
      }
    })

    // 搜索城市
    for (const cityList of cities.values()) {
      cityList.forEach(city => {
        if (city.pinyin.includes(lowerKeyword) ||
          city.name.includes(lowerKeyword) ||
          city.shortName.includes(lowerKeyword)) {
          results.push(city)
        }
      })
    }

    // 搜索区县
    for (const districtList of districts.values()) {
      districtList.forEach(district => {
        if (district.pinyin.includes(lowerKeyword) ||
          district.name.includes(lowerKeyword) ||
          district.shortName.includes(lowerKeyword)) {
          results.push(district)
        }
      })
    }

    // 去重并限制数量
    const uniqueResults = Array.from(new Map(results.map(item => [item.id, item])).values())
    return uniqueResults.slice(0, 20) // 限制为20条结果
  }

  // Actions
  const loadProvinces = async (forceRefresh = false): Promise<Province[]> => {
    if (provinces.value.length > 0 && !forceRefresh) {
      return provinces.value
    }

    const cacheKey = "provinces"
    if (!forceRefresh) {
      const cached = cacheManager.get<Province[]>(cacheKey)
      if (cached) {
        provinces.value = cached
        return cached
      }
    }

    try {
      loading.provinces = true
      error.value = null

      const data = await RegionAPI.getProvinces()
      const transformedData = data
        .filter(item => item.parentId === CHINA_PARENT_ID)
        .map(transformRegion)
        .filter((region): region is Province => region.level === 1)

      provinces.value = transformedData

      cacheManager.set(cacheKey, transformedData, 1000 * 60 * 60 * 24 * 7) // 缓存7天
      return transformedData
    } catch (err) {
      error.value = err instanceof Error ? err.message : "加载省份数据失败"
      throw err
    } finally {
      loading.provinces = false
    }
  }

  const loadCities = async (provinceId: number, forceRefresh = false): Promise<City[]> => {
    const cacheKey = `cities_${provinceId}`

    if (!forceRefresh) {
      const cached = cacheManager.get<City[]>(cacheKey)
      if (cached) {
        cities.set(provinceId, cached)
        return cached
      }
    }

    try {
      loading.cities.set(provinceId, true)
      error.value = null

      const data = await RegionAPI.getCities(provinceId)
      const transformedData = data
        .map(transformRegion)
        .filter((region): region is City => region.level === 2)

      cities.set(provinceId, transformedData)

      cacheManager.set(cacheKey, transformedData, 1000 * 60 * 60 * 24 * 7) // 缓存7天
      return transformedData
    } catch (err) {
      error.value = err instanceof Error ? err.message : `加载省份 ${provinceId} 的城市数据失败`

      if (!cities.has(provinceId)) {
        cities.set(provinceId, [])
      }

      throw err
    } finally {
      loading.cities.set(provinceId, false)
    }
  }

  const loadDistricts = async (cityId: number, forceRefresh = false): Promise<District[]> => {
    const cacheKey = `districts_${cityId}`

    if (!forceRefresh) {
      const cached = cacheManager.get<District[]>(cacheKey)
      if (cached) {
        districts.set(cityId, cached)
        return cached
      }
    }

    try {
      loading.districts.set(cityId, true)
      error.value = null

      const data = await RegionAPI.getDistricts(cityId)
      const transformedData = data
        .map(transformRegion)
        .filter((region): region is District => region.level === 3)

      districts.set(cityId, transformedData)

      cacheManager.set(cacheKey, transformedData, 1000 * 60 * 60 * 24 * 7) // 缓存7天
      return transformedData
    } catch (err) {
      error.value = err instanceof Error ? err.message : `加载城市 ${cityId} 的区县数据失败`

      if (!districts.has(cityId)) {
        districts.set(cityId, [])
      }

      throw err
    } finally {
      loading.districts.set(cityId, false)
    }
  }

  const loadHotCities = async (forceRefresh = false): Promise<HotCity[]> => {
    const cacheKey = "hot_cities"

    if (!forceRefresh) {
      const cached = cacheManager.get<HotCity[]>(cacheKey)
      if (cached) {
        hotCities.value = cached
        return cached
      }
    }

    try {
      loading.hotCities = true
      error.value = null

      const data = await RegionAPI.getHotCity()
      const transformedData = data.map(item => ({
        id: item.id,
        name: item.name,
        shortName: item.shortName,
        pinyin: item.pinyin || '',
        level: item.level,
        parentId: item.parentId,
        sortOrder: 0
      }))

      hotCities.value = transformedData

      cacheManager.set(cacheKey, transformedData, 1000 * 60 * 60 * 24) // 缓存24小时
      return transformedData
    } catch (err) {
      error.value = err instanceof Error ? err.message : "加载热门城市失败"
      throw err
    } finally {
      loading.hotCities = false
    }
  }

  const preloadData = async (provinceIds?: number[]): Promise<PromiseSettledResult<any>[]> => {
    const promises: Promise<any>[] = []

    promises.push(loadProvinces())

    if (provinceIds?.length) {
      provinceIds.forEach(provinceId => {
        promises.push(loadCities(provinceId))
      })
    } else {
      const provincesData = await loadProvinces()
      const topProvinces = provincesData.slice(0, 3)
      topProvinces.forEach(province => {
        promises.push(loadCities(province.id))
      })
    }

    return Promise.allSettled(promises)
  }

  const findRegionById = async (id: number, level?: number): Promise<Region | undefined> => {
    // 先从缓存中查找
    if (!level || level === 1) {
      const province = provinces.value.find(p => p.id === id)
      if (province) return province
    }

    if (!level || level === 2) {
      for (const cityList of cities.values()) {
        const city = cityList.find(c => c.id === id)
        if (city) return city
      }
    }

    if (!level || level === 3) {
      for (const districtList of districts.values()) {
        const district = districtList.find(d => d.id === id)
        if (district) return district
      }
    }

    // 如果没找到，尝试从API获取
    try {
      const region = await RegionAPI.getById(id)
      if (region) {
        return transformRegion(region)
      }
    } catch (error) {
      console.error(`查找区域 ${id} 失败:`, error)
    }

    return undefined
  }

  const clearCache = () => {
    provinces.value = []
    cities.clear()
    districts.clear()
    hotCities.value = []
    cacheManager.clear()
  }

  return {
    // State
    provinces,
    cities,
    districts,
    hotCities,
    loading,
    error,
    isLoading,

    // Getters
    getProvinces,
    getHotCities,

    // Methods
    getCitiesByProvince,
    getDistrictsByCity,
    searchByPinyin,

    // Actions
    loadProvinces,
    loadCities,
    loadDistricts,
    loadHotCities,
    preloadData,
    findRegionById,
    clearCache,
  }
})

// 辅助函数
function transformRegion(data: RegionResult): Region {
  const baseRegion: Region = {
    id: data.id,
    parentId: data.parentId,
    name: data.name,
    shortName: data.shortName,
    pinyin: data.pinyin || '',
    level: data.level,
    path: data.path,
    longitude: data.longitude,
    latitude: data.latitude,
  }

  switch (data.level) {
    case 1:
      return { ...baseRegion, level: 1 } as Province
    case 2:
      return { ...baseRegion, level: 2, provinceId: data.parentId } as City
    case 3:
      const provinceId = extractProvinceIdFromPath(data.path)
      return { ...baseRegion, level: 3, cityId: data.parentId, provinceId } as District
    default:
      throw new Error(`未知的区域级别: ${data.level}`)
  }
}

function extractProvinceIdFromPath(path: string): number {
  const parts = path.split('/').filter(Boolean)
  // 路径格式: /86/110000/110100 或 86/110000/110100
  if (parts.length >= 2) {
    return parseInt(parts[1], 10) || 0
  }
  return 0
}