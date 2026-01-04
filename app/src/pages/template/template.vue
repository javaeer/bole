<template>
  <view class="template-management page-container">
    <!-- 头部模式切换 -->
    <view class="mode-header card-container">
      <view class="mode-switch flex-center">
        <view
          :class="['mode-tab', { active: mode === 'preview' }]"
          @click="switchMode('preview')"
        >
          <text class="tab-icon">👁️</text>
          <text class="tab-text">预览</text>
        </view>
        <view
          :class="['mode-tab', { active: mode === 'edit' }]"
          @click="switchMode('edit')"
        >
          <text class="tab-icon">✏️</text>
          <text class="tab-text">编辑</text>
        </view>
      </view>

      <!-- 头部操作按钮 -->
      <view class="header-actions">
        <template v-if="mode === 'preview'">
          <button class="action-btn btn-secondary" @click="handleEdit">
            <text class="btn-text">编辑模板</text>
          </button>
          <button
            class="action-btn btn"
            :class="template?.collected ? 'btn-danger' : 'btn-secondary'"
            @click="handleToggleFavorite"
          >
            <text class="btn-text">
              {{ template?.collected ? '取消收藏' : '收藏' }}
            </text>
          </button>
          <button class="action-btn btn btn-primary" @click="handleUseTemplate">
            <text class="btn-text">使用模板</text>
          </button>
        </template>

        <template v-else>
          <button class="action-btn btn-secondary" @click="cancelEdit">
            <text class="btn-text">取消</text>
          </button>
          <button class="action-btn btn" @click="saveAsDraft">
            <text class="btn-text">保存草稿</text>
          </button>
          <button class="action-btn btn btn-primary" @click="saveTemplate">
            <text class="btn-text">{{ isEditMode ? '更新' : '保存' }}</text>
          </button>
        </template>
      </view>
    </view>

    <!-- 预览模式 -->
    <template v-if="mode === 'preview'">
      <!-- 模板基本信息 -->
      <view class="template-header card-container">
        <view class="header-main flex-between">
          <view class="template-badge" :style="{ backgroundColor: getAvatarColor(template?.id || 0) }">
            <text class="badge-text">{{ getTemplateInitial(template?.name || "") }}</text>
          </view>
          <view class="template-info">
            <text class="template-name text-truncate">{{ template?.name || "未知模板" }}</text>
            <text class="template-description text-multi-truncate">{{ template?.description || "暂无描述" }}</text>
            <view class="template-meta flex-between">
              <view class="meta-item flex-center">
                <text class="meta-label">编码：</text>
                <text class="meta-value text-ellipsis">{{ template?.code || "未设置" }}</text>
              </view>
              <view class="meta-item">
                <view class="status-tag" :class="template?.isActive ? 'active' : 'inactive'">
                  <text class="status-text">{{ template?.isActive ? "已启用" : "未启用" }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 模板预览 -->
      <view class="preview-section section-container">
        <view class="section-header flex-between">
          <text class="section-title">模板预览</text>
          <view class="device-switch flex-center">
            <button
              v-for="device in deviceOptions"
              :key="device.value"
              :class="['device-btn', { active: currentDevice === device.value }]"
              @click="handleDeviceSwitch(device.value)"
            >
              <text class="device-icon">{{ device.icon }}</text>
              <text class="device-name">{{ device.name }}</text>
            </button>
          </view>
        </view>
        <view class="preview-card" :class="currentDevice">
          <template-preview
            :key="previewKey"
            :components="previewComponents"
            :global-layout="previewGlobalLayout"
            :global-style="previewGlobalStyle"
            :device="currentDevice"
          />
        </view>

        <!-- 预览信息 -->
        <view class="preview-info flex-between">
          <view class="info-item">
            <text class="info-label">布局类型：</text>
            <text class="info-value">{{ getLayoutTypeLabel(previewGlobalLayout?.type) }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">组件数量：</text>
            <text class="info-value">{{ previewComponents.length }}个</text>
          </view>
          <view class="info-item">
            <text class="info-label">创建时间：</text>
            <text class="info-value">{{ formatDateTime(template?.createdAt) }}</text>
          </view>
        </view>
      </view>

      <!-- 模板配置详情 -->
      <view class="config-section">
        <!-- 全局样式配置 -->
        <view class="config-card card-container" v-if="previewGlobalStyle">
          <view class="config-header flex-between">
            <text class="config-title">全局样式</text>
          </view>
          <view class="config-content">
            <view class="config-row">
              <text class="config-label">颜色主题：</text>
              <view class="color-theme flex-between">
                <view
                  class="color-item flex-center"
                  v-for="color in previewColorItems"
                  :key="color.label"
                >
                  <view
                    class="color-dot"
                    :style="{ backgroundColor: color.value }"
                  ></view>
                  <text class="color-label">{{ color.label }}</text>
                  <text class="color-value">{{ color.value }}</text>
                </view>
              </view>
            </view>
            <view class="config-row">
              <text class="config-label">字体设置：</text>
              <text class="config-value">{{ previewGlobalStyle.fontFamily || "默认字体" }}</text>
            </view>
            <view class="config-row" v-if="previewGlobalStyle.fontSizes">
              <text class="config-label">字体大小：</text>
              <view class="font-sizes flex-between">
                <text class="font-size-item">标题: {{ previewGlobalStyle.fontSizes.h1 || 24 }}px</text>
                <text class="font-size-item">正文: {{ previewGlobalStyle.fontSizes.body || 14 }}px</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 布局配置 -->
        <view class="config-card card-container" v-if="previewGlobalLayout">
          <view class="config-header">
            <text class="config-title">布局配置</text>
          </view>
          <view class="config-content">
            <view class="config-row">
              <text class="config-label">布局类型：</text>
              <text class="config-value">{{ getLayoutTypeLabel(previewGlobalLayout.type) }}</text>
            </view>
            <view class="config-row" v-if="previewGlobalLayout.type === 'two-column'">
              <text class="config-label">栏位宽度：</text>
              <view class="column-widths flex-between">
                <text>左侧: {{ previewGlobalLayout.columns?.left || 40 }}%</text>
                <text>右侧: {{ previewGlobalLayout.columns?.right || 60 }}%</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 组件列表 -->
        <view class="config-card card-container">
          <view class="config-header flex-between">
            <text class="config-title">包含组件</text>
            <text class="config-count">{{ previewComponents.length }}个</text>
          </view>
          <view class="components-list">
            <view
              class="component-item flex-between"
              v-for="(component, index) in previewComponents"
              :key="component.componentId || index"
            >
              <view class="component-index flex-center">
                <text class="index-text">{{ index + 1 }}</text>
              </view>
              <view class="component-info">
                <text class="component-name text-truncate">{{ getComponentName(component) }}</text>
                <text class="component-desc text-multi-truncate">{{ getComponentDescription(component) }}</text>
              </view>
            </view>
          </view>
          <view v-if="previewComponents.length === 0" class="empty-state flex-center">
            <text class="empty-icon">📄</text>
            <text class="empty-text">暂无组件配置</text>
          </view>
        </view>
      </view>
    </template>

    <!-- 编辑模式 -->
    <template v-else>
      <!-- 主体内容 -->
      <view class="editor-container">
        <!-- 左侧配置面板 -->
        <view class="config-panel">
          <!-- 配置标签页 -->
          <view class="config-tabs">
            <view
              v-for="tab in configTabs"
              :key="tab.id"
              :class="['tab-item', { active: activeTab === tab.id }]"
              @click="activeTab = tab.id"
            >
              <text class="tab-text">{{ tab.label }}</text>
              <view v-if="activeTab === tab.id" class="tab-indicator"></view>
            </view>
          </view>
          <!-- 配置内容区域 -->
          <scroll-view
            class="config-content"
            scroll-y="true"
            :scroll-top="scrollTop"
            @scroll="onScroll"
          >
            <!-- 基础配置 -->
            <view v-if="activeTab === 'basic'" class="config-section">
              <view class="component-header">
                <text class="component-title">基础信息</text>
                <text class="component-desc">设置模板的基本属性和分类</text>
              </view>

              <view class="form-group">
                <text class="form-label required">模板编码</text>
                <input
                  v-model="templateForm.code"
                  :disabled="isEditMode"
                  placeholder="英文、数字、下划线，如: resume_tech"
                  :class="['form-input', { disabled: isEditMode }]"
                  @focus="onInputFocus"
                  @blur="onInputBlur"
                />
                <text class="form-tip" v-if="isEditMode">模板编码创建后不可修改</text>
              </view>

              <view class="form-group">
                <text class="form-label required">模板名称</text>
                <input
                  v-model="templateForm.name"
                  placeholder="如: 科技风格简历"
                  class="form-input"
                  @focus="onInputFocus"
                  @blur="onInputBlur"
                />
              </view>

              <view class="form-group">
                <text class="form-label">预览图</text>
                <view class="preview-image-container" @click="handlePreviewImageUpload">
                  <image
                    v-if="templateForm.previewImage"
                    :src="templateForm.previewImage"
                    class="preview-image"
                    mode="aspectFill"
                  />
                  <view v-else class="preview-image-placeholder">
                    <text class="preview-image-icon">+</text>
                    <text class="preview-image-text">上传预览图</text>
                  </view>

                  <!-- 上传遮罩层 -->
                  <view v-if="uploadingPreviewImage" class="upload-mask">
                    <view class="upload-progress">
                      <view class="progress-circle">
                        <view class="circle-bg"></view>
                        <view class="circle-fill" :style="{ transform: `rotate(${uploadPreviewProgress * 3.6}deg)` }"></view>
                      </view>
                      <text class="progress-text">{{ uploadPreviewProgress }}%</text>
                    </view>
                  </view>
                </view>
                <text class="preview-image-tips">建议尺寸 1080×1920 像素，支持 JPG/PNG</text>
              </view>

              <view class="form-group">
                <text class="form-label">描述</text>
                <textarea
                  v-model="templateForm.description"
                  placeholder="请输入模板描述..."
                  class="form-textarea"
                  maxlength="200"
                  @focus="onTextareaFocus"
                  @blur="onTextareaBlur"
                />
                <view class="textarea-footer flex-between">
                  <text class="textarea-tip">简要描述模板用途和特点</text>
                  <text class="textarea-count">{{ templateForm.description?.length || 0 }}/200</text>
                </view>
              </view>
            </view>

            <!-- 布局配置 -->
            <view v-if="activeTab === 'layout'" class="config-section">
              <view class="component-header">
                <text class="component-title">布局设置</text>
                <text class="component-desc">调整页面结构和区块排列</text>
              </view>

              <view class="form-group">
                <text class="form-label">布局类型</text>
                <view class="layout-types">
                  <view
                    v-for="type in LAYOUT_TYPES"
                    :key="type.value"
                    :class="['layout-type-item', { active: templateForm.globalLayout.type === type.value }]"
                    @click="templateForm.globalLayout.type = type.value"
                  >
                    <view :class="['layout-icon', type.icon]"></view>
                    <text class="layout-name">{{ type.label }}</text>
                  </view>
                </view>
              </view>

              <!-- 双栏布局配置 -->
              <view v-if="templateForm.globalLayout.type === 'two-column'" class="layout-config">
                <view class="layout-preview-container">
                  <text class="preview-title">布局预览</text>
                  <view class="two-column-preview">
                    <view
                      class="column-left"
                      :style="{ width: getColumnWidth('left') }"
                    >
                      <text class="column-label">左侧</text>
                    </view>
                    <view
                      class="column-right"
                      :style="{ width: getColumnWidth('right') }"
                    >
                      <text class="column-label">右侧</text>
                    </view>
                  </view>
                </view>

                <view class="width-controls">
                  <view class="control-group">
                    <text class="control-label">左侧宽度: {{ templateForm.globalLayout.columns.left }}%</text>
                    <slider
                      :value="templateForm.globalLayout.columns.left"
                      min="20"
                      max="80"
                      step="5"
                      @changing="onColumnWidthChanging"
                      data-side="left"
                      active-color="#d4af37"
                      background-color="#ebeef5"
                      class="form-slider"
                    />
                  </view>
                  <view class="control-group">
                    <text class="control-label">右侧宽度: {{ templateForm.globalLayout.columns.right }}%</text>
                    <slider
                      :value="templateForm.globalLayout.columns.right"
                      min="20"
                      max="80"
                      step="5"
                      @changing="onColumnWidthChanging"
                      data-side="right"
                      active-color="#d4af37"
                      background-color="#ebeef5"
                      class="form-slider"
                    />
                  </view>
                </view>
              </view>

              <!-- 区块管理 -->
              <view class="component-management">
                <view class="component-header">
                  <text class="component-title">区块管理</text>
                  <text class="component-desc">选择区块后使用上下按钮调整顺序</text>
                </view>

                <!-- 组件推荐区域 -->
                <view v-if="getRecommendedComponents.length > 0" class="component-recommendation">
                  <text class="recommendation-title">常用组件推荐</text>
                  <view class="recommendation-list">
                    <view
                      v-for="component in getRecommendedComponents"
                      :key="component.id"
                      class="recommendation-item"
                      @click="addSectionWithComponent(component)"
                    >
                      <text>{{ component.name }}</text>
                    </view>
                  </view>
                </view>

                <view class="component-list">
                  <view
                    v-for="(component, index) in templateForm.components"
                    :key="`component-${component.componentId || 'empty'}-${index}`"
                    :class="['component-item', { 'selected': selectedIndex === index }]"
                    @click="selectComponent(index)"
                  >
                    <view class="component-item-main">
                      <view class="component-index">
                        <text class="index-text">{{ index + 1 }}</text>
                      </view>
                      <view class="component-info">
                        <!-- 组件信息显示区域 -->
                        <view class="component-name-display">
                          <text class="component-name">
                            {{ getComponentNameById(component.componentId) || "未选择组件" }}
                          </text>
                          <text v-if="component.componentId" class="component-key">
                            {{ getComponentKey(component.componentId) }}
                          </text>
                        </view>

                        <!-- 组件选择器 -->
                        <picker
                          :value="getComponentIndex(component.componentId)"
                          :range="componentOptions"
                          @change="(e) => onComponentChange(index, e)"
                          class="component-picker"
                        >
                          <view class="picker-display flex-between">
                            <text>{{ getSelectedComponentLabel(component.componentId) }}</text>
                            <text class="picker-arrow">▼</text>
                          </view>
                        </picker>

                        <!-- 组件描述 -->
                        <view v-if="component.componentId" class="component-description">
                          <text class="desc-text">{{ getComponentDescriptionById(component.componentId) }}</text>
                        </view>
                      </view>
                      <view class="component-actions">
                        <button
                          @click.stop="moveComponentUp(index)"
                          :class="['action-btn move-up-btn', { disabled: index === 0 }]"
                          :disabled="index === 0"
                        >
                          <text class="action-icon">↑</text>
                        </button>
                        <button
                          @click.stop="moveComponentDown(index)"
                          :class="['action-btn move-down-btn', { disabled: index === templateForm.components.length - 1 }]"
                          :disabled="index === templateForm.components.length - 1"
                        >
                          <text class="action-icon">↓</text>
                        </button>
                        <button
                          @click.stop="removeSection(index)"
                          class="action-btn remove-btn"
                        >
                          <text class="action-icon">×</text>
                        </button>
                      </view>
                    </view>

                    <!-- 组件详情按钮 -->
                    <view v-if="component.componentId" class="component-details-container">
                      <button @click.stop="showComponentDetails(component.componentId)" class="component-details-btn">
                        <text class="details-icon">ℹ️</text>
                        <text class="details-text">查看组件详情</text>
                      </button>
                    </view>
                  </view>
                </view>

                <view class="sort-controls" v-if="selectedIndex !== -1 && templateForm.components.length > 0">
                  <text class="sort-hint">
                    当前选中: {{ getComponentNameById(templateForm.components[selectedIndex]?.componentId) || "未命名区块" }}
                  </text>
                  <view class="sort-buttons">
                    <button
                      @click="moveSelectedToTop"
                      :class="['sort-btn', { disabled: selectedIndex === 0 }]"
                      :disabled="selectedIndex === 0"
                    >
                      <text class="sort-icon">⏫</text>
                      移到顶部
                    </button>
                    <button
                      @click="moveSelectedToBottom"
                      :class="['sort-btn', { disabled: selectedIndex === templateForm.components.length - 1 }]"
                      :disabled="selectedIndex === templateForm.components.length - 1"
                    >
                      <text class="sort-icon">⏬</text>
                      移到底部
                    </button>
                  </view>
                </view>

                <button @click="addSection" class="add-component-btn">
                  <text class="add-icon">+</text>
                  添加空区块
                </button>

                <!-- 组件计数显示 -->
                <view class="component-count">
                  <text class="count-label">区块总数: {{ templateForm.components.length }}</text>
                  <text class="count-valid">有效区块: {{ validComponentsCount }}</text>
                </view>

                <!-- 添加组件选择帮助提示 -->
                <view class="component-help">
                  <text class="help-icon">💡</text>
                  <text class="help-text">
                    从下拉列表中选择组件，系统会自动填充组件的默认配置
                  </text>
                </view>
              </view>
            </view>

            <!-- 样式配置 -->
            <view v-if="activeTab === 'style'" class="config-section">
              <view class="component-header">
                <text class="component-title">样式设置</text>
                <text class="component-desc">自定义颜色、字体和间距</text>
              </view>

              <!-- 颜色配置 -->
              <view class="color-config">
                <view class="config-header">
                  <text class="config-title">主题颜色</text>
                  <button @click="resetColors" class="reset-btn">重置</button>
                </view>

                <view class="color-group">
                  <view class="color-item">
                    <view class="color-info">
                      <text class="color-label">主色调</text>
                      <text class="color-desc">用于标题和重要元素</text>
                    </view>
                    <view class="color-controls">
                      <input
                        type="text"
                        :value="templateForm.globalStyle.primaryColor"
                        @input="onColorInput($event, 'primaryColor')"
                        class="color-input"
                        placeholder="#d4af37"
                      />
                      <view
                        class="color-preview"
                        :style="{ backgroundColor: templateForm.globalStyle.primaryColor }"
                        @click="showColorPicker('primaryColor')"
                      />
                    </view>
                  </view>

                  <view class="color-item">
                    <view class="color-info">
                      <text class="color-label">辅色调</text>
                      <text class="color-desc">用于背景和次要元素</text>
                    </view>
                    <view class="color-controls">
                      <input
                        type="text"
                        :value="templateForm.globalStyle.secondaryColor"
                        @input="onColorInput($event, 'secondaryColor')"
                        class="color-input"
                        placeholder="#f9f3e3"
                      />
                      <view
                        class="color-preview"
                        :style="{ backgroundColor: templateForm.globalStyle.secondaryColor }"
                        @click="showColorPicker('secondaryColor')"
                      />
                    </view>
                  </view>

                  <view class="color-item">
                    <view class="color-info">
                      <text class="color-label">强调色</text>
                      <text class="color-desc">用于按钮和交互元素</text>
                    </view>
                    <view class="color-controls">
                      <input
                        type="text"
                        :value="templateForm.globalStyle.accentColor"
                        @input="onColorInput($event, 'accentColor')"
                        class="color-input"
                        placeholder="#f7ef8a"
                      />
                      <view
                        class="color-preview"
                        :style="{ backgroundColor: templateForm.globalStyle.accentColor }"
                        @click="showColorPicker('accentColor')"
                      />
                    </view>
                  </view>
                </view>

                <!-- 预定义颜色选择 -->
                <view class="color-presets" v-if="showColorPresets">
                  <view class="presets-title">常用颜色</view>
                  <view class="presets-grid">
                    <view
                      v-for="color in colorPresets"
                      :key="color"
                      class="preset-color"
                      :style="{ backgroundColor: color }"
                      @click="setColor(currentColorField, color)"
                    />
                  </view>
                </view>
              </view>

              <!-- 字体配置 -->
              <view class="font-config">
                <view class="config-header">
                  <text class="config-title">字体设置</text>
                </view>

                <view class="form-group">
                  <text class="form-label">字体家族</text>
                  <input
                    v-model="templateForm.globalStyle.fontFamily"
                    placeholder="如: 'Microsoft YaHei', sans-serif"
                    class="form-input"
                  />
                </view>

                <view class="font-sizes">
                  <view class="size-item">
                    <text class="size-label">标题文字</text>
                    <view class="size-controls">
                      <input
                        type="number"
                        v-model="templateForm.globalStyle.fontSizes.h1"
                        class="size-input"
                        min="12"
                        max="72"
                      />
                      <text class="size-unit">px</text>
                      <view class="size-slider">
                        <slider
                          :value="Number(templateForm.globalStyle.fontSizes.h1)"
                          min="12"
                          max="72"
                          step="2"
                          @changing="onFontSizeChanging"
                          data-type="h1"
                          active-color="#d4af37"
                          class="size-slider-bar"
                        />
                      </view>
                    </view>
                  </view>

                  <view class="size-item">
                    <text class="size-label">正文字体</text>
                    <view class="size-controls">
                      <input
                        type="number"
                        v-model="templateForm.globalStyle.fontSizes.body"
                        class="size-input"
                        min="10"
                        max="36"
                      />
                      <text class="size-unit">px</text>
                      <view class="size-slider">
                        <slider
                          :value="Number(templateForm.globalStyle.fontSizes.body)"
                          min="10"
                          max="36"
                          step="1"
                          @changing="onFontSizeChanging"
                          data-type="body"
                          active-color="#d4af37"
                          class="size-slider-bar"
                        />
                      </view>
                    </view>
                  </view>
                </view>
              </view>

              <!-- 间距配置 -->
              <view class="spacing-config">
                <view class="config-header">
                  <text class="config-title">间距设置</text>
                </view>

                <view class="spacing-items">
                  <view class="spacing-item">
                    <view class="spacing-info">
                      <text class="spacing-label">区块间距</text>
                      <text class="spacing-desc">控制区块之间的垂直距离</text>
                    </view>
                    <input
                      type="text"
                      v-model="templateForm.globalStyle.spacing.sectionMargin"
                      placeholder="如: 20px"
                      class="spacing-input"
                    />
                  </view>

                  <view class="spacing-item">
                    <view class="spacing-info">
                      <text class="spacing-label">内容内边距</text>
                      <text class="spacing-desc">控制内容区域的留白</text>
                    </view>
                    <input
                      type="text"
                      v-model="templateForm.globalStyle.spacing.padding"
                      placeholder="如: 15px"
                      class="spacing-input"
                    />
                  </view>

                  <view class="spacing-item">
                    <view class="spacing-info">
                      <text class="spacing-label">行高</text>
                      <text class="spacing-desc">控制文字行间距</text>
                    </view>
                    <input
                      type="text"
                      v-model="templateForm.globalStyle.spacing.lineHeight"
                      placeholder="如: 1.5"
                      class="spacing-input"
                    />
                  </view>
                </view>
              </view>
            </view>
          </scroll-view>
        </view>

        <!-- 右侧预览面板 -->
        <view class="preview-panel">
          <view class="preview-header flex-between">
            <view class="preview-title-container">
              <text class="preview-title">实时预览</text>
              <text class="preview-subtitle">所见即所得</text>
            </view>

            <view class="preview-controls">
              <view class="device-switch">
                <text class="switch-label">设备:</text>
                <view class="switch-buttons">
                  <button
                    v-for="device in deviceOptions"
                    :key="device.value"
                    :class="['device-btn', { active: currentDevice === device.value }]"
                    @click="handleDeviceSwitch(device.value)"
                  >
                    <text class="device-icon">{{ device.icon }}</text>
                    <text class="device-name">{{ device.name }}</text>
                  </button>
                </view>
              </view>

              <button @click="refreshPreview" class="refresh-btn">
                <text class="refresh-icon">🔄</text>
                刷新预览
              </button>
            </view>
          </view>

          <view class="preview-content" :class="currentDevice">
            <!-- 实时预览组件 -->
            <template-preview
              :key="editPreviewKey"
              :components="editPreviewComponents"
              :globalLayout="editPreviewGlobalLayout"
              :global-style="editPreviewGlobalStyle"
              :device="currentDevice"
              class="template-preview-container"
            />

            <view class="preview-info">
              <text class="info-title">模板信息</text>
              <view class="info-items">
                <view class="info-item">
                  <text class="info-label">名称:</text>
                  <text class="info-value">{{ templateForm.name || "未命名" }}</text>
                </view>
                <view class="info-item">
                  <text class="info-label">布局:</text>
                  <text class="info-value">{{ editLayoutTypeLabel }}</text>
                </view>
                <view class="info-item">
                  <text class="info-label">区块数:</text>
                  <text class="info-value">{{ templateForm.components.length }}</text>
                </view>
                <view class="info-item">
                  <text class="info-label">有效组件:</text>
                  <text class="info-value">{{ validComponentsCount }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </template>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-overlay flex-center">
      <view class="loading-content flex-center">
        <view class="loading-spinner"></view>
        <text class="loading-text">加载中...</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, watch } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import TemplatePreview from "@/components/template/TemplatePreview.vue";
import { TemplateResult, TemplateForm } from "@/types/template";
import { TemplateComponentItem, TemplateComponentForm } from "@/types/template-component";
import { COMPONENT_LIBRARY } from "@/constants/component";
import { DEVICE_OPTIONS, LAYOUT_TYPES, CONFIG_TABS, COLOR_PRESETS } from "@/constants/template";
import { useTemplateStore } from "@/stores/template";
import TemplateAPI from "@/api/template";
import FileAPI from "@/api/file";
import { FileResult } from "@/types/file";
import { UploadOptions } from "@/types/request";

// 页面模式：preview（预览）或 edit（编辑）
const mode = ref<'preview' | 'edit'>('preview');

// 响应式数据 - 预览模式
const template = ref<TemplateResult | null>(null);
const loading = ref(true);
const previewKey = ref(0);
// 统一设备状态，确保预览和编辑模式保持一致
const currentDevice = ref("desktop");

// 响应式数据 - 编辑模式
const isEditMode = ref(false);
const activeTab = ref("basic");
const editPreviewKey = ref(0);
const scrollTop = ref(0);
const showColorPresets = ref(false);
const currentColorField = ref<"primaryColor" | "secondaryColor" | "accentColor">("primaryColor");
const selectedIndex = ref(-1);
const uploadingPreviewImage = ref(false);
const uploadPreviewProgress = ref(0);

// 使用 store
const templateStore = useTemplateStore();
const templateForm = templateStore.editForm;

// 选项数据
const deviceOptions = ref(DEVICE_OPTIONS);
const configTabs = ref(CONFIG_TABS);
const colorPresets = ref(COLOR_PRESETS);
const availableComponents = ref(COMPONENT_LIBRARY);

// 组件选择历史（用于推荐）
const componentSelectionHistory = ref<number[]>([]);

// 计算属性 - 预览模式
// 使用统一的预览数据源，确保与编辑模式一致
const previewComponents = computed(() => {
  if (mode.value === 'preview' && template.value?.components) {
    return template.value.components;
  }
  return templateForm.components || [];
});

const previewGlobalLayout = computed(() => {
  if (mode.value === 'preview' && template.value?.globalLayout) {
    return template.value.globalLayout;
  }
  return templateForm.globalLayout || { type: 'single-column' };
});

const previewGlobalStyle = computed(() => {
  if (mode.value === 'preview' && template.value?.globalStyle) {
    return template.value.globalStyle;
  }
  return templateForm.globalStyle || {
    primaryColor: "#d4af37",
    secondaryColor: "#f9f3e3",
    accentColor: "#f7ef8a",
    fontFamily: "system-ui, -apple-system, sans-serif",
    fontSizes: {
      h1: "24",
      body: "14"
    },
    spacing: {
      sectionMargin: "20px",
      padding: "15px",
      lineHeight: "1.5"
    }
  };
});

const previewColorItems = computed(() => {
  const style = previewGlobalStyle.value;
  return [
    { label: "主色", value: style.primaryColor || "#d4af37" },
    { label: "辅色", value: style.secondaryColor || "#f9f3e3" },
    { label: "强调色", value: style.accentColor || "#f7ef8a" },
  ];
});

// 计算属性 - 编辑模式
const componentOptions = computed(() =>
  availableComponents.value.map(c => `${c.name} (${c.key})`),
);

const editLayoutTypeLabel = computed(() => {
  const type = LAYOUT_TYPES.find(t => t.value === templateForm.globalLayout?.type);
  return type ? type.label : "单栏";
});

// 编辑模式预览数据，直接使用templateForm中的数据
const editPreviewComponents = computed(() => {
  return templateForm.components || [];
});

const editPreviewGlobalLayout = computed(() => {
  return templateForm.globalLayout || { type: 'single-column' };
});

const editPreviewGlobalStyle = computed(() => {
  return templateForm.globalStyle || {
    primaryColor: "#d4af37",
    secondaryColor: "#f9f3e3",
    accentColor: "#f7ef8a",
    fontFamily: "system-ui, -apple-system, sans-serif",
    fontSizes: {
      h1: "24",
      body: "14"
    },
    spacing: {
      sectionMargin: "20px",
      padding: "15px",
      lineHeight: "1.5"
    }
  };
});

const validComponentsCount = computed(() => {
  return (templateForm.components || []).filter(c => c.componentId > 0).length;
});

const getRecommendedComponents = computed(() => {
  const historyMap = new Map<number, number>();
  componentSelectionHistory.value.forEach(id => {
    historyMap.set(id, (historyMap.get(id) || 0) + 1);
  });

  return [...availableComponents.value]
    .sort((a, b) => {
      const freqA = historyMap.get(a.id) || 0;
      const freqB = historyMap.get(b.id) || 0;
      return freqB - freqA;
    })
    .slice(0, 3);
});

const componentErrors = computed(() => {
  const components = templateForm.components || [];
  const errors: string[] = [];

  components.forEach((component, index) => {
    if (!component.componentId) {
      errors.push(`第 ${index + 1} 个区块未选择组件`);
    }
  });

  return errors;
});

// 加载模板数据
const loadTemplateDetail = async (id: string) => {
  try {
    loading.value = true;
    const templateId = parseInt(id);
    if (isNaN(templateId)) {
      throw new Error("无效的模板ID");
    }

    const response = await TemplateAPI.getById(templateId);
    if (response) {
      template.value = response as TemplateResult;
      templateStore.setCurrentTemplate(template.value);

      // 如果是编辑模式，同时更新表单数据
      if (mode.value === 'edit') {
        templateStore.updateEditForm(response);
      }
    } else {
      uni.showToast({
        title: "模板不存在",
        icon: "none",
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    }
    previewKey.value += 1;
  } catch (error) {
    console.error("加载模板详情失败:", error);
    uni.showToast({
      title: "加载失败",
      icon: "none",
    });
  } finally {
    loading.value = false;
  }
};

// 模式切换
const switchMode = (newMode: 'preview' | 'edit') => {
  if (mode.value === newMode) return;

  mode.value = newMode;

  if (newMode === 'edit') {
    // 切换到编辑模式时，如果已有模板数据，复制到表单
    if (template.value) {
      templateStore.updateEditForm(template.value);
    }
    // 初始化组件选择历史
    if (templateForm.components) {
      componentSelectionHistory.value = templateForm.components
        .map(c => c.componentId)
        .filter(id => id > 0);
    }
    // 编辑模式下也需要刷新预览
    editPreviewKey.value += 1;
  } else {
    // 切换到预览模式时，刷新预览
    previewKey.value += 1;
  }
};

// 导航处理
const handleEdit = () => {
  switchMode('edit');
};

const cancelEdit = () => {
  uni.showModal({
    title: "确认离开",
    content: "离开后未保存的更改将会丢失",
    confirmText: "离开",
    confirmColor: "#f56c6c",
    success: (res) => {
      if (res.confirm) {
        mode.value = 'preview';
        // 重新加载模板数据，恢复原始状态
        if (template.value?.id) {
          loadTemplateDetail(template.value.id.toString());
        }
      }
    },
  });
};

// 预览模式功能
const handleToggleFavorite = async () => {
  if (!template.value) return;

  try {
    if (template.value.collected) {
      await TemplateAPI.unfavorite(template.value.id);
      template.value.collected = false;
      uni.showToast({
        title: "取消收藏成功",
        icon: "success",
      });
      uni.$emit('template-favorite-updated', { id: template.value.id, collected: false });
    } else {
      await TemplateAPI.favorite(template.value.id);
      template.value.collected = true;
      uni.showToast({
        title: "收藏成功",
        icon: "success",
      });
      uni.$emit('template-favorite-updated', { id: template.value.id, collected: true });
    }
  } catch (error) {
    console.error("操作失败:", error);
    uni.showToast({
      title: "操作失败，请重试",
      icon: "error",
    });
  }
};

const handleUseTemplate = () => {
  if (!template.value) return;
  uni.navigateTo({
    url: `/pages/resumes/edit?templateId=${template.value.id}`,
  });
};

// 设备切换 - 统一处理预览和编辑模式的设备切换
const handleDeviceSwitch = (device: string) => {
  currentDevice.value = device;
  // 根据当前模式刷新对应的预览
  if (mode.value === 'preview') {
    previewKey.value += 1;
  } else {
    editPreviewKey.value += 1;
  }
};

// 编辑模式功能
const refreshPreview = () => {
  if (mode.value === 'preview') {
    previewKey.value += 1;
  } else {
    editPreviewKey.value += 1;
  }
  uni.showToast({
    title: "预览已刷新",
    icon: "success",
  });
};

const handlePreviewImageUpload = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ["compressed", "original"],
    sourceType: ["album", "camera"],
    success: async (res) => {
      const tempFilePath = res.tempFilePaths[0];
      const fileSize = res.tempFiles[0].size;

      const maxSize = 10 * 1024 * 1024;
      if (fileSize > maxSize) {
        uni.showToast({
          title: "图片大小不能超过10MB",
          icon: "error",
        });
        return;
      }

      uploadingPreviewImage.value = true;
      uploadPreviewProgress.value = 0;

      try {
        uni.showLoading({
          title: "上传中...",
          mask: true,
        });

        const uploadOptions: UploadOptions = {
          filePath: tempFilePath,
          name: "previewImage",
          compress: true,
          maxWidth: 1080,
          maxHeight: 1920,
          quality: 0.85,
          showProgress: true,
          onProgress: (progress: number) => {
            uploadPreviewProgress.value = progress;
            uni.showLoading({
              title: `上传中 ${progress}%`,
              mask: true,
            });
          },
          formData: {
            fileType: "template_preview",
            businessType: "template_editor"
          },
        };

        const result: FileResult = await FileAPI.upload(uploadOptions);
        templateForm.previewImage = result.accessUrl;

        uni.hideLoading();
        uni.showToast({
          title: "预览图上传成功",
          icon: "success",
        });

        editPreviewKey.value += 1;

      } catch (error: any) {
        console.error("预览图上传失败:", error);
        uni.hideLoading();

        let errorMsg = "预览图上传失败";
        if (error.code === "NETWORK_ERROR") {
          errorMsg = "网络错误，请检查网络连接";
        } else if (error.code === "UPLOAD_FAILED") {
          errorMsg = "上传失败，请稍后重试";
        } else if (error.message) {
          errorMsg = error.message;
        }

        uni.showToast({
          title: errorMsg,
          icon: "error",
          duration: 3000,
        });
      } finally {
        uploadingPreviewImage.value = false;
        uploadPreviewProgress.value = 0;
      }
    },
    fail: (error) => {
      console.error("选择图片失败:", error);
      if (error.errMsg?.includes("cancel")) {
        return;
      }
      uni.showToast({
        title: "选择图片失败",
        icon: "error",
      });
    },
  });
};

const saveAsDraft = () => {
  if (!validateRequiredFields()) return;

  uni.showModal({
    title: "保存草稿",
    content: "确定要将模板保存为草稿吗？",
    confirmText: "保存",
    confirmColor: "#d4af37",
    success: (res) => {
      if (res.confirm) {
        uni.showLoading({ title: "保存中..." });
        setTimeout(() => {
          uni.hideLoading();
          uni.showToast({
            title: "已保存为草稿",
            icon: "success",
            duration: 2000,
          });
        }, 1500);
      }
    },
  });
};

const saveTemplate = async () => {
  if (!validateRequiredFields()) return;

  if (componentErrors.value.length > 0) {
    uni.showModal({
      title: "组件未配置",
      content: `有 ${componentErrors.value.length} 个区块未选择组件，是否继续保存？`,
      confirmText: "继续保存",
      cancelText: "去配置",
      confirmColor: "#e6a23c",
      success: async (res) => {
        if (res.confirm) {
          await performSave();
        } else {
          activeTab.value = "layout";
        }
      },
    });
  } else {
    await performSave();
  }
};

const performSave = async () => {
  uni.showModal({
    title: isEditMode.value ? "更新模板" : "保存模板",
    content: isEditMode.value ? "确定要更新模板吗？" : "确定要发布模板吗？",
    confirmText: isEditMode.value ? "更新" : "发布",
    confirmColor: "#d4af37",
    success: async (res) => {
      if (res.confirm) {
        uni.showLoading({
          title: isEditMode.value ? "更新中..." : "发布中..."
        });

        try {
          if (!templateForm.components) {
            templateForm.components = [];
          }

          const submitData: TemplateForm = {
            ...templateForm,
            components: templateForm.components.map(comp => ({
              componentId: comp.componentId,
              props: comp.props || {},
              styles: comp.styles || {},
            })),
          };

          if (isEditMode.value) {
            const pages = getCurrentPages();
            const currentPage = pages[pages.length - 1];
            const options = currentPage.options;
            const templateId = options.id ? parseInt(options.id) : 0;

            if (templateId) {
              const editData = {
                ...submitData,
                id: templateId
              };
              await templateStore.editTemplate(editData);
              uni.showToast({
                title: "模板更新成功",
                icon: "success",
                duration: 2000,
              });
            } else {
              await templateStore.addTemplate(submitData);
              uni.showToast({
                title: "模板创建成功",
                icon: "success",
                duration: 2000,
              });
            }
          } else {
            await templateStore.addTemplate(submitData);
            uni.showToast({
              title: "模板创建成功",
              icon: "success",
              duration: 2000,
            });
          }

          // 保存成功后切换到预览模式并刷新数据
          setTimeout(() => {
            mode.value = 'preview';
            if (template.value?.id) {
              loadTemplateDetail(template.value.id.toString());
            }
          }, 2000);

        } catch (error) {
          console.error("保存模板失败:", error);
          let errorMessage = "保存失败，请重试";
          if (error.message && error.message.includes("网络")) {
            errorMessage = "网络异常，请检查网络连接";
          } else if (error.message && error.message.includes("权限")) {
            errorMessage = "没有操作权限";
          }

          uni.showToast({
            title: errorMessage,
            icon: "error",
            duration: 2000,
          });
        } finally {
          uni.hideLoading();
        }
      }
    },
  });
};

const validateRequiredFields = (): boolean => {
  if (!templateForm.code) {
    uni.showToast({
      title: "请填写模板编码",
      icon: "none",
    });
    return false;
  }

  if (!templateForm.name) {
    uni.showToast({
      title: "请填写模板名称",
      icon: "none",
    });
    return false;
  }

  return true;
};

// 组件管理功能
const selectComponent = (index: number) => {
  selectedIndex.value = index;
  templateStore.selectComponent(index);
};

const addSection = () => {
  const newComponent: TemplateComponentForm = {
    componentId: 0,
    props: {},
    styles: {},
  };

  templateStore.addComponent(newComponent);
  selectedIndex.value = (templateForm.components || []).length - 1;

  uni.showToast({
    title: "已添加区块",
    icon: "success",
    duration: 800,
  });
};

const addSectionWithComponent = (component: any) => {
  const newComponent: TemplateComponentForm = {
    componentId: component.id,
    props: { ...component.defaultConfig },
    styles: {},
  };

  templateStore.addComponent(newComponent);
  selectedIndex.value = (templateForm.components || []).length - 1;

  if (!componentSelectionHistory.value.includes(component.id)) {
    componentSelectionHistory.value.push(component.id);
  }

  uni.showToast({
    title: `已添加: ${component.name}`,
    icon: "success",
    duration: 1000,
  });

  editPreviewKey.value += 1;
};

const onComponentChange = (sectionIndex: number, e: any) => {
  const componentIndex = parseInt(e.detail.value);
  if (componentIndex >= 0 && componentIndex < availableComponents.value.length) {
    const selectedComponent = availableComponents.value[componentIndex];
    const components = templateForm.components || [];

    if (components[sectionIndex]) {
      components[sectionIndex].componentId = selectedComponent.id;
      components[sectionIndex].props = { ...selectedComponent.defaultConfig };
      templateStore.updateEditForm({ components: [...components] });

      uni.showToast({
        title: `已选择: ${selectedComponent.name}`,
        icon: "success",
        duration: 1000,
      });

      if (!componentSelectionHistory.value.includes(selectedComponent.id)) {
        componentSelectionHistory.value.push(selectedComponent.id);
      }

      editPreviewKey.value += 1;
    }
  }
};

const removeSection = (index: number) => {
  uni.showModal({
    title: "删除区块",
    content: "确定要删除这个区块吗？",
    confirmColor: "#f56c6c",
    success: (res) => {
      if (res.confirm) {
        templateStore.removeComponent(index);

        if (selectedIndex.value === index) {
          selectedIndex.value = -1;
        } else if (selectedIndex.value > index) {
          selectedIndex.value -= 1;
        }

        editPreviewKey.value += 1;
        uni.showToast({
          title: "删除成功",
          icon: "success",
        });
      }
    },
  });
};

const moveComponentUp = (index: number) => {
  if (index <= 0 || !templateForm.components) return;
  templateStore.moveComponent(index, index - 1);
  selectedIndex.value = index - 1;
  editPreviewKey.value += 1;
  uni.showToast({
    title: "已上移",
    icon: "success",
    duration: 800,
  });
};

const moveComponentDown = (index: number) => {
  if (!templateForm.components || index >= templateForm.components.length - 1) return;
  templateStore.moveComponent(index, index + 1);
  selectedIndex.value = index + 1;
  editPreviewKey.value += 1;
  uni.showToast({
    title: "已下移",
    icon: "success",
    duration: 800,
  });
};

const moveSelectedToTop = () => {
  if (selectedIndex.value <= 0 || !templateForm.components) return;
  const components = [...templateForm.components];
  const selected = components.splice(selectedIndex.value, 1)[0];
  components.unshift(selected);
  templateStore.updateEditForm({ components });
  selectedIndex.value = 0;
  editPreviewKey.value += 1;
  uni.showToast({
    title: "已移到顶部",
    icon: "success",
    duration: 1000,
  });
};

const moveSelectedToBottom = () => {
  if (!templateForm.components || selectedIndex.value >= templateForm.components.length - 1) return;
  const components = [...templateForm.components];
  const selected = components.splice(selectedIndex.value, 1)[0];
  components.push(selected);
  templateStore.updateEditForm({ components });
  selectedIndex.value = components.length - 1;
  editPreviewKey.value += 1;
  uni.showToast({
    title: "已移到底部",
    icon: "success",
    duration: 1000,
  });
};

const showComponentDetails = (componentId: number) => {
  if (!componentId) return;
  const component = availableComponents.value.find(c => c.id === componentId);
  if (component) {
    uni.showModal({
      title: `组件详情: ${component.name}`,
      content: `
组件标识: ${component.key}
描述: ${component.description}
分类: ${component.category}
默认配置: ${JSON.stringify(component.defaultConfig, null, 2)}
      `,
      showCancel: false,
      confirmText: "确定",
    });
  }
};

// 布局和样式配置功能
const getColumnWidth = (side: "left" | "right") => {
  return `${templateForm.globalLayout?.columns?.[side] || 50}%`;
};

const onColumnWidthChanging = (e: any) => {
  const value = e.detail.value;
  const side = e.currentTarget.dataset.side;
  if (templateForm.globalLayout?.columns) {
    if (side === "left") {
      templateForm.globalLayout.columns.left = value;
      templateForm.globalLayout.columns.right = 100 - value;
    } else {
      templateForm.globalLayout.columns.right = value;
      templateForm.globalLayout.columns.left = 100 - value;
    }
    editPreviewKey.value += 1;
  }
};

const onFontSizeChanging = (e: any) => {
  const value = e.detail.value;
  const type = e.currentTarget.dataset.type;
  if (templateForm.globalStyle?.fontSizes) {
    if (type === "h1") {
      templateForm.globalStyle.fontSizes.h1 = value.toString();
    } else {
      templateForm.globalStyle.fontSizes.body = value.toString();
    }
    editPreviewKey.value += 1;
  }
};

const onColorInput = (event: any, field: string) => {
  const value = event.detail?.value || event.target?.value;
  if (value && templateForm.globalStyle) {
    let formattedValue = value.trim();

    if (formattedValue.startsWith("#") && formattedValue.length !== 4 && formattedValue.length !== 7) {
      if (formattedValue.length === 4) {
        formattedValue = "#" + formattedValue[1] + formattedValue[1] +
          formattedValue[2] + formattedValue[2] +
          formattedValue[3] + formattedValue[3];
      }
    }

    if (!formattedValue.startsWith("#")) {
      const hexRegex = /^[0-9A-Fa-f]{3}$|^[0-9A-Fa-f]{6}$/;
      if (hexRegex.test(formattedValue)) {
        formattedValue = "#" + formattedValue;
      }
    }

    if (validateColorHex(formattedValue)) {
      ;(templateForm.globalStyle as any)[field] = formattedValue;
      editPreviewKey.value += 1;
    } else {
      uni.showToast({
        title: `颜色格式无效: ${formattedValue}`,
        icon: "none",
        duration: 2000,
      });
    }
  }
};

const validateColorHex = (color: string): boolean => {
  const hexRegex = /^#([0-9A-Fa-f]{3}){1,2}$/i;
  return hexRegex.test(color);
};

const showColorPicker = (field: "primaryColor" | "secondaryColor" | "accentColor") => {
  currentColorField.value = field;
  templateStore.componentState.currentColorField = field;
  templateStore.componentState.showColorPicker = true;

  if (uni.chooseColor) {
    uni.chooseColor({
      color: (templateForm.globalStyle as any)[field] || "#d4af37",
      success: (res) => {
        if (templateForm.globalStyle) {
          ;(templateForm.globalStyle as any)[field] = res.color;
          editPreviewKey.value += 1;
        }
      },
      fail: (err) => {
        console.log("颜色选择失败:", err);
        showColorPresets.value = !showColorPresets.value;
      },
    });
  } else {
    showColorPresets.value = !showColorPresets.value;
  }
};

const setColor = (field: string, color: string) => {
  if (templateForm.globalStyle) {
    ;(templateForm.globalStyle as any)[field] = color;
    editPreviewKey.value += 1;
    showColorPresets.value = false;
    uni.showToast({
      title: `${field} 已设置为 ${color}`,
      icon: "success",
      duration: 1000,
    });
  }
};

const resetColors = () => {
  if (templateForm.globalStyle) {
    templateForm.globalStyle.primaryColor = "#d4af37";
    templateForm.globalStyle.secondaryColor = "#f9f3e3";
    templateForm.globalStyle.accentColor = "#f7ef8a";
    editPreviewKey.value += 1;
    showColorPresets.value = false;
    uni.showToast({
      title: "颜色已重置为默认值",
      icon: "success",
    });
  }
};

// 工具函数
const getTemplateInitial = (name: string) => {
  if (!name) return "T";
  return name.charAt(0).toUpperCase();
};

const getAvatarColor = (id: number) => {
  const colors = [
    "#3498db",
    "#2ecc71",
    "#e74c3c",
    "#f39c12",
    "#9b59b6",
    "#1abc9c",
    "#d35400",
    "#c0392b",
  ];
  return colors[id % colors.length];
};

const getLayoutTypeLabel = (type?: string) => {
  const layout = LAYOUT_TYPES.find(item => item.value === type);
  return layout?.label || "单栏";
};

const getComponentName = (component: TemplateComponentItem) => {
  if (!component.componentId) return "未命名组件";
  const libComponent = COMPONENT_LIBRARY.find(
    (c) => c.id === component.componentId,
  );
  return libComponent?.name || "未知组件";
};

const getComponentDescription = (component: TemplateComponentItem) => {
  if (!component.componentId) return "";
  const libComponent = COMPONENT_LIBRARY.find(
    (c) => c.id === component.componentId,
  );
  return libComponent?.description || "";
};

const getComponentNameById = (componentId: number): string => {
  if (!componentId) return "";
  const component = availableComponents.value.find(c => c.id === componentId);
  return component ? component.name : "";
};

const getComponentKey = (componentId: number): string => {
  if (!componentId) return "";
  const component = availableComponents.value.find(c => c.id === componentId);
  return component ? component.key : "";
};

const getComponentDescriptionById = (componentId: number): string => {
  if (!componentId) return "";
  const component = availableComponents.value.find(c => c.id === componentId);
  return component ? component.description : "";
};

const getComponentIndex = (componentId: number): number => {
  if (!componentId) return -1;
  return availableComponents.value.findIndex(c => c.id === componentId);
};

const getSelectedComponentLabel = (componentId: number): string => {
  if (!componentId) return "选择组件";
  const component = availableComponents.value.find(c => c.id === componentId);
  return component ? `${component.name} (${component.key})` : "选择组件";
};

const formatDateTime = (dateStr?: string) => {
  if (!dateStr) return "未知";
  try {
    const date = new Date(dateStr);
    return date.toLocaleDateString("zh-CN");
  } catch {
    return dateStr;
  }
};

// 事件处理函数
const onScroll = (e: any) => {
  scrollTop.value = e.detail.scrollTop;
};

const onInputFocus = () => {};
const onInputBlur = () => {};
const onTextareaFocus = () => {};
const onTextareaBlur = () => {};

// 初始化示例组件
const initExampleComponents = () => {
  const exampleComponents: TemplateComponentForm[] = [
    {
      componentId: 1, // UserBasicInfo
      props: { showAvatar: true, showContact: true },
      styles: {},
    },
    {
      componentId: 3, // WorkExperience
      props: { showDuration: true, showCompany: true },
      styles: {},
    },
    {
      componentId: 4, // EducationExperience
      props: { showTime: true, showDegree: true },
      styles: {},
    },
  ];

  templateStore.updateEditForm({ components: exampleComponents });
  componentSelectionHistory.value = exampleComponents.map(c => c.componentId);
  editPreviewKey.value += 1;
};

// 响应式适配
const updateLayoutForScreen = () => {
  const systemInfo = uni.getSystemInfoSync();
  const windowWidth = systemInfo.windowWidth;

  if (windowWidth < 375) {
    currentDevice.value = "mobile";
  } else if (windowWidth < 768) {
    currentDevice.value = "tablet";
  } else {
    currentDevice.value = "desktop";
  }

  // 根据当前模式刷新预览
  if (mode.value === 'preview') {
    previewKey.value += 1;
  } else {
    editPreviewKey.value += 1;
  }
};

// 生命周期
onLoad((options) => {
  if (options.id) {
    loadTemplateDetail(options.id);
    if (options.mode === 'edit') {
      mode.value = 'edit';
      isEditMode.value = true;
    }
  } else {
    // 新建模板
    mode.value = 'edit';
    isEditMode.value = false;
    loading.value = false;
    initExampleComponents();
  }
});

onMounted(() => {
  updateLayoutForScreen();
  uni.onWindowResize && uni.onWindowResize(() => {
    updateLayoutForScreen();
  });
});

// 监听模板表单数据变化，实时更新预览
watch(
  () => templateForm.components,
  () => {
    editPreviewKey.value += 1;
  },
  { deep: true },
);

watch(
  () => templateForm.globalLayout,
  () => {
    editPreviewKey.value += 1;
  },
  { deep: true },
);

watch(
  () => templateForm.globalStyle,
  () => {
    editPreviewKey.value += 1;
  },
  { deep: true },
);
</script>

<style scoped lang="scss">
.template-management {
  background-color: $uni-bg-color-grey;
  min-height: 100vh;
}

/* 模式切换头部 */
.mode-header {
  background: $uni-bg-color;
  padding: $uni-spacing-row-base;
  border-bottom: 1rpx solid $border-color-extra-light;
  position: sticky;
  top: 0;
  z-index: $z-index-dropdown;
}

.mode-switch {
  display: flex;
  background: $background-color;
  border-radius: $border-radius-large;
  padding: 4rpx;
  margin-bottom: $uni-spacing-col-base;
}

.mode-tab {
  flex: 1;
  text-align: center;
  padding: 16rpx 0;
  border-radius: $border-radius;
  cursor: pointer;
  transition: all $transition-fast;

  .tab-icon {
    font-size: $uni-font-size-lg;
    margin-right: 8rpx;
  }

  .tab-text {
    font-size: $uni-font-size-base;
    font-weight: $font-weight-medium;
  }

  &.active {
    background: $primary-color;
    color: $uni-text-color-inverse;
    box-shadow: $box-shadow-light;
  }

  &:hover:not(.active) {
    background: rgba($primary-color, 0.1);
  }
}

.header-actions {
  display: flex;
  gap: $uni-spacing-col-base;

  .action-btn {
    flex: 1;
    height: $button-height;
    border-radius: $uni-border-radius-lg;
    font-size: $uni-font-size-base;
    font-weight: $font-weight-medium;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all $transition-fast;

    &.btn-secondary {
      background: $uni-bg-color;
      color: $text-regular;
      border: 1rpx solid $border-color-light;

      &:active {
        background: $background-color;
        border-color: $border-color;
      }
    }

    &.btn-danger {
      background: $danger-bg;
      color: $danger-color;
      border: 1rpx solid $danger-border;

      &:active {
        background: color.adjust($danger-bg, $lightness: -10%);
      }
    }

    &.btn-primary {
      background: $primary-color;
      color: $uni-bg-color;
      border: none;

      &:active {
        background: color.adjust($primary-color, $lightness: -10%);
      }
    }
  }
}

/* 预览模式样式 */
.template-header {
  background: $uni-bg-color;
  padding: $uni-spacing-row-base;
  border-bottom: 1rpx solid $border-color-extra-light;
}

.header-main {
  display: flex;
  align-items: center;
  gap: $uni-spacing-col-base;
}

.template-badge {
  width: 100rpx;
  height: 100rpx;
  border-radius: $uni-border-radius-lg;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
  box-shadow: $box-shadow;
}

.badge-text {
  font-size: $font-size-extra-large;
  color: $uni-text-color-inverse;
  font-weight: $font-weight-bold;
}

.template-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: $uni-spacing-col-sm;
}

