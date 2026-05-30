package com.campus.trade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 校园二手交易平台 - 后端API服务主启动类
 * 完全前后端分离架构
 */
@SpringBootApplication
@MapperScan("com.campus.trade.mapper")
public class CampusTradeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusTradeApplication.class, args);
        System.out.println("========================================");
        System.out.println("  校园二手交易平台 - 后端API服务启动成功！");
        System.out.println("  API地址: http://localhost:8080/api");
        System.out.println("  支持跨域: 已启用（完全前后端分离）");
        System.out.println("========================================");
    }
}
