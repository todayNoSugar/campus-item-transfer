<template>
  <div class="page-container">
    <div class="page-head">
      <h2>我的收藏</h2>
      <span class="count-tip">共 {{ list.length }} 件</span>
    </div>

    <div v-loading="loading">
      <div v-if="list.length" class="goods-grid mt-16">
        <GoodsCard v-for="g in list" :key="g.id" :goods="g" />
      </div>
      <el-empty v-else-if="!loading" description="还没有收藏任何物品">
        <el-button type="primary" @click="$router.push('/')">去逛逛</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getFavorites } from '../api'
import GoodsCard from '../components/GoodsCard.vue'

const list = ref([])
const loading = ref(false)

async function fetchList() {
  loading.value = true
  try {
    const res = await getFavorites()
    list.value = res.data
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.page-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.page-head h2 {
  font-size: 20px;
  color: #303133;
}
.count-tip {
  font-size: 13px;
  color: #909399;
}
</style>
