<template>
  <!-- 查看模式 -->
  <view v-if="currentMode === 'view'" class="resume-view-container">

    <!-- 简历预览内容 -->
    <scroll-view class="preview-content" scroll-y="true">
      <!-- 动态模板引擎渲染 -->
      <DynamicResumesRenderer
        :resume-data="previewData"
        ref="dynamicResumesRenderer"
      />
    </scroll-view>

    <!-- 底部操作栏 -->
    <view class="view-footer">
      <button class="footer-btn edit-btn" @click="switchToEditMode">
        <text class="footer-icon">✏️</text>
        <text class="footer-text">编辑</text>
      </button>
      <button class="footer-btn delete-btn" @click="handleDelete">
        <text class="footer-icon">🗑️</text>
        <text class="footer-text">删除</text>
      </button>
      <button class="footer-btn download-btn" @click="handleDownload">
        <text class="footer-icon">⬇️</text>
        <text class="footer-text">下载</text>
      </button>
      <button class="footer-btn share-btn" @click="showShareOptions">
        <text class="footer-icon">📤</text>
        <text class="footer-text">分享</text>
      </button>
    </view>

    <!-- 分享弹窗 -->
    <view v-if="showShareModal" class="custom-modal-overlay" @click="hideShareOptions">
      <view class="custom-modal" @click.stop>
        <view class="modal-header">
          <text class="modal-title">分享简历</text>
          <button class="modal-close" @click="hideShareOptions">✕</button>
        </view>
        <view class="modal-content">
          <view class="share-options">
            <button class="share-option" @click="shareToWeChat">
              <view class="option-icon wechat">💬</view>
              <text class="option-text">微信好友</text>
            </button>

            <button class="share-option" @click="generateQRCode">
              <view class="option-icon qrcode">📱</view>
              <text class="option-text">二维码</text>
            </button>

            <button class="share-option" @click="copyShareLink">
              <view class="option-icon link">🔗</view>
              <text class="option-text">复制链接</text>
            </button>
          </view>

          <!-- 二维码显示区域 -->
          <view v-if="qrcodeUrl" class="qrcode-section">
            <text class="qrcode-title">扫描二维码查看简历</text>
            <image :src="qrcodeUrl" class="qrcode-image" mode="widthFix" />
            <text class="qrcode-hint">该链接有效期7天</text>
          </view>
        </view>
      </view>
    </view>
  </view>

  <!-- 编辑模式 -->
  <view v-else-if="currentMode === 'edit'" class="page-container">
    <!-- 主要内容区域 -->
    <view class="main-content" :class="layoutClass">
      <!-- 左侧表单区域 -->
      <scroll-view
        class="form-section"
        scroll-y="true"
        :style="formSectionStyle"
      >
        <view class="form-container">
          <!-- 动态生成的表单区域 -->
          <template v-for="component in sortedComponents" :key="component.id">
            <!-- 用户基本信息 -->
            <template v-if="getComponentKey(component) === 'UserBasicInfo'">
              <view class="form-section-card">
                <view class="form-section-header">
                  <text class="section-title">{{ component.name || "基本信息" }}</text>
                  <view class="section-divider"></view>
                </view>

                <view class="dynamic-form-grid">
                  <view class="form-item">
                    <text class="item-label">姓名</text>
                    <input
                      class="form-input"
                      :value="component.props.name || ''"
                      @input="(e) => updateComponentProp(component.id, 'name', e.detail.value)"
                      placeholder="请输入姓名"
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
                    <text class="item-label">电话</text>
                    <input
                      class="form-input"
                      type="number"
                      :value="component.props.phone || ''"
                      @input="(e) => updateComponentProp(component.id, 'phone', e.detail.value)"
                      placeholder="请输入联系电话"
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
                    />
                  </view>

                  <view class="form-item">
                    <text class="item-label">所在地</text>
                    <input
                      class="form-input"
                      :value="component.props.location || ''"
                      @input="(e) => updateComponentProp(component.id, 'location', e.detail.value)"
                      placeholder="请输入所在城市"
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
                    />
                  </view>

                  <view class="form-item full-width">
                    <text class="item-label">职位标题</text>
                    <input
                      class="form-input"
                      :value="component.props.title || ''"
                      @input="(e) => updateComponentProp(component.id, 'title', e.detail.value)"
                      placeholder="例如：高级工程师"
                    />
                  </view>
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
                        />
                      </view>

                      <view class="form-item">
                        <text class="item-label">职位</text>
                        <input
                          class="form-input"
                          :value="exp.position || ''"
                          @input="(e) => updateArrayField(component.id, 'experiences', index, 'position', e.detail.value)"
                          placeholder="请输入职位"
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

                      <view class="form-item full-width">
                        <text class="item-label">工作描述</text>
                        <textarea
                          class="form-textarea"
                          :value="exp.description || ''"
                          @input="(e) => updateArrayField(component.id, 'experiences', index, 'description', e.detail.value)"
                          placeholder="请描述工作职责和成就"
                        />
                      </view>
                    </view>
                  </view>

                  <button class="add-section-btn" @click="addArrayItem(component.id, 'experiences', {
                    company: '',
                    position: '',
                    startDate: '',
                    endDate: '',
                    description: '',
                    isCurrent: false
                  })">
                    <text class="icon-add">+</text>
                    添加工作经历
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
                    </view>
                  </view>

                  <button class="add-section-btn" @click="addArrayItem(component.id, 'experiences', {
                    university: '',
                    degree: '',
                    major: '',
                    startDate: '',
                    endDate: ''
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
                          />
                          <text class="proficiency-unit">%</text>
                        </view>
                      </view>

                      <view class="form-item full-width">
                        <text class="item-label">技能描述</text>
                        <textarea
                          class="form-textarea"
                          :value="skill.description || ''"
                          @input="(e) => updateArrayField(component.id, 'skills', index, 'description', e.detail.value)"
                          placeholder="请描述技能掌握情况"
                        />
                      </view>
                    </view>
                  </view>

                  <button class="add-section-btn" @click="addArrayItem(component.id, 'skills', {
                    name: '',
                    proficiencyPercent: 0,
                    description: ''
                  })">
                    <text class="icon-add">+</text>
                    添加技能
                  </button>
                </view>
              </view>
            </template>
          </template>
        </view>
      </scroll-view>

      <!-- 右侧预览区域 -->
      <view class="preview-section fixed-preview" :style="previewSectionStyle">
        <view class="preview-container">
          <view class="preview-header">
            <text class="preview-title">简历预览</text>
            <button class="preview-action-btn" @click="refreshPreviewEdit">
              <text class="action-icon">🔄</text>
              <text class="action-text">刷新</text>
            </button>
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

    <!-- 编辑模式操作按钮 -->
    <view class="action-buttons-edit">
      <button class="save-btn" @click="handleSave" :disabled="loading">保存简历</button>
      <button class="back-btn" @click="switchToViewMode" :disabled="loading">返回查看</button>
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
import { computed, onMounted, ref, watch } from "vue";
import { onLoad, onShow } from "@dcloudio/uni-app";
import DynamicResumesRenderer from "@/components/DynamicResumesRenderer.vue";
import ResumesAPI from "@/api/resumes";

