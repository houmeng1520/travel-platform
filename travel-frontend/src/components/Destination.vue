<template>
  <div class="destination-container">
    <div class="section-header">
      <div class="header-left">
        <h2>🌟 精选目的地</h2>
        <span class="section-sub">热门城市推荐</span>
      </div>
    </div>
    
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in destinations" :key="item.id">
        <div class="destination-card" @click="goToCity(item.city)">
          <div class="destination-image" :style="{ background: item.color }">
            <span class="destination-emoji">{{ item.emoji }}</span>
          </div>
          <div class="destination-info">
            <h3>{{ item.city }}</h3>
            <p>{{ item.count }} 个景点</p>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const destinations = [
  { id: 1, city: '北京', count: 2, emoji: '🏯', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { id: 2, city: '杭州', count: 1, emoji: '🌊', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { id: 3, city: '成都', count: 2, emoji: '🐼', color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' },
  { id: 4, city: '丽江', count: 1, emoji: '🏔️', color: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' }
]

// 跳转到景点列表并传递城市参数
const goToCity = (city) => {
  router.push({
    path: '/scenic',
    query: { city: city }
  })
}
</script>

<style scoped>
.destination-container {
  margin-bottom: 30px;
}

.section-header {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 16px;
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

.destination-card {
  cursor: pointer;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  transition: transform 0.3s, box-shadow 0.3s;
}

.destination-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.destination-image {
  height: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.destination-emoji {
  font-size: 48px;
}

.destination-info {
  padding: 12px 16px;
  background: #fff;
}

.destination-info h3 {
  margin: 0 0 2px 0;
  font-size: 16px;
}

.destination-info p {
  margin: 0;
  color: #999;
  font-size: 13px;
}
</style>