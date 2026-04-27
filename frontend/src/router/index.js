import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', roles: [] }
  },
  {
    path: '/user',
    component: () => import('@/layout/UserLayout.vue'),
    meta: { title: '用户端', roles: [1] },
    children: [
      {
        path: 'home',
        name: 'UserHome',
        component: () => import('@/views/user/home/index.vue'),
        meta: { title: '首页', roles: [1] }
      },
      {
        path: 'vehicles',
        name: 'UserVehicles',
        component: () => import('@/views/user/vehicles/index.vue'),
        meta: { title: '车辆', roles: [1] }
      },
      {
        path: 'orders',
        name: 'UserOrders',
        component: () => import('@/views/user/orders/index.vue'),
        meta: { title: '我的订单', roles: [1] }
      },
      {
        path: 'fault',
        name: 'UserFault',
        component: () => import('@/views/user/fault/index.vue'),
        meta: { title: '故障上报', roles: [1] }
      }
    ]
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', roles: [2, 3] }
      },
      {
        path: '/sysUser',
        name: 'SysUser',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理', roles: [3] }
      },
      {
        path: '/vehicle',
        name: 'Vehicle',
        component: () => import('@/views/vehicle/index.vue'),
        meta: { title: '车辆管理', roles: [3] }
      },
      {
        path: '/order',
        name: 'Order',
        component: () => import('@/views/order/index.vue'),
        meta: { title: '订单管理', roles: [3] }
      },
      {
        path: '/fault',
        name: 'Fault',
        component: () => import('@/views/fault/index.vue'),
        meta: { title: '故障管理', roles: [2, 3] }
      },
      {
        path: '/operatorHome',
        name: 'OperatorHome',
        component: () => import('@/views/operatorHome/index.vue'),
        meta: { title: '运维首页', roles: [2] }
      },
      {
        path: '/operatorFault',
        name: 'OperatorFault',
        component: () => import('@/views/operatorFault/index.vue'),
        meta: { title: '运维接单查询', roles: [2] }
      },
      {
        path: '/operator',
        name: 'Operator',
        component: () => import('@/views/operator/index.vue'),
        meta: { title: '运维人员管理', roles: [3] }
      },
      {
        path: '/station',
        name: 'Station',
        component: () => import('@/views/station/index.vue'),
        meta: { title: '站点管理', roles: [3] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')
  const userRole = role ? Number(role) : null

  console.log('Router check - path:', to.path, 'userRole:', userRole, 'stored role:', role)

  if (to.path !== '/login') {
    if (!token) {
      next('/login')
      return
    }

    const allowedRoles = to.meta.roles || []
    if (allowedRoles.length > 0 && userRole !== null && !allowedRoles.includes(userRole)) {
      ElMessage.warning('您没有权限访问该页面')
      if (userRole === 1) {
        next('/user/home')
      } else if (userRole === 2) {
        next('/operatorHome')
      } else {
        next('/dashboard')
      }
      return
    }
  } else {
    if (token) {
      if (userRole === 1) {
        next('/user/home')
      } else if (userRole === 2) {
        next('/operatorHome')
      } else {
        next('/dashboard')
      }
      return
    }
  }

  next()
})

export default router