.template-name {
  font-size: $uni-font-size-lg;
  color: $text-primary;
  font-weight: $font-weight-bold;
  line-height: 1.4;
}

.template-description {
  font-size: $uni-font-size-base;
  color: $text-regular;
  line-height: 1.5;
}

.template-meta {
  display: flex;
  flex-wrap: wrap;
  gap: $uni-spacing-col-base;
  align-items: center;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: math.div($uni-spacing-col-sm, 2);
}

.meta-label {
  font-size: $uni-font-size-sm;
  color: $text-secondary;
}

.meta-value {
  font-size: $uni-font-size-sm;
  color: $text-regular;
  font-weight: $font-weight-medium;
}

.status-tag {
  padding: math.div($uni-spacing-col-sm, 2) $uni-spacing-col-sm;
  border-radius: $uni-border-radius-sm;
  font-size: $uni-font-size-sm;
  font-weight: $font-weight-medium;

  &.active {
    background: $success-bg;
    color: $success-color;
    border: 1rpx solid $success-border;
  }

  &.inactive {
    background: $background-color;
    color: $uni-text-color-grey;
    border: 1rpx solid $border-color-light;
  }
}

/* 模板预览 */
.preview-section {
  padding: $uni-spacing-row-base;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $uni-spacing-col-base;
}

