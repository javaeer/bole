<!-- components/resumes/EducationExperience.vue -->
<template>
  <BaseComponent
    :title="componentName"
    :component-data="componentData"
    :global-style="globalStyle"
    :custom-styles="customStyles"
  >
    <template #default="{ styles }">
      <view
        class="education-experience-container"
        :style="getContainerStyle(styles)"
      >
        <!-- 时间线布局 -->
        <view
          v-if="layoutType === 'timeline'"
          class="timeline-layout"
          :style="getTimelineLayoutStyle(styles)"
        >
          <view
            v-for="(education, index) in sortedEducations"
            :key="education.id || index"
            class="timeline-item"
            :style="getTimelineItemStyle(styles, index)"
          >
            <!-- 时间线节点 -->
            <view class="timeline-node" :style="getTimelineNodeStyle(styles, education)">
              <view class="node-core" :style="getNodeCoreStyle(styles, education)"></view>
              <view
                v-if="index < sortedEducations.length - 1"
                class="timeline-connector"
                :style="getTimelineConnectorStyle(styles)"
              ></view>
            </view>

            <!-- 内容区域 -->
            <view class="timeline-content" :style="getContentStyle(styles)">
              <!-- 学校信息 -->
              <view class="education-header">
                <view class="school-row">
                  <text
                    class="school-name"
                    :style="getSchoolNameStyle(styles)"
                  >
                    {{ education.university || '未填写学校' }}
                  </text>

                  <!-- 学校Logo（如果配置显示） -->
                  <view
                    v-if="showSchoolLogo && education.logo"
                    class="school-logo"
                    :style="getSchoolLogoStyle(styles)"
                  >
                    <image
                      :src="education.logo"
                      mode="aspectFit"
                      class="logo-image"
                    />
                  </view>
                </view>

                <view class="degree-major">
                  <text
                    class="major"
                    :style="getMajorStyle(styles)"
                  >
                    {{ education.major || '专业未填写' }}
                  </text>

                  <text
                    v-if="showDegree && education.degree"
                    class="degree"
                    :style="getDegreeStyle(styles)"
                  >
                    {{ education.degree }}
                  </text>
                </view>
              </view>

              <!-- 学习时间 -->
              <view
                v-if="showWorkPeriod"
                class="time-period"
                :style="getTimePeriodStyle(styles)"
              >
                <view class="time-info">
                  <text class="time-icon">📅</text>
                  <text class="time-text">
                    {{ formatDate(education.startDate) }} -
                    {{ education.isCurrent ? '至今' : formatDate(education.endDate) }}
                  </text>
                </view>

                <!-- 成绩信息 -->
                <view
                  v-if="showGPA && (education.gpa || education.grade)"
                  class="achievement-info"
                >
                  <text class="achievement-icon">🎯</text>
                  <text class="achievement-text">
                    {{ education.gpa ? `GPA: ${education.gpa}` : '' }}
                    {{ education.grade ? `成绩: ${education.grade}` : '' }}
                  </text>
                </view>
              </view>

              <!-- 学院/专业描述 -->
              <view
                v-if="education.description"
                class="description"
                :style="getDescriptionStyle(styles)"
              >
                {{ education.description }}
              </view>

              <!-- 课程亮点 -->
              <view
                v-if="education.courses && education.courses.length > 0"
                class="courses-section"
              >
                <text class="section-title" :style="getSectionTitleStyle(styles)">
                  相关课程：
                </text>
                <view class="courses-tags">
                  <view
                    v-for="(course, courseIndex) in getLimitedCourses(education.courses)"
                    :key="courseIndex"
                    class="course-tag"
                    :style="getCourseTagStyle(styles)"
                  >
                    {{ course }}
                  </view>
                  <view
                    v-if="education.courses.length > maxCourses"
                    class="more-courses"
                    :style="getMoreCoursesStyle(styles)"
                  >
                    +{{ education.courses.length - maxCourses }}更多
                  </view>
                </view>
              </view>

              <!-- 荣誉奖项 -->
              <view
                v-if="education.honors && education.honors.length > 0"
                class="honors-section"
              >
                <text class="section-title" :style="getSectionTitleStyle(styles)">
                  荣誉奖项：
                </text>
                <view class="honors-list">
                  <view
                    v-for="(honor, honorIndex) in education.honors"
                    :key="honorIndex"
                    class="honor-item"
                    :style="getHonorItemStyle(styles)"
                  >
                    <text class="honor-icon">🏆</text>
                    <text class="honor-text">{{ honor }}</text>
                  </view>
                </view>
              </view>

              <!-- 在校活动 -->
              <view
                v-if="education.activities && education.activities.length > 0"
                class="activities-section"
              >
                <text class="section-title" :style="getSectionTitleStyle(styles)">
                  在校活动：
                </text>
                <view class="activities-list">
                  <view
                    v-for="(activity, activityIndex) in education.activities"
                    :key="activityIndex"
                    class="activity-item"
                    :style="getActivityItemStyle(styles)"
                  >
                    <text class="activity-icon">🎨</text>
                    <text class="activity-text">{{ activity }}</text>
                  </view>
                </view>
              </view>

              <!-- 学术成果 -->
              <view
                v-if="education.achievements && education.achievements.length > 0"
                class="achievements-section"
              >
                <text class="section-title" :style="getSectionTitleStyle(styles)">
                  学术成果：
                </text>
                <view class="achievements-list">
                  <view
                    v-for="(achievement, achievementIndex) in education.achievements"
                    :key="achievementIndex"
                    class="achievement-item"
                    :style="getAchievementItemStyle(styles)"
                  >
                    <text class="achievement-icon">📚</text>
                    <text class="achievement-text">{{ achievement }}</text>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- 空状态 -->
        <view
          v-if="sortedEducations.length === 0"
          class="empty-state"
          :style="getEmptyStateStyle(styles)"
        >
          <view class="empty-icon">🎓</view>
          <text class="empty-title" :style="getEmptyTitleStyle(styles)">
            暂无教育经历
          </text>
          <text class="empty-description" :style="getEmptyDescriptionStyle(styles)">
            请添加您的教育背景信息
          </text>
        </view>
      </view>
    </template>
  </BaseComponent>
