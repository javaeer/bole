<template>
  <view class="agreement-container">

    <!-- 内容区域 -->
    <scroll-view class="agreement-content" scroll-y>
      <view class="content-wrapper">
        <!-- 协议标题 -->
        <view class="agreement-title">
          <text class="title-main">隐私政策</text>
          <text class="title-sub">最后更新日期：{{ updateDate }}</text>
        </view>

        <!-- 协议正文 -->
        <view class="agreement-body">
          <!-- 引言 -->
          <view class="section">
            <text class="section-title">引言</text>
            <text class="section-text">
              欢迎使用"简历大师"。我们深知个人信息对您的重要性，并会尽全力保护您的个人信息安全可靠。我们致力于维持您对我们的信任，恪守以下原则，保护您的个人信息：权责一致原则、目的明确原则、选择同意原则、最少够用原则、确保安全原则、主体参与原则、公开透明原则等。同时，我们承诺，我们将按业界成熟的安全标准，采取相应的安全保护措施来保护您的个人信息。
            </text>
          </view>

          <!-- 信息收集 -->
          <view class="section">
            <text class="section-title">一、我们如何收集和使用您的个人信息</text>
            <view class="section-list">
              <text class="list-item">1.1 注册和登录：当您注册账号时，我们会收集您的手机号码、邮箱地址，用于创建账号和验证身份。</text>
              <text class="list-item">1.2 简历信息：您主动填写的个人信息、教育背景、工作经历等，仅用于生成和管理您的简历。</text>
              <text class="list-item">1.3 设备信息：为保障服务安全运行，我们会收集设备型号、操作系统版本等基本信息。</text>
              <text class="list-item">1.4 日志信息：当您使用我们的服务时，我们会自动收集您对我们服务的详细使用情况，作为服务日志保存。</text>
            </view>
          </view>

          <!-- 信息存储 -->
          <view class="section">
            <text class="section-title">二、我们如何存储和保护您的个人信息</text>
            <view class="section-list">
              <text class="list-item">2.1 存储期限：我们仅在实现本政策所述目的所必需的期限内保留您的个人信息。</text>
              <text class="list-item">2.2 存储地点：我们在中华人民共和国境内运营中收集和产生的个人信息，存储在境内。</text>
              <text class="list-item">2.3 安全措施：我们采用符合业界标准的安全防护措施，防止个人信息遭到未经授权访问、公开披露、使用、修改、损坏或丢失。</text>
              <text class="list-item">2.4 安全事件：在不幸发生个人信息安全事件后，我们将按照法律法规的要求，及时向您告知安全事件的基本情况和可能的影响、我们已采取或将要采取的处置措施等。</text>
            </view>
          </view>

          <!-- 信息共享 -->
          <view class="section">
            <text class="section-title">三、我们如何共享、转让、公开披露您的个人信息</text>
            <view class="section-list">
              <text class="list-item">3.1 共享：我们不会与任何公司、组织和个人分享您的个人信息，但以下情况除外：
                (1) 获得您的明确同意；
                (2) 根据法律法规规定，或按政府主管部门的强制性要求；
                (3) 为保护用户或公众的人身财产安全免遭侵害。
              </text>
              <text class="list-item">3.2 转让：我们不会将您的个人信息转让给任何公司、组织和个人。</text>
              <text class="list-item">3.3 公开披露：我们仅会在以下情况下，公开披露您的个人信息：
                (1) 获得您明确同意后；
                (2) 基于法律的披露。
              </text>
            </view>
          </view>

          <!-- 用户权利 -->
          <view class="section">
            <text class="section-title">四、您的权利</text>
            <view class="section-list">
              <text class="list-item">4.1 访问和更正：您有权访问、更正您的个人信息，法律法规规定的例外情况除外。</text>
              <text class="list-item">4.2 删除：您可以通过注销账号的方式删除您的个人信息。</text>
              <text class="list-item">4.3 撤回同意：您有权改变您授权同意的范围或撤回您的授权。</text>
              <text class="list-item">4.4 注销账号：您可以在应用内申请注销账号，我们将在15个工作日内处理。</text>
            </view>
          </view>

          <!-- 儿童保护 -->
          <view class="section">
            <text class="section-title">五、儿童个人信息保护</text>
            <text class="section-text">
              我们的产品、网站和服务主要面向成人。如果没有父母或监护人的同意，儿童不应创建自己的个人信息主体账户。对于经父母同意而收集儿童个人信息的情况，我们只会在受到法律允许、父母或监护人明确同意或者保护儿童所必要的情况下使用或公开披露此信息。
            </text>
          </view>

          <!-- 联系我们 -->
          <view class="section">
            <text class="section-title">六、联系我们</text>
            <view class="contact-info">
              <text class="contact-item">如果您对本隐私政策有任何疑问、意见或建议，请通过以下方式与我们联系：</text>
              <text class="contact-item">邮箱：privacy@resume-master.com</text>
              <text class="contact-item">电话：400-xxx-xxxx</text>
              <text class="contact-item">工作时间：周一至周五 9:00-18:00</text>
            </view>
          </view>

          <!-- 政策更新 -->
          <view class="section">
            <text class="section-title">七、本政策如何更新</text>
            <text class="section-text">
              我们的隐私政策可能变更。未经您明确同意，我们不会削减您按照本隐私政策所应享有的权利。我们会在本页面上发布对本政策所做的任何变更。对于重大变更，我们还会提供更为显著的通知。本政策所指的重大变更包括但不限于：
              (1) 我们的服务模式发生重大变化；
              (2) 个人信息共享、转让或公开披露的主要对象发生变化；
              (3) 您参与个人信息处理方面的权利及其行使方式发生重大变化；
              (4) 我们负责处理个人信息安全的责任部门、联系方式及投诉渠道发生变化。
            </text>
          </view>

          <!-- 生效日期 -->
          <view class="effective-date">
            <text class="date-text">本隐私政策自{{ effectiveDate }}起生效</text>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const updateDate = ref('2024年1月1日')
