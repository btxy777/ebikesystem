import request from '@/utils/request'

export const getStationList = (page = 1, size = 10) => {
  return request({
    url: '/api/stations/list',
    method: 'get',
    params: { page, size }
  })
}

export const getStationById = (id) => {
  return request({
    url: `/api/stations/${id}`,
    method: 'get'
  })
}

export const addStation = (data) => {
  return request({
    url: '/api/stations/add',
    method: 'post',
    data
  })
}

export const updateStation = (data) => {
  return request({
    url: '/api/stations/update',
    method: 'put',
    data
  })
}

export const deleteStation = (id) => {
  return request({
    url: `/api/stations/${id}`,
    method: 'delete'
  })
}
