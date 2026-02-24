const api = require('../../utils/api')
const audioManager = require('../../utils/audioManager')
const app = getApp()

Page({
  data: {
    song: null,
    isPlaying: false,
    currentTime: 0,
    duration: 0,
    playMode: 'sequence',
    isFavorite: false,
    lyrics: [],
    currentLyricIndex: 0,
    showVipModal: false
  },

  onLoad(options) {
    const id = options.id
    if (id) {
      this.loadSong(id)
    }
    this.initAudioEvents()
  },

  onUnload() {
    audioManager.off('play', this.onPlay)
    audioManager.off('pause', this.onPause)
    audioManager.off('ended', this.onEnded)
    audioManager.off('timeUpdate', this.onTimeUpdate)
  },

  initAudioEvents() {
    audioManager.on('play', () => this.setData({ isPlaying: true }))
    audioManager.on('pause', () => this.setData({ isPlaying: false }))
    audioManager.on('ended', () => this.playNext())
    audioManager.on('timeUpdate', () => {
      this.setData({
        currentTime: audioManager.getCurrentTime(),
        duration: audioManager.getDuration()
      })
      this.updateLyric()
    })
  },

  async loadSong(id) {
    try {
      const res = await api.getSongDetail(id)
      const song = res.data
      
      if (song.isVip && !app.globalData.userInfo?.isVip) {
        this.setData({ showVipModal: true })
        return
      }

      this.setData({ song, isFavorite: song.isFavorite })
      this.playSong()
      this.loadLyrics(id)
      api.playSong(id)
    } catch (e) {
      console.error(e)
    }
  },

  playSong() {
    if (this.data.song) {
      audioManager.play(this.data.song)
    }
  },

  async loadLyrics(id) {
    try {
      const res = await api.getLyrics(id)
      const lyrics = this.parseLyrics(res.data || '')
      this.setData({ lyrics })
    } catch (e) {
      console.error(e)
    }
  },

  parseLyrics(text) {
    const lines = text.split('\n')
    return lines.map(line => {
      const match = line.match(/\[(\d{2}):(\d{2})\.(\d{2,3})\](.*)/)
      if (match) {
        const time = parseInt(match[1]) * 60 + parseInt(match[2]) + parseInt(match[3]) / 1000
        return { time, text: match[4] }
      }
      return null
    }).filter(Boolean)
  },

  updateLyric() {
    const { lyrics, currentTime, currentLyricIndex } = this.data
    for (let i = lyrics.length - 1; i >= 0; i--) {
      if (currentTime >= lyrics[i].time && i !== currentLyricIndex) {
        this.setData({ currentLyricIndex: i })
        break
      }
    }
  },

  togglePlay() {
    if (this.data.isPlaying) {
      audioManager.pause()
    } else {
      audioManager.resume()
    }
  },

  playPrev() {
    audioManager.prev()
  },

  playNext() {
    audioManager.next()
  },

  toggleMode() {
    const mode = audioManager.toggleMode()
    this.setData({ playMode: mode })
  },

  onSliderChange(e) {
    const value = e.detail.value
    audioManager.seek(value)
  },

  async toggleFavorite() {
    try {
      if (this.data.isFavorite) {
        await api.removeFavorite(this.data.song.id)
      } else {
        await api.addFavorite(this.data.song.id)
      }
      this.setData({ isFavorite: !this.data.isFavorite })
      wx.showToast({ title: this.data.isFavorite ? '已收藏' : '已取消收藏', icon: 'success' })
    } catch (e) {
      console.error(e)
    }
  },

  goToVip() {
    wx.navigateTo({ url: '/pages/vip/vip' })
  }
})
