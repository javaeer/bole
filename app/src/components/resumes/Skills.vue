<template>
  <BaseComponent
    :component-data="componentData"
    :global-style="globalStyle"
    :override-styles="customStyles"
    :show-header="showTitle"
    :custom-title="title"
    :responsive-center="true"
    :max-width="maxWidth"
  >
    <template #default="{ styles }">
      <view class="skills-component" :style="containerStyle">
        <!-- 按分类分组显示 -->
        <template v-if="groupByCategory && Object.keys(groupedSkills).length > 0">
          <view
            v-for="(skills, category) in groupedSkills"
            :key="category"
            class="category-group"
            :style="getCategoryStyle(category)"
          >
            <!-- 分类标题 -->
            <view class="category-header">
              <view class="category-title-line">
                <text class="category-title" :style="categoryTitleStyle">
                  {{ getCategoryDisplayName(category) }}
                </text>
                <text class="category-count" :style="categoryCountStyle">
                  （{{ skills.length }}项）
                </text>
              </view>
              <view
                v-if="showCategoryDescription"
                class="category-description"
                :style="categoryDescriptionStyle"
              >
                {{ getCategoryDescription(category) }}
              </view>
            </view>

            <!-- 技能列表 -->
            <view class="skills-list">
              <view
                v-for="skill in getSortedSkills(skills)"
                :key="skill.id || skill.name"
                class="skill-item"
                :class="{
                  'has-certification': skill.isCertified,
                  'has-progress': skill.proficiencyPercent,
                  'is-public': skill.isPublic
                }"
                :style="getSkillItemStyle(skill)"
              >
                <!-- 技能基本信息 -->
                <view class="skill-main-info">
                  <!-- 技能名称和等级 -->
                  <view class="skill-header">
                    <view class="skill-name-row">
                      <text class="skill-name" :style="skillNameStyle">
                        {{ skill.name }}
                      </text>
                      <view v-if="skill.level" class="skill-level-tag" :style="levelTagStyle">
                        <text class="level-text" :style="levelTextStyle">
                          {{ skill.level }}
                        </text>
                        <view v-if="skill.proficiencyPercent" class="level-percent">
                          {{ skill.proficiencyPercent }}%
                        </view>
                      </view>
                    </view>

                    <!-- 技能类别和公开状态 -->
                    <view class="skill-meta">
                      <text
                        v-if="showCategory && skill.category"
                        class="meta-item category-badge"
                        :style="categoryBadgeStyle"
                      >
                        {{ skill.category }}
                      </text>
                      <text
                        v-if="skill.isPublic"
                        class="meta-item public-badge"
                        :style="publicBadgeStyle"
                      >
                        公开
                      </text>
                      <text
                        v-if="!skill.isPublic"
                        class="meta-item private-badge"
                        :style="privateBadgeStyle"
                      >
                        私有
                      </text>
                    </view>
                  </view>

                  <!-- 技能描述 -->
                  <view
                    v-if="skill.description"
                    class="skill-description"
                    :style="descriptionStyle"
                  >
                    {{ skill.description }}
                  </view>

                  <!-- 技能标签 -->
                  <view
                    v-if="showTags && skill.tags && skill.tags.length > 0"
                    class="skill-tags-container"
                  >
                    <view
                      v-for="tag in skill.tags"
                      :key="tag"
                      class="tag-item"
                      :style="tagStyle"
                    >
                      <text class="tag-text" :style="tagTextStyle">
                        {{ tag }}
                      </text>
                    </view>
                  </view>
                </view>

                <!-- 技能详细信息 -->
                <view class="skill-details">
                  <!-- 经验年限 -->
                  <view
                    v-if="showExperienceYears && skill.experienceYears"
                    class="detail-item experience-item"
                  >
                    <view class="detail-label">
                      <text class="detail-icon">📅</text>
                      <text class="label-text" :style="labelTextStyle">经验</text>
                    </view>
                    <view class="detail-value">
                      <view class="experience-bar">
                        <view
                          class="experience-fill"
                          :style="getExperienceFillStyle(skill.experienceYears)"
                        ></view>
                      </view>
                      <text class="years-text" :style="yearsTextStyle">
                        {{ skill.experienceYears }}年
                      </text>
                    </view>
                  </view>

                  <!-- 技能进度条 -->
                  <view
                    v-if="showSkillLevel && skill.proficiencyPercent"
                    class="detail-item progress-item"
                  >
                    <view class="detail-label">
                      <text class="detail-icon">📊</text>
                      <text class="label-text" :style="labelTextStyle">熟练度</text>
                    </view>
                    <view class="detail-value">
                      <view class="progress-bar">
                        <view
                          class="progress-fill"
                          :style="getProgressFillStyle(skill.proficiencyPercent)"
                        ></view>
                        <view class="progress-marks">
                          <view class="progress-mark" style="left: 25%"></view>
                          <view class="progress-mark" style="left: 50%"></view>
                          <view class="progress-mark" style="left: 75%"></view>
                        </view>
                      </view>
                      <text class="progress-text" :style="progressTextStyle">
                        {{ skill.proficiencyPercent }}%
                      </text>
                    </view>
                  </view>

                  <!-- 证书信息 -->
                  <view
                    v-if="showCertification && skill.isCertified && skill.certificateName"
                    class="detail-item certification-item"
                  >
                    <view class="detail-label">
                      <text class="detail-icon">🏆</text>
                      <text class="label-text" :style="labelTextStyle">证书</text>
                    </view>
                    <view class="detail-value certification-details">
                      <text class="cert-name" :style="certNameStyle">
                        {{ skill.certificateName }}
                      </text>
                      <text
                        v-if="skill.certificateDate"
                        class="cert-date"
                        :style="certDateStyle"
                      >
                        {{ formatDate(skill.certificateDate) }}
                      </text>
                    </view>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </template>

        <!-- 不分组显示 -->
        <template v-else>
          <view class="skills-list">
            <view
              v-for="skill in sortedSkills"
              :key="skill.id || skill.name"
              class="skill-item"
              :class="{
                'has-certification': skill.isCertified,
                'has-progress': skill.proficiencyPercent,
                'is-public': skill.isPublic
              }"
              :style="getSkillItemStyle(skill)"
            >
              <!-- 技能基本信息 -->
              <view class="skill-main-info">
                <!-- 技能名称和等级 -->
                <view class="skill-header">
                  <view class="skill-name-row">
                    <text class="skill-name" :style="skillNameStyle">
                      {{ skill.name }}
                    </text>
                    <view v-if="skill.level" class="skill-level-tag" :style="levelTagStyle">
                      <text class="level-text" :style="levelTextStyle">
                        {{ skill.level }}
                      </text>
                      <view v-if="skill.proficiencyPercent" class="level-percent">
                        {{ skill.proficiencyPercent }}%
                      </view>
                    </view>
                  </view>

                  <!-- 技能类别和公开状态 -->
                  <view class="skill-meta">
                    <text
                      v-if="showCategory && skill.category"
                      class="meta-item category-badge"
                      :style="categoryBadgeStyle"
                    >
                      {{ skill.category }}
                    </text>
                    <text
                      v-if="skill.isPublic"
                      class="meta-item public-badge"
                      :style="publicBadgeStyle"
                    >
                      公开
                    </text>
                    <text
                      v-if="!skill.isPublic"
                      class="meta-item private-badge"
                      :style="privateBadgeStyle"
                    >
                      私有
                    </text>
                  </view>
                </view>

                <!-- 技能描述 -->
                <view
                  v-if="skill.description"
                  class="skill-description"
                  :style="descriptionStyle"
                >
                  {{ skill.description }}
                </view>

                <!-- 技能标签 -->
                <view
                  v-if="showTags && skill.tags && skill.tags.length > 0"
                  class="skill-tags-container"
                >
                  <view
                    v-for="tag in skill.tags"
                    :key="tag"
                    class="tag-item"
                    :style="tagStyle"
                  >
                    <text class="tag-text" :style="tagTextStyle">
                      {{ tag }}
                    </text>
                  </view>
                </view>
              </view>

              <!-- 技能详细信息 -->
              <view class="skill-details">
                <!-- 经验年限 -->
                <view
                  v-if="showExperienceYears && skill.experienceYears"
                  class="detail-item experience-item"
                >
                  <view class="detail-label">
                    <text class="detail-icon">📅</text>
                    <text class="label-text" :style="labelTextStyle">经验</text>
                  </view>
                  <view class="detail-value">
                    <view class="experience-bar">
                      <view
                        class="experience-fill"
                        :style="getExperienceFillStyle(skill.experienceYears)"
                      ></view>
                    </view>
                    <text class="years-text" :style="yearsTextStyle">
                      {{ skill.experienceYears }}年
                    </text>
                  </view>
                </view>

                <!-- 技能进度条 -->
                <view
                  v-if="showSkillLevel && skill.proficiencyPercent"
                  class="detail-item progress-item"
                >
                  <view class="detail-label">
                    <text class="detail-icon">📊</text>
                    <text class="label-text" :style="labelTextStyle">熟练度</text>
                  </view>
                  <view class="detail-value">
                    <view class="progress-bar">
                      <view
                        class="progress-fill"
                        :style="getProgressFillStyle(skill.proficiencyPercent)"
                      ></view>
                      <view class="progress-marks">
                        <view class="progress-mark" style="left: 25%"></view>
                        <view class="progress-mark" style="left: 50%"></view>
                        <view class="progress-mark" style="left: 75%"></view>
                      </view>
                    </view>
                    <text class="progress-text" :style="progressTextStyle">
                      {{ skill.proficiencyPercent }}%
                    </text>
                  </view>
                </view>

                <!-- 证书信息 -->
                <view
                  v-if="showCertification && skill.isCertified && skill.certificateName"
                  class="detail-item certification-item"
                >
                  <view class="detail-label">
                    <text class="detail-icon">🏆</text>
                    <text class="label-text" :style="labelTextStyle">证书</text>
                  </view>
                  <view class="detail-value certification-details">
                    <text class="cert-name" :style="certNameStyle">
                      {{ skill.certificateName }}
                    </text>
                    <text
                      v-if="skill.certificateDate"
                      class="cert-date"
                      :style="certDateStyle"
                    >
                      {{ formatDate(skill.certificateDate) }}
                    </text>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </template>
      </view>
    </template>
  </BaseComponent>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import BaseComponent from "./BaseComponent.vue";
