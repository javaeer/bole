<!-- components/DynamicFormGenerator.vue -->
<template>
  <view class="dynamic-form-generator">
    <!-- 按顺序渲染每个组件 -->
    <template v-for="component in sortedComponents" :key="component.id">
      <!-- 基本信息组件 -->
      <view v-if="component.component === 'UserBasicInfo'" class="form-section-card">
        <view class="form-section-header">
          <text class="section-title">{{ component.name || '基本信息' }}</text>
          <view class="section-divider"></view>
        </view>

        <view class="form-grid">
          <!-- 姓名 -->
          <view v-if="component.props?.showName !== false" class="form-item">
            <text class="item-label">姓名</text>
            <input
              class="form-input"
              :value="component.props.name || ''"
              @input="updateComponentProp(component.id, 'name', $event.detail.value)"
              placeholder="请输入姓名"
              placeholder-class="input-placeholder"
            />
          </view>

          <!-- 性别 -->
          <view v-if="component.props?.showGender !== false" class="form-item">
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

          <!-- 电话 -->
          <view v-if="component.props?.showPhone !== false" class="form-item">
            <text class="item-label">电话</text>
            <input
              class="form-input"
              type="number"
              :value="component.props.phone || ''"
              @input="updateComponentProp(component.id, 'phone', $event.detail.value)"
              placeholder="请输入联系电话"
              placeholder-class="input-placeholder"
            />
          </view>

          <!-- 邮箱 -->
          <view v-if="component.props?.showEmail !== false" class="form-item">
            <text class="item-label">邮箱</text>
            <input
              class="form-input"
              type="email"
              :value="component.props.email || ''"
              @input="updateComponentProp(component.id, 'email', $event.detail.value)"
              placeholder="请输入邮箱"
              placeholder-class="input-placeholder"
            />
          </view>

          <!-- 工作年限 -->
          <view v-if="component.props?.showWorkYears !== false" class="form-item">
            <text class="item-label">工作年限</text>
            <input
              class="form-input"
              type="number"
              :value="component.props.workYears || ''"
              @input="updateComponentProp(component.id, 'workYears', parseInt($event.detail.value) || 0)"
              placeholder="请输入工作年限"
              placeholder-class="input-placeholder"
            />
          </view>

          <!-- 所在地 -->
          <view v-if="component.props?.showLocation !== false" class="form-item">
            <text class="item-label">所在地</text>
            <input
              class="form-input"
              :value="component.props.location || ''"
              @input="updateComponentProp(component.id, 'location', $event.detail.value)"
              placeholder="请输入所在城市"
              placeholder-class="input-placeholder"
            />
          </view>

          <!-- 头像 -->
          <view v-if="component.props?.showAvatar !== false" class="form-item full-width">
            <text class="item-label">头像链接</text>
            <input
              class="form-input"
              :value="component.props.avatar || ''"
              @input="updateComponentProp(component.id, 'avatar', $event.detail.value)"
              placeholder="请输入头像URL链接（可选）"
              placeholder-class="input-placeholder"
            />
          </view>

          <!-- 职位标题 -->
          <view v-if="component.props?.title" class="form-item full-width">
            <text class="item-label">职位标题</text>
            <input
              class="form-input"
              :value="component.props.title || ''"
              @input="updateComponentProp(component.id, 'title', $event.detail.value)"
              placeholder="例如：高级前端工程师"
              placeholder-class="input-placeholder"
            />
          </view>
        </view>
      </view>

      <!-- 求职意向组件 -->
      <view v-else-if="component.component === 'JobIntention'" class="form-section-card">
        <view class="form-section-header">
          <text class="section-title">{{ component.name || '求职意向' }}</text>
          <view class="section-divider"></view>
        </view>

        <view class="form-grid">
          <!-- 期望职位 -->
          <view v-if="component.props?.showExpectedPosition !== false" class="form-item">
            <text class="item-label">期望职位</text>
            <input
              class="form-input"
              :value="getIntentionProp(component, 'position') || ''"
              @input="updateIntentionProp(component.id, 'position', $event.detail.value)"
              placeholder="请输入期望职位"
              placeholder-class="input-placeholder"
            />
          </view>

          <!-- 期望城市 -->
          <view v-if="component.props?.showWorkLocation !== false" class="form-item">
            <text class="item-label">期望城市</text>
            <input
              class="form-input"
              :value="getIntentionProp(component, 'city') || ''"
              @input="updateIntentionProp(component.id, 'city', $event.detail.value)"
              placeholder="请输入期望城市"
              placeholder-class="input-placeholder"
            />
          </view>

          <!-- 期望薪资 -->
          <view v-if="component.props?.showExpectedSalary !== false" class="form-item">
            <text class="item-label">期望薪资</text>
            <view class="salary-input">
              <input
                class="form-input"
                type="number"
                :value="getIntentionProp(component, 'salary') || ''"
                @input="updateIntentionProp(component.id, 'salary', $event.detail.value)"
                placeholder="薪资"
                placeholder-class="input-placeholder"
              />
              <text class="salary-unit">{{ component.props.salaryUnit || 'K' }}</text>
            </view>
          </view>

          <!-- 工作类型 -->
          <view v-if="component.props?.showJobType !== false" class="form-item">
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

          <!-- 期望行业 -->
          <view v-if="component.props?.showExpectedIndustry !== false" class="form-item">
            <text class="item-label">期望行业</text>
            <input
              class="form-input"
              :value="getIntentionProp(component, 'industry') || ''"
              @input="updateIntentionProp(component.id, 'industry', $event.detail.value)"
              placeholder="请输入期望行业"
              placeholder-class="input-placeholder"
            />
          </view>

          <!-- 到岗时间 -->
          <view v-if="component.props?.showOnboardingTime !== false" class="form-item">
            <text class="item-label">到岗时间</text>
            <input
              class="form-input"
              :value="getIntentionProp(component, 'onboardTime') || ''"
              @input="updateIntentionProp(component.id, 'onboardTime', $event.detail.value)"
              placeholder="例如：随时到岗、一周内"
              placeholder-class="input-placeholder"
            />
          </view>

          <!-- 技能标签 -->
          <view v-if="component.props?.skills" class="form-item full-width">
            <text class="item-label">相关技能</text>
            <view class="skills-input-area">
              <view class="skills-tags">
                <view
                  v-for="(skill, index) in (Array.isArray(component.props.skills) ? component.props.skills : [])"
                  :key="index"
                  class="skill-tag"
                >
                  <text>{{ skill }}</text>
                  <text class="tag-remove" @click="removeSkill(component.id, index)">×</text>
                </view>
              </view>
              <input
                v-if="showSkillInput"
                class="skill-input"
                v-model="newSkill"
                placeholder="输入技能后按回车"
                @confirm="addSkill(component.id)"
                @blur="addSkill(component.id)"
              />
              <text
                v-else
                class="add-skill-btn"
                @click="showSkillInput = true"
              >
                + 添加技能
              </text>
            </view>
          </view>
        </view>
      </view>

      <!-- 教育背景组件 -->
      <view v-else-if="component.component === 'EducationExperience'" class="form-section-card">
        <view class="form-section-header">
          <text class="section-title">{{ component.name || '教育背景' }}</text>
          <view class="section-divider"></view>
        </view>

        <!-- 教育经历列表 -->
        <view class="education-list">
          <view
            v-for="(edu, index) in (component.props.educations || component.props.experiences || [])"
            :key="index"
            class="education-item"
          >
            <view class="item-header">
              <text class="item-title">教育经历 {{ index + 1 }}</text>
              <text
                v-if="(component.props.educations || component.props.experiences || []).length > 1"
                class="remove-btn"
                @click="removeEducation(component.id, index)"
              >
                删除
              </text>
            </view>

            <view class="form-grid">
              <!-- 学校名称 -->
              <view v-if="component.props?.showSchoolName !== false" class="form-item">
                <text class="item-label">学校名称</text>
                <input
                  class="form-input"
                  :value="edu.school || ''"
                  @input="updateEducationField(component.id, index, 'school', $event.detail.value)"
                  placeholder="请输入学校名称"
                  placeholder-class="input-placeholder"
                />
              </view>

              <!-- 学历 -->
              <view v-if="component.props?.showDegree !== false" class="form-item">
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

              <!-- 专业 -->
              <view v-if="component.props?.showMajor !== false" class="form-item">
                <text class="item-label">专业</text>
                <input
                  class="form-input"
                  :value="edu.major || ''"
                  @input="updateEducationField(component.id, index, 'major', $event.detail.value)"
                  placeholder="请输入专业"
                  placeholder-class="input-placeholder"
                />
              </view>

              <!-- 教育时间 -->
              <view v-if="component.props?.showEducationPeriod !== false" class="form-item">
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

              <view v-if="component.props?.showEducationPeriod !== false" class="form-item">
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

              <!-- GPA -->
              <view v-if="component.props?.showGPA !== false" class="form-item full-width">
                <text class="item-label">GPA成绩</text>
                <input
                  class="form-input"
                  :value="edu.gpa || ''"
                  @input="updateEducationField(component.id, index, 'gpa', $event.detail.value)"
                  placeholder="例如：3.8/4.0"
                  placeholder-class="input-placeholder"
                />
              </view>

              <!-- 在校成就 -->
              <view v-if="component.props?.showHonors !== false" class="form-item full-width">
                <text class="item-label">在校成就</text>
                <textarea
                  class="form-textarea"
                  :value="edu.description || edu.achievements || ''"
                  @input="updateEducationField(component.id, index, 'description', $event.detail.value)"
                  placeholder="请描述在校期间的成就和荣誉（用逗号分隔）"
                  placeholder-class="input-placeholder"
                />
              </view>
            </view>
          </view>

          <button class="add-section-btn" @click="addEducation(component.id)">
            <text class="icon-add">+</text>
            添加教育经历
          </button>
        </view>
      </view>

      <!-- 工作经历组件 -->
      <view v-else-if="component.component === 'WorkExperience'" class="form-section-card">
        <view class="form-section-header">
          <text class="section-title">{{ component.name || '工作经历' }}</text>
          <view class="section-divider"></view>
        </view>

        <!-- 工作经历列表 -->
        <view class="experience-list">
          <view
            v-for="(exp, index) in (component.props.experiences || [])"
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

            <view class="form-grid">
              <!-- 公司名称 -->
              <view v-if="component.props?.showCompanyName !== false" class="form-item">
                <text class="item-label">公司名称</text>
                <input
                  class="form-input"
                  :value="exp.company || ''"
                  @input="updateWorkExperienceField(component.id, index, 'company', $event.detail.value)"
                  placeholder="请输入公司名称"
                  placeholder-class="input-placeholder"
                />
              </view>

              <!-- 职位 -->
              <view v-if="component.props?.showJobTitle !== false" class="form-item">
                <text class="item-label">职位</text>
                <input
                  class="form-input"
                  :value="exp.position || ''"
                  @input="updateWorkExperienceField(component.id, index, 'position', $event.detail.value)"
                  placeholder="请输入职位"
                  placeholder-class="input-placeholder"
                />
              </view>

              <!-- 工作时间 -->
              <view v-if="component.props?.showWorkPeriod !== false" class="form-item">
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

              <view v-if="component.props?.showWorkPeriod !== false" class="form-item">
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

              <!-- 工作描述 -->
              <view v-if="component.props?.showWorkContent !== false" class="form-item full-width">
                <text class="item-label">工作描述</text>
                <textarea
                  class="form-textarea"
                  :value="exp.description || ''"
                  @input="updateWorkExperienceField(component.id, index, 'description', $event.detail.value)"
                  placeholder="请描述工作职责和成就"
                  placeholder-class="input-placeholder"
                />
              </view>

              <!-- 项目经历 -->
              <view v-if="component.props?.showProjects && exp.projects" class="form-item full-width">
                <text class="item-label">项目经历</text>
                <view class="project-sub-list">
                  <view
                    v-for="(project, pIndex) in exp.projects"
                    :key="pIndex"
                    class="project-sub-item"
                  >
                    <view class="sub-item-header">
                      <text class="sub-item-title">项目 {{ pIndex + 1 }}</text>
                      <text class="remove-sub-btn" @click="removeProjectFromWorkExperience(component.id, index, pIndex)">
                        ×
                      </text>
                    </view>
                    <input
                      class="sub-input"
                      :value="project.name || ''"
                      @input="updateProjectField(component.id, index, pIndex, 'name', $event.detail.value)"
                      placeholder="项目名称"
                    />
                    <input
                      class="sub-input"
                      :value="project.role || ''"
                      @input="updateProjectField(component.id, index, pIndex, 'role', $event.detail.value)"
                      placeholder="担任角色"
                    />
                    <textarea
                      class="sub-textarea"
                      :value="project.description || ''"
                      @input="updateProjectField(component.id, index, pIndex, 'description', $event.detail.value)"
                      placeholder="项目描述"
                    />
                  </view>
                  <button class="add-sub-btn" @click="addProjectToWorkExperience(component.id, index)">
                    + 添加项目
                  </button>
                </view>
              </view>

              <!-- 技能 -->
              <view v-if="component.props?.showSkills && exp.skills" class="form-item full-width">
                <text class="item-label">使用技能</text>
                <view class="skills-tags">
                  <view
                    v-for="(skill, sIndex) in exp.skills"
                    :key="sIndex"
                    class="skill-tag"
                  >
                    <text>{{ skill }}</text>
                    <text class="tag-remove" @click="removeSkillFromWorkExperience(component.id, index, sIndex)">
                      ×
                    </text>
                  </view>
                </view>
                <view class="skill-add-area">
                  <input
                    v-if="showWorkSkillInput"
                    class="skill-input"
                    v-model="newWorkSkill"
                    placeholder="输入技能后按回车"
                    @confirm="addSkillToWorkExperience(component.id, index)"
                    @blur="addSkillToWorkExperience(component.id, index)"
                  />
                  <text
                    v-else
                    class="add-skill-btn"
                    @click="showWorkSkillInput = true"
                  >
                    + 添加技能
                  </text>
                </view>
              </view>
            </view>
          </view>

          <button class="add-section-btn" @click="addWorkExperience(component.id)">
            <text class="icon-add">+</text>
            添加工作经历
          </button>
        </view>
      </view>

      <!-- 技能专长组件 -->
      <view v-else-if="component.component === 'Skills'" class="form-section-card">
        <view class="form-section-header">
          <text class="section-title">{{ component.name || '技能专长' }}</text>
          <view class="section-divider"></view>
        </view>

        <!-- 技能列表 -->
        <view class="skills-list">
          <view
            v-for="(skill, index) in (component.props.skills || [])"
            :key="skill.id || index"
            class="skill-item"
          >
            <view class="item-header">
              <text class="item-title">技能 {{ index + 1 }}</text>
              <text
                v-if="(component.props.skills || []).length > 1"
                class="remove-btn"
                @click="removeSkillItem(component.id, index)"
              >
                删除
              </text>
            </view>

            <view class="form-grid">
              <!-- 技能名称 -->
              <view class="form-item">
                <text class="item-label">技能名称</text>
                <input
                  class="form-input"
                  :value="skill.name || ''"
                  @input="updateSkillField(component.id, index, 'name', $event.detail.value)"
                  placeholder="请输入技能名称"
                  placeholder-class="input-placeholder"
                />
              </view>

              <!-- 熟练度 -->
              <view v-if="component.props?.showSkillLevel !== false" class="form-item">
                <text class="item-label">熟练度</text>
                <view class="proficiency-input">
                  <input
                    class="form-input"
                    type="number"
                    :value="skill.proficiencyPercent || ''"
                    @input="updateSkillField(component.id, index, 'proficiencyPercent', parseInt($event.detail.value) || 0)"
                    placeholder="0-100"
                    placeholder-class="input-placeholder"
                  />
                  <text class="proficiency-unit">%</text>
                </view>
              </view>

              <!-- 经验年限 -->
              <view v-if="component.props?.showExperienceYears !== false" class="form-item">
                <text class="item-label">经验年限</text>
                <input
                  class="form-input"
                  type="number"
                  :value="skill.experienceYears || ''"
                  @input="updateSkillField(component.id, index, 'experienceYears', parseFloat($event.detail.value) || 0)"
                  placeholder="请输入经验年限"
                  placeholder-class="input-placeholder"
                />
              </view>

              <!-- 技能等级 -->
              <view v-if="component.props?.showSkillLevel !== false" class="form-item">
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

              <!-- 技能分类 -->
              <view v-if="component.props?.groupByCategory" class="form-item">
                <text class="item-label">技能分类</text>
                <input
                  class="form-input"
                  :value="skill.category || ''"
                  @input="updateSkillField(component.id, index, 'category', $event.detail.value)"
                  placeholder="例如：前端框架、数据库"
                  placeholder-class="input-placeholder"
                />
              </view>

              <!-- 技能描述 -->
              <view class="form-item full-width">
                <text class="item-label">技能描述</text>
                <textarea
                  class="form-textarea"
                  :value="skill.description || ''"
                  @input="updateSkillField(component.id, index, 'description', $event.detail.value)"
                  placeholder="请描述技能掌握情况和应用场景"
                  placeholder-class="input-placeholder"
                />
              </view>

              <!-- 证书信息 -->
              <view v-if="skill.isCertified" class="form-item full-width">
                <text class="item-label">证书信息</text>
                <input
                  class="form-input"
                  :value="skill.certificateName || ''"
                  @input="updateSkillField(component.id, index, 'certificateName', $event.detail.value)"
                  placeholder="证书名称"
                  placeholder-class="input-placeholder"
                />
                <input
                  class="form-input"
                  type="date"
                  :value="skill.certificateDate || ''"
                  @input="updateSkillField(component.id, index, 'certificateDate', $event.detail.value)"
                  placeholder="证书日期"
                  placeholder-class="input-placeholder"
                  style="margin-top: 10rpx;"
                />
              </view>
            </view>
          </view>

          <button class="add-section-btn" @click="addSkillItem(component.id)">
            <text class="icon-add">+</text>
            添加技能
          </button>
        </view>
      </view>

      <!-- 项目经历组件 -->
      <view v-else-if="component.component === 'ProjectExperience'" class="form-section-card">
        <view class="form-section-header">
          <text class="section-title">{{ component.name || '项目经历' }}</text>
          <view class="section-divider"></view>
        </view>

        <!-- 项目列表 -->
        <view class="project-list">
          <view
            v-for="(project, index) in (component.props.projects || [])"
            :key="index"
            class="project-item"
          >
            <view class="item-header">
              <text class="item-title">项目 {{ index + 1 }}</text>
              <text
                v-if="(component.props.projects || []).length > 1"
                class="remove-btn"
                @click="removeProjectItem(component.id, index)"
              >
                删除
              </text>
            </view>

            <view class="form-grid">
              <!-- 项目名称 -->
              <view class="form-item">
                <text class="item-label">项目名称</text>
                <input
                  class="form-input"
                  :value="project.name || ''"
                  @input="updateProjectItemField(component.id, index, 'name', $event.detail.value)"
                  placeholder="请输入项目名称"
                  placeholder-class="input-placeholder"
                />
              </view>

              <!-- 担任角色 -->
              <view class="form-item">
                <text class="item-label">担任角色</text>
                <input
                  class="form-input"
                  :value="project.role || ''"
                  @input="updateProjectItemField(component.id, index, 'role', $event.detail.value)"
                  placeholder="例如：前端负责人、核心开发"
                  placeholder-class="input-placeholder"
                />
              </view>

              <!-- 项目时间 -->
              <view class="form-item">
                <text class="item-label">开始时间</text>
                <picker
                  class="form-input"
                  mode="date"
                  fields="month"
                  :value="project.startDate || ''"
                  @change="(e) => updateProjectItemField(component.id, index, 'startDate', e.detail.value)"
                >
                  <view class="picker-content">
                    {{ project.startDate || '选择开始时间' }}
                  </view>
                </picker>
              </view>

              <view class="form-item">
                <text class="item-label">结束时间</text>
                <picker
                  class="form-input"
                  mode="date"
                  fields="month"
                  :value="project.endDate || ''"
                  @change="(e) => updateProjectItemField(component.id, index, 'endDate', e.detail.value)"
                >
                  <view class="picker-content">
                    {{ project.endDate || '选择结束时间' }}
                  </view>
                </picker>
              </view>

              <!-- 项目描述 -->
              <view class="form-item full-width">
                <text class="item-label">项目描述</text>
                <textarea
                  class="form-textarea"
                  :value="project.description || ''"
                  @input="updateProjectItemField(component.id, index, 'description', $event.detail.value)"
                  placeholder="请描述项目背景、职责和成果"
                  placeholder-class="input-placeholder"
                />
              </view>

              <!-- 技术栈 -->
              <view v-if="project.technologies" class="form-item full-width">
                <text class="item-label">使用技术</text>
                <view class="tech-tags">
                  <view
                    v-for="(tech, tIndex) in project.technologies"
                    :key="tIndex"
                    class="tech-tag"
                  >
                    <text>{{ tech }}</text>
                    <text class="tag-remove" @click="removeTechnology(component.id, index, tIndex)">
                      ×
                    </text>
                  </view>
                </view>
                <view class="tech-add-area">
                  <input
                    v-if="showTechInput"
                    class="tech-input"
                    v-model="newTechnology"
                    placeholder="输入技术后按回车"
                    @confirm="addTechnology(component.id, index)"
                    @blur="addTechnology(component.id, index)"
                  />
                  <text
                    v-else
                    class="add-tech-btn"
                    @click="showTechInput = true"
                  >
                    + 添加技术
                  </text>
                </view>
              </view>

              <!-- 项目成果 -->
              <view v-if="project.achievements" class="form-item full-width">
                <text class="item-label">项目成果</text>
                <view class="achievements-list">
                  <view
                    v-for="(achievement, aIndex) in project.achievements"
                    :key="aIndex"
                    class="achievement-item"
                  >
                    <input
                      class="achievement-input"
                      :value="achievement"
                      @input="updateAchievement(component.id, index, aIndex, $event.detail.value)"
                      placeholder="输入项目成果"
                    />
                    <text class="remove-achievement" @click="removeAchievement(component.id, index, aIndex)">
                      ×
                    </text>
                  </view>
                  <button class="add-achievement-btn" @click="addAchievement(component.id, index)">
                    + 添加成果
                  </button>
                </view>
              </view>
            </view>
          </view>

          <button class="add-section-btn" @click="addProjectItem(component.id)">
            <text class="icon-add">+</text>
            添加项目经历
          </button>
        </view>
      </view>

      <!-- 自我评价组件 -->
      <view v-else-if="component.component === 'SelfEvaluation'" class="form-section-card">
        <view class="form-section-header">
          <text class="section-title">{{ component.name || '自我评价' }}</text>
          <view class="section-divider"></view>
        </view>

        <view class="form-grid">
          <!-- 自我评价内容 -->
          <view class="form-item full-width">
            <text class="item-label">自我评价</text>
            <textarea
              class="form-textarea large"
              :value="getEvaluationContent(component) || ''"
              @input="updateEvaluationContent(component.id, $event.detail.value)"
              placeholder="请描述您的个人优势、工作态度和职业目标..."
              placeholder-class="input-placeholder"
            />
          </view>

          <!-- 关键词标签 -->
          <view v-if="component.props?.showKeywords !== false" class="form-item full-width">
            <text class="item-label">核心特质</text>
            <view class="keywords-input-area">
              <view class="keywords-tags">
                <view
                  v-for="(keyword, index) in (component.props.keywords || [])"
                  :key="index"
                  class="keyword-tag"
                >
                  <text>{{ keyword }}</text>
                  <text class="tag-remove" @click="removeKeyword(component.id, index)">×</text>
                </view>
              </view>
              <input
                v-if="showKeywordInput"
                class="keyword-input"
                v-model="newKeyword"
                placeholder="输入特质后按回车"
                @confirm="addKeyword(component.id)"
                @blur="addKeyword(component.id)"
              />
              <text
                v-else
                class="add-keyword-btn"
                @click="showKeywordInput = true"
              >
                + 添加特质
              </text>
            </view>
          </view>

          <!-- 优势列表 -->
          <view v-if="component.props?.showStrengths !== false" class="form-item full-width">
            <text class="item-label">个人优势</text>
            <view class="strengths-list">
              <view
                v-for="(strength, index) in (component.props.strengths || [])"
                :key="index"
                class="strength-item"
              >
                <view class="strength-header">
                  <input
                    class="strength-title-input"
                    :value="strength.title || ''"
                    @input="updateStrengthField(component.id, index, 'title', $event.detail.value)"
                    placeholder="优势标题"
                  />
                  <text class="remove-strength" @click="removeStrength(component.id, index)">×</text>
                </view>
                <textarea
                  class="strength-desc-input"
                  :value="strength.description || ''"
                  @input="updateStrengthField(component.id, index, 'description', $event.detail.value)"
                  placeholder="优势描述"
                />
              </view>
              <button class="add-strength-btn" @click="addStrength(component.id)">
                + 添加优势
              </button>
            </view>
          </view>

          <!-- 兴趣爱好 -->
          <view v-if="component.props?.showHobbies !== false" class="form-item full-width">
            <text class="item-label">兴趣爱好</text>
            <view class="hobbies-input-area">
              <view class="hobbies-tags">
                <view
                  v-for="(hobby, index) in (component.props.hobbies || [])"
                  :key="index"
                  class="hobby-tag"
                >
                  <text>{{ hobby }}</text>
                  <text class="tag-remove" @click="removeHobby(component.id, index)">×</text>
                </view>
              </view>
              <input
                v-if="showHobbyInput"
                class="hobby-input"
                v-model="newHobby"
                placeholder="输入兴趣爱好后按回车"
                @confirm="addHobby(component.id)"
                @blur="addHobby(component.id)"
              />
              <text
                v-else
                class="add-hobby-btn"
                @click="showHobbyInput = true"
              >
                + 添加兴趣
              </text>
            </view>
          </view>

          <!-- 证书资质 -->
          <view v-if="component.props?.showCertificates !== false" class="form-item full-width">
            <text class="item-label">证书资质</text>
            <view class="certificates-list">
              <view
                v-for="(cert, index) in (component.props.certificates || [])"
                :key="index"
                class="certificate-item"
              >
                <input
                  class="certificate-input"
                  :value="cert"
                  @input="updateCertificate(component.id, index, $event.detail.value)"
                  placeholder="证书名称"
                />
                <text class="remove-certificate" @click="removeCertificate(component.id, index)">
                  ×
                </text>
              </view>
              <button class="add-certificate-btn" @click="addCertificate(component.id)">
                + 添加证书
              </button>
            </view>
          </view>
        </view>
      </view>
    </template>
  </view>
