<template>
  <div class="manage-container">
    <el-card class="search-card">
      <el-button type="success" @click="handleAdd">➕ 添加轮播图</el-button>
    </el-card>

    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" width="180" />
        <el-table-column prop="description" label="描述" width="200" />
        <el-table-column prop="btnText" label="按钮文字" width="100" />
        <el-table-column prop="link" label="跳转链接" width="120" />
        <el-table-column prop="image" label="图片" width="80">
          <template #default="{ row }">
            <el-image 
              v-if="row.image && !row.image.startsWith('linear-gradient')" 
              :src="getImageUrl(row.image)" 
              fit="cover" 
              style="width: 60px; height: 40px; border-radius: 4px;"
            >
              <template #error>
                <div style="width: 60px; height: 40px; background: #f0f0f0; border-radius: 4px;"></div>
              </template>
            </el-image>
            <span v-else style="color: #ccc; font-size: 12px;">渐变背景</span>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="60" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="formData.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="按钮文字">
          <el-input v-model="formData.btnText" placeholder="如：查看详情" />
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-select v-model="formData.link" placeholder="请选择跳转页面" style="width: 100%;">
            <el-option label="景点列表" value="/scenic" />
            <el-option label="美食列表" value="/food" />
            <el-option label="路线列表" value="/route" />
            <el-option label="酒店列表" value="/hotel" />
          </el-select>
        </el-form-item>
        <el-form-item label="图片">
          <div class="upload-container">
            <div class="image-preview-list" v-if="formData.image && !formData.image.startsWith('linear-gradient')">
              <div class="image-preview-item">
                <el-image 
                  :src="getImageUrl(formData.image)" 
                  fit="cover" 
                  style="width: 80px; height: 80px; border-radius: 4px;"
                >
                  <template #error>
                    <div style="width: 80px; height: 80px; display: flex; align-items: center; justify-content: center; background: #f0f0f0; font-size: 24px;">🖼️</div>
                  </template>
                </el-image>
                <el-icon class="remove-image" @click="removeImage"><Close /></el-icon>
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
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="99" style="width: 100%;" />
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
const dialogVisible = ref(false)
const dialogTitle = ref('添加轮播图')
const isEdit = ref(false)

const formData = reactive({
  id: null,
  title: '',
  description: '',
  btnText: '查看详情',
  link: '/scenic',
  image: '',
  sortOrder: 0
})

const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}))

// ===== 图片工具 =====

const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  if (url.startsWith('/uploads')) return `http://localhost:8080${url}`
  return url
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
    formData.image = response.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const handleUploadError = () => {
  ElMessage.error('上传失败，请稍后重试')
}

const removeImage = () => {
  formData.image = ''
}

// ===== 加载数据 =====
const loadData = async () => {
  loading.value = true
  try {
    const res = await request({ url: '/banner/list', method: 'get' })
    if (res.code === 200) {
      tableData.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '添加轮播图'
  Object.assign(formData, {
    id: null,
    title: '',
    description: '',
    btnText: '查看详情',
    link: '/scenic',
    image: '',
    sortOrder: 0
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑轮播图'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!formData.title) {
    ElMessage.warning('请输入标题')
    return
  }

  try {
    const url = isEdit.value ? '/banner/update' : '/banner/add'
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
  ElMessageBox.confirm('确定要删除该轮播图吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request({ url: `/banner/delete/${id}`, method: 'delete' })
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