import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// The proxy sends any /api/... request from React (port 5173)
// to Spring Boot (port 8080), so we never hard-code the backend URL.
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})