<script setup>
import {ref,shallowRef,onMounted,onUnmounted,onBeforeUnmount,watch,computed} from 'vue'
import * as monaco from 'monaco-editor'
import { useRoute,useRouter} from 'vue-router'
import { getProblemDetail } from '@/api/problem'
import { ElMessage,ElLoading,ElMessageBox } from 'element-plus'
import { judgeCode } from '@/api/judge'

const judgeResultList = ref([])
const totalStatus = ref('')
const passCount = ref(0)
const totalCount = ref(0)
const judgeLoading = ref(false)




const route = useRoute()
const router = useRouter()
const problemId = ref('')
const problemDetail = ref({})



//===============description-panel================//
import DescriptionContent from '@/components/DescriptionContent.vue'
import SolutionContent from '@/components/SolutionContent.vue'
import SubmissionContent from '@/components/SubmissionContent.vue'

// 当前激活的菜单项
const activeTab = ref('description')

// 组件映射
const componentMap = {
  description: DescriptionContent,
  solution: SolutionContent,
  submission: SubmissionContent
}

// 当前显示的组件
const currentComponent = shallowRef(componentMap[activeTab.value])

// 菜单切换事件
const handleTabSelect = (index) => {
  activeTab.value = index
  currentComponent.value = componentMap[index]
}

//===============加载题目详情================//
const loadProblemDetail = async()=>{
    problemId.value = route.params.id
    if(!problemId.value){
        ElMessage.warning('题目ID不存在')
        return
    }

    const loading = ElLoading.service({text:'加载题目中...'})

    try{
        const res = await getProblemDetail(problemId.value)
        if(res.code == 1){
            problemDetail.value = res.data
            document.title = res.data.title
        }
    }catch(error){
        ElMessage.error('题目加载失败')
        console.error(error)
    }finally{
        loading.close()
    }
}


//===============edit-panel================//
const editorContainer = ref(null);
let editor = null;

const lang = ref('cpp')

const codeTemplates = {
  cpp: '#include <iostream>\nusing namespace std;\n\nint main() {\n\n    return 0;\n}',
  java: 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n\n    }\n}',
  python: '# Write your code here\nif __name__ == "__main__":\n    pass'
}

onMounted(async() => {
  await loadProblemDetail()

  editor = monaco.editor.create(editorContainer.value, {
    value: codeTemplates.cpp,
    language: 'cpp', // 支持：python/java/javascript/go 等
    theme: theme.value, // vs/vs-dark/hc-black
    automaticLayout: true,
    minimap: { enabled: false }, // OJ 通常关闭小地图
    fontSize: getFontSizeNum(fontSize.value),
    lineNumbers: getLineNumberStr(showLineNum.value),
    roundedSelection: true,
    scrollBeyondLastLine: false,
  });

  // 登录校验
  const token = localStorage.getItem('token')
  if(token){
    bindActiveEvent()
    resetIdleTimer()
    startTimer()
  }
});

const changeLang = (val) => {
  const model = editor.getModel()
  monaco.editor.setModelLanguage(model, val)
  editor.setValue(codeTemplates[val])
}

const settingDialogVisible = ref(false)
const form = ref({})
const theme = ref('vs')
const fontSize = ref('14px')
const showLineNum = ref(true)

const getFontSizeNum = (str)=>parseInt(str)
const getLineNumberStr = (boolVal) =>boolVal? 'on':'off'

// 字号变化
watch(fontSize, (newVal) => {
  if (!editor) return
  editor.updateOptions({
    fontSize: getFontSizeNum(newVal)
  })
})

// 主题变化
watch(theme, (newVal) => {
  if (!editor) return
  monaco.editor.setTheme(newVal)
})

// 行号开关变化
watch(showLineNum, (newBool) => {
  if (!editor) return
  editor.updateOptions({
    lineNumbers: getLineNumberStr(newBool)
  })
})

const reCode =()=>{
    ElMessageBox.confirm(
    '确认要将代码恢复为默认吗？',
    '提示',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
        editor.setValue(codeTemplates[lang.value])
      ElMessage({
        type: 'success',
        message: '操作成功',
      })
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '取消操作',
      })
    })
}


