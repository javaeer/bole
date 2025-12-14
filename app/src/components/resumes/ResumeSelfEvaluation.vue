<!-- /components/resume/ResumeSelfEvaluation.vue -->
<template>
  <view :class="['self-evaluation', `theme-${theme}`]" :style="computedStyle">
    <!-- 区块标题 -->
    <view class="section-header">
      <text class="section-title">自我评价</text>
      <view class="section-divider"></view>
    </view>

    <!-- 空状态 -->
    <view v-if="!hasContent" class="empty-state">
      <text class="empty-icon">💭</text>
      <text class="empty-text">暂无自我评价</text>
      <text class="empty-hint">请添加您的个人优势、工作态度或职业目标</text>
    </view>

    <!-- 内容区域 -->
    <view v-else class="evaluation-content">
      <!-- 主要评价内容 -->
      <view class="main-content" :style="contentStyle">
        <text class="content-text">{{ displayContent }}</text>

        <!-- 显示更多/收起按钮 -->
        <view
          v-if="shouldShowToggle && !forceShowAll"
          class="show-more-btn"
          @click="toggleContent"
        >
          <text class="btn-text">显示全部</text>
          <text class="btn-icon">↓</text>
        </view>

        <view
          v-if="shouldShowToggle && forceShowAll"
          class="show-less-btn"
          @click="toggleContent"
        >
          <text class="btn-text">收起</text>
          <text class="btn-icon">↑</text>
        </view>
      </view>

      <!-- 关键词标签 -->
      <view v-if="showKeywords && keywords.length > 0" class="keywords-section">
        <text class="keywords-title">核心特质</text>
        <view class="keywords-container">
          <text
            v-for="(keyword, index) in keywords"
            :key="index"
            :class="['keyword-tag', `keyword-${index % 4}`]"
            @click="onKeywordClick(keyword)"
          >
            <text v-if="showKeywordIcons" class="keyword-icon">{{ getKeywordIcon(keyword) }}</text>
            <text class="keyword-text">{{ keyword }}</text>
          </text>
        </view>
      </view>

      <!-- 优势列表 -->
      <view v-if="showStrengths && strengths.length > 0" class="strengths-section">
        <text class="strengths-title">个人优势</text>
        <view class="strengths-list">
          <view
            v-for="(strength, index) in strengths"
            :key="index"
            class="strength-item"
          >
            <view class="strength-header">
              <text class="strength-icon">⭐</text>
              <text class="strength-title">{{ strength.title }}</text>
            </view>
            <text class="strength-desc">{{ strength.description }}</text>
          </view>
        </view>
      </view>

      <!-- 兴趣爱好 -->
      <view v-if="showHobbies && hobbies.length > 0" class="hobbies-section">
        <text class="hobbies-title">兴趣爱好</text>
        <view class="hobbies-tags">
          <text
            v-for="(hobby, index) in hobbies"
            :key="index"
            class="hobby-tag"
          >
            {{ hobby }}
          </text>
        </view>
      </view>

      <!-- 证书资质 -->
      <view v-if="showCertificates && certificates.length > 0" class="certificates-section">
        <text class="certificates-title">证书资质</text>
        <view class="certificates-list">
          <view
            v-for="(cert, index) in certificates"
            :key="index"
            class="certificate-item"
          >
            <text class="cert-icon">🏅</text>
            <text class="cert-text">{{ cert }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'ResumeSelfEvaluation',

  props: {
    config: {
      type: Object,
      default: () => ({})
    },
    theme: {
      type: String,
      default: 'modern'
    }
  },

  data() {
    return {
      forceShowAll: false,
      maxContentLength: 200, // 默认最多显示200字符
      keywordIcons: ['🚀', '💡', '🤝', '🎯', '📚', '✨', '🌟', '💪']
    };
  },

  computed: {
    // 提取props和styles
    props() {
      return this.config.props || {};
    },

    styles() {
      return this.config.styles || {};
    },

    // 主要内容
    content() {
      return this.props.content || '';
    },

    // 关键词列表
    keywords() {
      return this.props.keywords || [];
    },

    // 优势列表
    strengths() {
      return this.props.strengths || [];
    },

    // 兴趣爱好
    hobbies() {
      return this.props.hobbies || [];
    },

    // 证书列表
    certificates() {
      return this.props.certificates || [];
    },

    // 检查是否有内容
    hasContent() {
      return this.content ||
        this.keywords.length > 0 ||
        this.strengths.length > 0 ||
        this.hobbies.length > 0 ||
        this.certificates.length > 0;
    },

    // 是否显示关键词
    showKeywords() {
      return this.styles.showKeywords !== false;
    },

    // 是否显示优势
    showStrengths() {
      return this.styles.showStrengths !== false;
    },

    // 是否显示兴趣爱好
    showHobbies() {
      return this.styles.showHobbies !== false;
    },

    // 是否显示证书
    showCertificates() {
      return this.styles.showCertificates !== false;
    },

    // 是否显示关键词图标
    showKeywordIcons() {
      return this.styles.showKeywordIcons !== false;
    },

    // 计算显示的文本内容
    displayContent() {
      if (!this.content) return '';

      if (this.forceShowAll || this.content.length <= this.maxContentLength) {
        return this.content;
      }

      // 截断文本，确保在完整句子处截断
      const truncated = this.content.substring(0, this.maxContentLength);
      const lastPeriod = truncated.lastIndexOf('。');
      const lastComma = truncated.lastIndexOf('，');
      const cutoff = Math.max(lastPeriod, lastComma, this.maxContentLength - 20);

      return truncated.substring(0, cutoff) + '...';
    },

    // 是否应该显示展开/收起按钮
    shouldShowToggle() {
      return this.content && this.content.length > this.maxContentLength;
    },

    // 样式计算
    computedStyle() {
      return {
        '--primary-color': this.styles.primaryColor || '#d4af37'
      };
    },

    contentStyle() {
      const align = this.styles.textAlign || 'left';
      return {
        'text-align': align
      };
    }
  },

  mounted() {
    console.log('自我评价组件加载完成', {
      内容长度: this.content?.length || 0,
      关键词数量: this.keywords.length,
      配置: this.config
    });
  },

  methods: {
    // 切换内容展开/收起
    toggleContent() {
      this.forceShowAll = !this.forceShowAll;
    },

    // 关键词点击事件
    onKeywordClick(keyword) {
      console.log('关键词被点击:', keyword);
      this.$emit('keyword-click', keyword);

      // 可以添加一些交互效果，比如放大动画
      uni.showToast({
        title: `关键词: ${keyword}`,
        icon: 'none',
        duration: 1000
      });
    },

    // 获取关键词图标
    getKeywordIcon(keyword) {
      // 简单的图标映射逻辑
      const iconMap = {
        '技术': '💻',
        '团队': '👥',
        '学习': '📖',
        '创新': '💡',
        '沟通': '💬',
        '领导': '👑',
        '效率': '⚡',
        '质量': '⭐'
      };

      // 尝试匹配关键词
      for (const [key, icon] of Object.entries(iconMap)) {
        if (keyword.includes(key)) {
          return icon;
        }
      }

      // 默认返回循环图标
      const index = this.keywords.indexOf(keyword) % this.keywordIcons.length;
      return this.keywordIcons[index];
    }
  }
};
</script>

