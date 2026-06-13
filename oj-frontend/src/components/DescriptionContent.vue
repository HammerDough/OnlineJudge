<template>
  <div class="problem-desc">
    <!-- 头部：标题、标签、限制信息 -->
    <div class="prob-header">
      <h2 class="prob-title">{{ detail.title }}</h2>
      <div class="info-line">
        <el-tag :type="diffTagType" size="small">{{ detail.difficultyText }}</el-tag>
        <span class="rate">通过率：{{ detail.passRate }}</span>
        <span class="limit">时间限制：{{ detail.timeLimit }} ms</span>
        <span class="limit">内存限制：{{ (detail.memoryLimit / 1024).toFixed(1) }} MB</span>
      </div>
    </div>

    <!-- 题目描述 -->
    <div class="block">
      <h3 class="block-title">题目描述</h3>
      <div class="content" v-html="detail.description"></div>
    </div>

    <!-- 输入描述 -->
    <div class="block">
      <h3 class="block-title">输入描述</h3>
      <div class="content" v-html="detail.inputDescription"></div>
    </div>

    <!-- 输出描述 -->
    <div class="block">
      <h3 class="block-title">输出描述</h3>
      <div class="content" v-html="detail.outputDescription"></div>
    </div>

    <!-- 样例输入 -->
    <div class="block">
      <h3 class="block-title">样例输入</h3>
      <el-input
        v-model="detail.sampleInput"
        type="textarea"
        readonly
        rows="4"
        class="code-area"
      />
    </div>

    <!-- 样例输出 -->
    <div class="block">
      <h3 class="block-title">样例输出</h3>
      <el-input
        v-model="detail.sampleOutput"
        type="textarea"
        readonly
        rows="4"
        class="code-area"
      />
    </div>

    <!-- 题目提示（有内容才展示） -->
    <div class="block" v-if="detail.hint">
      <h3 class="block-title">提示</h3>
      <div class="content" v-html="detail.hint"></div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

// 接收父组件传递的题目详情
const props = defineProps({
  detail: {
    type: Object,
    default: () => ({}),
    required: true
  }
})

// 难度标签颜色：简单=绿 中等=黄 困难=红
const diffTagType = computed(() => {
  const text = props.detail.difficultyText
  if (text === '简单') return 'success'
  if (text === '中等') return 'warning'
  if (text === '困难') return 'danger'
  return 'info'
})
</script>

<style scoped>
.problem-desc {
  padding: 24px;
  height: 100%;
  overflow-y: auto;
  box-sizing: border-box;
}

/* 头部区域 */
.prob-header {
  padding-bottom: 16px;
  margin-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}
.prob-title {
  font-size: 22px;
  margin: 0 0 10px 0;
  color: #1f2937;
}
.info-line {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  color: #666;
  font-size: 14px;
}
.limit {
  color: #888;
}

/* 通用模块 */
.block {
  margin-bottom: 24px;
}
.block-title {
  font-size: 16px;
  margin: 0 0 8px 0;
  color: #333;
  font-weight: 600;
}
.content {
  line-height: 1.8;
  color: #444;
  white-space: pre-wrap;
  font-size: 14px;
}

/* 代码样式输入框 */
.code-area {
  background-color: #f7f8fa !important;
  font-family: "Consolas", monospace;
  font-size: 13px;
}
</style>