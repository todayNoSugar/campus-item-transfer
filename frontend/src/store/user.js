import { defineStore } from 'pinia'
import { login as apiLogin, register as apiRegister, getInfo } from '../api'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    userInfo: null
  }),
  getters: {
    isLogin: (state) => !!state.token,
    isAdmin: (state) => state.userInfo?.role === 1
  },
  actions: {
    async login(account, password) {
      const res = await apiLogin({ account, password })
      this.token = res.data.token
      this.userInfo = res.data.user
    },
    async register(data) {
      return apiRegister(data)
    },
    /** 刷新当前用户信息（个人资料修改后调用） */
    async refreshInfo() {
      const res = await getInfo()
      this.userInfo = res.data
    },
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})
