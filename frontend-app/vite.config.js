import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  // server: {
  //   proxy: {
  //     "/api": {
  //       target: "https://15rydlq8ge.execute-api.ap-southeast-1.amazonaws.com",
  //       changeOrigin: true,
  //       secure: false,
  //     },
  //   },
  // },
})