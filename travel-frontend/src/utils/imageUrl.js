export function getFullImageUrl(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  if (url.startsWith('/uploads')) {
    // 从 API 地址推出服务器地址
    const apiBase = import.meta.env.VITE_API_BASE_URL || ''
    const serverUrl = apiBase.replace(/\/api$/, '')
    return `${serverUrl}${url}`
  }
  return url
}

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