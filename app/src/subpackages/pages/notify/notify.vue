<template>
  <view class="page-container">
    <!-- 顶部导航 -->
    <view class="custom-navbar">
      <view class="navbar-left">
        <text class="navbar-title">消息详情</text>
      </view>
      <view class="navbar-right" @click="handleMore">
        <text class="navbar-more-icon">⋮</text>
      </view>
    </view>

    <!-- 消息头部信息 -->
    <view class="message-header">
      <view class="message-sender">
        <image 
          class="sender-avatar" 
          :src="message.senderAvatar" 
          mode="aspectFill"
        />
        <view class="sender-info">
          <text class="sender-name">{{ message.senderName }}</text>
          <text class="sender-title">{{ message.senderTitle }}</text>
        </view>
        <view v-if="message.isOfficial" class="official-badge">官方</view>
      </view>

      <view class="message-meta">
        <text class="message-time">{{ formatTime(message.sendTime) }}</text>
        <text class="message-status">{{ getStatusText(message.status) }}</text>
      </view>
    </view>

    <!-- 消息内容区域 -->
    <view class="message-content">
      <view class="message-title-wrapper">
        <text class="message-title">{{ message.title }}</text>
        <view v-if="message.priority === 'high'" class="priority-badge priority-high">重要</view>
        <view v-else-if="message.priority === 'medium'" class="priority-badge priority-medium">一般</view>
      </view>

      <view class="message-body">
        <text class="message-text">{{ message.content }}</text>
        
        <!-- 附件列表 -->
        <view v-if="message.attachments && message.attachments.length > 0" class="attachments-section">
          <text class="attachments-title">附件 ({{ message.attachments.length }})</text>
          <view class="attachments-list">
            <view 
              v-for="(attachment, index) in message.attachments" 
              :key="index"
              class="attachment-item"
              @click="handlePreviewAttachment(attachment)"
            >
              <view class="attachment-icon">
                <text v-if="attachment.type === 'pdf'">📄</text>
                <text v-else-if="attachment.type === 'image'">🖼️</text>
                <text v-else-if="attachment.type === 'doc'">📝</text>
                <text v-else>📎</text>
              </view>
              <view class="attachment-info">
                <text class="attachment-name">{{ attachment.name }}</text>
                <text class="attachment-size">{{ formatFileSize(attachment.size) }}</text>
              </view>
              <view class="attachment-action" @click.stop="handleDownloadAttachment(attachment)">
                <text class="download-icon">↓</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 关联链接 -->
        <view v-if="message.links && message.links.length > 0" class="links-section">
          <text class="links-title">相关链接</text>
          <view class="links-list">
            <view 
              v-for="(link, index) in message.links" 
              :key="index"
              class="link-item"
              @click="handleOpenLink(link)"
            >
              <text class="link-icon">🔗</text>
              <text class="link-text">{{ link.title }}</text>
              <text class="link-arrow">→</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 操作按钮 -->
      <view class="message-actions">
        <button 
          v-if="message.status === 'unread'" 
          class="btn-action btn-read"
          @click="handleMarkAsRead"
        >
          <text class="action-icon">✓</text>
          <text class="action-text">标记为已读</text>
        </button>
        
        <button 
          class="btn-action btn-reply"
          @click="handleReply"
        >
          <text class="action-icon">↩</text>
          <text class="action-text">回复</text>
        </button>
        
        <button 
          class="btn-action btn-forward"
          @click="handleForward"
        >
          <text class="action-icon">↪</text>
          <text class="action-text">转发</text>
        </button>
        
        <button 
          class="btn-action btn-delete"
          @click="handleDelete"
        >
          <text class="action-icon">🗑️</text>
          <text class="action-text">删除</text>
        </button>
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-actions">
      <button 
        class="btn-bottom btn-reply-bottom"
        @click="handleQuickReply"
      >
        <text class="bottom-icon">💬</text>
        <text class="bottom-text">快速回复</text>
      </button>
      
      <button 
        class="btn-bottom btn-star"
        :class="{ 'starred': message.starred }"
        @click="handleToggleStar"
      >
        <text class="bottom-icon">{{ message.starred ? '⭐' : '☆' }}</text>
        <text class="bottom-text">{{ message.starred ? '已收藏' : '收藏' }}</text>
      </button>
    </view>

    <!-- 相关消息 -->
    <view v-if="relatedMessages.length > 0" class="related-messages">
      <view class="section-header">
        <text class="section-title">相关消息</text>
      </view>
      <view class="related-list">
        <view 
          v-for="(related, index) in relatedMessages" 
          :key="index"
          class="related-item"
          @click="handleOpenRelated(related)"
        >
          <view class="related-left">
            <text class="related-sender">{{ related.senderName }}</text>
            <text class="related-title">{{ related.title }}</text>
          </view>
          <view class="related-right">
            <text class="related-time">{{ formatRelativeTime(related.sendTime) }}</text>
            <view v-if="!related.read" class="unread-dot"></view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

