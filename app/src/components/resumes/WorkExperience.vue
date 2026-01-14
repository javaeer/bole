<template>
  <BaseComponent
    :component-data="componentData"
    :global-style="globalStyle"
    :show-header="showTitle"
    :custom-title="title"
    :responsive-center="responsiveCenter"
    :max-width="maxWidth"
  >
    <template #default="{ styles }">
      <view class="work-experience-container">
        <!-- 时间线样式 -->
        <view v-if="showTimeline && experiences.length > 0" class="timeline-line"></view>

        <!-- 工作经历列表 -->
        <view
          v-for="(experience, index) in sortedExperiences"
          :key="experience.id || index"
          class="experience-item"
          :class="{ 'current-item': experience.isCurrent }"
        >
          <!-- 时间轴节点 -->
          <view
            v-if="showTimeline"
            class="timeline-node"
            :class="{ 'current-node': experience.isCurrent }"
          ></view>

          <!-- 工作经历内容 -->
          <view class="experience-content">
            <!-- 公司信息区域 -->
            <view class="company-header">
              <view class="company-info">
                <text class="company-name">
                  {{ experience.company || '未填写公司' }}
                </text>
                <text class="position" v-if="showJobTitle && experience.position">
                  {{ experience.position }}
                </text>
              </view>

              <!-- 当前工作标识 -->
              <view
                v-if="experience.isCurrent"
                class="current-badge"
              >
                <text class="badge-text">在职</text>
              </view>
            </view>

            <!-- 工作时间和地点 -->
            <view v-if="showWorkPeriod" class="meta-info">
              <view v-if="showDuration" class="time-period">
                <text class="time-icon">📅</text>
                <text class="time-text">
                  {{ formatDate(experience.startDate) }} -
                  {{ experience.isCurrent ? '至今' : formatDate(experience.endDate) }}
                  <text v-if="showDuration && calculateDuration(experience)" class="duration">
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
              v-if="showWorkContent && experience.description"
              class="description"
            >
              {{ experience.description }}
            </view>

            <!-- 工作职责 -->
            <view
              v-if="experience.responsibilities && experience.responsibilities.length > 0"
              class="responsibilities"
            >
              <text class="section-title">
                工作职责：
              </text>
              <view class="responsibilities-list">
                <view
                  v-for="(responsibility, rIndex) in experience.responsibilities"
                  :key="rIndex"
                  class="responsibility-item"
                >
                  <text class="bullet">•</text>
                  <text class="item-text">{{ responsibility }}</text>
                </view>
              </view>
            </view>

            <!-- 工作成就 -->
            <view
              v-if="showAchievements && experience.achievements && experience.achievements.length > 0"
              class="achievements"
            >
              <text class="section-title">
                主要成就：
              </text>
              <view class="achievements-list">
                <view
                  v-for="(achievement, aIndex) in experience.achievements"
                  :key="aIndex"
                  class="achievement-item"
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
              <text class="section-title">
                使用技能：
              </text>
              <view class="skills-tags">
                <view
                  v-for="(skill, sIndex) in experience.skills"
                  :key="sIndex"
                  class="skill-tag"
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
              <text class="section-title">
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
        >
          <text class="placeholder-icon">💼</text>
          <text class="placeholder-text">暂无工作经历</text>
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
  overrideStyles?: any;
  responsiveCenter?: boolean;
  maxWidth?: string;
}

const props = withDefaults(defineProps<Props>(), {
  globalStyle: () => ({}),
  overrideStyles: () => ({}),
  responsiveCenter: false,
  maxWidth: "100%"
});

// ===================== 计算属性 =====================

// 组件数据
const componentProps = computed(() => props.componentData?.props || {});
const defaultConfig = computed(() => props.componentData?.defaultConfig || {});

// 配置选项
const showTitle = computed(() => defaultConfig.value.props?.showTitle ?? true);
const showDuration = computed(() => componentProps.value.showDuration ?? true);
const showJobTitle = computed(() => defaultConfig.value.props?.showJobTitle ?? true);
const showDepartment = computed(() => defaultConfig.value.props?.showDepartment ?? false);
const showWorkPeriod = computed(() => defaultConfig.value.props?.showWorkPeriod ?? true);
const showWorkContent = computed(() => defaultConfig.value.props?.showWorkContent ?? true);
const showAchievements = computed(() => defaultConfig.value.props?.showAchievements ?? true);
const showSkills = computed(() => defaultConfig.value.props?.showSkills ?? false);
const showLocation = computed(() => componentProps.value.showLocation ?? false);
const showReference = computed(() => componentProps.value.showReference ?? false);
const showTimeline = computed(() => defaultConfig.value.props?.showTimeline ?? true);

