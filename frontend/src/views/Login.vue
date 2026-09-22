<template>
  <div class="login-page">
    <!-- 背景光晕 -->
    <div class="bg-blob blob-1"></div>
    <div class="bg-blob blob-2"></div>
    <div class="bg-blob blob-3"></div>

    <div class="login-card">
      <div class="login-header">
        <div class="logo-icon">
          <el-icon :size="28"><School /></el-icon>
        </div>
        <h1 class="app-title">校园闲置</h1>
        <p class="app-subtitle">让闲置流动起来</p>
      </div>

      <!-- iOS 风格分段切换 -->
      <div class="segmented">
        <div
          class="segmented-item"
          :class="{ active: activeTab === 'login' }"
          @click="activeTab = 'login'"
        >登录</div>
        <div
          class="segmented-item"
          :class="{ active: activeTab === 'register' }"
          @click="activeTab = 'register'"
        >注册</div>
        <div class="segmented-thumb" :class="{ right: activeTab === 'register' }"></div>
      </div>

      <el-form v-show="activeTab === 'login'" ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large" class="login-form">
        <el-form-item prop="account">
          <el-input v-model="loginForm.account" placeholder="账号" :prefix-icon="User" clearable />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码" :prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <button type="button" class="apple-btn" :class="{ loading }" :disabled="loading" @click="handleLogin">
          {{ loading ? '登录中…' : '登 录' }}
        </button>
      </el-form>

      <el-form v-show="activeTab === 'register'" ref="registerFormRef" :model="registerForm" :rules="registerRules" size="large" class="login-form">
        <el-form-item prop="account">
          <el-input v-model="registerForm.account" placeholder="账号（3-20 位）" :prefix-icon="User" clearable />
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input v-model="registerForm.nickname" placeholder="昵称" :prefix-icon="Postcard" clearable />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="密码（6-20 位）" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="确认密码" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="registerForm.phone" placeholder="手机号（选填）" :prefix-icon="Iphone" clearable />
        </el-form-item>
        <button type="button" class="apple-btn" :class="{ loading }" :disabled="loading" @click="handleRegister">
          {{ loading ? '注册中…' : '注 册' }}
        </button>
      </el-form>

      <div class="login-tip">
        管理员账号 <code>admin</code> / <code>admin123</code>
      </div>
    </div>

    <p class="page-footer">仅用于校园内部闲置物品交流</p>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Postcard, Iphone, School } from '@element-plus/icons-vue'
import { useUserStore } from '../store/user'

const router = useRouter()
const route = useRoute()
const store = useUserStore()

const activeTab = ref('login')
const loading = ref(false)

const loginForm = reactive({ account: '', password: '' })
const registerForm = reactive({ account: '', nickname: '', password: '', confirmPassword: '', phone: '' })

const loginFormRef = ref()
const registerFormRef = ref()

const loginRules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const registerRules = {
  account: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度 3-20 位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度 6-20 位', trigger: 'blur' }
  ],
  confirmPassword: [
    {
      validator: (rule, value, callback) => {
        if (!value) callback(new Error('请再次输入密码'))
        else if (value !== registerForm.password) callback(new Error('两次输入的密码不一致'))
        else callback()
      },
      trigger: 'blur'
    }
  ]
}

