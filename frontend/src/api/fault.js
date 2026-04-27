import request from '@/utils/request'

export const getFaultList = (page = 1, size = 10) => {
  return request({
    url: '/api/fault-reports/list',
    method: 'get',
    params: { page, size }
  })
}

export const createFaultReport = (data) => {
  return request({
    url: '/api/fault-reports/create',
    method: 'post',
    data
  })
}

export const acceptFault = (reportId, operatorId) => {
  return request({
    url: '/api/fault-reports/accept',
    method: 'post',
    data: { reportId, operatorId }
  })
}

export const updateFaultStatus = (reportId, status, operatorId) => {
  return request({
    url: '/api/fault-reports/updateStatus',
    method: 'post',
    data: { reportId, status, operatorId }
  })
}

export const repairVehicle = (reportId, operatorId) => {
  return request({
    url: '/api/fault-reports/repair',
    method: 'post',
    data: { reportId, operatorId }
  })
}

export const getFaultListByOperator = (operatorId) => {
  return request({
    url: `/api/fault-reports/listByOperator/${operatorId}`,
    method: 'get'
  })
}
