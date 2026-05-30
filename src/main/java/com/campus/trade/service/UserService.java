package com.campus.trade.service;

import com.campus.trade.entity.User;

/**
 * 用户服务接口（面向接口编程 - 依赖倒置原则）
 */
public interface UserService {

    /**
     * 用户注册
     */
    User register(String phone, String password, String nickname);

    /**
     * 用户登录
     */
    String login(String phone, String password);

    /**
     * 根据ID查询用户
     */
    User getUserById(Long id);
}
