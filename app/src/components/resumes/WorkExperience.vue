<template>
  <view :class="['work-experience', `layout-${styles.layout}`, `theme-${theme}`]">
    <view class="section-header">
      <text class="section-title">工作经历</text>
      <view class="section-divider"></view>
    </view>

    <view class="experiences">
      <block v-for="(exp, index) in props.experiences" :key="index">
        <view class="experience-item">
          <!-- 时间线布局 -->
          <view v-if="styles.layout === 'timeline'" class="timeline-layout">
            <view class="timeline-dot"></view>
            <view class="timeline-line"></view>

            <view class="experience-content">
              <view class="experience-header">
                <view class="company-info">
                  <text class="company">{{ exp.company }}</text>
                  <text class="duration">
                    {{ exp.startDate }} - {{ exp.endDate }}
                  </text>
                </view>
                <text class="position">{{ exp.position }}</text>
              </view>

              <view class="experience-desc" v-if="exp.description">
                <text class="desc-text">{{ exp.description }}</text>
              </view>

              <!-- 项目经历 -->
              <view v-if="styles.showProjects && exp.projects" class="projects">
                <text class="projects-title">项目经历</text>
                <view
                  v-for="(project, pIndex) in exp.projects"
                  :key="pIndex"
                  class="project-item"
                >
                  <view class="project-header">
                    <text class="project-name">{{ project.name }}</text>
                    <text class="project-role">{{ project.role }}</text>
                  </view>

                  <view class="project-desc" v-if="project.description">
                    <text class="desc-text">{{ project.description }}</text>
                  </view>

                  <view v-if="project.achievements" class="achievements">
                    <text class="achievement-title">项目成果：</text>
                    <view
                      v-for="(achievement, aIndex) in project.achievements"
                      :key="aIndex"
                      class="achievement-item"
                    >
                      <text class="achievement-bullet">•</text>
                      <text class="achievement-text">{{ achievement }}</text>
                    </view>
                  </view>
                </view>
              </view>

              <!-- 技能标签 -->
              <view v-if="exp.skills" class="skill-tags">
                <text
                  v-for="(skill, sIndex) in exp.skills"
                  :key="sIndex"
                  class="skill-tag"
                >
                  {{ skill }}
                </text>
              </view>
            </view>
          </view>

          <!-- 卡片布局 -->
          <view v-else class="card-layout">
            <!-- 类似的时间线内容，用卡片样式展示 -->
          </view>
        </view>
      </block>
    </view>
  </view>
</template>

<script>
export default {
  name: 'WorkExperience',
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
  computed: {
    props() {
      return this.config.props || {};
    },
    styles() {
      return this.config.styles || {};
    }
  }
};
</script>

<style lang="scss" scoped>
.work-experience {
  &.theme-modern {
    .section-title {
      color: $primary-color;
      font-size: $font-size-large;
      font-weight: $font-weight-bold;
      margin-bottom: $margin-mini;
      display: block;
    }

    .company {
      color: $text-primary;
      font-size: $font-size-large;
      font-weight: $font-weight-bold;
    }

    .position {
      color: $text-regular;
      font-size: $font-size-medium;
      font-weight: $font-weight-medium;
      margin-top: $margin-mini;
      display: block;
    }
  }

  .section-divider {
    height: 2rpx;
    background: linear-gradient(90deg, $primary-color, $secondary-color);
    margin-bottom: $margin-base;
  }

  .timeline-layout {
    position: relative;
    padding-left: 40rpx;
    margin-bottom: $margin-large;

    &:last-child {
      margin-bottom: 0;
    }

    .timeline-dot {
      position: absolute;
      left: 0;
      top: 10rpx;
      width: 16rpx;
      height: 16rpx;
      background-color: $primary-color;
      border-radius: 50%;
      border: 3rpx solid white;
      box-shadow: 0 0 0 2rpx $primary-color;
    }

    .timeline-line {
      position: absolute;
      left: 7rpx;
      top: 26rpx;
      bottom: -30rpx;
      width: 2rpx;
      background-color: $border-color-light;

      &:last-child {
        display: none;
      }
    }

    .experience-content {
      background: $background-color-white;
      border-radius: $border-radius;
      padding: $padding-base;
      box-shadow: $box-shadow-light;
    }

    .company-info {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: $margin-mini;

      .duration {
        color: $text-secondary;
        font-size: $font-size-small;
      }
    }

    .experience-desc {
      margin-top: $margin-small;
      margin-bottom: $margin-small;

      .desc-text {
        color: $text-regular;
        font-size: $font-size-base;
        line-height: 1.6;
      }
    }

    .projects {
      margin-top: $margin-base;

      .projects-title {
        display: block;
        color: $primary-color;
        font-size: $font-size-medium;
        font-weight: $font-weight-bold;
        margin-bottom: $margin-small;
        padding-bottom: 8rpx;
        border-bottom: 1rpx solid $border-color-light;
      }

      .project-item {
        margin-bottom: $margin-base;
        padding: $padding-small;
        background-color: $background-color;
        border-radius: $border-radius-small;

        &:last-child {
          margin-bottom: 0;
        }

        .project-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: $margin-mini;

          .project-name {
            color: $text-primary;
            font-size: $font-size-base;
            font-weight: $font-weight-bold;
            flex: 1;
          }

          .project-role {
            color: $text-secondary;
            font-size: $font-size-small;
            margin-left: $margin-mini;
          }
        }

        .project-desc {
          margin-bottom: $margin-mini;

          .desc-text {
            color: $text-regular;
            font-size: $font-size-small;
            line-height: 1.5;
          }
        }

        .achievements {
          .achievement-title {
            color: $text-secondary;
            font-size: $font-size-small;
            font-weight: $font-weight-medium;
            display: block;
            margin-bottom: $margin-mini;
          }

          .achievement-item {
            display: flex;
            align-items: flex-start;
            margin-bottom: 6rpx;

            &:last-child {
              margin-bottom: 0;
            }

            .achievement-bullet {
              color: $primary-color;
              margin-right: 8rpx;
              flex-shrink: 0;
            }

            .achievement-text {
              color: $text-regular;
              font-size: $font-size-small;
              line-height: 1.5;
              flex: 1;
            }
          }
        }
      }
    }

    .skill-tags {
      display: flex;
      flex-wrap: wrap;
      gap: $margin-mini;
      margin-top: $margin-small;

      .skill-tag {
        padding: 6rpx 12rpx;
        background-color: $primary-lighter;
        border-radius: $border-radius-small;
        color: $text-regular;
        font-size: $font-size-extra-small;
        border: 1rpx solid rgba($primary-color, 0.1);
      }
    }
  }
}
</style>