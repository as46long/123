<template>
  <el-container class="main-layout">
    <el-aside width="200px">
      <div class="logo">乐语匣子</div>
      <el-menu :default-active="$route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/home">
          <el-icon><HomeFilled /></el-icon>
          <span>首页管理</span>
        </el-menu-item>
        <el-menu-item index="/user">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/song">
          <el-icon><Headset /></el-icon>
          <span>歌曲管理</span>
        </el-menu-item>
        <el-menu-item index="/order">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/leyuComment">
          <el-icon><ChatDotSquare /></el-icon>
          <span>乐语留言管理</span>
        </el-menu-item>
        <el-menu-item index="/songComment">
          <el-icon><ChatDotSquare /></el-icon>
          <span>歌曲留言管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="header-content">
          <span>{{ $route.meta.title }}</span>
          <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Odometer, HomeFilled, User, Headset, List, ChatDotSquare } from '@element-plus/icons-vue'

const router = useRouter()

const handleLogout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style scoped>
.main-layout { height: 100vh; }
.el-aside { background-color: #304156; }
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 20px;
  font-weight: bold;
  background-color: #263445;
}
.el-header { background-color: #fff; box-shadow: 0 1px 4px rgba(0,21,41,.08); }
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}
.el-main { background-color: #f0f2f5; }
</style>
