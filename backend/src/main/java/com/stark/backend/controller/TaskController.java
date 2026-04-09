package com.stark.backend.controller;

import com.stark.backend.entity.Task;
import com.stark.backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // 1. 查询列表（支持搜索）
    @GetMapping
    public List<Task> list(@RequestParam(required = false) String keyword) {
        return taskService.searchTasks(keyword);
    }

    // 2. 新增任务
    @PostMapping
    public String add(@RequestBody Task task) {
        taskService.addTask(task);
        return "新增成功";
    }

    // 3. 编辑任务
    @PutMapping
    public String update(@RequestBody Task task) {
        taskService.updateTask(task);
        return "修改成功";
    }

    // 4. 修改状态
    @PutMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        taskService.updateTaskStatus(id, status);
        return "状态更新成功";
    }

    // 5. 单条删除
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "删除成功";
    }

    // 6. 批量删除
    @DeleteMapping("/batch")
    public String batchDelete(@RequestBody List<Long> ids) {
        taskService.batchDelete(ids);
        return "批量删除成功";
    }

    // 7. 批量完成
    @PutMapping("/batch/complete")
    public String batchComplete(@RequestBody List<Long> ids) {
        taskService.batchComplete(ids);
        return "批量完成成功";
    }
}