<template>
  <div class="user-home">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="welcome-card">
          <h2>欢迎使用电动车租赁系统</h2>
          <p>您好，{{ userStore.username }}！当前账户为普通用户</p>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-icon available">
            <el-icon :size="40"><Van /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.availableCount }}</div>
            <div class="stat-label">可用车辆</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-icon using">
            <el-icon :size="40"><Van /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.usingCount }}</div>
            <div class="stat-label">使用中</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-icon order">
            <el-icon :size="40"><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.orderCount }}</div>
            <div class="stat-label">我的订单</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getAvailableVehicles } from '@/api/vehicle'
import { getMyOrders } from '@/api/order'
import { Van, Document } from '@element-plus/icons-vue'

const userStore = useUserStore()

const stats = ref({
  availableCount: 0,
  usingCount: 0,
  orderCount: 0
})

onMounted(async () => {
  try {
    const vehicleRes = await getAvailableVehicles()
    if (vehicleRes.data) {
      stats.value.availableCount = vehicleRes.data.length || 0
    }
  } catch (e) {
    console.error('获取车辆失败', e)
  }

  try {
    const orderRes = await getMyOrders()
    if (orderRes.data) {
      const orders = orderRes.data
      stats.value.orderCount = orders.length
      stats.value.usingCount = orders.filter(o => o.status === 0).length
    }
  } catch (e) {
    console.error('获取订单失败', e)
  }
})
</script>

<style scoped>
.user-home {
  padding: 20px;
}

.welcome-card {
  text-align: center;
  padding: 20px;
}

.welcome-card h2 {
  margin: 0 0 10px 0;
  color: #303133;
}

.welcome-card p {
  margin: 0;
  color: #606266;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
}

.stat-icon.available {
  background-color: #f0f9eb;
  color: #67c23a;
}

.stat-icon.using {
  background-color: #fef0f0;
  color: #f56c6c;
}

.stat-icon.order {
  background-color: #ecf5ff;
  color: #409eff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}
</style>