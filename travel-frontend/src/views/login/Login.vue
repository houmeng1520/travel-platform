<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>🌍 旅游平台登录</h2>
      </template>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            ref="usernameRef"
            v-model="loginForm.username" 
            placeholder="请输入用户名" 
            clearable
            @keyup.enter="focusPassword"
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input 
            ref="passwordRef"
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码" 
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">
            登 录
          </el-button>
        </el-form-item>
        
        <div class="register-link">
          还没有账号？<el-link type="primary" @click="goToRegister">立即注册</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { login } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref()
const usernameRef = ref()
const passwordRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

// ===== 聚焦到密码输入框 =====
const focusPassword = () => {
  // 先校验用户名是否填写
  if (!loginForm.username) {
    ElMessage.warning('请输入用户名')
    return
  }
  // 聚焦到密码输入框
  nextTick(() => {
    passwordRef.value?.focus()
  })
}

// ===== 登录 =====
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      const res = await login(loginForm)
      
      if (res.code === 200) {
        userStore.setUser(res.data)
        ElMessage.success('登录成功！')
        
        // 根据角色跳转
        if (res.data.role === 'ADMIN') {
          router.push('/admin')
        } else {
          router.push('/home')
        }
      } else {
        ElMessage.error(res.message || '登录失败')
      }
    } catch (error) {
      ElMessage.error('登录失败，请检查网络或用户名密码')
    } finally {
      loading.value = false
    }
  })
}

const goToRegister = () => {
  router.push('/register')
}

// ===== 页面加载后自动聚焦用户名输入框 =====
onMounted(() => {
  nextTick(() => {
    usernameRef.value?.focus()
  })
})
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 380px;
  padding: 20px;
}

.login-card h2 {
  text-align: center;
  margin: 0;
}

.register-link {
  text-align: center;
  margin-top: 10px;
}
</style>