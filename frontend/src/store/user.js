import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')
  const userId = ref(localStorage.getItem('userId') || '')
  const role = ref(localStorage.getItem('role') || '')
  const roleName = ref(localStorage.getItem('roleName') || '')

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (userInfo) => {
    username.value = userInfo.username || userInfo.nickname || ''
    userId.value = userInfo.id || ''
    role.value = userInfo.role || ''
    roleName.value = userInfo.roleName || ''
    localStorage.setItem('username', username.value)
    localStorage.setItem('userId', userId.value)
    localStorage.setItem('role', role.value)
    localStorage.setItem('roleName', roleName.value)
  }

  const logout = () => {
    token.value = ''
    username.value = ''
    userId.value = ''
    role.value = ''
    roleName.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('userId')
    localStorage.removeItem('role')
    localStorage.removeItem('roleName')
  }

  return {
    token,
    username,
    userId,
    role,
    roleName,
    setToken,
    setUserInfo,
    logout
  }
})
