package com.campus.trade.service.impl;

import com.campus.trade.entity.User;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.service.UserService;
import com.campus.trade.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 * 单一职责：仅处理用户相关业务逻辑
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserMapper userMapper, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public User register(String phone, String password, String nickname) {
        // 检查手机号是否已注册
        User existing = userMapper.findByPhone(phone);
        if (existing != null) {
            throw new RuntimeException("该手机号已注册");
        }

        User user = new User();
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname != null ? nickname : "用户" + phone.substring(7));
        user.setCreditScore(70);
        user.setIsAuth(0);
        user.setCreateTime(LocalDateTime.now());

        userMapper.insert(user);
        // 插入后 id 会回填到 user 对象
        return user;
    }

    @Override
    public String login(String phone, String password) {
        User user = userMapper.findByPhone(phone);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return jwtUtil.generateToken(user.getId(), user.getPhone());
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.findById(id);
    }
}
