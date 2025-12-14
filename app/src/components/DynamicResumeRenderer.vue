<template>
  <view class="resume-container" :style="globalStyle">
    <!-- 渲染所有简历板块 -->
    <block v-for="(section, index) in sections" :key="section.id || index">
      <!-- 动态组件渲染器 -->
      <component
        :is="section.component"
        :config="section"
        :theme="currentTheme"
        class="resume-section"
      />
    </block>
  </view>
</template>

<script>
// 导入所有简历组件
import ResumeBasicInfo from '@/components/resumes/ResumeBasicInfo.vue';
import ResumeJobIntention from '@/components/resumes/ResumeJobIntention.vue';
import ResumeWorkExperience from '@/components/resumes/ResumeWorkExperience.vue';
import ResumeEducation from '@/components/resumes/ResumeEducation.vue';
import ResumeSelfEvaluation from '@/components/resumes/ResumeSelfEvaluation.vue';

// 组件映射表
const componentMap = {
  'ResumeBasicInfo': ResumeBasicInfo,
  'ResumeJobIntention': ResumeJobIntention,
  'ResumeWorkExperience': ResumeWorkExperience,
  'ResumeEducation': ResumeEducation,
  'ResumeSelfEvaluation': ResumeSelfEvaluation,
};

export default {
  name: 'DynamicResumeRenderer',
  //注册组件
  components: {
    ResumeBasicInfo,
    ResumeJobIntention,
    ResumeWorkExperience,
    ResumeEducation,
    ResumeSelfEvaluation,
  },
  props: {
    config: {
      type: Object,
      required: true,
      default: () => ({})
    }
  },
  computed: {
    sections() {
      return this.config.sections || [];
    },
    globalStyle() {
      const style = this.config.globalStyle || {};
      return {
        '--primary-color': style.primaryColor || '#d4af37',
        '--secondary-color': style.secondaryColor || '#f7ef8a',
        'font-family': style.fontFamily || "'PingFang SC', 'Helvetica Neue', Arial, sans-serif"
      };
    },
    currentTheme() {
      return (this.config.globalStyle || {}).theme || 'modern';
    }
  }
};
</script>

<style lang="scss" scoped>
.resume-container {
  min-height: 100vh;
  background-color: $background-color;
  padding: $padding-base;

  .resume-section {
    margin-bottom: $margin-base;
  }
}

// 响应式设计
@media (min-width: $screen-md) {
  .resume-container {
    max-width: 750px;
    margin: 0 auto;
    padding: $padding-base * 1.5;
  }
}

@media (min-width: $screen-lg) {
  .resume-container {
    max-width: 900px;
    padding: $padding-large;
  }
}
</style>