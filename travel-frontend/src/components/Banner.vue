<template>
  <div class="banner-container">
    <el-carousel :interval="4000" height="320px" arrow="always">
      <el-carousel-item v-for="(item, index) in bannerList" :key="index">
        <div class="banner-item" :style="{ backgroundImage: `url(${getBannerImage(item.image)})`, backgroundSize: 'cover', backgroundPosition: 'center' }">
          <div class="banner-overlay">
            <div class="banner-content">
              <h1 class="banner-title">{{ item.title }}</h1>
              <p class="banner-desc">{{ item.desc }}</p>
              <el-button type="primary" size="large" round @click="goTo(item.link)">
                {{ item.btnText }} →
              </el-button>
            </div>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const bannerList = ref([])

// 获取轮播图数据
const loadBanners = async () => {
  try {
    const res = await request({
      url: '/banner/list',
      method: 'get'
    })
    if (res.code === 200 && res.data && res.data.length > 0) {
      bannerList.value = res.data
    } else {
      // 默认轮播图（当数据库无数据时）
      bannerList.value = [
        {
          title: '🌍 探索世界之美',
          desc: '发现全球最值得去的旅行目的地，开启你的冒险之旅',
          btnText: '查看景点',
          link: '/scenic',
          image: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
        },
        {
          title: '🍜 品味地道美食',
          desc: '从街头小吃到米其林星级，每一口都是旅行的记忆',
          btnText: '探索美食',
          link: '/food',
          image: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
        },
        {
          title: '🗺️ 定制专属路线',
          desc: '精选旅游路线，让你的旅程更轻松、更精彩',
          btnText: '查看路线',
          link: '/route',
          image: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
        },
        {
          title: '🏨 舒适酒店住宿',
          desc: '精选优质酒店，让旅途更舒适更安心',
          btnText: '预订酒店',
          link: '/hotel',
          image: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
        }
      ]
    }
  } catch (error) {
    console.error('加载轮播图失败', error)
    // 出错时使用默认数据
  }
}

const getBannerImage = (image) => {
  if (!image) return ''
  if (image.startsWith('http')) return image
  if (image.startsWith('/uploads')) return `http://localhost:8080${image}`
  if (image.startsWith('linear-gradient')) return image
  return image
}

const goTo = (link) => {
  router.push(link)
}

onMounted(() => {
  loadBanners()
})
</script>

<style scoped>
.banner-container {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  margin-bottom: 24px;
}

.banner-item {
  width: 100%;
  height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.banner-overlay {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0,0,0,0.25);
}

.banner-content {
  text-align: center;
  color: #fff;
  padding: 30px;
}

.banner-title {
  font-size: 38px;
  font-weight: 700;
  margin-bottom: 10px;
  text-shadow: 0 2px 8px rgba(0,0,0,0.3);
}

.banner-desc {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 18px;
  text-shadow: 0 1px 4px rgba(0,0,0,0.3);
}

:deep(.el-carousel__indicators) {
  bottom: 12px;
}

:deep(.el-carousel__indicator .el-carousel__button) {
  width: 24px;
  height: 3px;
  border-radius: 2px;
}
</style>