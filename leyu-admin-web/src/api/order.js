import request from '@/utils/request'

export const getOrderList = (params) => request.get('/api/admin/order/list', { params })

export const getOrderDetail = (orderNo) => request.get(`/api/admin/order/detail/${orderNo}`)
