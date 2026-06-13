import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    isLogin: false,
    userId: null,
    username: '',
    nickname: '',
    avatar: '',
    totalMinute: 0,
    finishProblemNum: 0
  }),
  actions: {
    // 存入用户信息
    setUserInfo(user) {
      this.isLogin = true
      this.userId = user.userId
      this.username = user.username
      this.nickname = user.nickname
      this.avatar = user.avatar
      this.totalMinute = user.totalMinute
      this.finishProblemNum = user.finishProblemNum
    },
    // 清空登录状态 + 本地Token
    clearUserInfo() {
      this.$reset()
      localStorage.removeItem('token')
    }
  }
})