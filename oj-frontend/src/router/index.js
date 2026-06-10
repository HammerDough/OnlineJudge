import { createRouter, createWebHistory } from 'vue-router'

import IndexView from '@/views/index/index.vue'
import LoginView from '@/views/login/index.vue'
import UsersView from '@/views/users/index.vue'
import ProblemsView from '@/views/problems/index.vue'
import LayoutView from '@/views/layout/index.vue'
import DescriptionView from '@/views/description/index.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: '',
      component: LayoutView,
      redirect: '/index',
      children: [
        {path: '/index', name: 'index', component: IndexView},
        {path: '/problems', name: 'problems', component: ProblemsView},
        {path: '/users', name: 'users', component: UsersView},
        {path: '/login', name: 'login', component: LoginView}
      ]
    },
    {path: '/description', name: 'description', component: DescriptionView}

  ],
})

export default router
