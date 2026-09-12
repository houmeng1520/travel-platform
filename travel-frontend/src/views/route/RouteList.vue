<template>
  <div class="route-container">
    <el-header>
      <div class="header-content">
        <h1>🗺️ 旅游路线</h1>
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
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input v-model="searchForm.keyword" placeholder="输入路线名称" clearable @keyup.enter="handleSearch" style="width: 200px;" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">🔍 搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
          <el-form-item style="float: right;">
            <span class="result-count">共 {{ routeList.length }} 条路线</span>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 加载中 -->
      <div v-if="loading" style="text-align: center; padding: 50px;">
        <el-icon class="is-loading"><Loading /></el-icon> 加载中...
      </div>

      <!-- 路线卡片 -->
      <el-row :gutter="20" v-else>
        <el-col :span="8" v-for="item in routeList" :key="item.id" style="margin-bottom: 20px;">
          <el-card class="route-card" @click="goDetail(item.id)">
            <div class="route-cover">
              <el-image :src="item.coverImage" fit="cover" style="width: 100%; height: 180px;">
                <template #error>
                  <div class="cover-placeholder">🗺️</div>
                </template>
              </el-image>
              <div class="route-days">{{ item.days }}天</div>
            </div>
            <div style="padding: 14px;">
              <h3>{{ item.title }}</h3>
              <p class="description">{{ item.description }}</p>
              <div class="route-meta">
                <span class="rating">⭐ {{ item.rating }}</span>
                <span class="price">¥{{ item.price }}</span>
              </div>
              <div class="route-city">📍 {{ item.city }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <div v-if="!loading && routeList.length === 0" class="empty-state">
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
import { getRouteList, searchRoute } from '@/api/route'

const router = useRouter()
const routeList = ref([])
const loading = ref(true)

const searchForm = reactive({
  city: '',
  keyword: ''
})

const loadRouteList = async () => {
  loading.value = true
  try {
    const res = await getRouteList()
    if (res.code === 200) {
      routeList.value = res.data
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch (error) {
    ElMessage.error('加载路线列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  loading.value = true
  try {
    const res = await searchRoute(searchForm.city, searchForm.keyword)
    if (res.code === 200) {
      routeList.value = res.data
    } else {
      ElMessage.error(res.message || '搜索失败')
    }
  } catch (error) {
    ElMessage.error('搜索失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.city = ''
  searchForm.keyword = ''
  loadRouteList()
}

const goDetail = (id) => {
  router.push(`/route/detail/${id}`)
}

const goHome = () => {
  router.push('/home')
}

onMounted(() => {
  loadRouteList()
})
</script>

<style scoped>
.route-container {
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

.route-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.route-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.route-cover {
  position: relative;
  height: 180px;
  overflow: hidden;
  background: #f0f0f0;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
}

.route-days {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(64, 158, 255, 0.9);
  color: #fff;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 13px;
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

.route-meta {
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

.route-city {
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