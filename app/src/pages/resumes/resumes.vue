<template>
  <!-- 模式选择区域 -->
  <view v-if="!resumeId && !templateId" class="mode-selector">
    <view class="mode-title">选择操作模式</view>
    <view class="mode-buttons">
      <button class="mode-btn view-mode" @click="enterViewMode">
        <view class="mode-icon">👁️</view>
        <text class="mode-text">查看简历</text>
        <text class="mode-desc">查看已存在的简历详情</text>
      </button>
      <button class="mode-btn edit-mode" @click="enterEditMode">
        <view class="mode-icon">✏️</view>
        <text class="mode-text">编辑简历</text>
        <text class="mode-desc">创建或编辑简历</text>
      </button>
    </view>

    <!-- 输入简历ID区域 -->
    <view v-if="selectedMode === 'view'" class="resume-input-section">
      <view class="input-title">请输入简历ID</view>
      <input
        class="resume-id-input"
        v-model="inputResumeId"
        placeholder="输入要查看的简历ID"
        placeholder-class="input-placeholder"
      />
      <button class="confirm-btn" @click="loadResumeById">确认</button>
    </view>

    <!-- 选择模板区域 -->
    <view v-if="selectedMode === 'edit'" class="template-select-section">
      <view class="select-title">选择创建方式</view>
      <view class="select-options">
        <button class="select-option" @click="createFromTemplate">
          <view class="option-icon">📄</view>
          <text class="option-text">从模板创建</text>
        </button>
        <button class="select-option" @click="createFromExisting">
          <view class="option-icon">📋</view>
          <text class="option-text">从现有简历复制</text>
        </button>
      </view>
    </view>
  </view>

  <!-- 查看模式 -->
  <view v-else-if="currentMode === 'view'" class="resume-detail-container">
    <!-- 页面头部 -->
    <view class="page-header">
      <view class="header-left">
        <text class="icon-back" @click="handleBack">←</text>
        <text class="header-title">简历详情</text>
      </view>
      <view class="header-actions">
        <button v-if="resumeData.status === 1" class="share-btn" @click="handleShare">
          <text class="icon-share">📤</text>
          <text class="btn-text">分享</text>
        </button>
        <button class="edit-btn" @click="switchToEditMode">
          <text class="icon-edit">✏️</text>
          <text class="btn-text">编辑</text>
        </button>
        <button class="mode-switch-btn" @click="switchToModeSelect">
          <text class="icon-switch">🔄</text>
          <text class="btn-text">切换</text>
        </button>
      </view>
    </view>

    <!-- 简历基本信息卡片 -->
    <view class="info-card">
      <view class="card-header">
        <text class="card-title">简历信息</text>
        <view class="status-badge" :class="statusClass">
          {{ getStatusText(resumeData.status) }}
        </view>
      </view>

      <view class="info-grid">
        <view class="info-item">
          <text class="info-label">简历ID</text>
          <text class="info-value">{{ resumeData.id || "未设置" }}</text>
        </view>

        <view class="info-item">
          <text class="info-label">创建时间</text>
          <text class="info-value">{{ formatDateTime(resumeData.createdAt) }}</text>
        </view>

        <view class="info-item">
          <text class="info-label">最后更新</text>
          <text class="info-value">{{ formatDateTime(resumeData.updatedAt) }}</text>
        </view>

        <view class="info-item">
          <text class="info-label">模板ID</text>
          <text class="info-value">{{ resumeData.templateId || "未设置" }}</text>
        </view>

        <view class="info-item">
          <text class="info-label">浏览数</text>
          <text class="info-value">{{ resumeData.viewCount || 0 }} 次</text>
        </view>

        <view class="info-item">
          <text class="info-label">下载数</text>
          <text class="info-value">{{ resumeData.downloadCount || 0 }} 次</text>
        </view>
      </view>
    </view>

    <!-- 样式配置信息 -->
    <view class="config-card">
      <view class="card-header">
        <text class="card-title">样式配置</text>
      </view>

      <view class="config-grid">
        <view class="config-item">
          <text class="config-label">主题风格</text>
          <text class="config-value">{{ getThemeName(resumeData.globalStyle?.theme) }}</text>
        </view>

        <view class="config-item">
          <text class="config-label">布局类型</text>
          <text class="config-value">{{ getLayoutName(resumeData.globalLayout?.type) }}</text>
        </view>

        <view class="config-item" v-if="resumeData.globalStyle?.fontFamily">
          <text class="config-label">字体</text>
          <text class="config-value">{{ resumeData.globalStyle.fontFamily }}</text>
        </view>

        <view class="config-item">
          <text class="config-label">主色调</text>
          <view class="color-preview"
                :style="{ backgroundColor: resumeData.globalStyle?.primaryColor || '#1890ff' }"></view>
          <text class="config-value">{{ resumeData.globalStyle?.primaryColor || "#1890ff" }}</text>
        </view>
      </view>
    </view>

    <!-- 包含的组件 -->
    <view class="components-card">
      <view class="card-header">
        <text class="card-title">包含组件</text>
        <text class="components-count">{{ resumeData.components?.length || 0 }} 个</text>
      </view>

      <view class="components-list">
        <view
          v-for="component in resumeData.components"
          :key="component.id"
          class="component-item"
        >
          <text class="component-icon">{{ getComponentIcon(component.key) }}</text>
          <text class="component-name">{{ component.name }}</text>
          <text class="component-key">{{ component.key }}</text>
        </view>
      </view>
    </view>

    <!-- 简历预览区域 -->
    <view class="preview-section">
      <view class="preview-header">
        <text class="preview-title">简历预览</text>
        <view class="preview-actions">
          <button class="preview-action-btn" @click="refreshPreview">
            <text class="action-icon">🔄</text>
            <text class="action-text">刷新</text>
          </button>
          <button class="preview-action-btn" @click="handleFullscreen">
            <text class="action-icon">🖥️</text>
            <text class="action-text">全屏</text>
          </button>
        </view>
      </view>

      <!-- 动态模板引擎渲染 -->
      <view class="resume-preview">
        <DynamicResumesRenderer
          :resume-data="previewData"
          ref="dynamicResumesRenderer"
        />
      </view>
    </view>

    <!-- 操作按钮区域 -->
    <view class="action-buttons">
      <button class="action-btn delete-btn" @click="handleDelete">
        <text class="action-icon">🗑️</text>
        <text class="action-text">删除</text>
      </button>

      <button class="action-btn copy-btn" @click="handleCopy">
        <text class="action-icon">📋</text>
        <text class="action-text">复制</text>
      </button>

      <button class="action-btn download-btn" @click="handleDownload">
        <text class="action-icon">⬇️</text>
        <text class="action-text">下载</text>
      </button>

      <button
        class="action-btn status-btn"
        :class="{ 'published': resumeData.status === 1 }"
        @click="togglePublishStatus"
      >
        <text class="action-icon">{{ resumeData.status === 1 ? "🔒" : "🌐" }}</text>
        <text class="action-text">
          {{ resumeData.status === 1 ? "取消发布" : "发布简历" }}
        </text>
      </button>
    </view>

    <!-- 分享模态框 -->
    <uni-popup ref="sharePopup" type="bottom">
      <view class="share-modal">
        <view class="modal-header">
          <text class="modal-title">分享简历</text>
          <text class="modal-close" @click="closeSharePopup">✕</text>
        </view>

        <view class="share-options">
          <button class="share-option" @click="shareToWeChat">
            <view class="option-icon wechat">💬</view>
            <text class="option-text">微信好友</text>
          </button>

          <button class="share-option" @click="generateQRCode">
            <view class="option-icon qrcode">📱</view>
            <text class="option-text">生成二维码</text>
          </button>

          <button class="share-option" @click="copyShareLink">
            <view class="option-icon link">🔗</view>
            <text class="option-text">复制链接</text>
          </button>

          <button class="share-option" @click="exportAsImage">
            <view class="option-icon image">🖼️</view>
            <text class="option-text">导出为图片</text>
          </button>
        </view>

        <view v-if="qrcodeUrl" class="qrcode-section">
          <text class="qrcode-title">扫描二维码查看简历</text>
          <image :src="qrcodeUrl" class="qrcode-image" mode="widthFix" />
          <text class="qrcode-hint">该链接有效期7天</text>
        </view>
      </view>
    </uni-popup>
  </view>

  <!-- 编辑模式 -->
  <view v-else-if="currentMode === 'edit'" class="page-container">
    <!-- 顶部安全区域占位（仅APP环境需要） -->
    <view v-if="isApp && safeAreaTop > 0" class="safe-area-top" :style="{ height: safeAreaTop + 'px' }"></view>

    <!-- 主要内容区域 -->
    <view class="main-content" :class="layoutClass">
      <!-- 左侧表单区域 -->
      <scroll-view
        class="form-section"
        scroll-y="true"
        :style="formSectionStyle"
        :scroll-top="formScrollTop"
        @scroll="handleFormScroll"
      >
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
                          :value="edu.university || ''"
                          @input="(e) => updateArrayField(component.id, 'experiences', index, 'university', e.detail.value)"
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
                    university: '',
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

      <!-- 右侧/底部预览区域 -->
      <view class="preview-section fixed-preview" :style="previewSectionStyle">
        <view class="preview-container">
          <view class="preview-header">
            <text class="preview-title">简历预览</text>
            <view class="preview-actions">
              <button class="preview-action-btn" @click="refreshPreviewEdit">
                <text class="action-icon">🔄</text>
                <text class="action-text">刷新</text>
              </button>
              <button class="preview-action-btn" @click="downloadResumeEdit">
                <text class="action-icon">⬇️</text>
                <text class="action-text">下载</text>
              </button>
              <button class="preview-action-btn mode-switch-btn" @click="switchToViewMode">
                <text class="action-icon">👁️</text>
                <text class="action-text">查看</text>
              </button>
            </view>
          </view>

          <!-- 动态模板引擎渲染 -->
          <scroll-view
            class="resume-preview-container"
            scroll-y="true"
            :style="previewContainerStyle"
          >
            <DynamicResumesRenderer
              :resume-data="previewDataEdit"
              ref="dynamicResumesRendererEdit"
            />
          </scroll-view>
        </view>
      </view>
    </view>

    <!-- 编辑模式操作按钮区域 -->
    <view class="action-buttons-edit">
      <button class="reset-btn" @click="handleReset" :disabled="loading">重置修改</button>
      <button class="save-btn" @click="handleSave" :disabled="loading">保存简历</button>
      <button class="publish-btn" @click="handlePublish" :disabled="loading">发布简历</button>
      <button class="mode-switch-btn" @click="switchToViewMode" :disabled="loading">
        <text class="action-icon">👁️</text>
        <text class="action-text">查看</text>
      </button>
    </view>
  </view>

  <!-- 加载状态 -->
  <view v-if="loading" class="loading-overlay">
    <view class="loading-content">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from "vue";
