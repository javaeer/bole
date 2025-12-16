<!-- /pages/resume/index.vue -->
<template>
  <view class="resume-page">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <view class="loading-spinner"></view>
      <text class="loading-text">正在加载简历配置...</text>
    </view>

    <!-- 错误状态 -->
    <view v-else-if="error" class="error-container">
      <text class="error-text">{{ error }}</text>
      <button @tap="loadResumeConfig" class="retry-btn">重试</button>
    </view>

    <!-- 动态渲染简历 -->
    <DynamicResumeRenderer v-else-if="resumeConfig.sections && resumeConfig.sections.length > 0"
                           :config="resumeConfig" />

    <!-- 空状态 -->
    <view v-else class="empty-container">
      <text class="empty-text">暂无简历数据</text>
      <button @tap="loadResumeConfig" class="retry-btn">加载示例数据</button>
    </view>

    <!--    &lt;!&ndash; 布局切换按钮（调试用） &ndash;&gt;
        <view class="debug-panel" v-if="!loading && !error">
          <text class="debug-title">调试面板</text>
          <button @tap="toggleDebug" class="debug-btn">
            {{ showDebug ? '隐藏' : '显示' }}配置信息
          </button>
          <button @tap="forceReload" class="debug-btn">强制重新加载</button>

          <view v-if="showDebug" class="debug-info">
            <text class="debug-label">当前布局：{{ currentLayout }}</text>
            <text class="debug-label">板块数量：{{ resumeConfig.sections?.length || 0 }}</text>
            <text class="debug-value">{{ JSON.stringify(resumeConfig, null, 2) }}</text>
          </view>
        </view>-->
  </view>
</template>

<script>
import DynamicResumeRenderer from "@/components/DynamicResumesRenderer.vue";
import UserBasicInfo from "@/components/resumes/UserBasicInfo.vue";
import JobIntention from "@/components/resumes/JobIntention.vue";
import WorkExperience from "@/components/resumes/WorkExperience.vue";
import EducationExperience from "@/components/resumes/EducationExperience.vue";
import SelfEvaluation from "@/components/resumes/SelfEvaluation.vue";

