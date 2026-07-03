package com.leyu.dto;

import lombok.Data;

/**
 * 歌曲数据传输对象
 * 用于歌曲的添加和更新操作
 */
@Data
public class SongDTO {
    /** 歌曲ID(更新时必填) */
    private Long id;

    /** 歌曲名称 */
    private String title;

    /** 歌手/艺术家 */
    private String artist;

    /** 所属专辑 */
    private String album;

    /** 封面图片URL */
    private String coverUrl;

    /** 音频文件URL */
    private String audioUrl;

    /** 歌词文件URL */
    private String lyricsUrl;

    /** 歌词内容 */
    private String lyrics;

    /** 歌曲时长(秒) */
    private Integer duration;

    /** 歌曲分类 */
    private String category;

    /** 是否VIP歌曲: 0-否 1-是 */
    private Integer isVip;
}
