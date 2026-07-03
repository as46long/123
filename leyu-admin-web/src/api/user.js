import request from '@/utils/request'

export const login = (data) => request.post('/api/admin/login', data)

export const getUserList = (params) => request.get('/api/admin/user/page', { params })

export const getUserDetail = (id) => request.get(`/api/admin/user/detail/${id}`)

export const updateUserStatus = (id, status) => request.put(`/api/admin/user/status/${id}?status=${status}`)

export const deleteUser = (id) => request.delete(`/api/admin/user/delete/${id}`)
