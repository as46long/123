<template>
  <div class="song-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>歌曲列表</span>
          <div>
            <el-input v-model="keyword" placeholder="搜索歌曲名/歌手" style="width: 200px; margin-right: 10px" @keyup.enter="loadData" clearable />
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
        <el-table-column label="VIP">
          <template #default="{ row }">
            <el-tag v-if="row.isVip" type="warning">VIP</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑歌曲' : '新增歌曲'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="歌曲名" prop="title"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="歌手" prop="artist"><el-input v-model="form.artist" /></el-form-item>
        <el-form-item label="专辑"><el-input v-model="form.album" /></el-form-item>
        <el-form-item label="分类"><el-input v-model="form.category" /></el-form-item>
        <el-form-item label="时长"><el-input v-model="form.duration" type="number" /></el-form-item>
        <el-form-item label="音频URL"><el-input v-model="form.audioUrl" /></el-form-item>
        <el-form-item label="封面URL"><el-input v-model="form.coverUrl" /></el-form-item>
        <el-form-item label="歌词URL"><el-input v-model="form.lyricsUrl" /></el-form-item>
        <el-form-item label="VIP歌曲"><el-switch v-model="form.isVip" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getSongList, addSong, updateSong, deleteSong } from '@/api/song'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const keyword = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({ id: null, title: '', artist: '', album: '', category: '', duration: null, audioUrl: '', coverUrl: '', lyricsUrl: '', isVip: 0 })
const rules = { title: [{ required: true, message: '请输入歌曲名', trigger: 'blur' }], artist: [{ required: true, message: '请输入歌手', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await getSongList({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, title: '', artist: '', album: '', category: '', duration: null, audioUrl: '', coverUrl: '', lyricsUrl: '', isVip: 0 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (isEdit.value) { await updateSong(form) } else { await addSong(form) }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  loadData()
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
</style>
