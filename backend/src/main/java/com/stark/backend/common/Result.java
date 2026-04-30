package com.stark.backend.common;

/**
 * 统一响应结果类
 * 用于包装所有API返回值，提供统一的响应格式
 */
public class Result<T> {

    private Integer code;   // 状态码：200成功，其他失败
    private String message; // 提示信息
    private T data;         // 数据

    public Result() {}

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功（无数据）
     */
    public static Result<String> success() {
        return new Result<>(200, "操作成功", null);
    }

    /**
     * 成功（带提示信息）
     */
    public static Result<String> success(String message) {
        return new Result<>(200, message, null);
    }

    /**
     * 成功（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 成功（带信息和数据）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 失败
     */
    public static Result<String> error(String message) {
        return new Result<>(500, message, null);
    }

    /**
     * 失败（自定义状态码）
     */
    public static Result<String> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    // Getter & Setter
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