import type { TemplateGlobalStyle } from "@/types/template";
import type { TemplateComponentResult } from "@/types/template-component";

interface Props {
  componentData: TemplateComponentResult;
  globalStyle?: TemplateGlobalStyle;
  customStyles?: Record<string, any>;
}

const props = defineProps<Props>();

// 最大宽度，根据布局类型调整
const maxWidth = computed(() => {
  const layoutType = props.globalStyle?.layout?.type || 'single';
  switch (layoutType) {
    case 'single': return '800px';
    case 'two-column': return '1200px';
    case 'timeline': return '900px';
    case 'card': return '1200px';
    case 'mixed': return '1000px';
    default: return '800px';
  }
});

// ===================== 计算属性 =====================

// 从defaultConfig获取默认配置
const defaultConfig = computed(() => props.componentData?.defaultConfig || {});
const configProps = computed(() => defaultConfig.value.props || {});

// 技能数据
const skills = computed(() => props.componentData.props?.skills || []);

// 组件标题
const title = computed(() => configProps.value.title || '技能专长');
const showTitle = computed(() => configProps.value.showTitle !== false);

// 显示控制
const showSkillLevel = computed(() => {
  const propValue = props.componentData.props?.skillLevel;
  const defaultValue = configProps.value.showSkillLevel;
  return propValue !== undefined ? propValue : (defaultValue !== false);
});

