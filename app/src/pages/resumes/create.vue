<template>
  <view class="page-container">
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
                <text class="item-label">模板ID</text>
                <text class="item-value">{{ resumeConfig.templateId || "未设置" }}</text>
              </view>
              <view class="info-item">
                <text class="item-label">主题风格</text>
                <text class="item-value">{{ getThemeName(resumeConfig.globalStyle?.theme) }}</text>
              </view>
              <view class="info-item">
                <text class="item-label">布局类型</text>
                <text class="item-value">{{ getLayoutName(resumeConfig.globalLayout?.type) }}</text>
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
            <template v-if="getComponentKey(component) === 'UserBasicInfo'">
              <view class="form-section-card">
                <view class="form-section-header">
                  <text class="section-title">{{ component.name || "基本信息" }}</text>
                  <view class="section-divider"></view>
                </view>

                <!-- 动态表单字段 -->
                <view class="dynamic-form-grid">
                  <template v-if="getComponentShowProp(component, 'showName') !== false">
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

                  <template v-if="getComponentShowProp(component, 'showGender') !== false">
                    <view class="form-item">
                      <text class="item-label">性别</text>
                      <picker
                        class="form-input"
                        :value="getPickerIndex(component.props.gender, genderOptions)"
                        :range="genderOptions"
                        @change="(e) => updateComponentProp(component.id, 'gender', genderOptions[e.detail.value])"
                      >
                        <view class="picker-content">
                          {{ component.props.gender || "请选择性别" }}
                        </view>
                      </picker>
                    </view>
                  </template>

                  <template v-if="getComponentShowProp(component, 'showPhone') !== false">
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

                  <template v-if="getComponentShowProp(component, 'showEmail') !== false">
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

                  <template v-if="getComponentShowProp(component, 'showWorkYears') !== false">
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

                  <template v-if="getComponentShowProp(component, 'showLocation') !== false">
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

                  <template v-if="getComponentShowProp(component, 'showAvatar') !== false">
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

                  <!-- 额外字段 -->
                  <view class="form-item full-width">
                    <text class="item-label">个人网站</text>
                    <input
                      class="form-input"
                      :value="component.props.website || ''"
                      @input="(e) => updateComponentProp(component.id, 'website', e.detail.value)"
                      placeholder="请输入个人网站"
                      placeholder-class="input-placeholder"
                    />
                  </view>

                  <view class="form-item">
                    <text class="item-label">GitHub</text>
                    <input
                      class="form-input"
                      :value="component.props.github || ''"
                      @input="(e) => updateComponentProp(component.id, 'github', e.detail.value)"
                      placeholder="GitHub用户名"
                      placeholder-class="input-placeholder"
                    />
                  </view>

                  <view class="form-item">
                    <text class="item-label">微信</text>
                    <input
                      class="form-input"
                      :value="component.props.wechat || ''"
                      @input="(e) => updateComponentProp(component.id, 'wechat', e.detail.value)"
                      placeholder="微信ID"
                      placeholder-class="input-placeholder"
                    />
                  </view>

                  <view class="form-item full-width">
                    <text class="item-label">职位标题</text>
                    <input
                      class="form-input"
                      :value="component.props.title || ''"
                      @input="(e) => updateComponentProp(component.id, 'title', e.detail.value)"
                      placeholder="例如：高级工程师"
                      placeholder-class="input-placeholder"
                    />
                  </view>
                </view>
              </view>
            </template>

            <!-- 求职意向 -->
            <template v-if="getComponentKey(component) === 'JobIntention'">
              <view class="form-section-card">
                <view class="form-section-header">
                  <text class="section-title">{{ component.name || "求职意向" }}</text>
                  <view class="section-divider"></view>
                </view>

                <!-- 多求职意向列表 -->
                <view class="intentions-list">
                  <view
                    v-for="(intention, index) in component.props.intentions || []"
                    :key="intention.id || index"
                    class="intention-item"
                  >
                    <view class="item-header">
                      <text class="item-title">意向 {{ index + 1 }}</text>
                      <text
                        v-if="(component.props.intentions || []).length > 1"
                        class="remove-btn"
                        @click="removeIntention(component.id, index)"
                      >
                        删除
                      </text>
                    </view>

                    <view class="dynamic-form-grid">
                      <template v-if="getComponentShowProp(component, 'showExpectedPosition') !== false">
                        <view class="form-item">
                          <text class="item-label">期望职位</text>
                          <input
                            class="form-input"
                            :value="intention.position || ''"
                            @input="(e) => updateIntentionField(component.id, index, 'position', e.detail.value)"
                            placeholder="请输入期望职位"
                            placeholder-class="input-placeholder"
                          />
                        </view>
                      </template>

                      <template v-if="getComponentShowProp(component, 'showWorkLocation') !== false">
                        <view class="form-item">
                          <text class="item-label">期望城市</text>
                          <input
                            class="form-input"
                            :value="intention.city || ''"
                            @input="(e) => updateIntentionField(component.id, index, 'city', e.detail.value)"
                            placeholder="请输入期望城市"
                            placeholder-class="input-placeholder"
                          />
                        </view>
                      </template>

                      <template v-if="getComponentShowProp(component, 'showExpectedSalary') !== false">
                        <view class="form-item">
                          <text class="item-label">期望薪资</text>
                          <view class="salary-input">
                            <input
                              class="form-input"
                              type="number"
                              :value="intention.salary || ''"
                              @input="(e) => updateIntentionField(component.id, index, 'salary', e.detail.value)"
                              placeholder="薪资"
                              placeholder-class="input-placeholder"
                            />
                            <text class="salary-unit">{{ component.props.salaryUnit || "K" }}</text>
                          </view>
                        </view>
                      </template>

                      <template v-if="getComponentShowProp(component, 'showJobType') !== false">
                        <view class="form-item">
                          <text class="item-label">工作类型</text>
                          <picker
                            class="form-input"
                            :value="getPickerIndex(intention.jobType, jobTypeOptions)"
                            :range="jobTypeOptions"
                            @change="(e) => updateIntentionField(component.id, index, 'jobType', jobTypeOptions[e.detail.value])"
                          >
                            <view class="picker-content">
                              {{ intention.jobType || "请选择工作类型" }}
                            </view>
                          </picker>
                        </view>
                      </template>
                    </view>
                  </view>

                  <button class="add-section-btn" @click="addIntention(component.id)">
                    <text class="icon-add">+</text>
                    添加求职意向
                  </button>
                </view>
              </view>
            </template>

            <!-- 工作经历 -->
            <template v-if="getComponentKey(component) === 'WorkExperience'">
              <view class="form-section-card">
                <view class="form-section-header">
                  <text class="section-title">{{ component.name || "工作经历" }}</text>
                  <view class="section-divider"></view>
                </view>

                <view class="experience-list">
                  <view
                    v-for="(exp, index) in component.props.experiences || []"
                    :key="exp.id || index"
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
                      <template v-if="getComponentShowProp(component, 'showCompanyName') !== false">
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

                      <template v-if="getComponentShowProp(component, 'showJobTitle') !== false">
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

                      <template v-if="getComponentShowProp(component, 'showDepartment') !== false">
                        <view class="form-item">
                          <text class="item-label">部门</text>
                          <input
                            class="form-input"
                            :value="exp.department || ''"
                            @input="(e) => updateWorkExperienceField(component.id, index, 'department', e.detail.value)"
                            placeholder="请输入部门"
                            placeholder-class="input-placeholder"
                          />
                        </view>
                      </template>

                      <template v-if="getComponentShowProp(component, 'showWorkPeriod') !== false">
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
                              {{ exp.startDate || "选择开始时间" }}
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
                              {{ exp.isCurrent ? "至今" : (exp.endDate || "选择结束时间") }}
                            </view>
                          </picker>
                        </view>
                      </template>

                      <view class="form-item">
                        <text class="item-label">是否在职</text>
                        <view class="checkbox-group">
                          <label class="checkbox-label">
                            <checkbox
                              :checked="exp.isCurrent"
                              @change="(e) => updateWorkExperienceField(component.id, index, 'isCurrent', e.detail.value)"
                            />
                            <text class="checkbox-text">当前在职</text>
                          </label>
                        </view>
                      </view>

                      <template v-if="getComponentShowProp(component, 'showWorkContent') !== false">
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

                      <template v-if="getComponentShowProp(component, 'showAchievements') !== false">
                        <view class="form-item full-width">
                          <text class="item-label">主要成就</text>
                          <textarea
                            class="form-textarea"
                            :value="exp.achievements || ''"
                            @input="(e) => updateWorkExperienceField(component.id, index, 'achievements', e.detail.value)"
                            placeholder="请描述主要工作成就"
                            placeholder-class="input-placeholder"
                          />
                        </view>
                      </template>

                      <template v-if="getComponentShowProp(component, 'showSkills') !== false">
                        <view class="form-item full-width">
                          <text class="item-label">使用技能</text>
                          <input
                            class="form-input"
                            :value="exp.skills || ''"
                            @input="(e) => updateWorkExperienceField(component.id, index, 'skills', e.detail.value)"
                            placeholder="请输入使用的技能，用逗号分隔"
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

            <!-- 公司经历 -->
            <template v-if="getComponentKey(component) === 'CompanyExperience'">
              <view class="form-section-card">
                <view class="form-section-header">
                  <text class="section-title">{{ component.name || "公司经历" }}</text>
                  <view class="section-divider"></view>
                </view>

                <view class="experience-list">
                  <view
                    v-for="(exp, index) in component.props.experiences || []"
                    :key="exp.id || index"
                    class="experience-item"
                  >
                    <view class="item-header">
                      <text class="item-title">公司经历 {{ index + 1 }}</text>
                      <text
                        v-if="(component.props.experiences || []).length > 1"
                        class="remove-btn"
                        @click="removeCompanyExperience(component.id, index)"
                      >
                        删除
                      </text>
                    </view>

                    <view class="dynamic-form-grid">
                      <template v-if="getComponentShowProp(component, 'showCompanyName') !== false">
                        <view class="form-item">
                          <text class="item-label">公司名称</text>
                          <input
                            class="form-input"
                            :value="exp.company || ''"
                            @input="(e) => updateCompanyExperienceField(component.id, index, 'company', e.detail.value)"
                            placeholder="请输入公司名称"
                            placeholder-class="input-placeholder"
                          />
                        </view>
                      </template>

                      <template v-if="getComponentShowProp(component, 'showJobTitle') !== false">
                        <view class="form-item">
                          <text class="item-label">职位</text>
                          <input
                            class="form-input"
                            :value="exp.position || ''"
                            @input="(e) => updateCompanyExperienceField(component.id, index, 'position', e.detail.value)"
                            placeholder="请输入职位"
                            placeholder-class="input-placeholder"
                          />
                        </view>
                      </template>

                      <template v-if="getComponentShowProp(component, 'showWorkPeriod') !== false">
                        <view class="form-item">
                          <text class="item-label">开始时间</text>
                          <picker
                            class="form-input"
                            mode="date"
                            fields="month"
                            :value="exp.startDate || ''"
                            @change="(e) => updateCompanyExperienceField(component.id, index, 'startDate', e.detail.value)"
                          >
                            <view class="picker-content">
                              {{ exp.startDate || "选择开始时间" }}
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
                            @change="(e) => updateCompanyExperienceField(component.id, index, 'endDate', e.detail.value)"
                          >
                            <view class="picker-content">
                              {{ exp.isCurrent ? "至今" : (exp.endDate || "选择结束时间") }}
                            </view>
                          </picker>
                        </view>
                      </template>

                      <view class="form-item">
                        <text class="item-label">是否在职</text>
                        <view class="checkbox-group">
                          <label class="checkbox-label">
                            <checkbox
                              :checked="exp.isCurrent"
                              @change="(e) => updateCompanyExperienceField(component.id, index, 'isCurrent', e.detail.value)"
                            />
                            <text class="checkbox-text">当前在职</text>
                          </label>
                        </view>
                      </view>

                      <template v-if="getComponentShowProp(component, 'showWorkContent') !== false">
                        <view class="form-item full-width">
                          <text class="item-label">工作描述</text>
                          <textarea
                            class="form-textarea"
                            :value="exp.description || ''"
                            @input="(e) => updateCompanyExperienceField(component.id, index, 'description', e.detail.value)"
                            placeholder="请描述工作职责和成就"
                            placeholder-class="input-placeholder"
                          />
                        </view>
                      </template>
                    </view>
                  </view>

                  <button class="add-section-btn" @click="addCompanyExperience(component.id)">
                    <text class="icon-add">+</text>
                    添加公司经历
                  </button>
                </view>
              </view>
            </template>

            <!-- 项目经历 -->
            <template v-if="getComponentKey(component) === 'ProjectExperience'">
              <view class="form-section-card">
                <view class="form-section-header">
                  <text class="section-title">{{ component.name || "项目经历" }}</text>
                  <view class="section-divider"></view>
                </view>

                <view class="project-list">
                  <view
                    v-for="(project, index) in component.props.experiences || []"
                    :key="project.id || index"
                    class="project-item"
                  >
                    <view class="item-header">
                      <text class="item-title">项目 {{ index + 1 }}</text>
                      <text
                        v-if="(component.props.experiences || []).length > 1"
                        class="remove-btn"
                        @click="removeProjectExperience(component.id, index)"
                      >
                        删除
                      </text>
                    </view>

                    <view class="dynamic-form-grid">
                      <view class="form-item">
                        <text class="item-label">项目名称</text>
                        <input
                          class="form-input"
                          :value="project.name || ''"
                          @input="(e) => updateProjectExperienceField(component.id, index, 'name', e.detail.value)"
                          placeholder="请输入项目名称"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <template v-if="getComponentShowProp(component, 'showWorkPeriod') !== false">
                        <view class="form-item">
                          <text class="item-label">开始时间</text>
                          <picker
                            class="form-input"
                            mode="date"
                            fields="month"
                            :value="project.startDate || ''"
                            @change="(e) => updateProjectExperienceField(component.id, index, 'startDate', e.detail.value)"
                          >
                            <view class="picker-content">
                              {{ project.startDate || "选择开始时间" }}
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
                            @change="(e) => updateProjectExperienceField(component.id, index, 'endDate', e.detail.value)"
                          >
                            <view class="picker-content">
                              {{ project.endDate || "选择结束时间" }}
                            </view>
                          </picker>
                        </view>
                      </template>

                      <view class="form-item full-width">
                        <text class="item-label">项目描述</text>
                        <textarea
                          class="form-textarea"
                          :value="project.description || ''"
                          @input="(e) => updateProjectExperienceField(component.id, index, 'description', e.detail.value)"
                          placeholder="请描述项目背景、目标和成果"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <template v-if="getComponentShowProp(component, 'showSkills') !== false">
                        <view class="form-item full-width">
                          <text class="item-label">使用技术</text>
                          <input
                            class="form-input"
                            :value="project.technologies || ''"
                            @input="(e) => updateProjectExperienceField(component.id, index, 'technologies', e.detail.value)"
                            placeholder="请输入使用的技术栈，用逗号分隔"
                            placeholder-class="input-placeholder"
                          />
                        </view>
                      </template>

                      <template v-if="getComponentShowProp(component, 'showRole') !== false">
                        <view class="form-item">
                          <text class="item-label">担任角色</text>
                          <input
                            class="form-input"
                            :value="project.role || ''"
                            @input="(e) => updateProjectExperienceField(component.id, index, 'role', e.detail.value)"
                            placeholder="例如：项目经理、开发工程师"
                            placeholder-class="input-placeholder"
                          />
                        </view>
                      </template>

                      <view class="form-item full-width">
                        <text class="item-label">项目职责</text>
                        <textarea
                          class="form-textarea"
                          :value="project.responsibilities || ''"
                          @input="(e) => updateProjectExperienceField(component.id, index, 'responsibilities', e.detail.value)"
                          placeholder="请描述您在项目中的具体职责"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item full-width">
                        <text class="item-label">项目成果</text>
                        <textarea
                          class="form-textarea"
                          :value="project.achievements || ''"
                          @input="(e) => updateProjectExperienceField(component.id, index, 'achievements', e.detail.value)"
                          placeholder="请描述项目的主要成果和影响"
                          placeholder-class="input-placeholder"
                        />
                      </view>
                    </view>
                  </view>

                  <button class="add-section-btn" @click="addProjectExperience(component.id)">
                    <text class="icon-add">+</text>
                    添加项目经历
                  </button>
                </view>
              </view>
            </template>

            <!-- 教育背景 -->
            <template v-if="getComponentKey(component) === 'EducationExperience'">
              <view class="form-section-card">
                <view class="form-section-header">
                  <text class="section-title">{{ component.name || "教育背景" }}</text>
                  <view class="section-divider"></view>
                </view>

                <!-- 动态渲染教育经历列表 -->
                <view class="education-list">
                  <view
                    v-for="(edu, index) in component.props.experiences || []"
                    :key="edu.id || index"
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
                      <template v-if="getComponentShowProp(component, 'showSchoolName') !== false">
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

                      <template v-if="getComponentShowProp(component, 'showDegree') !== false">
                        <view class="form-item">
                          <text class="item-label">学历</text>
                          <picker
                            class="form-input"
                            :value="getPickerIndex(edu.degree, degreeOptions)"
                            :range="degreeOptions"
                            @change="(e) => updateEducationField(component.id, index, 'degree', degreeOptions[e.detail.value])"
                          >
                            <view class="picker-content">
                              {{ edu.degree || "请选择学历" }}
                            </view>
                          </picker>
                        </view>
                      </template>

                      <template v-if="getComponentShowProp(component, 'showMajor') !== false">
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

                      <template v-if="getComponentShowProp(component, 'showEducationPeriod') !== false">
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
                              {{ edu.startDate || "选择开始时间" }}
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
                              {{ edu.endDate || "选择结束时间" }}
                            </view>
                          </picker>
                        </view>
                      </template>

                      <template v-if="getComponentShowProp(component, 'showGPA') !== false">
                        <view class="form-item">
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

                      <template v-if="getComponentShowProp(component, 'showHonors') !== false">
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

                      <template v-if="getComponentShowProp(component, 'showCourses') !== false">
                        <view class="form-item full-width">
                          <text class="item-label">相关课程</text>
                          <input
                            class="form-input"
                            :value="edu.courses || ''"
                            @input="(e) => updateEducationField(component.id, index, 'courses', e.detail.value)"
                            placeholder="请输入相关课程，用逗号分隔"
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

            <!-- 技能专长 -->
            <template v-if="getComponentKey(component) === 'Skills'">
              <view class="form-section-card">
                <view class="form-section-header">
                  <text class="section-title">{{ component.name || "技能专长" }}</text>
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
                          step="0.5"
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
                            {{ skill.level || "请选择技能等级" }}
                          </view>
                        </picker>
                      </view>

                      <view class="form-item">
                        <text class="item-label">技能分类</text>
                        <input
                          class="form-input"
                          :value="skill.category || ''"
                          @input="(e) => updateSkillField(component.id, index, 'category', e.detail.value)"
                          placeholder="请输入技能分类"
                          placeholder-class="input-placeholder"
                        />
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

                      <view class="form-item full-width">
                        <text class="item-label">技能标签</text>
                        <input
                          class="form-input"
                          :value="skill.tags || ''"
                          @input="(e) => updateSkillField(component.id, index, 'tags', e.detail.value)"
                          placeholder="请输入技能标签，用逗号分隔"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item">
                        <text class="item-label">是否认证</text>
                        <view class="checkbox-group">
                          <label class="checkbox-label">
                            <checkbox
                              :checked="skill.isCertified"
                              @change="(e) => updateSkillField(component.id, index, 'isCertified', e.detail.value)"
                            />
                            <text class="checkbox-text">已认证</text>
                          </label>
                        </view>
                      </view>

                      <template v-if="skill.isCertified">
                        <view class="form-item">
                          <text class="item-label">证书名称</text>
                          <input
                            class="form-input"
                            :value="skill.certificateName || ''"
                            @input="(e) => updateSkillField(component.id, index, 'certificateName', e.detail.value)"
                            placeholder="请输入证书名称"
                            placeholder-class="input-placeholder"
                          />
                        </view>

                        <view class="form-item">
                          <text class="item-label">获证日期</text>
                          <picker
                            class="form-input"
                            mode="date"
                            :value="skill.certificateDate || ''"
                            @change="(e) => updateSkillField(component.id, index, 'certificateDate', e.detail.value)"
                          >
                            <view class="picker-content">
                              {{ skill.certificateDate || "选择获证日期" }}
                            </view>
                          </picker>
                        </view>
                      </template>
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
            <template v-if="getComponentKey(component) === 'SelfEvaluation'">
              <view class="form-section-card">
                <view class="form-section-header">
                  <text class="section-title">{{ component.name || "自我评价" }}</text>
                  <view class="section-divider"></view>
                </view>

                <!-- 多评价内容 -->
                <view class="evaluations-list">
                  <view
                    v-for="(evaluation, index) in component.props.evaluations || []"
                    :key="evaluation.id || index"
                    class="evaluation-item"
                  >
                    <view class="item-header">
                      <text class="item-title">评价 {{ index + 1 }}</text>
                      <text
                        v-if="(component.props.evaluations || []).length > 1"
                        class="remove-btn"
                        @click="removeEvaluation(component.id, index)"
                      >
                        删除
                      </text>
                    </view>

                    <view class="dynamic-form-grid">
                      <view class="form-item full-width">
                        <text class="item-label">自我评价</text>
                        <textarea
                          class="form-textarea large"
                          :value="evaluation.content || ''"
                          @input="(e) => updateEvaluationField(component.id, index, 'content', e.detail.value)"
                          placeholder="请描述您的个人优势、工作态度和职业目标..."
                          placeholder-class="input-placeholder"
                        />
                      </view>
                    </view>
                  </view>

                  <button class="add-section-btn" @click="addEvaluation(component.id)">
                    <text class="icon-add">+</text>
                    添加评价
                  </button>
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
            <DynamicResumesRenderer :config="previewConfig" />
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

