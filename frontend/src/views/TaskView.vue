<template>
  <div class="task-container">
    <div><h2>员工任务管理系统</h2></div>
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <div class="left-actions">
        <el-button type="primary" @click="openAddDialog">新增任务</el-button>
        <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
          批量删除 ({{ selectedIds.length }})
        </el-button>
        <el-button type="success" :disabled="selectedIds.length === 0" @click="handleBatchComplete">
          批量完成
        </el-button>
        <el-button @click="handleExport">导出 Excel</el-button>
      </div>
      
      <div class="right-actions">
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索任务..." 
          style="width: 200px; margin-right: 10px;" 
          @keyup.enter="handleSearch"
        />
        <el-button @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
        
        <span style="margin-left: 20px;">用户: {{ currentUser?.name }}</span>
        <el-button size="small" style="margin-left: 10px;" @click="logout">退出</el-button>
      </div>
    </div>

    <!-- 表格 -->
    <el-table 
      :data="taskList" 
      style="width: 100%" 
      border 
      stripe
      @selection-change="handleSelectionChange"
      :row-class-name="tableRowClassName">
      
      <!-- 勾选列 -->
      <el-table-column type="selection" width="55" />

      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" width="180">
        <template #default="scope">
          <span :class="{ 'completed-task': scope.row.status === 2 }">
            {{ scope.row.title }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述">
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

      <el-table-column label="操作" width="300">
        <template #default="scope">
          <el-button v-if="scope.row.status === 0" size="small" @click="changeStatus(scope.row.id, 1)">开始</el-button>
          <el-button v-if="scope.row.status === 1" size="small" type="warning" @click="changeStatus(scope.row.id, 3)">暂停</el-button>
          <el-button v-if="scope.row.status === 3" size="small" type="primary" @click="changeStatus(scope.row.id, 1)">恢复</el-button>
          
          <el-button size="small" @click="openEditDialog(scope.row)" :disabled="scope.row.status === 2">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteTask(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑任务' : '新增任务'" width="30%">
      <el-form :model="form" label-width="80px">
        <el-form-item label="任务标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="form.priority" style="width: 100%">
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
import { useRouter } from 'vue-router'

const router = useRouter()
const taskList = ref([])
const selectedIds = ref([]) 
const searchKeyword = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentUser = ref(JSON.parse(localStorage.getItem('user')))

const form = ref({ id: null, title: '', description: '', priority: 2 })

// 获取列表
const fetchTasks = async () => {
  try {
    const res = await axios.get('/api/tasks', { params: { keyword: searchKeyword.value } })
    taskList.value = res.data
  } catch (error) {
    ElMessage.error('获取数据失败')
  }
}

// 勾选处理
const handleSelectionChange = (val) => {
  selectedIds.value = val.map(item => item.id)
}

// 搜索
const handleSearch = () => fetchTasks()
const handleReset = () => { searchKeyword.value = ''; fetchTasks() }

// 批量删除
const handleBatchDelete = async () => {
  await ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 条任务？`, '警告', { type: 'warning' })
  await axios.delete('/api/tasks/batch', { data: selectedIds.value })
  ElMessage.success('删除成功')
  fetchTasks()
}

// 批量完成
const handleBatchComplete = async () => {
  await axios.put('/api/tasks/batch/complete', selectedIds.value)
  ElMessage.success('批量完成成功')
  fetchTasks()
}

// 导出
const handleExport = () => {
  if (taskList.value.length === 0) { ElMessage.warning('无数据'); return; }
  let csv = "ID,标题,描述,状态,创建人\n"
  taskList.value.forEach(row => {
    let statusText = ['待办', '进行中', '已完成', '已暂停'][row.status]
    let desc = row.description ? row.description.replace(/,/g, '，').replace(/\n/g, ' ') : ''
    csv += `${row.id},${row.title},${desc},${statusText},${row.assigneeName || ''}\n`
  })
  const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `任务列表_${new Date().toLocaleDateString()}.csv`
  link.click()
}

// 其他方法
const changeStatus = async (id, status) => { await axios.put(`/api/tasks/${id}/status?status=${status}`); fetchTasks() }
// 提交表单（新增或编辑）
const submitForm = async () => {
  // 校验：必须登录
  if (!currentUser.value || !currentUser.value.id) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }

  try {
    if (isEdit.value) {
      // 编辑
      await axios.put('/api/tasks', form.value)
      ElMessage.success('修改成功')
    } else {
      // 新增：确保 assigneeId 存在
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
    ElMessage.error('操作失败：' + (error.response?.data?.message || '未知错误'))
  }
}
const deleteTask = async (id) => { await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }); await axios.delete(`/api/tasks/${id}`); fetchTasks() }
const openEditDialog = (row) => { isEdit.value = true; form.value = { ...row }; dialogVisible.value = true }
const logout = () => { localStorage.removeItem('user'); router.push('/login') }
const tableRowClassName = ({ row }) => row.status === 2 ? 'completed-row' : ''

onMounted(() => fetchTasks())
</script>

<style scoped>
.task-container { padding: 20px; }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; background: #fff; padding: 10px; border-radius: 4px; }
.left-actions { display: flex; gap: 10px; }
.right-actions { display: flex; align-items: center; }
.completed-task { text-decoration: line-through; color: #999; }
:deep(.completed-row) { background-color: #fafafa !important; color: #999; }
</style>