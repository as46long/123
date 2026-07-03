<template>
  <div class="song-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>歌曲列表</span>
          <div>
            <el-input v-model="keyword" placeholder="搜索歌曲名/歌手" style="width: 200px; margin-right: 10px" @keyup.enter="loadData" clearable />
            <el-select v-model="categoryFilter" placeholder="选择分类" clearable style="width: 150px; margin-right: 10px" @change="loadData">
              <el-option label="全部分类" value="" />
              <el-option label="流行" value="流行" />
              <el-option label="摇滚" value="摇滚" />
              <el-option label="古典" value="古典" />
              <el-option label="民谣" value="民谣" />
              <el-option label="电子" value="电子" />
            </el-select>
            <el-button type="primary" @click="handleAdd">新增歌曲</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="歌曲名" />
        <el-table-column prop="artist" label="歌手" />
        <el-table-column prop="album" label="专辑" />
        <el-table-column prop="category" label="分类" />
        <el-table-column prop="playCount" label="播放量" />
        <el-table-column label="封面" width="80">
          <template #default="{ row }">
            <el-image v-if="row.coverUrl" :src="row.coverUrl" style="width: 50px; height: 50px" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column label="歌词">
          <template #default="{ row }">
            <el-tag v-if="row.lyrics" type="success">有歌词</el-tag>
            <el-tag v-else type="info">无歌词</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="VIP">
          <template #default="{ row }">
            <el-tag v-if="row.isVip" type="warning">VIP</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" size="small" @click="handleLyrics(row)">歌词</el-button>
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

    <!-- 歌曲编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑歌曲' : '新增歌曲'" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="歌曲名" prop="title"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="歌手" prop="artist"><el-input v-model="form.artist" /></el-form-item>
        <el-form-item label="专辑"><el-input v-model="form.album" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" placeholder="请选择分类" clearable style="width: 100%">
            <el-option label="" value="" />
            <el-option label="流行" value="流行" />
            <el-option label="摇滚" value="摇滚" />
            <el-option label="古典" value="古典" />
            <el-option label="民谣" value="民谣" />
            <el-option label="电子" value="电子" />
          </el-select>
        </el-form-item>
        <el-form-item label="时长(秒)"><el-input v-model="form.duration" type="number" /></el-form-item>
        
        <el-form-item label="音频文件">
          <div class="upload-area">
            <el-upload
              :show-file-list="false"
              :before-upload="beforeAudioUpload"
              :http-request="handleAudioUpload"
              accept=".mp3,.wav,.flac,.aac,.ogg,.m4a"
            >
              <el-button type="primary">选择音频文件</el-button>
            </el-upload>
            <div v-if="form.audioUrl" class="file-info">
              <span>{{ audioFileName }}</span>
              <el-icon @click="form.audioUrl = ''" style="cursor: pointer; margin-left: 10px;"><Close /></el-icon>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="封面图片">
          <div class="upload-area">
            <el-upload
              :show-file-list="false"
              :before-upload="beforeCoverUpload"
              :http-request="handleCoverUpload"
              accept=".jpg,.jpeg,.png,.gif,.webp"
            >
              <el-button type="primary">选择封面图片</el-button>
            </el-upload>
            <el-image v-if="form.coverUrl" :src="form.coverUrl" style="width: 100px; height: 100px; margin-left: 10px" fit="cover" />
          </div>
        </el-form-item>
        
        <el-form-item label="歌词URL"><el-input v-model="form.lyricsUrl" placeholder="可选" /></el-form-item>
        <el-form-item label="VIP歌曲"><el-switch v-model="form.isVip" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 歌词编辑对话框 -->
    <el-dialog v-model="lyricsDialogVisible" title="歌词管理" width="800px">
      <div class="lyrics-manager">
        <!-- 搜索歌词 -->
        <div class="lyrics-search">
          <el-input 
            v-model="lyricsSearchKeyword" 
            placeholder="输入歌曲名搜索歌词" 
            style="width: 300px; margin-right: 10px"
            @keyup.enter="searchOnlineLyrics"
          />
          <el-button type="primary" @click="searchOnlineLyrics" :loading="searching">搜索歌词</el-button>
        </div>
        
        <!-- 搜索结果 -->
        <div v-if="lyricsSearchResults.length > 0" class="lyrics-results">
          <div class="results-header">搜索结果：</div>
          <div 
            v-for="item in lyricsSearchResults" 
            :key="item.id" 
            class="result-item"
            @click="selectLyrics(item)"
          >
            <div class="song-name">{{ item.name }}</div>
            <div class="song-info">{{ item.artist }} - {{ item.album }}</div>
          </div>
        </div>
        
        <!-- 歌词编辑器 -->
        <div class="lyrics-editor">
          <div class="editor-header">
            <span>歌词内容</span>
            <el-button size="small" @click="formatLyrics">格式化歌词</el-button>
          </div>
          <el-input
            v-model="lyricsContent"
            type="textarea"
            :rows="15"
            placeholder="请输入或粘贴歌词内容，格式：[00:00.00]歌词内容"
          />
        </div>
      </div>
      <template #footer>
        <el-button @click="lyricsDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveLyrics" :loading="savingLyrics">保存歌词</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { getSongList, addSong, updateSong, deleteSong, getLyrics, updateLyrics, searchLyrics, fetchLyrics } from '@/api/song'
