<template>
  <div class="manage-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="输入酒店名称" clearable style="width: 200px;" />
        </el-form-item>
        <el-form-item label="城市">
          <el-select v-model="searchForm.city" placeholder="全部城市" clearable style="width: 140px;">
            <el-option label="全部" value="" />
            <el-option label="北京" value="北京" />
            <el-option label="杭州" value="杭州" />
            <el-option label="成都" value="成都" />
            <el-option label="丽江" value="丽江" />
            <el-option label="泰安" value="泰安" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">🔍 搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" @click="handleAdd">➕ 添加酒店</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe row-key="id">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="酒店名称" width="150" />
        <el-table-column prop="city" label="城市" width="100" />
        <el-table-column prop="starLevel" label="星级" width="100">
          <template #default="{ row }">
            <span v-for="i in row.starLevel" :key="i">⭐</span>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="rating" label="评分" width="100">
          <template #default="{ row }">⭐ {{ row.rating }}</template>
        </el-table-column>
        <el-table-column prop="images" label="图片" width="80">
          <template #default="{ row }">
            <el-image 
              v-if="getFirstImage(row.images)" 
              :src="getFirstImage(row.images)" 
              fit="cover" 
              style="width: 50px; height: 50px; border-radius: 4px;"
            >
              <template #error>
                <div style="width: 50px; height: 50px; background: #f0f0f0; border-radius: 4px;"></div>
              </template>
            </el-image>
            <span v-else style="color: #ccc; font-size: 12px;">无图</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="650px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="酒店名称">
          <el-input v-model="formData.name" placeholder="请输入酒店名称" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="城市">
              <el-input v-model="formData.city" placeholder="请输入城市" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="星级">
              <el-select v-model="formData.starLevel" placeholder="请选择星级" style="width: 100%;">
                <el-option label="1星级" :value="1" />
                <el-option label="2星级" :value="2" />
                <el-option label="3星级" :value="3" />
                <el-option label="4星级" :value="4" />
                <el-option label="5星级" :value="5" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="价格">
              <el-input-number v-model="formData.price" :precision="2" :step="50" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="评分">
              <el-input-number v-model="formData.rating" :precision="1" :step="0.5" :min="0" :max="5" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址">
          <el-input v-model="formData.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="设施">
          <el-input v-model="formData.facilities" placeholder="多个设施用逗号分隔，如：WiFi,停车场,游泳池" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="2" placeholder="请输入酒店介绍" />
        </el-form-item>

        <!-- ===== 图片上传 ===== -->
        <el-form-item label="图片">
          <div class="upload-container">
            <div class="image-preview-list" v-if="formData.images">
              <div 
                v-for="(img, index) in getImageList(formData.images)" 
                :key="index" 
                class="image-preview-item"
              >
                <el-image 
                  :src="getFullImageUrl(img)" 
                  fit="cover" 
                  style="width: 80px; height: 80px; border-radius: 4px;"
                >
                  <template #error>
                    <div style="width: 80px; height: 80px; display: flex; align-items: center; justify-content: center; background: #f0f0f0; font-size: 24px;">🖼️</div>
                  </template>
                </el-image>
                <el-icon class="remove-image" @click="removeImage(index)"><Close /></el-icon>
              </div>
            </div>
            <el-upload
              action="/api/upload/image"
              :headers="uploadHeaders"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
              :show-file-list="false"
              accept="image/*"
              class="upload-btn-wrapper"
            >
              <el-button type="primary" plain>📤 上传图片</el-button>
              <div class="upload-tip">支持 jpg/png，单张不超过2MB</div>
            </el-upload>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Close } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('添加酒店')
const isEdit = ref(false)

const searchForm = reactive({
  keyword: '',
  city: ''
})

const formData = reactive({
  id: null,
  name: '',
  city: '',
  starLevel: 3,
  price: 0,
  rating: 0,
  address: '',
  phone: '',
  facilities: '',
  description: '',
  status: 1,
  images: ''
})

// 上传请求头
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}))

// ===== 图片工具方法 =====

const getImageList = (images) => {
  if (!images) return []
  try {
    const list = JSON.parse(images)
    return Array.isArray(list) ? list : [list]
  } catch {
    return images ? [images] : []
  }
}

const getFullImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  if (url.startsWith('/uploads')) return `http://localhost:8080${url}`
  return url
}

const getFirstImage = (images) => {
  const list = getImageList(images)
  if (list.length > 0) {
    return getFullImageUrl(list[0])
  }
  return ''
}

// ===== 上传事件 =====

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！')
    return false
  }
  return true
}

const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    const currentImages = getImageList(formData.images)
    currentImages.push(response.data)
    formData.images = JSON.stringify(currentImages)
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const handleUploadError = () => {
  ElMessage.error('上传失败，请稍后重试')
}

const removeImage = (index) => {
  const images = getImageList(formData.images)
  images.splice(index, 1)
  formData.images = images.length > 0 ? JSON.stringify(images) : ''
}

// ===== 加载数据 =====
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value
    }
    if (searchForm.keyword) params.keyword = searchForm.keyword
    if (searchForm.city) params.city = searchForm.city

    const res = await request({
      url: '/hotel/admin/list',
      method: 'get',
      params
    })
    if (res.code === 200) {
      if (res.data.list) {
        tableData.value = res.data.list || []
        total.value = res.data.total || 0
      } else {
        const allData = res.data || []
        total.value = allData.length
        const start = (page.value - 1) * pageSize.value
        const end = start + pageSize.value
        tableData.value = allData.slice(start, end)
      }
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.city = ''
  page.value = 1
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '添加酒店'
  Object.assign(formData, {
    id: null, name: '', city: '', starLevel: 3,
    price: 0, rating: 0, address: '', phone: '',
    facilities: '', description: '', status: 1, images: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑酒店'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!formData.name) {
    ElMessage.warning('请输入酒店名称')
    return
  }
  if (!formData.city) {
    ElMessage.warning('请输入城市')
    return
  }

  try {
    const url = isEdit.value ? '/hotel/update' : '/hotel/add'
    const method = isEdit.value ? 'put' : 'post'

    const res = await request({ url, method, data: formData })
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该酒店吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request({ url: `/hotel/delete/${id}`, method: 'delete' })
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadData()
      }
    } catch (error) {
      ElMessage.error('删除失败')
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
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* ===== 图片上传样式 ===== */
.upload-container {
  width: 100%;
}

.image-preview-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 12px;
}

.image-preview-item {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #e8ecf1;
}

.image-preview-item .remove-image {
  position: absolute;
  top: -6px;
  right: -6px;
  font-size: 14px;
  color: #fff;
  background: #f56c6c;
  border-radius: 50%;
  padding: 2px;
  cursor: pointer;
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-preview-item .remove-image:hover {
  background: #e53935;
}

.upload-btn-wrapper {
  display: inline-block;
}

.upload-tip {
  color: #ccc;
  font-size: 12px;
  margin-top: 4px;
}
</style>