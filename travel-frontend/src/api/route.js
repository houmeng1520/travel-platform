import request from '@/utils/request'

// 获取所有路线
export function getRouteList() {
  return request({
    url: '/route/list',
    method: 'get'
  })
}

// 搜索路线
export function searchRoute(city, keyword) {
  return request({
    url: '/route/search',
    method: 'get',
    params: { city, keyword }
  })
}

// 获取路线详情
export function getRouteDetail(id) {
  return request({
    url: `/route/detail/${id}`,
    method: 'get'
  })
}