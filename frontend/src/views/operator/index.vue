<template>
  <div class="operator-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>运维人员管理</span>
          <el-button type="primary" @click="handleAdd">新增运维人员</el-button>
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag v-if="scope.row" :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间">
          <template #default="scope">
            <span v-if="scope.row">{{ formatDate(scope.row.createTime) }}</span>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
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
import { getOperators } from '@/api/user'
import request from '@/utils/request'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const form = reactive({
  username: '',
  phone: '',
  password: ''
})

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const fetchOperators = async () => {
  loading.value = true
  try {
    const res = await getOperators(currentPage.value, pageSize.value)
    tableData.value = res.data?.data || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取运维人员列表失败:', error)
    ElMessage.error('获取运维人员列表失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchOperators()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchOperators()
}

const handleAdd = () => {
  Object.assign(form, { username: '', phone: '', password: '' })
  dialogTitle.value = '新增运维人员'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.username || !form.phone || !form.password) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    await request({
      url: '/api/auth/register',
      method: 'post',
      data: { ...form, role: 2 }
    })
    ElMessage.success('添加成功')
    dialogVisible.value = false
    fetchOperators()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchOperators()
})
</script>

<style scoped>
.operator-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
