/// <reference types="vite/client" />

// 环境变量类型定义
interface ImportMetaEnv {
  readonly VITE_APP_BASE_URL: string
  readonly VITE_APP_BASE_API: string
  readonly VITE_APP_TITLE: string
  readonly VITE_APP_MODE: string
  readonly VITE_APP_BUILD_TIME: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

// UniApp 类型扩展
declare module '*.uvue' {
  import { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}