// 运行代码
const runCode = async () => {
  const code = editor.getValue()
  const codeLang = lang.value
  const pid = problemId.value

  if (!code.trim()) {
    ElMessage.warning('代码不能为空')
    return
  }

  judgeLoading.value = true
  judgeResultList.value = []
  totalStatus.value = ''

  try {
    const res = await judgeCode({
      problemId: pid,
      code: code,
      language: codeLang,
      operateType: 0 // 运行
    })

    if (res.code === 1) {
      const data = res.data
      judgeResultList.value = data.caseResultList || []
      totalStatus.value = data.totalStatus
      passCount.value = data.passCount
      totalCount.value = data.totalCount
    } else {
      ElMessage.error(res.msg || '运行失败')
    }
  } catch (err) {
    ElMessage.error('请求异常，请稍后重试')
    console.error(err)
  } finally {
    judgeLoading.value = false
  }
}

// 提交代码
const submitCode = async () => {
  const code = editor.getValue()
  const codeLang = lang.value
  const pid = problemId.value

  if (!code.trim()) {
    ElMessage.warning('代码不能为空')
    return
  }

  judgeLoading.value = true
  judgeResultList.value = []
  totalStatus.value = ''

  try {
    const res = await judgeCode({
      problemId: pid,
      code: code,
      language: codeLang,
      operateType: 1 // 提交
    })

    if (res.code === 1) {
      const data = res.data
      judgeResultList.value = data.caseResultList || []
      totalStatus.value = data.totalStatus
      passCount.value = data.passCount
      totalCount.value = data.totalCount
      ElMessage.success('提交完成')
    } else {
      ElMessage.error(res.msg || '提交失败')
    }
  } catch (err) {
    ElMessage.error('请求异常，请稍后重试')
    console.error(err)
  } finally {
    judgeLoading.value = false
  }
}


onUnmounted(() => {
  editor?.dispose();
});


// =====================计时功能======================//
import dayjs from 'dayjs'
import { updateUserDailyMinute } from '@/api/userStat'
// 计时配置
const IDLE_THRESHOLD = 3 * 60 * 1000    // 闲置3分钟停止计时
const UPLOAD_INTERVAL = 5 * 60          // 累计5分钟批量上报
const TICK_DELAY = 1000                 // 1秒累加一次

// 计时状态
let timerTick = null
let idleTimer = null
const isTiming = ref(false)
const totalLocalMinute = ref(0)
const lastUploadMinute = ref(0)
let lastActiveTime = Date.now()


// 时长格式化 tooltip 展示
const formatMinuteToStr = (totalMin) => {
  const m = Math.floor(totalMin)
  const h = Math.floor(m / 60)
  const mm = m % 60
  return h > 0 ? `${h}小时${mm}分钟` : `${mm}分钟`
}


const todayWorkTimeTip = computed(() => {
  const token = ref(localStorage.getItem('token'))
  if(!token.value){
    return '请登录后查看有效做题时长'
  }
  return `今日有效做题时长：${formatMinuteToStr(totalLocalMinute.value)}`
})

// 重置闲置倒计时（用户交互触发）
const resetIdleTimer = () => {
  lastActiveTime = Date.now()
  clearTimeout(idleTimer)
  idleTimer = setTimeout(() => pauseTimer(), IDLE_THRESHOLD)
}

// 页面切后台/切标签
const handleVisibility = () => {
  if (document.hidden) {
    pauseTimer()
  } else {
    const now = Date.now()
    if (now - lastActiveTime < IDLE_THRESHOLD) startTimer()
  }
}

// 启动计时
const startTimer = () => {
  console.log("开始计时")
  if (isTiming.value) return
  isTiming.value = true
  clearInterval(timerTick)
  timerTick = setInterval(() => {
    const now = Date.now()
    if (now - lastActiveTime >= IDLE_THRESHOLD) {
      pauseTimer()
      return
    }
    totalLocalMinute.value += 1 / 60
    const diff = Math.floor(totalLocalMinute.value) - lastUploadMinute.value
    if (diff >= UPLOAD_INTERVAL) uploadMinute(diff)
  }, TICK_DELAY)
}

