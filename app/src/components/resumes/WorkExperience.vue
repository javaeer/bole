<!-- components/resumes/WorkExperience.vue -->
<template>
  <BaseComponent
    :title="componentName"
    :component-data="componentData"
    :global-style="globalStyle"
    :custom-styles="customStyles"
  >
    <template #default="{ styles }">
      <view
        class="work-experience-container"
        :style="getContainerStyle(styles)"
      >
        <!-- 时间线样式 -->
        <view class="timeline-line" :style="getTimelineStyle(styles)"></view>

        <view
          v-for="(experience, index) in sortedExperiences"
          :key="experience.id || index"
          class="experience-item"
          :style="getItemStyle(styles, index, experience.isCurrent)"
        >
          <!-- 时间轴节点 -->
          <view
            class="timeline-node"
            :style="getNodeStyle(styles, experience.isCurrent)"
          ></view>

          <!-- 公司信息区域 -->
          <view class="experience-content">
            <!-- 公司名称和logo -->
            <view class="company-header">
              <view class="company-info">
                <text
                  class="company-name"
                  :style="getCompanyNameStyle(styles)"
                >
                  {{ experience.company }}
                </text>
                <text
                  class="position"
                  :style="getPositionStyle(styles)"
                >
                  {{ experience.position }}
                </text>
              </view>

              <!-- 当前工作标识 -->
              <view
                v-if="experience.isCurrent"
                class="current-badge"
                :style="getCurrentBadgeStyle(styles)"
              >
                <text class="badge-text">在职</text>
              </view>
            </view>

            <!-- 工作时间和地点 -->
            <view v-if="showWorkPeriod" class="meta-info">
              <view v-if="showDuration" class="time-period">
                <text class="time-icon">📅</text>
                <text class="time-text" :style="getPeriodStyle(styles)">
                  {{ formatDate(experience.startDate) }} -
                  {{ experience.isCurrent ? "至今" : formatDate(experience.endDate) }}
                  <text v-if="calculateDuration(experience)" class="duration">
                    ({{ calculateDuration(experience) }})
                  </text>
                </text>
              </view>

              <view v-if="showLocation && experience.location" class="work-location">
                <text class="location-icon">📍</text>
                <text class="location-text">{{ experience.location }}</text>
              </view>

              <view v-if="showDepartment && experience.department" class="department">
                <text class="department-icon">🏢</text>
                <text class="department-text">{{ experience.department }}</text>
              </view>
            </view>

            <!-- 工作描述 -->
            <view
              v-if="experience.description"
              class="description"
              :style="getDescriptionStyle(styles)"
            >
              {{ experience.description }}
            </view>

            <!-- 工作职责 -->
            <view
              v-if="experience.responsibilities && experience.responsibilities.length > 0"
              class="responsibilities"
            >
              <text class="section-title" :style="getSectionTitleStyle(styles)">
                工作职责：
              </text>
              <view class="responsibilities-list">
                <view
                  v-for="(responsibility, rIndex) in experience.responsibilities"
                  :key="rIndex"
                  class="responsibility-item"
                  :style="getListItemStyle(styles)"
                >
                  <text class="bullet">•</text>
                  <text class="item-text">{{ responsibility }}</text>
                </view>
              </view>
            </view>

            <!-- 工作成就 -->
            <view
              v-if="experience.achievements && experience.achievements.length > 0"
              class="achievements"
            >
              <text class="section-title" :style="getSectionTitleStyle(styles)">
                主要成就：
              </text>
              <view class="achievements-list">
                <view
                  v-for="(achievement, aIndex) in experience.achievements"
                  :key="aIndex"
                  class="achievement-item"
                  :style="getListItemStyle(styles)"
                >
                  <text class="bullet">🏆</text>
                  <text class="item-text">{{ achievement }}</text>
                </view>
              </view>
            </view>

            <!-- 使用技能 -->
            <view
              v-if="showSkills && experience.skills && experience.skills.length > 0"
              class="skills-used"
            >
              <text class="section-title" :style="getSectionTitleStyle(styles)">
                使用技能：
              </text>
              <view class="skills-tags">
                <view
                  v-for="(skill, sIndex) in experience.skills"
                  :key="sIndex"
                  class="skill-tag"
                  :style="getSkillTagStyle(styles)"
                >
                  {{ skill }}
                </view>
              </view>
            </view>

            <!-- 项目成果 -->
            <view
              v-if="experience.projects && experience.projects.length > 0"
              class="projects"
            >
              <text class="section-title" :style="getSectionTitleStyle(styles)">
                参与项目：
              </text>
              <view class="projects-list">
                <view
                  v-for="(project, pIndex) in experience.projects"
                  :key="pIndex"
                  class="project-item"
                >
                  <view class="project-header">
                    <text class="project-name">{{ project.name }}</text>
                    <text v-if="project.role" class="project-role">{{ project.role }}</text>
                  </view>
                  <view v-if="project.description" class="project-description">
                    {{ project.description }}
                  </view>
                </view>
              </view>
            </view>

            <!-- 推荐人/证明人 -->
            <view
              v-if="showReference && experience.reference"
              class="reference"
            >
              <text class="reference-label">证明人：</text>
              <text class="reference-name">{{ experience.reference.name }}</text>
              <text v-if="experience.reference.position" class="reference-position">
                ({{ experience.reference.position }})
              </text>
              <text v-if="experience.reference.contact" class="reference-contact">
                {{ experience.reference.contact }}
              </text>
            </view>
          </view>
        </view>

        <!-- 没有工作经历时的占位 -->
        <view
          v-if="experiences.length === 0"
          class="empty-placeholder"
          :style="getPlaceholderStyle(styles)"
        >
          <text class="placeholder-icon">💼</text>
          <text class="placeholder-text">暂无工作经历</text>
          <text class="placeholder-hint">点击添加工作经历</text>
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

