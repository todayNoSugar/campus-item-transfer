<template>
  <div class="page-container">
    <div class="page-head">
      <h2>留言管理</h2>
      <el-input
        v-model="keyword"
        placeholder="搜索留言内容"
        clearable
        style="width: 220px"
        :prefix-icon="Search"
        @keyup.enter="onSearch"
        @clear="onSearch"
      />
    </div>

    <div class="table-card" v-loading="loading">
      <el-table :data="list" style="width: 100%">
        <el-table-column label="留言内容" min-width="260">
          <template #default="{ row }">
            <div class="msg-content">{{ row.content }}</div>
            <div v-if="row.reply" class="msg-reply">发布者回复：{{ row.reply }}</div>
          </template>
        </el-table-column>
        <el-table-column label="留言人" width="120">
          <template #default="{ row }">
            <span class="user-cell">
              <AvatarBox :src="row.avatar" :name="row.nickname" :size="26" />
              {{ row.nickname }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="留言时间" width="150">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="danger" size="small" @click="removeMessage(row)">删除</el-button>
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
import { Search } from '@element-plus/icons-vue'
import { adminGetMessages, adminDeleteMessage } from '../../api'
import { formatTime } from '../../utils/format'
import AvatarBox from '../../components/AvatarBox.vue'

const keyword = ref('')
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)

async function fetchList() {
  loading.value = true
  try {
    const res = await adminGetMessages({
      page: page.value,
      size: size.value,
      keyword: keyword.value || undefined
    })
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function onSearch() {
  page.value = 1
  fetchList()
}

async function removeMessage(row) {
  await ElMessageBox.confirm('确定删除这条留言吗？', '警告', { type: 'error' })
  await adminDeleteMessage(row.id)
  ElMessage.success('已删除')
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
.table-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
}
.msg-content {
  font-size: 14px;
  color: #303133;
}
.msg-reply {
  margin-top: 4px;
  font-size: 13px;
  color: #409eff;
  background: #f0f7ff;
  border-radius: 6px;
  padding: 4px 8px;
  display: inline-block;
}
.user-cell {
  display: inline-flex;
  align-items: center;
  gap: 6px;
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
