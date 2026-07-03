const api = require('../../utils/api')
const app = getApp()

Page({
  data: {
    comments: [],
    loading: true,
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    activeTab: 'community'
  },

  onLoad() {
    this.loadMyComments()
  },

  onShow() {
    this.setData({ pageNum: 1, comments: [] })
    this.loadMyComments()
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    if (tab === this.data.activeTab) return
    this.setData({
      activeTab: tab,
      pageNum: 1,
      comments: [],
      hasMore: true
    })
    this.loadMyComments()
  },

  async loadMyComments() {
    if (!this.data.hasMore) return
    this.setData({ loading: true })
    try {
      let res
      if (this.data.activeTab === 'community') {
        res = await api.getMyComments({
          pageNum: this.data.pageNum,
          pageSize: this.data.pageSize
        })
      } else {
        res = await api.getMySongComments({
          pageNum: this.data.pageNum,
          pageSize: this.data.pageSize
        })
      }
      const newComments = res.data?.records || []
      this.setData({
        comments: this.data.pageNum === 1 ? newComments : [...this.data.comments, ...newComments],
        hasMore: newComments.length >= this.data.pageSize,
        loading: false
      })
    } catch (e) {
      console.error(e)
      this.setData({ loading: false })
    }
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({ pageNum: this.data.pageNum + 1 })
      this.loadMyComments()
    }
  }
})