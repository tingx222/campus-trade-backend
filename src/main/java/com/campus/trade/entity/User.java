package com.campus.trade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long id;

    /** 手机号 */
    private String phone;

    /** 加密密码 */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 信用分 */
    private Integer creditScore;

    /** 是否实名认证：0未认证 1已认证 */
    private Integer isAuth;

    private Integer role;
// getter setter

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