</template>

<script setup lang="ts">
import { computed } from "vue";
import BaseComponent from "./BaseComponent.vue";
import type { TemplateGlobalStyle } from "@/types/template";

interface Props {
  componentData: any;
  globalStyle?: TemplateGlobalStyle;
  customStyles?: Record<string, any>;
}

const props = defineProps<Props>();

const componentName = computed(() =>
  props.componentData?.name || '教育经历'
);

// 组件配置
const componentProps = computed(() =>
  props.componentData?.props || {}
);

const defaultConfig = computed(() =>
  props.componentData?.defaultConfig || {}
);

// 教育经历数据
const educations = computed(() =>
  componentProps.value.experiences || []
);

// 配置选项
const showWorkPeriod = computed(() =>
  defaultConfig.value.props?.showWorkPeriod ?? true
);

const showDegree = computed(() =>
  defaultConfig.value.props?.showDegree ?? true
);

const showGPA = computed(() =>
  defaultConfig.value.props?.showGPA ?? false
);

const showSchoolLogo = computed(() =>
  defaultConfig.value.props?.showSchoolLogo ?? false
);

const maxItems = computed(() =>
  defaultConfig.value.props?.maxItems || 5
);

const maxCourses = computed(() =>
  defaultConfig.value.props?.maxCoursesPerItem || 3
);

const layoutType = computed(() =>
  defaultConfig.value.props?.layout || 'timeline'
);

const orderDirection = computed(() =>
  defaultConfig.value.props?.orderDirection || 'desc'
);

// 按配置排序教育经历
const sortedEducations = computed(() => {
  const items = [...educations.value];

  // 限制最大显示数量
  const limitedItems = items.slice(0, maxItems.value);

  // 按开始日期排序
  return limitedItems.sort((a, b) => {
    const dateA = new Date(a.startDate || 0).getTime();
    const dateB = new Date(b.startDate || 0).getTime();

    return orderDirection.value === 'desc' ? dateB - dateA : dateA - dateB;
  });
});

