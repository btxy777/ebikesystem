import request from '@/utils/request'

export const getOrderAll = () => {
  return request({
    url: '/api/orders/all',
    method: 'get'
  })
}

export const getOrderList = (params = {}) => {
  return request({
    url: '/api/orders/list',
    method: 'get',
    params
  })
}

export const getMyOrders = () => {
  return request({
    url: '/api/orders/my',
    method: 'get'
  })
}

export const startRental = (data) => {
  return request({
    url: '/api/orders/start',
    method: 'post',
    data
  })
}

export const endRental = (data) => {
  return request({
    url: '/api/orders/end',
    method: 'post',
    data
  })
}

export const getOrderDetail = (orderId) => {
  return request({
    url: '/api/orders/detail',
    method: 'get',
    params: { orderId }
  })
}

export const updateOrder = (data) => {
  return request({
    url: '/api/orders/update',
    method: 'put',
    data
  })
}
