<template>
  <div class="food-container">
    <!-- 顶部导航 -->
    <el-header>
      <div class="header-content">
        <h1>🍜 美食推荐</h1>
        <el-button type="primary" @click="goHome">返回首页</el-button>
      </div>
    </el-header>

    <el-main>
      <!-- 搜索筛选栏 -->
      <el-card class="search-card">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="关键词">
            <el-input 
              v-model="searchForm.keyword" 
              placeholder="输入美食名称搜索" 
              clearable
              @keyup.enter="handleSearch"
              style="width: 200px;"
            />
          </el-form-item>
          
          <el-form-item label="城市">
            <el-select v-model="searchForm.city" placeholder="全部城市" clearable @change="handleSearch" style="width: 140px;">
              <el-option label="全部" value="" />
              <el-option label="北京" value="北京" />
              <el-option label="成都" value="成都" />
              <el-option label="杭州" value="杭州" />
              <el-option label="丽江" value="丽江" />
              <el-option label="泰安" value="泰安" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="分类">
            <el-select v-model="searchForm.category" placeholder="全部分类" clearable @change="handleSearch" style="width: 140px;">
              <el-option label="全部" value="" />
              <el-option label="川菜" value="川菜" />
              <el-option label="小吃" value="小吃" />
              <el-option label="江南菜" value="江南菜" />
              <el-option label="特色菜" value="特色菜" />
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleSearch">🔍 搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
          
          <el-form-item style="float: right;">
            <span class="result-count">共 {{ foodList.length }} 家美食</span>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 加载中 -->
      <div v-if="loading" style="text-align: center; padding: 50px;">
        <el-icon class="is-loading"><Loading /></el-icon> 加载中...
      </div>

      <!-- 美食卡片 -->
      <el-row :gutter="20" v-else>
        <el-col 
          :xs="24" :sm="12" :md="8" :lg="6"
          v-for="item in foodList" 
          :key="item.id" 
          style="margin-bottom: 20px;"
        >
          <el-card 
            :body-style="{ padding: '0px' }" 
            class="food-card" 
            @click="goDetail(item.id)"
          >
            <div class="food-image">
              <el-image 
                :src="getImageUrl(item.images)" 
                fit="cover"
                style="width: 100%; height: 200px;"
              >
                <template #error>
                  <div class="image-placeholder">🍽️</div>
                </template>
              </el-image>
              <div class="food-tag">
                <el-tag size="small" type="danger">{{ item.category }}</el-tag>
              </div>
            </div>
            <div style="padding: 14px;">
              <h3>{{ item.name }}</h3>
              <div class="food-meta">
                <span class="cuisine">{{ item.cuisine }}</span>
                <span class="rating">⭐ {{ item.rating }}</span>
              </div>
              <div class="food-tags" v-if="item.tags">
                <el-tag size="small" v-for="tag in item.tags.split(',')" :key="tag">{{ tag }}</el-tag>
              </div>
              <div class="food-price">¥{{ item.price }}</div>
              <div class="address">📍 {{ item.address }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <div v-if="!loading && foodList.length === 0" class="empty-state">
        <el-empty description="没有找到符合条件的美食" />
      </div>
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { getFoodList, searchFood } from '@/api/food'

const router = useRouter()
const foodList = ref([])
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

const loadFoodList = async () => {
  loading.value = true
  try {
    const res = await getFoodList()
    if (res.code === 200) {
      foodList.value = res.data || []
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch (error) {
    ElMessage.error('加载美食列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  loading.value = true
  try {
    const res = await searchFood(searchForm.category, searchForm.city, searchForm.keyword)
    if (res.code === 200) {
      foodList.value = res.data || []
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
  searchForm.category = ''
  loadFoodList()
}

const goDetail = (id) => {
  router.push(`/food/detail/${id}`)
}

const goHome = () => {
  router.push('/home')
}

onMounted(() => {
  loadFoodList()
})
</script>

<style scoped>
.food-container {
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

.food-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.food-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.food-image {
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

.food-tag {
  position: absolute;
  top: 10px;
  left: 10px;
}

.food-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 8px 0;
}

.cuisine {
  color: #666;
  font-size: 14px;
}

.rating {
  color: #ff9900;
  font-weight: 500;
}

.food-tags {
  margin: 8px 0;
}

.food-tags .el-tag {
  margin-right: 4px;
}

.food-price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 18px;
  margin: 8px 0;
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