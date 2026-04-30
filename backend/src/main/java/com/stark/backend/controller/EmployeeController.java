package com.stark.backend.controller;

import com.stark.backend.common.Result;
import com.stark.backend.config.JwtUtil;
import com.stark.backend.entity.Employee;
import com.stark.backend.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 查询所有员工
     */
    @GetMapping
    public List<Employee> list() {
        return employeeService.findAll();
    }

    /**
     * 根据ID查询单个员工
     */
    @GetMapping("/{id}")
    public Employee getById(@PathVariable Long id) {
        return employeeService.findById(id);
    }

    /**
     * 新增员工
     */
    @PostMapping
    public String add(@RequestBody Employee employee) {
        // 校验用户名是否重复
        Employee existing = employeeService.findByUsername(employee.getUsername());
        if (existing != null) {
            throw new RuntimeException("用户名已存在");
        }
        employeeService.addEmployee(employee);
        return "新增成功";
    }

    /**
     * 更新员工信息
     */
    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @RequestBody Employee employee) {
        employee.setId(id);
        employeeService.updateEmployee(employee);
        return "修改成功";
    }

    /**
     * 删除员工
     */
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return "删除成功";
    }

    /**
     * 员工登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");

        // 先通过用户名精确查找，避免全表扫描
        Employee user = employeeService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // Use service for password validation (supports BCrypt + plaintext fallback)
        employeeService.validatePassword(user, password);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getName());

        return Result.success("登录成功", Map.of("token", token, "user", user));
    }

    /**
     * 修改密码
     * PUT /api/employees/{id}/password
     * 请求体: { "oldPassword": "xxx", "newPassword": "xxx" }
     */
    @PutMapping("/{id}/password")
    public String updatePassword(@PathVariable Long id, @RequestBody Map<String, String> passwordForm) {
        String oldPassword = passwordForm.get("oldPassword");
        String newPassword = passwordForm.get("newPassword");
        employeeService.updatePassword(id, oldPassword, newPassword);
        return "密码修改成功";
    }
}
