<template>
  <footer class="footer">
    <div class="footer-container">
      <el-row :gutter="40">
        <el-col :span="8">
          <h4>✈️ 旅游平台</h4>
          <p class="footer-desc">
            致力于为用户提供最优质的旅游服务，<br>
            发现世界之美，创造难忘旅程。
          </p>
        </el-col>
        
        <el-col :span="8">
          <h4>快速链接</h4>
          <div class="footer-links">
            <a @click.prevent="goTo('/scenic')">🏔️ 景点列表</a>
            <a @click.prevent="goTo('/food')">🍜 美食推荐</a>
            <a @click.prevent="goTo('/route')">🗺️ 旅游路线</a>
            <a @click.prevent="goTo('/hotel')">🏨 酒店住宿</a>
            <a @click.prevent="goTo('/about')">📖 关于我们</a>
            <a @click.prevent="goTo('/contact')">📞 联系我们</a>
          </div>
        </el-col>
        
        <el-col :span="8">
          <h4>联系我们</h4>
          <div class="footer-contact">
            <p>📧 邮箱：support@travel.com</p>
            <p>📞 电话：400-888-8888</p>
            <p>🕐 服务时间：9:00 - 21:00</p>
          </div>
        </el-col>
      </el-row>
      
      <div class="footer-bottom">
        <p>© 2026 旅游平台 All Rights Reserved.</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goTo = (path) => {
  router.push(path)
}
</script>

<style scoped>
.footer {
  background: #2c3e50;
  color: #ecf0f1;
  margin-top: 40px;
  padding: 40px 0 0 0;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.footer h4 {
  font-size: 18px;
  margin-bottom: 16px;
  color: #fff;
}

.footer-desc {
  color: #bdc3c7;
  font-size: 14px;
  line-height: 1.8;
}

.footer-links {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.footer-links a {
  color: #bdc3c7;
  text-decoration: none;
  cursor: pointer;
  font-size: 14px;
  transition: color 0.3s;
}

.footer-links a:hover {
  color: #fff;
}

.footer-contact p {
  color: #bdc3c7;
  font-size: 14px;
  margin: 6px 0;
}

.footer-bottom {
  border-top: 1px solid #34495e;
  padding: 20px 0;
  margin-top: 30px;
  text-align: center;
}

.footer-bottom p {
  color: #7f8c8d;
  font-size: 13px;
  margin: 0;
}
</style>