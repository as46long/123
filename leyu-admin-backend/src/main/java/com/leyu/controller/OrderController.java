package com.leyu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.OrderDTO;
import com.leyu.service.OrderService;
import com.leyu.utils.JwtUtil;
import com.leyu.vo.OrderVO;
import com.leyu.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 * 处理用户端的订单创建、查询、支付等请求
 */
@RestController
@RequestMapping("/api/order")
@Tag(name = "订单管理")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 创建会员订单
     * @param token JWT令牌
     * @param dto 订单信息(套餐类型等)
     * @return 创建的订单信息
     */
    @PostMapping("/create")
    @Operation(summary = "创建订单")
    public Result<OrderVO> create(@RequestHeader("Authorization") String token, @RequestBody OrderDTO dto) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.success(orderService.create(userId, dto));
    }

    /**
     * 支付成功回调
     * @param orderNo 订单号
     * @return 处理结果
     */
    @PostMapping("/payNotify")
    @Operation(summary = "支付回调")
    public Result<Void> payNotify(@RequestParam String orderNo) {
        orderService.paySuccess(orderNo);
        return Result.success();
    }

    /**
     * 获取订单列表(管理员)
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
     * 获取当前用户的订单列表
     * @param token JWT令牌
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 用户订单分页数据
     */
    @GetMapping("/my")
    @Operation(summary = "获取我的订单")
    public Result<Page<OrderVO>> myOrders(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.success(orderService.getUserOrders(userId, pageNum, pageSize));
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
