<template>
  <div class="detail-container" v-loading="loading">
    <div class="back-btn">
      <el-button type="primary" plain @click="goBack">
        <el-icon><ArrowLeft /></el-icon> 返回列表
      </el-button>
    </div>

    <div v-if="detail" class="detail-content">
      <div class="detail-header">
        <h1>{{ detail.route.title }}</h1>
        <div class="header-tags">
          <el-tag type="primary" size="large">{{ detail.route.days }}天</el-tag>
          <el-tag type="warning" size="large" style="margin-left: 8px;">⭐ {{ detail.route.rating }}</el-tag>
          <el-tag type="success" size="large" style="margin-left: 8px;">¥{{ detail.route.price }}</el-tag>
        </div>
      </div>

      <div class="city-info">📍 {{ detail.route.city }}</div>

      <el-card class="desc-card">
        <template #header>
          <span style="font-weight: bold;">📖 路线介绍</span>
        </template>
        <p class="description">{{ detail.route.description }}</p>
      </el-card>

      <h2 class="section-title">🗓️ 行程安排</h2>

      <div v-for="day in getDays(detail.scenics)" :key="day" class="day-section">
        <el-card class="day-card">
          <template #header>
            <span style="font-weight: bold; font-size: 16px;">第 {{ day }} 天</span>
          </template>

          <div v-for="item in getScenicsByDay(detail.scenics, day)" :key="item.scenic.id" class="day-item">
            <div class="item-icon">🏔️</div>
            <div class="item-content">
              <div class="item-name">{{ item.scenic.name }}</div>
              <div class="item-meta">
                <span class="item-address">{{ item.scenic.address }}</span>
                <span v-if="item.stayTime" class="item-time">⏱️ {{ item.stayTime }}</span>
              </div>
            </div>
            <div class="item-rating">⭐ {{ item.scenic.rating }}</div>
          </div>
        </el-card>
      </div>

      <h2 class="section-title">🍜 美食推荐</h2>
      <el-card class="food-card">
        <div v-for="item in detail.foods" :key="item.food.id" class="food-item">
          <div class="food-icon">🍽️</div>
          <div class="food-content">
            <div class="food-name">{{ item.food.name }}</div>
            <div class="food-meta">
              <span class="food-tag">{{ item.mealType }}</span>
              <span class="food-cuisine">{{ item.food.cuisine }}</span>
              <span v-if="item.recommendation" class="food-recommend">💡 {{ item.recommendation }}</span>
            </div>
          </div>
          <div class="food-day">第 {{ item.dayIndex }} 天</div>
        </div>
      </el-card>

      <!-- ===== 路线报名 ===== -->
      <el-card class="book-card" v-if="detail">
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span style="font-weight: bold; font-size: 16px;">🗺️ 路线报名</span>
            <span style="color: #999; font-size: 13px;">价格：¥{{ detail.route.price }}/人</span>
          </div>
        </template>

        <div v-if="!userStore.isLoggedIn()" style="text-align: center; padding: 20px;">
          <p style="color: #999;">请先 <el-link type="primary" @click="goLogin">登录</el-link> 后再报名</p>
        </div>

        <div v-else style="display: flex; justify-content: space-between; align-items: center; padding: 10px 0;">
          <div>
            <span style="color: #666;">路线价格：</span>
            <span style="color: #f56c6c; font-size: 24px; font-weight: 700;">¥{{ detail.route.price }}</span>
            <span style="color: #999;">/人</span>
          </div>
          <el-button type="primary" size="large" @click="goBooking('route')" style="border-radius: 10px; padding: 12px 40px; font-size: 16px;">
            📋 立即报名
          </el-button>
        </div>
      </el-card>
    </div>

    <div v-else-if="!loading && !detail" class="empty-state">
      <el-empty description="路线不存在" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getRouteDetail } from '@/api/route'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const detail = ref(null)
const loading = ref(true)

const getDays = (scenics) => {
  if (!scenics || scenics.length === 0) return []
  const days = new Set()
  scenics.forEach(item => days.add(item.dayIndex))
  return Array.from(days).sort()
}

const getScenicsByDay = (scenics, day) => {
  if (!scenics) return []
  return scenics.filter(item => item.dayIndex === day)
}

const loadDetail = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('参数错误')
    router.push('/route')
    return
  }

  loading.value = true
  try {
    const res = await getRouteDetail(id)
    if (res.code === 200) {
      detail.value = res.data
    } else {
      ElMessage.error(res.message || '加载失败')
      router.push('/route')
    }
  } catch (error) {
    ElMessage.error('加载详情失败')
    router.push('/route')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/route')
}

const goLogin = () => {
  router.push('/login')
}

const goBooking = (type) => {
  if (!detail.value) return
  router.push({
    path: '/order/booking',
    query: { targetId: detail.value.route.id, type: type }
  })
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.detail-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 30px 20px;
  min-height: 100vh;
  background: #f5f7fa;
}
.back-btn { margin-bottom: 20px; }
.detail-content { background: #fff; border-radius: 12px; padding: 30px; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
.detail-header { display: flex; align-items: center; gap: 16px; margin-bottom: 12px; flex-wrap: wrap; }
.detail-header h1 { margin: 0; font-size: 28px; flex: 1; }
.city-info { color: #666; font-size: 15px; margin-bottom: 20px; }
.desc-card { margin-bottom: 30px; }
.description { font-size: 15px; line-height: 1.8; color: #555; margin: 0; }
.section-title { font-size: 20px; margin: 30px 0 16px 0; color: #333; border-left: 4px solid #409EFF; padding-left: 12px; }
.day-section { margin-bottom: 16px; }
.day-card { background: #f8f9fa; }
.day-item { display: flex; align-items: center; padding: 12px 0; border-bottom: 1px solid #e8e8e8; }
.day-item:last-child { border-bottom: none; }
.item-icon { font-size: 24px; margin-right: 16px; flex-shrink: 0; }
.item-content { flex: 1; }
.item-name { font-weight: 500; font-size: 16px; }
.item-meta { color: #999; font-size: 13px; margin-top: 2px; }
.item-time { margin-left: 12px; }
.item-rating { color: #ff9900; font-weight: 500; }
.food-card { background: #f8f9fa; margin-top: 8px; }
.food-item { display: flex; align-items: center; padding: 12px 0; border-bottom: 1px solid #e8e8e8; }
.food-item:last-child { border-bottom: none; }
.food-icon { font-size: 24px; margin-right: 16px; flex-shrink: 0; }
.food-content { flex: 1; }
.food-name { font-weight: 500; font-size: 16px; }
.food-meta { color: #666; font-size: 13px; margin-top: 2px; display: flex; flex-wrap: wrap; gap: 8px; }
.food-tag { background: #fff3e0; color: #e67e22; padding: 0 8px; border-radius: 4px; font-size: 12px; }
.food-cuisine { color: #999; }
.food-recommend { color: #409EFF; }
.food-day { color: #409EFF; font-weight: 500; font-size: 14px; }
.empty-state { padding: 60px 0; text-align: center; background: #fff; border-radius: 12px; }

.book-card {
  margin-top: 20px;
  border-radius: 12px;
}
.book-card .el-card__header {
  border-bottom: 1px solid #f0f0f0;
  padding: 16px 20px;
}
</style>