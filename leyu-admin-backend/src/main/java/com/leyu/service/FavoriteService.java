package com.leyu.service;

import com.leyu.vo.SongVO;

import java.util.List;

public interface FavoriteService {
    List<SongVO> getUserFavorites(Long userId);
    void add(Long userId, Long songId);
    void remove(Long userId, Long songId);
    boolean isFavorite(Long userId, Long songId);
}
