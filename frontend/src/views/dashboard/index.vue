<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background-color: #409EFF;">
              <el-icon :size="40"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.userCount }}</div>
              <div class="stat-label">用户数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background-color: #67C23A;">
              <el-icon :size="40"><Van /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.vehicleCount }}</div>
              <div class="stat-label">车辆数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background-color: #E6A23C;">
              <el-icon :size="40"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.orderCount }}</div>
              <div class="stat-label">订单数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background-color: #F56C6C;">
              <el-icon :size="40"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.faultCount }}</div>
              <div class="stat-label">故障数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>订单趋势</span>
          </template>
          <div class="chart-placeholder">订单趋势图表</div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>车辆分布</span>
          </template>
          <div class="chart-placeholder">车辆分布图表</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { User, Van, Document, Warning } from '@element-plus/icons-vue'
import { getDashboardStats } from '@/api/dashboard'
import { ElMessage } from 'element-plus'

const stats = reactive({
  userCount: 0,
  vehicleCount: 0,
  orderCount: 0,
  faultCount: 0
})

const fetchStats = async () => {
  try {
    const res = await getDashboardStats()
    if (res.data) {
      stats.userCount = res.data.userCount || 0
      stats.vehicleCount = res.data.vehicleCount || 0
      stats.orderCount = res.data.orderCount || 0
      stats.faultCount = res.data.faultCount || 0
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败')
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 70px;
  height: 70px;
  border-radius: 10px;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.chart-placeholder {
  height: 250px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>
