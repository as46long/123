package com.leyu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.service.OrderService;
import com.leyu.vo.OrderVO;
import com.leyu.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员-订单管理控制器
 * 提供订单列表查询、详情查看等后台管理功能
 */
@RestController
@RequestMapping("/api/admin/order")
@Tag(name = "管理员-订单管理")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 获取订单列表(分页)
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param payStatus 支付状态(可选)
     * @param packageType 套餐类型(可选)
     * @return 订单分页数据
     */
    @GetMapping("/list")
    @Operation(summary = "获取订单列表")
    public Result<Page<OrderVO>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer payStatus,
            @RequestParam(required = false) String packageType) {
        return Result.success(orderService.getPage(pageNum, pageSize, payStatus, packageType));
    }

    /**
     * 获取订单详情
     * @param orderNo 订单号
     * @return 订单详细信息
     */
    @GetMapping("/detail/{orderNo}")
    @Operation(summary = "获取订单详情")
    public Result<OrderVO> detail(@PathVariable String orderNo) {
        return Result.success(orderService.getByOrderNo(orderNo));
    }
}