<script setup>
import { computed, nextTick, reactive, ref } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import DynamicResumesRenderer from "@/components/DynamicResumesRenderer.vue";
import ResumesAPI from "@/api/resumes";

// 响应式数据
const loading = ref(false);
const resumeId = ref(null);
const templateId = ref(null);
const resumeConfig = reactive({});
const originalResumeConfig = reactive({});

// 选项列表
const genderOptions = ["男", "女"];
const jobTypeOptions = ["全职", "兼职", "实习", "远程"];
const degreeOptions = ["初中", "高中", "大专", "本科", "硕士", "博士"];
const skillLevelOptions = ["入门", "初级", "中级", "高级", "专家"];

// 主题映射
const themeMap = {
  "light": "明亮",
  "dark": "深色",
  "modern": "现代",
  "classic": "经典",
  "academic": "学术",
};

// 布局映射
const layoutMap = {
  "single-column": "单列",
  "two-column": "双列",
  "grid": "网格",
};

// 计算属性
const getComponentKey = (component) => {
  return component.key || "";
};

const getComponentShowProp = (component, propName) => {
  return component.props?.[propName] !== undefined
    ? component.props[propName]
    : component.defaultConfig?.props?.[propName];
};

const sortedComponents = computed(() => {
  if (!resumeConfig.components) {
    return [];
  }

  const componentOrder = resumeConfig.globalLayout?.componentOrder || [];
  const components = [...resumeConfig.components];

  if (componentOrder.length === 0) {
    return components;
  }

  const sorted = [];
  const componentMap = {};

  // 建立组件映射
  components.forEach(component => {
    const key = getComponentKey(component);
    componentMap[key] = component;
  });

  // 按照顺序添加组件
  componentOrder.forEach(key => {
    if (componentMap[key]) {
      sorted.push(componentMap[key]);
      delete componentMap[key];
    }
  });

  // 添加剩余组件
  Object.values(componentMap).forEach(component => {
    sorted.push(component);
  });

  return sorted;
});

