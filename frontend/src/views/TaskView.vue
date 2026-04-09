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

    <el-table :data="taskList" style="width: 100%" border stripe 
      :row-class-name="tableRowClassName">
      
      <!-- 新增：勾选列 -->
      <el-table-column width="55">
        <template #default="scope">
          <el-checkbox 
            v-model="scope.row.checked" 
            @change="handleCheckChange(scope.row)"
            :disabled="scope.row.status === 2">
          </el-checkbox>
        </template>
      </el-table-column>

      <el-table-column prop="title" label="任务标题" width="180">
        <template #default="scope">
          <!-- 如果已完成，标题加删除线 -->
          <span :class="{ 'completed-task': scope.row.status === 2 }">
            {{ scope.row.title }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" />
      
      <!-- 新增：创建人列 -->
      <el-table-column prop="assigneeName" label="创建人" width="100" />
      
      <el-table-column prop="priority" label="优先级" width="80" />

      <el-table-column label="操作" width="300">
        <template #default="scope">
          
          <!-- 逻辑：未完成状态(0/1/3)显示"开始/暂停/恢复" -->
          <template v-if="scope.row.status !== 2">
            <!-- 待办(0) -> 开始 -->
            <el-button 
              v-if="scope.row.status === 0" 
              size="small" 
              @click="changeStatus(scope.row.id, 1)">开始</el-button>
            
            <!-- 进行中(1) -> 暂停 -->
            <el-button 
              v-if="scope.row.status === 1" 
              size="small" 
              type="warning" 
              @click="changeStatus(scope.row.id, 3)">暂停</el-button>
            
            <!-- 已暂停(3) -> 恢复(变成进行中) -->
            <el-button 
              v-if="scope.row.status === 3" 
              size="small" 
              type="primary" 
              @click="changeStatus(scope.row.id, 1)">恢复</el-button>
          </template>

          <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteTask(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑 弹窗 (代码同前一步，略) -->
    <!-- 此处省略弹窗代码，请保留您之前的弹窗代码，但在 submitForm 中需绑定 assigneeId -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑任务' : '新增任务'" width="30%">
       <!-- ... 表单内容 ... -->
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
const form = ref({ id: null, title: '', description: '', priority: 2 })

// 当前登录用户
const currentUser = ref(null)

onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    currentUser.value = JSON.parse(userStr)
  }
  fetchTasks()
})

// 获取任务列表
const fetchTasks = async () => {
  const res = await axios.get('/api/tasks')
  // 初始化 checked 状态用于 checkbox 绑定
  taskList.value = res.data.map(item => ({
    ...item,
    checked: item.status === 2 // 如果已完成，checkbox为选中状态
  }))
}

// 勾选完成/取消完成逻辑
const handleCheckChange = async (row) => {
  if (row.checked) {
    // 勾选 -> 完成任务
    await axios.put(`/api/tasks/${row.id}/status?status=2`)
    ElMessage.success('任务已完成')
    row.status = 2 // 更新本地状态
  } else {
    // 取消勾选 -> 恢复为进行中
    await axios.put(`/api/tasks/${row.id}/status?status=1`)
    ElMessage.info('任务已恢复')
    row.status = 1
  }
  fetchTasks() // 刷新列表以更新样式
}

// 提交表单（新增时绑定创建人）
const submitForm = async () => {
  try {
    if (isEdit.value) {
      await axios.put('/api/tasks', form.value)
      ElMessage.success('修改成功')
    } else {
      // 新增时，绑定当前登录用户为创建人
      const dataToAdd = { ...form.value, assigneeId: currentUser.value.id }
      await axios.post('/api/tasks', dataToAdd)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchTasks()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 修改状态（开始/暂停/恢复）
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
  form.value = { id: null, title: '', description: '', priority: 2 }
  dialogVisible.value = true
}

// 打开编辑弹窗
const openEditDialog = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

// 退出登录
const logout = () => {
  localStorage.removeItem('user')
  router.push('/login')
}

// 行样式控制：已完成置灰
const tableRowClassName = ({ row }) => {
  if (row.status === 2) {
    return 'completed-row'
  }
  return ''
}
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
/* 标题删除线样式 */
.completed-task {
  text-decoration: line-through;
  color: #999;
}
/* 整行置灰样式 */
:deep(.completed-row) {
  background-color: #f5f5f5 !important;
  color: #999;
}
</style>