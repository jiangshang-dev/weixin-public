import axios from 'axios'
import { message } from 'ant-design-vue'
import router from '../router'

const http = axios.create({
  baseURL: import.meta.env.VUE_APP_BASE_API || '',
  timeout: 60000
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('weixin-token')
  if (token && !(config.url || '').endsWith('/login')) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body && typeof body.code === 'number' && body.code !== 200) {
      message.error(body.message || '请求失败')
      return Promise.reject(body)
    }
    return body.data
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('weixin-token')
      router.push('/login')
    }
    message.error(error.response?.data?.message || error.message || '网络异常')
    return Promise.reject(error)
  }
)

export default http
