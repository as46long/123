const api = require('../../utils/api')

Page({
  data: {
    tabs: ['我的收藏', '推荐歌单'],
    currentTab: 0,
    favorites: [],
    recommends: [],
    loading: false
  },

  onShow() {
    this.loadData()
  },

  async loadData() {
    this.setData({ loading: true })
    try {
      const [favRes, recRes] = await Promise.all([
        api.getFavorites(),
        api.getRecommend(getApp().globalData.userInfo?.id || 0)
      ])
      this.setData({
        favorites: favRes.data || [],
        recommends: recRes.data || []
      })
    } catch (e) {
      console.error(e)
    } finally {
      this.setData({ loading: false })
    }
  },

  switchTab(e) {
    this.setData({ currentTab: e.currentTarget.dataset.index })
  },

  onSongTap(e) {
    const song = e.currentTarget.dataset.song
    wx.navigateTo({ url: `/pages/player/player?id=${song.id}` })
  },

  async onPlayAll() {
    const songs = this.data.currentTab === 0 ? this.data.favorites : this.data.recommends
    if (songs.length > 0) {
      wx.navigateTo({ url: `/pages/player/player?id=${songs[0].id}&playAll=true` })
    }
  }
})