const previewConfig = computed(() => {
  return {
    globalStyle: resumeConfig.globalStyle || {},
    globalLayout: resumeConfig.globalLayout || {},
    components: sortedComponents.value.map(component => ({
      id: component.componentId,
      name: component.name,
      key: getComponentKey(component),
      defaultConfig: component.defaultConfig || {},
      props: component.props || {},
      styles: component.styles || {},
      createdAt: component.createdAt,
      updatedAt: component.updatedAt,
      deleted: component.deleted,
      templateId: component.templateId,
      componentId: component.componentId,
    })),
  };
});

// 方法定义
// 加载简历数据
const loadResumeData = async () => {
  loading.value = true;

  try {
    // 根据 resumeId 或 templateId 加载数据
    if (resumeId.value) {
      // 加载已有的简历数据
      await loadExistingResume();
    } else if (templateId.value) {
      // 基于模板创建新简历
      await createResumeFromTemplate(templateId);
    } else {
      // 如果没有ID，使用测试数据
      await loadTestData();
    }

    // 保存原始数据用于重置
    Object.assign(originalResumeConfig, JSON.parse(JSON.stringify(resumeConfig)));

    console.log("简历数据加载完成:", resumeConfig);

  } catch (error) {
    console.error("加载简历数据失败:", error);
    uni.showToast({
      title: "加载失败",
      icon: "error",
    });
  } finally {
    loading.value = false;
  }
};