const showTags = computed(() => {
  const propValue = props.componentData.props?.showTags;
  return propValue !== undefined ? propValue : configProps.value.showTags;
});

const groupByCategory = computed(() => {
  const propValue = props.componentData.props?.groupByCategory;
  const defaultValue = configProps.value.groupByCategory;
  return propValue !== undefined ? propValue : defaultValue;
});

const showCategory = computed(() => true);
const showExperienceYears = computed(() => configProps.value.showExperienceYears || true);
const showCertification = computed(() => true);
const showCategoryDescription = computed(() => false);

// 技能进度条显示类型
const skillLevelType = computed(() => configProps.value.skillLevelType || 'progress');

// 技能分类映射
const categoryConfig = ref({
  "编程语言": {
    color: "#1890ff",
    icon: "💻",
    description: "掌握多种编程语言及其特性",
  },
  "框架": {
    color: "#52c41a",
    icon: "⚙️",
    description: "熟悉主流开发框架和工具",
  },
  "数据库": {
    color: "#fa8c16",
    icon: "🗃️",
    description: "具备数据库设计和管理能力",
  },
  "其他技能": {
    color: "#722ed1",
    icon: "✨",
    description: "其他相关技能和知识",
  },
});

// ===================== 数据处理 =====================

// 按分类分组
const groupedSkills = computed(() => {
  const groups: Record<string, any[]> = {};

  skills.value.forEach(skill => {
    const category = skill.category || "其他技能";
    if (!groups[category]) {
      groups[category] = [];
    }
    groups[category].push(skill);
  });

  // 按配置中的分类顺序排序
  const orderedGroups: Record<string, any[]> = {};
  const defaultCategories = configProps.value.skillCategories ||
    ["编程语言", "框架", "数据库", "其他技能"];

  defaultCategories.forEach(cat => {
    if (groups[cat]) {
      orderedGroups[cat] = groups[cat];
    }
  });

  // 添加其他未在配置中的分类
  Object.keys(groups).forEach(cat => {
    if (!orderedGroups[cat]) {
      orderedGroups[cat] = groups[cat];
    }
  });

  return orderedGroups;
});

