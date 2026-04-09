package com.stark.backend.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Employee {
    private Long id;
    private String username;
    private String name;
    private String department;
    private Date createTime;
    
    // 【新增】密码字段
    private String password;
}