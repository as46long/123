const app = getApp()

Component({
  properties: {},

  data: {
    song: null,
    isPlaying: false,
    show: false
  },

  lifetimes: {
    attached() {
      this.updateState()
      app.globalData.audioManager.onPlay(() => this.updateState())
      app.globalData.audioManager.onPause(() => this.updateState())
    }
  },

  methods: {
    updateState() {
      this.setData({
        song: app.globalData.currentSong,
        isPlaying: app.globalData.audioManager && !app.globalData.audioManager.paused,
        show: !!app.globalData.currentSong
      })
    },

    onTap() {
      if (this.data.song) {
        wx.navigateTo({ url: `/pages/player/player?id=${this.data.song.id}` })
      }
    },

    togglePlay(e) {
      e.stopPropagation()
      const audioManager = app.globalData.audioManager
      if (this.data.isPlaying) {
        audioManager.pause()
      } else {
        audioManager.play()
      }
    }
  }
})