.section-title {
  font-size: $uni-font-size-lg;
  color: $text-primary;
  font-weight: $font-weight-bold;
}

.device-switch {
  display: flex;
  gap: 8rpx;
}

.device-btn {
  padding: 8rpx 16rpx;
  border: 1rpx solid $border-color;
  border-radius: $uni-border-radius-sm;
  background: $uni-bg-color;
  font-size: $uni-font-size-sm;
  display: flex;
  align-items: center;
  gap: 4rpx;

  &.active {
    background: $primary-color;
    color: $uni-bg-color;
    border-color: $primary-color;
  }

  .device-icon {
    font-size: $uni-font-size-base;
  }
}

.preview-card {
  background: $uni-bg-color;
  border-radius: $border-radius-large;
  padding: $uni-spacing-row-base;
  box-shadow: $card-shadow;
}

.preview-info {
  margin-top: $margin-base;
  padding: $padding-base;
  background: $background-color;
  border-radius: $border-radius;
  display: flex;
  flex-direction: column;
  gap: $uni-spacing-col-sm;
}

.info-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.info-label {
  font-size: $uni-font-size-base;
  color: $text-secondary;
}

.info-value {
  font-size: $uni-font-size-base;
  color: $text-primary;
  font-weight: $font-weight-medium;
}

