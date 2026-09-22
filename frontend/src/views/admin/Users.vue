<template>
  <div class="page-container">
    <div class="page-head">
      <h2>用户管理</h2>
      <el-input
        v-model="keyword"
        placeholder="搜索账号 / 昵称"
        clearable
        style="width: 240px"
        :prefix-icon="Search"
        @keyup.enter="onSearch"
        @clear="onSearch"
      />
    </div>

    <div class="table-card" v-loading="loading">
      <el-table :data="list" style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="account" label="账号" width="130" />
        <el-table-column label="昵称" min-width="130">
          <template #default="{ row }">
            <span class="user-cell">
              <AvatarBox :src="row.avatar" :name="row.nickname" :size="28" />
              {{ row.nickname }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="角色" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.role === 1" type="danger" size="small">管理员</el-tag>
            <el-tag v-else type="info" size="small">普通用户</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130">
          <template #default="{ row }">{{ row.phone || '—' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">
              {{ row.status === 0 ? '正常' : '已禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" width="160">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="110" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.role !== 1"
              link
              :type="row.status === 0 ? 'danger' : 'success'"
              size="small"
              @click="toggleStatus(row)"
            >{{ row.status === 0 ? '禁用' : '启用' }}</el-button>
            <span v-else class="no-op">—</span>
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
import { adminGetUsers, adminUpdateUserStatus } from '../../api'
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
    const res = await adminGetUsers({
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

async function toggleStatus(row) {
  const action = row.status === 0 ? '禁用' : '启用'
  await ElMessageBox.confirm(`确定${action}用户“${row.nickname}”吗？`, '提示', { type: 'warning' })
  await adminUpdateUserStatus(row.id, row.status === 0 ? 1 : 0)
  ElMessage.success(`已${action}`)
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
.user-cell {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
.no-op {
  color: #c0c4cc;
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