import { onLoad, onShow } from "@dcloudio/uni-app";
import DynamicResumesRenderer from "@/components/DynamicResumesRenderer.vue";
import ResumesAPI from "@/api/resumes";

// 当前模式：select（选择模式）、view（查看模式）、edit（编辑模式）
const currentMode = ref('select');
const selectedMode = ref(''); // 用户选择的模式

// 查看模式数据
const loading = ref(false);
const resumeData = ref({
  id: null,
  createdAt: null,
  updatedAt: null,
  userId: null,
  templateId: null,
  status: 0,
  viewCount: 0,
  downloadCount: 0,
  globalStyle: {},
  globalLayout: {},
  components: [],
});

const resumeId = ref(null);
const templateId = ref(null);
const sharePopup = ref(null);
const qrcodeUrl = ref("");
const dynamicResumesRenderer = ref(null);
const inputResumeId = ref(""); // 用户输入的简历ID

// 编辑模式数据
const hasLoadedData = ref(false);
const editingData = ref({
  id: null,
  templateId: null,
  globalStyle: {},
  globalLayout: {},
  components: []
});

const resumeConfig = ref({
  templateId: null,
  globalStyle: {},
  globalLayout: {},
  updatedAt: null
});

const dynamicResumesRendererEdit = ref(null);

// 响应式布局相关变量
const screenWidth = ref(0);
const screenHeight = ref(0);
const isH5 = ref(false);
const isApp = ref(false);
const isWideScreen = ref(false);
const formScrollTop = ref(0);

// 安全区域变量
const safeAreaTop = ref(0);
const safeAreaBottom = ref(0);

// 计算布局类名
const layoutClass = computed(() => {
  if (currentMode.value !== 'edit') return '';

  if (isApp.value) {
    return 'app-layout'; // APP始终使用下侧布局
  }

  if (isH5.value) {
    return isWideScreen.value ? 'h5-wide-layout' : 'h5-narrow-layout';
  }

  return 'default-layout';
});

// 计算顶部偏移量
const topOffset = computed(() => {
  if (isApp.value) {
    return `${safeAreaTop.value}px`;
  } else if (isH5.value && isWideScreen.value) {
    // H5宽屏模式下，给一个默认的顶部间距，避免内容贴着浏览器顶部
    return '50px';
  }
  return '0px';
});

// 计算顶部偏移的像素值（用于高度计算）
const topOffsetValue = computed(() => {
  if (isApp.value) {
    return safeAreaTop.value;
  } else if (isH5.value && isWideScreen.value) {
    return 20; // 20px
  }
  return 0;
});

// 计算表单区域样式
const formSectionStyle = computed(() => {
  const bottomButtonHeight = '120rpx';

  if (isWideScreen.value && isH5.value) {
    // 大屏幕时，表单占据左侧50%，高度减去底部按钮和顶部偏移
    return {
      width: '50%',
      height: `calc(100vh - ${bottomButtonHeight} - ${topOffsetValue.value}px)`,
      position: 'fixed',
      left: '0',
      top: topOffset.value,
      overflow: 'hidden'
    };
  } else {
    // 小屏幕时，表单占据整个宽度，高度50%减去底部按钮
    return {
      width: '100%',
      height: `calc(50% - ${bottomButtonHeight})`,
      position: 'fixed',
      left: '0',
      top: '0',
      overflow: 'hidden'
    };
  }
});

// 计算预览区域样式
const previewSectionStyle = computed(() => {
  const bottomButtonHeight = '120rpx';

  if (isWideScreen.value && isH5.value) {
    // 大屏幕时，预览占据右侧50%，高度减去底部按钮和顶部偏移
    return {
      width: '50%',
      height: `calc(100vh - ${bottomButtonHeight} - ${topOffsetValue.value}px)`,
      position: 'fixed',
      right: '0',
      top: topOffset.value,
      borderLeft: '1px solid #e5e5e5',
      boxSizing: 'border-box',
      overflow: 'hidden'
    };
  } else {
    // 小屏幕时，预览占据整个宽度，高度50%减去底部按钮
    return {
      width: '100%',
      height: `calc(50% - ${bottomButtonHeight})`,
      position: 'fixed',
      left: '0',
      bottom: bottomButtonHeight,
      borderTop: '1px solid #e5e5e5',
      boxSizing: 'border-box',
      overflow: 'hidden'
    };
  }
});

// 计算预览容器样式
const previewContainerStyle = computed(() => {
  // 预览头部高度
  const previewHeaderHeight = '60px';

  if (isWideScreen.value && isH5.value) {
    // 大屏幕时，预览容器高度减去头部高度
    return {
      height: `calc(100% - ${previewHeaderHeight})`,
      overflowY: 'auto'
    };
  } else {
    // 小屏幕时，预览容器高度减去头部高度
    return {
      height: `calc(100% - ${previewHeaderHeight})`,
      overflowY: 'auto'
    };
  }
});

