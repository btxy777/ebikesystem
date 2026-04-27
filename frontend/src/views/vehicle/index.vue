<template>
  <div class="vehicle-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>车辆管理</span>
          <div>
            <el-button type="primary" @click="handleAdd">新增车辆</el-button>
            <el-button type="primary" @click="handleRefresh">刷新数据</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="vehicleCode" label="车辆编号" />
        <el-table-column prop="vehicleType" label="车辆类型" />
        <el-table-column prop="batteryLevel" label="电量">
          <template #default="{ row }">
            <el-progress
              :percentage="row.batteryLevel || 0"
              :color="getBatteryColor(row.batteryLevel)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.statusName || getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="currentStation" label="当前位置">
          <template #default="{ row }">
            <span v-if="row.currentStation">{{ row.currentStation.stationName }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" label-width="100px">
        <el-form-item label="车辆编号">
          <el-input v-model="form.vehicleCode" placeholder="请输入车辆编号" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="车辆类型">
          <el-select v-model="form.vehicleType" placeholder="请选择" style="width: 100%;">
            <el-option label="电动车" value="电动车" />
            <el-option label="自行车" value="自行车" />
          </el-select>
        </el-form-item>
        <el-form-item label="电量">
          <el-slider v-model="form.batteryLevel" :min="0" :max="100" show-input />
        </el-form-item>
        <el-form-item v-if="isEdit" label="状态">
          <el-select v-model="form.status" placeholder="请选择" style="width: 100%;">
            <el-option label="离线" :value="0" />
            <el-option label="空闲" :value="1" />
            <el-option label="使用中" :value="2" />
            <el-option label="故障" :value="3" />
            <el-option label="维修中" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="isEdit" label="当前位置">
          <el-select v-model="form.currentStationId" placeholder="请选择站点" style="width: 100%;" clearable>
            <el-option
              v-for="station in stationList"
              :key="station.id"
              :label="station.stationName"
              :value="station.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="isEdit" label="最后维护时间">
          <span>{{ form.lastMaintenanceTime || '暂无' }}</span>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getVehicleList, updateVehicle, addVehicle } from '@/api/vehicle'
import { getStationList } from '@/api/station'

const tableData = ref([])
const stationList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)

const form = reactive({
  id: null,
  vehicleCode: '',
  vehicleType: '',
  batteryLevel: 100,
  status: 1,
  currentStationId: null,
  lastMaintenanceTime: null
})

const dialogTitle = computed(() => isEdit.value ? '编辑车辆' : '新增车辆')

const statusOptions = {
  0: { text: '离线', type: 'info' },
  1: { text: '空闲', type: 'success' },
  2: { text: '使用中', type: 'warning' },
  3: { text: '故障', type: 'danger' },
  4: { text: '维修中', type: 'danger' }
}

const getStatusText = (status) => statusOptions[status]?.text || '未知'
const getStatusType = (status) => statusOptions[status]?.type || 'info'

const getBatteryColor = (battery) => {
  if (!battery) return '#909399'
  if (battery < 20) return '#F56C6C'
  if (battery < 50) return '#E6A23C'
  return '#67C23A'
}

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

const fetchVehicleList = async () => {
  loading.value = true
  try {
    const res = await getVehicleList(currentPage.value, pageSize.value)
    tableData.value = res.data?.data || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取车辆列表失败:', error)
    ElMessage.error('获取车辆列表失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchVehicleList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchVehicleList()
}

const handleRefresh = () => {
  fetchVehicleList()
}

const handleAdd = () => {
  Object.assign(form, {
    id: null,
    vehicleCode: '',
    vehicleType: '',
    batteryLevel: 100,
    status: 1,
    currentStationId: null,
    lastMaintenanceTime: null
  })
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.id = row.id
  form.vehicleCode = row.vehicleCode
  form.vehicleType = row.vehicleType
  form.batteryLevel = row.batteryLevel || 0
  form.status = row.status
  form.currentStationId = row.currentStationId
  form.lastMaintenanceTime = row.lastMaintenanceTime ? formatDate(row.lastMaintenanceTime) : null
  isEdit.value = true
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.vehicleCode) {
    ElMessage.warning('请输入车辆编号')
    return
  }
  if (!form.vehicleType) {
    ElMessage.warning('请选择车辆类型')
    return
  }
  try {
    if (isEdit.value) {
      const data = {
        id: form.id,
        vehicleCode: form.vehicleCode,
        vehicleType: form.vehicleType,
        batteryLevel: form.batteryLevel,
        status: form.status,
        currentStationId: form.currentStationId
      }
      await updateVehicle(data)
      ElMessage.success('修改成功')
    } else {
      const data = {
        vehicleCode: form.vehicleCode,
        vehicleType: form.vehicleType,
        batteryLevel: form.batteryLevel
      }
      await addVehicle(data)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchVehicleList()
  } catch (error) {
    console.error(error)
    ElMessage.error('修改失败')
  }
}

onMounted(() => {
  fetchVehicleList()
  fetchStationList()
})
</script>

<style scoped>
.vehicle-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
