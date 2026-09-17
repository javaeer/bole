<template>
  <view class="chat-container">
    <!-- 顶部导航栏 -->
    <view class="chat-header">
      <view class="header-center">
        <image class="chat-avatar" :src="targetAvatar" mode="aspectFit" />
        <view class="chat-info">
          <text class="chat-name">{{ targetName }}</text>
          <text class="chat-status">{{ onlineStatus }}</text>
        </view>
      </view>

      <view class="header-right">
        <text class="icon-more" @click="handleMore">···</text>
      </view>
    </view>

    <!-- 消息列表区域 -->
    <scroll-view
      class="message-list"
      scroll-y
      :scroll-into-view="scrollToView"
      :scroll-with-animation="true"
      @scrolltolower="loadHistory"
    >
      <!-- 日期分隔 -->
      <view class="date-divider" v-if="showDateDivider">
        <text class="date-text">今天</text>
      </view>

      <!-- 消息列表 -->
      <view
        v-for="message in messages"
        :key="message.id"
        :id="'msg-' + message.id"
        class="message-wrapper"
        :class="messageClass(message)"
      >
        <!-- 对方消息 -->
        <view v-if="!message.isSelf" class="message-other">
          <image class="message-avatar" :src="targetAvatar" mode="aspectFit" />
          <view class="message-bubble other-bubble">
            <text class="message-content">{{ message.content }}</text>
            <view class="message-meta">
              <text class="message-time">{{ formatTime(message.time) }}</text>
            </view>
          </view>
        </view>

        <!-- 自己消息 -->
        <view v-else class="message-self">
          <view class="message-bubble self-bubble">
            <text class="message-content">{{ message.content }}</text>
            <view class="message-meta">
              <text class="message-time">{{ formatTime(message.time) }}</text>
              <text
                v-if="message.status !== 'sent'"
                class="message-status"
                :class="message.status"
              >
                {{ statusIcon[message.status] }}
              </text>
            </view>
          </view>
          <image class="message-avatar" :src="userAvatar" mode="aspectFit" />
        </view>
      </view>

      <!-- 加载更多 -->
      <view v-if="loading" class="loading-more">
        <view class="loading-spinner"></view>
        <text class="loading-text">加载中...</text>
      </view>
    </scroll-view>

    <!-- 输入区域 -->
    <view class="input-area">
      <view class="input-row">
        <!-- 左侧功能按钮 -->
        <view class="input-actions">
          <view class="action-btn" @click="toggleEmoji">
            <text class="action-icon">😊</text>
          </view>
          <view class="action-btn" @click="handleAttachment">
            <text class="action-icon">📎</text>
          </view>
        </view>

        <!-- 文本输入 -->
        <view class="input-wrapper">
          <input
            v-model="inputText"
            class="chat-input"
            type="text"
            placeholder="输入消息..."
            placeholder-class="placeholder"
            :focus="inputFocus"
            @focus="handleInputFocus"
            @blur="handleInputBlur"
            @confirm="sendMessage"
          />
        </view>

        <!-- 发送按钮 -->
        <button
class="btn-send"
          :class="{ 'btn-send--active': canSend }"
          :disabled="!canSend"
          @click="sendMessage"
        >
          <text class="send-text">{{ sendButtonText }}</text>
        </button>
      </view>

      <!-- 表情面板 -->
      <view v-if="showEmojiPanel" class="emoji-panel">
        <scroll-view class="emoji-list" scroll-y>
          <view class="emoji-row" v-for="(row, index) in emojiList" :key="index">
            <view
              v-for="emoji in row"
              :key="emoji"
              class="emoji-item"
              @click="insertEmoji(emoji)"
            >
              <text class="emoji">{{ emoji }}</text>
            </view>
          </view>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick } from 'vue'

// 聊天对象信息
const targetName = ref('张三')
const targetAvatar = ref('/static/avatar-default.png')
const userAvatar = ref('/static/avatar-user.png')
const onlineStatus = ref('在线')

// 消息数据
const messages = ref([
  { id: 1, content: '你好！最近怎么样？', time: '09:30', isSelf: false, status: 'sent' },
  { id: 2, content: '挺好的，最近在忙项目', time: '09:31', isSelf: true, status: 'sent' },
  { id: 3, content: '上次说的简历修改，有什么建议吗？', time: '09:32', isSelf: false, status: 'sent' },
  { id: 4, content: '我看了你的简历，有几个地方可以优化...', time: '09:33', isSelf: true, status: 'sent' },
  { id: 5, content: '谢谢！能具体说说吗？😊', time: '09:34', isSelf: false, status: 'sent' },
  { id: 6, content: '比如项目经验可以更突出量化成果，技能部分需要重新组织', time: '09:35', isSelf: true, status: 'sent' },
  { id: 7, content: '明白了，我这就去修改', time: '09:36', isSelf: false, status: 'sent' },
])

// 输入状态
const inputText = ref('')
const inputFocus = ref(false)
const showEmojiPanel = ref(false)
const loading = ref(false)
const scrollToView = ref('')