// 获取分类显示名称
const getCategoryDisplayName = (category: string) => {
  const config = categoryConfig.value[category];
  return config ? `${config.icon} ${category}` : category;
};

// 获取分类描述
const getCategoryDescription = (category: string) => {
  return categoryConfig.value[category]?.description || "";
};

// 获取技能排序
const getSortedSkills = (skillList: any[]) => {
  const maxSkills = configProps.value.maxSkillsPerCategory || 8;
  const sorted = [...skillList].sort((a, b) => (a.sort || 0) - (b.sort || 0));
  return sorted.slice(0, maxSkills);
};

// 按排序字段排序
const sortedSkills = computed(() => {
  const maxSkills = configProps.value.maxSkillsPerCategory || 8;
  return [...skills.value]
    .sort((a, b) => (a.sort || 0) - (b.sort || 0))
    .slice(0, maxSkills);
});

// 获取进度条颜色
const getProgressColor = (percent: number) => {
  if (percent >= 90) return "#52c41a";
  if (percent >= 75) return "#1890ff";
  if (percent >= 50) return "#faad14";
  return "#ff4d4f";
};

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return "";
  try {
    const date = new Date(dateStr);
    return `${date.getFullYear()}年${(date.getMonth() + 1).toString().padStart(2, "0")}月`;
  } catch (e) {
    return dateStr;
  }
};

// ===================== 样式计算 =====================

// 容器样式 - 使用BaseComponent的样式
const containerStyle = computed(() => ({
  width: '100%'
}));

// 分类样式
const getCategoryStyle = (category: string) => {
  const config = categoryConfig.value[category];
  const borderColor = config?.color || 'var(--base-primary-color, #1890ff)';
  
  return {
    borderLeft: `4px solid ${borderColor}`,
    paddingLeft: '16px',
    marginBottom: '24px',
    width: '100%',
    '--category-color': borderColor
  };
};

// 静态样式 - 使用CSS变量
const categoryTitleStyle = computed(() => ({
  fontSize: '18px',
  fontWeight: 600,
  color: 'var(--category-color)',
  marginRight: '8px',
  display: 'inline-block'
}));

const categoryCountStyle = computed(() => ({
  fontSize: '14px',
  color: '#666',
  opacity: 0.8,
  display: 'inline-block'
}));

const categoryDescriptionStyle = computed(() => ({
  fontSize: '13px',
  color: '#666',
  lineHeight: '1.5',
  paddingLeft: '4px',
  borderLeft: '2px solid rgba(0, 0, 0, 0.1)',
  marginLeft: '4px',
  width: '100%'
}));

// 技能项样式
const getSkillItemStyle = (skill: any) => {
  const style: Record<string, any> = {
    width: '94%',
    maxWidth: '94%',
    padding: '16px',
    background: '#ffffff',
    borderRadius: '8px',
    border: '1px solid #f0f0f0',
    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.05)',
    boxSizing: 'border-box'
  };

  // 根据熟练度设置边框色
  if (skill.proficiencyPercent) {
    const color = getProgressColor(skill.proficiencyPercent);
    style.borderLeft = `3px solid ${color}`;
  } else {
    style.borderLeft = `3px solid var(--base-primary-color, #1890ff)`;
  }

  // 证书技能特殊样式
  if (skill.isCertified) {
    style.background = 'linear-gradient(90deg, #fffaf0 0%, #ffffff 100%)';
  }

  return style;
};

// 技能名称样式
const skillNameStyle = computed(() => ({
  fontSize: '16px',
  fontWeight: 600,
  color: '#333',
  flex: 1,
  wordBreak: 'break-word'
}));

