<template>
  <div class="success-container">
    <el-card class="success-card">
      <!-- 成功图标 -->
      <div class="success-icon">
        <el-icon :size="64" color="#67C23A"><CircleCheck /></el-icon>
      </div>

      <h1 class="success-title">🎉 支付成功！</h1>
      <p class="success-desc">您的订单已支付成功，酒店预订已确认</p>

      <el-divider />

      <div class="order-info" v-if="order">
        <div class="info-row">
          <span class="label">订单号</span>
          <span class="value order-no">{{ order.orderNo }}</span>
        </div>
        <div class="info-row">
          <span class="label">酒店名称</span>
          <span class="value">{{ order.hotelName }}</span>
        </div>
        <div class="info-row">
          <span class="label">入住日期</span>
          <span class="value">{{ order.checkInDate }} ~ {{ order.checkOutDate }}</span>
        </div>
        <div class="info-row">
          <span class="label">支付金额</span>
          <span class="value price">¥{{ order.totalAmount }}</span>
        </div>
        <div class="info-row">
          <span class="label">订单状态</span>
          <el-tag type="success" size="large">已确认</el-tag>
        </div>
      </div>

      <el-divider />

      <div class="action-buttons">
        <el-button type="primary" size="large" @click="goOrders" style="border-radius: 8px; padding: 12px 40px;">
          📋 查看我的预订
        </el-button>
        <el-button size="large" @click="goHome" style="border-radius: 8px; padding: 12px 40px;">
          🏠 返回首页
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { CircleCheck } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()

const order = ref(null)

const loadOrder = async () => {
  const orderId = route.query.orderId
  if (!orderId) return

  try {
    const res = await request({
      url: `/hotel-order/detail/${orderId}`,
      method: 'get'
    })
    if (res.code === 200) {
      order.value = res.data
    }
  } catch (error) {
    console.error('加载订单失败', error)
  }
}

const goOrders = () => {
  router.push('/profile')
}

const goHome = () => {
  router.push('/home')
}

onMounted(() => {
  loadOrder()
})
</script>

<style scoped>
.success-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.success-card {
  max-width: 500px;
  width: 100%;
  border-radius: 16px;
  padding: 10px 20px 30px;
}

.success-icon {
  text-align: center;
  margin-top: 10px;
}

.success-title {
  text-align: center;
  font-size: 28px;
  color: #333;
  margin: 16px 0 8px 0;
}

.success-desc {
  text-align: center;
  color: #999;
  font-size: 15px;
  margin: 0;
}

.order-info {
  padding: 4px 0;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
}

.info-row .label {
  color: #999;
  font-size: 14px;
}

.info-row .value {
  color: #333;
  font-size: 15px;
  font-weight: 500;
}

.info-row .value.order-no {
  color: #409EFF;
  font-family: monospace;
  font-size: 14px;
}

.info-row .value.price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: 700;
}

.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
  margin-top: 8px;
}
</style>