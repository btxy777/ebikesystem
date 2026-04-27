<template>
  <div class="fault-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>故障管理</span>
          <div class="header-actions">
            <el-select v-model="filterType" placeholder="筛选类型" style="width: 150px; margin-right: 10px;" @change="handleFilterChange">
              <el-option label="全部" value="all" />
              <el-option label="故障车辆" value="fault" />
              <el-option label="低电量车辆" value="lowBattery" />
            </el-select>
            <el-button type="primary" @click="handleRefresh">刷新数据</el-button>
          </div>
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
        <el-table-column label="处理人">
          <template #default="scope">
            <span v-if="scope.row?.operator">{{ scope.row.operator.username }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
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
                v-if="scope.row.status === 0 && isOperator"
                type="primary"
                size="small"
                @click="handleAccept(scope.row)"
              >接单</el-button>
              <el-button
                v-if="scope.row.status === 1 && (isAdmin || scope.row.operatorId === userId)"
                type="warning"
                size="small"
                @click="handleUpdateStatus(scope.row, 2)"
              >处理中</el-button>
              <el-button
                v-if="scope.row.status === 1 && (isAdmin || scope.row.operatorId === userId)"
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

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 20px; justify-content: flex-end;"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFaultList, acceptFault, updateFaultStatus, repairVehicle } from '@/api/fault'
import { useUserStore } from '@/store/user'
import request from '@/utils/request'

const userStore = useUserStore()

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const detailVisible = ref(false)
const currentFault = ref({})
const filterType = ref('all')

const userRole = computed(() => Number(userStore.role))
const userId = computed(() => Number(userStore.userId))
const isOperator = computed(() => userRole.value === 2)
const isAdmin = computed(() => userRole.value === 3)

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
    let res
    if (filterType.value === 'lowBattery') {
      res = await request.get('/api/fault-reports/lowBattery')
    } else {
      res = await getFaultList(currentPage.value, pageSize.value)
    }
    tableData.value = res.data?.data || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取故障列表失败:', error)
    ElMessage.error('获取故障列表失败')
  } finally {
    loading.value = false
  }
}

const handleFilterChange = () => {
  currentPage.value = 1
  fetchFaultList()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchFaultList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchFaultList()
}

const handleRefresh = () => {
  fetchFaultList()
}

const handleAccept = async (row) => {
  try {
    await acceptFault(row.id, userId.value)
    ElMessage.success('接单成功')
    fetchFaultList()
  } catch (error) {
    console.error(error)
  }
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
.fault-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.header-actions {
  display: flex;
  align-items: center;
}
</style>
