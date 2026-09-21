import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  envPrefix: 'VUE_APP_',
  plugins: [vue()],
  server: { port: 5177 }
})