// 计算页面容器样式
const pageContainerStyle = computed(() => {
  if (currentMode.value === 'edit') {
    return {
      paddingTop: isApp.value ? `${safeAreaTop.value}px` : '0px'
    };
  }
  return {};
});

// 共享的映射数据
const themeMap = {
  "light": "明亮",
  "dark": "深色",
  "modern": "现代",
  "classic": "经典",
  "academic": "学术",
};

const layoutMap = {
  "single-column": "单列",
  "two-column": "双列",
  "three-column": "三列",
  "creative": "创意",
};

const statusMap = {
  0: "草稿",
  1: "已发布",
  2: "已归档",
};

const componentIconMap = {
  "UserBasicInfo": "👤",
  "JobIntention": "🎯",
  "WorkExperience": "💼",
  "EducationExperience": "🎓",
  "SelfEvaluation": "💭",
  "Skills": "⭐",
  "ProjectExperience": "📁",
  "CompanyExperience": "🏢",
};

// 编辑模式选项列表
const genderOptions = ["男", "女"];
const jobTypeOptions = ["全职", "兼职", "实习", "远程"];
const degreeOptions = ["初中", "高中", "大专", "本科", "硕士", "博士"];
const skillLevelOptions = ["入门", "初级", "中级", "高级", "专家"];

// 查看模式计算属性
const previewData = computed(() => {
  return {
    id: resumeData.value.id,
    templateId: resumeData.value.templateId,
    globalStyle: resumeData.value.globalStyle || {},
    globalLayout: resumeData.value.globalLayout || {},
    components: (resumeData.value.components || []).map(component => ({
      id: component.id,
      componentId: component.componentId,
      name: component.name,
      key: component.key,
      defaultConfig: component.defaultConfig || {},
      props: component.props || {},
      styles: component.styles || {},
    })),
  };
});

const statusClass = computed(() => {
  const status = resumeData.value.status;
  return {
    "status-draft": status === 0,
    "status-published": status === 1,
    "status-archived": status === 2,
  };
});

// 编辑模式计算属性
const getComponentKey = (component) => {
  return component.key || "";
};

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

