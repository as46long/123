package com.leyu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置实体类
 * 用于存储系统级别的配置参数
 * 对应数据库表: t_system_config
 */
@Data
@TableName("t_system_config")
public class SystemConfig {
    /** 配置ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 配置键，唯一标识 */
    private String configKey;

    /** 配置值 */
    private String configValue;

    /** 配置描述说明 */
    private String description;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
