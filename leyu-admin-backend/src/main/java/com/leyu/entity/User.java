package com.leyu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库表: t_user
 */
@Data
@TableName("t_user")
public class User {
    /** 用户ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名，唯一标识 */
    private String username;

    /** 密码，BCrypt加密存储 */
    private String password;

    /** 用户昵称 */
    private String nickname;

    /** 头像URL */
    private String avatar;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 微信OpenID，用于微信登录 */
    private String wxOpenid;

    /** 是否VIP用户: 0-否 1-是 */
    private Integer isVip;

    /** VIP过期时间 */
    private LocalDateTime vipExpireTime;

    /** 账号状态: 0-禁用 1-正常 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
