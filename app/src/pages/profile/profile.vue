<template>
	<view class="page-container">
		<!-- 用户信息头部 -->
		<view class="user-header">
			<view class="user-avatar-section">
				<image :src="userInfo.avatar" class="user-avatar" mode="aspectFit" @click="handleEditAvatar" />
				<view class="user-info">
					<text class="user-name">{{ userInfo.name }}</text>
					<text class="user-title">{{ userInfo.title }}</text>
					<view class="user-stats">
						<view class="stat-item">
							<text class="stat-number">{{ userInfo.followers }}</text>
							<text class="stat-label">关注</text>
						</view>
						<view class="stat-item">
							<text class="stat-number">{{ userInfo.fans }}</text>
							<text class="stat-label">粉丝</text>
						</view>
						<view class="stat-item">
							<text class="stat-number">{{ userInfo.likes }}</text>
							<text class="stat-label">获赞</text>
						</view>
					</view>
				</view>
			</view>
			<button class="btn-edit" @click="handleEditProfile">编辑资料</button>
		</view>

		<!-- 简历管理 -->
		<view class="section">
			<view class="section-header">
				<text class="section-title">简历管理</text>
				<text class="section-more" @click="handleViewAllResumes">查看全部</text>
			</view>
			<view class="resume-stats">
				<view class="stat-card" @click="handleCreateResume">
					<text class="stat-icon">📝</text>
					<text class="stat-title">创建简历</text>
				</view>
				<view class="stat-card" @click="handleMyResumes">
					<text class="stat-number">{{ resumeStats.total }}</text>
					<text class="stat-title">我的简历</text>
				</view>
				<view class="stat-card" @click="handleViewedResumes">
					<text class="stat-number">{{ resumeStats.viewed }}</text>
					<text class="stat-title">被查看</text>
				</view>
				<view class="stat-card" @click="handleDownloadResumes">
					<text class="stat-number">{{ resumeStats.downloaded }}</text>
					<text class="stat-title">已下载</text>
				</view>
			</view>
		</view>

		<!-- 功能菜单 -->
		<view class="section">
			<view class="menu-list">
				<view class="menu-item" v-for="item in menuList" :key="item.id" @click="handleMenuClick(item)">
					<view class="menu-left">
						<text class="menu-icon">{{ item.icon }}</text>
						<text class="menu-text">{{ item.name }}</text>
					</view>
					<text class="menu-arrow">›</text>
				</view>
			</view>
		</view>

		<!-- 设置入口 -->
		<view class="section">
			<view class="menu-list">
				<view class="menu-item" @click="handleSettings">
					<view class="menu-left">
						<text class="menu-icon">⚙️</text>
						<text class="menu-text">设置</text>
					</view>
					<text class="menu-arrow">›</text>
				</view>
				<view class="menu-item" @click="handleFeedback">
					<view class="menu-left">
						<text class="menu-icon">💬</text>
						<text class="menu-text">意见反馈</text>
					</view>
					<text class="menu-arrow">›</text>
				</view>
				<view class="menu-item" @click="handleAbout">
					<view class="menu-left">
						<text class="menu-icon">ℹ️</text>
						<text class="menu-text">关于我们</text>
					</view>
					<text class="menu-arrow">›</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
	import { ref, onMounted } from 'vue'

	// 用户信息
	const userInfo = ref({
		name: '伯乐用户',
		title: '前端开发工程师',
		avatar: '/static/avatar/default-avatar.jpg',
		followers: 24,
		fans: 18,
		likes: 156
	})

	// 简历统计
	const resumeStats = ref({
		total: 3,
		viewed: 12,
		downloaded: 5
	})

	// 菜单列表
	const menuList = ref([
		{ id: 1, name: '我的收藏', icon: '❤️', path: '/pages/favorite/list' },
		{ id: 2, name: '浏览记录', icon: '👀', path: '/pages/history/list' },
		{ id: 3, name: '我的投递', icon: '📤', path: '/pages/application/list' },
		{ id: 4, name: '面试邀请', icon: '📅', path: '/pages/interview/list' },
		{ id: 5, name: '会员中心', icon: '👑', path: '/pages/vip/index' },
		{ id: 6, name: '我的钱包', icon: '💰', path: '/pages/wallet/index' }
	])

	// 事件处理
	const handleEditAvatar = () => {
		uni.chooseImage({
			count: 1,
			sizeType: ['compressed'],
			sourceType: ['album', 'camera'],
			success: (res) => {
				const tempFilePaths = res.tempFilePaths
				userInfo.value.avatar = tempFilePaths[0]
				uni.showToast({
					title: '头像更新成功',
					icon: 'success'
				})
			}
		})
	}

	const handleEditProfile = () => {
		uni.navigateTo({
			url: '/pages/profile/edit'
		})
	}

	const handleCreateResume = () => {
		uni.navigateTo({
			url: '/pages/resume/create'
		})
	}

	const handleMyResumes = () => {
		uni.navigateTo({
			url: '/pages/resume/list'
		})
	}

	const handleViewedResumes = () => {
		uni.navigateTo({
			url: '/pages/resume/viewed'
		})
	}

	const handleDownloadResumes = () => {
		uni.navigateTo({
			url: '/pages/resume/downloaded'
		})
	}

	const handleViewAllResumes = () => {
		uni.navigateTo({
			url: '/pages/resume/list'
		})
	}

	const handleMenuClick = (item : any) => {
		uni.navigateTo({
			url: item.path
		})
	}

	const handleSettings = () => {
		uni.navigateTo({
			url: '/pages/settings/index'
		})
	}

	const handleFeedback = () => {
		uni.navigateTo({
			url: '/pages/feedback/index'
		})
	}

	const handleAbout = () => {
		uni.navigateTo({
			url: '/pages/about/index'
		})
	}

	onMounted(() => {
		console.log('我的页面加载完成')
	})
