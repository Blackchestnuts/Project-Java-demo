package com.stark.backend.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Task {
    private Long id;
    private String title;
    private String description;
    private Integer status;
    private Integer priority;
    
    // 【必须确认这个字段存在】
    private Long assigneeId; 
    
    private Date createTime;
    private Date updateTime;
    
    // 用于展示名字（非数据库字段）
    private String assigneeName; 
}