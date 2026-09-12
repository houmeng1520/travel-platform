<template>
  <div class="profile-container">
    <!-- 顶部导航 -->
    <el-header>
      <div class="header-content">
        <h1>👤 个人中心</h1>
        <el-button type="primary" @click="goHome">返回首页</el-button>
      </div>
    </el-header>

    <el-main>
      <el-row :gutter="20">
        <!-- ===== 左侧：个人信息 ===== -->
        <el-col :span="6">
          <el-card class="profile-card">
            <div class="avatar-section">
              <div class="avatar-wrapper">
                <el-avatar :size="80" :src="userAvatar" class="user-avatar">
                  {{ userStore.username?.charAt(0)?.toUpperCase() }}
                </el-avatar>
                <div class="avatar-upload-btn" @click="triggerUpload">
                  <el-icon><Camera /></el-icon>
                </div>
              </div>
              <h3>{{ userStore.userInfo?.nickname || userStore.username }}</h3>
              <p class="role-tag">
                <el-tag :type="userStore.role === 'ADMIN' ? 'danger' : 'info'" size="small">
                  {{ userStore.role === 'ADMIN' ? '管理员' : '普通用户' }}
                </el-tag>
              </p>
            </div>
            <el-divider />
            <div class="info-item">
              <span class="label">用户名</span>
              <span class="value">{{ userStore.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">昵称</span>
              <span class="value">{{ userStore.userInfo?.nickname || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱</span>
              <span class="value">{{ userStore.userInfo?.email || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">手机号</span>
              <span class="value">{{ userStore.userInfo?.phone || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">性别</span>
              <span class="value">{{ getGenderText(userStore.userInfo?.gender) }}</span>
            </div>
            <el-button 
              type="primary" 
              plain 
              size="small" 
              style="width: 100%; margin-top: 12px;" 
              @click="showEditDialog = true"
            >
              ✏️ 编辑资料
            </el-button>
          </el-card>
        </el-col>

        <!-- ===== 右侧：订单 + 评价 ===== -->
        <el-col :span="18">
          <el-tabs v-model="activeTab" class="profile-tabs">
            <!-- ===== 我的订单 ===== -->
            <el-tab-pane label="📋 我的订单" name="orders">
              <div class="tab-header">
                <div style="display: flex; align-items: center; gap: 8px; flex-wrap: wrap;">
                  <el-radio-group v-model="filterType" size="small" @change="loadOrders">
                    <el-radio-button label="">全部</el-radio-button>
                    <el-radio-button label="hotel">酒店</el-radio-button>
                    <el-radio-button label="scenic">景点</el-radio-button>
                    <el-radio-button label="food">美食</el-radio-button>
                    <el-radio-button label="route">路线</el-radio-button>
                  </el-radio-group>
                  <el-button type="primary" size="small" @click="loadOrders" :loading="loading">
                    🔄 刷新
                  </el-button>
                </div>
              </div>

              <!-- 加载中 -->
              <div v-if="loading" style="text-align: center; padding: 30px;">
                <el-icon class="is-loading"><Loading /></el-icon> 加载中...
              </div>

              <!-- 订单列表 -->
              <div v-else-if="orderList.length > 0">
                <div v-for="order in orderList" :key="order.id" class="order-item">
                  <el-row :gutter="16" align="middle">
                    <el-col :span="5">
                      <div class="order-type">
                        <el-tag :type="getTypeTag(order.orderType)" size="small">
                          {{ getTypeText(order.orderType) }}
                        </el-tag>
                      </div>
                      <div class="order-name">{{ order.targetName }}</div>
                    </el-col>
                    <el-col :span="5">
                      <div class="order-date">
                        📅 {{ order.orderDate }}
                        <span v-if="order.endDate">~ {{ order.endDate }}</span>
                      </div>
                      <div class="order-detail">
                        {{ order.quantity }} {{ getUnitText(order.orderType) }}
                        <span v-if="order.nights && order.nights > 1"> × {{ order.nights }}晚</span>
                      </div>
                    </el-col>
                    <el-col :span="3">
                      <div class="order-price">¥{{ order.totalAmount }}</div>
                    </el-col>
                    <el-col :span="4">
                      <el-tag :type="getStatusType(order.status)" size="small">
                        {{ getStatusText(order.status) }}
                      </el-tag>
                    </el-col>
                    <el-col :span="7" style="text-align: right;">
                      <!-- 待支付 → 去支付 + 取消 -->
                      <el-button 
                        v-if="order.status === 0" 
                        type="success" 
                        size="small" 
                        @click="goPay(order.id)"
                      >
                        💳 支付
                      </el-button>
                      <el-button 
                        v-if="order.status === 0" 
                        type="danger" 
                        size="small" 
                        @click="handleCancel(order.id)"
                      >
                        取消
                      </el-button>
                      
                      <!-- 已支付 → 去使用 + 取消 -->
                      <el-button 
                        v-if="order.status === 1" 
                        type="primary" 
                        size="small" 
                        @click="openUse(order)"
                      >
                        📱 去使用
                      </el-button>
                      <el-button 
                        v-if="order.status === 1" 
                        type="danger" 
                        size="small" 
                        @click="handleCancel(order.id)"
                      >
                        取消
                      </el-button>
                      
                      <!-- 已完成 → 评价（所有类型都显示） -->
                      <el-button 
                        v-if="order.status === 3" 
                        type="warning" 
                        size="small" 
                        @click="openReview(order)"
                      >
                        ✍️ 评价
                      </el-button>
                      
                      <!-- 已取消 → 显示状态 -->
                      <el-button 
                        v-if="order.status === 4" 
                        type="info" 
                        size="small" 
                        disabled
                      >
                        已取消
                      </el-button>
                    </el-col>
                  </el-row>
                </div>
              </div>

              <!-- 空状态 -->
              <div v-else style="text-align: center; padding: 40px;">
                <el-empty description="暂无订单" />
              </div>
            </el-tab-pane>

            <!-- ===== 我的评价 ===== -->
            <el-tab-pane label="💬 我的评价" name="reviews">
              <div class="tab-header">
                <el-button type="primary" size="small" @click="loadMyReviews" :loading="reviewLoading">
                  🔄 刷新
                </el-button>
              </div>

              <div v-if="reviewLoading" style="text-align: center; padding: 30px;">
                <el-icon class="is-loading"><Loading /></el-icon> 加载中...
              </div>

              <div v-else-if="myReviews.length > 0">
                <div v-for="review in myReviews" :key="review.id" class="my-review-item">
                  <div class="review-header">
                    <div class="review-info">
                      <div class="review-hotel-name">
                        <el-tag :type="getTypeTag(review.orderType)" size="small" style="margin-right: 6px;">
                          {{ getTypeText(review.orderType) }}
                        </el-tag>
                        {{ review.targetName || '未知' }}
                      </div>
                      <div class="review-order-no">
                        订单号：{{ review.orderNo || '-' }}
                      </div>
                      <div class="review-meta">
                        <el-rate v-model="review.rating" disabled :colors="['#99A9BF', '#F7BA2A', '#FF9900']" />
                        <span class="review-time">{{ formatDate(review.createTime) }}</span>
                      </div>
                    </div>
                    <el-button type="danger" size="small" text @click="handleDeleteReview(review.id)">
                      🗑️ 删除
                    </el-button>
                  </div>
                  <div class="review-content">{{ review.content }}</div>
                </div>
              </div>

              <div v-else style="text-align: center; padding: 40px;">
                <el-empty description="暂无评价" />
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-col>
      </el-row>
    </el-main>

    <!-- 底部 -->
    <Footer />
    <BackToTop />

    <!-- ===== 编辑资料对话框 ===== -->
    <EditProfileDialog 
      v-model="showEditDialog" 
      :user-info="userStore.userInfo || {}"
      @success="loadOrders"
    />

    <!-- ===== 评价对话框 ===== -->
    <ReviewDialog 
      v-model="showReviewDialog" 
      :order-id="currentOrder?.id" 
      :order-type="currentOrder?.orderType"
      @success="onReviewSuccess"
    />

    <!-- ===== 使用订单对话框 ===== -->
    <UseOrderDialog 
      v-model="showUseDialog" 
      :order="currentUseOrder"
      @success="onUseSuccess"
    />

    <!-- ===== 隐藏的文件上传 ===== -->
    <input 
      type="file" 
      ref="fileInput" 
      accept="image/*" 
      style="display: none;" 
      @change="handleFileChange"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading, Camera } from '@element-plus/icons-vue'
import { getMyOrders, cancelOrder } from '@/api/order'
import { getUserReviews, deleteReview } from '@/api/review'
import { uploadAvatar } from '@/api/user'
import Footer from '@/components/Footer.vue'
import BackToTop from '@/components/BackToTop.vue'
import EditProfileDialog from '@/components/EditProfileDialog.vue'
import ReviewDialog from '@/components/ReviewDialog.vue'
import UseOrderDialog from '@/components/UseOrderDialog.vue'

const router = useRouter()
const userStore = useUserStore()

// ===== 标签页 =====
const activeTab = ref('orders')

// ===== 订单数据 =====
const loading = ref(false)
const orderList = ref([])
const filterType = ref('')

// ===== 评价数据 =====
const reviewLoading = ref(false)
const myReviews = ref([])

// ===== 对话框 =====
const showEditDialog = ref(false)
const showReviewDialog = ref(false)
const showUseDialog = ref(false)
const currentOrder = ref(null)
const currentUseOrder = ref(null)
const fileInput = ref(null)

// ===== 头像URL =====
const userAvatar = computed(() => {
  if (userStore.userInfo?.avatar) {
    if (userStore.userInfo.avatar.startsWith('/uploads')) {
      return `http://localhost:8080${userStore.userInfo.avatar}`
    }
    return userStore.userInfo.avatar
  }
  return ''
})

// ===== 订单类型转换 =====
const getTypeText = (type) => {
  const map = { hotel: '酒店', scenic: '景点', food: '美食', route: '路线' }
  return map[type] || type
}

const getTypeTag = (type) => {
  const map = { hotel: 'primary', scenic: 'success', food: 'warning', route: 'info' }
  return map[type] || 'info'
}

const getUnitText = (type) => {
  const map = { hotel: '间', scenic: '张', food: '份', route: '人' }
  return map[type] || '份'
}

const getStatusText = (status) => {
  const map = { 0: '待支付', 1: '已支付', 2: '已使用', 3: '已完成', 4: '已取消' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'primary', 3: 'info', 4: 'danger' }
  return map[status] || 'info'
}

const getGenderText = (gender) => {
  const map = { 0: '未知', 1: '男', 2: '女' }
  return map[gender] || '未知'
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

// ===== 加载订单 =====
const loadOrders = async () => {
  loading.value = true
  try {
    const res = await getMyOrders(filterType.value)
    if (res.code === 200) {
      orderList.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

// ===== 加载我的评价 =====
const loadMyReviews = async () => {
  reviewLoading.value = true
  try {
    const res = await getUserReviews()
    if (res.code === 200) {
      myReviews.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载评价失败')
  } finally {
    reviewLoading.value = false
  }
}

// ===== 取消订单 =====
const handleCancel = (id) => {
  ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await cancelOrder(id)
      if (res.code === 200) {
        ElMessage.success('已取消订单')
        loadOrders()
      } else {
        ElMessage.error(res.message || '取消失败')
      }
    } catch (error) {
      ElMessage.error('取消失败')
    }
  }).catch(() => {})
}

// ===== 删除评价 =====
const handleDeleteReview = (id) => {
  ElMessageBox.confirm('确定要删除该评价吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteReview(id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadMyReviews()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// ===== 去支付 =====
const goPay = (id) => {
  router.push({
    path: '/payment',
    query: { orderId: id }
  })
}

// ===== 打开评价对话框 =====
const openReview = (order) => {
  currentOrder.value = order
  showReviewDialog.value = true
}

// ===== 评价成功回调 =====
const onReviewSuccess = () => {
  ElMessage.success('评价成功')
  loadOrders()
  loadMyReviews()
}

// ===== 打开使用对话框 =====
const openUse = (order) => {
  currentUseOrder.value = order
  showUseDialog.value = true
}

// ===== 使用成功回调 =====
const onUseSuccess = () => {
  loadOrders()
}

// ===== 头像上传 =====
const triggerUpload = () => {
  fileInput.value?.click()
}

const handleFileChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请上传图片文件')
    return
  }

  if (file.size > 2 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过2MB')
    return
  }

  try {
    const res = await uploadAvatar(file)
    if (res.code === 200) {
      ElMessage.success('头像上传成功')
      const updatedUser = {
        ...userStore.userInfo,
        avatar: res.data
      }
      userStore.userInfo = updatedUser
      localStorage.setItem('userInfo', JSON.stringify(updatedUser))
      loadOrders()
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (error) {
    ElMessage.error('上传失败，请稍后重试')
  }

  event.target.value = ''
}

// ===== 跳转 =====
const goHome = () => {
  router.push('/home')
}

// ===== 标签切换时加载对应数据 =====
watch(activeTab, (val) => {
  if (val === 'reviews' && myReviews.value.length === 0) {
    loadMyReviews()
  }
})

// ===== 生命周期 =====
onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  background: #f5f7fa;
}

/* ===== 顶部导航 ===== */
.el-header {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 24px;
}
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
}
.header-content h1 {
  font-size: 20px;
  margin: 0;
}

/* ===== 主内容 ===== */
.el-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  width: 100%;
}

/* ===== 个人信息卡片 ===== */
.profile-card {
  text-align: center;
}
.avatar-section { padding: 12px 0; }
.avatar-wrapper {
  position: relative;
  display: inline-block;
}
.user-avatar { border: 3px solid #e8ecf1; }
.avatar-upload-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #409EFF;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 14px;
}
.avatar-upload-btn:hover { background: #66b1ff; }
.avatar-section h3 { margin: 12px 0 4px 0; }
.role-tag { margin: 0; }
.info-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
}
.info-item .label { color: #999; }
.info-item .value { color: #333; font-weight: 500; }

/* ===== 标签页 ===== */
.profile-tabs :deep(.el-tabs__header) {
  margin-bottom: 16px;
}
.profile-tabs :deep(.el-tabs__item) {
  font-size: 15px;
  font-weight: 500;
}

.tab-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

/* ===== 订单列表 ===== */
.order-item {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.2s;
}
.order-item:hover { background: #f8f9fa; }
.order-item:last-child { border-bottom: none; }
.order-type { margin-bottom: 4px; }
.order-name { font-weight: 500; font-size: 15px; }
.order-date { color: #333; font-size: 14px; }
.order-detail { color: #999; font-size: 12px; }
.order-price { color: #f56c6c; font-weight: 700; font-size: 18px; }

/* ===== 评价列表 ===== */
.my-review-item {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.2s;
}
.my-review-item:hover { background: #f8f9fa; }
.my-review-item:last-child { border-bottom: none; }

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.review-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.review-hotel-name {
  font-weight: 600;
  font-size: 15px;
  color: #333;
  display: flex;
  align-items: center;
}

.review-order-no {
  color: #999;
  font-size: 12px;
  font-family: monospace;
  margin-bottom: 4px;
}

.review-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.review-time {
  color: #999;
  font-size: 13px;
}

.review-content {
  color: #555;
  font-size: 14px;
  line-height: 1.6;
  padding: 8px 0 0 0;
  border-top: 1px dashed #f0f0f0;
  margin-top: 8px;
}
</style>