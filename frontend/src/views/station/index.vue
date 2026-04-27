<template>
  <div class="station-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>站点管理</span>
          <div>
            <el-button type="primary" @click="handleAdd">新增站点</el-button>
            <el-button type="primary" @click="handleRefresh">刷新数据</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="stationName" label="站点名称" />
        <el-table-column prop="address" label="位置" />
        <el-table-column prop="latitude" label="纬度" />
        <el-table-column prop="longitude" label="经度" />
        <el-table-column prop="capacity" label="容量" />
        <el-table-column prop="availableSlots" label="可用槽位" />
        <el-table-column prop="createTime" label="创建时间">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" label-width="100px">
        <el-form-item label="站点名称" prop="stationName">
          <el-input v-model="form.stationName" placeholder="请输入站点名称" />
        </el-form-item>
        <el-form-item label="位置" prop="address">
          <el-input v-model="form.address" placeholder="请输入位置" />
        </el-form-item>
        <el-form-item label="纬度" prop="latitude">
          <el-input-number v-model="form.latitude" :precision="6" :step="0.000001" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="经度" prop="longitude">
          <el-input-number v-model="form.longitude" :precision="6" :step="0.000001" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="容量" prop="capacity">
          <el-input-number v-model="form.capacity" :min="0" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="可用槽位" prop="availableSlots">
          <el-input-number v-model="form.availableSlots" :min="0" style="width: 100%;" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStationList, addStation, updateStation, deleteStation } from '@/api/station'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const isEdit = computed(() => !!form.id)

const form = reactive({
  id: null,
  stationName: '',
  address: '',
  latitude: 0,
  longitude: 0,
  capacity: 0,
  availableSlots: 0
})

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const fetchStationList = async () => {
  loading.value = true
  try {
    const res = await getStationList(currentPage.value, pageSize.value)
    tableData.value = res.data?.data || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取站点列表失败:', error)
    ElMessage.error('获取站点列表失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchStationList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchStationList()
}

const handleRefresh = () => {
  fetchStationList()
}

const resetForm = () => {
  form.id = null
  form.stationName = ''
  form.address = ''
  form.latitude = 0
  form.longitude = 0
  form.capacity = 0
  form.availableSlots = 0
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增站点'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.id = row.id
  form.stationName = row.stationName
  form.address = row.address
  form.latitude = row.latitude || 0
  form.longitude = row.longitude || 0
  form.capacity = row.capacity || 0
  form.availableSlots = row.availableSlots || 0
  dialogTitle.value = '编辑站点'
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该站点吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteStation(row.id)
      ElMessage.success('删除成功')
      fetchStationList()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!form.stationName) {
    ElMessage.warning('请输入站点名称')
    return
  }
  if (!form.address) {
    ElMessage.warning('请输入位置')
    return
  }

  try {
    const data = {
      id: form.id,
      stationName: form.stationName,
      address: form.address,
      latitude: form.latitude,
      longitude: form.longitude,
      capacity: form.capacity,
      availableSlots: form.availableSlots
    }

    if (isEdit.value) {
      await updateStation(data)
      ElMessage.success('更新成功')
    } else {
      await addStation(data)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchStationList()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchStationList()
})
</script>

<style scoped>
.station-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
