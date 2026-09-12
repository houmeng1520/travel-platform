import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

// ===== 用户端页面 =====
import Login from '@/views/login/Login.vue'
import Register from '@/views/login/Register.vue'
import Home from '@/views/home/Home.vue'
import ScenicList from '@/views/scenic/ScenicList.vue'
import ScenicDetail from '@/views/scenic/ScenicDetail.vue'
import FoodList from '@/views/food/FoodList.vue'
import FoodDetail from '@/views/food/FoodDetail.vue'
import RouteList from '@/views/route/RouteList.vue'
import RouteDetail from '@/views/route/RouteDetail.vue'
import HotelList from '@/views/hotel/HotelList.vue'
import HotelDetail from '@/views/hotel/HotelDetail.vue'
import HotelBooking from '@/views/hotel/HotelBooking.vue'
import OrderSuccess from '@/views/hotel/OrderSuccess.vue'
import Payment from '@/views/hotel/Payment.vue'
import PaymentSuccess from '@/views/hotel/PaymentSuccess.vue'
import Booking from '@/views/order/Booking.vue'
import About from '@/views/about/About.vue'
import Contact from '@/views/about/Contact.vue'
import Profile from '@/views/user/Profile.vue'

// ===== 管理后台页面 =====
import AdminLayout from '@/components/admin/AdminLayout.vue'
import Admin from '@/views/admin/Admin.vue'
import ScenicManage from '@/views/admin/ScenicManage.vue'
import FoodManage from '@/views/admin/FoodManage.vue'
import RouteManage from '@/views/admin/RouteManage.vue'
import HotelManage from '@/views/admin/HotelManage.vue'
// import HotelOrderManage from '@/views/admin/HotelOrderManage.vue' //酒店预订
import OrderManage from '@/views/admin/OrderManage.vue'
import BannerManage from '@/views/admin/BannerManage.vue'
import UserManage from '@/views/admin/UserManage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // ===== 用户端路由 =====
    { path: '/', redirect: '/home' },
    { path: '/login', name: 'login', component: Login },
    { path: '/register', name: 'register', component: Register },
    { path: '/home', name: 'home', component: Home },
    { path: '/about', name: 'about', component: About },
    { path: '/contact', name: 'contact', component: Contact },
    { path: '/profile', name: 'profile', component: Profile },
    { path: '/order/booking', name: 'booking', component: Booking },
    { path: '/order-success', name: 'orderSuccess', component: OrderSuccess },
    { path: '/payment', name: 'payment', component: Payment },
    { path: '/payment-success', name: 'paymentSuccess', component: PaymentSuccess },
    
    { path: '/scenic', name: 'scenic', component: ScenicList },
    { path: '/scenic/detail/:id', name: 'scenicDetail', component: ScenicDetail },
    
    { path: '/food', name: 'food', component: FoodList },
    { path: '/food/detail/:id', name: 'foodDetail', component: FoodDetail },
    
    { path: '/route', name: 'route', component: RouteList },
    { path: '/route/detail/:id', name: 'routeDetail', component: RouteDetail },
    
    { path: '/hotel', name: 'hotel', component: HotelList },
    { path: '/hotel/detail/:id', name: 'hotelDetail', component: HotelDetail },
    { path: '/hotel/booking', name: 'hotelBooking', component: HotelBooking },

    // ===== 管理后台路由 =====
    {
      path: '/admin',
      component: AdminLayout,
      meta: { requiresAuth: true },
      children: [
        { path: '', name: 'admin', component: Admin },
        { path: 'scenic', name: 'adminScenic', component: ScenicManage },
        { path: 'food', name: 'adminFood', component: FoodManage },
        { path: 'route', name: 'adminRoute', component: RouteManage },
        { path: 'hotel', name: 'adminHotel', component: HotelManage },
        // { path: 'hotel-order', name: 'adminHotelOrder', component: HotelOrderManage }, /之前的酒店预订
        { path: 'order', name: 'adminOrder', component: OrderManage },
        { path: 'banner', name: 'adminBanner', component: BannerManage },
        { path: 'user', name: 'adminUser', component: UserManage }
      ]
    }
  ]
})

// ===== 路由守卫 =====
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role') || 'USER'

  if (to.meta.requiresAuth) {
    if (!token) {
      ElMessage.warning('请先登录')
      next('/login')
      return
    }
    if (role !== 'ADMIN') {
      ElMessage.error('需要管理员权限')
      next('/home')
      return
    }
    next()
    return
  }

  if (to.path === '/login' || to.path === '/register') {
    if (token) {
      next('/home')
    } else {
      next()
    }
    return
  }

  next()
})

export default router