// 当前模式：view（查看模式）、edit（编辑模式）
const currentMode = ref("view");
const loading = ref(false);
const resumeId = ref(null);
const templateId = ref(null);
const showShareModal = ref(false);
const qrcodeUrl = ref("");
const dynamicResumesRenderer = ref(null);

// 查看模式数据
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

// 编辑模式数据
const hasLoadedData = ref(false);
const editingData = ref({
  id: null,
  templateId: null,
  globalStyle: {},
  globalLayout: {},
  components: [],
});

const dynamicResumesRendererEdit = ref(null);

// 响应式布局相关变量
const screenWidth = ref(0);
const isH5 = ref(false);
const isWideScreen = ref(false);

// 计算布局类名
const layoutClass = computed(() => {
  if (currentMode.value !== "edit") return "";

  if (isH5.value) {
    return isWideScreen.value ? "h5-wide-layout" : "h5-narrow-layout";
  }

  return "default-layout";
});

// 计算表单区域样式
const formSectionStyle = computed(() => {
  const bottomButtonHeight = "120rpx";

  if (isWideScreen.value && isH5.value) {
    return {
      width: "50%",
      height: `calc(100vh - ${bottomButtonHeight})`,
      position: "fixed",
      left: "0",
      top: "0",
      overflow: "hidden",
    };
  } else {
    return {
      width: "100%",
      height: `calc(50% - ${bottomButtonHeight})`,
      position: "fixed",
      left: "0",
      top: "0",
      overflow: "hidden",
    };
  }
});

