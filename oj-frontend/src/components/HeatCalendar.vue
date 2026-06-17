<script setup>
import { ref, onUnmounted, watch, nextTick } from 'vue'
import { storeToRefs } from 'pinia' // 关键：确保Pinia状态响应式
import CalHeatmap from 'cal-heatmap'
import Tooltip from 'cal-heatmap/plugins/Tooltip'
import LegendLite from 'cal-heatmap/plugins/LegendLite'
import CalendarLabel from 'cal-heatmap/plugins/CalendarLabel'
import 'cal-heatmap/cal-heatmap.css'
import dayjs from 'dayjs'
import { getUserDailyStat } from '@/api/userStat'
import { useUserStore } from '@/stores/user'

let cal = null
const userStore = useUserStore()
// 关键：用storeToRefs解构，保证userId和isLogin是响应式ref
const { userId, isLogin } = storeToRefs(userStore)

const heatMode = ref('minute')

const colorConfig = {
  minute:{
    domain:[1,30,60,90]
  },
  count:{
    domain:[1,3,5,10]
  }
}



// 同时监听登录状态和用户ID，双重保险
watch(
  [isLogin, userId,heatMode],
  ([newIsLogin, newUserId]) => {
    // 严格校验：必须已登录 + userId是大于0的数字
    if (!newIsLogin || typeof newUserId !== 'number' || newUserId <= 0) {
      cal?.destroy()
      cal = null
      return
    }

    // 销毁旧实例，防止重复渲染
    cal?.destroy()
    // 加nextTick确保DOM完全就绪后再初始化
    nextTick(() => {
      initHeatmap(newUserId)
    })
  },
  { 
    immediate: true,
    deep: false // 基本类型不需要深度监听
  }
)

// 抽离独立的初始化函数
const initHeatmap = async (userId) => {
  try {
    cal = new CalHeatmap()
    const { data: statList } = await getUserDailyStat(userId)

    const startDate = dayjs().subtract(364, 'day').toDate()
    const currentDomain = colorConfig[heatMode.value]

    cal.paint(
      {
        data: {
          source: statList,
          x: 'date',
          y: heatMode.value === 'minute' ? d => d.totalMinute : d => d.finishCount,
          groupY: 'max',
        },
        date: { 
          start: startDate,
          max: dayjs().toDate()
        },
        range: 12,
        scale: {
          color: {
            type: 'threshold',
            range: ['#eeeeee', '#ffd5b3', '#ffb380', '#ff9e59'],
            domain: currentDomain.domain,
          },
        },
        domain: {
          type: 'month',
          gutter: 4,
          label: { text: 'MMM', textAlign: 'start', position: 'top' },
        },
        subDomain: {
          type: 'ghDay',
          radius: 2,
          width: 11,
          height: 11,
          gutter: 4,
        },
        itemSelector: '#ex-ghDay',
      },
      [
        [Tooltip, {
          text: (date, value, dayjsDate) => {
            const dayData = statList.find(item => item.date === dayjsDate.format('YYYY-MM-DD'))
            if (!dayData ) {
              return heatMode.value === 'minute'
              ?`0分钟 | ${dayjsDate.format('YYYY-MM-DD')}`
              :`0题  | ${dayjsDate.format('YYYY-MM-DD')}`
            }
            return heatMode.value === 'minute'
              ?`${dayData.totalMinute}分钟 | ${dayjsDate.format('YYYY-MM-DD')}`
              :`${dayData.finishCount}题  | ${dayjsDate.format('YYYY-MM-DD')}`
          },
        }],
        [LegendLite, {
          includeBlank: true,
          itemSelector: '#ex-ghDay-legend',
          radius: 2,
          width: 11,
          height: 11,
          gutter: 4,
        }],
        [CalendarLabel, {
          width: 30,
          textAlign: 'start',
          text: () => dayjs.weekdaysShort().map((d, i) => (i % 2 === 0 ? '' : d)),
          padding: [25, 0, 0, 0],
        }],
      ]
    )
  } catch (error) {
    console.error('热力图加载失败:', error)
  }
}

// 翻页功能
const prev = () => cal?.previous()
const next = () => cal?.next()

// 组件卸载时销毁实例
onUnmounted(() => {
  cal?.destroy()
  cal = null
})
</script>

<template>
  <div
    style="
      background: #3a3a3a;
      color: #fff;
      border-radius: 3px;
      padding: 1rem;
      overflow: hidden;
      width: 100%;
      box-sizing: border-box;
    "
  >
    <div id="ex-ghDay" style="margin-bottom: 12px;"></div>

    <!-- 翻页按钮 -->
    <div style="display: flex; justify-content: space-between; align-items: center;">
      <div>
        <button @click="prev" style="margin-right: 6px;"><el-icon><CaretLeft /></el-icon></button>
        <button @click="next"><el-icon><CaretRight /></el-icon></button>
      </div>

       <!-- 模式切换按钮 -->
    <div style="display:flex;gap:10px;">
      <el-radio-group v-model="heatMode" size="small" text-color="#fff" fill="#ff9e59">
        <el-radio-button value="minute">刷题时长</el-radio-button>
        <el-radio-button value="count">做题数量</el-radio-button>
      </el-radio-group>
    </div>
      <!-- 右侧图例 -->
      <div style="font-size: 12px;">
        <span style="color: #768390;">Less</span>
        <div
          id="ex-ghDay-legend"
          style="display: inline-block; margin: 0 4px;"
        ></div>
        <span style="color: #768390;">More</span>
      </div>
    </div>
  </div>
</template>


<style scoped>
button {
  background: #ff9e59;
  border: 1px solid #444c56;
  padding: 4px 10px;
  border-radius: 4px;
  cursor: pointer;
}
button:hover {
  background: #ff9040;
}
</style>