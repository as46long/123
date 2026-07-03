package com.leyu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.SongDTO;
import com.leyu.service.SongService;
import com.leyu.utils.JwtUtil;
import com.leyu.vo.Result;
import com.leyu.vo.SongVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 歌曲控制器
 * 处理歌曲的增删改查、搜索、推荐、分类、歌词管理等请求
 */
@RestController
@RequestMapping("/api/song")
@Tag(name = "歌曲管理")
public class SongController {

    @Autowired
    private SongService songService;

    @Autowired
    private JwtUtil jwtUtil;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取歌曲分页列表
     * @param pageNum 页码，默认1
     * @param pageSize 每页数量，默认10
     * @param keyword 搜索关键词(可选)
     * @param category 歌曲分类(可选)
     * @return 歌曲分页数据
     */
    @GetMapping("/list")
    @Operation(summary = "歌曲列表")
    public Result<Page<SongVO>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return Result.success(songService.getPage(pageNum, pageSize, keyword, category));
    }

    /**
     * 获取歌曲详情
     * @param id 歌曲ID
     * @param token JWT令牌(可选，用于判断是否已收藏)
     * @return 歌曲详细信息
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "获取详情")
    public Result<SongVO> getById(@PathVariable Long id,
                                   @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = null;
        if (token != null && token.startsWith("Bearer ")) {
            userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        }
        return Result.success(songService.getVOById(id, userId));
    }

    /**
     * 添加新歌曲
     * @param dto 歌曲信息DTO
     * @return 添加结果
     */
    @PostMapping("/add")
    @Operation(summary = "添加歌曲")
    public Result<Void> add(@RequestBody SongDTO dto) {
        songService.add(dto);
        return Result.success();
    }

    /**
     * 更新歌曲信息
     * @param dto 歌曲更新DTO
     * @return 更新结果
     */
    @PutMapping("/update")
    @Operation(summary = "更新歌曲")
    public Result<Void> update(@RequestBody SongDTO dto) {
        songService.update(dto);
        return Result.success();
    }

    /**
     * 删除歌曲
     * @param id 歌曲ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除歌曲")
    public Result<Void> delete(@PathVariable Long id) {
        songService.delete(id);
        return Result.success();
    }

    /**
     * 搜索歌曲
     * @param keyword 搜索关键词
     * @param token JWT令牌(可选)
     * @return 匹配的歌曲列表
     */
    @GetMapping("/search")
    @Operation(summary = "搜索歌曲")
    public Result<List<SongVO>> search(@RequestParam String keyword,
                                        @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = null;
        if (token != null && token.startsWith("Bearer ")) {
            userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        }
        return Result.success(songService.search(keyword, userId));
    }

    /**
     * 获取推荐歌曲
     * 基于用户行为的协同过滤推荐算法
     * @param userId 用户ID(可选)
     * @param num 推荐数量，默认10
     * @return 推荐歌曲列表
     */
    @GetMapping("/recommend")
    @Operation(summary = "推荐歌曲")
    public Result<List<SongVO>> recommend(@RequestParam(required = false) Long userId,
                                          @RequestParam(defaultValue = "10") int num) {
        return Result.success(songService.getRecommend(userId, num));
    }

    /**
     * 获取热门歌曲
     * @param limit 返回数量，默认10
     * @return 热门歌曲列表(按播放量排序)
     */
    @GetMapping("/hot")
    @Operation(summary = "热门歌曲")
    public Result<List<SongVO>> hot(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(songService.getHot(limit));
    }

    /**
     * 按分类获取歌曲
     * @param category 歌曲分类名称
     * @param token JWT令牌(可选)
     * @return 该分类下的歌曲列表
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "分类歌曲")
    public Result<List<SongVO>> byCategory(@PathVariable String category,
                                            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = null;
        if (token != null && token.startsWith("Bearer ")) {
            userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        }
        return Result.success(songService.getByCategory(category, userId));
    }

    /**
     * 记录歌曲播放
     * 同时更新播放次数和用户行为记录
     * @param id 歌曲ID
     * @param token JWT令牌(可选)
     * @return 操作结果
     */
    @PostMapping("/play/{id}")
    @Operation(summary = "记录播放")
    public Result<Void> play(@PathVariable Long id,
                             @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = null;
        if (token != null && token.startsWith("Bearer ")) {
            userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        }
        songService.incrementPlayCount(id, userId);
        return Result.success();
    }

    /**
     * 获取歌曲歌词
     * @param id 歌曲ID
     * @return 歌词内容
     */
    @GetMapping("/lyrics/{id}")
    @Operation(summary = "获取歌词")
    public Result<String> getLyrics(@PathVariable Long id) {
        return Result.success(songService.getLyrics(id));
    }

    /**
     * 更新歌曲歌词
     * @param id 歌曲ID
     * @param body 包含歌词内容的请求体
     * @return 更新结果
     */
    @PutMapping("/lyrics/{id}")
    @Operation(summary = "更新歌词")
    public Result<Void> updateLyrics(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String lyrics = body.get("lyrics");
        songService.updateLyrics(id, lyrics);
        return Result.success();
    }

    /**
     * 搜索网络歌词
     * 调用网易云音乐API搜索歌词
     * @param keyword 搜索关键词(歌曲名+歌手)
     * @return 歌曲列表(包含id、name、artist、album)
     */
    @GetMapping("/lyrics/search")
    @Operation(summary = "搜索网络歌词")
    public Result<List<Map<String, Object>>> searchLyrics(@RequestParam String keyword) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            String url = "https://music.163.com/api/search/get/web?s=" + keyword + "&type=1&limit=10";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            headers.set("Referer", "https://music.163.com");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode songs = root.path("result").path("songs");
            
            if (songs.isArray()) {
                for (JsonNode song : songs) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", song.path("id").asText());
                    item.put("name", song.path("name").asText());
                    
                    StringBuilder artists = new StringBuilder();
                    JsonNode artistNode = song.path("artists");
                    if (artistNode.isArray()) {
                        for (int i = 0; i < artistNode.size(); i++) {
                            if (i > 0) artists.append(",");
                            artists.append(artistNode.get(i).path("name").asText());
                        }
                    }
                    item.put("artist", artists.toString());
                    item.put("album", song.path("album").path("name").asText());
                    
                    results.add(item);
                }
            }
        } catch (Exception e) {
            System.err.println("搜索歌词失败: " + e.getMessage());
        }
        
        return Result.success(results);
    }

    /**
     * 获取网络歌词内容
     * 根据网易云音乐歌曲ID获取LRC格式歌词
     * @param songId 网易云音乐歌曲ID
     * @return LRC格式歌词内容
     */
    @GetMapping("/lyrics/fetch/{songId}")
    @Operation(summary = "获取网络歌词内容")
    public Result<String> fetchLyrics(@PathVariable String songId) {
        try {
            String url = "https://music.163.com/api/song/lyric?id=" + songId + "&lv=1";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            headers.set("Referer", "https://music.163.com");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode lrcNode = root.path("lrc");
            
            if (!lrcNode.isMissingNode()) {
                JsonNode lyricText = lrcNode.path("lyric");
                if (!lyricText.isMissingNode() && lyricText.isTextual()) {
                    String lyrics = lyricText.asText();
                    return Result.success(lyrics);
                }
            }
        } catch (Exception e) {
            System.err.println("获取歌词失败: " + e.getMessage());
        }
        
        return Result.success("");
    }
}
