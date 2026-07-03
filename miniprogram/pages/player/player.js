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
    currentLyricIndex: -1,
    showVipModal: false,
    progressPercent: 0,
    formattedCurrentTime: '00:00',
    formattedDuration: '00:00',
    isDragging: false,
    loading: false,
    hasRecordedPlay: false,
    playList: [],
    showPlayList: false,
    showCommentModal: false,
    comments: [],
    commentLoading: false,
    commentText: '',
    commentPageNum: 1,
    commentPageSize: 10,
    hasMoreComments: true,
    commentScrollTop: 0,
    // 新功能
    showLyrics: true,
    waveformBars: [],
    gestureTipVisible: false,
    gestureTipIcon: '',
    gestureTipText: '',
    playModeIcon: '↻',
    // 手势
    touchStartX: 0,
    touchStartY: 0,
    touchStartTime: 0
  },

  onLoad(options) {
    const id = options.id
    const playAll = options.playAll === 'true'

    if (!id) {
      wx.showToast({ title: '歌曲ID不存在', icon: 'none' })
      setTimeout(() => wx.navigateBack(), 1500)
      return
    }

    // 初始化波形数据
    this.initWaveformBars()

    // 先初始化事件监听
    this.initAudioEvents()

    // 检查是否已经在播放这首歌
    const currentSong = audioManager.getCurrentSong()
    if (currentSong && currentSong.id == id) {
      // 同一首歌，直接同步状态，不重新加载
      this.syncPlayState()
      this.loadLyrics(id)
    } else {
      // 不同的歌，加载新歌曲
      this.loadSong(id, playAll)
    }

    // 同步播放列表
    this.syncPlayList()
    
    // 更新播放模式图标
    this.updatePlayModeIcon()
  },

  onShow() {
    // 每次显示时同步播放列表
    this.syncPlayList()
  },

  onUnload() {
    if (this.audioPlayHandler) {
      audioManager.off('play', this.audioPlayHandler)
    }
    if (this.audioPauseHandler) {
      audioManager.off('pause', this.audioPauseHandler)
    }
    if (this.audioEndedHandler) {
      audioManager.off('ended', this.audioEndedHandler)
    }
    if (this.audioTimeUpdateHandler) {
      audioManager.off('timeUpdate', this.audioTimeUpdateHandler)
    }
    if (this.audioErrorHandler) {
      audioManager.off('error', this.audioErrorHandler)
    }
    if (this.songChangeHandler) {
      audioManager.off('songChange', this.songChangeHandler)
    }
    if (this.playListChangeHandler) {
      audioManager.off('playListChange', this.playListChangeHandler)
    }
  },

  initAudioEvents() {
    this.audioPlayHandler = () => {
      this.setData({ isPlaying: true })
      
      // 首次播放时记录播放次数
      if (!this.data.hasRecordedPlay && this.data.song) {
        this.recordPlayCount(this.data.song.id)
      }
      
      // 启动波形动画
      this.startWaveformAnimation()
    }
    this.audioPauseHandler = () => {
      this.setData({ isPlaying: false })
      // 停止波形动画
      this.stopWaveformAnimation()
    }
    this.audioEndedHandler = () => {
      this.setData({ hasRecordedPlay: false })
    }
    this.audioTimeUpdateHandler = () => {
      // 如果正在拖动，不更新时间
      if (this.data.isDragging) return
      
      const currentTime = audioManager.getCurrentTime()
      const duration = audioManager.getDuration()
      const progressPercent = duration > 0 ? (currentTime / duration) * 100 : 0
      
      this.setData({
        currentTime,
        duration,
        progressPercent,
        formattedCurrentTime: this.formatTime(currentTime),
        formattedDuration: this.formatTime(duration)
      })
      this.updateLyric(currentTime)
    }
    this.audioErrorHandler = (err) => {
      console.error('播放错误:', err)
      wx.showToast({ title: '播放失败', icon: 'none' })
    }
    this.songChangeHandler = (song) => {
      // 歌曲变化时更新页面数据
      if (song) {
        this.setData({ 
          song,
          isFavorite: song.isFavorite,
          hasRecordedPlay: false,
          currentLyricIndex: -1
        })
        this.loadLyrics(song.id)
        this.syncPlayList()
      }
    }
    this.playListChangeHandler = (playList) => {
      this.setData({ playList })
    }
    
    audioManager.on('play', this.audioPlayHandler)
    audioManager.on('pause', this.audioPauseHandler)
    audioManager.on('ended', this.audioEndedHandler)
    audioManager.on('timeUpdate', this.audioTimeUpdateHandler)
    audioManager.on('error', this.audioErrorHandler)
    audioManager.on('songChange', this.songChangeHandler)
    audioManager.on('playListChange', this.playListChangeHandler)
  },

  // 同步当前播放状态
  syncPlayState() {
    const song = audioManager.getCurrentSong()
    const isPlaying = audioManager.isPlaying()
    const currentTime = audioManager.getCurrentTime()
    const duration = audioManager.getDuration()
    
    this.setData({
      song,
      isPlaying,
      currentTime,
      duration,
      formattedCurrentTime: this.formatTime(currentTime),
      formattedDuration: this.formatTime(duration),
      progressPercent: duration > 0 ? (currentTime / duration) * 100 : 0,
      playMode: audioManager.playMode || 'sequence'
    })
  },

  // 同步播放列表
  syncPlayList() {
    const playList = audioManager.getPlayList() || []
    this.setData({ playList })
  },

  formatTime(seconds) {
    if (!seconds || isNaN(seconds)) return '00:00'
    const mins = Math.floor(seconds / 60)
    const secs = Math.floor(seconds % 60)
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  },

  goBack() {
    wx.navigateBack()
  },

  async loadSong(id, playAll = false) {
    if (this.data.loading) return
    
    this.setData({ loading: true, hasRecordedPlay: false })
    wx.showLoading({ title: '加载中...' })
    
    try {
      const res = await api.getSongDetail(id)
      const song = res.data
      
      if (!song) {
        throw new Error('歌曲不存在')
      }
      
      if (song.isVip && !app.globalData.userInfo?.isVip) {
        wx.hideLoading()
        this.setData({ loading: false, showVipModal: true })
        return
      }

      this.setData({ 
        song, 
        isFavorite: song.isFavorite,
        formattedCurrentTime: '00:00',
        formattedDuration: '00:00',
        progressPercent: 0,
        currentTime: 0,
        duration: 0,
        currentLyricIndex: -1
      })
      
      // 播放歌曲
      if (song.audioUrl) {
        // 如果是播放全部模式，获取完整的播放列表
        let playList = []
        if (playAll) {
          // 尝试从全局数据获取推荐列表
          playList = app.globalData.playAllList || []
        }
        this.playSong(playList)
        this.loadLyrics(id)
      } else {
        wx.showToast({ title: '音频地址无效', icon: 'none' })
      }
      
      wx.hideLoading()
      this.setData({ loading: false })
    } catch (e) {
      wx.hideLoading()
      this.setData({ loading: false })
      console.error('加载歌曲失败:', e)
      wx.showToast({ title: '加载失败', icon: 'none' })
      setTimeout(() => wx.navigateBack(), 1500)
    }
  },

  playSong(playList = []) {
    if (this.data.song && this.data.song.audioUrl) {
      console.log('开始播放:', this.data.song.audioUrl, '播放列表长度:', playList.length)
      audioManager.play(this.data.song, playList)
      this.syncPlayList()
    }
  },

  // 记录播放次数
  async recordPlayCount(songId) {
    try {
      await api.playSong(songId)
      this.setData({ hasRecordedPlay: true })
      console.log('播放次数已记录')
    } catch (e) {
      console.error('记录播放次数失败:', e)
    }
  },

  async loadLyrics(id) {
    try {
      const res = await api.getLyrics(id)
      const lyrics = this.parseLyrics(res.data || '')
      this.setData({ lyrics, currentLyricIndex: -1 })
    } catch (e) {
      console.error('加载歌词失败:', e)
      this.setData({ lyrics: [], currentLyricIndex: -1 })
    }
  },

  parseLyrics(text) {
    if (!text) return []
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

  updateLyric(currentTime) {
    const { lyrics, currentLyricIndex } = this.data
    if (!lyrics || lyrics.length === 0) return
    
    // 找到当前应该显示的歌词索引
    let newIndex = -1
    for (let i = 0; i < lyrics.length; i++) {
      if (currentTime >= lyrics[i].time) {
        newIndex = i
      } else {
        break
      }
    }
    
    // 只有当索引变化时才更新
    if (newIndex !== -1 && newIndex !== currentLyricIndex) {
      this.setData({ currentLyricIndex: newIndex })
    }
  },

  // 滑动进度条 - 拖动中
  onSliderChanging(e) {
    const value = e.detail.value
    const duration = this.data.duration || 100
    
    this.setData({
      isDragging: true,
      currentTime: value,
      formattedCurrentTime: this.formatTime(value),
      progressPercent: (value / duration) * 100
    })
  },

  // 滑动进度条 - 拖动结束
  onSliderChange(e) {
    const value = e.detail.value
    
    this.setData({
      isDragging: false,
      currentTime: value,
      currentLyricIndex: -1  // 重置歌词索引
    })
    
    audioManager.seek(value)
  },

  togglePlay() {
    if (this.data.isPlaying) {
      audioManager.pause()
    } else {
      audioManager.resume()
    }
  },

  playPrev() {
    this.setData({ hasRecordedPlay: false })
    audioManager.prev()
  },

  playNext() {
    this.setData({ hasRecordedPlay: false })
    audioManager.next()
  },

  toggleMode() {
    const mode = audioManager.toggleMode()
    this.setData({ playMode: mode })
    this.updatePlayModeIcon()
    const modeText = mode === 'sequence' ? '顺序播放' : mode === 'random' ? '随机播放' : '单曲循环'
    wx.showToast({ title: modeText, icon: 'none' })
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
  },

  closeVipModal() {
    this.setData({ showVipModal: false })
    wx.navigateBack()
  },

  // 切换播放列表显示
  togglePlayList() {
    this.setData({ showPlayList: !this.data.showPlayList })
  },

  // 关闭播放列表
  closePlayList() {
    this.setData({ showPlayList: false })
  },

  // 从播放列表选择歌曲
  onPlayListItemTap(e) {
    const song = e.currentTarget.dataset.song
    if (song && song.id !== this.data.song?.id) {
      this.setData({ 
        hasRecordedPlay: false,
        showPlayList: false
      })
      audioManager.play(song, this.data.playList)
    }
  },

  // 从播放列表移除歌曲
  onRemoveSong(e) {
    const songId = e.currentTarget.dataset.id
    const song = this.data.playList.find(s => s.id === songId)
    
    wx.showModal({
      title: '提示',
      content: `确定要从播放列表移除「${song?.title || '这首歌曲'}」吗？`,
      success: (res) => {
        if (res.confirm) {
          audioManager.removeFromPlaylist(songId)
          wx.showToast({ title: '已移除', icon: 'success' })
        }
      }
    })
  },

  // 清空播放列表
  onClearPlaylist() {
    if (this.data.playList.length === 0) return
    
    wx.showModal({
      title: '提示',
      content: '确定要清空播放列表吗？',
      success: (res) => {
        if (res.confirm) {
          audioManager.clearPlaylist()
          this.setData({ showPlayList: false })
          wx.showToast({ title: '已清空', icon: 'success' })
          // 返回上一页
          wx.navigateBack()
        }
      }
    })
  },

  // 显示评论弹窗
  showComments() {
    if (!app.globalData.token) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      setTimeout(() => {
        wx.navigateTo({ url: '/pages/login/login' })
      }, 1500)
      return
    }
    this.setData({
      showCommentModal: true,
      commentPageNum: 1,
      comments: []
    })
    this.loadComments()
  },

  // 隐藏评论弹窗
  hideComments() {
    this.setData({ showCommentModal: false })
  },

  // 加载评论
  async loadComments() {
    if (this.data.commentLoading || !this.data.hasMoreComments) return
    
    this.setData({ commentLoading: true })
    try {
      const res = await api.getSongComments(this.data.song.id, {
        pageNum: this.data.commentPageNum,
        pageSize: this.data.commentPageSize
      })
      const newComments = res.data?.records || []
      this.setData({
        comments: this.data.commentPageNum === 1 ? newComments : [...this.data.comments, ...newComments],
        hasMoreComments: newComments.length >= this.data.commentPageSize,
        commentLoading: false
      })
    } catch (e) {
      console.error('加载评论失败', e)
      this.setData({ commentLoading: false })
    }
  },

  // 评论输入
  onCommentInput(e) {
    this.setData({ commentText: e.detail.value })
  },

  // 发送评论
  async sendComment() {
    const content = this.data.commentText.trim()
    if (!content) {
      wx.showToast({ title: '请输入评论内容', icon: 'none' })
      return
    }

    try {
      wx.showLoading({ title: '发布中...' })

      await api.postSongComment({
        songId: this.data.song.id,
        content: content
      })

      wx.hideLoading()
      wx.showToast({ title: '发布成功', icon: 'success' })

      // 重新加载评论
      this.setData({
        commentText: '',
        commentPageNum: 1,
        comments: [],
        hasMoreComments: true
      })
      this.loadComments()
    } catch (e) {
      wx.hideLoading()
      console.error('发布评论失败', e)
      wx.showToast({
        title: e.message || '发布失败，请重试',
        icon: 'none'
      })
    }
  },

  // 点赞评论
  async likeSongComment(e) {
    const id = e.currentTarget.dataset.id
    try {
      await api.likeSongComment(id)
      const comments = this.data.comments.map(comment => {
        if (comment.id === id) {
          return {
            ...comment,
            likes: comment.likes + 1,
            isLiked: true
          }
        }
        return comment
      })
      this.setData({ comments })
    } catch (e) {
      console.error('点赞失败', e)
    }
  },

  // 评论滚动到底部加载更多
  onCommentScrollToLower() {
    if (this.data.hasMoreComments && !this.data.commentLoading) {
      this.setData({ commentPageNum: this.data.commentPageNum + 1 })
      this.loadComments()
    }
  },

  // ==========================================
  // 新功能方法
  // ==========================================

  // 初始化波形数据
  initWaveformBars() {
    const bars = []
    const count = 24
    for (let i = 0; i < count; i++) {
      bars.push(8 + Math.random() * 20)
    }
    this.setData({ waveformBars: bars })
  },

  // 启动波形动画
  startWaveformAnimation() {
    if (this.waveformTimer) return
    this.waveformTimer = setInterval(() => {
      const bars = this.data.waveformBars.map(() => {
        return 8 + Math.random() * 44
      })
      this.setData({ waveformBars: bars })
    }, 150)
  },

  // 停止波形动画
  stopWaveformAnimation() {
    if (this.waveformTimer) {
      clearInterval(this.waveformTimer)
      this.waveformTimer = null
    }
    // 恢复为低高度
    const bars = this.data.waveformBars.map(() => 8 + Math.random() * 12)
    this.setData({ waveformBars: bars })
  },

  // 切换歌词显示
  toggleLyrics() {
    this.setData({ showLyrics: !this.data.showLyrics })
  },

  // 更新播放模式图标
  updatePlayModeIcon() {
    const icons = {
      sequence: '🔁',
      random: '🔀',
      single: '🔂'
    }
    this.setData({ playModeIcon: icons[this.data.playMode] || '🔁' })
  },

  // 手势处理
  onTouchStart(e) {
    this.setData({
      touchStartX: e.touches[0].clientX,
      touchStartY: e.touches[0].clientY,
      touchStartTime: Date.now()
    })
  },

  onTouchMove(e) {
    // 可以在这里添加拖动时的视觉反馈
  },

  onTouchEnd(e) {
    const endX = e.changedTouches[0].clientX
    const endY = e.changedTouches[0].clientY
    const startTime = this.data.touchStartTime
    
    const deltaX = endX - this.data.touchStartX
    const deltaY = endY - this.data.touchStartY
    const deltaTime = Date.now() - startTime
    
    // 只处理水平滑动，忽略垂直滑动（可能是滚动）
    if (Math.abs(deltaX) < Math.abs(deltaY)) return
    
    // 滑动距离需要超过 80px
    if (Math.abs(deltaX) < 80) return
    
    // 滑动时间需要在 100ms - 500ms 之间（防止误触）
    if (deltaTime < 100 || deltaTime > 500) return
    
    if (deltaX > 0) {
      // 向右滑动 - 上一首
      this.showGestureTip('⏮', '上一首')
      this.playPrev()
    } else {
      // 向左滑动 - 下一首
      this.showGestureTip('⏭', '下一首')
      this.playNext()
    }
  },

  // 显示手势提示
  showGestureTip(icon, text) {
    this.setData({
      gestureTipVisible: true,
      gestureTipIcon: icon,
      gestureTipText: text
    })
    
    if (this.gestureTipTimer) {
      clearTimeout(this.gestureTipTimer)
    }
    
    this.gestureTipTimer = setTimeout(() => {
      this.setData({ gestureTipVisible: false })
    }, 600)
  },

  // 分享歌曲
  shareSong() {
    wx.showShareMenu({
      withShareTicket: true,
      menus: ['shareAppMessage', 'shareTimeline']
    })
    
    wx.showToast({ title: '点击右上角分享', icon: 'none' })
  },

  onShareAppMessage() {
    const song = this.data.song
    return {
      title: song ? `我在听 ${song.title} - ${song.artist}` : '分享一首好歌',
      path: `/pages/player/player?id=${song?.id}`,
      imageUrl: song?.coverUrl || ''
    }
  },

  onShareTimeline() {
    const song = this.data.song
    return {
      title: song ? `${song.title} - ${song.artist}` : '乐语匣子音乐',
      query: `id=${song?.id}`,
      imageUrl: song?.coverUrl || ''
    }
  }
})