// 加载已有的简历数据
const loadExistingResume = async () => {
  try {
    const response = await ResumesAPI.getById(resumeId.value);

    Object.assign(resumeConfig, response);
  } catch (error) {
    console.error("加载简历数据失败:", error);
    throw error;
  }
};

// 基于模板创建新简历
const createResumeFromTemplate = async () => {
  try {
    const response = await ResumesAPI.getPreview(templateId.value);
    Object.assign(resumeConfig, response);
  } catch (error) {
    console.error("创建简历失败:", error);
    throw error;
  }
};

// 加载测试数据
const loadTestData = async () => {
  Object.assign(resumeConfig, {
    "id": 1,
    "createdAt": new Date().toISOString(),
    "updatedAt": new Date().toISOString(),
    "templateId": 1,
    "globalStyle": {
      "theme": "light",
      "fontFamily": "'Microsoft YaHei', 'PingFang SC', sans-serif",
      "primaryColor": "#1890ff",
      "backgroundColor": "#ffffff",
    },
    "globalLayout": {
      "type": "single-column",
    },
    "components": [],
  });
};

// 获取主题名称
const getThemeName = (theme) => {
  return themeMap[theme] || theme || "默认";
};

// 获取布局名称
const getLayoutName = (layout) => {
  return layoutMap[layout] || layout || "单列";
};

