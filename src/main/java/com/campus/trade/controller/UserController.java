package com.campus.trade.controller;

import com.campus.trade.common.ResultVO;
import com.campus.trade.entity.User;
import com.campus.trade.service.UserService;
import com.campus.trade.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 * 单一职责：处理用户相关的HTTP请求
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户注册 POST /api/user/register
     */
    @PostMapping("/register")
    public ResultVO<Map<String, Object>> register(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String password = params.get("password");
        String nickname = params.get("nickname");

        if (phone == null || phone.length() != 11) {
            return ResultVO.fail("手机号格式不正确");
        }
        if (password == null || password.length() < 6) {
            return ResultVO.fail("密码长度不能少于6位");
        }

        try {
            User user = userService.register(phone, password, nickname);
            String token = jwtUtil.generateToken(user.getId(), user.getPhone());
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("nickname", user.getNickname());
            data.put("token", token);
            return ResultVO.success("注册成功", data);
        } catch (RuntimeException e) {
            return ResultVO.fail(e.getMessage());
        }
    }

    /**
     * 用户登录 POST /api/user/login
     */
    @PostMapping("/login")
    public ResultVO<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String password = params.get("password");

        if (phone == null || password == null) {
            return ResultVO.fail("手机号和密码不能为空");
        }

        try {
            String token = userService.login(phone, password);
            Long userId = jwtUtil.getUserId(token);
            User user = userService.getUserById(userId);
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", user.getId());
            data.put("nickname", user.getNickname());
            return ResultVO.success("登录成功", data);
        } catch (RuntimeException e) {
            return ResultVO.fail(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息 GET /api/user/info
     */
    @GetMapping("/info")
    public ResultVO getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResultVO.fail(401, "请先登录");
        }
        token = token.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResultVO.fail(401, "登录已过期");
        }
        Long userId = jwtUtil.getUserId(token);
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResultVO.fail(404, "用户不存在");
        }
        // 打印调试
        System.out.println("用户ID: " + userId + ", role: " + user.getRole());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("phone", user.getPhone());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("creditScore", user.getCreditScore());
        userInfo.put("role", user.getRole());
        return ResultVO.success(userInfo);
    }

}
