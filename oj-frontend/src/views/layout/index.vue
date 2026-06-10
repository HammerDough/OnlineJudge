<script setup>

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
              <el-menu-item index="/login">登录/注册</el-menu-item>
            <!-- <el-menu-item index="/users">个人中心</el-menu-item> -->
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

</style>
