<template>
  <div class="admin-dashboard">
    <!-- ===== 统计卡片 ===== -->
    <el-row :gutter="20" class="stats-grid">
      <el-col :span="6">
        <div class="stat-card stat-blue">
          <div class="stat-icon">🏔️</div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.totalScenic || 0 }}</div>
            <div class="stat-label">景点总数</div>
          </div>
          <div class="stat-trend up">
            <span>↑ 12%</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-orange">
          <div class="stat-icon">🍜</div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.totalFood || 0 }}</div>
            <div class="stat-label">美食总数</div>
          </div>
          <div class="stat-trend up">
            <span>↑ 8%</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-green">
          <div class="stat-icon">🗺️</div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.totalRoute || 0 }}</div>
            <div class="stat-label">路线总数</div>
          </div>
          <div class="stat-trend up">
            <span>↑ 5%</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-purple">
          <div class="stat-icon">🏨</div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.totalHotel || 0 }}</div>
            <div class="stat-label">酒店总数</div>
          </div>
          <div class="stat-trend up">
            <span>↑ 10%</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- ===== 用户统计 ===== -->
    <el-row :gutter="20" class="stats-grid">
      <el-col :span="6">
        <div class="stat-card stat-cyan">
          <div class="stat-icon">👤</div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.totalUser || 0 }}</div>
            <div class="stat-label">注册用户</div>
          </div>
          <div class="stat-trend up">
            <span>↑ 3%</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-pink">
          <div class="stat-icon">📋</div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.totalOrders || 0 }}</div>
            <div class="stat-label">总订单数</div>
          </div>
          <div class="stat-trend up">
            <span>↑ 15%</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-indigo">
          <div class="stat-icon">💰</div>
          <div class="stat-info">
            <div class="stat-number">¥{{ stats.totalRevenue || 0 }}</div>
            <div class="stat-label">总营收</div>
          </div>
          <div class="stat-trend up">
            <span>↑ 20%</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-teal">
          <div class="stat-icon">⭐</div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.avgRating || 0 }}</div>
            <div class="stat-label">平均评分</div>
          </div>
          <div class="stat-trend up">
            <span>↑ 2%</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- ===== 图表区域 ===== -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>📊 按城市分布</span>
              <el-tag size="small" type="info">景点</el-tag>
            </div>
          </template>
          <div class="city-list">
            <div v-for="item in cityStats" :key="item.city" class="city-item">
              <span class="city-name">{{ item.city }}</span>
              <el-progress 
                :percentage="getPercentage(item.count)" 
                :color="getColor(item.city)"
                :stroke-width="8"
              />
              <span class="city-count">{{ item.count }} 个</span>
            </div>
            <div v-if="cityStats.length === 0" class="empty-tip">
              暂无数据
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>📂 按分类分布</span>
              <el-tag size="small" type="warning">景点</el-tag>
            </div>
          </template>
          <div class="category-list">
            <div v-for="item in categoryStats" :key="item.category" class="category-item">
              <span class="category-name">{{ item.category }}</span>
              <el-progress 
                :percentage="getCategoryPercentage(item.count)" 
                :color="getCategoryColor(item.category)"
                :stroke-width="8"
              />
              <span class="category-count">{{ item.count }} 个</span>
            </div>
            <div v-if="categoryStats.length === 0" class="empty-tip">
              暂无数据
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- ===== 快捷操作 ===== -->
    <el-card class="quick-actions">
      <template #header>
        <span>⚡ 快捷操作</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="4">
          <div class="quick-item" @click="goTo('/admin/scenic')">
            <span class="quick-icon">🏔️</span>
            <span>管理景点</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="quick-item" @click="goTo('/admin/food')">
            <span class="quick-icon">🍜</span>
            <span>管理美食</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="quick-item" @click="goTo('/admin/route')">
            <span class="quick-icon">🗺️</span>
            <span>管理路线</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="quick-item" @click="goTo('/admin/hotel')">
            <span class="quick-icon">🏨</span>
            <span>管理酒店</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="quick-item" @click="goTo('/admin/hotel-order')">
            <span class="quick-icon">📋</span>
            <span>酒店预订</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="quick-item" @click="goTo('/admin/user')">
            <span class="quick-icon">👤</span>
            <span>管理用户</span>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getScenicStats } from '@/api/scenic'
import { getFoodStats } from '@/api/food'
import { getHotelStats } from '@/api/hotel'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()

const stats = ref({
  totalScenic: 0,
  totalFood: 0,
  totalRoute: 3,
  totalHotel: 0,
  totalUser: 0,
  totalOrders: 0,
  totalRevenue: 0,
  avgRating: 4.8
})

const cityStats = ref([])
const categoryStats = ref([])

