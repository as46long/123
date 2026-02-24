<template>
  <div class="comment-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>留言列表</span>
          <el-select v-model="status" placeholder="审核状态" clearable style="width: 120px" @change="loadData">
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户" />
        <el-table-column prop="content" label="留言内容" show-overflow-tooltip />
        <el-table-column prop="likes" label="点赞数" />
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'danger' : 'warning'">
              {{ row.status === 1 ? '已通过' : row.status === 2 ? '已拒绝' : '待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-if="row.status !== 1" type="success" size="small" @click="handleAudit(row, 1)">通过</el-button>
            <el-button v-if="row.status !== 2" type="warning" size="small" @click="handleAudit(row, 2)">拒绝</el-button>
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
import { getCommentList, auditComment, deleteComment } from '@/api/comment'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const status = ref(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCommentList({ pageNum: pageNum.value, pageSize: pageSize.value, status: status.value })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleAudit = async (row, status) => {
  await auditComment(row.id, status)
  ElMessage.success('审核成功')
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该留言？', '提示', { type: 'warning' })
  await deleteComment(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => loadData())
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