// 通用更新组件属性方法
const updateComponentProp = (componentId, propName, value) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component) {
    if (!component.props) {
      component.props = {};
    }
    component.props[propName] = value;

    // 触发视图更新
    nextTick(() => {
      // Vue 3 中通常不需要手动强制更新
    });
  }
};

// 更新求职意向字段
const updateIntentionField = (componentId, index, fieldName, value) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component) {
    if (!component.props.intentions) {
      component.props.intentions = [];
    }
    if (!component.props.intentions[index]) {
      component.props.intentions[index] = {};
    }
    component.props.intentions[index][fieldName] = value;
  }
};

// 添加求职意向
const addIntention = (componentId) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component) {
    if (!component.props.intentions) {
      component.props.intentions = [];
    }
    component.props.intentions.push({
      position: "",
      city: "",
      salary: "",
      jobType: "全职",
    });
  }
};

// 删除求职意向
const removeIntention = (componentId, index) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component && component.props.intentions) {
    component.props.intentions.splice(index, 1);
  }
};

// 更新教育经历字段
const updateEducationField = (componentId, index, fieldName, value) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component) {
    if (!component.props.experiences) {
      component.props.experiences = [];
    }
    if (!component.props.experiences[index]) {
      component.props.experiences[index] = {};
    }
    component.props.experiences[index][fieldName] = value;
  }
};

