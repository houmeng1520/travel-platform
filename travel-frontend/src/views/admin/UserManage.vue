<template>
  <div class="manage-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="搜索用户名/昵称/邮箱" clearable style="width: 220px;" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">🔍 搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
        <el-form-item style="float: right;">
          <span style="color: #999; font-size: 14px;">共 {{ total }} 位用户</span>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe row-key="id">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="100" />
        <el-table-column prop="nickname" label="昵称" width="100" />
        <el-table-column prop="email" label="邮箱" width="160" />
        <el-table-column prop="phone" label="手机号" width="110" />
        <el-table-column prop="role" label="角色" width="80">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'" size="small">
              {{ row.role === 'ADMIN' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <template v-if="row.role !== 'ADMIN'">
              <el-button 
                :type="row.status === 1 ? 'warning' : 'success'" 
                size="small" 
                @click="handleToggleStatus(row)"
              >
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(row.id)">
                删除
              </el-button>
            </template>
            <span v-else style="color: #999; font-size: 12px;">不可操作</span>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// ===== 数据 =====
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  keyword: ''
})

// ===== 加载数据 =====
const loadData = async () => {
  loading.value = true
  try {
    let res
    const keyword = searchForm.keyword ? searchForm.keyword.trim() : ''
    console.log('1. 开始加载数据, keyword:', keyword)
    
    if (keyword) {
      res = await request({
        url: '/user/search',
        method: 'get',
        params: { keyword: keyword }
      })
    } else {
      res = await request({
        url: '/user/list',
        method: 'get'
      })
    }
    
    console.log('2. 接口返回:', res)
    
    if (res.code === 200) {
      console.log('3. 返回数据类型:', typeof res.data)
      console.log('3.1 是否是数组:', Array.isArray(res.data))
      console.log('3.2 数据内容:', JSON.stringify(res.data))
      
      // 如果 res.data 不是数组，但 res.data.data 是数组
      let data = res.data
      if (data && !Array.isArray(data) && data.data && Array.isArray(data.data)) {
        data = data.data
      }
      
      tableData.value = data || []
      total.value = tableData.value.length
      console.log('4. 当前表格数据:', tableData.value)
    }
  } catch (error) {
    console.error('5. 加载失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// ===== 重置搜索 =====
const resetSearch = () => {
  searchForm.keyword = ''
  loadData()
}

// ===== 切换状态（启用/禁用） =====
const handleToggleStatus = (row) => {
  const action = row.status === 1 ? '禁用' : '启用'
  ElMessageBox.confirm(`确定要${action}用户 "${row.username}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const newStatus = row.status === 1 ? 0 : 1
      
      const res = await request({
        url: `/user/status/${row.id}`,
        method: 'put',
        params: { status: newStatus }
      })
      
      if (res.code === 200) {
        ElMessage.success(`${action}成功`)
        
        // ===== 直接更新本地数据，不重新加载 =====
        const index = tableData.value.findIndex(item => item.id === row.id)
        if (index !== -1) {
          tableData.value[index].status = newStatus
          // 触发响应式更新
          tableData.value = [...tableData.value]
        }
      } else {
        ElMessage.error(res.message || '操作失败')
      }
    } catch (error) {
      ElMessage.error('操作失败')
    }
  }).catch(() => {})
}

// ===== 删除 =====
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该用户吗？此操作不可恢复！', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'error'
  }).then(async () => {
    try {
      const res = await request({
        url: `/user/delete/${id}`,
        method: 'delete'
      })
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadData()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// ===== 生命周期 =====
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.manage-container { padding: 0; }
.search-card { margin-bottom: 20px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>