package com.stark.backend.service;

import com.stark.backend.entity.Task;
import com.stark.backend.mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service // 告诉 Spring：这是业务逻辑层
public class TaskService {

    @Autowired // 自动注入刚才写的 Mapper，就像插拔组件一样
    private TaskMapper taskMapper;

    // 查询任务列表
    public List<Task> getAllTasks() {
        return taskMapper.findAll();
    }

    // 新增任务
    public void addTask(Task task) {
        task.setStatus(0); // 默认状态为待办
        taskMapper.insert(task);
    }

    // 更新状态
    public void updateTaskStatus(Long id, Integer status) {
        Task task = new Task();
        task.setId(id);
        task.setStatus(status);
        taskMapper.updateStatus(task);
    }
    // 在 TaskService.java 中新增以下方法

   // 编辑任务
    public void updateTask(Task task) {
        taskMapper.update(task);
}

    // 删除任务
    public void deleteTask(Long id) {
        taskMapper.deleteById(id);
}
}