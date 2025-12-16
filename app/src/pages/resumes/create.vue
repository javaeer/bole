<template>
  <view class="resume-edit-page">
    <!-- 页面头部 -->
    <view class="page-header">
      <view class="header-left" @click="handleBack">
        <text class="icon-back">←</text>
        <text class="back-text">返回</text>
      </view>
      <text class="header-title">编辑简历</text>
      <view class="header-actions">
        <button class="save-btn" @click="handleSave">保存</button>
        <button class="preview-btn" @click="handlePreview">预览</button>
      </view>
    </view>

    <!-- 主要内容区域 -->
    <view class="main-content">
      <!-- 左侧表单区域 -->
      <scroll-view class="form-section" scroll-y="true">
        <view class="form-container">
          <!-- 简历基本信息 -->
          <view class="resume-info-card">
            <text class="info-title">简历信息</text>
            <view class="info-content">
              <view class="info-item">
                <text class="item-label">模板名称</text>
                <text class="item-value">{{ resumeConfig.name || '未命名' }}</text>
              </view>
              <view class="info-item">
                <text class="item-label">模板风格</text>
                <text class="item-value">{{ resumeConfig.globalStyle?.theme || '经典' }}</text>
              </view>
              <view class="info-item">
                <text class="item-label">最后更新</text>
                <text class="item-value">{{ formatDate(resumeConfig.updatedAt) }}</text>
              </view>
            </view>
          </view>

          <!-- 动态生成的表单区域 -->
          <template v-for="component in sortedComponents" :key="component.id">
            <!-- 用户基本信息 -->
            <template v-if="component.component === 'UserBasicInfo'">
              <view class="form-section-card">
                <view class="form-section-header">
                  <text class="section-title">{{ component.name || '基本信息' }}</text>
                  <view class="section-divider"></view>
                </view>

                <!-- 动态表单字段 -->
                <view class="dynamic-form-grid">
                  <template v-if="component.props?.showName !== false">
                    <view class="form-item">
                      <text class="item-label">姓名</text>
                      <input
                        class="form-input"
                        :value="component.props.name || ''"
                        @input="(e) => updateComponentProp(component.id, 'name', e.detail.value)"
                        placeholder="请输入姓名"
                        placeholder-class="input-placeholder"
                      />
                    </view>
                  </template>

                  <template v-if="component.props?.showGender !== false">
                    <view class="form-item">
                      <text class="item-label">性别</text>
                      <picker
                        class="form-input"
                        :value="getPickerIndex(component.props.gender, genderOptions)"
                        :range="genderOptions"
                        @change="(e) => updateComponentProp(component.id, 'gender', genderOptions[e.detail.value])"
                      >
                        <view class="picker-content">
                          {{ component.props.gender || '请选择性别' }}
                        </view>
                      </picker>
                    </view>
                  </template>

                  <template v-if="component.props?.showPhone !== false">
                    <view class="form-item">
                      <text class="item-label">电话</text>
                      <input
                        class="form-input"
                        type="number"
                        :value="component.props.phone || ''"
                        @input="(e) => updateComponentProp(component.id, 'phone', e.detail.value)"
                        placeholder="请输入联系电话"
                        placeholder-class="input-placeholder"
                      />
                    </view>
                  </template>

                  <template v-if="component.props?.showEmail !== false">
                    <view class="form-item">
                      <text class="item-label">邮箱</text>
                      <input
                        class="form-input"
                        type="email"
                        :value="component.props.email || ''"
                        @input="(e) => updateComponentProp(component.id, 'email', e.detail.value)"
                        placeholder="请输入邮箱"
                        placeholder-class="input-placeholder"
                      />
                    </view>
                  </template>

                  <template v-if="component.props?.showWorkYears !== false">
                    <view class="form-item">
                      <text class="item-label">工作年限</text>
                      <input
                        class="form-input"
                        type="number"
                        :value="component.props.workYears || ''"
                        @input="(e) => updateComponentProp(component.id, 'workYears', parseInt(e.detail.value) || 0)"
                        placeholder="请输入工作年限"
                        placeholder-class="input-placeholder"
                      />
                    </view>
                  </template>

                  <template v-if="component.props?.showLocation !== false">
                    <view class="form-item">
                      <text class="item-label">所在地</text>
                      <input
                        class="form-input"
                        :value="component.props.location || ''"
                        @input="(e) => updateComponentProp(component.id, 'location', e.detail.value)"
                        placeholder="请输入所在城市"
                        placeholder-class="input-placeholder"
                      />
                    </view>
                  </template>

                  <template v-if="component.props?.showAvatar !== false">
                    <view class="form-item full-width">
                      <text class="item-label">头像链接</text>
                      <input
                        class="form-input"
                        :value="component.props.avatar || ''"
                        @input="(e) => updateComponentProp(component.id, 'avatar', e.detail.value)"
                        placeholder="请输入头像URL链接"
                        placeholder-class="input-placeholder"
                      />
                    </view>
                  </template>
                </view>
              </view>
            </template>

            <!-- 求职意向 -->
            <template v-if="component.component === 'JobIntention'">
              <view class="form-section-card">
                <view class="form-section-header">
                  <text class="section-title">{{ component.name || '求职意向' }}</text>
                  <view class="section-divider"></view>
                </view>

                <view class="dynamic-form-grid">
                  <template v-if="component.props?.showExpectedPosition !== false">
                    <view class="form-item">
                      <text class="item-label">期望职位</text>
                      <input
                        class="form-input"
                        :value="getIntentionProp(component, 'position') || ''"
                        @input="(e) => updateIntentionProp(component.id, 'position', e.detail.value)"
                        placeholder="请输入期望职位"
                        placeholder-class="input-placeholder"
                      />
                    </view>
                  </template>

                  <template v-if="component.props?.showWorkLocation !== false">
                    <view class="form-item">
                      <text class="item-label">期望城市</text>
                      <input
                        class="form-input"
                        :value="getIntentionProp(component, 'city') || ''"
                        @input="(e) => updateIntentionProp(component.id, 'city', e.detail.value)"
                        placeholder="请输入期望城市"
                        placeholder-class="input-placeholder"
                      />
                    </view>
                  </template>

                  <template v-if="component.props?.showExpectedSalary !== false">
                    <view class="form-item">
                      <text class="item-label">期望薪资</text>
                      <view class="salary-input">
                        <input
                          class="form-input"
                          type="number"
                          :value="getIntentionProp(component, 'salary') || ''"
                          @input="(e) => updateIntentionProp(component.id, 'salary', e.detail.value)"
                          placeholder="薪资"
                          placeholder-class="input-placeholder"
                        />
                        <text class="salary-unit">{{ component.props.salaryUnit || 'K' }}</text>
                      </view>
                    </view>
                  </template>

                  <template v-if="component.props?.showJobType !== false">
                    <view class="form-item">
                      <text class="item-label">工作类型</text>
                      <picker
                        class="form-input"
                        :value="getPickerIndex(getIntentionProp(component, 'jobType'), jobTypeOptions)"
                        :range="jobTypeOptions"
                        @change="(e) => updateIntentionProp(component.id, 'jobType', jobTypeOptions[e.detail.value])"
                      >
                        <view class="picker-content">
                          {{ getIntentionProp(component, 'jobType') || '请选择工作类型' }}
                        </view>
                      </picker>
                    </view>
                  </template>
                </view>
              </view>
            </template>

            <!-- 教育背景 -->
            <template v-if="component.component === 'EducationExperience'">
              <view class="form-section-card">
                <view class="form-section-header">
                  <text class="section-title">{{ component.name || '教育背景' }}</text>
                  <view class="section-divider"></view>
                </view>

                <!-- 动态渲染教育经历列表 -->
                <view class="education-list">
                  <view
                    v-for="(edu, index) in component.props.experiences || []"
                    :key="index"
                    class="education-item"
                  >
                    <view class="item-header">
                      <text class="item-title">教育经历 {{ index + 1 }}</text>
                      <text
                        v-if="(component.props.experiences || []).length > 1"
                        class="remove-btn"
                        @click="removeEducation(component.id, index)"
                      >
                        删除
                      </text>
                    </view>

                    <view class="dynamic-form-grid">
                      <template v-if="component.props?.showSchoolName !== false">
                        <view class="form-item">
                          <text class="item-label">学校名称</text>
                          <input
                            class="form-input"
                            :value="edu.school || ''"
                            @input="(e) => updateEducationField(component.id, index, 'school', e.detail.value)"
                            placeholder="请输入学校名称"
                            placeholder-class="input-placeholder"
                          />
                        </view>
                      </template>

                      <template v-if="component.props?.showDegree !== false">
                        <view class="form-item">
                          <text class="item-label">学历</text>
                          <picker
                            class="form-input"
                            :value="getPickerIndex(edu.degree, degreeOptions)"
                            :range="degreeOptions"
                            @change="(e) => updateEducationField(component.id, index, 'degree', degreeOptions[e.detail.value])"
                          >
                            <view class="picker-content">
                              {{ edu.degree || '请选择学历' }}
                            </view>
                          </picker>
                        </view>
                      </template>

                      <template v-if="component.props?.showMajor !== false">
                        <view class="form-item">
                          <text class="item-label">专业</text>
                          <input
                            class="form-input"
                            :value="edu.major || ''"
                            @input="(e) => updateEducationField(component.id, index, 'major', e.detail.value)"
                            placeholder="请输入专业"
                            placeholder-class="input-placeholder"
                          />
                        </view>
                      </template>

                      <template v-if="component.props?.showEducationPeriod !== false">
                        <view class="form-item">
                          <text class="item-label">开始时间</text>
                          <picker
                            class="form-input"
                            mode="date"
                            fields="month"
                            :value="edu.startDate || ''"
                            @change="(e) => updateEducationField(component.id, index, 'startDate', e.detail.value)"
                          >
                            <view class="picker-content">
                              {{ edu.startDate || '选择开始时间' }}
                            </view>
                          </picker>
                        </view>

                        <view class="form-item">
                          <text class="item-label">结束时间</text>
                          <picker
                            class="form-input"
                            mode="date"
                            fields="month"
                            :value="edu.endDate || ''"
                            @change="(e) => updateEducationField(component.id, index, 'endDate', e.detail.value)"
                          >
                            <view class="picker-content">
                              {{ edu.endDate || '选择结束时间' }}
                            </view>
                          </picker>
                        </view>
                      </template>

                      <template v-if="component.props?.showGPA !== false">
                        <view class="form-item full-width">
                          <text class="item-label">GPA成绩</text>
                          <input
                            class="form-input"
                            :value="edu.gpa || ''"
                            @input="(e) => updateEducationField(component.id, index, 'gpa', e.detail.value)"
                            placeholder="例如：3.8/4.0"
                            placeholder-class="input-placeholder"
                          />
                        </view>
                      </template>

                      <template v-if="component.props?.showHonors !== false">
                        <view class="form-item full-width">
                          <text class="item-label">在校成就</text>
                          <textarea
                            class="form-textarea"
                            :value="edu.description || ''"
                            @input="(e) => updateEducationField(component.id, index, 'description', e.detail.value)"
                            placeholder="请描述在校期间的成就和荣誉"
                            placeholder-class="input-placeholder"
                          />
                        </view>
                      </template>
                    </view>
                  </view>

                  <button class="add-section-btn" @click="addEducation(component.id)">
                    <text class="icon-add">+</text>
                    添加教育经历
                  </button>
                </view>
              </view>
            </template>

            <!-- 工作经历 -->
            <template v-if="component.component === 'WorkExperience'">
              <view class="form-section-card">
                <view class="form-section-header">
                  <text class="section-title">{{ component.name || '工作经历' }}</text>
                  <view class="section-divider"></view>
                </view>

                <view class="experience-list">
                  <view
                    v-for="(exp, index) in component.props.experiences || []"
                    :key="index"
                    class="experience-item"
                  >
                    <view class="item-header">
                      <text class="item-title">工作经历 {{ index + 1 }}</text>
                      <text
                        v-if="(component.props.experiences || []).length > 1"
                        class="remove-btn"
                        @click="removeWorkExperience(component.id, index)"
                      >
                        删除
                      </text>
                    </view>

                    <view class="dynamic-form-grid">
                      <template v-if="component.props?.showCompanyName !== false">
                        <view class="form-item">
                          <text class="item-label">公司名称</text>
                          <input
                            class="form-input"
                            :value="exp.company || ''"
                            @input="(e) => updateWorkExperienceField(component.id, index, 'company', e.detail.value)"
                            placeholder="请输入公司名称"
                            placeholder-class="input-placeholder"
                          />
                        </view>
                      </template>

                      <template v-if="component.props?.showJobTitle !== false">
                        <view class="form-item">
                          <text class="item-label">职位</text>
                          <input
                            class="form-input"
                            :value="exp.position || ''"
                            @input="(e) => updateWorkExperienceField(component.id, index, 'position', e.detail.value)"
                            placeholder="请输入职位"
                            placeholder-class="input-placeholder"
                          />
                        </view>
                      </template>

                      <template v-if="component.props?.showWorkPeriod !== false">
                        <view class="form-item">
                          <text class="item-label">开始时间</text>
                          <picker
                            class="form-input"
                            mode="date"
                            fields="month"
                            :value="exp.startDate || ''"
                            @change="(e) => updateWorkExperienceField(component.id, index, 'startDate', e.detail.value)"
                          >
                            <view class="picker-content">
                              {{ exp.startDate || '选择开始时间' }}
                            </view>
                          </picker>
                        </view>

                        <view class="form-item">
                          <text class="item-label">结束时间</text>
                          <picker
                            class="form-input"
                            mode="date"
                            fields="month"
                            :value="exp.endDate || (exp.isCurrent ? '至今' : '')"
                            @change="(e) => updateWorkExperienceField(component.id, index, 'endDate', e.detail.value)"
                          >
                            <view class="picker-content">
                              {{ exp.isCurrent ? '至今' : (exp.endDate || '选择结束时间') }}
                            </view>
                          </picker>
                        </view>
                      </template>

                      <template v-if="component.props?.showWorkContent !== false">
                        <view class="form-item full-width">
                          <text class="item-label">工作描述</text>
                          <textarea
                            class="form-textarea"
                            :value="exp.description || ''"
                            @input="(e) => updateWorkExperienceField(component.id, index, 'description', e.detail.value)"
                            placeholder="请描述工作职责和成就"
                            placeholder-class="input-placeholder"
                          />
                        </view>
                      </template>
                    </view>
                  </view>

                  <button class="add-section-btn" @click="addWorkExperience(component.id)">
                    <text class="icon-add">+</text>
                    添加工作经历
                  </button>
                </view>
              </view>
            </template>

            <!-- 技能专长 -->
            <template v-if="component.component === 'Skills'">
              <view class="form-section-card">
                <view class="form-section-header">
                  <text class="section-title">{{ component.name || '技能专长' }}</text>
                  <view class="section-divider"></view>
                </view>

                <view class="skills-list">
                  <view
                    v-for="(skill, index) in component.props.skills || []"
                    :key="skill.id || index"
                    class="skill-item"
                  >
                    <view class="item-header">
                      <text class="item-title">技能 {{ index + 1 }}</text>
                      <text
                        v-if="(component.props.skills || []).length > 1"
                        class="remove-btn"
                        @click="removeSkill(component.id, index)"
                      >
                        删除
                      </text>
                    </view>

                    <view class="dynamic-form-grid">
                      <view class="form-item">
                        <text class="item-label">技能名称</text>
                        <input
                          class="form-input"
                          :value="skill.name || ''"
                          @input="(e) => updateSkillField(component.id, index, 'name', e.detail.value)"
                          placeholder="请输入技能名称"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item">
                        <text class="item-label">熟练度</text>
                        <view class="proficiency-input">
                          <input
                            class="form-input"
                            type="number"
                            :value="skill.proficiencyPercent || ''"
                            @input="(e) => updateSkillField(component.id, index, 'proficiencyPercent', parseInt(e.detail.value) || 0)"
                            placeholder="0-100"
                            placeholder-class="input-placeholder"
                          />
                          <text class="proficiency-unit">%</text>
                        </view>
                      </view>

                      <view class="form-item">
                        <text class="item-label">经验年限</text>
                        <input
                          class="form-input"
                          type="number"
                          :value="skill.experienceYears || ''"
                          @input="(e) => updateSkillField(component.id, index, 'experienceYears', parseFloat(e.detail.value) || 0)"
                          placeholder="请输入经验年限"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item">
                        <text class="item-label">技能等级</text>
                        <picker
                          class="form-input"
                          :value="getPickerIndex(skill.level, skillLevelOptions)"
                          :range="skillLevelOptions"
                          @change="(e) => updateSkillField(component.id, index, 'level', skillLevelOptions[e.detail.value])"
                        >
                          <view class="picker-content">
                            {{ skill.level || '请选择技能等级' }}
                          </view>
                        </picker>
                      </view>

                      <view class="form-item full-width">
                        <text class="item-label">技能描述</text>
                        <textarea
                          class="form-textarea"
                          :value="skill.description || ''"
                          @input="(e) => updateSkillField(component.id, index, 'description', e.detail.value)"
                          placeholder="请描述技能掌握情况和应用场景"
                          placeholder-class="input-placeholder"
                        />
                      </view>
                    </view>
                  </view>

                  <button class="add-section-btn" @click="addSkill(component.id)">
                    <text class="icon-add">+</text>
                    添加技能
                  </button>
                </view>
              </view>
            </template>

            <!-- 自我评价 -->
            <template v-if="component.component === 'SelfEvaluation'">
              <view class="form-section-card">
                <view class="form-section-header">
                  <text class="section-title">{{ component.name || '自我评价' }}</text>
                  <view class="section-divider"></view>
                </view>

                <view class="dynamic-form-grid">
                  <view class="form-item full-width">
                    <text class="item-label">自我评价</text>
                    <textarea
                      class="form-textarea large"
                      :value="getEvaluationContent(component) || ''"
                      @input="(e) => updateEvaluationContent(component.id, e.detail.value)"
                      placeholder="请描述您的个人优势、工作态度和职业目标..."
                      placeholder-class="input-placeholder"
                    />
                  </view>
                </view>
              </view>
            </template>
          </template>
        </view>
      </scroll-view>

      <!-- 右侧预览区域 -->
      <view class="preview-section">
        <view class="preview-container">
          <view class="preview-header">
            <text class="preview-title">简历预览</text>
            <view class="preview-actions">
              <button class="preview-action-btn" @click="refreshPreview">
                <text class="action-icon">🔄</text>
                <text class="action-text">刷新</text>
              </button>
              <button class="preview-action-btn" @click="downloadResume">
                <text class="action-icon">⬇️</text>
                <text class="action-text">下载</text>
              </button>
            </view>
          </view>

          <!-- 动态模板引擎渲染 -->
          <view class="resume-preview">
            <DynamicResumeRenderer :config="previewConfig" />
          </view>
        </view>
      </view>
    </view>

    <!-- 操作按钮区域 -->
    <view class="action-buttons">
      <button class="reset-btn" @click="handleReset">重置修改</button>
      <button class="save-btn" @click="handleSave">保存简历</button>
      <button class="publish-btn" @click="handlePublish">发布简历</button>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-overlay">
      <view class="loading-content">
        <view class="loading-spinner"></view>
        <text class="loading-text">加载中...</text>
      </view>
    </view>
  </view>
