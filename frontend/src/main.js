import { createApp } from 'vue'
import { createVuetify } from 'vuetify'
import { es } from 'vuetify/locale'
import 'vuetify/styles'
import '@mdi/font/css/materialdesignicons.css'
import axios from 'axios'
import Swal from 'sweetalert2'

import App from './App.vue'
import router from './router'
import { store } from './store'

const vuetify = createVuetify({
  locale: {
    locale: 'es',
    fallback: 'es',
    messages: { es },
  },
})

// Instancia de axios usada como this.$http en los componentes.
// Las peticiones a '/rhumanos' y '/catalogosRh' se redirigen al backend
// Grails mediante el proxy configurado en vite.config.js.
const http = axios.create({
  baseURL: '/',
  timeout: 30000,
})

// URL absoluta del backend, usada para abrir reportes/impresiones en pestaña nueva.
const backendUrl = import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080/'

const app = createApp(App)

app.use(vuetify)
app.use(router)

app.config.globalProperties.$http = http
app.config.globalProperties.$store = store
app.config.globalProperties.$swal = Swal
app.config.globalProperties.$baseUrl = backendUrl

app.mount('#app')
