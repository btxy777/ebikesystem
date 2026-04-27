<template>
  <div class="operator-home">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="welcome-card">
          <h2>运维人员工作台</h2>
          <p>您好，{{ userStore.username }}！当前待维护车辆 {{ stats.pendingCount }} 辆</p>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="8">
        <el-card class="stat-card" @click="goToFaultList('fault')">
          <div class="stat-icon fault">
            <el-icon :size="40"><Warning /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.faultCount }}</div>
            <div class="stat-label">故障车辆</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card" @click="goToFaultList('lowBattery')">
          <div class="stat-icon low-battery">
            <el-icon :size="40"><Tools /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.lowBatteryCount }}</div>
            <div class="stat-label">低电量车辆</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-icon pending">
            <el-icon :size="40"><Tools /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingCount }}</div>
            <div class="stat-label">待维护总计</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { Warning, Tools } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()

const stats = ref({
  faultCount: 0,
  lowBatteryCount: 0,
  pendingCount: 0
})

const fetchStats = async () => {
  try {
    const res = await request.get('/api/vehicles/maintenance-stats')
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const goToFaultList = (type) => {
  if (type === 'fault') {
    router.push({ path: '/fault', query: { type: 'fault' } })
  } else {
    router.push({ path: '/fault', query: { type: 'lowBattery' } })
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.operator-home {
  padding: 20px;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.welcome-card h2 {
  margin: 0 0 10px 0;
}

.welcome-card p {
  margin: 0;
  opacity: 0.9;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
}

.stat-icon.fault {
  background-color: #fef0f0;
  color: #f56c6c;
}

.stat-icon.low-battery {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.stat-icon.pending {
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
