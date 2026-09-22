<template>
  <div class="page-container">
    <div class="profile-card">
      <h2 class="profile-title">个人中心</h2>

      <!-- 头像 -->
      <div class="avatar-section">
        <el-upload
          class="avatar-uploader"
          action="/api/user/upload"
          :headers="uploadHeaders"
          :show-file-list="false"
          :before-upload="beforeAvatarUpload"
          :on-success="onAvatarSuccess"
          accept="image/*"
        >
          <div class="avatar-wrap">
            <AvatarBox :src="form.avatar" :name="form.nickname" :size="88" />
            <div class="avatar-mask"><el-icon><Camera /></el-icon>更换头像</div>
          </div>
        </el-upload>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="mt-16">
        <el-form-item label="账号">
          <el-input :model-value="userInfo?.account" disabled />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" maxlength="30" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" maxlength="20" placeholder="选填" />
        </el-form-item>
        <el-form-item label="注册时间">
          <el-input :model-value="formatTime(userInfo?.createTime)" disabled />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="saving" @click="save">保存修改</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Camera } from '@element-plus/icons-vue'
import { updateProfile } from '../api'
import { useUserStore } from '../store/user'
import { formatTime } from '../utils/format'
import AvatarBox from '../components/AvatarBox.vue'

const store = useUserStore()
const userInfo = store.userInfo

const form = reactive({
  nickname: userInfo?.nickname || '',
  phone: userInfo?.phone || '',
  avatar: userInfo?.avatar || ''
})

const formRef = ref()
const saving = ref(false)

const uploadHeaders = computed(() => ({ token: store.token || '' }))

const rules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
}

function beforeAvatarUpload(file) {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (file.size / 1024 / 1024 > 5) {
    ElMessage.error('头像图片不能超过 5MB')
    return false
  }
  return true
}

async function onAvatarSuccess(response) {
  if (response.code === 200) {
    form.avatar = response.data
    ElMessage.success('头像上传成功，点击保存生效')
  } else {
    ElMessage.error(response.msg || '头像上传失败')
  }
}

async function save() {
  await formRef.value.validate()
  saving.value = true
  try {
    await updateProfile({ ...form })
    await store.refreshInfo()
    ElMessage.success('资料已保存')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  Object.assign(form, {
    nickname: userInfo?.nickname || '',
    phone: userInfo?.phone || '',
    avatar: userInfo?.avatar || ''
  })
})
</script>

<style scoped>
.profile-card {
  max-width: 640px;
  margin: 0 auto;
  background: #fff;
  border-radius: 12px;
  padding: 24px 28px;
}
.profile-title {
  font-size: 20px;
  color: #303133;
}
.avatar-section {
  display: flex;
  justify-content: center;
  margin-top: 12px;
}
.avatar-wrap {
  position: relative;
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;
}
.avatar-mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  color: #fff;
  font-size: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  opacity: 0;
  transition: opacity 0.2s;
}
.avatar-wrap:hover .avatar-mask {
  opacity: 1;
}
@media (max-width: 768px) {
  .profile-card {
    padding: 16px 14px;
  }
}
</style>
