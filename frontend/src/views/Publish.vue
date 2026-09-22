<template>
  <div class="page-container">
    <div class="publish-card">
      <h2 class="publish-title">{{ editId ? '编辑物品' : '发布闲置物品' }}</h2>
      <p class="publish-sub">发布后需管理员审核通过，前台才会展示</p>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" class="mt-16">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" maxlength="100" show-word-limit placeholder="如：九成新高数教材，转让" />
        </el-form-item>

        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="流转类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio-button :value="0">标价转让</el-radio-button>
            <el-radio-button :value="1">以物换物（不花钱）</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="form.type === 0" label="期望价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="5" placeholder="元" style="width: 200px" />
          <span class="price-unit">元</span>
        </el-form-item>

        <el-form-item label="交易地点" prop="location">
          <el-input v-model="form.location" maxlength="100" placeholder="如：图书馆一楼大厅 / 三食堂门口" />
        </el-form-item>

        <el-form-item label="物品图片">
          <el-upload
            v-model:file-list="fileList"
            list-type="picture-card"
            action="/api/user/upload"
            :headers="uploadHeaders"
            accept="image/*"
            :limit="6"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :on-remove="handleRemove"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">最多上传 6 张，支持手机拍照/相册上传，单张不超过 10MB</div>
        </el-form-item>

        <el-form-item label="详细描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="5"
            maxlength="2000"
            show-word-limit
            placeholder="描述物品新旧程度、入手渠道、转让/交换意向物品等..."
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" :loading="submitting" @click="submit">
            {{ editId ? '保存修改' : '提交发布' }}
          </el-button>
          <el-button size="large" @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '../store/user'
import { getCategories, createGoods, updateGoods, getGoodsDetail } from '../api'

const route = useRoute()
const router = useRouter()

const editId = route.query.id ? Number(route.query.id) : null
const categories = ref([])
const formRef = ref()
const submitting = ref(false)

const form = reactive({
  title: '',
  categoryId: null,
  type: 0,
  price: null,
  location: '',
  description: ''
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  type: [{ required: true, message: '请选择流转类型', trigger: 'change' }],
  price: [
    {
      validator: (rule, value, callback) => {
        if (form.type === 0 && (value === null || value === undefined)) {
          callback(new Error('标价转让请填写期望价格'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],
  location: [{ required: true, message: '请填写期望交易地点', trigger: 'blur' }]
}

const store = useUserStore()
const uploadHeaders = computed(() => ({ token: store.token || '' }))
const fileList = ref([])
const imageUrls = ref([])

function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (file.size / 1024 / 1024 > 10) {
    ElMessage.error('图片大小不能超过 10MB')
    return false
  }
  return true
}

function handleUploadSuccess(response, file) {
  if (response.code === 200) {
    imageUrls.value.push(response.data)
  } else {
    ElMessage.error(response.msg || '上传失败')
    fileList.value = fileList.value.filter((f) => f.uid !== file.uid)
  }
}

function handleUploadError() {
  ElMessage.error('图片上传失败，请重试')
}

function handleRemove(file) {
  const url = file.response?.data || file.url
  if (url) {
    imageUrls.value = imageUrls.value.filter((u) => u !== url)
  }
}

async function loadEditData() {
  const res = await getGoodsDetail(editId)
  const g = res.data
  form.title = g.title
  form.categoryId = g.categoryId
  form.type = g.type
  form.price = g.price === null ? null : Number(g.price)
  form.location = g.location
  form.description = g.description || ''
  if (g.images) {
    imageUrls.value = g.images.split(',').filter(Boolean)
    fileList.value = imageUrls.value.map((url, i) => ({ name: `img-${i}`, url }))
  }
}

async function submit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    const payload = {
      ...form,
      images: imageUrls.value.join(',')
    }
    if (editId) {
      await updateGoods(editId, payload)
      ElMessage.success('保存成功')
    } else {
      await createGoods(payload)
      ElMessage.success('发布成功，等待管理员审核')
    }
    router.push('/my/goods')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  const res = await getCategories()
  categories.value = res.data
  if (editId) {
    await loadEditData()
  }
})
</script>

<style scoped>
.publish-card {
  max-width: 760px;
  margin: 0 auto;
  background: #fff;
  border-radius: 12px;
  padding: 24px 28px;
}
.publish-title {
  font-size: 20px;
  color: #303133;
}
.publish-sub {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}
.price-unit {
  margin-left: 8px;
  color: #909399;
}
.upload-tip {
  font-size: 12px;
  color: #a8abb2;
  margin-top: 6px;
  line-height: 1.6;
}
@media (max-width: 768px) {
  .publish-card {
    padding: 16px 14px;
  }
}
</style>
