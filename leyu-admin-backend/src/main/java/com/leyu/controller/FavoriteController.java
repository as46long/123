package com.leyu.controller;

import com.leyu.service.FavoriteService;
import com.leyu.utils.JwtUtil;
import com.leyu.vo.Result;
import com.leyu.vo.SongVO;
import com.leyu.dto.FavoriteDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏控制器
 * 处理用户对歌曲的收藏和取消收藏操作
 */
@RestController
@RequestMapping("/api/favorite")
@Tag(name = "收藏管理")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取用户收藏列表
     * @param token JWT令牌
     * @return 用户收藏的歌曲列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取用户收藏列表")
    public Result<List<SongVO>> list(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.success(favoriteService.getUserFavorites(userId));
    }

   /**
    * 添加歌曲到收藏
    * @param token JWT令牌
    * @param dto 收藏信息(歌曲ID)
    * @return 操作结果
    */
   @PostMapping("/add")
   @Operation(summary = "添加收藏")
   public Result<Void> add(@RequestHeader("Authorization") String token, @RequestBody FavoriteDTO dto) {
       Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
       favoriteService.add(userId, dto.getSongId());
       return Result.success();
   }

    /**
     * 取消收藏歌曲
     * @param token JWT令牌
     * @param songId 歌曲ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{songId}")
    @Operation(summary = "取消收藏")
    public Result<Void> remove(@RequestHeader("Authorization") String token, @PathVariable Long songId) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        favoriteService.remove(userId, songId);
        return Result.success();
    }
}
