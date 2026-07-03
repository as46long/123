package com.leyu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.service.UserService;
import com.leyu.vo.Result;
import com.leyu.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员-用户管理控制器
 * 提供用户列表查询、详情查看、状态管理等后台管理功能
 */
@RestController
@RequestMapping("/api/admin/user")
@Tag(name = "管理员-用户管理")
public class AdminUserController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param keyword 搜索关键词(可选)
     * @return 用户分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询用户列表")
    public Result<Page<UserVO>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(userService.getPage(pageNum, pageSize, keyword));
    }

    /**
     * 获取用户详情
     * @param id 用户ID
     * @return 用户详细信息
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "获取用户详情")
    public Result<UserVO> detail(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    /**
     * 更新用户状态(启用/禁用)
     * @param id 用户ID
     * @param status 目标状态(0-禁用 1-启用)
     * @return 操作结果
     */
    @PutMapping("/status/{id}")
    @Operation(summary = "更新用户状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除用户")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }
}
