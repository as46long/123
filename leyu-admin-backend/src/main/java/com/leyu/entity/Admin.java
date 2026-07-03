package com.leyu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 管理员实体类
 * 对应数据库表: t_admin
 */
@Data
@TableName("t_admin")
public class Admin {
    /** 管理员ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名，唯一标识 */
    private String username;

    /** 密码，BCrypt加密存储 */
    private String password;

    /** 角色: ADMIN-管理员 */
    private String role;

    /** 账号状态: 0-禁用 1-正常 */
    private Integer status;

    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;

    /** 创建时间 */
    private LocalDateTime createTime;
}