/* 配置详情 */
.config-section {
  padding: 0 $uni-spacing-row-base $uni-spacing-row-base;
}

.config-card {
  background: $uni-bg-color;
  border-radius: $border-radius-large;
  padding: $padding-base;
  margin-bottom: $margin-base;
  box-shadow: $card-shadow;
}

.config-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $margin-base;
  padding-bottom: $padding-small;
  border-bottom: 1rpx solid $border-color-extra-light;
}

.config-title {
  font-size: $uni-font-size-lg;
  color: $text-primary;
  font-weight: $font-weight-medium;
}

.config-count {
  font-size: $uni-font-size-sm;
  color: $text-secondary;
  background: $background-color;
  padding: math.div($uni-spacing-col-sm, 2) $uni-spacing-col-sm;
  border-radius: $uni-border-radius-sm;
}

.config-content {
  display: flex;
  flex-direction: column;
  gap: $uni-spacing-col-sm;
}

.config-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: math.div($uni-spacing-col-sm, 2) 0;
}

.config-label {
  font-size: $uni-font-size-base;
  color: $text-secondary;
  flex-shrink: 0;
}

.config-value {
  font-size: $uni-font-size-base;
  color: $text-primary;
  font-weight: $font-weight-medium;
  text-align: right;
  margin-left: $uni-spacing-col-sm;
}

