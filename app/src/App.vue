<script setup lang="ts">
import { onHide, onLaunch, onShow } from "@dcloudio/uni-app";
import { configHandler } from "@/utils/config";
import { dictHandler } from "@/utils/dict";

onLaunch(() => {
  console.log("App Launch");

  //提示：onLaunch 是应用初始化完成时触发，全局只触发一次，非常适合进行应用级别的初始化操作。
  // 初始化系统配置

  /*configHandler.initConfigHandling()
    .then(() => {
      console.log("✅ 配置初始化完成，开始加载字典数据");
    })
    .then(() => {
      console.log("🛡️ 加载系统名称",configHandler.getConfigValue("system.name"));
    })
    .catch((error) => {
      console.error("❌ 配置初始化失败:", error);
    });

  dictHandler.initDictHandling()
    .then(() => {
      console.log("✅ 字典初始化完成。");
    })
    .then(() => {
      console.log("📊 加载的字典类型", dictHandler.getAllDictTypes());
    })
    .catch((error) => {
      console.error("❌ 字典初始化失败:", error);
    });*/

  Promise.all([
    configHandler.initConfigHandling(),
    dictHandler.initDictHandling()
  ]).then(() => {
    console.log("✅ 所有初始化完成");
    console.log("🛡️ 加载系统名称",configHandler.getConfigValue("system.name"));
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