</template>

<script>
import DynamicResumeRenderer from '@/components/DynamicResumesRenderer.vue'

export default {
  name: 'ResumeEditPage',

  components: {
    DynamicResumeRenderer
  },

  data() {
    return {
      loading: false,
      resumeId: null,
      templateId: null,
      resumeConfig: {},
      originalResumeConfig: {},

      // 选项列表
      genderOptions: ['男', '女'],
      jobTypeOptions: ['全职', '兼职', '实习', '远程'],
      degreeOptions: ['初中', '高中', '大专', '本科', '硕士', '博士'],
      skillLevelOptions: ['入门', '初级', '中级', '高级', '专家']
    }
  },

  computed: {
    // 按顺序排序的组件列表
    sortedComponents() {
      if (!this.resumeConfig.layout?.componentOrder || !this.resumeConfig.components) {
        return this.resumeConfig.components || []
      }

      const componentOrder = this.resumeConfig.layout.componentOrder
      const components = [...(this.resumeConfig.components || [])]

      // 根据 componentOrder 排序
      return components.sort((a, b) => {
        const indexA = componentOrder.indexOf(this.getComponentOrderKey(a.component))
        const indexB = componentOrder.indexOf(this.getComponentOrderKey(b.component))

        if (indexA === -1 && indexB === -1) return 0
        if (indexA === -1) return 1
        if (indexB === -1) return -1
        return indexA - indexB
      })
    },

    // 预览配置
    previewConfig() {
      return {
        globalStyle: this.resumeConfig.globalStyle || {},
        sections: this.sortedComponents.map(component => ({
          id: component.id?.toString() || component.component,
          component: component.component,
          props: component.props || {},
          styles: component.styles || {}
        }))
      }
    }
  },

  onLoad(options) {
    if (options.resumeId) {
      this.resumeId = options.resumeId
    }
    if (options.templateId) {
      this.templateId = options.templateId
    }
    console.log(this.templateId)
    this.loadResumeData()
  },

  methods: {
    // 加载简历数据
    async loadResumeData() {
      this.loading = true

      try {
        // 根据 resumeId 或 templateId 加载数据
        if (this.resumeId) {
          // 加载已有的简历数据
          await this.loadExistingResume()
        } else if (this.templateId) {
          // 基于模板创建新简历
          await this.createResumeFromTemplate()
        }

        // 保存原始数据用于重置
        this.originalResumeConfig = JSON.parse(JSON.stringify(this.resumeConfig))

        console.info("原始数据："+JSON.stringify(this.resumeConfig) )

      } catch (error) {
        console.error('加载简历数据失败:', error)
        uni.showToast({
          title: '加载失败',
          icon: 'error'
        })
      } finally {
        this.loading = false
      }
    },

    // 加载已有的简历数据
    async loadExistingResume() {
      // 这里应该调用API获取已有的简历数据
      // 暂时使用模拟数据
      const response = {
        code: 200,
        data: {
          id: this.resumeId,
          templateId: 1,
          globalStyle: {
            theme: "classic",
            primaryColor: "#3498db",
            secondaryColor: "#7f8c8d",
            backgroundColor: "#ffffff"
          },
          layout: {
            columns: 1,
            componentOrder: [
              "UserBasicInfo",
              "JobIntention",
              "WorkExperience",
              "EducationExperience",
              "Skills",
              "SelfEvaluation"
            ]
          },
          components: [
            {
              id: 1,
              name: "基本信息",
              component: "UserBasicInfo",
              props: {
                name: "张三",
                gender: "男",
                phone: "13800138001",
                email: "zhangsan@example.com",
                workYears: 5,
                location: "深圳",
                avatar: "https://example.com/avatar.jpg",
                showName: true,
                showGender: true,
                showPhone: true,
                showEmail: true,
                showWorkYears: true,
                showLocation: true,
                showAvatar: true
              },
              styles: {}
            },
            {
              id: 2,
              name: "求职意向",
              component: "JobIntention",
              props: {
                intentions: [{
                  position: "高级软件开发工程师",
                  city: "北京",
                  salary: "30",
                  jobType: "全职"
                }],
                showExpectedPosition: true,
                showWorkLocation: true,
                showExpectedSalary: true,
                showJobType: true,
                salaryUnit: "K"
              },
              styles: {}
            },
            {
              id: 3,
              name: "工作经历",
              component: "WorkExperience",
              props: {
                experiences: [
                  {
                    company: "字节跳动",
                    position: "高级软件工程师",
                    startDate: "2021-07",
                    endDate: "2023-12",
                    description: "负责核心业务系统架构设计和开发，主导团队技术方案评审",
                    isCurrent: false
                  },
                  {
                    company: "腾讯",
                    position: "软件工程师",
                    startDate: "2018-06",
                    endDate: "2021-06",
                    description: "参与微信支付系统开发，负责核心模块的编码和测试",
                    isCurrent: false
                  }
                ],
                showCompanyName: true,
                showJobTitle: true,
                showWorkPeriod: true,
                showWorkContent: true
              },
              styles: {}
            },
            {
              id: 4,
              name: "教育背景",
              component: "EducationExperience",
              props: {
                experiences: [
                  {
                    school: "清华大学",
                    degree: "本科",
                    major: "计算机科学与技术",
                    startDate: "2014-09",
                    endDate: "2018-06",
                    gpa: "3.8/4.0",
                    description: "主修计算机相关课程，获得校级优秀毕业生"
                  }
                ],
                showSchoolName: true,
                showDegree: true,
                showMajor: true,
                showEducationPeriod: true,
                showGPA: true,
                showHonors: true
              },
              styles: {}
            },
            {
              id: 5,
              name: "技能专长",
              component: "Skills",
              props: {
                skills: [
                  {
                    name: "Java编程",
                    proficiencyPercent: 85,
                    experienceYears: 5.5,
                    level: "高级",
                    description: "熟练掌握Java语言特性，包括集合、多线程、IO等"
                  },
                  {
                    name: "Spring框架",
                    proficiencyPercent: 90,
                    experienceYears: 4,
                    level: "高级",
                    description: "精通Spring、Spring Boot、Spring Cloud等框架"
                  },
                  {
                    name: "MySQL",
                    proficiencyPercent: 75,
                    experienceYears: 3,
                    level: "中级",
                    description: "熟悉MySQL数据库设计、优化和SQL调优"
                  }
                ],
                showSkillLevel: true,
                showExperienceYears: true,
                groupByCategory: true
              },
              styles: {}
            },
            {
              id: 6,
              name: "自我评价",
              component: "SelfEvaluation",
              props: {
                evaluations: [{
                  content: "5年Java开发经验，精通Spring Cloud微服务架构，具有良好的团队协作能力和问题解决能力。对技术充满热情，持续学习新技术，致力于构建高效、可扩展的系统架构。"
                }],
                showSkillsSummary: true,
                showStrengths: true,
                maxLength: 500
              },
              styles: {}
            }
          ],
          updatedAt: new Date().toISOString()
        }
      }

      this.resumeConfig = response.data
    },

    // 基于模板创建新简历
    async createResumeFromTemplate() {
      // 这里应该调用API获取模板配置
      // 暂时使用模拟数据
      this.resumeConfig =  {
    "id": 5,
        "createdAt": "2025-12-14 06:39:34",
        "updatedAt": "2025-12-14 16:16:27",
        "deleted": 0,
        "name": "学术研究",
        "code": "academic_research_v1",
        "description": "学术研究风格，适合学者、研究人员、教育工作者，突出学术成果",
        "previewImage": "https://example.com/images/resumes/academic_research.png",
        "isActive": true,
        "version": "1.0.0",
        "globalStyle": {
          "theme": "academic",
          "fontSize": "12px",
          "fontFamily": "Cambria, Georgia, serif",
          "lineHeight": "1.8",
          "headerColor": "#1b5e20",
          "primaryColor": "#2e7d32",
          "citationStyle": "APA",
          "secondaryColor": "#558b2f",
          "sectionSpacing": "20px",
          "backgroundColor": "#ffffff",
          "paragraphIndent": "2em"
        },
        "layout": {
          "columns": 1,
          "bibStyle": "APA",
          "pageSize": "A4",
          "showPhoto": false,
          "components": [
            "UserBasicInfo",
            "EducationExperience",
            "WorkExperience",
            "Skills",
            "ProjectExperience"
          ],
          "orientation": "portrait",
          "headerFooter": true,
          "componentOrder": [
            "header",
            "UserBasicInfo",
            "EducationExperience",
            "WorkExperience",
            "ProjectExperience",
            "Skills"
          ],
          "showPageNumbers": true
        },
    "components": [
          {
            "id": 6,
            "createdAt": "2025-12-14 06:39:34",
            "updatedAt": "2025-12-14 16:05:02",
            "deleted": 0,
            "templateId": 1,
            "name": "技能专长",
            "component": "Skills",
            "props": {
              "showSkillLevel": true,
              "skillLevelType": "progress",
              "groupByCategory": true,
              "skillCategories": [
                "编程语言",
                "框架工具",
                "数据库",
                "其他技能"
              ],
              "showExperienceYears": true,
              "maxSkillsPerCategory": 8
            },
            "styles": {
              "padding": "20px",
              "fontSize": "14px",
              "titleColor": "#333333",
              "borderRadius": "8px",
              "skillSpacing": "12px",
              "progressColor": "#1890ff",
              "skillNameColor": "#555555",
              "backgroundColor": "#FFFFFF",
              "categorySpacing": "24px"
            }
          },
          {
            "id": 4,
            "createdAt": "2025-12-14 06:39:34",
            "updatedAt": "2025-12-14 16:05:16",
            "deleted": 0,
            "templateId": 1,
            "name": "教育背景",
            "component": "EducationExperience",
            "props": {
              "orderBy": "graduationDate",
              "showGPA": true,
              "maxItems": 3,
              "showMajor": true,
              "showDegree": true,
              "showHonors": true,
              "showCourses": true,
              "degreeFormat": "full",
              "orderDirection": "desc",
              "showSchoolLogo": true,
              "showSchoolName": true,
              "showEducationPeriod": true
            },
            "styles": {
              "padding": "20px",
              "fontSize": "14px",
              "borderLeft": "3px solid #52c41a",
              "majorColor": "#666666",
              "titleColor": "#333333",
              "itemSpacing": "12px",
              "schoolColor": "#52c41a",
              "borderRadius": "8px",
              "backgroundColor": "#FFFFFF"
            }
          },
          {
            "id": 3,
            "createdAt": "2025-12-14 06:39:34",
            "updatedAt": "2025-12-14 16:05:22",
            "deleted": 0,
            "templateId": 1,
            "name": "工作经历",
            "component": "WorkExperience",
            "props": {
              "orderBy": "startDate",
              "maxItems": 5,
              "showSkills": true,
              "showJobTitle": true,
              "orderDirection": "desc",
              "showDepartment": true,
              "showWorkPeriod": true,
              "showCompanyLogo": true,
              "showCompanyName": true,
              "showWorkContent": true,
              "showAchievements": true
            },
            "styles": {
              "padding": "20px",
              "fontSize": "14px",
              "titleColor": "#333333",
              "itemSpacing": "16px",
              "periodColor": "#999999",
              "borderRadius": "8px",
              "companyColor": "#1890ff",
              "timelineColor": "#e8e8e8",
              "backgroundColor": "#FFFFFF"
            }
          },
          {
            "id": 2,
            "createdAt": "2025-12-14 06:39:34",
            "updatedAt": "2025-12-14 16:05:27",
            "deleted": 0,
            "templateId": 1,
            "name": "求职意向",
            "component": "JobIntention",
            "props": {
              "salaryUnit": "K",
              "showJobType": true,
              "locationType": "city",
              "showWorkLocation": true,
              "showCurrentStatus": true,
              "showExpectedSalary": true,
              "showOnboardingTime": true,
              "showExpectedIndustry": true,
              "showExpectedPosition": true
            },
            "styles": {
              "padding": "20px",
              "fontSize": "16px",
              "boxShadow": "0 2px 8px rgba(0,0,0,0.1)",
              "titleColor": "#333333",
              "borderRadius": "8px",
              "highlightColor": "#1890ff",
              "backgroundColor": "#FFFFFF"
            }
          },
          {
            "id": 1,
            "createdAt": "2025-12-14 06:39:34",
            "updatedAt": "2025-12-14 16:09:35",
            "deleted": 0,
            "templateId": 1,
            "name": "基本信息",
            "component": "UserBasicInfo",
            "props": {
              "showName": true,
              "showEmail": true,
              "showPhone": true,
              "avatarSize": "medium",
              "showAvatar": true,
              "showGender": true,
              "showBirthday": true,
              "showLocation": true,
              "showWorkYears": true
            },
            "styles": {
              "padding": "20px",
              "fontSize": "16px",
              "fieldColor": "#666666",
              "titleColor": "#333333",
              "avatarBorder": "2px solid #e8e8e8",
              "borderRadius": "8px",
              "backgroundColor": "#FFFFFF"
            }
          },
          {
            "id": 5,
            "createdAt": "2025-12-14 06:39:34",
            "updatedAt": "2025-12-14 16:09:43",
            "deleted": 0,
            "templateId": 1,
            "name": "自我评价",
            "component": "SelfEvaluation",
            "props": {
              "format": "paragraph",
              "maxLength": 500,
              "evaluations": [
                {
                  "content": "责任心强,学习能力强,团队协作",
                  "keywords": [
                    "1",
                    "2"
                  ]
                }
              ],
              "showHobbies": true,
              "allowRichText": true,
              "showStrengths": true,
              "showCareerGoals": true,
              "showSkillsSummary": true,
              "showCharacterTraits": true
            },
            "styles": {
              "border": "1px solid #f0f0f0",
              "padding": "20px",
              "fontSize": "14px",
              "lineHeight": "1.8",
              "titleColor": "#333333",
              "borderRadius": "8px",
              "contentColor": "#555555",
              "backgroundColor": "#fafafa",
              "highlightBackground": "#fff7e6"
            }
          }
        ]
  }
    },

    // 获取组件在排序列表中的key
    getComponentOrderKey(componentName) {
      const map = {
        'UserBasicInfo': 'UserBasicInfo',
        'JobIntention': 'JobIntention',
        'WorkExperience': 'WorkExperience',
        'EducationExperience': 'EducationExperience',
        'Skills': 'Skills',
        'ProjectExperience': 'ProjectExperience',
        'SelfEvaluation': 'SelfEvaluation',
        'CompanyExperience': 'CompanyExperience'
      }
      return map[componentName] || componentName.toLowerCase()
    },

    // 获取求职意向属性
    getIntentionProp(component, propName) {
      if (component.props.intentions && component.props.intentions.length > 0) {
        return component.props.intentions[0][propName]
      }
      return component.props[propName]
    },

    // 更新求职意向属性
    updateIntentionProp(componentId, propName, value) {
      const component = this.resumeConfig.components.find(c => c.id === componentId)
      if (!component) return

      if (component.props.intentions) {
        if (!component.props.intentions[0]) {
          component.props.intentions[0] = {}
        }
        component.props.intentions[0][propName] = value
      } else {
        component.props[propName] = value
      }
    },

    // 获取自我评价内容
    getEvaluationContent(component) {
      if (component.props.evaluations && component.props.evaluations.length > 0) {
        return component.props.evaluations[0].content
      }
      return component.props.content
    },

    // 更新自我评价内容
    updateEvaluationContent(componentId, value) {
      const component = this.resumeConfig.components.find(c => c.id === componentId)
      if (!component) return

      if (component.props.evaluations) {
        if (!component.props.evaluations[0]) {
          component.props.evaluations[0] = {}
        }
        component.props.evaluations[0].content = value
      } else {
        component.props.content = value
      }
    },

    // 通用更新组件属性方法
    updateComponentProp(componentId, propName, value) {
      const component = this.resumeConfig.components.find(c => c.id === componentId)
      if (component) {
        if (!component.props) {
          component.props = {}
        }
        component.props[propName] = value
      }
    },

    // 更新教育经历字段
    updateEducationField(componentId, index, fieldName, value) {
      const component = this.resumeConfig.components.find(c => c.id === componentId)
      if (component && component.props.experiences && component.props.experiences[index]) {
        component.props.experiences[index][fieldName] = value
      }
    },

    // 添加教育经历
    addEducation(componentId) {
      const component = this.resumeConfig.components.find(c => c.id === componentId)
      if (component) {
        if (!component.props.experiences) {
          component.props.experiences = []
        }
        component.props.experiences.push({
          school: '',
          degree: '',
          major: '',
          startDate: '',
          endDate: '',
          gpa: '',
          description: ''
        })
      }
    },

    // 删除教育经历
    removeEducation(componentId, index) {
      const component = this.resumeConfig.components.find(c => c.id === componentId)
      if (component && component.props.experiences) {
        component.props.experiences.splice(index, 1)
      }
    },

    // 更新工作经历字段
    updateWorkExperienceField(componentId, index, fieldName, value) {
      const component = this.resumeConfig.components.find(c => c.id === componentId)
      if (component && component.props.experiences && component.props.experiences[index]) {
        component.props.experiences[index][fieldName] = value
      }
    },

    // 添加工作经历
    addWorkExperience(componentId) {
      const component = this.resumeConfig.components.find(c => c.id === componentId)
      if (component) {
        if (!component.props.experiences) {
          component.props.experiences = []
        }
        component.props.experiences.push({
          company: '',
          position: '',
          startDate: '',
          endDate: '',
          description: '',
          isCurrent: false
        })
      }
    },

    // 删除工作经历
    removeWorkExperience(componentId, index) {
      const component = this.resumeConfig.components.find(c => c.id === componentId)
      if (component && component.props.experiences) {
        component.props.experiences.splice(index, 1)
      }
    },

    // 更新技能字段
    updateSkillField(componentId, index, fieldName, value) {
      const component = this.resumeConfig.components.find(c => c.id === componentId)
      if (component && component.props.skills && component.props.skills[index]) {
        component.props.skills[index][fieldName] = value
      }
    },

    // 添加技能
    addSkill(componentId) {
      const component = this.resumeConfig.components.find(c => c.id === componentId)
      if (component) {
        if (!component.props.skills) {
          component.props.skills = []
        }
        component.props.skills.push({
          name: '',
          proficiencyPercent: 0,
          experienceYears: 0,
          level: '中级',
          description: ''
        })
      }
    },

    // 删除技能
    removeSkill(componentId, index) {
      const component = this.resumeConfig.components.find(c => c.id === componentId)
      if (component && component.props.skills) {
        component.props.skills.splice(index, 1)
      }
    },

    // 获取选择器索引
    getPickerIndex(value, options) {
      if (!value || !options) return 0
      const index = options.indexOf(value)
      return index >= 0 ? index : 0
    },

    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return '未知'
      const date = new Date(dateString)
      return date.toLocaleDateString()
    },

    // 返回上一页
    handleBack() {
      uni.navigateBack()
    },

    // 保存简历
    async handleSave() {
      this.loading = true
      try {
        // 这里应该调用API保存简历
        console.log('保存简历数据:', this.resumeConfig)

        // 模拟API调用延迟
        await new Promise(resolve => setTimeout(resolve, 1000))

        uni.showToast({
          title: '保存成功',
          icon: 'success'
        })

        // 更新原始数据
        this.originalResumeConfig = JSON.parse(JSON.stringify(this.resumeConfig))
      } catch (error) {
        console.error('保存失败:', error)
        uni.showToast({
          title: '保存失败',
          icon: 'error'
        })
      } finally {
        this.loading = false
      }
    },

    // 预览简历
    handlePreview() {
      uni.showToast({
        title: '请查看右侧预览区域',
        icon: 'success'
      })
    },

    // 重置修改
    handleReset() {
      uni.showModal({
        title: '确认重置',
        content: '确定要重置所有修改吗？',
        success: (res) => {
          if (res.confirm) {
            this.resumeConfig = JSON.parse(JSON.stringify(this.originalResumeConfig))
            uni.showToast({
              title: '已重置',
              icon: 'success'
            })
          }
        }
      })
    },

    // 发布简历
    handlePublish() {
      uni.showModal({
        title: '发布简历',
        content: '确定要发布这份简历吗？发布后其他人可以看到您的简历。',
        success: async (res) => {
          if (res.confirm) {
            this.loading = true
            try {
              // 这里应该调用API发布简历
              await new Promise(resolve => setTimeout(resolve, 1000))

              uni.showToast({
                title: '发布成功',
                icon: 'success'
              })
            } catch (error) {
              console.error('发布失败:', error)
              uni.showToast({
                title: '发布失败',
                icon: 'error'
              })
            } finally {
              this.loading = false
            }
          }
        }
      })
    },

    // 刷新预览
    refreshPreview() {
      uni.showToast({
        title: '预览已刷新',
        icon: 'success'
      })
    },

    // 下载简历
    downloadResume() {
      uni.showModal({
        title: '下载简历',
        content: '确定要下载当前简历吗？',
        success: (res) => {
          if (res.confirm) {
            uni.showToast({
              title: '开始下载',
              icon: 'success'
            })
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.resume-edit-page {
  min-height: 100vh;
  background: #f8fafc;
}

/* 页面头部 */
.page-header {
  background: white;
  padding: 20rpx 30rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;

  .header-left {
    display: flex;
    align-items: center;
    gap: 10rpx;
    color: #666;
    font-size: 28rpx;
    cursor: pointer;

    .icon-back {
      font-size: 32rpx;
    }
  }

  .header-title {
    font-size: 34rpx;
    font-weight: 600;
    color: #333;
  }

  .header-actions {
    display: flex;
    gap: 20rpx;

    .save-btn,
    .preview-btn {
      padding: 12rpx 24rpx;
      border-radius: 8rpx;
      font-size: 26rpx;
      font-weight: 500;
      border: none;
    }

    .save-btn {
      background: #3b82f6;
      color: white;
    }

    .preview-btn {
      background: #10b981;
      color: white;
    }
  }
}

/* 主要内容区域 */
.main-content {
  display: flex;
  height: calc(100vh - 120rpx);
}

/* 左侧表单区域 */
.form-section {
  flex: 1;
  background: #f8fafc;
  border-right: 1rpx solid #e2e8f0;
}

.form-container {
  padding: 30rpx;
  padding-bottom: 180rpx;
}

.resume-info-card {
  background: white;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
  border: 1rpx solid #e2e8f0;

  .info-title {
    display: block;
    font-size: 32rpx;
    font-weight: 600;
    color: #333;
    margin-bottom: 20rpx;
  }

  .info-content {
    display: grid;
    gap: 16rpx;
  }

  .info-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12rpx 0;
    border-bottom: 1rpx solid #f1f5f9;

    &:last-child {
      border-bottom: none;
    }

    .item-label {
      font-size: 26rpx;
      color: #666;
    }

    .item-value {
      font-size: 26rpx;
      color: #333;
      font-weight: 500;
    }
  }
}

.form-section-card {
  background: white;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
  border: 1rpx solid #e2e8f0;

  .form-section-header {
    margin-bottom: 30rpx;

    .section-title {
      display: block;
      font-size: 30rpx;
      font-weight: 600;
      color: #333;
      margin-bottom: 16rpx;
    }

    .section-divider {
      height: 2rpx;
      background: linear-gradient(90deg, #3b82f6, #10b981);
      border-radius: 1rpx;
    }
  }
}

.dynamic-form-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24rpx;

  @media (min-width: 768px) {
    grid-template-columns: repeat(2, 1fr);
  }
}

.form-item {
  &.full-width {
    grid-column: 1 / -1;
  }

  .item-label {
    display: block;
    font-size: 26rpx;
    color: #555;
    margin-bottom: 12rpx;
    font-weight: 500;
  }

  .form-input {
    width: 100%;
    height: 80rpx;
    background: #f8fafc;
    border: 1rpx solid #e2e8f0;
    border-radius: 8rpx;
    padding: 0 20rpx;
    font-size: 28rpx;
    color: #333;
    transition: all 0.2s;

    &:focus {
      border-color: #3b82f6;
      box-shadow: 0 0 0 3rpx rgba(59, 130, 246, 0.1);
    }
  }

  .picker-content {
    height: 80rpx;
    background: #f8fafc;
    border: 1rpx solid #e2e8f0;
    border-radius: 8rpx;
    padding: 0 20rpx;
    display: flex;
    align-items: center;
    font-size: 28rpx;
    color: #333;
  }

  .salary-input,
  .proficiency-input {
    display: flex;
    align-items: center;
    gap: 10rpx;

    .form-input {
      flex: 1;
    }

    .salary-unit,
    .proficiency-unit {
      font-size: 28rpx;
      color: #666;
    }
  }

  .form-textarea {
    width: 100%;
    min-height: 120rpx;
    background: #f8fafc;
    border: 1rpx solid #e2e8f0;
    border-radius: 8rpx;
    padding: 20rpx;
    font-size: 28rpx;
    color: #333;
    transition: all 0.2s;

    &:focus {
      border-color: #3b82f6;
      box-shadow: 0 0 0 3rpx rgba(59, 130, 246, 0.1);
    }

    &.large {
      min-height: 180rpx;
    }
  }
}

.education-list,
.experience-list,
.skills-list {
  .education-item,
  .experience-item,
  .skill-item {
    background: #f8fafc;
    border-radius: 12rpx;
    padding: 24rpx;
    margin-bottom: 24rpx;
    border: 1rpx solid #e2e8f0;

    .item-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20rpx;

      .item-title {
        font-size: 28rpx;
        font-weight: 600;
        color: #333;
      }

      .remove-btn {
        color: #ef4444;
        font-size: 24rpx;
        cursor: pointer;
        padding: 8rpx 16rpx;
        border-radius: 6rpx;
        background: rgba(239, 68, 68, 0.1);

        &:active {
          background: rgba(239, 68, 68, 0.2);
        }
      }
    }
  }

  .add-section-btn {
    width: 100%;
    height: 80rpx;
    background: transparent;
    border: 1rpx dashed #cbd5e1;
    border-radius: 8rpx;
    color: #666;
    font-size: 28rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8rpx;

    .icon-add {
      font-size: 32rpx;
    }

    &:active {
      background: #f1f5f9;
    }
  }
}

/* 右侧预览区域 */
.preview-section {
  flex: 1;
  background: white;
}

.preview-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.preview-header {
  padding: 20rpx 30rpx;
  border-bottom: 1rpx solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .preview-title {
    font-size: 30rpx;
    font-weight: 600;
    color: #333;
  }

  .preview-actions {
    display: flex;
    gap: 16rpx;

    .preview-action-btn {
      display: flex;
      align-items: center;
      gap: 8rpx;
      background: #f1f5f9;
      border: 1rpx solid #cbd5e1;
      border-radius: 8rpx;
      padding: 12rpx 20rpx;
      font-size: 24rpx;
      color: #64748b;

      .action-icon {
        font-size: 24rpx;
      }
    }
  }
}

.resume-preview {
  flex: 1;
  overflow-y: auto;
  padding: 30rpx;
  background: #f8fafc;
}

/* 操作按钮区域 */
.action-buttons {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 50%;
  background: linear-gradient(to top, white 80%, transparent);
  padding: 20rpx 30rpx;
  display: flex;
  gap: 20rpx;
  z-index: 999;

  .reset-btn,
  .save-btn,
  .publish-btn {
    flex: 1;
    height: 80rpx;
    border-radius: 8rpx;
    font-size: 28rpx;
    font-weight: 500;
    border: none;
  }

  .reset-btn {
    background: #f1f5f9;
    color: #64748b;
  }

  .save-btn {
    background: #3b82f6;
    color: white;
  }

  .publish-btn {
    background: #10b981;
    color: white;
  }
}

/* 加载状态 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.loading-content {
  background: white;
  border-radius: 16rpx;
  padding: 40rpx;
  box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20rpx;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid #e2e8f0;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: 28rpx;
  color: #666;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
  }

  .form-section,
  .preview-section {
    flex: none;
    height: 50vh;
  }

  .action-buttons {
    right: 0;
  }

  .dynamic-form-grid {
    grid-template-columns: 1fr;
  }
}
</style>