<style lang="scss" scoped>
.self-evaluation {
  margin-bottom: 40rpx;

  &.theme-modern {
    .section-title {
      color: #d4af37;
      font-size: 36rpx;
      font-weight: 600;
      margin-bottom: 16rpx;
      display: block;
    }

    .section-divider {
      height: 2rpx;
      background: linear-gradient(90deg, #d4af37, #f7ef8a);
      margin-bottom: 30rpx;
      width: 80rpx;
    }
  }

  // 空状态
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60rpx 30rpx;
    background: #fafafa;
    border-radius: 16rpx;
    border: 1rpx dashed #e0e0e0;
    text-align: center;

    .empty-icon {
      font-size: 60rpx;
      margin-bottom: 20rpx;
    }

    .empty-text {
      color: #333;
      font-size: 30rpx;
      font-weight: 500;
      margin-bottom: 12rpx;
    }

    .empty-hint {
      color: #999;
      font-size: 24rpx;
      line-height: 1.4;
    }
  }

  // 主要内容区域
  .evaluation-content {
    background: #ffffff;
    border-radius: 16rpx;
    padding: 30rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
    border: 1rpx solid #f0f0f0;
  }

  // 主要评价内容
  .main-content {
    margin-bottom: 30rpx;

    .content-text {
      color: #444;
      font-size: 28rpx;
      line-height: 1.8;
      display: block;
      margin-bottom: 20rpx;
    }

    // 显示更多/收起按钮
    .show-more-btn, .show-less-btn {
      display: inline-flex;
      align-items: center;
      background: #f5f7fa;
      color: #d4af37;
      font-size: 24rpx;
      padding: 8rpx 16rpx;
      border-radius: 20rpx;
      border: 1rpx solid #e4e7ed;
      margin-top: 10rpx;

      .btn-text {
        margin-right: 8rpx;
      }

      .btn-icon {
        font-size: 20rpx;
      }

      &:active {
        background: #e8eaf1;
      }
    }
  }

  // 关键词区域
  .keywords-section {
    margin-bottom: 30rpx;
    padding-bottom: 30rpx;
    border-bottom: 1rpx solid #f0f0f0;

    .keywords-title {
      color: #666;
      font-size: 28rpx;
      font-weight: 500;
      display: block;
      margin-bottom: 20rpx;
    }

    .keywords-container {
      display: flex;
      flex-wrap: wrap;
      gap: 16rpx;

      .keyword-tag {
        display: inline-flex;
        align-items: center;
        padding: 10rpx 20rpx;
        border-radius: 30rpx;
        font-size: 24rpx;
        border: 1rpx solid transparent;
        transition: all 0.2s;

        &:active {
          transform: scale(0.98);
        }

        .keyword-icon {
          margin-right: 8rpx;
          font-size: 22rpx;
        }

        // 不同颜色的标签
        &.keyword-0 {
          background: rgba(212, 175, 55, 0.1);
          color: #d4af37;
          border-color: rgba(212, 175, 55, 0.2);
        }

        &.keyword-1 {
          background: rgba(103, 194, 58, 0.1);
          color: #67c23a;
          border-color: rgba(103, 194, 58, 0.2);
        }

        &.keyword-2 {
          background: rgba(64, 158, 255, 0.1);
          color: #409eff;
          border-color: rgba(64, 158, 255, 0.2);
        }

        &.keyword-3 {
          background: rgba(230, 162, 60, 0.1);
          color: #e6a23c;
          border-color: rgba(230, 162, 60, 0.2);
        }
      }
    }
  }

  // 优势列表
  .strengths-section {
    margin-bottom: 30rpx;
    padding-bottom: 30rpx;
    border-bottom: 1rpx solid #f0f0f0;

    .strengths-title {
      color: #666;
      font-size: 28rpx;
      font-weight: 500;
      display: block;
      margin-bottom: 20rpx;
    }

    .strengths-list {
      .strength-item {
        margin-bottom: 24rpx;

        &:last-child {
          margin-bottom: 0;
        }

        .strength-header {
          display: flex;
          align-items: center;
          margin-bottom: 10rpx;

          .strength-icon {
            font-size: 24rpx;
            margin-right: 12rpx;
            color: #d4af37;
          }

          .strength-title {
            font-size: 26rpx;
            font-weight: 500;
            color: #444;
          }
        }

        .strength-desc {
          color: #666;
          font-size: 24rpx;
          line-height: 1.6;
          padding-left: 36rpx;
        }
      }
    }
  }

  // 兴趣爱好
  .hobbies-section {
    margin-bottom: 30rpx;

    .hobbies-title {
      color: #666;
      font-size: 28rpx;
      font-weight: 500;
      display: block;
      margin-bottom: 20rpx;
    }

    .hobbies-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 16rpx;

      .hobby-tag {
        background: #f5f7fa;
        color: #555;
        font-size: 24rpx;
        padding: 8rpx 16rpx;
        border-radius: 20rpx;
        border: 1rpx solid #e4e7ed;
      }
    }
  }

  // 证书资质
  .certificates-section {
    .certificates-title {
      color: #666;
      font-size: 28rpx;
      font-weight: 500;
      display: block;
      margin-bottom: 20rpx;
    }

    .certificates-list {
      .certificate-item {
        display: flex;
        align-items: center;
        margin-bottom: 16rpx;

        &:last-child {
          margin-bottom: 0;
        }

        .cert-icon {
          font-size: 24rpx;
          margin-right: 12rpx;
          color: #d4af37;
        }

        .cert-text {
          color: #555;
          font-size: 24rpx;
        }
      }
    }
  }
}
</style>