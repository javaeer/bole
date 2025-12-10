<template>
	<view class="page-container">
		<!-- 顶部品牌栏 -->
		<view class="brand-header">
			<view class="brand-info">
				<image src="/static/logo.png" class="logo" mode="aspectFit" />
				<text class="brand-name">{{ systemName }} 版本: {{ systemVersion }}</text>
			</view>
			<view class="search-btn" @click="handleSearch">
				<text class="icon-search">🔍</text>
			</view>
		</view>

		<!-- 加载状态 -->
		<view v-if="configStore.loading" class="loading">
			配置加载中...
		</view>

		<!-- 轮播图 -->
		<swiper class="banner-swiper" :indicator-dots="true" :autoplay="true" :interval="3000" :duration="500">
			<swiper-item v-for="(item, index) in bannerList" :key="index">
				<image :src="item.image" class="banner-image" mode="aspectFill" @click="handleBannerClick(item)" />
			</swiper-item>
		</swiper>

		<!-- 功能入口 -->
		<view class="function-grid">
			<view class="grid-item" v-for="item in functionList" :key="item.id" @click="handleFunctionClick(item)">
				<view class="grid-icon">
					<text class="icon">{{ item.icon }}</text>
				</view>
				<text class="grid-text">{{ item.name }}</text>
			</view>
		</view>

		<!-- 推荐模板 -->
		<view class="section">
			<view class="section-header">
				<text class="section-title">热门简历模板</text>
				<text class="section-more" @click="handleMoreTemplates">查看更多</text>
			</view>
			<scroll-view class="template-scroll" scroll-x="true">
				<view class="template-list">
					<view class="template-item" v-for="template in templateList" :key="template.id"
						@click="handleTemplateClick(template)">
						<image :src="template.cover" class="template-cover" mode="aspectFill" />
						<view class="template-info">
							<text class="template-name">{{ template.name }}</text>
							<text class="template-desc">{{ template.description }}</text>
							<view class="template-meta">
								<text class="template-price" v-if="template.price > 0">¥{{ template.price }}</text>
								<text class="template-free" v-else>免费</text>
								<text class="template-users">{{ template.users }}人使用</text>
							</view>
						</view>
					</view>
				</view>
			</scroll-view>
		</view>

		<!-- 使用指南 -->
		<view class="section">
			<view class="section-header">
				<text class="section-title">使用指南</text>
			</view>
			<view class="guide-list">
				<view class="guide-item" v-for="(guide, index) in guideList" :key="index">
					<text class="guide-number">{{ index + 1 }}</text>
					<view class="guide-content">
						<text class="guide-title">{{ guide.title }}</text>
						<text class="guide-desc">{{ guide.description }}</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
	import { ref, onMounted, computed } from 'vue'

	import { useConfigStore } from '@/stores/config';

	const configStore = useConfigStore();

	// 使用计算属性获取配置值
	const systemName = computed(() =>
		configStore.getConfigValue('system.name') || '默认系统名称'
	);

	const systemVersion = computed(() =>
		configStore.getConfigValue('system.version') || '1.0.0'
	);

	// 轮播图数据
	const bannerList = ref([
		{
			id: 1,
			image: '/static/banner/banner1.jpg',
			link: '/pages/template/list'
		},
		{
			id: 2,
			image: '/static/banner/banner2.jpg',
			link: '/pages/guide/detail'
		},
		{
			id: 3,
			image: '/static/banner/banner3.jpg',
			link: '/pages/activity/detail'
		}
	])

	// 功能列表
	const functionList = ref([
		{ id: 1, name: '创建简历', icon: '📝', path: '/pages/resume/create' },
		{ id: 2, name: '模板中心', icon: '🎨', path: '/pages/template/list' },
		{ id: 3, name: '简历分析', icon: '📊', path: '/pages/analysis/analysis' },
		{ id: 4, name: '求职指南', icon: '📚', path: '/pages/guide/guide' }
	])

	// 模板列表
	const templateList = ref([
		{
			id: 1,
			name: '经典简约',
			description: '适合应届毕业生',
			cover: '/static/template/classic.jpg',
			price: 0,
			users: 12543
		},
		{
			id: 2,
			name: '专业商务',
			description: '适合职场人士',
			cover: '/static/template/business.jpg',
			price: 9.9,
			users: 8765
		},
		{
			id: 3,
			name: '创意设计',
			description: '适合设计岗位',
			cover: '/static/template/creative.jpg',
			price: 19.9,
			users: 5432
		}
	])

	// 指南列表
	const guideList = ref([
		{
			title: '选择模板',
			description: '从海量模板中选择适合您的简历样式'
		},
		{
			title: '填写信息',
			description: '按照指引填写您的个人信息和工作经历'
		},
		{
			title: '生成简历',
			description: '一键生成专业简历，支持多种格式导出'
		},
		{
			title: '分享导出',
			description: '将您的简历分享或导出'
		}
	])

	// 事件处理
	const handleSearch = () => {
		uni.navigateTo({
			url: '/pages/search/search' // 你的搜索页面路径
		});
	};

	const handleBannerClick = (item : any) => {
		uni.navigateTo({
			url: item.link
		})
	}

	const handleFunctionClick = (item : any) => {
		uni.navigateTo({
			url: item.path
		})
	}

	const handleTemplateClick = (template : any) => {
		uni.navigateTo({
			url: `/pages/template/detail?id=${template.id}`
		})
	}

	const handleMoreTemplates = () => {
		uni.navigateTo({
			url: '/pages/template/list'
		})
	}

	onMounted(() => {
		console.log('首页加载完成')
	})
