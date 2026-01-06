<!-- /pages/test/resumes.vue -->
<template>
  <view class="resume-test-page">
    <!-- 页面标题 -->
    <view class="page-header">
      <text class="page-title">简历样式切换测试</text>
      <text class="page-subtitle">测试动态切换主题、颜色和布局</text>
    </view>

    <!-- 样式切换器 -->
    <ThemeSwitcher
      :initial-config="currentConfig"
      @change="handleStyleChange"
    />

    <!-- 当前设置显示 -->
    <view class="current-settings">
      <text class="settings-title">当前设置</text>
      <view class="settings-grid">
        <view class="setting-item">
          <text class="setting-label">主题:</text>
          <text class="setting-value">{{ currentTheme || 'modern' }}</text>
        </view>
        <view class="setting-item">
          <text class="setting-label">主色:</text>
          <text class="setting-value">{{ getColorName(currentPrimaryColor) }}</text>
          <view class="color-preview" :style="{ backgroundColor: currentPrimaryColor }"></view>
        </view>
        <view class="setting-item">
          <text class="setting-label">布局:</text>
          <text class="setting-value">{{ currentLayoutType || 'single-column' }}</text>
        </view>
        <view class="setting-item">
          <text class="setting-label">字体:</text>
          <text class="setting-value">{{ currentFontSize || 'medium' }}</text>
        </view>
      </view>
    </view>

    <!-- 测试简历渲染 -->
    <view class="resume-test-area">
      <text class="test-title">实时预览</text>

      <!-- 模式切换 -->
      <view class="view-mode">
        <button
          v-for="mode in viewModes"
          :key="mode.id"
          :class="['mode-btn', { active: currentViewMode === mode.id }]"
          @click="switchViewMode(mode.id)"
        >
          {{ mode.name }}
        </button>
      </view>

      <!-- 简历预览区域 -->
      <view
        class="resume-preview-container"
        :class="[
          `theme-${currentTheme}`,
          `layout-${currentLayoutType}`,
          `font-${currentFontSize}`
        ]"
        :style="containerStyle"
      >
        <!-- 组件模式 -->
        <template v-if="currentViewMode === 'components'">
          <DynamicResumeRenderer
            :resume-data="testResumeData"
            :key="renderKey"
          />
        </template>

        <!-- 对比模式 -->
        <template v-else-if="currentViewMode === 'compare'">
          <view class="compare-container">
            <view class="compare-left">
              <text class="compare-title">现代风格</text>
              <DynamicResumeRenderer
                :resume-data="modernResumeData"
                class="compare-resume"
              />
            </view>
            <view class="compare-right">
              <text class="compare-title">当前风格</text>
              <DynamicResumeRenderer
                :resume-data="testResumeData"
                class="compare-resume"
                :key="renderKey"
              />
            </view>
          </view>
        </template>

        <!-- 单个组件测试 -->
        <template v-else-if="currentViewMode === 'single'">
          <view class="component-selector">
            <text class="selector-title">选择测试组件:</text>
            <view class="component-buttons">
              <button
                v-for="comp in availableComponents"
                :key="comp.key"
                :class="['comp-btn', { active: currentComponent === comp.key }]"
                @click="selectComponent(comp.key)"
              >
                {{ comp.name }}
              </button>
            </view>
          </view>

          <view class="single-component-test">
            <!-- 使用条件渲染替代动态组件 -->
            <!-- UserBasicInfo -->
            <UserBasicInfo
              v-if="currentComponent === 'UserBasicInfo' && currentComponentData"
              :component="currentComponentData"
              :theme="currentTheme"
              class="component-preview"
            />

            <!-- JobIntention -->
            <JobIntention
              v-else-if="currentComponent === 'JobIntention' && currentComponentData"
              :component="currentComponentData"
              :theme="currentTheme"
              class="component-preview"
            />

            <!-- WorkExperience -->
            <WorkExperience
              v-else-if="currentComponent === 'WorkExperience' && currentComponentData"
              :component="currentComponentData"
              :theme="currentTheme"
              class="component-preview"
            />

            <!-- EducationExperience -->
            <EducationExperience
              v-else-if="currentComponent === 'EducationExperience' && currentComponentData"
              :component="currentComponentData"
              :theme="currentTheme"
              class="component-preview"
            />

            <!-- SelfEvaluation -->
            <SelfEvaluation
              v-else-if="currentComponent === 'SelfEvaluation' && currentComponentData"
              :component="currentComponentData"
              :theme="currentTheme"
              class="component-preview"
            />

            <!-- Skills -->
            <Skills
              v-else-if="currentComponent === 'Skills' && currentComponentData"
              :component="currentComponentData"
              :theme="currentTheme"
              class="component-preview"
            />

            <!-- ProjectExperience -->
            <ProjectExperience
              v-else-if="currentComponent === 'ProjectExperience' && currentComponentData"
              :component="currentComponentData"
              :theme="currentTheme"
              class="component-preview"
            />


            <view v-else class="no-component">
              <text class="no-component-text">请选择组件</text>
            </view>
          </view>
        </template>
      </view>
    </view>

    <!-- 配置信息显示 -->
    <view class="config-info">
      <text class="info-title">当前配置信息</text>
      <view class="json-viewer">
        <text class="json-content">{{ formattedConfig }}</text>
      </view>
      <button class="copy-btn" @click="copyConfig">复制配置JSON</button>
    </view>

    <!-- 测试用例 -->
    <view class="test-cases">
      <text class="cases-title">测试用例</text>
      <view class="case-buttons">
        <button
          v-for="testCase in testCases"
          :key="testCase.id"
          class="case-btn"
          @click="runTestCase(testCase.id)"
        >
          {{ testCase.name }}
        </button>
      </view>
    </view>

    <!-- 性能监控 -->
    <view class="performance-monitor">
      <text class="monitor-title">性能监控</text>
      <view class="monitor-stats">
        <view class="stat-item">
          <text class="stat-label">渲染时间:</text>
          <text class="stat-value">{{ renderTime }}ms</text>
        </view>
        <view class="stat-item">
          <text class="stat-label">组件数量:</text>
          <text class="stat-value">{{ testResumeData.components?.length || 0 }}</text>
        </view>
        <view class="stat-item">
          <text class="stat-label">数据大小:</text>
          <text class="stat-value">{{ dataSize }}KB</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import ThemeSwitcher from '@/components/ThemeSwitcher.vue';
