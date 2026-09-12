<template>
  <div class="manage-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true">
        <el-form-item label="关键词">
          <el-input v-model="searchKeyword" placeholder="搜索订单号/名称" clearable style="width: 200px;" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchType" placeholder="全部类型" clearable style="width: 120px;">
            <el-option label="全部" value="" />
            <el-option label="酒店" value="hotel" />
            <el-option label="景点" value="scenic" />
            <el-option label="美食" value="food" />
            <el-option label="路线" value="route" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchStatus" placeholder="全部状态" clearable style="width: 120px;">
            <el-option label="全部" value="" />
            <el-option label="待支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="已使用" :value="2" />
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

    <!-- 数据表格 - 所有字段一行显示，横向滚动 -->
    <el-card>
      <div class="table-wrapper">
        <el-table 
          :data="tableData" 
          v-loading="loading" 
          stripe 
          border
          style="width: 100%; min-width: 1300px;"
          :header-cell-style="{ background: '#f5f7fa', height: '40px' }"
        >
          <!-- 订单号 -->
          <el-table-column prop="orderNo" label="订单号" width="175" fixed="left" />
          
          <!-- 类型 -->
          <el-table-column label="类型" width="70">
            <template #default="{ row }">
              <el-tag :type="getTypeTag(row.orderType)" size="small">
                {{ getTypeText(row.orderType) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <!-- 名称 -->
          <el-table-column prop="targetName" label="名称" width="160" />
          
          <!-- 联系人 -->
          <el-table-column prop="guestName" label="联系人" width="80" />
          
          <!-- 电话 -->
          <el-table-column prop="guestPhone" label="电话" width="120" />
          
          <!-- 数量 -->
          <el-table-column prop="quantity" label="数量" width="60" />
          
          <!-- 金额 -->
          <el-table-column label="金额" width="100">
            <template #default="{ row }">¥{{ row.totalAmount }}</template>
          </el-table-column>
          
          <!-- 日期 -->
          <el-table-column prop="orderDate" label="日期" width="115" />
          
          <!-- 状态 -->
          <el-table-column label="状态" width="85">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small" style="white-space: nowrap;">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <!-- 操作列 -->
          <el-table-column label="操作" width="210" fixed="right">
            <template #default="{ row }">
              <div style="display: flex; gap: 3px; align-items: center; justify-content: center; flex-wrap: nowrap;">
                <el-button v-if="row.status === 0" type="success" size="small" style="padding: 4px 10px; font-size: 12px;" @click="handlePay(row.id)">支付</el-button>
                <el-button v-if="row.status === 1" type="warning" size="small" style="padding: 4px 10px; font-size: 12px;" @click="handleUse(row.id)">使用</el-button>
                <el-button v-if="row.status === 2" type="info" size="small" style="padding: 4px 10px; font-size: 12px;" @click="handleComplete(row.id)">完成</el-button>
                <el-button v-if="row.status === 0 || row.status === 1" type="danger" size="small" style="padding: 4px 10px; font-size: 12px;" @click="handleCancel(row.id)">取消</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
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
const searchType = ref('')
const searchStatus = ref('')

const getTypeText = (type) => {
  const map = { hotel: '酒店', scenic: '景点', food: '美食', route: '路线' }
  return map[type] || type
}

const getTypeTag = (type) => {
  const map = { hotel: 'primary', scenic: 'success', food: 'warning', route: 'info' }
  return map[type] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '待支付', 1: '已支付', 2: '已使用', 3: '已完成', 4: '已取消' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'primary', 3: 'info', 4: 'danger' }
  return map[status] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/order/admin/list',
      method: 'get',
      params: {
        keyword: searchKeyword.value,
        orderType: searchType.value,
        status: searchStatus.value
      }
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
  searchType.value = ''
  searchStatus.value = ''
  loadData()
}

const handlePay = (id) => {
  ElMessageBox.confirm('确认代为支付该订单？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    const res = await request({ url: `/order/pay/${id}`, method: 'put' })
    if (res.code === 200) {
      ElMessage.success('支付成功')
      loadData()
    }
  }).catch(() => {})
}

const handleUse = (id) => {
  ElMessageBox.confirm('标记为已使用？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    const res = await request({ url: `/order/use/${id}`, method: 'put' })
    if (res.code === 200) {
      ElMessage.success('已标记使用')
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
    const res = await request({ url: `/order/complete/${id}`, method: 'put' })
    if (res.code === 200) {
      ElMessage.success('已完成')
      loadData()
    }
  }).catch(() => {})
}

const handleCancel = (id) => {
  ElMessageBox.confirm('取消该订单？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await request({ url: `/order/cancel/${id}`, method: 'put' })
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

/* 表格容器 - 支持横向滚动 */
.table-wrapper {
  width: 100%;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.table-wrapper::-webkit-scrollbar {
  height: 8px;
}

.table-wrapper::-webkit-scrollbar-track {
  background: #f0f0f0;
  border-radius: 4px;
}

.table-wrapper::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.table-wrapper::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 表格样式 */
:deep(.el-table) {
  font-size: 13px;
}

:deep(.el-table .cell) {
  padding: 2px 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

:deep(.el-table__body td) {
  padding: 3px 0;
}

:deep(.el-table .cell .el-button) {
  margin: 0 2px;
  padding: 4px 10px;
  font-size: 12px;
  border-radius: 4px;
}

/* 固定列阴影 */
:deep(.el-table__fixed) {
  box-shadow: 2px 0 6px rgba(0, 0, 0, 0.08);
}

:deep(.el-table__fixed-right) {
  box-shadow: -2px 0 6px rgba(0, 0, 0, 0.08);
}
</style>