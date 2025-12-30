<template>
  <view class="page-container">
    <!-- 头部信息 -->
    <view class="detail-header">
      <view class="header-title">
        {{ companyDetail?.name || "公司详情" }}
      </view>
      <view class="header-actions">
        <view
          v-if="!isEditMode"
          class="header-btn"
          @click="enterEditMode"
        >
          编辑
        </view>
        <view
          v-else
          class="header-btn"
          @click="cancelEdit"
        >
          取消
        </view>
      </view>
    </view>

    <!-- 主要内容区域 -->
    <scroll-view
      class="content-container"
      scroll-y
      @scrolltolower="loadMoreComments"
    >
      <!-- 公司基本信息 -->
      <view class="section-container">
        <view class="section-title">基本信息</view>

        <template v-if="!isEditMode">
          <!-- 查看模式 -->
          <view class="info-group">
            <view class="info-item">
              <view class="info-label">公司名称</view>
              <view class="info-value">{{ companyDetail?.name }}</view>
            </view>
            <view class="info-item">
              <view class="info-label">负责人</view>
              <view class="info-value">{{ companyDetail?.holder }}</view>
            </view>
            <view class="info-item">
              <view class="info-label">所在地</view>
              <view class="info-value">{{ companyDetail?.location }}</view>
            </view>
            <view class="info-item">
              <view class="info-label">邮箱</view>
              <view class="info-value">{{ companyDetail?.email }}</view>
            </view>
            <view class="info-item">
              <view class="info-label">官网</view>
              <view
                class="info-value link"
                @click="openLink(companyDetail?.website || '')"
              >
                {{ companyDetail?.website }}
              </view>
            </view>
          </view>
        </template>

        <template v-else>
          <!-- 编辑模式 -->
          <view class="form-group">
            <view class="form-item">
              <view class="form-label">公司名称</view>
              <input
                v-model="editForm.name"
                class="form-input"
                placeholder="请输入公司名称"
              />
            </view>
            <view class="form-item">
              <view class="form-label">负责人</view>
              <input
                v-model="editForm.holder"
                class="form-input"
                placeholder="请输入负责人"
              />
            </view>
            <view class="form-item">
              <view class="form-label">所在地</view>
              <input
                v-model="editForm.location"
                class="form-input"
                placeholder="请输入所在地"
              />
            </view>
            <view class="form-item">
              <view class="form-label">邮箱</view>
              <input
                v-model="editForm.email"
                class="form-input"
                placeholder="请输入邮箱"
                type="email"
              />
            </view>
            <view class="form-item">
              <view class="form-label">官网</view>
              <input
                v-model="editForm.website"
                class="form-input"
                placeholder="请输入官网地址"
              />
            </view>
            <view class="form-item">
              <view class="form-label">公司简介</view>
              <textarea
                v-model="editForm.bio"
                class="form-textarea"
                placeholder="请输入公司简介"
                maxlength="200"
              />
              <view class="textarea-count">
                {{ editForm.bio?.length || 0 }}/200
              </view>
            </view>
          </view>
        </template>
      </view>

      <!-- 社交信息 -->
      <view class="section-container">
        <view class="section-title">社交信息</view>
        <view class="social-links">
          <view
            v-if="companyDetail?.github"
            class="social-item"
            @click="openLink(`https://github.com/${companyDetail.github}`)"
          >
            <uni-icons type="github" size="24" />
            <text>GitHub</text>
          </view>
          <view
            v-if="companyDetail?.wechat"
            class="social-item"
          >
            <uni-icons type="weixin" size="24" />
            <text>微信</text>
          </view>
        </view>
      </view>

      <!-- 统计数据 -->
      <view class="section-container">
        <view class="section-title">统计数据</view>
        <view class="stats-grid">
          <view class="stat-card">
            <view class="stat-number">{{ companyDetail?.followers || 0 }}</view>
            <view class="stat-label">关注者</view>
          </view>
          <view class="stat-card">
            <view class="stat-number">{{ companyDetail?.fans || 0 }}</view>
            <view class="stat-label">粉丝</view>
          </view>
          <view class="stat-card">
            <view class="stat-number">{{ companyDetail?.likes || 0 }}</view>
            <view class="stat-label">点赞</view>
          </view>
        </view>
      </view>

      <!-- 时间信息 -->
      <view class="section-container">
        <view class="section-title">时间信息</view>
        <view class="time-info">
          <view class="time-item">
            <uni-icons type="calendar" size="18" color="$text-secondary" />
            <text>创建时间：{{ formatDateTime(companyDetail?.createdAt || "") }}</text>
          </view>
          <view class="time-item">
            <uni-icons type="calendar" size="18" color="$text-secondary" />
            <text>更新时间：{{ formatDateTime(companyDetail?.updatedAt || "") }}</text>
          </view>
        </view>
      </view>

      <!-- 评论区域 -->
      <view class="section-container">
        <view class="section-header">
          <view class="section-title">评价 ({{ commentTotal }})</view>
          <view
            class="add-comment-btn"
            @click="showCommentInput = true"
          >
            <uni-icons type="plus" size="20" />
            <text>添加评价</text>
          </view>
        </view>

        <!-- 评论输入框 -->
        <view v-if="showCommentInput" class="comment-input-container">
          <textarea
            v-model="newComment"
            class="comment-textarea"
            placeholder="写下您的评价..."
            maxlength="500"
            auto-height
          />
          <view class="comment-actions">
            <view class="textarea-count">
              {{ newComment.length }}/500
            </view>
            <view class="action-buttons">
              <view
                class="action-btn cancel"
                @click="showCommentInput = false; newComment = ''"
              >
                取消
              </view>
              <view
                class="action-btn submit"
                :class="{ disabled: !newComment.trim() }"
                @click="submitComment"
              >
                发布
              </view>
            </view>
          </view>
        </view>

        <!-- 评论列表 -->
        <view v-if="commentList.length > 0" class="comment-list">
          <view
            v-for="comment in commentList"
            :key="comment.id"
            class="comment-item"
          >
            <view class="comment-header">
              <view class="comment-user">
                <view class="user-avatar">
                  {{ comment.userName?.charAt(0) || "U" }}
                </view>
                <view class="user-info">
                  <view class="user-name">{{ comment.userName }}</view>
                  <view class="comment-time">{{ formatDateTime(comment.createdAt) }}</view>
                </view>
              </view>
              <view
                v-if="comment.canDelete"
                class="comment-delete"
                @click="deleteComment(comment.id)"
              >
                <uni-icons type="trash" size="18" color="$text-secondary" />
              </view>
            </view>
            <view class="comment-content">
              {{ comment.content }}
            </view>
          </view>

          <!-- 加载更多 -->
          <view v-if="!commentFinished" class="load-more">
            <view v-if="commentLoading" class="loading-text">
              <uni-icons type="spinner-cycle" size="20" />
              <text>加载中...</text>
            </view>
            <view v-else class="load-more-btn" @click="loadMoreComments">
              加载更多
            </view>
          </view>
          <view v-else-if="commentList.length > 0" class="no-more">
            <text>没有更多了</text>
          </view>
        </view>

        <!-- 空评论 -->
        <view v-else class="empty-comments">
          <uni-icons type="chat" size="60" color="$empty-text-color" />
          <text class="empty-text">暂无评价</text>
        </view>
      </view>
    </scroll-view>

    <!-- 底部操作栏 -->
    <view v-if="!isEditMode" class="action-bar">
      <view
        class="follow-btn"
        :class="{ followed: companyDetail?.followed }"
        @click="toggleFollow"
      >
        <uni-icons
          :type="companyDetail?.followed ? 'heart-filled' : 'heart'"
          size="20"
          :color="companyDetail?.followed ? 'white' : '$primary-color'"
        />
        <text>{{ companyDetail?.followed ? "已关注" : "关注" }}</text>
      </view>
      <view class="action-divider"></view>
      <view class="like-btn">
        <uni-icons type="hand-up" size="20" color="$warning-color" />
        <text>点赞</text>
      </view>
    </view>

    <!-- 编辑模式保存按钮 -->
    <view v-else class="edit-actions">
      <view class="save-btn" @click="saveEdit">
        保存修改
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import CompanyAPI from "@/api/company";
import type { CompanyResult } from "@/types/company";
import { CompanyCommentQuery, CompanyCommentResult } from "@/types/company-comment-result";
import CompanyCommentAPI from "@/api/company-comment";