.color-theme {
  display: flex;
  gap: $uni-spacing-col-sm;
}

.color-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.color-dot {
  width: 32rpx;
  height: 32rpx;
  border-radius: $uni-border-radius-circle;
  border: 1rpx solid rgba(0, 0, 0, 0.1);
}

.color-label {
  font-size: $uni-font-size-sm;
  color: $text-secondary;
}

.color-value {
  padding: math.div($uni-spacing-col-sm, 2) $uni-spacing-col-sm;
  border-radius: $uni-border-radius-sm;
  color: $uni-text-color-inverse;
  text-shadow: 0 1rpx 2rpx rgba(0, 0, 0, 0.3);
  font-family: monospace;
  font-size: $uni-font-size-sm;
  min-width: 120rpx;
  text-align: center;
}

.font-sizes {
  display: flex;
  gap: $uni-spacing-col-base;
}

.font-size-item {
  font-size: $uni-font-size-sm;
  color: $text-primary;
}

.column-widths {
  display: flex;
  gap: $uni-spacing-col-base;
}

.components-list {
  display: flex;
  flex-direction: column;
  gap: $uni-spacing-col-sm;
}

.component-item {
  display: flex;
  align-items: center;
  padding: $padding-small;
  background: $background-color;
  border-radius: $uni-border-radius-lg;
  border: 1rpx solid $border-color-light;
  transition: all $transition-fast;

  &:active {
    background: color.adjust($background-color, $lightness: -5%);
  }
}

