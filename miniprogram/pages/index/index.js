const api = require('../../utils/api')
const app = getApp()

Page({
  data: {
    banners: [],
    recommendSongs: [],
    categories: ['流行', '摇滚', '古典', '民谣', '电子'],
    loading: true
  },

  onLoad() {
    this.loadData()
  },

  onPullDownRefresh() {
    this.loadData().then(() => wx.stopPullDownRefresh())
  },

  async loadData() {
    try {
      const userId = app.globalData.userInfo?.id || 0
      const res = await api.getRecommend(userId)
      this.setData({
        recommendSongs: res.data || [],
        loading: false
      })
    } catch (e) {
      this.setData({ loading: false })
    }
  },

  onSearchTap() {
    wx.navigateTo({ url: '/pages/search/search' })
  },

  onCategoryTap(e) {
    const category = e.currentTarget.dataset.category
    wx.navigateTo({ url: `/pages/search/search?category=${category}` })
  },

  onSongTap(e) {
    const song = e.currentTarget.dataset.song
    wx.navigateTo({ url: `/pages/player/player?id=${song.id}` })
  },

  onPlayAllTap() {
    if (this.data.recommendSongs.length > 0) {
      const songs = this.data.recommendSongs
      wx.navigateTo({ url: `/pages/player/player?id=${songs[0].id}&playAll=true` })
    }
  }
})
