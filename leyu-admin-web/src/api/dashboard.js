import request from '@/utils/request'

export const getStats = () => request.get('/api/admin/dashboard/stats')
export const getCharts = () => request.get('/api/admin/dashboard/charts')
