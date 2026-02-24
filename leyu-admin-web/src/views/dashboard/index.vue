<template>
  <div class="dashboard">
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
              <div class="stat-label">今日播放量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #F56C6C"><el-icon><ChatDotSquare /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingComments }}</div>
              <div class="stat-label">待审核留言</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <template #header><span>热门歌曲TOP10</span></template>
      <el-table :data="stats.hotSongs" stripe>
        <el-table-column prop="title" label="歌曲名" />
        <el-table-column prop="artist" label="歌手" />
        <el-table-column prop="album" label="专辑" />
        <el-table-column prop="playCount" label="播放量" />
        <el-table-column prop="category" label="分类" />
        <el-table-column label="VIP">
          <template #default="{ row }">
            <el-tag v-if="row.isVip" type="warning">VIP</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStats } from '@/api/dashboard'

const stats = ref({
  totalUsers: 0,
  totalSongs: 0,
  todayPlayCount: 0,
  pendingComments: 0,
  hotSongs: []
})

onMounted(async () => {
  const res = await getStats()
  stats.value = res.data
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
</style>
