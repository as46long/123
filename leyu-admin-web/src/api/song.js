import request from '@/utils/request'

export const getSongList = (params) => request.get('/song/list', { params })

export const getSongDetail = (id) => request.get(`/song/detail/${id}`)

export const addSong = (data) => request.post('/song/add', data)

export const updateSong = (data) => request.put('/song/update', data)

export const deleteSong = (id) => request.delete(`/song/delete/${id}`)
