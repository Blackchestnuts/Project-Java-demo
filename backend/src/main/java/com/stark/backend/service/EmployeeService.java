package com.stark.backend.service;

import com.stark.backend.entity.Employee;
import com.stark.backend.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

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
     * 修改密码
     */
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        Employee employee = employeeMapper.findById(id);
        if (employee == null) {
            throw new RuntimeException("员工不存在");
        }
        if (!employee.getPassword().equals(oldPassword)) {
            throw new RuntimeException("原密码错误");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("新密码不能为空");
        }
        employeeMapper.updatePassword(id, newPassword);
    }
}
