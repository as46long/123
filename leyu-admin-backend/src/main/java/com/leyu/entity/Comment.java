package com.leyu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 留言实体类
 * 对应数据库表: t_comment
 */
@Data
@TableName("t_comment")
public class Comment {
    /** 留言ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 留言内容 */
    private String content;

    /** 点赞数 */
    private Integer likes;

    /** 留言状态: 0-待审核 1-已通过 2-已拒绝 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;
}
