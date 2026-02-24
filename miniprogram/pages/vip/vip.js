const api = require('../../utils/api')
const app = getApp()

Page({
  data: {
    userInfo: null,
    packages: [
      { type: 'MONTH', name: '月卡', price: 15, desc: '30天VIP特权' },
      { type: 'QUARTER', name: '季卡', price: 40, desc: '90天VIP特权', recommend: true },
      { type: 'YEAR', name: '年卡', price: 128, desc: '365天VIP特权' }
    ],
    selectedPackage: 1
  },

  onShow() {
    this.setData({ userInfo: app.globalData.userInfo })
  },

  selectPackage(e) {
    this.setData({ selectedPackage: e.currentTarget.dataset.index })
  },

  async handlePay() {
    const pkg = this.data.packages[this.data.selectedPackage]
    try {
      const res = await api.createOrder({ packageType: pkg.type, amount: pkg.price })
      // 模拟支付成功
      wx.showModal({
        title: '模拟支付',
        content: `确认支付¥${pkg.price}开通${pkg.name}？`,
        success: async (modalRes) => {
          if (modalRes.confirm) {
            await api.request({ url: `/order/payNotify?orderNo=${res.data.orderNo}`, method: 'POST' })
            wx.showToast({ title: '开通成功', icon: 'success' })
            app.getUserInfo()
            setTimeout(() => wx.navigateBack(), 1500)
          }
        }
      })
    } catch (e) {
      console.error(e)
    }
  }
})
