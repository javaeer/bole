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
        :class="{ active: currentTab === 'interaction' }"
        @click="switchTab('interaction')"
      >
        <text>互动消息</text>
        <view v-if="unreadInteractionCount > 0" class="badge">{{ unreadInteractionCount }}</view>
      </view>
    </view>

    <!-- 系统消息 -->
    <view v-if="currentTab === 'system'" class="message-list">
      <view class="message-item"
            v-for="message in systemMessages"
            :key="message.id"
            @click="handleBadgeClick">
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
          <view class="message-action"
                v-if="message.type === 'like'"
          @click="handleReply(message.id)">
            <text>赞了你的简历</text>
          </view>
          <view class="message-action"
                v-else-if="message.type === 'comment'"
                @click="handleReply(message.id)">
            <text>评论了你的简历</text>
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


const unreadInteractionCount = computed(() => {
  return interactionMessages.value.filter(msg => !msg.read).length
})

// 计算是否显示空状态
const showEmpty = computed(() => {
  switch (currentTab.value) {
    case 'system':
      return systemMessages.value.length === 0
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
    case 'interaction':
      interactionMessages.value.forEach(msg => msg.read = true)
      break
  }
}

const handleBadgeClick = () => {
  // 跳转到通知详情页
  uni.navigateTo({
    url: `/subpackages/pages/notify/notify`
  })
}

// 回复评论
const handleReply = (messageId: number) => {
  uni.navigateTo({
    url: `/subpackages/pages/chat/chat?messageId=${messageId}`
  })
}

onMounted(() => {
  console.log('消息页面加载完成')
})
</script>

<style lang="scss" scoped>

.message-tabs {
  display: flex;
  background: $background-color-white;
  border-bottom: 1rpx solid $border-color-lighter;
  position: sticky;
  top: 0;
  z-index: $z-index-base;
  box-shadow: $box-shadow-light;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: ($padding-base - 10rpx) 0;
  font-size: $tab-item-font-size;
  color: $tab-inactive-color;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  transition: color $transition-fast;
  cursor: pointer;
  height: $tab-item-height;

  &.active {
    color: $tab-active-color;
    font-weight: $font-weight-semibold;

    .badge {
      background: $tab-active-color;
    }
  }
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: $tab-indicator-width;
  height: $tab-indicator-height;
  background: $tab-active-color;
  border-radius: calc($tab-indicator-height / 2);
}

.badge {
  background: $badge-bg-color;
  color: $badge-text-color;
  font-size: $badge-font-size;
  padding: $badge-padding;
  border-radius: $badge-border-radius;
  min-width: $badge-min-width;
  text-align: center;
  line-height: 1;
  font-weight: $font-weight-medium;
}

.message-list {
  padding: $padding-base;
}

.message-item {
  background: $message-bg-color;
  border-radius: $message-border-radius;
  padding: $message-padding;
  margin-bottom: $message-margin-bottom;
  display: flex;
  align-items: flex-start;
  position: relative;
  box-shadow: $message-item-shadow;
  transition: transform $transition-fast;

  &.unread {
    border-color: rgba($primary-color, 0.1);

    .unread-dot {
      display: block;
    }
  }

  &:hover {
    transform: translateY(-2rpx);
  }
}

.message-avatar {
  width: $avatar-size;
  height: $avatar-size;
  border-radius: $avatar-border-radius;
  margin-right: $margin-small;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $avatar-bg-color;
  flex-shrink: 0;
  overflow: hidden;

  &.system-avatar {
    background: $message-system-color;
    color: $background-color-white;
  }

  &.notice-avatar {
    background: $message-notice-color;
    color: $background-color-white;
  }

  &.interact-avatar {
    background: $message-interact-color;
    color: $background-color-white;
  }
}

.message-avatar .icon-system {
  font-size: $font-size-large;
  color: $message-system-color;
}

.empty-icon {
  font-size: $empty-icon-size;
  color: $empty-text-color;
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: $margin-mini;
  gap: $margin-mini;
}

.message-title {
  font-size: $font-size-medium;
  font-weight: $font-weight-semibold;
  color: $text-primary;
  margin-bottom: 2rpx;
  @extend .text-truncate;
}

.message-sender {
  font-size: $font-size-small;
  color: $text-secondary;
  @extend .text-truncate;
}

.message-time {
  font-size: $font-size-extra-small;
  color: $text-placeholder;
  white-space: nowrap;
  flex-shrink: 0;
  margin-top: 2rpx;
}

.message-preview {
  font-size: $font-size-base;
  color: $text-regular;
  line-height: 1.5;
  margin-bottom: $margin-mini;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.message-action {
  margin-top: $margin-mini;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.btn-reply {
  @extend .btn;
  @extend .btn-small;
  background: $primary-color;
  color: $background-color-white;
  border: none;
  padding: 0 $padding-mini;
  height: $button-small-height;
  border-radius: $button-small-border-radius;
  font-size: $button-small-font-size;
  font-weight: $font-weight-medium;
  transition: background-color $transition-fast;
  cursor: pointer;

  &:hover {
    background: color.adjust($primary-color, $lightness: -10%);
  }

  &:disabled {
    background: $text-placeholder;
    cursor: not-allowed;
  }
}

.unread-dot {
  position: absolute;
  top: $padding-mini;
  right: $padding-mini;
  width: $unread-dot-size;
  height: $unread-dot-size;
  background: $unread-dot-color;
  border-radius: 50%;
  display: none;
}

.empty-state {
  display: none;
  flex-direction: column;
  align-items: center;
  padding: 150rpx 0;
  text-align: center;
}

.empty-icon {
  font-size: $empty-icon-size;
  margin-bottom: $margin-base;
  opacity: 0.3;
  color: $empty-text-color;
}

.empty-text {
  font-size: $empty-text-font-size;
  color: $empty-text-color;
  line-height: 1.5;
}

.loading-more {
  text-align: center;
  padding: $padding-base 0;
  color: $text-placeholder;
  font-size: $font-size-small;
}

@media (max-width: $breakpoint-sm) {
  .message-list {
    padding: $padding-small;
  }

  .message-item {
    padding: $padding-small;
  }

  .message-avatar {
    width: 70rpx;
    height: 70rpx;
    margin-right: 20rpx;
  }

  .icon-system {
    font-size: $font-size-medium;
  }

  .message-title {
    font-size: $font-size-base;
  }

  .message-preview {
    font-size: $font-size-small;
  }
}

.tab-item.active {
  color: $tab-active-color;
}

.badge {
  background: $badge-bg-color;
  color: $badge-text-color;
}

</style>