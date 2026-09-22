<template>
  <div class="layout">
    <!-- 顶栏 -->
    <header class="topbar">
      <div class="topbar-inner">
        <div class="brand" @click="router.push('/')">
          <span class="brand-icon"><el-icon :size="20"><School /></el-icon></span>
          <span class="brand-name">校园闲置</span>
        </div>

        <!-- 搜索框（PC） -->
        <div class="search-box">
          <el-input
            v-model="keyword"
            placeholder="搜索闲置物品..."
            clearable
            @keyup.enter="doSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="doSearch" />
            </template>
          </el-input>
        </div>

        <div class="topbar-right">
          <!-- 未登录 -->
          <template v-if="!store.isLogin">
            <el-button round @click="router.push('/login')">登录 / 注册</el-button>
          </template>

          <!-- 已登录 -->
          <template v-else>
            <el-button type="primary" round :icon="Plus" class="publish-btn" @click="router.push('/publish')">
              发布闲置
            </el-button>
            <el-dropdown trigger="click" @command="handleCommand">
              <span class="user-entry">
                <AvatarBox :src="store.userInfo?.avatar" :name="store.userInfo?.nickname" :size="32" />
                <span class="user-name">{{ store.userInfo?.nickname }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="myGoods">我的发布</el-dropdown-item>
                  <el-dropdown-item command="myReserve">我的预约</el-dropdown-item>
                  <el-dropdown-item command="favorites">我的收藏</el-dropdown-item>
                  <el-dropdown-item v-if="store.isAdmin" command="adminStats" divided>数据看板</el-dropdown-item>
                  <el-dropdown-item v-if="store.isAdmin" command="adminUsers">用户管理</el-dropdown-item>
                  <el-dropdown-item v-if="store.isAdmin" command="adminGoods">物品审核</el-dropdown-item>
                  <el-dropdown-item v-if="store.isAdmin" command="adminMessages">留言管理</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </div>

        <!-- 移动端菜单按钮 -->
        <el-button v-if="store.isLogin" class="menu-btn" :icon="Menu" circle @click="drawerOpen = true" />
      </div>
    </header>

    <!-- 移动端抽屉菜单 -->
    <el-drawer v-model="drawerOpen" direction="rtl" size="280px" :with-header="false">
      <div class="drawer-user">
        <AvatarBox :src="store.userInfo?.avatar" :name="store.userInfo?.nickname" :size="48" />
        <span class="drawer-name">{{ store.userInfo?.nickname }}</span>
      </div>
      <el-menu :default-active="route.path" @select="onMenuSelect">
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/publish"><el-icon><Plus /></el-icon>发布闲置</el-menu-item>
        <el-menu-item index="/my/goods"><el-icon><Box /></el-icon>我的发布</el-menu-item>
        <el-menu-item index="/my/reserve"><el-icon><Calendar /></el-icon>我的预约</el-menu-item>
        <el-menu-item index="/my/favorite"><el-icon><Star /></el-icon>我的收藏</el-menu-item>
        <el-menu-item index="/profile"><el-icon><User /></el-icon>个人中心</el-menu-item>
        <template v-if="store.isAdmin">
          <el-menu-item index="/admin/stats"><el-icon><DataAnalysis /></el-icon>数据看板</el-menu-item>
          <el-menu-item index="/admin/users"><el-icon><UserFilled /></el-icon>用户管理</el-menu-item>
          <el-menu-item index="/admin/goods"><el-icon><Goods /></el-icon>物品审核</el-menu-item>
          <el-menu-item index="/admin/messages"><el-icon><ChatDotRound /></el-icon>留言管理</el-menu-item>
        </template>
      </el-menu>
      <div class="drawer-footer">
        <el-button type="danger" plain style="width: 100%" @click="handleLogout">退出登录</el-button>
      </div>
    </el-drawer>

    <!-- 内容区 -->
    <main class="main">
      <router-view />
    </main>

    <footer class="footer">校园闲置物品流转平台 · SpringBoot + Vue3 + MySQL · 仅限校园内部交流使用</footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Menu, School, Box, Calendar, Star, User, DataAnalysis, UserFilled, Goods, ChatDotRound } from '@element-plus/icons-vue'
import { useUserStore } from '../store/user'
import AvatarBox from '../components/AvatarBox.vue'

const router = useRouter()
const route = useRoute()
const store = useUserStore()

const keyword = ref('')
const drawerOpen = ref(false)

function doSearch() {
  router.push({ name: 'home', query: { keyword: keyword.value } })
}

function handleCommand(command) {
  const map = {
    profile: '/profile',
    myGoods: '/my/goods',
    myReserve: '/my/reserve',
    favorites: '/my/favorite',
    adminStats: '/admin/stats',
    adminUsers: '/admin/users',
    adminGoods: '/admin/goods',
    adminMessages: '/admin/messages'
  }
  if (command === 'logout') {
    handleLogout()
  } else if (map[command]) {
    router.push(map[command])
  }
}

function onMenuSelect(index) {
  drawerOpen.value = false
  router.push(index)
}

function handleLogout() {
  ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
    .then(() => {
      store.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    })
    .catch(() => {})
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.topbar {
  background: #fff;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}
.topbar-inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 60px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  gap: 16px;
}
.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex-shrink: 0;
}
.brand-icon {
  width: 34px;
  height: 34px;
  border-radius: 9px;
  background: linear-gradient(135deg, #1f6feb, #38bdf8);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}
.brand-name {
  font-size: 17px;
  font-weight: 700;
  color: #1f6feb;
  white-space: nowrap;
}
.search-box {
  flex: 1;
  max-width: 420px;
  margin: 0 auto;
}
.topbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: auto;
}
.publish-btn {
  flex-shrink: 0;
}
.user-entry {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 20px;
  transition: background 0.2s;
}
.user-entry:hover {
  background: #f5f7fa;
}
.user-name {
  font-size: 14px;
  color: #303133;
  max-width: 80px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.menu-btn {
  display: none;
}
.main {
  flex: 1;
}
.footer {
  text-align: center;
  padding: 18px 12px;
  font-size: 12px;
  color: #a8abb2;
  background: #fff;
  border-top: 1px solid #f0f2f5;
}
.drawer-user {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 8px 16px;
  border-bottom: 1px solid #f0f2f5;
  margin-bottom: 8px;
}
.drawer-name {
  font-size: 15px;
  font-weight: 600;
}
.drawer-footer {
  margin-top: 16px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .topbar-inner {
    height: 54px;
    gap: 10px;
  }
  .brand-name {
    display: none;
  }
  .search-box {
    max-width: none;
  }
  .publish-btn span {
    display: none;
  }
  .user-name {
    display: none;
  }
  .menu-btn {
    display: inline-flex;
    flex-shrink: 0;
  }
}
</style>