// 限制显示课程数量
const getLimitedCourses = (courses: string[]) => {
  return courses.slice(0, maxCourses.value);
};

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

// ============= 样式函数 =============

const getContainerStyle = (styles: any) => {
  if (!styles) return {};

  return {
    width: '100%',
    boxSizing: 'border-box',
    fontFamily: styles.fontFamily,
    fontSize: styles.bodySize,
    lineHeight: styles.lineHeight
  };
};

const getTimelineLayoutStyle = (styles: any) => {
  if (!styles) return {};

  return {
    width: '100%',
    position: 'relative'
  };
};

const getTimelineItemStyle = (styles: any, index: number) => {
  if (!styles) return {};

  return {
    display: 'flex',
    position: 'relative',
    marginBottom: index < sortedEducations.length - 1 ?
      (styles.itemSpacing || '24px') : '0',
    width: '100%'
  };
};

const getTimelineNodeStyle = (styles: any, education: any) => {
  if (!styles) return {};

  return {
    position: 'relative',
    width: '24px',
    marginRight: '16px',
    flexShrink: 0
  };
};

const getNodeCoreStyle = (styles: any, education: any) => {
  if (!styles) return {};

  const isCurrent = education.isCurrent;
  const nodeColor = isCurrent ? styles.accentColor : styles.primaryColor;

  return {
    width: '12px',
    height: '12px',
    borderRadius: '50%',
    backgroundColor: nodeColor,
    border: `2px solid ${styles.backgroundColor || '#fff'}`,
    boxShadow: `0 0 0 2px ${nodeColor}`,
    zIndex: 2,
    position: 'relative'
  };
};

const getTimelineConnectorStyle = (styles: any) => {
  if (!styles) return {};

  return {
    position: 'absolute',
    left: '5px',
    top: '14px',
    bottom: '-24px',
    width: '2px',
    backgroundColor: styles.timelineColor || '#e8e8e8',
    zIndex: 1
  };
};

const getContentStyle = (styles: any) => {
  if (!styles) return {};

  return {
    flex: 1,
    width: '100%',
    paddingBottom: sortedEducations.length > 1 ? '16px' : '0',
    borderBottom: sortedEducations.length > 1 ?
      `1px solid ${styles.secondaryColor || '#f0f0f0'}` : 'none'
  };
};

const getSchoolNameStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '16px',
    fontWeight: 'bold',
    color: styles.companyColor || styles.primaryColor,
    marginBottom: '4px',
    display: 'block'
  };
};

const getSchoolLogoStyle = (styles: any) => {
  if (!styles) return {};

  return {
    width: '40px',
    height: '40px',
    borderRadius: '50%',
    overflow: 'hidden',
    backgroundColor: '#f5f5f5',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    flexShrink: 0
  };
};

const getMajorStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '14px',
    color: styles.textColor,
    fontWeight: '500',
    marginRight: '8px'
  };
};

const getDegreeStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '13px',
    color: styles.periodColor || '#999',
    marginLeft: '8px'
  };
};

const getTimePeriodStyle = (styles: any) => {
  if (!styles) return {};

  return {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    flexWrap: 'wrap',
    marginTop: '8px',
    marginBottom: '12px'
  };
};

const getDescriptionStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '14px',
    color: styles.textColor,
    lineHeight: '1.6',
    marginTop: '8px'
  };
};

const getSectionTitleStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '14px',
    color: styles.titleColor,
    fontWeight: 'bold',
    display: 'block',
    marginTop: '12px',
    marginBottom: '8px'
  };
};

const getCourseTagStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '12px',
    color: styles.primaryColor,
    backgroundColor: `${styles.primaryColor}15`,
    padding: '4px 10px',
    borderRadius: '12px',
    border: `1px solid ${styles.primaryColor}30`
  };
};

const getMoreCoursesStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '11px',
    color: styles.periodColor || '#999',
    fontStyle: 'italic'
  };
};

