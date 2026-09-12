import request from '@/utils/request'

// 创建预订
export function createHotelOrder(data) {
  return request({
    url: '/hotel-order/create',
    method: 'post',
    data
  })
}

// 获取用户预订列表
export function getHotelOrderList(userId) {
  return request({
    url: '/hotel-order/list',
    method: 'get',
    params: { userId }
  })
}

// 获取预订详情
export function getHotelOrderDetail(id) {
  return request({
    url: `/hotel-order/detail/${id}`,
    method: 'get'
  })
}

// 取消预订
export function cancelHotelOrder(id) {
  return request({
    url: `/hotel-order/cancel/${id}`,
    method: 'put'
  })
}