// 添加教育经历
const addEducation = (componentId) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component) {
    if (!component.props.experiences) {
      component.props.experiences = [];
    }
    component.props.experiences.push({
      school: "",
      degree: "",
      major: "",
      startDate: "",
      endDate: "",
      gpa: "",
      description: "",
      courses: "",
    });
  }
};

// 删除教育经历
const removeEducation = (componentId, index) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component && component.props.experiences) {
    component.props.experiences.splice(index, 1);
  }
};

// 更新工作经历字段
const updateWorkExperienceField = (componentId, index, fieldName, value) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component) {
    if (!component.props.experiences) {
      component.props.experiences = [];
    }
    if (!component.props.experiences[index]) {
      component.props.experiences[index] = {};
    }
    component.props.experiences[index][fieldName] = value;
  }
};

// 添加工作经历
const addWorkExperience = (componentId) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component) {
    if (!component.props.experiences) {
      component.props.experiences = [];
    }
    component.props.experiences.push({
      company: "",
      position: "",
      department: "",
      startDate: "",
      endDate: "",
      description: "",
      achievements: "",
      skills: "",
      isCurrent: false,
    });
  }
};

// 删除工作经历
const removeWorkExperience = (componentId, index) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component && component.props.experiences) {
    component.props.experiences.splice(index, 1);
  }
};

