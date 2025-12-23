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
                  <!-- 基础字段 -->
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

                  <view class="form-item">
                    <text class="item-label">出生日期</text>
                    <input
                      class="form-input"
                      :value="component.props.birthday || ''"
                      @input="(e) => updateComponentProp(component.id, 'birthday', e.detail.value)"
                      placeholder="YYYY-MM-DD"
                      placeholder-class="input-placeholder"
                    />
                  </view>

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
                        @click="removeArrayItem(component.id, 'intentions', index)"
                      >
                        删除
                      </text>
                    </view>

                    <view class="dynamic-form-grid">
                      <view class="form-item">
                        <text class="item-label">期望职位</text>
                        <input
                          class="form-input"
                          :value="intention.position || ''"
                          @input="(e) => updateArrayField(component.id, 'intentions', index, 'position', e.detail.value)"
                          placeholder="请输入期望职位"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item">
                        <text class="item-label">期望城市</text>
                        <input
                          class="form-input"
                          :value="intention.city || ''"
                          @input="(e) => updateArrayField(component.id, 'intentions', index, 'city', e.detail.value)"
                          placeholder="请输入期望城市"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item">
                        <text class="item-label">期望薪资</text>
                        <view class="salary-input">
                          <input
                            class="form-input"
                            type="text"
                            :value="intention.salary || ''"
                            @input="(e) => updateArrayField(component.id, 'intentions', index, 'salary', e.detail.value)"
                            placeholder="薪资"
                            placeholder-class="input-placeholder"
                          />
                          <text class="salary-unit">元/月</text>
                        </view>
                      </view>

                      <view class="form-item">
                        <text class="item-label">工作类型</text>
                        <picker
                          class="form-input"
                          :value="getPickerIndex(intention.jobType, jobTypeOptions)"
                          :range="jobTypeOptions"
                          @change="(e) => updateArrayField(component.id, 'intentions', index, 'jobType', jobTypeOptions[e.detail.value])"
                        >
                          <view class="picker-content">
                            {{ intention.jobType || "请选择工作类型" }}
                          </view>
                        </picker>
                      </view>
                    </view>
                  </view>

                  <button class="add-section-btn" @click="addArrayItem(component.id, 'intentions', {
                    position: '',
                    city: '',
                    salary: '',
                    jobType: '全职'
                  })">
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
                        @click="removeArrayItem(component.id, 'experiences', index)"
                      >
                        删除
                      </text>
                    </view>

                    <view class="dynamic-form-grid">
                      <view class="form-item">
                        <text class="item-label">公司名称</text>
                        <input
                          class="form-input"
                          :value="exp.company || ''"
                          @input="(e) => updateArrayField(component.id, 'experiences', index, 'company', e.detail.value)"
                          placeholder="请输入公司名称"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item">
                        <text class="item-label">职位</text>
                        <input
                          class="form-input"
                          :value="exp.position || ''"
                          @input="(e) => updateArrayField(component.id, 'experiences', index, 'position', e.detail.value)"
                          placeholder="请输入职位"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item">
                        <text class="item-label">部门</text>
                        <input
                          class="form-input"
                          :value="exp.department || ''"
                          @input="(e) => updateArrayField(component.id, 'experiences', index, 'department', e.detail.value)"
                          placeholder="请输入部门"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item">
                        <text class="item-label">开始时间</text>
                        <picker
                          class="form-input"
                          mode="date"
                          fields="month"
                          :value="exp.startDate || ''"
                          @change="(e) => updateArrayField(component.id, 'experiences', index, 'startDate', e.detail.value)"
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
                          :value="exp.endDate || ''"
                          @change="(e) => updateArrayField(component.id, 'experiences', index, 'endDate', e.detail.value)"
                        >
                          <view class="picker-content">
                            {{ exp.isCurrent ? "至今" : (exp.endDate || "选择结束时间") }}
                          </view>
                        </picker>
                      </view>

                      <view class="form-item">
                        <text class="item-label">是否在职</text>
                        <view class="checkbox-group">
                          <label class="checkbox-label">
                            <checkbox
                              :checked="exp.isCurrent || false"
                              @change="(e) => updateArrayField(component.id, 'experiences', index, 'isCurrent', e.detail.value)"
                            />
                            <text class="checkbox-text">当前在职</text>
                          </label>
                        </view>
                      </view>

                      <view class="form-item full-width">
                        <text class="item-label">工作描述</text>
                        <textarea
                          class="form-textarea"
                          :value="exp.description || ''"
                          @input="(e) => updateArrayField(component.id, 'experiences', index, 'description', e.detail.value)"
                          placeholder="请描述工作职责和成就"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item full-width">
                        <text class="item-label">主要成就</text>
                        <textarea
                          class="form-textarea"
                          :value="Array.isArray(exp.achievements) ? exp.achievements.join('、') : exp.achievements || ''"
                          @input="(e) => {
                            const value = e.detail.value;
                            const achievements = value.split('、').filter(item => item.trim());
                            updateArrayField(component.id, 'experiences', index, 'achievements', achievements);
                          }"
                          placeholder="请描述主要工作成就，用中文顿号分隔"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item full-width">
                        <text class="item-label">使用技能</text>
                        <input
                          class="form-input"
                          :value="Array.isArray(exp.skills) ? exp.skills.join(', ') : exp.skills || ''"
                          @input="(e) => {
                            const value = e.detail.value;
                            const skills = value.split(',').map(item => item.trim()).filter(item => item);
                            updateArrayField(component.id, 'experiences', index, 'skills', skills);
                          }"
                          placeholder="请输入使用的技能，用逗号分隔"
                          placeholder-class="input-placeholder"
                        />
                      </view>
                    </view>
                  </view>

                  <button class="add-section-btn" @click="addArrayItem(component.id, 'experiences', {
                    company: '',
                    position: '',
                    department: '',
                    startDate: '',
                    endDate: '',
                    description: '',
                    achievements: [],
                    skills: [],
                    isCurrent: false
                  })">
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
                        @click="removeArrayItem(component.id, 'experiences', index)"
                      >
                        删除
                      </text>
                    </view>

                    <view class="dynamic-form-grid">
                      <view class="form-item">
                        <text class="item-label">公司名称</text>
                        <input
                          class="form-input"
                          :value="exp.name || exp.company || ''"
                          @input="(e) => updateArrayField(component.id, 'experiences', index, 'name', e.detail.value)"
                          placeholder="请输入公司名称"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item">
                        <text class="item-label">职位</text>
                        <input
                          class="form-input"
                          :value="exp.position || ''"
                          @input="(e) => updateArrayField(component.id, 'experiences', index, 'position', e.detail.value)"
                          placeholder="请输入职位"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item">
                        <text class="item-label">部门</text>
                        <input
                          class="form-input"
                          :value="exp.department || ''"
                          @input="(e) => updateArrayField(component.id, 'experiences', index, 'department', e.detail.value)"
                          placeholder="请输入部门"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item">
                        <text class="item-label">开始时间</text>
                        <picker
                          class="form-input"
                          mode="date"
                          fields="month"
                          :value="exp.startDate || ''"
                          @change="(e) => updateArrayField(component.id, 'experiences', index, 'startDate', e.detail.value)"
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
                          :value="exp.endDate || ''"
                          @change="(e) => updateArrayField(component.id, 'experiences', index, 'endDate', e.detail.value)"
                        >
                          <view class="picker-content">
                            {{ exp.isCurrent ? "至今" : (exp.endDate || "选择结束时间") }}
                          </view>
                        </picker>
                      </view>

                      <view class="form-item">
                        <text class="item-label">是否在职</text>
                        <view class="checkbox-group">
                          <label class="checkbox-label">
                            <checkbox
                              :checked="exp.isCurrent || false"
                              @change="(e) => updateArrayField(component.id, 'experiences', index, 'isCurrent', e.detail.value)"
                            />
                            <text class="checkbox-text">当前在职</text>
                          </label>
                        </view>
                      </view>

                      <view class="form-item full-width">
                        <text class="item-label">工作描述</text>
                        <textarea
                          class="form-textarea"
                          :value="exp.description || ''"
                          @input="(e) => updateArrayField(component.id, 'experiences', index, 'description', e.detail.value)"
                          placeholder="请描述工作职责和成就"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item full-width">
                        <text class="item-label">工作成就</text>
                        <textarea
                          class="form-textarea"
                          :value="Array.isArray(exp.achievements) ? exp.achievements.join('、') : exp.achievements || ''"
                          @input="(e) => {
                            const value = e.detail.value;
                            const achievements = value.split('、').filter(item => item.trim());
                            updateArrayField(component.id, 'experiences', index, 'achievements', achievements);
                          }"
                          placeholder="请描述工作成就，用中文顿号分隔"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item full-width">
                        <text class="item-label">所用技能</text>
                        <input
                          class="form-input"
                          :value="Array.isArray(exp.skills) ? exp.skills.join(', ') : exp.skills || ''"
                          @input="(e) => {
                            const value = e.detail.value;
                            const skills = value.split(',').map(item => item.trim()).filter(item => item);
                            updateArrayField(component.id, 'experiences', index, 'skills', skills);
                          }"
                          placeholder="请输入使用的技能，用逗号分隔"
                          placeholder-class="input-placeholder"
                        />
                      </view>
                    </view>
                  </view>

                  <button class="add-section-btn" @click="addArrayItem(component.id, 'experiences', {
                    name: '',
                    position: '',
                    department: '',
                    startDate: '',
                    endDate: '',
                    description: '',
                    achievements: [],
                    skills: [],
                    isCurrent: false
                  })">
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
                        @click="removeArrayItem(component.id, 'experiences', index)"
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
                          @input="(e) => updateArrayField(component.id, 'experiences', index, 'name', e.detail.value)"
                          placeholder="请输入项目名称"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item">
                        <text class="item-label">担任角色</text>
                        <input
                          class="form-input"
                          :value="project.role || ''"
                          @input="(e) => updateArrayField(component.id, 'experiences', index, 'role', e.detail.value)"
                          placeholder="例如：项目经理、开发工程师"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item">
                        <text class="item-label">开始时间</text>
                        <picker
                          class="form-input"
                          mode="date"
                          fields="month"
                          :value="project.startDate || ''"
                          @change="(e) => updateArrayField(component.id, 'experiences', index, 'startDate', e.detail.value)"
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
                          @change="(e) => updateArrayField(component.id, 'experiences', index, 'endDate', e.detail.value)"
                        >
                          <view class="picker-content">
                            {{ project.endDate || "选择结束时间" }}
                          </view>
                        </picker>
                      </view>

                      <view class="form-item full-width">
                        <text class="item-label">项目描述</text>
                        <textarea
                          class="form-textarea"
                          :value="project.description || ''"
                          @input="(e) => updateArrayField(component.id, 'experiences', index, 'description', e.detail.value)"
                          placeholder="请描述项目背景、目标和成果"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item full-width">
                        <text class="item-label">使用技术</text>
                        <input
                          class="form-input"
                          :value="Array.isArray(project.technologies) ? project.technologies.join(', ') : project.technologies || ''"
                          @input="(e) => {
                            const value = e.detail.value;
                            const technologies = value.split(',').map(item => item.trim()).filter(item => item);
                            updateArrayField(component.id, 'experiences', index, 'technologies', technologies);
                          }"
                          placeholder="请输入使用的技术栈，用逗号分隔"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item full-width">
                        <text class="item-label">项目职责</text>
                        <textarea
                          class="form-textarea"
                          :value="Array.isArray(project.responsibilities) ? project.responsibilities.join('、') : project.responsibilities || ''"
                          @input="(e) => {
                            const value = e.detail.value;
                            const responsibilities = value.split('、').filter(item => item.trim());
                            updateArrayField(component.id, 'experiences', index, 'responsibilities', responsibilities);
                          }"
                          placeholder="请描述您在项目中的具体职责，用中文顿号分隔"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item full-width">
                        <text class="item-label">项目成果</text>
                        <textarea
                          class="form-textarea"
                          :value="Array.isArray(project.achievements) ? project.achievements.join('、') : project.achievements || ''"
                          @input="(e) => {
                            const value = e.detail.value;
                            const achievements = value.split('、').filter(item => item.trim());
                            updateArrayField(component.id, 'experiences', index, 'achievements', achievements);
                          }"
                          placeholder="请描述项目的主要成果和影响，用中文顿号分隔"
                          placeholder-class="input-placeholder"
                        />
                      </view>
                    </view>
                  </view>

                  <button class="add-section-btn" @click="addArrayItem(component.id, 'experiences', {
                    name: '',
                    role: '',
                    startDate: '',
                    endDate: '',
                    description: '',
                    technologies: [],
                    responsibilities: [],
                    achievements: []
                  })">
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
                        @click="removeArrayItem(component.id, 'experiences', index)"
                      >
                        删除
                      </text>
                    </view>

                    <view class="dynamic-form-grid">
                      <view class="form-item">
                        <text class="item-label">学校名称</text>
                        <input
                          class="form-input"
                          :value="edu.school || ''"
                          @input="(e) => updateArrayField(component.id, 'experiences', index, 'school', e.detail.value)"
                          placeholder="请输入学校名称"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item">
                        <text class="item-label">学历</text>
                        <picker
                          class="form-input"
                          :value="getPickerIndex(edu.degree, degreeOptions)"
                          :range="degreeOptions"
                          @change="(e) => updateArrayField(component.id, 'experiences', index, 'degree', degreeOptions[e.detail.value])"
                        >
                          <view class="picker-content">
                            {{ edu.degree || "请选择学历" }}
                          </view>
                        </picker>
                      </view>

                      <view class="form-item">
                        <text class="item-label">专业</text>
                        <input
                          class="form-input"
                          :value="edu.major || ''"
                          @input="(e) => updateArrayField(component.id, 'experiences', index, 'major', e.detail.value)"
                          placeholder="请输入专业"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item">
                        <text class="item-label">开始时间</text>
                        <picker
                          class="form-input"
                          mode="date"
                          fields="month"
                          :value="edu.startDate || ''"
                          @change="(e) => updateArrayField(component.id, 'experiences', index, 'startDate', e.detail.value)"
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
                          @change="(e) => updateArrayField(component.id, 'experiences', index, 'endDate', e.detail.value)"
                        >
                          <view class="picker-content">
                            {{ edu.endDate || "选择结束时间" }}
                          </view>
                        </picker>
                      </view>

                      <view class="form-item">
                        <text class="item-label">GPA成绩</text>
                        <input
                          class="form-input"
                          :value="edu.gpa || ''"
                          @input="(e) => updateArrayField(component.id, 'experiences', index, 'gpa', e.detail.value)"
                          placeholder="例如：3.8/4.0"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item">
                        <text class="item-label">排名</text>
                        <input
                          class="form-input"
                          :value="edu.ranking || ''"
                          @input="(e) => updateArrayField(component.id, 'experiences', index, 'ranking', e.detail.value)"
                          placeholder="例如：前10%"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item full-width">
                        <text class="item-label">在校描述</text>
                        <textarea
                          class="form-textarea"
                          :value="edu.description || ''"
                          @input="(e) => updateArrayField(component.id, 'experiences', index, 'description', e.detail.value)"
                          placeholder="请描述在校期间的成就和荣誉"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item full-width">
                        <text class="item-label">相关课程</text>
                        <input
                          class="form-input"
                          :value="Array.isArray(edu.courses) ? edu.courses.join(', ') : edu.courses || ''"
                          @input="(e) => {
                            const value = e.detail.value;
                            const courses = value.split(',').map(item => item.trim()).filter(item => item);
                            updateArrayField(component.id, 'experiences', index, 'courses', courses);
                          }"
                          placeholder="请输入相关课程，用逗号分隔"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item full-width">
                        <text class="item-label">在校成就</text>
                        <textarea
                          class="form-textarea"
                          :value="Array.isArray(edu.achievements) ? edu.achievements.join('、') : edu.achievements || ''"
                          @input="(e) => {
                            const value = e.detail.value;
                            const achievements = value.split('、').filter(item => item.trim());
                            updateArrayField(component.id, 'experiences', index, 'achievements', achievements);
                          }"
                          placeholder="请描述在校成就，用中文顿号分隔"
                          placeholder-class="input-placeholder"
                        />
                      </view>
                    </view>
                  </view>

                  <button class="add-section-btn" @click="addArrayItem(component.id, 'experiences', {
                    school: '',
                    degree: '',
                    major: '',
                    startDate: '',
                    endDate: '',
                    gpa: '',
                    ranking: '',
                    description: '',
                    courses: [],
                    achievements: []
                  })">
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
                        @click="removeArrayItem(component.id, 'skills', index)"
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
                          @input="(e) => updateArrayField(component.id, 'skills', index, 'name', e.detail.value)"
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
                            @input="(e) => updateArrayField(component.id, 'skills', index, 'proficiencyPercent', parseInt(e.detail.value) || 0)"
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
                          @input="(e) => updateArrayField(component.id, 'skills', index, 'experienceYears', parseFloat(e.detail.value) || 0)"
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
                          @change="(e) => updateArrayField(component.id, 'skills', index, 'level', skillLevelOptions[e.detail.value])"
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
                          @input="(e) => updateArrayField(component.id, 'skills', index, 'category', e.detail.value)"
                          placeholder="请输入技能分类"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item full-width">
                        <text class="item-label">技能描述</text>
                        <textarea
                          class="form-textarea"
                          :value="skill.description || ''"
                          @input="(e) => updateArrayField(component.id, 'skills', index, 'description', e.detail.value)"
                          placeholder="请描述技能掌握情况和应用场景"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item full-width">
                        <text class="item-label">技能标签</text>
                        <input
                          class="form-input"
                          :value="skill.tags || ''"
                          @input="(e) => updateArrayField(component.id, 'skills', index, 'tags', e.detail.value)"
                          placeholder="请输入技能标签，用逗号分隔"
                          placeholder-class="input-placeholder"
                        />
                      </view>

                      <view class="form-item">
                        <text class="item-label">是否认证</text>
                        <view class="checkbox-group">
                          <label class="checkbox-label">
                            <checkbox
                              :checked="skill.isCertified || false"
                              @change="(e) => updateArrayField(component.id, 'skills', index, 'isCertified', e.detail.value)"
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
                            @input="(e) => updateArrayField(component.id, 'skills', index, 'certificateName', e.detail.value)"
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
                            @change="(e) => updateArrayField(component.id, 'skills', index, 'certificateDate', e.detail.value)"
                          >
                            <view class="picker-content">
                              {{ skill.certificateDate || "选择获证日期" }}
                            </view>
                          </picker>
                        </view>
                      </template>
                    </view>
                  </view>

                  <button class="add-section-btn" @click="addArrayItem(component.id, 'skills', {
                    name: '',
                    proficiencyPercent: 0,
                    experienceYears: 0,
                    level: '中级',
                    category: '',
                    description: '',
                    tags: '',
                    isCertified: false,
                    certificateName: '',
                    certificateDate: ''
                  })">
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
                        @click="removeArrayItem(component.id, 'evaluations', index)"
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
                          @input="(e) => updateArrayField(component.id, 'evaluations', index, 'content', e.detail.value)"
                          placeholder="请描述您的个人优势、工作态度和职业目标..."
                          placeholder-class="input-placeholder"
                        />
                      </view>
                    </view>
                  </view>

                  <button class="add-section-btn" @click="addArrayItem(component.id, 'evaluations', {
                    content: ''
                  })">
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
            <DynamicResumesRenderer :resume-data="previewData" ref="dynamicResumesRenderer" />
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
import { computed, ref } from "vue";
import { onLoad, onShow } from "@dcloudio/uni-app";
import DynamicResumesRenderer from "@/components/DynamicResumesRenderer.vue";
import ResumesAPI from "@/api/resumes";

