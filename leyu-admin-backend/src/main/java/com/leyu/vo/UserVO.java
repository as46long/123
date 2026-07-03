package com.leyu.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户视图对象
 * 用于返回用户信息给前端，不包含敏感字段(如密码)
 */
@Data
public class UserVO {
    /** 用户ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 头像URL */
    private String avatar;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 是否VIP: 0-否 1-是 */
    private Integer isVip;

    /** VIP过期时间 */
    private LocalDateTime vipExpireTime;

    /** 账号状态: 0-禁用 1-正常 */
    private Integer status;

    /** 注册时间 */
    private LocalDateTime createTime;
}
