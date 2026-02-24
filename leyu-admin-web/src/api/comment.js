import request from '@/utils/request'

export const getCommentList = (params) => request.get('/comment/list', { params })

export const auditComment = (id, status) => request.put(`/comment/audit/${id}?status=${status}`)

export const deleteComment = (id) => request.delete(`/comment/delete/${id}`)
