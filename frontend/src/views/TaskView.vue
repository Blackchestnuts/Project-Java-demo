<template>
  <div class="task-container">
    <h1>员工任务管理系统</h1>
    
    <!-- 顶部操作栏 -->
    <div class="header-actions">
      <el-button type="primary" @click="openAddDialog">新增任务</el-button>
    </div>

    <!-- 任务列表表格 -->
    <el-table :data="taskList" style="width: 100%" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="任务标题" width="180" />
      <el-table-column prop="description" label="任务描述" />
      
      <!-- 状态列 -->
      <el-table-column label="状态" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="info">待办</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="warning">进行中</el-tag>
          <el-tag v-else-if="scope.row.status === 3" type="danger">已暂停</el-tag>
          <el-tag v-else type="success">已完成</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="priority" label="优先级" width="100" />
      
      <!-- 操作列 (核心改动) -->
      <el-table-column label="操作" width="300">
        <template #default="scope">
          <!-- 状态流转按钮 -->
          <el-button 
            v-if="scope.row.status === 0" 
            size="small" 
            @click="changeStatus(scope.row.id, 1)">开始</el-button>
          
          <el-button 
            v-if="scope.row.status === 1" 
            size="small" 
            type="warning" 
            @click="changeStatus(scope.row.id, 3)">暂停</el-button>
          
          <el-button 
            v-if="scope.row.status === 3" 
            size="small" 
            type="primary" 
            @click="changeStatus(scope.row.id, 1)">恢复</el-button>

          <el-button 
            v-if="scope.row.status === 1 || scope.row.status === 3" 
            size="small" 
            type="success" 
            @click="changeStatus(scope.row.id, 2)">完成</el-button>

          <!-- 编辑与删除 -->
          <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteTask(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑 弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑任务' : '新增任务'" width="30%">
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
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

// 数据定义
const taskList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false) // 标记当前是新增还是编辑
const form = ref({
  id: null,
  title: '',
  description: '',
  priority: 2
})

// 1. 获取任务列表
const fetchTasks = async () => {
  try {
    const res = await axios.get('/api/tasks')
    taskList.value = res.data
  } catch (error) {
    ElMessage.error('获取数据失败')
  }
}

// 2. 提交表单 (新增或编辑)
const submitForm = async () => {
  try {
    if (isEdit.value) {
      // 编辑模式 -> PUT
      await axios.put('/api/tasks', form.value)
      ElMessage.success('修改成功')
    } else {
      // 新增模式 -> POST
      await axios.post('/api/tasks', form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchTasks()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 3. 更新状态 (开始、暂停、恢复、完成)
const changeStatus = async (id, status) => {
  try {
    await axios.put(`/api/tasks/${id}/status?status=${status}`)
    ElMessage.success('状态更新成功')
    fetchTasks()
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

// 4. 删除任务
const deleteTask = async (id) => {
  try {
    // 弹出确认框
    await ElMessageBox.confirm('确定要删除该任务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    
    await axios.delete(`/api/tasks/${id}`)
    ElMessage.success('删除成功')
    fetchTasks()
  } catch (error) {
    // 用户点击取消也会进入 catch，这里不报错
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 5. 打开新增弹窗
const openAddDialog = () => {
  isEdit.value = false
  form.value = { id: null, title: '', description: '', priority: 2 }
  dialogVisible.value = true
}

// 6. 打开编辑弹窗
const openEditDialog = (row) => {
  isEdit.value = true
  // 将当前行的数据复制到表单
  form.value = { ...row }
  dialogVisible.value = true
}

// 页面加载
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