package com.leyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.SongDTO;
import com.leyu.entity.Song;
import com.leyu.entity.UserBehavior;
import com.leyu.mapper.SongMapper;
import com.leyu.mapper.UserBehaviorMapper;
import com.leyu.recommendation.ItemBasedCF;
import com.leyu.service.FavoriteService;
import com.leyu.service.SongService;
import com.leyu.vo.SongVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 歌曲服务实现类
 * 实现歌曲的增删改查、搜索、推荐、播放记录等功能
 */
@Slf4j
@Service
public class SongServiceImpl implements SongService {

    @Autowired
    private SongMapper songMapper;

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private ItemBasedCF itemBasedCF;

    @Override
    public Song getById(Long id) {
        return songMapper.selectById(id);
    }

    @Override
    public SongVO getVOById(Long id, Long userId) {
        Song song = getById(id);
        if (song == null || song.getStatus() != 1) return null;
        SongVO vo = convertToVO(song);
        if (userId != null) {
            vo.setIsFavorite(favoriteService.isFavorite(userId, id));
        }
        return vo;
    }

    @Override
    public Page<SongVO> getPage(int pageNum, int pageSize, String keyword, String category) {
        Page<Song> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Song> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Song::getStatus, 1);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Song::getTitle, keyword).or().like(Song::getArtist, keyword));
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(Song::getCategory, category);
        }
        wrapper.orderByDesc(Song::getCreateTime);
        Page<Song> songPage = songMapper.selectPage(page, wrapper);
        Page<SongVO> voPage = new Page<>(pageNum, pageSize, songPage.getTotal());
        voPage.setRecords(songPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public List<SongVO> search(String keyword, Long userId) {
        LambdaQueryWrapper<Song> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Song::getStatus, 1);
        wrapper.and(w -> w.like(Song::getTitle, keyword).or().like(Song::getArtist, keyword).or().like(Song::getAlbum, keyword).or().like(Song::getCategory, keyword));
        List<Song> songs = songMapper.selectList(wrapper);
        return songs.stream().map(song -> {
            SongVO vo = convertToVO(song);
            if (userId != null) {
                vo.setIsFavorite(favoriteService.isFavorite(userId, song.getId()));
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SongVO> getRecommend(Long userId, int num) {
        List<Long> songIds = itemBasedCF.recommendForUser(userId, num);
        if (songIds.isEmpty()) {
            return getHot(num);
        }
        return songIds.stream()
                .map(id -> getVOById(id, userId))
                .filter(vo -> vo != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<SongVO> getHot(int limit) {
        List<Song> songs = songMapper.findHotSongs(limit);
        return songs.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<SongVO> getByCategory(String category, Long userId) {
        List<Song> songs = songMapper.findByCategory(category);
        return songs.stream().map(song -> {
            SongVO vo = convertToVO(song);
            if (userId != null) {
                vo.setIsFavorite(favoriteService.isFavorite(userId, song.getId()));
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void add(SongDTO dto) {
        Song song = new Song();
        BeanUtils.copyProperties(dto, song);
        song.setPlayCount(0);
        song.setStatus(1);
        songMapper.insert(song);
    }

    @Override
    public void update(SongDTO dto) {
        Song song = new Song();
        BeanUtils.copyProperties(dto, song);
        songMapper.updateById(song);
    }

    @Override
    public void delete(Long id) {
        Song song = new Song();
        song.setId(id);
        song.setStatus(0);
        songMapper.updateById(song);
    }

    @Override
    public void incrementPlayCount(Long id, Long userId) {
        // 更新歌曲播放次数
        Song song = getById(id);
        if (song != null) {
            song.setPlayCount(song.getPlayCount() + 1);
            songMapper.updateById(song);
        }
        
       // 记录用户播放行为（用于统计今日播放量和推荐算法）
        // 无论用户是否登录，都记录播放行为（用于统计今日播放量）
        try {
            UserBehavior behavior = new UserBehavior();
            behavior.setUserId(userId); // 未登录时为null
            behavior.setSongId(id);
            behavior.setBehaviorType("play");
            behavior.setScore(1);
            userBehaviorMapper.insert(behavior);
            log.info("记录播放行为: userId={}, songId={}", userId, id);
        } catch (Exception e) {
            log.error("记录播放行为失败", e);
        }
    }

    @Override
    public String getLyrics(Long id) {
        Song song = getById(id);
        if (song != null && StringUtils.hasText(song.getLyrics())) {
            return song.getLyrics();
        }
        return "";
    }

    @Override
    public void updateLyrics(Long id, String lyrics) {
        Song song = getById(id);
        if (song != null) {
            song.setLyrics(lyrics);
            songMapper.updateById(song);
        }
    }

    private SongVO convertToVO(Song song) {
        SongVO vo = new SongVO();
        BeanUtils.copyProperties(song, vo);
        return vo;
    }

    @Override
    public List<String> getAllCategories() {
        try {
            LambdaQueryWrapper<Song> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Song::getStatus, 1);
            wrapper.select(Song::getCategory);
            wrapper.isNotNull(Song::getCategory);
            wrapper.groupBy(Song::getCategory);
            List<Song> songs = songMapper.selectList(wrapper);
            return songs.stream().map(Song::getCategory).collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("获取歌曲分类异常: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
