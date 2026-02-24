const api = require('../../utils/api')

Page({
  data: {
    keyword: '',
    history: [],
    hotKeywords: ['周杰伦', '陈奕迅', 'Beyond', '流行', '摇滚'],
    results: [],
    searching: false
  },

  onLoad(options) {
    const category = options.category
    if (category) {
      this.setData({ keyword: category })
      this.search()
    }
    this.loadHistory()
  },

  loadHistory() {
    const history = wx.getStorageSync('searchHistory') || []
    this.setData({ history })
  },

  onInput(e) {
    this.setData({ keyword: e.detail.value })
  },

  onClear() {
    this.setData({ keyword: '', results: [] })
  },

  async search() {
    if (!this.data.keyword.trim()) return
    
    this.setData({ searching: true })
    this.saveHistory(this.data.keyword)
    
    try {
      const res = await api.searchSong(this.data.keyword)
      this.setData({ results: res.data || [] })
    } catch (e) {
      console.error(e)
    } finally {
      this.setData({ searching: false })
    }
  },

  saveHistory(keyword) {
    let history = this.data.history.filter(h => h !== keyword)
    history.unshift(keyword)
    history = history.slice(0, 20)
    this.setData({ history })
    wx.setStorageSync('searchHistory', history)
  },

  onHistoryTap(e) {
    const keyword = e.currentTarget.dataset.keyword
    this.setData({ keyword })
    this.search()
  },

  clearHistory() {
    wx.removeStorageSync('searchHistory')
    this.setData({ history: [] })
  },

  onSongTap(e) {
    const song = e.currentTarget.dataset.song
    wx.navigateTo({ url: `/pages/player/player?id=${song.id}` })
  }
})
