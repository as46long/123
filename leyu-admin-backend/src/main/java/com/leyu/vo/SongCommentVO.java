package com.leyu.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 歌曲评论视图对象
 * 用于返回歌曲评论信息给前端
 */
@Data
public class SongCommentVO {
    /** 评论ID */
    private Long id;

    /** 歌曲ID */
    private Long songId;

    /** 用户ID */
    private Long userId;

    /** 用户名(关联查询) */
    private String username;

    /** 用户头像(关联查询) */
    private String avatar;

    /** 评论内容 */
    private String content;

    /** 点赞数 */
    private Integer likes;

    /** 评论状态: 0-待审核 1-已通过 2-已拒绝 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 当前用户是否已点赞(可选) */
    private Boolean isLiked;

    /** 歌曲名称(关联查询) */
    private String songTitle;

    /** 歌手名称(关联查询) */
    private String songArtist;

    /** 歌曲分类(关联查询) */
    private String songCategory;
}
