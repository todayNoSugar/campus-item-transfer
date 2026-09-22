<template>
  <div class="page-container">
    <div class="page-head">
      <h2>物品审核</h2>
      <div class="head-right">
        <el-radio-group v-model="statusFilter" size="small" @change="onFilterChange">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="0">待审核</el-radio-button>
          <el-radio-button value="1">上架中</el-radio-button>
          <el-radio-button value="2">已成交</el-radio-button>
          <el-radio-button value="3">已下架</el-radio-button>
        </el-radio-group>
        <el-input
          v-model="keyword"
          placeholder="搜索标题"
          clearable
          style="width: 200px"
          :prefix-icon="Search"
          @keyup.enter="onSearch"
          @clear="onSearch"
        />
      </div>
    </div>

    <div class="table-card" v-loading="loading">
      <el-table :data="list" style="width: 100%">
        <el-table-column label="物品" min-width="230">
          <template #default="{ row }">
            <div class="goods-cell" @click="$router.push(`/goods/${row.id}`)">
              <el-image v-if="row.images?.split(',')[0]" :src="row.images.split(',')[0]" fit="cover" class="cell-img" />
              <div v-else class="cell-img cell-img-empty"><el-icon><Picture /></el-icon></div>
              <div class="cell-info">
                <div class="cell-title text-ellipsis">{{ row.title }}</div>
                <div class="cell-sub">{{ row.categoryName }} · {{ row.ownerNickname }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="类型/价格" width="110">
          <template #default="{ row }">
            <div v-if="row.type === 0" class="price">¥{{ Number(row.price).toFixed(2) }}</div>
            <div v-else class="swap-text">以物换物</div>
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
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" link type="success" size="small" @click="audit(row, 1)">通过</el-button>
            <el-button v-if="row.status === 0" link type="danger" size="small" @click="audit(row, 3)">拒绝</el-button>
            <el-button v-if="row.status === 1" link type="danger" size="small" @click="offline(row)">下架</el-button>
            <el-button link type="primary" size="small" @click="$router.push(`/goods/${row.id}`)">查看</el-button>
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Picture } from '@element-plus/icons-vue'
import { adminGetGoods, adminAuditGoods, adminOfflineGoods } from '../../api'
import { formatTime, GOODS_STATUS } from '../../utils/format'

const statusFilter = ref('')
const keyword = ref('')
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)

function statusMeta(status) {
  return GOODS_STATUS[status] || { text: '未知', type: 'info' }
}

async function fetchList() {
  loading.value = true
  try {
    const res = await adminGetGoods({
      page: page.value,
      size: size.value,
      status: statusFilter.value === '' ? undefined : Number(statusFilter.value),
      keyword: keyword.value || undefined
    })
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function onFilterChange() {
  page.value = 1
  fetchList()
}

function onSearch() {
  page.value = 1
  fetchList()
}

async function audit(row, status) {
  const action = status === 1 ? '通过' : '拒绝'
  await ElMessageBox.confirm(`确定${action}“${row.title}”的审核吗？`, '提示', { type: 'warning' })
  await adminAuditGoods(row.id, status)
  ElMessage.success(`已${action}`)
  fetchList()
}

async function offline(row) {
  await ElMessageBox.confirm(`确定下架“${row.title}”吗？前台将不再展示`, '提示', { type: 'warning' })
  await adminOfflineGoods(row.id)
  ElMessage.success('已下架')
  fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
.page-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 10px;
}
.page-head h2 {
  font-size: 20px;
  color: #303133;
}
.head-right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
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
  cursor: pointer;
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
  max-width: 180px;
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
