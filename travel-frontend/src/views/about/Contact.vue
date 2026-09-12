<template>
  <div class="contact-container">
    <!-- 顶部导航 -->
    <el-header>
      <div class="header-content">
        <h1>🌍 旅游平台</h1>
        <div class="user-info">
          <el-button type="success" size="small" @click="goHome">返回首页</el-button>
          <template v-if="!userStore.isLoggedIn()">
            <el-button type="primary" size="small" @click="goLogin">登录</el-button>
            <el-button type="success" size="small" @click="goRegister">注册</el-button>
          </template>
          <template v-else>
            <span class="username">{{ userStore.userInfo?.nickname || userStore.username }}</span>
            <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
          </template>
        </div>
      </div>
    </el-header>

    <el-main>
      <div class="contact-content">
        <div class="contact-hero">
          <h1>📞 联系我们</h1>
          <p>我们随时准备为您服务</p>
        </div>

        <el-row :gutter="30">
          <!-- 左侧：联系方式 -->
          <el-col :span="14">
            <el-card>
              <h2>📋 联系方式</h2>
              <div class="contact-item">
                <span class="contact-icon">📧</span>
                <div>
                  <div class="contact-label">邮箱</div>
                  <div class="contact-value">support@travel.com</div>
                </div>
              </div>
              <div class="contact-item">
                <span class="contact-icon">📞</span>
                <div>
                  <div class="contact-label">电话</div>
                  <div class="contact-value">400-888-8888</div>
                </div>
              </div>
              <div class="contact-item">
                <span class="contact-icon">🕐</span>
                <div>
                  <div class="contact-label">服务时间</div>
                  <div class="contact-value">周一至周日 9:00 - 21:00</div>
                </div>
              </div>
              <div class="contact-item">
                <span class="contact-icon">📍</span>
                <div>
                  <div class="contact-label">地址</div>
                  <div class="contact-value">贵州省兴义市街心花园888号</div>
                </div>
              </div>
            </el-card>
          </el-col>

          <!-- 右侧：意见反馈 -->
          <el-col :span="10">
            <el-card>
              <h2>✉️ 意见反馈</h2>
              <el-form :model="feedbackForm" label-width="60px">
                <el-form-item label="姓名">
                  <el-input v-model="feedbackForm.name" placeholder="请输入您的姓名" />
                </el-form-item>
                <el-form-item label="邮箱">
                  <el-input v-model="feedbackForm.email" placeholder="请输入您的邮箱" />
                </el-form-item>
                <el-form-item label="内容">
                  <el-input v-model="feedbackForm.content" type="textarea" :rows="4" placeholder="请描述您的问题或建议" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleSubmit">提交反馈</el-button>
                </el-form-item>
              </el-form>
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
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import Footer from '@/components/Footer.vue'
import BackToTop from '@/components/BackToTop.vue'

const router = useRouter()
const userStore = useUserStore()

const feedbackForm = reactive({
  name: '',
  email: '',
  content: ''
})

const goHome = () => router.push('/home')
const goLogin = () => router.push('/login')
const goRegister = () => router.push('/register')

const handleLogout = () => {
  userStore.clearUser()
  ElMessage.success('已退出登录')
  router.push('/home')
}

const handleSubmit = () => {
  if (!feedbackForm.content) {
    ElMessage.warning('请填写反馈内容')
    return
  }
  ElMessage.success('感谢您的反馈，我们会尽快处理！')
  feedbackForm.name = ''
  feedbackForm.email = ''
  feedbackForm.content = ''
}
</script>

<style scoped>
.contact-container {
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

.header-content h1 {
  font-size: 20px;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.username {
  font-weight: 500;
}

.el-main {
  max-width: 1000px;
  margin: 0 auto;
  padding: 30px 24px;
}

.contact-hero {
  text-align: center;
  padding: 20px 0 30px;
}

.contact-hero h1 {
  font-size: 36px;
  margin: 0;
  color: #333;
}

.contact-hero p {
  color: #999;
  font-size: 18px;
  margin-top: 8px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 14px 0;
  border-bottom: 1px solid #f0f0f0;
}

.contact-item:last-child {
  border-bottom: none;
}

.contact-icon {
  font-size: 28px;
  flex-shrink: 0;
}

.contact-label {
  font-size: 13px;
  color: #999;
}

.contact-value {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.el-card h2 {
  font-size: 18px;
  margin: 0 0 16px 0;
  color: #333;
}
</style>