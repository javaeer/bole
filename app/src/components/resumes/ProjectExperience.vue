<template>
  <BaseComponent
    :title="componentName"
    :component-data="componentData"
    :global-style="globalStyle"
    :override-styles="customStyles"
    :responsive-center="true"
    :max-width="maxWidth" 
  >
    <template #default="{ styles }">
      <view
        class="project-experience-container"
        :style="getContainerStyle(styles)"
      >
        <view
          v-for="project in sortedProjects"
          :key="project.id || project.projectName"
          class="project-item"
          :class="{
            'current-project': project.isCurrent,
            'has-achievements': project.achievements?.length > 0
          }"
          :style="getProjectItemStyle(styles)"
        >
          <!-- 项目头部 -->
          <view class="project-header" :style="getHeaderStyle(styles)">
            <view class="project-title-section">
              <text
                class="project-name"
                :style="getProjectNameStyle(styles)"
              >
                {{ project.projectName || project.name || '未命名项目' }}
              </text>

              <!-- 项目角色 -->
              <view v-if="showRole && (project.role || project.position)" class="project-role">
                <text class="role-text" :style="getRoleStyle(styles)">
                  {{ project.role || project.position }}
                </text>
              </view>
            </view>

            <!-- 项目时间 -->
            <view v-if="showTime && (project.startDate || project.endDate)" class="project-time">
              <text :style="getTimeStyle(styles)">
                {{ formatDate(project.startDate) }} - {{ project.isCurrent ? '至今' : formatDate(project.endDate) }}
              </text>
            </view>
          </view>

          <!-- 项目描述 -->
          <view v-if="project.description" class="project-description">
            <text :style="getDescriptionStyle(styles)">
              {{ project.description }}
            </text>
          </view>

          <!-- 项目链接 -->
          <view
            v-if="(project.link || project.url) && showLinks"
            class="project-link"
            :style="getLinkContainerStyle(styles)"
          >
            <text class="link-icon" :style="getLinkIconStyle(styles)">🔗</text>
            <text class="link-text" :style="getLinkTextStyle(styles)">
              {{ project.link || project.url }}
            </text>
          </view>

          <!-- 职责描述 -->
          <view
            v-if="project.responsibilities && project.responsibilities.length > 0"
            class="project-responsibilities"
            :style="getSectionStyle(styles, 'responsibilities')"
          >
            <text class="responsibilities-title" :style="getSectionTitleStyle(styles)">
              主要职责：
            </text>
            <view class="responsibilities-list">
              <view
                v-for="(responsibility, index) in project.responsibilities"
                :key="index"
                class="responsibility-item"
                :style="getListItemStyle(styles, 'responsibility')"
              >
                <text class="responsibility-marker" :style="getMarkerStyle(styles)">•</text>
                <text class="responsibility-text" :style="getListItemTextStyle(styles)">
                  {{ responsibility }}
                </text>
              </view>
            </view>
          </view>

          <!-- 项目成就 -->
          <view
            v-if="showAchievements && project.achievements && project.achievements.length > 0"
            class="project-achievements"
            :style="getSectionStyle(styles, 'achievements')"
          >
            <text class="achievements-title" :style="getSectionTitleStyle(styles)">
              项目成果：
            </text>
            <view class="achievements-list">
              <view
                v-for="(achievement, index) in project.achievements"
                :key="index"
                class="achievement-item"
                :style="getListItemStyle(styles, 'achievement')"
              >
                <text class="achievement-marker" :style="getAchievementMarkerStyle(styles)">✓</text>
                <text class="achievement-text" :style="getListItemTextStyle(styles)">
                  {{ achievement }}
                </text>
              </view>
            </view>
          </view>

          <!-- 技术栈 -->
          <view
            v-if="showTechnologies && project.technologies && project.technologies.length > 0"
            class="project-technologies"
            :style="getSectionStyle(styles, 'technologies')"
          >
            <text class="tech-title" :style="getSectionTitleStyle(styles)">
              技术栈：
            </text>
            <view class="tech-tags">
              <view
                v-for="(tech, index) in project.technologies"
                :key="index"
                class="tech-tag"
                :style="getTechTagStyle(styles, tech)"
              >
                {{ tech }}
              </view>
            </view>
          </view>

          <!-- 项目数据（如果有） -->
          <view
            v-if="showMetrics && project.metrics"
            class="project-metrics"
            :style="getMetricsStyle(styles)"
          >
            <view
              v-for="(value, key) in project.metrics"
              :key="key"
              class="metric-item"
            >
              <text class="metric-value" :style="getMetricValueStyle(styles)">
                {{ value }}
              </text>
              <text class="metric-label" :style="getMetricLabelStyle(styles)">
                {{ key }}
              </text>
            </view>
          </view>
        </view>

        <!-- 空状态 -->
        <view
          v-if="projects.length === 0"
          class="empty-state"
          :style="getEmptyStateStyle(styles)"
        >
          <text class="empty-icon">📁</text>
          <text class="empty-text">暂无项目经验</text>
          <text class="empty-hint">添加您的项目经验以展示您的能力</text>
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

