<template>
  <el-dialog v-model="visible" title="📱 使用订单" width="480px" :close-on-click-modal="false">
    <div v-if="order" class="use-order-content">
      <!-- 订单信息 -->
      <div class="order-info">
        <div class="info-row">
          <span class="label">订单号</span>
          <span class="value order-no">{{ order.orderNo }}</span>
        </div>
        <div class="info-row">
          <span class="label">名称</span>
          <span class="value">{{ order.targetName }}</span>
        </div>
        <div class="info-row">
          <span class="label">数量</span>
          <span class="value">{{ order.quantity }} {{ getUnitText(order.orderType) }}</span>
        </div>
        <div class="info-row">
          <span class="label">金额</span>
          <span class="value price">¥{{ order.totalAmount }}</span>
        </div>
      </div>

      <el-divider />

      <!-- 核销码 -->
      <div class="qr-section">
        <div class="qr-title">📱 核销码</div>
        <div class="qr-code">{{ order.orderNo }}</div>
        <div class="qr-tip">请向商家出示此核销码</div>
      </div>

      <el-divider />

      <!-- 提示 -->
      <el-alert 
        title="确认使用后将无法撤销" 
        type="warning" 
        :closable="false" 
        show-icon
        style="margin-bottom: 16px;"
      />
    </div>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleConfirm" :loading="submitting">
        ✅ 确认使用
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  order: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(false)
const submitting = ref(false)

watch(() => props.modelValue, (val) => {
  visible.value = val
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

// 单位
const getUnitText = (type) => {
  const map = { hotel: '间', scenic: '张', food: '份', route: '人' }
  return map[type] || '份'
}

// 确认使用
const handleConfirm = async () => {
  if (!props.order) return

  submitting.value = true
  try {
    const res = await request({
      url: `/order/use/${props.order.id}`,
      method: 'put'
    })
    if (res.code === 200) {
      ElMessage.success('订单已使用')
      visible.value = false
      emit('success')
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.use-order-content {
  padding: 4px 0;
}

.order-info {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 14px 18px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
}

.info-row .label {
  color: #999;
  font-size: 14px;
}

.info-row .value {
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

.info-row .value.order-no {
  color: #409EFF;
  font-family: monospace;
  font-size: 14px;
}

.info-row .value.price {
  color: #f56c6c;
  font-weight: 700;
  font-size: 16px;
}

.qr-section {
  text-align: center;
  padding: 16px 0;
}

.qr-title {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin-bottom: 12px;
}

.qr-code {
  display: inline-block;
  background: #f5f7fa;
  border: 2px dashed #dcdfe6;
  border-radius: 10px;
  padding: 16px 30px;
  font-size: 18px;
  font-family: monospace;
  font-weight: 600;
  color: #333;
  letter-spacing: 1px;
  margin-bottom: 10px;
}

.qr-tip {
  color: #999;
  font-size: 13px;
}
</style>