import DynamicResumeRenderer from '@/components/DynamicResumesRenderer.vue';

// 导入单个组件用于测试
import UserBasicInfo from '@/components/resumes/UserBasicInfo.vue';
import JobIntention from '@/components/resumes/JobIntention.vue';
import WorkExperience from '@/components/resumes/WorkExperience.vue';
import EducationExperience from '@/components/resumes/EducationExperience.vue';
import SelfEvaluation from '@/components/resumes/SelfEvaluation.vue';
import Skills from '@/components/resumes/Skills.vue';
import ProjectExperience from '@/components/resumes/ProjectExperience.vue';

export default {
  components: {
    ThemeSwitcher,
    DynamicResumeRenderer,
    // 注册组件用于动态加载
    UserBasicInfo,
    JobIntention,
    WorkExperience,
    EducationExperience,
    SelfEvaluation,
    Skills,
    ProjectExperience
  },

  data() {
    return {
      // 当前设置
      currentConfig: {
        globalStyle: {
          theme: 'modern',
          primaryColor: '#d4af37',
          fontSizes: {
            h1: '32',
            body: '14'
          },
          fontFamily: "'Microsoft YaHei', 'PingFang SC', sans-serif",
          headerColor: null,
          primaryColor: '#f56c6c',
          accentColor: '#f56c6c',
          secondaryColor: '#f56c6c',
          backgroundColor: '',
          spacing: {
            sectionMargin: '20px',
            padding: '15px',
            lineHeight: '1.6'
          }
        },
        globalLayout: {
          type: 'single-column',
          columns: {
            left: 40,
            right: 60
          },
          orientation: 'portrait',
          componentOrder: []
        }
      },

      // 测试简历数据
      testResumeData: {},

      // 现代风格简历数据（对比用）
      modernResumeData: {},

      // 视图模式
      currentViewMode: 'components',
      viewModes: [
        { id: 'components', name: '完整简历' },
        { id: 'compare', name: '对比模式' },
        { id: 'single', name: '单组件测试' }
      ],

      // 单组件测试
      currentComponent: 'UserBasicInfo',
      currentComponentData: null,
      availableComponents: [
        { key: 'UserBasicInfo', name: '基本信息' },
        { key: 'JobIntention', name: '求职意向' },
        { key: 'WorkExperience', name: '工作经历' },
        { key: 'EducationExperience', name: '教育背景' },
        { key: 'SelfEvaluation', name: '自我评价' },
        { key: 'Skills', name: '技能专长' },
        { key: 'ProjectExperience', name: '项目经历' },
        { key: 'CompanyExperience', name: '公司经历' }
      ],

      // 性能监控
      renderTime: 0,
      renderStartTime: 0,
      renderKey: 0,

      // 测试用例
      testCases: [
        { id: 'modern', name: '现代风格' },
        { id: 'classic', name: '经典商务' },
        { id: 'tech', name: '科技风格' },
        { id: 'dark', name: '深色模式' },
        { id: 'minimal', name: '极简测试' },
        { id: 'complex', name: '复杂数据' }
      ]
    };
  },

  computed: {
    // 当前主题
    currentTheme() {
      return this.currentConfig.globalStyle?.theme || 'modern';
    },

    // 当前主色
    currentPrimaryColor() {
      return this.currentConfig.globalStyle?.primaryColor || '#d4af37';
    },

    // 当前布局类型
    currentLayoutType() {
      return this.currentConfig.globalLayout?.type || 'single-column';
    },

    // 当前字体大小
    currentFontSize() {
      const bodySize = this.currentConfig.globalStyle?.fontSizes?.body || '14';
      if (parseInt(bodySize) >= 16) return 'large';
      if (parseInt(bodySize) <= 12) return 'small';
      return 'medium';
    },

    containerStyle() {
      const style = {};
      const color = this.currentPrimaryColor;

      // 设置CSS变量
      style['--primary-color'] = color;
      style['--primary-light'] = this.lightenColor(color, 40);

      return style;
    },

    formattedConfig() {
      return JSON.stringify(this.currentConfig, null, 2);
    },

    dataSize() {
      const jsonString = JSON.stringify(this.testResumeData);
      return (jsonString.length / 1024).toFixed(2);
    }
  },

  created() {
    // 初始化配置
    this.initConfigs();

    // 开始渲染计时
    this.renderStartTime = Date.now();
  },

  mounted() {
    // 完成渲染计时
    this.renderTime = Date.now() - this.renderStartTime;

    // 每5秒更新一次渲染时间（模拟）
    setInterval(() => {
      this.renderTime = Math.floor(Math.random() * 100) + 50;
    }, 5000);
  },

  methods: {
    initConfigs() {
      // 生成测试配置
      this.testResumeData = this.generateTestResumeData();
      this.modernResumeData = this.generateModernResumeData();
      this.updateCurrentComponentData();
    },

    generateTestResumeData() {
      // 合并当前样式设置到测试配置
      const baseData = this.getDefaultResumeData();

      // 应用当前样式
      if (this.currentConfig.globalStyle) {
        Object.assign(baseData.globalStyle, this.currentConfig.globalStyle);
      }

      // 应用当前布局
      if (this.currentConfig.globalLayout) {
        Object.assign(baseData.globalLayout, this.currentConfig.globalLayout);
      }

      return baseData;
    },

    generateModernResumeData() {
      const data = this.getDefaultResumeData();
      data.globalStyle = {
        theme: 'modern',
        fontSizes: {
          h1: '32',
          body: '14'
        },
        fontFamily: "'Microsoft YaHei', 'PingFang SC', sans-serif",
        primaryColor: '#007aff',
        accentColor: '#007aff',
        secondaryColor: '#007aff',
        backgroundColor: '#ffffff',
        spacing: {
          sectionMargin: '20px',
          padding: '15px',
          lineHeight: '1.6'
        }
      };
      data.globalLayout = {
        type: 'single-column',
        columns: { left: 40, right: 60 },
        orientation: 'portrait',
        componentOrder: []
      };
      return data;
    },

    getDefaultResumeData() {
      return {
        id: 1,
        createdAt: "2025-12-22T03:47:57",
        updatedAt: "2025-12-22T03:47:57",
        deleted: 0,
        userId: 1,
        templateId: 32,
        status: 1,
        viewCount: 0,
        downloadCount: 0,
        globalStyle: {
          theme: "modern",
          fontSizes: {
            h1: "32",
            body: "14"
          },
          fontFamily: "'Microsoft YaHei', 'PingFang SC', sans-serif",
          headerColor: null,
          primaryColor: "#d4af37",
          accentColor: "#d4af37",
          secondaryColor: "#d4af37",
          backgroundColor: "#ffffff",
          spacing: {
            sectionMargin: "20px",
            padding: "15px",
            lineHeight: "1.6"
          }
        },
        globalLayout: {
          type: "single-column",
          columns: {
            left: 40,
            right: 60
          },
          orientation: "portrait",
          componentOrder: []
        },
        components: [
          {
            id: 1,
            createdAt: "2025-12-22 03:47:57",
            updatedAt: "2025-12-22 03:47:57",
            deleted: 0,
            templateId: 32,
            componentId: 1,
            name: "基本信息",
            key: "UserBasicInfo",
            defaultConfig: {
              props: {
                title: "基本信息",
                fields: [
                  "name",
                  "gender",
                  "birthday",
                  "phone",
                  "email",
                  "location",
                  "workYears"
                ],
                showName: true,
                showEmail: true,
                showPhone: true,
                avatarSize: "medium",
                showAvatar: true,
                showGender: true,
                showBirthday: true,
                showLocation: true,
                showWorkYears: true
              },
              styles: {
                padding: "20px",
                fontSize: "16px",
                fieldColor: "#666666",
                titleColor: "#333333",
                avatarBorder: "2px solid #e8e8e8",
                borderRadius: "8px",
                backgroundColor: "#FFFFFF"
              }
            },
            props: {
              workYears: 5,
              website: "https://zhangsan.dev",
              github: "zhangsan",
              wechat: "zhangsan_wx",
              avatar: "https://example.com/avatar1.jpg",
              title: "高级工程师",
              createdAt: "2025-12-17T20:06:50.18694",
              deleted: 0,
              phone: "13800138001",
              name: "张三",
              showContact: true,
              location: "深圳",
              id: 1,
              showAvatar: true,
              email: "zhangsan@tencent.com",
              updatedAt: "2025-12-22T00:42:35.589538"
            },
            styles: {}
          },
          {
            id: 2,
            createdAt: "2025-12-22 03:47:57",
            updatedAt: "2025-12-22 03:47:57",
            deleted: 0,
            templateId: 32,
            componentId: 3,
            name: "工作经历",
            key: "WorkExperience",
            defaultConfig: {
              props: {
                title: "工作经历",
                orderBy: "startDate",
                maxItems: 5,
                showSkills: true,
                showJobTitle: true,
                orderDirection: "desc",
                showDepartment: true,
                showWorkPeriod: true,
                showCompanyLogo: true,
                showCompanyName: true,
                showWorkContent: true,
                showAchievements: true
              },
              styles: {
                padding: "20px",
                fontSize: "14px",
                titleColor: "#333333",
                itemSpacing: "16px",
                periodColor: "#999999",
                borderRadius: "8px",
                companyColor: "#1890ff",
                timelineColor: "#e8e8e8",
                backgroundColor: "#FFFFFF"
              }
            },
            props: {
              showDuration: true,
              experiences: [
                {
                  createdAt: "2025-12-17T20:06:50.403839",
                  isCurrent: true,
                  deleted: 0,
                  description: "负责系统架构设计和团队指导",
                  position: "高级软件工程师",
                  sort: 2,
                  id: 2,
                  startDate: "2023-07-01",
                  updatedAt: "2025-12-17T20:06:50.403839",
                  company: "字节跳动"
                },
                {
                  createdAt: "2025-12-17T20:06:50.403839",
                  isCurrent: false,
                  deleted: 0,
                  endDate: "2023-06-30",
                  description: "负责核心业务功能开发",
                  position: "软件工程师",
                  sort: 1,
                  id: 1,
                  startDate: "2021-07-01",
                  updatedAt: "2025-12-17T20:06:50.403839",
                  company: "阿里巴巴"
                }
              ],
              showCompany: true
            },
            styles: {}
          },
          {
            id: 3,
            createdAt: "2025-12-22 03:47:57",
            updatedAt: "2025-12-22 03:47:57",
            deleted: 0,
            templateId: 32,
            componentId: 4,
            name: "教育背景",
            key: "EducationExperience",
            defaultConfig: {
              props: {
                title: "教育背景",
                orderBy: "graduationDate",
                showGPA: true,
                maxItems: 3,
                showMajor: true,
                showDegree: true,
                showHonors: true,
                showCourses: true,
                degreeFormat: "full",
                orderDirection: "desc",
                showUniversityLogo: true,
                showUniversityName: true,
                showEducationPeriod: true
              },
              styles: {
                padding: "20px",
                fontSize: "14px",
                borderLeft: "3px solid #52c41a",
                majorColor: "#666666",
                titleColor: "#333333",
                itemSpacing: "12px",
                universityColor: "#52c41a",
                borderRadius: "8px",
                backgroundColor: "#FFFFFF"
              }
            },
            props: {
              showDegree: true,
              experiences: [
                {
                  createdAt: "2025-12-17T20:06:50.336946",
                  deleted: 0,
                  major: "软件工程",
                  endDate: "2021-06-30",
                  university: "北京大学",
                  degree: "硕士",
                  isHighest: 1,
                  description: "研究方向：分布式系统",
                  sort: 2,
                  id: 2,
                  startDate: "2018-09-01",
                  updatedAt: "2025-12-17T20:06:50.336946"
                },
                {
                  createdAt: "2025-12-17T20:06:50.336946",
                  deleted: 0,
                  major: "计算机科学与技术",
                  endDate: "2018-06-30",
                  university: "清华大学",
                  degree: "本科",
                  isHighest: 0,
                  description: "主修计算机相关课程",
                  sort: 1,
                  id: 1,
                  startDate: "2014-09-01",
                  updatedAt: "2025-12-17T20:06:50.336946"
                }
              ],
              showTime: true
            },
            styles: {}
          },
          {
            id: 4,
            createdAt: "2025-12-22 03:47:57",
            updatedAt: "2025-12-22 03:47:57",
            deleted: 0,
            templateId: 32,
            componentId: 2,
            name: "求职意向",
            key: "JobIntention",
            defaultConfig: {
              props: {
                title: "求职意向",
                salaryUnit: "K",
                showJobType: true,
                locationType: "city",
                showWorkLocation: true,
                showCurrentStatus: true,
                showExpectedSalary: true,
                showOnboardingTime: true,
                showExpectedIndustry: true,
                showExpectedPosition: true
              },
              styles: {
                padding: "20px",
                fontSize: "16px",
                boxShadow: "0 2px 8px rgba(0,0,0,0.1)",
                titleColor: "#333333",
                borderRadius: "8px",
                keywordColor: "#1890ff",
                backgroundColor: "#FFFFFF"
              }
            },
            props: {
              showSalary: false,
              showLocation: true,
              intentions: [
                {
                  createdAt: "2025-12-22T03:56:23.789648",
                  deleted: 0,
                  city: "西安",
                  position: "高级前端工程师",
                  id: 3,
                  salary: "30000",
                  jobType: "全",
                  updatedAt: "2025-12-22T03:56:23.789648"
                },
                {
                  createdAt: "2025-12-22T03:55:22.452805",
                  deleted: 0,
                  city: "北京",
                  position: "前端架构师",
                  id: 1,
                  salary: "40000",
                  jobType: "全",
                  updatedAt: "2025-12-22T03:55:22.452805"
                }
              ]
            },
            styles: {}
          },
          {
            id: 5,
            createdAt: "2025-12-22 03:47:57",
            updatedAt: "2025-12-22 03:47:57",
            deleted: 0,
            templateId: 32,
            componentId: 5,
            name: "自我评价",
            key: "SelfEvaluation",
            defaultConfig: {
              props: {
                title: "自我评价",
                format: "paragraph",
                maxLength: 500,
                showHobbies: true,
                allowRichText: true,
                showStrengths: true,
                characterTraits: [
                  "责任心强",
                  "学习能力强",
                  "团队协作"
                ],
                showCareerGoals: true,
                showSkillsSummary: true,
                showCharacterTraits: true
              },
              styles: {
                border: "1px solid #f0f0f0",
                padding: "20px",
                fontSize: "14px",
                lineHeight: "1.8",
                titleColor: "#333333",
                borderRadius: "8px",
                contentColor: "#555555",
                backgroundColor: "#fafafa",
                keywordBackground: "#fff7e6"
              }
            },
            props: {
              maxLength: 500,
              evaluations: [
                {
                  createdAt: "2025-12-22T03:59:05.460659",
                  deleted: 0,
                  id: 1,
                  content: "拥有5年大型互联网公司前端开发经验，精通Vue、React等主流框架，对前端性能优化有深入研究。具备良好的团队协作能力和项目管理经验，能够带领团队完成复杂项目开发。热爱技术，持续学习，对新技术保持敏感和热情。",
                  updatedAt: "2025-12-22T03:59:05.460659"
                },
                {
                  createdAt: "2025-12-22T03:59:31.804483",
                  deleted: 0,
                  id: 2,
                  content: "熟悉微前端架构，有大型中后台系统开发经验，能够独立完成项目架构设计和技术选型。对用户体验有深入理解，注重代码质量和可维护性。",
                  updatedAt: "2025-12-22T03:59:31.804483"
                }
              ]
            },
            styles: {}
          },
          {
            id: 6,
            createdAt: "2025-12-22 03:47:57",
            updatedAt: "2025-12-22 03:47:57",
            deleted: 0,
            templateId: 32,
            componentId: 6,
            name: "技能专长",
            key: "Skills",
            defaultConfig: {
              props: {
                title: "技能专长",
                showSkillLevel: true,
                skillLevelType: "progress",
                groupByCategory: true,
                skillCategories: [
                  "编程语言",
                  "框架工具",
                  "数据库",
                  "其他技能"
                ],
                showExperienceYears: true,
                maxSkillsPerCategory: 8
              },
              styles: {
                padding: "20px",
                fontSize: "14px",
                titleColor: "#333333",
                borderRadius: "8px",
                skillSpacing: "12px",
                progressColor: "#1890ff",
                skillNameColor: "#555555",
                backgroundColor: "#FFFFFF",
                categorySpacing: "24px"
              }
            },
            props: {
              skills: [
                {
                  proficiencyPercent: 75,
                  level: "中级",
                  isCertified: false,
                  description: "熟悉MySQL数据库设计、优化和SQL调优",
                  sort: 3,
                  experienceYears: 3,
                  tags: "数据库,SQL,MySQL",
                  createdAt: "2025-12-17T20:06:50.763561",
                  deleted: 0,
                  name: "MySQL",
                  isPublic: true,
                  id: 3,
                  category: "数据库",
                  updatedAt: "2025-12-17T20:06:50.763561"
                },
                {
                  proficiencyPercent: 90,
                  certificateDate: "2023-01-20",
                  level: "高级",
                  isCertified: true,
                  description: "精通Spring、Spring Boot、Spring Cloud等框架",
                  certificateName: "Spring Professional Certification",
                  sort: 2,
                  experienceYears: 4,
                  tags: "Spring,Java,微服务",
                  createdAt: "2025-12-17T20:06:50.763561",
                  deleted: 0,
                  name: "Spring框架",
                  isPublic: true,
                  id: 2,
                  category: "框架",
                  updatedAt: "2025-12-17T20:06:50.763561"
                },
                {
                  proficiencyPercent: 85,
                  certificateDate: "2022-03-15",
                  level: "高级",
                  isCertified: true,
                  description: "熟练掌握Java语言特性，包括集合、多线程、IO等",
                  certificateName: "Oracle Certified Professional",
                  sort: 1,
                  experienceYears: 5.5,
                  tags: "Java,后端,编程",
                  createdAt: "2025-12-17T20:06:50.763561",
                  deleted: 0,
                  name: "Java编程",
                  isPublic: true,
                  id: 1,
                  category: "编程语言",
                  updatedAt: "2025-12-17T20:06:50.763561"
                }
              ],
              skillLevel: true,
              showTags: true
            },
            styles: {}
          },
          {
            id: 7,
            createdAt: "2025-12-22 03:47:57",
            updatedAt: "2025-12-22 03:47:57",
            deleted: 0,
            templateId: 32,
            componentId: 7,
            name: "项目经历",
            key: "ProjectExperience",
            defaultConfig: {
              props: {
                title: "项目经历",
                orderBy: "startDate",
                maxItems: 5,
                showSkills: true,
                showJobTitle: true,
                orderDirection: "desc",
                showDepartment: true,
                showWorkPeriod: true,
                showCompanyLogo: true,
                showCompanyName: true,
                showWorkContent: true,
                showAchievements: true
              },
              styles: {
                padding: "20px",
                fontSize: "14px",
                titleColor: "#333333",
                itemSpacing: "16px",
                periodColor: "#999999",
                borderRadius: "8px",
                companyColor: "#1890ff",
                timelineColor: "#e8e8e8",
                backgroundColor: "#FFFFFF"
              }
            },
            props: {
              experiences: [
                {
                  createdAt: "2025-12-17T20:06:50.36515",
                  deleted: 0,
                  endDate: "2022-12-31",
                  name: "分布式消息队列系统",
                  description: "设计并实现高可用分布式消息队列，支持百万级并发消息处理",
                  sort: 1,
                  id: 1,
                  startDate: "2022-01-01",
                  status: 1,
                  updatedAt: "2025-12-17T20:06:50.36515",
                  role: "系统架构师",
                  technologies: ["Kafka", "Redis", "Go", "Docker"],
                  achievements: ["系统吞吐量提升300%", "消息延迟降低80%"]
                },
                {
                  createdAt: "2025-12-17T20:06:50.36515",
                  deleted: 0,
                  endDate: "2021-11-30",
                  name: "微服务架构迁移",
                  description: "将单体应用迁移到微服务架构，实现服务解耦和独立部署",
                  sort: 2,
                  id: 2,
                  startDate: "2021-03-01",
                  status: 1,
                  updatedAt: "2025-12-17T20:06:50.36515",
                  role: "技术负责人",
                  technologies: ["Spring Cloud", "Docker", "Kubernetes", "Nacos"],
                  achievements: ["部署效率提升200%", "系统可用性达到99.99%"]
                }
              ],
              showRole: true,
              showTechnologies: true
            },
            styles: {}
          },
          {
            id: 8,
            createdAt: "2025-12-22 03:47:57",
            updatedAt: "2025-12-22 03:47:57",
            deleted: 0,
            templateId: 32,
            componentId: 8,
            name: "公司经历",
            key: "CompanyExperience",
            defaultConfig: {
              props: {
                title: "公司经历",
                orderBy: "startDate",
                maxItems: 5,
                showSkills: true,
                showJobTitle: true,
                orderDirection: "desc",
                showDepartment: true,
                showWorkPeriod: true,
                showCompanyLogo: true,
                showCompanyName: true,
                showWorkContent: true,
                showAchievements: true
              },
              styles: {
                padding: "20px",
                fontSize: "14px",
                titleColor: "#333333",
                itemSpacing: "16px",
                periodColor: "#999999",
                borderRadius: "8px",
                companyColor: "#1890ff",
                timelineColor: "#e8e8e8",
                backgroundColor: "#FFFFFF"
              }
            },
            props: {
              experiences: [
                {
                  createdAt: "2025-12-17T20:06:50.311725",
                  isCurrent: true,
                  deleted: 0,
                  description: "负责系统架构设计和团队管理，主导技术架构演进",
                  position: "高级软件工程师",
                  sort: 2,
                  id: 2,
                  startDate: "2022-06-01",
                  updatedAt: "2025-12-22T03:54:25.769164",
                  company: "腾讯科技",
                  achievements: ["团队规模从5人扩大到15人", "系统稳定性提升50%"]
                },
                {
                  createdAt: "2025-12-17T20:06:50.311725",
                  isCurrent: false,
                  deleted: 0,
                  endDate: "2022-05-31",
                  description: "负责核心业务系统开发，参与架构设计和代码审查",
                  position: "软件开发工程师",
                  sort: 1,
                  id: 1,
                  startDate: "2020-03-01",
                  updatedAt: "2025-12-22T03:54:25.113883",
                  company: "百度",
                  achievements: ["完成千万级用户系统重构", "性能优化提升40%"]
                }
              ],
              showRole: true,
              showTechnologies: true
            },
            styles: {}
          }
        ]
      };
    },

    getColorName(colorValue) {
      const colorMap = {
        '#d4af37': '典雅金',
        '#007aff': '科技蓝',
        '#4cd964': '生机绿',
        '#9b59b6': '浪漫紫',
        '#ff3b30': '热情红',
        '#5ac8fa': '清新青',
        '#ff9500': '活力橙',
        '#8e8e93': '高级灰',
        '#f56c6c': '珊瑚红'
      };

      return colorMap[colorValue] || '自定义颜色';
    },

    lightenColor(color, percent) {
      // 简化版颜色变亮函数
      const num = parseInt(color.replace('#', ''), 16);
      const amt = Math.round(2.55 * percent);
      const R = (num >> 16) + amt;
      const G = (num >> 8 & 0x00FF) + amt;
      const B = (num & 0x0000FF) + amt;

      return `#${(
        0x1000000 +
        (R < 255 ? (R < 1 ? 0 : R) : 255) * 0x10000 +
        (G < 255 ? (G < 1 ? 0 : G) : 255) * 0x100 +
        (B < 255 ? (B < 1 ? 0 : B) : 255)
      ).toString(16).slice(1)}`;
    },

    // 更新当前组件数据
    updateCurrentComponentData() {
      const component = this.testResumeData.components?.find(
        comp => comp.key === this.currentComponent
      );
      this.currentComponentData = component || null;
    },

    // 选择组件
    selectComponent(key) {
      this.currentComponent = key;
      this.updateCurrentComponentData();
    },

    handleStyleChange(newConfig) {
      console.log('样式设置变化:', newConfig);

      // 更新当前配置
      this.currentConfig = {
        ...this.currentConfig,
        ...newConfig
      };

      // 重新生成测试配置
      this.testResumeData = this.generateTestResumeData();
      this.updateCurrentComponentData();

      // 强制重新渲染
      this.renderKey++;
    },

    switchViewMode(mode) {
      this.currentViewMode = mode;
    },

    runTestCase(testId) {
      console.log('运行测试用例:', testId);

      let newConfig = {
        globalStyle: { ...this.currentConfig.globalStyle },
        globalLayout: { ...this.currentConfig.globalLayout }
      };

      switch(testId) {
        case 'modern':
          newConfig.globalStyle = {
            theme: 'modern',
            fontSizes: { h1: '32', body: '14' },
            fontFamily: "'Microsoft YaHei', 'PingFang SC', sans-serif",
            primaryColor: '#007aff',
            accentColor: '#007aff',
            secondaryColor: '#007aff',
            backgroundColor: '#ffffff',
            spacing: {
              sectionMargin: '20px',
              padding: '15px',
              lineHeight: '1.6'
            }
          };
          newConfig.globalLayout = {
            type: 'single-column',
            columns: { left: 40, right: 60 },
            orientation: 'portrait',
            componentOrder: []
          };
          break;

        case 'classic':
          newConfig.globalStyle = {
            theme: 'classic',
            fontSizes: { h1: '36', body: '16' },
            fontFamily: "'Times New Roman', serif",
            primaryColor: '#2c3e50',
            accentColor: '#2c3e50',
            secondaryColor: '#2c3e50',
            backgroundColor: '#f8f9fa',
            spacing: {
              sectionMargin: '24px',
              padding: '20px',
              lineHeight: '1.8'
            }
          };
          newConfig.globalLayout = {
            type: 'two-column',
            columns: { left: 35, right: 65 },
            orientation: 'portrait',
            componentOrder: []
          };
          break;

        case 'tech':
          newConfig.globalStyle = {
            theme: 'tech',
            fontSizes: { h1: '30', body: '13' },
            fontFamily: "'Roboto', 'Helvetica Neue', Arial, sans-serif",
            primaryColor: '#5ac8fa',
            accentColor: '#5ac8fa',
            secondaryColor: '#5ac8fa',
            backgroundColor: '#f0f8ff',
            spacing: {
              sectionMargin: '16px',
              padding: '12px',
              lineHeight: '1.5'
            }
          };
          newConfig.globalLayout = {
            type: 'timeline',
            columns: { left: 50, right: 50 },
            orientation: 'portrait',
            componentOrder: ['UserBasicInfo', 'WorkExperience', 'EducationExperience']
          };
          break;

        case 'dark':
          newConfig.globalStyle = {
            theme: 'dark',
            fontSizes: { h1: '32', body: '14' },
            fontFamily: "'Arial', sans-serif",
            primaryColor: '#ff9500',
            accentColor: '#ff9500',
            secondaryColor: '#ff9500',
            backgroundColor: '#1a1a1a',
            textColor: '#ffffff',
            spacing: {
              sectionMargin: '20px',
              padding: '15px',
              lineHeight: '1.6'
            }
          };
          newConfig.globalLayout = {
            type: 'compact',
            columns: { left: 45, right: 55 },
            orientation: 'portrait',
            componentOrder: []
          };
          break;

        case 'minimal':
          newConfig.globalStyle = {
            theme: 'modern',
            fontSizes: { h1: '28', body: '12' },
            fontFamily: "'Helvetica', 'Arial', sans-serif",
            primaryColor: '#8e8e93',
            accentColor: '#8e8e93',
            secondaryColor: '#8e8e93',
            backgroundColor: '#ffffff',
            spacing: {
              sectionMargin: '12px',
              padding: '10px',
              lineHeight: '1.4'
            }
          };
          newConfig.globalLayout = {
            type: 'compact',
            columns: { left: 100, right: 0 },
            orientation: 'portrait',
            componentOrder: []
          };
          break;

        case 'complex':
          newConfig.globalStyle = {
            theme: 'creative',
            fontSizes: { h1: '40', body: '18' },
            fontFamily: "'Comic Sans MS', cursive",
            primaryColor: '#9b59b6',
            accentColor: '#9b59b6',
            secondaryColor: '#9b59b6',
            backgroundColor: '#f9f0ff',
            spacing: {
              sectionMargin: '30px',
              padding: '25px',
              lineHeight: '2.0'
            }
          };
          newConfig.globalLayout = {
            type: 'three-column',
            columns: { left: 30, right: 40 },
            orientation: 'landscape',
            componentOrder: [
              'UserBasicInfo',
              'JobIntention',
              'WorkExperience',
              'EducationExperience',
              'Skills',
              'ProjectExperience',
              'SelfEvaluation'
            ]
          };
          break;
      }

      // 应用测试用例
      this.currentConfig = {
        ...this.currentConfig,
        ...newConfig
      };

      // 重新生成配置
      this.testResumeData = this.generateTestResumeData();
      this.updateCurrentComponentData();
      this.renderKey++;

      uni.showToast({
        title: `已切换到${this.testCases.find(t => t.id === testId)?.name}`,
        icon: 'success'
      });
    },

    copyConfig() {
      uni.setClipboardData({
        data: this.formattedConfig,
        success: () => {
          uni.showToast({
            title: '配置已复制',
            icon: 'success'
          });
        }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.resume-test-page {
  padding: 30rpx;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

.page-header {
  text-align: center;
  margin-bottom: 40rpx;

  .page-title {
    display: block;
    font-size: 44rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 10rpx;
  }

  .page-subtitle {
    display: block;
    font-size: 28rpx;
    color: #666;
  }
}

.current-settings {
  background: white;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .settings-title {
    display: block;
    font-size: 32rpx;
    font-weight: 600;
    color: #333;
    margin-bottom: 20rpx;
    padding-bottom: 12rpx;
    border-bottom: 2rpx solid #f0f0f0;
  }

  .settings-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20rpx;

    .setting-item {
      display: flex;
      align-items: center;

      .setting-label {
        color: #666;
        font-size: 26rpx;
        margin-right: 10rpx;
        min-width: 80rpx;
      }

      .setting-value {
        color: #333;
        font-size: 26rpx;
        font-weight: 500;
        margin-right: 10rpx;
        flex: 1;
      }

      .color-preview {
        width: 24rpx;
        height: 24rpx;
        border-radius: 50%;
        border: 2rpx solid white;
        box-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
      }
    }
  }
}

.resume-test-area {
  background: white;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .test-title {
    display: block;
    font-size: 32rpx;
    font-weight: 600;
    color: #333;
    margin-bottom: 20rpx;
  }

  .view-mode {
    display: flex;
    gap: 20rpx;
    margin-bottom: 30rpx;

    .mode-btn {
      flex: 1;
      background: #f5f5f5;
      border: none;
      border-radius: 8rpx;
      padding: 16rpx 0;
      font-size: 26rpx;
      color: #666;
      transition: all 0.2s;

      &.active {
        background: var(--primary-color, #d4af37);
        color: white;
        font-weight: 500;
      }
    }
  }

  .resume-preview-container {
    border: 2rpx dashed #e0e0e0;
    border-radius: 12rpx;
    padding: 30rpx;
    min-height: 400rpx;
    background: white;
    transition: all 0.3s;
    overflow: auto;

    // 主题样式
    &.theme-dark {
      background: #1a1a1a;
      color: white;
    }

    &.theme-tech {
      background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
    }

    // 字体大小
    &.font-small {
      font-size: 0.9em;
    }

    &.font-medium {
      font-size: 1em;
    }

    &.font-large {
      font-size: 1.1em;
    }
  }
}

.compare-container {
  display: flex;
  gap: 20rpx;

  .compare-left, .compare-right {
    flex: 1;

    .compare-title {
      display: block;
      font-size: 26rpx;
      font-weight: 500;
      color: #666;
      margin-bottom: 16rpx;
      text-align: center;
    }

    .compare-resume {
      border: 1rpx solid #e0e0e0;
      border-radius: 8rpx;
      overflow: hidden;
    }
  }
}

.component-selector {
  margin-bottom: 30rpx;

  .selector-title {
    display: block;
    font-size: 28rpx;
    color: #333;
    margin-bottom: 16rpx;
  }

  .component-buttons {
    display: flex;
    flex-wrap: wrap;
    gap: 16rpx;

    .comp-btn {
      background: #f5f5f5;
      border: none;
      border-radius: 20rpx;
      padding: 12rpx 24rpx;
      font-size: 24rpx;
      color: #666;
      transition: all 0.2s;

      &.active {
        background: var(--primary-color, #d4af37);
        color: white;
        font-weight: 500;
      }
    }
  }
}

.single-component-test {
  border: 2rpx solid #f0f0f0;
  border-radius: 12rpx;
  padding: 20rpx;
  background: #fafafa;
  min-height: 300rpx;

  .component-preview {
    width: 100%;
  }

  .no-component {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 200rpx;

    .no-component-text {
      color: #999;
      font-size: 28rpx;
    }
  }
}

.config-info {
  background: white;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .info-title {
    display: block;
    font-size: 32rpx;
    font-weight: 600;
    color: #333;
    margin-bottom: 20rpx;
    padding-bottom: 12rpx;
    border-bottom: 2rpx solid #f0f0f0;
  }

  .json-viewer {
    background: #f8f9fa;
    border-radius: 8rpx;
    padding: 20rpx;
    margin-bottom: 20rpx;
    max-height: 300rpx;
    overflow-y: auto;
    font-family: 'Monaco', 'Menlo', monospace;

    .json-content {
      font-size: 22rpx;
      color: #333;
      white-space: pre-wrap;
      word-break: break-all;
    }
  }

  .copy-btn {
    background: var(--primary-color, #007aff);
    color: white;
    border: none;
    border-radius: 8rpx;
    padding: 20rpx 0;
    font-size: 28rpx;
    font-weight: 500;
  }
}

.test-cases {
  background: white;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .cases-title {
    display: block;
    font-size: 32rpx;
    font-weight: 600;
    color: #333;
    margin-bottom: 20rpx;
    padding-bottom: 12rpx;
    border-bottom: 2rpx solid #f0f0f0;
  }

  .case-buttons {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16rpx;

    .case-btn {
      background: #f5f5f5;
      border: none;
      border-radius: 8rpx;
      padding: 20rpx 0;
      font-size: 24rpx;
      color: #666;
      transition: all 0.2s;

      &:active {
        background: #e0e0e0;
      }
    }
  }
}

.performance-monitor {
  background: white;
  border-radius: 16rpx;
  padding: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .monitor-title {
    display: block;
    font-size: 32rpx;
    font-weight: 600;
    color: #333;
    margin-bottom: 20rpx;
    padding-bottom: 12rpx;
    border-bottom: 2rpx solid #f0f0f0;
  }

  .monitor-stats {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20rpx;

    .stat-item {
      text-align: center;

      .stat-label {
        display: block;
        color: #666;
        font-size: 24rpx;
        margin-bottom: 8rpx;
      }

      .stat-value {
        display: block;
        color: var(--primary-color, #d4af37);
        font-size: 28rpx;
        font-weight: bold;
      }
    }
  }
}
</style>