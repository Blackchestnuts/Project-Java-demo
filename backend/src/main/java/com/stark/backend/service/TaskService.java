package com.stark.backend.service;

import com.stark.backend.entity.Task;
import com.stark.backend.mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskMapper taskMapper;

    // 搜索/查询列表
        // 修改 searchTasks，增加 assigneeId 参数并传递给 Mapper
    public List<Task> searchTasks(String keyword, Long assigneeId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            // 注意：这里也要改，我们需要一个新的 findAll 方法，或者直接调用 search("", assigneeId)
            // 为了简洁，我们直接调用 search
            return taskMapper.search("", assigneeId);
        }
        return taskMapper.search(keyword, assigneeId);
    }

    // 新增
    public void addTask(Task task) {
        task.setStatus(0); // 默认待办
        taskMapper.insert(task);
    }

    // 编辑
    public void updateTask(Task task) {
        taskMapper.update(task);
    }

    // 更新状态
    public void updateTaskStatus(Long id, Integer status) {
        Task task = new Task();
        task.setId(id);
        task.setStatus(status);
        taskMapper.updateStatus(task);
    }

    // 单条删除
    public void deleteTask(Long id) {
        taskMapper.deleteById(id);
    }

    // 批量删除
    public void batchDelete(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            taskMapper.deleteByIds(ids);
        }
    }

    // 批量完成
    public void batchComplete(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            taskMapper.updateStatusByIds(ids, 2); // 2代表完成
        }
    }
}