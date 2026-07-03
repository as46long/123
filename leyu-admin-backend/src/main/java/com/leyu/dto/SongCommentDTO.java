package com.leyu.dto;

import lombok.Data;

/**
 * 歌曲评论数据传输对象
 * 用于发布歌曲评论时的参数传递
 */
@Data
public class SongCommentDTO {
    /** 歌曲ID */
    private Long songId;

    /** 评论内容 */
    private String content;
}