// 页面参数
const pageParams = ref<{ id: number }>();

// 公司详情数据
const companyDetail = ref<CompanyResult | null>(null);

// 编辑模式
const isEditMode = ref(false);
const editForm = reactive<Partial<CompanyResult>>({});

// 评论分页相关
const commentList = ref<CompanyCommentResult[]>([]);
const newComment = ref("");
const showCommentInput = ref(false);
const commentPage = ref(1);
const commentSize = ref(10);
const commentTotal = ref(0);
const commentLoading = ref(false);
const commentFinished = ref(false);

// 加载详情
const loadCompanyDetail = async (id: number) => {
  try {
    const data = await CompanyAPI.getById(id);
    companyDetail.value = data;
    Object.assign(editForm, data);
  } catch (error) {
    uni.showToast({
      title: "加载失败",
      icon: "error",
    });
  }
};

// 加载评论
const loadComments = async (companyId: number, reset: boolean = true) => {
  if (commentLoading.value) return;

  if (reset) {
    commentPage.value = 1;
    commentFinished.value = false;
    commentList.value = [];
  }

  commentLoading.value = true;

  try {
    const pageQuery = {
      page: commentPage.value,
      size: commentSize.value,
    };

    const query: CompanyCommentQuery = {
      companyId: companyId,
    };

    const { records, total } = await CompanyCommentAPI.page(pageQuery, query);

    commentTotal.value = total;

    if (reset) {
      commentList.value = records;
    } else {
      commentList.value = [...commentList.value, ...records];
    }

    // 判断是否还有更多数据
    if (records.length < commentSize.value || commentList.value.length >= total) {
      commentFinished.value = true;
    } else {
      commentPage.value++;
    }
  } catch (error) {
    console.error("加载评论失败:", error);
    uni.showToast({
      title: "加载评论失败",
      icon: "error",
    });
  } finally {
    commentLoading.value = false;
  }
};