// 更新公司经历字段
const updateCompanyExperienceField = (componentId, index, fieldName, value) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component) {
    if (!component.props.experiences) {
      component.props.experiences = [];
    }
    if (!component.props.experiences[index]) {
      component.props.experiences[index] = {};
    }
    component.props.experiences[index][fieldName] = value;
  }
};

// 添加公司经历
const addCompanyExperience = (componentId) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component) {
    if (!component.props.experiences) {
      component.props.experiences = [];
    }
    component.props.experiences.push({
      company: "",
      position: "",
      startDate: "",
      endDate: "",
      description: "",
      isCurrent: false,
    });
  }
};

// 删除公司经历
const removeCompanyExperience = (componentId, index) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component && component.props.experiences) {
    component.props.experiences.splice(index, 1);
  }
};

// 更新项目经历字段
const updateProjectExperienceField = (componentId, index, fieldName, value) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component) {
    if (!component.props.experiences) {
      component.props.experiences = [];
    }
    if (!component.props.experiences[index]) {
      component.props.experiences[index] = {};
    }
    component.props.experiences[index][fieldName] = value;
  }
};

// 添加项目经历
const addProjectExperience = (componentId) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component) {
    if (!component.props.experiences) {
      component.props.experiences = [];
    }
    component.props.experiences.push({
      name: "",
      role: "",
      startDate: "",
      endDate: "",
      description: "",
      technologies: "",
      responsibilities: "",
      achievements: "",
    });
  }
};

// 删除项目经历
const removeProjectExperience = (componentId, index) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component && component.props.experiences) {
    component.props.experiences.splice(index, 1);
  }
};

// 更新技能字段
const updateSkillField = (componentId, index, fieldName, value) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component) {
    if (!component.props.skills) {
      component.props.skills = [];
    }
    if (!component.props.skills[index]) {
      component.props.skills[index] = {};
    }
    component.props.skills[index][fieldName] = value;
  }
};

// 添加技能
const addSkill = (componentId) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component) {
    if (!component.props.skills) {
      component.props.skills = [];
    }
    component.props.skills.push({
      name: "",
      proficiencyPercent: 0,
      experienceYears: 0,
      level: "中级",
      category: "",
      description: "",
      tags: "",
      isCertified: false,
      certificateName: "",
      certificateDate: "",
    });
  }
};

// 删除技能
const removeSkill = (componentId, index) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component && component.props.skills) {
    component.props.skills.splice(index, 1);
  }
};

// 更新自我评价字段
const updateEvaluationField = (componentId, index, fieldName, value) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component) {
    if (!component.props.evaluations) {
      component.props.evaluations = [];
    }
    if (!component.props.evaluations[index]) {
      component.props.evaluations[index] = {};
    }
    component.props.evaluations[index][fieldName] = value;
  }
};

// 添加自我评价
const addEvaluation = (componentId) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component) {
    if (!component.props.evaluations) {
      component.props.evaluations = [];
    }
    component.props.evaluations.push({
      content: "",
    });
  }
};

