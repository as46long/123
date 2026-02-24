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

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

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
            user.setIsVip(1);
            user.setVipExpireTime(order.getExpireTime());
            userMapper.updateById(user);
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

    private String generateOrderNo() {
        return System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);
    }

    private LocalDateTime calculateExpireTime(String packageType) {
        LocalDateTime now = LocalDateTime.now();
        switch (packageType) {
            case "MONTH":
                return now.plusMonths(1);
            case "QUARTER":
                return now.plusMonths(3);
            case "YEAR":
                return now.plusYears(1);
            default:
                return now.plusMonths(1);
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
