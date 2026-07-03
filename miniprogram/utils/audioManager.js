const audioManager = {
  innerAudioContext: null,
  currentSong: null,
  playList: [],
  playMode: 'sequence',
  listeners: {},
  initialized: false,

  init() {
    if (this.initialized) return
    
    this.innerAudioContext = wx.createInnerAudioContext()
    this.innerAudioContext.obeyMuteSwitch = false
    
    this.innerAudioContext.onPlay(() => {
      console.log('音频开始播放')
      this.emit('play')
    })
    
    this.innerAudioContext.onPause(() => {
      console.log('音频暂停')
      this.emit('pause')
    })
    
    this.innerAudioContext.onEnded(() => {
      // 自动播放下一首
      this.next()
      console.log('音频播放结束')
      this.emit('ended')
    })
    
    this.innerAudioContext.onError((err) => {
      console.error('音频播放错误:', err)
      this.emit('error', err)
    })
    
    this.innerAudioContext.onTimeUpdate(() => {
      this.emit('timeUpdate')
    })
    
    this.innerAudioContext.onWaiting(() => {
      console.log('音频加载中...')
    })
    
    this.innerAudioContext.onCanplay(() => {
      console.log('音频可以播放')
      this.emit('canplay')
    })
    
    this.initialized = true
  },

  on(event, callback) {
    if (!this.listeners[event]) this.listeners[event] = []
    this.listeners[event].push(callback)
  },

  off(event, callback) {
    if (this.listeners[event]) {
      this.listeners[event] = this.listeners[event].filter(cb => cb !== callback)
    }
  },

  emit(event, data) {
    if (this.listeners[event]) {
      this.listeners[event].forEach(cb => cb(data))
    }
  },

  play(song, list = []) {
    if (!song || !song.audioUrl) {
      console.error('歌曲信息或音频URL不存在')
      wx.showToast({ title: '音频地址无效', icon: 'none' })
      return
    }

    this.init()
    this.currentSong = song
    this.playList = list.length > 0 ? list : [song]
    
    console.log('播放音频:', song.audioUrl)
    
    this.innerAudioContext.stop()
    this.innerAudioContext.src = song.audioUrl
   
    try {
      const playResult = this.innerAudioContext.play()
      if (playResult && playResult.catch) {
        playResult.catch(err => {
          console.error('播放失败:', err)
          wx.showToast({ title: '播放失败', icon: 'none' })
        })
      }
    } catch (err) {
      console.error('播放失败:', err)
      wx.showToast({ title: '播放失败', icon: 'none' })
    }
    
    // 通知歌曲变化
    this.emit('songChange', song)
    this.emit('playListChange', this.playList)
  },

  pause() {
    if (this.innerAudioContext) {
      this.innerAudioContext.pause()
    }
  },

  resume() {
    if (this.innerAudioContext) {
      this.innerAudioContext.play()
    }
  },

  seek(position) {
    if (this.innerAudioContext) {
      this.innerAudioContext.seek(position)
    }
  },

  stop() {
    if (this.innerAudioContext) {
      this.innerAudioContext.stop()
      this.currentSong = null
      this.playList = []
      this.emit('songChange', null)
      this.emit('playListChange', this.playList)
    }
  },

  next() {
    if (this.playList.length === 0) return
    const index = this.playList.findIndex(s => s.id === this.currentSong.id)
    let nextIndex
    if (this.playMode === 'random') {
      nextIndex = Math.floor(Math.random() * this.playList.length)
    } else if (this.playMode === 'single') {
      nextIndex = index
    } else {
      nextIndex = (index + 1) % this.playList.length
    }
    this.play(this.playList[nextIndex], this.playList)
  },

  prev() {
    if (this.playList.length === 0) return
    const index = this.playList.findIndex(s => s.id === this.currentSong.id)
    const prevIndex = index > 0 ? index - 1 : this.playList.length - 1
    this.play(this.playList[prevIndex], this.playList)
  },

  toggleMode() {
    const modes = ['sequence', 'random', 'single']
    const index = modes.indexOf(this.playMode)
    this.playMode = modes[(index + 1) % modes.length]
    return this.playMode
  },

  // 从播放列表中移除歌曲
  removeFromPlaylist(songId) {
    const index = this.playList.findIndex(s => s.id === songId)
    if (index === -1) return false
    
    // 如果移除的是当前播放的歌曲
    if (this.currentSong && this.currentSong.id === songId) {
      // 如果列表只剩一首歌，停止播放
      if (this.playList.length === 1) {
        this.stop()
        return true
      }
      // 先移除歌曲
      this.playList.splice(index, 1)
      // 播放下一首（或第一首）
      const nextIndex = Math.min(index, this.playList.length - 1)
      this.play(this.playList[nextIndex], this.playList)
    } else {
      // 移除非当前播放的歌曲
      this.playList.splice(index, 1)
      this.emit('playListChange', this.playList)
    }
    
    return true
  },

  // 清空播放列表
  clearPlaylist() {
    this.stop()
    this.playList = []
    this.emit('playListChange', this.playList)
  },

  getDuration() {
    return this.innerAudioContext ? this.innerAudioContext.duration : 0
  },

  getCurrentTime() {
    return this.innerAudioContext ? this.innerAudioContext.currentTime : 0
  },

  isPlaying() {
    return this.innerAudioContext && !this.innerAudioContext.paused
  },

  getCurrentSong() {
    return this.currentSong
  },

  getPlayList() {
    return this.playList
  }
}

module.exports = audioManager