// 删除自我评价
const removeEvaluation = (componentId, index) => {
  const component = resumeConfig.components.find(c => c.id === componentId);
  if (component && component.props.evaluations) {
    component.props.evaluations.splice(index, 1);
  }
};

// 获取选择器索引
const getPickerIndex = (value, options) => {
  if (!value || !options) return 0;
  const index = options.indexOf(value);
  return index >= 0 ? index : 0;
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return "未知";
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString("zh-CN", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    });
  } catch (error) {
    return dateString;
  }
};

// 返回上一页
const handleBack = () => {
  uni.navigateBack();
};

// 保存简历
const handleSave = async () => {
  loading.value = true;
  try {
    console.log("保存简历数据:", resumeConfig);

    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 1000));

    uni.showToast({
      title: "保存成功",
      icon: "success",
    });

    // 更新原始数据
    Object.assign(originalResumeConfig, JSON.parse(JSON.stringify(resumeConfig)));
  } catch (error) {
    console.error("保存失败:", error);
    uni.showToast({
      title: "保存失败",
      icon: "error",
    });
  } finally {
    loading.value = false;
  }
};

// 预览简历
const handlePreview = () => {
  uni.showToast({
    title: "请查看右侧预览区域",
    icon: "none",
    duration: 2000,
  });
};

// 重置修改
const handleReset = () => {
  uni.showModal({
    title: "确认重置",
    content: "确定要重置所有修改吗？",
    success: (res) => {
      if (res.confirm) {
        Object.assign(resumeConfig, JSON.parse(JSON.stringify(originalResumeConfig)));
        uni.showToast({
          title: "已重置",
          icon: "success",
        });
      }
    },
  });
};

// 发布简历
const handlePublish = () => {
  uni.showModal({
    title: "发布简历",
    content: "确定要发布这份简历吗？发布后其他人可以看到您的简历。",
    success: async (res) => {
      if (res.confirm) {
        loading.value = true;
        try {
          // 这里应该调用API发布简历
          await new Promise(resolve => setTimeout(resolve, 1000));

          uni.showToast({
            title: "发布成功",
            icon: "success",
          });
        } catch (error) {
          console.error("发布失败:", error);
          uni.showToast({
            title: "发布失败",
            icon: "error",
          });
        } finally {
          loading.value = false;
        }
      }
    },
  });
};

// 刷新预览
const refreshPreview = () => {
  uni.showToast({
    title: "预览已刷新",
    icon: "success",
    duration: 1500,
  });
};

// 下载简历
const downloadResume = () => {
  uni.showModal({
    title: "下载简历",
    content: "确定要下载当前简历吗？",
    success: (res) => {
      if (res.confirm) {
        uni.showToast({
          title: "开始下载",
          icon: "success",
        });
      }
    },
  });
};

// 生命周期
onLoad((options) => {
  console.log("编辑页面参数:", options);

  if (options.resumeId) {
    resumeId.value = options.resumeId;
  }
  if (options.templateId) {
    templateId.value = options.templateId;
  }

  loadResumeData();
});
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

  @media (max-width: 768px) {
    flex-direction: column;
    height: auto;
  }
}

/* 左侧表单区域 */
.form-section {
  flex: 1;
  background: #f8fafc;
  border-right: 1rpx solid #e2e8f0;

  @media (max-width: 768px) {
    flex: none;
    height: 50vh;
    border-right: none;
    border-bottom: 1rpx solid #e2e8f0;
  }
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
    cursor: pointer;

    &:active {
      background: #e2e8f0;
    }
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
      min-width: 40rpx;
    }
  }

  .checkbox-group {
    margin-top: 10rpx;

    .checkbox-label {
      display: flex;
      align-items: center;
      gap: 10rpx;
      font-size: 28rpx;
      color: #333;
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

.intentions-list,
.education-list,
.experience-list,
.project-list,
.skills-list,
.evaluations-list {
  .intention-item,
  .education-item,
  .experience-item,
  .project-item,
  .skill-item,
  .evaluation-item {
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
      padding-bottom: 12rpx;
      border-bottom: 1rpx solid #e2e8f0;

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
    margin-top: 16rpx;

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

  @media (max-width: 768px) {
    flex: none;
    height: 50vh;
  }
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

      &:active {
        background: #e2e8f0;
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

  @media (max-width: 768px) {
    right: 0;
    background: white;
    border-top: 1rpx solid #e2e8f0;
  }

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

  .dynamic-form-grid {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    position: static;
    margin-top: 20rpx;
    padding: 20rpx;
    border-top: 1rpx solid #e2e8f0;
  }
}

/* 输入框占位符样式 */
.input-placeholder {
  color: #94a3b8;
  font-size: 28rpx;
}
</style>