// 组件标题
const title = computed(() => defaultConfig.value.props?.title || "工作经历");

// 工作经历数据
const experiences = computed(() => componentProps.value.experiences || []);

// 按开始日期倒序排序
const sortedExperiences = computed(() => {
  const orderBy = defaultConfig.value.props?.orderBy || "startDate";
  const orderDirection = defaultConfig.value.props?.orderDirection || "desc";
  const maxItems = defaultConfig.value.props?.maxItems || 10;

  return [...experiences.value]
    .sort((a, b) => {
      const aValue = a[orderBy] || "";
      const bValue = b[orderBy] || "";
      
      if (orderDirection === "desc") {
        return new Date(bValue).getTime() - new Date(aValue).getTime();
      } else {
        return new Date(aValue).getTime() - new Date(bValue).getTime();
      }
    })
    .slice(0, maxItems);
});

// ===================== 工具函数 =====================

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

  try {
    const start = new Date(experience.startDate);
    const end = experience.isCurrent ? new Date() : new Date(experience.endDate);

    if (isNaN(start.getTime()) || isNaN(end.getTime())) return "";

    const diffMonths = (end.getFullYear() - start.getFullYear()) * 12 + 
                      (end.getMonth() - start.getMonth());

    const years = Math.floor(diffMonths / 12);
    const months = diffMonths % 12;

    let result = "";
    if (years > 0) result += `${years}年`;
    if (months > 0) result += `${months}个月`;

    return result;
  } catch (e) {
    return "";
  }
};
</script>

<style scoped lang="scss">
.work-experience-container {
  position: relative;
  width: 100%;
  padding: var(--base-padding, 16px);
  box-sizing: border-box;
}

