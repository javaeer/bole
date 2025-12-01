<script setup lang="ts">
import { onHide, onLaunch, onShow } from "@dcloudio/uni-app";
import { configHandler } from "@/utils/config";

onLaunch(() => {
  console.log("App Launch");

  // 初始化系统配置
  configHandler.initConfigHandling()
    .then(() => {
      console.log("✅ 配置初始化完成，可以继续其他启动逻辑");
    })
    .catch((error) => {
      console.error("❌ 配置初始化失败:", error);
    });

  /*//提示：onLaunch 是应用初始化完成时触发，全局只触发一次，非常适合进行应用级别的初始化操作。
  // 使用 Promise 的 then/catch 替代 await
  const configStore = useSystemConfigStore();

  // 先尝试从本地存储加载配置
  configStore.loadConfigFromStorage();

  // 然后从服务器获取最新配置
  configStore.fetchSystemConfig()
    .then(() => {
      console.log('应用配置已加载:', configStore.systemConfig);
      const systemName = configStore.getConfigValue('system.name') || 'bole';
      console.log('设置应用主题:', systemName);
    })
    .catch(error => {
      console.error('启动时加载配置失败:', error);
    });*/
});
onShow(() => {
  console.log("App Hide");
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
