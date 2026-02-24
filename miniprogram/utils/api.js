const BASE_URL = 'http://localhost:8080/api'

const request = (options) => {
  const token = wx.getStorageSync('token') || ''
  return new Promise((resolve, reject) => {
    wx.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      success: (res) => {
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
  login: (data) => request({ url: '/user/login', method: 'POST', data }),
  wxLogin: (code) => request({ url: '/user/wxLogin', method: 'POST', data: { code } }),
  register: (data) => request({ url: '/user/register', method: 'POST', data }),
  getUserInfo: () => request({ url: '/user/info' }),
  updateUserInfo: (data) => request({ url: '/user/update', method: 'PUT', data }),
  getRecommend: (userId) => request({ url: `/song/recommend/${userId}` }),
  searchSong: (keyword) => request({ url: '/song/search', data: { keyword } }),
  getSongDetail: (id) => request({ url: `/song/detail/${id}` }),
  getLyrics: (id) => request({ url: `/song/lyrics/${id}` }),
  playSong: (id) => request({ url: `/song/play/${id}`, method: 'POST' }),
  addFavorite: (songId) => request({ url: '/favorite/add', method: 'POST', data: { songId } }),
  removeFavorite: (songId) => request({ url: `/favorite/delete/${songId}`, method: 'DELETE' }),
  getFavorites: () => request({ url: '/favorite/list' }),
  createOrder: (data) => request({ url: '/order/create', method: 'POST', data }),
  getMyOrders: (params) => request({ url: '/order/my', data: params }),
  getComments: (params) => request({ url: '/comment/list', data: params }),
  postComment: (data) => request({ url: '/comment/post', method: 'POST', data }),
  likeComment: (id) => request({ url: `/comment/like/${id}`, method: 'POST' }),
  getRecommendComments: (userId) => request({ url: `/comment/recommend/${userId}` })
}
