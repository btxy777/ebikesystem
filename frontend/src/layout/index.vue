<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <div class="logo">电动车管理</div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item v-if="roleAsNumber === 3" index="/sysUser">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item v-if="roleAsNumber === 3" index="/vehicle">
          <el-icon><Van /></el-icon>
          <span>车辆管理</span>
        </el-menu-item>
        <el-menu-item v-if="roleAsNumber === 3" index="/order">
          <el-icon><Document /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/fault">
          <el-icon><Warning /></el-icon>
          <span>故障管理</span>
        </el-menu-item>
        <el-menu-item v-if="roleAsNumber === 2" index="/operatorFault">
          <el-icon><List /></el-icon>
          <span>运维接单查询</span>
        </el-menu-item>
        <el-menu-item v-if="roleAsNumber === 3" index="/operator">
          <el-icon><Tools /></el-icon>
          <span>运维人员</span>
        </el-menu-item>
        <el-menu-item v-if="roleAsNumber === 3" index="/station">
          <el-icon><Location /></el-icon>
          <span>站点管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="header-right">
          <span class="username">{{ userStore.username }}</span>
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
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessageBox } from 'element-plus'
import { Odometer, User, Van, Document, Warning, Tools, Location, List } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const roleAsNumber = computed(() => Number(userStore.role))

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  background-color: #2b3a4a;
}

.el-menu {
  border-right: none;
}

:deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
}

:deep(.el-menu-item.is-active) {
  background-color: #263445 !important;
}

.el-header {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  color: #606266;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
