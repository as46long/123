const api = require('../../utils/api')
const app = getApp()

Page({
  data: {
    userInfo: null,
    packages: [
      { type: 'WEEK', name: '周卡', price: 5, desc: '7天VIP特权', days: 7 },
      { type: 'MONTH', name: '月卡', price: 15, desc: '30天VIP特权', days: 30 },
      { type: 'QUARTER', name: '季卡', price: 40, desc: '90天VIP特权', days: 90, recommend: true },
      { type: 'YEAR', name: '年卡', price: 128, desc: '365天VIP特权', days: 365 }
    ],
    selectedPackage: 2,
    loading: false,
    expireTimeText: ''
  },

  onShow() {
    this.loadUserInfo()
  },

  async loadUserInfo() {
    if (!app.globalData.token) {
      wx.showModal({
        title: '提示',
        content: '请先登录后再开通会员',
        showCancel: true,
        confirmText: '去登录',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({ url: '/pages/login/login' })
          } else {
            wx.navigateBack()
          }
        }
      })
      return
    }

    try {
      const res = await api.getUserInfo()
      this.setData({ 
        userInfo: res.data,
        expireTimeText: this.formatExpireTime(res.data.vipExpireTime)
      })
      app.globalData.userInfo = res.data
    } catch (e) {
      console.error('获取用户信息失败', e)
    }
  },

  formatExpireTime(time) {
    if (!time) return ''
    // 处理 LocalDateTime 格式 (如: 2026-06-02T12:17:07)
    const date = new Date(time.replace('T', ' '))
    if (isNaN(date.getTime())) return time
    
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  },

  selectPackage(e) {
    this.setData({ selectedPackage: e.currentTarget.dataset.index })
  },

  async handlePay() {
    // 检查登录状态
    if (!app.globalData.token) {
      wx.showModal({
        title: '提示',
        content: '请先登录后再开通会员',
        confirmText: '去登录',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({ url: '/pages/login/login' })
          }
        }
      })
      return
    }

    const pkg = this.data.packages[this.data.selectedPackage]
    
    // 显示确认弹窗
    wx.showModal({
      title: '确认开通',
      content: `确定开通${pkg.name}（¥${pkg.price}）？`,
      confirmText: '确认开通',
      confirmColor: '#FF6B9D',
      success: async (res) => {
        if (res.confirm) {
          await this.doPay(pkg)
        }
      }
    })
  },

  async doPay(pkg) {
    if (this.data.loading) return
    this.setData({ loading: true })
    
    wx.showLoading({ title: '处理中...', mask: true })
    
    try {
      // 1. 创建订单
      const orderRes = await api.createOrder({ 
        packageType: pkg.type, 
        amount: pkg.price 
      })
      
      // 2. 直接支付成功（模拟支付）
      await api.payNotify(orderRes.data.orderNo)
      
      wx.hideLoading()
      
      // 3. 重新获取用户信息
      await this.loadUserInfo()
      
      // 4. 显示成功提示
      wx.showToast({ 
        title: '开通成功', 
        icon: 'success',
        duration: 2000
      })
      
      // 5. 延迟返回上一页
      setTimeout(() => {
        wx.navigateBack()
      }, 2000)
      
    } catch (e) {
      wx.hideLoading()
      console.error('开通失败', e)
      
      let errorMsg = '开通失败，请稍后重试'
      if (e.message) {
        errorMsg = e.message
      }
      
      wx.showModal({
        title: '开通失败',
        content: errorMsg,
        showCancel: false,
        confirmText: '知道了'
      })
    } finally {
      this.setData({ loading: false })
    }
  }
})
