const audioManager = require('../../utils/audioManager')

Component({
  properties: {
    // 是否显示控制栏
    show: {
      type: Boolean,
      value: false
    }
  },

  data: {
    song: null,
    isPlaying: false,
    currentTime: 0,
    duration: 0,
    progressPercent: 0
  },

  lifetimes: {
    attached() {
      this.initAudioEvents()
      this.updateCurrentSong()
    },

    detached() {
      if (this.audioPlayHandler) {
        audioManager.off('play', this.audioPlayHandler)
      }
      if (this.audioPauseHandler) {
        audioManager.off('pause', this.audioPauseHandler)
      }
      if (this.audioTimeUpdateHandler) {
        audioManager.off('timeUpdate', this.audioTimeUpdateHandler)
      }
      if (this.songChangeHandler) {
        audioManager.off('songChange', this.songChangeHandler)
      }
    }
  },

  methods: {
    initAudioEvents() {
      this.audioPlayHandler = () => {
        this.setData({ isPlaying: true })
      }
      
      this.audioPauseHandler = () => {
        this.setData({ isPlaying: false })
      }
      
      this.audioTimeUpdateHandler = () => {
        const currentTime = audioManager.getCurrentTime()
        const duration = audioManager.getDuration()
        const progressPercent = duration > 0 ? (currentTime / duration) * 100 : 0
        
        this.setData({
          currentTime,
          duration,
          progressPercent
        })
      }
      
      this.songChangeHandler = (song) => {
        if (song) {
          this.setData({ 
            song, 
            isPlaying: true,
            currentTime: 0,
            duration: 0,
            progressPercent: 0
          })
        } else {
          this.setData({ 
            song: null,
            isPlaying: false,
            currentTime: 0,
            duration: 0,
            progressPercent: 0
          })
        }
      }
      
      audioManager.on('play', this.audioPlayHandler)
      audioManager.on('pause', this.audioPauseHandler)
      audioManager.on('timeUpdate', this.audioTimeUpdateHandler)
      audioManager.on('songChange', this.songChangeHandler)
    },

    updateCurrentSong() {
      const song = audioManager.getCurrentSong()
      const isPlaying = audioManager.isPlaying()
      const currentTime = audioManager.getCurrentTime()
      const duration = audioManager.getDuration()
      const progressPercent = duration > 0 ? (currentTime / duration) * 100 : 0
      
      this.setData({
        song,
        isPlaying,
        currentTime,
        duration,
        progressPercent
      })
    },

    // 播放/暂停
    togglePlay() {
      if (this.data.isPlaying) {
        audioManager.pause()
      } else {
        audioManager.resume()
      }
    },

    // 下一首
    playNext() {
      audioManager.next()
    },

    // 进入播放页面
    goToPlayer() {
      if (this.data.song) {
        wx.navigateTo({
          url: `/pages/player/player?id=${this.data.song.id}`
        })
      }
    }
  }
})
