const api = require('../../utils/api')

Page({
  data: {
    orders: [],
    loading: true
  },

  onLoad() {
    this.loadOrders()
  },

  async loadOrders() {
    try {
      const res = await api.getMyOrders({ pageNum: 1, pageSize: 20 })
      this.setData({ orders: res.data?.records || [] })
    } catch (e) {
      console.error(e)
    } finally {
      this.setData({ loading: false })
    }
  },

  getPackageText(type) {
    const map = { MONTH: '月卡', QUARTER: '季卡', YEAR: '年卡' }
    return map[type] || type
  }
})
