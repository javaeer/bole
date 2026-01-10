<template>
  <BaseComponent
    :component-data="componentData"
    :global-style="globalStyle"
    :override-styles="customStyles"
    :responsive-center="true"
    :show-header="showTitle"
  >
    <template #default="{ styles }">
      <view class="education-experience-container">
        <!-- 空状态 -->
        <view
          v-if="sortedEducations.length === 0"
          class="empty-state"
        >
          <view class="empty-icon">🎓</view>
          <text class="empty-title">
            暂无教育经历
          </text>
          <text class="empty-description">
            请添加您的教育背景信息
          </text>
        </view>

        <!-- 教育经历列表 -->
        <view
          v-else
          class="education-list"
        >
          <view
            v-for="(education, index) in sortedEducations"
            :key="education.id || index"
            class="education-item"
            :class="{ 'current-item': education.isCurrent }"
          >
            <!-- 左侧竖线装饰 -->
            <view class="item-decoration"></view>

            <!-- 内容区域 -->
            <view class="item-content">
              <!-- 学校信息行 -->
              <view class="school-info">
                <text class="school-name">
                  {{ education.university || '未填写学校' }}
                </text>
                <view class="degree-major">
                  <text class="major">
                    {{ education.major || '专业未填写' }}
                  </text>
                  <text
                    v-if="showDegree && education.degree"
                    class="degree"
                  >
                    | {{ education.degree }}
                  </text>
                </view>
              </view>

              <!-- 时间信息 -->
              <view
                v-if="showEducationPeriod"
                class="time-info"
              >
                <text class="time-icon">📅</text>
                <text class="time-text">
                  {{ formatDate(education.startDate) }} - 
                  {{ education.isCurrent ? '至今' : formatDate(education.endDate) }}
                </text>
              </view>

              <!-- 描述 -->
              <view
                v-if="education.description"
                class="description"
              >
                {{ education.description }}
              </view>

              <!-- 荣誉奖项 -->
              <view
                v-if="showHonors && education.achievements && education.achievements.length > 0"
                class="achievements-section"
              >
                <text class="section-title">荣誉奖项：</text>
                <view class="achievements-list">
                  <view
                    v-for="(achievement, achievementIndex) in education.achievements"
                    :key="achievementIndex"
                    class="achievement-item"
                  >
                    <text class="achievement-icon">🏆</text>
                    <text class="achievement-text">{{ achievement }}</text>
                  </view>
                </view>
              </view>

              <!-- 分隔线（非最后一个项目） -->
              <view
                v-if="index < sortedEducations.length - 1"
                class="item-divider"
              ></view>
            </view>
          </view>
        </view>
      </view>
    </template>
  </BaseComponent>
</template>

<script setup lang="ts">
import { computed } from "vue";
import BaseComponent from "./BaseComponent.vue";

interface Props {
  componentData: any;
  globalStyle?: any;
  customStyles?: Record<string, any>;
}

const props = defineProps<Props>();

// ===================== 计算属性 =====================

// 组件名称
const componentName = computed(() => props.componentData?.name || '教育背景');

// 配置选项（从defaultConfig.props中获取）
const configProps = computed(() => props.componentData?.defaultConfig?.props || {});

// 显示控制
const showTitle = computed(() => configProps.value.showTitle ?? true);
const showDegree = computed(() => configProps.value.showDegree ?? true);
const showHonors = computed(() => configProps.value.showHonors ?? true);
const showGPA = computed(() => configProps.value.showGPA ?? false);
const showCourses = computed(() => configProps.value.showCourses ?? false);
const showEducationPeriod = computed(() => configProps.value.showEducationPeriod ?? true);
const showUniversityLogo = computed(() => configProps.value.showUniversityLogo ?? false);

// 教育经历数据
const educations = computed(() => {
  return props.componentData?.props?.experiences || [];
});

// 排序方向
const orderDirection = computed(() => configProps.value.orderDirection || 'desc');
const orderBy = computed(() => configProps.value.orderBy || 'graduationDate');
const maxItems = computed(() => configProps.value.maxItems || 3);

// 按配置排序教育经历
const sortedEducations = computed(() => {
  const items = [...educations.value];

  // 限制最大显示数量
  const limitedItems = items.slice(0, maxItems.value);

  // 按指定字段排序
  return limitedItems.sort((a, b) => {
    if (orderBy.value === 'graduationDate') {
      const dateA = new Date(a.endDate || 0).getTime();
      const dateB = new Date(b.endDate || 0).getTime();
      return orderDirection.value === 'desc' ? dateB - dateA : dateA - dateB;
    } else if (orderBy.value === 'startDate') {
      const dateA = new Date(a.startDate || 0).getTime();
      const dateB = new Date(b.startDate || 0).getTime();
      return orderDirection.value === 'desc' ? dateB - dateA : dateA - dateB;
    } else {
      // 默认按开始日期排序
      const dateA = new Date(a.startDate || 0).getTime();
      const dateB = new Date(b.startDate || 0).getTime();
      return orderDirection.value === 'desc' ? dateB - dateA : dateA - dateB;
    }
  });
});