// 暂停计时并上报增量
const pauseTimer = (isUnload = false) => {
  console.log("暂停")
  if (!isTiming.value) return
  isTiming.value = false
  clearInterval(timerTick)
  clearTimeout(idleTimer)
  const diff = Math.floor(totalLocalMinute.value) - lastUploadMinute.value
  if (diff <= 0) return

  const token = localStorage.getItem('token')
  if(!token) return
  const today = dayjs().format('YYYY-MM-DD')

  // 页面卸载：同步XHR，阻塞页面销毁，确保请求发完
  if(isUnload){
    const xhr = new XMLHttpRequest()
    xhr.open('POST', '/user/daily-stat/add-minute', false)
    xhr.setRequestHeader('Content-Type', 'application/json')
    xhr.setRequestHeader('Authorization', `Bearer ${token}`)
    xhr.send(JSON.stringify({ date: today, addMinute: diff }))
    lastUploadMinute.value = Math.floor(totalLocalMinute.value)
    return
  }
  // 正常闲置暂停：异步上报
  uploadMinute(diff)
}
// 异步上报增量时长（正常页面运行时使用）
const uploadMinute = async (addMinute) => {
  const token = localStorage.getItem('token')
  if (addMinute <= 0||!token) return
  try {
    const today = dayjs().format('YYYY-MM-DD')
    await updateUserDailyMinute({ date: today, addMinute })
    lastUploadMinute.value = Math.floor(totalLocalMinute.value)
  } catch (err) {
    console.error('时长上报失败，等待下次重试', err)
  }
}

// 关闭标签/刷新兜底：同步请求防止被浏览器中断
const handlePageUnload = () => {
  
  const diff = Math.floor(totalLocalMinute.value) - lastUploadMinute.value
  if (diff <= 0) return
  const today = dayjs().format('YYYY-MM-DD')
  const xhr = new XMLHttpRequest()
  // false 同步阻塞，页面不会直接销毁
  xhr.open('POST', '/user/daily-stat/add-minute', false)
  xhr.setRequestHeader('Content-Type', 'application/json')
  const token = localStorage.getItem('token')
  if (token) xhr.setRequestHeader('Authorization', `Bearer ${token}`)
  xhr.send(JSON.stringify({ date: today, addMinute: diff }))
}

// 绑定全局监听
const bindActiveEvent = () => {
  window.addEventListener('mousemove', resetIdleTimer)
  window.addEventListener('mousedown', resetIdleTimer)
  window.addEventListener('keydown', resetIdleTimer)
  window.addEventListener('visibilitychange', handleVisibility)
  window.addEventListener('beforeunload', handlePageUnload)
}

// 解绑全局监听
const unbindActiveEvent = () => {
  window.removeEventListener('mousemove', resetIdleTimer)
  window.removeEventListener('mousedown', resetIdleTimer)
  window.removeEventListener('keydown', resetIdleTimer)
  window.removeEventListener('visibilitychange', handleVisibility)
  window.removeEventListener('beforeunload', handlePageUnload)
}

onBeforeUnmount(() => {
  pauseTimer(true)
  unbindActiveEvent()
  clearTimeout(idleTimer)
  clearInterval(timerTick)
})
</script>