// 加载统计数据
const loadStats = async () => {
  try {
    const scenicRes = await getScenicStats()
    if (scenicRes.code === 200) {
      const data = scenicRes.data
      stats.value.totalScenic = data.totalScenic || 0
      
      if (data.cityStats) {
        cityStats.value = data.cityStats.map(item => ({
          city: item.city || '未知',
          count: parseInt(item.count)
        })).sort((a, b) => b.count - a.count)
      }
      
      if (data.categoryStats) {
        categoryStats.value = data.categoryStats.map(item => ({
          category: item.category || '未知',
          count: parseInt(item.count)
        })).sort((a, b) => b.count - a.count)
      }
    }

    const foodRes = await getFoodStats()
    if (foodRes.code === 200) {
      stats.value.totalFood = foodRes.data.totalFood || 0
    }

    const hotelRes = await getHotelStats()
    if (hotelRes.code === 200) {
      stats.value.totalHotel = hotelRes.data.totalHotel || 0
    }

    // 获取用户数量
    const userRes = await request({ url: '/user/list', method: 'get' })
    if (userRes.code === 200) {
      stats.value.totalUser = (userRes.data || []).length
    }

    // 获取订单数量
    const orderRes = await request({ url: '/hotel-order/admin/list', method: 'get' })
    if (orderRes.code === 200) {
      const orders = orderRes.data || []
      stats.value.totalOrders = orders.length
      stats.value.totalRevenue = orders.reduce((sum, o) => sum + (o.totalAmount || 0), 0)
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

// 计算百分比
const getPercentage = (count) => {
  const total = stats.value.totalScenic || 1
  return Math.round((count / total) * 100)
}

const getCategoryPercentage = (count) => {
  const total = stats.value.totalScenic || 1
  return Math.round((count / total) * 100)
}

// 颜色配置
const getColor = (city) => {
  const colors = {
    '北京': '#409EFF',
    '杭州': '#67C23A',
    '成都': '#E6A23C',
    '丽江': '#F56C6C',
    '泰安': '#8E44AD'
  }
  return colors[city] || '#909399'
}

const getCategoryColor = (category) => {
  const colors = {
    '历史文化': '#409EFF',
    '自然风光': '#67C23A'
  }
  return colors[category] || '#909399'
}

// 跳转
const goTo = (path) => {
  router.push(path)
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 0;
}

/* ===== 统计卡片 ===== */
.stats-grid {
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 18px 22px;
  display: flex;
  align-items: center;
  gap: 14px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  transition: transform 0.3s, box-shadow 0.3s;
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
}

.stat-blue::before { background: #409EFF; }
.stat-orange::before { background: #E6A23C; }
.stat-green::before { background: #67C23A; }
.stat-purple::before { background: #8E44AD; }
.stat-cyan::before { background: #00BCD4; }
.stat-pink::before { background: #E91E63; }
.stat-indigo::before { background: #3F51B5; }
.stat-teal::before { background: #009688; }

.stat-icon {
  font-size: 32px;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 26px;
  font-weight: 700;
  color: #333;
  line-height: 1.2;
}

.stat-label {
  color: #999;
  font-size: 13px;
}

.stat-trend {
  font-size: 12px;
  font-weight: 500;
  padding: 2px 12px;
  border-radius: 12px;
}

.stat-trend.up {
  color: #67C23A;
  background: #f0faf4;
}

/* ===== 图表卡片 ===== */
.chart-row {
  margin-bottom: 24px;
}

.chart-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.city-list,
.category-list {
  padding: 4px 0;
}

.city-item,
.category-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}

.city-item:last-child,
.category-item:last-child {
  border-bottom: none;
}

.city-name,
.category-name {
  width: 60px;
  font-weight: 500;
  color: #333;
  flex-shrink: 0;
}

.city-item :deep(.el-progress),
.category-item :deep(.el-progress) {
  flex: 1;
}

.city-item :deep(.el-progress-bar__outer),
.category-item :deep(.el-progress-bar__outer) {
  border-radius: 10px;
}

.city-item :deep(.el-progress-bar__inner),
.category-item :deep(.el-progress-bar__inner) {
  border-radius: 10px;
}

.city-count,
.category-count {
  width: 60px;
  text-align: right;
  color: #999;
  font-size: 13px;
  flex-shrink: 0;
}

.empty-tip {
  text-align: center;
  color: #999;
  padding: 20px 0;
}

/* ===== 快捷操作 ===== */
.quick-actions {
  margin-bottom: 0;
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 18px 12px;
  background: #f5f7fa;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 500;
  color: #333;
  font-size: 13px;
}

.quick-item:hover {
  background: #409EFF;
  color: #fff;
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.quick-icon {
  font-size: 28px;
}
</style>