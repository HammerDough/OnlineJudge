<template>
  <div class="calendar-card">
    <!-- 自定义头部 -->
    <div class="calendar-header">
      <h2 class="title">打卡日历</h2>
      <div class="month-controls">
        <!-- 使用 moveBy 方法直接控制月份 -->
        <button @click="prevMonth">‹</button>
        <span class="month-text">{{ currentMonth }}月</span>
        <button @click="nextMonth">›</button>
      </div>
    </div>

    <!-- 关键：监听 update:pages 事件，并移除 v-model 绑定 -->
    <Calendar
      ref="calendarRef"
      :initial-page="{ month: initialMonth, year: initialYear }"
      :show-other-months="false"
      :attributes="checkAttrs"
      @update:pages="handlePageUpdate"
      class="custom-calendar"
      transparent
      borderless
    >
      <template #day-content="{ day }">
        <div class="day-cell" :class="{ today: isToday(day) }">
          {{ day.day }}
        </div>
      </template>
    </Calendar>


    <div class="calendar-footer">
      <el-tooltip 
    content="完成一题即可完成今日打卡" 
    effect="light" 
    placement="bottom">                              
      <span style="cursor:pointer">打卡规则</span>
    </el-tooltip>
    </div>
    
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Calendar } from 'v-calendar'
import 'v-calendar/style.css'

import { getSignCalendar } from '@/api/sign'


// 获取日历组件的引用
const calendarRef = ref(null)

// 当前显示的年月信息
const currentYear = ref(new Date().getFullYear())
const currentMonth = ref(new Date().getMonth() + 1) // v-calendar 的 month 是 1-12

// 初始年月，默认为当前年月
const initialYear = ref(new Date().getFullYear())
const initialMonth = ref(new Date().getMonth() + 1)


// 打卡日期示例
const checkedDates = ref([])
const loadedCache = new Set()

// 上一月：通过调用 moveBy 方法
const prevMonth = () => {
  if (calendarRef.value) {
    // 参数 -1 表示向前移动一个月
    calendarRef.value.moveBy(-1)
  }
}

// 下一月：通过调用 moveBy 方法
const nextMonth = () => {
  if (calendarRef.value) {
    // 参数 1 表示向后移动一个月
    calendarRef.value.moveBy(1)
  }
}

// 监听月份变化，更新显示的文本
const handlePageUpdate = async (pages) => {
  if (pages && pages.length > 0) {
    const page = pages[0]
    const y = page.year
    const m = page.month
    currentYear.value = y
    currentMonth.value = m
    // 拼接缓存key
    const cacheKey = `${y}-${m}`
    // 已请求过直接返回，不再发接口
    if (loadedCache.has(cacheKey)) return

    // 未请求过，拉取数据并存入缓存
    await fetchSignData(y, m, cacheKey)
    }
}

const fetchSignData = async (year, month, cacheKey) => {
  try {
    const res = await getSignCalendar({ year, month })
    if (res.code === 1) {
      // 兜底：res.data 不存在/为null/空，直接赋值空数组
      const rawList = res.data ?? []
      const dateArr = rawList.map(item => item.signDate)
      checkedDates.value = dateArr
      loadedCache.add(cacheKey)
    }
  } catch (err) {
    console.error("打卡日历加载失败", err)
    // 请求失败清空打卡日期，避免残留上月数据
    checkedDates.value = []
  }
}

// 判断今天（用于高亮）
const isToday = (day) => {
  if (!day?.date) return false
  const todayStr = new Date().toDateString()
  const dayStr = day.date.toDateString()
  return todayStr === dayStr
}

// 打卡圆点配置
const checkAttrs = computed(() => [
  {
    key: 'checked',
    dates: checkedDates.value,
    dot: { color: '#fff', size: 4 }
  }
])

// 组件挂载后，可选：如果 initial-page 设置正确，这里可以留空
onMounted(() => {
  const y = initialYear.value
  const m = initialMonth.value
  const cacheKey = `${y}-${m}`
  fetchSignData(y, m, cacheKey)
})
</script>

<style scoped>
.calendar-card {
  width: 100%;
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  margin: 0 auto;
 
}
.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.title {
  font-size: 20px;
  font-weight: bold;
  margin: 0;
}
.month-controls {
  display: flex;
  align-items: center;
}
.month-controls button {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: none;
  background: #f5f5f5;
  cursor: pointer;
}
.month-text {
  min-width: 50px;
  text-align: center;
}
:deep(.vc-header) {
  display: none !important;
}
:deep(.vc-calendar) {
  border: none !important;
  background: transparent !important;
  box-shadow: none !important;
}
:deep(.vc-day) {
  background: transparent !important;
  border: none !important;
}
.day-cell {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
}
.day-cell.today {
  background: #ff9e59;
  color: white;
  border-radius: 50%;
  width: 34px;
  height: 34px;
}
.calendar-footer {
  text-align: right;
  font-size: 14px;
  color: #999;
  margin-top: 12px;
  position: relative;
}
</style>