// 加载更多评论
const loadMoreComments = () => {
  if (!commentFinished.value && !commentLoading.value && pageParams.value?.id) {
    loadComments(pageParams.value.id, false);
  }
};

// 进入编辑模式
const enterEditMode = () => {
  isEditMode.value = true;
  uni.pageScrollTo({
    scrollTop: 0,
    duration: 300,
  });
};

// 保存编辑
const saveEdit = async () => {
  if (!companyDetail.value) return;

  try {
    const updated = await CompanyAPI.update(
      companyDetail.value.id,
      editForm,
    );

    companyDetail.value = updated;
    isEditMode.value = false;

    uni.showToast({
      title: "保存成功",
      icon: "success",
    });
  } catch (error) {
    uni.showToast({
      title: "保存失败",
      icon: "error",
    });
  }
};

// 取消编辑
const cancelEdit = () => {
  isEditMode.value = false;
  if (companyDetail.value) {
    Object.assign(editForm, companyDetail.value);
  }
};

// 关注/取消关注 - 使用 followed 字段
const toggleFollow = async () => {
  if (!companyDetail.value) return;

  try {
    if (companyDetail.value.followed) {
      await CompanyAPI.unfollow(companyDetail.value.id);
      companyDetail.value.followed = false;
      companyDetail.value.followers = Math.max(0, (companyDetail.value.followers || 1) - 1);
      uni.showToast({
        title: "已取消关注",
        icon: "success",
      });
    } else {
      await CompanyAPI.follow(companyDetail.value.id);
      companyDetail.value.followed = true;
      companyDetail.value.followers = (companyDetail.value.followers || 0) + 1;
      uni.showToast({
        title: "关注成功",
        icon: "success",
      });
    }
  } catch (error) {
    uni.showToast({
      title: "操作失败",
      icon: "error",
    });
  }
};