const previewDataEdit = computed(() => {
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

// 监听编辑数据变化，实时更新预览
watch(editingData, () => {
  // 编辑模式下数据变化时，可以触发预览更新
  if (currentMode.value === 'edit' && dynamicResumesRendererEdit.value) {
    // 可以在这里添加预览更新逻辑
    console.log('编辑数据已更新，预览将自动刷新');
  }
}, { deep: true });

// 检测屏幕宽度变化
const checkScreenWidth = () => {
  try {
    const systemInfo = uni.getSystemInfoSync();
    screenWidth.value = systemInfo.windowWidth;
    screenHeight.value = systemInfo.windowHeight;

    // 获取安全区域
    safeAreaTop.value = systemInfo.safeAreaInsets?.top || systemInfo.statusBarHeight || 0;
    safeAreaBottom.value = systemInfo.safeAreaInsets?.bottom || 0;

    // 宽屏阈值设置为768px
    isWideScreen.value = screenWidth.value > 768;

    // 检测平台
    const platform = systemInfo.platform?.toLowerCase() || '';
    const appVersion = systemInfo.appVersion || '';

    isH5.value = platform.includes('h5') ||
      (typeof window !== 'undefined' && window.navigator) ||
      appVersion.includes('HBuilder');
    isApp.value = ['ios', 'android'].includes(platform);

    console.log('屏幕信息:', {
      width: screenWidth.value,
      height: screenHeight.value,
      safeAreaTop: safeAreaTop.value,
      safeAreaBottom: safeAreaBottom.value,
      platform: platform,
      appVersion: appVersion,
      isH5: isH5.value,
      isApp: isApp.value,
      isWideScreen: isWideScreen.value,
      windowHeight: systemInfo.windowHeight,
      screenHeight: systemInfo.screenHeight
    });
  } catch (error) {
    console.error('获取屏幕信息失败:', error);
    // 默认值
    screenWidth.value = 375;
    screenHeight.value = 667;
    safeAreaTop.value = 0;
    safeAreaBottom.value = 0;
    isH5.value = true;
    isApp.value = false;
    isWideScreen.value = false;
  }
};

// 监听屏幕旋转和尺寸变化
const onResize = () => {
  checkScreenWidth();
};

// 处理表单滚动
const handleFormScroll = (e) => {
  formScrollTop.value = e.detail.scrollTop;
};

// 模式切换方法
const enterViewMode = () => {
  selectedMode.value = 'view';
};

const enterEditMode = () => {
  selectedMode.value = 'edit';
};

const switchToModeSelect = () => {
  currentMode.value = 'select';
  resumeId.value = null;
  templateId.value = null;
  inputResumeId.value = "";
  selectedMode.value = "";
};

const switchToViewMode = () => {
  if (resumeId.value) {
    currentMode.value = 'view';
    loadResumeData();
  } else {
    uni.showToast({
      title: "请先保存简历",
      icon: "none"
    });
  }
};

const switchToEditMode = () => {
  if (resumeData.value.id) {
    currentMode.value = 'edit';
    resumeId.value = resumeData.value.id;
    initializeEditData(resumeData.value);
  }
};

const loadResumeById = async () => {
  if (!inputResumeId.value.trim()) {
    uni.showToast({
      title: "请输入简历ID",
      icon: "none"
    });
    return;
  }

  resumeId.value = inputResumeId.value;
  currentMode.value = 'view';
  loadResumeData();
};

const createFromTemplate = () => {
  uni.navigateTo({
    url: "/pages/template/select?mode=edit",
    success: () => {
      console.log("跳转到模板选择页面");
    }
  });
};

const createFromExisting = () => {
  uni.navigateTo({
    url: "/pages/resume/list?mode=copy",
    success: () => {
      console.log("跳转到简历列表页面");
    }
  });
};

// 查看模式方法
const loadResumeData = async () => {
  if (!resumeId.value) {
    uni.showToast({
      title: "简历ID不存在",
      icon: "error",
    });
    setTimeout(() => {
      switchToModeSelect();
    }, 1500);
    return;
  }

  loading.value = true;

  try {
    const response = await ResumesAPI.getById(resumeId.value);
    resumeData.value = response;

    // 增加浏览数
    await ResumesAPI.incrementViewCount(resumeId.value);

    console.log("简历详情加载完成:", resumeData.value);
  } catch (error) {
    console.error("加载简历详情失败:", error);
    uni.showToast({
      title: "加载失败",
      icon: "error",
    });
    setTimeout(() => {
      switchToModeSelect();
    }, 1500);
  } finally {
    loading.value = false;
  }
};

const formatDateTime = (dateString) => {
  if (!dateString) return "未知";

  try {
    const date = new Date(dateString);
    return date.toLocaleString("zh-CN", {
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

const getThemeName = (theme) => {
  return themeMap[theme] || theme || "默认";
};

const getLayoutName = (layout) => {
  return layoutMap[layout] || layout || "默认";
};

const getStatusText = (status) => {
  return statusMap[status] || "未知";
};

const getComponentIcon = (key) => {
  return componentIconMap[key] || "📄";
};

const handleBack = () => {
  if (currentMode.value === 'view' || currentMode.value === 'edit') {
    switchToModeSelect();
  } else {
    uni.navigateBack();
  }
};

const handleShare = () => {
  if (resumeData.value.status !== 1) {
    uni.showModal({
      title: "简历未发布",
      content: "请先发布简历后才能分享",
      showCancel: false,
      success: (res) => {
        if (res.confirm) {
          togglePublishStatus();
        }
      },
    });
    return;
  }

  if (sharePopup.value) {
    sharePopup.value.open();
  }
};

const closeSharePopup = () => {
  if (sharePopup.value) {
    sharePopup.value.close();
  }
  qrcodeUrl.value = "";
};

const handleDelete = () => {
  uni.showModal({
    title: "确认删除",
    content: "确定要删除这份简历吗？删除后无法恢复。",
    confirmText: "删除",
    confirmColor: "#ef4444",
    success: async (res) => {
      if (res.confirm) {
        loading.value = true;
        try {
          await ResumesAPI.delete(resumeId.value);
          uni.showToast({
            title: "删除成功",
            icon: "success",
          });

          setTimeout(() => {
            switchToModeSelect();
          }, 1500);
        } catch (error) {
          console.error("删除失败:", error);
          uni.showToast({
            title: "删除失败",
            icon: "error",
          });
        } finally {
          loading.value = false;
        }
      }
    },
  });
};

const handleCopy = () => {
  uni.showModal({
    title: "复制简历",
    content: "确定要复制这份简历吗？",
    success: async (res) => {
      if (res.confirm) {
        loading.value = true;
        try {
          const copyData = {
            templateId: resumeData.value.templateId,
            globalStyle: { ...resumeData.value.globalStyle },
            globalLayout: { ...resumeData.value.globalLayout },
            components: resumeData.value.components.map(comp => ({
              componentId: comp.componentId,
              name: comp.name,
              key: comp.key,
              defaultConfig: comp.defaultConfig,
              props: { ...comp.props },
              styles: { ...comp.styles },
            })),
          };

          await ResumesAPI.create(copyData);
          uni.showToast({
            title: "复制成功",
            icon: "success",
          });
        } catch (error) {
          console.error("复制失败:", error);
          uni.showToast({
            title: "复制失败",
            icon: "error",
          });
        } finally {
          loading.value = false;
        }
      }
    },
  });
};

const handleDownload = async () => {
  loading.value = true;
  try {
    // 增加下载数
    await ResumesAPI.incrementDownloadCount(resumeId.value);

    uni.showToast({
      title: "开始下载",
      icon: "success",
    });

    // 模拟下载过程
    setTimeout(() => {
      uni.showModal({
        title: "下载提示",
        content: "简历已准备好下载，请选择格式",
        showCancel: true,
        cancelText: "PDF",
        confirmText: "图片",
        success: (res) => {
          if (res.confirm) {
            uni.showToast({
              title: "图片格式下载中",
              icon: "success",
            });
          } else if (res.cancel) {
            uni.showToast({
              title: "PDF格式下载中",
              icon: "success",
            });
          }
        },
      });
    }, 1000);

  } catch (error) {
    console.error("下载失败:", error);
    uni.showToast({
      title: "下载失败",
      icon: "error",
    });
  } finally {
    loading.value = false;
  }
};

const togglePublishStatus = () => {
  const newStatus = resumeData.value.status === 1 ? 0 : 1;
  const action = newStatus === 1 ? "发布" : "取消发布";

  uni.showModal({
    title: `${action}简历`,
    content: newStatus === 1
      ? "确定要发布这份简历吗？发布后其他人可以看到您的简历。"
      : "确定要取消发布这份简历吗？取消后其他人将无法查看。",
    success: async (res) => {
      if (res.confirm) {
        loading.value = true;
        try {
          await ResumesAPI.updateStatus(resumeId.value, newStatus);
          resumeData.value.status = newStatus;

          uni.showToast({
            title: `${action}成功`,
            icon: "success",
          });
        } catch (error) {
          console.error(`${action}失败:`, error);
          uni.showToast({
            title: `${action}失败`,
            icon: "error",
          });
        } finally {
          loading.value = false;
        }
      }
    },
  });
};

const shareToWeChat = () => {
  uni.share({
    provider: "weixin",
    scene: "WXSceneSession",
    type: 0,
    href: `${getBaseUrl()}/resume/share/${resumeId.value}`,
    title: "我的简历",
    summary: "查看我的个人简历",
    success: function(res) {
      console.log("分享成功:", res);
      uni.showToast({
        title: "分享成功",
        icon: "success",
      });
    },
    fail: function(err) {
      console.log("分享失败:", err);
      uni.showToast({
        title: "分享失败",
        icon: "error",
      });
    },
  });
};

const generateQRCode = async () => {
  const shareUrl = `${getBaseUrl()}/resume/share/${resumeId.value}`;

  try {
    // 这里可以调用生成二维码的接口
    // 暂时使用模拟数据
    qrcodeUrl.value = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + encodeURIComponent(shareUrl);

    uni.showToast({
      title: "二维码生成成功",
      icon: "success",
    });
  } catch (error) {
    console.error("生成二维码失败:", error);
    uni.showToast({
      title: "生成失败",
      icon: "error",
    });
  }
};

const copyShareLink = () => {
  const shareUrl = `${getBaseUrl()}/resume/share/${resumeId.value}`;

  uni.setClipboardData({
    data: shareUrl,
    success: () => {
      uni.showToast({
        title: "链接已复制",
        icon: "success",
      });
    },
    fail: () => {
      uni.showToast({
        title: "复制失败",
        icon: "error",
      });
    },
  });
};

const exportAsImage = () => {
  uni.showModal({
    title: "导出为图片",
    content: "确定要将简历导出为图片吗？",
    success: (res) => {
      if (res.confirm) {
        uni.showLoading({
          title: "正在生成图片...",
        });

        // 这里可以调用截图或生成图片的接口
        setTimeout(() => {
          uni.hideLoading();
          uni.showToast({
            title: "图片已保存到相册",
            icon: "success",
          });
        }, 2000);
      }
    },
  });
};

const refreshPreview = () => {
  if (dynamicResumesRenderer.value) {
    // 可以添加重新加载逻辑
    uni.showToast({
      title: "预览已刷新",
      icon: "success",
      duration: 1500,
    });
  }
};

const handleFullscreen = () => {
  uni.navigateTo({
    url: `/pages/resume/preview?resumeId=${resumeId.value}`,
  });
};

const getBaseUrl = () => {
  // 返回基础URL，这里需要根据实际情况修改
  return "https://your-domain.com";
};

// 编辑模式方法
const initializeEditData = (data) => {
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

  hasLoadedData.value = true;
};

const loadEditData = async () => {
  loading.value = true;

  try {
    let response;

    // 根据 resumeId 或 templateId 加载数据
    if (resumeId.value) {
      console.log("加载已有的简历数据，resumeId:", resumeId.value);
      response = await ResumesAPI.getById(resumeId.value);
    } else if (templateId.value) {
      console.log("基于模板创建新简历，templateId:", templateId.value);
      response = await ResumesAPI.getPreview(templateId.value);
    } else {
      console.error("不应该执行到这里：resumeId和templateId都为空");
      return;
    }

    // 初始化数据
    initializeEditData(response);

    console.log("简历数据加载完成:", editingData.value);

  } catch (error) {
    console.error("加载简历数据失败:", error);

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

    // 延迟返回选择模式
    setTimeout(() => {
      switchToModeSelect();
    }, 2000);
  } finally {
    loading.value = false;
  }
};

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

const getPickerIndex = (value, options) => {
  if (!value || !options) return 0;
  const index = options.indexOf(value);
  return index >= 0 ? index : 0;
};

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

    let response;
    // 调用API保存
    if (resumeId.value) {
      // 更新已有简历
      response = await ResumesAPI.update(resumeId.value, formData);
    } else {
      // 创建新简历
      response = await ResumesAPI.create(formData);
      // 更新ID
      editingData.value.id = response.id;
      resumeId.value = response.id;
    }

    uni.showToast({
      title: "保存成功",
      icon: "success",
    });

    // 更新查看模式的数据
    if (currentMode.value === 'edit') {
      const updatedData = await ResumesAPI.getById(resumeId.value);
      resumeData.value = updatedData;
    }

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
        loadEditData();
        uni.showToast({
          title: "已重置",
          icon: "success",
        });
      }
    },
  });
};

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
          // 首先确保简历已保存
          if (!resumeId.value) {
            const saveResponse = await ResumesAPI.create(formData);
            resumeId.value = saveResponse.id;
          }

          // 调用API发布简历
          await ResumesAPI.updateStatus(resumeId.value, 1);

          // 更新本地数据
          editingData.value.status = 1;
          resumeData.value.status = 1;

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

const refreshPreviewEdit = () => {
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

const downloadResumeEdit = () => {
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

// 生命周期
onMounted(() => {
  checkScreenWidth();
  // 监听窗口尺寸变化（仅H5环境）
  if (isH5.value) {
    window.addEventListener('resize', onResize);
  }
});

onUnmounted(() => {
  if (isH5.value) {
    window.removeEventListener('resize', onResize);
  }
});

onLoad((options) => {
  console.log("合并页面参数:", options);
  checkScreenWidth(); // 初始化时检查一次

  // 根据参数决定进入哪种模式
  if (options.resumeId) {
    resumeId.value = options.resumeId;
    if (options.mode === 'edit') {
      currentMode.value = 'edit';
      loadEditData();
    } else {
      currentMode.value = 'view';
      loadResumeData();
    }
  } else if (options.templateId) {
    templateId.value = options.templateId;
    currentMode.value = 'edit';
    loadEditData();
  } else if (options.mode) {
    // 如果指定了模式但没有ID，显示模式选择器
    selectedMode.value = options.mode;
  }
});

onShow(() => {
  checkScreenWidth(); // 每次显示时检查一次

  // 页面显示时检查是否需要重新加载
  if (currentMode.value === 'view' && resumeId.value && !resumeData.value.id) {
    loadResumeData();
  } else if (currentMode.value === 'edit' && (resumeId.value || templateId.value) && !hasLoadedData.value) {
    loadEditData();
  }
});
</script>

<style lang="scss" scoped>
/* ==================== 模式选择器样式 ==================== */
.mode-selector {
  min-height: 100vh;
  background: linear-gradient(135deg, color.adjust($primary-color, $lightness: -10%) 0%, color.adjust($primary-color, $saturation: 20%) 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $padding-base;
}

.mode-title {
  font-size: $font-size-extra-large;
  font-weight: $font-weight-bold;
  color: $background-color-white;
  margin-bottom: $margin-base * 1.5;
  text-align: center;
}

.mode-buttons {
  display: flex;
  flex-direction: column;
  gap: $margin-base;
  width: 100%;
  max-width: 600rpx;
}

.mode-btn {
  background: rgba($background-color-white, 0.95);
  border: none;
  border-radius: $border-radius-large;
  padding: $padding-base * 2 $padding-base * 1.33;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: $margin-mini;
  box-shadow: $box-shadow-dark;
  transition: all $transition-normal $ease-in-out;

  &:active {
    transform: translateY(5rpx);
    box-shadow: $box-shadow;
  }

  &.view-mode {
    border-left: 12rpx solid color.adjust($primary-color, $lightness: -10%);
  }

  &.edit-mode {
    border-left: 12rpx solid $success-color;
  }
}

.mode-icon {
  font-size: 80rpx;
  margin-bottom: 10rpx;
}

.mode-text {
  font-size: $font-size-large;
  font-weight: $font-weight-semibold;
  color: $text-primary;
}

.mode-desc {
  font-size: $font-size-base;
  color: $text-secondary;
  text-align: center;
}

/* ==================== 输入简历ID区域 ==================== */
.resume-input-section {
  background: rgba($background-color-white, 0.95);
  border-radius: $border-radius-large;
  padding: $padding-base;
  margin-top: $margin-base;
  width: 100%;
  max-width: 600rpx;
  box-shadow: $box-shadow-dark;
}

.input-title {
  font-size: $font-size-medium;
  font-weight: $font-weight-semibold;
  color: $text-primary;
  margin-bottom: $margin-base * 0.75;
  text-align: center;
}

.resume-id-input {
  width: 100%;
  height: $input-height;
  background: $background-color;
  border: 2rpx solid $border-color-lighter;
  border-radius: $border-radius-small;
  padding: 0 24rpx;
  font-size: $font-size-base;
  color: $text-primary;
  margin-bottom: $margin-base * 0.75;
  transition: all $transition-fast $ease-in-out;

  &:focus {
    border-color: $primary-color;
    box-shadow: $input-focus-shadow;
  }
}

.confirm-btn {
  width: 100%;
  height: $button-height;
  background: $primary-color;
  color: $background-color-white;
  border: none;
  border-radius: $border-radius-small;
  font-size: $font-size-medium;
  font-weight: $font-weight-semibold;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color $transition-fast $ease-in-out;

  &:active {
    background: color.adjust($primary-color, $lightness: -10%);
  }
}

/* ==================== 选择模板区域 ==================== */
.template-select-section {
  background: rgba($background-color-white, 0.95);
  border-radius: $border-radius-large;
  padding: $padding-base;
  margin-top: $margin-base;
  width: 100%;
  max-width: 600rpx;
  box-shadow: $box-shadow-dark;
}

.select-title {
  font-size: $font-size-medium;
  font-weight: $font-weight-semibold;
  color: $text-primary;
  margin-bottom: $margin-base;
  text-align: center;
}

.select-options {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: $margin-base * 0.75;
}

.select-option {
  background: $background-color;
  border: 2rpx solid $border-color-lighter;
  border-radius: $border-radius;
  padding: $padding-base $padding-mini;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $margin-mini;
  transition: all $transition-normal $ease-in-out;

  &:active {
    background: $border-color-lighter;
    transform: translateY(5rpx);
  }
}

.option-icon {
  font-size: 60rpx;
}

.option-text {
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: $text-primary;
}

/* ==================== 查看模式样式 ==================== */
.resume-detail-container {
  min-height: 100vh;
  background: $background-color;
  padding-bottom: 120rpx;
}

/* 页面头部 */
.page-header {
  background: $background-color-white;
  padding: $padding-mini $padding-small;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: $box-shadow-light;
  position: sticky;
  top: 0;
  left: 0;
  right: 0;
  z-index: $z-index-modal;

  .header-left {
    display: flex;
    align-items: center;
    gap: 16rpx;

    .icon-back {
      font-size: $font-size-large;
      color: $text-secondary;
      cursor: pointer;
      padding: 8rpx;
      border-radius: $border-radius-small;

      &:active {
        background: $background-color;
      }
    }

    .header-title {
      font-size: $font-size-medium;
      font-weight: $font-weight-semibold;
      color: $text-primary;
    }
  }

  .header-actions {
    display: flex;
    gap: 16rpx;

    .share-btn,
    .edit-btn,
    .mode-switch-btn {
      display: flex;
      align-items: center;
      gap: 8rpx;
      padding: 12rpx 24rpx;
      border-radius: $border-radius-small;
      font-size: $font-size-small;
      font-weight: $font-weight-medium;
      border: none;
      transition: opacity $transition-fast $ease-in-out;

      .btn-text {
        font-size: $font-size-small;
      }

      &:active {
        opacity: 0.8;
      }
    }

    .share-btn {
      background: $background-color;
      color: $text-secondary;
    }

    .edit-btn {
      background: $primary-color;
      color: $background-color-white;
    }

    .mode-switch-btn {
      background: $success-color;
      color: $background-color-white;
    }
  }
}

/* 信息卡片样式 */
.info-card,
.config-card,
.components-card {
  background: $background-color-white;
  border-radius: $border-radius;
  padding: $padding-small;
  margin: $margin-base * 0.75;
  box-shadow: $box-shadow-light;
  border: 1rpx solid $border-color-lighter;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $margin-base * 0.75;
    padding-bottom: $padding-mini;
    border-bottom: 1rpx solid $background-color;

    .card-title {
      font-size: $font-size-base * 1.07;
      font-weight: $font-weight-semibold;
      color: $text-primary;
    }

    .status-badge {
      padding: 6rpx 16rpx;
      border-radius: 20rpx;
      font-size: $font-size-extra-small;
      font-weight: $font-weight-medium;

      &.status-draft {
        background: $warning-light;
        color: $warning-color;
      }

      &.status-published {
        background: $success-bg;
        color: $success-color;
      }

      &.status-archived {
        background: rgba($info-color, 0.1);
        color: $info-color;
      }
    }

    .components-count {
      font-size: $font-size-extra-small;
      color: $text-secondary;
      background: $background-color;
      padding: 4rpx 12rpx;
      border-radius: $border-radius-small;
    }
  }
}

/* 信息网格 */
.info-grid,
.config-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $margin-mini;

  .info-item,
  .config-item {
    .info-label,
    .config-label {
      display: block;
      font-size: $font-size-small;
      color: $text-secondary;
      margin-bottom: 8rpx;
    }

    .info-value,
    .config-value {
      display: block;
      font-size: $font-size-base;
      color: $text-primary;
      font-weight: $font-weight-medium;
      word-break: break-all;
    }
  }
}

/* 配置项特殊样式 */
.config-item {
  display: flex;
  align-items: center;
  gap: 12rpx;

  .color-preview {
    width: 24rpx;
    height: 24rpx;
    border-radius: 4rpx;
    border: 1rpx solid $border-color-lighter;
  }
}

/* 组件列表 */
.components-list {
  display: grid;
  gap: 16rpx;

  .component-item {
    display: flex;
    align-items: center;
    gap: 16rpx;
    padding: $padding-mini;
    background: $background-color;
    border-radius: $border-radius-small;
    border: 1rpx solid $border-color-lighter;

    .component-icon {
      font-size: $font-size-medium;
      width: 40rpx;
      text-align: center;
    }

    .component-name {
      flex: 1;
      font-size: $font-size-base;
      color: $text-primary;
      font-weight: $font-weight-medium;
    }

    .component-key {
      font-size: $font-size-extra-small;
      color: $text-secondary;
      background: $background-color;
      padding: 4rpx 12rpx;
      border-radius: $border-radius-small;
    }
  }
}

/* 查看模式预览区域 */
.resume-detail-container .preview-section {
  background: $background-color-white;
  margin: $margin-base * 0.75;
  border-radius: $border-radius;
  overflow: hidden;
  box-shadow: $box-shadow-light;
  border: 1rpx solid $border-color-lighter;

  .preview-header {
    padding: $padding-mini $padding-small;
    border-bottom: 1rpx solid $border-color-lighter;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .preview-title {
      font-size: $font-size-base * 1.07;
      font-weight: $font-weight-semibold;
      color: $text-primary;
    }

    .preview-actions {
      display: flex;
      gap: 16rpx;

      .preview-action-btn {
        display: flex;
        align-items: center;
        gap: 8rpx;
        background: $background-color;
        border: 1rpx solid $border-color;
        border-radius: $border-radius-small;
        padding: 12rpx 20rpx;
        font-size: $font-size-extra-small;
        color: $text-secondary;
        transition: background-color $transition-fast $ease-in-out;

        .action-icon {
          font-size: $font-size-extra-small;
        }

        &:active {
          background: $border-color-lighter;
        }
      }
    }
  }

  .resume-preview {
    height: 800rpx;
    overflow-y: auto;
    padding: $padding-small;
    background: $background-color;
  }
}

/* 查看模式操作按钮区域 */
.resume-detail-container .action-buttons {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: $background-color-white;
  padding: $padding-mini $padding-small;
  display: flex;
  gap: 16rpx;
  border-top: 1rpx solid $border-color-lighter;
  z-index: $z-index-modal - 1;

  .action-btn {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8rpx;
    padding: $padding-mini;
    border-radius: $border-radius-small;
    border: none;
    font-size: $font-size-extra-small;
    transition: background-color $transition-fast $ease-in-out;

    .action-icon {
      font-size: $font-size-medium;
    }

    .action-text {
      font-size: 22rpx;
    }

    &.delete-btn {
      background: $danger-bg;
      color: $danger-color;

      &:active {
        background: color.adjust($danger-color, $alpha: -0.9);
      }
    }

    &.copy-btn {
      background: rgba(color.adjust($primary-color, $lightness: 20%), 0.1);
      color: color.adjust($primary-color, $lightness: -20%);

      &:active {
        background: rgba(color.adjust($primary-color, $lightness: 20%), 0.2);
      }
    }

    &.download-btn {
      background: $success-bg;
      color: $success-color;

      &:active {
        background: color.adjust($success-color, $alpha: -0.9);
      }
    }

    &.status-btn {
      background: $warning-light;
      color: $warning-color;

      &.published {
        background: $success-bg;
        color: $success-color;
      }

      &:active {
        opacity: 0.8;
      }
    }
  }
}

/* ==================== 安全区域样式 ==================== */
.safe-area-top {
  background: transparent;
  width: 100%;
  position: absolute;
  top: 0;
  left: 0;
  z-index: 1;
  pointer-events: none;
}

/* ==================== 编辑模式响应式布局 ==================== */
.page-container {
  min-height: 100vh;
  background: $background-color;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  box-sizing: border-box;
}

/* 主要内容区域 */
.main-content {
  flex: 1;
  position: relative;
  width: 100%;
  height: calc(100vh - 120rpx); /* 减去底部按钮高度 */
  overflow: hidden;
  box-sizing: border-box;
}

/* 默认布局（适用于未知平台或默认情况） */
.default-layout {
  .form-section {
    width: 100%;
    height: 50%;
    position: fixed;
    left: 0;
    top: 0;
    z-index: 10;
    background: $background-color-white;
    border-bottom: 1px solid $border-color-lighter;
  }

  .preview-section.fixed-preview {
    width: 100%;
    height: 50%;
    position: fixed;
    left: 0;
    bottom: 0;
    z-index: 10;
    background: $background-color-white;
    border-top: 1px solid $border-color-lighter;
  }
}

/* H5环境 - 窄屏布局（预览固定在底部） */
.h5-narrow-layout {
  .form-section {
    width: 100%;
    height: 50%;
    position: fixed;
    left: 0;
    top: 0;
    z-index: 10;
    background: $background-color-white;
    border-bottom: 1px solid $border-color-lighter;
  }

  .preview-section.fixed-preview {
    width: 100%;
    height: 50%;
    position: fixed;
    left: 0;
    bottom: 0;
    z-index: 10;
    background: $background-color-white;
    border-top: 1px solid $border-color-lighter;
  }
}

/* H5环境 - 宽屏布局（预览固定在右侧） */
.h5-wide-layout {
  .form-section {
    width: 50%;
    height: calc(100vh - 120rpx); /* 减去底部按钮高度 */
    position: fixed;
    left: 0;
    top: 0;
    z-index: 10;
    background: $background-color-white;
    border-right: 1px solid $border-color-lighter;
    overflow-y: auto;
    box-sizing: border-box;
  }

  .preview-section.fixed-preview {
    width: 50%;
    height: calc(100vh - 120rpx); /* 减去底部按钮高度 */
    position: fixed;
    right: 0;
    top: 0;
    z-index: 10;
    background: $background-color-white;
    border-left: 1px solid $border-color-lighter;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    box-sizing: border-box;
  }
}

/* APP环境布局（预览固定在底部） */
.app-layout {
  .form-section {
    width: 100%;
    height: 50%;
    position: fixed;
    left: 0;
    top: 0;
    z-index: 10;
    background: $background-color-white;
    border-bottom: 1px solid $border-color-lighter;
    overflow-y: auto;
  }

  .preview-section.fixed-preview {
    width: 100%;
    height: 50%;
    position: fixed;
    left: 0;
    bottom: 60px; /* 底部按钮高度 */
    z-index: 10;
    background: $background-color-white;
    border-top: 1px solid $border-color-lighter;
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }
}

/* 表单区域 */
.form-section {
  overflow: hidden;

  .form-container {
    padding: $padding-small;
    padding-bottom: 180rpx;
  }
}

.resume-info-card {
  background: $background-color-white;
  border-radius: $border-radius;
  padding: $padding-small;
  margin-bottom: $margin-base * 0.75;
  box-shadow: $box-shadow-light;
  border: 1rpx solid $border-color-lighter;

  .info-title {
    display: block;
    font-size: $font-size-medium;
    font-weight: $font-weight-semibold;
    color: $text-primary;
    margin-bottom: $margin-mini;
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
    border-bottom: 1rpx solid $background-color;

    &:last-child {
      border-bottom: none;
    }

    .item-label {
      font-size: $font-size-small;
      color: $text-secondary;
    }

    .item-value {
      font-size: $font-size-small;
      color: $text-primary;
      font-weight: $font-weight-medium;
    }
  }
}

.form-section-card {
  background: $background-color-white;
  border-radius: $border-radius;
  padding: $padding-small;
  margin-bottom: $margin-base * 0.75;
  box-shadow: $box-shadow-light;
  border: 1rpx solid $border-color-lighter;

  .form-section-header {
    margin-bottom: $margin-base * 0.75;

    .section-title {
      display: block;
      font-size: $font-size-base * 1.07;
      font-weight: $font-weight-semibold;
      color: $text-primary;
      margin-bottom: 16rpx;
    }

    .section-divider {
      height: 2rpx;
      background: linear-gradient(90deg, $primary-color, $success-color);
      border-radius: 1rpx;
    }
  }
}

/* 动态表单网格响应式调整 */
.dynamic-form-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24rpx;

  // 在H5宽屏布局下使用两列
  .h5-wide-layout & {
    @media (min-width: 769px) {
      grid-template-columns: repeat(2, 1fr);
    }
  }

  // 在APP和H5窄屏布局下始终使用单列
  .h5-narrow-layout &,
  .app-layout & {
    grid-template-columns: 1fr;
  }
}

.form-item {
  &.full-width {
    grid-column: 1 / -1;
  }

  .item-label {
    display: block;
    font-size: $font-size-small;
    color: $text-regular;
    margin-bottom: 12rpx;
    font-weight: $font-weight-medium;
  }

  .form-input {
    width: 100%;
    height: $input-height;
    background: $background-color;
    border: 1rpx solid $border-color-lighter;
    border-radius: $border-radius-small;
    padding: 0 $padding-mini;
    font-size: $font-size-base;
    color: $text-primary;
    transition: all $transition-fast $ease-in-out;

    &:focus {
      border-color: $primary-color;
      box-shadow: $input-focus-shadow;
    }
  }

  .picker-content {
    height: $input-height;
    background: $background-color;
    border: 1rpx solid $border-color-lighter;
    border-radius: $border-radius-small;
    padding: 0 $padding-mini;
    display: flex;
    align-items: center;
    font-size: $font-size-base;
    color: $text-primary;
    cursor: pointer;
    transition: background-color $transition-fast $ease-in-out;

    &:active {
      background: $border-color-lighter;
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
      font-size: $font-size-base;
      color: $text-secondary;
      min-width: 40rpx;
    }
  }

  .checkbox-group {
    margin-top: 10rpx;

    .checkbox-label {
      display: flex;
      align-items: center;
      gap: 10rpx;
      font-size: $font-size-base;
      color: $text-primary;
    }
  }

  .form-textarea {
    width: 100%;
    min-height: 120rpx;
    background: $background-color;
    border: 1rpx solid $border-color-lighter;
    border-radius: $border-radius-small;
    padding: $padding-mini;
    font-size: $font-size-base;
    color: $text-primary;
    transition: all $transition-fast $ease-in-out;

    &:focus {
      border-color: $primary-color;
      box-shadow: $input-focus-shadow;
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
    background: $background-color;
    border-radius: $border-radius-small;
    padding: 24rpx;
    margin-bottom: 24rpx;
    border: 1rpx solid $border-color-lighter;

    .item-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: $margin-mini;
      padding-bottom: 12rpx;
      border-bottom: 1rpx solid $border-color-lighter;

      .item-title {
        font-size: $font-size-base;
        font-weight: $font-weight-semibold;
        color: $text-primary;
      }

      .remove-btn {
        color: $danger-color;
        font-size: $font-size-extra-small;
        cursor: pointer;
        padding: 8rpx 16rpx;
        border-radius: $border-radius-small;
        background: rgba($danger-color, 0.1);
        transition: background-color $transition-fast $ease-in-out;

        &:active {
          background: rgba($danger-color, 0.2);
        }
      }
    }
  }

  .add-section-btn {
    width: 100%;
    height: $input-height;
    background: transparent;
    border: 1rpx dashed $border-color;
    border-radius: $border-radius-small;
    color: $text-secondary;
    font-size: $font-size-base;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8rpx;
    margin-top: 16rpx;
    transition: background-color $transition-fast $ease-in-out;

    .icon-add {
      font-size: $font-size-medium;
    }

    &:active {
      background: $background-color;
    }
  }
}

/* 预览区域 */
.preview-section.fixed-preview {
  display: flex;
  flex-direction: column;
  box-sizing: border-box;

  .preview-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    height: 100%;
    width: 100%;
  }

  .preview-header {
    padding: 8rpx 16rpx;
    border-bottom: 1rpx solid $border-color-lighter;
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: $background-color-white;
    flex-shrink: 0;
    min-height: 48px;
    height: 48px;
    box-sizing: border-box;

    .preview-title {
      font-size: $font-size-base;
      font-weight: $font-weight-semibold;
      color: $text-primary;
      line-height: 1.4;
    }

    .preview-actions {
      display: flex;
      gap: 8rpx;
      flex-wrap: nowrap;

      .preview-action-btn {
        display: flex;
        align-items: center;
        gap: 4rpx;
        background: $background-color;
        border: 1rpx solid $border-color;
        border-radius: $border-radius-small;
        padding: 8rpx 12rpx;
        font-size: $font-size-extra-small;
        color: $text-secondary;
        white-space: nowrap;
        transition: background-color $transition-fast $ease-in-out;
        height: 32px;
        min-height: 32px;
        box-sizing: border-box;

        .action-icon {
          font-size: $font-size-small;
        }

        .action-text {
          font-size: $font-size-extra-small;
        }

        &:active {
          background: $border-color-lighter;
        }

        &.mode-switch-btn {
          background: $success-color;
          color: $background-color-white;
        }
      }
    }
  }

  .resume-preview-container {
    flex: 1;
    overflow-y: auto;
    width: 100%;
    height: calc(100% - 48px); /* 减去头部高度 */

    .resume-preview {
      padding: $padding-small;
      background: $background-color;
      min-height: 100%;
      box-sizing: border-box;
    }
  }
}

