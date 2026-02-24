package com.leyu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.SongDTO;
import com.leyu.entity.Song;
import com.leyu.vo.SongVO;

import java.util.List;

public interface SongService {
    Song getById(Long id);
    SongVO getVOById(Long id, Long userId);
    Page<SongVO> getPage(int pageNum, int pageSize, String keyword, String category);
    List<SongVO> search(String keyword, Long userId);
    List<SongVO> getRecommend(Long userId, int num);
    List<SongVO> getHot(int limit);
    List<SongVO> getByCategory(String category, Long userId);
    void add(SongDTO dto);
    void update(SongDTO dto);
    void delete(Long id);
    void incrementPlayCount(Long id);
    String getLyrics(Long id);
}
