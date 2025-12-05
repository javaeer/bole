<template>
  <view class="detail-container">
    <!-- 头部信息 -->
    <view class="detail-header">
      <view class="header-background">
        <view class="header-overlay"></view>
      </view>

      <view class="header-content">
        <!-- 简历标题 -->
        <view class="resume-title-section">
          <text class="resume-title">{{ resumeData.title }}</text>
          <text class="resume-subtitle">{{ resumeData.subtitle }}</text>
        </view>

        <!-- 操作按钮 -->
        <view class="header-actions">
          <button class="btn-action" @click="handleEdit">
            <text class="action-icon">✏️</text>
            <text class="action-text">编辑</text>
          </button>
          <button class="btn-action" @click="handleShare">
            <text class="action-icon">↗️</text>
            <text class="action-text">分享</text>
          </button>
        </view>
      </view>
    </view>

    <!-- 内容区域 -->
    <scroll-view class="detail-content" scroll-y>
      <!-- 基本信息卡片 -->
      <view class="card basic-info">
        <view class="card-header">
          <text class="card-title">基本信息</text>
        </view>
        <view class="card-body">
          <view class="info-row">
            <text class="info-label">姓 名</text>
            <text class="info-value">{{ resumeData.name }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">性 别</text>
            <text class="info-value">{{ resumeData.gender }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">年 龄</text>
            <text class="info-value">{{ resumeData.age }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">联系电话</text>
            <text class="info-value">{{ resumeData.phone }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">邮 箱</text>
            <text class="info-value">{{ resumeData.email }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">期望职位</text>
            <text class="info-value">{{ resumeData.position }}</text>
          </view>
        </view>
      </view>

      <!-- 教育背景 -->
      <view class="card education">
        <view class="card-header">
          <text class="card-title">教育背景</text>
          <button class="btn-add" @click="handleAddEducation">
            <text class="add-icon">+</text>
          </button>
        </view>
        <view class="card-body">
          <view v-for="(edu, index) in resumeData.educations" :key="index" class="education-item">
            <view class="education-header">
              <text class="education-school">{{ edu.school }}</text>
              <text class="education-time">{{ edu.time }}</text>
            </view>
            <view class="education-info">
              <text class="education-degree">{{ edu.degree }}</text>
              <text class="education-major">{{ edu.major }}</text>
            </view>
            <view class="education-desc">{{ edu.description }}</view>
          </view>
        </view>
      </view>

      <!-- 工作经历 -->
      <view class="card experience">
        <view class="card-header">
          <text class="card-title">工作经历</text>
          <button class="btn-add" @click="handleAddExperience">
            <text class="add-icon">+</text>
          </button>
        </view>
        <view class="card-body">
          <view v-for="(exp, index) in resumeData.experiences" :key="index" class="experience-item">
            <view class="experience-header">
              <text class="experience-company">{{ exp.company }}</text>
              <text class="experience-time">{{ exp.time }}</text>
            </view>
            <text class="experience-position">{{ exp.position }}</text>
            <view class="experience-desc">{{ exp.description }}</view>
            <view class="experience-skills">
              <view v-for="(skill, skillIndex) in exp.skills" :key="skillIndex" class="skill-tag">
                {{ skill }}
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 专业技能 -->
      <view class="card skills">
        <view class="card-header">
          <text class="card-title">专业技能</text>
          <button class="btn-add" @click="handleAddSkill">
            <text class="add-icon">+</text>
          </button>
        </view>
        <view class="card-body">
          <view class="skill-category" v-for="(category, index) in resumeData.skills" :key="index">
            <text class="skill-category-title">{{ category.name }}</text>
            <view class="skill-levels">
              <view v-for="(skill, skillIndex) in category.items" :key="skillIndex" class="skill-item">
                <text class="skill-name">{{ skill.name }}</text>
                <view class="skill-level-bar">
                  <view
                    class="skill-level-fill"
                    :style="{ width: skill.level + '%' }"
                    :class="getSkillLevelClass(skill.level)"
                  ></view>
                </view>
                <text class="skill-level-text">{{ skill.level }}%</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 项目经历 -->
      <view class="card projects">
        <view class="card-header">
          <text class="card-title">项目经历</text>
          <button class="btn-add" @click="handleAddProject">
            <text class="add-icon">+</text>
          </button>
        </view>
        <view class="card-body">
          <view v-for="(project, index) in resumeData.projects" :key="index" class="project-item">
            <view class="project-header">
              <text class="project-name">{{ project.name }}</text>
              <view class="project-tags">
                <view class="project-tag" v-for="(tag, tagIndex) in project.tags" :key="tagIndex">
                  {{ tag }}
                </view>
              </view>
            </view>
            <text class="project-time">{{ project.time }}</text>
            <view class="project-desc">{{ project.description }}</view>
            <view class="project-responsibilities">
              <text class="responsibilities-title">主要职责：</text>
              <view v-for="(resp, respIndex) in project.responsibilities" :key="respIndex" class="responsibility-item">
                <text class="responsibility-icon">•</text>
                <text class="responsibility-text">{{ resp }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 底部操作栏 -->
      <view class="detail-footer">
        <button class="btn-download" @click="handleDownload">
          <text class="download-icon">⬇️</text>
          <text class="download-text">下载简历</text>
        </button>
        <button class="btn-preview" @click="handlePreview">
          <text class="preview-icon">👁️</text>
          <text class="preview-text">预览效果</text>
        </button>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'

// 模拟简历数据
const resumeData = reactive({
  title: '高级前端开发工程师',
  subtitle: '5年经验 | React专家 | Vue.js精通',
  name: '张三',
  gender: '男',
  age: '28岁',
  phone: '138****8888',
  email: 'zhangsan@email.com',
  position: '高级前端开发工程师',

  educations: [
    {
      school: '清华大学',
      time: '2015-2019',
      degree: '本科',
      major: '计算机科学与技术',
      description: '主修课程：数据结构、算法设计、计算机网络、操作系统'
    },
    {
      school: '北京大学',
      time: '2019-2022',
      degree: '硕士',
      major: '软件工程',
      description: '研究方向：前端工程化、用户体验设计'
    }
  ],

  experiences: [
    {
      company: '阿里巴巴',
      time: '2022-至今',
      position: '高级前端工程师',
      description: '负责核心业务的前端架构设计与开发，主导多个大型项目',
      skills: ['React', 'TypeScript', 'Node.js', '微前端']
    },
    {
      company: '腾讯科技',
      time: '2020-2022',
      position: '前端工程师',
      description: '参与微信小程序生态开发，优化用户体验',
      skills: ['Vue.js', '小程序', 'Webpack', '性能优化']
    }
  ],

  skills: [
    {
      name: '前端技术',
      items: [
        { name: 'HTML/CSS', level: 95 },
        { name: 'JavaScript', level: 90 },
        { name: 'TypeScript', level: 85 },
        { name: 'Vue.js', level: 88 },
        { name: 'React', level: 92 }
      ]
    },
    {
      name: '工程能力',
      items: [
        { name: 'Webpack', level: 80 },
        { name: 'Vite', level: 85 },
        { name: 'Git', level: 90 },
        { name: 'CI/CD', level: 75 }
      ]
    }
  ],

  projects: [
    {
      name: '企业级中后台管理系统',
      time: '2023.01-2023.06',
      tags: ['React', 'TypeScript', '微前端'],
      description: '为企业提供统一的管理平台，支持多租户、权限管理等',
      responsibilities: [
        '负责前端架构设计和核心模块开发',
        '实现微前端架构，支持子应用独立开发部署',
        '优化首屏加载速度，性能提升40%',
        '设计并实现组件库，提升开发效率'
      ]
    }
  ]
})

// 获取技能等级样式类
const getSkillLevelClass = (level: number) => {
  if (level >= 80) return 'skill-level-high'
  if (level >= 60) return 'skill-level-medium'
  return 'skill-level-low'
}

// 事件处理函数
const handleBack = () => {
  uni.navigateBack()
}

const handleEdit = () => {
  uni.showToast({
    title: '进入编辑模式',
    icon: 'success'
  })
}

const handleShare = () => {
  uni.showActionSheet({
    itemList: ['分享给好友', '生成海报', '复制链接'],
    success: (res) => {
      console.log('选择了：' + res.tapIndex)
    }
  })
}

const handleDownload = () => {
  uni.showLoading({
    title: '生成PDF中...'
  })

  setTimeout(() => {
    uni.hideLoading()
    uni.showToast({
      title: '下载成功',
      icon: 'success'
    })
  }, 2000)
}

const handlePreview = () => {
  uni.navigateTo({
    url: '/pages/resume/preview'
  })
}

const handleAddEducation = () => {
  uni.navigateTo({
    url: '/pages/resume/education/edit'
  })
}

const handleAddExperience = () => {
  uni.navigateTo({
    url: '/pages/resume/experience/edit'
  })
}

const handleAddSkill = () => {
  uni.navigateTo({
    url: '/pages/resume/skill/edit'
  })
}

const handleAddProject = () => {
  uni.navigateTo({
    url: '/pages/resume/project/edit'
  })
}
</script>

<style scoped lang="scss">
.detail-container {
  background-color: $background-color;
  min-height: 100vh;
}

/* 头部样式 */
.detail-header {
  position: relative;
  background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
  padding-top: $navigation-bar-height;
  overflow: hidden;
}

.header-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;

  .header-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.1);
  }
}

.header-content {
  position: relative;
  z-index: $z-index-base + 1;
  padding: $padding-base;
  color: $background-color-white;
}


.resume-title-section {
  margin-bottom: $margin-base;

  .resume-title {
    display: block;
    font-size: $font-size-extra-large;
    font-weight: $font-weight-bold;
    margin-bottom: $margin-mini;
  }

  .resume-subtitle {
    display: block;
    font-size: $font-size-base;
    opacity: 0.9;
  }
}

.header-actions {
  display: flex;
  gap: $margin-small;

  .btn-action {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: $margin-mini;
    background: rgba($background-color-white, 0.2);
    border: 2rpx solid $background-color-white;
    border-radius: $border-radius;
    padding: $padding-mini;
    color: $background-color-white;
    font-size: $font-size-small;

    .action-icon {
      font-size: $font-size-medium;
    }
  }
}

/* 内容区域 */
.detail-content {
  height: calc(100vh - 400rpx);
  padding: $padding-base;
}

/* 卡片通用样式 */
.card {
  background: $background-color-white;
  border-radius: $border-radius;
  margin-bottom: $margin-base;
  box-shadow: $box-shadow;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $padding-base;
  border-bottom: 1rpx solid $border-color-extra-light;

  .card-title {
    font-size: $font-size-medium;
    font-weight: $font-weight-bold;
    color: $text-primary;
  }

  .btn-add {
    width: 60rpx;
    height: 60rpx;
    border-radius: $border-radius-round;
    background: $primary-color;
    color: $background-color-white;
    display: flex;
    align-items: center;
    justify-content: center;

    .add-icon {
      font-size: $font-size-medium;
      font-weight: $font-weight-bold;
    }
  }
}

.card-body {
  padding: $padding-base;
}

/* 基本信息样式 */
.basic-info {
  .info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: $padding-mini 0;
    border-bottom: 1rpx solid $border-color-extra-light;

    &:last-child {
      border-bottom: none;
    }

    .info-label {
      font-size: $font-size-base;
      color: $text-secondary;
      flex: 1;
    }

    .info-value {
      font-size: $font-size-base;
      color: $text-primary;
      flex: 2;
      text-align: right;
      font-weight: $font-weight-medium;
    }
  }
}

/* 教育背景样式 */
.education-item {
  padding: $padding-small 0;
  border-bottom: 1rpx solid $border-color-extra-light;

  &:last-child {
    border-bottom: none;
  }

  .education-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $margin-mini;

    .education-school {
      font-size: $font-size-base;
      font-weight: $font-weight-bold;
      color: $text-primary;
    }

    .education-time {
      font-size: $font-size-small;
      color: $text-secondary;
      background: $background-color;
      padding: 2rpx $padding-mini;
      border-radius: $border-radius-small;
    }
  }

  .education-info {
    display: flex;
    gap: $margin-small;
    margin-bottom: $margin-mini;

    .education-degree,
    .education-major {
      font-size: $font-size-small;
      color: $text-regular;
      background: $background-color;
      padding: 2rpx $padding-mini;
      border-radius: $border-radius-small;
    }
  }

  .education-desc {
    font-size: $font-size-small;
    color: $text-secondary;
    line-height: 1.6;
  }
}

/* 工作经历样式 */
.experience-item {
  padding: $padding-small 0;
  border-bottom: 1rpx solid $border-color-extra-light;

  &:last-child {
    border-bottom: none;
  }

  .experience-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $margin-mini;

    .experience-company {
      font-size: $font-size-base;
      font-weight: $font-weight-bold;
      color: $text-primary;
    }

    .experience-time {
      font-size: $font-size-small;
      color: $text-secondary;
      background: $background-color;
      padding: 2rpx $padding-mini;
      border-radius: $border-radius-small;
    }
  }

  .experience-position {
    display: block;
    font-size: $font-size-base;
    color: $primary-color;
    font-weight: $font-weight-medium;
    margin-bottom: $margin-mini;
  }

  .experience-desc {
    font-size: $font-size-small;
    color: $text-secondary;
    line-height: 1.6;
    margin-bottom: $margin-small;
  }

  .experience-skills {
    display: flex;
    flex-wrap: wrap;
    gap: $margin-mini;

    .skill-tag {
      font-size: $font-size-extra-small;
      color: $primary-color;
      background: $primary-lighter;
      padding: 4rpx $padding-mini;
      border-radius: $border-radius-small;
    }
  }
}