// 响应式数据
const loading = ref(false);
const resumeId = ref(null);
const templateId = ref(null);
const hasLoadedData = ref(false); // 标记是否已加载数据
const navigateFailed = ref(false); // 跳转失败标记
const resumeConfig = ref({
  templateId: null,
  globalStyle: {},
  globalLayout: {},
  updatedAt: null
});

// 编辑数据
const editingData = ref({
  id: null,
  templateId: null,
  globalStyle: {},
  globalLayout: {},
  components: []
});

// 预览数据
const previewData = computed(() => {
  // 处理组件数据，确保与渲染器期望的数据结构一致
  const components = (editingData.value.components || []).map(component => {
    // 确保每个组件都有正确的数据结构
    return {
      id: component.id,
      componentId: component.componentId,
      name: component.name,
      key: component.key,
      defaultConfig: component.defaultConfig || {},
      props: component.props || {},
      styles: component.styles || {}
    };
  });

  return {
    id: editingData.value.id,
    templateId: editingData.value.templateId,
    globalStyle: editingData.value.globalStyle || {},
    globalLayout: editingData.value.globalLayout || {},
    components: components
  };
});

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
  "three-column": "三列",
  "creative": "创意",
};

// 计算属性
const getComponentKey = (component) => {
  return component.key || "";
};

