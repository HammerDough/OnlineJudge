import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },

  server: {
    proxy: {
      // 匹配 /api 开头的请求
      '/api': {
        target: 'http://localhost:8080', // 后端服务地址
        changeOrigin: true, // 开启跨域
        rewrite: (path) => path.replace(/^\/api/, '') // 去掉请求路径中的 /api 前缀
      }
    }
  }
})
