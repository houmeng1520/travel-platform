<template>
  <div class="manage-container">
    <el-card class="search-card">
      <el-form :inline="true">
        <el-form-item label="订单号">
          <el-input v-model="searchKeyword" placeholder="搜索订单号/酒店名" clearable style="width: 200px;" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchStatus" placeholder="全部状态" clearable style="width: 140px;">
            <el-option label="全部" value="" />
            <el-option label="待确认" :value="0" />
            <el-option label="已确认" :value="1" />
            <el-option label="已入住" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">🔍 搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%;">
        <el-table-column prop="orderNo" label="订单号" width="190" />
        <el-table-column prop="hotelName" label="酒店名称" width="150" />
        <el-table-column prop="guestName" label="入住人" width="80" />
        <el-table-column prop="guestPhone" label="电话" width="110" />
        <el-table-column prop="roomType" label="房型" width="80" />
        <el-table-column prop="nights" label="天数" width="60" />
        <el-table-column prop="totalAmount" label="总金额" width="100">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="checkInDate" label="入住日期" width="110" />
        <el-table-column prop="checkOutDate" label="离店日期" width="110" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 操作列：增加宽度，不固定，让表格自然滚动 -->
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <div style="display: flex; gap: 4px; flex-wrap: wrap;">
              <el-button v-if="row.status === 0" type="success" size="small" @click="handleConfirm(row.id)">确认</el-button>
              <el-button v-if="row.status === 1" type="warning" size="small" @click="handleCheckIn(row.id)">入住</el-button>
              <el-button v-if="row.status === 2" type="info" size="small" @click="handleComplete(row.id)">完成</el-button>
              <el-button v-if="row.status === 0 || row.status === 1" type="danger" size="small" @click="handleCancel(row.id)">取消</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const searchKeyword = ref('')
const searchStatus = ref('')

const getStatusText = (status) => {
  const map = { 0: '待确认', 1: '已确认', 2: '已入住', 3: '已完成', 4: '已取消' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info', 4: 'danger' }
  return map[status] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/hotel-order/admin/list',
      method: 'get',
      params: { keyword: searchKeyword.value, status: searchStatus.value }
    })
    if (res.code === 200) {
      tableData.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchKeyword.value = ''
  searchStatus.value = ''
  loadData()
}

const handleConfirm = (id) => {
  ElMessageBox.confirm('确认该预订？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    const res = await request({ url: `/hotel-order/confirm/${id}`, method: 'put' })
    if (res.code === 200) {
      ElMessage.success('确认成功')
      loadData()
    }
  }).catch(() => {})
}

const handleCheckIn = (id) => {
  ElMessageBox.confirm('标记为已入住？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    const res = await request({ url: `/hotel-order/checkin/${id}`, method: 'put' })
    if (res.code === 200) {
      ElMessage.success('已标记入住')
      loadData()
    }
  }).catch(() => {})
}

const handleComplete = (id) => {
  ElMessageBox.confirm('标记为已完成？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    const res = await request({ url: `/hotel-order/complete/${id}`, method: 'put' })
    if (res.code === 200) {
      ElMessage.success('已完成')
      loadData()
    }
  }).catch(() => {})
}

const handleCancel = (id) => {
  ElMessageBox.confirm('取消该预订？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await request({ url: `/hotel-order/cancel/${id}`, method: 'put' })
    if (res.code === 200) {
      ElMessage.success('已取消')
      loadData()
    }
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.manage-container { padding: 0; }
.search-card { margin-bottom: 20px; }

/* 表格容器允许横向滚动 */
:deep(.el-table) {
  overflow-x: auto;
}

/* 操作列按钮不换行 */
:deep(.el-table .cell) {
  white-space: normal;
}
</style>