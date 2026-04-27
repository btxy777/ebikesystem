<template>
  <div class="operator-fault-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>运维接单查询</span>
          <el-button type="primary" @click="handleRefresh">刷新数据</el-button>
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="上报人">
          <template #default="scope">
            <span v-if="scope.row?.user">{{ scope.row.user.username }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="车辆">
          <template #default="scope">
            <span v-if="scope.row?.vehicle">{{ scope.row.vehicle.vehicleCode }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="故障描述" show-overflow-tooltip />
        <el-table-column prop="createTime" label="上报时间">
          <template #default="scope">
            <span v-if="scope.row">{{ formatDate(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag v-if="scope.row" :type="getStatusType(scope.row.status)">
              {{ scope.row.statusName || getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240">
          <template #default="scope">
            <template v-if="scope.row">
              <el-button
                v-if="scope.row.status === 1 && scope.row.operatorId === userId"
                type="warning"
                size="small"
                @click="handleUpdateStatus(scope.row, 2)"
              >处理中</el-button>
              <el-button
                v-if="scope.row.status === 1 && scope.row.operatorId === userId"
                type="success"
                size="small"
                @click="handleRepair(scope.row)"
              >修复车辆</el-button>
              <el-button
                type="info"
                size="small"
                @click="handleDetail(scope.row)"
              >详情</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailVisible" title="故障详情" width="600px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="故障ID">{{ currentFault.id }}</el-descriptions-item>
        <el-descriptions-item label="车辆编号">
          {{ currentFault.vehicle?.vehicleCode || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="上报人">
          {{ currentFault.user?.username || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="处理人">
          {{ currentFault.operator?.username || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentFault.status)">
            {{ currentFault.statusName || getStatusText(currentFault.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="上报时间">
          {{ formatDate(currentFault.createTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="故障描述" :span="2">
          {{ currentFault.description }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFaultListByOperator, updateFaultStatus, repairVehicle } from '@/api/fault'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const tableData = ref([])
const loading = ref(false)
const detailVisible = ref(false)
const currentFault = ref({})
const userId = ref(Number(userStore.userId))

const statusOptions = {
  0: { text: '待处理', type: 'danger' },
  1: { text: '处理中', type: 'warning' },
  2: { text: '已解决', type: 'success' }
}

const getStatusText = (status) => {
  return statusOptions[status]?.text || '未知'
}

const getStatusType = (status) => {
  return statusOptions[status]?.type || 'info'
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const fetchFaultList = async () => {
  loading.value = true
  try {
    const res = await getFaultListByOperator(userId.value)
    tableData.value = res.data || []
  } catch (error) {
    console.error('获取接单列表失败:', error)
    ElMessage.error('获取接单列表失败')
  } finally {
    loading.value = false
  }
}

const handleRefresh = () => {
  fetchFaultList()
}

const handleUpdateStatus = async (row, status) => {
  try {
    await updateFaultStatus(row.id, status, userId.value)
    ElMessage.success('状态更新成功')
    fetchFaultList()
  } catch (error) {
    console.error(error)
  }
}

const handleRepair = async (row) => {
  ElMessageBox.confirm('确定要修复该车辆吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await repairVehicle(row.id, userId.value)
      ElMessage.success('车辆修复成功，故障单已关闭')
      fetchFaultList()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleDetail = (row) => {
  currentFault.value = row
  detailVisible.value = true
}

onMounted(() => {
  fetchFaultList()
})
</script>

<style scoped>
.operator-fault-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>