// 消息数据
const message = reactive({
  id: '1',
  senderName: '系统通知',
  senderTitle: '简历大师官方',
  senderAvatar: '/static/logo.png',
  isOfficial: true,
  title: '您的简历优化建议已生成',
  content: `尊敬的会员，您好！\n\n我们已根据您的最新简历信息，为您生成了专业的优化建议报告。报告包含以下内容：\n\n1. 格式优化建议\n2. 关键词匹配度分析\n3. 行业竞争力评估\n4. 个性化改进方案\n\n请及时查看完整报告，如有疑问请联系客服。`,
  sendTime: '2024-01-15 14:30:00',
  status: 'unread', // unread, read
  priority: 'high', // high, medium, low
  starred: false,
  attachments: [
    { id: '1', name: '优化建议报告.pdf', type: 'pdf', size: 2457600 },
    { id: '2', name: '模板参考.png', type: 'image', size: 512000 }
  ],
  links: [
    { title: '查看完整优化报告', url: '/pages/report/detail' },
    { title: '简历模板库', url: '/pages/template/list' }
  ]
})

// 相关消息
const relatedMessages = ref([
  { id: '2', senderName: '系统通知', title: '简历模板更新通知', sendTime: '2024-01-14', read: true },
  { id: '3', senderName: '客服中心', title: '关于您的问题回复', sendTime: '2024-01-13', read: false }
])

// 格式化时间
const formatTime = (timeStr: string) => {
  const date = new Date(timeStr)
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString('zh-CN', { 
    hour: '2-digit', 
    minute: '2-digit' 
  })
}

// 相对时间
const formatRelativeTime = (timeStr: string) => {
  const now = new Date()
  const date = new Date(timeStr)
  const diffDays = Math.floor((now.getTime() - date.getTime()) / (1000 * 3600 * 24))
  
  if (diffDays === 0) return '今天'
  if (diffDays === 1) return '昨天'
  if (diffDays < 7) return `${diffDays}天前`
  return formatTime(timeStr)
}

// 格式化文件大小
const formatFileSize = (bytes: number) => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    unread: '未读',
    read: '已读'
  }
  return statusMap[status] || status
}

// 页面加载
onLoad((options: any) => {
  if (options.id) {
    // 根据ID加载消息详情
    console.log('加载消息ID:', options.id)
  }
})


const handleMore = () => {
  uni.showActionSheet({
    itemList: ['标记为未读', '加入收藏', '举报消息', '取消'],
    success: (res) => {
      switch (res.tapIndex) {
        case 0:
          handleMarkAsUnread()
          break
        case 1:
          handleToggleStar()
          break
        case 2:
          handleReport()
          break
      }
    }
  })
}

const handleMarkAsRead = () => {
  message.status = 'read'
  uni.showToast({ title: '已标记为已读', icon: 'success' })
}

const handleMarkAsUnread = () => {
  message.status = 'unread'
  uni.showToast({ title: '已标记为未读', icon: 'success' })
}

const handleReply = () => {
  uni.navigateTo({
    url: `/pages/message/reply?id=${message.id}`
  })
}

const handleQuickReply = () => {
  uni.showModal({
    title: '快速回复',
    placeholderText: '请输入回复内容',
    success: (res) => {
      if (res.confirm && res.content) {
        uni.showToast({ title: '回复成功', icon: 'success' })
      }
    }
  })
}

const handleForward = () => {
  uni.showToast({ title: '转发功能开发中', icon: 'none' })
}

const handleDelete = () => {
  uni.showModal({
    title: '确认删除',
    content: '删除后消息将无法恢复',
    success: (res) => {
      if (res.confirm) {
        uni.showToast({ title: '已删除', icon: 'success' })
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      }
    }
  })
}

const handleToggleStar = () => {
  message.starred = !message.starred
  uni.showToast({ 
    title: message.starred ? '已加入收藏' : '已取消收藏',
    icon: 'success'
  })
}