.component-index {
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $avatar-bg-color;
  border-radius: $uni-border-radius-circle;
  margin-right: $uni-spacing-col-base;
  flex-shrink: 0;
}

.index-text {
  font-size: $uni-font-size-base;
  color: $text-secondary;
  font-weight: $font-weight-medium;
}

.component-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: math.div($uni-spacing-col-sm, 2);
}

.component-name {
  font-size: $uni-font-size-base;
  color: $text-primary;
  font-weight: $font-weight-medium;
}

.component-desc {
  font-size: $uni-font-size-sm;
  color: $text-secondary;
}

.empty-state {
  padding: $padding-base 0;
  flex-direction: column;
}

.empty-icon {
  font-size: $font-size-extra-large;
  margin-bottom: $uni-spacing-col-sm;
}

.empty-text {
  font-size: $uni-font-size-base;
  color: $text-placeholder;
}

/* 编辑模式样式 - 响应式布局 */
.editor-container {
  display: flex;
  padding: $uni-spacing-row-base;
  gap: $uni-spacing-col-base;
  min-height: calc(100vh - 200rpx);
  position: relative;

  /* 默认桌面布局 */
  flex-direction: row;
}

.config-panel {
  flex: 1;
  background: $background-color-white;
  border-radius: $border-radius-large;
  box-shadow: $box-shadow;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  /* 桌面端：正常宽度 */
  width: 100%;
}

/* 右侧预览面板 - 响应式定位 */
.preview-panel {
  flex: 0 0 500rpx;
  background: $background-color-white;
  border-radius: $border-radius-large;
  box-shadow: $box-shadow;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  /* 桌面端：固定在右侧 */
  position: sticky;
  top: 100rpx;
  height: calc(100vh - 200rpx);
  overflow-y: auto;

  /* 添加滚动条样式 */
  &::-webkit-scrollbar {
    width: 4rpx;
  }

  &::-webkit-scrollbar-thumb {
    background: $border-color-light;
    border-radius: 2rpx;
  }
}

.config-tabs {
  display: flex;
  background: $background-color;
  padding: 8rpx;
  border-bottom: 2rpx solid $border-color-lighter;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  position: relative;
  cursor: pointer;
  transition: all $transition-fast $ease-in-out;
}

.tab-text {
  font-size: $font-size-base;
  color: $text-secondary;
  font-weight: $font-weight-medium;
}

.tab-item.active .tab-text {
  color: $primary-color;
  font-weight: $font-weight-semibold;
}

.tab-indicator {
  position: absolute;
  bottom: -2rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 60%;
  height: 4rpx;
  background: $primary-color;
  border-radius: 2rpx;
}

.config-content {
  flex: 1;
  padding: $padding-base;
  overflow-y: auto;
}

.config-section {
  .component-header {
    margin-bottom: $margin-base;
    padding-bottom: $padding-small;
    border-bottom: 1px solid $border-color-lighter;
  }

  .component-title {
    display: block;
    font-size: $font-size-large;
    font-weight: $font-weight-semibold;
    color: $text-primary;
    margin-bottom: 4rpx;
  }

  .component-desc {
    font-size: $font-size-small;
    color: $text-secondary;
  }
}

.form-group {
  margin-bottom: $margin-base;
}

.form-label {
  display: block;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: $text-primary;
  margin-bottom: var(--margin-mini);
}

.form-label.required::after {
  content: '*';
  color: $danger-color;
  margin-left: 4rpx;
}

.form-input {
  width: 100%;
  padding: 20rpx;
  border: 2rpx solid $border-color-lighter;
  border-radius: $border-radius;
  font-size: $font-size-base;
  color: $text-primary;
  background: $background-color-white;
  transition: all $transition-fast $ease-in-out;
}

.form-input:focus {
  border-color: $primary-color;
  box-shadow: $input-focus-shadow;
  outline: none;
}

.form-input.disabled {
  background: $background-color;
  color: $text-secondary;
  cursor: not-allowed;
}

.form-tip {
  display: block;
  font-size: $font-size-small;
  color: $text-secondary;
  margin-top: 4rpx;
}