<template>
    <div class="page-wrapper">
        <el-splitter>
            <el-splitter-panel size="35%" :collapsible="true">
                <div class="description-panel">
                    <el-container>
                        <el-header class="description-head">
                            <el-tooltip content="返回首页" effect="light">
                                <span @click="router.push('/')" style="cursor:pointer">
                                    <img src="@/assets/logo.png" alt="Logo" class="logo-img" width="30px">
                                </span>
                            </el-tooltip>
                            <span class="title">{{problemDetail.id}}.{{ problemDetail.title }}</span>
                        </el-header>
                        <el-container>
                            <el-aside class="description-aside" width="100px">
                                <el-menu
                                    :default-active="activeTab"
                                    class="aside-menu"
                                    background-color="transparent"
                                    text-color="#3a3a3a"
                                    active-text-color="#ff9e59"
                                    @select="handleTabSelect"
                                >
                                    <el-menu-item index="description">
                                    <div class="menu-item-content">
                                        <el-icon><Notebook /></el-icon>
                                        <span class="menu-text">题目描述</span>
                                    </div>
                                    </el-menu-item>
                                    <el-menu-item index="solution">
                                    <div class="menu-item-content">
                                        <el-icon><Guide /></el-icon>
                                        <span class="menu-text">题解</span>
                                    </div>
                                    </el-menu-item>
                                    <el-menu-item index="submission">
                                    <div class="menu-item-content">
                                        <el-icon><DataLine /></el-icon>
                                        <span class="menu-text">提交记录</span>
                                    </div>
                                    </el-menu-item>
                                    <el-menu-item index="time-tip" disabled>
                                      <el-tooltip :content="todayWorkTimeTip" effect="light" placement="right">
                                        <div class="menu-item-content">
                                          <el-icon><Clock /></el-icon>
                                          <span class="menu-text">今日时长</span>
                                        </div>
                                      </el-tooltip>
                                    </el-menu-item>
                                </el-menu>
                            </el-aside>
                            <el-main class="description-main">
                                <component :is="currentComponent" :detail="problemDetail" />
                            </el-main>
                        </el-container>
                    </el-container>
                </div>
            </el-splitter-panel>
            <el-splitter-panel :min="200">
                <el-splitter layout="vertical">
                    <el-splitter-panel size="80%">
                        <div class="edit-panel" style="height: 100%;">
                            <el-container style="height: 100%;display:flex;flex-direction:column;">
                            <!-- 顶部：语言切换 + 运行 + 提交 -->
                            <el-header class="edit-header">
                                <div class="left">
                                    <span class="header-title">代码</span>
                                    <el-select v-model="lang" size="small" @change="changeLang" style="width:100px;">
                                    <el-option label="C++" value="cpp"></el-option>
                                    <el-option label="Java" value="java"></el-option>
                                    <el-option label="Python" value="python"></el-option>
                                    </el-select>
                                </div>

                                <div class="center">
                                    <el-button color="#40c35d" :dark="isDark" plain @click="runCode"><el-icon><VideoPlay /></el-icon>运行</el-button>
                                    <el-button color="#ff9e59" :dark="isDark" plain @click="submitCode"><el-icon><UploadFilled /></el-icon>提交</el-button>
                                </div>

                                <div class="right">
                                    <el-tooltip content="设置" effect="light">
                                        <el-button color="#ff9e59" :dark="isDark" text @click="settingDialogVisible=true"><el-icon size="larger"><Setting /></el-icon></el-button>
                                    </el-tooltip>
                                    <el-tooltip content="清空代码为默认" effect="light">
                                        <el-button color="#ff9e59" :dark="isDark" text @click="reCode"><el-icon size="larger"><Refresh /></el-icon></el-button>
                                    </el-tooltip>
                                </div>
                                
                                
                            </el-header>

                            <!-- 编辑器区域：自动填满 -->
                            <el-main class="edit-main">
                                <div ref="editorContainer" style="width:100%;height:100%;"></div>
                            </el-main>
                            </el-container>
                        </div>
                    </el-splitter-panel>
                    <el-splitter-panel :min="32">
                        <div class="console-panel">
                            <el-container>
                                <el-header class="console-header">控制台</el-header>
                                <el-main class="console-main">
                                    <!-- 整体汇总信息 -->
                                    <div class="console-summary" v-if="totalStatus">
                                        <span class="status-text">整体结果：{{ totalStatus }}</span>
                                        <span class="count-text">通过 {{ passCount }} / {{ totalCount }} 组用例</span>
                                    </div>

                                    <!-- 判题条目列表 -->
                                    <div class="result-list" v-loading="judgeLoading">
                                        <div class="result-item" v-for="item in judgeResultList" :key="item.caseId" :class="item.judgeStatus">
                                        <div class="item-left">
                                            <span class="case-index">用例 {{ item.caseId }}</span>
                                            <span class="status-tag">{{ item.judgeStatus }}</span>
                                        </div>
                                        <div class="item-right">
                                            <span class="time">耗时：{{ item.timeCost }} ms</span>
                                            <span class="memory">内存：{{ item.memoryCost }} KB</span>
                                        </div>
                                        <!-- 可选：展开查看输入/输出 -->
                                        <el-collapse-transition>
                                            <div class="detail-box">
                                            <p><strong>输入：</strong>{{ item.caseInput }}</p>
                                            <p><strong>标准答案：</strong>{{ item.standardOutput }}</p>
                                            <p><strong>你的输出：</strong>{{ item.userOutput }}</p>
                                            </div>
                                        </el-collapse-transition>
                                        </div>

                                        <!-- 空数据提示 -->
                                        <div class="empty-tip" v-if="!judgeLoading && judgeResultList.length === 0">
                                        暂无判题结果，请点击运行/提交
                                        </div>
                                    </div>
                                </el-main>
                            </el-container>
                        </div>
                    </el-splitter-panel>
                </el-splitter>
            </el-splitter-panel>
    </el-splitter>
    </div>
    <el-dialog v-model="settingDialogVisible" title="编辑器设置" width="300">
    <el-form :model="form">
      <el-form-item label="字号" :label-width="50">
        <el-select v-model="fontSize" placeholder="请选择字号">
          <el-option label="12px" value="12px" />
          <el-option label="14px" value="14px" />
          <el-option label="16px" value="16px" />
          <el-option label="18px" value="18px" />
          <el-option label="20px" value="20px" />
        </el-select>
      </el-form-item>
      <el-form-item label="主题" :label-width="50">
        <el-select v-model="theme" placeholder="主题">
          <el-option label="白色" value="vs" />
          <el-option label="黑色" value="vs-dark" />
        </el-select>
      </el-form-item>
      <el-form-item label="显示行号" :label-width="80">
        <el-switch v-model="showLineNum"
        style="--el-switch-on-color: #3a3a3a;"/>
      </el-form-item>
    </el-form>
  </el-dialog>

