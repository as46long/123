import request from '@/utils/request'

export const getSongList = (params) => request.get('/api/song/list', { params })

export const getSongDetail = (id) => request.get(`/api/song/detail/${id}`)

export const addSong = (data) => request.post('/api/song/add', data)

export const updateSong = (data) => request.put('/api/song/update', data)

export const deleteSong = (id) => request.delete(`/api/song/delete/${id}`)

export const getLyrics = (id) => request.get(`/api/song/lyrics/${id}`)

export const updateLyrics = (id, lyrics) => request.put(`/api/song/lyrics/${id}`, { lyrics })

export const searchLyrics = (keyword) => request.get('/api/song/lyrics/search', { params: { keyword } })

export const fetchLyrics = (songId) => request.get(`/api/song/lyrics/fetch/${songId}`)
