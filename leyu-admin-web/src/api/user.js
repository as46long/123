import request from '@/utils/request'

export const login = (data) => request.post('/admin/login', data)

export const getUserList = (params) => request.get('/user/list', { params })

export const getUserDetail = (id) => request.get(`/user/detail/${id}`)

export const updateUserStatus = (id, status) => request.put(`/user/status/${id}?status=${status}`)

export const deleteUser = (id) => request.delete(`/user/delete/${id}`)
