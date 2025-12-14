<template>
  <view class="resume-form-page">
    <view class="form-header">
      <view class="header-back" @click="backToSelect">
        <text class="icon-back">←</text>
        <text>更换模板</text>
      </view>
      <text class="header-title">创建简历</text>
      <view class="template-tag" :class="'tag-' + currentTemplate.style">
        {{ currentTemplate.name }}
      </view>
    </view>

    <scroll-view class="form-container" scroll-y>
      <view class="form-card">
        <!-- 基本信息 -->
        <view class="form-section">
          <view class="section-header">
            <text class="section-title">基本信息</text>
            <view class="section-divider"></view>
          </view>

          <view class="form-grid">
            <view class="form-item">
              <text class="item-label">姓名</text>
              <input
                class="form-input"
                placeholder="请输入姓名"
                placeholder-class="input-placeholder"
                v-model="formData.name"
              />
            </view>

            <view class="form-item">
              <text class="item-label">联系电话</text>
              <input
                class="form-input"
                type="number"
                placeholder="请输入联系电话"
                placeholder-class="input-placeholder"
                v-model="formData.phone"
              />
            </view>

            <view class="form-item">
              <text class="item-label">邮箱</text>
              <input
                class="form-input"
                type="email"
                placeholder="请输入邮箱"
                placeholder-class="input-placeholder"
                v-model="formData.email"
              />
            </view>

            <view class="form-item">
              <text class="item-label">求职意向</text>
              <input
                class="form-input"
                placeholder="请输入求职意向"
                placeholder-class="input-placeholder"
                v-model="formData.position"
              />
            </view>
          </view>
        </view>

        <!-- 教育经历 -->
        <view class="form-section">
          <view class="section-header">
            <text class="section-title">教育经历</text>
            <view class="section-divider"></view>
          </view>

          <view class="education-list">
            <view class="education-item" v-for="(edu, index) in formData.education" :key="index">
              <view class="education-header">
                <text class="education-school">{{ edu.school }}</text>
                <text class="education-period">{{ edu.startTime }} - {{ edu.endTime }}</text>
              </view>
              <text class="education-major">{{ edu.major }} | {{ edu.degree }}</text>
            </view>
          </view>

          <button class="add-button" @click="addEducation">
            <text class="icon-add">+</text>
            添加教育经历
          </button>
        </view>

        <!-- 工作经历 -->
        <view class="form-section">
          <view class="section-header">
            <text class="section-title">工作经历</text>
            <view class="section-divider"></view>
          </view>

          <view class="experience-list">
            <view class="experience-item" v-for="(exp, index) in formData.experience" :key="index">
              <view class="experience-header">
                <text class="experience-company">{{ exp.company }}</text>
                <text class="experience-period">{{ exp.startTime }} - {{ exp.endTime }}</text>
              </view>
              <text class="experience-position">{{ exp.position }}</text>
              <text class="experience-description">{{ exp.description }}</text>
            </view>
          </view>

          <button class="add-button" @click="addExperience">
            <text class="icon-add">+</text>
            添加工作经历
          </button>
        </view>

        <!-- 技能专长 -->
        <view class="form-section">
          <view class="section-header">
            <text class="section-title">技能专长</text>
            <view class="section-divider"></view>
          </view>

          <view class="skill-tags">
            <view
              v-for="(skill, index) in formData.skills"
              :key="index"
              class="skill-tag"
            >
              <text>{{ skill }}</text>
              <text class="tag-remove" @click="removeSkill(index)">×</text>
            </view>
            <view class="skill-add" @click="showSkillInput = true">
              <text>+ 添加技能</text>
            </view>
          </view>

          <view class="skill-input-area" v-if="showSkillInput">
            <input
              class="skill-input"
              v-model="newSkill"
              placeholder="请输入技能"
              @confirm="addSkill"
            />
            <view class="skill-input-actions">
              <text class="skill-cancel" @click="cancelAddSkill">取消</text>
              <text class="skill-confirm" @click="addSkill">确定</text>
            </view>
          </view>
        </view>

        <!-- 项目经历 -->
        <view class="form-section">
          <view class="section-header">
            <text class="section-title">项目经历</text>
            <view class="section-divider"></view>
          </view>

          <view class="project-list">
            <view class="project-item" v-for="(project, index) in formData.projects" :key="index">
              <view class="project-header">
                <text class="project-name">{{ project.name }}</text>
                <text class="project-period">{{ project.period }}</text>
              </view>
              <text class="project-description">{{ project.description }}</text>
            </view>
          </view>

          <button class="add-button" @click="addProject">
            <text class="icon-add">+</text>
            添加项目经历
          </button>
        </view>
      </view>
    </scroll-view>

    <view class="form-actions">
      <button class="preview-button" @click="previewResume">
        预览简历
      </button>
      <button class="save-button" @click="saveResume">
        保存简历
      </button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      templateId: null,
      showSkillInput: false,
      newSkill: '',

      templates: [
        { id: 1, name: '传统风', style: 'traditional' },
        { id: 2, name: '科技风', style: 'tech' },
        { id: 3, name: '简约风', style: 'simple' },
        { id: 4, name: '创意风', style: 'creative' },
        { id: 5, name: '商务风', style: 'business' },
        { id: 6, name: '学术风', style: 'academic' }
      ],

      formData: {
        name: '',
        phone: '',
        email: '',
        position: '',
        education: [
          {
            school: '清华大学',
            major: '计算机科学与技术',
            degree: '本科',
            startTime: '2018.09',
            endTime: '2022.06'
          }
        ],
        experience: [
          {
            company: '字节跳动',
            position: '前端开发工程师',
            startTime: '2022.07',
            endTime: '至今',
            description: '负责公司核心业务的前端开发工作'
          }
        ],
        skills: ['Vue.js', 'React', 'TypeScript', 'Node.js'],
        projects: [
          {
            name: '智能简历系统',
            period: '2023.01-2023.06',
            description: '负责前端架构设计和核心模块开发'
          }
        ]
      }
    }
  },

  onLoad(options) {
    // 检查是否有合法的模板ID
    if (!options.templateId) {
      uni.showToast({
        title: '请先选择模板',
        icon: 'none',
        duration: 2000
      })

      // 延迟跳转，让用户看到提示
      setTimeout(() => {
        uni.redirectTo({
          url: '/pages/template/select'
        })
      }, 1500)
      return
    }

    // 验证模板ID是否有效
    const templateId = parseInt(options.templateId)
    const isValidTemplate = this.templates.some(t => t.id === templateId)

    if (!isValidTemplate) {
      uni.showToast({
        title: '模板选择无效',
        icon: 'none'
      })

      setTimeout(() => {
        uni.redirectTo({
          url: '/pages/template/select'
        })
      }, 1500)
      return
    }

    this.templateId = templateId
  },

  computed: {
    currentTemplate() {
      return this.templates.find(t => t.id === this.templateId) || {}
    }
  },

  methods: {
    backToSelect() {
      uni.navigateBack()
    },

    addEducation() {
      this.formData.education.push({
        school: '',
        major: '',
        degree: '',
        startTime: '',
        endTime: ''
      })
    },

    addExperience() {
      this.formData.experience.push({
        company: '',
        position: '',
        startTime: '',
        endTime: '',
        description: ''
      })
    },

    addProject() {
      this.formData.projects.push({
        name: '',
        period: '',
        description: ''
      })
    },

    addSkill() {
      if (this.newSkill.trim()) {
        this.formData.skills.push(this.newSkill.trim())
        this.newSkill = ''
        this.showSkillInput = false
      }
    },

    removeSkill(index) {
      this.formData.skills.splice(index, 1)
    },

    cancelAddSkill() {
      this.newSkill = ''
      this.showSkillInput = false
    },

    previewResume() {
      uni.showToast({
        title: '预览功能开发中',
        icon: 'none'
      })
    },

    saveResume() {
      uni.showToast({
        title: '简历保存成功',
        icon: 'success'
      })
    }
  }
}
</script>

