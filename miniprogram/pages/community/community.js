const api = require('../../utils/api')
const audioManager = require('../../utils/audioManager')
const app = getApp()

Page({
  data: {
    comments: [],
    content: '',
    loading: true,
    showMusicBar: false
  },

  onLoad() {
    this.loadComments()
  },

  onShow() {
    this.checkCurrentSong()
  },

  checkCurrentSong() {
    const currentSong = audioManager.getCurrentSong()
    this.setData({
      showMusicBar: !!currentSong
    })
  },

  async loadComments() {
    try {
      const res = await api.getComments({ pageNum: 1, pageSize: 20, status: 1 })
      this.setData({ comments: res.data?.records || [] })
    } catch (e) {
      console.error(e)
    } finally {
      this.setData({ loading: false })
    }
  },

  onInput(e) {
    this.setData({ content: e.detail.value })
  },

  async submitComment() {
    if (!this.data.content.trim()) {
      wx.showToast({ title: '请输入留言内容', icon: 'none' })
      return
    }
    if (!app.globalData.userInfo) {
      wx.navigateTo({ url: '/pages/login/login' })
      return
    }
    try {
      await api.postComment({ content: this.data.content })
      wx.showToast({ title: '发布成功', icon: 'success' })
      this.setData({ content: '' })
      this.loadComments()
    } catch (e) {
      console.error(e)
    }
  },

  async likeComment(e) {
    const id = e.currentTarget.dataset.id
    try {
      await api.likeComment(id)
      this.loadComments()
    } catch (e) {
      console.error(e)
    }
  }
})
