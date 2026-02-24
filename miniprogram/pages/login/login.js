const api = require('../../utils/api')
const app = getApp()

Page({
  data: {
    username: '',
    password: '',
    isRegister: false,
    nickname: ''
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
      } else {
        res = await api.login({ username, password })
      }
      
      wx.setStorageSync('token', res.data.token)
      app.globalData.token = res.data.token
      app.globalData.userInfo = res.data.user
      
      wx.showToast({ title: isRegister ? '注册成功' : '登录成功', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 1500)
    } catch (e) {
      console.error(e)
    }
  },

  async handleWxLogin() {
    try {
      const { code } = await wx.login()
      const res = await api.wxLogin(code)
      
      wx.setStorageSync('token', res.data.token)
      app.globalData.token = res.data.token
      app.globalData.userInfo = res.data.user
      
      wx.showToast({ title: '登录成功', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 1500)
    } catch (e) {
      console.error(e)
    }
  }
})
