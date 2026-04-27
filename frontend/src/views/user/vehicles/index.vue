<template>
  <div class="vehicle-list">
    <el-row :gutter="20">
      <el-col
        v-for="vehicle in vehicleList"
        :key="vehicle.id"
        :xs="24"
        :sm="12"
        :md="8"
        :lg="6"
        :xl="4"
      >
        <el-card class="vehicle-card" shadow="hover">
          <div class="vehicle-image">
            <el-icon :size="60" color="#409eff"><Van /></el-icon>
          </div>
          <div class="vehicle-info">
            <h3>{{ vehicle.vehicleCode }}</h3>
            <p class="status">
              <el-tag :type="getStatusType(vehicle.status)" size="small">
                {{ getStatusText(vehicle.status) }}
              </el-tag>
            </p>
            <p class="station" v-if="vehicle.currentStation">
              <el-icon><Location /></el-icon>
              {{ vehicle.currentStation.stationName || '未知站点' }}
            </p>
            <p class="battery" v-if="vehicle.batteryLevel">
              <el-icon><Odometer /></el-icon>
              电量: {{ vehicle.batteryLevel }}%
            </p>
          </div>
          <div class="vehicle-actions">
            <el-button
              type="primary"
              size="small"
              :disabled="vehicle.status !== 1"
              @click="handleRent(vehicle)"
            >
              租车
            </el-button>
            <el-button size="small" @click="handleDetail(vehicle)">详情</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="!loading && vehicleList.length === 0" description="暂无可用车辆" />

    <el-dialog v-model="detailVisible" title="车辆详情" width="500px" append-to-body>
      <el-descriptions :column="2" border v-if="currentVehicle">
        <el-descriptions-item label="车辆编号">{{ currentVehicle.vehicleCode }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentVehicle.status)">
            {{ getStatusText(currentVehicle.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="电量" v-if="currentVehicle.batteryLevel">
          {{ currentVehicle.batteryLevel }}%
        </el-descriptions-item>
        <el-descriptions-item label="当前位置" v-if="currentVehicle.currentStation">
          {{ currentVehicle.currentStation.name }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button
          type="primary"
          :disabled="currentVehicle?.status !== 1"
          @click="handleRent(currentVehicle)"
        >
          开始租车
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAvailableVehicles } from '@/api/vehicle'
import { startRental } from '@/api/order'
import { Van, Location, Odometer } from '@element-plus/icons-vue'

const router = useRouter()

const loading = ref(false)
const vehicleList = ref([])
const detailVisible = ref(false)
const currentVehicle = ref({})

const statusMap = {
  0: { text: '使用中', type: 'danger' },
  1: { text: '可用', type: 'success' },
  2: { text: '维修中', type: 'warning' },
  3: { text: '已报废', type: 'info' }
}

const getStatusText = (status) => {
  return statusMap[status]?.text || '未知'
}

const getStatusType = (status) => {
  return statusMap[status]?.type || 'info'
}

const fetchVehicles = async () => {
  loading.value = true
  try {
    const res = await getAvailableVehicles()
    vehicleList.value = res.data || []
  } catch (error) {
    console.error('获取车辆列表失败:', error)
    ElMessage.error('获取车辆列表失败')
  } finally {
    loading.value = false
  }
}

const handleDetail = (vehicle) => {
  currentVehicle.value = vehicle
  detailVisible.value = true
}

const handleRent = async (vehicle) => {
  if (vehicle.batteryLevel && vehicle.batteryLevel < 20) {
    ElMessage.warning('车辆电量不足，无法使用')
    return
  }
  try {
    await startRental({ vehicleId: vehicle.id })
    ElMessage.success('租车成功')
    detailVisible.value = false
    router.push('/user/orders')
  } catch (error) {
    console.error('租车失败:', error)
    ElMessage.error(error.message || '租车失败')
  }
}

onMounted(() => {
  fetchVehicles()
})
</script>

<style scoped>
.vehicle-list {
  padding: 20px;
}

.vehicle-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.vehicle-card:hover {
  transform: translateY(-5px);
}

.vehicle-image {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 15px;
}

.vehicle-info {
  text-align: center;
}

.vehicle-info h3 {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: #303133;
}

.vehicle-info p {
  margin: 5px 0;
  font-size: 14px;
  color: #606266;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.vehicle-actions {
  margin-top: 15px;
  display: flex;
  justify-content: center;
  gap: 10px;
}
</style>