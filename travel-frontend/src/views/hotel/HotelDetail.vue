<template>
  <div class="detail-container" v-loading="loading">
    <div class="back-btn">
      <el-button type="primary" plain @click="goBack">
        <el-icon><ArrowLeft /></el-icon> 返回列表
      </el-button>
    </div>

    <div v-if="detail" class="detail-content">
      <!-- ===== 酒店基本信息 ===== -->
      <div class="detail-header">
        <h1>{{ detail.name }}</h1>
        <div class="header-tags">
          <el-tag type="warning" size="large">⭐ {{ detail.rating }}</el-tag>
          <el-tag type="success" size="large" style="margin-left: 8px;">¥{{ detail.price }}/晚</el-tag>
          <el-tag type="primary" size="large" style="margin-left: 8px;">{{ detail.starLevel }}星级</el-tag>
        </div>
      </div>

      <div class="detail-image">
        <el-image :src="getImageUrl(detail.images)" fit="cover" style="width: 100%; height: 400px;">
          <template #error>
            <div class="image-placeholder">🏨</div>
          </template>
        </el-image>
      </div>

      <el-row :gutter="20" class="info-row">
        <el-col :span="8">
          <div class="info-item">
            <div class="info-label">📍 地址</div>
            <div class="info-value">{{ detail.address }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <div class="info-label">📞 电话</div>
            <div class="info-value">{{ detail.phone || '暂无' }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <div class="info-label">🏙️ 城市</div>
            <div class="info-value">{{ detail.city }}</div>
          </div>
        </el-col>
      </el-row>

      <el-card class="facility-card" v-if="detail.facilities">
        <template #header>
          <span style="font-weight: bold;">🛋️ 酒店设施</span>
        </template>
        <el-tag v-for="fac in detail.facilities.split(',')" :key="fac" style="margin-right: 8px; margin-bottom: 4px;" size="large">
          {{ fac }}
        </el-tag>
      </el-card>

      <el-card class="desc-card">
        <template #header>
          <span style="font-weight: bold;">📖 酒店介绍</span>
        </template>
        <p class="description">{{ detail.description }}</p>
      </el-card>

      <!-- ===== 预订酒店 ===== -->
      <el-card class="book-card">
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span style="font-weight: bold; font-size: 16px;">📅 酒店预订</span>
            <span style="color: #999; font-size: 13px;">价格：¥{{ detail?.price }}/晚</span>
          </div>
        </template>

        <div v-if="!userStore.isLoggedIn()" style="text-align: center; padding: 20px;">
          <p style="color: #999;">请先 <el-link type="primary" @click="goLogin">登录</el-link> 后再预订</p>
        </div>

        <div v-else style="text-align: center; padding: 20px 0;">
          <div style="margin-bottom: 12px;">
            <span style="color: #666;">选择日期和房型，</span>
            <span style="color: #f56c6c; font-weight: bold;">¥{{ detail?.price }}/晚</span>
          </div>
          <el-button type="primary" size="large" @click="goBooking" style="border-radius: 10px; padding: 14px 60px; font-size: 18px;">
            📋 立即预订
          </el-button>
        </div>
      </el-card>

      <!-- ===== 用户评价 ===== -->
      <el-card class="review-card">
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span style="font-weight: bold; font-size: 16px;">💬 用户评价</span>
            <span style="color: #999; font-size: 13px;">共 {{ reviewList.length }} 条评价</span>
          </div>
        </template>

        <!-- 评价统计 -->
        <div v-if="reviewStats.total > 0" class="review-stats">
          <div class="stats-left">
            <div class="avg-score">{{ reviewStats.avgRating }}</div>
            <div class="avg-label">综合评分</div>
            <el-rate v-model="reviewStats.avgRating" disabled show-score text-color="#ff9900" />
          </div>
          <div class="stats-right">
            <div v-for="i in 5" :key="i" class="rating-bar">
              <span class="rating-label">{{ 6 - i }}星</span>
              <el-progress 
                :percentage="getRatingPercent(6 - i)" 
                :stroke-width="8"
                :show-text="false"
                color="#ff9900"
              />
              <span class="rating-count">{{ reviewStats.ratingCounts?.[6 - i] || 0 }}</span>
            </div>
          </div>
        </div>

        <el-divider v-if="reviewStats.total > 0" />

        <!-- 评价列表 -->
        <div v-if="reviewLoading" style="text-align: center; padding: 20px;">
          <el-icon class="is-loading"><Loading /></el-icon> 加载中...
        </div>

        <div v-else-if="reviewList.length > 0">
          <div v-for="item in reviewList" :key="item.id" class="review-item">
            <div class="review-header">
              <el-avatar :size="36" :src="getAvatarUrl(item.avatar)">
                {{ item.username?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <div class="review-user">
                <div class="review-username">{{ item.username }}</div>
                <div class="review-time">{{ formatDate(item.createTime) }}</div>
              </div>
              <el-rate v-model="item.rating" disabled :colors="['#99A9BF', '#F7BA2A', '#FF9900']" />
            </div>
            <div class="review-content">{{ item.content }}</div>
          </div>
        </div>

        <div v-else style="text-align: center; padding: 20px; color: #999;">
          暂无评价，快来发表第一条评价吧～
        </div>
      </el-card>
    </div>

    <div v-else-if="!loading && !detail" class="empty-state">
      <el-empty description="酒店不存在" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Loading } from '@element-plus/icons-vue'
import { getHotelDetail } from '@/api/hotel'
import { getHotelReviews, getHotelReviewStats } from '@/api/review'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const detail = ref(null)
const loading = ref(true)

// ===== 评价相关 =====
const reviewList = ref([])
const reviewStats = ref({ total: 0, avgRating: 0, ratingCounts: [] })
const reviewLoading = ref(false)

// ===== 图片工具 =====
const getImageUrl = (images) => {
  if (!images) return ''
  try {
    const list = JSON.parse(images)
    return list[0] || ''
  } catch {
    return images
  }
}

// ===== 头像工具 =====
const getAvatarUrl = (avatar) => {
  if (!avatar) return ''
  if (avatar.startsWith('http')) return avatar
  if (avatar.startsWith('/uploads')) return `http://localhost:8080${avatar}`
  return avatar
}

// ===== 日期格式化 =====
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// ===== 计算评分百分比 =====
const getRatingPercent = (star) => {
  if (!reviewStats.value.total) return 0
  const count = reviewStats.value.ratingCounts?.[star] || 0
  return Math.round((count / reviewStats.value.total) * 100)
}

// ===== 加载酒店详情 =====
const loadDetail = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('参数错误')
    router.push('/hotel')
    return
  }

  loading.value = true
  try {
    const res = await getHotelDetail(id)
    if (res.code === 200) {
      detail.value = res.data
      // 详情加载完成后加载评价
      loadReviews(id)
    } else {
      ElMessage.error(res.message || '加载失败')
      router.push('/hotel')
    }
  } catch (error) {
    ElMessage.error('加载详情失败')
    router.push('/hotel')
  } finally {
    loading.value = false
  }
}

// ===== 加载评价 =====
const loadReviews = async (hotelId) => {
  reviewLoading.value = true
  try {
    const [reviewsRes, statsRes] = await Promise.all([
      getHotelReviews(hotelId),
      getHotelReviewStats(hotelId)
    ])
    if (reviewsRes.code === 200) {
      reviewList.value = reviewsRes.data || []
    }
    if (statsRes.code === 200) {
      reviewStats.value = statsRes.data || { total: 0, avgRating: 0, ratingCounts: [] }
    }
  } catch (error) {
    console.error('加载评价失败', error)
  } finally {
    reviewLoading.value = false
  }
}

// ===== 跳转 =====
const goBack = () => {
  router.push('/hotel')
}

const goLogin = () => {
  router.push('/login')
}

const goBooking = () => {
  router.push({
    path: '/hotel/booking',
    query: { hotelId: detail.value.id }
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
.detail-header { display: flex; align-items: center; gap: 16px; margin-bottom: 20px; flex-wrap: wrap; }
.detail-header h1 { margin: 0; font-size: 28px; flex: 1; }
.header-tags { display: flex; flex-wrap: wrap; }
.detail-image { width: 100%; height: 400px; border-radius: 8px; overflow: hidden; background: #f0f0f0; margin-bottom: 24px; }
.detail-image .el-image { width: 100%; height: 100%; }
.image-placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 64px; background: #f0f0f0; }
.info-row { margin-bottom: 24px; }
.info-item { background: #f8f9fa; border-radius: 8px; padding: 16px; text-align: center; }
.info-label { color: #999; font-size: 13px; margin-bottom: 4px; }
.info-value { font-size: 18px; font-weight: 500; color: #333; }
.facility-card { margin-bottom: 20px; }
.desc-card { margin-top: 8px; }
.description { font-size: 15px; line-height: 1.8; color: #555; margin: 0; }
.empty-state { padding: 60px 0; text-align: center; background: #fff; border-radius: 12px; }

/* ===== 预订卡片 ===== */
.book-card {
  margin-top: 20px;
  border-radius: 12px;
}
.book-card .el-card__header {
  border-bottom: 1px solid #f0f0f0;
  padding: 16px 20px;
}

/* ===== 评价卡片 ===== */
.review-card {
  margin-top: 20px;
  border-radius: 12px;
}
.review-card .el-card__header {
  border-bottom: 1px solid #f0f0f0;
  padding: 16px 20px;
}

.review-stats {
  display: flex;
  gap: 40px;
  padding: 16px 0;
  align-items: center;
}

.stats-left {
  text-align: center;
  flex-shrink: 0;
  width: 140px;
}

.avg-score {
  font-size: 42px;
  font-weight: 700;
  color: #ff9900;
  line-height: 1;
}

.avg-label {
  color: #999;
  font-size: 13px;
  margin: 6px 0;
}

.stats-right {
  flex: 1;
}

.rating-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 0;
}

.rating-label {
  width: 40px;
  color: #999;
  font-size: 13px;
  flex-shrink: 0;
}

.rating-bar .el-progress {
  flex: 1;
}

.rating-count {
  width: 30px;
  text-align: right;
  color: #999;
  font-size: 13px;
  flex-shrink: 0;
}

.review-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.review-user {
  flex: 1;
}

.review-username {
  font-weight: 500;
  font-size: 14px;
}

.review-time {
  color: #999;
  font-size: 12px;
}

.review-content {
  color: #555;
  font-size: 14px;
  line-height: 1.6;
  padding-left: 48px;
}
</style>