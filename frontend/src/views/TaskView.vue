<template>
  <div class="task-container">
    <div class="header-actions">
      <h1>员工任务管理系统</h1>
      <div>
        <span style="margin-right: 10px;">当前用户: {{ currentUser?.name }}</span>
        <el-button size="small" @click="logout">退出</el-button>
      </div>
    </div>

    <el-button type="primary" @click="openAddDialog" style="margin-bottom: 20px;">新增任务</el-button>

    <el-table 
      :data="taskList" 
      style="width: 100%" 
      border 
      stripe
      :row-class-name="tableRowClassName">
      
      <!-- 勾选列 -->
      <el-table-column width="55">
        <template #default="scope">
          <el-checkbox 
            v-model="scope.row.checked" 
            @change="handleCheckChange(scope.row)"
            :disabled="scope.row.status === 2">
          </el-checkbox>
        </template>
      </el-table-column>

      <el-table-column prop="id" label="ID" width="80" />
      
      <el-table-column prop="title" label="任务标题" width="180">
        <template #default="scope">
          <span :class="{ 'completed-task': scope.row.status === 2 }">
            {{ scope.row.title }}
          </span>
        </template>
      </el-table-column>

      <!-- 任务描述 -->
      <el-table-column prop="description" label="任务描述">
        <template #default="scope">
           <span :class="{ 'completed-task': scope.row.status === 2 }">
            {{ scope.row.description }}
          </span>
        </template>
      </el-table-column>

      <el-table-column prop="assigneeName" label="创建人" width="100" />
      
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="info">待办</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="warning">进行中</el-tag>
          <el-tag v-else-if="scope.row.status === 3" type="danger">已暂停</el-tag>
          <el-tag v-else type="success">已完成</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="priority" label="优先级" width="80" />

      <el-table-column label="操作" width="350">
        <template #default="scope">
          <!-- 1. 待办状态(0)：显示 [开始] [完成] [编辑] [删除] -->
          <template v-if="scope.row.status === 0">
            <el-button size="small" @click="changeStatus(scope.row.id, 1)">开始</el-button>
            <el-button size="small" type="success" @click="changeStatus(scope.row.id, 2)">完成</el-button>
          </template>

          <!-- 2. 进行中状态(1)：显示 [暂停] [完成] [编辑] [删除] -->
          <template v-else-if="scope.row.status === 1">
            <el-button size="small" type="warning" @click="changeStatus(scope.row.id, 3)">暂停</el-button>
            <el-button size="small" type="success" @click="changeStatus(scope.row.id, 2)">完成</el-button>
          </template>

          <!-- 3. 已暂停状态(3)：显示 [恢复] [完成] [编辑] [删除] -->
          <template v-else-if="scope.row.status === 3">
            <el-button size="small" type="primary" @click="changeStatus(scope.row.id, 1)">恢复</el-button>
            <el-button size="small" type="success" @click="changeStatus(scope.row.id, 2)">完成</el-button>
          </template>

          <!-- 4. 已完成状态(2)：不显示状态按钮，或者只显示删除 -->
          <!-- 这里保持编辑和删除始终显示，方便管理 -->
          <el-button size="small" @click="openEditDialog(scope.row)" :disabled="scope.row.status === 2">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteTask(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑 弹窗 (修复：确保description输入框存在) -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑任务' : '新增任务'" width="30%">
      <el-form :model="form" label-width="80px">
        <el-form-item label="任务标题">
          <el-input v-model="form.title" placeholder="请输入标题"></el-input>
        </el-form-item>
        
        <!-- 修复：这里必须有描述输入框 -->
        <el-form-item label="任务描述">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入任务详情内容">
          </el-input>
        </el-form-item>

        <el-form-item label="优先级">
          <el-select v-model="form.priority" placeholder="请选择" style="width: 100%">
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
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const taskList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)

// 表单数据：必须包含 description
const form = ref({
  id: null,
  title: '',
  description: '', // 确保这里有默认值
  priority: 2
})

// 获取当前用户
const currentUser = ref(JSON.parse(localStorage.getItem('user')))

// 获取任务列表
const fetchTasks = async () => {
  try {
    const res = await axios.get('/api/tasks')
    taskList.value = res.data
  } catch (error) {
    ElMessage.error('获取数据失败')
  }
}

// 提交表单（新增或编辑）
const submitForm = async () => {
  if (!form.value.title) {
    ElMessage.warning('请输入任务标题')
    return
  }
  try {
    if (isEdit.value) {
      // 编辑逻辑
      await axios.put('/api/tasks', form.value)
      ElMessage.success('修改成功')
    } else {
      // 新增逻辑：带上创建人ID
      const dataToAdd = { 
        ...form.value, 
        assigneeId: currentUser.value.id 
      }
      await axios.post('/api/tasks', dataToAdd)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchTasks()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 更新状态
const changeStatus = async (id, status) => {
  await axios.put(`/api/tasks/${id}/status?status=${status}`)
  ElMessage.success('状态更新成功')
  fetchTasks()
}

// 删除任务
const deleteTask = async (id) => {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await axios.delete(`/api/tasks/${id}`)
  ElMessage.success('删除成功')
  fetchTasks()
}

// 打开新增弹窗
const openAddDialog = () => {
  isEdit.value = false
  // 重置表单，确保 description 为空
  form.value = { id: null, title: '', description: '', priority: 2 }
  dialogVisible.value = true
}

// 打开编辑弹窗
const openEditDialog = (row) => {
  isEdit.value = true
  // 深拷贝行数据到表单，确保 description 能回显
  form.value = { ...row }
  dialogVisible.value = true
}

// 退出登录
const logout = () => {
  localStorage.removeItem('user')
  router.push('/login')
}

// 勾选框逻辑：勾选=完成，取消=恢复
const handleCheckChange = async (row) => {
  if (row.checked) {
    await axios.put(`/api/tasks/${row.id}/status?status=2`)
    ElMessage.success('任务已完成')
  } else {
    // 如果取消勾选，恢复为进行中
    await axios.put(`/api/tasks/${row.id}/status?status=1`)
    ElMessage.info('任务已恢复')
  }
  fetchTasks()
}

// 行样式
const tableRowClassName = ({ row }) => {
  if (row.status === 2) {
    return 'completed-row'
  }
  return ''
}

onMounted(() => {
  fetchTasks()
})
</script>

<style scoped>
.task-container {
  padding: 20px;
}
.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.completed-task {
  text-decoration: line-through;
  color: #999;
}
:deep(.completed-row) {
  background-color: #f5f5f5 !important;
  color: #999;
}
</style>