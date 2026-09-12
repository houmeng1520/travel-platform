<template>
  <div class="booking-container">
    <!-- 顶部导航 -->
    <el-header>
      <div class="header-content">
        <div class="header-left">
          <span class="logo" @click="goHome">✈️ 旅游平台</span>
        </div>
        <div class="header-right">
          <el-button type="primary" plain size="small" @click="goBack">← 返回酒店详情</el-button>
          <el-button type="primary" size="small" @click="goHome">🏠 首页</el-button>
        </div>
      </div>
    </el-header>

    <el-main>
      <div class="booking-wrapper">
        <!-- ===== 左侧：酒店信息卡片 ===== -->
        <div class="hotel-card">
          <div class="hotel-image">
            <el-image :src="getImageUrl(hotel?.images)" fit="cover">
              <template #error>
                <div class="image-placeholder">🏨</div>
              </template>
            </el-image>
          </div>
          <div class="hotel-body">
            <h2 class="hotel-name">{{ hotel?.name }}</h2>
            <div class="hotel-stars">
              <span v-for="i in (hotel?.starLevel || 0)" :key="i">⭐</span>
              <span class="rating">{{ hotel?.rating }}分</span>
            </div>
            <div class="hotel-info-line">
              <span class="info-label">📍 地址</span>
              <span class="info-text">{{ hotel?.address }}</span>
            </div>
            <div class="hotel-info-line">
              <span class="info-label">🏙️ 城市</span>
              <span class="info-text">{{ hotel?.city }}</span>
            </div>
            <div class="hotel-price-box">
              <span class="price-label">💰 参考价格</span>
              <span class="price-value">¥{{ hotel?.price }}</span>
              <span class="price-unit">/晚</span>
            </div>
            <div class="hotel-facilities" v-if="hotel?.facilities">
              <span class="fac-label">🛋️ 设施</span>
              <div class="fac-tags">
                <el-tag v-for="fac in hotel.facilities.split(',')" :key="fac" size="small">
                  {{ fac }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>

        <!-- ===== 右侧：预订表单 ===== -->
        <div class="form-card">
          <h2 class="form-title">📝 填写预订信息</h2>

          <!-- 未登录 -->
          <div v-if="!userStore.isLoggedIn()" class="login-tip">
            <el-empty description="请先登录后再预订">
              <el-button type="primary" @click="goLogin">去登录</el-button>
            </el-empty>
          </div>

          <!-- 表单 -->
          <div v-else class="form-body">
            <!-- 日期 -->
            <div class="form-row">
              <div class="form-group">
                <label>📅 入住日期 <span class="required">*</span></label>
                <el-date-picker 
                  v-model="orderForm.checkInDate" 
                  type="date" 
                  placeholder="请选择入住日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  :disabled-date="disabledCheckInDate"
                  style="width: 100%;"
                  clearable
                />
              </div>
              <div class="form-group">
                <label>📅 离店日期 <span class="required">*</span></label>
                <el-date-picker 
                  v-model="orderForm.checkOutDate" 
                  type="date" 
                  placeholder="请选择离店日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  :disabled-date="disabledCheckOutDate"
                  style="width: 100%;"
                  clearable
                />
              </div>
            </div>

            <!-- 房型 + 房间数 -->
            <div class="form-row">
              <div class="form-group">
                <label>🏠 房型</label>
                <el-select v-model="orderForm.roomType" placeholder="请选择房型" style="width: 100%;">
                  <el-option label="标准间" value="标准间" />
                  <el-option label="大床房" value="大床房" />
                  <el-option label="双床房" value="双床房" />
                  <el-option label="套房" value="套房" />
                </el-select>
              </div>
              <div class="form-group">
                <label>🛏️ 房间数</label>
                <el-input-number v-model="orderForm.quantity" :min="1" :max="10" style="width: 100%;" />
              </div>
            </div>

            <!-- 入住人信息 -->
            <div class="form-row">
              <div class="form-group">
                <label>👤 入住人 <span class="required">*</span></label>
                <el-input v-model="orderForm.guestName" placeholder="请输入入住人姓名" />
              </div>
              <div class="form-group">
                <label>📞 联系电话 <span class="required">*</span></label>
                <el-input v-model="orderForm.guestPhone" placeholder="请输入联系电话" />
              </div>
            </div>

            <!-- 邮箱 + 备注 -->
            <div class="form-row">
              <div class="form-group">
                <label>📧 邮箱</label>
                <el-input v-model="orderForm.guestEmail" placeholder="请输入邮箱（选填）" />
              </div>
              <div class="form-group">
                <label>📝 备注</label>
                <el-input v-model="orderForm.remark" placeholder="特殊要求备注（选填）" />
              </div>
            </div>

            <!-- 价格汇总 -->
            <div class="price-summary">
              <div class="price-row">
                <span>单价</span>
                <span>¥{{ hotel?.price }}/晚</span>
              </div>
              <div class="price-row">
                <span>入住天数</span>
                <span>{{ getNights }} 晚</span>
              </div>
              <div class="price-row">
                <span>房间数量</span>
                <span>{{ orderForm.quantity }} 间</span>
              </div>
              <div class="price-total">
                <span>💰 总价</span>
                <span>¥{{ calculateTotal }}</span>
              </div>
            </div>

            <!-- 提交 -->
            <el-button type="primary" size="large" @click="handleSubmitOrder" :loading="submitting" class="submit-btn">
              ✅ 确认预订
            </el-button>
          </div>
        </div>
      </div>
    </el-main>

    <!-- 底部 -->
    <Footer />
    <BackToTop />
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getHotelDetail } from '@/api/hotel'
import { createHotelOrder } from '@/api/hotelOrder'
import { useUserStore } from '@/stores/user'
import Footer from '@/components/Footer.vue'
import BackToTop from '@/components/BackToTop.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const hotel = ref(null)

