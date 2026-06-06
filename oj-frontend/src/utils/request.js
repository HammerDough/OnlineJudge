import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 60000,
})


//axios的响应拦截器
request.interceptors.response.use(
    (response) => {
        return response.data
    },
    (error) => {
        return Promise.reject(error)
    }
)


export default request