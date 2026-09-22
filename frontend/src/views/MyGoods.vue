<template>
  <div class="page-container">
    <div class="page-head">
      <h2>我的发布</h2>
      <el-button type="primary" :icon="Plus" @click="$router.push('/publish')">发布新物品</el-button>
    </div>

    <div class="table-card" v-loading="loading">
      <el-table :data="list" style="width: 100%">
        <el-table-column label="物品" min-width="240">
          <template #default="{ row }">
            <div class="goods-cell">
              <el-image v-if="row.images?.split(',')[0]" :src="row.images.split(',')[0]" fit="cover" class="cell-img" />
              <div v-else class="cell-img cell-img-empty"><el-icon><Picture /></el-icon></div>
              <div class="cell-info">
                <div class="cell-title text-ellipsis">{{ row.title }}</div>
                <div class="cell-sub">
                  {{ row.categoryName }} · {{ row.type === 0 ? '标价转让' : '以物换物' }}
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="价格" width="100">
          <template #default="{ row }">
            <span v-if="row.type === 0" class="price">¥{{ Number(row.price).toFixed(2) }}</span>
            <span v-else class="swap-text">换物</span>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusMeta(row.status).type" size="small">{{ statusMeta(row.status).text }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="发布时间" width="150">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>

        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="$router.push(`/goods/${row.id}`)">查看</el-button>
            <el-button link type="primary" size="small" @click="$router.push(`/publish?id=${row.id}`)">编辑</el-button>
            <el-button
              v-if="row.status === 3"
              link
              type="success"
              size="small"
              @click="changeStatus(row, 1)"
            >重新上架</el-button>
            <el-button
              v-if="row.status === 1"
              link
              type="warning"
              size="small"
              @click="changeStatus(row, 3)"
            >下架</el-button>
            <el-button
              v-if="row.status === 1"
              link
              type="success"
              size="small"
              @click="changeStatus(row, 2)"
            >标记成交</el-button>
            <el-button link type="danger" size="small" @click="removeGoods(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          layout="total, prev, pager, next"
          background
          small
          @current-change="fetchList()"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Picture } from '@element-plus/icons-vue'
import { getMyGoods, updateGoodsStatus, deleteGoods } from '../api'
import { formatTime, GOODS_STATUS } from '../utils/format'

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)

let timer = null

function statusMeta(status) {
  return GOODS_STATUS[status] || { text: '未知', type: 'info' }
}

async function fetchList(silent = false) {
  if (!silent) loading.value = true
  try {
    const res = await getMyGoods({ page: page.value, size: size.value })
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    if (!silent) loading.value = false
  }
}

async function changeStatus(row, status) {
  const map = { 1: '重新上架', 2: '标记为已成交', 3: '下架' }
  await ElMessageBox.confirm(`确定${map[status]}“${row.title}”吗？`, '提示', { type: 'warning' })
  await updateGoodsStatus(row.id, status)
  ElMessage.success('操作成功')
  fetchList()
}

async function removeGoods(row) {
  await ElMessageBox.confirm(`删除后不可恢复，确定删除“${row.title}”吗？`, '警告', { type: 'error' })
  await deleteGoods(row.id)
  ElMessage.success('已删除')
  fetchList()
}

onMounted(() => {
  fetchList()
  timer = setInterval(() => fetchList(true), 30000)
})
onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.page-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.page-head h2 {
  font-size: 20px;
  color: #303133;
}
.table-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
}
.goods-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}
.cell-img {
  width: 56px;
  height: 42px;
  border-radius: 6px;
  flex-shrink: 0;
}
.cell-img-empty {
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #a8abb2;
}
.cell-title {
  font-size: 14px;
  color: #303133;
  max-width: 200px;
}
.cell-sub {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}
.swap-text {
  color: #409eff;
  font-size: 13px;
}
.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}
@media (max-width: 768px) {
  .table-card {
    padding: 8px;
    overflow-x: auto;
  }
}
</style>