// ===================== 计算属性 =====================

// 组件数据
const componentData = computed(() => props.componentData || {});
const componentProps = computed(() => componentData.value.props || {});
const defaultConfig = computed(() => componentData.value.defaultConfig || {});

const componentName = computed(() =>
  componentData.value.name || defaultConfig.value.props?.title || '项目经验'
);

// 最大宽度 - 根据布局类型自适应
const maxWidth = computed(() => {
  // 可以从 globalStyle 或布局配置中获取
  return props.globalStyle?.maxWidth || "100%";
});

// 项目经验数据
const projects = computed(() =>
  componentProps.value.experiences || []
);

// 显示控制 - 从defaultConfig.props中获取配置
const showTime = computed(() =>
  componentProps.value.showTime ?? defaultConfig.value.props?.showTime ?? true
);

const showRole = computed(() =>
  componentProps.value.showRole ?? defaultConfig.value.props?.showRole ?? true
);

const showTechnologies = computed(() =>
  componentProps.value.showTechnologies ?? defaultConfig.value.props?.showTechnologies ?? true
);

const showAchievements = computed(() =>
  componentProps.value.showAchievements ?? defaultConfig.value.props?.showAchievements ?? true
);

const showLinks = computed(() =>
  componentProps.value.showLinks ?? true
);

const showMetrics = computed(() =>
  componentProps.value.showMetrics ?? false
);

// 排序方式
const orderBy = computed(() =>
  componentProps.value.orderBy || defaultConfig.value.props?.orderBy || 'startDate'
);

const orderDirection = computed(() =>
  componentProps.value.orderDirection || defaultConfig.value.props?.orderDirection || 'desc'
);

// 按指定方式排序
const sortedProjects = computed(() => {
  const items = [...projects.value];

  if (orderBy.value === 'startDate') {
    items.sort((a, b) => {
      const dateA = a.startDate ? new Date(a.startDate).getTime() : 0;
      const dateB = b.startDate ? new Date(b.startDate).getTime() : 0;
      return orderDirection.value === 'desc' ? dateB - dateA : dateA - dateB;
    });
  } else if (orderBy.value === 'importance') {
    // 按重要性排序（如果有importance字段）
    items.sort((a, b) => {
      const importanceA = a.importance || 0;
      const importanceB = b.importance || 0;
      return orderDirection.value === 'desc' ? importanceB - importanceA : importanceA - importanceB;
    });
  }

  return items;
});

// ===================== 工具函数 =====================

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return '';
  try {
    const date = new Date(dateStr);
    return `${date.getFullYear()}.${(date.getMonth() + 1).toString().padStart(2, '0')}`;
  } catch (e) {
    return dateStr;
  }
};

// ========== 样式计算方法 ==========

const getContainerStyle = (styles: any) => {
  if (!styles) return {};

  return {
    display: 'flex',
    flexDirection: 'column',
    gap: styles.itemSpacing || '24px',
    fontFamily: styles.fontFamily,
    fontSize: styles.bodySize,
    lineHeight: styles.lineHeight,
    width: '100%',
    boxSizing: 'border-box',
  };
};

