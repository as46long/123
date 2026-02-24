package com.leyu.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SongVO {
    private Long id;
    private String title;
    private String artist;
    private String album;
    private String coverUrl;
    private String audioUrl;
    private String lyricsUrl;
    private Integer duration;
    private String category;
    private Integer isVip;
    private Integer playCount;
    private Integer status;
    private LocalDateTime createTime;
    private Boolean isFavorite;
}
