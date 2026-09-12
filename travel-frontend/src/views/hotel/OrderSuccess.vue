<template>
  <div class="success-container">
    <!-- 迷你顶部 -->
    <div class="mini-header">
      <span class="logo" @click="goHome">✈️ 旅游平台</span>
      <div class="header-actions">
        <el-button type="text" @click="goHome">🏠 首页</el-button>
        <el-button type="text" @click="goOrders">📋 我的预订</el-button>
      </div>
    </div>

    <el-card class="success-card" v-loading="loading">
      <!-- 成功图标 -->
      <div class="success-icon">
        <el-icon :size="64" color="#67C23A"><CircleCheck /></el-icon>
      </div>

      <h1 class="success-title">🎉 预订成功！</h1>
      <p class="success-desc">您的酒店预订已提交，请完成支付以确认订单</p>

      <!-- ===== 移除倒计时，改为提示文字 ===== -->
      <div class="action-tip">
        <span style="color: #999; font-size: 14px;">请点击下方按钮完成支付或查看我的预订</span>
      </div>

      <el-divider />
      <div class="order-info" v-if="order.id">
        <div class="order-header">
          <div class="order-number">
            <span class="label">📋 订单号</span>
            <span class="value order-no">{{ order.orderNo }}</span>
            <el-button size="small" type="primary" text @click="copyOrderNo" style="margin-left: 8px;">📋 复制</el-button>
          </div>
          <el-tag type="warning" size="large">待支付</el-tag>
        </div>

        <el-row :gutter="20" class="order-row">
          <el-col :span="24">
            <div class="info-item">
              <span class="label">🏨 酒店名称</span>
              <span class="value hotel-name">{{ order.hotelName }}</span>
            </div>
          </el-col>
        </el-row>

        <el-row :gutter="20" class="order-row">
          <el-col :span="12">
            <div class="info-item">
              <span class="label">📅 入住日期</span>
              <span class="value">{{ order.checkInDate }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">📅 离店日期</span>
              <span class="value">{{ order.checkOutDate }}</span>
            </div>
          </el-col>
        </el-row>

        <el-row :gutter="20" class="order-row">
          <el-col :span="8">
            <div class="info-item">
              <span class="label">🏠 房型</span>
              <span class="value">{{ order.roomType }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">🛏️ 房间数</span>
              <span class="value">{{ order.quantity }} 间</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">🌙 入住天数</span>
              <span class="value">{{ order.nights }} 晚</span>
            </div>
          </el-col>
        </el-row>

        <div class="total-amount">
          <span class="label">💰 总金额</span>
          <span class="price">¥{{ order.totalAmount }}</span>
        </div>

        <el-divider />
        <div class="guest-info">
          <span style="font-weight: 500; font-size: 15px;">👤 入住人信息</span>
          <div class="guest-details">
            <span>{{ order.guestName }}</span>
            <span>{{ order.guestPhone }}</span>
            <span v-if="order.guestEmail">{{ order.guestEmail }}</span>
          </div>
          <div v-if="order.remark" class="guest-remark">📝 备注：{{ order.remark }}</div>
        </div>
      </div>

      <el-divider />

      <!-- 推荐酒店 -->
      <div v-if="recommendHotels.length > 0" class="recommend-section">
        <h3 class="recommend-title">🏨 同城其他推荐</h3>
        <el-row :gutter="16">
          <el-col :span="6" v-for="hotel in recommendHotels" :key="hotel.id">
            <div class="recommend-item" @click="goHotelDetail(hotel.id)">
              <div class="recommend-image">
                <el-image :src="getHotelImage(hotel.images)" fit="cover" style="width: 100%; height: 100px;">
                  <template #error>
                    <div style="height: 100px; display: flex; align-items: center; justify-content: center; background: #f0f0f0;">🏨</div>
                  </template>
                </el-image>
                <div class="recommend-price">¥{{ hotel.price }}/晚</div>
              </div>
              <div class="recommend-name">{{ hotel.name }}</div>
              <div class="recommend-rating">⭐ {{ hotel.rating }}</div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- ===== 操作按钮 ===== -->
      <div class="action-buttons">
        <el-button type="success" size="large" @click="goPay" style="border-radius: 8px; padding: 12px 40px; font-size: 16px;">
          💳 去支付
        </el-button>
        <el-button type="primary" size="large" @click="goOrders" style="border-radius: 8px; padding: 12px 40px; font-size: 16px;">
          📋 查看我的预订
        </el-button>
        <el-button size="large" @click="goHotelList" style="border-radius: 8px; padding: 12px 40px; font-size: 16px;">
          🏨 继续选酒店
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CircleCheck } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const order = ref({})
const recommendHotels = ref([])

const loadOrder = async () => {
  const orderId = route.query.orderId
  if (!orderId) return

  loading.value = true
  try {
    const res = await request({
      url: `/hotel-order/detail/${orderId}`,
      method: 'get'
    })
    if (res.code === 200) {
      order.value = res.data || {}
      if (order.value.city) {
        loadRecommendHotels(order.value.city)
      }
    }
  } catch (error) {
    console.error('加载订单失败', error)
  } finally {
    loading.value = false
  }
}

const loadRecommendHotels = async (city) => {
  try {
    const res = await request({
      url: '/hotel/search',
      method: 'get',
      params: { city: city, limit: 4 }
    })
    if (res.code === 200) {
      recommendHotels.value = (res.data || []).filter(item => item.id !== order.value.hotelId).slice(0, 4)
    }
  } catch (error) {
    console.error('加载推荐酒店失败', error)
  }
}

const getHotelImage = (images) => {
  if (!images) return ''
  try {
    const list = JSON.parse(images)
    return list[0] || ''
  } catch {
    return images
  }
}

// 复制订单号
const copyOrderNo = () => {
  if (!order.value.orderNo) return
  navigator.clipboard?.writeText(order.value.orderNo)
  ElMessage.success('已复制订单号')
}

// 去支付
const goPay = () => {
  router.push({
    path: '/payment',
    query: { orderId: order.value.id }
  })
}

// 跳转
const goOrders = () => {
  router.push('/profile')
}

const goHome = () => {
  router.push('/home')
}

const goHotelList = () => {
  router.push('/hotel')
}

const goHotelDetail = (id) => {
  router.push(`/hotel/detail/${id}`)
}

onMounted(() => {
  loadOrder()
})
</script>

<style scoped>
.success-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}
.mini-header {
  max-width: 700px;
  margin: 0 auto 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 4px;
}
.mini-header .logo {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
}
.mini-header .el-button { color: rgba(255,255,255,0.85); }
.mini-header .el-button:hover { color: #fff; }
.success-card {
  max-width: 700px;
  margin: 0 auto;
  border-radius: 16px;
  padding: 10px 20px 30px;
}
.success-icon { text-align: center; margin-top: 10px; }
.success-title { text-align: center; font-size: 28px; color: #333; margin: 16px 0 8px 0; }
.success-desc { text-align: center; color: #999; font-size: 15px; margin: 0 0 12px 0; }
.action-tip { text-align: center; padding: 6px 0; }
.order-info { padding: 4px 0; }
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 8px;
}
.order-number { display: flex; align-items: center; gap: 4px; }
.order-number .label { color: #999; font-size: 13px; }
.order-no { font-weight: 600; color: #409EFF; font-family: monospace; font-size: 16px; }
.order-row { padding: 4px 0; }
.info-item { display: flex; align-items: center; padding: 6px 0; }
.info-item .label { width: 100px; color: #999; font-size: 14px; flex-shrink: 0; }
.info-item .value { color: #333; font-size: 15px; flex: 1; }
.info-item .value.hotel-name { font-weight: 500; font-size: 16px; }
.total-amount {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-top: 2px dashed #e8e8e8;
  margin-top: 8px;
}
.total-amount .label { color: #999; font-size: 15px; }
.total-amount .price { color: #f56c6c; font-size: 28px; font-weight: 700; }
.guest-info { padding: 4px 0; }
.guest-details { display: flex; gap: 24px; padding: 8px 0; color: #333; flex-wrap: wrap; }
.guest-remark { color: #999; font-size: 13px; padding: 4px 0; }
.recommend-section { margin: 8px 0; }
.recommend-title { font-size: 16px; margin: 0 0 12px 0; color: #333; }
.recommend-item {
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #f0f0f0;
  transition: transform 0.3s, box-shadow 0.3s;
  background: #fff;
}
.recommend-item:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.recommend-image { position: relative; height: 100px; overflow: hidden; background: #f0f0f0; }
.recommend-price { position: absolute; bottom: 6px; right: 8px; background: rgba(0,0,0,0.7); color: #fff; padding: 2px 10px; border-radius: 10px; font-size: 13px; }
.recommend-name { padding: 8px 10px 2px; font-weight: 500; font-size: 14px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.recommend-rating { padding: 0 10px 8px; color: #ff9900; font-size: 13px; }
.action-buttons { display: flex; gap: 16px; justify-content: center; flex-wrap: wrap; margin-top: 12px; }
</style>