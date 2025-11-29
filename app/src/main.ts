import { createSSRApp } from "vue";
import App from "./App.vue";
import setupPlugins from "@/plugins";
import { createPinia } from "pinia";


export function createApp() {
  const app = createSSRApp(App);
  const pinia = createPinia();

// 先安装 Pinia
  app.use(pinia);
  app.use(setupPlugins);
  return {
    app,
  };
}
