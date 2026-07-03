<template>
  <div class="song-comment-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>歌曲留言列表</span>
          <div class="filters">
            <el-select v-model="status" placeholder="状态" clearable style="width: 120px; margin-right: 10px" @change="loadData">
              <el-option label="正常" :value="1" />
              <el-option label="已封禁" :value="0" />
            </el-select>
            <el-select v-model="category" placeholder="歌曲类型" clearable style="width: 150px" @change="loadData">
              <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
            </el-select>
          </div>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="歌曲信息" width="200">
          <template #default="{ row }">
            <div class="song-info">
              <div class="song-title">{{ row.songTitle }}</div>
              <div class="song-artist">{{ row.songArtist }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="content" label="评论内容" show-overflow-tooltip />
        <el-table-column prop="likes" label="点赞数" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '已封禁' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" type="success" size="small" @click="handleUnban(row)">解封</el-button>
            <el-button v-if="row.status === 1" type="warning" size="small" @click="handleBan(row)">封禁</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="pageNum" v-model:page-size="pageSize"
        :total="total" @current-change="loadData"
        layout="total, prev, pager, next" style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSongCommentList, banSongComment, unbanSongComment, deleteSongComment, getCategories } from '@/api/songComment'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const status = ref(null)
const category = ref(null)
const categories = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getSongCommentList({ pageNum: pageNum.value, pageSize: pageSize.value, status: status.value, category: category.value })
    console.log('歌曲评论列表响应:', res)
    if (res && res.data) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      tableData.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('加载歌曲评论失败:', error)
    ElMessage.error(error.message || '加载歌曲评论失败')
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const res = await getCategories()
    console.log('歌曲分类响应:', res)
    if (res && res.data) {
      categories.value = res.data || []
    }
  } catch (error) {
    console.error('加载歌曲分类失败:', error)
    categories.value = []
  }
}

const handleBan = async (row) => {
  try {
    await ElMessageBox.confirm(`确定封禁这条评论吗？`, '提示', { type: 'warning' })
    await banSongComment(row.id)
    ElMessage.success('封禁成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('封禁失败:', error)
      ElMessage.error(error.message || '封禁失败')
    }
  }
}

const handleUnban = async (row) => {
  try {
    await unbanSongComment(row.id)
    ElMessage.success('解封成功')
    loadData()
  } catch (error) {
    console.error('解封失败:', error)
    ElMessage.error(error.message || '解封失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该评论？', '提示', { type: 'warning' })
    await deleteSongComment(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }
}

onMounted(() => {
  console.log('歌曲评论管理页面挂载')
  loadCategories()
  loadData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filters {
  display: flex;
  align-items: center;
}

.song-info {
  display: flex;
  flex-direction: column;
}

.song-title {
  font-weight: bold;
  margin-bottom: 5px;
}

.song-artist {
  color: #666;
  font-size: 12px;
}
</style>