async function handleLogin() {
  await loginFormRef.value.validate()
  loading.value = true
  try {
    await store.login(loginForm.account, loginForm.password)
    ElMessage.success('登录成功')
    const redirect = route.query.redirect
    router.push(redirect || '/')
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  await registerFormRef.value.validate()
  loading.value = true
  try {
    await store.register({
      account: registerForm.account,
      nickname: registerForm.nickname,
      password: registerForm.password,
      phone: registerForm.phone
    })
    ElMessage.success('注册成功，请登录')
    activeTab.value = 'login'
    loginForm.account = registerForm.account
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ===== 页面与背景 ===== */
.login-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: #f5f5f7;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Display", "PingFang SC", "Helvetica Neue", sans-serif;
  overflow: hidden;
}

/* 柔和光晕背景（macOS Big Sur 风） */
.bg-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.55;
  pointer-events: none;
  z-index: 0;
}
.blob-1 {
  width: 480px; height: 480px;
  background: radial-gradient(circle, #a1c4fd 0%, transparent 70%);
  top: -120px; left: -120px;
}
.blob-2 {
  width: 420px; height: 420px;
  background: radial-gradient(circle, #fbc2eb 0%, transparent 70%);
  bottom: -100px; right: -80px;
}
.blob-3 {
  width: 360px; height: 360px;
  background: radial-gradient(circle, #c2e9fb 0%, transparent 70%);
  top: 40%; left: 50%;
  transform: translateX(-50%);
  opacity: 0.35;
}

/* ===== 毛玻璃卡片 ===== */
.login-card {
  position: relative;
  z-index: 1;
  width: 400px;
  max-width: 100%;
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(28px) saturate(180%);
  -webkit-backdrop-filter: blur(28px) saturate(180%);
  border-radius: 24px;
  padding: 40px 36px 28px;
  box-shadow:
    0 24px 60px rgba(0, 0, 0, 0.10),
    0 1px 0 rgba(255, 255, 255, 0.8) inset;
  border: 1px solid rgba(255, 255, 255, 0.6);
}

/* ===== 头部 ===== */
.login-header {
  text-align: center;
  margin-bottom: 28px;
}
.logo-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  border-radius: 18px;
  background: linear-gradient(145deg, #0a84ff 0%, #5e5ce6 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 12px 28px rgba(10, 132, 255, 0.35);
}
.app-title {
  font-size: 28px;
  font-weight: 700;
  letter-spacing: -0.5px;
  color: #1d1d1f;
  margin: 0;
}
.app-subtitle {
  font-size: 15px;
  color: #86868b;
  margin-top: 6px;
  font-weight: 400;
}

/* ===== iOS 分段切换控件 ===== */
.segmented {
  position: relative;
  display: flex;
  background: rgba(118, 118, 128, 0.12);
  border-radius: 12px;
  padding: 3px;
  margin-bottom: 28px;
  height: 36px;
}
.segmented-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 500;
  color: #6e6e73;
  cursor: pointer;
  border-radius: 9px;
  z-index: 1;
  transition: color 0.25s ease;
  user-select: none;
}
.segmented-item.active {
  color: #1d1d1f;
  font-weight: 600;
}
.segmented-thumb {
  position: absolute;
  top: 3px;
  left: 3px;
  width: calc(50% - 3px);
  height: calc(100% - 6px);
  background: #fff;
  border-radius: 9px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s cubic-bezier(0.32, 0.72, 0, 1);
}
.segmented-thumb.right {
  transform: translateX(100%);
}

/* ===== 表单 ===== */
.login-form {
  margin-bottom: 8px;
}
.login-form :deep(.el-form-item) {
  margin-bottom: 16px;
}
.login-form :deep(.el-input__wrapper) {
  background: rgba(118, 118, 128, 0.08);
  border: none;
  border-radius: 12px;
  box-shadow: none;
  padding: 4px 14px;
  height: 46px;
  transition: background 0.2s ease;
}
.login-form :deep(.el-input__wrapper.is-focus) {
  background: rgba(10, 132, 255, 0.10);
  box-shadow: 0 0 0 3px rgba(10, 132, 255, 0.20);
}
.login-form :deep(.el-input__inner) {
  font-size: 15px;
  color: #1d1d1f;
}
.login-form :deep(.el-input__inner::placeholder) {
  color: #99999e;
}
.login-form :deep(.el-input__prefix) {
  color: #8e8e93;
}
.login-form :deep(.el-form-item__error) {
  font-size: 12px;
  padding-top: 4px;
}

/* ===== 苹果风格按钮 ===== */
.apple-btn {
  width: 100%;
  height: 48px;
  margin-top: 8px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(180deg, #0a84ff 0%, #0071e3 100%);
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 2px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 8px 20px rgba(0, 113, 227, 0.30);
  font-family: inherit;
}
.apple-btn:hover {
  background: linear-gradient(180deg, #1a8eff 0%, #1076e8 100%);
  box-shadow: 0 10px 24px rgba(0, 113, 227, 0.40);
  transform: translateY(-1px);
}
.apple-btn:active {
  transform: translateY(0);
  box-shadow: 0 4px 12px rgba(0, 113, 227, 0.30);
}
.apple-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* ===== 提示与页脚 ===== */
.login-tip {
  margin-top: 20px;
  text-align: center;
  font-size: 12.5px;
  color: #86868b;
}
.login-tip code {
  background: rgba(118, 118, 128, 0.12);
  padding: 2px 8px;
  border-radius: 6px;
  font-size: 12px;
  color: #1d1d1f;
  font-weight: 500;
}
.page-footer {
  position: relative;
  z-index: 1;
  margin-top: 28px;
  font-size: 12px;
  color: #aeaeb2;
  letter-spacing: 0.3px;
}

/* ===== 移动端适配 ===== */
@media (max-width: 480px) {
  .login-card {
    padding: 32px 24px 24px;
  }
  .app-title {
    font-size: 24px;
  }
}
</style>
