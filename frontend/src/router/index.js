import { createRouter, createWebHistory } from 'vue-router'
import Menurh from '@/views/rhumanos/menurh.vue'
import Incidencias from '@/views/rhumanos/incidencias.vue'
import Trabajadores from '@/views/rhumanos/trabajadores.vue'
import Reportes from '@/views/rhumanos/reportes.vue'

const routes = [
  { path: '/', redirect: '/rhumanos' },
  { path: '/rhumanos', name: 'menurh', component: Menurh },
  { path: '/rhumanos/incidencias', name: 'incidencias', component: Incidencias },
  { path: '/rhumanos/trabajadores', name: 'trabajadores', component: Trabajadores },
  { path: '/rhumanos/reportes', name: 'reportes', component: Reportes },
]

export default createRouter({
  history: createWebHistory(),
  routes,
})
