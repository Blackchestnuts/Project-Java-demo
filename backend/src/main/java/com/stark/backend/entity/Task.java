package com.stark.backend.entity;

import lombok.Data;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class Task {
    private Long id;
    private String title;
    private String description;
    private Integer status;
    private Integer priority;
    
    // 【必须确认这个字段存在】
    private Long assigneeId; 
    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    //更新时间
    private Date updateTime;
    
    // 用于展示名字（非数据库字段）
    private String assigneeName; 
    
    // 【新增】截止日期
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dueDate;

    // 【新增】实际完成时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date completionTime;
    
    
}