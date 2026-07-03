const api = require('../../utils/api')
const app = getApp()

Page({
  data: {
    username: '',
    password: '',
    isRegister: false,
    nickname: '',
    showWxInfoModal: false,
    avatarUrl: '',
    wxNickname: '',
    loginCode: ''
  },

  onUsernameInput(e) {
    this.setData({ username: e.detail.value })
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value })
  },

  onNicknameInput(e) {
    this.setData({ nickname: e.detail.value })
  },

  toggleMode() {
    this.setData({ isRegister: !this.data.isRegister })
  },

  handleWxLogin() {
    wx.showLoading({ title: '登录中...' })
    
    wx.login({
      success: async (loginRes) => {
        if (!loginRes.code) {
          wx.hideLoading()
          wx.showToast({ title: '登录失败，请重试', icon: 'none' })
          return
        }

        try {
          const apiResult = await api.wxLogin({ code: loginRes.code })
          
          wx.hideLoading()
          
          // 保存登录信息
          wx.setStorageSync('token', apiResult.data.token)
          app.globalData.token = apiResult.data.token
          app.globalData.userInfo = apiResult.data.user

          // 如果是新用户（默认昵称），引导完善信息
          if (apiResult.data.user.nickname === '微信用户') {
            this.setData({ showWxInfoModal: true, loginCode: loginRes.code })
          } else {
            wx.showToast({ title: '登录成功', icon: 'success' })
            setTimeout(() => {
              wx.navigateBack()
            }, 1500)
          }
        } catch (e) {
          wx.hideLoading()
          console.error('登录异常:', e)
          wx.showToast({ title: e.message || '登录失败', icon: 'none' })
        }
      },
      fail: () => {
        wx.hideLoading()
        wx.showToast({ title: '获取登录凭证失败', icon: 'none' })
      }
    })
  },

  hideWxInfoModal() {
    this.setData({ showWxInfoModal: false })
  },

  onChooseAvatar(e) {
    const avatarUrl = e.detail.avatarUrl
    this.setData({ avatarUrl })
    console.log('选择的头像:', avatarUrl)
  },

  onWxNicknameInput(e) {
    this.setData({ wxNickname: e.detail.value })
  },

  async confirmWxLogin() {
    const { avatarUrl, wxNickname } = this.data

    if (!wxNickname) {
      wx.showToast({ title: '请输入昵称', icon: 'none' })
      return
    }

    wx.showLoading({ title: '登录中...' })

    // 重新获取 code（code 只能用一次）
    wx.login({
      success: async (loginRes) => {
        if (!loginRes.code) {
          wx.hideLoading()
          wx.showToast({ title: '获取登录凭证失败', icon: 'none' })
          return
        }

        try {
          const apiResult = await api.wxLoginWithInfo({
            code: loginRes.code,
            nickname: wxNickname,
            avatar: avatarUrl
          })

          wx.setStorageSync('token', apiResult.data.token)
          app.globalData.token = apiResult.data.token
          app.globalData.userInfo = apiResult.data.user

          wx.hideLoading()
          this.setData({ showWxInfoModal: false })
          wx.showToast({ title: '登录成功', icon: 'success' })

          setTimeout(() => {
            wx.navigateBack()
          }, 1500)

        } catch (e) {
          wx.hideLoading()
          console.error('登录异常:', e)
          wx.showToast({ title: e.message || '登录失败', icon: 'none' })
        }
      },
      fail: () => {
        wx.hideLoading()
        wx.showToast({ title: '获取登录凭证失败', icon: 'none' })
      }
    })
  },

  async handleSubmit() {
    const { username, password, nickname, isRegister } = this.data
    if (!username || !password) {
      wx.showToast({ title: '请填写完整信息', icon: 'none' })
      return
    }

    try {
      let res
      if (isRegister) {
        res = await api.register({ username, password, nickname })
        // 注册成功后直接跳转到登录
        wx.showToast({ title: '注册成功，请登录', icon: 'success' })
        setTimeout(() => {
          this.setData({ isRegister: false })
        }, 1500)
      } else {
        res = await api.login({ username, password })
        wx.setStorageSync('token', res.data.token)
        app.globalData.token = res.data.token
        app.globalData.userInfo = res.data.user
        
        wx.showToast({ title: '登录成功', icon: 'success' })
        setTimeout(() => wx.navigateBack(), 1500)
      }
    } catch (e) {
      console.error(e)
    }
  }
})
