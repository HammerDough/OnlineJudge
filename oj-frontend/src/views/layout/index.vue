<script setup>
import {onMounted, ref} from 'vue'
import { ElDropdown,ElDropdownItem,ElDropdownMenu,ElAvatar } from 'element-plus';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { getUserInfo } from '@/api/user';

const router = useRouter()
const userStore = useUserStore()

// 页面初始化：刷新页面后，有Token则自动拉取用户信息
const initLoginState = async () => {
  const token = localStorage.getItem('token')
  // 有Token 但 Pinia 无登录状态，重新请求用户信息
  if (token && !userStore.isLogin) {
    try {
      const res = await getUserInfo()
      if (res.code === 1) {
        userStore.setUserInfo(res.data)
      } else {
        // Token 失效，清空状态
        userStore.clearUserInfo()
      }
    } catch (err) {
      userStore.clearUserInfo()
    }
  }
}

// 页面挂载时执行初始化
onMounted(() => {
  initLoginState()
})

// 下拉菜单点击事件
const handleMenuCommand = (cmd) => {
  if (cmd === 'userInfo') {
    // 跳转到个人中心
    router.push(`/users/${userStore.userId}`)
  } else if (cmd === 'logout') {
    // 退出登录：清空Pinia + 清空Token
    userStore.clearUserInfo()
    router.push('/login')
  }
}
</script>

<template>
    <div class="layout">

    <el-container>
    
      <!-- 顶部导航栏 -->
      <el-header class="navbar">
        <el-menu mode="horizontal" :ellipsis="false" router>
            <el-menu-item index="/index">
              <div class="logo">
                <img src="@/assets/logo.png" alt="Logo" class="logo-img">
                <span class="logo-text">HD-OJ</span>
              </div>
            </el-menu-item>
            <el-menu-item index="/problems">题库</el-menu-item>
            
            <div class="right-items">
              <el-menu-item v-if="!userStore.isLogin" index="/login">登录/注册</el-menu-item>
              <el-dropdown v-else trigger="click" @command="handleMenuCommand">
                <div class="avatar-wrap">
                  <!-- 后端有头像则展示，无则用默认头像 -->
                  <ElAvatar
                    size="36"
                    :src="userStore.avatar || '@/assets/avatar.png'"
                    class="avatar"
                  />
                  <!-- 优先展示昵称，无昵称展示用户名 -->
                  <span class="nickname">{{ userStore.nickname }}</span>
                </div>
                <template #dropdown>
                  <ElDropdownMenu>
                    <ElDropdownItem command="userInfo">个人中心</ElDropdownItem>
                    <ElDropdownItem command="logout" divided>退出登录</ElDropdownItem>
                  </ElDropdownMenu>
                </template>
              </el-dropdown>
            </div>
        </el-menu>
      </el-header>

      <!-- 主要内容区 -->
      <el-main class="content">
        <router-view></router-view>
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>

.navbar{
  padding: 0 12vw;
  background-color: #fff;
  border-bottom: 1px solid #ddd;
}


:deep(.el-menu) {
  border-bottom: none !important;
  background-color: transparent !important;
  display: flex;
  align-items: center;
}


:deep(.el-menu-item) {
  background-color: transparent !important;
  border-bottom: none !important;
  color: #444 !important;
}

:deep(.el-menu-item:hover) {
  font-weight: 500;
  color: #000 !important;     
  background: transparent !important;
  border-bottom: none !important;
}


/* 去掉激活项的背景色、下划线 */
:deep(.el-menu-item.is-active) {
  background-color: transparent !important;
  border-bottom: none !important;
  color: #ff9e59 !important;
  font-weight: 500;
}

.logo {
  display: flex;
  align-items: center;
  gap:5px;
}

.logo-img {
  width: 30px;
  height: 30px;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.right-items {
  margin-left: auto;
  display: flex;
}

.content {
  padding:0;
}

.avatar {
  cursor: pointer;
  border: 2px solid #eee;
  transition: all 0.2s;
}
.avatar:hover {
  border-color: #ff9e59;
}

.avatar-wrap {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
}
.nickname {
  color: #444;
  font-size: 14px;
}
</style>