const getHonorItemStyle = (styles: any) => {
  if (!styles) return {};

  return {
    display: 'flex',
    alignItems: 'center',
    marginBottom: '6px',
    fontSize: '13px',
    color: styles.textColor
  };
};

const getActivityItemStyle = getHonorItemStyle;
const getAchievementItemStyle = getHonorItemStyle;

const getEmptyStateStyle = (styles: any) => {
  if (!styles) return {};

  return {
    textAlign: 'center',
    padding: '60px 20px'
  };
};

const getEmptyTitleStyle = (styles: any) => {
  if (!styles) return {};

  return {
    display: 'block',
    fontSize: '16px',
    color: styles.textColor,
    marginTop: '12px',
    marginBottom: '8px'
  };
};

const getEmptyDescriptionStyle = (styles: any) => {
  if (!styles) return {};

  return {
    display: 'block',
    fontSize: '14px',
    color: styles.periodColor || '#999'
  };
};
</script>

<style scoped>
.education-experience-container {
  font-family: inherit;
  width: 100%;
}

/* 时间线布局 */
.timeline-layout {
  position: relative;
  width: 100%;
}

.timeline-item {
  display: flex;
  width: 100%;
}

.timeline-node {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.node-core {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  z-index: 2;
}

.timeline-connector {
  position: absolute;
  width: 2px;
  z-index: 1;
}

/* 学校行 */
.school-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 4px;
  width: 100%;
}

.school-logo {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.logo-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.degree-major {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  width: 100%;
}

/* 时间信息 */
.time-info, .achievement-info {
  display: flex;
  align-items: center;
  gap: 6px;
}

.time-icon, .achievement-icon {
  font-size: 14px;
}

.time-text, .achievement-text {
  font-size: 13px;
}

/* 标签云 */
.courses-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
  width: 100%;
}

.course-tag {
  transition: all 0.3s ease;
}

.course-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

/* 列表样式 */
.honors-list,
.activities-list,
.achievements-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-top: 6px;
  width: 100%;
}

.honor-item,
.activity-item,
.achievement-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  width: 100%;
}

.honor-icon,
.activity-icon,
.achievement-icon {
  flex-shrink: 0;
  margin-top: 2px;
}

.honor-text,
.activity-text,
.achievement-text {
  flex: 1;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.empty-icon {
  font-size: 48px;
  opacity: 0.5;
  margin-bottom: 12px;
}

.empty-title {
  font-weight: 500;
}

.empty-description {
  opacity: 0.7;
}

/* ============= 响应式设计 ============= */

/* 移动端优化 */
@media (max-width: 768px) {
  .timeline-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .timeline-node {
    position: relative;
    width: 100%;
    margin-right: 0;
    margin-bottom: 12px;
    display: flex;
    flex-direction: row;
    align-items: center;
  }

  .node-core {
    margin-left: 8px;
  }

  .timeline-connector {
    position: absolute;
    left: 14px !important;
    top: 14px !important;
    bottom: -12px !important;
    height: auto;
  }

  .timeline-content {
    width: 100% !important;
    padding-left: 32px;
    padding-bottom: 16px;
  }

  .school-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .school-logo {
    margin-top: 8px;
    align-self: flex-start;
  }

  .degree-major {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .degree {
    margin-left: 0 !important;
    margin-top: 2px;
  }

  .time-period {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .courses-tags {
    gap: 6px;
  }

  .course-tag {
    font-size: 11px !important;
    padding: 3px 8px !important;
  }
}

/* 平板优化 */
@media (min-width: 769px) and (max-width: 1024px) {
  .education-experience-container {
    max-width: 100%;
  }

  .timeline-content {
    width: 100% !important;
  }
}

/* 打印优化 */
@media print {
  .education-experience-container {
    break-inside: avoid;
    page-break-inside: avoid;
    width: 100% !important;
  }

  .timeline-item {
    break-inside: avoid;
    page-break-inside: avoid;
  }

  .course-tag {
    border: 1px solid #333 !important;
    background: white !important;
    color: #333 !important;
  }
}
</style>