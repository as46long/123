const api = require('../../utils/api.js')
const app = getApp()

Page({
  data: {
    userInfo: {
      nickname: '',
      avatar: '',
      phone: '',
      email: ''
    },
    tempAvatarPath: ''
  },

  onLoad() {
    this.loadUserInfo()
  },

  loadUserInfo() {
    const userInfo = app.globalData.userInfo
    if (userInfo) {
      this.setData({
        userInfo: {
          nickname: userInfo.nickname || '',
          avatar: userInfo.avatar || '',
          phone: userInfo.phone || '',
          email: userInfo.email || ''
        }
      })
    }
  },

  onInputChange(e) {
    const field = e.currentTarget.dataset.field
    const value = e.detail.value
    this.setData({
      [`userInfo.${field}`]: value
    })
  },

  onChooseAvatar() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFilePath = res.tempFiles[0].tempFilePath
        this.setData({
          tempAvatarPath: tempFilePath
        })
      }
    })
  },

  uploadFile(filePath) {
    return new Promise((resolve, reject) => {
      const token = wx.getStorageSync('token') || ''
      wx.uploadFile({
        url: 'http://localhost:8080/api/file/uploadAvatar',
        filePath: filePath,
        name: 'file',
        header: {
          'Authorization': `Bearer ${token}`
        },
        success: (res) => {
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            resolve(data.data)
          } else {
            reject(new Error(data.message || '上传失败'))
          }
        },
        fail: (err) => {
          reject(err)
        }
      })
    })
  },

  async onSave() {
    const { userInfo, tempAvatarPath } = this.data
    
    if (!userInfo.nickname.trim()) {
      wx.showToast({ title: '昵称不能为空', icon: 'none' })
      return
    }

    wx.showLoading({ title: '保存中...' })

    try {
      let updateData = { ...userInfo }
      
      // 如果有新头像，先上传
      if (tempAvatarPath) {
        const avatarUrl = await this.uploadFile(tempAvatarPath)
        updateData.avatar = avatarUrl
      }

      // 更新用户信息
      await api.updateUserInfo(updateData)
      
      // 更新全局数据
      const updatedUser = { ...app.globalData.userInfo, ...updateData }
      app.globalData.userInfo = updatedUser
      
      wx.hideLoading()
      wx.showToast({ title: '保存成功', icon: 'success' })
      
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    } catch (error) {
      wx.hideLoading()
      wx.showToast({ title: error.message || '保存失败', icon: 'none' })
    }
  }
})
