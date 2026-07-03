const api = require('../../utils/api')
const app = getApp()

Page({
  data: {
    keyword: '',
    history: [],
    hotKeywords: ['周杰伦', '陈奕迅', 'Beyond', '流行', '摇滚'],
    results: [],
    searching: false,
    fromCategory: false
  },

  onLoad(options) {
    this.loadHistory()
    const category = options.category
    if (category) {
      this.setData({ keyword: category, fromCategory: true })
      this.searchByCategory(category)
    }
  },

  loadHistory() {
    const history = wx.getStorageSync('searchHistory') || []
    this.setData({ history })
  },

  onInput(e) {
    this.setData({ keyword: e.detail.value })
  },

  onClear() {
    this.setData({ keyword: '', results: [], fromCategory: false })
  },

 async search() {
   if (!this.data.keyword.trim()) return
   
   this.setData({ searching: true, fromCategory: false })
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

 async searchByCategory(category) {
   this.setData({ searching: true })
   
   try {
     const res = await api.getSongsByCategory(category)
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
  },

  onPlayAll() {
    if (this.data.results.length > 0) {
      const songs = this.data.results
      app.globalData.playAllList = songs
      wx.navigateTo({ url: `/pages/player/player?id=${songs[0].id}&playAll=true` })
    }
  }
})
