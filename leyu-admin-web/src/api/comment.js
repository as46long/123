import request from '@/utils/request'

export function getCommentList(params) {
  return request({
    url: '/api/admin/comment/list',
    method: 'get',
    params
  })
}

export function auditComment(id, status) {
  return request({
    url: `/api/admin/comment/audit/${id}`,
    method: 'put',
    params: { status }
  })
}

export function deleteComment(id) {
  return request({
    url: `/api/admin/comment/delete/${id}`,
    method: 'delete'
  })
}
