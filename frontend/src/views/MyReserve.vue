<template>
  <div class="page-container">
    <div class="page-head">
      <h2>我的预约</h2>
    </div>

    <el-tabs v-model="activeTab" class="mt-8">
      <el-tab-pane :label="`我发起的（${myList.length}）`" name="my">
        <div v-loading="loading">
          <el-empty v-if="!myList.length" description="还没有发起过预约" />
          <div v-else class="reserve-list">
            <div v-for="r in myList" :key="r.id" class="reserve-card">
              <el-image v-if="r.goodsImage" :src="r.goodsImage" fit="cover" class="reserve-img" @click="$router.push(`/goods/${r.goodsId}`)" />
              <div v-else class="reserve-img reserve-img-empty" @click="$router.push(`/goods/${r.goodsId}`)">
                <el-icon><Picture /></el-icon>
              </div>
              <div class="reserve-info">
                <div class="reserve-title text-ellipsis" @click="$router.push(`/goods/${r.goodsId}`)">{{ r.goodsTitle }}</div>
                <div class="reserve-line">
                  <el-icon><Calendar /></el-icon>期望时间：{{ formatTime(r.reserveTime) }}
                </div>
                <div class="reserve-line">
                  <el-icon><User /></el-icon>发布者：{{ r.ownerName }}
                </div>
              </div>
              <div class="reserve-right">
                <el-tag :type="statusMeta(r.status).type">{{ statusMeta(r.status).text }}</el-tag>
                <div class="reserve-time">{{ formatTime(r.createTime) }} 发起</div>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane :label="`别人预约我的（${toMeList.length}）`" name="to-me">
        <div v-loading="loading">
          <el-empty v-if="!toMeList.length" description="还没有人预约你的物品" />
          <div v-else class="reserve-list">
            <div v-for="r in toMeList" :key="r.id" class="reserve-card">
              <el-image v-if="r.goodsImage" :src="r.goodsImage" fit="cover" class="reserve-img" @click="$router.push(`/goods/${r.goodsId}`)" />
              <div v-else class="reserve-img reserve-img-empty" @click="$router.push(`/goods/${r.goodsId}`)">
                <el-icon><Picture /></el-icon>
              </div>
              <div class="reserve-info">
                <div class="reserve-title text-ellipsis" @click="$router.push(`/goods/${r.goodsId}`)">{{ r.goodsTitle }}</div>
                <div class="reserve-line">
                  <el-icon><User /></el-icon>预约人：{{ r.userName }}
                </div>
                <div class="reserve-line">
                  <el-icon><Calendar /></el-icon>期望时间：{{ formatTime(r.reserveTime) }}
                </div>
              </div>
              <div class="reserve-right">
                <el-tag :type="statusMeta(r.status).type">{{ statusMeta(r.status).text }}</el-tag>
                <div class="reserve-time">{{ formatTime(r.createTime) }} 发起</div>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Calendar, User, Picture } from '@element-plus/icons-vue'
import { getMyReserves, getToMeReserves } from '../api'
import { formatTime, RESERVE_STATUS } from '../utils/format'

const activeTab = ref('my')
const myList = ref([])
const toMeList = ref([])
const loading = ref(false)

function statusMeta(status) {
  return RESERVE_STATUS[status] || { text: '未知', type: 'info' }
}

async function fetchData() {
  loading.value = true
  try {
    const [my, toMe] = await Promise.all([getMyReserves(), getToMeReserves()])
    myList.value = my.data
    toMeList.value = toMe.data
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.page-head h2 { font-size: 20px; color: #303133; }
.reserve-list { display: flex; flex-direction: column; gap: 12px; }
.reserve-card {
  background: #fff;
  border-radius: 12px;
  padding: 14px;
  display: flex;
  gap: 14px;
  align-items: center;
}
.reserve-img { width: 90px; height: 68px; border-radius: 8px; cursor: pointer; flex-shrink: 0; }
.reserve-img-empty { background: #f0f2f5; display: flex; align-items: center; justify-content: center; color: #a8abb2; }
.reserve-info { flex: 1; min-width: 0; }
.reserve-title { font-size: 15px; font-weight: 600; color: #303133; cursor: pointer; }
.reserve-line { font-size: 13px; color: #606266; margin-top: 4px; display: flex; align-items: center; gap: 4px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }
.reserve-right { display: flex; flex-direction: column; align-items: flex-end; gap: 8px; flex-shrink: 0; }
.reserve-time { font-size: 12px; color: #a8abb2; }

@media (max-width: 768px) {
  .reserve-card { flex-wrap: wrap; }
  .reserve-right { width: 100%; flex-direction: row; align-items: center; justify-content: space-between; }
}
</style>
