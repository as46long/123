package com.leyu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.SongDTO;
import com.leyu.service.SongService;
import com.leyu.utils.JwtUtil;
import com.leyu.vo.Result;
import com.leyu.vo.SongVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/song")
@Api(tags = "歌曲管理")
public class SongController {

    @Autowired
    private SongService songService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/list")
    @ApiOperation("分页查询歌曲列表")
    public Result<Page<SongVO>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return Result.success(songService.getPage(pageNum, pageSize, keyword, category));
    }

    @GetMapping("/detail/{id}")
    @ApiOperation("获取歌曲详情")
    public Result<SongVO> detail(@PathVariable Long id,
                                 @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = null;
        if (token != null && token.startsWith("Bearer ")) {
            userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        }
        return Result.success(songService.getVOById(id, userId));
    }

    @GetMapping("/search")
    @ApiOperation("搜索歌曲")
    public Result<List<SongVO>> search(@RequestParam String keyword,
                                       @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = null;
        if (token != null && token.startsWith("Bearer ")) {
            userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        }
        return Result.success(songService.search(keyword, userId));
    }

    @GetMapping("/recommend/{userId}")
    @ApiOperation("获取推荐歌曲")
    public Result<List<SongVO>> recommend(@PathVariable Long userId,
                                          @RequestParam(defaultValue = "10") int num) {
        return Result.success(songService.getRecommend(userId, num));
    }

    @GetMapping("/hot")
    @ApiOperation("获取热门歌曲")
    public Result<List<SongVO>> hot(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(songService.getHot(limit));
    }

    @GetMapping("/category/{category}")
    @ApiOperation("按分类获取歌曲")
    public Result<List<SongVO>> byCategory(@PathVariable String category,
                                          @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = null;
        if (token != null && token.startsWith("Bearer ")) {
            userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        }
        return Result.success(songService.getByCategory(category, userId));
    }

    @GetMapping("/lyrics/{id}")
    @ApiOperation("获取歌词")
    public Result<String> lyrics(@PathVariable Long id) {
        return Result.success(songService.getLyrics(id));
    }

    @PostMapping("/add")
    @ApiOperation("添加歌曲")
    public Result<Void> add(@RequestBody SongDTO dto) {
        songService.add(dto);
        return Result.success();
    }

    @PutMapping("/update")
    @ApiOperation("更新歌曲")
    public Result<Void> update(@RequestBody SongDTO dto) {
        songService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除歌曲")
    public Result<Void> delete(@PathVariable Long id) {
        songService.delete(id);
        return Result.success();
    }

    @PostMapping("/play/{id}")
    @ApiOperation("记录播放")
    public Result<Void> play(@PathVariable Long id) {
        songService.incrementPlayCount(id);
        return Result.success();
    }
}
