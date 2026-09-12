<template>
  <div class="detail-container" v-loading="loading">
    <div class="back-btn">
      <el-button type="primary" plain @click="goBack">
        <el-icon><ArrowLeft /></el-icon> 返回列表
      </el-button>
    </div>

    <div v-if="detail" class="detail-content">
      <div class="detail-header">
        <h1>{{ detail.name }}</h1>
        <div class="header-tags">
          <el-tag type="danger" size="large">{{ detail.category }}</el-tag>
          <el-tag type="info" size="large" style="margin-left: 8px;">{{ detail.cuisine }}</el-tag>
          <el-tag type="warning" size="large" style="margin-left: 8px;">⭐ {{ detail.rating }}</el-tag>
        </div>
      </div>

      <div class="tags-row" v-if="detail.tags">
        <el-tag v-for="tag in detail.tags.split(',')" :key="tag" size="small" type="warning" style="margin-right: 4px;">
          {{ tag }}
        </el-tag>
      </div>

      <div class="detail-image">
        <el-image :src="getImageUrl(detail.images)" fit="cover" style="width: 100%; height: 400px;">
          <template #error>
            <div class="image-placeholder">🍽️</div>
          </template>
        </el-image>
      </div>

      <el-row :gutter="20" class="info-row">
        <el-col :span="6">
          <div class="info-item">
            <div class="info-label">📍 地址</div>
            <div class="info-value">{{ detail.address }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="info-item">
            <div class="info-label">🏷️ 参考价格</div>
            <div class="info-value price">¥{{ detail.price }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="info-item">
            <div class="info-label">🏙️ 所在城市</div>
            <div class="info-value">{{ detail.city }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="info-item">
            <div class="info-label">🕐 营业时间</div>
            <div class="info-value">{{ detail.businessHours || '暂无' }}</div>
          </div>
        </el-col>
      </el-row>

      <el-card class="desc-card">
        <template #header>
          <span style="font-weight: bold;">📖 美食介绍</span>
        </template>
        <p class="description">{{ detail.description }}</p>
      </el-card>

      <!-- ===== 美食预订 ===== -->
      <el-card class="book-card" v-if="detail">
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span style="font-weight: bold; font-size: 16px;">🍜 美食预订</span>
            <span style="color: #999; font-size: 13px;">价格：¥{{ detail.price }}/份</span>
          </div>
        </template>

        <div v-if="!userStore.isLoggedIn()" style="text-align: center; padding: 20px;">
          <p style="color: #999;">请先 <el-link type="primary" @click="goLogin">登录</el-link> 后再预订</p>
        </div>

        <div v-else style="display: flex; justify-content: space-between; align-items: center; padding: 10px 0;">
          <div>
            <span style="color: #666;">套餐价格：</span>
            <span style="color: #f56c6c; font-size: 24px; font-weight: 700;">¥{{ detail.price }}</span>
            <span style="color: #999;">/份</span>
          </div>
          <el-button type="primary" size="large" @click="goBooking('food')" style="border-radius: 10px; padding: 12px 40px; font-size: 16px;">
            📋 立即预订
          </el-button>
        </div>
      </el-card>
    </div>

    <div v-else-if="!loading && !detail" class="empty-state">
      <el-empty description="美食不存在" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getFoodDetail } from '@/api/food'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const detail = ref(null)
const loading = ref(true)

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

const loadDetail = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('参数错误')
    router.push('/food')
    return
  }

  loading.value = true
  try {
    const res = await getFoodDetail(id)
    if (res.code === 200) {
      detail.value = res.data
    } else {
      ElMessage.error(res.message || '加载失败')
      router.push('/food')
    }
  } catch (error) {
    ElMessage.error('加载详情失败')
    router.push('/food')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/food')
}

const goLogin = () => {
  router.push('/login')
}

const goBooking = (type) => {
  if (!detail.value) return
  router.push({
    path: '/order/booking',
    query: { targetId: detail.value.id, type: type }
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
.header-tags { display: flex; flex-wrap: wrap; }
.tags-row { margin-bottom: 16px; }
.detail-image { width: 100%; height: 400px; border-radius: 8px; overflow: hidden; background: #f0f0f0; margin-bottom: 24px; }
.detail-image .el-image { width: 100%; height: 100%; }
.image-placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 64px; background: #f0f0f0; }
.info-row { margin-bottom: 24px; }
.info-item { background: #f8f9fa; border-radius: 8px; padding: 16px; text-align: center; }
.info-label { color: #999; font-size: 13px; margin-bottom: 4px; }
.info-value { font-size: 18px; font-weight: 500; color: #333; }
.info-value.price { color: #f56c6c; font-size: 22px; }
.desc-card { margin-top: 8px; }
.description { font-size: 15px; line-height: 1.8; color: #555; margin: 0; }
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