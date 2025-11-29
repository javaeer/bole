import type { App } from "vue";

import { setupDirective } from "@/directive";
export default {
  install(app: App<Element>) {
    // 自定义指令(directive)
    setupDirective(app);
  },
};