const getProjectItemStyle = (styles: any) => {
  if (!styles) return {};

  const defaultStyle = {
    background: 'linear-gradient(135deg, #ffffff 0%, #f8fafc 100%)',
    border: styles.border || '1px solid #e9ecef',
    borderRadius: styles.borderRadius || '10px',
    padding: styles.padding || '20px',
    boxShadow: styles.boxShadow || '0 2px 8px rgba(0,0,0,0.05)',
    transition: 'all 0.3s ease',
    position: 'relative',
    overflow: 'hidden',
    width: '94%',
  };

  return defaultStyle;
};

const getHeaderStyle = (styles: any) => {
  if (!styles) return {};

  return {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: '12px',
    flexWrap: 'wrap',
    gap: '8px',
  };
};

const getProjectNameStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '17px',
    fontWeight: 'bold',
    color: 'var(--base-title-color, #1890ff)',
    lineHeight: '1.3',
    marginBottom: '6px',
    display: 'block',
    width: '100%',
  };
};

const getRoleStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '13px',
    color: '#fff',
    background: `linear-gradient(135deg, var(--base-accent-color, #5ac8fa), var(--base-primary-color, #1890ff))`,
    padding: '3px 10px',
    borderRadius: '12px',
    fontWeight: '500',
    display: 'inline-block',
  };
};

const getTimeStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '13px',
    color: styles.periodColor || '#666',
    background: styles.periodBackground || '#f8f9fa',
    padding: '3px 10px',
    borderRadius: '12px',
    whiteSpace: 'nowrap',
    flexShrink: 0,
  };
};

const getDescriptionStyle = (styles: any) => {
  if (!styles) return {};

  return {
    color: 'var(--base-text-color, #555)',
    fontSize: styles.bodySize || '14px',
    lineHeight: styles.lineHeight || '1.6',
    marginBottom: '16px',
    paddingBottom: '12px',
    borderBottom: '1px dashed var(--base-secondary-color, #eee)',
    width: '100%',
  };
};

const getLinkContainerStyle = (styles: any) => {
  if (!styles) return {};

  return {
    display: 'flex',
    alignItems: 'center',
    gap: '6px',
    marginBottom: '12px',
    padding: '8px 12px',
    background: styles.linkBackground || 'var(--base-tag-bg, #f0f7ff)',
    borderRadius: '6px',
    borderLeft: `3px solid var(--base-accent-color, #5ac8fa)`,
    width: '100%',
    boxSizing: 'border-box',
  };
};

const getLinkIconStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '14px',
    flexShrink: 0,
  };
};

const getLinkTextStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '13px',
    color: styles.linkColor || 'var(--base-primary-color, #1890ff)',
    flex: 1,
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    whiteSpace: 'nowrap',
  };
};

const getSectionStyle = (styles: any, sectionType: string) => {
  if (!styles) return {};

  const baseStyle = {
    marginBottom: '16px',
    width: '100%',
  };

  if (sectionType === 'achievements') {
    baseStyle.marginTop = '16px';
    baseStyle.paddingTop = '16px';
    baseStyle.borderTop = '1px dashed var(--base-secondary-color, #eee)';
  }

  return baseStyle;
};

const getSectionTitleStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '14px',
    color: 'var(--base-title-color, #333)',
    fontWeight: '500',
    display: 'block',
    marginBottom: '8px',
    width: '100%',
  };
};

const getListItemStyle = (styles: any, type: string) => {
  if (!styles) return {};

  const baseStyle: any = {
    display: 'flex',
    alignItems: 'flex-start',
    gap: '8px',
    width: '100%',
  };

  if (type === 'achievement') {
    baseStyle.padding = '6px 10px';
    baseStyle.background = `linear-gradient(135deg, var(--base-highlight-bg, #f0fff4), var(--base-accent-color, #e6fff7)20)`;
    baseStyle.borderRadius = '6px';
    baseStyle.borderLeft = `3px solid var(--base-accent-color, #52c41a)`;
    baseStyle.marginBottom = '6px';
  }

  return baseStyle;
};

const getListItemTextStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '13px',
    color: 'var(--base-text-color, #666)',
    lineHeight: '1.5',
    flex: 1,
  };
};

const getMarkerStyle = (styles: any) => {
  if (!styles) return {};

  return {
    color: 'var(--base-accent-color, #5ac8fa)',
    fontSize: '14px',
    marginTop: '2px',
    flexShrink: 0,
  };
};

const getAchievementMarkerStyle = (styles: any) => {
  if (!styles) return {};

  return {
    color: 'var(--base-accent-color, #52c41a)',
    fontSize: '14px',
    marginTop: '1px',
    flexShrink: 0,
  };
};

const getTechTagStyle = (styles: any, tech: string) => {
  if (!styles) return {};

  // 为不同的技术类型设置不同的颜色
  const techColors: Record<string, { background: string, color: string }> = {
    // 前端技术
    'Vue': { background: '#e6f7ff', color: '#5ac8fa' },
    'React': { background: '#f0f7ff', color: '#61dafb' },
    'TypeScript': { background: '#f0f7ff', color: '#3178c6' },
    'JavaScript': { background: '#fff7e6', color: '#f0db4f' },
    // 后端技术
    'Java': { background: '#fff1f0', color: '#e34c26' },
    'Spring': { background: '#f6ffed', color: '#6db33f' },
    'Node.js': { background: '#f6ffed', color: '#68a063' },
    // 数据库
    'MySQL': { background: '#f0f7ff', color: '#00758f' },
    'MongoDB': { background: '#f6ffed', color: '#47a248' },
    // 默认使用CSS变量
    'default': {
      background: 'var(--base-tag-bg, #e6f7ff)',
      color: 'var(--base-accent-color, #5ac8fa)'
    }
  };

  const techKey = Object.keys(techColors).find(key =>
    tech.toLowerCase().includes(key.toLowerCase())
  ) || 'default';

  const { background, color } = techColors[techKey];

  return {
    fontSize: '12px',
    color: color,
    background: background,
    padding: '4px 10px',
    borderRadius: '15px',
    border: `1px solid ${color}30`,
    transition: 'all 0.2s ease',
    display: 'inline-block',
  };
};

const getMetricsStyle = (styles: any) => {
  if (!styles) return {};

  return {
    display: 'flex',
    justifyContent: 'space-around',
    marginTop: '16px',
    paddingTop: '16px',
    borderTop: '1px solid var(--base-secondary-color, #eee)',
    textAlign: 'center',
    width: '100%',
    flexWrap: 'wrap',
    gap: '12px',
  };
};

const getMetricValueStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '20px',
    color: 'var(--base-primary-color, #1890ff)',
    fontWeight: 'bold',
    display: 'block',
    marginBottom: '4px',
  };
};

const getMetricLabelStyle = (styles: any) => {
  if (!styles) return {};

  return {
    fontSize: '12px',
    color: 'var(--base-text-color, #666)',
    display: 'block',
  };
};

const getEmptyStateStyle = (styles: any) => {
  if (!styles) return {};

  return {
    textAlign: 'center',
    padding: '60px 20px',
    color: 'var(--base-secondary-color, #999)',
    background: 'repeating-linear-gradient(45deg, #fafafa, #fafafa 10px, #f0f0f0 10px, #f0f0f0 20px)',
    borderRadius: '8px',
    border: '2px dashed var(--base-secondary-color, #ddd)',
    width: '100%',
    boxSizing: 'border-box',
  };
};
</script>

<style scoped lang="scss">
.project-experience-container {
  position: relative;
  width: 100%;
  box-sizing: border-box;
}

.project-item {
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }
}

