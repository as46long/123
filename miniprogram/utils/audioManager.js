const audioManager = {
  innerAudioContext: null,
  currentSong: null,
  playList: [],
  playMode: 'sequence',
  listeners: {},

  init() {
    this.innerAudioContext = wx.createInnerAudioContext()
    this.innerAudioContext.onPlay(() => this.emit('play'))
    this.innerAudioContext.onPause(() => this.emit('pause'))
    this.innerAudioContext.onEnded(() => this.emit('ended'))
    this.innerAudioContext.onError((err) => this.emit('error', err))
    this.innerAudioContext.onTimeUpdate(() => this.emit('timeUpdate'))
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
    this.currentSong = song
    this.playList = list.length > 0 ? list : [song]
    this.innerAudioContext.src = song.audioUrl
    this.innerAudioContext.play()
  },

  pause() {
    this.innerAudioContext.pause()
  },

  resume() {
    this.innerAudioContext.play()
  },

  seek(position) {
    this.innerAudioContext.seek(position)
  },

  next() {
    const index = this.playList.findIndex(s => s.id === this.currentSong.id)
    let nextIndex
    if (this.playMode === 'random') {
      nextIndex = Math.floor(Math.random() * this.playList.length)
    } else if (this.playMode === 'single') {
      nextIndex = index
    } else {
      nextIndex = (index + 1) % this.playList.length
    }
    this.play(this.playList[nextIndex])
  },

  prev() {
    const index = this.playList.findIndex(s => s.id === this.currentSong.id)
    const prevIndex = index > 0 ? index - 1 : this.playList.length - 1
    this.play(this.playList[prevIndex])
  },

  toggleMode() {
    const modes = ['sequence', 'random', 'single']
    const index = modes.indexOf(this.playMode)
    this.playMode = modes[(index + 1) % modes.length]
    return this.playMode
  },

  getDuration() {
    return this.innerAudioContext.duration
  },

  getCurrentTime() {
    return this.innerAudioContext.currentTime
  },

  isPlaying() {
    return !this.innerAudioContext.paused
  }
}

module.exports = audioManager
