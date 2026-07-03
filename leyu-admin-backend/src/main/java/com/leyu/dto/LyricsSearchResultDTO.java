package com.leyu.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 歌词搜索结果DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LyricsSearchResultDTO {
    /**
     * 歌曲ID（来自第三方API）
     */
    private String songId;
    
    /**
     * 歌曲名称
     */
    private String title;
    
    /**
     * 歌手名称
     */
    private String artist;
    
    /**
     * 专辑名称
     */
    private String album;
    
    /**
     * 歌词内容
     */
    private String lyrics;
}
