package com.campus.trade.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应封装类（工厂模式）
 * @param <T> 响应数据类型
 */
@Data
public class ResultVO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态码：200成功，其他失败 */
    private int code;

    /** 提示信息 */
    private String message;

    /** 响应数据 */
    private T data;

    private ResultVO(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功（带数据）
     */
    public static <T> ResultVO<T> success(T data) {
        return new ResultVO<>(200, "操作成功", data);
    }

    /**
     * 成功（无数据）
     */
    public static <T> ResultVO<T> success() {
        return new ResultVO<>(200, "操作成功", null);
    }

    /**
     * 成功（自定义消息）
     */
    public static <T> ResultVO<T> success(String message, T data) {
        return new ResultVO<>(200, message, data);
    }

    /**
     * 失败
     */
    public static <T> ResultVO<T> fail(String message) {
        return new ResultVO<>(500, message, null);
    }

    /**
     * 失败（自定义状态码）
     */
    public static <T> ResultVO<T> fail(int code, String message) {
        return new ResultVO<>(code, message, null);
    }
}