// 计算预览区域样式
const previewSectionStyle = computed(() => {
  const bottomButtonHeight = "120rpx";

  if (isWideScreen.value && isH5.value) {
    return {
      width: "50%",
      height: `calc(100vh - ${bottomButtonHeight})`,
      position: "fixed",
      right: "0",
      top: "0",
      borderLeft: "1px solid #e5e5e5",
      boxSizing: "border-box",
      overflow: "hidden",
    };
  } else {
    return {
      width: "100%",
      height: `calc(50% - ${bottomButtonHeight})`,
      position: "fixed",
      left: "0",
      bottom: bottomButtonHeight,
      borderTop: "1px solid #e5e5e5",
      boxSizing: "border-box",
      overflow: "hidden",
    };
  }
});

// 计算预览容器样式
const previewContainerStyle = computed(() => {
  const previewHeaderHeight = "60px";
  return {
    height: `calc(100% - ${previewHeaderHeight})`,
    overflowY: "auto",
  };
});

// 编辑模式选项列表
const genderOptions = ["男", "女"];
const degreeOptions = ["初中", "高中", "大专", "本科", "硕士", "博士"];

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

// 编辑模式计算属性
const getComponentKey = (component) => {
  return component.key || "";
};

const sortedComponents = computed(() => {
  if (!editingData.value?.components) return [];
  return [...editingData.value.components];
});

const previewDataEdit = computed(() => {
  const components = (editingData.value.components || []).map(component => ({
    id: component.id,
    componentId: component.componentId,
    name: component.name,
    key: component.key,
    defaultConfig: component.defaultConfig || {},
    props: component.props || {},
    styles: component.styles || {},
  }));

  return {
    id: editingData.value.id,
    templateId: editingData.value.templateId,
    globalStyle: editingData.value.globalStyle || {},
    globalLayout: editingData.value.globalLayout || {},
    components: components,
  };
});

// 检测屏幕宽度变化
const checkScreenWidth = () => {
  try {
    const systemInfo = uni.getSystemInfoSync();
    screenWidth.value = systemInfo.windowWidth;
    isWideScreen.value = screenWidth.value > 768;
    const platform = systemInfo.platform?.toLowerCase() || "";
    isH5.value = platform.includes("h5") ||
      (typeof window !== "undefined" && window.navigator);
  } catch (error) {
    console.error("获取屏幕信息失败:", error);
    screenWidth.value = 375;
    isH5.value = true;
    isWideScreen.value = false;
  }
};

// 模式切换方法
const switchToViewMode = () => {
  if (resumeId.value) {
    currentMode.value = "view";
    loadResumeData();
  } else {
    uni.showToast({
      title: "请先保存简历",
      icon: "none",
    });
  }
};

const switchToEditMode = () => {
  if (resumeData.value.id) {
    currentMode.value = "edit";
    resumeId.value = resumeData.value.id;
    initializeEditData(resumeData.value);
  }
};

// 查看模式方法
const loadResumeData = async () => {
  if (!resumeId.value) {
    uni.showToast({
      title: "简历ID不存在",
      icon: "error",
    });
    setTimeout(() => {
      uni.navigateBack();
    }, 1500);
    return;
  }

  loading.value = true;

  try {
    resumeData.value = await ResumesAPI.getById(resumeId.value);
    console.log("简历详情加载完成:", resumeData.value);
  } catch (error) {
    console.error("加载简历详情失败:", error);
    uni.showToast({
      title: "加载失败",
      icon: "error",
    });
    setTimeout(() => {
      uni.navigateBack();
    }, 1500);
  } finally {
    loading.value = false;
  }
};

