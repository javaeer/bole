// utils/cache-manager.ts
interface CacheItem<T = any> {
  data: T
  timestamp: number
  expiresAt: number
  accessCount: number
  lastAccessed: number
}

export class CacheManager {
  private cache = new Map<string, CacheItem>()
  private readonly DEFAULT_DURATION: number
  private readonly MAX_CACHE_SIZE: number
  private accessHistory = new Map<string, number>() // 访问频率统计

  constructor(options?: {
    defaultDuration?: number
    maxSize?: number
  }) {
    this.DEFAULT_DURATION = options?.defaultDuration ?? 1000 * 60 * 60 // 1小时
    this.MAX_CACHE_SIZE = options?.maxSize ?? 100 // 最大缓存条目数
  }

  set<T = any>(key: string, data: T, duration?: number): void {
    // 检查缓存大小，清理过期和最少使用的
    if (this.cache.size >= this.MAX_CACHE_SIZE) {
      this.cleanup()
    }

    const expiresAt = Date.now() + (duration || this.DEFAULT_DURATION)
    this.cache.set(key, {
      data,
      timestamp: Date.now(),
      expiresAt,
      accessCount: 0,
      lastAccessed: Date.now()
    })
  }

  get<T = any>(key: string): T | null {
    const item = this.cache.get(key)
    if (!item) {
      return null
    }

    // 检查是否过期
    if (Date.now() > item.expiresAt) {
      this.cache.delete(key)
      return null
    }

    // 更新访问统计
    item.accessCount++
    item.lastAccessed = Date.now()
    this.accessHistory.set(key, (this.accessHistory.get(key) || 0) + 1)

    return item.data as T
  }

  // 根据路径模式获取缓存（如获取某个省份的所有城市）
  getByPattern(pattern: RegExp): Array<{ key: string; data: any }> {
    const results: Array<{ key: string; data: any }> = []

    for (const [key, item] of this.cache.entries()) {
      if (pattern.test(key) && Date.now() <= item.expiresAt) {
        results.push({ key, data: item.data })
      }
    }

    return results
  }

  delete(key: string): boolean {
    this.accessHistory.delete(key)
    return this.cache.delete(key)
  }

  clear(): void {
    this.cache.clear()
    this.accessHistory.clear()
  }

  // 清理过期和最少使用的缓存
  private cleanup(): void {
    const now = Date.now()

    // 清理过期的
    for (const [key, item] of this.cache.entries()) {
      if (now > item.expiresAt) {
        this.cache.delete(key)
        this.accessHistory.delete(key)
      }
    }

    // 如果仍然超过限制，清理最少使用的
    if (this.cache.size >= this.MAX_CACHE_SIZE) {
      const sortedEntries = Array.from(this.cache.entries())
        .sort(([, a], [, b]) => {
          // 按访问次数和最近访问时间排序
          const scoreA = a.accessCount + (now - a.lastAccessed) / (1000 * 60 * 60)
          const scoreB = b.accessCount + (now - b.lastAccessed) / (1000 * 60 * 60)
          return scoreA - scoreB
        })

      const toDelete = sortedEntries.slice(0, Math.floor(this.cache.size * 0.2)) // 删除20%
      toDelete.forEach(([key]) => {
        this.cache.delete(key)
        this.accessHistory.delete(key)
      })
    }
  }

  // 获取缓存统计信息
  getStats() {
    const now = Date.now()
    let expiredCount = 0
    let totalAccessCount = 0
    let avgAccessCount = 0

    for (const item of this.cache.values()) {
      if (now > item.expiresAt) {
        expiredCount++
      }
      totalAccessCount += item.accessCount
    }

    if (this.cache.size > 0) {
      avgAccessCount = totalAccessCount / this.cache.size
    }

    return {
      size: this.cache.size,
      expiredCount,
      totalAccessCount,
      avgAccessCount,
      maxSize: this.MAX_CACHE_SIZE
    }
  }
}

// 创建缓存管理器实例
export const cacheManager = new CacheManager({
  defaultDuration: 1000 * 60 * 60 * 24, // 24小时（城市数据变更不频繁）
  maxSize: 500 // 最多缓存500条数据
})