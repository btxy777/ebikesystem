import request from '@/utils/request'

export const getVehicleList = (page = 1, size = 10) => {
  return request({
    url: '/api/vehicles/list',
    method: 'get',
    params: { page, size }
  })
}

export const getAvailableVehicles = () => {
  return request({
    url: '/api/vehicles/available',
    method: 'get'
  })
}

export const getVehicleDetail = (id) => {
  return request({
    url: '/api/vehicles/detail',
    method: 'get',
    params: { id }
  })
}

export const addVehicle = (data) => {
  return request({
    url: '/api/vehicles/add',
    method: 'post',
    data
  })
}

export const updateVehicle = (data) => {
  return request({
    url: '/api/vehicles/update',
    method: 'put',
    data
  })
}