const handlePreviewAttachment = (attachment: any) => {
  uni.showToast({ 
    title: `预览: ${attachment.name}`, 
    icon: 'none' 
  })
}

const handleDownloadAttachment = (attachment: any) => {
  uni.showLoading({ title: '下载中...' })
  setTimeout(() => {
    uni.hideLoading()
    uni.showToast({ 
      title: `已下载: ${attachment.name}`, 
      icon: 'success' 
    })
  }, 1000)
}

const handleOpenLink = (link: any) => {
  if (link.url.startsWith('/')) {
    uni.navigateTo({ url: link.url })
  } else {
    uni.showModal({
      title: '打开链接',
      content: link.url,
      success: (res) => {
        if (res.confirm) {
          // 实际项目中这里会使用web-view或跳转到外部浏览器
          uni.showToast({ title: '打开链接开发中', icon: 'none' })
        }
      }
    })
  }
}

const handleOpenRelated = (related: any) => {
  uni.navigateTo({
    url: `/pages/message/detail?id=${related.id}`
  })
}

const handleReport = () => {
  uni.showToast({ title: '举报功能开发中', icon: 'none' })
}
</script>

<style scoped lang="scss">

/* 自定义导航栏 */
.custom-navbar {
  background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
  color: $background-color-white;
  padding: $navigation-bar-height $padding-base $padding-base;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: $margin-small;
}

.navbar-back-icon {
  font-size: $font-size-large;
  font-weight: $font-weight-bold;
}

.navbar-title {
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
}

.navbar-right {
  .navbar-more-icon {
    font-size: $font-size-medium;
    font-weight: $font-weight-bold;
    padding: $padding-mini;
  }
}

/* 消息头部信息 */
.message-header {
  background: $background-color-white;
  padding: $padding-base;
  border-bottom: 1rpx solid $border-color-extra-light;
}

.message-sender {
  display: flex;
  align-items: center;
  gap: $margin-small;
  margin-bottom: $margin-mini;
}

.sender-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: $border-radius-round;
}

.sender-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.sender-name {
  font-size: $font-size-base;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.sender-title {
  font-size: $font-size-small;
  color: $text-secondary;
}

.official-badge {
  background: $primary-color;
  color: $background-color-white;
  font-size: $font-size-extra-small;
  padding: 4rpx $padding-mini;
  border-radius: $border-radius-small;
  font-weight: $font-weight-medium;
}

.message-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: $margin-mini;
}

.message-time {
  font-size: $font-size-small;
  color: $text-secondary;
}

.message-status {
  font-size: $font-size-small;
  color: $info-color;
  padding: 2rpx $padding-mini;
  background: $border-color-extra-light;
  border-radius: $border-radius-small;
}

/* 消息内容区域 */
.message-content {
  background: $background-color-white;
  margin: $margin-base;
  border-radius: $border-radius;
  padding: $padding-base;
  box-shadow: $box-shadow;
}

.message-title-wrapper {
  display: flex;
  align-items: center;
  gap: $margin-small;
  margin-bottom: $margin-base;
  padding-bottom: $padding-small;
  border-bottom: 1rpx solid $border-color-extra-light;
}

.message-title {
  flex: 1;
  font-size: $font-size-large;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.priority-badge {
  font-size: $font-size-extra-small;
  font-weight: $font-weight-bold;
  padding: 4rpx $padding-mini;
  border-radius: $border-radius-small;
  
  &.priority-high {
    background: $danger-light;
    color: $danger-color;
  }
  
  &.priority-medium {
    background: $warning-light;
    color: $warning-color;
  }
}

.message-body {
  padding-bottom: $padding-base;
  border-bottom: 1rpx solid $border-color-extra-light;
}

.message-text {
  font-size: $font-size-base;
  color: $text-regular;
  line-height: 1.6;
  white-space: pre-line;
}

/* 附件区域 */
.attachments-section {
  margin-top: $margin-base;
}

.attachments-title,
.links-title {
  font-size: $font-size-base;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $margin-small;
  display: block;
}

.attachments-list {
  display: flex;
  flex-direction: column;
  gap: $margin-small;
}

.attachment-item {
  display: flex;
  align-items: center;
  padding: $padding-small;
  background: $background-color;
  border-radius: $border-radius;
  border: 1rpx solid $border-color-light;
  transition: all $transition-fast;
  
  &:active {
    background: color.adjust($background-color, $lightness:  - 5%);
  }
}

.attachment-icon {
  font-size: $font-size-medium;
  margin-right: $margin-small;
  width: 40rpx;
  text-align: center;
}

.attachment-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2rpx;
}

