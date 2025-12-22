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
    url: `/pages/notify/notify`
  })
}

// 回复评论
const handleReply = (messageId: number) => {
  uni.navigateTo({
    url: `/pages/chat/chat?messageId=${messageId}`
  })
}

onMounted(() => {
  console.log('消息页面加载完成')
})
</script>

<style lang="scss" scoped>

/* 消息标签页 */
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
  transition: all $transition-fast;
  cursor: pointer;
  height: $tab-item-height;

  &.active {
    color: $tab-active-color;
    font-weight: $font-weight-semibold;

    .badge {
      background: $tab-active-color;
    }
  }

  &:not(.active):hover {
    color: color.adjust($tab-inactive-color, $lightness:  20%);
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
  animation: slideIn $transition-normal;
}

@keyframes slideIn {
  from {
    width: 0;
    opacity: 0;
  }
  to {
    width: $tab-indicator-width;
    opacity: 1;
  }
}

/* 徽章 */
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
  box-shadow: 0 2rpx 8rpx rgba($danger-color, 0.3);
}

/* 消息列表 */
.message-list {
  padding: $padding-base;

  &:empty {
    .empty-state {
      display: flex;
    }
  }
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
  transition: all $transition-fast;
  border: 1rpx solid transparent;

  &.unread {
    @extend .status-unread;
    border-color: rgba($primary-color, 0.1);

    .unread-dot {
      display: block;
    }
  }

  &:hover {
    box-shadow: $message-item-hover-shadow;
    transform: translateY(-2rpx);
  }

  &:active {
    transform: translateY(0);
  }
}

/* 消息头像 */
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
    background: linear-gradient(135deg, $message-system-color, color.adjust($message-system-color, $lightness:  20%));
    color: $background-color-white;
  }

  &.notice-avatar {
    background: linear-gradient(135deg, $message-notice-color, color.adjust($message-notice-color, $lightness:  20%));
    color: $background-color-white;
  }

  &.interact-avatar {
    background: linear-gradient(135deg, $message-interact-color, color.adjust($message-interact-color, $lightness:  20%));
    color: $background-color-white;
  }
}

.icon-system {
  font-size: $font-size-large;
}

/* 消息内容区域 */
.message-content {
  flex: 1;
  min-width: 0; /* 防止flex溢出 */
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: $margin-mini;
  gap: $margin-mini;

  .message-title-wrapper {
    flex: 1;
    min-width: 0;
  }
}

.message-title {
  font-size: $font-size-medium;
  font-weight: $font-weight-semibold;
  color: $text-primary;
  margin-bottom: 2rpx;
  @extend .text-ellipsis;
}

.message-sender {
  font-size: $font-size-small;
  color: $text-secondary;
  @extend .text-ellipsis;
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

/* 消息操作 */
.message-action {
  margin-top: $margin-mini;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.btn-reply {
  background: $primary-color;
  color: $background-color-white;
  border: none;
  padding: 0 $padding-mini;
  height: $button-small-height;
  border-radius: $button-small-border-radius;
  font-size: $button-small-font-size;
  font-weight: $font-weight-medium;
  transition: all $transition-fast;
  cursor: pointer;

  &:hover {
    background: color.adjust($primary-color, $lightness:  -10%);
    transform: scale(1.05);
  }

  &:active {
    transform: scale(0.95);
  }

  &:disabled {
    background: $text-placeholder;
    cursor: not-allowed;
    transform: none;
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
  animation: pulse 2s infinite;
  box-shadow: 0 0 0 rgba($danger-color, 0.4);
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba($danger-color, 0.4);
  }
  70% {
    box-shadow: 0 0 0 10rpx rgba($danger-color, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba($danger-color, 0);
  }
}

/* 空状态 */
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

  .action-text {
    color: $primary-color;
    font-weight: $font-weight-medium;
    margin-top: $margin-mini;
    display: inline-block;
    cursor: pointer;

    &:hover {
      text-decoration: underline;
    }
  }
}

/* 加载状态 */
.loading-more {
  text-align: center;
  padding: $padding-base 0;
  color: $text-placeholder;
  font-size: $font-size-small;
}

/* 响应式调整 */
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

/* 暗色模式支持 */
/*@media (prefers-color-scheme: dark) {
  .page-container {
    background-color: color.adjust($background-color, $lightness:  - 80%);
  }

  .message-tabs {
    background: color.adjust($background-color-white, $lightness:  -80%);
    border-bottom-color: color.adjust($primary-lighter, $lightness:  -80%);
  }

  .message-item {
    background: color.adjust($primary-lighter, $lightness:  -80%);
  }

  .message-title {
    color: color.adjust($text-primary, $lightness:  80%);
  }

  .message-preview {
    color: color.adjust($text-regular, $lightness:  60%);
  }
}*/
</style>