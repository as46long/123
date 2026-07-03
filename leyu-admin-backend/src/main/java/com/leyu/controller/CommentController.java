package com.leyu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.CommentDTO;
import com.leyu.service.CommentService;
import com.leyu.utils.JwtUtil;
import com.leyu.vo.CommentVO;
import com.leyu.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 留言控制器
 * 处理用户留言的发布、查询、审核、点赞等操作
 */
@RestController
@RequestMapping("/api/comment")
@Tag(name = "留言管理")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取留言列表(分页)
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param status 留言状态(可选)
     * @return 留言分页数据
     */
    @GetMapping("/list")
    @Operation(summary = "获取留言列表")
    public Result<Page<CommentVO>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status) {
        return Result.success(commentService.getPage(pageNum, pageSize, status));
    }

    /**
     * 获取推荐留言
     * @param userId 用户ID
     * @param num 推荐数量
     * @return 推荐留言列表
     */
    @GetMapping("/recommend/{userId}")
    @Operation(summary = "获取推荐留言")
    public Result<List<CommentVO>> recommend(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "10") int num) {
        return Result.success(commentService.getRecommend(userId, num));
    }

    /**
     * 发布留言
     * @param token JWT令牌
     * @param dto 留言内容DTO
     * @return 发布结果
     */
    @PostMapping("/post")
    @Operation(summary = "发布留言")
    public Result<Void> post(@RequestHeader("Authorization") String token,
                             @Valid @RequestBody CommentDTO dto) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        commentService.post(userId, dto);
        return Result.success();
    }

    /**
     * 审核留言
     * @param id 留言ID
     * @param status 目标状态(1-通过 2-拒绝)
     * @return 审核结果
     */
    @PutMapping("/audit/{id}")
    @Operation(summary = "审核留言")
    public Result<Void> audit(@PathVariable Long id, @RequestParam Integer status) {
        commentService.audit(id, status);
        return Result.success();
    }

    /**
     * 删除留言
     * @param id 留言ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除留言")
    public Result<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return Result.success();
    }

    /**
     * 点赞留言
     * @param id 留言ID
     * @param token JWT令牌
     * @return 操作结果
     */
    @PostMapping("/like/{id}")
    @Operation(summary = "点赞留言")
    public Result<Void> like(@PathVariable Long id,
                             @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        commentService.like(id, userId);
        return Result.success();
    }

    /**
     * 获取我的留言列表
     * @param token JWT令牌
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 用户留言分页数据
     */
    @GetMapping("/my")
    @Operation(summary = "获取我的留言")
    public Result<Page<CommentVO>> getMyComments(@RequestHeader("Authorization") String token,
                                                   @RequestParam(defaultValue = "1") int pageNum,
                                                   @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.success(commentService.getMyComments(userId, pageNum, pageSize));
    }
}
