<template>
  <div class="goods-card" @click="goDetail">
    <div class="card-img">
      <el-image v-if="firstImage" :src="firstImage" fit="cover" lazy>
        <template #error>
          <div class="img-placeholder"><el-icon><Picture /></el-icon></div>
        </template>
      </el-image>
      <div v-else class="img-placeholder">
        <el-icon><Picture /></el-icon>
        <span>暂无图片</span>
      </div>
      <span v-if="goods.status !== 1" class="badge badge-status">{{ statusText }}</span>
      <span class="badge badge-type" :class="goods.type === 0 ? 'badge-sell' : 'badge-swap'">
        {{ goods.type === 0 ? '标价转让' : '以物换物' }}
      </span>
    </div>
    <div class="card-body">
      <div class="card-title text-2line">{{ goods.title }}</div>
      <div class="card-price-row">
        <template v-if="goods.type === 0">
          <span class="price">¥{{ Number(goods.price).toFixed(2) }}</span>
        </template>
        <span v-else class="swap-text">以物换物 · 免费</span>
      </div>
      <div class="card-meta">
        <span class="meta-item"><el-icon><User /></el-icon>{{ goods.ownerNickname || '未知用户' }}</span>
        <span class="meta-item"><el-icon><LocationInformation /></el-icon>{{ goods.location || '校园内' }}</span>
      </div>
      <div class="card-meta card-meta-bottom">
        <span class="meta-tag">{{ goods.categoryName || '其他' }}</span>
        <span class="meta-time">{{ formatTime(goods.createTime) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { formatTime, goodsStatusText } from '../utils/format'

const props = defineProps({
  goods: { type: Object, required: true }
})

const router = useRouter()
const firstImage = computed(() => {
  const images = props.goods?.images
  if (!images) return ''
  return images.split(',')[0]
})
const statusText = computed(() => goodsStatusText(props.goods.status))

function goDetail() {
  router.push(`/goods/${props.goods.id}`)
}
</script>

<style scoped>
.goods-card {
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s, box-shadow 0.2s;
}
.goods-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}
.card-img {
  position: relative;
  aspect-ratio: 4 / 3;
  background: #f0f2f5;
}
.card-img :deep(.el-image),
.card-img :deep(.el-image img) {
  width: 100%;
  height: 100%;
}
.img-placeholder {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #a8abb2;
  font-size: 13px;
}
.img-placeholder .el-icon {
  font-size: 34px;
}
.badge {
  position: absolute;
  top: 8px;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: #fff;
}
.badge-status {
  left: 8px;
  background: rgba(0, 0, 0, 0.55);
}
.badge-type {
  right: 8px;
}
.badge-sell {
  background: #e6a23c;
}
.badge-swap {
  background: #409eff;
}
.card-body {
  padding: 10px 12px 12px;
}
.card-title {
  font-size: 14px;
  line-height: 20px;
  min-height: 40px;
  color: #303133;
}
.card-price-row {
  margin: 6px 0 4px;
  display: flex;
  align-items: center;
}
.price {
  font-size: 16px;
}
.swap-text {
  color: #409eff;
  font-size: 13px;
  font-weight: 500;
}
.card-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  max-width: 50%;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.meta-item .el-icon {
  font-size: 13px;
}
.card-meta-bottom {
  border-top: 1px dashed #ebeef5;
  padding-top: 6px;
  margin-top: 6px;
}
.meta-tag {
  background: #f0f2f5;
  color: #606266;
  padding: 1px 8px;
  border-radius: 10px;
}
.meta-time {
  font-size: 11px;
}
</style>
