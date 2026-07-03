package com.leyu.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 留言视图对象
 * 用于返回留言信息给前端
 */
@Data
public class CommentVO {
    /** 留言ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 用户名(关联查询) */
    private String username;

    /** 用户头像(关联查询) */
    private String avatar;

    /** 留言内容 */
    private String content;

    /** 点赞数 */
    private Integer likes;

    /** 留言状态: 0-待审核 1-已通过 2-已拒绝 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 当前用户是否已点赞(可选) */
    private Boolean isLiked;
}
