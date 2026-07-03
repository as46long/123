package com.leyu.vo;

import lombok.Data;

/**
 * 统一响应结果封装类
 * 用于封装所有API接口的返回数据
 * @param <T> 数据类型
 */
@Data
public class Result<T> {
    /** 响应状态码: 200-成功, 500-失败 */
    private Integer code;

    /** 响应消息 */
    private String message;

    /** 响应数据 */
    private T data;

    /**
     * 返回成功结果(无数据)
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 返回成功结果(带数据)
     * @param data 响应数据
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    /**
     * 返回失败结果
     * @param message 错误消息
     */
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    /**
     * 返回失败结果(自定义状态码)
     * @param code 错误状态码
     * @param message 错误消息
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