</template>

<script>
export default {
  name: 'DynamicResumesFormGenerator',

  props: {
    components: {
      type: Array,
      default: () => []
    },
    resumeConfig: {
      type: Object,
      default: () => ({})
    }
  },

  data() {
    return {
      // 选项列表
      genderOptions: ['男', '女'],
      jobTypeOptions: ['全职', '兼职', '实习', '远程'],
      degreeOptions: ['初中', '高中', '大专', '本科', '硕士', '博士'],
      skillLevelOptions: ['入门', '初级', '中级', '高级', '专家'],

      // 输入状态
      showSkillInput: false,
      showKeywordInput: false,
      showHobbyInput: false,
      showTechInput: false,
      showWorkSkillInput: false,
      newSkill: '',
      newKeyword: '',
      newHobby: '',
      newTechnology: '',
      newWorkSkill: '',
      newStrengthTitle: '',
      newStrengthDesc: ''
    }
  },

  computed: {
    // 按顺序排序的组件列表
    sortedComponents() {
      const componentOrder = this.resumeConfig.layout?.componentOrder || [
        "UserBasicInfo",
        "JobIntention",
        "WorkExperience",
        "EducationExperience",
        "Skills",
        "ProjectExperience",
        "SelfEvaluation"
      ]

      const components = [...(this.components || [])]

      return components.sort((a, b) => {
        const indexA = componentOrder.indexOf(a.component)
        const indexB = componentOrder.indexOf(b.component)
        if (indexA === -1 && indexB === -1) return 0
        if (indexA === -1) return 1
        if (indexB === -1) return -1
        return indexA - indexB
      })
    }
  },

  methods: {
    // 获取选择器索引
    getPickerIndex(value, options) {
      if (!value || !options) return 0
      const index = options.indexOf(value)
      return index >= 0 ? index : 0
    },

    // 获取求职意向属性
    getIntentionProp(component, propName) {
      if (component.props.intentions && component.props.intentions.length > 0) {
        return component.props.intentions[0][propName]
      }
      return component.props[propName]
    },

    // 获取自我评价内容
    getEvaluationContent(component) {
      if (component.props.evaluations && component.props.evaluations.length > 0) {
        return component.props.evaluations[0].content
      }
      return component.props.content
    },

    // 通用更新方法
    updateComponentProp(componentId, propName, value) {
      this.$emit('update', {
        componentId,
        propPath: propName,
        value
      })
    },

    // 更新求职意向属性
    updateIntentionProp(componentId, propName, value) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      if (component.props.intentions) {
        if (!component.props.intentions[0]) {
          component.props.intentions[0] = {}
        }
        component.props.intentions[0][propName] = value
        this.$emit('update', {
          componentId,
          propPath: 'intentions',
          value: component.props.intentions
        })
      } else {
        this.$emit('update', {
          componentId,
          propPath: propName,
          value
        })
      }
    },

    // 更新自我评价内容
    updateEvaluationContent(componentId, value) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      if (component.props.evaluations) {
        if (!component.props.evaluations[0]) {
          component.props.evaluations[0] = {}
        }
        component.props.evaluations[0].content = value
        this.$emit('update', {
          componentId,
          propPath: 'evaluations',
          value: component.props.evaluations
        })
      } else {
        this.$emit('update', {
          componentId,
          propPath: 'content',
          value
        })
      }
    },

    // 教育经历相关方法
    updateEducationField(componentId, index, fieldName, value) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const educations = component.props.educations || component.props.experiences || []
      if (educations[index]) {
        educations[index][fieldName] = value

        this.$emit('update', {
          componentId,
          propPath: component.props.educations ? 'educations' : 'experiences',
          value: [...educations]
        })
      }
    },

    addEducation(componentId) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const educations = component.props.educations || component.props.experiences || []
      const newEducation = {
        school: '',
        degree: '',
        major: '',
        startDate: '',
        endDate: '',
        gpa: '',
        description: ''
      }

      if (component.props.educations) {
        component.props.educations.push(newEducation)
        this.$emit('update', {
          componentId,
          propPath: 'educations',
          value: [...component.props.educations]
        })
      } else {
        component.props.experiences = component.props.experiences || []
        component.props.experiences.push(newEducation)
        this.$emit('update', {
          componentId,
          propPath: 'experiences',
          value: [...component.props.experiences]
        })
      }
    },

    removeEducation(componentId, index) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const educations = component.props.educations || component.props.experiences || []
      if (educations.length > 1) {
        educations.splice(index, 1)

        this.$emit('update', {
          componentId,
          propPath: component.props.educations ? 'educations' : 'experiences',
          value: [...educations]
        })
      }
    },

    // 工作经历相关方法
    updateWorkExperienceField(componentId, index, fieldName, value) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const experiences = component.props.experiences || []
      if (experiences[index]) {
        experiences[index][fieldName] = value

        this.$emit('update', {
          componentId,
          propPath: 'experiences',
          value: [...experiences]
        })
      }
    },

    addWorkExperience(componentId) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      component.props.experiences = component.props.experiences || []
      component.props.experiences.push({
        company: '',
        position: '',
        startDate: '',
        endDate: '',
        description: '',
        isCurrent: false
      })

      this.$emit('update', {
        componentId,
        propPath: 'experiences',
        value: [...component.props.experiences]
      })
    },

    removeWorkExperience(componentId, index) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const experiences = component.props.experiences || []
      if (experiences.length > 1) {
        experiences.splice(index, 1)

        this.$emit('update', {
          componentId,
          propPath: 'experiences',
          value: [...experiences]
        })
      }
    },

    // 项目经历相关方法（嵌套在工作经历中）
    updateProjectField(componentId, expIndex, projectIndex, fieldName, value) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const experiences = component.props.experiences || []
      if (experiences[expIndex] && experiences[expIndex].projects && experiences[expIndex].projects[projectIndex]) {
        experiences[expIndex].projects[projectIndex][fieldName] = value

        this.$emit('update', {
          componentId,
          propPath: 'experiences',
          value: [...experiences]
        })
      }
    },

    addProjectToWorkExperience(componentId, expIndex) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const experiences = component.props.experiences || []
      if (experiences[expIndex]) {
        experiences[expIndex].projects = experiences[expIndex].projects || []
        experiences[expIndex].projects.push({
          name: '',
          role: '',
          description: ''
        })

        this.$emit('update', {
          componentId,
          propPath: 'experiences',
          value: [...experiences]
        })
      }
    },

    removeProjectFromWorkExperience(componentId, expIndex, projectIndex) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const experiences = component.props.experiences || []
      if (experiences[expIndex] && experiences[expIndex].projects) {
        experiences[expIndex].projects.splice(projectIndex, 1)

        this.$emit('update', {
          componentId,
          propPath: 'experiences',
          value: [...experiences]
        })
      }
    },

    // 技能相关方法
    addSkill(componentId) {
      if (this.newSkill.trim()) {
        const component = this.components.find(c => c.id === componentId)
        if (!component) return

        component.props.skills = component.props.skills || []
        component.props.skills.push(this.newSkill.trim())

        this.$emit('update', {
          componentId,
          propPath: 'skills',
          value: [...component.props.skills]
        })

        this.newSkill = ''
        this.showSkillInput = false
      }
    },

    removeSkill(componentId, index) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const skills = component.props.skills || []
      skills.splice(index, 1)

      this.$emit('update', {
        componentId,
        propPath: 'skills',
        value: [...skills]
      })
    },

    // 工作经历中的技能
    addSkillToWorkExperience(componentId, expIndex) {
      if (this.newWorkSkill.trim()) {
        const component = this.components.find(c => c.id === componentId)
        if (!component) return

        const experiences = component.props.experiences || []
        if (experiences[expIndex]) {
          experiences[expIndex].skills = experiences[expIndex].skills || []
          experiences[expIndex].skills.push(this.newWorkSkill.trim())

          this.$emit('update', {
            componentId,
            propPath: 'experiences',
            value: [...experiences]
          })

          this.newWorkSkill = ''
          this.showWorkSkillInput = false
        }
      }
    },

    removeSkillFromWorkExperience(componentId, expIndex, skillIndex) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const experiences = component.props.experiences || []
      if (experiences[expIndex] && experiences[expIndex].skills) {
        experiences[expIndex].skills.splice(skillIndex, 1)

        this.$emit('update', {
          componentId,
          propPath: 'experiences',
          value: [...experiences]
        })
      }
    },

    // 技能专长相关方法
    updateSkillField(componentId, index, fieldName, value) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const skills = component.props.skills || []
      if (skills[index]) {
        skills[index][fieldName] = value

        this.$emit('update', {
          componentId,
          propPath: 'skills',
          value: [...skills]
        })
      }
    },

    addSkillItem(componentId) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      component.props.skills = component.props.skills || []
      component.props.skills.push({
        name: '',
        proficiencyPercent: 0,
        experienceYears: 0,
        level: '中级',
        description: '',
        category: '',
        isCertified: false
      })

      this.$emit('update', {
        componentId,
        propPath: 'skills',
        value: [...component.props.skills]
      })
    },

    removeSkillItem(componentId, index) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const skills = component.props.skills || []
      if (skills.length > 1) {
        skills.splice(index, 1)

        this.$emit('update', {
          componentId,
          propPath: 'skills',
          value: [...skills]
        })
      }
    },

    // 项目经历组件方法
    updateProjectItemField(componentId, index, fieldName, value) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const projects = component.props.projects || []
      if (projects[index]) {
        projects[index][fieldName] = value

        this.$emit('update', {
          componentId,
          propPath: 'projects',
          value: [...projects]
        })
      }
    },

    addProjectItem(componentId) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      component.props.projects = component.props.projects || []
      component.props.projects.push({
        name: '',
        role: '',
        startDate: '',
        endDate: '',
        description: '',
        technologies: [],
        achievements: []
      })

      this.$emit('update', {
        componentId,
        propPath: 'projects',
        value: [...component.props.projects]
      })
    },

    removeProjectItem(componentId, index) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const projects = component.props.projects || []
      if (projects.length > 1) {
        projects.splice(index, 1)

        this.$emit('update', {
          componentId,
          propPath: 'projects',
          value: [...projects]
        })
      }
    },

    // 技术栈相关方法
    addTechnology(componentId, projectIndex) {
      if (this.newTechnology.trim()) {
        const component = this.components.find(c => c.id === componentId)
        if (!component) return

        const projects = component.props.projects || []
        if (projects[projectIndex]) {
          projects[projectIndex].technologies = projects[projectIndex].technologies || []
          projects[projectIndex].technologies.push(this.newTechnology.trim())

          this.$emit('update', {
            componentId,
            propPath: 'projects',
            value: [...projects]
          })

          this.newTechnology = ''
          this.showTechInput = false
        }
      }
    },

    removeTechnology(componentId, projectIndex, techIndex) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const projects = component.props.projects || []
      if (projects[projectIndex] && projects[projectIndex].technologies) {
        projects[projectIndex].technologies.splice(techIndex, 1)

        this.$emit('update', {
          componentId,
          propPath: 'projects',
          value: [...projects]
        })
      }
    },

    // 项目成果相关方法
    updateAchievement(componentId, projectIndex, achievementIndex, value) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const projects = component.props.projects || []
      if (projects[projectIndex] && projects[projectIndex].achievements) {
        projects[projectIndex].achievements[achievementIndex] = value

        this.$emit('update', {
          componentId,
          propPath: 'projects',
          value: [...projects]
        })
      }
    },

    addAchievement(componentId, projectIndex) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const projects = component.props.projects || []
      if (projects[projectIndex]) {
        projects[projectIndex].achievements = projects[projectIndex].achievements || []
        projects[projectIndex].achievements.push('')

        this.$emit('update', {
          componentId,
          propPath: 'projects',
          value: [...projects]
        })
      }
    },

    removeAchievement(componentId, projectIndex, achievementIndex) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const projects = component.props.projects || []
      if (projects[projectIndex] && projects[projectIndex].achievements) {
        projects[projectIndex].achievements.splice(achievementIndex, 1)

        this.$emit('update', {
          componentId,
          propPath: 'projects',
          value: [...projects]
        })
      }
    },

    // 自我评价相关方法
    addKeyword(componentId) {
      if (this.newKeyword.trim()) {
        const component = this.components.find(c => c.id === componentId)
        if (!component) return

        component.props.keywords = component.props.keywords || []
        component.props.keywords.push(this.newKeyword.trim())

        this.$emit('update', {
          componentId,
          propPath: 'keywords',
          value: [...component.props.keywords]
        })

        this.newKeyword = ''
        this.showKeywordInput = false
      }
    },

    removeKeyword(componentId, index) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const keywords = component.props.keywords || []
      keywords.splice(index, 1)

      this.$emit('update', {
        componentId,
        propPath: 'keywords',
        value: [...keywords]
      })
    },

    // 优势相关方法
    updateStrengthField(componentId, index, fieldName, value) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const strengths = component.props.strengths || []
      if (strengths[index]) {
        strengths[index][fieldName] = value

        this.$emit('update', {
          componentId,
          propPath: 'strengths',
          value: [...strengths]
        })
      }
    },

    addStrength(componentId) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      component.props.strengths = component.props.strengths || []
      component.props.strengths.push({
        title: '',
        description: ''
      })

      this.$emit('update', {
        componentId,
        propPath: 'strengths',
        value: [...component.props.strengths]
      })
    },

    removeStrength(componentId, index) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const strengths = component.props.strengths || []
      strengths.splice(index, 1)

      this.$emit('update', {
        componentId,
        propPath: 'strengths',
        value: [...strengths]
      })
    },

    // 兴趣爱好相关方法
    addHobby(componentId) {
      if (this.newHobby.trim()) {
        const component = this.components.find(c => c.id === componentId)
        if (!component) return

        component.props.hobbies = component.props.hobbies || []
        component.props.hobbies.push(this.newHobby.trim())

        this.$emit('update', {
          componentId,
          propPath: 'hobbies',
          value: [...component.props.hobbies]
        })

        this.newHobby = ''
        this.showHobbyInput = false
      }
    },

    removeHobby(componentId, index) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const hobbies = component.props.hobbies || []
      hobbies.splice(index, 1)

      this.$emit('update', {
        componentId,
        propPath: 'hobbies',
        value: [...hobbies]
      })
    },

    // 证书相关方法
    updateCertificate(componentId, index, value) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const certificates = component.props.certificates || []
      if (certificates[index]) {
        certificates[index] = value

        this.$emit('update', {
          componentId,
          propPath: 'certificates',
          value: [...certificates]
        })
      }
    },

    addCertificate(componentId) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      component.props.certificates = component.props.certificates || []
      component.props.certificates.push('')

      this.$emit('update', {
        componentId,
        propPath: 'certificates',
        value: [...component.props.certificates]
      })
    },

    removeCertificate(componentId, index) {
      const component = this.components.find(c => c.id === componentId)
      if (!component) return

      const certificates = component.props.certificates || []
      certificates.splice(index, 1)

      this.$emit('update', {
        componentId,
        propPath: 'certificates',
        value: [...certificates]
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.dynamic-form-generator {
  display: flex;
  flex-direction: column;
  gap: 30rpx;
}

.form-section-card {
  background: white;
  border-radius: 16rpx;
  padding: 30rpx;
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

.form-grid {
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
.skills-list,
.project-list {
  .education-item,
  .experience-item,
  .skill-item,
  .project-item {
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

.skills-input-area,
.keywords-input-area,
.hobbies-input-area {
  .skills-tags,
  .keywords-tags,
  .hobbies-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 12rpx;
    margin-bottom: 16rpx;
  }

  .skill-tag,
  .keyword-tag,
  .hobby-tag {
    display: inline-flex;
    align-items: center;
    background: #e0e7ff;
    color: #4f46e5;
    padding: 8rpx 20rpx;
    border-radius: 30rpx;
    font-size: 24rpx;
    gap: 8rpx;

    .tag-remove {
      cursor: pointer;
      color: #666;
      font-size: 28rpx;
    }
  }

  .skill-input,
  .keyword-input,
  .hobby-input {
    width: 100%;
    height: 70rpx;
    background: #f8fafc;
    border: 1rpx dashed #cbd5e1;
    border-radius: 8rpx;
    padding: 0 20rpx;
    font-size: 28rpx;
    color: #333;
  }

  .add-skill-btn,
  .add-keyword-btn,
  .add-hobby-btn {
    display: inline-block;
    background: #f8fafc;
    border: 1rpx dashed #cbd5e1;
    border-radius: 8rpx;
    padding: 16rpx 20rpx;
    color: #666;
    font-size: 26rpx;
    cursor: pointer;
  }
}

.project-sub-list {
  margin-top: 10rpx;

  .project-sub-item {
    background: white;
    border-radius: 8rpx;
    padding: 16rpx;
    margin-bottom: 16rpx;
    border: 1rpx solid #e2e8f0;

    .sub-item-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12rpx;

      .sub-item-title {
        font-size: 24rpx;
        font-weight: 500;
        color: #666;
      }

      .remove-sub-btn {
        color: #ef4444;
        font-size: 28rpx;
        cursor: pointer;
      }
    }

    .sub-input,
    .sub-textarea {
      width: 100%;
      background: #f8fafc;
      border: 1rpx solid #e2e8f0;
      border-radius: 6rpx;
      padding: 12rpx;
      font-size: 26rpx;
      color: #333;
      margin-bottom: 10rpx;
    }

    .sub-textarea {
      min-height: 80rpx;
    }
  }

  .add-sub-btn {
    width: 100%;
    padding: 12rpx;
    background: transparent;
    border: 1rpx dashed #cbd5e1;
    border-radius: 6rpx;
    color: #666;
    font-size: 24rpx;
    text-align: center;
  }
}

.skill-add-area,
.tech-add-area {
  margin-top: 10rpx;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-bottom: 10rpx;

  .tech-tag {
    display: inline-flex;
    align-items: center;
    background: #d1fae5;
    color: #059669;
    padding: 6rpx 16rpx;
    border-radius: 20rpx;
    font-size: 22rpx;
    gap: 6rpx;

    .tag-remove {
      cursor: pointer;
      color: #666;
      font-size: 24rpx;
    }
  }
}

.tech-input {
  width: 100%;
  height: 60rpx;
  background: #f8fafc;
  border: 1rpx dashed #cbd5e1;
  border-radius: 6rpx;
  padding: 0 16rpx;
  font-size: 26rpx;
  color: #333;
}

.add-tech-btn {
  display: inline-block;
  background: #f8fafc;
  border: 1rpx dashed #cbd5e1;
  border-radius: 6rpx;
  padding: 12rpx 16rpx;
  color: #666;
  font-size: 24rpx;
  cursor: pointer;
}

.achievements-list {
  .achievement-item {
    display: flex;
    align-items: center;
    gap: 10rpx;
    margin-bottom: 10rpx;

    .achievement-input {
      flex: 1;
      height: 60rpx;
      background: #f8fafc;
      border: 1rpx solid #e2e8f0;
      border-radius: 6rpx;
      padding: 0 16rpx;
      font-size: 26rpx;
      color: #333;
    }

    .remove-achievement {
      color: #ef4444;
      font-size: 30rpx;
      cursor: pointer;
    }
  }

  .add-achievement-btn {
    width: 100%;
    padding: 12rpx;
    background: transparent;
    border: 1rpx dashed #cbd5e1;
    border-radius: 6rpx;
    color: #666;
    font-size: 24rpx;
    text-align: center;
  }
}

.strengths-list {
  .strength-item {
    background: #f8fafc;
    border-radius: 8rpx;
    padding: 16rpx;
    margin-bottom: 16rpx;
    border: 1rpx solid #e2e8f0;

    .strength-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10rpx;

      .strength-title-input {
        flex: 1;
        height: 60rpx;
        background: white;
        border: 1rpx solid #e2e8f0;
        border-radius: 6rpx;
        padding: 0 16rpx;
        font-size: 26rpx;
        color: #333;
      }

      .remove-strength {
        margin-left: 10rpx;
        color: #ef4444;
        font-size: 30rpx;
        cursor: pointer;
      }
    }

    .strength-desc-input {
      width: 100%;
      min-height: 80rpx;
      background: white;
      border: 1rpx solid #e2e8f0;
      border-radius: 6rpx;
      padding: 12rpx 16rpx;
      font-size: 26rpx;
      color: #333;
    }
  }

  .add-strength-btn {
    width: 100%;
    padding: 12rpx;
    background: transparent;
    border: 1rpx dashed #cbd5e1;
    border-radius: 6rpx;
    color: #666;
    font-size: 24rpx;
    text-align: center;
  }
}

.certificates-list {
  .certificate-item {
    display: flex;
    align-items: center;
    gap: 10rpx;
    margin-bottom: 10rpx;

    .certificate-input {
      flex: 1;
      height: 60rpx;
      background: #f8fafc;
      border: 1rpx solid #e2e8f0;
      border-radius: 6rpx;
      padding: 0 16rpx;
      font-size: 26rpx;
      color: #333;
    }

    .remove-certificate {
      color: #ef4444;
      font-size: 30rpx;
      cursor: pointer;
    }
  }

  .add-certificate-btn {
    width: 100%;
    padding: 12rpx;
    background: transparent;
    border: 1rpx dashed #cbd5e1;
    border-radius: 6rpx;
    color: #666;
    font-size: 24rpx;
    text-align: center;
  }
}

.input-placeholder {
  color: #999;
  font-size: 26rpx;
}
</style>