.preview-image-container {
  position: relative;
  width: 100%;
  height: 240rpx;
  border: 2rpx dashed $border-color-light;
  border-radius: $border-radius;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.preview-image-container:active {
  opacity: 0.9;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: $background-color;
}

.preview-image-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.preview-image-icon {
  font-size: 64rpx;
  color: $primary-color;
  margin-bottom: $uni-spacing-col-sm;
}

.preview-image-text {
  font-size: $font-size-base;
  color: $text-placeholder;
}

.upload-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: $uni-bg-color-mask;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-progress {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.progress-circle {
  position: relative;
  width: 60rpx;
  height: 60rpx;
  margin-bottom: $uni-spacing-col-sm;
}

.circle-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  border: 4rpx solid rgba($background-color-white, 0.3);
  border-radius: $border-radius-round;
}

.circle-fill {
  position: absolute;
  width: 100%;
  height: 100%;
  border: 4rpx solid $background-color-white;
  border-radius: $border-radius-round;
  clip: rect(0, 30rpx, 60rpx, 0);
  transform-origin: center;
}

.progress-text {
  font-size: $font-size-small;
  color: $background-color-white;
  font-weight: $font-weight-medium;
}

.preview-image-tips {
  display: block;
  font-size: $font-size-extra-small;
  color: $text-placeholder;
  margin-top: $margin-mini;
  text-align: center;
}

.form-textarea {
  width: 100%;
  min-height: 160rpx;
  padding: 20rpx;
  border: 2rpx solid $border-color-lighter;
  border-radius: $border-radius;
  font-size: $font-size-base;
  color: $text-primary;
  background: $background-color-white;
  resize: vertical;
}

.form-textarea:focus {
  border-color: $primary-color;
  box-shadow: $input-focus-shadow;
  outline: none;
}

.textarea-footer {
  margin-top: 8rpx;
}

.textarea-tip {
  font-size: $font-size-small;
  color: $text-secondary;
}

.textarea-count {
  font-size: $font-size-small;
  color: $text-secondary;
}

.layout-types {
  display: flex;
  gap: $margin-small;
  flex-wrap: wrap;
}

.layout-type-item {
  flex: 1;
  min-width: 0;
  padding: 24rpx 16rpx;
  border: 2rpx solid $border-color-lighter;
  border-radius: $border-radius;
  background: $background-color-white;
  text-align: center;
  cursor: pointer;
  transition: all $transition-fast $ease-in-out;
}

.layout-type-item:hover {
  border-color: $primary-color;
  transform: translateY(-2rpx);
}

.layout-type-item.active {
  border-color: $primary-color;
  background: rgba($primary-color, 0.05);
  box-shadow: $input-focus-shadow;
}

.layout-icon {
  width: 48rpx;
  height: 48rpx;
  margin: 0 auto 12rpx;
  background: $background-color;
  border-radius: var(--border-radius-small);
}

.layout-icon.single {
  background: linear-gradient(90deg, $primary-color 100%, transparent 0%);
}

.layout-icon.double {
  background: linear-gradient(90deg, $primary-color 40%, $secondary-color 40%, $secondary-color 100%);
}

.layout-icon.triple {
  background: linear-gradient(90deg, $primary-color 33%, $secondary-color 33%, $secondary-color 66%, $primary-color 66%);
}

.layout-icon.creative {
  background: linear-gradient(45deg, $primary-color 25%, $secondary-color 25%, $secondary-color 50%, $primary-color 50%, $primary-color 75%, $secondary-color 75%);
  background-size: 20rpx 20rpx;
}

.layout-name {
  font-size: $font-size-small;
  font-weight: $font-weight-medium;
  color: $text-primary;
}

.layout-config {
  background: $background-color;
  border-radius: $border-radius;
  padding: $padding-base;
  margin: $margin-base 0;
}

.layout-preview-container {
  margin-bottom: $margin-base;
}

.preview-title {
  display: block;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: $text-primary;
  margin-bottom: $margin-small;
}

.two-column-preview {
  height: 120rpx;
  background: $background-color-white;
  border: 2rpx solid $border-color-lighter;
  border-radius: $border-radius;
  overflow: hidden;
  display: flex;
}

.column-left,
.column-right {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: $font-weight-medium;
  color: white;
}

.column-label {
  font-size: $font-size-small;
}

.column-left {
  background: $primary-color;
}

.column-right {
  background: $secondary-color;
}

.width-controls {
  .control-group {
    margin-bottom: $margin-base;
  }

  .control-label {
    display: block;
    font-size: $font-size-base;
    color: $text-primary;
    margin-bottom: var(--margin-mini);
  }
}

.form-slider {
  margin: 0;
}

.component-recommendation {
  margin-bottom: $margin-base;
  padding: $padding-base;
  background: rgba($success-color, 0.05);
  border-radius: $border-radius;
  border: 1px solid rgba($success-color, 0.2);
}

.recommendation-title {
  font-size: $font-size-small;
  color: $success-color;
  font-weight: $font-weight-medium;
  margin-bottom: $padding-small;
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.recommendation-title:before {
  content: '⭐';
}

.recommendation-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
}

.recommendation-item {
  padding: 12rpx 16rpx;
  background: white;
  border: 1px solid $border-color-lighter;
  border-radius: var(--border-radius-small);
  font-size: $font-size-small;
  color: $text-primary;
  cursor: pointer;
  transition: all 0.2s ease;
}

.recommendation-item:active {
  background: $background-color;
  transform: translateY(1rpx);
}

.recommendation-item:hover {
  border-color: $primary-color;
  background: rgba($primary-color, 0.05);
}

.component-list {
  margin-bottom: $margin-base;
}

.component-item {
  margin-bottom: $margin-small;
  background: $background-color-white;
  border: 2rpx solid $border-color-lighter;
  border-radius: $border-radius;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
}

.component-item:hover {
  border-color: rgba($primary-color, 0.5);
  box-shadow: $box-shadow-light;
}

.component-item.selected {
  border-color: $primary-color;
  background: rgba($primary-color, 0.05);
  box-shadow: 0 4rpx 16rpx rgba($primary-color, 0.15);
}

.component-item.selected .component-index {
  background: $primary-color;
  color: white;
}

.component-item-main {
  display: flex;
  align-items: center;
  padding: $padding-small;
  gap: $padding-small;
}

.component-index {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $background-color;
  border-radius: 50%;
  font-weight: $font-weight-semibold;
  color: $text-secondary;
  transition: all 0.3s ease;
}

.index-text {
  font-size: $font-size-base;
}

.component-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.component-name-display {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8rpx;
}

.component-name {
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: $text-primary;
  display: block;
}

.component-key {
  font-size: $font-size-small;
  color: $text-secondary;
  font-family: monospace;
  background: rgba($primary-color, 0.05);
  padding: 2rpx 8rpx;
  border-radius: 4rpx;
}

.component-description {
  margin-top: 4rpx;
}

.desc-text {
  font-size: $font-size-small;
  color: $text-secondary;
  font-style: italic;
}

.component-picker .picker-display {
  padding: 12rpx;
  border: 1px solid $border-color-lighter;
  border-radius: var(--border-radius-small);
  background: $background-color-white;
  font-size: $font-size-base;
  color: $text-primary;
  transition: all 0.2s ease;
}

.component-picker .picker-display:active {
  background: $background-color;
}

.picker-arrow {
  color: $text-secondary;
  font-size: $font-size-small;
}

.component-actions {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.action-btn {
  width: 64rpx;
  height: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: var(--border-radius-small);
  font-size: $font-size-small;
  transition: all 0.2s ease;
}

.move-up-btn {
  background: rgba($success-color, 0.1);
  color: $success-color;
}

.move-up-btn:not(.disabled):active {
  background: rgba($success-color, 0.2);
}

.move-up-btn.disabled {
  background: rgba($text-secondary, 0.1);
  color: $text-secondary;
  cursor: not-allowed;
  opacity: 0.5;
}

.move-down-btn {
  background: rgba($warning-color, 0.1);
  color: $warning-color;
}

.move-down-btn:not(.disabled):active {
  background: rgba($warning-color, 0.2);
}

.move-down-btn.disabled {
  background: rgba($text-secondary, 0.1);
  color: $text-secondary;
  cursor: not-allowed;
  opacity: 0.5;
}

.remove-btn {
  background: rgba($danger-color, 0.1);
  color: $danger-color;
  margin-top: 4rpx;
}

.remove-btn:active {
  background: rgba($danger-color, 0.2);
}

.action-icon {
  font-weight: $font-weight-bold;
  font-size: 16rpx;
}

.component-details-container {
  padding: 0 $padding-small $padding-small;
}

.component-details-btn {
  width: 100%;
  padding: 8rpx;
  background: rgba($info-color, 0.05);
  border: 1px solid rgba($info-color, 0.2);
  border-radius: var(--border-radius-small);
  color: $info-color;
  font-size: $font-size-small;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4rpx;
}

.component-details-btn:active {
  background: rgba($info-color, 0.1);
}

.details-icon {
  font-size: $font-size-base;
}

.details-text {
  font-size: $font-size-small;
}

.sort-controls {
  margin-bottom: $margin-base;
  padding: $padding-base;
  background: $background-color;
  border-radius: $border-radius;
  border: 1px solid $border-color-lighter;
}

.sort-hint {
  display: block;
  font-size: $font-size-small;
  color: $text-primary;
  margin-bottom: $padding-small;
  font-weight: $font-weight-medium;
}

.sort-hint:before {
  content: '📌 ';
}

.sort-buttons {
  display: flex;
  gap: $padding-small;
}

.sort-btn {
  flex: 1;
  padding: 16rpx;
  background: rgba($primary-color, 0.1);
  border: 1px solid $primary-color;
  border-radius: var(--border-radius-small);
  color: $primary-color;
  font-size: $font-size-small;
  font-weight: $font-weight-medium;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  transition: all 0.2s ease;
}

.sort-btn:not(.disabled):active {
  background: rgba($primary-color, 0.2);
  transform: translateY(1rpx);
}

.sort-btn.disabled {
  background: rgba($text-secondary, 0.1);
  border-color: $text-secondary;
  color: $text-secondary;
  cursor: not-allowed;
  opacity: 0.5;
}

.sort-icon {
  font-size: $font-size-base;
}

.component-help {
  margin-top: $margin-base;
  padding: 12rpx;
  background: rgba($info-color, 0.05);
  border-radius: var(--border-radius-small);
  font-size: $font-size-small;
  color: $text-secondary;
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.help-icon {
  color: $info-color;
  font-size: $font-size-base;
}

.help-text {
  flex: 1;
}

.add-component-btn {
  width: 100%;
  padding: 24rpx;
  background: rgba($primary-color, 0.05);
  border: 2rpx dashed rgba($primary-color, 0.3);
  border-radius: $border-radius;
  color: $primary-color;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  transition: all $transition-fast $ease-in-out;
  position: relative;
  overflow: hidden;
}

.add-component-btn:hover {
  background: rgba($primary-color, 0.1);
  border-color: $primary-color;
}

.add-component-btn:active {
  background: rgba($primary-color, 0.15);
}

.add-icon {
  font-size: $font-size-large;
  font-weight: $font-weight-bold;
}

.component-count {
  margin-top: $margin-base;
  padding: $padding-small;
  background: $background-color;
  border-radius: $border-radius;
  display: flex;
  justify-content: space-between;
}

.count-label,
.count-valid {
  font-size: $font-size-small;
  color: $text-primary;
  font-weight: $font-weight-medium;
}

.color-config {
  background: $background-color;
  border-radius: $border-radius;
  padding: $padding-base;
  margin-bottom: $margin-base;
}

.color-config .config-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $margin-base;
}

.color-config .config-title {
  font-size: $font-size-base;
  font-weight: $font-weight-semibold;
  color: $text-primary;
}

.reset-btn {
  padding: 8rpx 16rpx;
  background: rgba($danger-color, 0.1);
  color: $danger-color;
  border: 1px solid $danger-color;
  border-radius: var(--border-radius-small);
  font-size: $font-size-small;
  transition: all 0.2s ease;
}

.reset-btn:active {
  background: rgba($danger-color, 0.2);
}

.color-group .color-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $padding-small 0;
  border-bottom: 1px solid $border-color-lighter;
}

.color-group .color-item:last-child {
  border-bottom: none;
}

.color-info {
  flex: 1;
}

.color-label {
  display: block;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: $text-primary;
  margin-bottom: 4rpx;
}

.color-desc {
  font-size: $font-size-small;
  color: $text-secondary;
}

.color-controls {
  display: flex;
  align-items: center;
  gap: $padding-small;
}

.color-input {
  width: 160rpx;
  padding: 12rpx;
  border: 1px solid $border-color-lighter;
  border-radius: var(--border-radius-small);
  font-size: $font-size-base;
  text-align: center;
  font-family: monospace;
  background: white;
  transition: all 0.2s ease;
}

.color-input:focus {
  border-color: $primary-color;
  box-shadow: 0 0 0 2px rgba($primary-color, 0.1);
  outline: none;
}

.color-input::placeholder {
  color: #c0c4cc;
}

.color-preview {
  width: 48rpx;
  height: 48rpx;
  border-radius: var(--border-radius-small);
  border: 2rpx solid $border-color-lighter;
  cursor: pointer;
  transition: all 0.2s ease;
}

.color-preview:hover {
  transform: scale(1.1);
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.15);
}