// 获取排序后的组件（基于 globalLayout.componentOrder）
const sortedComponents = computed(() => {
  if (!editingData.value?.components) return [];

  const components = [...editingData.value.components];
  const componentOrder = editingData.value.globalLayout?.componentOrder || [];

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

// 检查是否需要跳转到选择页面
const checkAndNavigateToSelect = () => {
  console.log("检查参数:", { resumeId: resumeId.value, templateId: templateId.value });

  if (!resumeId.value && !templateId.value) {
    console.log("参数为空，跳转到选择页面");
    uni.showToast({
      title: "请选择模板或简历",
      icon: "none",
      duration: 1500
    });

    setTimeout(() => {
      uni.navigateTo({
        url: "/pages/template/select",
        success: () => {
          console.log("成功跳转到选择页面");
        },
        fail: (err) => {
          console.error("跳转失败:", err);
          navigateFailed.value = true;
          loading.value = false;
        }
      });
    }, 1500);
    return true;
  }
  return false;
};


// 加载简历数据
const loadResumeData = async () => {
  // 先检查是否需要跳转
  if (checkAndNavigateToSelect()) {
    return;
  }

  loading.value = true;

  try {
    let response;

    // 根据 resumeId 或 templateId 加载数据
    if (resumeId.value) {
      console.log("加载已有的简历数据，resumeId:", resumeId.value);
      // 加载已有的简历数据
      response = await ResumesAPI.getById(resumeId.value);
    } else if (templateId.value) {
      console.log("基于模板创建新简历，templateId:", templateId.value);
      // 基于模板创建新简历
      response = await ResumesAPI.getPreview(templateId.value);
    } else {
      // 不应该执行到这里，因为前面已经检查了
      console.error("不应该执行到这里：resumeId和templateId都为空");
      checkAndNavigateToSelect();
      return;
    }

    // 初始化数据
    initializeData(response);

    // 标记已加载数据
    hasLoadedData.value = true;

    console.log("简历数据加载完成:", editingData.value);

  } catch (error) {
    console.error("加载简历数据失败:", error);

    // 如果加载失败，也跳转到选择页面
    if (error.response && error.response.status === 404) {
      uni.showToast({
        title: "简历或模板不存在",
        icon: "error",
      });
    } else {
      uni.showToast({
        title: "加载失败",
        icon: "error",
      });
    }

    // 延迟跳转，让用户看到提示
    setTimeout(() => {
      checkAndNavigateToSelect();
    }, 2000);
  } finally {
    loading.value = false;
  }
};

// 初始化数据
const initializeData = (data) => {
  editingData.value = {
    id: data.id || null,
    templateId: data.templateId || null,
    globalStyle: data.globalStyle || {},
    globalLayout: data.globalLayout || {},
    components: data.components || []
  };

  // 更新简历配置
  resumeConfig.value = {
    templateId: data.templateId || null,
    globalStyle: data.globalStyle || {},
    globalLayout: data.globalLayout || {},
    updatedAt: data.updatedAt || null
  };
};

// 通用更新组件属性方法
const updateComponentProp = (componentId, propName, value) => {
  const componentIndex = editingData.value.components.findIndex(c => c.id === componentId);
  if (componentIndex !== -1) {
    const component = editingData.value.components[componentIndex];

    // 确保props对象存在
    if (!component.props) {
      component.props = {};
    }

    // 更新属性
    component.props[propName] = value;

    // 触发响应式更新
    editingData.value.components.splice(componentIndex, 1, { ...component });
  }
};

// 更新数组字段
const updateArrayField = (componentId, arrayPath, index, fieldName, value) => {
  const componentIndex = editingData.value.components.findIndex(c => c.id === componentId);
  if (componentIndex !== -1) {
    const component = editingData.value.components[componentIndex];

    // 确保props对象存在
    if (!component.props) {
      component.props = {};
    }

    // 确保数组存在
    if (!component.props[arrayPath]) {
      component.props[arrayPath] = [];
    }

    // 确保数组项存在
    if (!component.props[arrayPath][index]) {
      component.props[arrayPath][index] = {};
    }

    // 更新数组项字段
    component.props[arrayPath][index][fieldName] = value;

    // 触发响应式更新
    editingData.value.components.splice(componentIndex, 1, { ...component });
  }
};

// 添加数组项
const addArrayItem = (componentId, arrayPath, defaultValue = {}) => {
  const componentIndex = editingData.value.components.findIndex(c => c.id === componentId);
  if (componentIndex !== -1) {
    const component = editingData.value.components[componentIndex];

    // 确保props对象存在
    if (!component.props) {
      component.props = {};
    }

    // 确保数组存在
    if (!component.props[arrayPath]) {
      component.props[arrayPath] = [];
    }

    // 添加新项
    component.props[arrayPath].push({
      id: Date.now() + Math.random(),
      ...defaultValue
    });

    // 触发响应式更新
    editingData.value.components.splice(componentIndex, 1, { ...component });
  }
};

// 删除数组项
const removeArrayItem = (componentId, arrayPath, index) => {
  const componentIndex = editingData.value.components.findIndex(c => c.id === componentId);
  if (componentIndex !== -1) {
    const component = editingData.value.components[componentIndex];

    if (component.props && component.props[arrayPath] && component.props[arrayPath].length > index) {
      // 删除指定索引的项
      component.props[arrayPath].splice(index, 1);

      // 触发响应式更新
      editingData.value.components.splice(componentIndex, 1, { ...component });
    }
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

// 获取主题名称
const getThemeName = (theme) => {
  return themeMap[theme] || theme || "默认";
};

// 获取布局名称
const getLayoutName = (layout) => {
  return layoutMap[layout] || layout || "单列";
};

// 获取表单数据
const getFormData = () => {
  return {
    id: editingData.value.id,
    templateId: editingData.value.templateId,
    globalStyle: editingData.value.globalStyle,
    globalLayout: editingData.value.globalLayout,
    components: editingData.value.components.map(component => ({
      id: component.id,
      componentId: component.componentId,
      name: component.name,
      key: component.key,
      defaultConfig: component.defaultConfig,
      props: component.props,
      styles: component.styles
    }))
  };
};

// 保存简历
const handleSave = async () => {
  // 检查是否已加载数据
  if (!hasLoadedData.value) {
    uni.showToast({
      title: "请先选择模板或简历",
      icon: "none"
    });
    return;
  }

  loading.value = true;
  try {
    const formData = getFormData();
    console.log("保存简历数据:", formData);

    // 调用API保存
    if (resumeId.value) {
      // 更新已有简历
      await ResumesAPI.update(resumeId.value, formData);
    } else {
      // 创建新简历
      const response = await ResumesAPI.create(formData);
      // 更新ID
      editingData.value.id = response.id;
      resumeId.value = response.id;
    }

    uni.showToast({
      title: "保存成功",
      icon: "success",
    });

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

// 重置修改
const handleReset = () => {
  // 检查是否已加载数据
  if (!hasLoadedData.value) {
    uni.showToast({
      title: "请先选择模板或简历",
      icon: "none"
    });
    return;
  }

  uni.showModal({
    title: "确认重置",
    content: "确定要重置所有修改吗？",
    success: (res) => {
      if (res.confirm) {
        // 重新加载数据
        loadResumeData();
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
  // 检查是否已加载数据
  if (!hasLoadedData.value) {
    uni.showToast({
      title: "请先选择模板或简历",
      icon: "none"
    });
    return;
  }

  uni.showModal({
    title: "发布简历",
    content: "确定要发布这份简历吗？发布后其他人可以看到您的简历。",
    success: async (res) => {
      if (res.confirm) {
        loading.value = true;
        try {
          const formData = getFormData();
          // 调用API发布简历
          await ResumesAPI.publish(resumeId.value || formData.id, {
            status: 1 // 发布状态
          });

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
  // 检查是否已加载数据
  if (!hasLoadedData.value) {
    uni.showToast({
      title: "请先选择模板或简历",
      icon: "none"
    });
    return;
  }

  uni.showToast({
    title: "预览已刷新",
    icon: "success",
    duration: 1500,
  });
};

// 下载简历
const downloadResume = () => {
  // 检查是否已加载数据
  if (!hasLoadedData.value) {
    uni.showToast({
      title: "请先选择模板或简历",
      icon: "none"
    });
    return;
  }

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

// 返回选择页面
const goToSelectPage = () => {
  uni.navigateTo({
    url: "/pages/template/select",
    success: () => {
      console.log("返回选择页面成功");
    }
  });
};

// 重试跳转
const retryNavigate = () => {
  navigateFailed.value = false;
  loading.value = true;
  setTimeout(() => {
    checkAndNavigateToSelect();
  }, 500);
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

  // 立即检查参数，如果需要跳转则不加载数据
  if (!resumeId.value && !templateId.value) {
    console.log("参数为空，直接跳转到选择页面");
    loading.value = true;
    setTimeout(() => {
      uni.navigateTo({
        url: "/pages/template/select",
        success: () => {
          loading.value = false;
        },
        fail: (err) => {
          console.error("跳转失败:", err);
          loading.value = false;
          navigateFailed.value = true;
        }
      });
    }, 100);
    return;
  }

  // 加载数据
  loadResumeData();
});

// 页面显示时再次检查（防止用户通过返回按钮返回）
onShow(() => {
  // 如果页面显示时还没有数据，检查是否需要跳转
  if (!hasLoadedData.value) {
    // 延迟检查，避免与onLoad中的跳转冲突
    setTimeout(() => {
      checkAndNavigateToSelect();
    }, 500);
  }
});
</script>

<style lang="scss" scoped>
.page-container {
  min-height: 100vh;
  background: #f8fafc;
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

    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
  }

  .save-btn {
    background: #3b82f6;
    color: white;

    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
  }

  .publish-btn {
    background: #10b981;
    color: white;

    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
  }
}

/* 加载状态 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  flex-direction: column;
  gap: 30rpx;
}

.loading-content {
  background: white;
  border-radius: 16rpx;
  padding: 60rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 30rpx;
  max-width: 600rpx;
  width: 80%;
}

.loading-spinner {
  width: 80rpx;
  height: 80rpx;
  border: 6rpx solid #e2e8f0;
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
  font-size: 32rpx;
  color: #333;
  font-weight: 500;
}

/* 跳转失败状态 */
.navigate-failed-content {
  background: white;
  border-radius: 16rpx;
  padding: 60rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 30rpx;
  max-width: 600rpx;
  width: 80%;
  text-align: center;
}

.failed-icon {
  font-size: 120rpx;
  color: #ef4444;
}

.failed-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333;
}

.failed-text {
  font-size: 28rpx;
  color: #64748b;
  line-height: 1.5;
}

.retry-btn {
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 8rpx;
  padding: 20rpx 40rpx;
  font-size: 28rpx;
  font-weight: 500;
  margin-top: 20rpx;

  &:active {
    background: #2563eb;
  }
}

.back-btn {
  background: #f1f5f9;
  color: #64748b;
  border: none;
  border-radius: 8rpx;
  padding: 20rpx 40rpx;
  font-size: 28rpx;
  font-weight: 500;

  &:active {
    background: #e2e8f0;
  }
}

.button-group {
  display: flex;
  gap: 20rpx;
  width: 100%;

  .retry-btn, .back-btn {
    flex: 1;
  }
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

  .loading-content,
  .navigate-failed-content {
    width: 90%;
    padding: 40rpx;
  }
}

/* 输入框占位符样式 */
.input-placeholder {
  color: #94a3b8;
  font-size: 28rpx;
}

/* 空状态提示 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 60vh;
  padding: 40rpx;
  text-align: center;

  .empty-icon {
    font-size: 120rpx;
    color: #cbd5e1;
    margin-bottom: 30rpx;
  }

  .empty-title {
    font-size: 36rpx;
    font-weight: 600;
    color: #64748b;
    margin-bottom: 20rpx;
  }

  .empty-text {
    font-size: 28rpx;
    color: #94a3b8;
    margin-bottom: 40rpx;
    line-height: 1.5;
  }

  .select-btn {
    background: #3b82f6;
    color: white;
    border: none;
    border-radius: 8rpx;
    padding: 20rpx 40rpx;
    font-size: 28rpx;
    font-weight: 500;

    &:active {
      background: #2563eb;
    }
  }
}

/* 当没有数据时的样式 */
.main-content.no-data {
  display: flex;
  align-items: center;
  justify-content: center;
}
.form-input {
  @extend .form-input; // 使用uni.scss中的表单样式
  // 特定样式覆盖
  border-radius: $border-radius-small;
}

.item-label {
  font-size: $font-size-base;
  color: $text-primary;
  font-weight: $font-weight-medium;
}

</style>