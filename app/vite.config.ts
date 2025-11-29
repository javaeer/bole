import { defineConfig, type UserConfig, type ConfigEnv, loadEnv } from "vite";
import uni from "@dcloudio/vite-plugin-uni";
import AutoImport from "unplugin-auto-import/vite";

export default defineConfig(async ({ mode }: ConfigEnv): Promise<UserConfig> => {
  const env = loadEnv(mode, process.cwd());

  return {
    server: {
      host: "0.0.0.0",
      port: +env.VITE_APP_PORT,
      open: false,
      // 代理配置只在 H5（浏览器）开发时生效。 其他平台（如小程序、App）在开发时不使用 Vite 的开发服务器，它们直接运行在各自的环境中。
      proxy: {
        [env.VITE_APP_BASE_API]: {
          changeOrigin: true,
          target: env.VITE_APP_API_URL,
          rewrite: (path) => path.replace(new RegExp("^" + env.VITE_APP_BASE_API), ""),
        },
      },
    },
    plugins: [
      uni(),
	  AutoImport({ // 配置 unplugin-auto-import
	        imports: ['vue', 'pinia'], // 自动导入 Vue 和 Pinia 的相关 API
	        dts: 'src/auto-imports.d.ts', // 生成类型声明文件
	        eslintrc: {
	          enabled: true, // 生成 ESLint 配置
	          filepath: './.eslintrc-auto-import.json'
	        },
	      }),
    ],
  };
});
