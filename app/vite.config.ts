import { defineConfig, type UserConfig, type ConfigEnv, loadEnv } from "vite";
import uni from "@dcloudio/vite-plugin-uni";
import AutoImport from "unplugin-auto-import/vite";
import { resolve } from "path";

const pathResolve = (dir: string): string => {
  return resolve(process.cwd(), '.', dir);
};

export default defineConfig(async ({ mode }: ConfigEnv): Promise<UserConfig> => {
  // 加载环境变量
  const env = loadEnv(mode, process.cwd());
  
  // 获取环境变量，提供默认值
  const baseUrl = env.VITE_APP_BASE_URL || getDefaultBaseUrl(mode);
  const baseApi = env.VITE_APP_BASE_API || '/api';
  
  // 环境变量验证
  validateEnvVariables({ baseUrl, baseApi, mode });

  return {
    base: './',
    
    plugins: [
      AutoImport({
        imports: [
          "vue", 
          "uni-app",
          {
            from: 'pinia',
            imports: ['storeToRefs']
          }
        ],
        dts: "types/auto-imports.d.ts",
        eslintrc: {
          enabled: true,
        },
        vueTemplate: true,
        dirs: [
          './stores/**',
          './utils/**'
        ]
      }),
      uni()
    ],
    
    resolve: {
      alias: {
        '@': pathResolve(''),
        '~': pathResolve(''),
        '@/stores': pathResolve('stores'),
        '@/apis': pathResolve('apis'),
        '@/utils': pathResolve('utils'),
        '@/types': pathResolve('types'),
        '@/components': pathResolve('components'),
        '@/styles': pathResolve('styles'),
        '@/static': pathResolve('static'),
        '@/pages': pathResolve('pages')
      },
      extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue', '.uvue']
    },
    
    define: {
      'import.meta.env.VITE_APP_BASE_URL': JSON.stringify(baseUrl),
      'import.meta.env.VITE_APP_BASE_API': JSON.stringify(baseApi),
      'import.meta.env.VITE_APP_MODE': JSON.stringify(mode),
      'import.meta.env.VITE_APP_NODE_ENV': JSON.stringify(env.VITE_APP_NODE_ENV || mode),
    },
    
    server: {
      host: "0.0.0.0",
      port: 3000,
      open: false,
      cors: true,
      hmr: {
        overlay: true
      },
      proxy: baseApi ? {
        [baseApi]: {
          target: baseUrl,
          changeOrigin: true,
          secure: false,
          rewrite: (path) => path.replace(new RegExp(`^${baseApi}`), ""),
        },
      } : undefined,
    },
    
    build: {
      target: 'es2015',
      minify: 'terser',
      outDir: 'dist',
      assetsDir: 'static',
      terserOptions: {
        compress: {
          drop_console: mode === 'production',
          drop_debugger: mode === 'production'
        }
      }
    },
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: `
            @import "@/styles/variables.scss";
            @import "@/styles/mixins.scss";
          `
        }
      }
    }
  };
});

// 获取默认的 BASE_URL
function getDefaultBaseUrl(mode: string): string {
  switch (mode) {
    case 'development':
      return 'http://localhost:8080';
    case 'test':
      return 'https://test-api.boole.com';
    case 'production':
      return 'https://api.boole.com';
    default:
      return 'http://localhost:8080';
  }
}

// 环境变量验证
function validateEnvVariables({ baseUrl, baseApi, mode }: { baseUrl: string; baseApi: string; mode: string }) {
  if (!baseUrl) {
    console.warn(`⚠️  VITE_APP_BASE_URL 环境变量未设置，使用默认值: ${getDefaultBaseUrl(mode)}`);
  }
  
  if (!baseApi) {
    console.warn(`⚠️  VITE_APP_BASE_API 环境变量未设置，使用默认值: /api`);
  }
  
  if (mode === 'production' && baseUrl.includes('localhost')) {
    console.error('❌ 生产环境不能使用 localhost 作为 BASE_URL');
  }
}