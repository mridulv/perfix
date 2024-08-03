import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  build: {
    rollupOptions: {
      external: ['echarts']
    }
  },
  server: {
      host: '0.0.0.0',
      port: 3000,
  }
})