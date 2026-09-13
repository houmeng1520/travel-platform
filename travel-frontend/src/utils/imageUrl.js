// 获取完整图片URL
export function getFullImageUrl(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  if (url.startsWith('/uploads')) {
    const baseUrl = import.meta.env.VITE_API_BASE_URL
    const apiPrefix = '/api'
    const serverUrl = baseUrl.endsWith(apiPrefix)
      ? baseUrl.slice(0, -apiPrefix.length)
      : baseUrl
    return `${serverUrl}${url}`
  }
  return url
}

// 获取图片列表中的第一张图片
export function getFirstImageUrl(images) {
  if (!images) return ''
  try {
    const list = JSON.parse(images)
    if (Array.isArray(list) && list.length > 0) {
      return getFullImageUrl(list[0])
    }
    return ''
  } catch {
    return getFullImageUrl(images)
  }
}