package com.leyu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.OrderDTO;
import com.leyu.service.OrderService;
import com.leyu.utils.JwtUtil;
import com.leyu.vo.OrderVO;
import com.leyu.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@Api(tags = "订单管理")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/create")
    @ApiOperation("创建订单")
    public Result<OrderVO> create(@RequestHeader("Authorization") String token, @RequestBody OrderDTO dto) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.success(orderService.create(userId, dto));
    }

    @PostMapping("/payNotify")
    @ApiOperation("支付回调")
    public Result<Void> payNotify(@RequestParam String orderNo) {
        orderService.paySuccess(orderNo);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("获取订单列表")
    public Result<Page<OrderVO>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer payStatus,
            @RequestParam(required = false) String packageType) {
        return Result.success(orderService.getPage(pageNum, pageSize, payStatus, packageType));
    }

    @GetMapping("/my")
    @ApiOperation("获取我的订单")
    public Result<Page<OrderVO>> myOrders(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.success(orderService.getUserOrders(userId, pageNum, pageSize));
    }

    @GetMapping("/detail/{orderNo}")
    @ApiOperation("获取订单详情")
    public Result<OrderVO> detail(@PathVariable String orderNo) {
        return Result.success(orderService.getByOrderNo(orderNo));
    }
}
