package com.stark.backend.mapper;

import com.stark.backend.entity.Task;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TaskMapper {

    // // 查询所有任务
    // @Select("SELECT * FROM t_task")
    // List<Task> findAll();

    // 替换原有的 findAll 方法
    @Select("SELECT t.*, e.name as assignee_name " +
            "FROM t_task t " +
            "LEFT JOIN t_employee e ON t.assignee_id = e.id")
    List<Task> findAll();

    // 根据ID查询任务
    @Select("SELECT * FROM t_task WHERE id = #{id}")
    Task findById(Long id);

    // 新增任务
    @Insert("INSERT INTO t_task (title, description, status, priority, assignee_id) " +
            "VALUES (#{title}, #{description}, #{status}, #{priority}, #{assigneeId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Task task);

    // 更新任务状态
    @Update("UPDATE t_task SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    void updateStatus(Task task);
    // 在 TaskMapper.java 中新增以下方法

    // 编辑任务详情
    @Update("UPDATE t_task SET title=#{title}, description=#{description}, priority=#{priority}, update_time=NOW() WHERE id=#{id}")
    void update(Task task);

    // 删除任务
    @Delete("DELETE FROM t_task WHERE id=#{id}")
    void deleteById(Long id);
}