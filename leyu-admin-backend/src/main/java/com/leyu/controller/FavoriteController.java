package com.leyu.controller;

import com.leyu.service.FavoriteService;
import com.leyu.utils.JwtUtil;
import com.leyu.vo.Result;
import com.leyu.vo.SongVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
@Api(tags = "收藏管理")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/list")
    @ApiOperation("获取用户收藏列表")
    public Result<List<SongVO>> list(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.success(favoriteService.getUserFavorites(userId));
    }

    @PostMapping("/add")
    @ApiOperation("添加收藏")
    public Result<Void> add(@RequestHeader("Authorization") String token, @RequestParam Long songId) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        favoriteService.add(userId, songId);
        return Result.success();
    }

    @DeleteMapping("/delete/{songId}")
    @ApiOperation("取消收藏")
    public Result<Void> remove(@RequestHeader("Authorization") String token, @PathVariable Long songId) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        favoriteService.remove(userId, songId);
        return Result.success();
    }
}
