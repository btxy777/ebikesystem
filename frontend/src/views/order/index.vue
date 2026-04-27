<template>
  <div class="order-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
          <el-button type="primary" @click="handleRefresh">刷新数据</el-button>
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="用户">
          <template #default="{ row }">
            <span v-if="row.user">{{ row.user.username }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="车辆">
          <template #default="{ row }">
            <span v-if="row.vehicle">{{ row.vehicle.vehicleCode }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="起始站点">
          <template #default="{ row }">
            <span v-if="row.startStation">{{ row.startStation.stationName }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="结束站点">
          <template #default="{ row }">
            <span v-if="row.endStation">{{ row.endStation.stationName }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间">
          <template #default="{ row }">
            {{ formatDate(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间">
          <template #default="{ row }">
            <span v-if="row.endTime">{{ formatDate(row.endTime) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长(分钟)" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" title="编辑订单" width="500px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" label-width="100px">
        <el-form-item label="订单ID">
          <el-input v-model="form.id" disabled />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="请选择" style="width: 100%;">
            <el-option label="进行中" :value="0" />
            <el-option label="已完成" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="结束站点">
          <el-select v-model="form.endStationId" placeholder="请选择站点" style="width: 100%;" clearable>
            <el-option
              v-for="station in stationList"
              :key="station.id"
              :label="station.stationName"
              :value="station.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="时长(分钟)">
          <el-input-number v-model="form.duration" :min="0" style="width: 100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrderList, updateOrder } from '@/api/order'
import { getStationList } from '@/api/station'

const tableData = ref([])
const stationList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  status: 0,
  endStationId: null,
  duration: 0
})

const statusOptions = {
  0: { text: '进行中', type: 'primary' },
  1: { text: '已完成', type: 'success' }
}

const getStatusText = (status) => statusOptions[status]?.text || '未知'
const getStatusType = (status) => statusOptions[status]?.type || 'info'

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const fetchStationList = async () => {
  try {
    const res = await getStationList(1, 100)
    stationList.value = res.data?.data || []
  } catch (error) {
    console.error('获取站点列表失败:', error)
  }
}

const fetchOrderList = async () => {
  loading.value = true
  try {
    const res = await getOrderList({ page: currentPage.value, size: pageSize.value })
    tableData.value = res.data?.data || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchOrderList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchOrderList()
}

const handleRefresh = () => {
  fetchOrderList()
}

const handleEdit = (row) => {
  form.id = row.id
  form.status = row.status
  form.endStationId = row.endStationId
  form.duration = row.duration || 0
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    const data = {
      id: form.id,
      status: form.status,
      endStationId: form.endStationId,
      duration: form.duration
    }
    await updateOrder(data)
    ElMessage.success('修改成功')
    dialogVisible.value = false
    fetchOrderList()
  } catch (error) {
    console.error(error)
    ElMessage.error('修改失败')
  }
}

onMounted(() => {
  fetchOrderList()
  fetchStationList()
})
</script>

<style scoped>
.order-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