</template>

<style scoped>
.page-wrapper {
 width: 100%;
 height: 100vh;
 overflow: hidden;
}



.description-panel{
    height: 100%;
    overflow: hidden;
}

.description-head{
    display: flex;
    white-space: nowrap;
    align-items: center;
    gap: 20px;
    background-color: #fafafa;
    border-bottom: 1px solid #eee;
}

.description-main{
    padding: 0;
}

.el-container{
    height: 100%;
}
.description-aside {
  width: 60px;              
  border-right: 1px solid #eee;
}

.aside-menu {
  height: 100%;
  border-right: none;
}

.aside-menu .el-menu-item {
  height: 70px;
  line-height: 1.4;
  padding: 0 !important;
  display: flex;
  align-items: center;
  justify-content: center;
}

.menu-item-content {
  display: flex;
  flex-direction: column;    /* 垂直排列 */
  align-items: center;
  gap: 8px;
}

.menu-item-content .el-icon {
  font-size: 20px;
  margin-right: 0;       
}

.menu-text {
  font-size: 10px;
  color: inherit;
}

/* 编辑器样式部分 */
.edit-header{
    display: flex;
    align-items: center;
    justify-content: space-between;
    background:#fafafa;
}
.left{
    display: flex;
    gap: 20px;
}

.right{
    display: flex;
    .el-button{
        width: 20px;
        margin: 0 !important;
    }
}


.center{
    display: flex;
    left: 50%;
    transform: translate(-60%);
}

.edit-main{
    flex: 1;
    padding: 0;
    position: relative;
    overflow: hidden;
}

.edit-header .el-select{
    margin-right: 150px;
}

.header-title{
    white-space: nowrap;
}


/* 控制台样式部分 */

.console-panel{
    height: 100%;
    overflow: hidden;
}

.console-header{
    height: 32px;
    background-color: #fafafa;
}

.console-main{
    height: 100%;
    overflow: hidden;
}


/* 控制台汇总栏 */
.console-summary {
  padding: 8px 12px;
  background: #f5f7fa;
  border-bottom: 1px solid #eee;
  display: flex;
  gap: 20px;
  font-size: 13px;
}
.status-text {
  font-weight: bold;
}
.count-text {
  color: #666;
}

/* 结果列表 */
.result-list {
  height: calc(100% - 36px);
  overflow-y: auto;
  padding: 8px;
}
.empty-tip {
  text-align: center;
  line-height: 40px;
  color: #999;
  font-size: 13px;
}

/* 单条用例条目 */
.result-item {
  border: 1px solid #eee;
  border-radius: 4px;
  padding: 10px;
  margin-bottom: 8px;
  font-size: 13px;
}
.result-item.AC {
  border-left: 4px solid #67c23a;
}
.result-item.WA {
  border-left: 4px solid #f56c6c;
}
.result-item.TLE, .result-item.MLE, .result-item.RE {
  border-left: 4px solid #e6a23c;
}

.item-left {
  display: inline-block;
  width: 50%;
}
.item-right {
  display: inline-block;
  width: 48%;
  text-align: right;
}

.case-index {
  margin-right: 12px;
  color: #333;
}
.status-tag.AC {
  color: #67c23a;
  font-weight: bold;
}
.status-tag.WA {
  color: #f56c6c;
  font-weight: bold;
}
.status-tag.TLE, .status-tag.MLE, .status-tag.RE {
  color: #e6a23c;
  font-weight: bold;
}

.time, .memory {
  margin-left: 10px;
  color: #666;
}

/* 详情区域 */
.detail-box {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px dashed #eee;
  color: #666;
  line-height: 1.6;
  word-break: break-all;
}

</style>