// 获取组件数据
const componentName = computed(() =>
  props.componentData?.name || "工作经历"
);

const componentProps = computed(() =>
  props.componentData?.props || {}
);

const defaultConfig = computed(() =>
  props.componentData?.defaultConfig || {}
);

const componentStyles = computed(() =>
  props.componentData?.styles || {}
);

// 工作经验数据
const experiences = computed(() =>
  componentProps.value.experiences || []
);

// 配置选项（从默认配置中获取）
const showDuration = computed(() =>
  componentProps.value.showDuration ?? defaultConfig.value.props?.showWorkPeriod ?? true
);

const showCompanyLogo = computed(() =>
  defaultConfig.value.props?.showCompanyLogo ?? false
);

const showCompanyName = computed(() =>
  defaultConfig.value.props?.showCompanyName ?? true
);

const showJobTitle = computed(() =>
  defaultConfig.value.props?.showJobTitle ?? true
);

const showDepartment = computed(() =>
  defaultConfig.value.props?.showDepartment ?? false
);

const showWorkPeriod = computed(() =>
  defaultConfig.value.props?.showWorkPeriod ?? true
);

const showWorkContent = computed(() =>
  defaultConfig.value.props?.showWorkContent ?? true
);

const showAchievements = computed(() =>
  defaultConfig.value.props?.showAchievements ?? true
);

const showSkills = computed(() =>
  defaultConfig.value.props?.showSkills ?? false
);

const showLocation = computed(() =>
  componentProps.value.showLocation ?? false
);

const showReference = computed(() =>
  componentProps.value.showReference ?? false
);

// 按开始日期倒序排序
const sortedExperiences = computed(() => {
  const orderBy = defaultConfig.value.props?.orderBy || "startDate";
  const orderDirection = defaultConfig.value.props?.orderDirection || "desc";

  return [...experiences.value].sort((a, b) => {
    const aValue = a[orderBy] || "";
    const bValue = b[orderBy] || "";

    if (orderDirection === "desc") {
      return new Date(bValue).getTime() - new Date(aValue).getTime();
    } else {
      return new Date(aValue).getTime() - new Date(bValue).getTime();
    }
  }).slice(0, defaultConfig.value.props?.maxItems || 10); // 限制显示数量
});

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return "";
  try {
    const date = new Date(dateStr);
    return `${date.getFullYear()}.${(date.getMonth() + 1).toString().padStart(2, "0")}`;
  } catch (e) {
    return dateStr;
  }
};

