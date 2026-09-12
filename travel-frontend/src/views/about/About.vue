<template>
  <div class="about-container">
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
      <div class="about-content">
        <!-- 标题 -->
        <div class="about-hero">
          <h1>📖 关于我们</h1>
          <p>让每一次旅行都成为难忘的回忆</p>
        </div>

        <!-- 公司介绍 -->
        <el-card class="about-card">
          <h2>🌍 我们的使命</h2>
          <p>
            旅游平台致力于为用户提供一站式的旅行服务体验。
            我们整合了景点、美食、路线等全方位信息，
            帮助用户轻松规划每一次旅行。
          </p>
        </el-card>

        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="feature-card">
              <div class="feature-icon">🏔️</div>
              <h3>丰富景点</h3>
              <p>精选全球热门景点，实时更新</p>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="feature-card">
              <div class="feature-icon">🍜</div>
              <h3>地道美食</h3>
              <p>发现各地特色美食，品味文化</p>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="feature-card">
              <div class="feature-icon">🗺️</div>
              <h3>智能路线</h3>
              <p>专业规划路线，省心省力</p>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="feature-card">
              <div class="feature-icon">💎</div>
              <h3>优质服务</h3>
              <p>7x24小时客服，随时响应</p>
            </el-card>
          </el-col>
        </el-row>

        <!-- 团队介绍 -->
        <el-card class="about-card">
          <h2>👥 我们的团队</h2>
          <p>
            我们是一群热爱旅行的技术人和旅行达人，
            希望通过技术让更多人享受旅行的美好。
            团队成员来自全国各地，拥有丰富的旅游和互联网行业经验。
          </p>
        </el-card>

        <!-- 发展历程 -->
        <el-card class="about-card">
          <h2>📅 发展历程</h2>
          <el-timeline>
            <el-timeline-item timestamp="2026年" placement="top">
              <el-card>
                <h4>🚀 平台上线</h4>
                <p>旅游平台正式上线，为用户提供全面的旅游服务</p>
              </el-card>
            </el-timeline-item>
            <el-timeline-item timestamp="2025年" placement="top">
              <el-card>
                <h4>💡 项目启动</h4>
                <p>团队组建完成，开始旅游平台的规划和开发</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </div>
    </el-main>

    <!-- 底部 -->
    <Footer />
    <BackToTop />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import Footer from '@/components/Footer.vue'
import BackToTop from '@/components/BackToTop.vue'

const router = useRouter()
const userStore = useUserStore()

const goHome = () => router.push('/home')
const goLogin = () => router.push('/login')
const goRegister = () => router.push('/register')

const handleLogout = () => {
  userStore.clearUser()
  ElMessage.success('已退出登录')
  router.push('/home')
}
</script>

<style scoped>
.about-container {
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

.about-hero {
  text-align: center;
  padding: 40px 0 30px;
}

.about-hero h1 {
  font-size: 36px;
  margin: 0;
  color: #333;
}

.about-hero p {
  color: #999;
  font-size: 18px;
  margin-top: 8px;
}

.about-card {
  margin-bottom: 24px;
}

.about-card h2 {
  font-size: 20px;
  margin: 0 0 12px 0;
  color: #333;
}

.about-card p {
  color: #666;
  line-height: 1.8;
  font-size: 15px;
  margin: 0;
}

.feature-card {
  text-align: center;
  padding: 10px 0;
  height: 100%;
}

.feature-icon {
  font-size: 40px;
  margin-bottom: 8px;
}

.feature-card h3 {
  font-size: 16px;
  margin: 0 0 6px 0;
}

.feature-card p {
  color: #999;
  font-size: 13px;
  margin: 0;
}
</style>