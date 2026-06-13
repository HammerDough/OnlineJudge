<script setup>
import { ref } from 'vue'
import {login,register,getUserInfo} from '@/api/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'


const router = useRouter()
const userStore = useUserStore()

// 切换登录/注册
const isLogin = ref(true)

// 登录表单
const loginForm = ref({
  username: '',
  password: ''
})

// 注册表单
const registerForm = ref({
  username: '',
  password: ''
})

// 登录逻辑
const handleLogin = async()=>{
  const{username,password} = loginForm.value

  if(!username||!password){
    ElMessage.warning('用户名和密码不能为空')
    return
  }

  try{
    const res = await login(loginForm.value)
    if(res.code == 1){
      localStorage.setItem('token',res.data)
      ElMessage.success('登录成功')

      const userRes = await getUserInfo()
      if(userRes.code == 1){
        userStore.setUserInfo(userRes.data)
      }

      router.push('/index')
    }else{
      ElMessage.error(res.msg)
    }
  }catch(err){
    ElMessage.error('服务器请求超时')
  }
}

// 注册逻辑
const handleRegister = async () => {
  const { username, password } = registerForm.value
  if (!username || !password) {
    ElMessage.warning('用户名和密码不能为空')
    return
  }
  try {
    const res = await register(registerForm.value)
    if (res.code === 1) {
      ElMessage.success('注册成功，请前往登录')
      // 注册成功切到登录页，清空表单
      isLogin.value = true
      registerForm.value = { username: '', password: '' }
    } else {
      ElMessage.error(res.msg)
    }
  } catch (err) {
    ElMessage.error('服务器请求超时')
  }
}

</script>


<template>
  <div class="login-page">
    
    <!-- 登录卡片 -->
    <div class="login-card">
        <div class="logo">
            <img src="@/assets/logo.png" alt="Logo" class="logo-img" />
            <span class="logo-text">HD-OJ</span>
        </div>
      <!-- 切换选项：登录 / 注册 -->
      <div class="tab-switch">
        <span :class="{ active: isLogin }" @click="isLogin = true">登录</span>
        <span :class="{ active: !isLogin }" @click="isLogin = false">注册</span>
      </div>

      <!-- 登录表单 -->
      <el-form v-if="isLogin" model="loginForm" class="form" @keyup.enter="handleLogin">
        <el-form-item>
          <el-input v-model="loginForm.username" placeholder="请输入用户名" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="loginForm.password" placeholder="请输入密码" size="large" show-password />
        </el-form-item>
        <el-button color="#3a3a3a" size="large" :dark="isDark" @click="handleLogin">登录</el-button>
      </el-form>

      <!-- 注册表单 -->
      <el-form v-else model="registerForm" class="form" @keyup.enter="handleRegister">
        <el-form-item>
          <el-input v-model="registerForm.username" placeholder="请输入用户名" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="registerForm.password" placeholder="请输入密码" size="large" show-password />
        </el-form-item>
        <el-button color="#3a3a3a" size="large" :dark="isDark" @click="handleRegister">注册</el-button>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  width: 100%;
  height: 93vh;
  display: flex;
  justify-content: center;
  background-color: #fafbfc;
}

.login-card {
  width: 400px;
  height: 500px;
  margin-top: 100px;
  background: #fff;
  border-radius: 12px;
  padding: 50px 30px;
  border: 4px dashed #ff9e59;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.06);
}

.logo{
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 30px;
}

.logo-img {
  width: 60px;
  height: 60px;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}


.tab-switch {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 30px;
  font-size: 16px;
}

.tab-switch span {
  cursor: pointer;
  color: #666;
}

.tab-switch span.active {
  color: #ff9e59;
  font-weight: 500;
  padding-bottom: 4px;
}

/* 表单 */
.form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

</style>