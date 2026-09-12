import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 从 localStorage 恢复数据
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  const userId = ref(localStorage.getItem('userId') || '')
  const username = ref(localStorage.getItem('username') || '')
  const role = ref(localStorage.getItem('role') || 'USER')

  // 设置用户（登录成功后调用）
  function setUser(data) {
    token.value = data.token
    userInfo.value = data.user || { 
		id: data.userId, 
		username: data.username, 
		nickname: data.nickname ,
		role: data.role //新增管理员控制
		}
    userId.value = data.userId
    username.value = data.username
    role.value = data.role || 'USER' //新增管理员控制

    localStorage.setItem('token', data.token)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    localStorage.setItem('userId', data.userId)
    localStorage.setItem('username', data.username)
	localStorage.setItem('role', data.role || 'USER')//新增管理员控制
  }

  // 清除用户（退出登录时调用）
  function clearUser() {
    token.value = ''
    userInfo.value = null
    userId.value = ''
    username.value = ''
	role.value = 'USER' //新增管理员控制

    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
	localStorage.removeItem('role') //新增管理员控制
  }

  // 判断是否已登录
  function isLoggedIn() {
    return !!token.value
  }

//判断是否为管理员函数
  function isAdmin(){
	  return role.value === 'ADMIN' //判断是否为管理员
  }

  return {
    token,
    userInfo,
    userId,
    username,
	role, //新增
    setUser,
    clearUser,
    isLoggedIn,
	isAdmin //新增
  }
})