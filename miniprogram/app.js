App({
  globalData: {
    userInfo: null,
    token: '',
    audioManager: null,
    currentSong: null,
    playList: [],
    playMode: 'sequence'
  },

  onLaunch() {
    this.initAudioManager()
    this.checkLogin()
  },

  initAudioManager() {
    const audioManager = wx.createInnerAudioContext()
    audioManager.onPlay(() => {
      console.log('开始播放')
    })
    audioManager.onEnded(() => {
      this.globalData.audioManager.emit('ended')
    })
    audioManager.onError((err) => {
      console.error('播放错误', err)
    })
    this.globalData.audioManager = audioManager
  },

  checkLogin() {
    const token = wx.getStorageSync('token')
    if (token) {
      this.globalData.token = token
      this.getUserInfo()
    }
  },

  async getUserInfo() {
    try {
      const res = await this.request({ url: '/user/info', method: 'GET' })
      this.globalData.userInfo = res.data
    } catch (e) {
      console.error(e)
    }
  },

  request(options) {
    const baseUrl = 'http://localhost:8080/api'
    return new Promise((resolve, reject) => {
      wx.request({
        url: baseUrl + options.url,
        method: options.method || 'GET',
        data: options.data,
        header: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.globalData.token}`
        },
        success: (res) => {
          if (res.data.code === 200) {
            resolve(res.data)
          } else {
            reject(res.data)
          }
        },
        fail: reject
      })
    })
  }
})
