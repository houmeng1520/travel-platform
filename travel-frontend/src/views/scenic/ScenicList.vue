<template>
  <div class="scenic-container">
    <!-- 顶部导航 -->
    <el-header>
      <div class="header-content">
        <h1>🏔️ 景点列表</h1>
        <el-button type="primary" @click="goHome">返回首页</el-button>
      </div>
    </el-header>

    <el-main>
      <!-- 搜索筛选栏 -->
      <el-card class="search-card">
        <el-form :inline="true" :model="searchForm" class="search-form">
          
          <el-form-item label="关键词">
            <el-input 
              v-model="searchForm.keyword" 
              placeholder="输入景点名称搜索" 
              clearable
              @keyup.enter="handleSearch"
              style="width: 200px;"
            />
          </el-form-item>
          
          <el-form-item label="城市">
            <el-select 
              v-model="searchForm.city" 
              placeholder="全部城市" 
              clearable 
              @change="handleSearch"
              style="width: 140px;"
            >
              <el-option label="全部" value="" />
              <el-option label="北京" value="北京" />
              <el-option label="杭州" value="杭州" />
              <el-option label="成都" value="成都" />
              <el-option label="丽江" value="丽江" />
              <el-option label="泰安" value="泰安" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="分类">
            <el-select 
              v-model="searchForm.category" 
              placeholder="全部分类" 
              clearable 
              @change="handleSearch"
              style="width: 140px;"
            >
              <el-option label="全部" value="" />
              <el-option label="历史文化" value="历史文化" />
              <el-option label="自然风光" value="自然风光" />
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleSearch">🔍 搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
          
          <el-form-item style="float: right;">
            <span class="result-count">共 {{ scenicList.length }} 个景点</span>
          </el-form-item>
          
        </el-form>
      </el-card>

      <!-- 加载中 -->
      <div v-if="loading" style="text-align: center; padding: 50px;">
        <el-icon class="is-loading"><Loading /></el-icon> 加载中...
      </div>

      <!-- 景点卡片 -->
      <el-row :gutter="20" v-else>
        <el-col 
          :xs="24" :sm="12" :md="8" :lg="6"
          v-for="item in scenicList" 
          :key="item.id" 
          style="margin-bottom: 20px;"
        >
          <el-card 
            :body-style="{ padding: '0px' }" 
            class="scenic-card" 
            @click="goDetail(item.id)"
          >
            <div class="scenic-image">
              <el-image 
                :src="getImageUrl(item.images)" 
                fit="cover"
                style="width: 100%; height: 200px;"
              >
                <template #error>
                  <div class="image-placeholder">🏔️</div>
                </template>
              </el-image>
              <div class="scenic-tag">
                <el-tag size="small" type="danger">{{ item.category }}</el-tag>
              </div>
            </div>
            <div style="padding: 14px;">
              <h3>{{ item.name }}</h3>
              <p class="description">{{ item.description }}</p>
              <div class="scenic-meta">
                <span class="price">¥{{ item.price }}</span>
                <span class="rating">⭐ {{ item.rating }}</span>
              </div>
              <div class="address">📍 {{ item.address }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <div v-if="!loading && scenicList.length === 0" class="empty-state">
        <el-empty description="没有找到符合条件的景点" />
      </div>
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { getScenicList, searchScenic } from '@/api/scenic'

const router = useRouter()
const route = useRoute()

const scenicList = ref([])
const loading = ref(true)

const searchForm = reactive({
  keyword: '',
  city: '',
  category: ''
})

// ===== 获取图片URL（支持多图JSON格式） =====
const getImageUrl = (images) => {
  if (!images) return ''
  try {
    const list = JSON.parse(images)
    if (Array.isArray(list) && list.length > 0) {
      const img = list[0]
      if (img.startsWith('http')) return img
      if (img.startsWith('/uploads')) return `http://localhost:8080${img}`
      return img
    }
    return ''
  } catch {
    if (images && images.startsWith('/uploads')) {
      return `http://localhost:8080${images}`
    }
    return images || ''
  }
}

// ===== 加载景点列表 =====
const loadScenicList = async () => {
  loading.value = true
  try {
    const res = await getScenicList()
    if (res.code === 200) {
      scenicList.value = res.data
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch (error) {
    ElMessage.error('加载景点列表失败')
  } finally {
    loading.value = false
  }
}

// ===== 搜索 =====
const handleSearch = async () => {
  loading.value = true
  try {
    const res = await searchScenic(searchForm.city, searchForm.category, searchForm.keyword)
    if (res.code === 200) {
      scenicList.value = res.data
    } else {
      ElMessage.error(res.message || '搜索失败')
    }
  } catch (error) {
    ElMessage.error('搜索失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// ===== 重置搜索 =====
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.city = ''
  searchForm.category = ''
  loadScenicList()
}

// ===== 跳转详情 =====
const goDetail = (id) => {
  router.push(`/scenic/detail/${id}`)
}

// ===== 返回首页 =====
const goHome = () => {
  router.push('/home')
}

// ===== 生命周期 =====
onMounted(() => {
  // 检查 URL 中是否有筛选参数
  const cityParam = route.query.city
  const categoryParam = route.query.category
  const keywordParam = route.query.keyword

  if (cityParam) {
    searchForm.city = cityParam
    handleSearch()
  } else if (categoryParam) {
    searchForm.category = categoryParam
    handleSearch()
  } else if (keywordParam) {
    searchForm.keyword = keywordParam
    handleSearch()
  } else {
    loadScenicList()
  }
})
</script>

<style scoped>
.scenic-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.el-header {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
}

.header-content h1 {
  font-size: 20px;
  margin: 0;
}

.search-card {
  margin-bottom: 20px;
}

.result-count {
  color: #999;
  font-size: 14px;
  line-height: 40px;
}

.scenic-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.scenic-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.scenic-image {
  position: relative;
  height: 200px;
  overflow: hidden;
  background: #f0f0f0;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  background: #f0f0f0;
}

.scenic-tag {
  position: absolute;
  top: 10px;
  left: 10px;
}

.description {
  color: #666;
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 40px;
  margin: 8px 0;
}

.scenic-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 8px 0;
}

.price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 18px;
}

.rating {
  color: #ff9900;
  font-weight: 500;
}

.address {
  color: #999;
  font-size: 12px;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
  background: #fff;
  border-radius: 12px;
}
</style>