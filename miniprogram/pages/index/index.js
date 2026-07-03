const api = require('../../utils/api')
const audioManager = require('../../utils/audioManager')
const app = getApp()

Page({
  data: {
    banners: [],
    recommendSongs: [],
    categories: [],
    categorySongs: {},
    loading: true,
    showMusicBar: false,
    userInfo: null,
    greetingText: '发现好音乐',
    greetingEmoji: '🎵'
  },

  onLoad() {
    this.initCategories()
    this.updateGreeting()
    this.loadData()
    this.checkCurrentSong()
    this.getUserInfo()
  },

  onShow() {
    this.checkCurrentSong()
    this.getUserInfo()
  },

  onUnload() {
    if (this.songChangeHandler) {
      audioManager.off('songChange', this.songChangeHandler)
    }
  },

  onPullDownRefresh() {
    this.loadData().then(() => wx.stopPullDownRefresh())
  },

  // 初始化分类（带图标）
  initCategories() {
    const categories = [
      { id: '流行', name: '流行', icon: '🎤' },
      { id: '摇滚', name: '摇滚', icon: '🎸' },
      { id: '古典', name: '古典', icon: '🎻' },
      { id: '民谣', name: '民谣', icon: '🪕' },
      { id: '电子', name: '电子', icon: '🎹' }
    ]
    this.setData({ categories })
  },

  // 根据时间更新问候语
  updateGreeting() {
    const hour = new Date().getHours()
    let greeting = '发现好音乐'
    let emoji = '🎵'
    
    if (hour < 6) {
      greeting = '夜深了'
      emoji = '🌙'
    } else if (hour < 12) {
      greeting = '早上好'
      emoji = '☀️'
    } else if (hour < 18) {
      greeting = '下午好'
      emoji = '🌤️'
    } else {
      greeting = '晚上好'
      emoji = '🌆'
    }
    
    this.setData({ greetingText: greeting, greetingEmoji: emoji })
  },

  // 获取用户信息
  getUserInfo() {
    if (app.globalData.userInfo) {
      this.setData({ userInfo: app.globalData.userInfo })
    }
  },

  async loadData() {
    try {
      const userId = app.globalData.userInfo?.id || 0
      const [recommendRes, ...categoryResults] = await Promise.all([
        api.getRecommend(userId),
        ...this.data.categories.map(cat => api.getSongsByCategory(cat.id).catch(() => ({ data: [] })))
      ])
      
      const categorySongs = {}
      this.data.categories.forEach((cat, index) => {
        categorySongs[cat.id] = categoryResults[index]?.data || []
      })
      
      this.setData({
        recommendSongs: recommendRes.data || [],
        categorySongs,
        loading: false
      })
    } catch (e) {
      this.setData({ loading: false })
    }
  },

  checkCurrentSong() {
    const currentSong = audioManager.getCurrentSong()
    this.setData({
      showMusicBar: !!currentSong
    })
    if (!this.songChangeHandler) {
      this.songChangeHandler = (song) => {
        this.setData({
          showMusicBar: !!song
        })
      }
      audioManager.on('songChange', this.songChangeHandler)
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
      app.globalData.playAllList = songs
      wx.navigateTo({ url: `/pages/player/player?id=${songs[0].id}&playAll=true` })
    }
  },

  goToProfile() {
    wx.switchTab({ url: '/pages/profile/profile' })
  },

  goToLogin() {
    wx.navigateTo({ url: '/pages/login/login' })
  }
})
