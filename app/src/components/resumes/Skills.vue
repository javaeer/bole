<!-- components/resumes/Skills.vue -->
<template>
  <BaseComponent
    :component-data="componentData"
    :global-style="globalStyle"
    :custom-styles="customStyles"
  >
    <template #default="{ styles }">
      <view class="skills-component" :style="getContainerStyle(styles)">
        <!-- 按分类分组显示 -->
        <template v-if="groupByCategory && Object.keys(groupedSkills).length > 0">
          <view
            v-for="(skills, category) in groupedSkills"
            :key="category"
            class="category-group"
            :style="getCategoryStyle(styles, category)"
          >
            <!-- 分类标题 -->
            <view class="category-header">
              <view class="category-title-line">
                <text class="category-title" :style="getCategoryTitleStyle(styles)">
                  {{ getCategoryDisplayName(category) }}
                </text>
                <text class="category-count" :style="getCategoryCountStyle(styles)">
                  （{{ skills.length }}项）
                </text>
              </view>
              <view
                v-if="showCategoryDescription"
                class="category-description"
                :style="getCategoryDescriptionStyle(styles)"
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
                :style="getSkillItemStyle(styles, skill)"
              >
                <!-- 技能基本信息 -->
                <view class="skill-main-info">
                  <!-- 技能名称和等级 -->
                  <view class="skill-header">
                    <view class="skill-name-row">
                      <text class="skill-name" :style="getSkillNameStyle(styles)">
                        {{ skill.name }}
                      </text>
                      <view v-if="skill.level" class="skill-level-tag" :style="getLevelTagStyle(styles)">
                        <text class="level-text" :style="getLevelTextStyle(styles)">
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
                        :style="getCategoryBadgeStyle(styles)"
                      >
                        {{ skill.category }}
                      </text>
                      <text
                        v-if="skill.isPublic"
                        class="meta-item public-badge"
                        :style="getPublicBadgeStyle(styles)"
                      >
                        公开
                      </text>
                      <text
                        v-if="!skill.isPublic"
                        class="meta-item private-badge"
                        :style="getPrivateBadgeStyle(styles)"
                      >
                        私有
                      </text>
                    </view>
                  </view>

                  <!-- 技能描述 -->
                  <view
                    v-if="skill.description"
                    class="skill-description"
                    :style="getDescriptionStyle(styles)"
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
                      :style="getTagStyle(styles, tag)"
                    >
                      <text class="tag-text" :style="getTagTextStyle(styles)">
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
                      <text class="label-text" :style="getLabelTextStyle(styles)">经验</text>
                    </view>
                    <view class="detail-value">
                      <view class="experience-bar">
                        <view
                          class="experience-fill"
                          :style="getExperienceFillStyle(styles, skill.experienceYears)"
                        ></view>
                      </view>
                      <text class="years-text" :style="getYearsTextStyle(styles)">
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
                      <text class="label-text" :style="getLabelTextStyle(styles)">熟练度</text>
                    </view>
                    <view class="detail-value">
                      <view class="progress-bar">
                        <view
                          class="progress-fill"
                          :style="getProgressFillStyle(styles, skill.proficiencyPercent)"
                        ></view>
                        <view class="progress-marks">
                          <view class="progress-mark" style="left: 25%"></view>
                          <view class="progress-mark" style="left: 50%"></view>
                          <view class="progress-mark" style="left: 75%"></view>
                        </view>
                      </view>
                      <text class="progress-text" :style="getProgressTextStyle(styles)">
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
                      <text class="label-text" :style="getLabelTextStyle(styles)">证书</text>
                    </view>
                    <view class="detail-value certification-details">
                      <text class="cert-name" :style="getCertNameStyle(styles)">
                        {{ skill.certificateName }}
                      </text>
                      <text
                        v-if="skill.certificateDate"
                        class="cert-date"
                        :style="getCertDateStyle(styles)"
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
              :style="getSkillItemStyle(styles, skill)"
            >
              <!-- 技能基本信息 -->
              <view class="skill-main-info">
                <!-- 技能名称和等级 -->
                <view class="skill-header">
                  <view class="skill-name-row">
                    <text class="skill-name" :style="getSkillNameStyle(styles)">
                      {{ skill.name }}
                    </text>
                    <view v-if="skill.level" class="skill-level-tag" :style="getLevelTagStyle(styles)">
                      <text class="level-text" :style="getLevelTextStyle(styles)">
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
                      :style="getCategoryBadgeStyle(styles)"
                    >
                      {{ skill.category }}
                    </text>
                    <text
                      v-if="skill.isPublic"
                      class="meta-item public-badge"
                      :style="getPublicBadgeStyle(styles)"
                    >
                      公开
                    </text>
                    <text
                      v-if="!skill.isPublic"
                      class="meta-item private-badge"
                      :style="getPrivateBadgeStyle(styles)"
                    >
                      私有
                    </text>
                  </view>
                </view>

                <!-- 技能描述 -->
                <view
                  v-if="skill.description"
                  class="skill-description"
                  :style="getDescriptionStyle(styles)"
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
                    :style="getTagStyle(styles, tag)"
                  >
                    <text class="tag-text" :style="getTagTextStyle(styles)">
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
                    <text class="label-text" :style="getLabelTextStyle(styles)">经验</text>
                  </view>
                  <view class="detail-value">
                    <view class="experience-bar">
                      <view
                        class="experience-fill"
                        :style="getExperienceFillStyle(styles, skill.experienceYears)"
                      ></view>
                    </view>
                    <text class="years-text" :style="getYearsTextStyle(styles)">
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
                    <text class="label-text" :style="getLabelTextStyle(styles)">熟练度</text>
                  </view>
                  <view class="detail-value">
                    <view class="progress-bar">
                      <view
                        class="progress-fill"
                        :style="getProgressFillStyle(styles, skill.proficiencyPercent)"
                      ></view>
                      <view class="progress-marks">
                        <view class="progress-mark" style="left: 25%"></view>
                        <view class="progress-mark" style="left: 50%"></view>
                        <view class="progress-mark" style="left: 75%"></view>
                      </view>
                    </view>
                    <text class="progress-text" :style="getProgressTextStyle(styles)">
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
                    <text class="label-text" :style="getLabelTextStyle(styles)">证书</text>
                  </view>
                  <view class="detail-value certification-details">
                    <text class="cert-name" :style="getCertNameStyle(styles)">
                      {{ skill.certificateName }}
                    </text>
                    <text
                      v-if="skill.certificateDate"
                      class="cert-date"
                      :style="getCertDateStyle(styles)"
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

