package com.leyu.service.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网易云音乐API服务
 * 用于在线搜索歌曲和获取歌词
 */
@Slf4j
@Service
public class NetEaseMusicService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // 网易云音乐API地址
    private static final String SEARCH_URL = "https://music.163.com/api/search/get";
    private static final String LYRIC_URL = "https://music.163.com/api/song/lyric";
    
    private static final String REFERER = "https://music.163.com";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    public NetEaseMusicService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 在线搜索歌曲
     * @param keyword 搜索关键词（歌曲名 作者名）
     * @param limit 返回数量
     * @return 搜索结果列表，包含歌曲ID、名称、歌手、专辑信息
     */
    public List<Map<String, Object>> searchSongs(String keyword, int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        if (keyword == null || keyword.trim().isEmpty()) {
            log.warn("搜索关键词为空");
            return results;
        }
        
        try {
            // 构建搜索URL - 使用网易云音乐搜索接口
            // type=1 表示搜索单曲
            String encodedKeyword = URLEncoder.encode(keyword.trim(), StandardCharsets.UTF_8);
            String url = SEARCH_URL + "?s=" + encodedKeyword + "&type=1&limit=" + limit + "&offset=0";

            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            log.info("在线搜索歌曲: keyword={}", keyword);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            String responseBody = response.getBody();
            if (responseBody == null || responseBody.isEmpty()) {
                log.warn("搜索API返回空响应");
                return results;
            }
            
            JsonNode root = objectMapper.readTree(responseBody);
            int code = root.path("code").asInt(-1);

            if (code != 200) {
                log.warn("搜索API返回错误码: {}", code);
                return results;
            }
            
            JsonNode result = root.path("result");
            JsonNode songs = result.path("songs");
            
            if (!songs.isArray() || songs.isEmpty()) {
                log.info("未找到匹配的歌曲: keyword={}", keyword);
                return results;
            }
            
            // 解析搜索结果
            for (JsonNode song : songs) {
                Map<String, Object> item = new HashMap<>();
                
                // 网易云歌曲ID（用于获取歌词）
                long songId = song.path("id").asLong();
                item.put("id", songId);
                item.put("name", song.path("name").asText("").trim());

                // 获取歌手名（可能有多个歌手）
                StringBuilder artistName = new StringBuilder();
                JsonNode artists = song.path("artists");
                if (artists.isArray()) {
                    for (int i = 0; i < artists.size(); i++) {
                        if (i > 0) artistName.append(" ");
                        artistName.append(artists.get(i).path("name").asText(""));
                    }
                }
                item.put("artist", artistName.toString().trim());

                // 获取专辑名
                JsonNode album = song.path("album");
                String albumName = album.isMissingNode() ? "" : album.path("name").asText("");
                item.put("album", albumName.trim());
                
                // 标记来源为网易云
                item.put("source", "netease");

                results.add(item);
            }
            
            log.info("搜索到 {} 首歌曲: keyword={}", results.size(), keyword);
            
        } catch (Exception e) {
            log.error("在线搜索歌曲异常: keyword={}, error={}", keyword, e.getMessage(), e);
        }
        
        return results;
    }

    /**
     * 获取歌曲歌词
     * @param neteaseSongId 网易云歌曲ID（注意：这是网易云的歌曲ID，不是本地数据库ID）
     * @return 歌词内容（LRC格式，带时间轴）
     */
    public String getLyrics(Long neteaseSongId) {
        if (neteaseSongId == null || neteaseSongId <= 0) {
            log.warn("无效的网易云歌曲ID: {}", neteaseSongId);
            return "";
        }
        
        try {
            String url = LYRIC_URL + "?id=" + neteaseSongId + "&lv=1&tv=-1";

            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            log.info("在线获取歌词: neteaseSongId={}", neteaseSongId);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            String responseBody = response.getBody();
            if (responseBody == null || responseBody.isEmpty()) {
                log.warn("歌词API返回空响应");
                return "";
            }
            
            JsonNode root = objectMapper.readTree(responseBody);
            int code = root.path("code").asInt(-1);

            if (code != 200) {
                log.warn("获取歌词失败, code: {}", code);
                return "";
            }
            
            // 获取带时间轴的歌词
            JsonNode lrcNode = root.path("lrc").path("lyric");
            if (!lrcNode.isMissingNode() && !lrcNode.isNull()) {
                String lyrics = lrcNode.asText("").trim();
                if (!lyrics.isEmpty()) {
                    log.info("获取歌词成功: neteaseSongId={}, 长度={}", neteaseSongId, lyrics.length());
                    return lyrics;
                }
            }
            
            // 尝试获取翻译歌词
            JsonNode tlyricNode = root.path("tlyric").path("lyric");
            if (!tlyricNode.isMissingNode() && !tlyricNode.isNull()) {
                String lyrics = tlyricNode.asText("").trim();
                if (!lyrics.isEmpty()) {
                    log.info("获取翻译歌词成功: neteaseSongId={}, 长度={}", neteaseSongId, lyrics.length());
                    return lyrics;
                }
            }
            
            log.info("该歌曲暂无歌词: neteaseSongId={}", neteaseSongId);
            
        } catch (Exception e) {
            log.error("在线获取歌词异常: neteaseSongId={}, error={}", neteaseSongId, e.getMessage(), e);
        }
        
        return "";
    }

    /**
     * 创建HTTP请求头
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Referer", REFERER);
        headers.set("User-Agent", USER_AGENT);
        headers.set("Accept", "application/json, text/plain, */*");
        headers.set("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        headers.set("Origin", "https://music.163.com");
        // 添加必要的Cookie
        headers.set("Cookie", "_ntes_nnid=7eced19b; _ntes_nuid=88888888");
        return headers;
    }
}
