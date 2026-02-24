import request from '@/utils/request'

export const getOrderList = (params) => request.get('/order/list', { params })

export const getOrderDetail = (orderNo) => request.get(`/order/detail/${orderNo}`)
