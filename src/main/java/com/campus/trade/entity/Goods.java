package com.campus.trade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 */
@Data
public class Goods implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 商品ID */
    private Long id;

    /** 卖家用户ID */
    private Long userId;

    /** 商品标题 */
    private String title;

    /** 商品描述 */
    private String description;

    /** 商品价格 */
    private BigDecimal price;

    /** 商品类别 */
    private String category;

    /** 商品图片（多张逗号分隔） */
    private String pics;

    /** 商品状态：1上架 2下架 3售出 */
    private Integer status;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // ========== 非数据库字段，用于关联查询 ==========

    /** 卖家昵称 */
    private String sellerNickname;

    /** 卖家信用分 */
    private Integer sellerCreditScore;
}
