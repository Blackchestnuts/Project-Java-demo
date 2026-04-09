package com.stark.backend.mapper;

import com.stark.backend.entity.Task;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TaskMapper {

    // 1. 查询所有（带创建人姓名）
    @Select("SELECT t.*, e.name as assignee_name FROM t_task t LEFT JOIN t_employee e ON t.assignee_id = e.id")
    List<Task> findAll();

    // 2. 搜索（模糊查询）
    @Select("SELECT t.*, e.name as assignee_name FROM t_task t LEFT JOIN t_employee e ON t.assignee_id = e.id " +
            "WHERE t.title LIKE CONCAT('%', #{keyword}, '%') OR t.description LIKE CONCAT('%', #{keyword}, '%')")
    List<Task> search(@Param("keyword") String keyword);

    // 3. 新增
    @Insert("INSERT INTO t_task (title, description, status, priority, assignee_id) " +
            "VALUES (#{title}, #{description}, #{status}, #{priority}, #{assigneeId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Task task);

    // 4. 编辑
    @Update("UPDATE t_task SET title=#{title}, description=#{description}, priority=#{priority}, update_time=NOW() WHERE id=#{id}")
    void update(Task task);

    // 5. 单条状态更新
    @Update("UPDATE t_task SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    void updateStatus(Task task);

    // 6. 单条删除
    @Delete("DELETE FROM t_task WHERE id=#{id}")
    void deleteById(Long id);

    // 7. 批量删除
    @Delete("<script>" +
            "DELETE FROM t_task WHERE id IN " +
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    void deleteByIds(@Param("ids") List<Long> ids);

    // 8. 批量完成
    @Update("<script>" +
            "UPDATE t_task SET status=#{status}, update_time=NOW() WHERE id IN " +
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    void updateStatusByIds(@Param("ids") List<Long> ids, @Param("status") Integer status);
}