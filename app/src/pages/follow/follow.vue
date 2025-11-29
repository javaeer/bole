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
        :class="{ active: currentTab === 'hr' }"
        @click="switchTab('hr')"
      >
        <text>关注HR</text>
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

    <!-- 关注HR -->
    <view v-if="currentTab === 'hr'" class="content">
      <view class="hr-list">
        <view class="hr-item" v-for="hr in hrList" :key="hr.id">
          <image :src="hr.avatar" class="hr-avatar" mode="aspectFit" />
          <view class="hr-info">
            <text class="hr-name">{{ hr.name }}</text>
            <text class="hr-position">{{ hr.position }} · {{ hr.company }}</text>
            <text class="hr-tag">{{ hr.tag }}</text>
          </view>
          <button class="btn-chat" @click="handleChatWithHR(hr.id)">发消息</button>
        </view>
      </view>
      
      <view v-if="hrList.length === 0" class="empty-state">
        <text class="empty-icon">👔</text>
        <text class="empty-text">还没有关注任何HR</text>
        <button class="btn-explore" @click="handleExploreHRs">去发现HR</button>
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
    url: `/pages/company/detail?id=${id}`
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

<style scoped>
.page-container {
  background-color: #f8f8f8;
  min-height: 100vh;
}

.tabs {
  display: flex;
  background: white;
  border-bottom: 1rpx solid #f0f0f0;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 30rpx 0;
  font-size: 28rpx;
  color: #666;
  position: relative;
}

.tab-item.active {
  color: #d4af37;
  font-weight: bold;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 80rpx;
  height: 4rpx;
  background: #d4af37;
  border-radius: 2rpx;
}

.content {
  padding: 30rpx;
}

.company-list, .hr-list {
  display: flex;
  flex-direction: column;
  gap: 30rpx;
}

.company-item, .hr-item {
  background: white;
  border-radius: 15rpx;
  padding: 30rpx;
  display: flex;
  align-items: center;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.company-logo, .hr-avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 15rpx;
  margin-right: 25rpx;
}

.hr-avatar {
  border-radius: 50%;
}

.company-info, .hr-info {
  flex: 1;
}

.company-name, .hr-name {
  display: block;
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.company-industry, .hr-position {
  display: block;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 8rpx;
}

.company-desc {
  display: block;
  font-size: 24rpx;
  color: #999;
}

.hr-tag {
  display: inline-block;
  background: #e8f4ff;
  color: #1890ff;
  font-size: 22rpx;
  padding: 4rpx 12rpx;
  border-radius: 20rpx;
}

.company-actions {
  display: flex;
  flex-direction: column;
  gap: 15rpx;
}

.btn-unfollow, .btn-view, .btn-chat {
  padding: 12rpx 24rpx;
  font-size: 24rpx;
  border-radius: 20rpx;
  border: none;
}

.btn-unfollow {
  background: #f8f8f8;
  color: #666;
}

.btn-view, .btn-chat {
  background: #d4af37;
  color: white;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30rpx;
}

.template-item {
  background: white;
  border-radius: 15rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.template-cover {
  width: 100%;
  height: 200rpx;
}

.template-info {
  padding: 20rpx;
}

.template-name {
  display: block;
  font-size: 28rpx;
  font-weight: bold;
  margin-bottom: 8rpx;
}

.template-type {
  display: block;
  font-size: 24rpx;
  color: #666;
}

.template-actions {
  padding: 0 20rpx 20rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.icon-favorite {
  font-size: 32rpx;
  cursor: pointer;
}

.btn-use {
  background: #d4af37;
  color: white;
  border: none;
  padding: 12rpx 24rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 0;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
  margin-bottom: 40rpx;
}

.btn-explore {
  background: #d4af37;
  color: white;
  border: none;
  padding: 20rpx 40rpx;
  border-radius: 25rpx;
  font-size: 28rpx;
}
</style>