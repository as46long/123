import request from '@/utils/request'

export function getSongCommentList(params) {
  console.log('获取歌曲评论列表，参数:', params)
  return request({
    url: '/api/admin/songComment/list',
    method: 'get',
    params
  })
}

export function banSongComment(id) {
  console.log('封禁歌曲评论:', id)
  return request({
    url: `/api/admin/songComment/ban/${id}`,
    method: 'put'
  })
}

export function unbanSongComment(id) {
  console.log('解封歌曲评论:', id)
  return request({
    url: `/api/admin/songComment/unban/${id}`,
    method: 'put'
  })
}

export function deleteSongComment(id) {
  console.log('删除歌曲评论:', id)
  return request({
    url: `/api/admin/songComment/delete/${id}`,
    method: 'delete'
  })
}

export function getCategories() {
  console.log('获取歌曲分类列表')
  return request({
    url: '/api/admin/songComment/categories',
    method: 'get'
  })
}
