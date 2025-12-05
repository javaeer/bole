<template>
  <view class="page-container">
    <!-- 顶部选项卡 -->
    <view class="tabs">
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 'company' }"
        @click="switchTab('company')"
      >
        <text>关注企业</text>
      </view>
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 'template' }"
        @click="switchTab('template')"
      >
        <text>收藏模板</text>
      </view>
    </view>

    <!-- 关注企业 -->
    <view v-if="currentTab === 'company'" class="content">
      <view class="company-list">
        <view class="company-item" v-for="company in companyList" :key="company.id">
          <image :src="company.logo" class="company-logo" mode="aspectFit" />
          <view class="company-info">
            <text class="company-name">{{ company.name }}</text>
            <text class="company-industry">{{ company.industry }}</text>
            <text class="company-desc">{{ company.description }}</text>
          </view>
          <view class="company-actions">
            <button class="btn-unfollow" @click="handleUnfollowCompany(company.id)">取消关注</button>
            <button class="btn-view" @click="handleViewCompany(company.id)">查看详情</button>
          </view>
        </view>
      </view>
      
      <view v-if="companyList.length === 0" class="empty-state">
        <text class="empty-icon">🏢</text>
        <text class="empty-text">还没有关注任何企业</text>
        <button class="btn-explore" @click="handleExploreCompanies">去发现企业</button>
      </view>
    </view>

    <!-- 收藏模板 -->
    <view v-if="currentTab === 'template'" class="content">
      <view class="template-grid">
        <view class="template-item" v-for="template in templateList" :key="template.id">
          <image :src="template.cover" class="template-cover" mode="aspectFill" />
          <view class="template-info">
            <text class="template-name">{{ template.name }}</text>
            <text class="template-type">{{ template.type }}</text>
          </view>
          <view class="template-actions">
            <text class="icon-favorite" @click="handleUnfavoriteTemplate(template.id)">❤️</text>
            <button class="btn-use" @click="handleUseTemplate(template.id)">使用</button>
          </view>
        </view>
      </view>
      
      <view v-if="templateList.length === 0" class="empty-state">
        <text class="empty-icon">📄</text>
        <text class="empty-text">还没有收藏任何模板</text>
        <button class="btn-explore" @click="handleExploreTemplates">去发现模板</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const currentTab = ref('company')

// 关注企业数据
const companyList = ref([
  {
    id: 1,
    name: '阿里巴巴集团',
    industry: '互联网',
    logo: '/static/company/alibaba.jpg',
    description: '全球领先的互联网公司'
  },
  {
    id: 2,
    name: '腾讯科技',
    industry: '互联网',
    logo: '/static/company/tencent.jpg',
    description: '以技术丰富互联网用户的生活'
  }
])

// 关注HR数据
const hrList = ref([
  {
    id: 1,
    name: '张经理',
    position: '招聘经理',
    company: '阿里巴巴',
    avatar: '/static/avatar/hr1.jpg',
    tag: '活跃'
  },
  {
    id: 2,
    name: '李总监',
    position: '人力资源总监', 
    company: '腾讯科技',
    avatar: '/static/avatar/hr2.jpg',
    tag: '在线'
  }
])

// 收藏模板数据
const templateList = ref([
  {
    id: 1,
    name: '简约现代',
    type: '通用模板',
    cover: '/static/template/simple.jpg'
  },
  {
    id: 2,
    name: '专业商务',
    type: '职场模板', 
    cover: '/static/template/professional.jpg'
  }
])

// 切换选项卡
const switchTab = (tab: string) => {
  currentTab.value = tab
}

// 事件处理
const handleUnfollowCompany = (id: number) => {
  uni.showModal({
    title: '确认取消关注',
    content: '确定不再关注该企业吗？',
    success: (res) => {
      if (res.confirm) {
        companyList.value = companyList.value.filter(company => company.id !== id)
        uni.showToast({
          title: '取消关注成功',
          icon: 'success'
        })
      }
    }
  })
}

const handleViewCompany = (id: number) => {
  uni.navigateTo({
    url: `/pages/company/company?id=${id}`
  })
}

const handleChatWithHR = (id: number) => {
  uni.navigateTo({
    url: `/pages/message/chat?hrId=${id}`
  })
}

const handleUnfavoriteTemplate = (id: number) => {
  templateList.value = templateList.value.filter(template => template.id !== id)
  uni.showToast({
    title: '取消收藏成功',
    icon: 'success'
  })
}

const handleUseTemplate = (id: number) => {
  uni.navigateTo({
    url: `/pages/resume/create?templateId=${id}`
  })
}

const handleExploreCompanies = () => {
  uni.navigateTo({
    url: '/pages/company/list'
  })
}

const handleExploreHRs = () => {
  uni.navigateTo({
    url: '/pages/hr/list'
  })
}

const handleExploreTemplates = () => {
  uni.navigateTo({
    url: '/pages/template/list'
  })
}

onMounted(() => {
  console.log('关注页面加载完成')
})
</script>

<style scoped lang="scss">
.page-container {
  background-color: $background-color;
  min-height: 100vh;
}

.tabs {
  display: flex;
  background: $background-color-white;
  border-bottom: 1rpx solid $border-color-light;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: $padding-base 0;
  font-size: $font-size-base;
  color: $text-secondary;
  position: relative;
  transition: color $transition-fast;

  &.active {
    color: $primary-color;
    font-weight: $font-weight-bold;

    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 50%;
      transform: translateX(-50%);
      width: 80rpx;
      height: 4rpx;
      background: $primary-color;
      border-radius: 2rpx;
      animation: slideIn $transition-duration $transition-timing-function;
    }
  }
}

