package com.stark.backend.mapper;

import com.stark.backend.entity.Employee;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 查询所有员工
     */
    @Select("SELECT * FROM t_employee")
    List<Employee> findAll();

    /**
     * 根据ID查询员工
     */
    @Select("SELECT * FROM t_employee WHERE id = #{id}")
    Employee findById(Long id);

    /**
     * 根据用户名查询员工（用于登录和注册时校验）
     */
    @Select("SELECT * FROM t_employee WHERE username = #{username}")
    Employee findByUsername(String username);

    /**
     * 新增员工
     */
    @Insert("INSERT INTO t_employee (username, name, department, password) " +
            "VALUES (#{username}, #{name}, #{department}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Employee employee);

    /**
     * 更新员工信息（不修改密码和用户名）
     */
    @Update("UPDATE t_employee SET name=#{name}, department=#{department} WHERE id=#{id}")
    void update(Employee employee);

    /**
     * 删除员工
     */
    @Delete("DELETE FROM t_employee WHERE id=#{id}")
    void deleteById(Long id);

    /**
     * 修改密码
     */
    @Update("UPDATE t_employee SET password=#{password} WHERE id=#{id}")
    void updatePassword(@Param("id") Long id, @Param("password") String password);
}