</script>

<style scoped>
	.page-container {
		background-color: #f8f8f8;
		min-height: 100vh;
		padding-bottom: 50rpx;
	}

	.user-header {
		background: linear-gradient(135deg, #d4af37 0%, #f7ef8a 100%);
		padding: 60rpx 30rpx 40rpx;
		color: white;
		position: relative;
	}

	.user-avatar-section {
		display: flex;
		align-items: center;
		margin-bottom: 30rpx;
	}

	.user-avatar {
		width: 120rpx;
		height: 120rpx;
		border-radius: 50%;
		border: 4rpx solid white;
		margin-right: 30rpx;
	}

	.user-info {
		flex: 1;
	}

	.user-name {
		display: block;
		font-size: 36rpx;
		font-weight: bold;
		margin-bottom: 10rpx;
	}

	.user-title {
		display: block;
		font-size: 26rpx;
		opacity: 0.9;
		margin-bottom: 20rpx;
	}

	.user-stats {
		display: flex;
		gap: 40rpx;
	}

	.stat-item {
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.stat-number {
		font-size: 28rpx;
		font-weight: bold;
		margin-bottom: 5rpx;
	}

	.stat-label {
		font-size: 22rpx;
		opacity: 0.8;
	}

	.btn-edit {
		position: absolute;
		top: 60rpx;
		right: 30rpx;
		background: rgba(255, 255, 255, 0.2);
		color: white;
		border: 2rpx solid white;
		border-radius: 25rpx;
		padding: 12rpx 30rpx;
		font-size: 24rpx;
	}

	.section {
		background: white;
		margin: 30rpx;
		border-radius: 15rpx;
		padding: 30rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
	}

	.section-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 30rpx;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.section-more {
		font-size: 26rpx;
		color: #d4af37;
	}

	.resume-stats {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 20rpx;
	}

	.stat-card {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 30rpx 20rpx;
		background: #f8f8f8;
		border-radius: 15rpx;
		transition: all 0.3s;
	}

	.stat-card:active {
		background: #e8e8e8;
		transform: scale(0.95);
	}

	.stat-icon,
	.stat-number {
		font-size: 36rpx;
		font-weight: bold;
		margin-bottom: 15rpx;
		color: #d4af37;
	}

	.stat-title {
		font-size: 24rpx;
		color: #666;
	}

	.menu-list {
		display: flex;
		flex-direction: column;
	}

	.menu-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 30rpx 0;
		border-bottom: 1rpx solid #f0f0f0;
		transition: all 0.3s;
	}

	.menu-item:active {
		background: #f8f8f8;
	}

	.menu-item:last-child {
		border-bottom: none;
	}

	.menu-left {
		display: flex;
		align-items: center;
	}

	.menu-icon {
		font-size: 36rpx;
		margin-right: 25rpx;
		width: 40rpx;
		text-align: center;
	}

	.menu-text {
		font-size: 28rpx;
		color: #333;
	}

	.menu-arrow {
		font-size: 36rpx;
		color: #999;
	}
</style>