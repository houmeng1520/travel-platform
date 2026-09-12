import request from '@/utils/request'

/**
 * 获取所有景点列表
 * GET /api/scenic/list
 */
export function getScenicList() {
  return request({
    url: '/scenic/list',
    method: 'get'
  })
}

/**
 * 搜索景点（支持城市、分类、关键词）
 * GET /api/scenic/search?city=北京&category=历史文化&keyword=故宫
 * 
 * @param {string} city - 城市（可选）
 * @param {string} category - 分类（可选）
 * @param {string} keyword - 关键词（可选）
 */
export function searchScenic(city, category, keyword) {
  return request({
    url: '/scenic/search',
    method: 'get',
    params: { 
      city: city || '',      // 如果为空，传空字符串
      category: category || '',
      keyword: keyword || ''
    }
  })
}

/**
 * 根据ID获取景点详情
 * GET /api/scenic/detail/{id}
 */
export function getScenicDetail(id) {
  return request({
    url: `/scenic/detail/${id}`,
    method: 'get'
  })
}

//获取景点统计信息
export function getScenicStats() {
	return request({
		url: '/scenic/stats',
		method: 'get'
	})
	
}

//获取热门景点
export function getHotScenic() {
	return request({
		url: '/scenic/hot',
		method: 'get'
	})
}

//获取特价景点
export function getDiscountScenic(){
	return request({
		url:'/scenic/discount',
		method:'get'
	})
}
// 添加景点
export function addScenic(data) {
  return request({
    url: '/scenic/add',
    method: 'post',
    data
  })
}

// 更新景点
export function updateScenic(data) {
  return request({
    url: '/scenic/update',
    method: 'put',
    data
  })
}

// 删除景点
export function deleteScenic(id) {
  return request({
    url: `/scenic/delete/${id}`,
    method: 'delete'
  })
}
// 获取所有城市列表
export function getCities() {
  return request({
    url: '/scenic/cities',
    method: 'get'
  })
}