// 表情列表
const emojiList = [
  ['😊', '😂', '😍', '😎', '😘', '😭', '😁', '👍'],
  ['👌', '❤️', '🎉', '🔥', '⭐', '💯', '✨', '🙏'],
  ['🤔', '👏', '💪', '🎯', '📚', '💼', '💰', '🚀'],
]

// 消息状态图标
const statusIcon = {
  sending: '⏳',
  failed: '❌',
  sent: '✓'
}

// 计算属性
const canSend = computed(() => inputText.value.trim().length > 0)
const sendButtonText = computed(() => inputText.value.trim().length > 0 ? '发送' : '⌨️')
const showDateDivider = computed(() => messages.value.length > 0)

// 消息样式类
const messageClass = (message: any) => {
  return {
    'message-first': message.isFirst,
    'message-last': message.isLast,
    'message-sending': message.status === 'sending'
  }
}

// 格式化时间
const formatTime = (time: string) => {
  return time
}

// 发送消息
const sendMessage = () => {
  if (!canSend.value) return

  const newMessage = {
    id: Date.now(),
    content: inputText.value.trim(),
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
    isSelf: true,
    status: 'sending' as const
  }

  messages.value.push(newMessage)
  inputText.value = ''

  // 滚动到底部
  scrollToBottom()

  // 模拟发送成功
  setTimeout(() => {
    newMessage.status = 'sent'
  }, 1000)

  // 模拟回复
  setTimeout(() => {
    const replyMessage = {
      id: Date.now() + 1,
      content: '收到！我会尽快处理',
      time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
      isSelf: false,
      status: 'sent' as const
    }
    messages.value.push(replyMessage)
    scrollToBottom()
  }, 2000)
}

// 滚动到底部
const scrollToBottom = () => {
  if (messages.value.length > 0) {
    const lastId = messages.value[messages.value.length - 1].id
    scrollToView.value = `msg-${lastId}`
  }
}

// 加载历史消息
const loadHistory = () => {
  if (loading.value) return

  loading.value = true
  // 模拟加载
  setTimeout(() => {
    const newMessages = [
      { id: Date.now() - 1000, content: '这是昨天的消息', time: '昨天 18:30', isSelf: false, status: 'sent' },
      { id: Date.now() - 2000, content: '好的，明天见！', time: '昨天 18:31', isSelf: true, status: 'sent' },
    ]
    messages.value = [...newMessages, ...messages.value]
    loading.value = false
  }, 1000)
}

// 插入表情
const insertEmoji = (emoji: string) => {
  inputText.value += emoji
}

// 切换表情面板
const toggleEmoji = () => {
  showEmojiPanel.value = !showEmojiPanel.value
  if (showEmojiPanel.value) {
    inputFocus.value = false
  }
}

// 输入框焦点处理
const handleInputFocus = () => {
  inputFocus.value = true
  showEmojiPanel.value = false
}

const handleInputBlur = () => {
  inputFocus.value = false
}

// 附件功能
const handleAttachment = () => {
  uni.showActionSheet({
    itemList: ['图片', '文件', '位置'],
    success: (res) => {
      const actions = ['选择图片', '选择文件', '发送位置']
      uni.showToast({
        title: `开发中: ${actions[res.tapIndex]}`,
        icon: 'none'
      })
    }
  })
}

// 更多操作
const handleMore = () => {
  uni.showActionSheet({
    itemList: ['清空聊天', '投诉举报', '用户信息'],
    success: (res) => {
      const actions = ['清空聊天记录', '投诉该用户', '查看用户信息']
      uni.showToast({
        title: `开发中: ${actions[res.tapIndex]}`,
        icon: 'none'
      })
    }
  })
}

// 返回
const handleBack = () => {
  uni.navigateBack()
}

// 页面加载完成
onMounted(() => {
  nextTick(() => {
    scrollToBottom()
  })
})
</script>

<style scoped lang="scss">
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: $bg-color;
}

/* 顶部导航栏 */
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $padding-base;
  height: $navigation-bar-height;
  background: $bg-color-white;
  border-bottom: 1rpx solid $color-border-light;
  box-shadow: $box-shadow-light;
  z-index: $z-index-base + 1;
}


.header-center {
  flex: 2;
  display: flex;
  align-items: center;
  justify-content: center;

  .chat-avatar {
    width: 60rpx;
    height: 60rpx;
    border-radius: $border-radius-circle;
    margin-right: $margin-sm;
  }

  .chat-info {
    display: flex;
    flex-direction: column;

    .chat-name {
      font-size: $font-size-base;
      font-weight: $font-weight-bold;
      color: $color-text-primary;
    }

    .chat-status {
      font-size: $font-size-sm;
      color: $color-success;
    }
  }
}

.header-right {
  flex: 1;
  display: flex;
  justify-content: flex-end;

  .icon-more {
    font-size: $font-size-lg;
    color: $color-text-regular;
  }
}