const orderForm = reactive({
  checkInDate: null,
  checkOutDate: null,
  roomType: '标准间',
  quantity: 1,
  guestName: '',
  guestPhone: '',
  guestEmail: '',
  remark: ''
})

const getImageUrl = (images) => {
  if (!images) return ''
  try {
    const list = JSON.parse(images)
    return list[0] || ''
  } catch {
    return images
  }
}

const disabledCheckInDate = (time) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

const disabledCheckOutDate = (time) => {
  if (!orderForm.checkInDate) {
    return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
  }
  const checkInTime = new Date(orderForm.checkInDate).getTime()
  return time.getTime() <= checkInTime
}

const getNights = computed(() => {
  if (!orderForm.checkInDate || !orderForm.checkOutDate) return 0
  const diff = new Date(orderForm.checkOutDate) - new Date(orderForm.checkInDate)
  return Math.ceil(diff / (1000 * 60 * 60 * 24))
})

const calculateTotal = computed(() => {
  if (!hotel.value || !orderForm.checkInDate || !orderForm.checkOutDate) {
    return 0
  }
  const days = getNights.value
  if (days <= 0) return 0
  return (hotel.value.price * days * orderForm.quantity).toFixed(2)
})

const loadHotel = async () => {
  const id = route.query.hotelId
  if (!id) {
    ElMessage.error('参数错误')
    router.push('/hotel')
    return
  }

  loading.value = true
  try {
    const res = await getHotelDetail(id)
    if (res.code === 200) {
      hotel.value = res.data
    } else {
      ElMessage.error('加载酒店信息失败')
      router.push('/hotel')
    }
  } catch (error) {
    ElMessage.error('加载酒店信息失败')
    router.push('/hotel')
  } finally {
    loading.value = false
  }
}