// 等级标签样式
const levelTagStyle = computed(() => ({
  display: 'flex',
  alignItems: 'center',
  gap: '8px',
  background: '#f6f6f6',
  borderRadius: '16px',
  padding: '4px 12px',
  border: '1px solid #e8e8e8',
  flexShrink: 0
}));

const levelTextStyle = computed(() => ({
  fontSize: '13px',
  fontWeight: 500,
  color: '#666'
}));

// 分类徽章样式
const categoryBadgeStyle = computed(() => ({
  background: 'rgba(24, 144, 255, 0.1)',
  color: 'var(--base-primary-color, #1890ff)',
  border: '1px solid rgba(24, 144, 255, 0.2)',
  display: 'inline-block',
  whiteSpace: 'nowrap'
}));

// 公开/私有徽章样式
const publicBadgeStyle = computed(() => ({
  background: 'rgba(82, 196, 26, 0.1)',
  color: 'var(--base-accent-color, #52c41a)',
  border: '1px solid rgba(82, 196, 26, 0.2)',
  display: 'inline-block',
  whiteSpace: 'nowrap'
}));

const privateBadgeStyle = computed(() => ({
  background: 'rgba(255, 77, 79, 0.1)',
  color: '#ff4d4f',
  border: '1px solid rgba(255, 77, 79, 0.2)',
  display: 'inline-block',
  whiteSpace: 'nowrap'
}));

// 描述样式
const descriptionStyle = computed(() => ({
  fontSize: '14px',
  color: '#555',
  lineHeight: '1.6',
  marginBottom: '12px',
  padding: '8px',
  background: '#fafafa',
  borderRadius: '6px',
  borderLeft: '3px solid #e8e8e8',
  width: '94%',
  wordBreak: 'break-word'
}));

// 标签样式
const tagStyle = computed(() => ({
  backgroundColor: '#e6f7ff',
  border: '1px solid rgba(24, 144, 255, 0.2)',
  display: 'inline-block'
}));

const tagTextStyle = computed(() => ({
  fontSize: '11px',
  fontWeight: 500,
  color: '#333',
  wordBreak: 'break-word'
}));

// 详细标签样式
const labelTextStyle = computed(() => ({
  fontSize: '13px',
  color: '#666',
  fontWeight: 500
}));

// 经验填充样式
const getExperienceFillStyle = (years: number) => {
  const maxYears = 10;
  const width = Math.min((years / maxYears) * 100, 100);
  
  return {
    width: `${width}%`,
    background: 'linear-gradient(90deg, var(--base-primary-color, #1890ff), var(--base-accent-color, #52c41a))',
    minWidth: '20px'
  };
};

const yearsTextStyle = computed(() => ({
  fontSize: '13px',
  color: '#333',
  fontWeight: 500,
  minWidth: '50px',
  textAlign: 'right',
  flexShrink: 0
}));

// 进度条填充样式
const getProgressFillStyle = (percent: number) => {
  const color = getProgressColor(percent);
  return {
    width: `${percent}%`,
    background: color,
    minWidth: '5px'
  };
};

const progressTextStyle = computed(() => ({
  fontSize: '13px',
  color: '#333',
  fontWeight: 600,
  minWidth: '35px',
  textAlign: 'right',
  flexShrink: 0
}));

// 证书样式
const certNameStyle = computed(() => ({
  fontSize: '14px',
  color: '#333',
  fontWeight: 500,
  wordBreak: 'break-word'
}));

const certDateStyle = computed(() => ({
  fontSize: '12px',
  color: '#999'
}));
</script>

<style scoped lang="scss">
.skills-component {
  width: 100%;
}

.category-header {
  margin-bottom: 16px;
  width: 100%;
}

.category-title-line {
  display: flex;
  align-items: baseline;
  margin-bottom: 4px;
  flex-wrap: wrap;
  gap: 4px;
}

.skills-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
}

.skill-item {
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  &.has-certification {
    border-left: 3px solid #faad14;
    background: linear-gradient(90deg, #fffaf0 0%, #ffffff 100%);
  }

  &.is-public {
    border-top: 2px solid #e6f7ff;
  }
}

.skill-main-info {
  margin-bottom: 12px;
  width: 100%;
}

.skill-header {
  margin-bottom: 12px;
  width: 100%;
}

.skill-name-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  flex-wrap: wrap;
  gap: 8px;
  width: 100%;
}

