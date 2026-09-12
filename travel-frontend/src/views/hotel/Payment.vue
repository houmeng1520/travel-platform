<template>
  <div class="payment-container">
    <!-- 顶部导航 -->
    <el-header>
      <div class="header-content">
        <div class="header-left">
          <span class="logo" @click="goHome">✈️ 旅游平台</span>
        </div>
        <div class="header-right">
          <el-button type="primary" plain size="small" @click="goHome">🏠 首页</el-button>
          <el-button type="primary" size="small" @click="goOrders">📋 我的预订</el-button>
        </div>
      </div>
    </el-header>

    <el-main>
      <div class="payment-wrapper">
        <div class="payment-title">
          <h1>💳 确认支付</h1>
          <p>请仔细核对订单信息，确认无误后支付</p>
        </div>

        <el-row :gutter="30">
          <el-col :span="16">
            <el-card class="order-card">
              <template #header>
                <span style="font-weight: bold; font-size: 16px;">📋 订单信息</span>
              </template>

              <div v-if="loading" style="text-align: center; padding: 40px;">
                <el-icon class="is-loading"><Loading /></el-icon> 加载中...
              </div>

              <div v-else-if="order" class="order-info">
                <div class="order-row">
                  <span class="label">订单号</span>
                  <span class="value order-no">{{ order.orderNo }}</span>
                </div>
                <div class="order-row">
                  <span class="label">订单类型</span>
                  <span class="value">
                    <el-tag :type="getOrderTypeTag(order.orderType)" size="small">
                      {{ getOrderTypeText(order.orderType) }}
                    </el-tag>
                  </span>
                </div>
                <div class="order-row">
                  <span class="label">名称</span>
                  <span class="value">{{ order.targetName || order.hotelName }}</span>
                </div>
                <!-- 酒店专用字段 -->
                <div class="order-row" v-if="order.orderType === 'hotel' || order.hotelName">
                  <span class="label">入住日期</span>
                  <span class="value">{{ order.checkInDate || order.orderDate }} ~ {{ order.checkOutDate || order.endDate }}</span>
                </div>
                <div class="order-row" v-else>
                  <span class="label">使用日期</span>
                  <span class="value">{{ order.orderDate }}</span>
                </div>
                <div class="order-row">
                  <span class="label">数量</span>
                  <span class="value">{{ order.quantity }} {{ getUnitText(order.orderType) }}</span>
                </div>
                <div class="order-row" v-if="order.orderType === 'hotel' || order.nights">
                  <span class="label">入住天数</span>
                  <span class="value">{{ order.nights }} 晚</span>
                </div>
                <div class="order-row">
                  <span class="label">联系人</span>
                  <span class="value">{{ order.guestName }}</span>
                </div>
                <div class="order-row">
                  <span class="label">联系电话</span>
                  <span class="value">{{ order.guestPhone }}</span>
                </div>
                <div class="order-row total-row">
                  <span class="label">💰 总金额</span>
                  <span class="value total-price">¥{{ order.totalAmount }}</span>
                </div>
              </div>

              <div v-else style="text-align: center; padding: 40px;">
                <el-empty description="订单不存在" />
              </div>
            </el-card>
          </el-col>

          <el-col :span="8">
            <el-card class="payment-card">
              <template #header>
                <span style="font-weight: bold; font-size: 16px;">💳 支付方式</span>
              </template>

              <div class="payment-methods">
                <div 
                  class="payment-method" 
                  :class="{ active: paymentMethod === 'wechat' }"
                  @click="paymentMethod = 'wechat'"
                >
                  <span class="method-icon">💚</span>
                  <span class="method-name">微信支付</span>
                  <span class="method-check" v-if="paymentMethod === 'wechat'">✓</span>
                </div>
                <div 
                  class="payment-method" 
                  :class="{ active: paymentMethod === 'alipay' }"
                  @click="paymentMethod = 'alipay'"
                >
                  <span class="method-icon">💙</span>
                  <span class="method-name">支付宝</span>
                  <span class="method-check" v-if="paymentMethod === 'alipay'">✓</span>
                </div>
                <div 
                  class="payment-method" 
                  :class="{ active: paymentMethod === 'card' }"
                  @click="paymentMethod = 'card'"
                >
                  <span class="method-icon">💳</span>
                  <span class="method-name">银行卡</span>
                  <span class="method-check" v-if="paymentMethod === 'card'">✓</span>
                </div>
              </div>

              <div class="payment-total">
                <span>需支付</span>
                <span class="total-price">¥{{ order?.totalAmount || 0 }}</span>
              </div>

              <el-button 
                type="primary" 
                size="large" 
                class="pay-btn"
                @click="handlePay"
                :loading="paying"
              >
                ✅ 确认支付
              </el-button>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-main>

    <!-- 底部 -->
    <Footer />
    <BackToTop />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { getOrderDetail, payOrder } from '@/api/order'
import Footer from '@/components/Footer.vue'
import BackToTop from '@/components/BackToTop.vue'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const paying = ref(false)
const order = ref(null)
const paymentMethod = ref('wechat')
const isHotelOrder = ref(false)

// ===== 订单类型转换 =====
const getOrderTypeText = (type) => {
  const map = { hotel: '酒店', scenic: '景点', food: '美食', route: '路线' }
  return map[type] || '酒店'
}