// ===================== 工具函数 =====================

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return '未填写';
  try {
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    return `${year}.${month}`;
  } catch (e) {
    return dateStr;
  }
};

// 格式化学位显示
const formatDegree = (degree: string, format: string = 'full') => {
  if (!degree) return '';
  
  const degreeMap: Record<string, string> = {
    '本科': '学士',
    '硕士': '硕士',
    '博士': '博士',
    '专科': '专科',
    '高中': '高中'
  };
  
  const shortMap: Record<string, string> = {
    '本科': '本',
    '硕士': '硕',
    '博士': '博',
    '专科': '专',
    '高中': '高'
  };
  
  if (format === 'short') {
    return shortMap[degree] || degree;
  }
  
  return degreeMap[degree] || degree;
};
</script>

<style scoped lang="scss">
.education-experience-container {
  width: 100%;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 48px;
  opacity: 0.5;
  margin-bottom: 12px;
  display: block;
}

.empty-title {
  display: block;
  font-size: 16px;
  color: var(--base-text-color, #555);
  margin-bottom: 8px;
  font-weight: 500;
}

.empty-description {
  display: block;
  font-size: 14px;
  color: var(--base-secondary-color, #999);
}

/* 教育经历列表 */
.education-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.education-item {
  position: relative;
  display: flex;
  gap: 16px;
}

/* 装饰竖线 */
.item-decoration {
  flex-shrink: 0;
  width: 4px;
  border-radius: 2px;
  background-color: var(--base-primary-color, #52c41a);
}

.current-item .item-decoration {
  background-color: var(--base-accent-color, #fa8c16);
}

/* 内容区域 */
.item-content {
  flex: 1;
  min-width: 0; /* 防止内容溢出 */
}

/* 学校信息 */
.school-info {
  margin-bottom: 8px;
}

.school-name {
  display: block;
  font-size: 16px;
  font-weight: bold;
  color: var(--base-university-color, var(--base-primary-color, #52c41a));
  margin-bottom: 4px;
  line-height: 1.3;
}

.degree-major {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.major {
  font-size: 14px;
  color: var(--base-text-color, #666);
  font-weight: 500;
}

.degree {
  font-size: 13px;
  color: var(--base-accent-color, #fa8c16);
}

/* 时间信息 */
.time-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.time-icon {
  font-size: 14px;
  flex-shrink: 0;
}

.time-text {
  font-size: 13px;
  color: var(--base-period-color, var(--base-secondary-color, #999));
}

/* 描述 */
.description {
  font-size: 14px;
  color: var(--base-text-color, #555);
  line-height: 1.6;
  margin-bottom: 12px;
  word-wrap: break-word;
  word-break: break-word;
}

/* 荣誉奖项 */
.achievements-section {
  margin-top: 12px;
}

.section-title {
  display: block;
  font-size: 14px;
  color: var(--base-title-color, #333);
  font-weight: 600;
  margin-bottom: 8px;
}

.achievements-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.achievement-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.achievement-icon {
  font-size: 14px;
  flex-shrink: 0;
  margin-top: 2px;
}

.achievement-text {
  font-size: 13px;
  color: var(--base-text-color, #555);
  flex: 1;
  word-wrap: break-word;
  word-break: break-word;
}

/* 分隔线 */
.item-divider {
  height: 1px;
  background-color: var(--base-secondary-color, #f0f0f0);
  margin-top: 24px;
}

/* ===================== 响应式设计 ===================== */

/* 移动端优化 */
@media (max-width: 768px) {
  .education-item {
    flex-direction: column;
    gap: 12px;
  }

  .item-decoration {
    width: 100%;
    height: 3px;
  }

  .school-name {
    font-size: 15px;
  }

  .major {
    font-size: 13px;
  }

  .degree {
    font-size: 12px;
  }

  .time-text {
    font-size: 12px;
  }

  .description {
    font-size: 13px;
  }

  .section-title {
    font-size: 13px;
  }

  .achievement-text {
    font-size: 12px;
  }

  .item-divider {
    margin-top: 20px;
  }

  .empty-icon {
    font-size: 36px;
  }
  
  .empty-title {
    font-size: 14px;
  }
  
  .empty-description {
    font-size: 12px;
  }
}

/* 平板设备 */
@media (min-width: 769px) and (max-width: 1024px) {
  .education-list {
    gap: 20px;
  }
  
  .school-name {
    font-size: 15px;
  }
}

/* 打印样式 */
@media print {
  .education-experience-container {
    break-inside: avoid;
  }

  .education-item {
    break-inside: avoid;
    page-break-inside: avoid;
  }

  .item-decoration {
    background-color: #333 !important;
  }

  .school-name {
    color: #000 !important;
  }

  .description,
  .achievement-text {
    color: #333 !important;
  }

  .item-divider {
    background-color: #ddd !important;
  }

  .empty-icon {
    display: none;
  }
}
</style>