/* 编辑模式操作按钮区域 */
.action-buttons-edit {
  background: $background-color-white;
  padding: $padding-mini $padding-small;
  display: flex;
  gap: $margin-mini;
  border-top: 1rpx solid $border-color-lighter;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 60px;
  min-height: 60px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);

  @media (max-width: 768px) {
    flex-wrap: wrap;
    height: auto;
    min-height: auto;
    padding: 8rpx;
  }

  .reset-btn,
  .save-btn,
  .publish-btn,
  .mode-switch-btn {
    flex: 1;
    height: 44px;
    min-height: 44px;
    border-radius: $border-radius-small;
    font-size: $font-size-small;
    font-weight: $font-weight-medium;
    border: none;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6rpx;
    transition: opacity $transition-fast $ease-in-out;
    white-space: nowrap;
    padding: 0 8rpx;

    &:disabled {
      opacity: $uni-opacity-disabled;
      cursor: not-allowed;
    }
  }

  .reset-btn {
    background: $background-color;
    color: $text-secondary;
  }

  .save-btn {
    background: $primary-color;
    color: $background-color-white;
  }

  .publish-btn {
    background: $success-color;
    color: $background-color-white;
  }

  .mode-switch-btn {
    background: color.adjust($primary-color, $saturation: 20%);
    color: $background-color-white;
    flex: 0.5;

    @media (max-width: 768px) {
      flex: 1;
    }
  }
}

