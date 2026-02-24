const app = getApp()

Page({
  data: {
    userInfo: null,
    isVip: false,
    vipExpireTime: ''
  },

  onShow() {
    this.loadUserInfo()
  },

  loadUserInfo() {
    const userInfo = app.globalData.userInfo
    if (userInfo) {
      this.setData({
        userInfo,
        isVip: userInfo.isVip === 1,
        vipExpireTime: userInfo.vipExpireTime || ''
      })
    }
  },

  onLoginTap() {
    wx.navigateTo({ url: '/pages/login/login' })
  },

  onVipTap() {
    wx.navigateTo({ url: '/pages/vip/vip' })
  },

  onOrderTap() {
    wx.navigateTo({ url: '/pages/order/order' })
  },

  onPlaylistTap() {
    wx.navigateTo({ url: '/pages/playlist/playlist' })
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
          this.setData({ userInfo: null })
          wx.showToast({ title: '已退出登录', icon: 'success' })
        }
      }
    })
  }
})
