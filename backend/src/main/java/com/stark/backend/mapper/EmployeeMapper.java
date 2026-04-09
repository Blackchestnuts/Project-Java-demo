package com.stark.backend.mapper;

import com.stark.backend.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface EmployeeMapper {
    @Select("SELECT * FROM t_employee")
    List<Employee> findAll();
}