</script>

<style lang="scss" scoped>
	.page-container {
		background-color: $background-color;
		min-height: 100vh;
	}

	.brand-header {
		padding: $padding-small $padding-base;
		background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.brand-info {
		display: flex;
		align-items: center;
	}

	.logo {
		width: 60rpx;
		height: 60rpx;
		margin-right: $margin-small;
		border-radius: $border-radius-small;
	}

	.brand-name {
		font-size: $font-size-large;
		font-weight: $font-weight-bold;
		color: $background-color-white;
	}

	.search-box {
		flex: 1;
		max-width: 400rpx;
		background: rgba($background-color-white, 0.9);
		border-radius: $border-radius-small;
		padding: $padding-mini $padding-small;
		display: flex;
		align-items: center;
		margin-left: $margin-base;
	}

	.icon-search {
		margin-right: $margin-mini;
		font-size: $font-size-base;
		color: $text-secondary;
	}

	.search-text {
		color: $text-placeholder;
		font-size: $font-size-base;
	}

	.loading {
		text-align: center;
		padding: $padding-base;
		color: $text-secondary;
		font-size: $font-size-base;
	}

	.banner-swiper {
		height: 300rpx;
		margin: $margin-small $margin-base;
		border-radius: $border-radius * 1.5;
		overflow: hidden;
		box-shadow: $box-shadow;
	}

	.banner-image {
		width: 100%;
		height: 100%;
	}

	.function-grid {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		padding: $padding-base $margin-base;
		background: $background-color-white;
		margin: $margin-small $margin-base;
		border-radius: $border-radius * 1.5;
		box-shadow: $box-shadow;
		gap: $margin-base;
	}

	.grid-item {
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.grid-icon {
		width: 80rpx;
		height: 80rpx;
		background: $background-color;
		border-radius: $border-radius-round;
		display: flex;
		justify-content: center;
		align-items: center;
		margin-bottom: $margin-mini;
	}

	.grid-icon .icon {
		font-size: $font-size-medium;
	}

	.grid-text {
		font-size: $font-size-small;
		color: $text-regular;
		text-align: center;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		max-width: 100%;
	}

	.section {
		background: $background-color-white;
		margin: $margin-base;
		border-radius: $border-radius * 1.5;
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

	.section-more {
		font-size: $font-size-small;
		color: $primary-color;
	}

	.template-scroll {
		white-space: nowrap;
	}

	.template-list {
		display: inline-flex;
	}

	.template-item {
		width: 300rpx;
		margin-right: $margin-small;
		background: $background-color;
		border-radius: $border-radius;
		overflow: hidden;
		flex-shrink: 0;
	}

	.template-cover {
		width: 100%;
		height: 200rpx;
	}

	.template-info {
		padding: $padding-small;
	}

	.template-name {
		display: block;
		font-size: $font-size-base;
		font-weight: $font-weight-bold;
		margin-bottom: 5rpx;
		color: $text-primary;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.template-desc {
		display: block;
		font-size: $font-size-small;
		color: $text-secondary;
		margin-bottom: $margin-mini;
	}

	.template-meta {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.template-price {
		color: $danger-color;
		font-weight: $font-weight-bold;
		font-size: $font-size-base;
	}

	.template-free {
		color: $primary-color;
		font-weight: $font-weight-bold;
		font-size: $font-size-base;
	}

	.template-users {
		font-size: $font-size-extra-small;
		color: $text-placeholder;
	}

	.guide-list {
		display: flex;
		flex-direction: column;
	}

	.guide-item {
		display: flex;
		align-items: flex-start;
		padding: $padding-small 0;
		border-bottom: 1rpx solid $border-color-extra-light;
	}

	.guide-item:last-child {
		border-bottom: none;
	}

	.guide-number {
		width: 50rpx;
		height: 50rpx;
		background: $primary-color;
		color: $background-color-white;
		border-radius: $border-radius-round;
		display: flex;
		justify-content: center;
		align-items: center;
		font-size: $font-size-small;
		margin-right: $padding-small;
		flex-shrink: 0;
	}

	.guide-content {
		flex: 1;
	}

	.guide-title {
		display: block;
		font-size: $font-size-base;
		font-weight: $font-weight-bold;
		margin-bottom: 5rpx;
		color: $text-primary;
	}

	.guide-desc {
		display: block;
		font-size: $font-size-small;
		color: $text-secondary;
	}
</style>