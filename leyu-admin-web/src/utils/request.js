import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '',
  timeout: 30000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  console.log('发送请求:', config.method?.toUpperCase(), config.url, config.data)
  return config
}, error => {
  return Promise.reject(error)
})

request.interceptors.response.use(response => {
  console.log('接收响应:', response.status, response.data)
  const res = response.data
  if (res.code !== 200) {
    ElMessage.error(res.message || '请求失败')
    if (res.code === 401 || res.code === 403) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
    }
    return Promise.reject(new Error(res.message || 'Error'))
  }
  return res
}, error => {
  console.error('请求错误:', error)
  ElMessage.error(error.message || '网络错误')
  if (error.response && (error.response.status === 401 || error.response.status === 403)) {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login')
  }
  return Promise.reject(error)
})

export default request