// 提交评论
const submitComment = async () => {
  if (!newComment.value.trim() || !companyDetail.value) return;

  try {
    // 显示加载提示
    uni.showLoading({
      title: "发布中...",
      mask: true,
    });

    // 假设有一个添加评论的API
    // 这里需要根据实际的API进行调整
    const comment = await CompanyCommentAPI.add({
      companyId: companyDetail.value.id,
      content: newComment.value.trim(),
    });

    if (comment) {
      // 清空输入框并关闭输入区域
      newComment.value = "";
      showCommentInput.value = false;

      // 直接刷新评论列表（从第一页重新加载）
      await loadComments(companyDetail.value.id, true);

      uni.hideLoading();
      uni.showToast({
        title: "评论成功",
        icon: "success",
      });
    }
  } catch (error) {
    uni.hideLoading();
    uni.showToast({
      title: "评论失败",
      icon: "error",
    });
  }
};

// 删除评论
const deleteComment = async (commentId: number) => {
  try {
    uni.showLoading({
      title: "删除中...",
      mask: true,
    });

    // 假设有一个删除评论的API
    await CompanyCommentAPI.delete(commentId);

    // 重新加载评论列表
    if (companyDetail.value?.id) {
      await loadComments(companyDetail.value.id, true);
    }

    uni.hideLoading();
    uni.showToast({
      title: "删除成功",
      icon: "success",
    });
  } catch (error) {
    uni.hideLoading();
    uni.showToast({
      title: "删除失败",
      icon: "error",
    });
  }
};

// 格式化时间
const formatDateTime = (dateStr: string) => {
  if (!dateStr) return "";
  return dateStr.replace(" ", " · ");
};

// 打开链接（使用 Webview）
const openLink = (url: string) => {
  if (!url) {
    uni.showToast({
      title: "链接无效",
      icon: "error",
    });
    return;
  }

  // 确保 URL 有协议头
  let targetUrl = url;
  if (!targetUrl.startsWith("http://") && !targetUrl.startsWith("https://")) {
    targetUrl = "https://" + targetUrl;
  }

  // 导航到 Webview 页面
  uni.navigateTo({
    url: `/pages/webview/webview?url=${encodeURIComponent(targetUrl)}`,
  });
};

// 页面加载
onLoad((options) => {
  const id = Number(options.id);
  if (id) {
    pageParams.value = { id };
    loadCompanyDetail(id);
    loadComments(id);
  }
});
</script>

<style lang="scss">
.detail-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: $navigation-bar-height;
  background: $background-color-white;
  display: flex;
  align-items: center;
  padding: 0 $padding-base;
  border-bottom: 1rpx solid $border-color-lighter;
  z-index: $z-index-base;

  .header-back {
    width: 60rpx;
    @extend .flex-center;
  }

  .header-title {
    flex: 1;
    text-align: center;
    font-size: $font-size-medium;
    font-weight: $font-weight-semibold;
    color: $text-primary;
    @extend .text-ellipsis;
    margin: 0 20rpx;
  }

  .header-actions {
    width: 60rpx;
    @extend .flex-center;

    .header-btn {
      color: $primary-color;
      font-size: $font-size-base;
      font-weight: $font-weight-medium;
    }
  }
}

