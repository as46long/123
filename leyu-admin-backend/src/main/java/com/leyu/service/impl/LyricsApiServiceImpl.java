package com.leyu.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyu.dto.LyricsSearchResultDTO;
import com.leyu.service.LyricsApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * 歌词API服务实现
 * 使用网易云音乐API进行歌词搜索
 */
@Slf4j
@Service
public class LyricsApiServiceImpl implements LyricsApiService {
    
    // 使用公开的音乐API服务（这里使用网易云音乐API的公开服务）
    private static final String API_BASE_URL = "https://music.163.com/api";
    private static final String SEARCH_URL = API_BASE_URL + "/search/get";
    private static final String LYRICS_URL = API_BASE_URL + "/song/lyric";
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public LyricsApiServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public List<LyricsSearchResultDTO> searchLyrics(String keyword) {
        List<LyricsSearchResultDTO> results = new ArrayList<>();
        
        try {
            // 构建搜索URL
            String url = UriComponentsBuilder.fromHttpUrl(SEARCH_URL)
                    .queryParam("s", keyword)
                    .queryParam("type", "1") // 1: 单曲
                    .queryParam("limit", "10")
                    .toUriString();
            
            log.info("搜索歌词: {}", url);
            
            // 发送请求
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null) {
                JsonNode root = objectMapper.readTree(response);
                JsonNode songs = root.path("result").path("songs");
                
                if (songs.isArray()) {
                    for (JsonNode song : songs) {
                        LyricsSearchResultDTO dto = new LyricsSearchResultDTO();
                        dto.setSongId(song.path("id").asText());
                        dto.setTitle(song.path("name").asText());
                        
                        // 获取歌手名称
                        JsonNode artists = song.path("artists");
                        if (artists.isArray() && artists.size() > 0) {
                            StringBuilder artistNames = new StringBuilder();
                            for (int i = 0; i < artists.size(); i++) {
                                if (i > 0) artistNames.append("、");
                                artistNames.append(artists.get(i).path("name").asText());
                            }
                            dto.setArtist(artistNames.toString());
                        }
                        
                        // 获取专辑名称
                        JsonNode album = song.path("album");
                        if (!album.isMissingNode()) {
                            dto.setAlbum(album.path("name").asText());
                        }
                        
                        results.add(dto);
                    }
                }
            }
            
            log.info("搜索到 {} 条歌词结果", results.size());
            
        } catch (Exception e) {
            log.error("搜索歌词失败: {}", e.getMessage(), e);
        }
        
        return results;
    }
    
    @Override
    public String getLyricsById(String songId) {
        try {
            // 构建歌词URL
            String url = UriComponentsBuilder.fromHttpUrl(LYRICS_URL)
                    .queryParam("id", songId)
                    .queryParam("lv", "1")
                    .toUriString();
            
            log.info("获取歌词详情: {}", url);
            
            // 发送请求
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null) {
                JsonNode root = objectMapper.readTree(response);
                JsonNode lrc = root.path("lrc");
                
                if (!lrc.isMissingNode()) {
                    String lyrics = lrc.path("lyric").asText();
                    log.info("成功获取歌词，长度: {}", lyrics.length());
                    return lyrics;
                }
            }
            
        } catch (Exception e) {
            log.error("获取歌词失败: {}", e.getMessage(), e);
        }
        
        return "";
    }
}
