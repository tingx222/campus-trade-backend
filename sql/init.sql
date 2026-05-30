-- ============================================
-- 校园二手交易平台 - 数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS `campus_trade` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `campus_trade`;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `phone`       VARCHAR(11)  NOT NULL COMMENT '手机号',
    `password`    VARCHAR(100) NOT NULL COMMENT '加密密码',
    `nickname`    VARCHAR(20)  DEFAULT NULL COMMENT '昵称',
    `credit_score` INT         NOT NULL DEFAULT 70 COMMENT '信用分',
    `is_auth`     TINYINT      NOT NULL DEFAULT 0 COMMENT '0 未认证 1 已认证',
    `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品表
CREATE TABLE IF NOT EXISTS `goods` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT        NOT NULL COMMENT '卖家用户ID',
    `title`       VARCHAR(50)   NOT NULL COMMENT '商品标题',
    `description` VARCHAR(500)  NOT NULL COMMENT '商品描述',
    `price`       DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    `category`    VARCHAR(20)   NOT NULL COMMENT '商品类别',
    `pics`        VARCHAR(1000) NOT NULL COMMENT '商品图片(多张逗号分隔)',
    `status`      TINYINT       NOT NULL DEFAULT 1 COMMENT '1 上架 2 下架 3 售出',
    `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 订单表
CREATE TABLE IF NOT EXISTS `orders` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT,
    `order_no`    VARCHAR(32)   NOT NULL COMMENT '订单号',
    `goods_id`    BIGINT        NOT NULL COMMENT '商品ID',
    `buyer_id`    BIGINT        NOT NULL COMMENT '买家ID',
    `seller_id`   BIGINT        NOT NULL COMMENT '卖家ID',
    `price`       DECIMAL(10,2) NOT NULL COMMENT '成交价格',
    `address`     VARCHAR(100)  NOT NULL COMMENT '收货地址',
    `status`      TINYINT       NOT NULL COMMENT '1 待付款 2 待发货 3 待收货 4 已完成 5 已取消 6 纠纷中',
    `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_buyer` (`buyer_id`),
    KEY `idx_seller` (`seller_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 评价表
CREATE TABLE IF NOT EXISTS `evaluate` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `order_id`     BIGINT       NOT NULL COMMENT '订单ID',
    `from_user_id` BIGINT       NOT NULL COMMENT '评价人ID',
    `to_user_id`   BIGINT       NOT NULL COMMENT '被评价人ID',
    `score`        INT          NOT NULL COMMENT '1-5星',
    `content`      VARCHAR(200) DEFAULT NULL COMMENT '评价内容',
    `create_time`  DATETIME     DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_user` (`order_id`, `from_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 纠纷表
CREATE TABLE IF NOT EXISTS `dispute` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `order_id`      BIGINT       NOT NULL COMMENT '订单ID',
    `apply_user_id` BIGINT       NOT NULL COMMENT '申请人ID',
    `reason`        VARCHAR(200) NOT NULL COMMENT '申诉理由',
    `evidence`      VARCHAR(1000) DEFAULT NULL COMMENT '证据图片',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '1 待处理 2 处理中 3 已裁决',
    `result`        TINYINT      DEFAULT NULL COMMENT '1 支持买家 2 支持卖家 3 驳回',
    `admin_id`      BIGINT       DEFAULT NULL COMMENT '审核员ID',
    `create_time`   DATETIME     DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='纠纷表';
