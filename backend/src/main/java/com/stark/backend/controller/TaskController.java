package com.stark.backend.controller;

import com.stark.backend.entity.Task;
import com.stark.backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController // 告诉 Spring：这是一个接口控制器，返回的数据会自动转成 JSON
@RequestMapping("/api/tasks") // 定义接口的统一前缀路径
public class TaskController {

    @Autowired
    private TaskService taskService;

    // 1. 查询所有任务接口
    // 访问地址：GET http://localhost:8080/api/tasks
    @GetMapping
    public List<Task> list() {
        return taskService.getAllTasks();
    }

    // 2. 新增任务接口
    // 访问地址：POST http://localhost:8080/api/tasks
    @PostMapping
    public String add(@RequestBody Task task) {
        taskService.addTask(task);
        return "新增成功";
    }

    // 3. 更新任务状态接口
    // 访问地址：PUT http://localhost:8080/api/tasks/{id}/status?status=1
    @PutMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        taskService.updateTaskStatus(id, status);
        return "状态更新成功";
    }
    // 在 TaskController.java 中新增以下接口

    // 编辑任务接口
    // 访问地址：PUT http://localhost:8080/api/tasks
    @PutMapping
    public String update(@RequestBody Task task) {
        taskService.updateTask(task);
        return "修改成功";
    }

    // 删除任务接口
    // 访问地址：DELETE http://localhost:8080/api/tasks/{id}
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "删除成功";
    }
}