<template>
  <div class="page-container">
    <h2 class="page-title">平台数据看板</h2>

    <!-- 数字指标 -->
    <div class="stat-cards">
      <div class="stat-card">
        <div class="stat-num">{{ stats.totalGoods ?? 0 }}</div>
        <div class="stat-label">总发布物品</div>
      </div>
      <div class="stat-card">
        <div class="stat-num green">{{ stats.totalDealt ?? 0 }}</div>
        <div class="stat-label">已成交</div>
      </div>
      <div class="stat-card">
        <div class="stat-num blue">{{ stats.totalOnline ?? 0 }}</div>
        <div class="stat-label">上架中</div>
      </div>
      <div class="stat-card">
        <div class="stat-num orange">{{ stats.totalPending ?? 0 }}</div>
        <div class="stat-label">待审核</div>
      </div>
      <div class="stat-card">
        <div class="stat-num purple">{{ stats.totalUsers ?? 0 }}</div>
        <div class="stat-label">注册用户</div>
      </div>
    </div>

    <!-- 图表 -->
    <div class="chart-grid mt-16">
      <div class="chart-card">
        <h4>各类物品占比</h4>
        <div ref="pieRef" class="chart-box" />
      </div>
      <div class="chart-card">
        <h4>近7天发布量</h4>
        <div ref="barRef" class="chart-box" />
      </div>
      <div class="chart-card chart-wide">
        <h4>物品状态分布</h4>
        <div ref="statusRef" class="chart-box" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { adminGetStats } from '../../api'

const stats = ref({})
const pieRef = ref()
const barRef = ref()
const statusRef = ref()

let pieChart = null
let barChart = null
let statusChart = null
let timer = null

const STATUS_TEXT = { 0: '待审核', 1: '上架中', 2: '已成交', 3: '已下架' }
const STATUS_COLOR = ['#e6a23c', '#67c23a', '#909399', '#f56c6c']

function renderCharts() {
  const s = stats.value

  // 分类占比饼图
  if (pieChart) pieChart.dispose()
  pieChart = echarts.init(pieRef.value)
  pieChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, type: 'scroll' },
    series: [
      {
        type: 'pie',
        radius: ['35%', '62%'],
        center: ['50%', '45%'],
        itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
        label: { formatter: '{b}: {c}' },
        data: (s.categoryList || []).map((c) => ({
          name: c.categoryName || '未分类',
          value: Number(c.cnt) || 0
        }))
      }
    ]
  })

  // 近7天发布量柱状图
  if (barChart) barChart.dispose()
  barChart = echarts.init(barRef.value)
  const days = (s.last7Days || []).map((d) => String(d.day))
  const counts = (s.last7Days || []).map((d) => Number(d.cnt) || 0)
  barChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 16, top: 30, bottom: 30 },
    xAxis: { type: 'category', data: days, axisLabel: { fontSize: 11 } },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        type: 'bar',
        data: counts,
        barWidth: '45%',
        itemStyle: { color: '#38bdf8', borderRadius: [6, 6, 0, 0] }
      }
    ]
  })

  // 状态分布条形图
  if (statusChart) statusChart.dispose()
  statusChart = echarts.init(statusRef.value)
  const statusData = (s.statusList || []).map((d) => ({
    name: STATUS_TEXT[Number(d.status)] || `状态${d.status}`,
    value: Number(d.cnt) || 0,
    itemStyle: { color: STATUS_COLOR[Number(d.status)] || '#909399' }
  }))
  statusChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 40, right: 16, top: 20, bottom: 30 },
    xAxis: { type: 'category', data: statusData.map((d) => d.name) },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{ type: 'bar', data: statusData, barWidth: '45%', itemStyle: { borderRadius: [6, 6, 0, 0] } }]
  })
}

async function fetchStats() {
  const res = await adminGetStats()
  stats.value = res.data
  await nextTick()
  renderCharts()
}

function handleResize() {
  pieChart?.resize()
  barChart?.resize()
  statusChart?.resize()
}

onMounted(() => {
  fetchStats()
  timer = setInterval(fetchStats, 60000)
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
  window.removeEventListener('resize', handleResize)
  pieChart?.dispose()
  barChart?.dispose()
  statusChart?.dispose()
})
</script>

<style scoped>
.page-title {
  font-size: 20px;
  color: #303133;
}
.stat-cards {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 14px;
  margin-top: 16px;
}
.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 18px 16px;
  text-align: center;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}
.stat-num {
  font-size: 28px;
  font-weight: 700;
  color: #1f6feb;
}
.stat-num.green { color: #67c23a; }
.stat-num.blue { color: #38bdf8; }
.stat-num.orange { color: #e6a23c; }
.stat-num.purple { color: #8b5cf6; }
.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 6px;
}
.chart-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}
.chart-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
}
.chart-card h4 {
  font-size: 15px;
  color: #303133;
  margin-bottom: 8px;
}
.chart-box {
  height: 300px;
}
.chart-wide {
  grid-column: 1 / -1;
}
@media (max-width: 768px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  .chart-grid {
    grid-template-columns: 1fr;
  }
  .chart-wide {
    grid-column: auto;
  }
}
</style>
