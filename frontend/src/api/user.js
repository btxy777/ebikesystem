import request from '@/utils/request'

export const login = (data) => {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

export const register = (data) => {
  return request({
    url: '/api/auth/register',
    method: 'post',
    data
  })
}

export const getUserInfo = () => {
  return request({
    url: '/api/auth/info',
    method: 'get'
  })
}

export const getOperators = (page = 1, size = 10) => {
  return request({
    url: '/api/auth/operators',
    method: 'get',
    params: { page, size }
  })
}

export const getUserList = (page = 1, size = 10) => {
  return request({
    url: '/api/users/list',
    method: 'get',
    params: { page, size }
  })
}

export const getUserById = (id) => {
  return request({
    url: `/api/users/${id}`,
    method: 'get'
  })
}

export const addUser = (data) => {
  return request({
    url: '/api/users/add',
    method: 'post',
    data
  })
}

export const updateUser = (data) => {
  return request({
    url: '/api/users/update',
    method: 'put',
    data
  })
}

export const deleteUser = (id) => {
  return request({
    url: `/api/users/${id}`,
    method: 'delete'
  })
}
