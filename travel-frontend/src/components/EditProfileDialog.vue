<template>
  <el-dialog v-model="visible" title="✏️ 编辑资料" width="480px" :close-on-click-modal="false">
    <el-form :model="form" label-width="80px">
      <el-form-item label="昵称">
        <el-input v-model="form.nickname" placeholder="请输入昵称" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="form.phone" placeholder="请输入手机号" />
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="form.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="性别">
        <el-radio-group v-model="form.gender">
          <el-radio :label="0">未知</el-radio>
          <el-radio :label="1">男</el-radio>
          <el-radio :label="2">女</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleSave" :loading="loading">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  userInfo: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const userStore = useUserStore()
const visible = ref(false)
const loading = ref(false)

const form = reactive({
  nickname: '',
  phone: '',
  email: '',
  gender: 0
})

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val && props.userInfo) {
    form.nickname = props.userInfo.nickname || ''
    form.phone = props.userInfo.phone || ''
    form.email = props.userInfo.email || ''
    form.gender = props.userInfo.gender || 0
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const handleSave = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/user/update',
      method: 'put',
      data: {
        id: userStore.userId,
        nickname: form.nickname,
        phone: form.phone,
        email: form.email,
        gender: form.gender
      }
    })
    if (res.code === 200) {
      ElMessage.success('资料更新成功')
      // 更新本地用户信息
      const updatedUser = {
        ...userStore.userInfo,
        nickname: form.nickname,
        phone: form.phone,
        email: form.email,
        gender: form.gender
      }
      userStore.userInfo = updatedUser
      localStorage.setItem('userInfo', JSON.stringify(updatedUser))
      
      visible.value = false
      emit('success')
    } else {
      ElMessage.error(res.message || '更新失败')
    }
  } catch (error) {
    ElMessage.error('更新失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>