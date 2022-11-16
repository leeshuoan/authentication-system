import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import UserLanding from '../views/UserLanding.vue'
import AdminLanding from '../views/AdminLanding.vue'
import Profile from '../views/Profile.vue'
import SuperAdminLanding from '../views/SuperAdminLanding.vue'
import ErrorPage from '../views/ErrorPage.vue'
import Redirect from '../views/Redirect.vue'

const routes = [
  { path: '/', name: Login, component: Login },
  { path: '/register', name: Register, component: Register },
  {
    path: '/home', name: UserLanding, component: UserLanding,
    beforeEnter: (to, from, next) => {
      let userData = sessionStorage.getItem("user")
      if (userData == null) {
        next('/')
      }
      next();
    },
  },
  {
    path: '/profile', name: Profile, component: Profile, 
    beforeEnter: (to, from, next) => {
      let userData = sessionStorage.getItem("user")
      if (userData == null) {
        next('/')
      }
      next();
    },
  },
  {
    path: '/admin', name: AdminLanding, component: AdminLanding,
    beforeEnter: (to, from, next) => {
      let userData = sessionStorage.getItem("user")
      if (userData == null) {
        next('/')
      }
      userData = JSON.parse(userData)
      let roles = userData.roles
      // hardcoded admin  users for SSO check
      if (userData.email) {
        if (userData.email == "pagac_vince@yost.io") {
          next();
        } else {
          next('/');
        }
      }
      let superadmin = roles.find(o => o.name === 'ROLE_SUPER_ADMIN');
      let admin = roles.find(o => o.name === 'ROLE_ADMIN');
      if (superadmin == null && admin == null) {
        next('/')
      }
      next();
    },
  },
  {
    path: '/superadmin', name: SuperAdminLanding, component: SuperAdminLanding,
    beforeEnter: (to, from, next) => {
      let userData = sessionStorage.getItem("user")
      if (userData == null) {
        next('/')
      }
      userData = JSON.parse(userData)
      let roles = userData.roles
      // hardcoded  superadmin users for SSO check
      if (userData.email) {
        if (userData.email == "russel.stephan@kihn.name" || userData.email == "admin@example.com") {
          next();
        } else {
          next('/');
        }
      }
      let superadmin = roles.find(o => o.name === 'ROLE_SUPER_ADMIN');
      let admin = roles.find(o => o.name === 'ROLE_ADMIN');
      if (superadmin == null) {
        next('/')
      }
      next();
    },
  },
  { path: '/error', name: ErrorPage, component: ErrorPage },
  { path: '/sso/auth/callback', name: Redirect, component: Redirect },
]

const router = createRouter({
  routes,
  history: createWebHistory()
}) 

export default router