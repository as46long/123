import request from '@/utils/request'

export const getSplashImage = () => {
  return request.get('/api/system/splashImage')
}

export const uploadSplashImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/api/system/uploadSplashImage', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
