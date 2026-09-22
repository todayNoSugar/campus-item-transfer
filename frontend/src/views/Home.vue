<template>
  <div class="page-container">
    <!-- 顶部横幅 -->
    <div class="home-banner">
      <div>
        <h1>校园闲置物品流转平台</h1>
        <p>让闲置流动起来 —— 转让、以物换物，线下当面交易更安心</p>
      </div>
      <el-button type="primary" round size="large" :icon="Plus" @click="$router.push(store.isLogin ? '/publish' : '/login')">
        发布闲置
      </el-button>
    </div>

    <!-- 筛选区 -->
    <div class="filter-bar">
      <div class="filter-row">
        <span class="filter-label">分类</span>
        <div class="cat-tabs">
          <span
            class="cat-tab"
            :class="{ active: activeCategory === null }"
            @click="onCategoryChange(null)"
          >全部</span>
          <span
            v-for="c in categories"
            :key="c.id"
            class="cat-tab"
            :class="{ active: activeCategory === c.id }"
            @click="onCategoryChange(c.id)"
          >{{ c.name }}</span>
        </div>
      </div>
      <div class="filter-row">
        <span class="filter-label">类型</span>
        <el-radio-group v-model="activeType" size="small" @change="onTypeChange">
          <el-radio-button value="all">全部</el-radio-button>
          <el-radio-button value="0">标价转让</el-radio-button>
          <el-radio-button value="1">以物换物</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 物品列表 -->
    <div v-loading="loading" class="mt-16">
      <div class="list-head flex-between">
        <span class="list-title">
          {{ keyword ? `“${keyword}” 的搜索结果` : activeCategoryName || '最新闲置' }}
          <span class="list-count">共 {{ total }} 件</span>
        </span>
      </div>

      <div v-if="list.length" class="goods-grid mt-16">
        <GoodsCard v-for="g in list" :key="g.id" :goods="g" />
      </div>

      <el-empty v-else-if="!loading" description="暂无相关物品，快来发布第一件闲置吧">
        <el-button type="primary" @click="$router.push(store.isLogin ? '/publish' : '/login')">去发布</el-button>
      </el-empty>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[12, 24, 48]"
          layout="total, sizes, prev, pager, next"
          background
          small
          @current-change="fetchList()"
          @size-change="onSizeChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { Refresh, Plus } from '@element-plus/icons-vue'
import { getGoodsList, getCategories } from '../api'
import { useUserStore } from '../store/user'
import GoodsCard from '../components/GoodsCard.vue'

const route = useRoute()
const store = useUserStore()

const categories = ref([])
const activeCategory = ref(null)
const activeType = ref('all')
const keyword = ref('')
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(12)
const loading = ref(false)

const activeCategoryName = computed(() => {
  const c = categories.value.find((x) => x.id === activeCategory.value)
  return c ? c.name : ''
})

async function fetchList(silent = false) {
  if (!silent) loading.value = true
  try {
    const res = await getGoodsList({
      page: page.value,
      size: size.value,
      keyword: keyword.value || undefined,
      categoryId: activeCategory.value ?? undefined,
      type: activeType.value === 'all' ? undefined : Number(activeType.value)
    })
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    if (!silent) loading.value = false
  }
}

function onCategoryChange(id) {
  activeCategory.value = id
  page.value = 1
  fetchList()
}

function onTypeChange() {
  page.value = 1
  fetchList()
}

function onSizeChange() {
  page.value = 1
  fetchList()
}

onMounted(async () => {
  const res = await getCategories()
  categories.value = res.data
  keyword.value = route.query.keyword || ''
  await fetchList()
})
</script>

<style scoped>
.home-banner {
  background: linear-gradient(120deg, #1f6feb 0%, #38bdf8 100%);
  border-radius: 14px;
  color: #fff;
  padding: 28px 28px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}
.home-banner h1 {
  font-size: 24px;
  margin-bottom: 8px;
}
.home-banner p {
  font-size: 14px;
  opacity: 0.92;
}
.filter-bar {
  background: #fff;
  border-radius: 10px;
  padding: 14px 16px;
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.filter-row {
  display: flex;
  align-items: center;
  gap: 12px;
}
.filter-label {
  font-size: 13px;
  color: #909399;
  flex-shrink: 0;
}
.cat-tabs {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 2px;
}
.cat-tab {
  flex-shrink: 0;
  padding: 4px 14px;
  border-radius: 16px;
  font-size: 13px;
  color: #606266;
  background: #f5f7fa;
  cursor: pointer;
  transition: all 0.2s;
}
.cat-tab:hover {
  color: #1f6feb;
}
.cat-tab.active {
  background: #1f6feb;
  color: #fff;
}
.list-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.list-count {
  font-size: 13px;
  font-weight: 400;
  color: #909399;
  margin-left: 6px;
}
.sync-tip {
  font-size: 12px;
  color: #909399;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.sync-tip .el-icon {
  animation: spin 4s linear infinite;
}
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
@media (max-width: 768px) {
  .home-banner {
    flex-direction: column;
    align-items: flex-start;
    padding: 20px;
  }
  .home-banner h1 {
    font-size: 19px;
  }
  .home-banner p {
    font-size: 13px;
  }
  .filter-label {
    display: none;
  }
}
</style>
