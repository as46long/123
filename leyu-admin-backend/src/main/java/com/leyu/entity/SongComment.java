package com.leyu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 歌曲评论实体类
 * 对应数据库表: t_song_comment
 */
@Data
@TableName("t_song_comment")
public class SongComment {
    /** 评论ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 歌曲ID */
    private Long songId;

    /** 用户ID */
    private Long userId;

    /** 评论内容 */
    private String content;

    /** 点赞数 */
    private Integer likes;

    /** 评论状态: 0-待审核 1-已通过 2-已拒绝 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
