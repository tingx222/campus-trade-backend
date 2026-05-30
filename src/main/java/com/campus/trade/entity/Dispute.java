package com.campus.trade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Dispute implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long orderId;
    private Long goodsId;
    private Long applicantId;
    private Long respondentId;
    private String reason;
    /** 1-买家发起 2-卖家发起 */
    private Integer type;
    /** 1-待处理 2-处理中 3-已解决 4-已关闭 */
    private Integer status;
    private String result;
    private Long handlerId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 非数据库字段
    private String orderNo;
    private String goodsTitle;
    private String goodsPics;
    private String applicantNickname;
    private String respondentNickname;
}