// 计算工作持续时间
const calculateDuration = (experience: any) => {
  if (!experience.startDate) return "";

  const start = new Date(experience.startDate);
  const end = experience.isCurrent ? new Date() : new Date(experience.endDate);

  if (isNaN(start.getTime()) || isNaN(end.getTime())) return "";

  const diffTime = Math.abs(end.getTime() - start.getTime());
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  const years = Math.floor(diffDays / 365);
  const months = Math.floor((diffDays % 365) / 30);

  let result = "";
  if (years > 0) result += `${years}年`;
  if (months > 0) result += `${months}个月`;

  return result;
};

// 样式计算方法
const getContainerStyle = (styles: any) => {
  if (!styles) return {};

  return {
    position: 'relative',
    padding: styles.padding,
    backgroundColor: styles.backgroundColor,
    borderRadius: styles.borderRadius,
    fontFamily: styles.fontFamily,
    fontSize: styles.bodySize,
    lineHeight: styles.lineHeight,
    gap: styles.itemSpacing || '20px',
    display: 'flex',
    flexDirection: 'column'
  };
};

const getTimelineStyle = (styles: any) => {
  if (!styles) return {};

  return {
    position: 'absolute',
    left: '16px',
    top: '0',
    bottom: '0',
    width: '2px',
    backgroundColor: styles.timelineColor || '#e8e8e8',
    zIndex: '1'
  };
};

const getItemStyle = (styles: any, index: number, isCurrent: boolean) => {
  if (!styles) return {};

  const baseStyle: any = {
    position: 'relative',
    paddingLeft: '40px',
    paddingBottom: index < sortedExperiences.value.length - 1 ?
      (styles.itemSpacing || '20px') : '0',
    marginBottom: index < sortedExperiences.value.length - 1 ?
      (styles.itemSpacing || '20px') : '0',
    borderBottom: index < sortedExperiences.value.length - 1 ?
      `1px dashed ${styles.secondaryColor || '#e8e8e8'}` : 'none'
  };

  if (isCurrent) {
    baseStyle.borderLeft = `3px solid ${styles.accentColor || '#52c41a'}`;
    baseStyle.paddingLeft = '37px'; // 调整左边距
    baseStyle.backgroundColor = 'rgba(82, 196, 26, 0.05)';
    baseStyle.borderRadius = '4px';
    baseStyle.padding = '12px';
    baseStyle.marginLeft = '-12px';
  }

  return baseStyle;
};

const getNodeStyle = (styles: any, isCurrent: boolean) => {
  if (!styles) return {};

  const baseStyle: any = {
    position: 'absolute',
    left: '12px',
    top: '10px',
    width: '12px',
    height: '12px',
    borderRadius: '50%',
    backgroundColor: isCurrent ?
      (styles.accentColor || '#52c41a') :
      (styles.primaryColor || '#1890ff'),
    border: `2px solid ${styles.backgroundColor || '#fff'}`,
    zIndex: '2',
    boxShadow: `0 0 0 2px ${isCurrent ?
      (styles.accentColor + '30' || 'rgba(82, 196, 26, 0.2)') :
      (styles.primaryColor + '30' || 'rgba(24, 144, 255, 0.2)')}`
  };

  return baseStyle;
};

const getCompanyNameStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '16px',
    fontWeight: 'bold',
    color: styles.companyColor || styles.primaryColor || '#1890ff',
    marginBottom: '4px',
    display: 'block'
  };
};

const getPositionStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '14px',
    fontWeight: '500',
    color: styles.textColor || '#333',
    marginBottom: '8px',
    display: 'block'
  };
};

const getCurrentBadgeStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '12px',
    color: '#fff',
    backgroundColor: styles.accentColor || '#52c41a',
    padding: '2px 8px',
    borderRadius: '10px',
    fontWeight: 'bold'
  };
};

const getPeriodStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '12px',
    color: styles.periodColor || '#999999',
    backgroundColor: '#f5f5f5',
    padding: '2px 8px',
    borderRadius: '10px',
    display: 'inline-block'
  };
};

const getDescriptionStyle = (styles: any) => {
  if (!styles) return {};

  return {
    color: styles.textColor || '#555',
    fontSize: '14px',
    lineHeight: '1.6',
    margin: '8px 0',
    textAlign: 'justify'
  };
};

const getSectionTitleStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '14px',
    fontWeight: 'bold',
    color: styles.titleColor || '#333',
    marginBottom: '8px',
    display: 'block'
  };
};

const getListItemStyle = (styles: any) => {
  if (!styles) return {};

  return {
    color: styles.textColor || '#666',
    fontSize: '14px',
    lineHeight: '1.5',
    marginBottom: '4px',
    display: 'flex',
    alignItems: 'flex-start',
    gap: '8px'
  };
};

const getSkillTagStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '12px',
    color: styles.primaryColor || '#1890ff',
    backgroundColor: 'rgba(24, 144, 255, 0.1)',
    padding: '2px 8px',
    borderRadius: '12px',
    border: `1px solid rgba(24, 144, 255, 0.2)`
  };
};

const getPlaceholderStyle = (styles: any) => {
  if (!styles) return {};

  return {
    textAlign: 'center',
    color: '#999',
    padding: '40px 20px',
    background: 'repeating-linear-gradient(45deg, #fafafa, #fafafa 10px, #f0f0f0 10px, #f0f0f0 20px)',
    borderRadius: '4px',
    fontSize: styles.bodySize,
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    gap: '8px'
  };
};
</script>

<style scoped>
.work-experience-container {
  position: relative;
}

.timeline-line {
  position: absolute;
  z-index: 1;
}

.experience-item {
  position: relative;
  z-index: 2;
}

.experience-content {
  flex: 1;
}

.company-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.company-info {
  flex: 1;
}

.current-badge {
  flex-shrink: 0;
  margin-left: 12px;
  font-size: 12px;
}

.meta-info {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 12px;
  align-items: center;
}

.time-period,
.work-location,
.department {
  display: flex;
  align-items: center;
  gap: 4px;
}

.time-icon,
.location-icon,
.department-icon {
  font-size: 12px;
}

.duration {
  margin-left: 4px;
  color: #999;
}

.description {
  white-space: pre-wrap;
  word-break: break-word;
}

.responsibilities,
.achievements,
.skills-used,
.projects {
  margin-top: 12px;
}

.responsibilities-list,
.achievements-list {
  padding-left: 16px;
}

.bullet {
  margin-right: 4px;
  flex-shrink: 0;
}

.item-text {
  flex: 1;
}

.skills-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 8px;
}

.project-item {
  margin-bottom: 12px;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 4px;
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.project-name {
  font-weight: 500;
  color: #333;
}

.project-role {
  font-size: 12px;
  color: #666;
  background: #e9ecef;
  padding: 1px 6px;
  border-radius: 3px;
}

.project-description {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
}

.reference {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #eee;
  font-size: 12px;
  color: #888;
}

.reference-label {
  font-weight: 500;
  margin-right: 4px;
}

.reference-position,
.reference-contact {
  margin-left: 8px;
}

.empty-placeholder {
  min-height: 100px;
}

.placeholder-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.placeholder-text {
  font-size: 14px;
  color: #999;
}

.placeholder-hint {
  font-size: 12px;
  color: #ccc;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .experience-item {
    padding-left: 30px;
  }

  .timeline-node {
    left: 8px;
    width: 10px;
    height: 10px;
  }

  .company-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .current-badge {
    margin-left: 0;
  }

  .meta-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}

/* 打印样式 */
@media print {
  .work-experience-container {
    break-inside: avoid;
  }

  .current-badge {
    background: #fff !important;
    color: #000 !important;
    border: 1px solid #000 !important;
  }

  .timeline-line,
  .timeline-node {
    display: none;
  }
}
</style>