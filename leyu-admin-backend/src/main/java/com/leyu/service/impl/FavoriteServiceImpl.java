package com.leyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leyu.entity.Favorite;
import com.leyu.entity.Song;
import com.leyu.mapper.FavoriteMapper;
import com.leyu.mapper.SongMapper;
import com.leyu.service.FavoriteService;
import com.leyu.vo.SongVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏服务实现类
 * 实现歌曲收藏的添加、删除、查询等功能
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private SongMapper songMapper;

    @Override
    public List<SongVO> getUserFavorites(Long userId) {
        List<Long> songIds = favoriteMapper.findSongIdsByUserId(userId);
        return songIds.stream()
                .map(songId -> {
                    Song song = songMapper.selectById(songId);
                    if (song == null) return null;
                    SongVO vo = new SongVO();
                    BeanUtils.copyProperties(song, vo);
                    vo.setIsFavorite(true);
                    return vo;
                })
                .filter(vo -> vo != null)
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