.current-project {
  position: relative;

  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 4px;
    background: linear-gradient(to bottom, var(--base-accent-color, #52c41a), var(--base-primary-color, #73d13d));
    border-radius: 2px 0 0 2px;
  }
}

.project-header {
  position: relative;
  width: 100%;
  box-sizing: border-box;
}

.project-title-section {
  flex: 1;
  min-width: 0;
}

.project-role {
  display: inline-block;
  margin-top: 4px;
}

.role-text {
  white-space: nowrap;
}

.project-time {
  flex-shrink: 0;
}

.project-link {
  &:hover {
    .link-text {
      text-decoration: underline;
    }
  }
}

.project-description {
  text-align: justify;
  width: 100%;
  word-break: break-word;
}

.project-responsibilities,
.project-achievements,
.project-technologies {
  .section-title {
    display: flex;
    align-items: center;

    &::before {
      content: '';
      display: inline-block;
      width: 4px;
      height: 16px;
      background: currentColor;
      margin-right: 8px;
      border-radius: 2px;
    }
  }
}

.responsibilities-list,
.achievements-list {
  .list-item {
    &:hover {
      background: rgba(var(--base-primary-color-rgb, 90, 200, 250), 0.05);
      border-radius: 4px;
      padding-left: 8px;
    }
  }
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  width: 100%;
}

.tech-tag {
  cursor: default;
  user-select: none;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 3px 6px rgba(0, 0, 0, 0.1);
  }
}

.project-metrics {
  .metric-item {
    flex: 1;
    min-width: 60px;

    &:hover {
      .metric-value {
        transform: scale(1.1);
        transition: transform 0.3s ease;
      }
    }
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  width: 100%;
  box-sizing: border-box;

  .empty-icon {
    font-size: 48px;
    opacity: 0.5;
  }

  .empty-text {
    font-size: 16px;
    font-weight: 500;
    color: var(--base-text-color, #333);
  }

  .empty-hint {
    font-size: 12px;
    color: var(--base-secondary-color, #ccc);
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .project-experience-container {
    padding: 0;
  }

  .project-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .project-time {
    align-self: flex-start;
    margin-top: 4px;
  }

  .project-item {
    padding: 16px !important;
    border-radius: 8px !important;
  }

  .project-name {
    font-size: 16px !important;
  }

  .tech-tags {
    gap: 6px;
  }

  .tech-tag {
    font-size: 11px;
    padding: 3px 8px;
  }

  .project-metrics {
    flex-direction: column;
    gap: 16px;
  }

  .metric-item {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .responsibility-item,
  .achievement-item {
    padding: 4px 0;
  }
}

/* 平板设备 */
@media (min-width: 769px) and (max-width: 1024px) {
  .project-item {
    padding: 18px !important;
  }

  .tech-tags {
    gap: 8px;
  }
}

/* 小屏幕手机 */
@media (max-width: 480px) {
  .project-item {
    padding: 14px !important;
  }

  .project-name {
    font-size: 15px !important;
  }

  .tech-tags {
    gap: 4px;
  }

  .tech-tag {
    font-size: 10px !important;
    padding: 2px 6px !important;
  }

  .role-text,
  .project-time {
    font-size: 12px !important;
  }
}

/* 打印优化 */
@media print {
  .project-experience-container {
    break-inside: avoid;
  }

  .project-item {
    break-inside: avoid;
    box-shadow: none !important;
    border: 1px solid #ddd !important;
    background: white !important;
    padding: 12px !important;
  }

  .tech-tag {
    border: 1px solid #333 !important;
    background: white !important;
    color: #333 !important;
  }

  .project-header {
    flex-direction: column !important;
    align-items: flex-start !important;
  }
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .project-item {
    background: linear-gradient(135deg, #2a2a2a 0%, #1e1e1e 100%);
    border-color: #424242;
  }

  .project-link {
    background: #2d3748 !important;
    border-left-color: var(--base-accent-color, #5ac8fa) !important;
  }

  .tech-tag {
    background: #374151 !important;
    color: #e5e7eb !important;
    border-color: #4b5563 !important;
  }

  .empty-state {
    background: #374151;
    border-color: #4b5563;
    color: #9ca3af;
  }
}
</style>