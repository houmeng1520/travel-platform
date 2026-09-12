<template>
  <div class="admin-layout">
    <el-container>
      <!-- ===== 顶部导航 ===== -->
      <el-header class="admin-header">
        <div class="header-left">
          <h2>⚙️ 管理后台</h2>
          <div class="welcome-text">
            <span class="welcome-greeting">👋 欢迎回来，{{ userStore.username }}</span>
          </div>
        </div>
        <div class="header-right">
          <div class="user-info">
            <el-avatar :size="32" :src="userStore.userInfo?.avatar || ''">
              {{ userStore.username?.charAt(0)?.toUpperCase() }}
            </el-avatar>
            <span class="username">{{ userStore.username }}</span>
          </div>
          <el-button type="primary" plain size="small" @click="goHome">返回首页</el-button>
          <el-button type="danger" plain size="small" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>

      <el-container>
        <!-- ===== 侧边栏 ===== -->
        <el-aside width="200px">
          <el-menu
            :default-active="$route.path"
            router
            background-color="#304156"
            text-color="#bfcbd9"
            active-text-color="#409EFF"
            style="height: 100%; border-right: none;"
          >
            <el-menu-item index="/admin">
              <span>📊 数据概览</span>
            </el-menu-item>
            <el-menu-item index="/admin/scenic">
              <span>🏔️ 景点管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/food">
              <span>🍜 美食管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/route">
              <span>🗺️ 路线管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/hotel">
              <span>🏨 酒店管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/order">
              <span>📋 订单管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/banner">
              <span>🖼️ 轮播图</span>
            </el-menu-item>
            <el-menu-item index="/admin/user">
              <span>👤 用户管理</span>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <!-- ===== 内容区 ===== -->
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const goHome = () => {
  router.push('/home')
}

const handleLogout = () => {
  userStore.clearUser()
  ElMessage.success('已退出登录')
  router.push('/home')
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  background: #f0f2f5;
}

/* ===== 顶部导航（白色背景） ===== */
.admin-header {
  background: #fff;
  border-bottom: 1px solid #e8ecf1;
  padding: 0 24px;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.header-left h2 {
  margin: 0;
  font-size: 18px;
  color: #333;
  white-space: nowrap;
}

.welcome-text {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #666;
  padding-left: 16px;
  border-left: 1px solid #e8ecf1;
  white-space: nowrap;
}

.welcome-greeting {
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-right: 12px;
  border-right: 1px solid #e8ecf1;
}

.username {
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

/* ===== 主内容 ===== */
.el-main {
  padding: 20px;
  background: #f0f2f5;
  min-height: calc(100vh - 60px);
}

/* ===== 侧边栏 ===== */
.el-aside {
  background: #304156;
}

:deep(.el-menu) {
  border-right: none;
}

/* ===== 内容区卡片 ===== */
:deep(.el-card) {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}
</style>