/* 消息列表 */
.message-list {
  flex: 1;
  padding: $padding-base;
  overflow: hidden;
}

.date-divider {
  display: flex;
  justify-content: center;
  margin: $margin-base 0;

  .date-text {
    padding: $padding-mini $padding-base;
    background: $color-border-light;
    color: $color-text-secondary;
    font-size: $font-size-sm;
    border-radius: $border-radius;
  }
}

.message-wrapper {
  margin-bottom: $margin-base;

  &.message-first {
    margin-top: $margin-base;
  }
}

.message-other,
.message-self {
  display: flex;
  align-items: flex-start;
  max-width: 80%;
}

.message-other {
  .message-bubble {
    margin-left: $margin-sm;
  }
}

.message-self {
  justify-content: flex-end;
  margin-left: auto;

  .message-bubble {
    margin-right: $margin-sm;
  }
}

.message-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: $border-radius-sm;
  flex-shrink: 0;
}

.message-bubble {
  position: relative;
  padding: $padding-sm $padding-base;
  border-radius: $border-radius;
  max-width: 100%;

  &.other-bubble {
    background: $bg-color-white;
    border: 1rpx solid $color-border-light;

    &::before {
      content: '';
      position: absolute;
      left: -16rpx;
      top: 20rpx;
      border: 8rpx solid transparent;
      border-right-color: $bg-color-white;
    }
  }

  &.self-bubble {
    background: linear-gradient(135deg, $color-primary 0%, color.adjust($color-primary, $lightness:   10%) 100%);
    color: $bg-color-white;

    &::before {
      content: '';
      position: absolute;
      right: -16rpx;
      top: 20rpx;
      border: 8rpx solid transparent;
      border-left-color: $color-primary;
    }
  }
}

.message-content {
  font-size: $font-size-base;
  line-height: 1.5;
  word-break: break-word;
}

.message-meta {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-top: $margin-mini;
}

.message-time {
  font-size: $font-size-2xs;
  opacity: 0.7;
}

.message-status {
  margin-left: $margin-mini;
  font-size: $font-size-2xs;

  &.sending {
    opacity: 0.6;
  }

  &.failed {
    color: $color-danger;
  }
}

/* 加载更多 */
.loading-more {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: $padding-base;

  .loading-spinner {
    width: 40rpx;
    height: 40rpx;
    border: 4rpx solid $color-border;
    border-top-color: $color-primary;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: $margin-mini;
  }

  .loading-text {
    font-size: $font-size-sm;
    color: $color-text-secondary;
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 输入区域 */
.input-area {
  background: $bg-color-white;
  border-top: 1rpx solid $color-border-light;
  padding: $padding-sm $padding-base;
}

.input-row {
  display: flex;
  align-items: center;
  gap: $margin-sm;
}

.input-actions {
  display: flex;
  gap: $margin-mini;

  .action-btn {
    width: 60rpx;
    height: 60rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: $border-radius-circle;
    background: $bg-color;

    .action-icon {
      font-size: $font-size-base;
    }
  }
}

.input-wrapper {
  flex: 1;
  background: $bg-color;
  border-radius: $border-radius;
  border: 2rpx solid $color-border-light;

  .chat-input {
    width: 100%;
    height: 60rpx;
    padding: 0 $padding-sm;
    font-size: $font-size-base;
    color: $color-text-primary;
    background: transparent;
    border: none;
    outline: none;
  }

  .placeholder {
    color: $color-text-placeholder;
    font-size: $font-size-base;
  }
}

.btn-send {
  width: 120rpx;
  height: 60rpx;
  background: $color-border-light;
  border: none;
  border-radius: $border-radius;
  font-size: $font-size-base;
  color: $color-text-placeholder;
  transition: all $transition-fast;

  &--active {
    background: $color-primary;
    color: $bg-color-white;
    box-shadow: $box-shadow-light;
  }

  .send-text {
    font-weight: $font-weight-medium;
  }
}

/* 表情面板 */
.emoji-panel {
  height: 300rpx;
  background: $bg-color-white;
  border-top: 1rpx solid $color-border-light;
  margin-top: $margin-sm;
}

.emoji-list {
  height: 100%;
}

.emoji-row {
  display: flex;
  padding: $padding-sm;
}

.emoji-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60rpx;

  .emoji {
    font-size: $font-size-base;
  }
}

.chat-container {
  // 响应式调整
  @media (max-width: 375px) {
    .chat-header {
      padding: 0 $padding-sm;
    }

    .message-avatar {
      width: 70rpx;
      height: 70rpx;
    }

    .input-area {
      padding: $padding-mini;
    }
  }
  
  // 添加响应式
  @media (max-width: $screen-md) {
    .message-list {
      padding: $padding-sm;
    }
    
    .input-area {
      padding: $padding-mini;
    }
  }
}
.message-wrapper {
  transition: all $transition-fast $ease-in-out;
  
  &:hover {
    transform: translateY(-2rpx);
    box-shadow: $box-shadow-light;
  }
}

</style>