.attachment-name {
  font-size: $font-size-base;
  color: $text-primary;
  @extend .text-truncate;
}

.attachment-size {
  font-size: $font-size-extra-small;
  color: $text-secondary;
}

.attachment-action {
  padding: $padding-mini;
  margin-left: $margin-small;
  
  .download-icon {
    font-size: $font-size-medium;
    color: $primary-color;
  }
}

/* 链接区域 */
.links-section {
  margin-top: $margin-base;
}

.links-list {
  display: flex;
  flex-direction: column;
  gap: $margin-small;
}

.link-item {
  display: flex;
  align-items: center;
  padding: $padding-small;
  background: $primary-color-lighter;
  border-radius: $border-radius;
  border: 1rpx solid rgba($primary-color, 0.2);
  transition: all $transition-fast;
  
  &:active {
    background: color.adjust($primary-color-lighter, $lightness:  -5%);
  }
}

.link-icon {
  font-size: $font-size-medium;
  margin-right: $margin-small;
}

.link-text {
  flex: 1;
  font-size: $font-size-base;
  color: $text-primary;
  @extend .text-truncate;
}

.link-arrow {
  font-size: $font-size-medium;
  color: $primary-color;
  margin-left: $margin-small;
}

/* 操作按钮 */
.message-actions {
  display: flex;
  flex-wrap: wrap;
  gap: $margin-small;
  margin-top: $margin-base;
  padding-top: $padding-base;
  border-top: 1rpx solid $border-color-extra-light;
}

.btn-action {
  flex: 1;
  min-width: calc(50% - #{$margin-small});
  height: $button-height;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $margin-mini;
  border: none;
  border-radius: $border-radius-small;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  transition: all $transition-fast;
  
  &:active {
    transform: translateY(2rpx);
  }
  
  &.btn-read {
    background: $success-color;
    color: $background-color-white;
  }
  
  &.btn-reply {
    background: $primary-color;
    color: $background-color-white;
  }
  
  &.btn-forward {
    background: $info-color;
    color: $background-color-white;
  }
  
  &.btn-delete {
    background: $danger-color;
    color: $background-color-white;
  }
}

.action-icon {
  font-size: $font-size-base;
}

.action-text {
  font-size: $font-size-base;
}

/* 底部操作栏 */
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: $background-color-white;
  padding: $padding-small $padding-base;
  border-top: 1rpx solid $border-color-light;
  display: flex;
  gap: $margin-small;
  z-index: $z-index-dropdown;
}

.btn-bottom {
  flex: 1;
  height: $button-height;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $margin-mini;
  border: none;
  border-radius: $border-radius;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  transition: all $transition-fast;
  
  &:active {
    transform: translateY(2rpx);
  }
  
  &.btn-reply-bottom {
    background: $primary-color;
    color: $background-color-white;
  }
  
  &.btn-star {
    background: $background-color;
    color: $text-secondary;
    
    &.starred {
      background: $warning-light;
      color: $warning-color;
    }
  }
}

.bottom-icon {
  font-size: $font-size-base;
}

.bottom-text {
  font-size: $font-size-base;
}

/* 相关消息 */
.related-messages {
  background: $background-color-white;
  margin: $margin-base;
  border-radius: $border-radius;
  padding: $padding-base;
  box-shadow: $box-shadow;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $margin-base;
}

.section-title {
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.related-list {
  display: flex;
  flex-direction: column;
  gap: 1rpx;
}

.related-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $padding-small 0;
  border-bottom: 1rpx solid $border-color-extra-light;
  transition: all $transition-fast;
  
  &:active {
    background: $background-color;
  }
  
  &:last-child {
    border-bottom: none;
  }
}

.related-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.related-sender {
  font-size: $font-size-small;
  color: $text-secondary;
}

.related-title {
  font-size: $font-size-base;
  color: $text-primary;
  @extend .text-truncate;
  max-width: 400rpx;
}

.related-right {
  display: flex;
  align-items: center;
  gap: $margin-mini;
}

.related-time {
  font-size: $font-size-extra-small;
  color: $text-secondary;
  white-space: nowrap;
}

.unread-dot {
  width: 12rpx;
  height: 12rpx;
  background: $primary-color;
  border-radius: $border-radius-round;
}

/* 响应式调整 */
@media (max-width: 375px) {
  .message-actions {
    .btn-action {
      min-width: 100%;
    }
  }
}
</style>