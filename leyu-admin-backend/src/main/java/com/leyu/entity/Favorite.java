package com.leyu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 收藏实体类
 * 对应数据库表: t_favorite
 */
@Data
@TableName("t_favorite")
public class Favorite {
    /** 收藏ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 歌曲ID */
    private Long songId;

    /** 收藏时间 */
    private LocalDateTime createTime;
}
