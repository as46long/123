package com.leyu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.service.CommentService;
import com.leyu.vo.CommentVO;
import com.leyu.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员-留言管理控制器
 * 提供留言审核、删除等后台管理功能
 */
@RestController
@RequestMapping("/api/admin/comment")
@Tag(name = "管理员-留言管理")
public class AdminCommentController {

    @Autowired
    private CommentService commentService;

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
}
