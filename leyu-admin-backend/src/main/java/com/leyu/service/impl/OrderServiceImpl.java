package com.leyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.OrderDTO;
import com.leyu.entity.Order;
import com.leyu.entity.User;
import com.leyu.mapper.OrderMapper;
import com.leyu.mapper.UserMapper;
import com.leyu.service.OrderService;
import com.leyu.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 * 实现订单创建、支付处理、查询等功能
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 创建订单
     * 生成唯一订单号，保存订单信息
     */
    @Override
    public OrderVO create(Long userId, OrderDTO dto) {
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setPackageType(dto.getPackageType());
        order.setAmount(dto.getAmount());
        order.setPayStatus(0);
        orderMapper.insert(order);
        return convertToVO(order);
    }

    /**
     * 处理支付成功
     * 更新订单状态、支付时间，计算会员到期时间，更新用户VIP状态
     */
    @Override
    public void paySuccess(String orderNo) {
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
        if (order != null && order.getPayStatus() == 0) {
            order.setPayStatus(1);
            order.setPayTime(LocalDateTime.now());
            order.setExpireTime(calculateExpireTime(order.getPackageType()));
            orderMapper.updateById(order);

            // 更新用户VIP状态
            User user = userMapper.selectById(order.getUserId());
            if (user != null) {
                // 如果已有会员且未过期，在原到期时间基础上延长
                LocalDateTime baseTime = user.getVipExpireTime();
                if (baseTime == null || baseTime.isBefore(LocalDateTime.now())) {
                    baseTime = LocalDateTime.now();
                }
                user.setVipExpireTime(calculateExpireTimeFromBase(baseTime, order.getPackageType()));
                user.setIsVip(1);
                userMapper.updateById(user);
            }
        }
    }

    @Override
    public OrderVO getByOrderNo(String orderNo) {
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
        return order != null ? convertToVO(order) : null;
    }

    @Override
    public Page<OrderVO> getPage(int pageNum, int pageSize, Integer payStatus, String packageType) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (payStatus != null) {
            wrapper.eq(Order::getPayStatus, payStatus);
        }
        if (StringUtils.hasText(packageType)) {
            wrapper.eq(Order::getPackageType, packageType);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        Page<Order> orderPage = orderMapper.selectPage(page, wrapper);
        Page<OrderVO> voPage = new Page<>(pageNum, pageSize, orderPage.getTotal());
        voPage.setRecords(orderPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public Page<OrderVO> getUserOrders(Long userId, int pageNum, int pageSize) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId).orderByDesc(Order::getCreateTime);
        Page<Order> orderPage = orderMapper.selectPage(page, wrapper);
        Page<OrderVO> voPage = new Page<>(pageNum, pageSize, orderPage.getTotal());
        voPage.setRecords(orderPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));
        return voPage;
    }

    /**
     * 生成订单号
     * 格式: 时间戳 + 随机UUID片段
     */
    private String generateOrderNo() {
        return System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);
    }

    private LocalDateTime calculateExpireTime(String packageType) {
        return calculateExpireTimeFromBase(LocalDateTime.now(), packageType);
    }

    /**
     * 根据套餐类型计算会员到期时间
     * @param packageType 套餐类型(WEEK/MONTH/QUARTER/YEAR)
     * @return 到期时间
     */
    private LocalDateTime calculateExpireTimeFromBase(LocalDateTime baseTime, String packageType) {
        switch (packageType) {
            case "WEEK":
                return baseTime.plusDays(7);
            case "MONTH":
                return baseTime.plusMonths(1);
            case "QUARTER":
                return baseTime.plusMonths(3);
            case "YEAR":
                return baseTime.plusYears(1);
            default:
                return baseTime.plusMonths(1);
        }
    }

    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        User user = userMapper.selectById(order.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
        }
        return vo;
    }
}
