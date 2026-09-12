<template>
  <div class="booking-container">
    <el-header>
      <div class="header-content">
        <h1>📋 预订 {{ typeText }}</h1>
        <el-button type="primary" @click="goBack">返回详情</el-button>
      </div>
    </el-header>

    <el-main>
      <el-card v-loading="loading">
        <el-form :model="form" label-width="100px">
          <!-- 名称 -->
          <el-form-item label="名称">
            <span style="font-weight: 500;">{{ displayName }}</span>
          </el-form-item>
          
          <!-- 价格 -->
          <el-form-item label="价格">
            <span style="color: #f56c6c; font-size: 20px; font-weight: 700;">¥{{ displayPrice }}</span>
            <span style="color: #999;"> / {{ unitText }}</span>
          </el-form-item>

          <!-- ===== 数量/人数（根据类型动态显示） ===== -->
          <el-form-item :label="quantityLabel">
            <div style="display: flex; align-items: center; gap: 12px;">
              <el-input-number 
                v-model="form.quantity" 
                :min="1" 
                :max="maxQuantity" 
                size="large"
                style="width: 140px;"
              />
              <span style="color: #999; font-size: 13px;">{{ quantityUnit }}</span>
            </div>
          </el-form-item>

          <!-- 联系人 -->
          <el-form-item label="联系人" required>
            <el-input v-model="form.guestName" placeholder="请输入联系人姓名" />
          </el-form-item>

          <!-- 联系电话 -->
          <el-form-item label="联系电话" required>
            <el-input v-model="form.guestPhone" placeholder="请输入联系电话" />
          </el-form-item>

          <!-- 邮箱 -->
          <el-form-item label="邮箱">
            <el-input v-model="form.guestEmail" placeholder="请输入邮箱（选填）" />
          </el-form-item>

          <!-- 备注 -->
          <el-form-item label="备注">
            <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="备注（选填）" />
          </el-form-item>

          <!-- 提交 -->
          <el-form-item>
            <div style="display: flex; justify-content: space-between; align-items: center; width: 100%;">
              <span style="font-size: 16px;">
                总价：<span style="color: #f56c6c; font-size: 24px; font-weight: 700;">¥{{ totalPrice }}</span>
              </span>
              <el-button type="primary" size="large" @click="handleSubmit" :loading="submitting">
                提交订单
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </el-card>
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createOrder } from '@/api/order'
import { getScenicDetail } from '@/api/scenic'
import { getFoodDetail } from '@/api/food'
import { getRouteDetail } from '@/api/route'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const submitting = ref(false)
const detail = ref(null)
const type = ref('')
const typeText = ref('')
const unitText = ref('')

const form = ref({
  quantity: 1,
  guestName: '',
  guestPhone: '',
  guestEmail: '',
  remark: ''
})

// ===== 根据类型动态显示数量标签 =====
const quantityLabel = computed(() => {
  const map = {
    scenic: '门票数量',
    food: '套餐数量',
    route: '参与人数'
  }
  return map[type.value] || '数量'
})

const quantityUnit = computed(() => {
  const map = {
    scenic: '张',
    food: '份',
    route: '人'
  }
  return map[type.value] || '个'
})

const maxQuantity = computed(() => {
  const map = {
    scenic: 20,
    food: 20,
    route: 50
  }
  return map[type.value] || 10
})

// ===== 显示名称（兼容不同数据结构） =====
const displayName = computed(() => {
  if (!detail.value) return ''
  if (type.value === 'route') {
    return detail.value.route?.title || detail.value.title || '未知路线'
  }
  return detail.value.name || detail.value.title || '未知名称'
})

// ===== 显示价格 =====
const displayPrice = computed(() => {
  if (!detail.value) return 0
  if (type.value === 'route') {
    return detail.value.route?.price || detail.value.price || 0
  }
  return detail.value.price || 0
})

// ===== 总价 =====
const totalPrice = computed(() => {
  return (displayPrice.value * form.value.quantity).toFixed(2)
})

// ===== 加载详情 =====
const loadDetail = async () => {
  const targetId = route.query.targetId
  type.value = route.query.type || 'scenic'

  const typeMap = {
    scenic: { text: '景点门票', unit: '张', api: getScenicDetail },
    food: { text: '美食套餐', unit: '份', api: getFoodDetail },
    route: { text: '旅游路线', unit: '人', api: getRouteDetail }
  }

  typeText.value = typeMap[type.value]?.text || '商品'
  unitText.value = typeMap[type.value]?.unit || '份'
  const api = typeMap[type.value]?.api

  if (!api) {
    ElMessage.error('不支持的预订类型')
    return
  }

  loading.value = true
  try {
    const res = await api(targetId)
    if (res.code === 200) {
      detail.value = res.data
    } else {
      ElMessage.error(res.message || '加载数据失败')
    }
  } catch (error) {
    console.error('加载失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// ===== 提交订单 =====
const handleSubmit = async () => {
  if (!form.value.guestName) {
    ElMessage.warning('请输入联系人姓名')
    return
  }
  if (!form.value.guestPhone) {
    ElMessage.warning('请输入联系电话')
    return
  }

  submitting.value = true
  try {
    const res = await createOrder({
      orderType: type.value,
      targetId: detail.value.id || detail.value.route?.id,
      quantity: form.value.quantity,
      guestName: form.value.guestName,
      guestPhone: form.value.guestPhone,
      guestEmail: form.value.guestEmail,
      remark: form.value.remark
    })
    if (res.code === 200) {
      ElMessage.success('订单创建成功！')
      router.push({
        path: '/payment',
        query: { orderId: res.data.id }
      })
    } else {
      ElMessage.error(res.message || '创建订单失败')
    }
  } catch (error) {
    console.error('创建订单失败:', error)
    ElMessage.error('创建订单失败')
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  router.go(-1)
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.booking-container {
  min-height: 100vh;
  background: #f5f7fa;
}
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
.el-main {
  max-width: 600px;
  margin: 0 auto;
  padding: 24px;
}
</style>