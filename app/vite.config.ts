import { type ConfigEnv, defineConfig, loadEnv, type UserConfig } from "vite";
import uni from "@dcloudio/vite-plugin-uni";
import AutoImport from "unplugin-auto-import/vite";

export default defineConfig(async ({ mode }: ConfigEnv): Promise<UserConfig> => {

  let env = undefined;
  if(!mode){//这里是为了确保mode有值，假如开发人员直接在开发工具选择编译到浏览器，也能兼容到
    if(process.env.NODE_ENV=='production'){
      mode = 'production'
    }else{
      mode = 'development'
    }
  }
  switch (process.env.UNI_SCRIPT){//这个就是第三步配置的uni-app的script,一一对应判断
    case 'dev-h5':
      mode = 'development';
      break;
    case 'test-h5':
      mode = 'test'
      break;
    case 'prod-h5':
      mode = 'production'
      break;
  }

  env = loadEnv(mode, process.cwd());
  console.log(`编译${mode}环境`)
  console.log(env);//此时已经把env配置读取到了，并且这是个json格式，
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
        imports: ["vue", "pinia"], // 自动导入 Vue 和 Pinia 的相关 API
        dts: "src/auto-imports.d.ts", // 生成类型声明文件
        eslintrc: {
          enabled: true, // 生成 ESLint 配置
          filepath: "./.eslintrc-auto-import.json",
        },
      }),
    ],
    define: {//根据vite的官方文档，可以把define定义的变量名，在项目编译时，识别项目文件中的这个变量名直接替换成env配置，我们把这个变量放到config.js文件中，对env配置进行集中管理
      __VITE_ENV__:JSON.stringify(env)
    },
  };
});
