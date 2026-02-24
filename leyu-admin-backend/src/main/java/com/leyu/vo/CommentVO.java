package com.leyu.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentVO {
    private Long id;
    private Long userId;
    private String username;
    private String avatar;
    private String content;
    private Integer likes;
    private Integer status;
    private LocalDateTime createTime;
    private Boolean isLiked;
}