.color-preview:active {
  transform: scale(0.95);
}

.color-presets {
  margin-top: $margin-base;
  padding-top: $padding-base;
  border-top: 1px solid $border-color-lighter;
}

.presets-title {
  font-size: $font-size-small;
  color: $text-secondary;
  margin-bottom: $padding-small;
}

.presets-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 8rpx;
}

.preset-color {
  width: 36rpx;
  height: 36rpx;
  border-radius: 4rpx;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.preset-color:hover {
  transform: scale(1.2);
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
}

.preset-color:active {
  transform: scale(0.9);
}

.font-config {
  background: $background-color;
  border-radius: $border-radius;
  padding: $padding-base;
  margin-bottom: $margin-base;
}

.font-config .config-header {
  margin-bottom: $margin-base;
}

.font-sizes .size-item {
  padding: $padding-small 0;
  border-bottom: 1px solid $border-color-lighter;
}

.font-sizes .size-item:last-child {
  border-bottom: none;
}

.size-label {
  display: block;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: $text-primary;
  margin-bottom: var(--margin-mini);
}

.size-controls {
  display: flex;
  align-items: center;
  gap: $padding-small;
}

.size-input {
  width: 80rpx;
  padding: 12rpx;
  border: 1px solid $border-color-lighter;
  border-radius: var(--border-radius-small);
  font-size: $font-size-base;
  text-align: center;
}

.size-unit {
  font-size: $font-size-base;
  color: $text-secondary;
}

.size-slider {
  flex: 1;
}

.size-slider-bar {
  margin: 0;
}

.spacing-config {
  background: $background-color;
  border-radius: $border-radius;
  padding: $padding-base;
}

.spacing-config .config-header {
  margin-bottom: $margin-base;
}

.spacing-items .spacing-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $padding-small 0;
  border-bottom: 1px solid $border-color-lighter;
}

.spacing-items .spacing-item:last-child {
  border-bottom: none;
}

.spacing-info {
  flex: 1;
}

.spacing-label {
  display: block;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: $text-primary;
  margin-bottom: 4rpx;
}

.spacing-desc {
  font-size: $font-size-small;
  color: $text-secondary;
}

.spacing-input {
  width: 120rpx;
  padding: 12rpx;
  border: 1px solid $border-color-lighter;
  border-radius: var(--border-radius-small);
  font-size: $font-size-base;
  text-align: center;
}

/* 预览面板样式 */
.preview-header {
  padding: $padding-base;
  border-bottom: 1px solid $border-color-lighter;
  background: linear-gradient(to right, rgba($primary-color, 0.03), rgba($secondary-color, 0.03));
}

.preview-title-container {
  .preview-title {
    display: block;
    font-size: $font-size-large;
    font-weight: $font-weight-semibold;
    color: $text-primary;
    margin-bottom: 4rpx;
  }

  .preview-subtitle {
    font-size: $font-size-small;
    color: $text-secondary;
  }
}

.preview-controls {
  display: flex;
  align-items: center;
  gap: $margin-base;
}

.switch-label {
  font-size: $font-size-base;
  color: $text-primary;
}

.switch-buttons {
  display: flex;
  gap: 8rpx;
}

.refresh-btn {
  padding: 12rpx 24rpx;
  background: $primary-color;
  color: white;
  border: none;
  border-radius: $border-radius;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.refresh-icon {
  font-size: $font-size-base;
}

.preview-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: $padding-base;
}

.preview-content.desktop .template-preview-container {
  max-width: 100%;
  height: 400rpx;
}

.preview-content.tablet .template-preview-container {
  max-width: 768px;
  height: 600rpx;
  margin: 0 auto;
  border-radius: 16rpx;
}

.preview-content.mobile .template-preview-container {
  max-width: 375px;
  height: 600rpx;
  margin: 0 auto;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.template-preview-container {
  width: 100%;
  height: 100%;
  min-height: 400rpx;
  overflow-y: auto;
  background: #ffffff;
  border: 1px solid #ebeef5;
  border-radius: 8rpx;
}

.preview-info {
  margin-top: $margin-base;
  padding: $padding-base;
  background: $background-color;
  border-radius: $border-radius;
}

.info-title {
  display: block;
  font-size: $font-size-base;
  font-weight: $font-weight-semibold;
  color: $text-primary;
  margin-bottom: $margin-small;
}

.info-items .info-item {
  display: flex;
  justify-content: space-between;
  padding: 8rpx 0;
  border-bottom: 1px solid $border-color-lighter;
}

.info-items .info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-size: $font-size-base;
  color: $text-secondary;
}

.info-value {
  font-size: $font-size-base;
  color: $text-primary;
  font-weight: $font-weight-medium;
}

/* 加载状态 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba($uni-bg-color, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: $z-index-modal;
}

.loading-content {
  background: $uni-bg-color;
  border-radius: $border-radius-large;
  padding: $uni-spacing-row-base;
  box-shadow: $box-shadow-dark;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $uni-spacing-col-sm;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid $border-color-light;
  border-top-color: $primary-color;
  border-radius: $uni-border-radius-circle;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: $uni-font-size-base;
  color: $text-regular;
}

/* 工具类 */
.flex-center {
  display: flex;
  justify-content: center;
  align-items: center;
}

.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 响应式调整 - 移动端小屏 */
@media (max-width: 768px) {
  .editor-container {
    flex-direction: column;
    padding: $uni-spacing-row-sm;
    gap: $uni-spacing-col-sm;
  }

  .config-panel {
    width: 100%;
    margin-bottom: 520rpx; /* 为底部的预览面板留出空间 */
  }

  .preview-panel {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    top: auto;
    height: 500rpx;
    width: 100%;
    z-index: 1000;
    border-radius: $border-radius-large $border-radius-large 0 0;
    box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.1);

    /* 重置桌面端样式 */
    position: fixed;
    top: auto;
    height: 500rpx;
    overflow-y: auto;
  }

  .preview-header {
    padding: $padding-small;
  }

  .preview-controls {
    flex-direction: column;
    gap: $margin-small;
  }

  .device-switch {
    flex-direction: column;
    align-items: flex-start;
  }

  .switch-buttons {
    width: 100%;
    justify-content: space-between;
  }

  .preview-content {
    padding: $padding-small;
  }

  .preview-content.desktop .template-preview-container,
  .preview-content.tablet .template-preview-container,
  .preview-content.mobile .template-preview-container {
    height: 300rpx;
  }
}

/* 响应式调整 - 平板端中屏 */
@media (min-width: 769px) and (max-width: 1024px) {
  .editor-container {
    flex-direction: row;
  }

  .config-panel {
    flex: 0 0 55%;
  }

  .preview-panel {
    flex: 0 0 40%;
    position: sticky;
    top: 100rpx;
    height: calc(100vh - 200rpx);
  }

  .preview-content.desktop .template-preview-container,
  .preview-content.tablet .template-preview-container,
  .preview-content.mobile .template-preview-container {
    height: 350rpx;
  }
}

/* 响应式调整 - 桌面端大屏 */
@media (min-width: 1025px) {
  .editor-container {
    flex-direction: row;
  }

  .config-panel {
    flex: 0 0 60%;
  }

  .preview-panel {
    flex: 0 0 35%;
    position: sticky;
    top: 100rpx;
    height: calc(100vh - 200rpx);
  }

  .preview-content.desktop .template-preview-container,
  .preview-content.tablet .template-preview-container,
  .preview-content.mobile .template-preview-container {
    height: 400rpx;
  }
}

/* 通用响应式调整 */
@media (max-width: $screen-md) {
  .mode-switch .mode-tab .tab-text {
    display: none;
  }

  .header-actions {
    flex-direction: column;
  }

  .header-actions .action-btn {
    width: 100%;
  }

  .device-switch .device-btn .device-name {
    display: none;
  }

  .color-presets .presets-grid {
    grid-template-columns: repeat(4, 1fr);
  }

  .component-management .component-item .component-item-main {
    flex-direction: column;
    align-items: stretch;
  }

  .component-management .component-item .component-item-main .component-index {
    align-self: flex-start;
    margin-bottom: 8rpx;
  }

  .component-management .component-item .component-item-main .component-info .component-name-display {
    flex-direction: column;
    align-items: flex-start;
  }

  .component-management .component-item .component-item-main .component-info .component-name-display .component-key {
    margin-left: 0;
    margin-top: 4rpx;
  }

  .component-management .component-item .component-item-main .component-actions {
    flex-direction: row;
    justify-content: flex-end;
    margin-top: 12rpx;
  }

  .component-management .component-item .component-item-main .component-actions .action-btn {
    width: 32rpx;
    height: 32rpx;
  }

  .component-management .component-item .component-item-main .component-actions .action-btn .action-icon {
    font-size: 14rpx;
  }

  .component-management .component-recommendation .recommendation-list {
    flex-direction: column;
  }

  .component-management .component-recommendation .recommendation-list .recommendation-item {
    width: 100%;
  }

  .component-management .sort-controls .sort-buttons {
    flex-direction: column;
  }

  .preview-header {
    flex-direction: column;
    gap: $margin-base;
  }

  .preview-header .preview-controls {
    width: 100%;
    justify-content: space-between;
  }

  .form-row {
    flex-direction: column;
  }

  .color-config .color-item {
    flex-direction: column;
    align-items: flex-start;
    gap: $padding-small;
  }

  .color-config .color-item .color-info {
    width: 100%;
  }

  .color-config .color-item .color-controls {
    width: 100%;
    justify-content: space-between;
  }

  .color-config .color-item .color-controls .color-input {
    flex: 1;
  }
}
</style>