package com.stark.backend.service;

import com.stark.backend.entity.Employee;
import com.stark.backend.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 查询所有员工
     */
    public List<Employee> findAll() {
        return employeeMapper.findAll();
    }

    /**
     * 根据ID查询员工
     */
    public Employee findById(Long id) {
        return employeeMapper.findById(id);
    }

    /**
     * 根据用户名查询员工（登录和注册校验用）
     */
    public Employee findByUsername(String username) {
        return employeeMapper.findByUsername(username);
    }

    /**
     * 新增员工
     */
    public void addEmployee(Employee employee) {
        // 设置默认密码（如果未提供）
        if (employee.getPassword() == null || employee.getPassword().trim().isEmpty()) {
            employee.setPassword("123456");
        }
        // Encode password with BCrypt
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employeeMapper.insert(employee);
    }

    /**
     * 更新员工信息（不修改密码）
     */
    public void updateEmployee(Employee employee) {
        employeeMapper.update(employee);
    }

    /**
     * 删除员工
     */
    public void deleteEmployee(Long id) {
        employeeMapper.deleteById(id);
    }

    /**
     * 验证密码（支持BCrypt和明文密码）
     */
    public void validatePassword(Employee user, String rawPassword) {
        // Support both BCrypt and plaintext passwords for backward compatibility
        boolean passwordMatches;
        if (user.getPassword().startsWith("$2a$") || user.getPassword().startsWith("$2b$")) {
            passwordMatches = passwordEncoder.matches(rawPassword, user.getPassword());
        } else {
            // Legacy plaintext comparison
            passwordMatches = user.getPassword().equals(rawPassword);
            // Upgrade to BCrypt on successful login
            if (passwordMatches) {
                employeeMapper.updatePassword(user.getId(), passwordEncoder.encode(rawPassword));
            }
        }
        if (!passwordMatches) {
            throw new RuntimeException("密码错误");
        }
    }

    /**
     * 修改密码
     */
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        Employee employee = employeeMapper.findById(id);
        if (employee == null) {
            throw new RuntimeException("员工不存在");
        }
        // Validate old password using the same BCrypt/plaintext logic
        validatePassword(employee, oldPassword);
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("新密码不能为空");
        }
        // Encode new password with BCrypt
        employeeMapper.updatePassword(id, passwordEncoder.encode(newPassword));
    }
}