const handleBack = () => {
  uni.navigateBack();
};

const showShareOptions = () => {
  showShareModal.value = true;
};

const hideShareOptions = () => {
  showShareModal.value = false;
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
            uni.navigateTo({
              url: "/pages/resumes/list?refresh=true",
            });
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
      hideShareOptions();
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
  qrcodeUrl.value = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + encodeURIComponent(shareUrl);
  uni.showToast({
    title: "二维码生成成功",
    icon: "success",
  });
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
      hideShareOptions();
    },
    fail: () => {
      uni.showToast({
        title: "复制失败",
        icon: "error",
      });
    },
  });
};

const getBaseUrl = () => {
  return "https://your-domain.com";
};

// 编辑模式方法
const initializeEditData = (data) => {
  editingData.value = {
    id: data.id || null,
    templateId: data.templateId || null,
    globalStyle: data.globalStyle || {},
    globalLayout: data.globalLayout || {},
    components: data.components || [],
  };
  hasLoadedData.value = true;
};

const loadEditData = async () => {
  loading.value = true;

  try {
    let response;

    if (resumeId.value) {
      response = await ResumesAPI.getById(resumeId.value);
    } else if (templateId.value) {
      response = await ResumesAPI.getPreview(templateId.value);
    } else {
      console.error("resumeId和templateId都为空");
      return;
    }

    initializeEditData(response);
    console.log("简历数据加载完成:", editingData.value);

  } catch (error) {
    console.error("加载简历数据失败:", error);
    uni.showToast({
      title: "加载失败",
      icon: "error",
    });
    setTimeout(() => {
      uni.navigateBack();
    }, 2000);
  } finally {
    loading.value = false;
  }
};

const updateComponentProp = (componentId, propName, value) => {
  const componentIndex = editingData.value.components.findIndex(c => c.id === componentId);
  if (componentIndex !== -1) {
    const component = editingData.value.components[componentIndex];
    if (!component.props) component.props = {};
    component.props[propName] = value;
    editingData.value.components.splice(componentIndex, 1, { ...component });
  }
};

const updateArrayField = (componentId, arrayPath, index, fieldName, value) => {
  const componentIndex = editingData.value.components.findIndex(c => c.id === componentId);
  if (componentIndex !== -1) {
    const component = editingData.value.components[componentIndex];
    if (!component.props) component.props = {};
    if (!component.props[arrayPath]) component.props[arrayPath] = [];
    if (!component.props[arrayPath][index]) component.props[arrayPath][index] = {};
    component.props[arrayPath][index][fieldName] = value;
    editingData.value.components.splice(componentIndex, 1, { ...component });
  }
};

const addArrayItem = (componentId, arrayPath, defaultValue = {}) => {
  const componentIndex = editingData.value.components.findIndex(c => c.id === componentId);
  if (componentIndex !== -1) {
    const component = editingData.value.components[componentIndex];
    if (!component.props) component.props = {};
    if (!component.props[arrayPath]) component.props[arrayPath] = [];
    component.props[arrayPath].push({
      id: Date.now() + Math.random(),
      ...defaultValue,
    });
    editingData.value.components.splice(componentIndex, 1, { ...component });
  }
};

const removeArrayItem = (componentId, arrayPath, index) => {
  const componentIndex = editingData.value.components.findIndex(c => c.id === componentId);
  if (componentIndex !== -1) {
    const component = editingData.value.components[componentIndex];
    if (component.props && component.props[arrayPath] && component.props[arrayPath].length > index) {
      component.props[arrayPath].splice(index, 1);
      editingData.value.components.splice(componentIndex, 1, { ...component });
    }
  }
};

const getPickerIndex = (value, options) => {
  if (!value || !options) return 0;
  const index = options.indexOf(value);
  return index >= 0 ? index : 0;
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
      styles: component.styles,
    })),
  };
};

