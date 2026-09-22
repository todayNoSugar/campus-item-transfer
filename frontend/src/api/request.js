import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'
import { useUserStore } from '../store/user'

// axios 实例：统一 baseURL，自动携带 token，统一处理业务码
const request = axios.create({
  baseURL: '/api',
  timeout: 20000
})

request.interceptors.request.use(
  (config) => {
    const store = useUserStore()
    if (store.token) {
      config.headers['token'] = store.token
    }
    return config
  },
  (error) => Promise.reject(error)
)

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res
    }
    if (res.code === 401) {
      const store = useUserStore()
      store.logout()
      if (router.currentRoute.value.path !== '/login') {
        ElMessage.error(res.msg || '未登录或登录已过期')
        router.push('/login')
      }
      return Promise.reject(new Error(res.msg || '未登录'))
    }
    ElMessage.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg || '请求失败'))
  },
  (error) => {
    const status = error.response?.status
    const msg = error.response?.data?.msg || error.message || '网络错误'
    if (status === 401) {
      const store = useUserStore()
      store.logout()
      if (router.currentRoute.value.path !== '/login') {
        ElMessage.error(msg || '未登录或登录已过期')
        router.push('/login')
      }
    } else if (msg !== 'Network Error') {
      ElMessage.error(msg)
    }
    return Promise.reject(error)
  }
)

export default request