const handleSubmitOrder = async () => {
  if (!orderForm.checkInDate || !orderForm.checkOutDate) {
    ElMessage.warning('请选择入住和离店日期')
    return
  }
  if (!orderForm.guestName) {
    ElMessage.warning('请输入入住人姓名')
    return
  }
  if (!orderForm.guestPhone) {
    ElMessage.warning('请输入联系电话')
    return
  }

  submitting.value = true
  try {
    const res = await createHotelOrder({
      hotelId: hotel.value.id,
      roomType: orderForm.roomType,
      checkInDate: orderForm.checkInDate,
      checkOutDate: orderForm.checkOutDate,
      quantity: orderForm.quantity,
      guestName: orderForm.guestName,
      guestPhone: orderForm.guestPhone,
      guestEmail: orderForm.guestEmail,
      remark: orderForm.remark
    })
    if (res.code === 200) {
      ElMessage.success('预订成功！')
      router.push({
        path: '/order-success',
        query: { orderId: res.data.id }
      })
    } else {
      ElMessage.error(res.message || '预订失败')
    }
  } catch (error) {
    ElMessage.error('预订失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  router.push(`/hotel/detail/${hotel.value?.id}`)
}

const goHome = () => {
  router.push('/home')
}

const goLogin = () => {
  router.push('/login')
}

onMounted(() => {
  loadHotel()
})
</script>

<style scoped>
.booking-container {
  min-height: 100vh;
  background: #f5f7fa;
}

/* ===== 顶部导航 ===== */
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

.header-right {
  display: flex;
  gap: 10px;
}

/* ===== 主内容 ===== */
.el-main {
  max-width: 1100px;
  margin: 0 auto;
  padding: 30px 24px;
}

.booking-wrapper {
  display: flex;
  gap: 30px;
  align-items: flex-start;
}

/* ===== 左侧酒店卡片 ===== */
.hotel-card {
  flex: 0 0 300px;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.hotel-image {
  height: 180px;
  overflow: hidden;
}

.hotel-image .el-image {
  width: 100%;
  height: 100%;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 20%, #764ba2 100%);
  color: #fff;
  font-size: 48px;
}

.hotel-body {
  padding: 16px 18px 20px;
}

.hotel-name {
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 4px 0;
}

.hotel-stars {
  font-size: 13px;
  color: #ffd93d;
  margin-bottom: 12px;
}

.hotel-stars .rating {
  color: #333;
  font-weight: 500;
  margin-left: 4px;
}

.hotel-info-line {
  display: flex;
  font-size: 13px;
  padding: 4px 0;
  color: #666;
  border-bottom: 1px solid #f5f5f5;
}

.hotel-info-line:last-of-type {
  border-bottom: none;
}

.hotel-info-line .info-label {
  color: #999;
  width: 65px;
  flex-shrink: 0;
}

.hotel-info-line .info-text {
  color: #333;
}

.hotel-price-box {
  margin: 10px 0 8px 0;
  padding: 10px 14px;
  background: #f8f9fa;
  border-radius: 8px;
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.hotel-price-box .price-label {
  color: #999;
  font-size: 13px;
}

.hotel-price-box .price-value {
  color: #f56c6c;
  font-size: 22px;
  font-weight: 700;
}

.hotel-price-box .price-unit {
  color: #999;
  font-size: 12px;
}

.hotel-facilities {
  margin-top: 8px;
}

.hotel-facilities .fac-label {
  display: block;
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.hotel-facilities .fac-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.hotel-facilities .el-tag {
  font-size: 11px;
}

/* ===== 右侧表单卡片 ===== */
.form-card {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  padding: 24px 30px 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.form-title {
  font-size: 18px;
  margin: 0 0 18px 0;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f0f0;
}

.login-tip {
  padding: 30px 0;
  text-align: center;
}

/* ===== 表单 ===== */
.form-body {
  max-width: 100%;
}

.form-row {
  display: flex;
  gap: 16px;
  margin-bottom: 14px;
}

.form-group {
  flex: 1;
}

.form-group label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.form-group .required {
  color: #f56c6c;
}

.form-group .el-input,
.form-group .el-select,
.form-group .el-date-editor,
.form-group .el-input-number {
  width: 100%;
}

/* ===== 价格汇总 ===== */
.price-summary {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 14px 18px;
  margin: 14px 0 16px 0;
}

.price-row {
  display: flex;
  justify-content: space-between;
  padding: 4px 0;
  color: #666;
  font-size: 14px;
}

.price-total {
  display: flex;
  justify-content: space-between;
  padding: 8px 0 0 0;
  border-top: 2px solid #e8e8e8;
  margin-top: 4px;
  font-weight: 600;
  font-size: 16px;
}

.price-total span:last-child {
  color: #f56c6c;
  font-size: 22px;
}

/* ===== 提交按钮 ===== */
.submit-btn {
  width: 100%;
  padding: 14px;
  font-size: 16px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  color: #fff;
}

.submit-btn:hover {
  opacity: 0.9;
  color: #fff;
}

/* ===== 响应式 ===== */
@media (max-width: 820px) {
  .booking-wrapper {
    flex-direction: column;
    align-items: stretch;
  }

  .hotel-card {
    flex: 1;
    width: 100%;
  }

  .hotel-image {
    height: 200px;
  }

  .form-card {
    padding: 20px;
  }

  .form-row {
    flex-direction: column;
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .header-content {
    flex-wrap: wrap;
    gap: 8px;
  }

  .header-right .el-button {
    font-size: 12px;
    padding: 6px 12px;
  }

  .hotel-card {
    border-radius: 8px;
  }

  .form-card {
    padding: 14px;
    border-radius: 8px;
  }

  .price-total span:last-child {
    font-size: 18px;
  }
}
</style>