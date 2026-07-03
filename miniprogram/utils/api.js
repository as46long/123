const BASE_URL = 'http://localhost:8080/api'

const request = (options) => {
  const token = wx.getStorageSync('token') || ''
  return new Promise((resolve, reject) => {
    const header = {
      'Content-Type': 'application/json'
    }
    // 只有存在 token 时才添加 Authorization 头
    if (token) {
      header['Authorization'] = `Bearer ${token}`
    }

    wx.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: header,
      success: (res) => {
        if (res.statusCode === 403) {
          // Token 可能过期或无效，判断是否为公开接口
          const isPublicApi =
            (options.url.startsWith('/song/') && (options.method === 'GET' || !options.method)) ||
            (options.url.startsWith('/comment/') && (options.method === 'GET' || !options.method)) ||
            (options.url.startsWith('/songComment/') && (options.method === 'GET' || !options.method)) ||
            (options.url.startsWith('/system/') && (options.method === 'GET' || !options.method)) ||
            options.url.match(/\/song\/play\//)
          if (isPublicApi) {
            // 公开接口，清除无效 token 后重试
            wx.removeStorageSync('token')
            wx.removeStorageSync('userInfo')
            delete header['Authorization']
            wx.request({
              url: BASE_URL + options.url,
              method: options.method || 'GET',
              data: options.data,
              header: header,
              success: (retryRes) => {
                if (retryRes.data.code === 200) {
                  resolve(retryRes.data)
                } else {
                  wx.showToast({ title: retryRes.data.message || '请求失败', icon: 'none' })
                  reject(retryRes.data)
                }
              },
              fail: (err) => {
                wx.showToast({ title: '网络错误', icon: 'none' })
                reject(err)
              }
            })
            return
          }
          // 非公开接口的 403，提示用户重新登录
          wx.showToast({ title: '请重新登录', icon: 'none' })
          wx.removeStorageSync('token')
          wx.removeStorageSync('userInfo')
          reject({ code: 403, message: '请重新登录' })
          return
        }
        if (res.data.code === 200) {
          resolve(res.data)
        } else {
          wx.showToast({ title: res.data.message || '请求失败', icon: 'none' })
          reject(res.data)
        }
      },
      fail: (err) => {
        wx.showToast({ title: '网络错误', icon: 'none' })
        reject(err)
      }
    })
  })
}

module.exports = {
  // 导出 request 方法供其他页面使用
  request,
  // 用户相关
  login: (data) => request({ url: '/user/login', method: 'POST', data }),
  wxLogin: (data) => request({ url: '/user/wxLogin', method: 'POST', data }),
  wxLoginWithInfo: (data) => request({ url: '/user/wxLoginWithInfo', method: 'POST', data }),
  register: (data) => request({ url: '/user/register', method: 'POST', data }),
  getUserInfo: () => request({ url: '/user/info' }),
  getUserStats: () => request({ url: '/user/stats' }),
  updateUserInfo: (data) => request({ url: '/user/update', method: 'PUT', data }),
  // 歌曲相关
  getRecommend: (userId) => request({ url: `/song/recommend`, data: { userId } }),
  searchSong: (keyword) => request({ url: '/song/search', data: { keyword } }),
  getSongDetail: (id) => request({ url: `/song/detail/${id}` }),
  getLyrics: (id) => request({ url: `/song/lyrics/${id}` }),
  playSong: (id) => request({ url: `/song/play/${id}`, method: 'POST' }),
  // 收藏相关
  addFavorite: (songId) => request({ url: '/favorite/add', method: 'POST', data: { songId } }),
  removeFavorite: (songId) => request({ url: `/favorite/delete/${songId}`, method: 'DELETE' }),
  getFavorites: () => request({ url: '/favorite/list' }),
  // 订单相关
  createOrder: (data) => request({ url: '/order/create', method: 'POST', data }),
  payNotify: (orderNo) => request({ url: `/order/payNotify?orderNo=${orderNo}`, method: 'POST' }),
  getMyOrders: (params) => request({ url: '/order/my', data: params }),
  // 评论相关
  getComments: (params) => request({ url: '/comment/list', data: params }),
  postComment: (data) => request({ url: '/comment/post', method: 'POST', data }),
  likeComment: (id) => request({ url: `/comment/like/${id}`, method: 'POST' }),
  getRecommendComments: (userId) => request({ url: `/comment/recommend/${userId}` }),
  getMyComments: (params) => request({ url: '/comment/my', data: params }),
  // 歌曲评论相关
  getSongComments: (songId, params) => request({ url: `/songComment/list`, data: { songId, ...params } }),
  postSongComment: (data) => request({ url: '/songComment/post', method: 'POST', data }),
  likeSongComment: (id) => request({ url: `/songComment/like/${id}`, method: 'POST' }),
  getMySongComments: (params) => request({ url: '/songComment/my', data: params }),
  // 分类歌曲
  getSongsByCategory: (category) => request({ url: `/song/category/${encodeURIComponent(category)}` }),
  // 热门歌曲
  getHotSongs: (limit) => request({ url: '/song/hot', data: { limit } }),
  // 启动画面
  getSplashImage: () => request({ url: '/system/splashImage' })
}