/* 专业技能样式 */
.skill-category {
  margin-bottom: $margin-base;

  &:last-child {
    margin-bottom: 0;
  }

  .skill-category-title {
    display: block;
    font-size: $font-size-base;
    font-weight: $font-weight-medium;
    color: $text-primary;
    margin-bottom: $margin-small;
  }

  .skill-item {
    display: flex;
    align-items: center;
    gap: $margin-small;
    margin-bottom: $margin-mini;

    .skill-name {
      font-size: $font-size-small;
      color: $text-regular;
      flex: 1;
    }

    .skill-level-bar {
      flex: 2;
      height: 8rpx;
      background: $border-color-lighter;
      border-radius: 4rpx;
      overflow: hidden;

      .skill-level-fill {
        height: 100%;
        border-radius: 4rpx;
        transition: width 0.3s ease;

        &.skill-level-high {
          background: $success-color;
        }

        &.skill-level-medium {
          background: $warning-color;
        }

        &.skill-level-low {
          background: $danger-color;
        }
      }
    }

    .skill-level-text {
      font-size: $font-size-extra-small;
      color: $text-secondary;
      flex: 0 0 60rpx;
      text-align: right;
    }
  }
}

/* 项目经历样式 */
.project-item {
  padding: $padding-small 0;
  border-bottom: 1rpx solid $border-color-extra-light;

  &:last-child {
    border-bottom: none;
  }

  .project-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: $margin-mini;

    .project-name {
      font-size: $font-size-base;
      font-weight: $font-weight-bold;
      color: $text-primary;
      flex: 1;
    }

    .project-tags {
      display: flex;
      flex-wrap: wrap;
      gap: $margin-mini;
      flex-shrink: 0;

      .project-tag {
        font-size: $font-size-extra-small;
        color: $primary-color;
        background: $primary-lighter;
        padding: 4rpx $padding-mini;
        border-radius: $border-radius-small;
      }
    }
  }

  .project-time {
    display: block;
    font-size: $font-size-small;
    color: $text-secondary;
    margin-bottom: $margin-mini;
  }

  .project-desc {
    font-size: $font-size-small;
    color: $text-secondary;
    line-height: 1.6;
    margin-bottom: $margin-small;
  }

  .project-responsibilities {
    .responsibilities-title {
      display: block;
      font-size: $font-size-small;
      font-weight: $font-weight-medium;
      color: $text-primary;
      margin-bottom: $margin-mini;
    }

    .responsibility-item {
      display: flex;
      gap: $margin-mini;
      margin-bottom: calc($margin-mini / 2);

      .responsibility-icon {
        color: $primary-color;
        font-size: $font-size-small;
        flex-shrink: 0;
      }

      .responsibility-text {
        font-size: $font-size-small;
        color: $text-secondary;
        line-height: 1.6;
      }
    }
  }
}

/* 底部操作栏 */
.detail-footer {
  display: flex;
  gap: $margin-base;
  padding: $padding-base;
  background: $background-color-white;
  border-radius: $border-radius;
  box-shadow: $box-shadow-light;
  margin-bottom: $margin-base * 2;

  .btn-download,
  .btn-preview {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: $margin-mini;
    height: $button-height;
    border-radius: $border-radius;
    font-size: $font-size-base;
    font-weight: $font-weight-medium;
    transition: all $transition-duration;

    &:active {
      transform: translateY(2rpx);
    }
  }

  .btn-download {
    background: $primary-color;
    color: $background-color-white;

    &:active {
      box-shadow: 0 4rpx 12rpx rgba($primary-color, 0.3);
    }
  }

  .btn-preview {
    background: $background-color-white;
    color: $text-primary;
    border: 2rpx solid $border-color;

    &:active {
      background: $background-color;
    }
  }
}
</style>