export default {
  components: {
    DynamicResumeRenderer,
    UserBasicInfo: UserBasicInfo,
    JobIntention: JobIntention,
    WorkExperience: WorkExperience,
    EducationExperience: EducationExperience,
    SelfEvaluation: SelfEvaluation,
  },

  data() {
    return {
      loading: true,
      error: null,
      resumeConfig: {},
      currentLayout: "modern",
      showDebug: false,

      // 可用的布局主题
      availableLayouts: [
        { id: "modern", name: "现代风格", color: "#d4af37" },
        { id: "classic", name: "经典风格", color: "#2c3e50" },
        { id: "simple", name: "简约风格", color: "#333333" },
        { id: "tech", name: "科技风格", color: "#007aff" },
        { id: "creative", name: "创意风格", color: "#ff6b6b" },
      ],
    };
  },

  onLoad() {
    console.log("简历页面加载");
    this.loadResumeConfig();
  },

  onShow() {
    console.log("简历页面显示");
  },

  onReady() {
    console.log("简历页面准备就绪");
  },

  methods: {
    async loadResumeConfig() {
      console.log("开始加载简历配置");
      this.loading = true;
      this.error = null;

      try {
        // 模拟网络请求延迟
        await new Promise(resolve => setTimeout(resolve, 800));

        // 获取配置（这里使用默认配置，实际项目中从服务器获取）
        const config = this.getDefaultConfig();
        console.log("获取到配置:", config);

        // 验证配置格式
        if (!config || !config.sections || !Array.isArray(config.sections)) {
          throw new Error("配置格式错误");
        }

        // 更新配置
        this.resumeConfig = config;
        console.log("配置更新完成，板块数量:", config.sections.length);

        // 检查组件是否注册
        this.checkComponents();

      } catch (err) {
        console.error("加载简历配置失败:", err);
        this.error = err.message || "加载失败，请检查网络连接";

        // 使用最简单的配置作为后备
        this.resumeConfig = this.getMinimalConfig();
      } finally {
        this.loading = false;
        console.log("加载状态结束，loading:", false);
      }
    },

    checkComponents() {
      // 检查所有需要的组件是否都已注册
      const requiredComponents = [
        "ResumeBasicInfo",
        "ResumeJobIntention",
        "ResumeWorkExperience",
        "ResumeEducation",
        "ResumeSelfEvaluation",
      ];

      console.log("检查组件注册...");
      requiredComponents.forEach(comp => {
        console.log(`组件 ${comp}:`, this.$options.components?.[comp] ? "已注册" : "未注册");
      });
    },

    getDefaultConfig() {
      // 完整的内置默认简历配置
      return {
        pageId: "resume_default",
        version: "1.0.0",
        globalStyle: {
          theme: "modern",
          primaryColor: "#d4af37",
          secondaryColor: "#f7ef8a",
          fontFamily: "'PingFang SC', 'Helvetica Neue', Arial, sans-serif",
          backgroundColor: "#f8f8f8",
          textColor: "#303133",
        },
        sections: [
          // 用户基础信息
          {
            id: "basic_info",
            component: "ResumeBasicInfo",
            props: {
              avatar: "",
              name: "伯乐用户",
              gender: "男",
              age: 28,
              phone: "13800380038",
              email: "super@bole.com",
              wechat: "bole_user",
              location: "北京市海淀区",
              workYears: 5,
            },
            styles: {
              layout: "card",
              showAvatar: true,
              showContact: true,
            },
          },

          // 求职意向
          {
            id: "job_intention",
            component: "ResumeJobIntention",
            props: {
              position: "高级前端开发工程师",
              industry: "互联网/科技",
              city: "北京",
              salary: "25-35k",
              jobType: "全职",
              onboardTime: "1个月内",
              skills: ["Vue.js", "React", "TypeScript", "Node.js", "Webpack", "小程序开发"],
            },
            styles: {
              layout: "tag",
              showSalary: true,
            },
          },

          // 工作经历
          {
            id: "work_experience",
            component: "ResumeWorkExperience",
            props: {
              experiences: [
                {
                  company: "字节跳动",
                  position: "高级前端开发工程师",
                  startDate: "2020-07",
                  endDate: "至今",
                  description: "负责抖音电商前端架构设计和核心功能开发",
                  projects: [
                    {
                      name: "抖音电商后台管理系统",
                      role: "前端负责人",
                      description: "从0到1搭建电商后台管理系统，支持千万级商品管理",
                      achievements: ["系统性能提升40%", "代码复用率达到70%", "团队开发效率提升30%"],
                    },
                  ],
                  skills: ["Vue3", "TypeScript", "微前端", "性能优化"],
                },
                {
                  company: "阿里巴巴",
                  position: "前端开发工程师",
                  startDate: "2018-03",
                  endDate: "2020-06",
                  description: "参与淘宝移动端核心业务开发",
                  projects: [
                    {
                      name: "双11大促活动页面",
                      role: "核心开发",
                      description: "负责大促活动页面的开发和性能优化",
                      achievements: ["页面加载时间优化50%", "用户参与度提升20%"],
                    },
                  ],
                  skills: ["React", "Node.js", "小程序", "性能监控"],
                },
              ],
            },
            styles: {
              layout: "timeline",
              showProjects: true,
            },
          },

          // 学历信息
          {
            id: "education",
            component: "ResumeEducation",
            props: {
              educations: [
                {
                  school: "清华大学",
                  degree: "硕士",
                  major: "计算机科学与技术",
                  startDate: "2015-09",
                  endDate: "2018-06",
                  achievements: ["优秀毕业生", "国家奖学金", "校级三好学生"],
                },
                {
                  school: "北京大学",
                  degree: "本科",
                  major: "软件工程",
                  startDate: "2011-09",
                  endDate: "2015-06",
                  achievements: ["优秀学生干部", "创新项目奖"],
                },
              ],
            },
          },

          // 自我评价
          {
            id: "self_evaluation",
            component: "ResumeSelfEvaluation",
            props: {
              content: "5年前端开发经验，精通Vue和React技术栈。对前端性能优化有深入研究，曾主导多个大型项目的前端架构设计。具备良好的团队协作和沟通能力，热衷于技术分享和团队建设。拥有强烈的主人翁意识，能够主动发现问题并提出解决方案。",
              keywords: ["技术驱动", "团队协作", "持续学习", "结果导向", "精益求精"],
            },
          },
        ],
      };
    },

    getMinimalConfig() {
      // 最简单的配置，确保至少显示一些内容
      return {
        globalStyle: {
          theme: "modern",
          primaryColor: "#007aff",
        },
        sections: [
          {
            component: "ResumeBasicInfo",
            props: {
              name: "测试用户",
              phone: "13800138000",
              email: "test@example.com",
            },
          },
        ],
      };
    },

    toggleDebug() {
      this.showDebug = !this.showDebug;
    },

    forceReload() {
      console.log("强制重新加载");
      this.resumeConfig = {};
      this.loadResumeConfig();
    },

    switchLayout(layoutId) {
      console.log("切换布局:", layoutId);
      this.currentLayout = layoutId;

      // 更新全局主题
      if (this.resumeConfig.globalStyle) {
        this.resumeConfig.globalStyle.theme = layoutId;

        // 根据布局切换主题色
        const layout = this.availableLayouts.find(l => l.id === layoutId);
        if (layout) {
          this.resumeConfig.globalStyle.primaryColor = layout.color;
        }

        // 强制更新视图
        this.$forceUpdate();
      }
    },
  },
};
</script>

