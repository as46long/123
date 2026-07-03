package com.leyu.service;

import com.leyu.vo.SongVO;

import java.util.List;

/**
 * 收藏服务接口
 * 提供歌曲收藏的添加、删除、查询等功能
 */
public interface FavoriteService {
    /**
     * 获取用户收藏的歌曲列表
     * @param userId 用户ID
     * @return 收藏的歌曲列表
     */
    List<SongVO> getUserFavorites(Long userId);

    /**
     * 添加收藏
     * @param userId 用户ID
     * @param songId 歌曲ID
     */
    void add(Long userId, Long songId);

    /**
     * 取消收藏
     * @param userId 用户ID
     * @param songId 歌曲ID
     */
    void remove(Long userId, Long songId);

    /**
     * 判断用户是否已收藏某歌曲
     * @param userId 用户ID
     * @param songId 歌曲ID
     * @return 是否已收藏
     */
    boolean isFavorite(Long userId, Long songId);
}
