import request from '@/utils/request'

export const uploadFile = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/api/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const uploadAudio = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/api/file/uploadAudio', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const uploadCover = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/api/file/uploadCover', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
