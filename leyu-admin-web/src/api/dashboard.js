import request from '@/utils/request'

export const getStats = () => request.get('/dashboard/stats')
