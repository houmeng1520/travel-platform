<template>
  <div class="home-container">
    <!-- ===== 顶部导航 ===== -->
    <el-header>
      <div class="header-content">
        <!-- 左侧：Logo -->
        <div class="header-left">
          <div class="logo" @click="goHome">
            <span class="logo-icon">✈️</span>
            <span class="logo-text">旅游平台</span>
          </div>
        </div>

        <!-- 中间：导航菜单 -->
        <div class="header-center">
          <el-button 
            type="text" 
            class="nav-btn" 
            :class="{ active: $route.path === '/scenic' }"
            @click="goScenic"
          >
            🏔️ 景点
          </el-button>
          <el-button 
            type="text" 
            class="nav-btn"
            :class="{ active: $route.path === '/food' }"
            @click="goFood"
          >
            🍜 美食
          </el-button>
          <el-button 
            type="text" 
            class="nav-btn"
            :class="{ active: $route.path === '/route' }"
            @click="goRoute"
          >
            🗺️ 路线
          </el-button>
          <el-button 
            type="text" 
            class="nav-btn"
            :class="{ active: $route.path === '/hotel' }"
            @click="goHotel"
          >
            🏨 酒店
          </el-button>
          <el-button 
            type="text" 
            class="nav-btn"
            :class="{ active: $route.path === '/about' }"
            @click="goAbout"
          >
            📖 关于
          </el-button>
        </div>

        <!-- 右侧：用户操作 -->
        <div class="header-right">
          <!-- 未登录 -->
          <template v-if="!userStore.isLoggedIn()">
            <el-button type="primary" plain size="small" @click="goLogin" class="login-btn">登录</el-button>
            <el-button type="success" size="small" @click="goRegister" class="register-btn">注册</el-button>
          </template>
          
          <!-- 已登录 -->
          <template v-else>
            <el-dropdown trigger="click" @command="handleDropdown">
              <div class="user-avatar">
                <el-avatar :size="32" :src="userStore.userInfo?.avatar || ''">
                  {{ userStore.username?.charAt(0)?.toUpperCase() }}
                </el-avatar>
                <span class="username">{{ userStore.userInfo?.nickname || userStore.username }}</span>
                <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon> 个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="orders">
                    <el-icon><Document /></el-icon> 我的预订
                  </el-dropdown-item>
                  <el-dropdown-item v-if="userStore.isAdmin()" command="admin" divided>
                    <el-icon><Setting /></el-icon> 管理后台
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon> 退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </div>
      </div>
    </el-header>

    <!-- ===== 主内容 ===== -->
    <el-main>
      <!-- 1. 轮播图 -->
      <Banner />

      <!-- 2. 精选目的地 -->
      <Destination />

      <!-- 3. 限时优惠 -->
      <Discount />

      <!-- 4. 精选路线 -->
      <RouteRecommend />

      <!-- 5. 热门酒店 -->
      <el-card class="hotel-section">
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span style="font-weight: bold; font-size: 16px;">🏨 热门酒店</span>
            <el-button type="text" @click="goHotel" style="color: #409EFF;">查看更多 →</el-button>
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="6" v-for="item in hotHotel" :key="item.id">
            <div class="hotel-item" @click="goHotelDetail(item.id)">
              <div class="hotel-image-mini">
                <el-image 
                  :src="getHotelImage(item.images)" 
                  fit="cover" 
                  style="width: 100%; height: 150px;"
                >
                  <template #error>
                    <div style="height: 150px; display: flex; align-items: center; justify-content: center; background: #f0f0f0; font-size: 32px;">🏨</div>
                  </template>
                </el-image>
                <div class="hotel-star-mini">
                  <span v-for="i in item.starLevel" :key="i">⭐</span>
                </div>
              </div>
              <div style="padding: 10px 0;">
                <div style="font-weight: 500; font-size: 14px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                  {{ item.name }}
                </div>
                <div style="color: #f56c6c; font-weight: bold; font-size: 16px;">
                  ¥{{ item.price }}/晚
                </div>
                <div style="color: #999; font-size: 12px;">
                  ⭐ {{ item.rating }} · {{ item.city }}
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </el-card>

      <!-- 6. 热门推荐 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="hot-card">
            <template #header>
              <span style="font-weight: bold; font-size: 16px;">🏔️ 热门景点</span>
              <el-button type="text" @click="goScenic" style="float: right; color: #409EFF;">查看更多 →</el-button>
            </template>
            <div v-for="item in hotScenic" :key="item.id" class="hot-item" @click="goScenicDetail(item.id)">
              <div class="hot-rank">{{ hotScenic.indexOf(item) + 1 }}</div>
              <div class="hot-info">
                <div class="hot-name">{{ item.name }}</div>
                <div class="hot-meta">⭐ {{ item.rating }} · {{ item.city }}</div>
              </div>
              <div class="hot-price">¥{{ item.price }}</div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="12">
          <el-card class="hot-card">
            <template #header>
              <span style="font-weight: bold; font-size: 16px;">🍜 热门美食</span>
              <el-button type="text" @click="goFood" style="float: right; color: #409EFF;">查看更多 →</el-button>
            </template>
            <div v-for="item in hotFood" :key="item.id" class="hot-item" @click="goFoodDetail(item.id)">
              <div class="hot-rank">{{ hotFood.indexOf(item) + 1 }}</div>
              <div class="hot-info">
                <div class="hot-name">{{ item.name }}</div>
                <div class="hot-meta">⭐ {{ item.rating }} · {{ item.city }}</div>
              </div>
              <div class="hot-price">¥{{ item.price }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-main>

    <!-- 底部 -->
    <Footer />
    <BackToTop />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { ArrowDown, User, Document, Setting, SwitchButton } from '@element-plus/icons-vue'
import { getScenicStats, getHotScenic } from '@/api/scenic'
import { getFoodStats, getHotFood } from '@/api/food'
import { getHotHotel } from '@/api/hotel'

// ===== 导入组件 =====
import Banner from '@/components/Banner.vue'
import Destination from '@/components/Destination.vue'
import Discount from '@/components/Discount.vue'
import RouteRecommend from '@/components/RouteRecommend.vue'
import Footer from '@/components/Footer.vue'
import BackToTop from '@/components/BackToTop.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// ===== 数据 =====
const hotScenic = ref([])
const hotFood = ref([])
const hotHotel = ref([])

// ===== 工具方法 =====

// 获取酒店图片
const getHotelImage = (images) => {
  if (!images) return ''
  try {
    const list = JSON.parse(images)
    return list[0] || ''
  } catch {
    return images
  }
}

// ===== 加载热门推荐 =====
const loadHot = async () => {
  try {
    const scenicRes = await getHotScenic()
    if (scenicRes.code === 200) {
      hotScenic.value = scenicRes.data || []
    }

    const foodRes = await getHotFood()
    if (foodRes.code === 200) {
      hotFood.value = foodRes.data || []
    }
  } catch (error) {
    console.error('加载热门推荐失败', error)
  }
}

// ===== 加载热门酒店 =====
const loadHotHotel = async () => {
  try {
    const res = await getHotHotel()
    if (res.code === 200) {
      hotHotel.value = res.data || []
    }
  } catch (error) {
    console.error('加载热门酒店失败', error)
  }
}

// ===== 跳转方法 =====
const goHome = () => {
  router.push('/home')
}

const goLogin = () => {
  router.push('/login')
}

const goRegister = () => {
  router.push('/register')
}

const goScenic = () => {
  router.push('/scenic')
}

const goFood = () => {
  router.push('/food')
}

const goRoute = () => {
  router.push('/route')
}

const goHotel = () => {
  router.push('/hotel')
}

const goAbout = () => {
  router.push('/about')
}

const goAdmin = () => {
  router.push('/admin')
}

const goScenicDetail = (id) => {
  router.push(`/scenic/detail/${id}`)
}

const goFoodDetail = (id) => {
  router.push(`/food/detail/${id}`)
}

const goHotelDetail = (id) => {
  router.push(`/hotel/detail/${id}`)
}

const handleLogout = () => {
  userStore.clearUser()
  ElMessage.success('已退出登录')
}

// ===== 下拉菜单命令处理 =====
const handleDropdown = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'orders':
      router.push('/profile')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// ===== 生命周期 =====
onMounted(() => {
  loadHot()
  loadHotHotel()
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: #f5f7fa;
}

/* ===== 顶部导航 ===== */
.el-header {
  background: #fff;
  border-bottom: 1px solid #e8ecf1;
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 100;
  height: 64px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
  max-width: 1200px;
  margin: 0 auto;
}

/* ===== Logo ===== */
.header-left .logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  user-select: none;
}

.logo-icon {
  font-size: 24px;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* ===== 导航菜单 ===== */
.header-center {
  display: flex;
  align-items: center;
  gap: 4px;
}

.nav-btn {
  font-size: 15px;
  color: #5a6a7a;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s;
  font-weight: 500;
}

.nav-btn:hover {
  color: #409EFF;
  background: #f0f7ff;
}

.nav-btn.active {
  color: #409EFF;
  background: #e8f3ff;
}

/* ===== 右侧用户操作 ===== */
.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.login-btn {
  border-radius: 8px;
  padding: 8px 20px;
}

.register-btn {
  border-radius: 8px;
  padding: 8px 20px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  color: #fff;
}

.register-btn:hover {
  opacity: 0.9;
  color: #fff;
}

/* ===== 用户头像下拉 ===== */
.user-avatar {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px 4px 4px;
  border-radius: 20px;
  transition: background 0.3s;
}

.user-avatar:hover {
  background: #f5f7fa;
}

.user-avatar .username {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dropdown-icon {
  font-size: 14px;
  color: #999;
}

/* ===== 下拉菜单样式 ===== */
:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  font-size: 14px;
}

:deep(.el-dropdown-menu__item .el-icon) {
  font-size: 16px;
}

/* ===== 主内容 ===== */
.el-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  width: 100%;
}

/* ===== 热门酒店 ===== */
.hotel-section {
  margin-bottom: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.hotel-item {
  cursor: pointer;
  transition: transform 0.3s;
  padding: 4px;
  border-radius: 8px;
}

.hotel-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.hotel-image-mini {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
}

.hotel-star-mini {
  position: absolute;
  bottom: 6px;
  left: 8px;
  background: rgba(0, 0, 0, 0.6);
  color: #ffd93d;
  padding: 0 10px;
  border-radius: 10px;
  font-size: 10px;
}

/* ===== 热门卡片 ===== */
.hot-card {
  height: 100%;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.hot-item {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.2s;
  border-radius: 6px;
}

.hot-item:hover {
  background: #f5f7fa;
}

.hot-item:last-child {
  border-bottom: none;
}

.hot-rank {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 13px;
  margin-right: 12px;
  flex-shrink: 0;
}

.hot-item:nth-child(1) .hot-rank {
  background: #ff6b6b;
  color: #fff;
}

.hot-item:nth-child(2) .hot-rank {
  background: #ffa94d;
  color: #fff;
}

.hot-item:nth-child(3) .hot-rank {
  background: #ffd93d;
  color: #333;
}

.hot-info {
  flex: 1;
}

.hot-name {
  font-weight: 500;
  font-size: 15px;
}

.hot-meta {
  color: #999;
  font-size: 13px;
}

.hot-price {
  color: #f56c6c;
  font-weight: 600;
  font-size: 16px;
}
</style>