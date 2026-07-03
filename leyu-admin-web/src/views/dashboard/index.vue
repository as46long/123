<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #409EFF"><el-icon><User /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalUsers }}</div>
              <div class="stat-label">总用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #67C23A"><el-icon><Headset /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalSongs }}</div>
              <div class="stat-label">总歌曲数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #E6A23C"><el-icon><Play /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayPlayCount }}</div>
              <div class="stat-label">歌曲播放量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #F56C6C"><el-icon><Money /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">¥{{ stats.totalRevenue || '0.00' }}</div>
              <div class="stat-label">累计收入</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header><span>用户增长趋势</span></template>
          <div ref="userChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header><span>收入趋势</span></template>
          <div ref="orderChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 排行榜 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header><span>热门歌曲TOP10</span></template>
          <el-carousel height="200px" :interval="3000" indicator-position="outside">
            <el-carousel-item v-for="(song, index) in stats.hotSongs" :key="song.id">
              <div class="song-carousel-item">
                <img :src="song.coverUrl || defaultCover" :alt="song.title" class="song-cover" />
                <div class="song-info">
                  <div class="song-rank">TOP {{ index + 1 }}</div>
                  <div class="song-title">{{ song.title }}</div>
                  <div class="song-artist">{{ song.artist }}</div>
                  <div class="song-play-count">
                    <el-icon><Headset /></el-icon>
                    {{ song.playCount || 0 }} 次播放
                  </div>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
          <el-table :data="stats.hotSongs" stripe max-height="300" style="margin-top: 20px">
            <el-table-column prop="title" label="歌曲名" />
            <el-table-column prop="artist" label="歌手" />
            <el-table-column prop="playCount" label="播放量" width="100" />
            <el-table-column label="VIP" width="80">
              <template #default="{ row }">
                <el-tag v-if="row.isVip" type="warning">VIP</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { getStats, getCharts } from '@/api/dashboard'
import * as echarts from 'echarts'

const defaultCover = 'https://via.placeholder.com/150x150?text=Music'

const stats = ref({
  totalUsers: 0,
  totalSongs: 0,
  todayPlayCount: 0,
  totalRevenue: 0,
  hotSongs: []
})

const chartData = ref({
  dates: [],
  userTrend: [],
  orderTrend: [],
  playTrend: []
})

const userChartRef = ref(null)
const orderChartRef = ref(null)
let userChart = null
let orderChart = null

const initCharts = () => {
  // 用户增长趋势图
  userChart = echarts.init(userChartRef.value)
  userChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: chartData.value.dates
    },
    yAxis: { type: 'value' },
    series: [{
      name: '新增用户',
      type: 'line',
      smooth: true,
      areaStyle: { opacity: 0.3 },
      itemStyle: { color: '#409EFF' },
      data: chartData.value.userTrend
    }]
  })

  // 收入趋势图
  orderChart = echarts.init(orderChartRef.value)
  orderChart.setOption({
    tooltip: { trigger: 'axis', formatter: '{b}<br/>收入: ¥{c}' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: chartData.value.dates
    },
    yAxis: { type: 'value', axisLabel: { formatter: '¥{value}' } },
    series: [{
      name: '收入',
      type: 'line',
      smooth: true,
      areaStyle: { opacity: 0.3 },
      itemStyle: { color: '#67C23A' },
      data: chartData.value.orderTrend
    }]
  })
}

const handleResize = () => {
  userChart?.resize()
  orderChart?.resize()
}

onMounted(async () => {
  const res = await getStats()
  stats.value = res.data

  const chartRes = await getCharts()
  chartData.value = chartRes.data

  initCharts()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  userChart?.dispose()
  orderChart?.dispose()
})
</script>

<style scoped>
.stat-card { display: flex; align-items: center; }
.stat-icon {
  width: 60px; height: 60px;
  border-radius: 8px;
  display: flex; justify-content: center; align-items: center;
  color: #fff; font-size: 24px;
}
.stat-info { margin-left: 16px; }
.stat-value { font-size: 28px; font-weight: bold; color: #303133; }
.stat-label { font-size: 14px; color: #909399; margin-top: 4px; }

/* 轮播图样式 */
.song-carousel-item {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  height: 100%;
}
.song-cover {
  width: 150px;
  height: 150px;
  border-radius: 8px;
  object-fit: cover;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
.song-info {
  margin-left: 24px;
  text-align: left;
}
.song-rank {
  font-size: 14px;
  color: #F56C6C;
  font-weight: bold;
  margin-bottom: 8px;
}
.song-title {
  font-size: 22px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}
.song-artist {
  font-size: 16px;
  color: #606266;
  margin-bottom: 12px;
}
.song-play-count {
  font-size: 14px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
