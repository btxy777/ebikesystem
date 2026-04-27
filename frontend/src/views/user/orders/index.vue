<template>
  <div class="order-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的订单</span>
          <el-button type="primary" @click="fetchOrders">刷新</el-button>
        </div>
      </template>

      <el-table :data="orderList" border stripe v-loading="loading">
        <el-table-column prop="id" label="订单ID" width="80" />
        <el-table-column label="车辆">
          <template #default="{ row }">
            {{ row.vehicle?.vehicleCode || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间">
          <template #default="{ row }">
            {{ formatDate(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间">
          <template #default="{ row }">
            {{ row.endTime ? formatDate(row.endTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="cost" label="费用">
          <template #default="{ row }">
            {{ row.cost ? `¥${row.cost}` : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              type="warning"
              size="small"
              @click="handleEndRental(row)"
            >
              结束租车
            </el-button>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyOrders, endRental } from '@/api/order'

const loading = ref(false)
const orderList = ref([])

const statusMap = {
  0: { text: '进行中', type: 'primary' },
  1: { text: '已完成', type: 'success' },
  2: { text: '已取消', type: 'info' }
}

const getStatusText = (status) => {
  return statusMap[status]?.text || '未知'
}

const getStatusType = (status) => {
  return statusMap[status]?.type || 'info'
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await getMyOrders()
    orderList.value = res.data || []
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const handleEndRental = async (order) => {
  try {
    await ElMessageBox.confirm('确定要结束租车吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await endRental({ orderId: order.id })
    ElMessage.success('结束租车成功')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('结束租车失败:', error)
    }
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-list {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>