<style lang="scss" scoped>



/* ==================== 简历表单页面样式 ==================== */
.resume-form-page {
  min-height: 100vh;
  background: $background-color;
}

.form-header {
  background: $background-color-white;
  padding: $padding-base;
  padding-top: calc(#{$navigation-bar-height} + #{$padding-base});
  position: relative;
  box-shadow: $box-shadow-light;
  margin-bottom: $margin-small;
}

.header-back {
  display: flex;
  align-items: center;
  font-size: $font-size-small;
  color: $text-secondary;
  margin-bottom: $margin-mini;

  .icon-back {
    margin-right: 4rpx;
    font-size: $font-size-base;
  }
}

.header-title {
  display: block;
  font-size: $font-size-extra-large;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $margin-mini;
}

.template-tag {
  display: inline-block;
  padding: 4rpx 16rpx;
  border-radius: $border-radius-round;
  font-size: $font-size-extra-small;
  font-weight: $font-weight-medium;
  color: $background-color-white;

  &.tag-traditional {
    background: linear-gradient(135deg, $primary-color 0%, color.adjust($primary-color, $lightness: -20%) 100%);
  }

  &.tag-tech {
    background: linear-gradient(135deg, #007aff 0%, #0056cc 100%);
  }

  &.tag-simple {
    background: linear-gradient(135deg, #909399 0%, #606266 100%);
  }

  &.tag-creative {
    background: linear-gradient(135deg, #e6a23c 0%, #b8821e 100%);
  }

  &.tag-business {
    background: linear-gradient(135deg, #303133 0%, #000000 100%);
  }

  &.tag-academic {
    background: linear-gradient(135deg, #67c23a 0%, #459a1c 100%);
  }
}

.form-container {
  height: calc(100vh - 200rpx);
  padding: 0 $padding-base $padding-base;
}

.form-card {
  background: $background-color-white;
  border-radius: $card-border-radius;
  padding: $padding-base;
  box-shadow: $card-shadow;
}

.form-section {
  margin-bottom: $margin-large;

  &:last-child {
    margin-bottom: 0;
  }
}

.section-header {
  margin-bottom: $margin-base;
}

.section-title {
  display: block;
  font-size: $font-size-large;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $margin-mini;
}

.section-divider {
  height: 2rpx;
  background: linear-gradient(90deg, $primary-color 0%, transparent 100%);
  border-radius: 1rpx;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: $margin-base;

  @media (min-width: $breakpoint-sm) {
    grid-template-columns: repeat(2, 1fr);
  }
}

.form-item {
  .item-label {
    display: block;
    font-size: $font-size-small;
    color: $text-regular;
    margin-bottom: $margin-mini;
    font-weight: $font-weight-medium;
  }
}

.form-input {
  width: 100%;
  height: $input-height;
  background: $background-color;
  border: 1px solid $border-color;
  border-radius: $input-border-radius;
  padding: 0 $padding-small;
  font-size: $font-size-base;
  color: $text-primary;
  transition: all $transition-fast;

  &:focus {
    border-color: $focus-border-color;
    box-shadow: $input-focus-shadow;
  }
}

.input-placeholder {
  color: $text-placeholder;
  font-size: $font-size-base;
}

.education-list,
.experience-list,
.project-list {
  margin-bottom: $margin-base;
}

.education-item,
.experience-item,
.project-item {
  background: $background-color;
  border-radius: $border-radius-small;
  padding: $padding-small;
  margin-bottom: $margin-small;

  &:last-child {
    margin-bottom: 0;
  }
}

.education-header,
.experience-header,
.project-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: $margin-mini;
}

.education-school,
.experience-company,
.project-name {
  font-size: $font-size-medium;
  font-weight: $font-weight-semibold;
  color: $text-primary;
  flex: 1;
}

.education-period,
.experience-period,
.project-period {
  font-size: $font-size-small;
  color: $text-secondary;
}

.education-major {
  display: block;
  font-size: $font-size-base;
  color: $text-regular;
}

.experience-position {
  display: block;
  font-size: $font-size-base;
  color: $primary-color;
  font-weight: $font-weight-medium;
  margin-bottom: $margin-mini;
}

.experience-description,
.project-description {
  display: block;
  font-size: $font-size-small;
  color: $text-secondary;
  line-height: 1.5;
}

.add-button {
  width: 100%;
  height: 60rpx;
  background: transparent;
  border: 1px dashed $border-color;
  border-radius: $border-radius-small;
  color: $text-secondary;
  font-size: $font-size-base;
  display: flex;
  align-items: center;
  justify-content: center;

  .icon-add {
    margin-right: 4rpx;
    font-size: $font-size-medium;
  }
}

.skill-tags {
  display: flex;
  flex-wrap: wrap;
  gap: $margin-mini;
  margin-bottom: $margin-base;
}

.skill-tag {
  display: flex;
  align-items: center;
  background: $info-bg;
  border: 1px solid $info-border;
  border-radius: $border-radius-round;
  padding: 4rpx 16rpx;
  font-size: $font-size-small;
  color: $text-regular;

  .tag-remove {
    margin-left: 8rpx;
    font-size: $font-size-medium;
    color: $text-placeholder;
    cursor: pointer;
  }
}

.skill-add {
  background: $background-color;
  border: 1px dashed $border-color;
  border-radius: $border-radius-round;
  padding: 4rpx 16rpx;
  font-size: $font-size-small;
  color: $text-secondary;
  cursor: pointer;
}

.skill-input-area {
  background: $background-color;
  border-radius: $border-radius-small;
  padding: $padding-small;
  margin-top: $margin-mini;
}

.skill-input {
  width: 100%;
  height: 60rpx;
  background: $background-color-white;
  border: 1px solid $border-color;
  border-radius: $border-radius-small;
  padding: 0 $padding-small;
  font-size: $font-size-base;
  color: $text-primary;
  margin-bottom: $margin-mini;
}

.skill-input-actions {
  display: flex;
  justify-content: flex-end;
  gap: $margin-base;
}

.skill-cancel,
.skill-confirm {
  font-size: $font-size-small;
  cursor: pointer;
}

.skill-cancel {
  color: $text-secondary;
}

.skill-confirm {
  color: $primary-color;
  font-weight: $font-weight-medium;
}

.form-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: $padding-base;
  background: $background-color-white;
  box-shadow: $box-shadow-dark;
  display: flex;
  gap: $margin-small;
}

.preview-button {
  flex: 1;
  height: $button-height;
  background: $background-color;
  border: 1px solid $border-color;
  border-radius: $button-border-radius;
  color: $text-regular;
  font-size: $font-size-medium;
  font-weight: $font-weight-semibold;
}

.save-button {
  flex: 1;
  height: $button-height;
  background: $button-primary-bg;
  border-radius: $button-border-radius;
  color: $background-color-white;
  font-size: $font-size-medium;
  font-weight: $font-weight-semibold;
  border: none;
}

/* 响应式调整 */
@media (max-width: $breakpoint-sm) {
  .template-grid {
    grid-template-columns: 1fr;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column;
  }

  .preview-button,
  .save-button {
    width: 100%;
  }
}
</style>