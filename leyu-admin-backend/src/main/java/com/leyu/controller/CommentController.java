package com.leyu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.CommentDTO;
import com.leyu.service.CommentService;
import com.leyu.utils.JwtUtil;
import com.leyu.vo.CommentVO;
import com.leyu.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/comment")
@Api(tags = "留言管理")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/list")
    @ApiOperation("获取留言列表")
    public Result<Page<CommentVO>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status) {
        return Result.success(commentService.getPage(pageNum, pageSize, status));
    }

    @GetMapping("/recommend/{userId}")
    @ApiOperation("获取推荐留言")
    public Result<List<CommentVO>> recommend(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "10") int num) {
        return Result.success(commentService.getRecommend(userId, num));
    }

    @PostMapping("/post")
    @ApiOperation("发布留言")
    public Result<Void> post(@RequestHeader("Authorization") String token,
                             @Valid @RequestBody CommentDTO dto) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        commentService.post(userId, dto);
        return Result.success();
    }

    @PutMapping("/audit/{id}")
    @ApiOperation("审核留言")
    public Result<Void> audit(@PathVariable Long id, @RequestParam Integer status) {
        commentService.audit(id, status);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除留言")
    public Result<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return Result.success();
    }

    @PostMapping("/like/{id}")
    @ApiOperation("点赞留言")
    public Result<Void> like(@PathVariable Long id,
                             @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        commentService.like(id, userId);
        return Result.success();
    }
}
