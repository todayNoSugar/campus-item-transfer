<template>
  <div class="page-container" v-loading="loading">
    <template v-if="goods">
      <el-breadcrumb separator="/" class="mb-16">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>{{ goods.categoryName || '物品' }}</el-breadcrumb-item>
        <el-breadcrumb-item>{{ goods.title }}</el-breadcrumb-item>
      </el-breadcrumb>

      <div class="detail-main">
        <div class="detail-images">
          <el-image v-if="imageList.length" :src="currentImage" :preview-src-list="imageList" fit="cover" class="main-image" />
          <div v-else class="no-image"><el-icon :size="48"><Picture /></el-icon><span>暂无图片</span></div>
          <div v-if="imageList.length > 1" class="thumb-list">
            <div v-for="(img, i) in imageList" :key="i" class="thumb" :class="{ active: img === currentImage }" @click="currentImage = img">
              <el-image :src="img" fit="cover" />
            </div>
          </div>
        </div>

        <div class="detail-info">
          <div class="info-head">
            <el-tag size="small" :type="goods.type === 0 ? 'warning' : 'primary'">
              {{ goods.type === 0 ? '标价转让' : '以物换物' }}
            </el-tag>
            <el-tag size="small" type="info" effect="plain">{{ goods.categoryName }}</el-tag>
          </div>

          <h1 class="goods-title">{{ goods.title }}</h1>

          <div class="price-box">
            <template v-if="goods.type === 0">
              <span class="price-label">期望价格</span>
              <span class="price big-price">¥{{ Number(goods.price).toFixed(2) }}</span>
            </template>
            <template v-else>
              <span class="swap-big">以物换物 · 不花钱</span>
            </template>
          </div>

          <div class="meta-list">
            <div class="meta-row">
              <span class="meta-key"><el-icon><LocationInformation /></el-icon>交易地点</span>
              <span class="meta-val">{{ goods.location }}</span>
            </div>
            <div class="meta-row">
              <span class="meta-key"><el-icon><Clock /></el-icon>发布时间</span>
              <span class="meta-val">{{ formatTime(goods.createTime) }}</span>
            </div>
          </div>

          <div class="owner-box">
            <AvatarBox :src="goods.ownerAvatar" :name="goods.ownerNickname" :size="42" />
            <div class="owner-info">
              <div class="owner-name">{{ goods.ownerNickname }}</div>
              <div class="owner-sub">发布于 {{ formatTime(goods.createTime) }}</div>
            </div>
          </div>

          <div v-if="isOwner" class="action-row">
            <el-button type="primary" :icon="Edit" @click="$router.push(`/publish?id=${goods.id}`)">编辑物品</el-button>
            <el-button @click="$router.push('/my/goods')">去我的发布管理</el-button>
          </div>
        </div>
      </div>

      <div class="section-card mt-16">
        <h3 class="section-title">物品描述</h3>
        <p class="desc-text">{{ goods.description || '发布者暂未填写描述' }}</p>
      </div>

      <div class="section-card mt-16">
        <h3 class="section-title">留言咨询（{{ messages.length }}）</h3>
        <div v-if="store.isLogin" class="msg-input">
          <el-input v-model="newMessage" type="textarea" :rows="2" :maxlength="500" show-word-limit placeholder="对物品有什么想了解的？留言给发布者..." />
          <el-button type="primary" class="msg-submit" :loading="msgSubmitting" @click="submitMessage">发表留言</el-button>
        </div>
        <div v-else class="msg-login-tip">
          <el-button type="primary" link @click="$router.push('/login')">登录后参与留言</el-button>
        </div>

        <div v-if="messages.length" class="msg-list">
          <div v-for="m in messages" :key="m.id" class="msg-item">
            <AvatarBox :src="m.avatar" :name="m.nickname" :size="36" />
            <div class="msg-body">
              <div class="msg-head">
                <span class="msg-user">{{ m.nickname }}</span>
                <span class="msg-time">{{ formatTime(m.createTime) }}</span>
              </div>
              <div class="msg-content">{{ m.content }}</div>
              <div v-if="m.reply" class="msg-reply">
                <span class="reply-label">发布者回复：</span>{{ m.reply }}
              </div>
            </div>
          </div>
        </div>
        <el-empty v-else description="还没有留言，来抢沙发~" :image-size="70" />
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Edit, LocationInformation, Clock, Picture } from '@element-plus/icons-vue'
import { getGoodsDetail, getMessages, addMessage } from '../api'
import { useUserStore } from '../store/user'
import { formatTime } from '../utils/format'
import AvatarBox from '../components/AvatarBox.vue'

const route = useRoute()
const store = useUserStore()

const goodsId = route.params.id
const goods = ref(null)
const loading = ref(true)

