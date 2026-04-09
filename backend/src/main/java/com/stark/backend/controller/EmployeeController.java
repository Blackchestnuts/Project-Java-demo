package com.stark.backend.controller;

import com.stark.backend.entity.Employee;
import com.stark.backend.mapper.EmployeeMapper; // 稍后需创建这个 Mapper
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeMapper employeeMapper;

    @GetMapping
    public List<Employee> list() {
        return employeeMapper.findAll();
    }
}