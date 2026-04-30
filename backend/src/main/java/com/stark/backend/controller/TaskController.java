package com.stark.backend.controller;

import com.stark.backend.entity.Task;
import com.stark.backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // ========== 基础CRUD ==========

    /**
     * 查询列表（支持搜索）
     */
    @GetMapping
    public List<Task> list(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) Long assigneeId) {
        return taskService.searchTasks(keyword, assigneeId);
    }

    /**
     * 新增任务
     */
    @PostMapping
    public String add(@RequestBody Task task) {
        taskService.addTask(task);
        return "新增成功";
    }

    /**
     * 编辑任务
     */
    @PutMapping
    public String update(@RequestBody Task task) {
        taskService.updateTask(task);
        return "修改成功";
    }

    /**
     * 修改状态
     */
    @PutMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        taskService.updateTaskStatus(id, status);
        return "状态更新成功";
    }

    /**
     * 单条删除
     */
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "删除成功";
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public String batchDelete(@RequestBody List<Long> ids) {
        taskService.batchDelete(ids);
        return "批量删除成功";
    }

    /**
     * 批量完成
     */
    @PutMapping("/batch/complete")
    public String batchComplete(@RequestBody List<Long> ids) {
        taskService.batchComplete(ids);
        return "批量完成成功";
    }

    // ========== 统计分析 ==========

    /**
     * 任务统计概览
     * GET /api/tasks/statistics?assigneeId=1
     * 返回: {total, todo, inProgress, paused, completed, overdue}
     */
    @GetMapping("/statistics")
    public Map<String, Object> statistics(@RequestParam(required = false) Long assigneeId) {
        return taskService.getStatistics(assigneeId);
    }

    /**
     * 本周完成趋势
     * GET /api/tasks/weekly-trend?assigneeId=1
     * 返回: [{day: "2026-04-27", count: 3}, ...]
     */
    @GetMapping("/weekly-trend")
    public List<Map<String, Object>> weeklyTrend(@RequestParam(required = false) Long assigneeId) {
        return taskService.getWeeklyTrend(assigneeId);
    }

    /**
     * 即将到期任务
     * GET /api/tasks/upcoming?assigneeId=1&limit=5
     * 返回: List<Task>
     */
    @GetMapping("/upcoming")
    public List<Task> upcoming(@RequestParam(required = false) Long assigneeId,
                               @RequestParam(defaultValue = "5") int limit) {
        return taskService.getUpcoming(assigneeId, limit);
    }
}
