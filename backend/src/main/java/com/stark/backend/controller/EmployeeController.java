package com.stark.backend.controller;

import com.stark.backend.entity.Employee;
import com.stark.backend.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map; // 必须引入 Map

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeMapper employeeMapper;

    @GetMapping
    public List<Employee> list() {
        return employeeMapper.findAll();
    }

    // 修正后的登录方法
    @PostMapping("/login")
    public Employee login(@RequestBody Map<String, String> loginForm) {
        // 1. 从 Map 中获取前端传来的数据
        String username = loginForm.get("username");
        String password = loginForm.get("password"); // 注意：这里定义了 password

        // 2. 查找用户
        List<Employee> users = employeeMapper.findAll();
        Employee user = users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 3. 校验密码
        // 这里必须使用上面定义的 password 变量，而不是不存在的 inputPassword
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }
        
        return user;
    }
}