.content-container {
  height: calc(100vh - #{$navigation-bar-height} - 120rpx);
  padding: $padding-base;
  padding-top: calc(#{$navigation-bar-height} + #{$padding-base});

  .section-container {
    background: $background-color-white;
    border-radius: $border-radius;
    padding: $padding-base;
    margin-bottom: $margin-base;
    box-shadow: $box-shadow-light;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: $margin-base;

      .add-comment-btn {
        display: flex;
        align-items: center;
        color: $primary-color;
        font-size: $font-size-small;

        .uni-icons {
          margin-right: 4rpx;
        }
      }
    }

    .section-title {
      font-size: $font-size-medium;
      font-weight: $font-weight-semibold;
      color: $text-primary;
      margin-bottom: $margin-base;
    }
  }
}

// 信息展示样式
.info-group {
  .info-item {
    display: flex;
    align-items: center;
    padding: 16rpx 0;
    border-bottom: 1rpx solid $border-color-lighter;

    &:last-child {
      border-bottom: none;
    }

    .info-label {
      width: 120rpx;
      font-size: $font-size-base;
      color: $text-secondary;
    }

    .info-value {
      flex: 1;
      font-size: $font-size-base;
      color: $text-primary;

      &.link {
        color: $primary-color;
        text-decoration: underline;
        cursor: pointer;
      }
    }
  }
}

// 表单样式
.form-group {
  .form-item {
    margin-bottom: $margin-small;

    &:last-child {
      margin-bottom: 0;
    }

    .form-label {
      font-size: $font-size-base;
      color: $text-primary;
      margin-bottom: $margin-mini;
      font-weight: $font-weight-medium;
    }

    .form-input {
      width: 100%;
      padding: 20rpx;
      border: 2rpx solid $border-color-lighter;
      border-radius: $border-radius;
      font-size: $font-size-base;
      color: $text-primary;
      background: $background-color;
      transition: all $transition-fast;

      &:focus {
        border-color: $primary-color;
        box-shadow: $input-focus-shadow;
      }
    }

    .form-textarea {
      width: 100%;
      min-height: 120rpx;
      padding: 20rpx;
      border: 2rpx solid $border-color-lighter;
      border-radius: $border-radius;
      font-size: $font-size-base;
      color: $text-primary;
      background: $background-color;
    }
  }
}

.form-textarea {
  .textarea-count {
    text-align: right;
    font-size: $font-size-extra-small;
    color: $text-secondary;
    margin-top: 8rpx;
  }
}

// 社交链接
.social-links {
  display: flex;
  gap: 20rpx;

  .social-item {
    display: flex;
    align-items: center;
    padding: 12rpx 20rpx;
    background: $background-color;
    border-radius: $border-radius;
    color: $text-primary;
    font-size: $font-size-small;
    cursor: pointer;

    .uni-icons {
      margin-right: 8rpx;
    }
  }
}

// 统计卡片
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20rpx;

  .stat-card {
    @extend .flex-center;
    flex-direction: column;
    padding: 20rpx;
    background: $background-color;
    border-radius: $border-radius;

    .stat-number {
      font-size: $font-size-large;
      font-weight: $font-weight-bold;
      color: $primary-color;
      margin-bottom: 4rpx;
    }

    .stat-label {
      font-size: $font-size-extra-small;
      color: $text-secondary;
    }
  }
}

// 时间信息
.time-info {
  .time-item {
    display: flex;
    align-items: center;
    margin-bottom: 12rpx;

    &:last-child {
      margin-bottom: 0;
    }

    .uni-icons {
      margin-right: 12rpx;
    }

    text {
      font-size: $font-size-small;
      color: $text-regular;
    }
  }
}

// 评论区域
.comment-input-container {
  margin-bottom: $margin-base;
  padding: $padding-base;
  background: $background-color;
  border-radius: $border-radius;

  .comment-textarea {
    width: 100%;
    min-height: 80rpx;
    font-size: $font-size-base;
    color: $text-primary;
  }

  .comment-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: $margin-mini;

    .textarea-count {
      font-size: $font-size-extra-small;
      color: $text-secondary;
    }

    .action-buttons {
      display: flex;
      gap: 20rpx;

      .action-btn {
        padding: 8rpx 20rpx;
        border-radius: $border-radius-small;
        font-size: $font-size-small;
        font-weight: $font-weight-medium;

        &.cancel {
          background: $background-color-white;
          color: $text-secondary;
          border: 1rpx solid $border-color-light;
        }

        &.submit {
          background: $primary-color;
          color: white;

          &.disabled {
            opacity: 0.6;
            cursor: not-allowed;
          }
        }
      }
    }
  }
}

