<template>
  <view class="page-container">
    <!-- 消息类型选项卡 -->
    <view class="message-tabs">
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 'system' }"
        @click="switchTab('system')"
      >
        <text>系统消息</text>
        <view v-if="unreadSystemCount > 0" class="badge">{{ unreadSystemCount }}</view>
      </view>
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 'hr' }"
        @click="switchTab('hr')"
      >
        <text>HR消息</text>
        <view v-if="unreadHRCount > 0" class="badge">{{ unreadHRCount }}</view>
      </view>
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 'interaction' }"
        @click="switchTab('interaction')"
      >
        <text>互动消息</text>
        <view v-if="unreadInteractionCount > 0" class="badge">{{ unreadInteractionCount }}</view>
      </view>
    </view>

    <!-- 系统消息 -->
    <view v-if="currentTab === 'system'" class="message-list">
      <view class="message-item" v-for="message in systemMessages" :key="message.id">
        <view class="message-avatar">
          <text class="icon-system">📢</text>
        </view>
        <view class="message-content">
          <view class="message-header">
            <text class="message-title">{{ message.title }}</text>
            <text class="message-time">{{ message.time }}</text>
          </view>
          <text class="message-preview">{{ message.content }}</text>
        </view>
        <view v-if="!message.read" class="unread-dot"></view>
      </view>
    </view>

    <!-- HR消息 -->
    <view v-if="currentTab === 'hr'" class="message-list">
      <view 
        class="message-item" 
        v-for="message in hrMessages" 
        :key="message.id"
        @click="handleOpenChat(message.hrId)"
      >
        <image :src="message.hrAvatar" class="message-avatar" mode="aspectFit" />
        <view class="message-content">
          <view class="message-header">
            <text class="message-sender">{{ message.hrName }}</text>
            <text class="message-time">{{ message.time }}</text>
          </view>
          <text class="message-preview">{{ message.preview }}</text>
        </view>
        <view v-if="!message.read" class="unread-dot"></view>
      </view>
    </view>

    <!-- 互动消息 -->
    <view v-if="currentTab === 'interaction'" class="message-list">
      <view class="message-item" v-for="message in interactionMessages" :key="message.id">
        <image :src="message.userAvatar" class="message-avatar" mode="aspectFit" />
        <view class="message-content">
          <view class="message-header">
            <text class="message-sender">{{ message.userName }}</text>
            <text class="message-time">{{ message.time }}</text>
          </view>
          <text class="message-preview">{{ message.content }}</text>
          <view class="message-action" v-if="message.type === 'like'">
            <text>赞了你的简历</text>
          </view>
          <view class="message-action" v-else-if="message.type === 'comment'">
            <text>评论了你的简历</text>
            <button class="btn-reply" @click.stop="handleReply(message.id)">回复</button>
          </view>
        </view>
        <view v-if="!message.read" class="unread-dot"></view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="showEmpty" class="empty-state">
      <text class="empty-icon">💌</text>
      <text class="empty-text">暂无消息</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'

const currentTab = ref('system')

// 消息数据
const systemMessages = ref([
  {
    id: 1,
    title: '系统通知',
    content: '您的简历已被10家企业查看',
    time: '2小时前',
    read: false
  },
  {
    id: 2,
    title: '活动提醒', 
    content: '新用户专享优惠券已发放',
    time: '1天前',
    read: true
  }
])

const hrMessages = ref([
  {
    id: 1,
    hrId: 1,
    hrName: '张经理',
    hrAvatar: '/static/avatar/hr1.jpg',
    preview: '您好，我们对您的简历很感兴趣...',
    time: '30分钟前',
    read: false
  },
  {
    id: 2,
    hrId: 2,
    hrName: '李总监',
    hrAvatar: '/static/avatar/hr2.jpg', 
    preview: '请问您什么时候方便面试？',
    time: '2小时前',
    read: true
  }
])

const interactionMessages = ref([
  {
    id: 1,
    userId: 1,
    userName: '王同学',
    userAvatar: '/static/avatar/user1.jpg',
    type: 'like',
    content: '',
    time: '1小时前',
    read: false
  },
  {
    id: 2,
    userId: 2,
    userName: '李学长',
    userAvatar: '/static/avatar/user2.jpg',
    type: 'comment',
    content: '你的简历写得很好，很有参考价值',
    time: '3小时前', 
    read: true
  }
])

// 计算未读数量
const unreadSystemCount = computed(() => {
  return systemMessages.value.filter(msg => !msg.read).length
})

const unreadHRCount = computed(() => {
  return hrMessages.value.filter(msg => !msg.read).length
})

const unreadInteractionCount = computed(() => {
  return interactionMessages.value.filter(msg => !msg.read).length
})

// 计算是否显示空状态
const showEmpty = computed(() => {
  switch (currentTab.value) {
    case 'system':
      return systemMessages.value.length === 0
    case 'hr':
      return hrMessages.value.length === 0  
    case 'interaction':
      return interactionMessages.value.length === 0
    default:
      return true
  }
})

// 切换选项卡
const switchTab = (tab: string) => {
  currentTab.value = tab
  // 标记当前选项卡的消息为已读
  markMessagesAsRead(tab)
}

// 标记消息为已读
const markMessagesAsRead = (tab: string) => {
  switch (tab) {
    case 'system':
      systemMessages.value.forEach(msg => msg.read = true)
      break
    case 'hr':
      hrMessages.value.forEach(msg => msg.read = true)  
      break
    case 'interaction':
      interactionMessages.value.forEach(msg => msg.read = true)
      break
  }
}

// 打开聊天
const handleOpenChat = (hrId: number) => {
  uni.navigateTo({
    url: `/pages/message/chat?hrId=${hrId}`
  })
}

// 回复评论
const handleReply = (messageId: number) => {
  uni.showToast({
    title: '回复功能开发中',
    icon: 'none'
  })
}

onMounted(() => {
  console.log('消息页面加载完成')
})
</script>

<style scoped>
.page-container {
  background-color: #f8f8f8;
  min-height: 100vh;
}

.message-tabs {
  display: flex;
  background: white;
  border-bottom: 1rpx solid #f0f0f0;
  position: relative;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 30rpx 0;
  font-size: 28rpx;
  color: #666;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
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

.badge {
  background: #ff4757;
  color: white;
  font-size: 20rpx;
  padding: 4rpx 12rpx;
  border-radius: 20rpx;
  min-width: 30rpx;
  text-align: center;
}

.message-list {
  padding: 30rpx;
}

.message-item {
  background: white;
  border-radius: 15rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  display: flex;
  align-items: flex-start;
  position: relative;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.message-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  margin-right: 25rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f8f8;
  flex-shrink: 0;
}

.icon-system {
  font-size: 40rpx;
}

.message-content {
  flex: 1;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15rpx;
}

.message-title, .message-sender {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.message-time {
  font-size: 24rpx;
  color: #999;
}

.message-preview {
  font-size: 26rpx;
  color: #666;
  line-height: 1.4;
}

.message-action {
  margin-top: 15rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.btn-reply {
  background: #d4af37;
  color: white;
  border: none;
  padding: 8rpx 20rpx;
  border-radius: 15rpx;
  font-size: 22rpx;
}

.unread-dot {
  position: absolute;
  top: 35rpx;
  right: 35rpx;
  width: 20rpx;
  height: 20rpx;
  background: #ff4757;
  border-radius: 50%;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 150rpx 0;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}
</style>