import request from '@/utils/request'

// 获取所有美食
export function getFoodList() {
  return request({
    url: '/food/list',
    method: 'get'
  })
}

// 搜索美食
export function searchFood(category, city, keyword) {
  return request({
    url: '/food/search',
    method: 'get',
    params: { category, city, keyword }
  })
}

// 获取美食详情
export function getFoodDetail(id) {
  return request({
    url: `/food/detail/${id}`,
    method: 'get'
  })
}

//获取美食统计数据
export function getFoodStats(){
	return request({
		url: '/food/stats',
		method: 'get'
	})
}

//获取热门美食
export function getHotFood(){
	return request ({
		url:'/food/hot',
		method:'get'
	})
}

//获取特价美食
export function getDiscountFood(){
	return request({
		url:'/food/discount',
		method: 'get'
	})
}

// 获取城市列表（复用景点接口，或单独写）
export function getCities() {
  return request({
    url: '/scenic/cities',  // 复用景点接口
    method: 'get'
  })
}