const effectiveDate = ref('2024年1月1日')

const goBack = () => {
  uni.navigateBack()
}
</script>

<style lang="scss" scoped>
.agreement-container {
  min-height: 100vh;
  background-color: $background-color;
  display: flex;
  flex-direction: column;
}

/* 内容区域 */
.agreement-content {
  flex: 1;
  height: calc(100vh - 120rpx);
}

.content-wrapper {
  padding: $padding-base;
  padding-bottom: $padding-base * 2;
}

/* 协议标题 */
.agreement-title {
  margin-bottom: $margin-base * 1.5;
  padding-bottom: $padding-base;
  border-bottom: 2rpx solid $border-color-lighter;

  .title-main {
    display: block;
    font-size: $font-size-extra-large;
    font-weight: $font-weight-bold;
    color: $text-primary;
    margin-bottom: $margin-mini;
    text-align: center;
  }

  .title-sub {
    display: block;
    font-size: $font-size-small;
    color: $text-secondary;
    text-align: center;
  }
}

/* 协议正文 */
.agreement-body {
  background-color: $background-color-white;
  border-radius: $border-radius;
  padding: $padding-base;
  box-shadow: $box-shadow;
}

/* 章节样式 */
.section {
  margin-bottom: $margin-base;

  &:last-child {
    margin-bottom: 0;
  }
}

.section-title {
  display: block;
  font-size: $font-size-medium;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $margin-small;
  line-height: 1.4;
}

.section-text {
  display: block;
  font-size: $font-size-base;
  color: $text-regular;
  line-height: 1.6;
  text-align: justify;
}

/* 列表样式 */
.section-list {
  display: flex;
  flex-direction: column;
  gap: $margin-mini;
}

.list-item {
  display: block;
  font-size: $font-size-base;
  color: $text-regular;
  line-height: 1.6;
  text-align: justify;
  padding-left: 20rpx;
  position: relative;

  &::before {
    content: "•";
    position: absolute;
    left: 0;
    color: $primary-color;
    font-weight: bold;
  }
}

/* 联系信息 */
.contact-info {
  display: flex;
  flex-direction: column;
  gap: $margin-mini;
}

.contact-item {
  display: block;
  font-size: $font-size-base;
  color: $text-regular;
  line-height: 1.6;
}

/* 生效日期 */
.effective-date {
  margin-top: $margin-base * 2;
  padding-top: $padding-base;
  border-top: 1rpx solid $border-color-lighter;
  text-align: center;

  .date-text {
    font-size: $font-size-small;
    color: $text-secondary;
    font-weight: $font-weight-medium;
  }
}
</style>