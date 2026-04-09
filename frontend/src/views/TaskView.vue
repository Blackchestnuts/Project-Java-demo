<template>
  <div class="task-container">
    <h1>员工任务管理系统</h1>
    
    <!-- 顶部操作栏 -->
    <div class="header-actions">
      <el-button type="primary" @click="dialogVisible = true">新增任务</el-button>
    </div>

    <!-- 任务列表表格 -->
    <el-table :data="taskList" style="width: 100%" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="任务标题" width="180" />
      <el-table-column prop="description" label="任务描述" />
      <el-table-column label="状态" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="info">待办</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="warning">进行中</el-tag>
          <el-tag v-else type="success">已完成</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级" width="100" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="changeStatus(scope.row.id, 1)">开始</el-button>
          <el-button size="small" type="success" @click="changeStatus(scope.row.id, 2)">完成</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增任务弹窗 -->
    <el-dialog v-model="dialogVisible" title="新增任务" width="30%">
      <el-form :model="form" label-width="80px">
        <el-form-item label="任务标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="form.priority" placeholder="请选择">
            <el-option label="低" :value="1" />
            <el-option label="中" :value="2" />
            <el-option label="高" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addTask">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 数据定义
const taskList = ref([])
const dialogVisible = ref(false)
const form = ref({
  title: '',
  description: '',
  priority: 2,
  status: 0
})

// 1. 获取任务列表
const fetchTasks = async () => {
  try {
    const res = await axios.get('/api/tasks') // 这里使用了代理
    taskList.value = res.data
  } catch (error) {
    ElMessage.error('获取数据失败')
  }
}

// 2. 新增任务
const addTask = async () => {
  try {
    await axios.post('/api/tasks', form.value)
    ElMessage.success('新增成功')
    dialogVisible.value = false
    fetchTasks() // 刷新列表
    // 重置表单
    form.value = { title: '', description: '', priority: 2, status: 0 }
  } catch (error) {
    ElMessage.error('新增失败')
  }
}

// 3. 更新状态
const changeStatus = async (id, status) => {
  try {
    await axios.put(`/api/tasks/${id}/status?status=${status}`)
    ElMessage.success('状态更新成功')
    fetchTasks()
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

// 页面加载时执行
onMounted(() => {
  fetchTasks()
})
</script>

<style scoped>
.task-container {
  padding: 20px;
}
.header-actions {
  margin-bottom: 20px;
}
</style>