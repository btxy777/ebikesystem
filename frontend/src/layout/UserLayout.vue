<template>
  <el-container class="user-layout">
    <el-aside width="200px">
      <div class="logo">用户端</div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#545c64"
        text-color="#fff"
        active-text-color="#ffd04b"
      >
        <el-menu-item index="/user/home" @click="handleMenuClick('/user/home')">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/user/vehicles" @click="handleMenuClick('/user/vehicles')">
          <el-icon><Van /></el-icon>
          <span>车辆</span>
        </el-menu-item>
        <el-menu-item index="/user/orders" @click="handleMenuClick('/user/orders')">
          <el-icon><Document /></el-icon>
          <span>我的订单</span>
        </el-menu-item>
        <el-menu-item index="/user/fault" @click="handleMenuClick('/user/fault')">
          <el-icon><Warning /></el-icon>
          <span>故障上报</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="header-content">
          <span class="welcome">欢迎，{{ userStore.username }}</span>
          <el-button type="danger" size="small" @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { HomeFilled, Van, Document, Warning } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const handleMenuClick = (path) => {
  console.log('Menu clicked:', path)
  router.push(path)
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.user-layout {
  height: 100vh;
}

.el-aside {
  background-color: #545c64;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  background-color: #434a50;
}

.el-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome {
  font-size: 16px;
  color: #333;
}

.el-main {
  background-color: #f5f5f5;
  padding: 20px;
}
</style>