.level-percent {
  font-size: 12px;
  color: var(--base-primary-color, #1890ff);
  font-weight: 600;
}

.skill-meta {
  display: flex;
  gap: 8px;
  margin-top: 4px;
  flex-wrap: wrap;
}

.meta-item {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 500;
  display: inline-block;
}

.category-badge {
  background: rgba(24, 144, 255, 0.1);
  color: var(--base-primary-color, #1890ff);
  border: 1px solid rgba(24, 144, 255, 0.2);
}

.public-badge {
  background: rgba(82, 196, 26, 0.1);
  color: var(--base-accent-color, #52c41a);
  border: 1px solid rgba(82, 196, 26, 0.2);
}

.private-badge {
  background: rgba(255, 77, 79, 0.1);
  color: #ff4d4f;
  border: 1px solid rgba(255, 77, 79, 0.2);
}

.skill-tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 8px;
  width: 100%;
}

.tag-item {
  font-size: 12px;
  padding: 2px 10px;
  border-radius: 12px;
  transition: all 0.2s ease;
  display: inline-block;
  background-color: var(--base-tag-bg, #e6f7ff);
  border: 1px solid rgba(24, 144, 255, 0.2);

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
}

.tag-text {
  font-size: 11px;
  font-weight: 500;
  color: #333;
  word-break: break-word;
}

.skill-details {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 12px;
  border-top: 1px dashed var(--base-secondary-color, #e8e8e8);
  width: 100%;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.detail-label {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 60px;
  flex-shrink: 0;
}

.detail-icon {
  font-size: 14px;
}

.detail-value {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

// 经验条样式
.experience-bar {
  flex: 1;
  height: 6px;
  background: #f0f0f0;
  border-radius: 3px;
  overflow: hidden;
  position: relative;
  min-width: 60px;
}

.experience-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 1s ease-in-out;
}

// 进度条样式
.progress-bar {
  flex: 1;
  height: 8px;
  background: #f5f5f5;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
  min-width: 60px;
}

.progress-fill {
  height: 100%;
  border-radius: 4px;
  transition: all 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  z-index: 1;
}

.progress-marks {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
}

.progress-mark {
  position: absolute;
  width: 1px;
  height: 100%;
  background: rgba(255, 255, 255, 0.5);
}

.certification-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  min-width: 0;
}

// 响应式调整 - 修复移动端居中问题
@media (max-width: 768px) {
  .skills-component {
    gap: 16px;
    margin: 0 auto;
    max-width: 100%;
  }

  .category-group {
    padding-left: 12px;
    margin-bottom: 20px;
    width: 100%;
  }

  .skill-item {
    padding: 12px;
    width: 100%;
    max-width: 100%;
  }

  .skill-name-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .skill-level-tag {
    align-self: flex-start;
  }

  .detail-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }

  .detail-label {
    width: 100%;
  }

  .detail-value {
    width: 100%;
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .experience-bar,
  .progress-bar {
    width: 100%;
  }

  .years-text,
  .progress-text {
    text-align: left;
  }

  // 标签容器优化
  .skill-tags-container {
    gap: 4px;
  }

  .tag-item {
    font-size: 11px;
    padding: 2px 8px;
  }

  // 元信息优化
  .skill-meta {
    gap: 4px;
  }

  .meta-item {
    font-size: 11px;
    padding: 2px 6px;
  }
}

// 小屏幕手机
@media (max-width: 480px) {
  .skills-component {
    padding: 0;
  }

  .category-group {
    padding-left: 8px;
  }

  .skill-item {
    padding: 10px;
  }

  .category-title {
    font-size: 16px !important;
  }

  .category-count {
    font-size: 12px !important;
  }

  .skill-name {
    font-size: 15px !important;
  }

  .skill-description {
    font-size: 13px !important;
    padding: 6px !important;
  }
}

// 平板设备
@media (min-width: 769px) and (max-width: 1024px) {
  .skill-item {
    padding: 14px;
  }
}

// 打印样式
@media print {
  .skills-component {
    border: none !important;
    box-shadow: none !important;
    background: white !important;
    padding: 0 !important;
  }

  .skill-item {
    border: 1px solid #ddd !important;
    box-shadow: none !important;
    break-inside: avoid;
    page-break-inside: avoid;
  }

  .progress-fill,
  .experience-fill {
    transition: none !important;
  }
}
</style>