package com.leyu.dto;

import lombok.Data;

@Data
public class SongDTO {
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
}
