package com.leyu.dto;

import lombok.Data;

/**
 * 收藏数据传输对象
 * 用于添加收藏时的参数传递
 */
@Data
public class FavoriteDTO {
    /** 歌曲ID */
    private Long songId;
}
