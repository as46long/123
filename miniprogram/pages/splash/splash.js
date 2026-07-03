const api = require('../../utils/api')
const app = getApp()

Page({
  data: {
    imageUrl: '',
    showSplash: false,
    ready: false
  },

  onLoad() {
    console.log('=== Splash 页面加载 ===')
    this.initSplash()
  },

  async initSplash() {
    const splashShown = app.globalData.splashShown
    
    if (splashShown) {
      console.log('=== 启动画面已显示过，直接跳转 ===')
      this.goToHome()
      return
    }

    try {
      console.log('=== 开始加载启动画面图片 ===')
      const res = await api.getSplashImage()
      console.log('=== 图片响应 ===', res)
      
      if (res.data) {
        this.setData({ 
          imageUrl: res.data,
          showSplash: true,
          ready: true
        })
        app.globalData.splashShown = true
      } else {
        console.log('=== 无图片，直接跳转 ===')
        app.globalData.splashShown = true
        this.goToHome()
      }
    } catch (e) {
      console.error('=== 加载失败 ===', e)
      app.globalData.splashShown = true
      this.goToHome()
    }
  },

  onImageTap() {
    console.log('=== 点击启动画面 ===')
    app.globalData.splashShown = true
    this.goToHome()
  },

  goToHome() {
    wx.reLaunch({ url: '/pages/index/index' })
  }
})
