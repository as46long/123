const api = require('../../utils/api')
const app = getApp()

Page({
  data: {
    userInfo: null,
    isVip: false,
    vipExpireTime: '',
    stats: {
      favoriteCount: 0,
      commentCount: 0,
      playCount: 0
    },
    showMusicBar: false
  },

  onShow() {
    this.loadUserInfo()
    this.checkCurrentSong()
  },

  onUnload() {
    const audioManager = require('../../utils/audioManager')
    if (this.songChangeHandler) {
      audioManager.off('songChange', this.songChangeHandler)
    }
  },

  loadUserInfo() {
    const userInfo = app.globalData.userInfo
    if (userInfo) {
      this.setData({
        userInfo,
        isVip: userInfo.isVip === 1,
        vipExpireTime: userInfo.vipExpireTime ? this.formatDate(userInfo.vipExpireTime) : ''
      })
      this.loadStats()
    }
  },

  async loadStats() {
    if (!app.globalData.userInfo || !app.globalData.userInfo.id) return
    
    try {
      const res = await api.getUserStats()
      this.setData({
        'stats.favoriteCount': res.data.favoriteCount || 0,
        'stats.commentCount': res.data.commentCount || 0,
        'stats.playCount': res.data.playCount || 0
      })
    } catch (e) {
      console.error('加载统计数据失败:', e)
    }
  },

  formatDate(timestamp) {
    if (!timestamp) return ''
    const date = new Date(timestamp)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  },

  checkCurrentSong() {
    const audioManager = require('../../utils/audioManager')
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

  onLoginTap() {
    wx.navigateTo({ url: '/pages/login/login' })
  },

  onEditProfile() {
    wx.navigateTo({ url: '/pages/edit-profile/edit-profile' })
  },

  onVipTap() {
    wx.navigateTo({ url: '/pages/vip/vip' })
  },

  onOrderTap() {
    wx.navigateTo({ url: '/pages/order/order' })
  },

  onPlaylistTap() {
    wx.switchTab({ url: '/pages/playlist/playlist' })
  },

  onMyCommentsTap() {
    if (!app.globalData.userInfo) {
      wx.navigateTo({ url: '/pages/login/login' })
      return
    }
    wx.navigateTo({ url: '/pages/my-comments/my-comments' })
  },

  onLogout() {
    wx.showModal({
      title: '提示',
      content: '确定退出登录？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('token')
          app.globalData.userInfo = null
          app.globalData.token = ''
          this.setData({ userInfo: null, stats: { favoriteCount: 0, commentCount: 0, playCount: 0 } })
          wx.showToast({ title: '已退出登录', icon: 'success' })
        }
      }
    })
  }
})