const getOrderTypeTag = (type) => {
  const map = { hotel: 'primary', scenic: 'success', food: 'warning', route: 'info' }
  return map[type] || 'primary'
}

const getUnitText = (type) => {
  const map = { hotel: '间', scenic: '张', food: '份', route: '人' }
  return map[type] || '份'
}

// ===== 加载订单 =====
const loadOrder = async () => {
  const orderId = route.query.orderId
  if (!orderId) {
    ElMessage.error('参数错误')
    router.push('/home')
    return
  }

  loading.value = true
  try {
    // 先尝试从统一订单接口获取
    try {
      const res = await getOrderDetail(orderId)
      if (res.code === 200 && res.data) {
        order.value = res.data
        isHotelOrder.value = false
        return
      }
    } catch (e) {
      // 统一订单接口失败，尝试酒店订单接口
    }

    // 如果统一订单接口失败，尝试酒店订单接口
    const res = await request({
      url: `/hotel-order/detail/${orderId}`,
      method: 'get'
    })
    if (res.code === 200) {
      const data = res.data
      // 将酒店订单数据转换为统一格式
      order.value = {
        id: data.id,
        orderNo: data.orderNo,
        orderType: 'hotel',
        targetName: data.hotelName,
        hotelName: data.hotelName,
        checkInDate: data.checkInDate,
        checkOutDate: data.checkOutDate,
        quantity: data.quantity,
        nights: data.nights,
        totalAmount: data.totalAmount,
        guestName: data.guestName,
        guestPhone: data.guestPhone,
        guestEmail: data.guestEmail,
        remark: data.remark,
        status: data.status
      }
      isHotelOrder.value = true
    } else {
      ElMessage.error(res.message || '加载订单失败')
      router.push('/home')
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
    router.push('/home')
  } finally {
    loading.value = false
  }
}

// ===== 支付 =====
const handlePay = async () => {
  if (!order.value) return

  await ElMessageBox.confirm(
    `确认支付 ¥${order.value.totalAmount}？`,
    '确认支付',
    {
      confirmButtonText: '确认支付',
      cancelButtonText: '取消',
      type: 'info'
    }
  )

  paying.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1500))

    // 根据订单类型调用不同的支付接口
    let res
    if (isHotelOrder.value) {
      // 酒店订单支付
      res = await request({
        url: `/hotel-order/pay/${order.value.id}`,
        method: 'put'
      })
    } else {
      // 统一订单支付
      res = await payOrder(order.value.id)
    }

    if (res.code === 200) {
      ElMessage.success('支付成功！')
      router.push({
        path: '/payment-success',
        query: { orderId: order.value.id }
      })
    } else {
      ElMessage.error(res.message || '支付失败')
    }
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付失败，请稍后重试')
  } finally {
    paying.value = false
  }
}

const goHome = () => {
  router.push('/home')
}

const goOrders = () => {
  router.push('/profile')
}

onMounted(() => {
  loadOrder()
})
</script>

<style scoped>
.payment-container {
  min-height: 100vh;
  background: #f5f7fa;
}
.el-header {
  background: #fff;
  border-bottom: 1px solid #e8ecf1;
  padding: 0 24px;
  height: 60px;
}
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  max-width: 1100px;
  margin: 0 auto;
}
.header-left .logo {
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.header-right { display: flex; gap: 10px; }
.el-main {
  max-width: 1100px;
  margin: 0 auto;
  padding: 30px 24px;
}
.payment-title { margin-bottom: 24px; }
.payment-title h1 { font-size: 26px; margin: 0 0 6px 0; color: #333; }
.payment-title p { color: #999; margin: 0; }
.order-card { border-radius: 12px; }
.order-info { padding: 4px 0; }
.order-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}
.order-row .label { color: #999; font-size: 14px; }
.order-row .value { color: #333; font-size: 14px; font-weight: 500; }
.order-row .value.order-no { color: #409EFF; font-family: monospace; font-size: 15px; }
.order-row.total-row { border-bottom: none; padding-top: 14px; }
.order-row.total-row .label { font-size: 16px; font-weight: 600; color: #333; }
.order-row.total-row .total-price { color: #f56c6c; font-size: 24px; font-weight: 700; }
.payment-card { border-radius: 12px; position: sticky; top: 84px; }
.payment-methods { margin-bottom: 16px; }
.payment-method {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border: 2px solid #e8ecf1;
  border-radius: 10px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.3s;
}
.payment-method:hover { border-color: #409EFF; }
.payment-method.active { border-color: #409EFF; background: #f0f7ff; }
.payment-method .method-icon { font-size: 24px; margin-right: 12px; }
.payment-method .method-name { flex: 1; font-weight: 500; }
.payment-method .method-check { color: #409EFF; font-weight: 700; font-size: 18px; }
.payment-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 0;
  border-top: 2px dashed #e8ecf1;
  margin: 4px 0 16px 0;
  font-size: 16px;
}
.payment-total .total-price { color: #f56c6c; font-size: 28px; font-weight: 700; }
.pay-btn {
  width: 100%;
  padding: 16px;
  font-size: 18px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
}
.pay-btn:hover { opacity: 0.9; }
@media (max-width: 768px) {
  .el-main { padding: 16px; }
  .payment-title h1 { font-size: 20px; }
  .payment-card { position: static; margin-top: 16px; }
  .order-row { flex-direction: column; gap: 4px; }
  .order-row .value { font-size: 15px; }
}
</style>