.comment-list {
  .comment-item {
    padding: $padding-base 0;
    border-bottom: 1rpx solid $border-color-lighter;

    &:last-child {
      border-bottom: none;
    }

    .comment-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: $margin-mini;

      .comment-user {
        display: flex;
        align-items: center;

        .user-avatar {
          width: 60rpx;
          height: 60rpx;
          @extend .flex-center;
          background: $primary-color;
          color: white;
          border-radius: $border-radius-round;
          font-size: $font-size-medium;
          font-weight: $font-weight-semibold;
          margin-right: 12rpx;
        }

        .user-info {
          .user-name {
            font-size: $font-size-base;
            font-weight: $font-weight-medium;
            color: $text-primary;
            margin-bottom: 2rpx;
          }

          .comment-time {
            font-size: $font-size-extra-small;
            color: $text-secondary;
          }
        }
      }

      .comment-delete {
        @extend .flex-center;
        width: 40rpx;
        height: 40rpx;
        border-radius: $border-radius-round;
        background: $background-color;
        cursor: pointer;
      }
    }

    .comment-content {
      font-size: $font-size-base;
      color: $text-regular;
      line-height: 1.5;
    }
  }

  // 加载更多样式
  .load-more {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 40rpx 0;

    .loading-text {
      display: flex;
      align-items: center;
      color: $text-secondary;
      font-size: $font-size-small;

      .uni-icons {
        margin-right: 8rpx;
        animation: rotate 1s linear infinite;
      }
    }

    .load-more-btn {
      padding: 16rpx 40rpx;
      background: $background-color;
      border-radius: $border-radius;
      color: $text-primary;
      font-size: $font-size-small;
      cursor: pointer;
    }
  }

  .no-more {
    text-align: center;
    padding: 40rpx 0;
    color: $text-secondary;
    font-size: $font-size-small;
  }
}

@keyframes rotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.empty-comments {
  @extend .flex-center;
  flex-direction: column;
  padding: 60rpx 0;

  .empty-text {
    margin-top: 20rpx;
    font-size: $font-size-base;
    color: $empty-text-color;
  }
}

// 底部操作栏
.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 120rpx;
  background: $background-color-white;
  display: flex;
  align-items: center;
  padding: 0 $padding-base;
  border-top: 1rpx solid $border-color-lighter;
  z-index: $z-index-base;

  .follow-btn, .like-btn {
    flex: 1;
    @extend .flex-center;
    flex-direction: column;
    padding: 20rpx 0;
    cursor: pointer;

    &.followed {
      background: $danger-color;
      border-radius: $border-radius;
      color: white;
    }

    .uni-icons {
      margin-bottom: 4rpx;
    }

    text {
      font-size: $font-size-extra-small;
    }
  }

  .follow-btn {
    text {
      color: $primary-color;
    }

    &.followed text {
      color: white;
    }
  }

  .like-btn text {
    color: $warning-color;
  }

  .action-divider {
    width: 1rpx;
    height: 40rpx;
    background: $border-color-lighter;
  }
}

// 编辑模式保存按钮
.edit-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 120rpx;
  background: $background-color-white;
  display: flex;
  align-items: center;
  padding: $padding-base;
  border-top: 1rpx solid $border-color-lighter;

  .save-btn {
    flex: 1;
    @extend .flex-center;
    height: 80rpx;
    background: $button-primary-bg;
    color: white;
    border-radius: $border-radius;
    font-size: $font-size-medium;
    font-weight: $font-weight-semibold;
    cursor: pointer;
  }
}

// 响应式调整
@media (max-width: $screen-md) {
  .stats-grid {
    gap: $padding-small;
  }

  .action-bar, .edit-actions {
    height: 100rpx;
  }
}
</style>