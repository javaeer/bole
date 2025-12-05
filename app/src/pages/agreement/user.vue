<template>
  <view class="agreement-container">
    <!-- 内容区域 -->
    <scroll-view class="agreement-content" scroll-y>
      <view class="content-wrapper">
        <!-- 协议标题 -->
        <view class="agreement-title">
          <text class="title-main">用户服务协议</text>
          <text class="title-sub">请仔细阅读并理解本协议</text>
        </view>

        <!-- 协议正文 -->
        <view class="agreement-body">
          <!-- 协议接受 -->
          <view class="section">
            <text class="section-title">一、协议的接受</text>
            <text class="section-text">
              欢迎您使用"简历大师"服务。请您仔细阅读以下条款，如果您对本协议的任何条款表示异议，您可以选择不使用我们的服务。当您注册成功或开始使用我们的服务时，即表示您已经阅读、理解并同意接受本协议的所有条款。
            </text>
          </view>

          <!-- 服务内容 -->
          <view class="section">
            <text class="section-title">二、服务内容</text>
            <view class="section-list">
              <text class="list-item">2.1 我们为您提供在线简历制作、管理、导出等相关服务。</text>
              <text class="list-item">2.2 我们有权根据实际情况随时调整服务内容，并不需事先通知用户。</text>
              <text class="list-item">2.3 我们提供的服务可能存在内容更改、中断或终止的情况，我们对此不承担任何责任。</text>
              <text class="list-item">2.4 您理解并同意，我们仅提供相关的网络服务，除此之外与相关网络服务有关的设备及所需的费用均应由您自行承担。</text>
            </view>
          </view>

          <!-- 用户权利 -->
          <view class="section">
            <text class="section-title">三、用户权利与义务</text>
            <view class="section-list">
              <text class="list-item">3.1 您有权拥有自己在"简历大师"的用户名及密码，并有权使用自己的用户名及密码随时登录服务。</text>
              <text class="list-item">3.2 您不得以任何形式转让或授权他人使用自己的"简历大师"账号。</text>
              <text class="list-item">3.3 您应对通过您的账号进行的所有活动和事件负法律责任。</text>
              <text class="list-item">3.4 您保证在"简历大师"上发布的信息真实、准确、完整、有效，不侵犯任何第三方合法权益。</text>
              <text class="list-item">3.5 您承诺遵守所有适用的法律、法规、规章、条例以及具有法律约束力的规范性文件。</text>
            </view>
          </view>

          <!-- 内容规范 -->
          <view class="section">
            <text class="section-title">四、内容规范</text>
            <text class="section-text">
              您承诺不得在"简历大师"制作、复制、发布、传播含有下列内容的信息：
            </text>
            <view class="section-list">
              <text class="list-item">(1) 反对宪法所确定的基本原则的；</text>
              <text class="list-item">(2) 危害国家安全，泄露国家秘密，颠覆国家政权，破坏国家统一的；</text>
              <text class="list-item">(3) 损害国家荣誉和利益的；</text>
              <text class="list-item">(4) 煽动民族仇恨、民族歧视，破坏民族团结的；</text>
              <text class="list-item">(5) 破坏国家宗教政策，宣扬邪教和封建迷信的；</text>
              <text class="list-item">(6) 散布谣言，扰乱社会秩序，破坏社会稳定的；</text>
              <text class="list-item">(7) 散布淫秽、色情、赌博、暴力、凶杀、恐怖或者教唆犯罪的；</text>
              <text class="list-item">(8) 侮辱或者诽谤他人，侵害他人合法权益的；</text>
              <text class="list-item">(9) 含有法律、行政法规禁止的其他内容的。</text>
            </view>
          </view>

          <!-- 知识产权 -->
          <view class="section">
            <text class="section-title">五、知识产权</text>
            <view class="section-list">
              <text class="list-item">5.1 "简历大师"提供的网络服务中包含的任何文本、图片、图形、音频和/或视频资料均受版权、商标和/或其它财产所有权法律的保护。</text>
              <text class="list-item">5.2 未经相关权利人同意，上述资料均不得在任何媒体直接或间接发布、播放、出于播放或发布目的而改写或再发行。</text>
              <text class="list-item">5.3 "简历大师"为提供服务而使用的任何软件的一切权利均属于该软件的著作权人。</text>
              <text class="list-item">5.4 您对自己上传的内容保留所有权，但授予我们全球性的、免费的、非独占的许可，以便我们在提供、维护和改进服务时使用这些内容。</text>
            </view>
          </view>

          <!-- 免责声明 -->
          <view class="section">
            <text class="section-title">六、免责声明</text>
            <view class="section-list">
              <text class="list-item">6.1 用户明确同意其使用"简历大师"服务所存在的风险将完全由其自己承担。</text>
              <text class="list-item">6.2 我们不保证服务一定能满足用户的要求，也不保证服务不会中断，对服务的及时性、安全性、准确性也都不作保证。</text>
              <text class="list-item">6.3 对于因不可抗力或我们不能控制的原因造成的网络服务中断或其他缺陷，我们不承担任何责任。</text>
              <text class="list-item">6.4 我们不对用户所发布信息的删除或储存失败负责。</text>
            </view>
          </view>

          <!-- 协议修改 -->
          <view class="section">
            <text class="section-title">七、协议修改</text>
            <text class="section-text">
              我们有权随时修改本协议的任何条款，一旦本协议的内容发生变动，我们将会直接在网站上公布修改之后的协议内容，该公布行为视为我们已经通知用户修改内容。如果您不同意我们对本协议相关条款所做的修改，您有权停止使用我们的服务。如果您继续使用我们的服务，则视为您接受我们对本协议相关条款所做的修改。
            </text>
          </view>

          <!-- 法律管辖 -->
          <view class="section">
            <text class="section-title">八、法律管辖</text>
            <text class="section-text">
              本协议的订立、执行和解释及争议的解决均应适用中华人民共和国法律。如双方就本协议内容或其执行发生任何争议，双方应尽量友好协商解决；协商不成时，任何一方均可向我们所在地的人民法院提起诉讼。
            </text>
          </view>

          <!-- 协议生效 -->
          <view class="effective-date">
            <text class="date-text">本协议自您同意并注册成功之日起生效</text>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
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
.section-list {
  .list-item {
    &::before {
      color: $primary-color; /* 使用主色调 */
    }
  }
}
</style>