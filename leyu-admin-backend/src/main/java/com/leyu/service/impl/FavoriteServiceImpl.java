package com.leyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leyu.entity.Favorite;
import com.leyu.entity.Song;
import com.leyu.mapper.FavoriteMapper;
import com.leyu.mapper.SongMapper;
import com.leyu.service.FavoriteService;
import com.leyu.service.SongService;
import com.leyu.vo.SongVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private SongMapper songMapper;

    @Autowired
    private SongService songService;

    @Override
    public List<SongVO> getUserFavorites(Long userId) {
        List<Long> songIds = favoriteMapper.findSongIdsByUserId(userId);
        return songIds.stream()
                .map(songId -> songService.getVOById(songId, userId))
                .collect(Collectors.toList());
    }

    @Override
    public void add(Long userId, Long songId) {
        if (!isFavorite(userId, songId)) {
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setSongId(songId);
            favoriteMapper.insert(favorite);
        }
    }

    @Override
    public void remove(Long userId, Long songId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getSongId, songId);
        favoriteMapper.delete(wrapper);
    }

    @Override
    public boolean isFavorite(Long userId, Long songId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getSongId, songId);
        return favoriteMapper.selectCount(wrapper) > 0;
    }
}
