const api = require('../../utils/api')
const audioManager = require('../../utils/audioManager')
const app = getApp()

Page({
  data: {
    tabs: ['我的收藏', '推荐歌单'],
    currentTab: 0,
    favorites: [],
    recommends: [],
    currentList: [],
    loading: false,
    isLoggedIn: false,
    showMusicBar: false
  },

  onShow() {
    this.checkLoginAndLoad()
    this.checkCurrentSong()
  },

  checkCurrentSong() {
    const currentSong = audioManager.getCurrentSong()
    this.setData({
      showMusicBar: !!currentSong
    })
  },

  checkLoginAndLoad() {
    const isLoggedIn = !!app.globalData.token
    this.setData({ isLoggedIn })
    
    if (!isLoggedIn) {
      this.loadRecommendsOnly()
    } else {
      this.loadData()
    }
  },

  async loadRecommendsOnly() {
    this.setData({ loading: true })
    try {
      const recRes = await api.getRecommend(0)
      const recommends = recRes.data || []
      this.setData({
        favorites: [],
        recommends,
        currentList: recommends,
        currentTab: 1
      })
    } catch (e) {
      console.error('加载推荐失败', e)
    } finally {
      this.setData({ loading: false })
    }
  },

  async loadData() {
    this.setData({ loading: true })
    try {
      const [favRes, recRes] = await Promise.all([
        api.getFavorites().catch(err => {
          console.error('获取收藏失败', err)
          return { data: [] }
        }),
        api.getRecommend(app.globalData.userInfo?.id || 0)
      ])
      const favorites = favRes.data || []
      const recommends = recRes.data || []
      this.setData({
        favorites,
        recommends,
        currentList: this.data.currentTab === 0 ? favorites : recommends
      })
    } catch (e) {
      console.error('加载数据失败', e)
    } finally {
      this.setData({ loading: false })
    }
  },

  switchTab(e) {
    const currentTab = e.currentTarget.dataset.index
    this.setData({
      currentTab,
      currentList: currentTab === 0 ? this.data.favorites : this.data.recommends
    })
  },

  onSongTap(e) {
    const song = e.currentTarget.dataset.song
    wx.navigateTo({ url: `/pages/player/player?id=${song.id}` })
  },

  async onPlayAll() {
    const songs = this.data.currentList
    if (songs.length > 0) {
      // 保存播放列表到全局数据
      app.globalData.playAllList = songs
      wx.navigateTo({ url: `/pages/player/player?id=${songs[0].id}&playAll=true` })
    }
  },

  goToLogin() {
    wx.navigateTo({ url: '/pages/login/login' })
  }
})
