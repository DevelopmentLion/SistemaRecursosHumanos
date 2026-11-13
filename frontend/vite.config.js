import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vuetify from 'vite-plugin-vuetify'
import { fileURLToPath, URL } from 'node:url'

// URL del backend Grails (bootRun escucha en el puerto 8080)
const backendUrl = process.env.VITE_BACKEND_URL || 'http://localhost:8080'

export default defineConfig({
  plugins: [
    vue(),
    vuetify({ autoImport: true }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    port: 5173,
    proxy: {
      '/rhumanos': {
        target: backendUrl,
        changeOrigin: true,
      },
      '/catalogosRh': {
        target: backendUrl,
        changeOrigin: true,
      },
    },
  },
})