/* 时间线样式 */
.timeline-line {
  position: absolute;
  left: 16px;
  top: 0;
  bottom: 0;
  width: 2px;
  background-color: var(--base-secondary-color, #e8e8e8);
  z-index: 1;
  
  @media (max-width: 768px) {
    left: 8px;
    display: none;
  }
}

/* 工作经历项 */
.experience-item {
  position: relative;
  padding-left: 40px;
  margin-bottom: var(--base-item-spacing, 20px);
  padding-bottom: var(--base-item-spacing, 20px);
  border-bottom: 1px dashed var(--base-secondary-color, #e8e8e8);
  width: 100%;
  box-sizing: border-box;
  transition: all 0.3s ease;
  
  &:last-child {
    margin-bottom: 0;
    padding-bottom: 0;
    border-bottom: none;
  }
  
  &.current-item {
    border-left: 3px solid var(--base-accent-color, #52c41a);
    padding-left: 37px;
    background-color: rgba(82, 196, 26, 0.05);
    border-radius: 4px;
    padding: 12px 12px 12px 37px;
    margin-left: -12px;
  }
  
  @media (max-width: 768px) {
    padding-left: 20px;
    margin-bottom: 16px;
    padding-bottom: 16px;
    
    &.current-item {
      padding-left: 17px;
      margin-left: 0;
    }
  }
}

/* 时间线节点 */
.timeline-node {
  position: absolute;
  left: 12px;
  top: 10px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: var(--base-primary-color, #1890ff);
  border: 2px solid var(--base-background-color, #fff);
  z-index: 2;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
  
  &.current-node {
    background-color: var(--base-accent-color, #52c41a);
    box-shadow: 0 0 0 2px rgba(82, 196, 26, 0.2);
  }
  
  @media (max-width: 768px) {
    left: 8px;
    width: 10px;
    height: 10px;
  }
}

.experience-content {
  flex: 1;
  width: 100%;
  box-sizing: border-box;
}

/* 公司信息 */
.company-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
  flex-wrap: wrap;
  width: 100%;
  gap: 8px;
}

.company-info {
  flex: 1;
  min-width: 0;
}

.company-name {
  font-size: 16px;
  font-weight: bold;
  color: var(--base-company-color, var(--base-primary-color, #1890ff));
  margin-bottom: 4px;
  display: block;
  width: 100%;
}

.position {
  font-size: 14px;
  font-weight: 500;
  color: var(--base-text-color, #333);
  display: block;
  width: 100%;
}

/* 当前工作标识 */
.current-badge {
  flex-shrink: 0;
  font-size: 12px;
  color: #fff;
  background-color: var(--base-accent-color, #52c41a);
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: bold;
}

/* 元信息 */
.meta-info {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 12px;
  align-items: center;
  width: 100%;
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

.time-text {
  font-size: 12px;
  color: var(--base-period-color, #999);
  background-color: #f5f5f5;
  padding: 2px 8px;
  border-radius: 10px;
}

.duration {
  margin-left: 4px;
  color: var(--base-secondary-color, #999);
}

/* 描述文本 */
.description {
  color: var(--base-text-color, #555);
  font-size: 14px;
  line-height: 1.6;
  margin: 8px 0;
  text-align: justify;
  width: 100%;
  word-break: break-word;
}

/* 分段标题 */
.section-title {
  font-size: 14px;
  font-weight: bold;
  color: var(--base-title-color, #333);
  margin-bottom: 8px;
  display: block;
  width: 100%;
}

/* 列表项 */
.responsibilities,
.achievements,
.skills-used,
.projects {
  margin-top: 12px;
  width: 100%;
}

.responsibilities-list,
.achievements-list {
  padding-left: 16px;
  width: 100%;
}

.responsibility-item,
.achievement-item {
  color: var(--base-text-color, #666);
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 4px;
  display: flex;
  align-items: flex-start;
  gap: 8px;
  width: 100%;
}

.bullet {
  margin-right: 4px;
  flex-shrink: 0;
}

.item-text {
  flex: 1;
  min-width: 0;
  word-break: break-word;
}

/* 技能标签 */
.skills-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 8px;
  width: 100%;
}

.skill-tag {
  font-size: 12px;
  color: var(--base-primary-color, #1890ff);
  background-color: rgba(24, 144, 255, 0.1);
  padding: 2px 8px;
  border-radius: 12px;
  border: 1px solid rgba(24, 144, 255, 0.2);
}

/* 项目列表 */
.project-item {
  margin-bottom: 12px;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 4px;
  width: 100%;
  box-sizing: border-box;
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
  flex-wrap: wrap;
  width: 100%;
  gap: 4px;
}

.project-name {
  font-weight: 500;
  color: var(--base-text-color, #333);
}

.project-role {
  font-size: 12px;
  color: var(--base-text-color, #666);
  background: #e9ecef;
  padding: 1px 6px;
  border-radius: 3px;
}

.project-description {
  font-size: 12px;
  color: var(--base-text-color, #666);
  line-height: 1.4;
  width: 100%;
}

/* 证明人 */
.reference {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed var(--base-secondary-color, #eee);
  font-size: 12px;
  color: var(--base-secondary-color, #888);
  width: 100%;
}

.reference-label {
  font-weight: 500;
  margin-right: 4px;
}

.reference-position,
.reference-contact {
  margin-left: 8px;
}

/* 空状态 */
.empty-placeholder {
  text-align: center;
  color: var(--base-secondary-color, #999);
  padding: 40px 20px;
  background: repeating-linear-gradient(
    45deg,
    #fafafa,
    #fafafa 10px,
    #f0f0f0 10px,
    #f0f0f0 20px
  );
  border-radius: 4px;
  font-size: var(--base-body-size, 14px);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  width: 100%;
  box-sizing: border-box;
}

.placeholder-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.placeholder-text {
  font-size: 14px;
  color: var(--base-secondary-color, #999);
}

/* ===================== 响应式设计 ===================== */
@media (max-width: 768px) {
  .work-experience-container {
    padding: 12px !important;
  }
  
  .company-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .current-badge {
    margin-left: 0;
    align-self: flex-start;
  }
  
  .meta-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .company-name {
    font-size: 15px !important;
  }
  
  .position {
    font-size: 13px !important;
  }
  
  .description {
    font-size: 13px !important;
    line-height: 1.5 !important;
  }
  
  .section-title {
    font-size: 13px !important;
  }
}

@media (max-width: 480px) {
  .work-experience-container {
    padding: 10px !important;
  }
  
  .experience-item {
    padding-left: 16px !important;
    margin-bottom: 12px !important;
    padding-bottom: 12px !important;
    
    &.current-item {
      padding-left: 13px !important;
    }
  }
  
  .company-name {
    font-size: 14px !important;
  }
  
  .position {
    font-size: 12px !important;
  }
  
  .description {
    font-size: 12px !important;
  }
  
  .skills-tags {
    gap: 4px;
  }
  
  .skill-tag {
    font-size: 10px !important;
    padding: 1px 6px !important;
  }
}

/* 平板设备 */
@media (min-width: 769px) and (max-width: 1024px) {
  .work-experience-container {
    padding: 20px !important;
  }
}

/* 打印样式 */
@media print {
  .work-experience-container {
    break-inside: avoid;
    page-break-inside: avoid;
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
  
  .experience-item {
    padding-left: 0 !important;
    border-bottom: 1px solid #eee !important;
  }
  
  .company-name,
  .position,
  .description,
  .item-text {
    color: #333 !important;
  }
}
</style>