import { defineStore } from 'pinia'
import { defineStore, ref } from 'pinia'

const userStore = defineStore('user', () => {
  // 登录状态
  const isLoggedIn = ref(false)

  // 登录令牌
  const token = ref('')

  // 用户信息
  const role = ref('')

  // 设置令牌
  const setToken = (newToken) => {
    token.value = newToken
  }

  // 登出，清空用户信息
  const logout = () => {
    role.value = ''
    isLoggedIn.value = false
    token.value = ''
  }

  return {
    role,
    isLoggedIn,
    token,
    setToken,
    logout
  }
}, {
  // 配置持久化
  persist: {
    // 持久化的键名，默认为store的id
    key: 'user-store',
    // 持久化存储方式，可选：localStorage、sessionStorage、cookie
    storage: localStorage,
    // 需要持久化的状态字段
    pick: ['role', 'isLoggedIn', 'token']
  }
})

export { userStore }