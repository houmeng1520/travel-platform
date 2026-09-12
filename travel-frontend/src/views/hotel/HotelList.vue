<template>
  <div class="hotel-container">
    <!-- 顶部导航 -->
    <el-header>
      <div class="header-content">
        <h1>🏨 酒店住宿</h1>
        <el-button type="primary" @click="goHome">返回首页</el-button>
      </div>
    </el-header>

    <el-main>
      <!-- 搜索栏 -->
      <el-card class="search-card">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="城市">
            <el-select v-model="searchForm.city" placeholder="全部城市" clearable @change="handleSearch" style="width: 140px;">
              <el-option label="全部" value="" />
              <el-option label="北京" value="北京" />
              <el-option label="杭州" value="杭州" />
              <el-option label="成都" value="成都" />
              <el-option label="丽江" value="丽江" />
              <el-option label="泰安" value="泰安" />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input v-model="searchForm.keyword" placeholder="输入酒店名称" clearable @keyup.enter="handleSearch" style="width: 200px;" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">🔍 搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
          <el-form-item style="float: right;">
            <span class="result-count">共 {{ hotelList.length }} 家酒店</span>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 加载中 -->
      <div v-if="loading" style="text-align: center; padding: 50px;">
        <el-icon class="is-loading"><Loading /></el-icon> 加载中...
      </div>

      <!-- 酒店卡片 -->
      <el-row :gutter="20" v-else>
        <el-col :span="8" v-for="item in hotelList" :key="item.id" style="margin-bottom: 20px;">
          <el-card class="hotel-card" @click="goDetail(item.id)">
            <div class="hotel-image">
              <el-image :src="getImageUrl(item.images)" fit="cover" style="width: 100%; height: 200px;">
                <template #error>
                  <div class="image-placeholder">🏨</div>
                </template>
              </el-image>
              <div class="hotel-star">
                <span v-for="i in item.starLevel" :key="i">⭐</span>
              </div>
            </div>
            <div style="padding: 14px;">
              <h3>{{ item.name }}</h3>
              <p class="address">📍 {{ item.address }}</p>
              <div class="hotel-meta">
                <span class="rating">⭐ {{ item.rating }}</span>
                <span class="price">¥{{ item.price }}/晚</span>
              </div>
              <div class="facilities" v-if="item.facilities">
                <el-tag size="small" v-for="fac in item.facilities.split(',')" :key="fac" style="margin-right: 4px; margin-top: 4px;">
                  {{ fac }}
                </el-tag>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <div v-if="!loading && hotelList.length === 0" class="empty-state">
        <el-empty description="没有找到符合条件的酒店" />
      </div>
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { getHotelList, searchHotel } from '@/api/hotel'

const router = useRouter()
const hotelList = ref([])
const loading = ref(true)

const searchForm = reactive({
  keyword: '',
  city: ''
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

const loadHotelList = async () => {
  loading.value = true
  try {
    const res = await getHotelList()
    if (res.code === 200) {
      hotelList.value = res.data || []
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch (error) {
    ElMessage.error('加载酒店列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  loading.value = true
  try {
    const res = await searchHotel(searchForm.city, searchForm.keyword)
    if (res.code === 200) {
      hotelList.value = res.data || []
    } else {
      ElMessage.error(res.message || '搜索失败')
    }
  } catch (error) {
    ElMessage.error('搜索失败')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.city = ''
  loadHotelList()
}

const goDetail = (id) => {
  router.push(`/hotel/detail/${id}`)
}

const goHome = () => {
  router.push('/home')
}

onMounted(() => {
  loadHotelList()
})
</script>

<style scoped>
.hotel-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.el-header {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 24px;
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

.el-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.search-card {
  margin-bottom: 20px;
}

.result-count {
  color: #999;
  font-size: 14px;
  line-height: 40px;
}

.hotel-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  height: 100%;
}

.hotel-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.hotel-image {
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
}

.hotel-star {
  position: absolute;
  top: 10px;
  left: 10px;
  background: rgba(0,0,0,0.6);
  color: #ffd93d;
  padding: 2px 12px;
  border-radius: 12px;
  font-size: 12px;
}

.address {
  color: #666;
  font-size: 14px;
  margin: 6px 0;
}

.hotel-meta {
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

.facilities {
  margin-top: 8px;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}
</style>