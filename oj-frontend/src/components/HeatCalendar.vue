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

<script setup>
import { onMounted, onUnmounted } from 'vue'
import CalHeatmap from 'cal-heatmap'
import Tooltip from 'cal-heatmap/plugins/Tooltip'
import LegendLite from 'cal-heatmap/plugins/LegendLite'
import CalendarLabel from 'cal-heatmap/plugins/CalendarLabel'
import 'cal-heatmap/cal-heatmap.css'
import dayjs from 'dayjs'

let cal = null

onMounted(() => {
  cal = new CalHeatmap()

  // 模拟一整年数据（保证布局完整）
  const data = []
  for (let i = 1; i <= 365; i++) {
    const d = new Date(2025, 0, i)
    data.push({
      date: d.toISOString().slice(0, 10),
      minute: Math.floor(Math.random() * 120)
    })
  }

  cal.paint(
    {
      data: {
        source: data,
        x: 'date',
        y: d => +d.minute,
        groupY: 'max',
      },
      date: { start: new Date('2025-01-01') },
      range: 12,
      scale: {
        color: {
          type: 'threshold',
          range: ['#eeeeee', '#ffd5b3', '#ffb380', '#ff9e59'],
          domain: [10, 20, 30],
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
          if (!value) return `0min on ${dayjsDate.format('YYYY-MM-DD')}`
          return `${value}min on ${dayjsDate.format('YYYY-MM-DD')}`
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
})

const prev = () => cal?.previous()
const next = () => cal?.next()

onUnmounted(() => cal?.destroy())
</script>

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