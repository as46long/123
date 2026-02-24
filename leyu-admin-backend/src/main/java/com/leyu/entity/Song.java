package com.leyu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_song")
public class Song {
    @TableId(type = IdType.AUTO)
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
    private LocalDateTime updateTime;
}
