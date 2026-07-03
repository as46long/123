package com.leyu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.OrderDTO;
import com.leyu.vo.OrderVO;

/**
 * 订单服务接口
 * 提供订单创建、支付处理、查询等功能
 */
public interface OrderService {
    /**
     * 创建订单
     * @param userId 用户ID
     * @param dto 订单信息DTO
     * @return 创建的订单视图对象
     */
    OrderVO create(Long userId, OrderDTO dto);

    /**
     * 处理支付成功
     * @param orderNo 订单号
     */
    void paySuccess(String orderNo);

    /**
     * 根据订单号获取订单详情
     * @param orderNo 订单号
     * @return 订单视图对象
     */
    OrderVO getByOrderNo(String orderNo);

    /**
     * 分页查询订单列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param payStatus 支付状态
     * @param packageType 套餐类型
     * @return 订单分页数据
     */
    Page<OrderVO> getPage(int pageNum, int pageSize, Integer payStatus, String packageType);

    /**
     * 获取用户订单列表
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 用户订单分页数据
     */
    Page<OrderVO> getUserOrders(Long userId, int pageNum, int pageSize);
}
