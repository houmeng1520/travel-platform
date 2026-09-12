<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <h2>📝 用户注册</h2>
      </template>
      
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            ref="usernameRef"
            v-model="registerForm.username" 
            placeholder="请输入用户名" 
            @keyup.enter="focusNext('passwordRef')"
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input 
            ref="passwordRef"
            v-model="registerForm.password" 
            type="password" 
            placeholder="请输入密码" 
            show-password
            @keyup.enter="focusNext('confirmPasswordRef')"
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            ref="confirmPasswordRef"
            v-model="registerForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码" 
            show-password
            @keyup.enter="focusNext('nicknameRef')"
          />
        </el-form-item>
        
        <el-form-item label="昵称" prop="nickname">
          <el-input 
            ref="nicknameRef"
            v-model="registerForm.nickname" 
            placeholder="请输入昵称" 
            @keyup.enter="focusNext('emailRef')"
          />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input 
            ref="emailRef"
            v-model="registerForm.email" 
            placeholder="请输入邮箱" 
            @keyup.enter="handleRegister"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading" style="width: 100%">
            立即注册
          </el-button>
        </el-form-item>
        
        <div class="login-link">
          已有账号？<el-link type="primary" @click="goToLogin">去登录</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/user'

const router = useRouter()

const registerFormRef = ref()
const usernameRef = ref()
const passwordRef = ref()
const confirmPasswordRef = ref()
const nicknameRef = ref()
const emailRef = ref()
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  email: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度3-20位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  nickname: [
    { required: false, message: '请输入昵称', trigger: 'blur' }
  ],
  email: [
    { required: false, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

// ===== 聚焦到下一个输入框 =====
const focusNext = (refName) => {
  nextTick(() => {
    // 通过 refName 字符串动态访问 ref
    const refMap = {
      passwordRef,
      confirmPasswordRef,
      nicknameRef,
      emailRef
    }
    refMap[refName]?.value?.focus()
  })
}

// ===== 注册 =====
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      const { username, password, nickname, email } = registerForm
      const res = await register({ username, password, nickname, email })
      
      if (res.code === 200) {
        ElMessage.success('注册成功！请登录')
        registerForm.username = ''
        registerForm.password = ''
        registerForm.confirmPassword = ''
        registerForm.nickname = ''
        registerForm.email = ''
        router.push('/login')
      } else {
        ElMessage.error(res.message || '注册失败')
      }
    } catch (error) {
      ElMessage.error('注册失败，请稍后重试')
    } finally {
      loading.value = false
    }
  })
}

const goToLogin = () => {
  router.push('/login')
}

// ===== 页面加载后自动聚焦用户名 =====
onMounted(() => {
  nextTick(() => {
    usernameRef.value?.focus()
  })
})
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-card {
  width: 420px;
  padding: 20px;
}

.register-card h2 {
  text-align: center;
  margin: 0;
}

.login-link {
  text-align: center;
  margin-top: 10px;
}
</style>