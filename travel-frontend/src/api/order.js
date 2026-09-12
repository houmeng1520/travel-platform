import request from '@/utils/request'

// 创建订单
export function createOrder(data) {
  return request({
    url: '/order/create',
    method: 'post',
    data
  })
}

// 获取我的订单列表
export function getMyOrders(orderType) {
  return request({
    url: '/order/my-orders',
    method: 'get',
    params: { orderType }
  })
}

// 获取订单详情
export function getOrderDetail(id) {
  return request({
    url: `/order/detail/${id}`,
    method: 'get'
  })
}

// 支付订单
export function payOrder(id) {
  return request({
    url: `/order/pay/${id}`,
    method: 'put'
  })
}

// 取消订单
export function cancelOrder(id) {
  return request({
    url: `/order/cancel/${id}`,
    method: 'put'
  })
}