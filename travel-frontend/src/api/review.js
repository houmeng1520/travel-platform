import request from '@/utils/request'

// 获取目标评价（通用）
export function getTargetReviews(orderType, targetId) {
  return request({
    url: `/review/target/${orderType}/${targetId}`,
    method: 'get'
  })
}

// 获取目标评价统计
export function getTargetReviewStats(orderType, targetId) {
  return request({
    url: `/review/target/${orderType}/${targetId}/stats`,
    method: 'get'
  })
}

// 兼容：获取酒店评价
export function getHotelReviews(hotelId) {
  return request({
    url: `/review/hotel/${hotelId}`,
    method: 'get'
  })
}

// 兼容：获取酒店评价统计
export function getHotelReviewStats(hotelId) {
  return request({
    url: `/review/hotel/${hotelId}/stats`,
    method: 'get'
  })
}

// 提交评价
export function submitReview(data) {
  return request({
    url: '/review/submit',
    method: 'post',
    data
  })
}

// 获取用户的所有评价
export function getUserReviews() {
  return request({
    url: '/review/user',
    method: 'get'
  })
}

// 删除评价
export function deleteReview(id) {
  return request({
    url: `/review/delete/${id}`,
    method: 'delete'
  })
}