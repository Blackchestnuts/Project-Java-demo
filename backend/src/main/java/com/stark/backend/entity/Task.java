package com.stark.backend.entity;

import lombok.Data;
import java.util.Date;

@Data // Lombok注解，自动生成 getter/setter/toString，非常省事
public class Task {
    private Long id;
    private String title;
    private String description;
    private Integer status;      // 0-待办 1-进行中 2-已完成
    private Integer priority;    // 1-低 2-中 3-高
    private Long assigneeId;     // 执行人ID
    private Date createTime;
    private Date updateTime;
}