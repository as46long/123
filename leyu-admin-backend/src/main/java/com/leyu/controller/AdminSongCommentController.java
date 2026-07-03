package com.leyu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.service.SongCommentService;
import com.leyu.service.SongService;
import com.leyu.vo.Result;
import com.leyu.vo.SongCommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员-歌曲评论管理控制器
 * 提供歌曲评论的审核、封禁、删除等后台管理功能
 */
@RestController
@RequestMapping("/api/admin/songComment")
@Tag(name = "管理员-歌曲留言管理")
public class AdminSongCommentController {

    @Autowired
    private SongCommentService songCommentService;

    @Autowired
    private SongService songService;

    /**
     * 获取歌曲评论列表(分页)
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param category 歌曲分类(可选)
     * @param status 评论状态(可选)
     * @return 评论分页数据
     */
    @GetMapping("/list")
    @Operation(summary = "获取歌曲留言列表")
    public Result<Page<SongCommentVO>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        try {
            return Result.success(songCommentService.getAdminSongComments(pageNum, pageSize, category, status));
        } catch (Exception e) {
            return Result.error(500, "获取歌曲留言失败：" + e.getMessage());
        }
    }

    /**
     * 封禁歌曲评论
     * @param id 评论ID
     * @return 操作结果
     */
    @PutMapping("/ban/{id}")
    @Operation(summary = "封禁歌曲留言")
    public Result<Void> ban(@PathVariable Long id) {
        songCommentService.banSongComment(id);
        return Result.success();
    }

    /**
     * 解封歌曲评论
     * @param id 评论ID
     * @return 操作结果
     */
    @PutMapping("/unban/{id}")
    @Operation(summary = "解封歌曲留言")
    public Result<Void> unban(@PathVariable Long id) {
        songCommentService.unbanSongComment(id);
        return Result.success();
    }

    /**
     * 删除歌曲评论
     * @param id 评论ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除歌曲留言")
    public Result<Void> delete(@PathVariable Long id) {
        songCommentService.deleteSongComment(id);
        return Result.success();
    }

    /**
     * 获取歌曲分类列表
     * @return 分类名称列表
     */
    @GetMapping("/categories")
    @Operation(summary = "获取歌曲分类列表")
    public Result<java.util.List<String>> getCategories() {
        return Result.success(songService.getAllCategories());
    }
}
