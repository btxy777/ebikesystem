<template>
  <div class="fault-report">
    <el-card>
      <template #header>
        <span>故障上报</span>
      </template>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        style="max-width: 600px;"
      >
        <el-form-item label="车辆" prop="vehicleId">
          <el-select
            v-model="form.vehicleId"
            placeholder="请选择车辆"
            filterable
            style="width: 100%;"
          >
            <el-option
              v-for="vehicle in vehicleList"
              :key="vehicle.id"
              :label="vehicle.vehicleCode"
              :value="vehicle.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="故障描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述故障情况"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAvailableVehicles } from '@/api/vehicle'
import { createFaultReport } from '@/api/fault'

const router = useRouter()

const formRef = ref(null)
const submitting = ref(false)
const vehicleList = ref([])

const form = reactive({
  vehicleId: null,
  description: ''
})

const rules = {
  vehicleId: [
    { required: true, message: '请选择车辆', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入故障描述', trigger: 'blur' },
    { min: 10, message: '故障描述至少10个字符', trigger: 'blur' }
  ]
}

const fetchVehicles = async () => {
  try {
    const res = await getAvailableVehicles()
    vehicleList.value = res.data || []
  } catch (error) {
    console.error('获取车辆列表失败:', error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      await createFaultReport({
        vehicleId: form.vehicleId,
        description: form.description
      })
      ElMessage.success('故障上报成功')
      handleReset()
      setTimeout(() => {
        router.push('/user/orders')
      }, 1500)
    } catch (error) {
      console.error('故障上报失败:', error)
      ElMessage.error(error.message || '故障上报失败')
    } finally {
      submitting.value = false
    }
  })
}

const handleReset = () => {
  formRef.value?.resetFields()
  form.vehicleId = null
  form.description = ''
}

onMounted(() => {
  fetchVehicles()
})
</script>

<style scoped>
.fault-report {
  padding: 20px;
}
</style>