<style lang="scss">

.resume-page {
  min-height: 100vh;
  background-color: $background-color;
  padding: $padding-base;
}

/* 加载状态样式 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 60vh;

  .loading-spinner {
    width: 60rpx;
    height: 60rpx;
    border: 4rpx solid $border-color-light;
    border-top: 4rpx solid $primary-color;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: $margin-base;
  }

  .loading-text {
    color: $text-secondary;
    font-size: $font-size-base;
  }
}

/* 错误状态样式 */
.error-container, .empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 60vh;
  text-align: center;
  padding: $padding-base;

  .error-text, .empty-text {
    color: $text-secondary;
    font-size: $font-size-base;
    margin-bottom: $margin-base;
  }

  .retry-btn {
    background-color: $primary-color;
    color: white;
    border: none;
    border-radius: $border-radius;
    padding: 16rpx 32rpx;
    font-size: $font-size-base;
    min-width: 200rpx;
  }
}

/* 调试面板 */
.debug-panel {
  position: fixed;
  bottom: 20rpx;
  left: 20rpx;
  right: 20rpx;
  background: rgba(255, 255, 255, 0.95);
  border: 1rpx solid $border-color;
  border-radius: $border-radius;
  padding: $padding-small;
  z-index: 1000;
  box-shadow: $box-shadow;

  .debug-title {
    display: block;
    font-size: $font-size-small;
    font-weight: $font-weight-bold;
    color: $text-primary;
    margin-bottom: $margin-mini;
  }

  .debug-btn {
    background-color: $primary-color;
    color: white;
    border: none;
    border-radius: $border-radius-small;
    padding: 8rpx 16rpx;
    font-size: $font-size-extra-small;
    margin-right: $margin-mini;
    margin-bottom: $margin-mini;
  }

  .debug-info {
    margin-top: $margin-mini;
    padding: $padding-mini;
    background-color: $background-color;
    border-radius: $border-radius-small;
    max-height: 300rpx;
    overflow-y: auto;

    .debug-label {
      display: block;
      font-size: $font-size-extra-small;
      color: $text-secondary;
      margin-bottom: 4rpx;
    }

    .debug-value {
      display: block;
      font-size: $font-size-extra-small;
      color: $text-regular;
      white-space: pre-wrap;
      font-family: monospace;
    }
  }
}

/* 动画 */
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* 响应式调整 */
@media (min-width: 768px) {
  .resume-page {
    max-width: 750px;
    margin: 0 auto;
    padding: $padding-base * 1.5;
  }
}
</style>