/* ==================== 共享样式 ==================== */
/* 加载状态 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba($background-color-white, 0.95);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: $z-index-toast;
  flex-direction: column;
  gap: $margin-base * 0.75;
}

.loading-content {
  background: $background-color-white;
  border-radius: $border-radius;
  padding: $padding-base * 2;
  box-shadow: $box-shadow-dark;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $margin-base * 0.75;
  max-width: 600rpx;
  width: 80%;
}

.loading-spinner {
  width: 80rpx;
  height: 80rpx;
  border: 6rpx solid $border-color-lighter;
  border-top-color: $primary-color;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: $font-size-medium;
  color: $text-primary;
  font-weight: $font-weight-medium;
}

/* 分享模态框 */
.share-modal {
  background: $background-color-white;
  border-radius: $border-radius-large $border-radius-large 0 0;
  padding: $padding-base $padding-small;
  max-height: 80vh;
  overflow-y: auto;

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $margin-base;
    padding-bottom: $padding-mini;
    border-bottom: 1rpx solid $background-color;

    .modal-title {
      font-size: $font-size-medium;
      font-weight: $font-weight-semibold;
      color: $text-primary;
    }

    .modal-close {
      font-size: $font-size-medium;
      color: $text-secondary;
      cursor: pointer;
      padding: 8rpx;
      border-radius: $border-radius-small;
      transition: background-color $transition-fast $ease-in-out;

      &:active {
        background: $background-color;
      }
    }
  }

  .share-options {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: $margin-mini;
    margin-bottom: $margin-base;

    .share-option {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 12rpx;
      padding: $padding-small;
      background: $background-color;
      border: 1rpx solid $border-color-lighter;
      border-radius: $border-radius;
      transition: background-color $transition-fast $ease-in-out;

      &:active {
        background: $border-color-lighter;
      }

      .option-icon {
        font-size: 48rpx;
        width: 80rpx;
        height: 80rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: $border-radius-round;
        margin-bottom: 12rpx;

        &.wechat {
          background: $success-color;
          color: $background-color-white;
        }

        &.qrcode {
          background: $primary-color;
          color: $background-color-white;
        }

        &.link {
          background: $warning-color;
          color: $background-color-white;
        }

        &.image {
          background: $info-color;
          color: $background-color-white;
        }
      }

      .option-text {
        font-size: $font-size-small;
        color: $text-primary;
        font-weight: $font-weight-medium;
      }
    }
  }

  .qrcode-section {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: $margin-mini;
    padding: $padding-small;
    background: $background-color;
    border-radius: $border-radius;
    border: 1rpx solid $border-color-lighter;

    .qrcode-title {
      font-size: $font-size-base;
      color: $text-primary;
      font-weight: $font-weight-medium;
    }

    .qrcode-image {
      width: 300rpx;
      height: 300rpx;
    }

    .qrcode-hint {
      font-size: $font-size-extra-small;
      color: $text-secondary;
    }
  }
}

