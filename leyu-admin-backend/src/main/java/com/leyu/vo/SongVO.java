package com.leyu.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 歌曲视图对象
 * 用于返回歌曲信息给前端
 */
@Data
public class SongVO {
    /** 歌曲ID */
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

    /** 播放次数 */
    private Integer playCount;

    /** 歌曲状态: 0-下架 1-上架 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 当前用户是否已收藏(可选) */
    private Boolean isFavorite;
}