const handleSave = async () => {
  if (!hasLoadedData.value) {
    uni.showToast({
      title: "请先选择模板或简历",
      icon: "none",
    });
    return;
  }

  loading.value = true;
  try {
    const formData = getFormData();
    console.log("保存简历数据:", formData);

    if (resumeId.value) {
      await ResumesAPI.edit(formData);
    } else {
      await ResumesAPI.add(formData);
    }

    uni.showToast({
      title: "保存成功",
      icon: "success",
    });

    uni.navigateTo({
      url: "/pages/resumes/list?refresh=true",
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

const refreshPreviewEdit = () => {
  if (!hasLoadedData.value) {
    uni.showToast({
      title: "请先选择模板或简历",
      icon: "none",
    });
    return;
  }

  uni.showToast({
    title: "预览已刷新",
    icon: "success",
    duration: 1500,
  });
};

// 生命周期
onMounted(() => {
  checkScreenWidth();
});

onLoad((options) => {
  console.log("页面参数:", options);
  checkScreenWidth();

  if (options.resumeId) {
    resumeId.value = options.resumeId;
    if (options.mode === "edit") {
      currentMode.value = "edit";
      loadEditData();
    } else {
      currentMode.value = "view";
      loadResumeData();
    }
  } else if (options.templateId) {
    templateId.value = options.templateId;
    currentMode.value = "edit";
    loadEditData();
  } else {
    uni.showToast({
      title: "参数错误",
      icon: "error",
    });
    setTimeout(() => {
      uni.navigateBack();
    }, 1500);
  }
});

onShow(() => {
  checkScreenWidth();

  if (currentMode.value === "view" && resumeId.value && !resumeData.value.id) {
    loadResumeData();
  } else if (currentMode.value === "edit" && (resumeId.value || templateId.value) && !hasLoadedData.value) {
    loadEditData();
  }
});
</script>

<style lang="scss" scoped>
/* ==================== 查看模式样式 ==================== */
.resume-view-container {
  min-height: 100vh;
  background: $background-color;
  display: flex;
  flex-direction: column;
}

.view-header {
  background: $background-color-white;
  padding: 0 $padding-small;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: $box-shadow-light;
  position: sticky;
  top: 0;
  left: 0;
  right: 0;
  z-index: $z-index-modal;
  flex-shrink: 0;

  .header-back-btn {
    display: flex;
    align-items: center;
    gap: 8rpx;
    background: transparent;
    border: none;
    padding: 0;

    .back-icon {
      font-size: $font-size-large;
      color: $text-secondary;
    }

    .back-text {
      font-size: $font-size-base;
      color: $text-secondary;
    }

    &:active {
      opacity: 0.8;
    }
  }

  .header-title {
    font-size: $font-size-medium;
    font-weight: $font-weight-semibold;
    color: $text-primary;
    flex: 1;
    text-align: center;
  }

  .header-right {
    display: flex;
    justify-content: flex-end;

    .action-btn {
      width: 40px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: $background-color;
      border: 1rpx solid $border-color-lighter;
      border-radius: $border-radius-round;

      .action-icon {
        font-size: $font-size-medium;
      }

      &.edit-btn {
        .action-icon {
          color: $primary-color;
        }
      }

      &:active {
        background: $border-color-lighter;
      }
    }
  }
}

.preview-content {
  flex: 1;
  background: $background-color-white;
  padding: $padding-small;
  box-sizing: border-box;
}

.view-footer {
  background: $background-color-white;
  padding: $padding-mini $padding-small;
  display: flex;
  gap: 16rpx;
  border-top: 1rpx solid $border-color-lighter;
  flex-shrink: 0;
  z-index: $z-index-modal - 1;

  .footer-btn {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 4rpx;
    padding: $padding-mini;
    border-radius: $border-radius-small;
    border: none;
    font-size: $font-size-extra-small;
    transition: all $transition-fast $ease-in-out;

    .footer-icon {
      font-size: $font-size-medium;
    }

    .footer-text {
      font-size: 22rpx;
    }

    &.delete-btn {
      background: $danger-bg;
      color: $danger-color;

      &:active {
        background: color.adjust($danger-color, $alpha: -0.9);
      }
    }

    &.download-btn {
      background: $success-bg;
      color: $success-color;

      &:active {
        background: color.adjust($success-color, $alpha: -0.9);
      }
    }

    &.share-btn {
      background: rgba($primary-color, 0.1);
      color: $primary-color;

      &:active {
        background: rgba($primary-color, 0.2);
      }
    }
  }
}

.custom-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: $z-index-modal + 10;
}

.custom-modal {
  background: $background-color-white;
  border-radius: $border-radius-large;
  width: 90%;
  max-width: 600rpx;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: $box-shadow-dark;
  animation: slideUp 0.3s ease;

  .modal-header {
    padding: $padding-base $padding-small;
    border-bottom: 1rpx solid $border-color-lighter;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .modal-title {
      font-size: $font-size-medium;
      font-weight: $font-weight-semibold;
      color: $text-primary;
    }

    .modal-close {
      font-size: $font-size-medium;
      color: $text-secondary;
      background: transparent;
      border: none;
      padding: 8rpx;
      border-radius: $border-radius-small;

      &:active {
        background: $background-color;
      }
    }
  }

  .modal-content {
    padding: $padding-base;
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
      width: 200rpx;
      height: 200rpx;
    }

    .qrcode-hint {
      font-size: $font-size-extra-small;
      color: $text-secondary;
    }
  }
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* ==================== 编辑模式样式 ==================== */
.page-container {
  min-height: 100vh;
  background: $background-color;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  box-sizing: border-box;
}

.main-content {
  flex: 1;
  position: relative;
  width: 100%;
  height: calc(100vh - 120rpx);
  overflow: hidden;
  box-sizing: border-box;
}

/* 默认布局 */
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

/* H5窄屏布局 */
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

/* H5宽屏布局 */
.h5-wide-layout {
  .form-section {
    width: 50%;
    height: calc(100vh - 120rpx);
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
    height: calc(100vh - 120rpx);
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

.form-section {
  overflow: hidden;

  .form-container {
    padding: $padding-small;
    padding-bottom: 180rpx;
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

.dynamic-form-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24rpx;

  .h5-wide-layout & {
    @media (min-width: 769px) {
      grid-template-columns: repeat(2, 1fr);
    }
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

  .proficiency-input {
    display: flex;
    align-items: center;
    gap: 10rpx;

    .form-input {
      flex: 1;
    }

    .proficiency-unit {
      font-size: $font-size-base;
      color: $text-secondary;
      min-width: 40rpx;
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
  }
}

.experience-list,
.education-list,
.skills-list {
  .experience-item,
  .education-item,
  .skill-item {
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
    }
  }

  .resume-preview-container {
    flex: 1;
    overflow-y: auto;
    width: 100%;
    height: calc(100% - 48px);

    .resume-preview {
      padding: $padding-small;
      background: $background-color;
      min-height: 100%;
      box-sizing: border-box;
    }
  }
}

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

  .save-btn,
  .back-btn {
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
    transition: opacity $transition-fast $ease-in-out;
    white-space: nowrap;
    padding: 0 8rpx;

    &:disabled {
      opacity: $uni-opacity-disabled;
      cursor: not-allowed;
    }
  }

  .save-btn {
    background: $primary-color;
    color: $background-color-white;
  }

  .back-btn {
    background: $background-color;
    color: $text-secondary;
  }
}

/* ==================== 共享样式 ==================== */
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

/* ==================== 响应式调整 ==================== */
@media (max-width: 768px) {
  .view-header {
    height: 50px;
    padding: 0 $padding-mini;

    .header-back-btn {
      .back-text {
        display: none;
      }
    }
  }

  .view-footer {
    padding: $padding-mini $padding-mini;
    gap: 12rpx;

    .footer-btn {
      .footer-text {
        font-size: 20rpx;
      }
    }
  }

  .preview-section.fixed-preview .preview-header {
    padding: 6rpx 12rpx;
    min-height: 44px;
    height: 44px;

    .preview-title {
      font-size: $font-size-small;
    }

    .preview-action-btn {
      padding: 6rpx 10rpx;

      .action-text {
        display: none;
      }
    }
  }
}

@media (min-width: 769px) {
  .preview-section.fixed-preview .preview-header {
    .preview-actions {
      .preview-action-btn {
        .action-text {
          display: inline;
        }
      }
    }
  }
}
</style>