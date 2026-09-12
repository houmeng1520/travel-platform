<template>
  <div class="discount-container">
    <div class="section-header">
      <div class="header-left">
        <h2>🔥 限时优惠</h2>
        <span class="section-sub">超值好价，手慢无</span>
      </div>
    </div>
    
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in discountList" :key="item.id">
        <div class="discount-card" @click="goToDetail(item)">
          <div class="discount-image" :style="{ background: item.color || '#f0f0f0' }">
            <span class="discount-emoji">{{ item.emoji || '🎉' }}</span>
            <div class="discount-badge">-{{ item.discount }}%</div>
          </div>
          <div class="discount-info">
            <h4>{{ item.name }}</h4>
            <div class="discount-price">
              <span class="current-price">¥{{ item.price }}</span>
              <span class="original-price">¥{{ item.originalPrice }}</span>
            </div>
            <div class="discount-meta">
              <span class="rating">⭐ {{ item.rating }}</span>
              <span class="city">📍 {{ item.city }}</span>
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
import { getDiscountScenic } from '@/api/scenic'
import { getDiscountFood } from '@/api/food'

const router = useRouter()
const discountList = ref([])

const loadDiscount = async () => {
  try {
    const scenicRes = await getDiscountScenic()
    const foodRes = await getDiscountFood()
    
    const list = []
    
    if (scenicRes.code === 200 && scenicRes.data) {
      scenicRes.data.forEach(item => {
        const originalPrice = (item.price * 1.3).toFixed(0)
        const discount = Math.round((1 - item.price / originalPrice) * 100)
        list.push({
          ...item,
          type: 'scenic',
          emoji: '🏔️',
          originalPrice: originalPrice,
          discount: discount,
          color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
        })
      })
    }
    
    if (foodRes.code === 200 && foodRes.data) {
      foodRes.data.forEach(item => {
        const originalPrice = (item.price * 1.2).toFixed(0)
        const discount = Math.round((1 - item.price / originalPrice) * 100)
        list.push({
          ...item,
          type: 'food',
          emoji: '🍜',
          originalPrice: originalPrice,
          discount: discount,
          color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
        })
      })
    }
    
    discountList.value = list.sort((a, b) => b.rating - a.rating).slice(0, 4)
  } catch (error) {
    console.error('加载限时优惠失败', error)
  }
}

const goToDetail = (item) => {
  if (item.type === 'scenic') {
    router.push(`/scenic/detail/${item.id}`)
  } else {
    router.push(`/food/detail/${item.id}`)
  }
}

onMounted(() => {
  loadDiscount()
})
</script>

<style scoped>
.discount-container {
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

.discount-card {
  cursor: pointer;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  transition: transform 0.3s, box-shadow 0.3s;
  background: #fff;
}

.discount-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.discount-image {
  height: 130px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.discount-emoji {
  font-size: 42px;
}

.discount-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: #ff6b6b;
  color: #fff;
  padding: 2px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
}

.discount-info {
  padding: 12px 16px;
}

.discount-info h4 {
  margin: 0 0 6px 0;
  font-size: 15px;
}

.discount-price {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 4px;
}

.current-price {
  font-size: 18px;
  font-weight: 700;
  color: #f56c6c;
}

.original-price {
  font-size: 13px;
  color: #ccc;
  text-decoration: line-through;
}

.discount-meta {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 13px;
}

.rating {
  color: #ff9900;
}
</style>