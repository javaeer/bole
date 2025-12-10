<script setup lang="ts">
import { onHide, onLaunch, onShow } from "@dcloudio/uni-app";
import { configHandler } from "@/utils/config";
import { dictHandler } from "@/utils/dict";
import { checkLaunchPage, setupGlobalRouterGuard } from "@/utils/router-guard";

onLaunch(() => {
  console.log("App Launch");

  //提示：onLaunch 是应用初始化完成时触发，全局只触发一次，非常适合进行应用级别的初始化操作。

  // 设置全局路由守卫（App/H5生效，微信小程序只能通过页面级useAuthGuard实现）
  setupGlobalRouterGuard();

  // 检查启动页面
  checkLaunchPage();

  // 初始化系统配置
  Promise.all([
    configHandler.initConfigHandling(),
    dictHandler.initDictHandling(),
  ]).then(() => {
    console.log("✅ 所有初始化完成");
    console.log("🛡️ 加载系统名称", configHandler.getConfigValue("system.name"));
    console.log("📊 加载的字典类型", dictHandler.getAllDictTypes());
  }).catch((error) => {
    console.error("❌ 初始化失败:", error);
  });


});
onShow(() => {
  console.log("App Show");
});
onHide(() => {
  console.log("App Hide");
});
</script>
<style lang="scss">
page {
  background: #f8f8f8;
}
</style>
