<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>任务管理系统 - 登录</h2>
      <el-form :model="form" label-width="80px">
        <el-form-item label="选择用户">
          <el-select v-model="selectedUserId" placeholder="请选择登录身份" style="width: 100%">
            <el-option 
              v-for="user in userList" 
              :key="user.id" 
              :label="user.name" 
              :value="user.id" />
          </el-select>
        </el-form-item>
        <el-button type="primary" style="width: 100%" @click="handleLogin">登 录</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userList = ref([])
const selectedUserId = ref(null)

onMounted(async () => {
  const res = await axios.get('/api/employees')
  userList.value = res.data
})

const handleLogin = () => {
  if (!selectedUserId.value) {
    ElMessage.warning('请选择用户')
    return
  }
  // 模拟登录：将用户信息存入 localStorage
  const user = userList.value.find(u => u.id === selectedUserId.value)
  localStorage.setItem('user', JSON.stringify(user))
  ElMessage.success('登录成功')
  router.push('/') // 跳转到任务列表
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
}
.login-card {
  width: 400px;
}
</style>