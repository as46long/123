package com.leyu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.SongCommentDTO;
import com.leyu.service.SongCommentService;
import com.leyu.utils.JwtUtil;
import com.leyu.vo.Result;
import com.leyu.vo.SongCommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 歌曲评论控制器
 * 处理歌曲评论的发布、查询、点赞等操作
 */
@RestController
@RequestMapping("/api/songComment")
@Tag(name = "歌曲评论管理")
public class SongCommentController {

    @Autowired
    private SongCommentService songCommentService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取歌曲评论列表
     * @param songId 歌曲ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param token JWT令牌(可选)
     * @return 评论分页数据
     */
    @GetMapping("/list")
    @Operation(summary = "获取歌曲评论列表")
    public Result<Page<SongCommentVO>> list(
            @RequestParam Long songId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = null;
            if (token != null && !token.isEmpty()) {
                try {
                    userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
                } catch (Exception e) {
                    // Token无效，继续处理但不设置userId
                }
            }
            return Result.success(songCommentService.getSongComments(songId, pageNum, pageSize, userId));
        } catch (Exception e) {
            return Result.error(500, "获取歌曲评论失败：" + e.getMessage());
        }
    }

    /**
     * 发布歌曲评论
     * @param token JWT令牌
     * @param dto 评论内容DTO
     * @return 发布结果
     */
    @PostMapping("/post")
    @Operation(summary = "发布歌曲评论")
    public Result<Void> post(@RequestHeader("Authorization") String token, @RequestBody SongCommentDTO dto) {
        try {
            Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
            songCommentService.postSongComment(userId, dto);
            return Result.success();
        } catch (Exception e) {
            return Result.error(500, "发布评论失败：" + e.getMessage());
        }
    }

    /**
     * 点赞歌曲评论
     * @param id 评论ID
     * @param token JWT令牌
     * @return 操作结果
     */
    @PostMapping("/like/{id}")
    @Operation(summary = "点赞歌曲评论")
    public Result<Void> like(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        songCommentService.likeSongComment(id, userId);
        return Result.success();
    }

    /**
     * 获取我的歌曲评论列表
     * @param token JWT令牌
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 用户评论分页数据
     */
    @GetMapping("/my")
    @Operation(summary = "获取我的歌曲评论")
    public Result<Page<SongCommentVO>> getMyComments(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.success(songCommentService.getMySongComments(userId, pageNum, pageSize));
    }
}
