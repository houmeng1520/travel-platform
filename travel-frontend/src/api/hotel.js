import request from '@/utils/request'

// 获取所有酒店
export function getHotelList() {
  return request({
    url: '/hotel/list',
    method: 'get'
  })
}

// 搜索酒店
export function searchHotel(city, keyword) {
  return request({
    url: '/hotel/search',
    method: 'get',
    params: { city, keyword }
  })
}

// 获取酒店详情
export function getHotelDetail(id) {
  return request({
    url: `/hotel/detail/${id}`,
    method: 'get'
  })
}

// 获取热门酒店
export function getHotHotel() {
  return request({
    url: '/hotel/hot',
    method: 'get'
  })
}

// 获取酒店统计数据
export function getHotelStats() {
  return request({
    url: '/hotel/stats',
    method: 'get'
  })
}

// 后台管理 - 获取所有酒店
export function getAdminHotelList() {
  return request({
    url: '/hotel/admin/list',
    method: 'get'
  })
}