const currentImage = ref('')
const imageList = computed(() => {
  if (!goods.value?.images) return []
  return goods.value.images.split(',').filter(Boolean)
})

const isOwner = computed(() => store.isLogin && goods.value && goods.value.userId === store.userInfo?.id)

const messages = ref([])
const newMessage = ref('')
const msgSubmitting = ref(false)

async function loadDetail() {
  const res = await getGoodsDetail(goodsId)
  goods.value = res.data
  if (imageList.value.length) {
    currentImage.value = imageList.value[0]
  }
}

async function loadMessages() {
  const res = await getMessages(goodsId)
  messages.value = res.data
}

async function submitMessage() {
  if (!newMessage.value.trim()) {
    ElMessage.warning('请输入留言内容')
    return
  }
  msgSubmitting.value = true
  try {
    await addMessage(goodsId, newMessage.value.trim())
    newMessage.value = ''
    ElMessage.success('留言成功')
    await loadMessages()
  } finally {
    msgSubmitting.value = false
  }
}

onMounted(async () => {
  try {
    await loadDetail()
    await loadMessages()
  } finally {
    loading.value = false
  }
})

watch(() => route.params.id, () => {
  loadDetail()
  loadMessages()
})
</script>

<style scoped>
.detail-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
}
.detail-images { min-width: 0; }
.main-image { width: 100%; aspect-ratio: 4 / 3; border-radius: 10px; }
.no-image {
  height: 320px;
  background: #f0f2f5;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #a8abb2;
}
.thumb-list { display: flex; gap: 8px; margin-top: 10px; overflow-x: auto; }
.thumb {
  width: 72px; height: 54px;
  border-radius: 6px;
  overflow: hidden;
  border: 2px solid transparent;
  cursor: pointer;
  flex-shrink: 0;
}
.thumb.active { border-color: #1f6feb; }
.thumb :deep(.el-image) { width: 100%; height: 100%; }
.detail-info { display: flex; flex-direction: column; }
.info-head { display: flex; gap: 8px; margin-bottom: 10px; }
.goods-title { font-size: 22px; color: #303133; line-height: 1.4; }
.price-box {
  margin: 14px 0;
  background: linear-gradient(90deg, #fff7f0, #fff);
  border-radius: 10px;
  padding: 12px 16px;
}
.price-label { font-size: 13px; color: #909399; margin-right: 10px; }
.big-price { font-size: 26px; font-weight: 700; }
.swap-big { font-size: 17px; color: #409eff; font-weight: 600; }
.meta-list { border-top: 1px dashed #ebeef5; border-bottom: 1px dashed #ebeef5; padding: 10px 0; }
.meta-row { display: flex; align-items: center; padding: 6px 0; font-size: 14px; }
.meta-key { width: 100px; color: #909399; display: inline-flex; align-items: center; gap: 4px; }
.meta-val { color: #303133; }
.owner-box { display: flex; align-items: center; gap: 12px; margin: 14px 0; }
.owner-name { font-size: 15px; font-weight: 600; }
.owner-sub { font-size: 12px; color: #909399; margin-top: 2px; }
.action-row { display: flex; gap: 10px; margin-top: 4px; flex-wrap: wrap; }
.section-card { background: #fff; border-radius: 12px; padding: 18px 20px; }
.section-title { font-size: 16px; color: #303133; margin-bottom: 12px; }
.desc-text { color: #606266; line-height: 1.8; white-space: pre-wrap; font-size: 14px; }
.msg-input { display: flex; gap: 10px; align-items: flex-start; }
.msg-input .el-input { flex: 1; }
.msg-submit { margin-top: 2px; }
.msg-login-tip { background: #f5f7fa; border-radius: 8px; padding: 12px; text-align: center; }
.msg-list { margin-top: 14px; }
.msg-item { display: flex; gap: 12px; padding: 12px 0; border-bottom: 1px solid #f5f7fa; }
.msg-body { flex: 1; min-width: 0; }
.msg-head { display: flex; align-items: center; gap: 10px; }
.msg-user { font-size: 14px; font-weight: 600; color: #303133; }
.msg-time { font-size: 12px; color: #a8abb2; }
.msg-content { font-size: 14px; color: #606266; margin-top: 6px; line-height: 1.6; }
.msg-reply { margin-top: 8px; background: #f0f7ff; border-radius: 8px; padding: 8px 12px; font-size: 13px; color: #409eff; line-height: 1.6; }
.reply-label { font-weight: 600; }

@media (max-width: 768px) {
  .detail-main { grid-template-columns: 1fr; padding: 12px; gap: 14px; }
  .goods-title { font-size: 18px; }
  .big-price { font-size: 22px; }
}
</style>
