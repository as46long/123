<template>
  <div class="order-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单列表</span>
          <div>
            <el-select v-model="payStatus" placeholder="支付状态" clearable style="width: 120px; margin-right: 10px">
              <el-option label="未支付" :value="0" />
              <el-option label="已支付" :value="1" />
            </el-select>
            <el-select v-model="packageType" placeholder="套餐类型" clearable style="width: 120px; margin-right: 10px">
              <el-option label="周卡" value="WEEK" />
              <el-option label="月卡" value="MONTH" />
              <el-option label="季卡" value="QUARTER" />
              <el-option label="年卡" value="YEAR" />
            </el-select>
            <el-button type="primary" @click="loadData">搜索</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="username" label="用户" />
        <el-table-column prop="packageType" label="套餐">
          <template #default="{ row }">
            {{ row.packageType === 'WEEK' ? '周卡' : row.packageType === 'MONTH' ? '月卡' : row.packageType === 'QUARTER' ? '季卡' : '年卡' }}
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额">
          <template #default="{ row }">¥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column label="支付状态">
          <template #default="{ row }">
            <el-tag :type="row.payStatus === 1 ? 'success' : 'info'">{{ row.payStatus === 1 ? '已支付' : '未支付' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payTime" label="支付时间" />
        <el-table-column prop="expireTime" label="到期时间" />
        <el-table-column prop="createTime" label="创建时间" />
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
import { getOrderList } from '@/api/order'

const loading = ref(false)
const tableData = ref([])
const payStatus = ref(null)
const packageType = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOrderList({ pageNum: pageNum.value, pageSize: pageSize.value, payStatus: payStatus.value, packageType: packageType.value })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

onMounted(() => loadData())
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