// 从defaultConfig获取默认配置
const defaultConfig = computed(() => props.componentData?.defaultConfig || {});

// 技能数据
const skills = computed(() => props.componentData.props?.skills || []);

// 显示控制 - 从props或defaultConfig中获取
const showSkillLevel = computed(() => {
  const propValue = props.componentData.props?.skillLevel;
  const defaultValue = defaultConfig.value.props?.showSkillLevel;
  return propValue !== undefined ? propValue : (defaultValue !== false);
});

const showTags = computed(() => {
  const propValue = props.componentData.props?.showTags;
  const defaultValue = defaultConfig.value.props?.showSkillLevel;
  return propValue !== undefined ? propValue : (defaultValue !== false);
});

const groupByCategory = computed(() => {
  const propValue = props.componentData.props?.groupByCategory;
  const defaultValue = defaultConfig.value.props?.groupByCategory;
  return propValue !== undefined ? propValue : (defaultValue !== false);
});

const showCategory = computed(() => true);
const showExperienceYears = computed(() => defaultConfig.value.props?.showExperienceYears || true);
const showCertification = computed(() => true);
const showCategoryDescription = computed(() => false);

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
  const defaultCategories = defaultConfig.value.props?.skillCategories ||
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

// ========== 样式函数 ==========
const getContainerStyle = (styles: any) => {
  if (!styles) return {};

  return {
    width: "100%",
    maxWidth: "100%",
    fontFamily: styles.fontFamily,
    fontSize: styles.bodySize,
    lineHeight: styles.lineHeight,
    color: styles.textColor,
    padding: styles.padding,
    margin: styles.margin,
    backgroundColor: styles.backgroundColor,
    borderRadius: styles.borderRadius,
    border: styles.border,
    boxShadow: styles.boxShadow,
    boxSizing: "border-box"
  };
};

const getCategoryStyle = (styles: any, category: string) => {
  if (!styles) return {};

  const config = categoryConfig.value[category];
  const borderColor = config?.color || styles.primaryColor;

  return {
    borderLeft: `4px solid ${borderColor}`,
    paddingLeft: "16px",
    marginBottom: "24px",
    width: "100%",
    '--category-color': borderColor
  };
};

const getCategoryTitleStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: "18px",
    fontWeight: 600,
    color: 'var(--category-color)',
    marginRight: "8px",
    display: "inline-block"
  };
};

const getCategoryCountStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: "14px",
    color: "#666",
    opacity: 0.8,
    display: "inline-block"
  };
};

const getCategoryDescriptionStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: "13px",
    color: "#666",
    lineHeight: "1.5",
    paddingLeft: "4px",
    borderLeft: "2px solid rgba(0, 0, 0, 0.1)",
    marginLeft: "4px",
    width: "100%"
  };
};

const getSkillItemStyle = (styles: any, skill: any) => {
  if (!styles) return {};

  const baseStyle = {
    width: "100%",
    maxWidth: "100%",
    padding: "16px",
    background: "#ffffff",
    borderRadius: "8px",
    border: "1px solid #f0f0f0",
    boxShadow: "0 2px 8px rgba(0, 0, 0, 0.05)",
    transition: "all 0.3s ease",
    boxSizing: "border-box"
  };

  // 根据熟练度设置边框色
  if (skill.proficiencyPercent) {
    const color = getProgressColor(skill.proficiencyPercent);
    baseStyle.borderLeft = `3px solid ${color}`;
  } else {
    baseStyle.borderLeft = `3px solid ${styles.primaryColor}`;
  }

  // 证书技能特殊样式
  if (skill.isCertified) {
    baseStyle.background = "linear-gradient(90deg, #fffaf0 0%, #ffffff 100%)";
  }

  return baseStyle;
};

const getSkillNameStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: "16px",
    fontWeight: 600,
    color: "#333",
    flex: 1,
    wordBreak: "break-word"
  };
};

const getLevelTagStyle = (styles: any) => {
  if (!styles) return {};

  return {
    display: "flex",
    alignItems: "center",
    gap: "8px",
    background: "#f6f6f6",
    borderRadius: "16px",
    padding: "4px 12px",
    border: "1px solid #e8e8e8",
    flexShrink: 0
  };
};

const getLevelTextStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: "13px",
    fontWeight: 500,
    color: "#666"
  };
};

const getCategoryBadgeStyle = (styles: any) => {
  if (!styles) return {};

  return {
    background: "rgba(24, 144, 255, 0.1)",
    color: styles.primaryColor,
    border: "1px solid rgba(24, 144, 255, 0.2)",
    display: "inline-block",
    whiteSpace: "nowrap"
  };
};

const getPublicBadgeStyle = (styles: any) => {
  if (!styles) return {};

  return {
    background: "rgba(82, 196, 26, 0.1)",
    color: styles.accentColor,
    border: "1px solid rgba(82, 196, 26, 0.2)",
    display: "inline-block",
    whiteSpace: "nowrap"
  };
};

const getPrivateBadgeStyle = (styles: any) => {
  if (!styles) return {};

  return {
    background: "rgba(255, 77, 79, 0.1)",
    color: "#ff4d4f",
    border: "1px solid rgba(255, 77, 79, 0.2)",
    display: "inline-block",
    whiteSpace: "nowrap"
  };
};

const getDescriptionStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: "14px",
    color: "#555",
    lineHeight: "1.6",
    marginBottom: "12px",
    padding: "8px",
    background: "#fafafa",
    borderRadius: "6px",
    borderLeft: "3px solid #e8e8e8",
    width: "100%",
    wordBreak: "break-word"
  };
};

const getTagStyle = (styles: any, tag: string) => {
  if (!styles) return {};

  const colors = ["#e6f7ff", "#f6ffed", "#fff7e6", "#f9f0ff", "#fff0f6"];
  const index = tag.length % colors.length;

  return {
    backgroundColor: colors[index],
    border: `1px solid rgba(${index * 40}, ${index * 60}, ${index * 80}, 0.2)`,
    display: "inline-block"
  };
};

const getTagTextStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: "11px",
    fontWeight: 500,
    color: "#333",
    wordBreak: "break-word"
  };
};

const getLabelTextStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: "13px",
    color: "#666",
    fontWeight: 500
  };
};

const getExperienceFillStyle = (styles: any, years: number) => {
  if (!styles) return {};

  const maxYears = 10;
  const width = Math.min((years / maxYears) * 100, 100);

  return {
    width: `${width}%`,
    background: `linear-gradient(90deg, ${styles.primaryColor}, ${styles.accentColor})`,
    minWidth: "20px"
  };
};

const getYearsTextStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: "13px",
    color: "#333",
    fontWeight: 500,
    minWidth: "50px",
    textAlign: "right",
    flexShrink: 0
  };
};

const getProgressFillStyle = (styles: any, percent: number) => {
  if (!styles) return {};

  return {
    width: `${percent}%`,
    background: getProgressColor(percent),
    minWidth: "5px"
  };
};

const getProgressTextStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: "13px",
    color: "#333",
    fontWeight: 600,
    minWidth: "35px",
    textAlign: "right",
    flexShrink: 0
  };
};

const getCertNameStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: "14px",
    color: "#333",
    fontWeight: 500,
    wordBreak: "break-word"
  };
};

const getCertDateStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: "12px",
    color: "#999"
  };
};

// 获取技能排序
const getSortedSkills = (skillList: any[]) => {
  const maxSkills = defaultConfig.value.props?.maxSkillsPerCategory || 8;
  const sorted = [...skillList].sort((a, b) => (a.sort || 0) - (b.sort || 0));
  return sorted.slice(0, maxSkills);
};

// 按排序字段排序
const sortedSkills = computed(() => {
  const maxSkills = defaultConfig.value.props?.maxSkillsPerCategory || 8;
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
</script>

<style scoped lang="scss">
.skills-component {
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
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
  color: #1890ff;
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

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
}

.skill-details {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 12px;
  border-top: 1px dashed #e8e8e8;
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
  min-width: 0; /* 防止flex元素溢出 */
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
    padding: 12px !important;
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
    padding: 8px !important;
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
  .skills-component {
    padding: 16px !important;
  }

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