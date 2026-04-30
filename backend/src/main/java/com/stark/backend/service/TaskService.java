package com.stark.backend.service;

import com.stark.backend.entity.Task;
import com.stark.backend.mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaskService {

    @Autowired
    private TaskMapper taskMapper;

    // ========== 基础CRUD ==========

    /**
     * 搜索/查询列表
     */
    public List<Task> searchTasks(String keyword, Long assigneeId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return taskMapper.search("", assigneeId);
        }
        return taskMapper.search(keyword, assigneeId);
    }

    /**
     * 新增任务
     */
    public void addTask(Task task) {
        task.setStatus(0); // 默认待办
        taskMapper.insert(task);
    }

    /**
     * 编辑任务
     */
    public void updateTask(Task task) {
        taskMapper.update(task);
    }

    /**
     * 更新状态
     */
    public void updateTaskStatus(Long id, Integer status) {
        Task task = new Task();
        task.setId(id);
        task.setStatus(status);
        taskMapper.updateStatus(task);
    }

    /**
     * 单条删除
     */
    public void deleteTask(Long id) {
        taskMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void batchDelete(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            taskMapper.deleteByIds(ids);
        }
    }

    /**
     * 批量完成
     */
    public void batchComplete(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            taskMapper.updateStatusByIds(ids, 2);
        }
    }

    // ========== 统计分析 ==========

    /**
     * 获取任务统计概览
     * 返回: {total, todo, inProgress, paused, completed, overdue}
     */
    public Map<String, Object> getStatistics(Long assigneeId) {
        Map<String, Object> stats = new HashMap<>();

        // 总数
        stats.put("total", taskMapper.countTotal(assigneeId));

        // 按状态分组统计
        List<Map<String, Object>> statusCounts = taskMapper.countByStatus(assigneeId);
        int todo = 0, inProgress = 0, paused = 0, completed = 0;
        for (Map<String, Object> row : statusCounts) {
            int status = ((Number) row.get("status")).intValue();
            int count = ((Number) row.get("count")).intValue();
            switch (status) {
                case 0: todo = count; break;
                case 1: inProgress = count; break;
                case 2: completed = count; break;
                case 3: paused = count; break;
            }
        }
        stats.put("todo", todo);
        stats.put("inProgress", inProgress);
        stats.put("paused", paused);
        stats.put("completed", completed);

        // 逾期数（动态计算：未完成 + 截止日期已过）
        stats.put("overdue", taskMapper.countOverdue(assigneeId));

        return stats;
    }

    /**
     * 获取本周每日完成趋势
     * 返回: [{day: "2026-04-27", count: 3}, ...]
     */
    public List<Map<String, Object>> getWeeklyTrend(Long assigneeId) {
        List<Map<String, Object>> dbData = taskMapper.weeklyCompleteTrend(assigneeId);

        // 补全本周7天数据（没有数据的天填0）
        Map<String, Integer> dayMap = new LinkedHashMap<>();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        for (int i = 0; i < 7; i++) {
            String dayStr = String.format("%tF", cal);
            dayMap.put(dayStr, 0);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        // 填入数据库查到的值
        for (Map<String, Object> row : dbData) {
            String day = row.get("day").toString();
            int count = ((Number) row.get("count")).intValue();
            dayMap.put(day, count);
        }

        // 转为List返回
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : dayMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("day", entry.getKey());
            item.put("count", entry.getValue());
            result.add(item);
        }
        return result;
    }

    /**
     * 获取即将到期的任务
     */
    public List<Task> getUpcoming(Long assigneeId, int limit) {
        if (limit <= 0) limit = 5;
        return taskMapper.findUpcoming(assigneeId, limit);
    }
}
