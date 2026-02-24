package com.leyu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.OrderDTO;
import com.leyu.vo.OrderVO;

public interface OrderService {
    OrderVO create(Long userId, OrderDTO dto);
    void paySuccess(String orderNo);
    OrderVO getByOrderNo(String orderNo);
    Page<OrderVO> getPage(int pageNum, int pageSize, Integer payStatus, String packageType);
    Page<OrderVO> getUserOrders(Long userId, int pageNum, int pageSize);
}
