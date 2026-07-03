package com.leyu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 歌曲实体类
 * 对应数据库表: t_song
 */
@Data
@TableName("t_song")
public class Song {
    /** 歌曲ID，自增主键 */
    @TableId(type = IdType.AUTO)
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

    /** 歌曲分类: 流行/摇滚/民谣/古典/电子等 */
    private String category;

    /** 是否VIP歌曲: 0-否 1-是 */
    private Integer isVip;

    /** 播放次数 */
    private Integer playCount;

    /** 歌曲状态: 0-下架 1-上架 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