import { uploadAudio, uploadCover } from '@/api/file'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Close } from '@element-plus/icons-vue'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const keyword = ref('')
const categoryFilter = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({ 
  id: null, 
  title: '', 
  artist: '', 
  album: '', 
  category: '', 
  duration: null, 
  audioUrl: '', 
  coverUrl: '', 
  lyricsUrl: '', 
  isVip: 0 
})

const audioFileName = computed(() => {
  if (form.audioUrl) {
    const parts = form.audioUrl.split('/')
    return parts[parts.length - 1]
  }
  return ''
})

const rules = { 
  title: [{ required: true, message: '请输入歌曲名', trigger: 'blur' }], 
  artist: [{ required: true, message: '请输入歌手', trigger: 'blur' }],
  audioUrl: [{ required: true, message: '请上传音频文件', trigger: 'change' }]
}

// 歌词相关
const lyricsDialogVisible = ref(false)
const currentSongId = ref(null)
const lyricsContent = ref('')
const lyricsSearchKeyword = ref('')
const lyricsSearchResults = ref([])
const searching = ref(false)
const savingLyrics = ref(false)

const loadData = async () => {
 loading.value = true
 try {
    const res = await getSongList({ 
      pageNum: pageNum.value, 
      pageSize: pageSize.value, 
      keyword: keyword.value,
      category: categoryFilter.value 
    })
   tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { 
    id: null, 
    title: '', 
    artist: '', 
    album: '', 
    category: '', 
    duration: null, 
    audioUrl: '', 
    coverUrl: '', 
    lyricsUrl: '', 
    isVip: 0 
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleLyrics = async (row) => {
  currentSongId.value = row.id
  lyricsSearchKeyword.value = `${row.title} ${row.artist}`
  lyricsSearchResults.value = []
  
  // 加载现有歌词
  try {
    const res = await getLyrics(row.id)
    lyricsContent.value = res.data || ''
  } catch (e) {
    lyricsContent.value = ''
  }
  
  lyricsDialogVisible.value = true
}

const searchOnlineLyrics = async () => {
  if (!lyricsSearchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  
  searching.value = true
  try {
    const res = await searchLyrics(lyricsSearchKeyword.value)
    lyricsSearchResults.value = res.data || []
    if (lyricsSearchResults.value.length === 0) {
      ElMessage.info('未找到相关歌词')
    }
  } catch (e) {
    ElMessage.error('搜索失败')
  } finally {
    searching.value = false
  }
}

const selectLyrics = async (item) => {
  try {
    const res = await fetchLyrics(item.id)
    if (res.data) {
      lyricsContent.value = res.data
      ElMessage.success('歌词获取成功')
    } else {
      ElMessage.warning('该歌曲暂无歌词')
    }
  } catch (e) {
    ElMessage.error('获取歌词失败')
  }
}

const formatLyrics = () => {
  if (!lyricsContent.value) return
  
  // 简单的歌词格式化：确保时间标签格式正确
  const lines = lyricsContent.value.split('\n')
  const formatted = lines.map(line => {
    line = line.trim()
    if (!line) return line
    
    // 如果已经有时间标签，保持不变
    if (/\[\d{2}:\d{2}/.test(line)) return line
    
    // 如果没有时间标签，添加默认标签
    return `[00:00.00]${line}`
  }).join('\n')
  
  lyricsContent.value = formatted
  ElMessage.success('格式化完成')
}

const saveLyrics = async () => {
  savingLyrics.value = true
  try {
    await updateLyrics(currentSongId.value, lyricsContent.value)
    ElMessage.success('歌词保存成功')
    lyricsDialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    savingLyrics.value = false
  }
}

const beforeAudioUpload = (file) => {
  const allowedTypes = ['audio/mpeg', 'audio/wav', 'audio/flac', 'audio/aac', 'audio/ogg', 'audio/mp4', 'audio/x-m4a']
  const isAudio = allowedTypes.includes(file.type) || file.name.match(/\.(mp3|wav|flac|aac|ogg|m4a)$/i)
  if (!isAudio) {
    ElMessage.error('只能上传音频文件 (mp3, wav, flac, aac, ogg, m4a)')
    return false
  }
  const isLt50M = file.size / 1024 / 1024 < 50
  if (!isLt50M) {
    ElMessage.error('音频文件大小不能超过 50MB')
    return false
  }
  return true
}

const beforeCoverUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}

const handleAudioUpload = async (options) => {
  try {
    ElMessage.info('正在上传音频文件...')
    const res = await uploadAudio(options.file)
    form.audioUrl = res.data
    ElMessage.success('音频上传成功')
  } catch (error) {
    ElMessage.error('音频上传失败')
  }
}

const handleCoverUpload = async (options) => {
  try {
    ElMessage.info('正在上传封面图片...')
    const res = await uploadCover(options.file)
    form.coverUrl = res.data
    ElMessage.success('封面上传成功')
  } catch (error) {
    ElMessage.error('封面上传失败')
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (!form.audioUrl) {
    ElMessage.error('请上传音频文件')
    return
  }
  submitting.value = true
  try {
    if (isEdit.value) { 
      await updateSong(form) 
    } else { 
      await addSong(form) 
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该歌曲？', '提示', { type: 'warning' })
  await deleteSong(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => loadData())
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.upload-area { display: flex; align-items: center; }
.file-info { 
  margin-left: 10px; 
  padding: 5px 10px; 
  background: #f0f9ff; 
  border-radius: 4px; 
  display: flex; 
  align-items: center;
  font-size: 12px;
  color: #409eff;
}

.lyrics-manager {
  padding: 10px 0;
}

.lyrics-search {
  margin-bottom: 20px;
}

.lyrics-results {
  margin-bottom: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  max-height: 200px;
  overflow-y: auto;
}

.results-header {
  padding: 10px;
  background: #f5f7fa;
  font-weight: bold;
  font-size: 14px;
}

.result-item {
  padding: 10px 15px;
  border-bottom: 1px solid #e4e7ed;
  cursor: pointer;
  transition: background 0.3s;
}

.result-item:hover {
  background: #f0f9ff;
}

.result-item:last-child {
  border-bottom: none;
}

.song-name {
  font-size: 14px;
  color: #303133;
}

.song-info {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.lyrics-editor {
 margin-top: 15px;
}
 </style>