@keyframes slideIn {
  from {
    width: 0;
    opacity: 0;
  }
  to {
    width: 80rpx;
    opacity: 1;
  }
}

.content {
  padding: $padding-base;
}

.company-list, .hr-list {
  display: flex;
  flex-direction: column;
  gap: $margin-base;
}

.company-item, .hr-item {
  background: $background-color-white;
  border-radius: $border-radius;
  padding: $padding-base;
  display: flex;
  align-items: center;
  box-shadow: $box-shadow;
  transition: transform $transition-fast, box-shadow $transition-fast;

  &:active {
    transform: translateY(2rpx);
    box-shadow: $box-shadow-light;
  }
}

.company-logo {
  width: 100rpx;
  height: 100rpx;
  border-radius: $border-radius;
  margin-right: 25rpx;
  background: $background-color;
  object-fit: cover;

  &.placeholder {
    @extend .flex-center;
    background: linear-gradient(135deg, $primary-light 0%, transparent 100%);
    font-size: $font-size-large;
  }
}

.hr-avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: $border-radius-round;
  margin-right: 25rpx;
  background: $background-color;
  object-fit: cover;

  &.placeholder {
    @extend .flex-center;
    background: linear-gradient(135deg, $primary-light 0%, transparent 100%);
    font-size: $font-size-large;
  }
}

.company-info, .hr-info {
  flex: 1;
  overflow: hidden;
}

.company-name, .hr-name {
  display: block;
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  margin-bottom: calc($margin-mini / 2);
  color: $text-primary;
  @extend .text-ellipsis;
}

.company-industry, .hr-position {
  display: block;
  font-size: $font-size-small;
  color: $text-regular;
  margin-bottom: calc($margin-mini / 2);
  @extend .text-ellipsis;
}

.company-desc {
  display: block;
  font-size: $font-size-extra-small;
  color: $text-secondary;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.hr-tag {
  display: inline-block;
  background: rgba($primary-color, 0.1);
  color: $primary-color;
  font-size: $font-size-extra-small;
  padding: 4rpx $padding-small;
  border-radius: 20rpx;
  font-weight: $font-weight-medium;
}

.company-actions {
  display: flex;
  flex-direction: column;
  gap: $margin-small;
  min-width: 120rpx;
}

.btn-unfollow, .btn-view, .btn-chat {
  padding: $padding-mini $padding-small;
  font-size: $font-size-small;
  border-radius: $border-radius-round;
  border: none;
  transition: all $transition-fast;
  cursor: pointer;
  font-weight: $font-weight-medium;

  &:active {
    transform: scale(0.98);
  }
}

.btn-unfollow {
  background: $background-color;
  color: $text-regular;

  &:active {
    background: color.adjust($background-color, $lightness:  - 5%);
  }
}

.btn-view, .btn-chat {
  background: $primary-color;
  color: $background-color-white;

  &:active {
    background: color.adjust($primary-color, $lightness:  -10%);
  }
}

/* 模板样式 */
.template-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $margin-base;
}

.template-item {
  background: $background-color-white;
  border-radius: $border-radius-small;
  overflow: hidden;
  box-shadow: $box-shadow;
  transition: transform $transition-fast, box-shadow $transition-fast;

  &:active {
    transform: translateY(2rpx);
    box-shadow: $box-shadow-light;
  }
}

.template-cover {
  width: 100%;
  height: 200rpx;
  background: $background-color;
  object-fit: cover;

  &.placeholder {
    @extend .flex-center;
    background: linear-gradient(135deg, $primary-light 0%, transparent 100%);
    font-size: $font-size-extra-large;
  }
}

.template-info {
  padding: $padding-small;
}

.template-name {
  display: block;
  font-size: $font-size-base;
  font-weight: $font-weight-bold;
  margin-bottom: calc($margin-mini / 2);
  color: $text-primary;
  @extend .text-ellipsis;
}

.template-type {
  display: block;
  font-size: $font-size-small;
  color: $text-regular;
  @extend .text-ellipsis;
}

.template-actions {
  padding: 0 $padding-small $padding-small;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.icon-favorite {
  font-size: $font-size-medium;
  color: $text-placeholder;
  cursor: pointer;
  transition: color $transition-fast;

  &.active {
    color: $danger-color;
  }

  &:active {
    transform: scale(0.9);
  }
}

.btn-use {
  background: $primary-color;
  color: $background-color-white;
  border: none;
  padding: $padding-mini $padding-small;
  border-radius: $border-radius-round;
  font-size: $font-size-small;
  font-weight: $font-weight-medium;
  transition: background-color $transition-fast;

  &:active {
    background: color.adjust($primary-color, $lightness:  -10%);
  }
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 0;
}

.empty-icon {
  font-size: 120rpx;
  color: $text-placeholder;
  margin-bottom: $margin-base;
  opacity: 0.6;
}

.empty-text {
  font-size: $font-size-base;
  color: $text-secondary;
  margin-bottom: $margin-base * 2;
  text-align: center;
}

.btn-explore {
  background: $primary-color;
  color: $background-color-white;
  border: none;
  padding: $padding-small $padding-base;
  border-radius: $border-radius-round;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  transition: all $transition-fast;

  &:active {
    transform: scale(0.98);
    background: color.adjust($primary-color, $lightness:  -10%);
  }
}

/* 响应式调整 */
@media (max-width: 375px) {
  .content {
    padding: $padding-small;
  }

  .template-grid {
    gap: $margin-small;
  }

  .company-item, .hr-item {
    padding: $padding-small;
  }
}
</style>