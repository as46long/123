package com.leyu.service;

import com.leyu.dto.LyricsSearchResultDTO;

import java.util.List;

/**
 * 歌词API服务接口
 */
public interface LyricsApiService {
    
    /**
     * 在线搜索歌词
     * @param keyword 搜索关键词（歌曲名或歌手名）
     * @return 歌词搜索结果列表
     */
    List<LyricsSearchResultDTO> searchLyrics(String keyword);
    
    /**
     * 根据歌曲ID获取歌词详情
     * @param songId 歌曲ID
     * @return 歌词内容
     */
    String getLyricsById(String songId);
}