/* 输入框占位符样式 */
.input-placeholder {
  color: $text-placeholder;
  font-size: $font-size-base;
}

/* ==================== 屏幕旋转和尺寸变化的过渡效果 ==================== */
.main-content {
  transition: flex-direction 0.3s ease;
}

.preview-section,
.form-section {
  transition: border 0.3s ease;
}

/* ==================== 响应式通用调整 ==================== */
@media (max-width: 768px) {
  // 模式选择器响应式调整
  .mode-buttons {
    gap: $margin-mini;
  }

  .mode-btn {
    padding: $padding-base $padding-small;
  }

  .mode-icon {
    font-size: 60rpx;
  }

  .mode-text {
    font-size: $font-size-medium;
  }

  .mode-desc {
    font-size: $font-size-extra-small;
  }

  .select-options {
    grid-template-columns: 1fr;
  }

  // 信息卡片响应式调整
  .info-grid,
  .config-grid {
    grid-template-columns: 1fr;
  }

  // 分享选项响应式调整
  .share-options {
    grid-template-columns: 1fr !important;
  }

  // 查看模式操作按钮响应式调整
  .action-buttons {
    .action-btn {
      .action-text {
        font-size: 20rpx;
      }
    }
  }

  // 编辑模式预览头部小屏幕调整
  .preview-section.fixed-preview .preview-header {
    padding: 6rpx 12rpx;
    min-height: 44px;
    height: 44px;

    .preview-title {
      font-size: $font-size-small;
    }

    .preview-actions {
      gap: 6rpx;

      .preview-action-btn {
        padding: 6rpx 10rpx;

        .action-text {
          display: none;
        }
      }
    }
  }
}

