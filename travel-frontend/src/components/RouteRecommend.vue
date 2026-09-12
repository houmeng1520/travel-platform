<template>
  <div class="route-container">
    <div class="section-header">
      <div class="header-left">
        <h2>🗺️ 精选路线</h2>
        <span class="section-sub">专业规划，轻松出行</span>
      </div>
      <el-button type="text" @click="goRouteList" class="view-all">查看全部 →</el-button>
    </div>
    
    <el-row :gutter="20">
      <el-col :span="8" v-for="item in routes" :key="item.id">
        <div class="route-card" @click="goRouteDetail(item.id)">
          <div class="route-image" :style="{ background: 'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)' }">
            <span style="font-size: 42px;">{{ getEmoji(item.id) }}</span>
            <div class="route-days">{{ item.days }}天{{ item.days > 1 ? '' : '' }}</div>
          </div>
          <div class="route-info">
            <h4>{{ item.title }}</h4>
            <p class="route-desc">{{ item.description }}</p>
            <div class="route-meta">
              <span class="rating">⭐ {{ item.rating }}</span>
              <span class="price">¥{{ item.price }}</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getRouteList } from '@/api/route'

const router = useRouter()
const routes = ref([])

const emojis = ['🗺️', '🏯', '🌊', '🏔️', '🌴', '🎡']

const getEmoji = (id) => {
  return emojis[(id - 1) % emojis.length]
}

const loadRoutes = async () => {
  try {
    const res = await getRouteList()
    if (res.code === 200) {
      routes.value = res.data.slice(0, 3)
    }
  } catch (error) {
    console.error('加载路线失败', error)
  }
}

const goRouteDetail = (id) => {
  router.push(`/route/detail/${id}`)
}

const goRouteList = () => {
  router.push('/route')
}

onMounted(() => {
  loadRoutes()
})
</script>

<style scoped>
.route-container {
  margin-bottom: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 16px;
}

.section-header .header-left {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.section-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.section-sub {
  color: #999;
  font-size: 13px;
}

.view-all {
  color: #409EFF;
  font-size: 14px;
}

.route-card {
  cursor: pointer;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  transition: transform 0.3s, box-shadow 0.3s;
  background: #fff;
  height: 100%;
}

.route-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.route-image {
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.route-days {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0,0,0,0.6);
  color: #fff;
  padding: 2px 14px;
  border-radius: 12px;
  font-size: 13px;
}

.route-info {
  padding: 14px 16px;
}

.route-info h4 {
  margin: 0 0 6px 0;
  font-size: 16px;
}

.route-desc {
  margin: 0 0 10px 0;
  color: #666;
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 40px;
}

.route-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.route-meta .rating {
  color: #ff9900;
  font-weight: 500;
}

.route-meta .price {
  color: #f56c6c;
  font-weight: 700;
  font-size: 18px;
}
</style>