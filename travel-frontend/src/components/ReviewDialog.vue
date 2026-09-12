<template>
  <el-dialog v-model="visible" title="✍️ 发表评价" width="500px" :close-on-click-modal="false">
    <el-form :model="form" label-width="80px">
      <el-form-item label="评分">
        <el-rate v-model="form.rating" :colors="['#99A9BF', '#F7BA2A', '#FF9900']" show-text :texts="texts" />
      </el-form-item>
      <el-form-item label="评价内容">
        <el-input v-model="form.content" type="textarea" :rows="4" :placeholder="placeholder" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">提交评价</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  orderId: {
    type: Number,
    default: null
  },
  orderType: {
    type: String,
    default: 'hotel'
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(false)
const submitting = ref(false)

const texts = ['很差', '一般', '不错', '很好', '完美']

// 动态提示语
const placeholder = computed(() => {
  const map = {
    hotel: '请分享您的入住体验...',
    scenic: '请分享您的游玩体验...',
    food: '请分享您的美食体验...',
    route: '请分享您的旅行体验...'
  }
  return map[props.orderType] || '请分享您的体验...'
})

const form = reactive({
  rating: 5,
  content: ''
})

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    form.rating = 5
    form.content = ''
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const handleSubmit = async () => {
  if (!form.content.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }

  submitting.value = true
  try {
    const res = await request({
      url: '/review/submit',
      method: 'post',
      data: {
        orderId: props.orderId,
        rating: form.rating,
        content: form.content
      }
    })
    if (res.code === 200) {
      ElMessage.success('评价成功！')
      visible.value = false
      emit('success')
    } else {
      ElMessage.error(res.message || '提交失败')
    }
  } catch (error) {
    ElMessage.error('提交失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}
</script>