/* 宽屏时调整预览区域头部 */
@media (min-width: 769px) {
  .preview-section.fixed-preview .preview-header {
    // 大屏时已经优化，使用更紧凑的设计
    .preview-actions {
      .preview-action-btn {
        .action-text {
          display: inline;
        }
      }
    }
  }

  // 大屏时表单区域增加内边距
  .form-section .form-container {
    padding-left: 20rpx;
    padding-right: 20rpx;
  }
}

/* 超小屏幕优化 */
@media (max-width: 320px) {
  .preview-section.fixed-preview .preview-header {
    .preview-title {
      font-size: $font-size-small;
    }

    .preview-actions {
      .preview-action-btn {
        padding: 4rpx 8rpx;

        .action-icon {
          margin: 0;
        }

        .action-text {
          display: none;
        }
      }
    }
  }

  .action-buttons-edit {
    .reset-btn,
    .save-btn,
    .publish-btn,
    .mode-switch-btn {
      font-size: $font-size-extra-small;
      padding: 0 4rpx;
    }
  }
}

/* 中等屏幕优化 */
@media (min-width: 769px) and (max-width: 1024px) {
  .preview-section.fixed-preview .preview-header {
    padding: 10rpx 16rpx;

    .preview-title {
      font-size: $font-size-base * 1.05;
    }

    .preview-actions {
      .preview-action-btn {
        padding: 10rpx 16rpx;

        .action-text {
          font-size: $font-size-small;
        }
      }
    }
  }
}

/* 大屏幕优化 */
@media (min-width: 1025px) {
  .preview-section.fixed-preview .preview-header {
    padding: 12rpx 20rpx;

    .preview-title {
      font-size: $font-size-medium;
    }

    .preview-actions {
      .preview-action-btn {
        padding: 12rpx 20rpx;

        .action-text {
          font-size: $font-size-base;
        }
      }
    }
  }
}

/* 确保在宽屏模式下，表单和预览区域不会遮挡顶部 */
@media (min-width: 769px) {
  .h5-wide-layout {
    .form-section {
      top: 20px;
      height: calc(100vh - 120rpx - 20px);
    }

    .preview-section.fixed-preview {
      top: 20px;
      height: calc(100vh - 120rpx - 20px);
    }
  }
}

/* 确保在横屏模式下的显示 */
@media (orientation: landscape) and (max-height: 500px) {
  .h5-wide-layout,
  .app-layout {
    .form-section,
    .preview-section.fixed-preview {
      height: calc(100vh - 80px); /* 横屏时减少底部空间 */
    }
  }

  .action-buttons-edit {
    height: 50px;
    min-height: 50px;
  }
}
</style>