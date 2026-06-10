<script setup>
import { ref } from 'vue'

const count = ref(10)
const loading = ref(false)
const finished = ref(false)

const load = () => {
  if (finished.value) return
  loading.value = true
  
  setTimeout(() => {
    count.value += 5
    loading.value = false
    if (count.value >= 30) finished.value = true
  }, 800)
}
</script>

<template>
  <div class="oj-home-page">
    <!-- 轮播 -->
    <section class="carousel-section">
      <el-carousel :interval="4000" type="card" height="280px">
        <el-carousel-item v-for="item in 6" :key="item">
          <h3 class="carousel-text">{{ item }}</h3>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- 社区 -->
    <section class="community-section">
      <h2 class="community-title">社区讨论</h2>
      <div class="list-container">
        <ul
          v-infinite-scroll="load"
          class="infinite-list"
          infinite-scroll-disabled="loading || finished"
          infinite-scroll-distance="100"
        >
          <li v-for="i in count" :key="i" class="list-item">
            社区帖子 #{{ i }}
          </li>
          <li v-if="loading" class="loading-tip">加载中...</li>
          <li v-if="finished" class="finished-tip">没有更多内容了</li>
        </ul>
      </div>
    </section>
  </div>

 
  <footer class="oj-footer">
    <div class="footer-container">
      HD Online Judge System | HammerDough
    </div>
  </footer>
</template>

<style scoped>
/* 页面主体 */
.oj-home-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 轮播 */
.carousel-section {
  margin: 20px 0 30px 0;
}
.carousel-text {
  color: #fff;
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  line-height: 280px;
  margin: 0;
}

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n + 1) {
  background-color: #d3dce6;
}
/* 社区 */
.community-section {
  margin-bottom: 50px;
}
.community-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
  padding-left: 10px;
  border-left: 4px solid #409eff;
}
.list-container {
  width: 100%;
  max-height: 65vh;
  overflow-y: auto;
  overflow-x: hidden;
}
.list-container::-webkit-scrollbar {
  display: none;
}
.infinite-list {
  padding: 0;
  margin: 0;
  list-style: none;
}
.list-item {
  height: 60px;
  line-height: 60px;
  padding: 0 20px;
  margin-bottom: 10px;
  background: #f5f7fa;
  border-radius: 8px;
  transition: 0.2s;
}
.list-item:hover {
  background: #edf2fc;
  padding-left: 25px;
}
.loading-tip,
.finished-tip {
  text-align: center;
  color: #909399;
  padding: 15px 0;
}

.oj-footer {
  width: 100%;           /* 全屏 */
  background: #000000;    /* 纯黑 */
  color: #cccccc;         /* 文字灰色 */
  text-align: center;
  padding: 35px 0;
  margin-top: 40px;
}
.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}
</style>